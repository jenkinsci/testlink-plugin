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
package hudson.plugins.testlink.result;

import hudson.model.BuildListener;
import hudson.model.StreamBuildListener;

import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;

/**
 * Tests TestResultSeeker with JUnit.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
public class TestTestResultSeekerJUnit
{
	
	private TestResultSeeker seeker;
	
	private TestLinkReport report;
	
	private final static String KEY_CUSTOM_FIELD = "testCustomField";
	
	private ReportFilesPatterns reportFilesPatterns;
	
	@BeforeMethod
	public void setUp()
	{
		this.report = new TestLinkReport();
		this.reportFilesPatterns = new ReportFilesPatterns();
		BuildListener listener = new StreamBuildListener(new PrintStream(System.out), Charset.defaultCharset());
		this.seeker = 
			new TestResultSeeker(report, KEY_CUSTOM_FIELD, reportFilesPatterns, listener);
	}

	@Test
	public void testTestResultSeekerJUnitOne()
	{
		TestCase tc = new TestCase();
		CustomField cf = new CustomField();
		cf.setName( KEY_CUSTOM_FIELD );
		cf.setValue("br.eti.kinoshita.Test");
		tc.getCustomFields().add(cf);
		this.report.getTestCases().add(tc);
		
		ClassLoader cl = TestTestResultSeekerJUnit.class.getClassLoader();
		URL url = cl.getResource("hudson/plugins/testlink/result/");
		File junitDir = new File( url.getFile() );
		this.reportFilesPatterns.setJunitXmlReportFilesPattern("TEST-*.xml");
		List<TestResult> found = seeker.seekTestResults(junitDir);
		Assert.assertNotNull( found );
		Assert.assertTrue( found.size() == 1 );
		Assert.assertTrue( found.get(0).getTestCase().getExecutionStatus() == ExecutionStatus.FAILED );
	}
	
	@Test
	public void testTestResultSeekerJUnitTwo()
	{
		TestCase tc = new TestCase();
		CustomField cf = new CustomField();
		cf.setName( KEY_CUSTOM_FIELD );
		cf.setValue("br.eti.kinoshita.Test");
		tc.getCustomFields().add(cf);
		this.report.addTestCase(tc);
		
		tc = new TestCase();
		cf = new CustomField();
		cf.setName( KEY_CUSTOM_FIELD );
		cf.setValue("br.eti.kinoshita.TestImmo");
		tc.getCustomFields().add(cf);
		this.report.addTestCase(tc);
		
		Assert.assertTrue( this.report.getTestCases().size() == 2 );
		
		ClassLoader cl = TestTestResultSeekerJUnit.class.getClassLoader();
		URL url = cl.getResource("hudson/plugins/testlink/result/");
		File junitDir = new File( url.getFile() );
		this.reportFilesPatterns.setJunitXmlReportFilesPattern("TEST-*.xml");
		List<TestResult> found = seeker.seekTestResults(junitDir);
		Assert.assertNotNull( found );
		Assert.assertTrue( found.size() == 2 );
		Assert.assertTrue( found.get(0).getTestCase().getExecutionStatus() == ExecutionStatus.FAILED );
		Assert.assertTrue( found.get(1).getTestCase().getExecutionStatus() == ExecutionStatus.PASSED );
	}
	
}
