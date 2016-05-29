package ru.ifmo.pashaac.segmentation;

import org.apache.log4j.Logger;
import ru.ifmo.pashaac.common.primitives.Cluster;
import ru.ifmo.pashaac.common.primitives.Marker;

import java.util.Collection;
import java.util.List;

/**
 * Proxy between ML algorithms and common server side
 * <p>
 * Created by Pavel Asadchiy
 * on 21.05.16 18:45.
 */
public class Segmentation {

    private static final Logger LOG = Logger.getLogger(Segmentation.class);

    public static List<Cluster> clustering(Algorithm algorithm, Collection<Marker> places) {
        switch (algorithm) {
            case OPTIMAL_CLUSTERS:
                LOG.info("Clustering K-measns++ optimal algorithm");
                return new KMeansPlusPlusClustering(places).getOptimalClusters();
            case BLACK_HOLE_RANDOM:
                LOG.info("Clustering MY BlackHole random algorithm");
                return new BlackHoleClustering(places).getDarkHoleRandom();
            case DBSCAN:
                LOG.info("Clustering DBSCAN algorithm");
                return new DBSCANClustering(places).getDBScanClusters();
            case KMEANSPP_MAXRAD:
                LOG.info("Clustering K-means++ algorithm with max radius");
                return new KMeansPlusPlusClustering(places).getClustersMaxRadius();
            case KMEANSPP_FILTER_SPLIT:
                LOG.info("Clustering K-means++ algorithm with cluster filter and splitting if need");
                return new KMeansPlusPlusClustering(places).getFiltersClustersWithConditions();
            case FUZZY_KMEANS_MAXRAD:
                LOG.info("Clustering Fuzzy K-means++ algorithm with max radius");
                return new FuzzyKMeans(places).getClustersMaxRadius();
            case FUZZY_KMEANS_FILTER_SPLIT:
                LOG.info("Clustering Fuzzy K-means++ algorithm with cluster filter and splitting if need");
                return new FuzzyKMeans(places).getFiltersClustersWithConditions();
            case MULTI_KMEANSPP_MAXRAD:
                LOG.info("Clustering Multi K-means++ algorithm with max radius");
                return new MultiKMeansPlusPlusClustering(places).getClustersMaxRadius();
            case MULTI_KMEANSPP_FILTER_SPLIT:
                LOG.info("Clustering Multi K-means++ algorithm with cluster filter and splitting if need");
                return new MultiKMeansPlusPlusClustering(places).getFiltersClustersWithConditions();
            default:
                throw new IllegalArgumentException("Incorrect algorithm value " + algorithm);
        }
    }

}
