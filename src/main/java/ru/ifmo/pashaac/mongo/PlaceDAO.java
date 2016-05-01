package ru.ifmo.pashaac.mongo;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.ifmo.pashaac.common.wrapper.Place;
import ru.ifmo.pashaac.configuration.SpringMongoConfig;

import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * Created by Pavel Asadchiy
 * 30.04.16 9:35.
 */
public class PlaceDAO {

    public static void update(@NotNull Collection<Place> places, String region, String country, String suffix) {
        String collection = country + "#" + region + "#" + suffix;
        MongoOperations mongoOperations = SpringMongoConfig.getMongoOperations();
        places.stream().forEach(place -> mongoOperations.save(place, collection));
    }

    public static void update(@NotNull Collection<Place> places) {
        Place first = places.iterator().next();
        update(places, first.getRegion(), first.getCountry(), first.getType());
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
        Place first = places.iterator().next();
        insert(places, first.getRegion(), first.getCountry(), first.getType());
    }

}
