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
 * @since 02/12/2010
 */
package hudson.plugins.testlink.model;

import hudson.plugins.testlink.model.TestLinkReport;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.model.TestProject;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 02/12/2010
 */
public class TestTestLinkReport
{

	private TestLinkReport report;
	
	private Build build;
	private TestPlan plan;
	private TestProject project;
	
	@BeforeClass
	public void setUp()
	{
		this.report = new TestLinkReport();
		build = new Build();
		build.setId(100);
		plan = new TestPlan();
		plan.setId(100);
		project = new TestProject();
		project.setId( 100 );
		
		Assert.assertNotNull ( this.report.getTestCases() );
		Assert.assertTrue( this.report.getTestCases().size() == 0 );
	}
	
	@Test(testName="Test Getters and Setters")
	public void testTesLinkReportGettersAndSetters()
	{
		Assert.assertNull ( this.report.getBuild() );
		this.report.setBuild ( this.build );
		Assert.assertNotNull( this.report.getBuild() );
		Assert.assertTrue( this.report.getBuild().getId() == 100 );
		
		Assert.assertNull( this.report.getTestPlan());
		this.report.setTestPlan( this.plan );
		Assert.assertNotNull( this.report.getTestPlan() );
		Assert.assertTrue( this.report.getTestPlan().getId() == 100 );
		
		Assert.assertNull( this.report.getTestProject() );
		this.report.setTestProject( this.project );
		Assert.assertNotNull( this.report.getTestProject() );
		Assert.assertTrue( this.report.getTestProject().getId() == 100 );
	}
	
	@Test(testName ="Test Report numbers")
	public void testReportNumbers()
	{
		Assert.assertTrue( report.getTestsBlocked() == 0 );
		Assert.assertTrue( report.getTestsFailed() == 0 );
		Assert.assertTrue( report.getTestsPassed() == 0 );
		Assert.assertTrue( report.getTestsTotal() == 0 );
		
		TestCase testCase = new TestCase();
		testCase.setExecutionStatus( ExecutionStatus.BLOCKED );
		report.getTestCases().add( testCase );
		
		Assert.assertTrue( report.getTestsBlocked() == 1 );
		Assert.assertTrue( report.getTestsFailed() == 0 );
		Assert.assertTrue( report.getTestsPassed() == 0 );
		Assert.assertTrue( report.getTestsTotal() == 1 );
		
		testCase = new TestCase();
		testCase.setExecutionStatus( ExecutionStatus.FAILED );
		report.getTestCases().add( testCase );
		
		Assert.assertTrue( report.getTestsBlocked() == 1 );
		Assert.assertTrue( report.getTestsFailed() == 1 );
		Assert.assertTrue( report.getTestsPassed() == 0 );
		Assert.assertTrue( report.getTestsTotal() == 2 );
		
		testCase = new TestCase();
		testCase.setExecutionStatus( ExecutionStatus.PASSED );
		report.getTestCases().add( testCase );
		
		Assert.assertTrue( report.getTestsBlocked() == 1 );
		Assert.assertTrue( report.getTestsFailed() == 1 );
		Assert.assertTrue( report.getTestsPassed() == 1 );
		Assert.assertTrue( report.getTestsTotal() == 3 );
		
		testCase = new TestCase();
		testCase.setExecutionStatus( ExecutionStatus.NOT_RUN);
		report.getTestCases().add( testCase );
		
		Assert.assertTrue( report.getTestsBlocked() == 1 );
		Assert.assertTrue( report.getTestsFailed() == 1 );
		Assert.assertTrue( report.getTestsPassed() == 1 );
		Assert.assertTrue( report.getTestsTotal() == 4 );
		
		testCase = new TestCase();
		testCase.setExecutionStatus( ExecutionStatus.PASSED);
		report.getTestCases().add( testCase );
		
		Assert.assertTrue( report.getTestsBlocked() == 1 );
		Assert.assertTrue( report.getTestsFailed() == 1 );
		Assert.assertTrue( report.getTestsPassed() == 2 );
		Assert.assertTrue( report.getTestsTotal() == 5 );
	}
	
	@Test(testName="Test toString()")
	public void testToString()
	{
		Assert.assertTrue( report.toString().startsWith("TestLinkReport [build=") );
	}
	
	@AfterClass
	public void tearDown()
	{
		this.report = new TestLinkReport(build, plan, project);
		Assert.assertNotNull( report.getBuild() );
		Assert.assertNotNull( report.getTestPlan() );
		Assert.assertNotNull( report.getTestProject() );
	}
}
