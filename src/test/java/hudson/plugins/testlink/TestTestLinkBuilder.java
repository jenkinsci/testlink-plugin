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

import hudson.model.Hudson;
import hudson.plugins.testlink.model.TestLinkLatestRevisionInfo;
import hudson.plugins.testlink.result.ReportFilesPatterns;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.jvnet.hudson.reactor.ReactorException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests TestLinkBuilder class.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
public class TestTestLinkBuilder
{

	private TestLinkBuilder builder;

	private Hudson hudson = null;
	
	@BeforeClass
	public void setUp()
	{

		try
		{

			ServletContext ctx = new ServletContext()
			{

				public void setAttribute( String name, Object object )
				{
				}

				public void removeAttribute( String name )
				{
				}

				public void log( String message, Throwable throwable )
				{
				}

				public void log( Exception exception, String msg )
				{
				}

				public void log( String msg )
				{

				}

				public Enumeration<?> getServlets()
				{
					return null;
				}

				public Enumeration<?> getServletNames()
				{
					return null;
				}

				public String getServletContextName()
				{
					return null;
				}

				public Servlet getServlet( String name )
						throws ServletException
				{
					return null;
				}

				public String getServerInfo()
				{
					return null;
				}

				public Set<?> getResourcePaths( String path )
				{
					return null;
				}

				public InputStream getResourceAsStream( String path )
				{
					return null;
				}

				public URL getResource( String path )
						throws MalformedURLException
				{
					return null;
				}

				public RequestDispatcher getRequestDispatcher( String path )
				{
					return null;
				}

				public String getRealPath( String path )
				{
					return null;
				}

				public RequestDispatcher getNamedDispatcher( String name )
				{
					return null;
				}

				public int getMinorVersion()
				{
					return 0;
				}

				public String getMimeType( String file )
				{
					return null;
				}

				public int getMajorVersion()
				{
					return 0;
				}

				public Enumeration<?> getInitParameterNames()
				{
					return null;
				}

				public String getInitParameter( String name )
				{
					return null;
				}

				public ServletContext getContext( String uripath )
				{
					return this;
				}

				public Enumeration<?> getAttributeNames()
				{
					return null;
				}

				public Object getAttribute( String name )
				{
					return null;
				}

				@SuppressWarnings("unused")
				public String getContextPath()
				{
					return null;
				}
			};

			hudson = new Hudson(new File("target"), ctx);

			TestLinkLatestRevisionInfo latestRevisionInfo = new TestLinkLatestRevisionInfo(
					"", "", "");
			builder = new TestLinkBuilder("No testlink", "No project",
					"No plan", "No build", "class, time", "dir", "dir",
					Boolean.FALSE, latestRevisionInfo, "class",
					"**/TEST-*.xml", "**/testng-results.xml", "**/*.tap");
		} 
		catch (ReactorException e)
		{
			Assert.fail("Failed to created Hudson test instance.", e);
		} 
		catch (IOException e)
		{
			Assert.fail("Failed to created Hudson test instance.", e);
		} 
		catch (InterruptedException e)
		{
			Assert.fail("Failed to created Hudson test instance.", e);
		} 
	}

	/**
	 * Tests the ReportPatterns object.
	 */
	@Test(description = "Tests the ReportPatterns object.")
	public void testReportPatterns()
	{
		ReportFilesPatterns reportPatterns = builder.getReportPatterns();
		Assert.assertNotNull(reportPatterns);
		Assert.assertEquals(reportPatterns.getJunitXmlReportFilesPattern(),
				"**/TEST-*.xml");
		Assert.assertEquals(reportPatterns.getTestNGXmlReportFilesPattern(),
				"**/testng-results.xml");
		Assert.assertEquals(reportPatterns.getTapStreamReportFilesPattern(),
				"**/*.tap");
	}
	/**
	 * Tests the generated list of custom fields.
	 */
	@Test(description="Tests the generated list of custom fields.")
	public void testListOfCustomFields()
	{
		String[] customFieldsNames = builder.getListOfCustomFieldsNames();
		
		Assert.assertNotNull( customFieldsNames );
		Assert.assertTrue( customFieldsNames.length == 2 );
		Assert.assertEquals( customFieldsNames[0], "class" );
		Assert.assertEquals( customFieldsNames[1], "time" );
	}
	
