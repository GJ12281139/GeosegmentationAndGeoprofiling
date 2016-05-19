package ru.ifmo.pashaac.segmentation;

import com.google.maps.model.LatLng;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import ru.ifmo.pashaac.common.GeoMath;
import ru.ifmo.pashaac.common.Properties;
import ru.ifmo.pashaac.common.Searcher;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Pavel Asadchiy
 * on 17.05.16 17:52.
 */
public class KmeansPlusPlusClustering {

    private final Collection<Searcher> places;
    private final KMeansPlusPlusClusterer<Searcher> clusterer;

    public KmeansPlusPlusClustering(Collection<Searcher> places, int kernelsCount, int iterationsCount) {
        this.places = places;
        this.clusterer = new KMeansPlusPlusClusterer<>(kernelsCount, iterationsCount, new GeoMath());
    }

    public KmeansPlusPlusClustering(Collection<Searcher> places) {
        this(places, Properties.getKernelDefaultCount(), Properties.getKernelIterationsCount());
    }

    public List<Searcher> getKernelsDefaultRadius() {
        return clusterer.cluster(places).stream()
                .map(CentroidCluster::getCenter)
                .map(center -> new Searcher(center.getPoint()[0], center.getPoint()[1],
                        Properties.getKernelDefaultRadius(), Properties.getIconKernel()))
                .collect(Collectors.toList());
    }

    public List<Searcher> getKernelsMaxRadius() {
        return clusterer.cluster(places).stream()
                .map(cluster -> {
                    LatLng center = new LatLng(cluster.getCenter().getPoint()[0], cluster.getCenter().getPoint()[1]);
                    double rad = 0;
                    for (Searcher place : cluster.getPoints()) {
                        rad = Math.max(rad, GeoMath.distance(center.lat, center.lng, place.getLat(), place.getLng()));
                    }
                    return new Searcher(center.lat, center.lng, rad, Properties.getIconKernel());
                })
                .collect(Collectors.toList());
    }

    public List<Searcher> getKernelsMaxRadiusWithClearing() {
        return clusterer.cluster(places).stream()
                .filter(clusterer -> clusterer.getPoints().size() > Properties.getClusterMinPlaces())
                .map(cluster -> {
                    LatLng center = new LatLng(cluster.getCenter().getPoint()[0], cluster.getCenter().getPoint()[1]);
                    double rad = 0;
                    for (Searcher place : cluster.getPoints()) {
                        rad = Math.max(rad, GeoMath.distance(center.lat, center.lng, place.getLat(), place.getLng()));
                    }
                    return new Searcher(center.lat, center.lng, rad, Properties.getIconKernel());
                })
                .collect(Collectors.toList());
    }

    public List<Searcher> getKernelsMaxRadiusWithClearingAndBigCircleClustering() {
        int splitClustersCount = 3;
        return clusterer.cluster(places).stream()
                .filter(clusterer -> clusterer.getPoints().size() > 1)
                .map(cluster -> {
                    LatLng center = new LatLng(cluster.getCenter().getPoint()[0], cluster.getCenter().getPoint()[1]);
                    double maxRad = 0;
                    for (Searcher place : cluster.getPoints()) {
                        maxRad = Math.max(maxRad, GeoMath.distance(center.lat, center.lng, place.getLat(), place.getLng()));
                    }

                    if (maxRad > Properties.getClusterMaxRadius()) {
                        if (cluster.getPoints().size() > Properties.getClusterMinPlaces()) {
                            return new KmeansPlusPlusClustering(cluster.getPoints(), splitClustersCount, Properties.getKernelIterationsCount())
                                    .getKernelsMaxRadiusWithClearingAndBigCircleClustering();
                        } else {
                            return null;
                        }
                    }
                    if (maxRad < Properties.getClusterMinRadius()) {
                        return null;
                    }
                    if (cluster.getPoints().size() < Properties.getClusterMinPlaces()) {
                        return null;
                    }
                    return Collections.singletonList(new Searcher(center.lat, center.lng, maxRad, Properties.getIconKernel()));
                })
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

}
