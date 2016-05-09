package ru.ifmo.pashaac.common;

import com.mongodb.BasicDBObject;
import org.springframework.data.mongodb.core.MongoOperations;
import ru.ifmo.pashaac.configuration.SpringMongoConfig;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Pavel Asadchiy
 * 09.05.16 11:20.
 */
public class UserDAO {

    public static void insert(double lat, double lng) {
        String collection = "User";
        MongoOperations mongoOperations = SpringMongoConfig.getMongoOperations();
        BasicDBObject dbObject = new BasicDBObject();
        dbObject.append("lat", lat);
        dbObject.append("lng", lng);
        dbObject.append("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        mongoOperations.insert(dbObject, collection);
    }

}
