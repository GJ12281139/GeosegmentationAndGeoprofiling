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
        LOG.info("Clustering DarkHole algorithm");
        return new DarkHoleClustering(places).getDarkHoleRandom();
    }

    public static List<Cluster> getDBSCANClusters(final Collection<Marker> places) {
        LOG.info("Clustering DBSCAN algorithm");
        return new DBSCANClustering(places).getDBScanClusters();
    }

    public static List<Cluster> getKmeansPPClustersDefaultRadius(final Collection<Marker> places) {
        LOG.info("Clustering K-means++ algorithm with default radius");
        return new KmeansPlusPlusClustering(places).getClustersDefaultRadius();
    }

    public static List<Cluster> getKmeansPPClustersMaxRadius(final Collection<Marker> places) {
        LOG.info("Clustering K-means++ algorithm with max radius in cluster visualisation");
        return new KmeansPlusPlusClustering(places).getClustersMaxRadius();
    }

    public static List<Cluster> getKmeansPPClustersWithClearingAndBigCirclesClustering(final Collection<Marker> places) {
        LOG.info("Clustering K-means++ algorithm with splitting big circles only");
        return new KmeansPlusPlusClustering(places).getClustersWithClearingAndBigCirclesClustering(3);
    }

    public static List<Cluster> getClustersWithClearingAndBigCircleClusteringLimitMaxClustersInCity(final Collection<Marker> places) {
        LOG.info("Clustering K-means++ algorithm with splitting big circles and limited out");
        return new KmeansPlusPlusClustering(places).getClustersWithClearingAndBigCircleClusteringLimitMaxClustersInCity();
    }

    public static List<Cluster> getClustersByString(@Nullable String mlAlgorithm, final Collection<Marker> places) {
        if (mlAlgorithm == null) {
            return new ArrayList<>();
        }
        if (mlAlgorithm.toLowerCase().contains("darkhole")) {
            return getDarkHoleClusters(places);
        }
        if (mlAlgorithm.toLowerCase().contains("dbscan")) {
            return getDBSCANClusters(places);
        }
        if (mlAlgorithm.toLowerCase().contains("kmeans") && mlAlgorithm.toLowerCase().contains("limit")) {
            return getClustersWithClearingAndBigCircleClusteringLimitMaxClustersInCity(places);
        }
        if (mlAlgorithm.toLowerCase().contains("kmeans")) {
            return getKmeansPPClustersWithClearingAndBigCirclesClustering(places);
        }
        return new ArrayList<>();
    }

}
