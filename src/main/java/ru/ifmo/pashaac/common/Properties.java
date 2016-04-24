package ru.ifmo.pashaac.common;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Pavel Asadchiy
 * 25.04.16 1:12.
 */
public class Properties implements Runnable {

    private static final Logger LOG = Logger.getLogger(Properties.class);

    private static Map<String, String> properties = new HashMap<>();

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            LOG.info("Trying update properties...");
            try {
                BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/application.properties"));
                properties = reader.lines()
                        .map(s -> s.replaceAll("\\s+", ""))
                        .filter(s -> s.contains("="))
                        .map(s -> s.split("="))
                        .collect(Collectors.toMap(s -> s[0], s -> s[1]));
                LOG.info("Properties updated: " + properties);
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                LOG.error("Error occur while thread sleep");
            } catch (FileNotFoundException e) {
                LOG.error("Error open application.properties");
            }
        }
    }

    // default radius around marker
    public static double getMarkerRadius() {
        return Double.parseDouble(properties.get("radius.around.marker"));
    }

    // diff between neighbour markers
    public static double getMarkerStep() {
        return Double.parseDouble(properties.get("distance.between.markers"));
    }

    // diff between neighbour markers on lat line (not geodesic)
    public static double getLatStep() {
        return Double.parseDouble(properties.get("lat.default.step"));
    }

    // diff between neighbour markers on lng line (not geodesic)
    public static double getLngStep() {
        return Double.parseDouble(properties.get("lng.default.step"));
    }

    public static double getMaxBoundingBoxDiagonal() {
        return Double.parseDouble(properties.get("max.boundingbox.diagonal"));
    }

}
