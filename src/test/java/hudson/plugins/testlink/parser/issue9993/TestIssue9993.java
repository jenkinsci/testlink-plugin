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
package hudson.plugins.testlink.parser.issue9993;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import org.junit.Test;
import org.jvnet.hudson.test.Issue;

import hudson.tasks.junit.SuiteResult;
import hudson.tasks.junit.TestResult;

/**
 * Tests the JUnit parser for issue 9993.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.5
 */
@Issue("9993")
public class TestIssue9993 
{
	
    @Test
	public void testParsingNumberOfResultsAndContents() throws IOException
	{
		ClassLoader cl = TestIssue9993.class.getClassLoader();
		URL url = cl.getResource("hudson/plugins/testlink/parser/issue9993/MST_DEMO_LOG_01_junit.xml");
		File junitFile = new File( url.getFile() );
		TestResult testResult = new TestResult();
		testResult.parse(junitFile);
		
		assertEquals( testResult.getSuites().size(), 1 );
		
		SuiteResult testSuite = testResult.getSuites().iterator().next();
		
		assertEquals( testSuite.getCases().size(), 3 );
		
		assertEquals( testSuite.getCases().get( 0 ).getName(), "UnitTest.Startup");
		
	}
	
	/**
	 * This tests a modified version of the XML result file.
	 */
    @Test
	public void testParsingNumberOfResultsAndContentsModified() throws IOException
	{
		ClassLoader cl = TestIssue9993.class.getClassLoader();
		URL url = cl.getResource("hudson/plugins/testlink/parser/issue9993/MST_DEMO_LOG_01_junit-modified.xml");
		File junitFile = new File( url.getFile() );
		TestResult testResult = new TestResult();
		testResult.parse(junitFile);
		
		assertEquals( testResult.getSuites().size(), 2 );
		
		Iterator<SuiteResult> suitesIterator = testResult.getSuites().iterator();
		
		SuiteResult testSuite = suitesIterator.next();
		
		assertEquals( testSuite.getCases().size(), 3 );
		
		assertEquals( testSuite.getCases().get( 0 ).getName(), "UnitTest.Startup");
		
		testSuite = suitesIterator.next();
		
		assertEquals( testSuite.getCases().size(), 3 );
		
		assertEquals( testSuite.getCases().get( 0 ).getName(), "A");
		assertEquals( testSuite.getCases().get( 0 ).getClassName(), "com.test.qa.A");
		
	}

}
