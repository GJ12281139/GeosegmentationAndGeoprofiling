package ru.ifmo.pashaac.common.primitives;

import com.google.maps.model.LatLng;
import ru.ifmo.pashaac.common.GeoMath;

import java.util.Collection;
import java.util.List;

/**
 * Cluster object, need to know places inside.
 * On client JSP side send Marker class
 * <p>
 * Created by Pavel Asadchiy
 * on 19.05.16 20:48.
 */
public class Cluster extends Marker implements Comparable<Cluster> {

    private final List<Marker> markers;

    public Cluster(double lat, double lng, double rad, final String icon, final List<Marker> markers) {
        super(lat, lng, rad, icon);
        this.markers = markers;
    }

    public List<Marker> getMarkers() {
        return markers;
    }

    @Override
    public String toString() {
        return "Cluster{" +
                "places=" + markers +
                '}';
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(Cluster other) {
        double density = markers.size() / (Math.PI * getRad() * getRad());
        double densityOther = other.getMarkers().size() / (Math.PI * other.getRad() * other.getRad());
        return Double.compare(density, densityOther);
    }

    public static double getClusterRadius(LatLng center, final Collection<Marker> places) {
        double maxRad = 0;
        for (Marker place : places) {
            maxRad = Math.max(maxRad, GeoMath.distance(center.lat, center.lng, place.getLat(), place.getLng()));
        }
        return maxRad;
    }

    public static double getClusterRadius(final Collection<Marker> places) {
        final LatLng center = getCenterOfMass(places);
        double maxRad = 0;
        for (Marker place : places) {
            maxRad = Math.max(maxRad, GeoMath.distance(center.lat, center.lng, place.getLat(), place.getLng()));
        }
        return maxRad;
    }

    public static LatLng getCenterOfMass(final Collection<Marker> markers) {
        double lat = 0;
        double lng = 0;
        for (Marker marker : markers) {
            lat += marker.getLat();
            lng += marker.getLng();
        }
        return new LatLng(lat / markers.size(), lng / markers.size());
    }
}
