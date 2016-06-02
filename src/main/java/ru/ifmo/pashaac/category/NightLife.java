package ru.ifmo.pashaac.category;

import ru.ifmo.pashaac.common.primitives.BoundingBox;
import ru.ifmo.pashaac.data.source.foursquare.FoursquarePlaceType;
import ru.ifmo.pashaac.data.source.google.maps.GooglePlaceType;
import ru.ifmo.pashaac.map.MapService;

import java.util.Arrays;

/**
 * For noisy friends company
 * <p>
 * Created by Pavel Asadchiy
 * 07.05.16 15:02.
 */
public class NightLife extends Category {

    private static final GooglePlaceType[] GOOGLE_PLACE_TYPES = {
            GooglePlaceType.BOWLING_ALLEY,
            GooglePlaceType.MOVIE_THEATER,
            GooglePlaceType.NIGHT_CLUB};

    private static final FoursquarePlaceType[] FOURSQUARE_PLACE_TYPES = {
            FoursquarePlaceType.NIGHTLIFE_SPOT,
            FoursquarePlaceType.BOWLING_ALLEY,
            FoursquarePlaceType.MOVIE_THEATER,
            FoursquarePlaceType.POOL_HALL};

    public NightLife(MapService mapService, BoundingBox boundingBox, String source) {
        super(Arrays.asList(GOOGLE_PLACE_TYPES), Arrays.asList(FOURSQUARE_PLACE_TYPES), mapService, boundingBox, source);
    }

}
