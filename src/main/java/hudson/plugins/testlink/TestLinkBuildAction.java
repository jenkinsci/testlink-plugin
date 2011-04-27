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

import hudson.model.Action;
import hudson.model.AbstractBuild;
import hudson.plugins.testlink.result.TestLinkReport;

import java.io.Serializable;

import org.kohsuke.stapler.StaplerProxy;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TestLinkBuildAction 
implements Action, Serializable, StaplerProxy
{

	private static final long serialVersionUID = -914904584770393909L;
	
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
	
	/**
	 * @return TestLink job execution result
	 */
	public TestLinkResult getResult()
	{
		return this.result;
	}
	
	/**
	 * @return Previous TestLink report
	 */
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
	
	/**
	 * @return Previous TestLink job execution result
	 */
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
	
	/**
	 * @return Previous Build Action
	 */
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
	
	/**
	 * @return Report summary
	 */
	public String getSummary(){
        return ResultsSummary.createReportSummary(result.getReport(), this.getPreviousReport());
    }
	
	/**
	 * @return Detailed Report summary
	 */
    public String getDetails(){
        return ResultsSummary.createReportSummaryDetails(result.getReport(), this.getPreviousReport());
    }
	
}
