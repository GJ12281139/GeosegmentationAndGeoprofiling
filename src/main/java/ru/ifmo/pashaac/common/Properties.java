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
    private static final int THREAD_SLEEP = 10_000;

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

    public static double getMarkerRadius() {
        return Double.parseDouble(properties.get("radius.around.marker"));
    }

    public static double getMarkerStep() {
        return Double.parseDouble(properties.get("distance.neighbor.markers"));
    }

    public static double getMaxBoundingBoxDiagonal() {
        return Double.parseDouble(properties.get("max.boundingbox.diagonal"));
    }

    public static int getMinRadarSearchPlaces() {
        return Integer.parseInt(properties.get("min.radar.search.places"));
    }

    public static int getMaxRadarSearchPlaces() {
        return Integer.parseInt(properties.get("max.radar.search.places"));
    }

    public static int getRadarSearchRadiusEps() {
        return Integer.parseInt(properties.get("radar.search.radius.eps"));
    }

    public static String getIconAzure48() {
        return properties.get("icon.azure.48");
    }

    public static String getIconAzure32() {
        return properties.get("icon.azure.32");
    }

    public static String getIconPink48() {
        return properties.get("icon.pink.48");
    }

    public static String getIconPink32() {
        return properties.get("icon.pink.32");
    }

    public static String getIconSearch48() {
        return properties.get("icon.search.48");
    }

    public static String getIconSearch32() {
        return properties.get("icon.search.32");
    }

}
