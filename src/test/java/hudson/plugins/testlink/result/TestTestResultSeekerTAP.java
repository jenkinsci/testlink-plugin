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
import java.util.Iterator;
import java.util.Set;

import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;

/**
 * Tests TestResultSeeker with TAP.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
public class TestTestResultSeekerTAP 
extends junit.framework.TestCase
{
	
	private TestResultsCallable seeker;
	
	private TestLinkReport report;
	
	private final static String KEY_CUSTOM_FIELD = "testCustomField";
	
	private ReportFilesPatterns reportFilesPatterns;
	
	private String tapReportFilesPattern = "*.tap";
	
	public void setUp()
	{
		this.report = new TestLinkReport();
		this.reportFilesPatterns = new ReportFilesPatterns(null, null, tapReportFilesPattern);
		BuildListener listener = new StreamBuildListener(new PrintStream(System.out), Charset.defaultCharset());
		this.seeker = 
			new TestResultsCallable(report, KEY_CUSTOM_FIELD, reportFilesPatterns, listener);
	}

	public void testTestResultSeekerTAPOne()
	{
		TestCase tc = new TestCase();
		CustomField cf = new CustomField();
		cf.setName( KEY_CUSTOM_FIELD );
		cf.setValue("br.eti.kinoshita.tap.SampleTest");
		tc.getCustomFields().add(cf);
		tc.setId(1);
		this.report.getTestCases().put( tc.getId(), tc );
		
		ClassLoader cl = TestTestResultSeekerTAP.class.getClassLoader();
		URL url = cl.getResource("hudson/plugins/testlink/result/");
		File tapDir = new File( url.getFile() );
		Set<TestCaseWrapper> found = seeker.seekTestResults(tapDir);
		assertNotNull( found );
		assertTrue( found.size() == 1 );
		assertTrue( found.iterator().next().getTestCase().getExecutionStatus() == ExecutionStatus.PASSED );
	}
	
	public void testTestResultSeekerTAPThree()
	{
		TestCase tc = new TestCase();
		CustomField cf = new CustomField();
		cf.setName( KEY_CUSTOM_FIELD );
		cf.setValue("br.eti.kinoshita.tap.SampleTest2");
		tc.getCustomFields().add(cf);
		tc.setId(2);
		this.report.addTestCase(tc);
		
		tc = new TestCase();
		cf = new CustomField();
		cf.setName( KEY_CUSTOM_FIELD );
		cf.setValue("br.eti.kinoshita.tap.SampleTest3");
		tc.getCustomFields().add(cf);
		tc.setId(3);
		this.report.addTestCase(tc);
		
		assertTrue( this.report.getTestCases().size() == 2 );
		
		ClassLoader cl = TestTestResultSeekerTAP.class.getClassLoader();
		URL url = cl.getResource("hudson/plugins/testlink/result/");
		File tapDir = new File( url.getFile() );
		Set<TestCaseWrapper> found = seeker.seekTestResults(tapDir);
		assertNotNull( found );
		assertTrue( found.size() == 2 );
		Iterator<TestCaseWrapper> iter = found.iterator();
		assertTrue( iter.next().getTestCase().getExecutionStatus() == ExecutionStatus.FAILED );
		assertTrue( iter.next().getTestCase().getExecutionStatus() == ExecutionStatus.FAILED );
	}
	
}
