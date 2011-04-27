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
package hudson.plugins.testlink.result.issue9229;

import hudson.model.BuildListener;
import hudson.model.StreamBuildListener;
import hudson.plugins.testlink.result.JUnitTestResultSeeker;
import hudson.plugins.testlink.result.TestLinkReport;
import hudson.plugins.testlink.result.TestCaseWrapper;
import hudson.plugins.testlink.result.TestTestResultSeekerJUnit;

import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Set;

import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TestIssue9229 
extends junit.framework.TestCase
{
	
	private JUnitTestResultSeeker seeker;
	
	private TestLinkReport report;
	
	private final static String KEY_CUSTOM_FIELD = "testCustomField";
	
	public void setUp()
	{
		this.report = new TestLinkReport();
		BuildListener listener = new StreamBuildListener(new PrintStream(System.out), Charset.defaultCharset());
		this.seeker = 
			new JUnitTestResultSeeker(report, KEY_CUSTOM_FIELD, listener);
	}
	
	public void testTestResultSeekerJUnitIssue9229()
	{
		TestCase tc = new TestCase();
		CustomField cf = new CustomField();
		cf.setName( KEY_CUSTOM_FIELD );
		cf.setValue( "br.eti.kinoshita.junit.SampleTest" );
		tc.setId( 1 );
		tc.setName( "TC for issue 9229" );
		tc.getCustomFields().add(cf);
		this.report.getTestCases().add(tc);
		
		ClassLoader cl = TestTestResultSeekerJUnit.class.getClassLoader();
		URL url = cl.getResource("hudson/plugins/testlink/result/issue9229/");
		File junitDir = new File( url.getFile() );
		Set<TestCaseWrapper> found = seeker.seek(junitDir, "TEST-*.xml");
		assertNotNull( found );
		assertTrue( found.size() == 1 );
		
		assertTrue( found.iterator().next().getTestCase().getExecutionStatus() == ExecutionStatus.FAILED );
		
	}

}
