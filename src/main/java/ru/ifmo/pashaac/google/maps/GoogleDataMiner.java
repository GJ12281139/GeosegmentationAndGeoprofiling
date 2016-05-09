package ru.ifmo.pashaac.google.maps;

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
import ru.ifmo.pashaac.common.wrapper.Searcher;
import ru.ifmo.pashaac.map.MapController;
import ru.ifmo.pashaac.map.MapService;

import javax.annotation.Nullable;
import java.util.*;


/**
 * Service get data from Google.Maps with API help
 * <p>
 * Created by Pavel Asadchiy
 * 08.05.16 14:47.
 */
@Service
public class GoogleDataMiner {

    private static final Logger LOG = Logger.getLogger(GoogleDataMiner.class);

    @Autowired
    private final MapService mapService;
    private final List<BoundingBox> boundingBoxes;
    private final List<Searcher> searchers;
    private final Set<GooglePlace> places;

    public GoogleDataMiner(MapService mapService) {
        this.mapService = mapService;
        this.boundingBoxes = new ArrayList<>();
        this.searchers = new ArrayList<>();
        this.places = new HashSet<>();
    }

    public Set<GooglePlace> getPlaces() {
        return places;
    }

    public List<Searcher> getSearchers() {
        return searchers;
    }

    public List<BoundingBox> getBoundingBoxes() {
        return boundingBoxes;
    }

    public void quadtreePlaceSearcher(BoundingBox boundingBox, PlaceType placeType) {
        boundingBoxes.add(boundingBox);
        int googleMapsApiCallCounter = 0;
        for (int i = 0; i < boundingBoxes.size(); i++) {
            final BoundingBox bBox = boundingBoxes.get(i);
            LOG.info("Trying get data for boundingbox #" + i + "... " + bBox);
            LatLng boxCenter = GeoMath.boundsCenter(bBox.getBounds());
            int rRad = (int) Math.ceil(GeoMath.halfDiagonal(bBox.getBounds()));
            int lRad = (int) ru.ifmo.pashaac.common.Properties.getDefaultSearcherRadius();
            while (lRad < rRad) {
                int mRad = rRad - lRad < Properties.getGoogleMapsPlacesSearchRadEps() ? rRad : (lRad + rRad) / 2;
                PlacesSearchResult[] searchResults = radarSearch(boxCenter, mRad, placeType);
                ++googleMapsApiCallCounter;
                if (searchResults == null) {
                    searchers.add(new Searcher(boxCenter, mRad, Properties.getIconSearchError()));
                    break;
                }
                if (mRad < rRad && searchResults.length > Properties.getGoogleMapsMaxPlacesSearch()) {
                    rRad = mRad - 1;
                    continue;
                }
                if (mRad < rRad && searchResults.length < Properties.getGoogleMapsMinPlacesSearch()) {
                    lRad = mRad + 1;
                    continue;
                }

                searchers.add(new Searcher(boxCenter, mRad, Properties.getIconSearch()));
                Arrays.stream(searchResults)
                        .forEach(place -> places.add(new GooglePlace(place.placeId, placeType.name(), bBox,
                                new LatLng(place.geometry.location.lat, place.geometry.location.lng), Properties.getIconGreen32())));

                if (mRad < rRad) {
                    Bounds leftDownBounds = GeoMath.leftDownBoundingBox(boxCenter, bBox.getBounds());
                    Bounds leftUpBounds = GeoMath.leftUpBoundingBox(boxCenter, bBox.getBounds());
                    Bounds rightDownBounds = GeoMath.rightDownBoundingBox(boxCenter, bBox.getBounds());
                    Bounds rightUpBounds = GeoMath.rightUpBoundingBox(boxCenter, bBox.getBounds());

                    boundingBoxes.add(new BoundingBox(leftDownBounds, bBox.getCity(), bBox.getCountry()));
                    boundingBoxes.add(new BoundingBox(leftUpBounds, bBox.getCity(), bBox.getCountry()));
                    boundingBoxes.add(new BoundingBox(rightDownBounds, bBox.getCity(), bBox.getCountry()));
                    boundingBoxes.add(new BoundingBox(rightUpBounds, bBox.getCity(), bBox.getCountry()));
                }
                break;
            }
        }

        LOG.info("Google Maps API called for getting places with id " + googleMapsApiCallCounter + " times");
        LOG.info("BoundingBoxes search cycle called " + boundingBoxes.size() + " times");

    }

    public void fullPlacesInformation(@Nullable String language) {
        int[] googleMapsApiCallCounter = {0};
        Set<GooglePlace> tmpPlaces = new HashSet<>(places);
        tmpPlaces.stream()
                .forEach(place -> {
                    try {
                        PlaceDetails details = language == null
                                ? PlacesApi.placeDetails(mapService.getGoogleContext(), place.getId()).await()
                                : PlacesApi.placeDetails(mapService.getGoogleContext(), place.getId()).language(language).await();
                        ++googleMapsApiCallCounter[0];
                        places.remove(place);
                        places.add(new GooglePlace(details, place));
                    } catch (Exception e) {
                        LOG.error("Can't get full place info, placeId = " + place.getId());
                    }
                });
        LOG.info("Google Maps API called for getting full places info " + googleMapsApiCallCounter[0] + " times");
    }

    private PlacesSearchResult[] radarSearch(LatLng boxCenter, int mRad, PlaceType placeType) {
        try {
            return PlacesApi.radarSearchQuery(mapService.getGoogleContext(), boxCenter, mRad).type(placeType).await().results;
        } catch (Exception e) {
            LOG.error("Can't get google maps places, placeType = " + placeType);
            return null;
        }
    }

    @Deprecated
    @SuppressWarnings("unused")
    public void searchersUniformGeodesicDistribution(BoundingBox boundingBox, ModelAndView view) {
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
                searchers.add(new Searcher(tmp.getLatitude(), tmp.getLongitude(), Properties.getDefaultSearcherRadius(), Properties.getIconSearch()));
            }
        }
        view.addObject(MapController.VIEW_BOUNDING_BOXES, boundingBoxes);
        view.addObject(MapController.VIEW_SEARCHERS, searchers);
    }

    @Deprecated
    @SuppressWarnings("unused")
    public void searchersUniformDistribution(BoundingBox boundingBox, ModelAndView view) {
        LOG.info("Static uniform distribution with simple (without geodesic) calculation...");
        List<Searcher> searchers = new ArrayList<>();
        double startLat = boundingBox.getSouthwest().getLat();
        double finishLat = boundingBox.getNortheast().getLat();
        double startLng = boundingBox.getSouthwest().getLng();
        double finishLng = boundingBox.getNortheast().getLng();
        for (double lat = startLat; lat <= finishLat; lat += 0.02) {
            for (double lng = startLng; lng <= finishLng; lng += 0.03) {
                searchers.add(new Searcher(lat, lng, Properties.getDefaultSearcherRadius(), Properties.getIconSearch()));
            }
        }
        view.addObject(MapController.VIEW_SEARCHERS, searchers);
    }
}
