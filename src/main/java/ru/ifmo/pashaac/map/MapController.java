package ru.ifmo.pashaac.map;

import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.ifmo.pashaac.common.GeoMath;
import ru.ifmo.pashaac.common.Properties;
import ru.ifmo.pashaac.common.UserDAO;
import ru.ifmo.pashaac.common.wrapper.BoundingBox;
import ru.ifmo.pashaac.common.wrapper.Searcher;
import ru.ifmo.pashaac.foursquare.FoursquareDataDAO;
import ru.ifmo.pashaac.foursquare.FoursquareDataMiner;
import ru.ifmo.pashaac.foursquare.FoursquarePlace;
import ru.ifmo.pashaac.foursquare.FoursquarePlaceType;
import ru.ifmo.pashaac.google.maps.GoogleDataDAO;
import ru.ifmo.pashaac.google.maps.GoogleDataMiner;
import ru.ifmo.pashaac.google.maps.GooglePlace;

import javax.annotation.Nullable;

/**
 * Created by Pavel Asadchiy
 * 18.04.16 22:17.
 */
@Controller
@RequestMapping
public class MapController {

    public static final String VIEW_JSP_NAME = "map";
    public static final String VIEW_KEY = "key";
    public static final String VIEW_ERROR = "error";
    public static final String VIEW_USER = "user";
    public static final String VIEW_BOUNDING_BOXES = "boxes";
    public static final String VIEW_SEARCHERS = "searchers";
    public static final String VIEW_GOOGLE_PLACES = "google_places";
    public static final String VIEW_FOURSQUARE_PLACES = "foursquare_places";

    private static final Logger LOG = Logger.getLogger(MapController.class);

    @Autowired
    private final MapService mapService;

