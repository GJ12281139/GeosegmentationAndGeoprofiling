package ru.ifmo.pashaac.category;

import ru.ifmo.pashaac.common.Searcher;
import ru.ifmo.pashaac.foursquare.FoursquarePlace;
import ru.ifmo.pashaac.google.maps.GooglePlace;

import java.util.List;
import java.util.Set;

/**
 * Created by Pavel Asadchiy
 * 07.05.16 15:02.
 */
public class NightLife implements Category {

    @Override
    public Set<GooglePlace> getGooglePlaces() {
        return null;
    }

    @Override
    public Set<FoursquarePlace> getFoursquarePlaces() {
        return null;
    }

    @Override
    public List<Searcher> getKernels(boolean needClearing) {
        return null;
    }

    @Override
    public List<Searcher> getGoogleKernels(boolean needClearing) {
        return null;
    }

    @Override
    public List<Searcher> getFoursquareKernels(boolean needClearing) {
        return null;
    }

}
