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
import java.util.Collections;
import java.util.List;

import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;

/**
 * Build Report.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.0
 */
public class Report 
implements Serializable
{
	
	private static final long serialVersionUID = -7174933827533855528L;
	
	private final List<TestCaseWrapper<?>> testCases;
	private final Build build;
	
	private int passed;
	private int failed;
	private int blocked;
	private int notRun;
	
	public Report(Build build) 
	{
		super();
		this.build = build;
		this.testCases = new ArrayList<TestCaseWrapper<?>>();
	}
	
	public Build getBuild() 
	{
		return this.build;
	}
	
	public List<TestCaseWrapper<?>> getTestCases() 
	{
		return Collections.unmodifiableList(this.testCases);
	}
	
	/**
	 * Adds a Test Case into the list of automated Test Cases.
	 * 
	 * @param testCase the Test Case.
	 */
	public void addTestCase(final TestCaseWrapper<?> testCase)
	{
		final ExecutionStatus status = testCase.getExecutionStatus();
		if ( status == ExecutionStatus.PASSED )
		{
			this.passed += 1;
		}
		else if ( status == ExecutionStatus.FAILED ) 
		{
			this.failed += 1;
		}
		else if ( status == ExecutionStatus.BLOCKED )
		{
			this.blocked += 1;
		}
		else if ( status == ExecutionStatus.NOT_RUN )
		{
			this.notRun += 1;
		}
		this.testCases.add( testCase );
	}

	public int getTestsTotal() 
	{
		return this.testCases.size();
	}
	
	public int getTestsPassed()
	{
		return this.passed;
	}
	
	public int getTestsFailed()
	{
		return this.failed;
	}
	
	public int getTestsBlocked()
	{
		return this.blocked;
	}
	
	public int getTestsNotRun() 
	{
		return this.notRun;
	}
	
}
