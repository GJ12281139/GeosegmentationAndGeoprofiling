package ru.ifmo.pashaac.data.source.foursquare;

import ru.ifmo.pashaac.common.primitives.Icon;

/**
 * Foursquare place types
 * <p>
 * Created by Pavel Asadchiy
 * 07.05.16 14:57.
 */
public enum FoursquarePlaceType {

    // Auto
    AUTO_DEALERSHIP("4eb1c1623b7b52c0e1adc2ec", Icon.VISTA_BALL_ORANGE_32.getPath()),
    AUTO_GARAGE("52f2ab2ebcbc57f1066b8b44", Icon.VISTA_BALL_SILVER_32.getPath()),
    AUTO_WORKSHOP("56aa371be4b08b9a8d5734d3", Icon.VISTA_BALL_BRONZE_32.getPath()),
    CAR_WASH("4f04ae1f2fb6e1c99f3db0ba", Icon.VISTA_BALL_BLUE_32.getPath()),

    // Culture
    MUSEUM("4bf58dd8d48988d181941735", Icon.VISTA_BALL_BRONZE_32.getPath()),
    PARK("4bf58dd8d48988d163941735", Icon.VISTA_BALL_GREEN_32.getPath()),
    PLAZA("4bf58dd8d48988d164941735", Icon.VISTA_BALL_LIGHT_RED_32.getPath()),
    SCULPTURE_GARDEN("4bf58dd8d48988d166941735", Icon.VISTA_BALL_PURPLE_32.getPath()),
    SPIRTUAL_CENTER("4bf58dd8d48988d131941735", Icon.VISTA_BALL_POISON_GREEN_32.getPath()),
    THEATER("4bf58dd8d48988d137941735", Icon.VISTA_BALL_IRON_32.getPath()),
    FOUNTAIN("56aa371be4b08b9a8d573547", Icon.VISTA_BALL_BLUE_32.getPath()),
    GARDEN("4bf58dd8d48988d15a941735", Icon.VISTA_BALL_GREEN_32.getPath()),
    PALACE("52e81612bcbc57f1066b7a14", Icon.VISTA_BALL_SILVER_32.getPath()),
    CASTLE("50aaa49e4b90af0d42d5de11", Icon.VISTA_BALL_ORANGE_32.getPath()),

    // Food
    ASIAN_RESTAURANT("4bf58dd8d48988d142941735", Icon.VISTA_BALL_IRON_32.getPath()),
    JAPANESE_RESTAURANT("4bf58dd8d48988d111941735", Icon.VISTA_BALL_LIGHT_RED_32.getPath()),
    FRENCH_RESTAURANT("4bf58dd8d48988d10c941735", Icon.VISTA_BALL_BLUE_32.getPath()),
    ITALIAN_RESTAURANT("4bf58dd8d48988d110941735", Icon.VISTA_BALL_POISON_GREEN_32.getPath()),
    BAKERY("4bf58dd8d48988d16a941735", Icon.VISTA_BALL_ORANGE_32.getPath()),
    BISTRO("52e81612bcbc57f1066b79f1", Icon.VISTA_BALL_HUE_32.getPath()),
    FAST_FOOD_RESTAURANT("4bf58dd8d48988d16e941735", Icon.VISTA_BALL_PINK_32.getPath()),
    CAFE("4bf58dd8d48988d16d941735", Icon.VISTA_BALL_BRONZE_32.getPath()),

    // Nightlife
    NIGHTLIFE_SPOT("4d4b7105d754a06376d81259", Icon.VISTA_BALL_POISON_GREEN_32.getPath()),
    BOWLING_ALLEY("4bf58dd8d48988d1e4931735", Icon.VISTA_BALL_ORANGE_32.getPath()),
    MOVIE_THEATER("4bf58dd8d48988d17f941735", Icon.VISTA_BALL_IRON_32.getPath()),
    POOL_HALL("4bf58dd8d48988d1e3931735", Icon.VISTA_BALL_GREEN_32.getPath()),

    // Sport
    ATHLETICS_SPORTS("4f4528bc4b90abdf24c9de85", Icon.VISTA_BALL_POISON_GREEN_32.getPath());

    private String categoryId;
    private String icon;

    FoursquarePlaceType(final String categoryId, final String icon) {
        this.categoryId = categoryId;
        this.icon = icon;
    }

    @Override
    public String toString() {
        return name();
    }

    public String getIcon() {
        return icon;
    }

    public String getCategoryId() {
        return categoryId;
    }
}
