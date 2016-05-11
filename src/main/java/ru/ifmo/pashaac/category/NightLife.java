package ru.ifmo.pashaac.category;

import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import ru.ifmo.pashaac.common.BoundingBox;
import ru.ifmo.pashaac.common.GeoMath;
import ru.ifmo.pashaac.common.Properties;
import ru.ifmo.pashaac.common.Searcher;
import ru.ifmo.pashaac.foursquare.FoursquarePlace;
import ru.ifmo.pashaac.foursquare.FoursquarePlaceType;
import ru.ifmo.pashaac.google.maps.GooglePlace;
import ru.ifmo.pashaac.google.maps.GooglePlaceType;
import ru.ifmo.pashaac.map.MapService;

import java.util.List;
import java.util.Set;

/**
 * Created by Pavel Asadchiy
 * 07.05.16 15:02.
 */
public class NightLife implements Category {

    public static final GooglePlaceType[] GOOGLE_PLACE_TYPES = {};

    public static final FoursquarePlaceType[] FOURSQUARE_PLACE_TYPES = {FoursquarePlaceType.NIGHTLIFE_SPOT, FoursquarePlaceType.BOWLING_GREEN};

    private final MapService mapService;
    private final BoundingBox boundingBox;
    private final KMeansPlusPlusClusterer<Searcher> clusterer;

    public NightLife(MapService mapService, BoundingBox boundingBox) {
        this.mapService = mapService;
        this.boundingBox = boundingBox;
        this.clusterer = new KMeansPlusPlusClusterer<>(Properties.getKernelsDefaultNumber(), Properties.getKernelIterationsCount(), new GeoMath());
    }

    @Override
    public Set<GooglePlace> getGooglePlaces() {
        return null;
    }

    @Override
    public Set<FoursquarePlace> getFoursquarePlaces() {
        return null;
    }

    @Override
    public List<Searcher> getKernels(boolean needClearing) {
        return null;
    }

    @Override
    public List<Searcher> getGoogleKernels(boolean needClearing) {
        return null;
    }

    @Override
    public List<Searcher> getFoursquareKernels(boolean needClearing) {
        return null;
    }

}