	@Test(description="Null")
	public void testNull()
	{
		builder = new TestLinkBuilder(null, null, null, null, null, null, null, null, null, null, null, null, null);
		
		Assert.assertNotNull( builder );
		
		Assert.assertNull( builder.getTestLinkName() );
		
		Assert.assertNull( builder.getTestProjectName() );
		
		Assert.assertNull( builder.getTestPlanName() );
		
		Assert.assertNull( builder.getBuildName() );
		
		Assert.assertNull( builder.getSingleTestCommand() );
		
		Assert.assertNull( builder.getIterativeTestCommand() );
		
		Assert.assertNull( builder.getLatestRevisionInfo() );
		
		Assert.assertFalse( builder.getLatestRevisionEnabled() );
		
		Assert.assertNull( builder.getCustomFields() );
		
		Assert.assertNull( builder.getTransactional() );
		
		Assert.assertNull( builder.getKeyCustomField() );
		
		Assert.assertNull( builder.getJunitReportFilesPattern() );
		
		Assert.assertNull( builder.getTestNGReportFilesPattern() );
		
		Assert.assertNull( builder.getTapReportFilesPattern() );
		
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
	@Test(description="Tests getters methods.")
	public void testGetters()
	{
		Assert.assertNotNull( hudson );
		//FreeStyleProject project = new FreeStyleProject(hudson, "No project");
		//Assert.assertNotNull ( (AbstractProject<?, ?>)builder.getProjectAction(project) );
		
		Assert.assertNotNull( builder.getTestLinkName() );
		Assert.assertEquals( builder.getTestLinkName(), "No testlink" );
		
		Assert.assertNotNull( builder.getTestProjectName() );
		Assert.assertEquals( builder.getTestProjectName(), "No project" );
		
		Assert.assertNotNull( builder.getTestPlanName() );
		Assert.assertEquals( builder.getTestPlanName(), "No plan" );
		
		Assert.assertNotNull( builder.getBuildName() );
		Assert.assertEquals( builder.getBuildName(), "No build" );
		
		Assert.assertNotNull( builder.getSingleTestCommand() );
		Assert.assertEquals( builder.getSingleTestCommand() , "dir");
		
		Assert.assertNotNull( builder.getIterativeTestCommand() );
		Assert.assertEquals( builder.getIterativeTestCommand(), "dir" );
		
		Assert.assertNotNull( builder.getLatestRevisionInfo() );
		
		Assert.assertTrue( builder.getLatestRevisionEnabled() );
		
		Assert.assertNotNull( builder.getCustomFields() );
		Assert.assertEquals( builder.getCustomFields(), "class, time" );
		
		Assert.assertFalse( builder.getTransactional() );
		
		Assert.assertNotNull( builder.getKeyCustomField() );
		Assert.assertEquals( builder.getKeyCustomField(), "class" );
		
		Assert.assertNotNull( builder.getJunitReportFilesPattern() );
		Assert.assertEquals( builder.getJunitReportFilesPattern(), "**/TEST-*.xml" );
		
		Assert.assertNotNull( builder.getTestNGReportFilesPattern() );
		Assert.assertEquals( builder.getTestNGReportFilesPattern(), "**/testng-results.xml" );
		
		Assert.assertNotNull( builder.getTapReportFilesPattern() );
		Assert.assertEquals( builder.getTapReportFilesPattern(), "**/*.tap" );
	}

	@AfterClass(alwaysRun=true)
	public void tearDown()
	{
		if (hudson != null)
		{
			hudson.cleanUp();
			hudson = null;
		}
	}
	
}
