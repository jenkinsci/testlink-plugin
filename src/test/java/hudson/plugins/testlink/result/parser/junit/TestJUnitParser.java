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

import hudson.plugins.testlink.parser.ParserException;
import hudson.plugins.testlink.parser.junit.Error;
import hudson.plugins.testlink.parser.junit.Failure;
import hudson.plugins.testlink.parser.junit.JUnitParser;
import hudson.plugins.testlink.parser.junit.TestCase;
import hudson.plugins.testlink.parser.junit.TestSuite;

import java.io.File;
import java.net.URL;

import junit.framework.Assert;

/**
 * Tests the JUnit parser.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.0
 */
public class TestJUnitParser 
extends junit.framework.TestCase
{
	private JUnitParser parser;
	
	/**
	 * Initializes the JUnit parser.
	 */
	public void setUp()
	{
		this.parser = new JUnitParser();
	}
	
	public void testJUnitParser()
	{
		Assert.assertEquals(this.parser.getName(), "JUnit");
		
		ClassLoader cl = TestJUnitParser.class.getClassLoader();
		URL url = cl.getResource("hudson/plugins/testlink/result/parser/junit/TEST-br.eti.kinoshita.Test.xml");
		File junitFile = new File( url.getFile() );
		
		TestSuite testSuite = null;
		try
		{
			testSuite = this.parser.parse( junitFile );
		} 
		catch (Exception e)
		{
			e.printStackTrace( System.err );
			Assert.fail("Failed to parse JUnit xml report '"+junitFile+"'.");
		}
		
		Assert.assertNotNull( testSuite );
		Assert.assertTrue( testSuite.getTestCases().size() == 1 );
		
		Assert.assertTrue( testSuite.getFailures().equals(1L));
		
	}
	
	public void testJunitCarburateurXml()
	{
		ClassLoader cl = TestJUnitParser.class.getClassLoader();
		URL url = cl.getResource("hudson/plugins/testlink/result/parser/junit/TEST-net.cars.engine.CarburateurTest.xml");
		File junitFile = new File( url.getFile() );
		
		TestSuite testSuite = null;
		try
		{
			testSuite = this.parser.parse( junitFile );
		} 
		catch (Exception e)
		{
			Assert.fail("Failed to parse JUnit xml report '"+junitFile+"'.");
		}
		
		Assert.assertNotNull( testSuite );
		Assert.assertTrue( testSuite.getTestCases().size() == 1 );
		
		Assert.assertTrue( testSuite.getFailures().equals( 1L ));
		Assert.assertTrue( testSuite.getTimestamp().equals("2007-11-02T23:13:50") );
		
		Assert.assertTrue( testSuite.getHostname().equals("hazelnut.osuosl.org"));
		TestCase t1 = testSuite.getTestCases().get(0);
		Failure failure = t1.getFailures().get(0);
		
		Assert.assertNotNull( failure );
		Assert.assertTrue( failure.getMessage().equals("Mix should be exactly 25. expected:<25> but was:<20>"));
		Assert.assertTrue( failure.getType().equals("junit.framework.AssertionFailedError") );
		Assert.assertTrue( failure.getText().equals("junit.framework.AssertionFailedError: Mix should be exactly 25. expected:<25> but was:<20>\n\tat net.cars.engine.CarburateurTest.mix(CarburateurTest.java:34)\n"));
		
		Assert.assertTrue( t1.removeFailure( failure ) );
		Assert.assertTrue( t1.getFailures().size() == 0 );
		
		Assert.assertTrue( testSuite.removeTestCase(t1) );
		
		Assert.assertTrue( testSuite.getTestCases().size() == 0 );
	}
	
	public void testJunitDelcoXml()
	{
		ClassLoader cl = TestJUnitParser.class.getClassLoader();
		URL url = cl.getResource("hudson/plugins/testlink/result/parser/junit/TEST-net.cars.engine.DelcoTest.xml");
		File junitFile = new File( url.getFile() );
		
		TestSuite testSuite = null;
		try
		{
			testSuite = this.parser.parse( junitFile );
		} 
		catch (Exception e)
		{
			Assert.fail("Failed to parse JUnit xml report '"+junitFile+"'.");
		}
		
		Assert.assertNotNull( testSuite );
		Assert.assertTrue( testSuite.getTestCases().size() == 1 );
		
		Assert.assertTrue( testSuite.getFailures().equals( 0L ));
		
		Assert.assertTrue( testSuite.getHostname().equals("hazelnut.osuosl.org"));
		
		String systemOut = testSuite.getSystemOut();
		Assert.assertNotNull( systemOut );
		Assert.assertTrue( systemOut.equals("Rotation is simulated for a four spark engine with an angle of 0?.\n"));
		
		String systemErr = testSuite.getSystemErr();
		Assert.assertNotNull( systemErr );
		Assert.assertTrue( systemErr.equals("BrunoPKinoshita"));
	}
	
	public void testJunitPistonXml()
	{
		ClassLoader cl = TestJUnitParser.class.getClassLoader();
		URL url = cl.getResource("hudson/plugins/testlink/result/parser/junit/TEST-net.cars.engine.PistonTest.xml");
		File junitFile = new File( url.getFile() );
		
		TestSuite testSuite = null;
		try
		{
			testSuite = this.parser.parse( junitFile );
		} 
		catch (Exception e)
		{
			Assert.fail("Failed to parse JUnit xml report '"+junitFile+"'.");
		}
		
		Assert.assertNotNull( testSuite );
		Assert.assertTrue( testSuite.getTestCases().size() == 5 );
		
		Assert.assertTrue( testSuite.getFailures().equals( 3L ));
		Assert.assertTrue( testSuite.getErrors().equals( 1L ));
		Assert.assertTrue( testSuite.getHostname().equals("hazelnut.osuosl.org"));
		Assert.assertTrue( testSuite.getName().equals("net.cars.engine.PistonTest"));
		Assert.assertEquals( ""+testSuite.getTestCases().size(), testSuite.getTests() );
		
		String systemOut = testSuite.getSystemOut();
		Assert.assertNotNull( systemOut );
		
		TestCase t1 = testSuite.getTestCases().get(0);
		Error error = t1.getErrors().get(0);
		Assert.assertNotNull( error );
		
		Assert.assertTrue( error.getMessage().equals("test timed out after 1 milliseconds") );
		Assert.assertTrue( error.getText().equals("java.lang.Exception: test timed out after 1 milliseconds\n") );
		Assert.assertTrue( error.getType().equals("java.lang.Exception") );
		
		Assert.assertTrue( t1.removeError( error ) );
		Assert.assertTrue( t1.getErrors().size() == 0 );
		
	}
	
	public void testInvalidJUnitReport()
	{
		ClassLoader cl = TestJUnitParser.class.getClassLoader();
		URL url = cl.getResource("hudson/plugins/testlink/result/parser/junit/TEST-invalid.xml");
		File junitFile = new File( url.getFile() );
		
		try
		{
			this.parser.parse( junitFile );
			Assert.fail( "Not supposed to get here." );
		}
		catch ( ParserException e )
		{
			
		}
	}
	
}
