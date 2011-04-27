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
package hudson.plugins.testlink.result.parser.junit.issue8531;

import hudson.plugins.testlink.parser.junit.JUnitParser;
import hudson.plugins.testlink.parser.junit.TestCase;
import hudson.plugins.testlink.parser.junit.TestSuite;

import java.io.File;
import java.net.URL;

import org.jvnet.hudson.test.Bug;

/**
 * Tests the JUnit parser for issue 8531.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
@Bug(8531)
public class TestIssue8531 
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
	
	public void testJUnitParserIssue8531()
	{
		assertEquals(this.parser.getName(), "JUnit");
		
		ClassLoader cl = TestIssue8531.class.getClassLoader();
		URL url = cl.getResource("hudson/plugins/testlink/result/parser/junit/issue8531/TEST-navigation_vers_les_massifs.xml");
		File junitFile = new File( url.getFile() );
		
		TestSuite testSuite = null;
		try
		{
			testSuite = this.parser.parse( junitFile );
		} 
		catch (Exception e)
		{
			fail("Failed to parse JUnit xml report '"+junitFile+"': " + e.getMessage());
		}
		
		assertNotNull( testSuite );
		
		assertTrue( "Invalid number of test cases.", testSuite.getTestCases().size() == 2 );
		
		assertTrue( testSuite.getTime().equals("63.296000") );
		
		assertTrue( "Invalid number of failures.", testSuite.getFailures().equals(0L) );
		assertTrue( "Invalid number of errors.", testSuite.getErrors().equals(0L));
		
		TestCase testCase1 = testSuite.getTestCases().get(0);
		assertNotNull( testCase1 );
		assertTrue( testCase1.getClassName().equals("Navigation vers les massifs.Acceder par la liste") );
		assertTrue( testCase1.getName().equals("Acceder par la liste (outline example : | Massif du Mercantour | Massif du Mercantour |)") );
		assertTrue( testCase1.getTime().equals("31.781000"));
		
		TestCase testCase2 = testSuite.getTestCases().get(1);
		assertNotNull( testCase2 );
		assertTrue( testCase2.getClassName().equals("Navigation vers les massifs.Acceder par la carte") );
		assertTrue( testCase2.getName().equals("Acceder par la carte (outline example : | 3 | Massif du Mercantour |)") );
		assertTrue( testCase2.getTime().equals("31.515000"));
		
	}
	
}
