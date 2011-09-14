/*
 * The MIT License
 *
 * Copyright (c) <2011> <Bruno P. Kinoshita>
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
package hudson.plugins.testlink.result.junit.issue10849;

import hudson.model.BuildListener;
import hudson.model.StreamBuildListener;
import hudson.plugins.testlink.result.TestCaseWrapper;
import hudson.plugins.testlink.result.TestLinkReport;
import hudson.plugins.testlink.result.TestResultSeeker;
import hudson.plugins.testlink.result.TestResultsCallable;
import hudson.plugins.testlink.result.junit.JUnitSuitesTestResultSeeker;
import hudson.plugins.testlink.result.junit.JUnitTestCasesTestResultSeeker;
import hudson.plugins.testlink.result.junit.TestJUnitTestCaseSeeker;

import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

import org.junit.Assert;
import org.jvnet.hudson.test.Bug;

import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;

/**
 * Tests for issue 10849. In this issue, the user reported
 * 
 * @author Bruno P. Kinoshita
 */
@Bug(10849)
public class TestPerformance10849 
extends junit.framework.TestCase
{
	
	private TestResultsCallable testResultsCallable;
	
	private TestLinkReport report;

	private static final String KEY_CUSTOM_FIELD = "testCustomField";
	private static final String JUNIT_XML_PATTERN = "**/TEST*.xml";

	public void setUp()
	{
		this.report = new TestLinkReport();
		BuildListener listener = new StreamBuildListener(new PrintStream(
				System.out), Charset.defaultCharset());
		this.testResultsCallable = new TestResultsCallable(report, 
				KEY_CUSTOM_FIELD, listener);
		final TestResultSeeker<?> junitSuitesSeeker = 
				new JUnitSuitesTestResultSeeker<hudson.plugins.testlink.parser.junit.TestSuite>(
					JUNIT_XML_PATTERN, 
					report, 
					KEY_CUSTOM_FIELD, 
					listener);
		testResultsCallable.addTestResultSeeker(junitSuitesSeeker);
			
		final TestResultSeeker<?> junitTestsSeeker = 
			new JUnitTestCasesTestResultSeeker<hudson.plugins.testlink.parser.junit.TestCase>(
					JUNIT_XML_PATTERN, 
					report, 
					KEY_CUSTOM_FIELD, 
					listener);
		testResultsCallable.addTestResultSeeker(junitTestsSeeker);
	}

	public void testPerformance10849()
	{
		long start = System.currentTimeMillis();
		for( int i = 1 ; i <= 100 ; ++i )
		{
			TestCase tc = new TestCase();
			CustomField cf = new CustomField();
			cf.setName(KEY_CUSTOM_FIELD);
			cf.setValue("br.eti.kinoshita.junit.SampleTest");
			tc.setId( i );
			tc.setName("TC for issue 10849");
			tc.getCustomFields().add(cf);
			this.report.getTestCases().put(tc.getId(), tc);
		}
		
		ClassLoader cl = TestJUnitTestCaseSeeker.class.getClassLoader();
		URL url = cl
				.getResource("hudson/plugins/testlink/result/junit/issue10849/");
		final Map<Integer, TestCaseWrapper> wrappedTestCases = testResultsCallable.seekTestResults(new File(url.getFile()));
		//assertTrue(found.size() == 1);

		//assertTrue(found.get(1).getTestCase().getExecutionStatus() == ExecutionStatus.FAILED);
		assertTrue( wrappedTestCases != null );
		long end = System.currentTimeMillis();
		
		System.out.println("Took: " + (end - start));
		
		Assert.assertTrue((end-start) < 5000);
	}
	
}
