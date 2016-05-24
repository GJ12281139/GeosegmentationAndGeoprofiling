package ru.ifmo.pashaac.google.maps;

import com.google.maps.model.PlaceType;
import ru.ifmo.pashaac.common.primitives.Icon;

/**
 * Created by Pavel Asadchiy
 * 11.05.16 23:21.
 */
public enum GooglePlaceType {

    // Culture
    MUSEUM("museum", Icon.VISTA_BALL_BRONZE_32.getPath()),
    PARK("park", Icon.VISTA_BALL_GREEN_32.getPath()),
    CHURCH("church", Icon.VISTA_BALL_LIGHT_RED_32.getPath()),

    // Nightlife
    BOWLING_ALLEY("bowling_alley", Icon.VISTA_BALL_ORANGE_32.getPath()),
    MOVIE_THEATER("movie_theater", Icon.VISTA_BALL_LIGHT_RED_32.getPath()),
    NIGHT_CLUB("night_club", Icon.VISTA_BALL_BLUE_GREEN_32.getPath()),

    // Food
    CAFE("cafe", Icon.VISTA_BALL_LIGHT_RED_32.getPath()),
    RESTAURANT("restaurant", Icon.VISTA_BALL_BLUE_GREEN_32.getPath()),

    // Sport
    GYM("gym", Icon.VISTA_BALL_SILVER_32.getPath()),
        // parks too

    // Auto
    CAR_DEALER("car_dealer", Icon.VISTA_BALL_IRON_32.getPath()),
    CAR_RENTAL("car_rental", Icon.VISTA_BALL_ORANGE_32.getPath()),
    CAR_REPAIR("car_repair", Icon.VISTA_BALL_BRONZE_32.getPath()),
    CAR_WASH("car_wash", Icon.VISTA_BALL_BLUE_32.getPath());


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
