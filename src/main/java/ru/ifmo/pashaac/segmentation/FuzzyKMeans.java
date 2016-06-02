package ru.ifmo.pashaac.segmentation;

/**
 * So long, will not be use it.
 *
 * Created by Pavel Asadchiy
 * on 22.05.16 1:39.
 */
public class FuzzyKMeans {

//    private static final double DEFAULT_FUZZINESS = 1.3;
//
//    private final Collection<Marker> places;
//    private final FuzzyKMeansClusterer<Marker> clusterer;
//
//    public FuzzyKMeans(Collection<Marker> places, int clustersCount, double fuzziness, int iterationsCount) {
//        this.places = places;
//        this.clusterer = new FuzzyKMeansClusterer<>(clustersCount, fuzziness, iterationsCount, new GeoMath());
//    }
//
//    public FuzzyKMeans(Collection<Marker> places) {
//        this(places, Properties.getClusterMaxInCity(), DEFAULT_FUZZINESS, Properties.getKernelIterationsCount());
//    }
//
//    public List<Cluster> getClustersMaxRadius() {
//        return clusterer.cluster(places).stream()
//                .filter(clusterer -> clusterer.getPoints().size() > Properties.getClusterMinPlaces())
//                .map(cluster -> {
//                    LatLng center = new LatLng(cluster.getCenter().getPoint()[0], cluster.getCenter().getPoint()[1]);
//                    double maxRad = Cluster.getClusterRadius(center, cluster.getPoints());
//                    return Collections.singletonList(new Cluster(center.lat, center.lng, maxRad, Properties.getIconKernel(), cluster.getPoints()));
//                })
//                .flatMap(Collection::stream)
//                .filter(cluster -> cluster.getRad() > Properties.getClusterMinRadius())
//                .collect(Collectors.toList());
//    }
//
//    public List<Cluster> getFiltersClustersWithConditions(int splitClustersCount) {
//        return clusterer.cluster(places).stream()
//                .filter(clusterer -> clusterer.getPoints().size() > Properties.getClusterMinPlaces())
//                .map(cluster -> {
//                    LatLng center = new LatLng(cluster.getCenter().getPoint()[0], cluster.getCenter().getPoint()[1]);
//                    double maxRad = Cluster.getClusterRadius(center, cluster.getPoints());
//                    if (maxRad > Properties.getClusterMaxRadius()) {
//                        return new FuzzyKMeans(cluster.getPoints(), splitClustersCount, DEFAULT_FUZZINESS, Properties.getKernelIterationsCount())
//                                .getFiltersClustersWithConditions(splitClustersCount);
//                    }
//                    return Collections.singletonList(new Cluster(center.lat, center.lng, maxRad, Properties.getIconKernel(), cluster.getPoints()));
//                })
//                .flatMap(Collection::stream)
//                .filter(cluster -> cluster.getRad() > Properties.getClusterMinRadius())
//                .collect(Collectors.toList());
//    }
//
//    public List<Cluster> getFiltersClustersWithConditions() {
//        return getFiltersClustersWithConditions(2);
//    }

}
