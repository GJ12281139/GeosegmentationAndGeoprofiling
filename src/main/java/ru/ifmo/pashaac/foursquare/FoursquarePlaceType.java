package ru.ifmo.pashaac.foursquare;

import ru.ifmo.pashaac.common.primitives.Icon;

/**
 * Foursquare place types
 * <p>
 * Created by Pavel Asadchiy
 * 07.05.16 14:57.
 */
public enum FoursquarePlaceType {

    // Culture
    MUSEUM("4bf58dd8d48988d181941735", Icon.VISTA_BALL_BRONZE_32.getPath(), 80),
    THEATER("4bf58dd8d48988d137941735", Icon.VISTA_BALL_IRON_32.getPath(), 80),
    PARK("4bf58dd8d48988d163941735", Icon.VISTA_BALL_GREEN_32.getPath(), 80),
    FOUNTAIN("56aa371be4b08b9a8d573547", Icon.VISTA_BALL_BLUE_32.getPath(), 95),
    GARDEN("4bf58dd8d48988d15a941735", Icon.VISTA_BALL_GREEN_32.getPath(), 90),
    PALACE("52e81612bcbc57f1066b7a14", Icon.VISTA_BALL_SILVER_32.getPath(), 95),
    CASTLE("50aaa49e4b90af0d42d5de11", Icon.VISTA_BALL_ORANGE_32.getPath(), 90),
    BRIDGE("4bf58dd8d48988d1df941735", Icon.VISTA_BALL_HUE_32.getPath(), 60);

    // NightLife (Bar/clubs/disco/friends meeting)
//    NIGHTLIFE_SPOT("4d4b7105d754a06376d81259"),
//    BOWLING_GREEN("52e81612bcbc57f1066b7a2f"),


    //    OPERA_HOUSE("4bf58dd8d48988d136941735"),
//    BAR("4bf58dd8d48988d116941735"),
//    FOOD("4d4b7105d754a06374d81259"),
//    ATHLETICS_AND_SPORTS("4f4528bc4b90abdf24c9de85"),
//    HOTEL("4bf58dd8d48988d1fa931735");

    String categoryId;
    String icon;
    int filterPercent;

    FoursquarePlaceType(final String categoryId, final String icon, int filterPercent) {
        this.categoryId = categoryId;
        this.icon = icon;
        this.filterPercent = filterPercent;
    }

    public int getFilterPercent() {
        return filterPercent;
    }

    @Override
    public String toString() {
        return name() + " " + categoryId;
    }
}
