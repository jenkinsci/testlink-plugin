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
 * @since 2.0
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
