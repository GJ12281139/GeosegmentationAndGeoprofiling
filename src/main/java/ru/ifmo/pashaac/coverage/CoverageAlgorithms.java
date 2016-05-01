package ru.ifmo.pashaac.coverage;

import com.google.maps.PlacesApi;
import com.google.maps.model.*;
import com.grum.geocalc.EarthCalc;
import com.grum.geocalc.Point;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import ru.ifmo.pashaac.common.GeoMath;
import ru.ifmo.pashaac.common.Properties;
import ru.ifmo.pashaac.common.wrapper.BoundingBox;
import ru.ifmo.pashaac.common.wrapper.Place;
import ru.ifmo.pashaac.common.wrapper.Searcher;
import ru.ifmo.pashaac.map.MapController;
import ru.ifmo.pashaac.map.MapService;
import ru.ifmo.pashaac.mongo.AdditionalDAO;
import ru.ifmo.pashaac.mongo.PlaceDAO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Pavel Asadchiy
 * 21.04.16 23:40.
 */
@Service
public class CoverageAlgorithms {

    private static final Logger LOG = Logger.getLogger(CoverageAlgorithms.class);

    @Autowired
    private final MapService mapService;

    private int placeSearcherCall;
    private int radarSearchCall;

    public CoverageAlgorithms(MapService mapService) {
        this.mapService = mapService;
    }

    /**
     * Линейное распределение с использованием обычного сложения/вычитания широт/долгот
     *
     * @param boundingBox around region of the country or coordinates
     */
    @Deprecated
    @SuppressWarnings("unused")
    public void staticSimpleMarkersDistribution(BoundingBox boundingBox, ModelAndView view) {
        LOG.info("Static uniform distribution with simple (without geodesic) calculation...");
        List<Searcher> searchers = new ArrayList<>();
        double startLat = boundingBox.getSouthwest().getLat();
        double finishLat = boundingBox.getNortheast().getLat();
        double startLng = boundingBox.getSouthwest().getLng();
        double finishLng = boundingBox.getNortheast().getLng();
        for (double lat = startLat; lat <= finishLat; lat += 0.02) {
            for (double lng = startLng; lng <= finishLng; lng += 0.03) {
                searchers.add(new Searcher(lat, lng, Properties.getMarkerRadius(), Properties.getIconSearch32()));
            }
        }
        view.addObject(MapController.VIEW_SEARCHERS, searchers);
    }

    /**
     * Линейное распределение с использованием геодезического пересчета расстояния между координатами и
     * нахождения точек в определенном направлении на заданном расстоянии.
     *
     * @param boundingBox around region of the country or coordinates
     */
    @SuppressWarnings("unused")
    public void staticUniformGeodesicMarkersDistribution(BoundingBox boundingBox, ModelAndView view) {
        LOG.info("Static uniform distribution with geodesic calculation...");
        Bounds bounds = boundingBox.getBounds();
        List<Searcher> searchers = new ArrayList<>();
        List<BoundingBox> boundingBoxes = new ArrayList<>();
        boundingBoxes.add(boundingBox);
        Point southwestPoint = GeoMath.point(bounds.southwest.lat, bounds.southwest.lng);
        Point northwestPoint = GeoMath.point(bounds.northeast.lat, bounds.southwest.lng);

        double distanceLat = EarthCalc.getVincentyDistance(southwestPoint, northwestPoint);
        double neighborLatDistance = GeoMath.neighborDistance(southwestPoint, northwestPoint);
        for (int i = 0; neighborLatDistance * (i - 1) < distanceLat; i++) {
            Point startLat = EarthCalc.pointRadialDistance(southwestPoint, 0, neighborLatDistance * i);
            if (startLat.getLatitude() > northwestPoint.getLatitude()) {
                startLat = northwestPoint;
            }
            Point finishLng = GeoMath.point(startLat, bounds.northeast.lng);
            double distanceLng = EarthCalc.getVincentyDistance(startLat, finishLng);
            double neighborLngDistance = GeoMath.neighborDistance(startLat, finishLng);
            for (int j = 0; neighborLngDistance * (j - 1) < distanceLng; j++) {
                Point tmp = EarthCalc.pointRadialDistance(startLat, 90, neighborLngDistance * j);
                if (tmp.getLongitude() > finishLng.getLongitude()) {
                    tmp = finishLng;
                }
                searchers.add(new Searcher(tmp.getLatitude(), tmp.getLongitude(), Properties.getMarkerRadius(), Properties.getIconSearch32()));
            }
        }
        view.addObject(MapController.VIEW_BOUNDING_BOXES, boundingBoxes);
        view.addObject(MapController.VIEW_SEARCHERS, searchers);
    }

