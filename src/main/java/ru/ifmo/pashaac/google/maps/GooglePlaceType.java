package ru.ifmo.pashaac.google.maps;

import com.google.maps.model.PlaceType;
import ru.ifmo.pashaac.map.MapService;

/**
 * Created by Pavel Asadchiy
 * 11.05.16 23:21.
 */
public enum GooglePlaceType {

    MUSEUM("museum", MapService.ICON_PATH + "vista.ball.bronze.32.png"),
    PARK("park", MapService.ICON_PATH + "vista.ball.green.32.png");
//    ART_GALLERY("art_gallery"),
//    CHURCH("church"),
//    LIBRARY("library"),
//    FOOD("food");

    String placeType;
    String icon;

    GooglePlaceType(final String placeType, final String icon) {
        this.placeType = placeType;
        this.icon = icon;
    }

    @Override
    public String toString() {
        return placeType;
    }

    public PlaceType getPlaceType() {
        return PlaceType.valueOf(name());
    }
}
