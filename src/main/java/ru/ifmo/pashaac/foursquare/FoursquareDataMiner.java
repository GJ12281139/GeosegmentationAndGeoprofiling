package ru.ifmo.pashaac.foursquare;

import com.google.maps.model.Bounds;
import com.google.maps.model.LatLng;
import com.sun.istack.internal.Nullable;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.Result;
import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.VenuesSearchResult;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ifmo.pashaac.common.GeoMath;
import ru.ifmo.pashaac.common.Properties;
import ru.ifmo.pashaac.common.wrapper.BoundingBox;
import ru.ifmo.pashaac.common.wrapper.Searcher;
import ru.ifmo.pashaac.map.MapService;

import java.util.*;

/**
 * Service get data from Foursquare with API help
 *
 * Created by Pavel Asadchiy
 * 07.05.16 15:08.
 */
@Service
public class FoursquareDataMiner {

    private static final Logger LOG = Logger.getLogger(FoursquareDataMiner.class);

    @Autowired
    private final MapService mapService;
    private final List<BoundingBox> boundingBoxes;
    private final List<Searcher> searchers;
    private final Set<FoursquarePlace> places;

    public FoursquareDataMiner(MapService mapService) {
        this.mapService = mapService;
        this.boundingBoxes = new ArrayList<>();
        this.searchers = new ArrayList<>();
        this.places = new HashSet<>();
    }

    public Set<FoursquarePlace> getPlaces() {
        return places;
    }

    public List<Searcher> getSearchers() {
        return searchers;
    }

    public List<BoundingBox> getBoundingBoxes() {
        return boundingBoxes;
    }

    public void quadtreePlaceSearcher(BoundingBox boundingBox, FoursquarePlaceType foursquarePlaceType) {
        boundingBoxes.add(boundingBox);
        int foursquareApiCallCounter = 0;
        for (int i = 0; i < boundingBoxes.size(); i++) {
            final BoundingBox bBox = boundingBoxes.get(i);
            LOG.info("Trying get data for boundingbox #" + i + "... " + bBox);
            LatLng boxCenter = GeoMath.boundsCenter(bBox.getBounds());
            int rRad = (int) Math.ceil(GeoMath.halfDiagonal(bBox.getBounds()));
            int lRad = (int) Properties.getDefaultSearcherRadius();
            while (lRad < rRad) {
                int mRad = rRad - lRad < Properties.getFoursquareVenuesSearchRadEps() ? rRad : (lRad + rRad) / 2;
                CompactVenue[] venues = venuesSearch(boxCenter, foursquarePlaceType, mRad);
                ++foursquareApiCallCounter;
                if (venues == null) {
                    searchers.add(new Searcher(boxCenter, mRad, Properties.getIconSearchError()));
                    break;
                }
                if (mRad < rRad && venues.length > Properties.getFoursquareMaxVenuesSearch()) {
                    rRad = mRad - 1;
                    continue;
                }
                if (mRad < rRad && venues.length < Properties.getFoursquareMinVenuesSearch()) {
                    lRad = mRad + 1;
                    continue;
                }

                searchers.add(new Searcher(boxCenter, mRad, Properties.getIconSearch()));
                Arrays.stream(venues)
                        .forEach(venue -> places.add(new FoursquarePlace(venue, boundingBox.getCity(),
                                boundingBox.getCountry(), foursquarePlaceType.name(), Properties.getIconPink32())));

                if (mRad < rRad) {
                    Bounds leftDownBounds = GeoMath.leftDownBoundingBox(boxCenter, bBox.getBounds());
                    Bounds leftUpBounds = GeoMath.leftUpBoundingBox(boxCenter, bBox.getBounds());
                    Bounds rightDownBounds = GeoMath.rightDownBoundingBox(boxCenter, bBox.getBounds());
                    Bounds rightUpBounds = GeoMath.rightUpBoundingBox(boxCenter, bBox.getBounds());

                    boundingBoxes.add(new BoundingBox(leftDownBounds, bBox.getCity(), bBox.getCountry()));
                    boundingBoxes.add(new BoundingBox(leftUpBounds, bBox.getCity(), bBox.getCountry()));
                    boundingBoxes.add(new BoundingBox(rightDownBounds, bBox.getCity(), bBox.getCountry()));
                    boundingBoxes.add(new BoundingBox(rightUpBounds, bBox.getCity(), bBox.getCountry()));
                }
                break;
            }
        }
        LOG.info("Foursquare API called " + foursquareApiCallCounter + " times");
        LOG.info("BoundingBoxes search cycle called " + boundingBoxes.size() + " times");
    }

    /**
     * P.S. for getting full place info use CompleteVenue completeVenue = foursquareApi.venue(venue.getId()).getResult();
     * @param location - current location/search center
     * @param foursquarePlaceType - wrapper for categoryId
     * @param rad - search radius
     * @return - compact search result array
     */
    @Nullable
    private CompactVenue[] venuesSearch(LatLng location, FoursquarePlaceType foursquarePlaceType, int rad) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("ll", location.lat + "," + location.lng);
            params.put("intent", "browse");
            params.put("categoryId", foursquarePlaceType.categoryId);
            params.put("radius", String.valueOf(rad));
            Result<VenuesSearchResult> venuesSearchResult = mapService.getFoursquareApi().venuesSearch(params);
            if (venuesSearchResult.getMeta().getCode() == 200) {
                return venuesSearchResult.getResult().getVenues();
            } else {
                LOG.error("Venues search return code " + venuesSearchResult.getMeta().getCode());
                return null;
            }
        } catch (FoursquareApiException e) {
            LOG.error("Error trying get foursquare venues, " + e.getMessage());
            return null;
        }
    }

}