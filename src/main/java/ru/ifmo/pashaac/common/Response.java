package ru.ifmo.pashaac.common;

import ru.ifmo.pashaac.common.primitives.BoundingBox;
import ru.ifmo.pashaac.common.primitives.Cluster;
import ru.ifmo.pashaac.data.source.Place;

import java.util.ArrayList;
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
        places = new ArrayList<>();
        for (Place place : allPlaces) {
            boolean inCluster = false;
            for (Cluster cluster : clusters) {
                if (GeoMath.distance(cluster.getLat(), cluster.getLng(), place.getLat(), place.getLng()) < cluster.getRad()) {
                    inCluster = true;
                    break;
                }
            }
            if (!inCluster) {
                places.add(place);
            }
        }
        places = places.stream()
                .sorted((p1, p2) -> Double.compare(p2.getRating(), p1.getRating()))
                .limit((long) Math.ceil(places.size() * 0.4))
                .collect(Collectors.toList());
        return this;
    }
}
