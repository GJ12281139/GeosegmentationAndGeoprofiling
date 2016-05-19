package ru.ifmo.pashaac.statistic;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import ru.ifmo.pashaac.foursquare.FoursquarePlace;

import java.util.Collection;

/**
 * Created by Pavel Asadchiy
 * on 17.05.16 21:28.
 */
public class FoursquareStatistic extends ApplicationFrame {

    public FoursquareStatistic(String applicationTitle,
                               String chartTitle,
                               String verticalText,
                               String horizontalText,
                               DefaultCategoryDataset dataset) {
        super(applicationTitle);
        JFreeChart barChart = ChartFactory.createBarChart(
                chartTitle,
                verticalText,
                horizontalText,
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new java.awt.Dimension());
        setContentPane(chartPanel);
    }

    public static void showCheckinsGraph(final Collection<FoursquarePlace> places) {
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        places.stream()
                .forEach(place -> dataset.addValue(1, "checkins", "blabla"));
//        new Long(place.getCheckinsCount() / 1000))
        final FoursquareStatistic statistic =
                new FoursquareStatistic("Checkins statistics", "Checkins statistics", "Places count", "Checkins value", dataset);
        statistic.pack();
        RefineryUtilities.centerFrameOnScreen(statistic);
        statistic.setVisible(true);
    }

    @Override
    public String toString() {
        return "FoursquareStatistic{}";
    }
}
