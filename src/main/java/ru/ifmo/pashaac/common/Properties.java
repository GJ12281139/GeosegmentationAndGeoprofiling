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

    public static int clusterMinRadius = 0;
    public static int clusterMaxRadius = 0;

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


    // Marker
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


    // Place
    public static int getMinPlacesSearch() {
        return Integer.parseInt(properties.get("min.places.search"));
    }

    public static int getMaxPlacesSearch() {
        return Integer.parseInt(properties.get("max.places.search"));
    }

    public static int getPlacesSearchRadEps() {
        return Integer.parseInt(properties.get("places.search.rad.eps"));
    }


    // Icons
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

    public static String getIconPathPrefix() {
        return properties.get("icon.path.prefix");
    }


    // Kernel
    public static int getKernelIterationsCount() {
        return Integer.parseInt(properties.get("kernel.iterations.count"));
    }

    public static int getKernelDefaultCount() {
        return Integer.parseInt(properties.get("kernel.default.count"));
    }

//    public static int getKernelDefaultRadius() {
//        return Integer.parseInt(properties.get("kernel.default.radius"));
//    }

    public static int getKernelKmeansRunCount() {
        return Integer.parseInt(properties.get("kernel.kmeans.run.count"));
    }


    // Clustering
    public static int getClusterMinPlaces() {
        return Integer.parseInt(properties.get("cluster.min.places"));
    }

    public static int getClusterMaxRadius() {
        return clusterMaxRadius == 0 ? Integer.parseInt(properties.get("cluster.max.radius")) : clusterMaxRadius;
    }

    public static int getClusterMinRadius() {
        return clusterMinRadius == 0 ? Integer.parseInt(properties.get("cluster.min.radius")) : clusterMinRadius;
    }

    public static int getClusterMaxInCity() {
        return Integer.parseInt(properties.get("cluster.max.in.city"));
    }

}
