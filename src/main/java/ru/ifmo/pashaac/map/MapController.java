package ru.ifmo.pashaac.map;

import com.google.maps.model.PlaceType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.ifmo.pashaac.coverage.CoverageAlgorithms;
import ru.ifmo.pashaac.coverage.CoverageModel;

/**
 * Created by Pavel Asadchiy
 * 18.04.16 22:17.
 */
@Controller
@RequestMapping
public class MapController {

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
                                    @RequestParam(value = "box", required = false) Boolean boundingbox,
                                    @RequestParam(value = "cover", required = false) Boolean coverage,
                                    @RequestParam(value = "pType", required = false) String placeType) {

        if (lat != null && lng != null) {
            LOG.info("Map with coordinates lat = " + lat + ", lng = " + lng);
            return buildModelAndView(service.getBoundingBoxModel(lat, lng), coverage, boundingbox, placeType);
        }
        if (city != null) {
            LOG.info("Map with coordinates city = " + city + ", country = " + country);
            return buildModelAndView(service.getBoundingBoxModel(city, country), coverage, boundingbox, placeType);
        }
        LOG.info("No input data for map initialization, trying get user geolocation from browser...");
        return new ModelAndView("map");
    }

    private ModelAndView buildModelAndView(CoverageModel model, Boolean coverage, Boolean boundingbox, String placeTypeStr) {
        ModelAndView view = new ModelAndView("map");
        if (model.getError() != null) {
            view.addObject("error", model.getError());
            return view;
        }
        view.addObject("key", System.getenv("GOOGLE_API_KEY"));
        PlaceType placeType = placeTypeStr != null ? PlaceType.valueOf(placeTypeStr.toUpperCase()) : PlaceType.PARK;
        view.addObject("model", flagsHandler(model, coverage, placeType));
        if (boundingbox != null && boundingbox) {
            view.addObject("box", true);
        }
        return view;
    }

    private CoverageModel flagsHandler(CoverageModel model, Boolean coverage, PlaceType placeType) {
        // ignore boundingbox flag, in future remove box around region
        if (coverage == null || !coverage) {
            return model;
        }
        CoverageModel distributionModel = algorithms.getDynamicTreeGeodesicMarkersDistribution(model, placeType);
        LOG.info("Distribution model for client is " + distributionModel);
        return distributionModel;
    }
}
