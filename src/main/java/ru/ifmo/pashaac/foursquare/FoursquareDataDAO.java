package ru.ifmo.pashaac.foursquare;

import org.apache.log4j.Logger;
import org.springframework.data.mongodb.core.MongoOperations;
import ru.ifmo.pashaac.common.primitives.BoundingBox;
import ru.ifmo.pashaac.configuration.SpringMongoConfig;
import ru.ifmo.pashaac.map.MapService;

import java.util.Collection;
import java.util.List;

/**
 * DAO for foursquare places, need for save/get/handle operations in MongoDB
 *
 * Created by Pavel Asadchiy
 * 08.05.16 23:13.
 */
public class FoursquareDataDAO {

    public static final String BOUNDINGBOX_SUFFIX = "boundingbox";
    public static final String SEARCHER_SUFFIX = "searcher";

    private static final Logger LOG = Logger.getLogger(FoursquareDataDAO.class);

    private final FoursquarePlaceType placeType;
    private final String collection;
    private final MongoOperations mongoOperations;

    public FoursquareDataDAO(FoursquarePlaceType placeType, BoundingBox boundingBox) {
        this.placeType = placeType;
        this.collection = getCollection(boundingBox, placeType);
        this.mongoOperations = SpringMongoConfig.getMongoOperations();
    }

    public static String getCollection(BoundingBox boundingBox, FoursquarePlaceType placeType) {
        return "Foursquare" + "#" + boundingBox.getCountry() + "#" + boundingBox.getCity() + "#" + placeType;
    }

    public Collection<FoursquarePlace> getPlaces() {
        return mongoOperations.findAll(FoursquarePlace.class, collection);
    }

    public Collection<FoursquarePlace> getTopRatingPlaces(int percents) {
        return FoursquarePlace.filterTopCheckins(getPlaces(), percents);
    }

    public void minePlacesIfNeed(MapService mapService, BoundingBox boundingBox) {
        if (exist()) {
            LOG.info("Foursquare table exist or same operation is handling");
            return;
        }
        LOG.info("(Foursquare) Table with city=" + boundingBox.getCity() + ", country=" + boundingBox.getCountry() + ", placeType=" + placeType + " not exist");
        minePlaces(mapService, boundingBox);
    }

    private void minePlaces(MapService mapService, BoundingBox boundingBox) {
        FoursquareDataMiner dataMiner = new FoursquareDataMiner(mapService, placeType);
        LOG.info("Wait until the quadtree algorithm collecting places...");
        dataMiner.quadtreePlaceSearcher(boundingBox);
        insert(dataMiner.getPlaces());
        recreate(dataMiner.getBoundingBoxes(), FoursquareDataDAO.BOUNDINGBOX_SUFFIX);
        recreate(dataMiner.getMarkers(), FoursquareDataDAO.SEARCHER_SUFFIX);
        LOG.info("All places was handled and saved in MongoDB");
    }

    public List<BoundingBox> getBoundingBoxes() {
        return mongoOperations.findAll(BoundingBox.class, collection + "#" + BOUNDINGBOX_SUFFIX);
    }

//    public List<Marker> getSearchers() {
//        return mongoOperations.findAll(Marker.class, collection + "#" + SEARCHER_SUFFIX);
//    }
//
//    public void update(Collection<FoursquarePlace> places) {
//        if (places.isEmpty()) {
//            LOG.error("Empty foursquare places to insert");
//            return;
//        }
//        places.stream().forEach(place -> mongoOperations.save(place, collection));
//    }

    public boolean exist() {
        return mongoOperations.collectionExists(collection);
    }

    public void insert(Collection<FoursquarePlace> places) {
        if (places.isEmpty()) {
            LOG.error("Empty foursquare places to insert");
            return;
        }
        if (exist()) {
            LOG.warn("Collection " + collection + " exists");
            return;
        }
        places.stream()
                .filter(place -> place.getPlaceType().equals(placeType.name()))
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
