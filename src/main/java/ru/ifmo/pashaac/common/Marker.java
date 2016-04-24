package ru.ifmo.pashaac.common;

import com.google.maps.model.LatLng;

/**
 * Need to parse data on client JSP side
 *
 * Created by Pavel Asadchiy
 * 19.04.16 22:36.
 */
public class Marker {

    private final double lat;
    private final double lng;
    private final double rad; // radius around marker

    public Marker(double lat, double lng, double rad) {
        this.lat = lat;
        this.lng = lng;
        this.rad = rad;
    }

    public Marker(LatLng latLng) {
        this(latLng.lat, latLng.lng, 0);
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public double getRad() {
        return rad;
    }

    @Override
    public String toString() {
        return "Marker{" +
                "lat=" + lat +
                ", lng=" + lng +
                ", rad=" + rad +
                '}';
    }
}
