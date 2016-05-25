package ru.ifmo.pashaac.map;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.*;
import fi.foyt.foursquare.api.FoursquareApi;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import ru.ifmo.pashaac.category.Category;
import ru.ifmo.pashaac.category.Culture;
import ru.ifmo.pashaac.common.GeoMath;
import ru.ifmo.pashaac.common.Properties;
import ru.ifmo.pashaac.common.primitives.BoundingBox;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
        String address = city + (country == null ? "" : " " + country);
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
        String address = city + (country == null ? "" : " " + country);
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

    public BoundingBox getCityBoundingBox(final String city, final String country) throws Exception {
        final String address = city + " " + country;
        final GeocodingResult[] boundingboxes = GeocodingApi.newRequest(googleContext).address(address).await();
        final Bounds box = boundingboxes[0].geometry.bounds;
        LOG.info("City boundingbox: southwest (lat = " + box.southwest.lat + ", lng = " + box.southwest.lng + ") " +
                "northeast (lat = " + box.northeast.lat + ", lng = " + box.northeast.lng + ")");
        double distance = GeoMath.distance(box.southwest.lat, box.southwest.lng, box.northeast.lat, box.northeast.lng);
        if (distance > Properties.getMaxBoundingBoxDiagonal()) {
            final String warn = "Diagonal distance " + distance + " more than service maximum " + Properties.getMaxBoundingBoxDiagonal() + " meters";
            LOG.warn(warn);
            throw new IllegalArgumentException(warn);
        }
        return new BoundingBox(box, city, country);
    }

    public BoundingBox getCityBoundingBox(double lat, double lng) throws Exception {
        final GeocodingResult[] userAddresses = getAddressByCoordinates(new LatLng(lat, lng));
        if (userAddresses == null) {
            final String error = "Service can't get address by coordinates latitude = " + lat + ", longitude = " + lng;
            LOG.error(error);
            throw new IllegalStateException(error);
        }
        final String city = getCorrectCity(userAddresses[0]);
        final String country = getCorrectCountry(userAddresses[0]);
        LOG.info("City = " + city + ", country = " + country);
        if (city == null || country == null) {
            final String error = "Service can't determine city/country by coordinates latitude = " + lat + ", longitude = " + lng;
            LOG.error(error);
            throw new IllegalStateException(error);
        }
        return getCityBoundingBox(city, country);
    }

    public List<Integer> percentsHandler(final @Nullable String percents) {
        return percents != null && percents.startsWith("[") && percents.endsWith("]")
                ? Arrays.stream(percents.substring(1, percents.length() - 1).split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList())
                : new ArrayList<>();
    }

    public Category getCategory(final String categoryStr, final BoundingBox boundingBox) {
        switch (categoryStr) {
            case "culture":
                return new Culture(this, boundingBox);
            //TODO: add another categories
            default:
                throw new IllegalArgumentException("Service can't determine category " + categoryStr);
        }
    }
}
