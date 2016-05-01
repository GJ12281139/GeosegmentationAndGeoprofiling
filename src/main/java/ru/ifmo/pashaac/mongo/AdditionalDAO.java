package ru.ifmo.pashaac.mongo;

import com.google.maps.model.PlaceType;
import com.mongodb.BasicDBObject;
import org.springframework.data.mongodb.core.MongoOperations;
import ru.ifmo.pashaac.common.wrapper.BoundingBox;
import ru.ifmo.pashaac.common.wrapper.Searcher;
import ru.ifmo.pashaac.configuration.SpringMongoConfig;

import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Pavel Asadchiy
 * 01.05.16 16:35.
 */
public class AdditionalDAO {

    // User geolocation
    public static void insert(double lat, double lng) {
        String collection = "User";
        MongoOperations mongoOperations = SpringMongoConfig.getMongoOperations();
        BasicDBObject dbObject = new BasicDBObject();
        dbObject.append("lat", lat);
        dbObject.append("lng", lng);
        dbObject.append("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        mongoOperations.insert(dbObject, collection);
    }

    // Searchers
    public static void insertSearchers(@NotNull List<Searcher> searchers, BoundingBox box, PlaceType placeType) {
        String collection = box.getCountry() + "#" + box.getRegion() + "#" + placeType + "#searcher";
        MongoOperations mongoOperations = SpringMongoConfig.getMongoOperations();
        mongoOperations.dropCollection(collection);
        mongoOperations.insert(searchers, collection);
    }

    public static List<Searcher> getSearchers(BoundingBox box, PlaceType placeType) {
        String collection = box.getCountry() + "#" + box.getRegion() + "#" + placeType + "#searcher";
        MongoOperations mongoOperations = SpringMongoConfig.getMongoOperations();
        return mongoOperations.findAll(Searcher.class, collection);
    }

    // Boundingboxes
    public static void insertBoundingBoxes(@NotNull List<BoundingBox> boundingBoxes, BoundingBox box, PlaceType placeType) {
        String collection = box.getCountry()+ "#" + box.getRegion()+ "#" + placeType + "#boundingbox";
        MongoOperations mongoOperations = SpringMongoConfig.getMongoOperations();
        mongoOperations.dropCollection(collection);
        mongoOperations.insert(boundingBoxes, collection);
    }

    public static List<BoundingBox> getBoundingboxes(BoundingBox box, PlaceType placeType) {
        String collection = box.getCountry() + "#" + box.getRegion() + "#" + placeType + "#boundingbox";
        MongoOperations mongoOperations = SpringMongoConfig.getMongoOperations();
        return mongoOperations.findAll(BoundingBox.class, collection);
    }

}
