package ru.ifmo.pashaac.category;

import ru.ifmo.pashaac.common.primitives.BoundingBox;
import ru.ifmo.pashaac.data.source.foursquare.FoursquarePlaceType;
import ru.ifmo.pashaac.data.source.google.maps.GooglePlaceType;
import ru.ifmo.pashaac.map.MapService;

import java.util.Arrays;

/**
 * Culture and leisure places
 * <p>
 * Created by Pavel Asadchiy
 * 07.05.16 12:55.
 */
public class Culture extends Category {

    private static final GooglePlaceType[] GOOGLE_PLACE_TYPES = {
            GooglePlaceType.MUSEUM,
            GooglePlaceType.PARK,
            GooglePlaceType.CHURCH};

    private static final FoursquarePlaceType[] FOURSQUARE_PLACE_TYPES = {
            FoursquarePlaceType.MUSEUM,
            FoursquarePlaceType.PARK,
            FoursquarePlaceType.PLAZA,
            FoursquarePlaceType.SCULPTURE_GARDEN,
            FoursquarePlaceType.SPIRTUAL_CENTER,
            FoursquarePlaceType.THEATER,
            FoursquarePlaceType.FOUNTAIN,
            FoursquarePlaceType.GARDEN,
            FoursquarePlaceType.PALACE,
            FoursquarePlaceType.CASTLE};

    public Culture(MapService mapService, BoundingBox boundingBox, String source) {
        super(Arrays.asList(GOOGLE_PLACE_TYPES), Arrays.asList(FOURSQUARE_PLACE_TYPES), mapService, boundingBox, source);
    }

}
