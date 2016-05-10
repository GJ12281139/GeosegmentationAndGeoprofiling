package ru.ifmo.pashaac.common;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Properties thread scanner. Check application.properties file and update Map<String, String>
 * <p>
 * Created by Pavel Asadchiy
 * 25.04.16 1:12.
 */
public class Properties implements Runnable {

    private static final Logger LOG = Logger.getLogger(Properties.class);
    private static final String PATH = "src/main/resources/application.properties";
    private static final int THREAD_SLEEP = 60_000;

    private static Map<String, String> properties = new HashMap<>();

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(PATH));
                properties = reader.lines()
                        .map(s -> s.replaceAll("\\s+", ""))
                        .filter(s -> s.contains("="))
                        .map(s -> s.split("="))
                        .collect(Collectors.toMap(s -> s[0], s -> s[1]));
                LOG.info("Properties updated... " + properties);
                Thread.sleep(THREAD_SLEEP);
            } catch (InterruptedException e) {
                LOG.error("Error occur while thread sleep");
            } catch (FileNotFoundException e) {
                LOG.error("Error open application.properties");
            }
        }
    }


    // Searcher
    public static double getDefaultSearcherRadius() {
        return Double.parseDouble(properties.get("default.searcher.radius"));
    }

    public static double getNeighborSearchersDistance() {
        return Double.parseDouble(properties.get("neighbor.searchers.distance"));
    }


    // BoundingBox
    public static double getMaxBoundingBoxDiagonal() {
        return Double.parseDouble(properties.get("max.boundingbox.diagonal"));
    }


    // Google Maps
    public static int getGoogleMapsMinPlacesSearch() {
        return Integer.parseInt(properties.get("google.maps.min.places.search"));
    }

    public static int getGoogleMapsMaxPlacesSearch() {
        return Integer.parseInt(properties.get("google.maps.max.places.search"));
    }

    public static int getGoogleMapsPlacesSearchRadEps() {
        return Integer.parseInt(properties.get("google.maps.places.search.rad.eps"));
    }


    // Foursquare
    public static int getFoursquareMaxVenuesSearch() {
        return Integer.parseInt(properties.get("foursquare.max.venues.search"));
    }

    public static int getFoursquareMinVenuesSearch() {
        return Integer.parseInt(properties.get("foursquare.min.venues.search"));
    }

    public static int getFoursquareVenuesSearchRadEps() {
        return Integer.parseInt(properties.get("foursquare.venues.search.rad.eps"));
    }

    public static int getFoursquareMinCheckinsCount() {
        return Integer.parseInt(properties.get("foursquare.min.checkins.count"));
    }

    public static int getFoursquareMinUserCount() {
        return Integer.parseInt(properties.get("foursquare.min.user.count"));
    }


    // Icons
    public static String getIconAzure48() {
        return properties.get("icon.place.azure.48");
    }

    public static String getIconAzure32() {
        return properties.get("icon.place.azure.32");
    }

    public static String getIconPink48() {
        return properties.get("icon.place.pink.48");
    }

    public static String getIconPink32() {
        return properties.get("icon.place.pink.32");
    }

    public static String getIconGreen48() {
        return properties.get("icon.place.green.48");
    }

    public static String getIconGreen32() {
        return properties.get("icon.place.green.32");
    }

    public static String getIconSearch() {
        return properties.get("icon.search");
    }

    public static String getIconSearchError() {
        return properties.get("icon.search.error");
    }

    public static String getIconUser() {
        return properties.get("icon.user");
    }

    public static String getIconKernel() {
        return properties.get("icon.kernel");
    }


    // Kernel
    public static int getKernelIterationsCount() {
        return Integer.parseInt(properties.get("kernel.iterations.count"));
    }

    public static int getKernelsDefaultNumber() {
        return Integer.parseInt(properties.get("kernels.default.number"));
    }

}
