package ru.ifmo.pashaac.category;

import ru.ifmo.pashaac.common.primitives.BoundingBox;
import ru.ifmo.pashaac.data.source.foursquare.FoursquarePlaceType;
import ru.ifmo.pashaac.data.source.google.maps.GooglePlaceType;
import ru.ifmo.pashaac.map.MapService;

import java.util.Arrays;

/**
 * Food category
 * <p>
 * Created by Pavel Asadchiy
 * on 25.05.16 14:54.
 */
public class Food extends Category {

    private static final GooglePlaceType[] GOOGLE_PLACE_TYPES = {
            GooglePlaceType.CAFE,
            GooglePlaceType.RESTAURANT};

    private static final FoursquarePlaceType[] FOURSQUARE_PLACE_TYPES = {
            FoursquarePlaceType.ASIAN_RESTAURANT,
            FoursquarePlaceType.JAPANESE_RESTAURANT,
            FoursquarePlaceType.FRENCH_RESTAURANT,
            FoursquarePlaceType.ITALIAN_RESTAURANT,
            FoursquarePlaceType.BAKERY,
            FoursquarePlaceType.BISTRO,
            FoursquarePlaceType.FAST_FOOD_RESTAURANT,
            FoursquarePlaceType.CAFE};

    public Food(MapService mapService, BoundingBox boundingBox, String source) {
        super(Arrays.asList(GOOGLE_PLACE_TYPES), Arrays.asList(FOURSQUARE_PLACE_TYPES), mapService, boundingBox, source);
    }

}
