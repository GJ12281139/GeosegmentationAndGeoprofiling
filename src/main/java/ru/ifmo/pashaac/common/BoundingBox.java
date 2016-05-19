package ru.ifmo.pashaac.common;

import com.google.maps.model.Bounds;
import com.google.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * BoundingBox around city information container
 * Need to parse data on client JSP side
 * <p>
 * Created by Pavel Asadchiy
 * 19.04.16 22:38.
 */
public class BoundingBox {

    private final Searcher southwest;
    private final Searcher northeast;
    private final String city;
    private final String country;

    public BoundingBox(LatLng southwest, LatLng northeast, String city, String country) {
        this.southwest = new Searcher(southwest.lat, southwest.lng);
        this.northeast = new Searcher(northeast.lat, northeast.lng);
        this.city = city;
        this.country = country;
    }

    public BoundingBox(Bounds box, String city, String country) {
        this(box.southwest, box.northeast, city, country);
    }

    public BoundingBox() {
        this.southwest = null;
        this.northeast = null;
        this.city = null;
        this.country = null;
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

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public static Collection<BoundingBox> getQuarters(BoundingBox bBox) {
        LatLng boxCenter = GeoMath.boundsCenter(bBox.getBounds());

        Bounds leftDownBounds = GeoMath.leftDownBoundingBox(boxCenter, bBox.getBounds());
        Bounds leftUpBounds = GeoMath.leftUpBoundingBox(boxCenter, bBox.getBounds());
        Bounds rightDownBounds = GeoMath.rightDownBoundingBox(boxCenter, bBox.getBounds());
        Bounds rightUpBounds = GeoMath.rightUpBoundingBox(boxCenter, bBox.getBounds());

        List<BoundingBox> quarters = new ArrayList<>();
        quarters.add(new BoundingBox(leftDownBounds, bBox.getCity(), bBox.getCountry()));
        quarters.add(new BoundingBox(leftUpBounds, bBox.getCity(), bBox.getCountry()));
        quarters.add(new BoundingBox(rightDownBounds, bBox.getCity(), bBox.getCountry()));
        quarters.add(new BoundingBox(rightUpBounds, bBox.getCity(), bBox.getCountry()));
        return quarters;
    }

    @Override
    public String toString() {
        return "BoundingBox{" +
                "southwest=" + southwest +
                ", northeast=" + northeast +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
