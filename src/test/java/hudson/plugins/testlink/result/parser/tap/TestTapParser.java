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
package hudson.plugins.testlink.result.parser.tap;

import hudson.plugins.testlink.parser.ParserException;
import hudson.plugins.testlink.parser.tap.TAPParser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.commons.lang.NotImplementedException;
import org.tap4j.model.TestSet;
import org.tap4j.util.StatusValues;

/**
 * Tests the TAP Parser.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.0
 */
public class TestTapParser 
extends TestCase
{
	
	/**
	 * The TAP Parser.
	 */
	private TAPParser parser;
	
	/**
	 * Initializes the TAP Parser.
	 */
	public void setUp()
	{
		this.parser = new TAPParser();
	}
	
	public void testTapParser()
	{
		assertEquals(this.parser.getName(), "TAP");
		
		ClassLoader cl = TestTapParser.class.getClassLoader();
		URL url = cl.getResource("hudson/plugins/testlink/result/parser/tap/br.eti.kinoshita.tap.SampleTest.tap");
		File file = new File( url.getFile() );
		
		TestSet testSet = null;
		
		try
		{
			testSet = this.parser.parse( file );
		}
		catch (ParserException pe)
		{
			fail("Failed to parse TAP file '"+file+"': " + pe.getMessage());
		}
		
		assertNotNull( "Failed to parse TAP. Null TestSet.", testSet );
		assertTrue( "Wrong number of test results in TAP file '"+file+"'.", testSet.getNumberOfTestResults() == 1 );
		assertTrue( "Wrong status for test result 1", testSet.getTestResult(1).getStatus()== StatusValues.OK );
		assertTrue( testSet.getTestResult(1).getDescription().equals("testOk") );
	}
	
	public void testCallingInvalidMethodInParser()
	{
		try
		{
			this.parser.parse(new ByteArrayInputStream(new byte[1024]));
		}
		catch (NotImplementedException p) 
		{
			assertNotNull(p);
		}
	}
		
	
	public void parseInvalidTapFile()
	{
		ClassLoader cl = TestTapParser.class.getClassLoader();
		URL url = cl.getResource("hudson/plugins/testlink/result/parser/tap/invalid.tap");
		File file = new File( url.getFile() );
		
		try
		{
			this.parser.parse( file );
		}
		catch (ParserException p) 
		{
			assertNotNull(p);
		}
		
	}
	
}