    public MapController(MapService mapService) {
        this.mapService = mapService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView boundingbox(@RequestParam(value = "lat", required = false) Double lat,          // user geolocation
                                    @RequestParam(value = "lng", required = false) Double lng,          // user geolocation
                                    @RequestParam(value = "city", required = false) String city,        // user geolocation
                                    @RequestParam(value = "country", required = false) String country,  // user geolocation
                                    @RequestParam(value = "place", required = false) String placeType,  // place type or category in future
                                    @RequestParam(value = "box", required = false) Boolean isBox,       // flag - show boundingboxes around searchers
                                    @RequestParam(value = "cover", required = false) Boolean isCover,   // flag - search placeType data or not
                                    @RequestParam(value = "clear", required = false) Boolean isClear) { // flag - do clearing (delete long away places), etc

        final ModelAndView view = new ModelAndView(VIEW_JSP_NAME);

        if (lat != null && lng != null) {
            LOG.info("User coordinates lat = " + lat + ", lng = " + lng);
            view.addObject(VIEW_USER, new Searcher(lat, lng, 0, Properties.getIconUser()));
            UserDAO.insert(lat, lng);
            BoundingBox boundingBox = mapService.getCityBoundingBox(lat, lng);
            if (boundingBox == null) {
                view.addObject(VIEW_ERROR, "Please, check your coordinates. If all are correct then our developer " +
                        "know about trouble and fix it as soon as possible.");
            } else {
                buildModelAndView(view, boundingBox, placeType, isCover, isBox, isClear);
            }
            return view;
        }

        if (city != null) {
            LOG.info("User city = " + city + ", country = " + country + ". Trying correct it...");
            String correctCity = mapService.getCorrectCity(city, country);
            String correctCountry = mapService.getCorrectCountry(city, country);
            LOG.info("After correcting city = " + correctCity + ", country = " + correctCountry);
            if (correctCity == null || correctCountry == null) {
                view.addObject(VIEW_ERROR, "Please, input correct city or add country info.");
            } else {
                BoundingBox boundingBox = mapService.getCityBoundingBox(correctCity, correctCountry);
                if (boundingBox == null) {
                    view.addObject(VIEW_ERROR, "Please, check your city/country input. If all are correct then our " +
                            "developer know about trouble and fix it as soon as possible.");
                } else {
                    LatLng center = GeoMath.boundsCenter(boundingBox.getBounds());
                    view.addObject(VIEW_USER, new Searcher(center.lat, center.lng, 0, Properties.getIconUser()));
                    buildModelAndView(view, boundingBox, placeType, isCover, isBox, isClear);
                }
            }
            return view;
        }

        LOG.info("No input data for map initialization, trying to get user geolocation from browser...");
        return view;
    }

    private void buildModelAndView(ModelAndView view,
                                   BoundingBox boundingBox,
                                   @Nullable String placeTypeStr,
                                   @Nullable Boolean isCover,
                                   @Nullable Boolean isBox,
                                   @Nullable Boolean isClear) {

        if (!view.getModel().containsKey(VIEW_USER)) {
            LatLng center = GeoMath.boundsCenter(boundingBox.getBounds());
            view.addObject(VIEW_USER, new Searcher(center.lat, center.lng, 0, Properties.getIconUser()));
        }
        view.addObject(VIEW_KEY, System.getenv("GOOGLE_API_KEY"));

        if (placeTypeStr == null) {
            new GoogleDataMiner(mapService).searchersUniformGeodesicDistribution(boundingBox, view);
        } else {
//            List<FoursquarePlaceType> placeTypes;
//            if (placeTypeStr.startsWith("[") && placeTypeStr.endsWith("]")) {
//                String placeTypesStr = placeTypeStr.substring(1, placeTypeStr.length() - 1);
//                placeTypes = Arrays.stream(placeTypesStr.split(","))
//                        .map(s -> FoursquarePlaceType.valueOf(s.trim().toUpperCase()))
//                        .collect(Collectors.toList());
//            } else {
//                placeTypes = new ArrayList<>();
//                placeTypes.add(FoursquarePlaceType.valueOf(placeTypeStr.toUpperCase()));
//            }
//            List<BoundingBox> boundingBoxes = new ArrayList<>();
//            List<Searcher> searchers = new ArrayList<>();
//            List<Place> places = new ArrayList<>();
//
//            placeTypes.stream()
//                    .forEach(placeType -> {
//                        if (!PlaceDAO.contains(language, boundingBox, placeType)) {
//                            algorithms.dynamicQuadTreeGeodesicMarkersDistribution(boundingBox, placeType, language);
//                            PlaceDAO.updatePlacesInfo(language, boundingBox, placeType, mapService.getGoogleContext());
//                        }
//                        boundingBoxes.addAll(AdditionalDAO.getBoundingboxes(boundingBox, placeType));
//                        searchers.addAll(AdditionalDAO.getSearchers(boundingBox, placeType));
//                        places.addAll(PlaceDAO.getPlaces(language, boundingBox, placeType));
//                    });


            FoursquareDataMiner foursquareDataMiner = new FoursquareDataMiner(mapService);
            FoursquareDataDAO foursquareDataDAO = new FoursquareDataDAO(FoursquarePlaceType.MUSEUM.name(), boundingBox.getCity(), boundingBox.getCountry());
            if (!foursquareDataDAO.exist()) {
                foursquareDataMiner.quadtreePlaceSearcher(boundingBox, FoursquarePlaceType.MUSEUM);
                foursquareDataDAO.insert(foursquareDataMiner.getPlaces());
                foursquareDataDAO.recreate(foursquareDataMiner.getBoundingBoxes(), FoursquareDataDAO.BOUNDINGBOX_SUFFIX);
                foursquareDataDAO.recreate(foursquareDataMiner.getSearchers(), FoursquareDataDAO.SEARCHER_SUFFIX);
            }

            if (isClear != null && isClear) {
                view.addObject(VIEW_FOURSQUARE_PLACES, FoursquarePlace.cleaner(foursquareDataDAO.getPlaces()));
            } else {
                view.addObject(VIEW_FOURSQUARE_PLACES, foursquareDataDAO.getPlaces());
            }

            GoogleDataMiner googleDataMiner = new GoogleDataMiner(mapService);
            GoogleDataDAO googleDataDAO = new GoogleDataDAO(PlaceType.MUSEUM.name(), boundingBox.getCity(), boundingBox.getCountry());
            if (!googleDataDAO.exist()) {
                googleDataMiner.quadtreePlaceSearcher(boundingBox, PlaceType.MUSEUM);
                googleDataMiner.fullPlacesInformation("ru");
                googleDataDAO.insert(googleDataMiner.getPlaces());
                googleDataDAO.recreate(googleDataMiner.getBoundingBoxes(), GoogleDataDAO.BOUNDINGBOX_SUFFIX);
                googleDataDAO.recreate(googleDataMiner.getSearchers(), GoogleDataDAO.SEARCHER_SUFFIX);
            }

            if (isClear != null && isClear) {
                view.addObject(VIEW_GOOGLE_PLACES, GooglePlace.cleaner(googleDataDAO.getPlaces()));
            } else {
                view.addObject(VIEW_GOOGLE_PLACES, googleDataDAO.getPlaces());
            }

        }
        if (isBox == null || !isBox) {
            view.getModel().remove(VIEW_BOUNDING_BOXES);
        }
        if (isCover == null || !isCover) {
            view.getModel().remove(VIEW_SEARCHERS);
        }

    }


}
