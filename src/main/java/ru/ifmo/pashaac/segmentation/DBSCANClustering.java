package ru.ifmo.pashaac.segmentation;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import ru.ifmo.pashaac.common.GeoMath;
import ru.ifmo.pashaac.common.Properties;
import ru.ifmo.pashaac.common.primitives.Marker;

import javax.validation.constraints.NotNull;
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

    public static List<Marker> getDBScanClusters(Collection<Marker> collection, int neighborDistance, int minPlacesInCluster) {
        DBSCANClusterer<Marker> dbscanClusterer = new DBSCANClusterer<>(neighborDistance, minPlacesInCluster,  new GeoMath());
        final List<Cluster<Marker>> clusters = dbscanClusterer.cluster(collection);
        return clusters.stream()
                .map(cluster -> getClusterCenter(cluster.getPoints())).collect(Collectors.toList());
    }

    public static List<Marker> getDBScanClusters(Collection<Marker> collection) {
        return getDBScanClusters(collection, DEFAULT_NEIGHBOR_DISTANCE, DEFAULT_MIN_PLACES_IN_CLUSTER);
    }

    @NotNull
    private static Marker getClusterCenter(Collection<Marker> collection) {
        Marker maxDstMarker1 = null;
        Marker maxDstMarker2 = null;
        double diameter = 0;
        for (Marker marker1 : collection) {
            for (Marker marker2 : collection) {
                final double distance = GeoMath.distance(marker1.getLat(), marker1.getLng(), marker2.getLat(), marker2.getLng());
                if (distance > diameter) {
                    diameter = distance;
                    maxDstMarker1 = marker1;
                    maxDstMarker2 = marker2;
                }
            }
        }
        if (maxDstMarker1 == null) {
            throw new IllegalStateException("Set is too small " + collection.size());
        }
        final double latCenter = (maxDstMarker1.getLat() + maxDstMarker2.getLat()) / 2;
        final double lngCenter = (maxDstMarker1.getLng() + maxDstMarker2.getLng()) / 2;
        return new Marker(latCenter, lngCenter, diameter / 2, Properties.getIconKernel());
    }

}
