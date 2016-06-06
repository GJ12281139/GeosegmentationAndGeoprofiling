package ru.ifmo.pashaac.segmentation;

import com.google.maps.model.LatLng;
import javafx.util.Pair;
import org.apache.log4j.Logger;
import ru.ifmo.pashaac.common.GeoMath;
import ru.ifmo.pashaac.common.Properties;
import ru.ifmo.pashaac.common.UserDAO;
import ru.ifmo.pashaac.common.primitives.Cluster;
import ru.ifmo.pashaac.data.source.Place;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Pavel Asadchiy
 * on 19.05.16 9:07.
 */
public class BlackHoleClustering {

    private static final Logger LOG = Logger.getLogger(BlackHoleClustering.class);

    private static final int algorithmStartCount = 16;

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

    public List<Cluster> getDarkHoleRandomAlgorithm() {
        long currentTimeMillis = System.currentTimeMillis();
        LOG.info("Start time: " + UserDAO.getTime());
        List<Cluster> clusters = new ArrayList<>();
        for (int i = 0; i < algorithmStartCount; i++) {
            Set<Place> places = new LinkedHashSet<>(this.places);
            List<Cluster> localClusters = new ArrayList<>();
            int wasteCycles = 0;
            while (!places.isEmpty() && wasteCycles < algorithmStartCount) {
                ++wasteCycles;
                Place place = getRemoteFromClustersPlace(localClusters, places);
                if (place == null) {
                    break; // no remote places
                }
                Cluster cluster = calculateCluster(place, places);
                if (cluster == null) {
                    ArrayList<Place> tmp = new ArrayList<>(places);
                    Collections.shuffle(tmp);
                    places = new LinkedHashSet<>(tmp);
                    continue;
                }
                wasteCycles = 0;
                localClusters.add(cluster);
            }
            clusters.addAll(localClusters);
        }
        LOG.info("End time: " + UserDAO.getTime() + ", operation time=" + (System.currentTimeMillis() - currentTimeMillis));
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
                .filter(p -> p.getValue() > (int) Math.floor(algorithmStartCount * 0.5))
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

    /**
     * Start dark hole top rating algorithm block
     */
    public List<Cluster> getDarkHoleTopRatingAlgorithm() {
        long currentTimeMillis = System.currentTimeMillis();
        LOG.info("Start time: " + UserDAO.getTime());
        List<Cluster> clusters = new ArrayList<>();
        Set<Place> places = new LinkedHashSet<>(this.places.stream()
                .sorted((p1, p2) -> Double.compare(p2.getRating(), p1.getRating()))
                .collect(Collectors.toList()));
        while (!places.isEmpty()) {
            Place place = getRemoteFromClustersPlace(clusters, places);
            if (place == null) {
                break; // no remote places
            }
            Cluster cluster = calculateCluster(place, places);
            if (cluster == null) {
                places.remove(place);
                continue;
            }
            clusters.add(cluster);
        }
        LOG.info("End time: " + UserDAO.getTime() + ", operation time=" + (System.currentTimeMillis() - currentTimeMillis));
        return topRatingAlgorithmFiltrationNormalization(clusters); // very fast
    }

    private List<Cluster> topRatingAlgorithmFiltrationNormalization(List<Cluster> clusters) {
        List<Cluster> result = new ArrayList<>();
        for (Cluster cluster : clusters) {
            if (cluster.getPlaces().size() < Properties.getClusterMinPlaces()) {
                continue;
            }
            if (cluster.getRad() < minSegmentRad) {
                result.add(new Cluster(cluster.getLat(), cluster.getLng(), minSegmentRad, cluster.getIcon(),
                        cluster.getPlaces()).withMessage(cluster.getMessage()));
                continue;
            }
            result.add(cluster);
        }
        return result.stream()
                .sorted((c1, c2) -> Double.compare(c2.getRating(), c1.getRating())) // descend
                .limit((long) Math.ceil(segmentsCountPercent * result.size() * 1.0 / 100))
                .collect(Collectors.toList());
    }
    /** End dark hole algorithm top rating block */

    /**
     * Common block
     */
    private Cluster calculateCluster(Place place, Set<Place> places) {
        List<Place> cluster = new ArrayList<>();
        places.remove(place);
        cluster.add(place);

        while (!places.isEmpty()) {
            Place near = getNearestPlace(Cluster.getCenterOfMass(cluster), places); // exist 3 methods to get
            if (near == null) {
                break;
            }
            cluster.add(near);
            places.remove(near);
            if (Cluster.getClusterRadius(cluster) >= maxSegmentRad) {
                places.add(near);
                cluster.remove(near);
                break;
            }
        }

        if (Cluster.getClusterRadius(cluster) < minSegmentRad || cluster.size() < Properties.getClusterMinPlaces()) {
            cluster.stream().forEach(places::add);  // bad cluster, return points
            return null;
        }

        return clusterNormalization(cluster);
    }

    private Cluster clusterNormalization(List<Place> cluster) {
        List<Place> tmp = new ArrayList<>();
        for (int i = 0; i < Properties.getClusterMinPlaces(); i++) {
            tmp.add(cluster.get(i));
        }
        double topRating = 0;
        int topIndex = 0;
        for (int i = Properties.getClusterMinPlaces(); i < cluster.size(); i++) {
            tmp.add(cluster.get(i));
            double rating = Cluster.getRating(tmp, Cluster.getClusterRadius(tmp), i + 1);
            if (rating > topRating) {
                topRating = rating;
                topIndex = i;
            }
        }
        for (int i = topIndex + 1; i < cluster.size(); i++) {
            places.add(cluster.get(i)); // return useless places
        }
        cluster = cluster.subList(0, topIndex + 1);
        LatLng center = Cluster.getCenterOfMass(cluster);
        Cluster clusterToAdd =
                new Cluster(center.lat, center.lng, Cluster.getClusterRadius(cluster), Properties.getIconSearch(), cluster);
        return clusterToAdd.withMessage("Cluster rating=" + clusterToAdd.getRating() + ", radius=" + clusterToAdd.getRad());
    }

    /**
     * Remote point from all clusters method
     */

    private Place getRemoteFromClustersPlace(List<Cluster> clusters, Collection<Place> places) {
        return places.stream()
                .filter(place -> clusters.stream()
                        .mapToDouble(cluster -> GeoMath.distance(place.getLat(), place.getLng(), cluster.getLat(), cluster.getLng()))
                        .allMatch(distance -> distance > maxSegmentRad * 1.25))
                .findFirst()
                .orElse(null);
    }

    /**
     * Getters next point for cluster different methods
     */

    private Place getNearPlace(LatLng center, Collection<Place> places) {
        return places.stream()
                .filter(place -> GeoMath.distance(center.lat, center.lng, place.getLat(), place.getLng()) < maxSegmentRad)
                .findFirst()
                .orElse(null);
    }

    private Place getNearestPlace(LatLng center, Collection<Place> places) {
        return places.parallelStream()
                .filter(place -> GeoMath.distance(center.lat, center.lng, place.getLat(), place.getLng()) < maxSegmentRad)
                .reduce(null, (p1, p2) -> {
                    if (p1 == null)
                        return p2;
                    if (p2 == null)
                        return p1;
                    return GeoMath.distance(center, p1.getLatLng()) < GeoMath.distance(center, p2.getLatLng()) ? p1 : p2;
                });
    }

    private Place getNearBestPlace(LatLng center, Collection<Place> places) {
        return places.parallelStream()
                .filter(place -> GeoMath.distance(center.lat, center.lng, place.getLat(), place.getLng()) < maxSegmentRad)
                .max((p1, p2) -> Double.compare(p1.getRating(), p2.getRating()))
                .orElse(null);
    }

}
