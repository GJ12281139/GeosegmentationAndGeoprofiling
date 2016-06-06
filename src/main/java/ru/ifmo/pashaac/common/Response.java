package ru.ifmo.pashaac.common;

import ru.ifmo.pashaac.common.primitives.BoundingBox;
import ru.ifmo.pashaac.common.primitives.Cluster;
import ru.ifmo.pashaac.data.source.Place;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by Pavel Asadchiy
 * on 02.06.16 20:58.
 */
public class Response {

    private final Collection<Cluster> clusters;
    private final Collection<BoundingBox> boundingBoxes;
    private Collection<Place> places;

    public Response(Collection<Cluster> clusters, Collection<BoundingBox> boundingBoxes) {
        this.clusters = clusters;
        this.boundingBoxes = boundingBoxes;
    }

    public Collection<Cluster> getClusters() {
        return clusters;
    }

    public Collection<BoundingBox> getBoundingBoxes() {
        return boundingBoxes;
    }

    public Collection<Place> getPlaces() {
        return places;
    }

    public Response withTopPlaces(Collection<Place> allPlaces) {
        places = allPlaces.parallelStream()
                .filter(p -> clusters.parallelStream()
                        .allMatch(c -> GeoMath.distance(c.getLat(), c.getLng(), p.getLat(), p.getLng()) > c.getRad()))
                .sorted((p1, p2) -> Double.compare(p2.getRating(), p1.getRating()))
                .collect(Collectors.toList());
        places = places.stream()
                .limit((long) Math.ceil(places.size() * 0.4))
                .collect(Collectors.toList());
        return this;
    }
}
