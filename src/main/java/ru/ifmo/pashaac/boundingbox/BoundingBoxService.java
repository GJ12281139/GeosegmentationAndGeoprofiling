package ru.ifmo.pashaac.boundingbox;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.ifmo.pashaac.common.GeoMath;
import ru.ifmo.pashaac.common.Properties;
import ru.ifmo.pashaac.coverage.CoverageModel;

import javax.annotation.Nullable;
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

    @Value("${min.radar.search.result}")
    private double minRadarSearchResult;

    private final GeoApiContext context;

    public BoundingBoxService() {
        if (System.getenv("GOOGLE_API_KEY") == null) {
            throw new IllegalStateException("set key for environment variable GOOGLE_API_KEY");
        }
        context = new GeoApiContext()
                .setApiKey(System.getenv("GOOGLE_API_KEY"))
                .setQueryRateLimit(3)
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }

    @Nullable
    public GeocodingResult[] getAddressByCoordinates(LatLng userGeolocation) {
        try {
            return GeocodingApi.newRequest(context).latlng(userGeolocation).await();
        } catch (Exception e) {
            LOG.error("Error getting address by coordinates (reverse geocoding)");
            return null;
        }
    }

    /**
     * Get boundingbox around region of the country with Google geocoding API help
     *
     * @param region  user geolocation region
     * @param country user geolocation country
     * @return boundingbox around region of the country
     */
    @Nullable
    public Bounds getRegionBoundingbox(String region, String country) {
        try {
            String address = region + (country != null ? ", " + country : "");
            GeocodingResult[] boundingboxes = GeocodingApi.newRequest(context).address(address).await();
            final Bounds box = boundingboxes[0].geometry.bounds;
            LOG.info("Boundingbox " +
                    "southwest (lat = " + box.southwest.lat + ", lng = " + box.southwest.lng + "), " +
                    "northeast (lat = " + box.northeast.lat + ", lng = " + box.northeast.lng + ")");
            double distance = GeoMath.distance(box.southwest.lat, box.southwest.lng, box.northeast.lat, box.northeast.lng);
            return distance > Properties.getMaxBoundingBoxDiagonal() ? null : box;
        } catch (Exception e) {
            LOG.error("Error getting boundingbox by region = " + region + " and country = " + country);
            return null;
        }
    }

    /**
     * Возвращает класс-обертку, в которой будет содержаться boundingbox региона, который соответствует координатам.
     *
     * @param lat широта
     * @param lng долгота
     * @return boundingbox региона, который соответствует координатам.
     */
    public CoverageModel getBoundingBox(Double lat, Double lng) {
        final LatLng userGeolocation = new LatLng(lat, lng);

        GeocodingResult[] userAddresses = getAddressByCoordinates(userGeolocation);
        if (userAddresses == null) {
            return new CoverageModel("Service can't get address by coordinates (latitude = " + lat + ", longitude = " + lng + ")");
        }
        final String region = getRegionByGeocoding(userAddresses); // should be city in most cases
        final String country = getCountryByGeocoding(userAddresses);
        LOG.info("Boundingbox for region = " + region + ", country = " + country);
        if (region == null || country == null) {
            return new CoverageModel("Service can't determine region by coordinates (latitude = " + lat + ", longitude = " + lng + ")");
        }
        Bounds box = getRegionBoundingbox(region, country);
        if (box == null) {
            return new CoverageModel("Service can't get region boundingbox by coordinates (latitude = " + lat + ", longitude = " + lng + ")");
        }
        return new CoverageModel(userGeolocation, box);
    }

    /**
     * Возвращает класс-обертку, в которой будет содержаться boundingbox
     *
     * @param region  города или одназначная небольшая область
     * @param country страна
     * @return boundingbox региона, который соответствует координатам.
     */
    public CoverageModel getBoundingBox(String region, String country) {
        Bounds box = getRegionBoundingbox(region, country);
        if (box == null) {
            return new CoverageModel("Service can't get region boundingbox by region = " + region + ", country = " + country);
        }
        return new CoverageModel(box);
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
