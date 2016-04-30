package ru.ifmo.pashaac.common.wrapper;

import com.google.maps.model.Bounds;
import com.google.maps.model.LatLng;

/**
 * BoundingBox with places inside it
 * Need to parse data on client JSP side
 *
 * Created by Pavel Asadchiy
 * 19.04.16 22:38.
 */
public class BoundingBox {

    private final Searcher southwest;
    private final Searcher northeast;
    private final String region;
    private final String country;

    public BoundingBox(LatLng southwest, LatLng northeast, String region, String country) {
        this.southwest = new Searcher(southwest.lat, southwest.lng);
        this.northeast = new Searcher(northeast.lat, northeast.lng);
        this.region = region;
        this.country = country;
    }

    public BoundingBox(Bounds box, String region, String country) {
        this(box.southwest, box.northeast, region, country);
    }

    public Searcher getSouthwest() {
        return southwest;
    }

    public Searcher getNortheast() {
        return northeast;
    }

    public Bounds getBounds() {
        Bounds bounds = new Bounds();
        bounds.southwest = southwest.getLatLng();
        bounds.northeast = northeast.getLatLng();
        return bounds;
    }

    public String getRegion() {
        return region;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public String toString() {
        return "BoundingBox{" +
                "southwest=" + southwest +
                ", northeast=" + northeast +
                ", region='" + region + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
