package ru.ifmo.pashaac.segmentation;

/**
 * Created by Pavel Asadchiy
 * on 22.05.16 1:05.
 */
public class MultiKMeansPlusPlusClustering {

//    private final Collection<Marker> places;
//    private final MultiKMeansPlusPlusClusterer<Marker> clusterer;
//
//    public MultiKMeansPlusPlusClustering(Collection<Marker> places, int kernelsCount, int iterationsCount, int kmeansRunCount) {
//        this.places = places;
//        KMeansPlusPlusClusterer<Marker> kMeansPlusPlusClusterer =
//                new KMeansPlusPlusClusterer<>(kernelsCount, iterationsCount, new GeoMath());
//        this.clusterer = new MultiKMeansPlusPlusClusterer<>(kMeansPlusPlusClusterer, kmeansRunCount);
//    }
//
//    public MultiKMeansPlusPlusClustering(Collection<Marker> places) {
//        this(places, Properties.getKernelDefaultCount(), Properties.getKernelIterationsCount(), Properties.getKernelKmeansRunCount());
//    }
//
//    public List<Cluster> getClustersMaxRadius() {
//        return clusterer.cluster(places).stream()
//                .map(cluster -> {
//                    LatLng center = new LatLng(cluster.getCenter().getPoint()[0], cluster.getCenter().getPoint()[1]);
//                    double maxRad = Cluster.getClusterRadius(center, cluster.getPoints());
//                    return new Cluster(center.lat, center.lng, maxRad, Properties.getIconKernel(), cluster.getPoints());
//                })
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
//                        return new MultiKMeansPlusPlusClustering(cluster.getPoints(), splitClustersCount, Properties.getKernelIterationsCount(), Properties.getKernelKmeansRunCount())
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
