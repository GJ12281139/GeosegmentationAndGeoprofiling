package ru.ifmo.pashaac.segmentation;

import org.apache.log4j.Logger;
import ru.ifmo.pashaac.common.primitives.Cluster;
import ru.ifmo.pashaac.data.source.Place;

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

    public static List<Cluster> clustering(Algorithm algorithm, Collection<Place> places, int minSegmentRad, int maxSegmentRad, int segmentsCountPercent) {
        switch (algorithm) {
            case BLACK_HOLE_RANDOM:
                LOG.info("Clustering My BlackHole random algorithm");
                return new BlackHoleClustering(places, minSegmentRad, maxSegmentRad, segmentsCountPercent).getDarkHoleRandomAlgorithm();
            case BLACK_HOLE_TOP_RATING:
                LOG.info("Clustering My BlackHole top rating algorithm");
                return new BlackHoleClustering(places, minSegmentRad, maxSegmentRad, segmentsCountPercent).getDarkHoleTopRatingAlgorithm();
            case BLACK_HOLE_HIERARCHICAL:
                LOG.info("Clustering My BlackHole hierarchical algorithm");
//                return new BlackHoleClustering(places, minSegmentRad, maxSegmentRad, segmentsCountPercent).getDarkHoleHierarchicalAlgorithm();
            case DBSCAN:
                LOG.info("Clustering DBSCAN algorithm");
                return new DBSCANClustering(places, minSegmentRad, maxSegmentRad, segmentsCountPercent).getDBScanClusters();
            case KMEANSPP_MAXRAD:
                LOG.info("Clustering K-means++ algorithm with max radius");
                return new KMeansPlusPlusClustering(places).getClustersMaxRadius();

            case KMEANSPP_FILTER_SPLIT:
                LOG.info("Clustering K-means++ algorithm with cluster filter and splitting if need");
                return new KMeansPlusPlusClustering(places).getFiltersClustersWithConditions();
            case FUZZY_KMEANS_MAXRAD:
                LOG.info("Clustering Fuzzy K-means++ algorithm with max radius");
//                return new FuzzyKMeans(places).getClustersMaxRadius();
            case FUZZY_KMEANS_FILTER_SPLIT:
                LOG.info("Clustering Fuzzy K-means++ algorithm with cluster filter and splitting if need");
//                return new FuzzyKMeans(places).getFiltersClustersWithConditions();
            case MULTI_KMEANSPP_MAXRAD:
                LOG.info("Clustering Multi K-means++ algorithm with max radius");
//                return new MultiKMeansPlusPlusClustering(places).getClustersMaxRadius();
            case MULTI_KMEANSPP_FILTER_SPLIT:
                LOG.info("Clustering Multi K-means++ algorithm with cluster filter and splitting if need");
//                return new MultiKMeansPlusPlusClustering(places).getFiltersClustersWithConditions();
            default:
                throw new IllegalArgumentException("Incorrect algorithm value " + algorithm);
        }
    }

}
