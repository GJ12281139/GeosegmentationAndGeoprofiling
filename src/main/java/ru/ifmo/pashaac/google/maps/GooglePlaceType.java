package ru.ifmo.pashaac.google.maps;

import com.google.maps.model.PlaceType;

/**
 * Created by Pavel Asadchiy
 * 11.05.16 23:21.
 */
public enum GooglePlaceType {

    MUSEUM("museum"),
    PARK("park"),
    ART_GALLERY("art_gallery"),
    CHURCH("church"),
    LIBRARY("library");

    private String placeType;

    GooglePlaceType(final String placeType) {
        this.placeType = placeType;
    }

    @Override
    public String toString() {
        return placeType;
    }

    public PlaceType getPlaceType() {
        return PlaceType.valueOf(name());
    }
}
