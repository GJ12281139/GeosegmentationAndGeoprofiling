package ru.ifmo.pashaac.category;

import ru.ifmo.pashaac.common.primitives.BoundingBox;
import ru.ifmo.pashaac.foursquare.FoursquarePlaceType;
import ru.ifmo.pashaac.google.maps.GooglePlaceType;
import ru.ifmo.pashaac.map.MapService;

import java.util.Arrays;

/**
 * Auto theme
 * <p>
 * Created by Pavel Asadchiy
 * 07.05.16 15:11.
 */
public class Auto extends Category {

    private static final GooglePlaceType[] GOOGLE_PLACE_TYPES = {
            GooglePlaceType.CAR_DEALER,
            GooglePlaceType.CAR_RENTAL,
            GooglePlaceType.CAR_REPAIR,
            GooglePlaceType.CAR_WASH};

    private static final FoursquarePlaceType[] FOURSQUARE_PLACE_TYPES = {
            FoursquarePlaceType.AUTO_DEALERSHIP,
            FoursquarePlaceType.AUTO_GARAGE,
            FoursquarePlaceType.AUTO_WORKSHOP,
            FoursquarePlaceType.CAR_WASH};

    public Auto(MapService mapService, BoundingBox boundingBox) {
        super(Arrays.asList(GOOGLE_PLACE_TYPES), Arrays.asList(FOURSQUARE_PLACE_TYPES), mapService, boundingBox);
    }

}
