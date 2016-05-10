package ru.ifmo.pashaac.category;

import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import ru.ifmo.pashaac.common.BoundingBox;
import ru.ifmo.pashaac.common.GeoMath;
import ru.ifmo.pashaac.common.Properties;
import ru.ifmo.pashaac.common.Searcher;
import ru.ifmo.pashaac.foursquare.FoursquareDataDAO;
import ru.ifmo.pashaac.foursquare.FoursquareDataMiner;
import ru.ifmo.pashaac.foursquare.FoursquarePlace;
import ru.ifmo.pashaac.foursquare.FoursquarePlaceType;
import ru.ifmo.pashaac.google.maps.GoogleDataDAO;
import ru.ifmo.pashaac.google.maps.GoogleDataMiner;
import ru.ifmo.pashaac.google.maps.GooglePlace;
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

    public static final PlaceType[] GOOGLE_PLACE_TYPES = {PlaceType.MUSEUM, PlaceType.PARK, PlaceType.ART_GALLERY,
            PlaceType.CHURCH, PlaceType.LIBRARY};

    public static final FoursquarePlaceType[] FOURSQUARE_PLACE_TYPES = {FoursquarePlaceType.MUSEUM,
            FoursquarePlaceType.THEATER, FoursquarePlaceType.PARK, FoursquarePlaceType.ART_GALLERY,
            FoursquarePlaceType.CIRCUS, FoursquarePlaceType.CONCERT_HALL, FoursquarePlaceType.PUBLIC_ART,
            FoursquarePlaceType.WATER_PARK, FoursquarePlaceType.BOTANICAL_GARDEN, FoursquarePlaceType.BRIDGE,
            FoursquarePlaceType.CASTLE, FoursquarePlaceType.FOUNTAIN, FoursquarePlaceType.GARDEN, FoursquarePlaceType.PALACE};

    private final MapService mapService;
    private final BoundingBox boundingBox;
    private final KMeansPlusPlusClusterer<Searcher> clusterer;

    public Culture(MapService mapService, BoundingBox boundingBox) {
        this.mapService = mapService;
        this.boundingBox = boundingBox;
        this.clusterer = new KMeansPlusPlusClusterer<>(Properties.getKernelsDefaultNumber(), Properties.getKernelIterationsCount(), new GeoMath());
    }

    @Override
    public Set<GooglePlace> getGooglePlaces() {
        return Arrays.stream(GOOGLE_PLACE_TYPES)
                .map(placeType -> {
                    GoogleDataDAO googleDataDAO = new GoogleDataDAO(placeType.name(), boundingBox.getCity(), boundingBox.getCountry());
                    if (!googleDataDAO.exist()) {
                        GoogleDataMiner googleDataMiner = new GoogleDataMiner(mapService, placeType);
                        googleDataMiner.quadtreePlaceSearcher(boundingBox);
                        googleDataMiner.fullPlacesInformation("ru");

                        googleDataDAO.insert(googleDataMiner.getPlaces());
                        googleDataDAO.recreate(googleDataMiner.getBoundingBoxes(), GoogleDataDAO.BOUNDINGBOX_SUFFIX);
                        googleDataDAO.recreate(googleDataMiner.getSearchers(), GoogleDataDAO.SEARCHER_SUFFIX);
                    }
                    return googleDataDAO.getPlaces();
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<FoursquarePlace> getFoursquarePlaces() {
        return Arrays.stream(FOURSQUARE_PLACE_TYPES)
                .map(placeType -> {
                    FoursquareDataDAO foursquareDataDAO = new FoursquareDataDAO(placeType.name(), boundingBox.getCity(), boundingBox.getCountry());
                    if (!foursquareDataDAO.exist()) {
                        FoursquareDataMiner foursquareDataMiner = new FoursquareDataMiner(mapService, placeType);
                        foursquareDataMiner.quadtreePlaceSearcher(boundingBox);

                        foursquareDataDAO.insert(foursquareDataMiner.getPlaces());
                        foursquareDataDAO.recreate(foursquareDataMiner.getBoundingBoxes(), FoursquareDataDAO.BOUNDINGBOX_SUFFIX);
                        foursquareDataDAO.recreate(foursquareDataMiner.getSearchers(), FoursquareDataDAO.SEARCHER_SUFFIX);
                    }
                    return foursquareDataDAO.getPlaces();
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
            kernels.add(new Searcher(center.lat, center.lng, rad, Properties.getIconKernel()));
        }
        return kernels;
    }

    @Override
    public List<Searcher> getKernels(boolean needClearing) {
        Collection<Searcher> collection = new HashSet<>();
        if (needClearing) {
            collection.addAll(GooglePlace.cleaner(getGooglePlaces()));
            collection.addAll(FoursquarePlace.cleaner(getFoursquarePlaces()));
        } else {
            collection.addAll(getGooglePlaces());
            collection.addAll(getFoursquarePlaces());
        }
        return getKernels(clusterer.cluster(collection));
    }

    @Override
    public List<Searcher> getGoogleKernels(boolean needClearing) {
        Collection<Searcher> collection = new HashSet<>();
        collection.addAll(needClearing ? GooglePlace.cleaner(getGooglePlaces()) : getGooglePlaces());
        return getKernels(clusterer.cluster(collection));
    }

    @Override
    public List<Searcher> getFoursquareKernels(boolean needClearing) {
        Collection<Searcher> collection = new HashSet<>();
        collection.addAll(needClearing ? FoursquarePlace.cleaner(getFoursquarePlaces()) : getFoursquarePlaces());
        return getKernels(clusterer.cluster(collection));
    }

    public static Set<FoursquarePlace> foursquareClearing(Set<FoursquarePlace> dirtyPlaces) {
        Set<FoursquarePlace> goodPlaces = new HashSet<>();
        for (FoursquarePlace dirtyPlace : dirtyPlaces) {
            int less500 = 0;
            int less750 = 0;
            int less1000 = 0;
            for (FoursquarePlace otherDirtyPlace : dirtyPlaces) {
                double dst = GeoMath.distance(dirtyPlace.getLat(), dirtyPlace.getLng(), otherDirtyPlace.getLat(), otherDirtyPlace.getLng());
                if (dst < 500) {
                    ++less500;
                }
                if (dst < 750) {
                    ++less750;
                }
                if (dst < 1000) {
                    ++less1000;
                }
            }
            if (less500 > 3 && less750 * 1.0 / less500 > 1.3) {
                goodPlaces.add(dirtyPlace);
            }
        }
        return goodPlaces;
    }
}
