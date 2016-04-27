package ru.ifmo.pashaac.map;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

/**
 * Created by Pavel Asadchiy
 * 27.04.16 12:15.
 */
@Service
public class MapService {

    private static final Logger LOG = Logger.getLogger(MapService.class);

    private final GeoApiContext context;

    public MapService() {
        if (System.getenv("GOOGLE_API_KEY") == null) {
            throw new IllegalStateException("Not found environment variable GOOGLE_API_KEY with key");
        }
        context = new GeoApiContext()
                .setApiKey(System.getenv("GOOGLE_API_KEY"))
                .setQueryRateLimit(3)
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }

    @Nullable
    public GeocodingResult[] getAddressByCoordinates(LatLng user) {
        try {
            return GeocodingApi.newRequest(context).latlng(user).await();
        } catch (Exception e) {
            LOG.error("Error getting address by coordinates (" + user.lat + ", " + user.lng + " - reverse geocoding)");
            return null;
        }
    }

}
