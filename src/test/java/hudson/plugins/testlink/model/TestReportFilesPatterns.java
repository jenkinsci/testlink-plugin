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
package hudson.plugins.testlink.model;

import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import hudson.plugins.testlink.model.ReportFilesPatterns;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 02/12/2010
 */
public class TestReportFilesPatterns
{

	private ReportFilesPatterns patterns;
	
	@BeforeClass
	public void setUp()
	{
		this.patterns = new ReportFilesPatterns();
	}
	
	@Test(testName="Test Getters and Setters")
	public void testGettersAndSetters()
	{
		Assert.assertNull( patterns.getJunitXmlReportFilesPattern() );
		String junitXmlReportFilesPattern = "**/TEST-*.xml";
		patterns.setJunitXmlReportFilesPattern( junitXmlReportFilesPattern );
		Assert.assertNotNull( patterns.getJunitXmlReportFilesPattern() );
		Assert.assertEquals( patterns.getJunitXmlReportFilesPattern(), junitXmlReportFilesPattern );
		
		Assert.assertNull( patterns.getTestNGXmlReportFilesPattern() );
		String testNgXmlReportFilesPattern = "**/testng-results.xml";
		patterns.setTestNGXmlReportFilesPattern( testNgXmlReportFilesPattern );
		Assert.assertNotNull( patterns.getTestNGXmlReportFilesPattern() );
		Assert.assertEquals( patterns.getTestNGXmlReportFilesPattern(), testNgXmlReportFilesPattern );
		
		Assert.assertNull( patterns.getTapStreamReportFilesPattern() );
		String tapReportFilesPattern = "**/*.tap";
		patterns.setTapStreamReportFilesPattern( tapReportFilesPattern );
		Assert.assertNotNull( patterns.getTapStreamReportFilesPattern() );
		Assert.assertEquals( patterns.getTapStreamReportFilesPattern(), tapReportFilesPattern );		
	}
	
	@Test(testName="Test ReportFilesPatterns toString()")
	public void testToString()
	{
		Assert.assertTrue( patterns.toString().startsWith( "TestReportDirectories [junitXmlReportFilesPattern=" ));
	}
	
}
