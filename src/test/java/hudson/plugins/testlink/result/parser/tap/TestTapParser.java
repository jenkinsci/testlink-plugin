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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;

import org.apache.commons.lang.NotImplementedException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import br.eti.kinoshita.tap4j.model.TestSet;
import br.eti.kinoshita.tap4j.parser.ParserException;
import br.eti.kinoshita.tap4j.util.StatusValues;

/**
 * Tests the TAP Parser.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.0
 */
public class TestTapParser
{
	
	/**
	 * The TAP Parser.
	 */
	private TAPParser parser;
	
	/**
	 * Initializes the TAP Parser.
	 */
	@BeforeClass
	public void setUp()
	{
		this.parser = new TAPParser();
	}
	
	@Test(testName="Test TAP Parser")
	public void testTapParser()
	{
		Assert.assertEquals(this.parser.getName(), "TAP");
		
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
			Assert.fail("Failed to parse TAP file '"+file+"'", pe);
		}
		
		Assert.assertNotNull( testSet, "Failed to parse TAP. Null TestSet." );
		Assert.assertTrue( testSet.getNumberOfTestResults() == 1, "Wrong number of test results in TAP file '"+file+"'." );
		Assert.assertTrue( testSet.getTestResult(1).getStatus()== StatusValues.OK , "Wrong status for test result 1");
		Assert.assertTrue( testSet.getTestResult(1).getDescription().equals("testOk") );
	}
	
	@Test(expectedExceptions=NotImplementedException.class)
	public void testCallingInvalidMethodInParser()
	{
		this.parser.parse(new ByteArrayInputStream(new byte[1024]));
	}
	
	@Test(expectedExceptions=ParserException.class)
	public void parseInvalidTapFile()
	{
		ClassLoader cl = TestTapParser.class.getClassLoader();
		URL url = cl.getResource("hudson/plugins/testlink/result/parser/tap/invalid.tap");
		File file = new File( url.getFile() );
		this.parser.parse( file );
	}
	
}
