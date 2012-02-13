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
import hudson.plugins.testlink.result.Report;

import java.io.IOException;
import java.util.Locale;

import org.jvnet.hudson.test.HudsonTestCase;

/**
 * Tests the TestLinkProjectAction class.
 * 
 * @see {@link TestLinkProjectAction}
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
public class TestTestLinkProjectAction 
extends HudsonTestCase
{

	private FreeStyleProject project;
	
	private TestLinkProjectAction action;
	
	/**
	 * Prepares for the tests.
	 */
	public void setUp() 
	throws Exception
	{
		super.setUp();
		
		Locale.setDefault(new Locale("en", "US"));
		
		project = createFreeStyleProject( "My project" );
		action = new TestLinkProjectAction(project);
	}

	/**
	 * Tests a TestLinkProjectAction object. 
	 */
	public void testObject()
	{
	
		assertNotNull( action );
		
		assertEquals( action.getDisplayName(), "TestLink results" );
		
		assertEquals( action.getIconFileName(), "/plugin/testlink/icons/testlink-24.png" );
		
		assertEquals( action.getUrlName(), "testLinkResult" );
		
		assertFalse( action.isDisplayGraph() );
		
		assertEquals( action.getSearchUrl(), "testLinkResult" );
			
	}
	
	public void testWithJenkinsObjects()
	{
		FreeStyleBuild hudsonBuild1 = null;
		FreeStyleBuild hudsonBuild2 = null;
		
		try
		{
			hudsonBuild1 = project.createExecutable();
			hudsonBuild1.number = 1;
			Report report = new Report();
			TestLinkResult result = new TestLinkResult(report, hudsonBuild1);
			TestLinkBuildAction buildAction = new TestLinkBuildAction(hudsonBuild1, result);
			hudsonBuild1.addAction(buildAction);
			hudsonBuild1.run();
			
			hudsonBuild2 = project.createExecutable();
			Report report2 = new Report();
			TestLinkResult result2 = new TestLinkResult(report2, hudsonBuild1);
			TestLinkBuildAction buildAction2 = new TestLinkBuildAction(hudsonBuild1, result2);
			hudsonBuild2.addAction(buildAction2);
			hudsonBuild2.run();
			
		} catch (IOException e)
		{
			fail("Failed to create Jenkins objects: " + e.getMessage());
		}
		
		assertTrue( action.isDisplayGraph() );
	}
	
}
