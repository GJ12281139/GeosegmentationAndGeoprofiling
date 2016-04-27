package ru.ifmo.pashaac.common;

import com.google.maps.model.Bounds;
import com.google.maps.model.LatLng;
import com.grum.geocalc.Coordinate;
import com.grum.geocalc.DegreeCoordinate;
import com.grum.geocalc.EarthCalc;
import com.grum.geocalc.Point;

/**
 * Created by Pavel Asadchiy
 * 24.04.16 0:36.
 */
public class GeoMath {

    public static double distance(double lat1, double lng1, double lat2, double lng2) {
        Coordinate p1Lat = new DegreeCoordinate(lat1);
        Coordinate p1Lng = new DegreeCoordinate(lng1);
        Coordinate p2Lat = new DegreeCoordinate(lat2);
        Coordinate p2Lng = new DegreeCoordinate(lng2);
        return EarthCalc.getVincentyDistance(new Point(p1Lat, p1Lng), new Point(p2Lat, p2Lng));
    }

    public static Point getPoint(double lat, double lng) {
        return new Point(new DegreeCoordinate(lat), new DegreeCoordinate(lng));
    }

    public static Point getPoint(Point lat, double lng) {
        return new Point(new DegreeCoordinate(lat.getLatitude()), new DegreeCoordinate(lng));
    }

    public static double getHalfDiagonal(Bounds box) {
        LatLng center = getBoundCenter(box);
        return distance(center.lat, center.lng, box.northeast.lat, box.northeast.lng);
    }

    public static LatLng getBoundCenter(Bounds box) {
        return new LatLng((box.northeast.lat + box.southwest.lat) / 2, (box.northeast.lng + box.southwest.lng) / 2);
    }

    public static Bounds getLeftUpBoundingBox(LatLng center, Bounds box) {
        Bounds bounds = new Bounds();
        bounds.southwest = new LatLng(center.lat, bounds.southwest.lng);
        bounds.northeast = new LatLng(box.northeast.lat, center.lng);
        return bounds;
    }

    public static Bounds getLeftDownBoundingBox(LatLng center, Bounds box) {
        Bounds bounds = new Bounds();
        bounds.southwest = new LatLng(box.southwest.lat, box.southwest.lng);
        bounds.northeast = new LatLng(center.lat, center.lng);
        return bounds;
    }

    public static Bounds getRightUpBoundingBox(LatLng center, Bounds box) {
        Bounds bounds = new Bounds();
        bounds.southwest = new LatLng(center.lat, center.lng);
        bounds.northeast = new LatLng(box.northeast.lat, box.northeast.lng);
        return bounds;
    }

    public static Bounds getRightDownBoundingBox(LatLng center, Bounds box) {
        Bounds bounds = new Bounds();
        bounds.southwest = new LatLng(box.southwest.lat, center.lng);
        bounds.northeast = new LatLng(center.lat, box.northeast.lng);
        return bounds;
    }

}
