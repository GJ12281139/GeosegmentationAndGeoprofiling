package ru.ifmo.pashaac.common;

import com.mongodb.BasicDBObject;
import org.springframework.data.mongodb.core.MongoOperations;
import ru.ifmo.pashaac.configuration.SpringMongoConfig;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * DAO for work with user input information
 * <p>
 * Created by Pavel Asadchiy
 * 09.05.16 11:20.
 */
public class UserDAO {

    public static void insert(double lat, double lng) {
        String collection = "UserGeolocation";
        MongoOperations mongoOperations = SpringMongoConfig.getMongoOperations();
        BasicDBObject dbObject = new BasicDBObject();
        dbObject.append("lat", lat);
        dbObject.append("lng", lng);
        dbObject.append("requestTime", getTime());
        mongoOperations.insert(dbObject, collection);
    }

    public static void insert(double lat, double lng, String city, String country, String source, String category,
                              List<Integer> percents, int segmentMinRadius, int segmentMaxRadius, int segmentsCountPercent) {
        String collection = "UserRequest";
        MongoOperations mongoOperations = SpringMongoConfig.getMongoOperations();
        BasicDBObject dbObject = new BasicDBObject();
        dbObject.append("lat", lat);
        dbObject.append("lng", lng);
        dbObject.append("city", city);
        dbObject.append("country", country);
        dbObject.append("source", source);
        dbObject.append("category", category);
        dbObject.append("percents", percents);
        dbObject.append("requestTime", getTime());
        dbObject.append("segmentMinRadius", segmentMinRadius);
        dbObject.append("segmentMaxRadius", segmentMaxRadius);
        dbObject.append("segmentsCountPercent", segmentsCountPercent);
        mongoOperations.insert(dbObject, collection);
    }

    public static void insert(String source, String placeType, int placesSize, String city, int apiCall, String startTime, String endTime) {
        String collection = "UserDataCollection";
        MongoOperations mongoOperations = SpringMongoConfig.getMongoOperations();
        BasicDBObject dbObject = new BasicDBObject();
        dbObject.append("source", source);
        dbObject.append("placeType", placeType);
        dbObject.append("city", city);
        dbObject.append("placesSize", placesSize);
        dbObject.append("apiCall", apiCall);
        dbObject.append("startTime", startTime);
        dbObject.append("endTime", endTime);
        mongoOperations.insert(dbObject, collection);
    }

    public static String getTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ").format(new Date());
    }

}
