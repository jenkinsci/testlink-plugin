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
package hudson.plugins.testlink.result.testng.issue9672;

import hudson.model.BuildListener;
import hudson.model.StreamBuildListener;
import hudson.plugins.testlink.result.TestCaseWrapper;
import hudson.plugins.testlink.result.TestLinkReport;
import hudson.plugins.testlink.result.testng.TestNGClassesTestResultSeeker;

import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

import org.jvnet.hudson.test.Bug;

import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;

/**
 * Tests for issue 9672.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.5
 */
@Bug(9672)
public class TestIssue9672 
extends junit.framework.TestCase
{

	private static final long serialVersionUID = 854861980155890127L;

	private TestNGClassesTestResultSeeker<hudson.plugins.testlink.parser.testng.Class> seeker;

	private TestLinkReport report;

	private final static String KEY_CUSTOM_FIELD = "testCustomField";

	public void setUp()
	{
		this.report = new TestLinkReport();
		BuildListener listener = new StreamBuildListener(new PrintStream(
				System.out), Charset.defaultCharset());
		this.seeker = new TestNGClassesTestResultSeeker<hudson.plugins.testlink.parser.testng.Class>(
				"testng-results*.xml", report, KEY_CUSTOM_FIELD, listener);
	}

	public void testOneTCtcA()
	{
		TestCase tc = new TestCase();
		CustomField cf = new CustomField();
		cf.setName(KEY_CUSTOM_FIELD);
		cf.setValue("br.eti.kinoshita.TestA");
		tc.getCustomFields().add(cf);
		tc.setId(1);
		this.report.getTestCases().put(tc.getId(), tc);

		ClassLoader cl = TestIssue9672.class.getClassLoader();
		URL url = cl
				.getResource("hudson/plugins/testlink/result/testng/issue9672/");
		File junitDir = new File(url.getFile());
		Map<Integer, TestCaseWrapper<hudson.plugins.testlink.parser.testng.Class>> found = seeker
				.seek(junitDir);
		assertNotNull(found);
		assertTrue(found.size() == 1);
		assertTrue(found.get(tc.getId()).getTestCase().getExecutionStatus() == ExecutionStatus.FAILED);
	}

	public void testTwoTCtcAAndtcB()
	{
		TestCase tc = new TestCase();
		CustomField cf = new CustomField();
		cf.setName(KEY_CUSTOM_FIELD);
		cf.setValue("br.eti.kinoshita.TestA, br.eti.kinoshita.TestB");
		tc.getCustomFields().add(cf);
		tc.setId(2);
		this.report.addTestCase(tc);

		assertTrue(this.report.getTestCases().size() == 1);

		ClassLoader cl = TestIssue9672.class.getClassLoader();
		URL url = cl
				.getResource("hudson/plugins/testlink/result/testng/issue9672");
		File junitDir = new File(url.getFile());
		Map<Integer, TestCaseWrapper<hudson.plugins.testlink.parser.testng.Class>> found = seeker
				.seek(junitDir);
		assertNotNull(found);
		assertTrue(found.size() == 1);
		assertTrue(found.get(2).getTestCase().getExecutionStatus() == ExecutionStatus.FAILED);
	}

	public void testTwoTCtcBAndtc2()
	{
		TestCase tc = new TestCase();
		CustomField cf = new CustomField();
		cf.setName(KEY_CUSTOM_FIELD);
		cf.setValue("br.eti.kinoshita.TestB, br.eti.kinoshita.Test2");
		tc.getCustomFields().add(cf);
		tc.setId(2);
		this.report.addTestCase(tc);

		assertTrue(this.report.getTestCases().size() == 1);

		ClassLoader cl = TestIssue9672.class.getClassLoader();
		URL url = cl
				.getResource("hudson/plugins/testlink/result/testng/issue9672");
		File junitDir = new File(url.getFile());
		Map<Integer, TestCaseWrapper<hudson.plugins.testlink.parser.testng.Class>> found = seeker
				.seek(junitDir);
		assertNotNull(found);
		assertTrue(found.size() == 1);
		assertTrue(found.get(2).getTestCase().getExecutionStatus() == ExecutionStatus.PASSED);
	}

	public void testThreeTCtcAAndNonExistenttcK()
	{
		TestCase tc = new TestCase();
		CustomField cf = new CustomField();
		cf.setName(KEY_CUSTOM_FIELD);
		cf.setValue("br.eti.kinoshita.TestB, br.eti.kinoshita.TestK");
		tc.getCustomFields().add(cf);
		tc.setId(2);
		this.report.addTestCase(tc);

		assertTrue(this.report.getTestCases().size() == 1);

		ClassLoader cl = TestIssue9672.class.getClassLoader();
		URL url = cl
				.getResource("hudson/plugins/testlink/result/testng/issue9672");
		File junitDir = new File(url.getFile());
		Map<Integer, TestCaseWrapper<hudson.plugins.testlink.parser.testng.Class>> found = seeker
				.seek(junitDir);
		assertNotNull(found);
		assertTrue(found.size() == 1);
		assertTrue(found.get(2).getTestCase().getExecutionStatus() == ExecutionStatus.NOT_RUN);
	}

	public void testThreeTCtcAAndtc3Andtc2()
	{
		TestCase tc = new TestCase();
		CustomField cf = new CustomField();
		cf.setName(KEY_CUSTOM_FIELD);
		cf.setValue("br.eti.kinoshita.TestA, br.eti.kinoshita.Test3, br.eti.kinoshita.Test2");
		tc.getCustomFields().add(cf);
		tc.setId(2);
		this.report.addTestCase(tc);

		assertTrue(this.report.getTestCases().size() == 1);

		ClassLoader cl = TestIssue9672.class.getClassLoader();
		URL url = cl
				.getResource("hudson/plugins/testlink/result/testng/issue9672");
		File junitDir = new File(url.getFile());
		Map<Integer, TestCaseWrapper<hudson.plugins.testlink.parser.testng.Class>> found = seeker
				.seek(junitDir);
		assertNotNull(found);
		assertTrue(found.size() == 1);
		assertTrue(found.get(2).getTestCase().getExecutionStatus() == ExecutionStatus.FAILED);
	}

}
