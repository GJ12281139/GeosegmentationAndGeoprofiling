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
import ru.ifmo.pashaac.segmentation.DarkHoleClustering;
import ru.ifmo.pashaac.segmentation.KmeansPlusPlusClustering;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Культурно досуговые места
 * <p>
 * Created by Pavel Asadchiy
 * 07.05.16 12:55.
 */
public class Culture implements Category {

    private static final GooglePlaceType[] GOOGLE_PLACE_TYPES = {GooglePlaceType.MUSEUM, GooglePlaceType.PARK};
//            GooglePlaceType.CHURCH};
//            /* GooglePlaceType.ART_GALLERY, GooglePlaceType.LIBRARY */}; TODO: ???

    private static final FoursquarePlaceType[] FOURSQUARE_PLACE_TYPES = {FoursquarePlaceType.MUSEUM,
            FoursquarePlaceType.THEATER, FoursquarePlaceType.PARK, FoursquarePlaceType.FOUNTAIN,
            FoursquarePlaceType.GARDEN, FoursquarePlaceType.PALACE}; //FoursquarePlaceType.ART_GALLERY,
//             FoursquarePlaceType.CONCERT_HALL, FoursquarePlaceType.PUBLIC_ART,
//            FoursquarePlaceType.WATER_PARK, FoursquarePlaceType.BOTANICAL_GARDEN, FoursquarePlaceType.BRIDGE,
//            FoursquarePlaceType.CASTLE, FoursquarePlaceType.FOUNTAIN, FoursquarePlaceType.GARDEN, FoursquarePlaceType.PALACE};

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
    public Set<GooglePlace> getGooglePlaces() {
        return Arrays.stream(GOOGLE_PLACE_TYPES)
                .map(placeType -> {
                    GoogleDataDAO googleDataDAO = new GoogleDataDAO(placeType.name(), city, country);
                    googleDataDAO.minePlacesIfNotExist(mapService, boundingBox);
                    return googleDataDAO.getPlaces();
                })
                .flatMap(Collection::stream)
                .filter(GooglePlace::filter)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<FoursquarePlace> getFoursquarePlaces() {
        final Set<FoursquarePlace> places = Arrays.stream(FOURSQUARE_PLACE_TYPES)
                .map(placeType -> {
                    FoursquareDataDAO foursquareDataDAO = new FoursquareDataDAO(placeType.name(), city, country);
                    foursquareDataDAO.minePlacesIfNotExist(mapService, boundingBox);
                    return foursquareDataDAO.getPlaces();
                })
                .flatMap(Collection::stream)
//                .filter(FoursquarePlace::filter)
                .collect(Collectors.toSet());
        return FoursquarePlace
                .filterTopCheckinsPercent(
                        FoursquarePlace.filterAverageEmptyAddress(
                                FoursquarePlace.filterLongDistancePlaces(places)), 85);
    }

    @Override
    public List<Marker> getClustersAllSources() {
        Collection<Marker> collection = new HashSet<>();
        collection.addAll(getGooglePlaces());
        collection.addAll(getFoursquarePlaces());
        return new KmeansPlusPlusClustering(collection)
                .getKernelsDefaultRadius().stream()
                .map(cluster -> (Marker) cluster)
                .collect(Collectors.toList());
    }

    @Override
    public List<Marker> getFoursquareClusters() {
//        return new KmeansPlusPlusClustering(new HashSet<>(getFoursquarePlaces()))
//                .getKernelsWithClearingAndBigCircleClusteringTheBest().stream()
//                .map(cluster -> (Marker) cluster)
//                .collect(Collectors.toList());

        return new DarkHoleClustering(new HashSet<>(getFoursquarePlaces()))
                .getDarkHoleRandom().stream()
                .map(cluster -> (Marker) cluster)
                .collect(Collectors.toList());
    }

    @Override
    public List<Marker> getGoogleClusters() {
        return new KmeansPlusPlusClustering(new HashSet<>(getGooglePlaces()))
                .getKernelsDefaultRadius().stream()
                .map(cluster -> (Marker) cluster)
                .collect(Collectors.toList());
    }

}
