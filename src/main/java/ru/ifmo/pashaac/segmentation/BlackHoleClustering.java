package ru.ifmo.pashaac.segmentation;

import com.google.maps.model.LatLng;
import ru.ifmo.pashaac.common.GeoMath;
import ru.ifmo.pashaac.common.Properties;
import ru.ifmo.pashaac.common.primitives.Cluster;
import ru.ifmo.pashaac.common.primitives.Marker;

import java.util.*;

/**
 * Created by Pavel Asadchiy
 * on 19.05.16 9:07.
 */
public class BlackHoleClustering {

    private final Collection<Marker> places;

    public BlackHoleClustering(final Collection<Marker> places) {
        this.places = places;
    }

    public List<Cluster> getDarkHoleRandom() {
        List<Marker> tmp = new ArrayList<>(places);
        List<Cluster> answer = new ArrayList<>();
        Collections.shuffle(tmp);
        while (!tmp.isEmpty()) {
            Marker marker = getLongDistanceFromClustersMarker(tmp, answer);
            if (marker == null) {
                break;
            }
            tmp.remove(marker);
            List<Marker> cluster = new ArrayList<>(Arrays.asList(marker));
            Marker nearestMarker = null;
            while (!tmp.isEmpty() && Cluster.getClusterRadius(cluster) < Properties.getClusterMaxRadius()) {
                nearestMarker = getNearMarker(Cluster.getCenterOfMass(cluster), tmp); // getNearestMarker(Cluster.getCenterOfMass(cluster), tmp);
                cluster.add(nearestMarker);
                tmp.remove(nearestMarker);
            }
            if (nearestMarker != null) {
                cluster.remove(nearestMarker);
            }
            if (cluster.size() > Properties.getClusterMinPlaces() && Cluster.getClusterRadius(cluster) > Properties.getClusterMinRadius()) {
                LatLng center = Cluster.getCenterOfMass(cluster);
                double radius = Cluster.getClusterRadius(cluster);
                answer.add(new Cluster(center.lat, center.lng, radius, Properties.getIconKernel(), cluster)
                        .setMessage("Segment rating " + -1 + " radius " + radius));
            }
            if (answer.size() > Properties.getClusterMaxInCity()) {
                break;
            }
        }
        return answer;
    }

    private Marker getLongDistanceFromClustersMarker(final List<Marker> tmp, final List<Cluster> clusters) {
        for (Marker marker : tmp) {
            if (minClusterDistance(marker.getLatLng(), clusters) > Properties.getClusterMaxRadius() * 1.4) {
                return marker;
            }
        }
        return null;
    }

    private Marker getNearestMarker(LatLng center, Collection<Marker> markers) {
        double minDistance = Properties.getMaxBoundingBoxDiagonal();
        Marker answer = null;
        for (Marker marker : markers) {
            double distance = GeoMath.distance(center.lat, center.lng, marker.getLat(), marker.getLng());
            if (distance < minDistance) {
                minDistance = distance;
                answer = marker;
            }
        }
        return answer;
    }

    private Marker getNearMarker(LatLng center, Collection<Marker> markers) {
        double minDistance = Properties.getMaxBoundingBoxDiagonal();
        Marker answer = null;
        for (Marker marker : markers) {
            double distance = GeoMath.distance(center.lat, center.lng, marker.getLat(), marker.getLng());
            if (distance < Properties.getClusterMinRadius()) {
                return marker;
            }
            if (distance < minDistance) {
                minDistance = distance;
                answer = marker;
            }
        }
        return answer;
    }

    private double minClusterDistance(LatLng center, final Collection<Cluster> clusters) {
        double minDistance = Properties.getMaxBoundingBoxDiagonal();
        for (Cluster cluster : clusters) {
            double distance = GeoMath.distance(center.lat, center.lng, cluster.getLat(), cluster.getLng());
            if (distance < minDistance) {
                minDistance = distance;
            }
        }
        return minDistance;
    }

}
