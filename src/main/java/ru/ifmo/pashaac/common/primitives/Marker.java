package ru.ifmo.pashaac.common.primitives;

import com.google.maps.model.LatLng;
import org.apache.commons.math3.ml.clustering.Clusterable;

import javax.annotation.Nullable;

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
    @Nullable
    private final String icon;

    public Marker(double lat, double lng, double rad, @Nullable String icon) {
        this.lat = lat;
        this.lng = lng;
        this.rad = rad;
        this.icon = icon;
    }

    public Marker(LatLng latLng, double rad, String icon) {
        this(latLng.lat, latLng.lng, rad, icon);
    }

    public Marker() {
        this(0, 0, 0, null);
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

    @Nullable
    public String getIcon() {
        return icon;
    }

    @Override
    public double[] getPoint() {
        return new double[]{lat, lng};
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
}
