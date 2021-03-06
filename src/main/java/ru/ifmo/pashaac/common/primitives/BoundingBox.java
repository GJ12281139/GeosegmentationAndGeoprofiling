package ru.ifmo.pashaac.common.primitives;

import com.google.maps.model.Bounds;
import com.google.maps.model.LatLng;
import ru.ifmo.pashaac.common.GeoMath;

import java.util.ArrayList;
import java.util.List;

/**
 * BoundingBox around city information container
 * Need to parse data on client JSP side
 * <p>
 * Created by Pavel Asadchiy
 * 19.04.16 22:38.
 */
public class BoundingBox {

    private final Marker southwest;
    private final Marker northeast;
    private final String city;
    private final String country;

    public BoundingBox(LatLng southwest, LatLng northeast, String city, String country) {
        this.southwest = new Marker(southwest.lat, southwest.lng, 0, null);
        this.northeast = new Marker(northeast.lat, northeast.lng, 0, null);
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

    public Marker getSouthwest() {
        return southwest;
    }

    public Marker getNortheast() {
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

    public static List<BoundingBox> getQuarters(BoundingBox bBox) {
        LatLng boxCenter = GeoMath.boundsCenter(bBox.getBounds());

        Bounds leftDownBounds = GeoMath.leftDownBoundingBox(boxCenter, bBox.getBounds());
        Bounds leftUpBounds = GeoMath.leftUpBoundingBox(boxCenter, bBox.getBounds());
        Bounds rightDownBounds = GeoMath.rightDownBoundingBox(boxCenter, bBox.getBounds());
        Bounds rightUpBounds = GeoMath.rightUpBoundingBox(boxCenter, bBox.getBounds());

        List<BoundingBox> quarters = new ArrayList<>(4);
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
