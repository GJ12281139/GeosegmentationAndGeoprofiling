package ru.ifmo.pashaac.google.maps;

import org.apache.log4j.Logger;
import org.springframework.data.mongodb.core.MongoOperations;
import ru.ifmo.pashaac.common.BoundingBox;
import ru.ifmo.pashaac.common.Searcher;
import ru.ifmo.pashaac.configuration.SpringMongoConfig;

import java.util.Collection;
import java.util.List;

/**
 * Created by Pavel Asadchiy
 * 09.05.16 0:03.
 */
public class GoogleDataDAO {

    public static final String BOUNDINGBOX_SUFFIX = "boundingbox";
    public static final String SEARCHER_SUFFIX = "searcher";

    private static final Logger LOG = Logger.getLogger(GoogleDataDAO.class);

    private final String placeType;
    private final String collection;
    private final MongoOperations mongoOperations;

    public GoogleDataDAO(String placeType, String city, String country) {
        this.placeType = placeType;
        this.collection = "Google" + "#" + country + "#" + city + "#" + placeType;
        this.mongoOperations = SpringMongoConfig.getMongoOperations();
    }

    public List<GooglePlace> getPlaces() {
        return mongoOperations.findAll(GooglePlace.class, collection);
    }

    public List<BoundingBox> getBoundingBoxes() {
        return mongoOperations.findAll(BoundingBox.class, collection + "#" + BOUNDINGBOX_SUFFIX);
    }

    public List<Searcher> getSearchers() {
        return mongoOperations.findAll(Searcher.class, collection + "#" + SEARCHER_SUFFIX);
    }

    public void update(Collection<GooglePlace> places) {
        if (places.isEmpty()) {
            LOG.error("Empty google maps places to insert");
            return;
        }
        places.stream().forEach(place -> mongoOperations.save(place, collection));
    }

    public boolean exist() {
        return mongoOperations.collectionExists(collection);
    }

    public void insert(Collection<GooglePlace> places) {
        if (places.isEmpty()) {
            LOG.error("Empty google maps places to insert");
            return;
        }
        if (exist()) {
            LOG.warn("Collection " + collection + " exists");
            return;
        }
        places.stream()
                .filter(place -> place.getPlaceType().equals(placeType))
                .forEach(place -> mongoOperations.insert(place, collection));
        LOG.info("Data inserted if placeType was matched");
    }

    public <T> void recreate(Collection<T> objects, String suffix) {
        if (objects.isEmpty()) {
            LOG.error("Empty objects to insert");
            return;
        }
        mongoOperations.dropCollection(collection + "#" + suffix);
        objects.stream().forEach(obj -> mongoOperations.insert(obj, collection + "#" + suffix));
        LOG.info("Collection " + collection + "#" + suffix + " was recreated");
    }

}
