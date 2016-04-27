package ru.ifmo.pashaac.map;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ifmo.pashaac.common.BoundingBox;
import ru.ifmo.pashaac.common.GeoMath;
import ru.ifmo.pashaac.common.Place;
import ru.ifmo.pashaac.common.Properties;
import ru.ifmo.pashaac.coverage.CoverageModel;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created by Pavel Asadchiy
 * 21.04.16 23:38.
 */
@Service
public class BoundingBoxService {

    private static final Logger LOG = Logger.getLogger(BoundingBoxService.class);

    @Autowired
    private final MapService mapService;

    private final GeoApiContext context;

    public BoundingBoxService(MapService mapService) {
        if (System.getenv("GOOGLE_API_KEY") == null) {
            throw new IllegalStateException("Not found environment variable GOOGLE_API_KEY with key");
        }
        context = new GeoApiContext()
                .setApiKey(System.getenv("GOOGLE_API_KEY"))
                .setQueryRateLimit(3)
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
        this.mapService = mapService;
    }


    /**
     * Get boundingbox around region of the country with Google geocoding API help
     *
     * @param region  user geolocation region
     * @param country user geolocation country
     * @return boundingbox around region of the country
     */
    @NotNull
    public CoverageModel getBoundingBoxModel(String region, String country) {
        try {
            String address = region + (country != null ? " " + country : "");
            GeocodingResult[] boundingboxes = GeocodingApi.newRequest(context).address(address).await();
            final Bounds box = boundingboxes[0].geometry.bounds;
            LOG.info("Boundingbox " +
                    "southwest (lat = " + box.southwest.lat + ", lng = " + box.southwest.lng + "), " +
                    "northeast (lat = " + box.northeast.lat + ", lng = " + box.northeast.lng + ")");
            double distance = GeoMath.distance(box.southwest.lat, box.southwest.lng, box.northeast.lat, box.northeast.lng);
            return distance > Properties.getMaxBoundingBoxDiagonal()
                    ? new CoverageModel("Service can't get region map by region = " + region + ", country = " + country)
                    : new CoverageModel(new Place.Builder().setLatLng(GeoMath.getBoundCenter(box)).setIcon(Properties.getIconUser48()).build(), new BoundingBox(box));
        } catch (Exception e) {
            LOG.error("Error getting boundingbox around region = " + region + " and country = " + country);
            return new CoverageModel("Service can't get region map by region = " + region + ", country = " + country);
        }
    }

    /**
     * CoverageModel - wrapper with boundingbox around coordinates and user location (without places)
     *
     * @param lat latitude
     * @param lng longitude
     * @return CoverageModel with boundingbox around coordinates and user location (without places)
     */
    @NotNull
    public CoverageModel getBoundingBoxModel(Double lat, Double lng) {
        Place user = new Place.Builder().setLat(lat).setLng(lng).setIcon(Properties.getIconUser48()).build();
        GeocodingResult[] userAddresses = mapService.getAddressByCoordinates(user.getLatLng());
        if (userAddresses == null) {
            return new CoverageModel("Service can't get address by coordinates (latitude = " + lat + ", longitude = " + lng + ")");
        }
        final String region = getRegionByGeocoding(userAddresses);
        final String country = getCountryByGeocoding(userAddresses);
        LOG.info("Boundingbox for region = " + region + ", country = " + country);
        if (region == null || country == null) {
            return new CoverageModel("Service can't determine region by coordinates (latitude = " + lat + ", longitude = " + lng + ")");
        }
        CoverageModel boxModel = getBoundingBoxModel(region, country);
        return boxModel.getError() != null ? boxModel : new CoverageModel(user, boxModel.getBox());
    }

    @Nullable
    public String getRegionByGeocoding(GeocodingResult[] geocodingResults) {
        String localityName = getComponentTypeLongName(geocodingResults[0], AddressComponentType.LOCALITY);
        String areaOneName = getComponentTypeLongName(geocodingResults[0], AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1);
        return localityName != null ? localityName : areaOneName;
    }

    @Nullable
    public String getCountryByGeocoding(GeocodingResult[] geocodingResults) {
        return getComponentTypeLongName(geocodingResults[0], AddressComponentType.COUNTRY);
    }

    @Nullable
    private String getComponentTypeLongName(GeocodingResult geocodingResult, AddressComponentType componentType) {
        Optional<AddressComponent> component = Arrays.stream(geocodingResult.addressComponents)
                .filter(address -> Arrays.stream(address.types)
                        .anyMatch(type -> type == componentType))
                .findAny();

        return component.isPresent() ? component.get().longName : null;
    }

}
