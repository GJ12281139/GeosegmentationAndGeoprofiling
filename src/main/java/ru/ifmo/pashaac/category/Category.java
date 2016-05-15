package ru.ifmo.pashaac.category;

import ru.ifmo.pashaac.common.Searcher;
import ru.ifmo.pashaac.foursquare.FoursquarePlace;
import ru.ifmo.pashaac.google.maps.GooglePlace;

import java.util.List;
import java.util.Set;

/**
 * Created by Pavel Asadchiy
 * 07.05.16 12:41.
 */
public interface Category {

    Set<GooglePlace> getGooglePlaces(boolean useSourceIcons);

    Set<FoursquarePlace> getFoursquarePlaces(boolean useSourceIcons);

    List<Searcher> getKernels(boolean needClearing);

    List<Searcher> getGoogleKernels(boolean needClearing);

    List<Searcher> getFoursquareKernels(boolean needClearing);

}
