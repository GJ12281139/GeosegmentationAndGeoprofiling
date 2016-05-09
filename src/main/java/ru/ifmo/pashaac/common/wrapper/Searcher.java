package ru.ifmo.pashaac.common.wrapper;

import com.google.maps.model.LatLng;

/**
 * Created by Pavel Asadchiy
 * 30.04.16 15:01.
 */
public class Searcher {

    private final double lat;
    private final double lng;
    private final double rad;
    private final String icon;

    public Searcher(double lat, double lng, double rad, String icon) {
        this.lat = lat;
        this.lng = lng;
        this.rad = rad;
        this.icon = icon;
    }

    public Searcher(LatLng latLng, double rad, String icon) {
        this(latLng.lat, latLng.lng, rad, icon);
    }

    public Searcher(double lat, double lng) {
        this(lat, lng, 0, null);
    }

    public Searcher() {
        this(0, 0);
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public LatLng getLatLng() {
        return new LatLng(lat, lng);
    }

    public double getRad() {
        return rad;
    }

    public String getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return "Searcher{" +
                "lat=" + lat +
                ", lng=" + lng +
                ", rad=" + rad +
                ", icon='" + icon + '\'' +
                '}';
    }
}
