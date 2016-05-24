package ru.ifmo.pashaac.category;

import ru.ifmo.pashaac.common.primitives.BoundingBox;
import ru.ifmo.pashaac.common.primitives.Marker;
import ru.ifmo.pashaac.foursquare.FoursquarePlace;
import ru.ifmo.pashaac.google.maps.GooglePlace;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by Pavel Asadchiy
 * 07.05.16 12:41.
 */
public interface Category {

    Set<GooglePlace> getGooglePlaces(List<Integer> percents);

    Set<FoursquarePlace> getFoursquarePlaces(List<Integer> percents);

    @Nonnull
    List<BoundingBox> getGoogleBoundingBoxes();

    @Nonnull
    List<BoundingBox> getFoursquareBoundingBoxes();

    List<Marker> getClusters(final Collection<Marker> places);

}
