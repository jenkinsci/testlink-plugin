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

import hudson.plugins.testlink.model.TestLinkTestCase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import testlink.api.java.client.TestLinkAPIConst;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 02/09/2010
 */
public class TestLinkReport 
implements Serializable
{

	private int testsFailed;
	private int testsPassed;
	private int testsBlocked;
	private int testsTotal;
	
	private List<TestLinkTestCase> testCases;
	
	private String buildName;
	private Integer buildId;
	
	public TestLinkReport()
	{
		super();
		this.testsBlocked = 0;
		this.testsFailed = 0;
		this.testsTotal = 0;
		this.testsPassed = 0;
		testCases = new ArrayList<TestLinkTestCase>();
		this.buildName = null;
		this.buildId = 0;
	}

	public int getTestsFailed()
	{
		return testsFailed;
	}

	public int getTestsPassed()
	{
		return testsPassed;
	}

	public int getTestsBlocked()
	{
		return testsBlocked;
	}

	public int getTestsTotal()
	{
		return this.testsFailed + this.testsPassed + this.testsBlocked;
	}
	
	public List<TestLinkTestCase> getListOfTestCases()
	{
		return this.testCases;
	}
	
	public String getBuildName()
	{
		return this.buildName;
	}
	
	public void setBuildName(String buildName)
	{
		this.buildName = buildName;
	}
	
	public Integer getBuildId()
	{
		return this.buildId;
	}
	
	public void setBuildId(Integer buildId)
	{
		this.buildId = buildId;
	}

	@Override
	public String toString()
	{
		return "TestLinkReport [testsBlocked=" + testsBlocked
				+ ", testsFailed=" + testsFailed + ", testsTotal="
				+ testsTotal + ", testsPassed=" + testsPassed + "]";
	}
	
	public void addTestCase(TestLinkTestCase tc)
	{
		this.testCases.add( tc );
		String testResultStatus = tc.getResultStatus();
		if ( ! StringUtils.isEmpty(testResultStatus) )
		{
			if ( testResultStatus.equals(TestLinkAPIConst.TEST_BLOCKED) )
			{
				this.testsBlocked += 1;
			}
			else if ( testResultStatus.equals(TestLinkAPIConst.TEST_FAILED))
			{
				this.testsFailed += 1;
			} 
			else if ( testResultStatus.equals(TestLinkAPIConst.TEST_PASSED))
			{
				this.testsPassed += 1;
			} 
		}
	}
	
}
