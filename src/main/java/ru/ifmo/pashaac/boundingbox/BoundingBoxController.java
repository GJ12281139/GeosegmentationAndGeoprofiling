package ru.ifmo.pashaac.boundingbox;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.ifmo.pashaac.common.Marker;
import ru.ifmo.pashaac.coverage.CoverageAlgorithms;
import ru.ifmo.pashaac.coverage.CoverageModel;

import java.util.List;

/**
 * Created by Pavel Asadchiy
 * 18.04.16 22:17.
 */
@Controller
public class BoundingBoxController {

    private static final Logger LOG = Logger.getLogger(BoundingBoxService.class);

    @Autowired
    private final BoundingBoxService service;

    @Autowired
    private final CoverageAlgorithms algorithms;

    public BoundingBoxController(BoundingBoxService service, CoverageAlgorithms algorithms) {
        this.service = service;
        this.algorithms = algorithms;
    }

    @RequestMapping(value = "/boundingbox", method = RequestMethod.GET)
    public ModelAndView boundingbox(@RequestParam(value = "lat", required = false) Double lat,
                                    @RequestParam(value = "lng", required = false) Double lng,
                                    @RequestParam(value = "coverage", required = false) Boolean coverage) {
        LOG.info("Boundingbox with coordinates lat = " + lat + ", lng = " + lng);
        if (lat == null || lng == null) {
            return new ModelAndView();
        }
        return viewResolve(service.getBoundingBox(lat, lng), coverage);
    }

    @RequestMapping(value = "/boundingbox/text", method = RequestMethod.GET)
    public ModelAndView boundingbox(@RequestParam(value = "city") String city,
                                    @RequestParam(value = "country", required = false) String country,
                                    @RequestParam(value = "coverage", required = false) Boolean coverage) {
        LOG.info("Boundingbox with coordinates city = " + city+ ", country = " + country);
        return viewResolve(service.getBoundingBox(city, country), coverage);
    }

    private ModelAndView viewResolve(CoverageModel model, Boolean coverage) {
        ModelAndView view = new ModelAndView("boundingbox");
        if (model.getMessage() != null) {
            view.addObject("error", model);
            return view;
        }
        view.addObject("model", modelResolve(model, coverage));
        view.addObject("key", System.getenv("GOOGLE_API_KEY"));
        return view;
    }

    private CoverageModel modelResolve(CoverageModel model, Boolean coverage) {
        if (coverage != null && coverage) {
            List<Marker> markers = algorithms.getStaticUniformGeodesicMarkersDistribution(model.getBoundingbox().getBounds());
            LOG.info("Markers count is " + markers.size());
            return new CoverageModel(model.getUserGeolocation(), model.getBoundingbox(), markers, null);
        }
        return model;
    }
}
