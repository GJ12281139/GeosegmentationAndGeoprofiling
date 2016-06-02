package ru.ifmo.pashaac.segmentation;

import com.google.maps.model.LatLng;
import javafx.util.Pair;
import ru.ifmo.pashaac.common.GeoMath;
import ru.ifmo.pashaac.common.Properties;
import ru.ifmo.pashaac.common.primitives.Cluster;
import ru.ifmo.pashaac.data.source.Place;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Pavel Asadchiy
 * on 19.05.16 9:07.
 */
public class BlackHoleClustering {

    private static final int algorithmStartCount = 26;

    private final List<Place> places;
    private final int minSegmentRad;
    private final int maxSegmentRad;
    private final int segmentsCountPercent;

    public BlackHoleClustering(Collection<Place> places, int minSegmentRad, int maxSegmentRad, int segmentsCountPercent) {
        this.places = new ArrayList<>(places);
        this.minSegmentRad = minSegmentRad;
        this.maxSegmentRad = maxSegmentRad;
        this.segmentsCountPercent = segmentsCountPercent;
    }

    public List<Cluster> getDarkHoleRandom() {
        List<Cluster> clusters = new ArrayList<>();
        for (int i = 0; i < algorithmStartCount; i++) {
            clusters.addAll(getDarkHoleRandomAlgorithm());
        }

//        return clusters;
        List<Cluster> result = filterClusters(clusters);
        return result.stream()
                .sorted((c1, c2) -> Double.compare(c2.getRating(), c1.getRating())) // descend
                .limit((long) Math.ceil(segmentsCountPercent * result.size() * 1.0 / 100))
                .collect(Collectors.toList());
    }

    private List<Cluster> filterClusters(List<Cluster> clusters) {
        List<Pair<Cluster, Integer>> clustersCross = new ArrayList<>();
        for (int i = 0; i < clusters.size(); i++) {
            int intersection = 0;
            for (Cluster cluster : clusters) {
                if (GeoMath.distance(clusters.get(i), cluster) < minSegmentRad) {
                    ++intersection;
                }
            }
            clustersCross.add(new Pair<>(clusters.get(i), intersection));
        }

        List<Cluster> sortedClusters = clustersCross.stream()
                .filter(p -> p.getValue() > (int) Math.floor(algorithmStartCount * 0.4))
                .sorted((p1, p2) -> Objects.equals(p2.getValue(), p1.getValue())
                        ? Double.compare(p2.getKey().getRating(), p1.getKey().getRating())
                        : Integer.compare(p2.getValue(), p1.getValue()))
                .map(Pair::getKey)
                .collect(Collectors.toList());

        List<Cluster> result = new ArrayList<>();
        while (!sortedClusters.isEmpty()) {
            Iterator<Cluster> iterator = sortedClusters.iterator();
            Cluster curr = iterator.next();
            iterator.remove();
            while (iterator.hasNext()) {
                Cluster next = iterator.next();
                if (GeoMath.distance(curr, next) < curr.getRad() + next.getRad() - 0.3 * Math.max(curr.getRad(), next.getRad())) {
                    iterator.remove();
                }
            }
            result.add(curr);
        }

        return result;
    }

    private List<Cluster> getDarkHoleRandomAlgorithm() {
        List<Cluster> clusters = new ArrayList<>();
        List<Place> tmp = new ArrayList<>(places);
        int wasteCycles = 0;
        while (!tmp.isEmpty() && wasteCycles < 15) {
            ++wasteCycles;
            Place place = getRemoteFromClustersPlace(clusters, tmp);
            if (place == null) {
                break; // no remote markers
            }
            tmp.remove(place);
            List<Place> cluster = new ArrayList<>();
            cluster.add(place);
            Place near = null;
            while (!tmp.isEmpty() && Cluster.getClusterRadius(cluster) < maxSegmentRad) {
//                near = getNearBestPlace(Cluster.getCenterOfMass(cluster), tmp);
                near = getNearPlace(Cluster.getCenterOfMass(cluster), tmp);
                if (near == null) {
                    break;
                }
                cluster.add(near);
                tmp.remove(near);
            }
            if (near != null) {
                cluster.remove(near);
                tmp.add(near);
            }
            double clusterRadius = Cluster.getClusterRadius(cluster);
            if (clusterRadius > minSegmentRad && cluster.size() > Properties.getClusterMinPlaces()) {
                wasteCycles = 0;
                LatLng center = Cluster.getCenterOfMass(cluster);
                Cluster addCluster = new Cluster(center.lat, center.lng, clusterRadius, Properties.getIconSearch(), cluster);
                clusters.add(addCluster.withMessage("Cluster rating=" + addCluster.getRating() + ", radius=" + clusterRadius));
            } else {
                cluster.stream().forEach(tmp::add); // bad cluster, return points
                Collections.shuffle(tmp);           // shuffle, need that get other result
            }
        }
        return clusters;
    }


    private Place getRemoteFromClustersPlace(List<Cluster> clusters, Collection<Place> places) {
        return places.stream()
                .filter(place -> clusters.stream()
                        .mapToDouble(cluster -> GeoMath.distance(place.getLat(), place.getLng(), cluster.getLat(), cluster.getLng()))
                        .allMatch(distance -> distance > maxSegmentRad * 1.25))
                .findFirst()
                .orElse(null);
    }

    private Place getNearPlace(LatLng center, Collection<Place> places) {
        return places.stream()
                .filter(place -> GeoMath.distance(center.lat, center.lng, place.getLat(), place.getLng()) < maxSegmentRad)
                .findFirst()
                .orElse(null);
    }

    private Place getNearBestPlace(LatLng center, Collection<Place> places) {
        return places.stream()
                .filter(place -> GeoMath.distance(center.lat, center.lng, place.getLat(), place.getLng()) < maxSegmentRad)
                .max((p1, p2) -> Double.compare(p1.getRating(), p2.getRating()))
                .orElse(null);
    }

}
