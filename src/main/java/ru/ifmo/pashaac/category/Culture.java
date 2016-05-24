package ru.ifmo.pashaac.category;

import ru.ifmo.pashaac.common.primitives.BoundingBox;
import ru.ifmo.pashaac.common.primitives.Marker;
import ru.ifmo.pashaac.foursquare.FoursquareDataDAO;
import ru.ifmo.pashaac.foursquare.FoursquarePlace;
import ru.ifmo.pashaac.foursquare.FoursquarePlaceType;
import ru.ifmo.pashaac.google.maps.GoogleDataDAO;
import ru.ifmo.pashaac.google.maps.GooglePlace;
import ru.ifmo.pashaac.google.maps.GooglePlaceType;
import ru.ifmo.pashaac.map.MapService;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Культурно досуговые места
 * <p>
 * Created by Pavel Asadchiy
 * 07.05.16 12:55.
 */
public class Culture implements Category {

    private static final GooglePlaceType[] GOOGLE_PLACE_TYPES = {GooglePlaceType.MUSEUM, GooglePlaceType.PARK,
            GooglePlaceType.CHURCH};


    private static final FoursquarePlaceType[] FOURSQUARE_PLACE_TYPES = {FoursquarePlaceType.MUSEUM,
            FoursquarePlaceType.PARK, FoursquarePlaceType.PLAZA, FoursquarePlaceType.SCULPTURE_GARDEN,
            FoursquarePlaceType.SPIRTUAL_CENTER, FoursquarePlaceType.THEATER, FoursquarePlaceType.FOUNTAIN,
            FoursquarePlaceType.GARDEN, FoursquarePlaceType.PALACE, FoursquarePlaceType.CASTLE};

    private final MapService mapService;
    private final BoundingBox boundingBox;
    private final String city;
    private final String country;

    public Culture(MapService mapService, BoundingBox boundingBox) {
        this.mapService = mapService;
        this.boundingBox = boundingBox;
        this.city = boundingBox.getCity();
        this.country = boundingBox.getCountry();
    }

    @Override
    public Set<GooglePlace> getGooglePlaces(List<Integer> percents) {
        // TODO: add filtering like in getFoursquarePlaces
        return null;
//        return Arrays.stream(GOOGLE_PLACE_TYPES)
//                .map(placeType -> {
//                    GoogleDataDAO googleDataDAO = new GoogleDataDAO(placeType.name(), city, country);
//                    googleDataDAO.minePlacesIfNotExist(mapService, boundingBox);
//                    return googleDataDAO.getPlaces();
//                })
//                .flatMap(Collection::stream)
//                .filter(GooglePlace::filter)
//                .collect(Collectors.toSet());
    }

    @Override
    public Set<FoursquarePlace> getFoursquarePlaces(List<Integer> percents) {
        Set<FoursquarePlace> foursquarePlaces = new HashSet<>();
        for (int i = 0; i < FOURSQUARE_PLACE_TYPES.length; i++) {
            FoursquareDataDAO foursquareDataDAO = new FoursquareDataDAO(FOURSQUARE_PLACE_TYPES[i].name(), city, country);
            foursquareDataDAO.minePlacesIfNotExist(mapService, boundingBox);
            int percent = percents.size() > i ? percents.get(i) : 100;
            foursquarePlaces.addAll(FoursquarePlace.filterTopCheckinsPercent(foursquareDataDAO.getAllPlaces(), percent));
        }
        return FoursquarePlace.filterPlaces(foursquarePlaces, 90);
    }

    @Nonnull
    @Override
    public List<BoundingBox> getGoogleBoundingBoxes() {
        return Arrays.stream(GOOGLE_PLACE_TYPES)
                .map(placeType -> new GoogleDataDAO(placeType.name(), city, country).getBoundingBoxes())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Nonnull
    @Override
    public List<BoundingBox> getFoursquareBoundingBoxes() {
        return Arrays.stream(FOURSQUARE_PLACE_TYPES)
                .map(placeType -> new FoursquareDataDAO(placeType.name(), city, country).getBoundingBoxes())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public List<Marker> getClusters(final Collection<Marker> places) {
//        return new KMeansPlusPlusClustering(new HashSet<>(getFoursquarePlaces()))
//                .getKernelsWithClearingAndBigCircleClusteringTheBest().stream()
//                .map(cluster -> (Marker) cluster)
//                .collect(Collectors.toList());

//        return new BlackHoleClustering(places)
//                .getDarkHoleRandom().stream()
//                .map(cluster -> (Marker) cluster)
//                .collect(Collectors.toList());
        // TODO: what do with it?
        return null;
    }

}
