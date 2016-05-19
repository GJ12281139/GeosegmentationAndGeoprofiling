package ru.ifmo.pashaac.segmentation;

import ru.ifmo.pashaac.common.GeoMath;
import ru.ifmo.pashaac.common.Properties;
import ru.ifmo.pashaac.common.Searcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Pavel Asadchiy
 * on 19.05.16 9:07.
 */
public class DarkHoleClustering {

    private final Collection<Searcher> places;

    public DarkHoleClustering(final Collection<Searcher> places) {
        this.places = places;
    }

    public List<Searcher> getKernelsDarkHole() {
        List<Searcher> placesCopy = new ArrayList<>(places);
        List<Searcher> kernels = new ArrayList<>();
        Collections.shuffle(placesCopy);

        while (!placesCopy.isEmpty()) {
            Searcher place = placesCopy.remove(0);
            Searcher kernel = new Searcher(place.getLat(), place.getLng(), 0, Properties.getIconKernel());
            List<Searcher> tmp = new ArrayList<>(placesCopy);
            for (int i = 0; i < tmp.size(); i++) {
                if (GeoMath.distance(kernel.getLat(), kernel.getLng(), tmp.get(i).getLat(), tmp.get(i).getLng()) < Properties.getClusterPlaceAvgDistance()) {

                }
            }

            List<Searcher> sortedList = new ArrayList<>(placesCopy);
            Collections.sort(sortedList, (p1, p2) -> Double.compare(
                    GeoMath.distance(place.getLat(), place.getLng(), p1.getLat(), p2.getLng()),
                    GeoMath.distance(place.getLat(), place.getLng(), p2.getLat(), p2.getLng())));


        }

        return null;
    }
}
