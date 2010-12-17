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
package hudson.plugins.testlink.parser;

import hudson.model.BuildListener;
import hudson.model.StreamBuildListener;
import hudson.plugins.testlink.model.TestLinkReport;
import hudson.plugins.testlink.model.TestResult;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.model.TestProject;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.0
 */
public class TestTestNGParser
{
	private TestNGParser parser;
	private static final String keyCustomField = "Test Class";
	
	private TestLinkReport report = null;
	
	private Build build;
	private TestPlan testPlan;
	private TestProject testProject;
	
	@BeforeClass
	public void setUp()
	{
		build = new Build();
		testPlan = new TestPlan();
		testProject = new TestProject();
		
		report = new TestLinkReport(build, testPlan, testProject);
		TestCase testCase = new TestCase();
		CustomField customField = new CustomField();
		customField.setName("Test Class");
		customField.setValue("br.eti.kinoshita.Test1");
		testCase.getCustomFields().add( customField );
		report.getTestCases().add(testCase);
		BuildListener listener = new StreamBuildListener(System.out, Charset.defaultCharset());
		this.parser = new TestNGParser(report, keyCustomField, listener, "**/testng-results.xml");
	}
	
	@Test(testName="Test TestNG Parser")
	public void testTapParser()
	{
		Assert.assertEquals(this.parser.getName(), "TestNG");
		
		ClassLoader cl = TestTestNGParser.class.getClassLoader();
		URL url = cl.getResource(".");
		File baseDir = new File( url.getFile() );
		
		TestResult[] testResults = null;
		try
		{
			testResults = this.parser.parse( baseDir );
		}
		catch (IOException e)
		{
			Assert.fail("", e);
		}
		
		Assert.assertNotNull( testResults );
		Assert.assertTrue( testResults.length == 1 );
		
		Assert.assertTrue( testResults[0].getTestCase().getExecutionStatus() == ExecutionStatus.PASSED );
	}
	
}
