package ru.ifmo.pashaac.category;

import ru.ifmo.pashaac.common.primitives.BoundingBox;
import ru.ifmo.pashaac.data.source.DataDAO;
import ru.ifmo.pashaac.data.source.Place;
import ru.ifmo.pashaac.data.source.foursquare.FoursquarePlaceType;
import ru.ifmo.pashaac.data.source.google.maps.GooglePlaceType;
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
    protected final String source;

    protected Category(List<GooglePlaceType> googlePlaceTypes,
                       List<FoursquarePlaceType> foursquarePlaceTypes,
                       MapService mapService,
                       BoundingBox boundingBox,
                       String source) {
        this.googlePlaceTypes = googlePlaceTypes;
        this.foursquarePlaceTypes = foursquarePlaceTypes;
        this.mapService = mapService;
        this.boundingBox = boundingBox;
        this.source = source;
    }

    public Collection<Place> getPlaces(List<Integer> percents) {
        if (Place.FOURSQUARE_SOURCE.equals(source)) {
            if (percents.size() != foursquarePlaceTypes.size()) {
                throw new IllegalStateException("Percents size " + percents.size() + " not equals foursquare type size " + foursquarePlaceTypes.size());
            }
            List<Place> places = new ArrayList<>();
            for (int i = 0; i < foursquarePlaceTypes.size(); i++) {
                places.addAll(placeTypePlaces(foursquarePlaceTypes.get(i).name(), percents.get(i)));
            }
            return places;
        }

        if (Place.GOOGLE_MAPS_SOURCE.equals(source)) {
            if (percents.size() != googlePlaceTypes.size()) {
                throw new IllegalStateException("Percents size " + percents.size() + " not equals google type size " + googlePlaceTypes.size());
            }
            List<Place> places = new ArrayList<>();
            for (int i = 0; i < googlePlaceTypes.size(); i++) {
                places.addAll(placeTypePlaces(googlePlaceTypes.get(i).name(), percents.get(i)));
            }
            return places;
        }
        return new ArrayList<>();
    }

    private Collection<Place> placeTypePlaces(String placeType, int percents) {
        if (!mapService.getHandlingMineOperations().containsKey(DataDAO.getCollectionName(boundingBox, placeType, source))) {
            mapService.getHandlingMineOperations().put(DataDAO.getCollectionName(boundingBox, placeType, source), true);
            DataDAO dao = new DataDAO(boundingBox, placeType, source);
            dao.minePlacesIfNeed(mapService);
            mapService.getHandlingMineOperations().remove(DataDAO.getCollectionName(boundingBox, placeType, source));
            return dao.getTopRatingPlaces(percents);
        }
        return new ArrayList<>();
    }

    public Collection<BoundingBox> getBoundingBoxes() {
        if (Place.GOOGLE_MAPS_SOURCE.equals(source)) {
            return googlePlaceTypes.stream()
                    .map(placeType -> new DataDAO(boundingBox, placeType.name(), source).getBoundingBoxes())
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        }
        if (Place.FOURSQUARE_SOURCE.equals(source)) {
            return foursquarePlaceTypes.stream()
                    .map(placeType -> new DataDAO(boundingBox, placeType.name(), source).getBoundingBoxes())
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public static Category getCategory(MapService mapService, BoundingBox boundingBox, String category, String source) {
        if (category == null) {
            throw new IllegalArgumentException("Incorrect category null");
        }
        switch (category.trim().toLowerCase()) {
            case "auto":
                return new Auto(mapService, boundingBox, source);
            case "culture":
                return new Culture(mapService, boundingBox, source);
            case "food":
                return new Food(mapService, boundingBox, source);
            case "nightlife":
                return new NightLife(mapService, boundingBox, source);
            case "sport":
                return new Sport(mapService, boundingBox, source);
            default:
                throw new IllegalArgumentException("Incorrect category " + category);
        }
    }
}
