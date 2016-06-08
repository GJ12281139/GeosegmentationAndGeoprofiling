package ru.ifmo.pashaac.data.source;

import com.google.maps.PlacesApi;
import com.google.maps.model.Bounds;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResult;
import com.grum.geocalc.EarthCalc;
import com.grum.geocalc.Point;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.Result;
import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.VenuesSearchResult;
import org.apache.log4j.Logger;
import ru.ifmo.pashaac.common.GeoMath;
import ru.ifmo.pashaac.common.Properties;
import ru.ifmo.pashaac.common.UserDAO;
import ru.ifmo.pashaac.common.primitives.BoundingBox;
import ru.ifmo.pashaac.common.primitives.Cluster;
import ru.ifmo.pashaac.common.primitives.Marker;
import ru.ifmo.pashaac.data.source.foursquare.FoursquarePlaceType;
import ru.ifmo.pashaac.data.source.google.maps.GooglePlaceType;
import ru.ifmo.pashaac.map.MapService;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Pavel Asadchiy
 * on 31.05.16 18:57.
 */
public class DataMiner {

    private static final Logger LOG = Logger.getLogger(DataMiner.class);

    private final MapService mapService;
    private final List<BoundingBox> boundingBoxes;
    private final List<Marker> searchers;
    private final Set<Place> places;
    private final String source;
    private final String placeType;

    public DataMiner(MapService mapService,
                     String source,
                     String placeType) {
        this.mapService = mapService;
        this.boundingBoxes = new ArrayList<>(500);
        this.searchers = new ArrayList<>(500);
        this.places = new HashSet<>(1000);
        this.source = source;
        this.placeType = placeType;
    }

    public void quadtreePlaceSearcher(BoundingBox boundingBox) {
        boundingBoxes.clear();
        searchers.clear();
        places.clear();

        boundingBoxes.add(boundingBox);
        int apiCallCounter = 0;
        String startTime = UserDAO.getTime();

        for (int i = 0; i < boundingBoxes.size(); i++) {
            BoundingBox bBox = boundingBoxes.get(i);
            LatLng bBoxCenter = GeoMath.boundsCenter(bBox.getBounds());
            LOG.info("Trying to get " + source + " data (" + placeType + ") for boundingbox #" + (i + 1) + "...");
            int bBoxRad = (int) Math.ceil(GeoMath.halfDiagonal(bBox.getBounds()));
            PlacesSearchResult[] googlePlaces = Place.GOOGLE_MAPS_SOURCE.equals(source) // max 20
                    ? nearbySearch(bBoxCenter, bBoxRad , GooglePlaceType.valueOf(placeType)) : null;
            CompactVenue[] foursquarePlaces = Place.FOURSQUARE_SOURCE.equals(source)    // max 30
                    ? venuesSearch(bBoxCenter, bBoxRad, FoursquarePlaceType.valueOf(placeType)) : null;
            ++apiCallCounter;
            if (foursquarePlaces != null && foursquarePlaces.length == 30 || googlePlaces != null && googlePlaces.length == 20) {
                boundingBoxes.addAll(BoundingBox.getQuarters(bBox));
                continue;
            }
            searchers.add(new Marker(bBoxCenter, bBoxRad, Properties.getIconSearch()));
            if (googlePlaces != null) {
                places.addAll(Arrays.stream(googlePlaces)
                        .parallel()
                        .map(placesSearchResult -> new Place(placesSearchResult, boundingBox, GooglePlaceType.valueOf(placeType)))
                        .collect(Collectors.toSet()));
                LOG.info("(" + Place.GOOGLE_MAPS_SOURCE + ") places " + places.size() + ", was searched " + googlePlaces.length);
            }
            if (foursquarePlaces != null) {
                places.addAll(Arrays.stream(foursquarePlaces)
                        .parallel()
                        .map(venue -> new Place(venue, boundingBox, FoursquarePlaceType.valueOf(placeType)))
                        .collect(Collectors.toSet()));
                LOG.info("(" + Place.FOURSQUARE_SOURCE + ") places " + places.size() + ", was searched " + foursquarePlaces.length);
            }
        }
        LOG.info(source + " API called " + apiCallCounter + " times");
        UserDAO.insert(source, placeType, places.size(), boundingBox.getCity(), apiCallCounter, startTime, UserDAO.getTime());
        LOG.info("BoundingBoxes search cycle called " + boundingBoxes.size() + " times");
    }

    private PlacesSearchResult[] nearbySearch(LatLng boxCenter, int mRad, GooglePlaceType googlePlaceType) {
        try {
            return PlacesApi.nearbySearchQuery(mapService.getGoogleContext(), boxCenter).radius(mRad).language("ru").type(googlePlaceType.getPlaceType()).await().results;
        } catch (Exception e) {
            LOG.error("Can't get google maps places, placeType = " + googlePlaceType);
            try {
                Thread.sleep(1000);
                return PlacesApi.nearbySearchQuery(mapService.getGoogleContext(), boxCenter).radius(mRad).language("ru").type(googlePlaceType.getPlaceType()).await().results;
            } catch (Exception e1) {
                LOG.error("Can't get google maps places AGAIN, placeType = " + googlePlaceType);
                return null;
            }
        }
    }

