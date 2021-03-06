package ru.ifmo.pashaac.map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import ru.ifmo.pashaac.category.Category;
import ru.ifmo.pashaac.common.Response;
import ru.ifmo.pashaac.common.UserDAO;
import ru.ifmo.pashaac.common.primitives.BoundingBox;
import ru.ifmo.pashaac.common.primitives.Cluster;
import ru.ifmo.pashaac.data.source.Place;
import ru.ifmo.pashaac.segmentation.Algorithm;
import ru.ifmo.pashaac.segmentation.Segmentation;

import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * Main controller!!!
 * <p>
 * Created by Pavel Asadchiy
 * 18.04.16 22:17.
 */
@Controller
@RequestMapping
public class MapController {

    public static final String VIEW_JSP_NAME = "map";

//    public static final String VIEW_DEBUG_JSP_NAME = "debug";
//    public static final String VIEW_ERROR = "error";
//    public static final String VIEW_USER = "user";
//    public static final String VIEW_USER_CITY = "user_city";
//    public static final String VIEW_USER_COUNTRY = "user_country";
//    public static final String VIEW_BOUNDING_BOXES = "boxes";
//    public static final String VIEW_MARKERS = "places";
//    public static final String VIEW_GOOGLE_PLACES = "google_places";
//    public static final String VIEW_FOURSQUARE_PLACES = "foursquare_places";
//    public static final String VIEW_CLUSTERS = "clusters";

    private static final Logger LOG = Logger.getLogger(MapController.class);

    @Autowired
    private final MapService mapService;

