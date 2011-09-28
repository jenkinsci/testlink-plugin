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
package hudson.plugins.testlink.util;

import hudson.EnvVars;
import hudson.model.BuildListener;
import hudson.model.StreamBuildListener;

import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;

import org.jvnet.hudson.test.Bug;

import junit.framework.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.model.TestProject;

/**
 *
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 */
public class TestTestLinkHelper 
extends TestCase
{
	
	BuildListener listener;

	/**
	 * Defines the Locale to US
	 */
	public void setUp()
	{
		listener = new StreamBuildListener(new PrintStream(System.out), Charset.defaultCharset());
		
		Locale.setDefault(new Locale("en", "US"));
		
		try
		{
			final Constructor<?> c = TestLinkHelper.class.getDeclaredConstructors()[0];
			c.setAccessible(true);
			final Object o = c.newInstance((Object[]) null);

			assertNotNull(o);
		}
		catch (Exception e)
		{
			fail("Failed to instantiate constructor: " + e.getMessage());
		}
	}
	
	public void testColoredExecutionStatusText()
	{
		ExecutionStatus status = ExecutionStatus.PASSED;
		String text = TestLinkHelper.getExecutionStatusTextColored(status); 
		assertTrue( text.equals("<span style='color: green'>Passed</span>") );
			
		status = ExecutionStatus.FAILED;
		text = TestLinkHelper.getExecutionStatusTextColored(status); 
		assertTrue( text.equals("<span style='color: red'>Failed</span>") );
		
		status = ExecutionStatus.NOT_RUN;
		text = TestLinkHelper.getExecutionStatusTextColored(status); 
		assertTrue( text.equals("<span style='color: gray'>Not Run</span>") );
		
		status = ExecutionStatus.BLOCKED;
		text = TestLinkHelper.getExecutionStatusTextColored(status); 
		assertTrue( text.equals("<span style='color: yellow'>Blocked</span>") );
	}
	
	public void testTestLinkJavaAPIProperties()
	{
		String testLinkJavaAPIProperties = "httpd.server=false, testlink.security=true, test";
		
		TestLinkHelper.setTestLinkJavaAPIProperties(testLinkJavaAPIProperties, listener);
		
		assertEquals( System.getProperties().get("httpd.server"), "false");
	}
	
	public void testCreateTestLinkEnvVars()
	{
		br.eti.kinoshita.testlinkjavaapi.model.TestCase testCase = 
			new br.eti.kinoshita.testlinkjavaapi.model.TestCase();
		testCase.setId( 100 );
		testCase.setName("Sample name");
		testCase.setTestSuiteId(10);
		testCase.setAuthorLogin("admin");
		testCase.setSummary("summary");

		CustomField cf = new CustomField();
		cf.setName("cf");
		cf.setValue("fc");
		testCase.getCustomFields().add(cf);
		
		TestProject testProject = new TestProject();
		testProject.setId( 1000 );
		testCase.setTestProjectId(testProject.getId());
		testProject.setName("Sample project name");
		
		TestPlan testPlan = new TestPlan();
		testPlan.setName ( "10000" );
		
		Build build = new Build();
		build.setName( "100000" );
		
		Map<String, String> envVars = TestLinkHelper.createTestLinkEnvironmentVariables(testCase, testProject, testPlan, build);
		
		assertEquals( envVars.get("TESTLINK_TESTCASE_ID"), "100");
		assertEquals( envVars.get("TESTLINK_TESTCASE_TESTPROJECTID"), "1000");
		assertEquals( envVars.get("TESTLINK_TESTPLAN_NAME"), "10000");
		assertEquals( envVars.get("TESTLINK_BUILD_NAME"), "100000");
		
		assertEquals( envVars.get("TESTLINK_TESTCASE_CF"), "fc");
		
		assertNull( envVars.get("TESTLINK_TESTCASE_CF_0") );
		
		EnvVars envVarsEnvVars = TestLinkHelper.buildTestCaseEnvVars(testCase, testProject, testPlan, build, listener);
		
		assertTrue( envVarsEnvVars.equals(envVars) );
	}
	
	@Bug(9672)
	public void testCreateTestLinkEnvVarsWithCommas()
	{
		br.eti.kinoshita.testlinkjavaapi.model.TestCase testCase = 
			new br.eti.kinoshita.testlinkjavaapi.model.TestCase();
		testCase.setId( 100 );
		testCase.setName("Sample name");
		testCase.setTestSuiteId(10);
		testCase.setAuthorLogin("admin");
		testCase.setSummary("summary");

		CustomField cf = new CustomField();
		cf.setName("cf");
		cf.setValue("fc, gh");
		testCase.getCustomFields().add(cf);
		
		TestProject testProject = new TestProject();
		testProject.setId( 1000 );
		testCase.setTestProjectId(testProject.getId());
		testProject.setName("Sample project name");
		
		TestPlan testPlan = new TestPlan();
		testPlan.setName ( "10000" );
		
		Build build = new Build();
		build.setName( "100000" );
		
		Map<String, String> envVars = TestLinkHelper.createTestLinkEnvironmentVariables(testCase, testProject, testPlan, build);
		
		assertEquals( envVars.get("TESTLINK_TESTCASE_ID"), "100");
		assertEquals( envVars.get("TESTLINK_TESTCASE_TESTPROJECTID"), "1000");
		assertEquals( envVars.get("TESTLINK_TESTPLAN_NAME"), "10000");
		assertEquals( envVars.get("TESTLINK_BUILD_NAME"), "100000");
		
		assertEquals( envVars.get("TESTLINK_TESTCASE_CF"), "fc, gh");
		
		assertEquals( envVars.get("TESTLINK_TESTCASE_CF_0"), "fc");
		assertEquals( envVars.get("TESTLINK_TESTCASE_CF_1"), "gh");
		
		EnvVars envVarsEnvVars = TestLinkHelper.buildTestCaseEnvVars(testCase, testProject, testPlan, build, listener);
		
		assertTrue( envVarsEnvVars.equals(envVars) );
	}
	
}
