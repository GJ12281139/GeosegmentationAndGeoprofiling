package ru.ifmo.pashaac.common;

import com.google.maps.model.Bounds;
import com.google.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Need to parse data on client JSP side
 *
 * Created by Pavel Asadchiy
 * 19.04.16 22:38.
 */
public class BoundingBox {

    private final Marker northeast;
    private final Marker southwest;
    private List<Marker> markers;

    public BoundingBox(Marker northeast, Marker southwest) {
        this.northeast = northeast;
        this.southwest = southwest;
        this.markers = new ArrayList<>();
    }

    public BoundingBox(Bounds bounds) {
        this(new Marker(bounds.northeast), new Marker(bounds.southwest));
    }

    public Marker getNortheast() {
        return northeast;
    }

    public Marker getSouthwest() {
        return southwest;
    }

    public List<Marker> getMarkers() {
        return markers;
    }

    public Bounds getBounds() {
        Bounds bounds = new Bounds();
        bounds.northeast = new LatLng(northeast.getLat(), northeast.getLng());
        bounds.southwest = new LatLng(southwest.getLat(), southwest.getLng());
        return bounds;
    }

    @Override
    public String toString() {
        return "BoundingBox{" +
                "northeast=" + northeast +
                ", southwest=" + southwest +
                ", markers=" + markers +
                '}';
    }

}
