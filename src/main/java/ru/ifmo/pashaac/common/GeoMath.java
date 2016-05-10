package ru.ifmo.pashaac.common;

import com.google.maps.model.Bounds;
import com.google.maps.model.LatLng;
import com.grum.geocalc.Coordinate;
import com.grum.geocalc.DegreeCoordinate;
import com.grum.geocalc.EarthCalc;
import com.grum.geocalc.Point;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

/**
 * Created by Pavel Asadchiy
 * 24.04.16 0:36.
 */
public class GeoMath implements DistanceMeasure {

    public static double distance(double lat1, double lng1, double lat2, double lng2) {
        Coordinate p1Lat = new DegreeCoordinate(lat1);
        Coordinate p1Lng = new DegreeCoordinate(lng1);
        Coordinate p2Lat = new DegreeCoordinate(lat2);
        Coordinate p2Lng = new DegreeCoordinate(lng2);
        return EarthCalc.getVincentyDistance(new Point(p1Lat, p1Lng), new Point(p2Lat, p2Lng));
    }

    public static double neighborDistance(Point start, Point finish) {
        double distanceLat = EarthCalc.getVincentyDistance(start, finish);
        int countLat = (int) Math.ceil(distanceLat / Properties.getNeighborSearchersDistance());
        return distanceLat / countLat;
    }

    public static Point point(double lat, double lng) {
        return new Point(new DegreeCoordinate(lat), new DegreeCoordinate(lng));
    }

    public static Point point(Point lat, double lng) {
        return new Point(new DegreeCoordinate(lat.getLatitude()), new DegreeCoordinate(lng));
    }

    public static double halfDiagonal(Bounds box) {
        LatLng center = boundsCenter(box);
        return distance(center.lat, center.lng, box.northeast.lat, box.northeast.lng);
    }

    public static LatLng boundsCenter(Bounds box) {
        double latCenter = (box.northeast.lat + box.southwest.lat) / 2;
        double lngCenter = (box.northeast.lng + box.southwest.lng) / 2;
        return new LatLng(latCenter, lngCenter);
    }

    public static Bounds leftUpBoundingBox(LatLng center, Bounds box) {
        return bounds(center.lat, box.southwest.lng, box.northeast.lat, center.lng);
    }

    public static Bounds leftDownBoundingBox(LatLng center, Bounds box) {
        return bounds(box.southwest.lat, box.southwest.lng, center.lat, center.lng);
    }

    public static Bounds rightUpBoundingBox(LatLng center, Bounds box) {
        return bounds(center.lat, center.lng, box.northeast.lat, box.northeast.lng);
    }

    public static Bounds rightDownBoundingBox(LatLng center, Bounds box) {
        return bounds(box.southwest.lat, center.lng, center.lat, box.northeast.lng);
    }

    private static Bounds bounds(double swLat, double swLng, double neLat, double neLng) {
        Bounds bounds = new Bounds();
        bounds.southwest = new LatLng(swLat, swLng);
        bounds.northeast = new LatLng(neLat, neLng);
        return bounds;
    }

    public static double getSquareInMeters(Bounds box) {
        return distance(box.southwest.lat, box.southwest.lng, box.northeast.lat, box.southwest.lng) *
                distance(box.southwest.lat, box.southwest.lng, box.southwest.lat, box.northeast.lng);
    }

    public static double getSquareInKilometers(Bounds box) {
        return getSquareInMeters(box) / 1_000_000;
    }

    @Override
    public double compute(double[] a, double[] b) throws DimensionMismatchException {
        return a.length == b.length && a.length == 2
                ? distance(a[0], a[1], b[0], b[1])
                : Properties.getMaxBoundingBoxDiagonal();
    }
}
