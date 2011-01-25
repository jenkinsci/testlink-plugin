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

import hudson.model.ProminentProjectAction;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Actionable;
import hudson.plugins.testlink.util.Messages;
import hudson.util.Graph;

import java.io.IOException;
import java.io.Serializable;

import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TestLinkProjectAction extends Actionable implements
		ProminentProjectAction, Serializable
{

	private static final long serialVersionUID = 5600270062198355080L;
	
	public static final String ICON_FILE_NAME = "/plugin/testlink/icons/testlink-24.png";
	public static final String URL_NAME = "testLinkResult";

	public static final int CHART_WIDTH = 500;
	public static final int CHART_HEIGHT = 200;

	private AbstractProject<?, ?> project;

	public TestLinkProjectAction(AbstractProject<?, ?> project)
	{
		this.project = project;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hudson.model.Action#getDisplayName()
	 */
	public String getDisplayName()
	{
		return Messages.TestLinkProjectAction_DisplayName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hudson.model.Action#getIconFileName()
	 */
	public String getIconFileName()
	{
		return ICON_FILE_NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hudson.model.Action#getUrlName()
	 */
	public String getUrlName()
	{
		return URL_NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hudson.search.SearchItem#getSearchUrl()
	 */
	public String getSearchUrl()
	{
		return URL_NAME;
	}

	/**
	 * 
	 * Redirects the index page to the last result.
	 * 
	 * @param request
	 *            Stapler request
	 * @param response
	 *            Stapler response
	 * @throws IOException
	 *             in case of an error
	 */
	public void doIndex( final StaplerRequest request,
			final StaplerResponse response ) throws IOException
	{
		AbstractBuild<?, ?> build = getLastFinishedBuild();
		if (build != null)
		{
			response.sendRedirect2(String.format("../%d/%s", build.getNumber(),
					TestLinkBuildAction.URL_NAME));
		}
	}

	/**
	 * Returns the last finished build.
	 * 
	 * @return the last finished build or <code>null</code> if there is no such
	 *         build
	 */
	private AbstractBuild<?, ?> getLastFinishedBuild()
	{
		AbstractBuild<?, ?> lastBuild = project.getLastBuild();
		while (lastBuild != null
				&& (lastBuild.isBuilding() || lastBuild
						.getAction(TestLinkBuildAction.class) == null))
		{
			lastBuild = lastBuild.getPreviousBuild();
		}
		return lastBuild;
	}

	/**
	 * Display the trend map. Delegates to the the associated
	 * {@link ResultAction}.
	 * 
	 * @param request
	 *            Stapler request
	 * @param response
	 *            Stapler response
	 * @throws IOException
	 *             in case of an error
	 */
	public void doTrendMap( final StaplerRequest request,
			final StaplerResponse response ) throws IOException
	{
		AbstractBuild<?, ?> lastBuild = this.getLastFinishedBuild();
		TestLinkBuildAction lastAction = lastBuild
				.getAction(TestLinkBuildAction.class);
		XYDataset dataset = ChartUtil.createXYDataset(lastAction);
		final JFreeChart chart = ChartUtil.buildXYChart(dataset);

		new Graph(-1, CHART_WIDTH, CHART_HEIGHT)
		{
			protected JFreeChart createGraph()
			{
				return chart;
			}
		}.doMap(request, response);

	}

	/**
	 * Display the trend graph. Delegates to the the associated
	 * {@link ResultAction}.
	 * 
	 * @param request
	 *            Stapler request
	 * @param response
	 *            Stapler response
	 * @throws IOException
	 *             in case of an error in
	 *             {@link ResultAction#doGraph(StaplerRequest, StaplerResponse, int)}
	 */
	public void doTrend( final StaplerRequest request,
			final StaplerResponse response ) throws IOException
	{
		AbstractBuild<?, ?> lastBuild = this.getLastFinishedBuild();
		TestLinkBuildAction lastAction = lastBuild
				.getAction(TestLinkBuildAction.class);
		XYDataset dataset = ChartUtil.createXYDataset(lastAction);
		final JFreeChart chart = ChartUtil.buildXYChart(dataset);

		new Graph(-1, CHART_WIDTH, CHART_HEIGHT)
		{
			protected JFreeChart createGraph()
			{
				return chart;
			}
		}.doPng(request, response);

	}

	/**
	 * Called from floatingBox.jelly to check if we have valid results.
	 * @return
	 */
	public final boolean hasValidResults()
	{
		AbstractBuild<?, ?> build = getLastFinishedBuild();
		if (build != null)
		{
			TestLinkBuildAction resultAction = build.getAction(TestLinkBuildAction.class);
			if (resultAction != null)
			{
				return resultAction.getPreviousResult() != null;
			}
		}
		return false;
	}

}
