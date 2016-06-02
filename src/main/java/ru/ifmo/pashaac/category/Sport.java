package ru.ifmo.pashaac.category;

import ru.ifmo.pashaac.common.primitives.BoundingBox;
import ru.ifmo.pashaac.data.source.foursquare.FoursquarePlaceType;
import ru.ifmo.pashaac.data.source.google.maps.GooglePlaceType;
import ru.ifmo.pashaac.map.MapService;

import java.util.Arrays;

/**
 * Sport places: football, basketball, etc
 * <p>
 * Created by Pavel Asadchiy
 * 07.05.16 14:53.
 */
public class Sport extends Category {

    private static final GooglePlaceType[] GOOGLE_PLACE_TYPES = {
            GooglePlaceType.GYM};

    private static final FoursquarePlaceType[] FOURSQUARE_PLACE_TYPES = {
            FoursquarePlaceType.ATHLETICS_SPORTS};

    public Sport(MapService mapService, BoundingBox boundingBox, String source) {
        super(Arrays.asList(GOOGLE_PLACE_TYPES), Arrays.asList(FOURSQUARE_PLACE_TYPES), mapService, boundingBox, source);
    }

}
