package ru.ifmo.pashaac.coverage;

import com.google.maps.model.Bounds;
import com.google.maps.model.LatLng;
import com.grum.geocalc.Coordinate;
import com.grum.geocalc.DegreeCoordinate;
import com.grum.geocalc.EarthCalc;
import com.grum.geocalc.Point;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.ifmo.pashaac.common.BoundingBox;
import ru.ifmo.pashaac.common.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pavel Asadchiy
 * 21.04.16 23:40.
 */
@Service
public class CoverageAlgorithms {

    private static final Logger LOG = Logger.getLogger(CoverageAlgorithms.class);

    @Value("${distance.between.markers}")
    private double markersDistance;

    @Value("${lat.default.step}")
    private double latStep;

    @Value("${lng.default.step}")
    private double lngStep;

//    @ReloadableProperty("radius.around.marker")
    private double radiusAroundMarker = 1000;


    /**
     * Линейное распределение с использованием обычного сложения/вычитания широт/долгот
     *
     * @param box - bounding box вокруг города
     * @return список маркеров на карте, согласно виду распределения
     */
    public List<Marker> getStaticSimpleMarkersDistribution(Bounds box) {
        List<Marker> markers = new ArrayList<>();
        for (double lat = box.southwest.lat; lat <= box.northeast.lat; lat += latStep) {
            for (double lng = box.southwest.lng; lng <= box.northeast.lng; lng += lngStep) {
                markers.add(new Marker(lat, lng, radiusAroundMarker));
            }
        }
        return markers;
    }

    /**
     * Линейное распределение с использованием геодезического пересчета расстояния между координатами и
     * нахождения точек в определенном направлении на заданном расстоянии.
     *
     * @param box - bounding box вокруг города
     * @return список маркеров на карте, согласно виду распределения
     */
    public List<Marker> getStaticUniformGeodesicMarkersDistribution(Bounds box) {
        LOG.info("Static uniform distribution with geodesic calculation");
        List<Marker> markers = new ArrayList<>();
        Coordinate southwestLat = new DegreeCoordinate(box.southwest.lat);
        Coordinate southwestLng = new DegreeCoordinate(box.southwest.lng);
        Coordinate northwestLat = new DegreeCoordinate(box.northeast.lat);
        Coordinate northeastLng = new DegreeCoordinate(box.northeast.lng);

        Point southwest = new Point(southwestLat, southwestLng);
        Point southeast = new Point(southwestLat, northeastLng);
        Point northwest = new Point(northwestLat, southwestLng);
        Point northeast = new Point(northwestLat, northeastLng);

        double distanceLat = EarthCalc.getVincentyDistance(new Point(southwestLat, southwestLng), new Point(northwestLat, southwestLng));
        int countLat = (int) Math.ceil(distanceLat / markersDistance);
        double distanceLatNeighbor = distanceLat / countLat;
        for (int i = 0; i < countLat + 1; i++) {
            Point startLat = EarthCalc.pointRadialDistance(new Point(southwestLat, southwestLng), 0, distanceLatNeighbor * i);
            if (startLat.getLatitude() > northwestLat.getValue()) {
                startLat = new Point(northwestLat, southwestLng);
            }
            Point finishLng = new Point(new DegreeCoordinate(startLat.getLatitude()), northeastLng);
            double distanceLng = EarthCalc.getVincentyDistance(startLat, finishLng);
            int countLng = (int) Math.ceil(distanceLng / markersDistance);
            double distanceLngNeighbor = distanceLng / countLng;
            for (int j = 0; j < countLng + 1; j++) {
                Point tmp = EarthCalc.pointRadialDistance(startLat, 90, distanceLngNeighbor * j);
                if (tmp.getLongitude() > finishLng.getLongitude()) {
                    tmp = finishLng;
                }
                markers.add(new Marker(tmp.getLatitude(), tmp.getLongitude(), radiusAroundMarker));
            }
        }
        return markers;
    }

    public List<LatLng> getDynamicalDistribution() {
        return null;
    }

    private List<BoundingBox> getCoverageBoundingBoxTree(BoundingBox boundingBox) {
        return null;
    }
}
