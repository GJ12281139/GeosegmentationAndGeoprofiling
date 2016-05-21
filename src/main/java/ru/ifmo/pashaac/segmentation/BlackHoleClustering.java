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
            List<Marker> cluster = new ArrayList<>(Collections.singletonList(marker));
            while (!tmp.isEmpty() && Cluster.getClusterRadius(cluster) < Properties.getClusterMaxRadius()) {
                Marker nearestMarker = getNearestMarker(Cluster.getCenterOfMass(cluster), tmp);
                cluster.add(nearestMarker);
                tmp.remove(nearestMarker);
            }
            if (cluster.size() > Properties.getClusterMinPlaces() && Cluster.getClusterRadius(cluster) > Properties.getClusterMinRadius()) {
                LatLng center = Cluster.getCenterOfMass(cluster);
                double radius = Cluster.getClusterRadius(cluster);
                answer.add(new Cluster(center.lat, center.lng, radius, Properties.getIconKernel(), cluster));
            }
            if (answer.size() > Properties.getClusterMaxInCity()) {
                break;
            }
        }
        return answer;
    }

    private Marker getLongDistanceFromClustersMarker(final List<Marker> tmp, final List<Cluster> clusters) {
        for (Marker marker : tmp) {
            if (minClusterDistance(marker.getLatLng(), clusters) > Properties.getClusterMaxRadius() * 1.6) {
                return marker;
            }
        }
        return null;
    }

    private Marker getNearestMarker(LatLng center, final Collection<Marker> markers) {
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

//    public List<Cluster> getDarkHoleClusterCityWeb() {
//
//        List<Marker> placesCopy = new ArrayList<>(places);
//        List<Marker> kernels = new ArrayList<>();
//        Collections.shuffle(placesCopy);
//
//        while (!placesCopy.isEmpty()) {
//            Marker place = placesCopy.remove(0);
//            Marker kernel = new Marker(place.getLat(), place.getLng(), 0, Properties.getIconKernel());
//            List<Marker> tmp = new ArrayList<>(placesCopy);
//            for (int i = 0; i < tmp.size(); i++) {
//                if (GeoMath.distance(kernel.getLat(), kernel.getLng(), tmp.get(i).getLat(), tmp.get(i).getLng()) < Properties.getClusterPlaceAvgDistance()) {
//
//                }
//            }
//
//            List<Marker> sortedList = new ArrayList<>(placesCopy);
//            Collections.sort(sortedList, (p1, p2) -> Double.compare(
//                    GeoMath.distance(place.getLat(), place.getLng(), p1.getLat(), p2.getLng()),
//                    GeoMath.distance(place.getLat(), place.getLng(), p2.getLat(), p2.getLng())));
//
//
//        }
//
//        return null;
//    }
//
//    private List<BoundingBox> getCityWeb() {
//
//    }
}