    public MapController(MapService mapService) {
        this.mapService = mapService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView entrance() {
        LOG.info("Service entrance time = " + UserDAO.getTime());
        return new ModelAndView(VIEW_JSP_NAME);
    }

    @RequestMapping(value = "/geolocation", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BoundingBox geolocation(@RequestBody Map<String, String> coordinates) throws Exception {
        LOG.info("Geolocation with arguments " + coordinates);
        if (coordinates.get("lat") != null && coordinates.get("lng") != null) {
            double lat = Double.parseDouble(coordinates.get("lat"));
            double lng = Double.parseDouble(coordinates.get("lng"));
            UserDAO.insert(lat, lng);
            return mapService.getCityBoundingBox(lat, lng);
        }
        if (coordinates.get("city") != null) {
            String tmpCountry = coordinates.get("country") != null ? coordinates.get("country") : "";
            return mapService.getCityBoundingBox(coordinates.get("city"), tmpCountry);
        }
        throw new IllegalArgumentException("Enter coordinates or city");
    }


    @RequestMapping(value = "/segmentation", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response segmentation(@RequestBody Map<String, String> data) throws Exception {
        LOG.info("Getting segments/clusters for category " + data.get("category") + " with algorithm " + data.get("algorithm") + " with source " + data.get("source"));
        double lat = Double.parseDouble(data.get("lat"));
        double lng = Double.parseDouble(data.get("lng"));
        String city = data.get("city");
        String country = data.get("country");
        String source = data.get("source");
        int segmentMinRadius = Integer.parseInt(data.get("segmentMinRadius"));
        int segmentMaxRadius = Integer.parseInt(data.get("segmentMaxRadius"));
        int segmentsCountPercent = Integer.parseInt(data.get("segmentsCountPercent"));
        List<Integer> percents = mapService.percentsHandler(data.get("percents"));
        String categoryStr = data.get("category");
        UserDAO.insert(lat, lng, city, country, source, categoryStr, percents, segmentMinRadius, segmentMaxRadius, segmentsCountPercent);
        Category category = makeCategory(data);
        List<Cluster> clusters = Segmentation.clustering(Algorithm.valueOf(data.get("algorithm")), places(data), segmentMinRadius, segmentMaxRadius, segmentsCountPercent);
        Collection<BoundingBox> boundingBoxes = category.getBoundingBoxes();
        Collection<Place> places =  category.getPlaces(percents);
//
//        for (FoursquarePlaceType foursquarePlaceType : Culture.FOURSQUARE_PLACE_TYPES) {
//            String placeType = foursquarePlaceType.name();
//            BoundingBox boundingBox = boundingBoxes.iterator().next();
//            if (!mapService.getHandlingMineOperations().containsKey(DataDAO.getCollectionName(boundingBox, placeType, source))) {
//                mapService.getHandlingMineOperations().put(DataDAO.getCollectionName(boundingBox, placeType, source), true);
//                DataDAO dao = new DataDAO(boundingBox, placeType, source);
//                dao.minePlacesIfNeed(mapService);
//                mapService.getHandlingMineOperations().remove(DataDAO.getCollectionName(boundingBox, placeType, source));
//                places.addAll(dao.getTopRatingPlaces(100));
//            }
//        }
//        places = places.stream()
//                .sorted((p1, p2) -> Double.compare(p2.getRating(), p1.getRating()))
//                .limit(50)
//                .collect(Collectors.toList());

        /////////////////////////////////

//        List<Cluster> clusters = new ArrayList<>(); // Segmentation.clustering(Algorithm.valueOf(data.get("algorithm")), places(data), segmentMinRadius, segmentMaxRadius, segmentsCountPercent);
//        BoundingBox mapServiceCityBoundingBox = mapService.getCityBoundingBox(data.get("city"), data.get("country"));
//        DataMiner miner = new DataMiner(mapService, data.get("source"), FoursquarePlaceType.MUSEUM.name());
//        miner.quadtreePlaceSearcher(mapServiceCityBoundingBox);
//        Collection<BoundingBox> boundingBoxes = miner.getBoundingBoxes();
//        Set<Place> places = miner.getPlaces();
//        clusters.addAll(miner.getSearchers().stream()
//                .map(marker -> new Cluster(marker.getLat(), marker.getLng(), marker.getRad(), Properties.getIconSearch(), new ArrayList<>()))
//                .collect(Collectors.toList()));

        //////////////////////////////////////////////////////

//        BoundingBox mapServiceCityBoundingBox = mapService.getCityBoundingBox(data.get("city"), data.get("country"));
//        DataMiner miner = new DataMiner(mapService, data.get("source"), FoursquarePlaceType.MUSEUM.name());
//        miner.searchersUniformGeodesicDistribution(mapServiceCityBoundingBox);
//        Collection<BoundingBox> boundingBoxes = miner.getBoundingBoxes();
//
//        List<Cluster> clusters = new ArrayList<>(); // Segmentation.clustering(Algorithm.valueOf(data.get("algorithm")), places(data), segmentMinRadius, segmentMaxRadius, segmentsCountPercent);
//        for (BoundingBox boundingbox : boundingBoxes) {
//            LatLng center = GeoMath.boundsCenter(boundingbox.getBounds());
//            int rad = (int) Math.ceil(GeoMath.halfDiagonal(boundingbox.getBounds()));
//            if (rad > Properties.getNeighborSearchersDistance() * 1.5) {
//                continue;
//            }
//            clusters.add(new Cluster(center.lat, center.lng, rad, Properties.getIconSearch(), new ArrayList<>()));
//        }
//
//        miner.miner(clusters);
//        Collection<Place> places = miner.getPlaces();
//        Collection<Place> places = new ArrayList<>();

        return new Response(clusters, boundingBoxes).withTopPlaces(places);
    }

    @RequestMapping(value = "/boundingboxes", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Collection<BoundingBox> boundingboxes(@RequestBody Map<String, String> data) throws Exception {
        LOG.info("Getting boundingboxes for category --- " + data.get("category") + " with source " + data.get("source"));
        return makeCategory(data).getBoundingBoxes();
    }

    @RequestMapping(value = "/places", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Collection<Place> places(@RequestBody Map<String, String> data) throws Exception {
        LOG.info("Getting places for category --- " + data.get("category") + " with source " + data.get("source"));
        return makeCategory(data).getPlaces(mapService.percentsHandler(data.get("percents")));
    }

    private Category makeCategory(Map<String, String> data) throws Exception {
        List<Integer> percents = mapService.percentsHandler(data.get("percents"));
        BoundingBox mapServiceCityBoundingBox = mapService.getCityBoundingBox(data.get("city"), data.get("country"));
        String city = mapServiceCityBoundingBox.getCity();
        String country = mapServiceCityBoundingBox.getCountry();
        String categoryStr = data.get("category");
        String source = data.get("source");
        LOG.info("Segmentation in city = " + city + ", country = " + country + ", category = " + categoryStr +
                ", percents = " + percents + ", source = " + source);
        BoundingBox boundingBox = mapService.getCityBoundingBox(city, country);
        return Category.getCategory(mapService, boundingBox, categoryStr, source);
    }

//    @RequestMapping(value = "/debug", method = RequestMethod.GET)
//    public ModelAndView debug() {
//
//            @RequestParam
//                                            Double lat,                     // user latitude geolocation
//                                    @RequestParam(value = "lng", required = false)
//                                            Double lng,                     // user longitude geolocation
//                                    @RequestParam(value = "city", required = false)
//                                            String city,                    // user city geolocation
//                                    @RequestParam(value = "country", required = false)
//                                            String country,                 // user country geolocation
//                                    @RequestParam(value = "gData", defaultValue = "false", required = false)
//                                            boolean isGoogleData,           // use data from Google Maps
//                                    @RequestParam(value = "fData", defaultValue = "false", required = false)
//                                            boolean isFoursquareData,       // use data from Foursquare
//                                    @RequestParam(value = "pType", required = false)
//                                            String placeType,               // placeType depends from source
//                                    @RequestParam(value = "category", required = false)
//                                            String category,                // places category
//                                    @RequestParam(value = "percents", required = false)
//                                            String percentsStr,         // percents list
//                                    @RequestParam(value = "clusterAlg", required = false)
//                                            String clusterAlgorithm,        // machine learning algorithm
//                                    @RequestParam(value = "box", defaultValue = "false", required = false)
//                                            boolean isBox,                  // show boundingboxes around searchers
//                                    @RequestParam(value = "searchers", defaultValue = "false", required = false)
//                                            boolean isSearchers,            // show searchers
//                                    @RequestParam(value = "srcIcons", defaultValue = "false", required = false)
//                                            boolean isSourceIcons) {        // use only sources icons (not places icons)

//        final ModelAndView view = new ModelAndView(VIEW_JSP_NAME);

//        final List<Integer> percents = percentsStr != null && percentsStr.startsWith("[") && percentsStr.endsWith("]")
//                ? Arrays.stream(percentsStr.substring(1, percentsStr.length() - 1).split(","))
//                .map(Integer::parseInt)
//                .collect(Collectors.toList())
//                : new ArrayList<>();
//
//        UserDAO.insert(lat, lng, city, country, isGoogleData, isFoursquareData, placeType, category, clusterAlgorithm,
//                isBox, isSearchers, isSourceIcons, percents);
//
//        if (lat != null && lng != null) {
//            LOG.info("User coordinates lat = " + lat + ", lng = " + lng);
//            view.addObject(VIEW_USER, new Marker(lat, lng, 0, Properties.getIconUser()));
//            BoundingBox boundingBox = mapService.getCityBoundingBox(lat, lng);
//            if (boundingBox == null) {
//                view.addObject(VIEW_ERROR, "Please, check your coordinates. If all are correct then our developer " +
//                        "know about trouble and fix it as soon as possible.");
//            } else {
//                view.addObject(VIEW_USER_CITY, boundingBox.getCity());
//                view.addObject(VIEW_USER_COUNTRY, boundingBox.getCountry());
//                buildModelAndView(view, boundingBox, isGoogleData, isFoursquareData, category, placeType,
//                        clusterAlgorithm, isSearchers, isBox, isSourceIcons, percents);
//            }
//            return view;
//        }
//
//        if (city != null) {
//            LOG.info("User city = " + city + ", country = " + country + ". Trying correct it...");
//            String correctCity = mapService.getCorrectCity(city, country);
//            String correctCountry = mapService.getCorrectCountry(city, country);
//            LOG.info("After correcting city = " + correctCity + ", country = " + correctCountry);
//            if (correctCity == null || correctCountry == null) {
//                view.addObject(VIEW_ERROR, "Please, input correct city or add country info.");
//            } else {
//                BoundingBox boundingBox = mapService.getCityBoundingBox(correctCity, correctCountry);
//                if (boundingBox == null) {
//                    view.addObject(VIEW_ERROR, "Please, check your city/country input. If all are correct then our " +
//                            "developer know about trouble and fix it as soon as possible.");
//                } else {
//                    LatLng center = GeoMath.boundsCenter(boundingBox.getBounds());
//                    view.addObject(VIEW_USER, new Marker(center.lat, center.lng, 0, Properties.getIconUser()));
//                    view.addObject(VIEW_USER_CITY, boundingBox.getCity());
//                    view.addObject(VIEW_USER_COUNTRY, boundingBox.getCountry());
//                    buildModelAndView(view, boundingBox, isGoogleData, isFoursquareData, category, placeType,
//                            clusterAlgorithm, isSearchers, isBox, isSourceIcons, percents);
//                }
//            }
//            return view;
//        }
//
//        LOG.info("No input data for map initialization");
//        return view;
//    }
//
//    private void buildModelAndView(ModelAndView view,
//                                   BoundingBox boundingBox,
//                                   boolean isGoogleSource,
//                                   boolean isFoursquareSource,
//                                   @Nullable String category,
//                                   @Nullable String placeType,
//                                   @Nullable String clusterAlgorithm,
//                                   boolean isSearchers,
//                                   boolean isBox,
//                                   boolean isSourceIcons,
//                                   @Nonnull List<Integer> percents) {
//
//        if (!view.getModel().containsKey(VIEW_USER)) {
//            LatLng center = GeoMath.boundsCenter(boundingBox.getBounds());
//            view.addObject(VIEW_USER, new Marker(center.lat, center.lng, 0, Properties.getIconUser()));
//        }
//
//        if (category != null) {
//            Category categoryObj = null;
//            if (category.toLowerCase().equals("culture")) {
//                categoryObj = new Culture(mapService, boundingBox);
//            }
//
//            if (categoryObj == null) {
//                return;
//            }
//
//            Set<GooglePlace> googlePlaces = isGoogleSource
//                    ? categoryObj.getGooglePlaces(percents) : new HashSet<>();
//            Set<FoursquarePlace> foursquarePlaces = isFoursquareSource
//                    ? categoryObj.getFoursquarePlaces(percents) : new HashSet<>();
//            Set<Marker> places = new HashSet<>();
//            places.addAll(googlePlaces);
//            places.addAll(foursquarePlaces);
//            view.addObject(VIEW_GOOGLE_PLACES, isSourceIcons
//                    ? GooglePlace.useSourceIcon(googlePlaces) : googlePlaces);
//            view.addObject(VIEW_FOURSQUARE_PLACES, isSourceIcons
//                    ? FoursquarePlace.useSourceIcon(foursquarePlaces) : foursquarePlaces);
//            view.addObject(VIEW_CLUSTERS, Segmentation.getClustersByString(clusterAlgorithm, places));
//
//            if (isBox) {
//                List<BoundingBox> foursuqareBoundingBoxes = isFoursquareSource
//                        ? categoryObj.getFoursquareBoundingBoxes() : new ArrayList<>();
//                List<BoundingBox> googleBoundingBoxes = isGoogleSource
//                        ? categoryObj.getGoogleBoundingBoxes() : new ArrayList<>();
//                foursuqareBoundingBoxes.addAll(googleBoundingBoxes);
//                view.addObject(VIEW_BOUNDING_BOXES, foursuqareBoundingBoxes);
//            }
//
//            return;
//        }
//
//        if (placeType != null) {
//
//            FoursquarePlaceType foursquarePlaceType = getEnumFromString(FoursquarePlaceType.class, placeType);
//            if (isFoursquareSource && foursquarePlaceType != null) {
//                FoursquareDataDAO foursquareDataDAO = new FoursquareDataDAO(foursquarePlaceType.name(), boundingBox.getCity(), boundingBox.getCountry());
//                foursquareDataDAO.minePlacesIfNeed(mapService, boundingBox);
//                Set<FoursquarePlace> filteredPlaces = FoursquarePlace.filterTopCheckins(
//                        foursquareDataDAO.getPlaces(), percents.size() > 0 ? percents.get(0) : 100);
//
//                view.addObject(VIEW_FOURSQUARE_PLACES, isSourceIcons
//                        ? FoursquarePlace.useSourceIcon(filteredPlaces)
//                        : filteredPlaces);
//                view.addObject(VIEW_BOUNDING_BOXES, isBox
//                        ? foursquareDataDAO.getBoundingBoxes() : new ArrayList<BoundingBox>());
//                view.addObject(VIEW_MARKERS, isSearchers
//                        ? foursquareDataDAO.getSearchers() : new ArrayList<Marker>());
//                view.addObject(VIEW_CLUSTERS, Segmentation.getClustersByString(clusterAlgorithm,
//                        FoursquarePlace.toMarkers(filteredPlaces)));
//            }
//
//            GooglePlaceType googlePlaceType = getEnumFromString(GooglePlaceType.class, placeType);
//            if (isGoogleSource && googlePlaceType != null) {
//                GoogleDataDAO googleDataDAO = new GoogleDataDAO(googlePlaceType.name(), boundingBox.getCity(), boundingBox.getCountry());
//                googleDataDAO.minePlacesIfNeed(mapService, boundingBox);
//                Set<GooglePlace> filteredPlaces = GooglePlace.filterPlaces(
//                        googleDataDAO.getPlaces(), percents.size() > 0 ? percents.get(0) : 100);
//
//                view.addObject(VIEW_GOOGLE_PLACES, isSourceIcons
//                        ? GooglePlace.useSourceIcon(filteredPlaces)
//                        : filteredPlaces);
//                view.addObject(VIEW_BOUNDING_BOXES, isBox
//                        ? googleDataDAO.getBoundingBoxes() : new ArrayList<BoundingBox>());
//                view.addObject(VIEW_MARKERS, isSearchers
//                        ? googleDataDAO.getSearchers() : new ArrayList<Marker>());
//                view.addObject(VIEW_CLUSTERS, Segmentation.getClustersByString(clusterAlgorithm,
//                        GooglePlace.toMarkers(filteredPlaces)));
//            }
//
//            return;
//        }
//
//        GoogleDataMiner googleDataMiner = new GoogleDataMiner(mapService, null);
//        googleDataMiner.searchersUniformGeodesicDistribution(boundingBox);
//        view.addObject(VIEW_BOUNDING_BOXES, isBox
//                ? googleDataMiner.getBoundingBoxes() : new ArrayList<BoundingBox>());
//        view.addObject(VIEW_MARKERS, isSearchers
//                ? googleDataMiner.getMarkers() : new ArrayList<Marker>());
//    }
//
//    public static <T extends Enum<T>> T getEnumFromString(Class<T> clazz, String type) {
//        if (clazz == null || type == null) {
//            return null;
//        }
//        try {
//            return Enum.valueOf(clazz, type.trim().toUpperCase());
//        } catch (IllegalArgumentException ignored) {
//            return null;
//        }
//    }

}
