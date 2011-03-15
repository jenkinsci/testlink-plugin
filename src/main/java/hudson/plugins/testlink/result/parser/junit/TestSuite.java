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
package hudson.plugins.testlink.result.parser.junit;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a <testsuite> tag.
 *
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
public class TestSuite 
implements Serializable
{

	private static final long serialVersionUID = 1L;

	/**
	 * The name attribute.
	 */
	private String name;
	
	/**
	 * The time attribute.
	 */
	private String time;
	
	/**
	 * The tests attribute.
	 */
	private String tests;
	
	/**
	 * The errors attribute.
	 */
	private Long errors;
	
	/**
	 * The failures attribute.
	 */
	private Long failures;
	
	/**
	 * The hostname attribute.
	 */
	private String hostname;
	
	/**
	 * The timestamp attribute.
	 */
	private String timestamp;
	
	/**
	 * The list of <testcase> tags. 
	 */
	private List<TestCase> testCases;

	/**
	 * The <system-out> text.
	 */
	private String systemOut;
	
	/**
	 * The <system-err> text.
	 */
	private String systemErr;
	
	/**
	 * Default constructor. Initializes the list of <testcase> tags.
	 */
	public TestSuite() 
	{
		super();
		
		testCases = new LinkedList<TestCase>();
	}

	/**
	 * Retrieves the name.
	 * 
	 * @return the name.
	 */
	public String getName() 
	{
		return name;
	}

	/**
	 * Sets the name attribute.
	 * 
	 * @param name the name.
	 */
	public void setName( String name ) 
	{
		this.name = name;
	}

	/**
	 * Retrives the time.
	 * 
	 * @return the time.
	 */
	public String getTime() 
	{
		return time;
	}

	/**
	 * Sets the time.
	 * 
	 * @param time the time.
	 */
	public void setTime( String time ) 
	{
		this.time = time;
	}

	/**
	 * Retrives the tests.
	 * 
	 * @return the tests.
	 */
	public String getTests() 
	{
		return tests;
	}

	/**
	 * Sets the tests.
	 * 
	 * @param tests the tests.
	 */
	public void setTests( String tests ) 
	{
		this.tests = tests;
	}

	/**
	 * Retrieves the errors.
	 * 
	 * @return the errors.
	 */
	public Long getErrors() 
	{
		return errors;
	}

	/**
	 * Sets the errors.
	 * 
	 * @param errors the errors.
	 */
	public void setErrors( Long errors ) 
	{
		this.errors = errors;
	}

	/**
	 * Retrieves the failures. 
	 * 
	 * @return the failures.
	 */
	public Long getFailures() 
	{
		return failures;
	}

	/**
	 * Sets the failures.
	 * 
	 * @param failures the failures.
	 */
	public void setFailures( Long failures ) 
	{
		this.failures = failures;
	}

	/**
	 * Retrieves the hostname.
	 * 
	 * @return the hostname.
	 */
	public String getHostname() 
	{
		return hostname;
	}

	/**
	 * Sets the hostname.
	 * 
	 * @param hostname the hostname.
	 */
	public void setHostname( String hostname ) 
	{
		this.hostname = hostname;
	}

	/**
	 * Retrieves the timestamp.
	 * 
	 * @return the timestamp.
	 */
	public String getTimestamp() 
	{
		return timestamp;
	}

	/**
	 * Sets the timestamp.
	 * 
	 * @param timestamp the timestamp.
	 */
	public void setTimestamp( String timestamp ) 
	{
		this.timestamp = timestamp;
	}
	
	/**
	 * Retrieves the systemout.
	 * 
	 * @return the systemout.
	 */
	public String getSystemOut() 
	{
		return systemOut;
	}

	/**
	 * Sets the systemout.
	 * 
	 * @param systemOut the systemout.
	 */
	public void setSystemOut( String systemOut ) 
	{
		this.systemOut = systemOut;
	}

	/**
	 * Retrieves the systemerr.
	 * 
	 * @return the systemerr.
	 */
	public String getSystemErr() 
	{
		return systemErr;
	}

	/**
	 * Sets the systemerr.
	 * 
	 * @param systemErr the systemerr.
	 */
	public void setSystemErr( String systemErr ) 
	{
		this.systemErr = systemErr;
	}

	/**
	 * Retrieves the list of <testcase> tags.
	 * 
	 * @return the list of <testcase> tags.
	 */
	public List<TestCase> getTestCases()
	{
		return this.testCases;
	}
	
	/**
	 * Adds a test case into the <testcase> list.
	 * 
	 * @param testCase the test case.
	 * @return true if added successfully, otherwise false.
	 */
	public boolean addTestCase( TestCase testCase )
	{
		return this.testCases.add( testCase );
	}
	
	/**
	 * Removes a test case from the list of <testcase> tags.
	 * 
	 * @param testCase the test case.
	 * @return true if added successfully, otherwise false.
	 */
	public boolean removeTestCase( TestCase testCase )
	{
		return this.testCases.remove( testCase );
	}
	
}
