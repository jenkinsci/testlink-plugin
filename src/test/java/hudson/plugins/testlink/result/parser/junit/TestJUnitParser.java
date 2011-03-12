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

import java.io.File;
import java.net.URL;

import br.eti.kinoshita.tap4j.parser.ParserException;

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
		assertEquals(this.parser.getName(), "JUnit");
		
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
			fail("Failed to parse JUnit xml report '"+junitFile+"': " + e.getMessage());
		}
		
		assertNotNull( testSuite );
		assertTrue( testSuite.getTestCases().size() == 1 );
		
		assertTrue( testSuite.getFailures().equals("1"));
		
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
			fail("Failed to parse JUnit xml report '"+junitFile+"': " + e.getMessage());
		}
		
		assertNotNull( testSuite );
		assertTrue( testSuite.getTestCases().size() == 1 );
		
		assertTrue( testSuite.getFailures().equals("1"));
		assertTrue( testSuite.getTimestamp().equals("2007-11-02T23:13:50") );
		
		assertTrue( testSuite.getHostname().equals("hazelnut.osuosl.org"));
		TestCase t1 = testSuite.getTestCases().get(0);
		Failure failure = t1.getFailures().get(0);
		
		assertNotNull( failure );
		assertTrue( failure.getMessage().equals("Mix should be exactly 25. expected:<25> but was:<20>"));
		assertTrue( failure.getType().equals("junit.framework.AssertionFailedError") );
		assertTrue( failure.getText().equals("junit.framework.AssertionFailedError: Mix should be exactly 25. expected:<25> but was:<20>\n\tat net.cars.engine.CarburateurTest.mix(CarburateurTest.java:34)\n"));
		
		assertTrue( t1.removeFailure( failure ) );
		assertTrue( t1.getFailures().size() == 0 );
		
		assertTrue( testSuite.removeTestCase(t1) );
		
		assertTrue( testSuite.getTestCases().size() == 0 );
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
			fail("Failed to parse JUnit xml report '"+junitFile+"': " + e.getMessage());
		}
		
		assertNotNull( testSuite );
		assertTrue( testSuite.getTestCases().size() == 1 );
		
		assertTrue( testSuite.getFailures().equals("0"));
		
		assertTrue( testSuite.getHostname().equals("hazelnut.osuosl.org"));
		
		String systemOut = testSuite.getSystemOut();
		assertNotNull( systemOut );
		assertTrue( systemOut.equals("Rotation is simulated for a four spark engine with an angle of 0?.\n"));
		
		String systemErr = testSuite.getSystemErr();
		assertNotNull( systemErr );
		assertTrue( systemErr.equals("BrunoPKinoshita"));
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
			fail("Failed to parse JUnit xml report '"+junitFile+"': "  + e.getMessage());
		}
		
		assertNotNull( testSuite );
		assertTrue( testSuite.getTestCases().size() == 5 );
		
		assertTrue( testSuite.getFailures().equals("3"));
		assertTrue( testSuite.getErrors().equals("1"));
		assertTrue( testSuite.getHostname().equals("hazelnut.osuosl.org"));
		assertTrue( testSuite.getName().equals("net.cars.engine.PistonTest"));
		assertEquals( ""+testSuite.getTestCases().size(), testSuite.getTests() );
		
		String systemOut = testSuite.getSystemOut();
		assertNotNull( systemOut );
		
		TestCase t1 = testSuite.getTestCases().get(0);
		Error error = t1.getErrors().get(0);
		assertNotNull( error );
		
		assertTrue( error.getMessage().equals("test timed out after 1 milliseconds") );
		assertTrue( error.getText().equals("java.lang.Exception: test timed out after 1 milliseconds\n") );
		assertTrue( error.getType().equals("java.lang.Exception") );
		
		assertTrue( t1.removeError( error ) );
		assertTrue( t1.getErrors().size() == 0 );
		
	}
	
	public void testInvalidJUnitReport()
	{
		ClassLoader cl = TestJUnitParser.class.getClassLoader();
		URL url = cl.getResource("hudson/plugins/testlink/result/parser/junit/TEST-invalid.xml");
		File junitFile = new File( url.getFile() );
		
		try
		{
			this.parser.parse( junitFile );
		}
		catch (ParserException p) 
		{
			assertNotNull(p);
		}	
	}
	
}
