package ru.ifmo.pashaac.category;

import com.google.maps.model.PlaceType;
import ru.ifmo.pashaac.google.maps.GooglePlace;

/**
 * Культурно досуговые места
 *
 * Created by Pavel Asadchiy
 * 07.05.16 12:55.
 */
public class Culture implements Category {

    public static final PlaceType[] googlePlaceTypes = {PlaceType.MUSEUM, PlaceType.PARK, PlaceType.ART_GALLERY};

    @Override
    public GooglePlace getPlace(PlaceType placeType) {
        return null;
    }

    @Override
    public GooglePlace[] getPlaces(PlaceType[] placeTypes) {
        return new GooglePlace[0];
    }

    @Override
    public void useDifferentIcons() {

    }

    @Override
    public void useSameIcons() {

    }

    @Override
    public void clearing() {

    }
}
