package ru.ifmo.pashaac.data.source;

import org.apache.log4j.Logger;
import org.springframework.data.mongodb.core.MongoOperations;
import ru.ifmo.pashaac.common.primitives.BoundingBox;
import ru.ifmo.pashaac.configuration.SpringMongoConfig;
import ru.ifmo.pashaac.map.MapService;

import java.util.Collection;
import java.util.List;

/**
 * Created by Pavel Asadchiy
 * on 30.05.16 23:39.
 */
public class DataDAO {

    public static final String BOUNDINGBOX_SUFFIX = "boundingbox";
    public static final String SEARCHER_SUFFIX = "searcher";

    private static final Logger LOG = Logger.getLogger(DataDAO.class);

    private final String placeType;
    private final String source;
    private final String collection;
    private final BoundingBox boundingBox;
    private final MongoOperations mongoOperations;

    public DataDAO(BoundingBox boundingBox, String placeType, String source) {
        this.placeType = placeType;
        this.source = source;
        this.collection = getCollectionName(boundingBox, placeType, source);
        this.boundingBox = boundingBox;
        this.mongoOperations = SpringMongoConfig.getMongoOperations();
    }

    public static String getCollectionName(BoundingBox boundingBox, String placeType, String source) {
        return source + "#" + boundingBox.getCountry() + "#" + boundingBox.getCity() + "#" + placeType;
    }

    private List<Place> getPlaces() {
        return mongoOperations.findAll(Place.class, collection);
    }

    public Collection<Place> getTopRatingPlaces(int percents) {
        return Place.filterTopRating(getPlaces(), percents);
    }

    public List<BoundingBox> getBoundingBoxes() {
        return mongoOperations.findAll(BoundingBox.class, collection + "#" + BOUNDINGBOX_SUFFIX);
    }

    private void minePlaces(MapService mapService) {
        DataMiner miner = new DataMiner(mapService, source, placeType);
        LOG.info("Wait until the quadtree algorithm collecting places...");
        miner.quadtreePlaceSearcher(boundingBox);
        insert(miner.getPlaces());
        recreate(miner.getBoundingBoxes(), BOUNDINGBOX_SUFFIX);
        recreate(miner.getSearchers(), SEARCHER_SUFFIX);
    }

    public void minePlacesIfNeed(MapService mapService) {
        if (exist()) {
            LOG.info(collection + " table exist or same operation is handling");
            return;
        }
        LOG.info("(" + source + ") Table with city=" + boundingBox.getCity() + ", country=" + boundingBox.getCountry() + ", placeType=" + placeType + " not exist");
        minePlaces(mapService);
    }

    public boolean exist() {
        return mongoOperations.collectionExists(collection);
    }

    public void insert(Collection<Place> places) {
        if (exist()) {
            LOG.warn("Table " + collection + " exists");
            return;
        }
        mongoOperations.createCollection(collection);
        mongoOperations.insert(places, collection);
        LOG.info("Table " + collection + " was inserted");
    }

    public <T> void recreate(Collection<T> objects, String suffix) {
        mongoOperations.dropCollection(collection + "#" + suffix);
        mongoOperations.insert(objects, collection + "#" + suffix);
        LOG.info("Table " + collection + "#" + suffix + " was recreated");
    }

}
