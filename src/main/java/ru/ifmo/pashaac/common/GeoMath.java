package ru.ifmo.pashaac.common;

import com.google.maps.model.Bounds;
import com.google.maps.model.LatLng;
import com.grum.geocalc.DegreeCoordinate;
import com.grum.geocalc.EarthCalc;
import com.grum.geocalc.Point;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import ru.ifmo.pashaac.common.primitives.Cluster;

/**
 * Created by Pavel Asadchiy
 * 24.04.16 0:36.
 */
public class GeoMath implements DistanceMeasure {

    public static Point point(double lat, double lng) {
        return new Point(new DegreeCoordinate(lat), new DegreeCoordinate(lng));
    }

    public static double distance(double lat1, double lng1, double lat2, double lng2) {
        return EarthCalc.getVincentyDistance(point(lat1, lng1), point(lat2, lng2));
    }

    public static double distance(LatLng ll1, LatLng ll2) {
        return distance(ll1.lat, ll1.lng, ll2.lat, ll2.lng);
    }

    public static double distance(Cluster c1, Cluster c2) {
        return distance(c1.getLatLng(), c2.getLatLng());
    }

    public static double neighborDistance(Point start, Point finish) {
        double distanceLat = EarthCalc.getVincentyDistance(start, finish);
        int countLat = (int) Math.ceil(distanceLat / Properties.getNeighborSearchersDistance());
        return distanceLat / countLat;
    }

    public static double halfDiagonal(Bounds box) {
        LatLng center = boundsCenter(box);
        return distance(center.lat, center.lng, box.northeast.lat, box.northeast.lng);
    }

    public static LatLng boundsCenter(Bounds box) {
        return new LatLng((box.northeast.lat + box.southwest.lat) / 2, (box.northeast.lng + box.southwest.lng) / 2);
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

    public static boolean insideCircle(LatLng center, double rad, LatLng point) {
        return distance(center.lat, center.lng, point.lat, point.lng) < rad;
    }

    @Override
    public double compute(double[] a, double[] b) throws DimensionMismatchException {
        if (a.length != b.length || a.length != 2) {
            throw new IllegalStateException("Can't compute distance between different lengths arrays and not equal two");
        }
        return distance(a[0], a[1], b[0], b[1]);
    }
}
