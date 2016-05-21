package ru.ifmo.pashaac.category;

import ru.ifmo.pashaac.common.primitives.Marker;
import ru.ifmo.pashaac.foursquare.FoursquarePlace;
import ru.ifmo.pashaac.google.maps.GooglePlace;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by Pavel Asadchiy
 * 07.05.16 12:41.
 */
public interface Category {

    Set<GooglePlace> getGooglePlaces(boolean all);

    Set<FoursquarePlace> getFoursquarePlaces(boolean all);

    List<Marker> getClusters(final Collection<Marker> places);

}
