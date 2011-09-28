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
package hudson.plugins.testlink.result.testng;

import hudson.model.BuildListener;
import hudson.model.StreamBuildListener;
import hudson.plugins.testlink.result.TestCaseWrapper;

import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;

/**
 * Tests TestResultSeeker with TestNG.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.5
 */
public class TestTestNGClassSeeker 
extends junit.framework.TestCase
{
	
	private TestNGClassesTestResultSeeker<hudson.plugins.testlink.parser.testng.Class> seeker;
	
	private final static String KEY_CUSTOM_FIELD = "testCustomField";
	
	public void setUp()
	{
		BuildListener listener = new StreamBuildListener(new PrintStream(System.out), Charset.defaultCharset());
		
		TestCase[] tcs = new TestCase[3];
		
		TestCase tc = new TestCase();
		CustomField cf = new CustomField();
		cf.setName( KEY_CUSTOM_FIELD );
		cf.setValue("br.eti.kinoshita.Test");
		tc.getCustomFields().add(cf);
		tc.setId(1);
		tcs[0] = tc;
		
		tc = new TestCase();
		cf = new CustomField();
		cf.setName( KEY_CUSTOM_FIELD );
		cf.setValue("br.eti.kinoshita.Test2");
		tc.getCustomFields().add(cf);
		tc.setId(2);
		tcs[1] = tc;
		
		tc = new TestCase();
		cf = new CustomField();
		cf.setName( KEY_CUSTOM_FIELD );
		cf.setValue("br.eti.kinoshita.TestImmo");
		tc.getCustomFields().add(cf);
		tc.setId(3);
		tcs[2] = tc;
		
		this.seeker = 
			new TestNGClassesTestResultSeeker<hudson.plugins.testlink.parser.testng.Class>("testng*.xml", tcs, KEY_CUSTOM_FIELD, listener);
	}

	public void testTestResultSeekerTestNGOne()
	{
		ClassLoader cl = TestTestNGClassSeeker.class.getClassLoader();
		URL url = cl.getResource("hudson/plugins/testlink/result/testng/");
		File testNGDir = new File( url.getFile() );
		Map<Integer, TestCaseWrapper<hudson.plugins.testlink.parser.testng.Class>> found = seeker.seek( testNGDir );
		assertNotNull( found );
		assertTrue( found.size() == 3 );
		assertTrue( found.get(1).getExecutionStatus() == ExecutionStatus.FAILED );
	}
	
	public void testTestResultSeekerTestNGTwo()
	{
		ClassLoader cl = TestTestNGClassSeeker.class.getClassLoader();
		URL url = cl.getResource("hudson/plugins/testlink/result/testng/");
		File testNGDir = new File( url.getFile() );
		Map<Integer, TestCaseWrapper<hudson.plugins.testlink.parser.testng.Class>> found = seeker.seek(testNGDir);
		assertNotNull( found );
		assertTrue( found.size() == 3 );
		assertTrue( found.get(2).getExecutionStatus() == ExecutionStatus.FAILED );
		assertTrue( found.get(3).getExecutionStatus() == ExecutionStatus.PASSED );
	}
	
}
