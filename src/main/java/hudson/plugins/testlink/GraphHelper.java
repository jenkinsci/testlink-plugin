package hudson.plugins.testlink;

import hudson.util.ChartUtil.NumberOnlyBuildLabel;
import hudson.util.ColorPalette;
import hudson.util.ShiftedCategoryAxis;
import hudson.util.StackedAreaRenderer2;

import java.awt.Color;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedAreaRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Helper class for trend graph generation.
 * 
 * @author TestNG plug-in
 * @since 1.0
 */
public class GraphHelper {

    /**
     * Do not instantiate GraphHelper.
     */
    private GraphHelper() {
        super();
    }

    public static void redirectWhenGraphUnsupported(StaplerResponse rsp, StaplerRequest req) throws IOException {
        // not available. send out error message
        rsp.sendRedirect2(req.getContextPath() + "/images/headless.png");
    }

    public static JFreeChart createChart(StaplerRequest req, CategoryDataset dataset) {

        final JFreeChart chart = ChartFactory.createStackedAreaChart(null, // chart title
                null, // unused
                "TestLink Tests Count", // range axis label
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips
                false // urls
                );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        final LegendTitle legend = chart.getLegend();
        legend.setPosition(RectangleEdge.RIGHT);

        chart.setBackgroundPaint(Color.white);

        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);
        plot.setForegroundAlpha(0.8f);
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.black);

        CategoryAxis domainAxis = new ShiftedCategoryAxis(null);
        plot.setDomainAxis(domainAxis);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
        domainAxis.setLowerMargin(0.0);
        domainAxis.setUpperMargin(0.0);
        domainAxis.setCategoryMargin(0.0);

        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        StackedAreaRenderer ar = new StackedAreaRenderer2() {
            private static final long serialVersionUID = 331915263367089058L;

            @Override
            public String generateURL(CategoryDataset dataset, int row, int column) {
                NumberOnlyBuildLabel label = (NumberOnlyBuildLabel) dataset.getColumnKey(column);
                // return label.build.getNumber() + "/" + PluginImpl.URL + "/";
                return label.build.getNumber() + "/" + TestLinkBuildAction.URL_NAME;
            }

            @Override
            public String generateToolTip(CategoryDataset dataset, int row, int column) {
                NumberOnlyBuildLabel label = (NumberOnlyBuildLabel) dataset.getColumnKey(column);
                TestLinkBuildAction build = label.build.getAction(TestLinkBuildAction.class);
                TestLinkResult result = build.getResult();
                Report report = result.getReport();

                switch (row) {
                case 0:
                    return String.valueOf(report.getBlocked()) + " Blocked";
                case 1:
                    return String.valueOf(report.getFailed()) + " Failed";
                case 2:
                    return String.valueOf(report.getNotRun()) + " Not Run";
                case 3:
                    return String.valueOf(report.getPassed()) + " Passed";
                default:
                    return "";
                }
            }

        };

        plot.setRenderer(ar);
        ar.setSeriesPaint(0, ColorPalette.YELLOW); // Blocked
        ar.setSeriesPaint(1, ColorPalette.RED); // Failures
        ar.setSeriesPaint(2, ColorPalette.GREY); // Not Run
        ar.setSeriesPaint(3, ColorPalette.BLUE); // Pass

        // crop extra space around the graph
        plot.setInsets(new RectangleInsets(0, 0, 0, 5.0));

        return chart;
    }

}
