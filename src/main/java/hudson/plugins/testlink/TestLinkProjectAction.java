/**
 *	 __                                        
 *	/\ \      __                               
 *	\ \ \/'\ /\_\    ___     ___   __  __  __  
 *	 \ \ , < \/\ \ /' _ `\  / __`\/\ \/\ \/\ \ 
 *	  \ \ \\`\\ \ \/\ \/\ \/\ \L\ \ \ \_/ \_/ \
 *	   \ \_\ \_\ \_\ \_\ \_\ \____/\ \___x___/'
 *	    \/_/\/_/\/_/\/_/\/_/\/___/  \/__//__/  
 *                                          
 * Copyright (c) 1999-present Kinow
 * Casa Verde - São Paulo - SP. Brazil.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Kinow ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Kinow.                                      
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 02/09/2010
 */
package hudson.plugins.testlink;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Actionable;
import hudson.model.ProminentProjectAction;
import hudson.plugins.testlink.util.ChartUtil;
import hudson.util.Graph;

import java.io.IOException;
import java.io.Serializable;

import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 02/09/2010
 */
public class TestLinkProjectAction extends Actionable implements
		ProminentProjectAction, Serializable
{

	public static final String DISPLAY_NAME = "TestLink Results";
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
		return DISPLAY_NAME;
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