    /**
     * P.S. for getting full place info use CompleteVenue completeVenue = foursquareApi.venue(venue.getId()).getResult();
     *
     * @param location            - current location/search center
     * @param foursquarePlaceType - wrapper for categoryId
     * @return - compact search result array
     */
    @Nullable
    private CompactVenue[] venuesSearch(LatLng location, int rad, FoursquarePlaceType foursquarePlaceType) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("ll", location.lat + "," + location.lng);
            params.put("intent", "browse");
            params.put("categoryId", foursquarePlaceType.getCategoryId());
            params.put("radius", String.valueOf(rad));
            Result<VenuesSearchResult> venuesSearchResult = mapService.getFoursquareApi().venuesSearch(params);
            if (venuesSearchResult.getMeta().getCode() == 200) {
                return venuesSearchResult.getResult().getVenues();
            } else {
                LOG.error("Venues search return code " + venuesSearchResult.getMeta().getCode());
                Thread.sleep(1000);
                LOG.info("Trying repeat request...");
                venuesSearchResult = mapService.getFoursquareApi().venuesSearch(params);
                if (venuesSearchResult.getMeta().getCode() == 200) {
                    LOG.info("Repeated request success");
                    return venuesSearchResult.getResult().getVenues();
                }
                return null;
            }
        } catch (FoursquareApiException e) {
            LOG.error("Error trying get foursquare venues, " + e.getMessage());
            return null;
        } catch (InterruptedException e) {
            LOG.error("Error trying get foursquare venues, " + e.getMessage());
            return null;
        }
    }


    @Deprecated
    @SuppressWarnings("unused")
    public void searchersUniformGeodesicDistribution(BoundingBox boundingBox) {
        boundingBoxes.clear();
        searchers.clear();
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
            Point finishLng = GeoMath.point(startLat.getLatitude(), bounds.northeast.lng);
            double distanceLng = EarthCalc.getVincentyDistance(startLat, finishLng);
            double neighborLngDistance = GeoMath.neighborDistance(startLat, finishLng);
            for (int j = 0; neighborLngDistance * (j - 1) < distanceLng; j++) {
                Point tmp = EarthCalc.pointRadialDistance(startLat, 90, neighborLngDistance * j);
                if (tmp.getLongitude() > finishLng.getLongitude()) {
                    tmp = finishLng;
                }
                searchers.add(new Marker(tmp.getLatitude(), tmp.getLongitude(), Properties.getDefaultSearcherRadius(), Properties.getIconSearch()));
            }
        }

        int diff = 10;
        for (int i = 0; i < searchers.size() - diff; i++) {
            if (searchers.get(i).getLng() < searchers.get(i + diff).getLng()) {
                boundingBoxes.add(new BoundingBox(searchers.get(i).getLatLng(),
                        searchers.get(i + diff).getLatLng(), boundingBox.getCity(), boundingBox.getCountry()));
            }
        }
    }

    public void miner(List<Cluster> clusters) {
        places.clear();
        int iteration = 0;
        for (Cluster cluster : clusters) {
            CompactVenue[] foursquarePlaces = venuesSearch(cluster.getLatLng(), (int) cluster.getRad(), FoursquarePlaceType.valueOf(placeType));
            places.addAll(Arrays.stream(foursquarePlaces)
                    .map(venue -> new Place(venue, boundingBoxes.get(0), FoursquarePlaceType.valueOf(placeType)))
                    .collect(Collectors.toSet()));
            LOG.info("Places size " + places.size() + " (was searched " + foursquarePlaces.length + ") iterations " + ++iteration);
        }
    }

    @Deprecated
    @SuppressWarnings("unused")
    public void searchersUniformDistribution(BoundingBox boundingBox) {
        boundingBoxes.clear();
        searchers.clear();
        places.clear();

        LOG.info("Static uniform distribution with simple (without geodesic) calculation...");
        double startLat = boundingBox.getSouthwest().getLat();
        double finishLat = boundingBox.getNortheast().getLat();
        double startLng = boundingBox.getSouthwest().getLng();
        double finishLng = boundingBox.getNortheast().getLng();
        for (double lat = startLat; lat <= finishLat; lat += 0.02) {
            for (double lng = startLng; lng <= finishLng; lng += 0.03) {
                searchers.add(new Marker(lat, lng, Properties.getDefaultSearcherRadius(), Properties.getIconSearch()));
            }
        }
    }

    public List<BoundingBox> getBoundingBoxes() {
        return boundingBoxes;
    }

    public List<Marker> getSearchers() {
        return searchers;
    }

    public Set<Place> getPlaces() {
        return places;
    }
}
