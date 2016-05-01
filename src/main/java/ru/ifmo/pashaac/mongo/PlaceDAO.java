package ru.ifmo.pashaac.mongo;

import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlaceType;
import org.apache.log4j.Logger;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.ifmo.pashaac.common.wrapper.BoundingBox;
import ru.ifmo.pashaac.common.wrapper.Place;
import ru.ifmo.pashaac.configuration.SpringMongoConfig;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Pavel Asadchiy
 * 30.04.16 9:35.
 */
public class PlaceDAO {

    private static final Logger LOG = Logger.getLogger(PlaceDAO.class);

    public static void merge(@NotNull Collection<Place> places, String region, String country, String suffix) {
        String collection = country + "#" + region + "#" + suffix;
        MongoOperations mongoOperations = SpringMongoConfig.getMongoOperations();
        places.stream().forEach(place -> mongoOperations.save(place, collection));
    }

    public static void merge(@NotNull Collection<Place> places) {
        LOG.info("Trying merge " + places.size() + " places...");
        if (places.isEmpty()) {
            LOG.error("Empty places list");
            return;
        }
        Place first = places.iterator().next();
        merge(places, first.getRegion(), first.getCountry(), first.getType());
    }

    public static void insert(@NotNull Collection<Place> places, String region, String country, String suffix) {
        String collection = country + "#" + region + "#" + suffix;
        MongoOperations mongoOperations = SpringMongoConfig.getMongoOperations();
        for (Place place : places) {
            Place one = mongoOperations.findOne(new Query(Criteria.where("_id").is(place.getId())), Place.class, collection);
            if (one == null) {
                mongoOperations.insert(place, collection);
            }
        }
    }

    public static void insert(@NotNull Collection<Place> places) {
        LOG.info("Trying insert " + places.size() + " places...");
        if (places.isEmpty()) {
            LOG.error("Empty places list");
            return;
        }
        Place first = places.iterator().next();
        insert(places, first.getRegion(), first.getCountry(), first.getType());
        LOG.info("Places inserted in MongoDB");
    }

    public static boolean contains(BoundingBox box, PlaceType placeType) {
        String collection = box.getCountry() + "#" + box.getRegion() + "#" + placeType;
        MongoOperations mongoOperations = SpringMongoConfig.getMongoOperations();
        return mongoOperations.collectionExists(collection);
    }

    public static List<Place> getPlaces(BoundingBox box, PlaceType placeType) {
        String collection = box.getCountry() + "#" + box.getRegion() + "#" + placeType;
        MongoOperations mongoOperations = SpringMongoConfig.getMongoOperations();
        return mongoOperations.findAll(Place.class, collection);
    }

    public static void updatePlacesInfo(BoundingBox box, PlaceType placeType, GeoApiContext context) {
        List<Place> oldPlaces = getPlaces(box, placeType);
        List<Place> newPlaces = oldPlaces.stream().map(place -> {
            try {
                PlaceDetails details = PlacesApi.placeDetails(context, place.getId()).language("en").await();
                return new Place(place, details);
            } catch (Exception e) {
                LOG.error("Can't get information about " + place);
                return null;
            }
        }).collect(Collectors.toList());
        merge(newPlaces);
    }

}
