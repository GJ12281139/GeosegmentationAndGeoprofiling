package ru.ifmo.pashaac.map;

import com.google.maps.model.LatLng;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.ifmo.pashaac.category.Category;
import ru.ifmo.pashaac.category.Culture;
import ru.ifmo.pashaac.common.*;
import ru.ifmo.pashaac.foursquare.FoursquareDataDAO;
import ru.ifmo.pashaac.foursquare.FoursquareDataMiner;
import ru.ifmo.pashaac.foursquare.FoursquarePlace;
import ru.ifmo.pashaac.foursquare.FoursquarePlaceType;
import ru.ifmo.pashaac.google.maps.GoogleDataDAO;
import ru.ifmo.pashaac.google.maps.GoogleDataMiner;
import ru.ifmo.pashaac.google.maps.GooglePlace;
import ru.ifmo.pashaac.google.maps.GooglePlaceType;

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
    public static final String VIEW_KERNELS = "kernels";

    private static final Logger LOG = Logger.getLogger(MapController.class);

    @Autowired
    private final MapService mapService;

    public MapController(MapService mapService) {
        this.mapService = mapService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView boundingbox(@RequestParam(value = "lat", required = false) Double lat,              // user geolocation
                                    @RequestParam(value = "lng", required = false) Double lng,              // user geolocation
                                    @RequestParam(value = "city", required = false) String city,            // user geolocation
                                    @RequestParam(value = "country", required = false) String country,      // user geolocation
                                    @RequestParam(value = "googleSource", defaultValue = "false", required = false) boolean isGoogleSource,        // use data from Google Maps
                                    @RequestParam(value = "foursquareSource", defaultValue = "false", required = false) boolean isFoursquareSource,    // use data from Foursquare
                                    @RequestParam(value = "placeType", required = false) String placeType,  // placeType depends from source
                                    @RequestParam(value = "category", required = false) String category,    // places category
                                    @RequestParam(value = "box", defaultValue = "false", required = false) boolean isBox,       // flag - show boundingboxes around searchers
                                    @RequestParam(value = "searchers", defaultValue = "false", required = false) boolean isSearchers,   // flag - search placeType data or not
                                    @RequestParam(value = "clear", defaultValue = "true", required = false) boolean isClear) {  // flag - do clearing (delete long away places), etc

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
                buildModelAndView(view, boundingBox, isGoogleSource, isFoursquareSource, category, placeType, isSearchers, isBox, isClear);
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
                    buildModelAndView(view, boundingBox, isGoogleSource, isFoursquareSource, category, placeType, isSearchers, isBox, isClear);
                }
            }
            return view;
        }

        LOG.info("No input data for map initialization, trying to get user geolocation from browser...");
        return view;
    }

    private void buildModelAndView(ModelAndView view,
                                   BoundingBox boundingBox,
                                   boolean isGoogleSource,
                                   boolean isFoursquareSource,
                                   @Nullable String category,
                                   @Nullable String placeType,
                                   boolean isSearchers,
                                   boolean isBox,
                                   boolean isClear) {

        if (!view.getModel().containsKey(VIEW_USER)) {
            LatLng center = GeoMath.boundsCenter(boundingBox.getBounds());
            view.addObject(VIEW_USER, new Searcher(center.lat, center.lng, 0, Properties.getIconUser()));
        }
        view.addObject(VIEW_KEY, System.getenv("GOOGLE_API_KEY"));

        if (category != null) {
            Category categoryObj = null;
            if (category.toLowerCase().equals("culture")) {
                categoryObj = new Culture(mapService, boundingBox);
            }

            if (categoryObj != null) {
                if (isFoursquareSource && isGoogleSource) {
                    view.addObject(VIEW_FOURSQUARE_PLACES, FoursquarePlace.cleaner(categoryObj.getFoursquarePlaces()));
                    view.addObject(VIEW_GOOGLE_PLACES, GooglePlace.cleaner(categoryObj.getGooglePlaces()));
                    view.addObject(VIEW_KERNELS, categoryObj.getKernels(true));
                } else if (isFoursquareSource) {
                    view.addObject(VIEW_FOURSQUARE_PLACES, FoursquarePlace.cleaner(categoryObj.getFoursquarePlaces()));
                    view.addObject(VIEW_KERNELS, categoryObj.getFoursquareKernels(true));
                } else if (isGoogleSource) {
                    view.addObject(VIEW_GOOGLE_PLACES, GooglePlace.cleaner(categoryObj.getGooglePlaces()));
                    view.addObject(VIEW_KERNELS, categoryObj.getGoogleKernels(true));
                }
            }
        } else if (placeType != null) {

            FoursquarePlaceType foursquarePlaceType = getEnumFromString(FoursquarePlaceType.class, placeType);
            if (isFoursquareSource && foursquarePlaceType != null) {
                FoursquareDataDAO foursquareDataDAO = new FoursquareDataDAO(foursquarePlaceType.name(), boundingBox.getCity(), boundingBox.getCountry());
                if (!foursquareDataDAO.exist()) {
                    FoursquareDataMiner foursquareDataMiner = new FoursquareDataMiner(mapService, foursquarePlaceType);
                    foursquareDataMiner.quadtreePlaceSearcher(boundingBox);

                    foursquareDataDAO.insert(foursquareDataMiner.getPlaces());
                    foursquareDataDAO.recreate(foursquareDataMiner.getBoundingBoxes(), FoursquareDataDAO.BOUNDINGBOX_SUFFIX);
                    foursquareDataDAO.recreate(foursquareDataMiner.getSearchers(), FoursquareDataDAO.SEARCHER_SUFFIX);
                }
                view.addObject(VIEW_FOURSQUARE_PLACES, foursquareDataDAO.getPlaces());
                view.addObject(VIEW_BOUNDING_BOXES, foursquareDataDAO.getBoundingBoxes());
                view.addObject(VIEW_SEARCHERS, foursquareDataDAO.getSearchers());
            }

            GooglePlaceType googlePlaceType = getEnumFromString(GooglePlaceType.class, placeType);
            if (isGoogleSource && googlePlaceType != null) {
                GoogleDataDAO googleDataDAO = new GoogleDataDAO(googlePlaceType.name(), boundingBox.getCity(), boundingBox.getCountry());
                if (!googleDataDAO.exist()) {
                    GoogleDataMiner googleDataMiner = new GoogleDataMiner(mapService, googlePlaceType);
                    googleDataMiner.quadtreePlaceSearcher(boundingBox);
                    googleDataMiner.fullPlacesInformation("ru");

                    googleDataDAO.insert(googleDataMiner.getPlaces());
                    googleDataDAO.recreate(googleDataMiner.getBoundingBoxes(), GoogleDataDAO.BOUNDINGBOX_SUFFIX);
                    googleDataDAO.recreate(googleDataMiner.getSearchers(), GoogleDataDAO.SEARCHER_SUFFIX);
                }
                view.addObject(VIEW_GOOGLE_PLACES, googleDataDAO.getPlaces());
                view.addObject(VIEW_BOUNDING_BOXES, googleDataDAO.getBoundingBoxes());
                view.addObject(VIEW_SEARCHERS, googleDataDAO.getSearchers());
            }
        } else {
            new GoogleDataMiner(mapService, null).searchersUniformGeodesicDistribution(boundingBox, view);
        }

        // flags handle
        if (!isBox) {
            view.getModel().remove(VIEW_BOUNDING_BOXES);
        }
        if (!isSearchers) {
            view.getModel().remove(VIEW_SEARCHERS);
        }

    }

    public static <T extends Enum<T>> T getEnumFromString(Class<T> c, String string) {
        if( c != null && string != null ) {
            try {
                return Enum.valueOf(c, string.trim().toUpperCase());
            } catch(IllegalArgumentException ignored) {
            }
        }
        return null;
    }

}
