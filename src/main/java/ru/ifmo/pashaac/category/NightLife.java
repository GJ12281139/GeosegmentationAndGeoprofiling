package ru.ifmo.pashaac.category;

import com.google.maps.model.PlaceType;
import ru.ifmo.pashaac.google.maps.GooglePlace;

/**
 * Created by Pavel Asadchiy
 * 07.05.16 15:02.
 */
public class NightLife implements Category {

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
