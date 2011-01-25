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
package hudson.plugins.testlink.result.parser.testng;

import hudson.plugins.testlink.result.parser.testng.Class;
import hudson.plugins.testlink.result.parser.testng.Suite;
import hudson.plugins.testlink.result.parser.testng.Test;
import hudson.plugins.testlink.result.parser.testng.TestMethod;
import hudson.plugins.testlink.result.parser.testng.TestNGParser;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;

import br.eti.kinoshita.tap4j.parser.ParserException;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.0
 */
public class TestTestNGParser
{
	
	/**
	 * The TestNG parser.
	 */
	private TestNGParser parser;
	
	/**
	 * Initializes the TestNG parser.
	 */
	@BeforeClass
	public void setUp()
	{
		this.parser = new TestNGParser();
	}
	
	@org.testng.annotations.Test(testName="Test TestNG Parser")
	public void testTapParser()
	{
		Assert.assertEquals(this.parser.getName(), "TestNG");
		
		ClassLoader cl = TestTestNGParser.class.getClassLoader();
		URL url = cl.getResource("hudson/plugins/testlink/parser/testng/testng-results.xml");
		File file = new File( url.getFile() );
		
		Suite suite = null;
		try
		{
			suite = this.parser.parse( file );
		}
		catch (ParserException e)
		{
			Assert.fail("Failed to parse testng file '"+file+"'.", e);
		}
		
		Assert.assertNotNull( suite );
		
		Assert.assertTrue( suite.getName().equals("Command line suite") );
		Assert.assertTrue( suite.getDurationMs().equals("0") );
		Assert.assertTrue( suite.getStartedAt().equals("2010-11-17T13:31:41Z") );
		Assert.assertTrue( suite.getFinishedAt().equals("2010-11-17T13:31:41Z") );
		
		List<Test> tests = suite.getTests();
		Assert.assertEquals( tests.size(), 1 );
		
		Test test = tests.get(0);
		Assert.assertTrue( test.getDurationMs().equals("0") );
		Assert.assertTrue( test.getStartedAt().equals("2010-11-17T13:31:41Z") );
		Assert.assertTrue( test.getFinishedAt().equals("2010-11-17T13:31:41Z") );
		Assert.assertTrue( test.getName().equals("Command line test") );
		
		List<Class> classes = test.getClasses();
		Assert.assertTrue( classes.size() == 1 );
		
		Class clazz = classes.get( 0 );
		Assert.assertNotNull( clazz );
		
		Assert.assertTrue( clazz.getName().equals("br.eti.kinoshita.Test1") );
		
		List<TestMethod> testMethods = clazz.getTestMethods();
		
		Assert.assertTrue( testMethods.size() == 1 );
		
		TestMethod testMethod = testMethods.get( 0 );
		
		Assert.assertNotNull( testMethod );
		
		Assert.assertTrue( testMethod.getStatus().equals("PASS") );
		Assert.assertTrue( testMethod.getSignature().equals("testVoid()") );
		Assert.assertTrue( testMethod.getName().equals("testVoid") );
		Assert.assertTrue( testMethod.getDurationMs().equals("0") );
		Assert.assertTrue( testMethod.getStartedAt().equals("2010-11-17T13:31:41Z") );
		Assert.assertTrue( testMethod.getFinishedAt().equals("2010-11-17T13:31:41Z") );
		
	}
	
}
