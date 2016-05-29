package ru.ifmo.pashaac.common.primitives;

import com.google.maps.model.LatLng;
import org.apache.commons.math3.ml.clustering.Clusterable;

/**
 * Point on map, if contains radius > 0 then it's searcher marker.
 * Otherwise it's place
 * <p>
 * Created by Pavel Asadchiy
 * 30.04.16 15:01.
 */
public class Marker implements Clusterable {

    private final double lat;
    private final double lng;
    private final double rad;
    private final double rating;
    private final String icon;

    public Marker(double lat, double lng, double rad, double rating, String icon) {
        this.lat = lat;
        this.lng = lng;
        this.rad = rad;
        this.rating = rating;
        this.icon = icon;
    }

    public Marker(LatLng latLng, double rad, String icon) {
        this(latLng.lat, latLng.lng, rad, 0, icon);
    }

    public Marker(double lat, double lng) {
        this(lat, lng, 0, 0, null);
    }

    public Marker() {
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

    public double getGoogleRating() {
        return rating;
    }

    @Override
    public String toString() {
        return "Marker{" +
                "lat=" + lat +
                ", lng=" + lng +
                ", rad=" + rad +
                ", icon='" + icon + '\'' +
                '}';
    }

    @Override
    public double[] getPoint() {
        return new double[]{lat, lng};
    }
}
