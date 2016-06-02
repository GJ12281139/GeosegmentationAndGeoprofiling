package ru.ifmo.pashaac.common.primitives;

import com.google.maps.model.LatLng;
import ru.ifmo.pashaac.common.*;
import ru.ifmo.pashaac.data.source.Place;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Cluster object, need to know places inside.
 * On client JSP side send Marker class
 * <p>
 * Created by Pavel Asadchiy
 * on 19.05.16 20:48.
 */
public class Cluster extends Marker implements Comparable<Cluster> {

    private final List<Place> places;
    private String message;

    public Cluster(double lat, double lng, double rad, final String icon, List<Place> places) {
        super(lat, lng, rad, icon);
        this.places = places;
        this.message = "";
    }

    public List<Place> getPlaces() {
        return places;
    }

    public String getMessage() {
        return message;
    }

    public Cluster withMessage(String message) {
        this.message = message;
        return this;
    }

    public double getRating() {
        return places.stream()
                .mapToDouble(Place::getRating)
                .sum() / (Math.PI * getRad());
    }

    @Override
    public int compareTo(@Nonnull Cluster other) {
        return Double.compare(getRating(), other.getRating());
    }

    public static double getClusterRadius(LatLng center, Collection<Place> places) {
        OptionalDouble max = places.stream()
                .mapToDouble(place -> GeoMath.distance(center.lat, center.lng, place.getLat(), place.getLng()))
                .max();
        return max.isPresent() ? max.getAsDouble() : 0;
    }

    public static double getClusterRadius(Collection<Place> places) {
        LatLng center = getCenterOfMass(places);
        return getClusterRadius(center, places);
    }

    public static LatLng getCenterOfMass(Collection<Place> places) {
        double lat = 0;
        double lng = 0;
        for (Place place : places) {
            lat += place.getLat();
            lng += place.getLng();
        }
        return new LatLng(lat / places.size(), lng / places.size());
    }

    public static Cluster centerOfClusters(List<Cluster> clusters, int maxSegmentRad) {
        List<Place> places = clusters.stream()
                .map(Cluster::getPlaces)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        LatLng centerOfMass = Cluster.getCenterOfMass(places);
        List<Place> placesToCluster = new ArrayList<>();
        Collections.sort(places, (p1, p2) -> Double.compare(GeoMath.distance(centerOfMass, p1.getLatLng()), GeoMath.distance(centerOfMass, p2.getLatLng())));
        for (Place place : places) {
            if (GeoMath.distance(centerOfMass, place.getLatLng()) > maxSegmentRad) {
                break;
            }
            placesToCluster.add(place);
        }
        double distance = GeoMath.distance(centerOfMass, placesToCluster.get(placesToCluster.size() - 1).getLatLng());
        return new Cluster(centerOfMass.lat, centerOfMass.lng, distance, ru.ifmo.pashaac.common.Properties.getIconSearch(), placesToCluster);
    }
}
