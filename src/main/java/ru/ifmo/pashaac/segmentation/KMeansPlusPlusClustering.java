package ru.ifmo.pashaac.segmentation;

import com.google.maps.model.LatLng;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import ru.ifmo.pashaac.common.GeoMath;
import ru.ifmo.pashaac.common.Properties;
import ru.ifmo.pashaac.common.primitives.Cluster;
import ru.ifmo.pashaac.common.primitives.Marker;

import java.util.*;
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

    public List<Cluster> getClustersMaxRadius() {
        return clusterer.cluster(places).stream()
                .map(cluster -> {
                    LatLng center = new LatLng(cluster.getCenter().getPoint()[0], cluster.getCenter().getPoint()[1]);
                    double maxRad = Cluster.getClusterRadius(center, cluster.getPoints());
                    return new Cluster(center.lat, center.lng, maxRad, Properties.getIconKernel(), cluster.getPoints());
                })
                .collect(Collectors.toList());
    }

    public List<Cluster> getFiltersClustersWithConditions(int splitClustersCount) {
        return clusterer.cluster(places).stream()
                .filter(clusterer -> clusterer.getPoints().size() > Properties.getClusterMinPlaces())
                .map(cluster -> {
                    LatLng center = new LatLng(cluster.getCenter().getPoint()[0], cluster.getCenter().getPoint()[1]);
                    double maxRad = Cluster.getClusterRadius(center, cluster.getPoints());
                    if (maxRad > Properties.getClusterMaxRadius()) {
                        return new KMeansPlusPlusClustering(cluster.getPoints(), splitClustersCount, Properties.getKernelIterationsCount())
                                .getFiltersClustersWithConditions(splitClustersCount);
                    }
                    return Collections.singletonList(new Cluster(center.lat, center.lng, maxRad, Properties.getIconKernel(), cluster.getPoints()));
                })
                .flatMap(Collection::stream)
                .filter(cluster -> cluster.getRad() > Properties.getClusterMinRadius())
                .collect(Collectors.toList());
    }

    public List<Cluster> getFiltersClustersWithConditions() {
        return getFiltersClustersWithConditions(2);
    }

    // My cool algorithm
    public List<Cluster> getOptimalClusters() {
        List<CentroidCluster<Marker>> kMeansClusters = clusterer.cluster(places);
        List<Cluster> kMeansMaxRadClusters = kMeansClusters.stream()
                .map(centroidCluster -> {
                    LatLng center = new LatLng(centroidCluster.getCenter().getPoint()[0], centroidCluster.getCenter().getPoint()[1]);
                    double maxRad = Cluster.getClusterRadius(center, centroidCluster.getPoints());
                    return new Cluster(center.lat, center.lng, maxRad, Properties.getIconKernel(), centroidCluster.getPoints());
                })
                .collect(Collectors.toList());
        return doMaxRatingClustering(kMeansMaxRadClusters);
    }

    private List<Cluster> doMaxRatingClustering(List<Cluster> maxRadClusters) {
        return maxRadClusters.stream()
                .map(cluster -> {
//                    LatLng centerOfMass = Cluster.getCenterOfMass(cluster.getMarkers());
                    // use cetner cluster instead center of mass
                    return doMaxRatingClustering(cluster.getLatLng(), cluster);
                })
                .collect(Collectors.toList());
    }

    private Cluster doMaxRatingClustering(LatLng center, Cluster cluster) {
        double bestRadius = 0;
        double bestCircleRating = 0;
        List<Marker> bestMarkers = new ArrayList<>();
        Random random = new Random();
        for (double rad = Properties.getClusterMinRadius(); rad < Properties.getClusterMaxRadius(); rad += 20 + random.nextInt(35)) {
            double rating = 0;
            List<Marker> markers = new ArrayList<>();
            for (Marker marker : cluster.getMarkers()) {
                if (GeoMath.insideCircle(center, rad, marker.getLatLng())) {
                    markers.add(marker);
                    rating += marker.getRating();
                }
            }
            rating *= Properties.getClusterMinRadius() * 1.0 / Properties.getClusterMaxRadius();
            if (markers.size() > Properties.getClusterMinPlaces() && rating / (Math.PI * rad * rad) > bestCircleRating) {
                bestCircleRating = rating / (Math.PI * rad * rad);
                bestRadius = rad;
                bestMarkers = markers;
            }
        }
        return new Cluster(center.lat, center.lng, bestRadius, Properties.getIconKernel(), bestMarkers)
                .setMessage("Segment rating " + bestCircleRating + " radius " + bestRadius);
    }

}