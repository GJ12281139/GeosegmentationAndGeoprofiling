package ru.ifmo.pashaac.common;

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
}
