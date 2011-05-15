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
package hudson.plugins.testlink.result;

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
extends junit.framework.TestCase
{

	private TestLinkReport report;
	
	private Build build;
	private TestPlan plan;
	private TestProject project;
	
	public void setUp()
	{
		this.report = new TestLinkReport();
		build = new Build();
		build.setId(100);
		plan = new TestPlan();
		plan.setId(100);
		project = new TestProject();
		project.setId( 100 );
		
		assertNotNull ( this.report.getTestCases() );
		assertTrue( this.report.getTestCases().size() == 0 );
	}
	
	public void testTesLinkReportGettersAndSetters()
	{
		assertNull ( this.report.getBuild() );
		this.report.setBuild ( this.build );
		assertNotNull( this.report.getBuild() );
		assertTrue( this.report.getBuild().getId() == 100 );
		
		assertNull( this.report.getTestPlan());
		this.report.setTestPlan( this.plan );
		assertNotNull( this.report.getTestPlan() );
		assertTrue( this.report.getTestPlan().getId() == 100 );
		
		assertNull( this.report.getTestProject() );
		this.report.setTestProject( this.project );
		assertNotNull( this.report.getTestProject() );
		assertTrue( this.report.getTestProject().getId() == 100 );
	}
	
	public void testReportNumbers()
	{
		assertTrue( report.getTestsBlocked() == 0 );
		assertTrue( report.getTestsFailed() == 0 );
		assertTrue( report.getTestsPassed() == 0 );
		assertTrue( report.getTestsTotal() == 0 );
		
		TestCase testCase = new TestCase();
		testCase.setId( 1 );
		testCase.setExecutionStatus( ExecutionStatus.BLOCKED );
		report.getTestCases().put( testCase.getId(), testCase );
		
		assertTrue( report.getTestsBlocked() == 1 );
		assertTrue( report.getTestsFailed() == 0 );
		assertTrue( report.getTestsPassed() == 0 );
		assertTrue( report.getTestsTotal() == 1 );
		
		testCase = new TestCase();
		testCase.setId( 2 );
		testCase.setExecutionStatus( ExecutionStatus.FAILED );
		report.getTestCases().put( testCase.getId(), testCase );
		
		assertTrue( report.getTestsBlocked() == 1 );
		assertTrue( report.getTestsFailed() == 1 );
		assertTrue( report.getTestsPassed() == 0 );
		assertTrue( report.getTestsTotal() == 2 );
		
		testCase = new TestCase();
		testCase.setId( 3 );
		testCase.setExecutionStatus( ExecutionStatus.PASSED );
		report.getTestCases().put( testCase.getId(), testCase );
		
		assertTrue( report.getTestsBlocked() == 1 );
		assertTrue( report.getTestsFailed() == 1 );
		assertTrue( report.getTestsPassed() == 1 );
		assertTrue( report.getTestsTotal() == 3 );
		
		testCase = new TestCase();
		testCase.setId( 4 );
		testCase.setExecutionStatus( ExecutionStatus.NOT_RUN);
		report.getTestCases().put( testCase.getId(), testCase );
		
		assertTrue( report.getTestsBlocked() == 1 );
		assertTrue( report.getTestsFailed() == 1 );
		assertTrue( report.getTestsPassed() == 1 );
		assertTrue( report.getTestsTotal() == 4 );
		
		testCase = new TestCase();
		testCase.setId( 5 );
		testCase.setExecutionStatus( ExecutionStatus.PASSED);
		report.getTestCases().put( testCase.getId(), testCase );
		
		assertTrue( report.getTestsBlocked() == 1 );
		assertTrue( report.getTestsFailed() == 1 );
		assertTrue( report.getTestsPassed() == 2 );
		assertTrue( report.getTestsTotal() == 5 );
	}
	
	public void testToString()
	{
		assertTrue( report.toString().startsWith("TestLinkReport [build=") );
	}
	
	public void tearDown()
	{
		this.report = new TestLinkReport(build, plan, project);
		assertNotNull( report.getBuild() );
		assertNotNull( report.getTestPlan() );
		assertNotNull( report.getTestProject() );
	}
}
