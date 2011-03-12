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
 * @since 03/02/2011
 */
package hudson.plugins.testlink;

import hudson.plugins.testlink.model.TestLinkLatestRevisionInfo;
import hudson.plugins.testlink.result.ReportFilesPatterns;

import org.jvnet.hudson.test.HudsonTestCase;

/**
 * Tests TestLinkBuilder class.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
public class TestTestLinkBuilder 
extends HudsonTestCase
{

	private final TestLinkLatestRevisionInfo latestRevisionInfo = new TestLinkLatestRevisionInfo(
			"", "", "");
	
	private TestLinkBuilder builder = null;	
	
	public void setUp() 
	throws Exception
	{
		super.setUp();
		
		builder = new TestLinkBuilder("No testlink", "No project",
				"No plan", "No build", "class, time", "dir", "dir",
				Boolean.FALSE, latestRevisionInfo, "class",
				"**/TEST-*.xml", "**/testng-results.xml", "**/*.tap");
	}
	
	/**
	 * Tests the ReportPatterns object.
	 */
	public void testReportPatterns() 
	throws Exception
	{
		ReportFilesPatterns reportPatterns = builder.getReportPatterns();
		assertNotNull(reportPatterns);
		assertEquals(reportPatterns.getJunitXmlReportFilesPattern(),
				"**/TEST-*.xml");
		assertEquals(reportPatterns.getTestNGXmlReportFilesPattern(),
				"**/testng-results.xml");
		assertEquals(reportPatterns.getTapStreamReportFilesPattern(),
				"**/*.tap");
	}
	
	/**
	 * Tests the generated list of custom fields.
	 */
	public void testListOfCustomFields()
	{
		String[] customFieldsNames = builder.getListOfCustomFieldsNames();
		
		assertNotNull( customFieldsNames );
		assertTrue( customFieldsNames.length == 2 );
		assertEquals( customFieldsNames[0], "class" );
		assertEquals( customFieldsNames[1], "time" );
	}
	
	public void testNull()
	{
		builder = new TestLinkBuilder(null, null, null, null, null, null, null, null, null, null, null, null, null);
		
		assertNotNull( builder );
		
		assertNull( builder.getTestLinkName() );
		
		assertNull( builder.getTestProjectName() );
		
		assertNull( builder.getTestPlanName() );
		
		assertNull( builder.getBuildName() );
		
		assertNull( builder.getSingleTestCommand() );
		
		assertNull( builder.getIterativeTestCommand() );
		
		assertNull( builder.getLatestRevisionInfo() );
		
		assertFalse( builder.getLatestRevisionEnabled() );
		
		assertNull( builder.getCustomFields() );
		
		assertNull( builder.getTransactional() );
		
		assertNull( builder.getKeyCustomField() );
		
		assertNull( builder.getJunitReportFilesPattern() );
		
		assertNull( builder.getTestNGReportFilesPattern() );
		
		assertNull( builder.getTapReportFilesPattern() );
		
		TestLinkLatestRevisionInfo latestRevisionInfo = new TestLinkLatestRevisionInfo(
				"", "", "");
		
		builder = new TestLinkBuilder("No testlink", "No project",
				"No plan", "No build", "class, time", "dir", "dir",
				Boolean.FALSE, latestRevisionInfo, "class",
				"**/TEST-*.xml", "**/testng-results.xml", "**/*.tap");
	}
	
	/**
	 * Tests getters methods.
	 */
	public void testGetters()
	{
		assertNotNull( hudson );
		//FreeStyleProject project = new FreeStyleProject(hudson, "No project");
		//assertNotNull ( (AbstractProject<?, ?>)builder.getProjectAction(project) );
		
		assertNotNull( builder.getTestLinkName() );
		assertEquals( builder.getTestLinkName(), "No testlink" );
		
		assertNotNull( builder.getTestProjectName() );
		assertEquals( builder.getTestProjectName(), "No project" );
		
		assertNotNull( builder.getTestPlanName() );
		assertEquals( builder.getTestPlanName(), "No plan" );
		
		assertNotNull( builder.getBuildName() );
		assertEquals( builder.getBuildName(), "No build" );
		
		assertNotNull( builder.getSingleTestCommand() );
		assertEquals( builder.getSingleTestCommand() , "dir");
		
		assertNotNull( builder.getIterativeTestCommand() );
		assertEquals( builder.getIterativeTestCommand(), "dir" );
		
		assertNotNull( builder.getLatestRevisionInfo() );
		
		assertTrue( builder.getLatestRevisionEnabled() );
		
		assertNotNull( builder.getCustomFields() );
		assertEquals( builder.getCustomFields(), "class, time" );
		
		assertFalse( builder.getTransactional() );
		
		assertNotNull( builder.getKeyCustomField() );
		assertEquals( builder.getKeyCustomField(), "class" );
		
		assertNotNull( builder.getJunitReportFilesPattern() );
		assertEquals( builder.getJunitReportFilesPattern(), "**/TEST-*.xml" );
		
		assertNotNull( builder.getTestNGReportFilesPattern() );
		assertEquals( builder.getTestNGReportFilesPattern(), "**/testng-results.xml" );
		
		assertNotNull( builder.getTapReportFilesPattern() );
		assertEquals( builder.getTapReportFilesPattern(), "**/*.tap" );
	}

}
