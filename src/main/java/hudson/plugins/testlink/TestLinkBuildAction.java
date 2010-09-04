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
import hudson.model.Action;

import java.io.Serializable;

import org.kohsuke.stapler.StaplerProxy;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 02/09/2010
 */
public class TestLinkBuildAction 
implements Action, Serializable, StaplerProxy
{

	public static final String DISPLAY_NAME = "TestLink";
	public static final String ICON_FILE_NAME = "/plugin/testlink/icons/testlink-24.png";
	public static final String URL_NAME = "testLinkResult";
	
	private AbstractBuild<?, ?> build;
	private TestLinkResult result;
	
	public TestLinkBuildAction(AbstractBuild<?, ?> build, TestLinkResult result)
	{
		this.build = build;
		this.result = result;
	}
	
	public String getDisplayName()
	{
		return DISPLAY_NAME;
	}

	public String getIconFileName()
	{
		return ICON_FILE_NAME;
	}

	public String getUrlName()
	{
		return URL_NAME;
	}

	public Object getTarget()
	{
		return this.result;
	}

	public AbstractBuild<?, ?> getBuild() {
		return build;
	}
	
	public TestLinkResult getResult()
	{
		return this.result;
	}
	
	private TestLinkReport getPreviousReport()
	{
		TestLinkResult previousResult = this.getPreviousResult();
		TestLinkReport previousReport = null;
		if ( previousResult != null )
		{
			previousReport = previousResult.getReport();
		}
		return previousReport;
	}
	
	public TestLinkResult getPreviousResult()
	{
		TestLinkBuildAction previousAction = this.getPreviousAction();
		TestLinkResult previousResult = null;
		if ( previousAction != null )
		{
			previousResult = previousAction.getResult();
		}
		return previousResult;
	}
	
	public TestLinkBuildAction getPreviousAction()
	{
		if ( this.build != null )
		{
			AbstractBuild<?, ?> previousBuild = this.build.getPreviousBuild();
			if ( previousBuild != null )
			{
				return previousBuild.getAction(TestLinkBuildAction.class);
			}
		}
		return null;
	}
	
	public String getSummary(){
        return ReportSummary.createReportSummary(result.getReport(), this.getPreviousReport());
    }

    public String getDetails(){
        return ReportSummary.createReportSummaryDetails(result.getReport(), this.getPreviousReport());
    }
	
}
