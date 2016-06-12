package ru.ifmo.pashaac.segmentation;

import com.google.maps.model.LatLng;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import ru.ifmo.pashaac.common.GeoMath;
import ru.ifmo.pashaac.common.Properties;
import ru.ifmo.pashaac.data.source.Place;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Pavel Asadchiy
 * on 17.05.16 17:49.
 */
public class DBSCANClustering {

    public static final int DEFAULT_MIN_PLACES_IN_CLUSTER = 5;

    private final List<Place> places;
    private final int minSegmentRad;
    private final int maxSegmentRad;
    private final int segmentsCountPercent;

    public DBSCANClustering(Collection<Place> places, int minSegmentRad, int maxSegmentRad, int segmentsCountPercent) {
        this.places = new ArrayList<>(places);
        this.minSegmentRad = minSegmentRad;
        this.maxSegmentRad = maxSegmentRad;
        this.segmentsCountPercent = segmentsCountPercent;
    }

    public List<ru.ifmo.pashaac.common.primitives.Cluster > getDBScanClusters(int neighborDistance, int minPlacesInCluster) {
        DBSCANClusterer<Place> dbscanClusterer = new DBSCANClusterer<>(neighborDistance, minPlacesInCluster,  new GeoMath());
        List<Cluster<Place>> clusters = dbscanClusterer.cluster(places);
        List<ru.ifmo.pashaac.common.primitives.Cluster> result = clusters.stream()
                .map(cluster -> getCluster(cluster.getPoints())).collect(Collectors.toList());
        return result.stream()
                .sorted((c1, c2) -> Double.compare(c2.getRating(), c1.getRating())) // descend
                .limit((long) Math.ceil(segmentsCountPercent * result.size() * 1.0 / 100))
                .collect(Collectors.toList());
    }

    public List<ru.ifmo.pashaac.common.primitives.Cluster> getDBScanClusters() {
        return getDBScanClusters((minSegmentRad + maxSegmentRad) / 2, DEFAULT_MIN_PLACES_IN_CLUSTER);
    }


    private static ru.ifmo.pashaac.common.primitives.Cluster getCluster(List<Place> places) {
        LatLng center = ru.ifmo.pashaac.common.primitives.Cluster.getCenterOfMass(places);
        double clusterRadius = ru.ifmo.pashaac.common.primitives.Cluster.getClusterRadius(places);
        return new ru.ifmo.pashaac.common.primitives.Cluster(center.lat, center.lng, clusterRadius, Properties.getIconSearch(),
                places);
    }

}
