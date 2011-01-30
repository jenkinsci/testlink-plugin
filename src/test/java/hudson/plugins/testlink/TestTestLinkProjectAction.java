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
package hudson.plugins.testlink;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Hudson;
import hudson.plugins.testlink.result.TestLinkReport;
import hudson.plugins.testlink.result.TestLinkResult;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.jvnet.hudson.reactor.ReactorException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests the TestLinkProjectAction class.
 * 
 * @see {@link TestLinkProjectAction}
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
public class TestTestLinkProjectAction
{
	
	private Hudson parent;
	
	private ServletContext ctx;
	
	private FreeStyleProject project;
	
	private TestLinkProjectAction action;
	
	/**
	 * Prepares for the tests.
	 */
	@BeforeClass
	public void setUp()
	{
		Locale.setDefault(new Locale("en", "US"));
		
		ctx = new ServletContext()
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
			
			public Servlet getServlet( String name ) throws ServletException
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
			
			public URL getResource( String path ) throws MalformedURLException
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

			public String getContextPath()
			{
				return null;
			}
		};
		try
		{
			parent = new Hudson(new File("target"), ctx);
		}
		catch ( ReactorException e  )
		{
			Assert.fail("Failed to created Jenkins test instance.", e);
		} 
		catch (IOException e)
		{
			Assert.fail("Failed to created Jenkins test instance.", e);
		} 
		catch (InterruptedException e)
		{
			Assert.fail("Failed to created Jenkins test instance.", e);
		}
		project = new FreeStyleProject(parent, "My project");
		action = new TestLinkProjectAction(project);
	}

	/**
	 * Tests a TestLinkProjectAction object. 
	 */
	@Test()
	public void testObject()
	{
	
		Assert.assertNotNull( action );
		
		Assert.assertEquals( action.getDisplayName(), "TestLink Results" );
		
		Assert.assertEquals( action.getIconFileName(), "/plugin/testlink/icons/testlink-24.png" );
		
		Assert.assertEquals( action.getUrlName(), "testLinkResult" );
		
		Assert.assertFalse( action.hasValidResults() );
		
		Assert.assertEquals( action.getSearchUrl(), "testLinkResult" );
			
	}
	
	@Test
	public void testWithJenkinsObjects()
	{
		FreeStyleBuild hudsonBuild1 = null;
		FreeStyleBuild hudsonBuild2 = null;
		
		try
		{
			hudsonBuild1 = project.createExecutable();
			hudsonBuild1.number = 1;
			TestLinkReport report = new TestLinkReport();
			TestLinkResult result = new TestLinkResult(report, hudsonBuild1);
			TestLinkBuildAction buildAction = new TestLinkBuildAction(hudsonBuild1, result);
			hudsonBuild1.addAction(buildAction);
			hudsonBuild1.run();
			
			hudsonBuild2 = project.createExecutable();
			TestLinkReport report2 = new TestLinkReport();
			TestLinkResult result2 = new TestLinkResult(report2, hudsonBuild1);
			TestLinkBuildAction buildAction2 = new TestLinkBuildAction(hudsonBuild1, result2);
			hudsonBuild2.addAction(buildAction2);
			hudsonBuild2.run();
			
		} catch (IOException e)
		{
			Assert.fail("Failed to create Jenkins objects", e);
		}
		
		Assert.assertFalse( action.hasValidResults() );
	}
	
}
