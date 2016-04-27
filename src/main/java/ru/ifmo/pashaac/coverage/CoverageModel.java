package ru.ifmo.pashaac.coverage;

import com.google.maps.model.Bounds;
import com.google.maps.model.LatLng;
import ru.ifmo.pashaac.common.BoundingBox;
import ru.ifmo.pashaac.common.Place;

/**
 * Created by Pavel Asadchiy
 * 19.04.16 16:16.
 */
public class CoverageModel {

    private final Place user;
    private final BoundingBox box;
    private final String error;

    private CoverageModel(Place user, BoundingBox box, String error) {
        this.user = user;
        this.box = box;
        this.error = error;
    }

    public CoverageModel(Place user, BoundingBox box) {
        this(user, box, null);
    }

    public CoverageModel(LatLng user, BoundingBox box) {
        this(new Place.Builder().setLat(user.lat).setLng(user.lng).build(), box, null);
    }

    public CoverageModel(String error) {
        this(null, null, error);
    }

    public Place getUser() {
        return user;
    }

    public BoundingBox getBox() {
        return box;
    }

    public Bounds getBounds() {
        Bounds bounds = new Bounds();
        bounds.southwest = box.getSouthwest().getLatLng();
        bounds.northeast = box.getNortheast().getLatLng();
        return bounds;
    }

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return "CoverageModel{" +
                "user=" + user +
                ", box=" + box +
                ", error='" + error + '\'' +
                '}';
    }
}
