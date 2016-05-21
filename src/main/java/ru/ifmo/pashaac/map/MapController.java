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
import ru.ifmo.pashaac.common.GeoMath;
import ru.ifmo.pashaac.common.Properties;
import ru.ifmo.pashaac.common.UserDAO;
import ru.ifmo.pashaac.common.primitives.BoundingBox;
import ru.ifmo.pashaac.common.primitives.Marker;
import ru.ifmo.pashaac.foursquare.FoursquareDataDAO;
import ru.ifmo.pashaac.foursquare.FoursquarePlace;
import ru.ifmo.pashaac.foursquare.FoursquarePlaceType;
import ru.ifmo.pashaac.google.maps.GoogleDataDAO;
import ru.ifmo.pashaac.google.maps.GoogleDataMiner;
import ru.ifmo.pashaac.google.maps.GooglePlace;
import ru.ifmo.pashaac.google.maps.GooglePlaceType;
import ru.ifmo.pashaac.segmentation.Segmentation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
    public static final String VIEW_MARKERS = "markers";
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
    public ModelAndView boundingbox(@RequestParam(value = "lat", required = false)
                                            Double lat,                     // user latitude geolocation
                                    @RequestParam(value = "lng", required = false)
                                            Double lng,                     // user longitude geolocation
                                    @RequestParam(value = "city", required = false)
                                            String city,                    // user city geolocation
                                    @RequestParam(value = "country", required = false)
                                            String country,                 // user country geolocation
                                    @RequestParam(value = "gData", defaultValue = "false", required = false)
                                            boolean isGoogleData,           // use data from Google Maps
                                    @RequestParam(value = "fData", defaultValue = "false", required = false)
                                            boolean isFoursquareData,       // use data from Foursquare
                                    @RequestParam(value = "pType", required = false)
                                            String placeType,               // placeType depends from source
                                    @RequestParam(value = "category", required = false)
                                            String category,                // places category
                                    @RequestParam(value = "clusterAlg", required = false)
                                            String clusterAlgorithm,        // machine learning algorithm
                                    @RequestParam(value = "box", defaultValue = "false", required = false)
                                            boolean isBox,                  // show boundingboxes around searchers
                                    @RequestParam(value = "searchers", defaultValue = "false", required = false)
                                            boolean isSearchers,            // show searchers
                                    @RequestParam(value = "srcIcons", defaultValue = "false", required = false)
                                            boolean isSourceIcons,          // use only sources icons (not places icons)
                                    @RequestParam(value = "all", defaultValue = "false", required = false)
                                            boolean isAllPlaces) {            // show all places or filtered

        final ModelAndView view = new ModelAndView(VIEW_JSP_NAME);
        UserDAO.insert(lat, lng, city, country, isGoogleData, isFoursquareData, placeType, category, clusterAlgorithm,
                isBox, isSearchers, isSourceIcons, isAllPlaces);
        if (lat != null && lng != null) {
            LOG.info("User coordinates lat = " + lat + ", lng = " + lng);
            view.addObject(VIEW_USER, new Marker(lat, lng, 0, Properties.getIconUser()));
            BoundingBox boundingBox = mapService.getCityBoundingBox(lat, lng);
            if (boundingBox == null) {
                view.addObject(VIEW_ERROR, "Please, check your coordinates. If all are correct then our developer " +
                        "know about trouble and fix it as soon as possible.");
            } else {
                buildModelAndView(view, boundingBox, isGoogleData, isFoursquareData, category, placeType,
                        clusterAlgorithm, isSearchers, isBox, isSourceIcons, isAllPlaces);
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
                    view.addObject(VIEW_USER, new Marker(center.lat, center.lng, 0, Properties.getIconUser()));
                    buildModelAndView(view, boundingBox, isGoogleData, isFoursquareData, category, placeType,
                            clusterAlgorithm, isSearchers, isBox, isSourceIcons, isAllPlaces);
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
                                   @Nullable String clusterAlgorithm,
                                   boolean isSearchers,
                                   boolean isBox,
                                   boolean isSourceIcons,
                                   boolean isAllPlaces) {

        if (!view.getModel().containsKey(VIEW_USER)) {
            LatLng center = GeoMath.boundsCenter(boundingBox.getBounds());
            view.addObject(VIEW_USER, new Marker(center.lat, center.lng, 0, Properties.getIconUser()));
        }
        view.addObject(VIEW_KEY, System.getenv("GOOGLE_API_KEY"));

        if (category != null) {
            Category categoryObj = null;
            if (category.toLowerCase().equals("culture")) {
                categoryObj = new Culture(mapService, boundingBox);
            }

            if (categoryObj != null) {
                Set<GooglePlace> googlePlaces = isGoogleSource
                        ? categoryObj.getGooglePlaces(isAllPlaces) : new HashSet<>();
                Set<FoursquarePlace> foursquarePlaces = isFoursquareSource
                        ? categoryObj.getFoursquarePlaces(isAllPlaces) : new HashSet<>();
                Set<Marker> places = new HashSet<>();
                places.addAll(googlePlaces);
                places.addAll(foursquarePlaces);
                view.addObject(VIEW_GOOGLE_PLACES, isSourceIcons
                        ? GooglePlace.useSourceIcon(googlePlaces) : googlePlaces);
                view.addObject(VIEW_FOURSQUARE_PLACES, isSourceIcons
                        ? FoursquarePlace.useSourceIcon(foursquarePlaces) : foursquarePlaces);
                view.addObject(VIEW_KERNELS, Segmentation.getClustersByString(clusterAlgorithm, places));
            }

            if (isBox) {
                view.addObject(VIEW_BOUNDING_BOXES, Collections.singletonList(boundingBox));
            }

            return;
        }

        if (placeType != null) {

            FoursquarePlaceType foursquarePlaceType = getEnumFromString(FoursquarePlaceType.class, placeType);
            if (isFoursquareSource && foursquarePlaceType != null) {
                FoursquareDataDAO foursquareDataDAO = new FoursquareDataDAO(foursquarePlaceType.name(), boundingBox.getCity(), boundingBox.getCountry());
                foursquareDataDAO.minePlacesIfNotExist(mapService, boundingBox);

                view.addObject(VIEW_FOURSQUARE_PLACES, isAllPlaces
                        ? (isSourceIcons ? FoursquarePlace.useSourceIcon(foursquareDataDAO.getPlaces()) : foursquareDataDAO.getPlaces())
                        : (isSourceIcons ? FoursquarePlace.useSourceIcon(foursquareDataDAO.getFilteredPlaces()) : foursquareDataDAO.getFilteredPlaces()));
                view.addObject(VIEW_BOUNDING_BOXES, isBox
                        ? foursquareDataDAO.getBoundingBoxes() : new ArrayList<BoundingBox>());
                view.addObject(VIEW_MARKERS, isSearchers
                        ? foursquareDataDAO.getSearchers() : new ArrayList<Marker>());
                view.addObject(VIEW_KERNELS, Segmentation.getClustersByString(clusterAlgorithm,
                        FoursquarePlace.toMarkers(isAllPlaces ? foursquareDataDAO.getPlaces() : foursquareDataDAO.getFilteredPlaces())));
            }

            GooglePlaceType googlePlaceType = getEnumFromString(GooglePlaceType.class, placeType);
            if (isGoogleSource && googlePlaceType != null) {
                GoogleDataDAO googleDataDAO = new GoogleDataDAO(googlePlaceType.name(), boundingBox.getCity(), boundingBox.getCountry());
                googleDataDAO.minePlacesIfNotExist(mapService, boundingBox);

                view.addObject(VIEW_GOOGLE_PLACES, isAllPlaces
                        ? (isSourceIcons ? GooglePlace.useSourceIcon(googleDataDAO.getPlaces()) : googleDataDAO.getPlaces())
                        : (isSourceIcons ? GooglePlace.useSourceIcon(googleDataDAO.getFilteredPlaces()) : googleDataDAO.getFilteredPlaces()));
                view.addObject(VIEW_BOUNDING_BOXES, isBox
                        ? googleDataDAO.getBoundingBoxes() : new ArrayList<BoundingBox>());
                view.addObject(VIEW_MARKERS, isSearchers
                        ? googleDataDAO.getSearchers() : new ArrayList<Marker>());
                view.addObject(VIEW_KERNELS, Segmentation.getClustersByString(clusterAlgorithm,
                        GooglePlace.toMarkers(isAllPlaces ? googleDataDAO.getPlaces() : googleDataDAO.getFilteredPlaces())));
            }

            return;
        }

        GoogleDataMiner googleDataMiner = new GoogleDataMiner(mapService, null);
        googleDataMiner.searchersUniformGeodesicDistribution(boundingBox);
        view.addObject(VIEW_BOUNDING_BOXES, isBox
                ? googleDataMiner.getBoundingBoxes() : new ArrayList<BoundingBox>());
        view.addObject(VIEW_MARKERS, isSearchers
                ? googleDataMiner.getMarkers() : new ArrayList<Marker>());
    }

    public static <T extends Enum<T>> T getEnumFromString(Class<T> c, String s) {
        if (c == null || s == null) {
            return null;
        }
        try {
            return Enum.valueOf(c, s.trim().toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

}
