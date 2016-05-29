package ru.ifmo.pashaac.segmentation;

/**
 * Clustering/Segmentation algorithms
 *
 * Created by Pavel Asadchiy
 * on 25.05.16 21:32.
 */
public enum Algorithm {

    OPTIMAL_CLUSTERS,
    BLACK_HOLE_RANDOM,
    DBSCAN,
    KMEANSPP_MAXRAD,
    KMEANSPP_FILTER_SPLIT,
    FUZZY_KMEANS_MAXRAD,
    FUZZY_KMEANS_FILTER_SPLIT,
    MULTI_KMEANSPP_MAXRAD,
    MULTI_KMEANSPP_FILTER_SPLIT

}
