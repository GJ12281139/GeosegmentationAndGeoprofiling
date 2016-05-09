package ru.ifmo.pashaac.map;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.*;
import fi.foyt.foursquare.api.FoursquareApi;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import ru.ifmo.pashaac.common.GeoMath;
import ru.ifmo.pashaac.common.Properties;
import ru.ifmo.pashaac.common.wrapper.BoundingBox;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created by Pavel Asadchiy
 * 27.04.16 12:15.
 */
@Service
public class MapService {

    private static final Logger LOG = Logger.getLogger(MapService.class);

    private final GeoApiContext googleContext;
    private final FoursquareApi foursquareApi;

    public MapService() {
        if (System.getenv("GOOGLE_API_KEY") == null) {
            throw new IllegalStateException("Not found environment variable GOOGLE_API_KEY with key");
        }
        googleContext = new GeoApiContext()
                .setApiKey(System.getenv("GOOGLE_API_KEY"))
                .setQueryRateLimit(3)
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);

        final String clientId = System.getenv("FOURSQUARE_CLIENT_ID");
        final String clientSecret = System.getenv("FOURSQUARE_CLIENT_SECRET");
        if (clientId == null || clientSecret == null) {
            LOG.error("No id/secret key for foursquare API usage");
            throw new IllegalStateException("No id/secret key for foursquare API usage");
        }
        this.foursquareApi = new FoursquareApi(clientId, clientSecret, "");
    }

    public GeoApiContext getGoogleContext() {
        return googleContext;
    }

    public FoursquareApi getFoursquareApi() {
        return foursquareApi;
    }

    @Nullable
    public GeocodingResult[] getAddressByCoordinates(LatLng user) {
        try {
            return GeocodingApi.newRequest(googleContext).latlng(user).await();
        } catch (Exception e) {
            LOG.error("Error getting address by coordinates (" + user.lat + ", " + user.lng + " - reverse geocoding)");
            return null;
        }
    }

    @Nullable
    public String getCorrectCity(String city, @Nullable String country) {
        String address = city + (country == null ? "" : " " +  country);
        try {
            GeocodingResult[] geocodingResults = GeocodingApi.newRequest(googleContext).address(address).await();
            return getCorrectCity(geocodingResults[0]);
        } catch (Exception e) {
            LOG.error("Can't correct address " + address);
            return null;
        }
    }

    @Nullable
    public String getCorrectCity(GeocodingResult geoResult) {
        String localityName = getComponentTypeLongName(geoResult, AddressComponentType.LOCALITY);
        if (localityName != null) {
            return localityName.trim().replaceAll("\\s+", "_");
        }
        String areaOneName = getComponentTypeLongName(geoResult, AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1);
        if (areaOneName != null) {
            return areaOneName.trim().replaceAll("\\s+", "_");
        }
        return null;
    }

    @Nullable
    public String getCorrectCountry(String city, @Nullable String country) {
        String address = city + (country == null ? "" : " " +  country);
        try {
            GeocodingResult[] geocodingResults = GeocodingApi.newRequest(googleContext).address(address).await();
            return getCorrectCountry(geocodingResults[0]);
        } catch (Exception e) {
            LOG.error("Can't correct address " + address);
            return null;
        }
    }

    @Nullable
    public String getCorrectCountry(GeocodingResult geoResult) {
        String country = getComponentTypeLongName(geoResult, AddressComponentType.COUNTRY);
        if (country != null) {
            return country.trim().replaceAll("\\s+", "_");
        }
        return null;
    }

    @Nullable
    public String getComponentTypeLongName(GeocodingResult geocodingResult, AddressComponentType componentType) {
        Optional<AddressComponent> component = Arrays.stream(geocodingResult.addressComponents)
                .filter(address -> Arrays.stream(address.types)
                        .anyMatch(type -> type == componentType))
                .findAny();

        return component.isPresent() ? component.get().longName : null;
    }

    @Nullable
    public BoundingBox getCityBoundingBox(String city, String country) {
        try {
            String address = city + country;
            GeocodingResult[] boundingboxes = GeocodingApi.newRequest(googleContext).address(address).await();
            final Bounds box = boundingboxes[0].geometry.bounds;
            LOG.info("Boundingbox: southwest (lat = " + box.southwest.lat + ", lng = " + box.southwest.lng + ") " +
                    "northeast (lat = " + box.northeast.lat + ", lng = " + box.northeast.lng + ")");
            double distance = GeoMath.distance(box.southwest.lat, box.southwest.lng, box.northeast.lat, box.northeast.lng);
            if (distance > Properties.getMaxBoundingBoxDiagonal()) {
                LOG.warn("Diagonal distance " + distance + " more than program maximum " + Properties.getMaxBoundingBoxDiagonal());
                return null;
            }
            return new BoundingBox(box, city, country);
        } catch (Exception e) {
            LOG.error("Error getting boundingbox around city = " + city + ", country = " + country);
            return null;
        }
    }

    @Nullable
    public BoundingBox getCityBoundingBox(double lat, double lng) {
        GeocodingResult[] userAddresses = getAddressByCoordinates(new LatLng(lat, lng));
        if (userAddresses == null) {
            LOG.error("Service can't get address by coordinates latitude = " + lat + ", longitude = " + lng);
            return null;
        }
        String city = getCorrectCity(userAddresses[0]);
        String country = getCorrectCountry(userAddresses[0]);
        LOG.info("Boundingbox city = " + city+ ", country = " + country);
        if (city == null || country == null) {
            LOG.error("Service can't determine city by coordinates latitude = " + lat + ", longitude = " + lng);
            return null;
        }
        return getCityBoundingBox(city, country);
    }
}
