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
 * @since 23/11/2010
 */
package hudson.plugins.testlink.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.model.TestProject;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 23/11/2010
 */
public class TestLinkReport 
implements Serializable
{
	
	private Build build;
	private TestPlan testPlan;
	private TestProject testProject;
	
	private List<TestCase> testCases;
	
	public TestLinkReport()
	{
		this.testCases = new ArrayList<TestCase>();
	}

	public TestLinkReport(Build build, TestPlan testPlan,
			TestProject testProject) {
		super();
		this.build = build;
		this.testPlan = testPlan;
		this.testProject = testProject;
		
		this.testCases = new ArrayList<TestCase>();
	}
	
	public Build getBuild() {
		return build;
	}

	public void setBuild(Build build) {
		this.build = build;
	}

	public TestPlan getTestPlan() {
		return testPlan;
	}

	public void setTestPlan(TestPlan testPlan) {
		this.testPlan = testPlan;
	}

	public TestProject getTestProject() {
		return testProject;
	}

	public void setTestProject(TestProject testProject) {
		this.testProject = testProject;
	}

	public List<TestCase> getTestCases() {
		return testCases;
	}

	/**
	 * @return
	 */
	public Integer getTestsTotal() 
	{
		return this.testCases.size();
	}

	/**
	 * @return
	 */
	public Integer getTestsPassed() 
	{
		int totalPassed = 0;
		for(TestCase testCase : this.testCases )
		{
			if ( testCase.getExecutionStatus() == ExecutionStatus.PASSED )
			{
				totalPassed = totalPassed + 1;
			}
		}
		return totalPassed;
	}

	/**
	 * @return
	 */
	public Integer getTestsFailed() 
	{
		int totalFailed = 0;
		for(TestCase testCase : this.testCases )
		{
			if ( testCase.getExecutionStatus() == ExecutionStatus.FAILED )
			{
				totalFailed = totalFailed + 1;
			}
		}
		return totalFailed;
	}

	/**
	 * @return
	 */
	public Integer getTestsBlocked() 
	{
		int totalBlocked = 0;
		for(TestCase testCase : this.testCases )
		{
			if ( testCase.getExecutionStatus() == ExecutionStatus.BLOCKED )
			{
				totalBlocked = totalBlocked + 1;
			}
		}
		return totalBlocked;
	}

	@Override
	public String toString()
	{
		return "TestLinkReport [build=" + build + ", testPlan=" + testPlan
				+ ", testProject=" + testProject + ", testCases=" + testCases
				+ "]";
	}	
	
}
