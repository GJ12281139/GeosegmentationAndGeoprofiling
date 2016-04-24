package ru.ifmo.pashaac.coverage;

import com.google.maps.model.Bounds;
import com.google.maps.model.LatLng;
import ru.ifmo.pashaac.common.BoundingBox;
import ru.ifmo.pashaac.common.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pavel Asadchiy
 * 19.04.16 16:16.
 */
public class CoverageModel {

    private final Marker userGeolocation;
    private final BoundingBox boundingbox;
    private final List<Marker> markers;
    private final String message;

    public CoverageModel(String message) {
        this(null, null, null, message);
    }

    public CoverageModel(Bounds bounds) {
        this(new LatLng((bounds.northeast.lat + bounds.southwest.lat) / 2, (bounds.northeast.lng + bounds.southwest.lng) / 2), bounds);
    }

    public CoverageModel(LatLng userGeolocation, Bounds boundingbox) {
        this(userGeolocation, boundingbox, new ArrayList<>());
    }

    public CoverageModel(LatLng userGeolocation, Bounds boundingbox, List<Marker> markers) {
        this(new Marker(userGeolocation), new BoundingBox(boundingbox), markers, null);
    }

    public CoverageModel(Marker userGeolocation, BoundingBox boundingbox, List<Marker> markers, String message) {
        this.userGeolocation = userGeolocation;
        this.boundingbox = boundingbox;
        this.markers = markers;
        this.message = message;
    }

    public Marker getUserGeolocation() {
        return userGeolocation;
    }

    public BoundingBox getBoundingbox() {
        return boundingbox;
    }

    public List<Marker> getMarkers() {
        return markers;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "CoverageModel{" +
                "userGeolocation=" + userGeolocation +
                ", boundingbox=" + boundingbox +
                ", markers=" + markers +
                ", message='" + message + '\'' +
                '}';
    }
}
