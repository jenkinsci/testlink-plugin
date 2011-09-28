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

import junit.framework.TestCase;

import org.junit.Assert;
import org.jvnet.hudson.test.Bug;

import br.eti.kinoshita.testlinkjavaapi.model.CustomField;

/**
 * Tests for issue 10849. In this issue, the user reported
 * 
 * @author Bruno P. Kinoshita
 */
@Bug(10849)
public class TestPerformance10849 
extends TestCase
{
	
	private TestResultsCallable testResultsCallable;
	
	private static final String KEY_CUSTOM_FIELD = "testCustomField";
	private static final String JUNIT_XML_PATTERN = "**/TEST*.xml";

	public void setUp()
	{
		br.eti.kinoshita.testlinkjavaapi.model.TestCase[] tcs = new br.eti.kinoshita.testlinkjavaapi.model.TestCase[0];
		BuildListener listener = new StreamBuildListener(new PrintStream(
				System.out), Charset.defaultCharset());
		this.testResultsCallable = new TestResultsCallable();
		final TestResultSeeker<?> junitSuitesSeeker = 
				new JUnitSuitesTestResultSeeker<hudson.plugins.testlink.parser.junit.TestSuite>(
					JUNIT_XML_PATTERN, 
					tcs, 
					KEY_CUSTOM_FIELD, 
					listener);
		testResultsCallable.addTestResultSeeker(junitSuitesSeeker);
			
		final TestResultSeeker<?> junitTestsSeeker = 
			new JUnitTestCasesTestResultSeeker<hudson.plugins.testlink.parser.junit.TestCase>(
					JUNIT_XML_PATTERN, 
					tcs, 
					KEY_CUSTOM_FIELD, 
					listener);
		testResultsCallable.addTestResultSeeker(junitTestsSeeker);
	}

	public void testPerformance10849()
	{
		long start = System.currentTimeMillis();
		br.eti.kinoshita.testlinkjavaapi.model.TestCase[] tcs = new br.eti.kinoshita.testlinkjavaapi.model.TestCase[100];
		for( int i = 0 ; i < 100 ; ++i )
		{
			br.eti.kinoshita.testlinkjavaapi.model.TestCase tc = new br.eti.kinoshita.testlinkjavaapi.model.TestCase();
			CustomField cf = new CustomField();
			cf.setName(KEY_CUSTOM_FIELD);
			cf.setValue("br.eti.kinoshita.junit.SampleTest");
			tc.setId( (i+1) );
			tc.setName("TC for issue 10849");
			tc.getCustomFields().add( cf );
			tcs[i] = tc;
		}
		
		ClassLoader cl = TestJUnitTestCaseSeeker.class.getClassLoader();
		URL url = cl
				.getResource("hudson/plugins/testlink/result/junit/issue10849/");
		@SuppressWarnings("rawtypes")
		final Map<Integer, TestCaseWrapper> wrappedTestCases = testResultsCallable.seekTestResults(new File(url.getFile()));
		//assertTrue(found.size() == 1);

		//assertTrue(found.get(1).getTestCase().getExecutionStatus() == ExecutionStatus.FAILED);
		assertTrue( wrappedTestCases != null );
		long end = System.currentTimeMillis();
		
		System.out.println("Took: " + (end - start));
		
		Assert.assertTrue((end-start) < 5000);
	}
	
}
