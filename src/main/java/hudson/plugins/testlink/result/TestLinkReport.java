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
 * @since 2.0
 */
public class TestLinkReport 
implements Serializable
{
	
	private static final long serialVersionUID = -8188069543814952158L;
	
	private Build build;
	private TestPlan testPlan;
	private TestProject testProject;
	
	private List<TestCase> testCases;
	
	public TestLinkReport()
	{
		this.testCases = new ArrayList<TestCase>();
	}

	public TestLinkReport(Build build, TestPlan testPlan,
			TestProject testProject) 
	{
		super();
		this.build = build;
		this.testPlan = testPlan;
		this.testProject = testProject;
		
		this.testCases = new ArrayList<TestCase>();
	}
	
	public Build getBuild() 
	{
		return build;
	}

	public void setBuild(Build build) 
	{
		this.build = build;
	}

	public TestPlan getTestPlan() 
	{
		return testPlan;
	}

	public void setTestPlan(TestPlan testPlan) 
	{
		this.testPlan = testPlan;
	}

	public TestProject getTestProject()
	{
		return testProject;
	}

	public void setTestProject(TestProject testProject) 
	{
		this.testProject = testProject;
	}

	public List<TestCase> getTestCases()
	{
		return testCases;
	}

	/**
	 * @return number of total tests
	 */
	public Integer getTestsTotal() 
	{
		return this.testCases.size();
	}
	
	/**
	 * Adds a Test Case into the list of automated Test Cases.
	 * 
	 * @param testCase the Test Case.
	 * @return true if added successfully, otherwise false.
	 */
	public boolean addTestCase(TestCase testCase)
	{
		return this.testCases.add( testCase );
	}

	/**
	 * @return number of passed tests.
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
	 * @return number of failed tests.
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
	 * @return number of blocked tests.
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
