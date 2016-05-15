package ru.ifmo.pashaac.category;

import com.google.maps.model.LatLng;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import ru.ifmo.pashaac.common.BoundingBox;
import ru.ifmo.pashaac.common.GeoMath;
import ru.ifmo.pashaac.common.Properties;
import ru.ifmo.pashaac.common.Searcher;
import ru.ifmo.pashaac.foursquare.FoursquareDataDAO;
import ru.ifmo.pashaac.foursquare.FoursquarePlace;
import ru.ifmo.pashaac.foursquare.FoursquarePlaceType;
import ru.ifmo.pashaac.google.maps.GoogleDataDAO;
import ru.ifmo.pashaac.google.maps.GooglePlace;
import ru.ifmo.pashaac.google.maps.GooglePlaceType;
import ru.ifmo.pashaac.map.MapService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Культурно досуговые места
 * <p>
 * Created by Pavel Asadchiy
 * 07.05.16 12:55.
 */
public class Culture implements Category {

    public static final GooglePlaceType[] GOOGLE_PLACE_TYPES = {GooglePlaceType.MUSEUM, GooglePlaceType.PARK};
//            GooglePlaceType.CHURCH};
//            /* GooglePlaceType.ART_GALLERY, GooglePlaceType.LIBRARY */}; TODO: ???

    public static final FoursquarePlaceType[] FOURSQUARE_PLACE_TYPES = {FoursquarePlaceType.MUSEUM};
//            FoursquarePlaceType.THEATER, FoursquarePlaceType.PARK, FoursquarePlaceType.ART_GALLERY,
//            FoursquarePlaceType.CIRCUS, FoursquarePlaceType.CONCERT_HALL, FoursquarePlaceType.PUBLIC_ART,
//            FoursquarePlaceType.WATER_PARK, FoursquarePlaceType.BOTANICAL_GARDEN, FoursquarePlaceType.BRIDGE,
//            FoursquarePlaceType.CASTLE, FoursquarePlaceType.FOUNTAIN, FoursquarePlaceType.GARDEN, FoursquarePlaceType.PALACE};

    private final MapService mapService;
    private final BoundingBox boundingBox;
    private final KMeansPlusPlusClusterer<Searcher> clusterer;

    public Culture(MapService mapService, BoundingBox boundingBox) {
        this.mapService = mapService;
        this.boundingBox = boundingBox;
        this.clusterer = new KMeansPlusPlusClusterer<>(Properties.getKernelsDefaultNumber(), Properties.getKernelIterationsCount(), new GeoMath());
    }

    @Override
    public Set<GooglePlace> getGooglePlaces(boolean useSourceIcons) {
        return Arrays.stream(GOOGLE_PLACE_TYPES)
                .map(placeType -> {
                    GoogleDataDAO googleDataDAO = new GoogleDataDAO(placeType.name(), boundingBox.getCity(), boundingBox.getCountry());
                    if (!googleDataDAO.exist()) {
                        googleDataDAO.minePlaces(mapService, boundingBox);
                    }
                    return googleDataDAO.getPlaces(useSourceIcons);
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<FoursquarePlace> getFoursquarePlaces(boolean useSourceIcons) {
        return Arrays.stream(FOURSQUARE_PLACE_TYPES)
                .map(placeType -> {
                    FoursquareDataDAO foursquareDataDAO = new FoursquareDataDAO(placeType.name(), boundingBox.getCity(), boundingBox.getCountry());
                    if (!foursquareDataDAO.exist()) {
                        foursquareDataDAO.minePlaces(mapService, boundingBox);
                    }
                    return foursquareDataDAO.getPlaces(useSourceIcons);
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private List<Searcher> getKernels(List<CentroidCluster<Searcher>> clusters) {
        List<Searcher> kernels = new ArrayList<>();
        for (CentroidCluster<Searcher> result : clusters) {
            double rad = 0;
            LatLng center = new LatLng(result.getCenter().getPoint()[0], result.getCenter().getPoint()[1]);
            for (Searcher place : result.getPoints()) {
                rad = Math.max(rad, GeoMath.distance(center.lat, center.lng, place.getLat(), place.getLng()));
            }
//            kernels.add(new Searcher(center.lat, center.lng, rad, Properties.getIconKernel()));

            KMeansPlusPlusClusterer<Clusterable> clusterer1 = new KMeansPlusPlusClusterer<>(3, 1000, new GeoMath());
            List<CentroidCluster<Clusterable>> clusters1 = clusterer1.cluster(new HashSet<>(result.getPoints()));
            for (CentroidCluster<Clusterable> result1 : clusters1) {
                center = new LatLng(result1.getCenter().getPoint()[0], result1.getCenter().getPoint()[1]);
                rad = 1000; // TODO: what about radius around kernels?
//                for (Searcher place : result.getPoints()) {
//                    rad = Math.max(rad, GeoMath.distance(center.lat, center.lng, place.getLat(), place.getLng()));
//                }
                kernels.add(new Searcher(center.lat, center.lng, rad, Properties.getIconKernel()));
            }
        }
        return kernels;
    }

    @Override
    public List<Searcher> getKernels(boolean needClearing) {
        Collection<Searcher> collection = new HashSet<>();
        if (needClearing) {
            collection.addAll(GooglePlace.cleaner(getGooglePlaces(false)));
            collection.addAll(FoursquarePlace.cleaner(getFoursquarePlaces(false)));
        } else {
            collection.addAll(getGooglePlaces(false));
            collection.addAll(getFoursquarePlaces(false));
        }
        return getKernels(clusterer.cluster(collection));
    }

    @Override
    public List<Searcher> getGoogleKernels(boolean needClearing) {
        Collection<Searcher> collection = new HashSet<>();
        collection.addAll(needClearing ? GooglePlace.cleaner(getGooglePlaces(false)) : getGooglePlaces(false));
        return getKernels(clusterer.cluster(collection));
    }

    @Override
    public List<Searcher> getFoursquareKernels(boolean needClearing) {
        Collection<Searcher> collection = new HashSet<>();
        collection.addAll(needClearing ? FoursquarePlace.cleaner(getFoursquarePlaces(false)) : getFoursquarePlaces(false));
        return getKernels(clusterer.cluster(collection));
    }

}
