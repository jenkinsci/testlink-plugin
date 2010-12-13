/**
 *	 __                                        
 *	/\ \      __                               
 *	\ \ \/'\ /\_\    ___     ___   __  __  __  
 *	 \ \ , < \/\ \ /' _ `\  / __`\/\ \/\ \/\ \ 
 *	  \ \ \\`\\ \ \/\ \/\ \/\ \L\ \ \ \_/ \_/ \
 *	   \ \_\ \_\ \_\ \_\ \_\ \____/\ \___x___/'
 *	    \/_/\/_/\/_/\/_/\/_/\/___/  \/__//__/  
 *                                          
 * Copyright (c) 1999-present Kinow
 * Casa Verde - São Paulo - SP. Brazil.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Kinow ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Kinow.                                      
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 02/12/2010
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
 * @since 02/12/2010
 */
public class TestJUnitParser
{
	private JUnitParser parser;
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
		customField.setValue("br.eti.kinoshita.Test");
		testCase.getCustomFields().add( customField );
		report.getTestCases().add(testCase);
		BuildListener listener = new StreamBuildListener(System.out, Charset.defaultCharset());
		this.parser = new JUnitParser(report, keyCustomField, listener, "**/TEST-*.xml");
	}
	
	@Test(testName="Test TestNG Parser")
	public void testTapParser()
	{
		Assert.assertEquals(this.parser.getName(), "JUnit");
		
		ClassLoader cl = TestJUnitParser.class.getClassLoader();
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
		
		Assert.assertTrue( testResults[0].getTestCase().getExecutionStatus() == ExecutionStatus.FAILED );
	}
	
}
