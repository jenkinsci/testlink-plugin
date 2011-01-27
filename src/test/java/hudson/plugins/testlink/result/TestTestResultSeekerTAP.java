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
 * @since 27/01/2011
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
 * Tests TestResultSeeker with TAP.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
public class TestTestResultSeekerTAP
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
	public void testTestResultSeekerTAPOne()
	{
		TestCase tc = new TestCase();
		CustomField cf = new CustomField();
		cf.setName( KEY_CUSTOM_FIELD );
		cf.setValue("br.eti.kinoshita.tap.SampleTest");
		tc.getCustomFields().add(cf);
		this.report.getTestCases().add(tc);
		
		ClassLoader cl = TestTestResultSeekerTAP.class.getClassLoader();
		URL url = cl.getResource("hudson/plugins/testlink/result/");
		File tapDir = new File( url.getFile() );
		this.reportFilesPatterns.setTapStreamReportFilesPattern("*.tap");
		List<TestResult> found = seeker.seekTestResults(tapDir);
		Assert.assertNotNull( found );
		Assert.assertTrue( found.size() == 1 );
		Assert.assertTrue( found.get(0).getTestCase().getExecutionStatus() == ExecutionStatus.PASSED );
	}
	
	@Test
	public void testTestResultSeekerTAPThree()
	{
		TestCase tc = new TestCase();
		CustomField cf = new CustomField();
		cf.setName( KEY_CUSTOM_FIELD );
		cf.setValue("br.eti.kinoshita.tap.SampleTest2");
		tc.getCustomFields().add(cf);
		this.report.addTestCase(tc);
		
		tc = new TestCase();
		cf = new CustomField();
		cf.setName( KEY_CUSTOM_FIELD );
		cf.setValue("br.eti.kinoshita.tap.SampleTest3");
		tc.getCustomFields().add(cf);
		this.report.addTestCase(tc);
		
		Assert.assertTrue( this.report.getTestCases().size() == 2 );
		
		ClassLoader cl = TestTestResultSeekerTAP.class.getClassLoader();
		URL url = cl.getResource("hudson/plugins/testlink/result/");
		File tapDir = new File( url.getFile() );
		this.reportFilesPatterns.setTapStreamReportFilesPattern("*.tap");
		List<TestResult> found = seeker.seekTestResults(tapDir);
		Assert.assertNotNull( found );
		Assert.assertTrue( found.size() == 2 );
		Assert.assertTrue( found.get(0).getTestCase().getExecutionStatus() == ExecutionStatus.FAILED );
		Assert.assertTrue( found.get(1).getTestCase().getExecutionStatus() == ExecutionStatus.FAILED );
	}
	
}
