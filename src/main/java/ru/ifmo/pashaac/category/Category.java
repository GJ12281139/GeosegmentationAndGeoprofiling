package ru.ifmo.pashaac.category;

import ru.ifmo.pashaac.common.primitives.Marker;
import ru.ifmo.pashaac.foursquare.FoursquarePlace;
import ru.ifmo.pashaac.google.maps.GooglePlace;

import java.util.List;
import java.util.Set;

/**
 * Created by Pavel Asadchiy
 * 07.05.16 12:41.
 */
public interface Category {

    Set<GooglePlace> getGooglePlaces();

    Set<FoursquarePlace> getFoursquarePlaces();

    List<Marker> getClustersAllSources();

    List<Marker> getGoogleClusters();

    List<Marker> getFoursquareClusters();

}