    public void dynamicQuadTreeGeodesicMarkersDistribution(BoundingBox boundingBox, ModelAndView view, PlaceType placeType) {
        LOG.info("Dynamic distribution with Quadtree inside boundingboxes and geodesic calculation...");

        List<Searcher> searchers = new ArrayList<>();
        List<BoundingBox> boundingBoxes = new ArrayList<>();
        boundingBoxes.add(boundingBox);
        Set<Place> places = new HashSet<>();
        placeSearcherCall = 0;
        radarSearchCall = 0;
        placesSearcher(boundingBox, placeType, boundingBoxes, searchers, places);
        LOG.info("Radar search called " + radarSearchCall + " times");
        LOG.info("Places search called " + placeSearcherCall + " times");
        if (places.isEmpty()) {
            LOG.error("Can't get places in with radar search help");
        }
        LOG.info("Places " + places.size() + ", searchers " + searchers.size() + ", boundingboxes " + boundingBoxes.size());
        PlaceDAO.insert(places);
        AdditionalDAO.insertBoundingBoxes(boundingBoxes, boundingBox.getRegion(), boundingBox.getCountry(), placeType.toString());
        AdditionalDAO.insertSearchers(searchers, boundingBox.getRegion(), boundingBox.getCountry(), placeType.toString());
        LOG.info("Places/Searchers/Boundingboxes inserted in Mongo database");
        view.addObject(MapController.VIEW_BOUNDING_BOXES, boundingBoxes);
        view.addObject(MapController.VIEW_SEARCHERS, searchers);
        view.addObject(MapController.VIEW_PLACES, places);
    }


    private void placesSearcher(BoundingBox bBox,
                                PlaceType placeType,
                                List<BoundingBox> boundingBoxes,
                                List<Searcher> searchers,
                                Set<Place> places) {
        ++placeSearcherCall;
        LatLng boxCenter = GeoMath.boundsCenter(bBox.getBounds());
        int rightRad = (int) Math.ceil(GeoMath.halfDiagonal(bBox.getBounds()));
        int leftRad = (int) Properties.getMarkerRadius();
        while (leftRad < rightRad) {
            try {
                int midRad;
                if (Math.abs(leftRad - rightRad) < Properties.getRadarSearchRadiusEps()) {
                    midRad = rightRad;
                    leftRad = rightRad;
                } else {
                    midRad = (leftRad + rightRad) / 2;
                }
                ++radarSearchCall;
                PlacesSearchResponse response = PlacesApi.radarSearchQuery(mapService.getGoogleContext(), boxCenter, midRad).type(placeType).await();
                if (midRad < rightRad && response.results.length > Properties.getMaxRadarSearchPlaces()) {
                    rightRad = midRad - 1;
                    continue;
                }
                if (midRad < rightRad && response.results.length < Properties.getMinRadarSearchPlaces()) {
                    leftRad = midRad + 1;
                    continue;
                }

                // add marker boxCenter
                searchers.add(new Searcher(boxCenter.lat, boxCenter.lng, midRad, Properties.getIconSearch48()));
                // add searched places in midRad
                for (PlacesSearchResult result : response.results) {
                    places.add(new Place(result.placeId, placeType.toString(), bBox.getRegion(), bBox.getCountry(),
                            new Searcher(result.geometry.location.lat, result.geometry.location.lng, 0, Properties.getIconGreen32())));
                }
                if (midRad == rightRad) {
                    break;
                }

                Bounds leftDownBounds = GeoMath.leftDownBoundingBox(boxCenter, bBox.getBounds());
                Bounds leftUpBounds = GeoMath.leftUpBoundingBox(boxCenter, bBox.getBounds());
                Bounds rightDownBounds = GeoMath.rightDownBoundingBox(boxCenter, bBox.getBounds());
                Bounds rightUpBounds = GeoMath.rightUpBoundingBox(boxCenter, bBox.getBounds());

                BoundingBox leftDownBBox = new BoundingBox(leftDownBounds, bBox.getRegion(), bBox.getCountry());
                BoundingBox leftUpBBox = new BoundingBox(leftUpBounds, bBox.getRegion(), bBox.getCountry());
                BoundingBox rightDownBBox = new BoundingBox(rightDownBounds, bBox.getRegion(), bBox.getCountry());
                BoundingBox rightUpBBox = new BoundingBox(rightUpBounds, bBox.getRegion(), bBox.getCountry());

                boundingBoxes.add(leftDownBBox);
                boundingBoxes.add(leftUpBBox);
                boundingBoxes.add(rightDownBBox);
                boundingBoxes.add(rightUpBBox);

                placesSearcher(leftDownBBox, placeType, boundingBoxes, searchers, places);
                placesSearcher(leftUpBBox, placeType, boundingBoxes, searchers, places);
                placesSearcher(rightDownBBox, placeType, boundingBoxes, searchers, places);
                placesSearcher(rightUpBBox, placeType, boundingBoxes, searchers, places);

                break;
            } catch (Exception e) {
                LOG.error("Radar search error " + e.getMessage());
            }
        }
    }

}
