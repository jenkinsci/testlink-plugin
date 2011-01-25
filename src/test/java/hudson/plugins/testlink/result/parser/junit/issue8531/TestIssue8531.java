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

import hudson.plugins.testlink.result.parser.junit.JUnitParser;
import hudson.plugins.testlink.result.parser.junit.TestCase;
import hudson.plugins.testlink.result.parser.junit.TestSuite;

import java.io.File;
import java.net.URL;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests the JUnit parser for issue 8531.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
public class TestIssue8531
{
	
	/**
	 * The JUnit parser.
	 */
	private JUnitParser parser;
	
	/**
	 * Initializes the JUnit parser.
	 */
	@BeforeClass
	public void setUp()
	{
		this.parser = new JUnitParser();
	}
	
	@Test(testName="Test JUnit Parser issue 8531")
	public void testJUnitParserIssue8531()
	{
		Assert.assertEquals(this.parser.getName(), "JUnit");
		
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
			Assert.fail("Failed to parse JUnit xml report '"+junitFile+"'.", e);
		}
		
		Assert.assertNotNull( testSuite );
		
		Assert.assertTrue( testSuite.getTestCases().size() == 2, "Invalid number of test cases." );
		
		Assert.assertTrue( testSuite.getTime().equals("63.296000") );
		
		Assert.assertTrue( testSuite.getFailures().equals("0"), "Invalid number of failures.");
		Assert.assertTrue( testSuite.getErrors().equals("0"), "Invalid number of errors.");
		
		TestCase testCase1 = testSuite.getTestCases().get(0);
		Assert.assertNotNull( testCase1 );
		Assert.assertTrue( testCase1.getClassName().equals("Navigation vers les massifs.Acceder par la liste") );
		Assert.assertTrue( testCase1.getName().equals("Acceder par la liste (outline example : | Massif du Mercantour | Massif du Mercantour |)") );
		Assert.assertTrue( testCase1.getTime().equals("31.781000"));
		
		TestCase testCase2 = testSuite.getTestCases().get(1);
		Assert.assertNotNull( testCase2 );
		Assert.assertTrue( testCase2.getClassName().equals("Navigation vers les massifs.Acceder par la carte") );
		Assert.assertTrue( testCase2.getName().equals("Acceder par la carte (outline example : | 3 | Massif du Mercantour |)") );
		Assert.assertTrue( testCase2.getTime().equals("31.515000"));
		
	}
	
}
