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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jvnet.hudson.reactor.ReactorException;
import org.testng.Assert;
import org.testng.annotations.Test;

import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.model.TestProject;

/**
 * Tests the ChartUtil class.
 * 
 * @see {@link ChartUtil}
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.0
 */
public class TestChartUtil
{

	/**
	 * Tests the hidden constructor.
	 */
	@Test(description="Tests the hidden constructor.")
	public void testConstructor()
	{
		try
		{
			final Constructor<?> c = ChartUtil.class.getDeclaredConstructors()[0];
			c.setAccessible(true);
			final Object o = c.newInstance((Object[]) null);

			Assert.assertNotNull(o);
		}
		catch (Exception e)
		{
			Assert.fail("Failed to instantiate constructor: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Tests createXYDataset() and buildXYChart() methods.
	 */
	@Test(description="Tests createXYDataset() and buildXYChart() methods.")
	public void testChartUtil()
	{
		TestProject project = new TestProject(
				1, 
				"My project", 
				"MP", 
				"Notes about my project", 
				Boolean.TRUE, 
				Boolean.TRUE, 
				Boolean.TRUE, 
				Boolean.TRUE, 
				Boolean.TRUE, 
				Boolean.TRUE);
		
		TestPlan plan = new TestPlan(
				1, 
				"My plan", 
				"My project", 
				"Notes about my project", 
				Boolean.TRUE, 
				Boolean.TRUE
		);
		
		Build build = new Build();
		build.setId(1);
		build.setName("My build");
		build.setNotes("Notes about my build");
		build.setTestPlanId(1);
		
		TestLinkReport report = new TestLinkReport(build, plan, project);
		
		Hudson parent = null;
		
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
			
			parent = new Hudson(new File("target"), ctx);
			
			FreeStyleProject hudsonProject = new FreeStyleProject(parent, "My project");
			FreeStyleBuild hudsonBuild1 = null;
			FreeStyleBuild hudsonBuild2 = null;
			
			try
			{
				hudsonBuild1 = hudsonProject.createExecutable();
				hudsonBuild1.number = 1;
				
				hudsonBuild2 = hudsonProject.createExecutable();
				hudsonBuild2.number = 2;
				
			} catch (IOException e)
			{
				Assert.fail("Failed to create Hudson objects", e);
			}
			
			Assert.assertTrue( hudsonBuild2.getPreviousBuild().equals(hudsonBuild1));
			
			TestLinkResult result = new TestLinkResult(report, hudsonBuild1);
			
			TestLinkBuildAction action1 = new TestLinkBuildAction(hudsonBuild1, result);
			hudsonBuild1.addAction(action1);
			TestLinkBuildAction action2 = new TestLinkBuildAction(hudsonBuild2, result);
			hudsonBuild2.addAction(action2);
			
			Assert.assertTrue( action2.getPreviousAction().equals(action1) );
			
			XYDataset dataset = ChartUtil.createXYDataset(action2);
			JFreeChart chart = ChartUtil.buildXYChart(dataset);
			Assert.assertNotNull( chart );
			
		} 
		catch (IOException e)
		{
			Assert.fail("Failed to created Hudson test instance:" + e.getMessage());
		} 
		catch (InterruptedException e)
		{
			Assert.fail("Failed to created Hudson test instance:" + e.getMessage());
		} 
		catch (ReactorException e)
		{
			Assert.fail("Failed to created Hudson test instance:" + e.getMessage());
		} 
		finally
		{
			if ( parent != null )
			{
				parent.cleanUp();
				parent = null;
			}
		}
	}	
	
}
