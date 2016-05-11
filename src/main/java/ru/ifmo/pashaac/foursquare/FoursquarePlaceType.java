package ru.ifmo.pashaac.foursquare;

/**
 * Foursquare place types
 * <p>
 * Created by Pavel Asadchiy
 * 07.05.16 14:57.
 */
public enum FoursquarePlaceType {
    // Culture
    MUSEUM("4bf58dd8d48988d181941735"),
    THEATER("4bf58dd8d48988d137941735"),
    PARK("4bf58dd8d48988d163941735"),
    ART_GALLERY("4bf58dd8d48988d1e2931735"),
    CIRCUS("52e81612bcbc57f1066b79e7"),
    CONCERT_HALL("5032792091d4c4b30a586d5c"),
    PUBLIC_ART("507c8c4091d498d9fc8c67a9"),
    WATER_PARK("4bf58dd8d48988d193941735"),
    BOTANICAL_GARDEN("52e81612bcbc57f1066b7a22"),
    BRIDGE("4bf58dd8d48988d1df941735"),
    CASTLE("50aaa49e4b90af0d42d5de11"),
    FOUNTAIN("56aa371be4b08b9a8d573547"),
    GARDEN("4bf58dd8d48988d15a941735"),
    PALACE("52e81612bcbc57f1066b7a14"),

    // NightLife (Bar/clubs/disco/friends meeting)
    NIGHTLIFE_SPOT("4d4b7105d754a06376d81259"),
    BOWLING_GREEN("52e81612bcbc57f1066b7a2f"),


    //    OPERA_HOUSE("4bf58dd8d48988d136941735"),
    BAR("4bf58dd8d48988d116941735"),
    FOOD("4d4b7105d754a06374d81259"),
    ATHLETICS_AND_SPORTS("4f4528bc4b90abdf24c9de85"),
    HOTEL("4bf58dd8d48988d1fa931735");

    String categoryId;

    FoursquarePlaceType(String categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return name() + " " + categoryId;
    }
}
