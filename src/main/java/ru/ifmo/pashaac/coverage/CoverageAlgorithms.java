package ru.ifmo.pashaac.coverage;

import com.google.maps.model.Bounds;
import com.grum.geocalc.EarthCalc;
import com.grum.geocalc.Point;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import ru.ifmo.pashaac.common.GeoMath;
import ru.ifmo.pashaac.common.Marker;
import ru.ifmo.pashaac.common.Properties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pavel Asadchiy
 * 21.04.16 23:40.
 */
@Service
public class CoverageAlgorithms {

    private static final Logger LOG = Logger.getLogger(CoverageAlgorithms.class);

    /**
     * Линейное распределение с использованием обычного сложения/вычитания широт/долгот
     *
     * @param box - bounding box вокруг города
     * @return список маркеров на карте, согласно виду распределения
     */
    @SuppressWarnings("unused")
    public List<Marker> getStaticSimpleMarkersDistribution(Bounds box) {
        LOG.info("Static uniform distribution with simple (without geodesic) calculation");

        List<Marker> markers = new ArrayList<>();
        for (double lat = box.southwest.lat; lat <= box.northeast.lat; lat += Properties.getLatStep()) {
            for (double lng = box.southwest.lng; lng <= box.northeast.lng; lng += Properties.getLngStep()) {
                markers.add(new Marker(lat, lng, Properties.getMarkerRadius()));
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
        Point southwestPoint = GeoMath.getPoint(box.southwest.lat, box.southwest.lng);
        Point northwestPoint = GeoMath.getPoint(box.northeast.lat, box.southwest.lng);

        double distanceLat = EarthCalc.getVincentyDistance(southwestPoint, northwestPoint);
        int countLat = (int) Math.ceil(distanceLat / Properties.getMarkerStep());
        double distanceLatNeighbor = distanceLat / countLat;
        for (int i = 0; i < countLat + 1; i++) {
            Point startLat = EarthCalc.pointRadialDistance(southwestPoint, 0, distanceLatNeighbor * i);
            if (startLat.getLatitude() > box.northeast.lat) {
                startLat = northwestPoint;
            }
            Point finishLng = GeoMath.getPoint(startLat, box.northeast.lng);
            double distanceLng = EarthCalc.getVincentyDistance(startLat, finishLng);
            int countLng = (int) Math.ceil(distanceLng / Properties.getMarkerStep());
            double distanceLngNeighbor = distanceLng / countLng;
            for (int j = 0; j < countLng + 1; j++) {
                Point tmp = EarthCalc.pointRadialDistance(startLat, 90, distanceLngNeighbor * j);
                if (tmp.getLongitude() > finishLng.getLongitude()) {
                    tmp = finishLng;
                }
                markers.add(new Marker(tmp.getLatitude(), tmp.getLongitude(), Properties.getMarkerRadius()));
            }
        }
        return markers;
    }

}
