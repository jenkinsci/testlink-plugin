/* 
 * The MIT License
 * 
 * Copyright (c) 2010 Bruno P. Kinoshita <http://www.kinoshita.eti.br>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.plugins.testlink;


import hudson.plugins.testlink.result.TestLinkReport;
import hudson.plugins.testlink.result.TestLinkResult;
import hudson.plugins.testlink.util.Messages;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class ChartUtil
{
	
	/**
	 * Hidden constructor.
	 */
	private ChartUtil()
	{
		super();
	}
	
	/**
	 * Creates the TestLink Trend Chart.
	 * 
	 * @param dataset of XY series
	 * @return a chart
	 */
	public static JFreeChart buildXYChart(XYDataset dataset) {

        final JFreeChart chart = ChartFactory.createXYLineChart(
                null,                   // chart title
                "Build #",                   // unused
                null,                    // range axis label
                dataset,                  // data
                PlotOrientation.VERTICAL, // orientation
                true,                     // include legend
                true,                     // tooltips
                false                     // urls
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION ON THE CHART...

        final LegendTitle legend = chart.getLegend();
        legend.setPosition(RectangleEdge.RIGHT);

        chart.setBackgroundPaint(Color.white);

        final XYPlot plot = chart.getXYPlot();

        plot.setBackgroundPaint(Color.lightGray);
        //    plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(1, true);
        
        renderer.setSeriesPaint(0, Color.green);
        renderer.setSeriesPaint(1, Color.red);
        renderer.setSeriesPaint(2, Color.orange);
        renderer.setSeriesPaint(3, Color.blue);
        
        plot.setRenderer(renderer);

     // change the auto tick unit selection to integer units only...
        /*final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());*/
        // OPTIONAL CUSTOMISATION COMPLETED.

        return chart;
    }
	
	/**
	 * Creates the XY dataset required to create the CCM Trend graph.
	 * 
	 * @param lastAction last TestLinkBuildAction
	 * @return XY Dataset
	 */
	public static final XYDataset createXYDataset( TestLinkBuildAction lastAction )
	{
		
		TestLinkBuildAction tempAction = lastAction;
		final XYSeriesCollection dataset = new XYSeriesCollection();
		final XYSeries passedTests = new XYSeries( Messages.ChartUtil_PassedTests() );
		final XYSeries failedTests = new XYSeries( Messages.ChartUtil_FailedTests() );
		final XYSeries blockedTests = new XYSeries( Messages.ChartUtil_BlockedTests() );
		final XYSeries totalTests = new XYSeries( Messages.ChartUtil_TotalTests() );
		
		dataset.addSeries(passedTests);
		dataset.addSeries(failedTests);
		dataset.addSeries(blockedTests);
		dataset.addSeries(totalTests);
		do 
		{
			TestLinkResult result = tempAction.getResult();
			TestLinkReport report = result.getReport();
			int buildNumber = tempAction.getBuild().number;			
			totalTests.add(buildNumber, report.getTestsTotal());
			passedTests.add(buildNumber, report.getTestsPassed());
			failedTests.add(buildNumber, report.getTestsFailed());
			blockedTests.add(buildNumber, report.getTestsBlocked());
			tempAction = tempAction.getPreviousAction();
		} while ( tempAction != null );
		
		return dataset;
		
	}
	
}
