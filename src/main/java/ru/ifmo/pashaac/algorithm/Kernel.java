package ru.ifmo.pashaac.algorithm;

import com.google.maps.model.Bounds;
import ru.ifmo.pashaac.common.Properties;
import ru.ifmo.pashaac.common.BoundingBox;
import ru.ifmo.pashaac.common.Searcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Pavel Asadchiy
 * 09.05.16 14:20.
 */
public class Kernel {

    public static List<Searcher> randomCentroids(BoundingBox box, int n) {
        Random latRandom = new Random();
        Random lngRandom = new Random();
        Bounds bounds = box.getBounds();
        List<Searcher> kernels = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            double lat = latRandom.nextDouble() * (bounds.northeast.lat - bounds.southwest.lat) + bounds.southwest.lat;
            double lng = lngRandom.nextDouble() * (bounds.northeast.lng - bounds.southwest.lng) + bounds.southwest.lng;
            kernels.add(new Searcher(lat, lng, 0, Properties.getIconKernel()));
        }
        return kernels;
    }
}
