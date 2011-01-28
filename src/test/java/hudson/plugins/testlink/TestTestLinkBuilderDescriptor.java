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

import hudson.model.Hudson;
import hudson.util.FormValidation;

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
import org.testng.annotations.Test;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
public class TestTestLinkBuilderDescriptor
{

	@Test
	public void testLinkBuilderDescriptor()
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
		};
		Hudson parent = null;
		try
		{
			parent = new Hudson(new File("target"), ctx);
		} catch (IOException e)
		{
			Assert.fail("Failed to create Hudson objects", e);
		} catch (InterruptedException e)
		{
			Assert.fail("Failed to create Hudson objects", e);
		} catch (ReactorException e)
		{
			Assert.fail("Failed to create Hudson objects", e);
		}
		Assert.assertNotNull( parent );
		
		TestLinkBuilderDescriptor descriptor = new TestLinkBuilderDescriptor();
		Assert.assertEquals( descriptor.getDisplayName(), "Invoke TestLink" );
		
		FormValidation formVal = descriptor.doCheckMandatory("Test");
		Assert.assertNotNull( formVal );
		
		try
		{
			parent.doQuietDown();
		}
		catch (IOException e)
		{
			Assert.fail("Failed to put Hudson down.", e);
		}
	}
	
}
