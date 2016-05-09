package ru.ifmo.pashaac.category;

import com.google.maps.model.PlaceType;
import ru.ifmo.pashaac.google.maps.GooglePlace;

/**
 * Created by Pavel Asadchiy
 * 07.05.16 12:41.
 */
public interface Category {

    GooglePlace getPlace(PlaceType placeType);

    GooglePlace[] getPlaces(PlaceType[] placeTypes);

    void useDifferentIcons();

    void useSameIcons();

    void clearing();
}
