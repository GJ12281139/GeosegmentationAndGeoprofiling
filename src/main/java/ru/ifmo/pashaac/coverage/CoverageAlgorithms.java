package ru.ifmo.pashaac.coverage;

import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.model.*;
import com.grum.geocalc.EarthCalc;
import com.grum.geocalc.Point;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import ru.ifmo.pashaac.common.BoundingBox;
import ru.ifmo.pashaac.common.GeoMath;
import ru.ifmo.pashaac.common.Place;
import ru.ifmo.pashaac.common.Properties;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Pavel Asadchiy
 * 21.04.16 23:40.
 */
@Service
public class CoverageAlgorithms {

    private static final Logger LOG = Logger.getLogger(CoverageAlgorithms.class);

    private final GeoApiContext context;

    public CoverageAlgorithms() {
        if (System.getenv("GOOGLE_API_KEY") == null) {
            throw new IllegalStateException("Set key for environment variable GOOGLE_API_KEY");
        }
        context = new GeoApiContext()
                .setApiKey(System.getenv("GOOGLE_API_KEY"))
                .setQueryRateLimit(3)
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }

    /**
     * Линейное распределение с использованием обычного сложения/вычитания широт/долгот
     *
     * @param box - bounding box вокруг города
     * @return список маркеров на карте, согласно виду распределения
     */
    @Deprecated
    @SuppressWarnings("unused")
    public CoverageModel getStaticSimpleMarkersDistribution(Bounds box) {
        LOG.info("Static uniform distribution with simple (without geodesic) calculation...");
        List<Place> places = new ArrayList<>();
        for (double lat = box.southwest.lat; lat <= box.northeast.lat; lat += 0.02) {
            for (double lng = box.southwest.lng; lng <= box.northeast.lng; lng += 0.03) {
                places.add(new Place.Builder().setLat(lat).setLng(lng).setRad(Properties.getMarkerRadius()).build());
            }
        }
        return new CoverageModel(GeoMath.getBoundCenter(box), new BoundingBox(box, places));
    }

    /**
     * Линейное распределение с использованием геодезического пересчета расстояния между координатами и
     * нахождения точек в определенном направлении на заданном расстоянии.
     *
     * @param model - модель состояния (интересует только bounding box вокруг города)
     * @return список маркеров на карте, согласно виду распределения
     */
    @Deprecated
    @SuppressWarnings("unused")
    public CoverageModel getStaticUniformGeodesicMarkersDistribution(CoverageModel model) {
        LOG.info("Static uniform distribution with geodesic calculation...");
        Bounds bounds = model.getBounds();
        List<Place> places = new ArrayList<>();
        Point southwestPoint = GeoMath.getPoint(bounds.southwest.lat, bounds.southwest.lng);
        Point northwestPoint = GeoMath.getPoint(bounds.northeast.lat, bounds.southwest.lng);

        double distanceLat = EarthCalc.getVincentyDistance(southwestPoint, northwestPoint);
        int countLat = (int) Math.ceil(distanceLat / Properties.getMarkerStep());
        double distanceLatNeighbor = distanceLat / countLat;
        for (int i = 0; i < countLat + 1; i++) {
            Point startLat = EarthCalc.pointRadialDistance(southwestPoint, 0, distanceLatNeighbor * i);
            if (startLat.getLatitude() > bounds.northeast.lat) {
                startLat = northwestPoint;
            }
            Point finishLng = GeoMath.getPoint(startLat, bounds.northeast.lng);
            double distanceLng = EarthCalc.getVincentyDistance(startLat, finishLng);
            int countLng = (int) Math.ceil(distanceLng / Properties.getMarkerStep());
            double distanceLngNeighbor = distanceLng / countLng;
            for (int j = 0; j < countLng + 1; j++) {
                Point tmp = EarthCalc.pointRadialDistance(startLat, 90, distanceLngNeighbor * j);
                if (tmp.getLongitude() > finishLng.getLongitude()) {
                    tmp = finishLng;
                }
                places.add(new Place.Builder()
                        .setLat(tmp.getLatitude())
                        .setLng(tmp.getLongitude())
                        .setIcon(Properties.getIconSearch48())
                        .setRad(Properties.getMarkerRadius()).build());
            }
        }
        return new CoverageModel(model.getUser(), new BoundingBox(model.getBox().getSouthwest(), model.getBox().getNortheast(), places));
    }

    public CoverageModel getDynamicTreeGeodesicMarkersDistribution(CoverageModel model, PlaceType placeType) {
        LOG.info("Dynamic uniform distribution with geodesic calculation");

        List<Place> places = placesSearch(model.getBounds(), placeType);
        if (places == null) {
            return new CoverageModel("Can't get places in " + model + " with radar search help");
        }
        places.add(model.getUser());
        return new CoverageModel(model.getUser(), new BoundingBox(model.getBounds(), places));
    }


    private List<Place> placesSearch(Bounds box, PlaceType placeType) {
        List<Place> places = new ArrayList<>();
        LatLng center = GeoMath.getBoundCenter(box);
        int maxRadius = (int) Math.ceil(GeoMath.getHalfDiagonal(box));
        int searchRadius = (int) Properties.getMarkerRadius();

        while (true) {
            try {
                PlacesSearchResponse response = PlacesApi.radarSearchQuery(context, center, searchRadius).type(placeType).await();

                if (Properties.getMinRadarSearchPlaces() < response.results.length
                        && response.results.length < Properties.getMaxRadarSearchPlaces() || searchRadius == maxRadius) {
                    // search center
                    places.add(new Place.Builder().setLat(center.lat).setLng(center.lng).setRad(searchRadius).setIcon(Properties.getIconSearch48()).build());
                    // places in search radius
                    for (PlacesSearchResult result : response.results) {
                        places.add(new Place.Builder()
                                .setLatLng(result.geometry.location)
                                .setPlaceId(result.placeId)
                                .setPlaceName(result.name)
                                .setAddress(result.formattedAddress)
                                .setIcon(Properties.getIconGreen32())
                                .setPlaceType(placeType.toString()).build());
                    }
                    return places; // OK or couldn't get more
                }
                if (response.results.length >= Properties.getMaxRadarSearchPlaces()) {
                    searchRadius = 3 * searchRadius / 4; // stupid, but control max border
                    continue;
                }
                if (maxRadius < searchRadius + Properties.getRadarSearchRadiusEps()) {
                    searchRadius = maxRadius;
                    continue;
                }

                // main binary search algorithm
                searchRadius *= 2;
                while (searchRadius > maxRadius + Properties.getRadarSearchRadiusEps()) {
                    searchRadius = 3 * searchRadius / 4;
                }
                if (searchRadius > maxRadius - Properties.getRadarSearchRadiusEps()) {
                    searchRadius = maxRadius;
                }
            } catch (Exception e) {
                LOG.error("Radar search error " + e.getMessage());
                return null;
            }
        }
    }
}
