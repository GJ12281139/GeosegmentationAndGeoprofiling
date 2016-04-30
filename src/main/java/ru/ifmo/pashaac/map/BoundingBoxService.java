package ru.ifmo.pashaac.map;

import com.google.maps.GeocodingApi;
import com.google.maps.model.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ifmo.pashaac.common.GeoMath;
import ru.ifmo.pashaac.common.Properties;
import ru.ifmo.pashaac.common.wrapper.BoundingBox;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;

/**
 * Created by Pavel Asadchiy
 * 21.04.16 23:38.
 */
@Service
public class BoundingBoxService {

    private static final Logger LOG = Logger.getLogger(BoundingBoxService.class);

    @Autowired
    private final MapService mapService;

    public BoundingBoxService(MapService mapService) {
        this.mapService = mapService;
    }

    /**
     * Get boundingbox around region of the country with Google geocoding API help
     *
     * @param region  user geolocation region
     * @param country user geolocation country
     * @return boundingbox around region of the country or null if error
     */
    @Nullable
    public BoundingBox getBoundingBox(String region, @Nullable String country) {
        try {
            String address = region + (country == null ? "" : " " +  country);
            GeocodingResult[] boundingboxes = GeocodingApi.newRequest(mapService.getGoogleContext()).address(address).await();
            final Bounds box = boundingboxes[0].geometry.bounds;
            LOG.info("Boundingbox: southwest (lat = " + box.southwest.lat + ", lng = " + box.southwest.lng + ") " +
                    "northeast (lat = " + box.northeast.lat + ", lng = " + box.northeast.lng + ")");
            double distance = GeoMath.distance(box.southwest.lat, box.southwest.lng, box.northeast.lat, box.northeast.lng);
            if (distance > Properties.getMaxBoundingBoxDiagonal()) {
                LOG.warn("Diagonal distance " + distance + " more than program maximum " + Properties.getMaxBoundingBoxDiagonal());
                return null;
            }
            return new BoundingBox(box, region, country);
        } catch (Exception e) {
            LOG.error("Error getting boundingbox around region = " + region + ", country = " + country);
            return null;
        }
    }

    /**
     * Get boundingbox around region of the country with Google geocoding API help
     *
     * @param lat user latitude
     * @param lng user longitude
     * @return boundingbox around region of the country or null if error
     */
    @Nullable
    public BoundingBox getBoundingBox(double lat, double lng) {
        GeocodingResult[] userAddresses = mapService.getAddressByCoordinates(new LatLng(lat, lng));
        if (userAddresses == null) {
            LOG.error("Service can't get address by coordinates latitude = " + lat + ", longitude = " + lng);
            return null;
        }
        String region = getRegionByGeocodings(userAddresses);
        String country = getCountryByGeocodings(userAddresses);
        LOG.info("Boundingbox region = " + region + ", country = " + country);
        if (region == null || country == null) {
            LOG.error("Service can't determine region by coordinates latitude = " + lat + ", longitude = " + lng);
            return null;
        }
        return getBoundingBox(region, country);
    }

    @Nullable
    public String getRegionByGeocodings(GeocodingResult[] geocodingResults) {
        String localityName = getComponentTypeLongName(geocodingResults[0], AddressComponentType.LOCALITY);
        String areaOneName = getComponentTypeLongName(geocodingResults[0], AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1);
        return localityName != null ? localityName : areaOneName;
    }

    @Nullable
    public String getCountryByGeocodings(GeocodingResult[] geocodingResults) {
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
