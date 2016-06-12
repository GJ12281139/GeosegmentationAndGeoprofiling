package ru.ifmo.pashaac.segmentation;

/**
 * Clustering/Segmentation algorithms
 *
 * Created by Pavel Asadchiy
 * on 25.05.16 21:32.
 */
public enum Algorithm {

    BLACK_HOLE_TOP_RATING,
    BLACK_HOLE_RANDOM,
    BLACK_HOLE_HIERARCHICAL,
    DBSCAN,
    KMEANSPP_MAXRAD,
    KMEANSPP_FILTER_SPLIT,
    FUZZY_KMEANS_MAXRAD,
    FUZZY_KMEANS_FILTER_SPLIT,
    MULTI_KMEANSPP_MAXRAD,
    MULTI_KMEANSPP_FILTER_SPLIT

}
