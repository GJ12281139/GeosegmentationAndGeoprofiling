package ru.ifmo.pashaac.segmentation;

import com.sun.istack.internal.NotNull;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import ru.ifmo.pashaac.common.GeoMath;
import ru.ifmo.pashaac.common.Properties;
import ru.ifmo.pashaac.common.Searcher;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Pavel Asadchiy
 * on 17.05.16 17:49.
 */
public class DBSCANClustering {

    public static final int DEFAULT_NEIGHBOR_DISTANCE = 300;
    public static final int DEFAULT_MIN_PLACES_IN_CLUSTER = 5;

    public static List<Searcher> getDBScanClusters(Collection<Searcher> collection, int neighborDistance, int minPlacesInCluster) {
        DBSCANClusterer<Searcher> dbscanClusterer = new DBSCANClusterer<>(neighborDistance, minPlacesInCluster,  new GeoMath());
        final List<Cluster<Searcher>> clusters = dbscanClusterer.cluster(collection);
        return clusters.stream()
                .map(cluster -> getClusterCenter(cluster.getPoints())).collect(Collectors.toList());
    }

    public static List<Searcher> getDBScanClusters(Collection<Searcher> collection) {
        return getDBScanClusters(collection, DEFAULT_NEIGHBOR_DISTANCE, DEFAULT_MIN_PLACES_IN_CLUSTER);
    }

    @NotNull
    private static Searcher getClusterCenter(Collection<Searcher> collection) {
        Searcher maxDstSearcher1 = null;
        Searcher maxDstSearcher2 = null;
        double diameter = 0;
        for (Searcher searcher1 : collection) {
            for (Searcher searcher2 : collection) {
                final double distance = GeoMath.distance(searcher1.getLat(), searcher1.getLng(), searcher2.getLat(), searcher2.getLng());
                if (distance > diameter) {
                    diameter = distance;
                    maxDstSearcher1 = searcher1;
                    maxDstSearcher2 = searcher2;
                }
            }
        }
        if (maxDstSearcher1 == null) {
            throw new IllegalStateException("Set is too small " + collection.size());
        }
        final double latCenter = (maxDstSearcher1.getLat() + maxDstSearcher2.getLat()) / 2;
        final double lngCenter = (maxDstSearcher1.getLng() + maxDstSearcher2.getLng()) / 2;
        return new Searcher(latCenter, lngCenter, diameter / 2, Properties.getIconKernel());
    }

}
