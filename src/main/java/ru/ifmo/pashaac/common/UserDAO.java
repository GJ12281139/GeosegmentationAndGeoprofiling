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

    public static void insert(Double lat,                     // user latitude geolocation
                              Double lng,                     // user longitude geolocation
                              String city,                    // user city geolocation
                              String country,                 // user country geolocation
                              boolean isGoogleData,           // use data from Google Maps
                              boolean isFoursquareData,       // use data from Foursquare
                              String placeType,               // placeType depends from source
                              String category,                // places category
                              String clusterAlgorithm,        // machine learning algorithm
                              boolean isBox,                  // show boundingboxes around searchers
                              boolean isSearchers,            // show searchers
                              boolean isSourceIcons,          // use only sources icons (not places icons)
                              List<Integer> isAllPlaces) {          // show all places or filtered)
        String collection = "UserRequests";
        MongoOperations mongoOperations = SpringMongoConfig.getMongoOperations();
        BasicDBObject dbObject = new BasicDBObject();
        dbObject.append("lat", lat);
        dbObject.append("lng", lng);
        dbObject.append("city", city);
        dbObject.append("country", country);
        dbObject.append("isGoogleData", isGoogleData);
        dbObject.append("isFoursquareData", isFoursquareData);
        dbObject.append("placeType", placeType);
        dbObject.append("category", category);
        dbObject.append("clusterAlgorithm", clusterAlgorithm);
        dbObject.append("isBox", isBox);
        dbObject.append("isSearchers", isSearchers);
        dbObject.append("isSourceIcons", isSourceIcons);
        dbObject.append("isAllPlaces", isAllPlaces);
        dbObject.append("requestTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        mongoOperations.insert(dbObject, collection);
    }

}
