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
import ru.ifmo.pashaac.common.wrapper.BoundingBox;
import ru.ifmo.pashaac.common.wrapper.Searcher;
import ru.ifmo.pashaac.coverage.CoverageAlgorithms;
import ru.ifmo.pashaac.mongo.AdditionalDAO;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

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
    public static final String VIEW_PLACES = "places";

    private static final Logger LOG = Logger.getLogger(BoundingBoxService.class);

    @Autowired
    private final BoundingBoxService service;

    @Autowired
    private final CoverageAlgorithms algorithms;

    public MapController(BoundingBoxService service, CoverageAlgorithms algorithms) {
        this.service = service;
        this.algorithms = algorithms;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView boundingbox(@RequestParam(value = "lat", required = false) Double lat,
                                    @RequestParam(value = "lng", required = false) Double lng,
                                    @RequestParam(value = "city", required = false) String city,
                                    @RequestParam(value = "country", required = false) String country,
                                    @RequestParam(value = "box", required = false) Boolean isBox,
                                    @RequestParam(value = "cover", required = false) Boolean isCover,
                                    @RequestParam(value = "placeType", required = false) String placeType) {
        final ModelAndView view = new ModelAndView(VIEW_JSP_NAME);
        if (lat != null && lng != null) {
            LOG.info("Map with coordinates lat = " + lat + ", lng = " + lng);
            view.addObject(VIEW_USER, new Searcher(lat, lng, 0, Properties.getIconUser48()));
            AdditionalDAO.insert(lat, lng); // add user record
            buildModelAndView(view, service.getBoundingBox(lat, lng), isCover, isBox, placeType);
        } else if (city != null) {
            LOG.info("Map with region = " + city + ", country = " + country);
            buildModelAndView(view, service.getBoundingBox(city, country), isCover, isBox, placeType);
        } else {
            LOG.info("No input data for map initialization, trying to get user geolocation from browser...");
        }
        return view;
    }

    private void buildModelAndView(@NotNull ModelAndView view,
                                   @Nullable BoundingBox boundingBox,
                                   @Nullable Boolean isCover,
                                   @Nullable Boolean isBox,
                                   @Nullable String placeTypeStr) {

        if (boundingBox == null) {
            view.addObject(VIEW_ERROR, "Please, check your input data and compare it with documentation. " +
                    "If all are correct then our developer know about trouble and fix it as soon as possible.");
            return;
        }
        if (!view.getModel().containsKey(VIEW_USER)) {
            LatLng center = GeoMath.boundsCenter(boundingBox.getBounds());
            view.addObject(VIEW_USER, new Searcher(center.lat, center.lng, 0, Properties.getIconUser48()));
        }
        view.addObject(VIEW_KEY, System.getenv("GOOGLE_API_KEY"));
        PlaceType placeType = placeTypeStr == null ? null : PlaceType.valueOf(placeTypeStr.toUpperCase());
        if (placeType != null) {
            algorithms.dynamicQuadTreeGeodesicMarkersDistribution(boundingBox, view, placeType);
        } else {
            algorithms.staticUniformGeodesicMarkersDistribution(boundingBox, view);
        }
        if (isBox == null || !isBox) {
            view.getModel().remove(VIEW_BOUNDING_BOXES);
        }
        if (isCover == null || !isCover) {
            view.getModel().remove(VIEW_SEARCHERS);
        }
    }

}
