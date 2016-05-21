package ru.ifmo.pashaac.segmentation;

import org.apache.log4j.Logger;
import ru.ifmo.pashaac.common.primitives.Cluster;
import ru.ifmo.pashaac.common.primitives.Marker;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Pavel Asadchiy
 * on 21.05.16 18:45.
 */
public class Segmentation {

    private static final Logger LOG = Logger.getLogger(Segmentation.class);

    public static List<Cluster> getDarkHoleClusters(final Collection<Marker> places) {
        LOG.info("Clustering BlackHole my algorithm");
        return new BlackHoleClustering(places).getDarkHoleRandom();
    }

    public static List<Cluster> getDBSCANClusters(final Collection<Marker> places) {
        LOG.info("Clustering DBSCAN algorithm");
        return new DBSCANClustering(places).getDBScanClusters();
    }

    public static List<Cluster> getKMeansPPClustersWithClearingAndBigCirclesClustering(final Collection<Marker> places) {
        LOG.info("Clustering K-means++ algorithm with splitting big circles only");
        return new KMeansPlusPlusClustering(places).getClustersWithClearingAndBigCirclesClustering(3);
    }

    public static List<Cluster> getKMeansPPClustersWithClearingAndBigCircleClusteringLimitMaxClustersInCity(final Collection<Marker> places) {
        LOG.info("Clustering K-means++ algorithm with splitting big circles and limited out");
        return new KMeansPlusPlusClustering(places).getClustersWithClearingAndBigCircleClusteringLimitMaxClustersInCity();
    }

    public static List<Cluster> getMultiKMeansPPClustersWithClearingAndBigCircleClustering(final Collection<Marker> places) {
        LOG.info("Clustering MultiK-means++ algorithm with splitting big circles only");
        return new MultiKMeansPlusPlusClustering(places).getClustersWithClearingAndBigCirclesClustering(3);
    }

    public static List<Cluster> getFuzzyKMeansClustersWithClearingAndBigCircleClustering(final Collection<Marker> places) {
        LOG.info("Clustering FuzzyK-means algorithm with splitting big circles only");
        return new FuzzyKMeans(places).getClustersWithClearingAndBigCirclesClustering(2);
    }

    public static List<Cluster> getClustersByString(@Nullable String mlAlgorithm, final Collection<Marker> places) {
        if (mlAlgorithm == null) {
            return new ArrayList<>();
        }
        if (mlAlgorithm.toLowerCase().contains("blackhole")) {
            return getDarkHoleClusters(places);
        }
        if (mlAlgorithm.toLowerCase().contains("dbscan")) {
            return getDBSCANClusters(places);
        }
        if (mlAlgorithm.toLowerCase().contains("fuzzy") && mlAlgorithm.toLowerCase().contains("kmeans")) {
            return getFuzzyKMeansClustersWithClearingAndBigCircleClustering(places);
        }
        if (mlAlgorithm.toLowerCase().contains("multi") && mlAlgorithm.toLowerCase().contains("kmeans")) {
            return getMultiKMeansPPClustersWithClearingAndBigCircleClustering(places);
        }
        if (mlAlgorithm.toLowerCase().contains("kmeans") && mlAlgorithm.toLowerCase().contains("limit")) {
            return getKMeansPPClustersWithClearingAndBigCircleClusteringLimitMaxClustersInCity(places);
        }
        if (mlAlgorithm.toLowerCase().contains("kmeans")) {
            return getKMeansPPClustersWithClearingAndBigCirclesClustering(places);
        }
        return new ArrayList<>();
    }

}
