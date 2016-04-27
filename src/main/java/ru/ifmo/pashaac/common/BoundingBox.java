package ru.ifmo.pashaac.common;

import com.google.maps.model.Bounds;
import com.google.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * BoundingBox with places inside it
 * Need to parse data on client JSP side
 *
 * Created by Pavel Asadchiy
 * 19.04.16 22:38.
 */
public class BoundingBox {

    private final Place southwest;
    private final Place northeast;
    private final List<Place> places;

    public BoundingBox(Place southwest, Place northeast, List<Place> places) {
        this.southwest = southwest;
        this.northeast = northeast;
        this.places = places;
    }

    public BoundingBox(LatLng southwest, LatLng northeast) {
        this(new Place.Builder().setLat(southwest.lat).setLng(southwest.lng).build(),
                new Place.Builder().setLat(northeast.lat).setLng(northeast.lng).build(), new ArrayList<>());
    }

    public BoundingBox(LatLng southwest, LatLng northeast, List<Place> places) {
        this(new Place.Builder().setLat(southwest.lat).setLng(southwest.lng).build(),
                new Place.Builder().setLat(northeast.lat).setLng(northeast.lng).build(), places);
    }

    public BoundingBox(Bounds box) {
        this(box.southwest, box.northeast);
    }

    public Place getSouthwest() {
        return southwest;
    }

    public Place getNortheast() {
        return northeast;
    }

    public List<Place> getPlaces() {
        return places;
    }

    @Override
    public String toString() {
        return "BoundingBox{" +
                "southwest=" + southwest +
                ", northeast=" + northeast +
                ", places=" + places +
                '}';
    }
}
