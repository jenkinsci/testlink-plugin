/*
 * The MIT License
 *
 * Copyright (c) <2011> <Bruno P. Kinoshita>
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
package hudson.plugins.testlink.result.parser.junit.issue11442;

import hudson.plugins.testlink.parser.junit.JUnitParser;
import hudson.plugins.testlink.parser.junit.TestSuite;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.junit.Test;
import org.jvnet.hudson.test.Bug;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 0.1
 */
@Bug(value=11442)
public class TestIssue11442
extends junit.framework.TestCase 
{

	/**
	 * The JUnit parser.
	 */
	private JUnitParser parser;
	
	/**
	 * Initializes the JUnit parser.
	 */
	public void setUp()
	{
		this.parser = new JUnitParser();
	}
	
	@Test
	public void testDisabledSuite() 
	{
		ClassLoader cl = TestIssue11442.class.getClassLoader();
		URL url = cl.getResource("hudson/plugins/testlink/result/parser/junit/issue11442/TEST-test.TestMyName.xml");
		File junitFile = new File( url.getFile() );
		
		List<TestSuite> testSuites = null;
		try
		{
			testSuites = this.parser.parse( junitFile );
		} 
		catch (Exception e)
		{
			fail("Failed to parse JUnit xml report '"+junitFile+"': " + e.getMessage());
		}
		
		assertTrue(testSuites.get(0).isDisabled());
	}
	
	@Test
	public void testDisabledTestCase() 
	{
		ClassLoader cl = TestIssue11442.class.getClassLoader();
		URL url = cl.getResource("hudson/plugins/testlink/result/parser/junit/issue11442/TEST-test.TestMyName.xml");
		File junitFile = new File( url.getFile() );
		
		List<TestSuite> testSuites = null;
		try
		{
			testSuites = this.parser.parse( junitFile );
		} 
		catch (Exception e)
		{
			fail("Failed to parse JUnit xml report '"+junitFile+"': " + e.getMessage());
		}
		
		assertTrue(testSuites.get(0).getTestCases().get(0).isSkipped());
	}
	
}
