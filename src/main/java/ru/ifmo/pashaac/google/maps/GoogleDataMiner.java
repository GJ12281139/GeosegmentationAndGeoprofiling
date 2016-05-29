package ru.ifmo.pashaac.google.maps;

import com.google.maps.PlacesApi;
import com.google.maps.model.Bounds;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResult;
import com.grum.geocalc.EarthCalc;
import com.grum.geocalc.Point;
import org.apache.log4j.Logger;
import ru.ifmo.pashaac.common.GeoMath;
import ru.ifmo.pashaac.common.Properties;
import ru.ifmo.pashaac.common.primitives.BoundingBox;
import ru.ifmo.pashaac.common.primitives.Marker;
import ru.ifmo.pashaac.map.MapService;

import java.util.*;


/**
 * Service get data from Google.Maps with API help
 * <p>
 * Created by Pavel Asadchiy
 * 08.05.16 14:47.
 */
public class GoogleDataMiner {

    private static final Logger LOG = Logger.getLogger(GoogleDataMiner.class);

    private final MapService mapService;
    private final List<BoundingBox> boundingBoxes;
    private final List<Marker> markers;
    private final Set<GooglePlace> places;
    private final GooglePlaceType googlePlaceType;

    public GoogleDataMiner(MapService mapService, GooglePlaceType googlePlaceType) {
        this.mapService = mapService;
        this.googlePlaceType = googlePlaceType;
        this.boundingBoxes = new ArrayList<>();
        this.markers = new ArrayList<>();
        this.places = new HashSet<>();
    }

    public Set<GooglePlace> getPlaces() {
        return places;
    }

    public List<Marker> getMarkers() {
        return markers;
    }

    public List<BoundingBox> getBoundingBoxes() {
        return boundingBoxes;
    }

    public void quadtreePlaceSearcher(BoundingBox boundingBox) {
        boundingBoxes.clear();
        markers.clear();
        places.clear();

        boundingBoxes.add(boundingBox);
        int googleMapsApiCallCounter = 0;
        for (int i = 0; i < boundingBoxes.size(); i++) {
            final BoundingBox bBox = boundingBoxes.get(i);
            LOG.info("Trying get data (" + googlePlaceType.name() + ") for boundingbox #" + i + "... " + bBox);
            LatLng boxCenter = GeoMath.boundsCenter(bBox.getBounds());
            int rRad = (int) Math.ceil(GeoMath.halfDiagonal(bBox.getBounds()));
            int lRad = Properties.getGoogleMapsPlacesSearchRadEps();
            while (lRad < rRad) {
                int mRad = rRad - lRad < Properties.getGoogleMapsPlacesSearchRadEps() ? rRad : (lRad + rRad) / 2;
                PlacesSearchResult[] searchResults = nearbySearch(boxCenter, mRad, googlePlaceType);
                ++googleMapsApiCallCounter;
                if (searchResults == null) {
                    markers.add(new Marker(boxCenter, mRad, Properties.getIconSearchError()));
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

                markers.add(new Marker(boxCenter, mRad, Properties.getIconSearch()));
                Arrays.stream(searchResults)
                        .forEach(place -> places.add(new GooglePlace(place, boundingBox, googlePlaceType)));
                LOG.info("Places size " + places.size() + " (+" + searchResults.length + ")");
                if (mRad < rRad) {
                    boundingBoxes.addAll(BoundingBox.getQuarters(bBox));
                }
                break;
            }
        }

        LOG.info("Google Maps API called for getting places with id " + googleMapsApiCallCounter + " times");
        LOG.info("BoundingBoxes search cycle called " + boundingBoxes.size() + " times");

    }

//    public void fullPlacesInformation(@Nullable String language) {
//        int[] googleMapsApiCallCounter = {0};
//        Set<GooglePlace> tmpPlaces = new HashSet<>(places);
//        places.clear();
//        tmpPlaces.stream()
//                .forEach(place -> {
//                    try {
//                        PlaceDetails details = language == null
//                                ? PlacesApi.placeDetails(mapService.getGoogleContext(), place.getId()).await()
//                                : PlacesApi.placeDetails(mapService.getGoogleContext(), place.getId()).language(language).await();
//                        ++googleMapsApiCallCounter[0];
//                        places.add(new GooglePlace(details, place));
//                    } catch (Exception e) {
//                        LOG.error("Can't get full place info, placeId = " + place.getId());
//                    }
//                });
//        LOG.info("Google Maps API called for getting full places info " + googleMapsApiCallCounter[0] + " times");
//    }

    private PlacesSearchResult[] nearbySearch(LatLng boxCenter, int mRad, GooglePlaceType googlePlaceType) {
        try {
            return PlacesApi.nearbySearchQuery(mapService.getGoogleContext(), boxCenter).radius(mRad).language("ru").type(googlePlaceType.getPlaceType()).await().results;
        } catch (Exception e) {
            LOG.error("Can't get google maps places, placeType = " + googlePlaceType);
            return null;
        }
    }

//    private PlacesSearchResult[] radarSearch(LatLng boxCenter, int mRad, GooglePlaceType googlePlaceType) {
//        try {
//            return PlacesApi.radarSearchQuery(mapService.getGoogleContext(), boxCenter, mRad).type(googlePlaceType.getPlaceType()).await().results;
//        } catch (Exception e) {
//            LOG.error("Can't get google maps places, placeType = " + googlePlaceType);
//            return null;
//        }
//    }

    @Deprecated
    @SuppressWarnings("unused")
    public void searchersUniformGeodesicDistribution(BoundingBox boundingBox) {
        boundingBoxes.clear();
        markers.clear();
        places.clear();

        LOG.info("Static uniform distribution with geodesic calculation...");
        boundingBoxes.add(boundingBox);
        Bounds bounds = boundingBox.getBounds();

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
                markers.add(new Marker(tmp.getLatitude(), tmp.getLongitude(), Properties.getDefaultSearcherRadius(), 0, Properties.getIconSearch()));
            }
        }
    }

    @Deprecated
    @SuppressWarnings("unused")
    public void searchersUniformDistribution(BoundingBox boundingBox) {
        boundingBoxes.clear();
        markers.clear();
        places.clear();

        LOG.info("Static uniform distribution with simple (without geodesic) calculation...");
        double startLat = boundingBox.getSouthwest().getLat();
        double finishLat = boundingBox.getNortheast().getLat();
        double startLng = boundingBox.getSouthwest().getLng();
        double finishLng = boundingBox.getNortheast().getLng();
        for (double lat = startLat; lat <= finishLat; lat += 0.02) {
            for (double lng = startLng; lng <= finishLng; lng += 0.03) {
                markers.add(new Marker(lat, lng, Properties.getDefaultSearcherRadius(), 0, Properties.getIconSearch()));
            }
        }
    }
}
