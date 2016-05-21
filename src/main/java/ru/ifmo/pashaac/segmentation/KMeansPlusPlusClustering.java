package ru.ifmo.pashaac.segmentation;

import com.google.maps.model.LatLng;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import ru.ifmo.pashaac.common.GeoMath;
import ru.ifmo.pashaac.common.Properties;
import ru.ifmo.pashaac.common.primitives.Cluster;
import ru.ifmo.pashaac.common.primitives.Marker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Pavel Asadchiy
 * on 17.05.16 17:52.
 */
public class KMeansPlusPlusClustering {

    private final Collection<Marker> places;
    private final KMeansPlusPlusClusterer<Marker> clusterer;

    public KMeansPlusPlusClustering(Collection<Marker> places, int kernelsCount, int iterationsCount) {
        this.places = places;
        this.clusterer = new KMeansPlusPlusClusterer<>(kernelsCount, iterationsCount, new GeoMath());
    }

    public KMeansPlusPlusClustering(Collection<Marker> places) {
        this(places, Properties.getKernelDefaultCount(), Properties.getKernelIterationsCount());
    }

    public List<Cluster> getClustersDefaultRadius() {
        return clusterer.cluster(places).stream()
                .map(cluster -> new Cluster(cluster.getCenter().getPoint()[0], cluster.getCenter().getPoint()[1],
                        Properties.getKernelDefaultRadius(), Properties.getIconKernel(), cluster.getPoints()))
                .collect(Collectors.toList());
    }

    public List<Cluster> getClustersMaxRadius() {
        return clusterer.cluster(places).stream()
                .map(cluster -> {
                    LatLng center = new LatLng(cluster.getCenter().getPoint()[0], cluster.getCenter().getPoint()[1]);
                    double maxRad = Cluster.getClusterRadius(center, cluster.getPoints());
                    return new Cluster(center.lat, center.lng, maxRad, Properties.getIconKernel(), cluster.getPoints());
                })
                .collect(Collectors.toList());
    }

    public List<Cluster> getClustersWithClearingAndBigCirclesClustering(int splitClustersCount) {
        return clusterer.cluster(places).stream()
                .filter(clusterer -> clusterer.getPoints().size() > Properties.getClusterMinPlaces())
                .map(cluster -> {
                    LatLng center = new LatLng(cluster.getCenter().getPoint()[0], cluster.getCenter().getPoint()[1]);
                    double maxRad = Cluster.getClusterRadius(center, cluster.getPoints());
                    if (maxRad > Properties.getClusterMaxRadius()) {
                        return new KMeansPlusPlusClustering(cluster.getPoints(), splitClustersCount, Properties.getKernelIterationsCount())
                                .getClustersWithClearingAndBigCirclesClustering(splitClustersCount);
                    }
                    return Collections.singletonList(new Cluster(center.lat, center.lng, maxRad, Properties.getIconKernel(), cluster.getPoints()));
                })
                .flatMap(Collection::stream)
                .filter(cluster -> cluster.getRad() > Properties.getClusterMinRadius())
                .collect(Collectors.toList());
    }

    public List<Cluster> getClustersWithClearingAndBigCircleClusteringLimitMaxClustersInCity() {
        List<Cluster> clusters = new ArrayList<>();
        for (int i = 0; i < Properties.getClusterMaxInCity(); i++) {
            clusters.addAll(getClustersWithClearingAndBigCirclesClustering(3));
        }
//        Collections.sort(clusters, (c1, c2) -> c2.compareTo(c1)); destiny
        Collections.sort(clusters, (c1, c2) -> Double.compare(c2.getRad(), c1.getRad())); // rad descend better
        List<Cluster> results = new ArrayList<>();
        for (Cluster cluster : clusters) {
            boolean flag = true;
            for (Cluster result : results) {
                if (GeoMath.distance(cluster.getLat(), cluster.getLng(), result.getLat(), result.getLng())
                        < cluster.getRad() + result.getRad() - Properties.getClusterMinRadius()) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                results.add(cluster);
            }
        }
        return results.stream().limit(Properties.getClusterMaxInCity()).collect(Collectors.toList());
    }

}
