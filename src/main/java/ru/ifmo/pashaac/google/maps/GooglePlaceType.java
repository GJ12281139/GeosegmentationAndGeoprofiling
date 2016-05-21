package ru.ifmo.pashaac.google.maps;

import com.google.maps.model.PlaceType;
import ru.ifmo.pashaac.common.primitives.Icon;

/**
 * Created by Pavel Asadchiy
 * 11.05.16 23:21.
 */
public enum GooglePlaceType {

    MUSEUM("museum", Icon.VISTA_BALL_BRONZE_32.getPath()),
    PARK("park", Icon.VISTA_BALL_GREEN_32.getPath()),
    CHURCH("church", Icon.VISTA_BALL_LIGHT_RED_32.getPath());

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
