package ru.ifmo.pashaac.foursquare;

/**
 * Foursquare place types
 *
 * Created by Pavel Asadchiy
 * 07.05.16 14:57.
 */
public enum FoursquarePlaceType {

    MUSEUM("4bf58dd8d48988d181941735"),
    THEATER("4bf58dd8d48988d137941735"),
    OPERA_HOUSE("4bf58dd8d48988d136941735"),
    BAR("4bf58dd8d48988d116941735"),
    FOOD("4d4b7105d754a06374d81259"),
    ATHLETICS_AND_SPORTS("4f4528bc4b90abdf24c9de85"),
    FOUNTAIN("56aa371be4b08b9a8d573547"),
    GARDEN("4bf58dd8d48988d15a941735"),
    PALACE("52e81612bcbc57f1066b7a14"),
    HOTEL("4bf58dd8d48988d1fa931735"),
    PARK("4bf58dd8d48988d163941735");

    String categoryId;

    FoursquarePlaceType(String categoryId) {
        this.categoryId = categoryId;
    }
}
