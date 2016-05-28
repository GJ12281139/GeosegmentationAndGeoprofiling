package ru.ifmo.pashaac.category;

import ru.ifmo.pashaac.common.primitives.BoundingBox;
import ru.ifmo.pashaac.foursquare.FoursquareDataDAO;
import ru.ifmo.pashaac.foursquare.FoursquarePlace;
import ru.ifmo.pashaac.foursquare.FoursquarePlaceType;
import ru.ifmo.pashaac.google.maps.GoogleDataDAO;
import ru.ifmo.pashaac.google.maps.GooglePlace;
import ru.ifmo.pashaac.google.maps.GooglePlaceType;
import ru.ifmo.pashaac.map.MapService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Common methods of specific categories
 * <p>
 * Created by Pavel Asadchiy
 * 07.05.16 12:41.
 */
public abstract class Category {

    protected final List<GooglePlaceType> googlePlaceTypes;
    protected final List<FoursquarePlaceType> foursquarePlaceTypes;
    protected final MapService mapService;
    protected final BoundingBox boundingBox;

    protected Category(List<GooglePlaceType> googlePlaceTypes,
                       List<FoursquarePlaceType> foursquarePlaceTypes,
                       MapService mapService,
                       BoundingBox boundingBox) {
        this.googlePlaceTypes = googlePlaceTypes;
        this.foursquarePlaceTypes = foursquarePlaceTypes;
        this.mapService = mapService;
        this.boundingBox = boundingBox;
    }

    public Collection<GooglePlace> getGooglePlaces(List<Integer> percents) {
        if (percents.size() != googlePlaceTypes.size()) {
            throw new IllegalStateException("Percents size " + percents.size() + " not equals google type size " + googlePlaceTypes.size());
        }
        for (GooglePlaceType placeType : googlePlaceTypes) {
            mapService.getHandlingMineOperations().put(GoogleDataDAO.getCollection(boundingBox, placeType), true);
        }
        List<GooglePlace> googlePlaces = new ArrayList<>();
        for (int i = 0; i < googlePlaceTypes.size(); i++) {
            GoogleDataDAO dao = new GoogleDataDAO(googlePlaceTypes.get(i), boundingBox);
            dao.minePlacesIfNeed(mapService, boundingBox);
            googlePlaces.addAll(dao.getTopRatingPlaces(percents.get(i)));
        }
        for (GooglePlaceType placeType : googlePlaceTypes) {
            mapService.getHandlingMineOperations().remove(GoogleDataDAO.getCollection(boundingBox, placeType));
        }
        return googlePlaces;
    }

    public Collection<FoursquarePlace> getFoursquarePlaces(List<Integer> percents) {
        if (percents.size() != foursquarePlaceTypes.size()) {
            throw new IllegalStateException("Percents size " + percents.size() + " not equals foursquare type size " + foursquarePlaceTypes.size());
        }
        for (FoursquarePlaceType placeType : foursquarePlaceTypes) {
            mapService.getHandlingMineOperations().put(FoursquareDataDAO.getCollection(boundingBox, placeType), true);
        }
        List<FoursquarePlace> foursquarePlaces = new ArrayList<>();
        for (int i = 0; i < foursquarePlaceTypes.size(); i++) {
            FoursquareDataDAO dao = new FoursquareDataDAO(foursquarePlaceTypes.get(i), boundingBox);
            dao.minePlacesIfNeed(mapService, boundingBox);
            foursquarePlaces.addAll(dao.getTopRatingPlaces(percents.get(i)));
        }
        for (FoursquarePlaceType placeType : foursquarePlaceTypes) {
            mapService.getHandlingMineOperations().remove(FoursquareDataDAO.getCollection(boundingBox, placeType));
        }
        return foursquarePlaces;
    }

    public Collection<BoundingBox> getGoogleBoundingBoxes() {
        return googlePlaceTypes.stream()
                .map(placeType -> new GoogleDataDAO(placeType, boundingBox).getBoundingBoxes())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public Collection<BoundingBox> getFoursquareBoundingBoxes() {
        return foursquarePlaceTypes.stream()
                .map(placeType -> new FoursquareDataDAO(placeType, boundingBox).getBoundingBoxes())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public static Category getCategory(String category, MapService mapService, BoundingBox boundingBox) {
        if (category == null) {
            throw new IllegalArgumentException("Incorrect category null");
        }
        switch (category.trim().toLowerCase()) {
            case "auto":
                return new Auto(mapService, boundingBox);
            case "culture":
                return new Culture(mapService, boundingBox);
            case "food":
                return new Food(mapService, boundingBox);
            case "nightlife":
                return new NightLife(mapService, boundingBox);
            case "sport":
                return new Sport(mapService, boundingBox);
            default:
                throw new IllegalArgumentException("Incorrect category " + category);
        }
    }
}
