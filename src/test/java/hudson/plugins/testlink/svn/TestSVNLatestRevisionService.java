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
package hudson.plugins.testlink.svn;

import hudson.plugins.testlink.svn.SVNLatestRevisionService;

import java.net.MalformedURLException;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tmatesoft.svn.core.SVNException;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 02/12/2010
 */
public class TestSVNLatestRevisionService
{
	
	private static final String repoUrl = "https://tap4j.svn.sourceforge.net/svnroot/tap4j/";
	private static final String userName = "";
	private static final String password = "";
	private SVNLatestRevisionService service;
	
	@BeforeClass
	public void setUp()
	{
		try
		{
			this.service = new SVNLatestRevisionService(repoUrl, userName, password);
		} 
		catch (MalformedURLException e)
		{
			Assert.fail("Bad SVN Url: " + repoUrl, e);
		}
	}
	
	@Test(testName="Test Get latest SVN Revision and SVN URL")
	public void testGetLatestSVNRevisionAndSvnUrl()
	{
		Assert.assertNotNull( this.service.getRepositoryURL() );
		
		Long revision = Long.valueOf( -1 );
		try
		{
			revision = this.service.getLatestRevision();
		} 
		catch (SVNException e)
		{
			Assert.fail("Failed to get latest revision: " + e.getMessage(), e);
		}
		
		Assert.assertNotNull( revision );
		
		Assert.assertTrue( revision > 0 );
		
	}
	
	@Test(testName="Test Get latest revision with credentials")
	public void testGetLatestRevisionWithCredentials()
	{
		try
		{
			this.service = new SVNLatestRevisionService(repoUrl, "fakeUser", "");
		}
		catch (MalformedURLException e)
		{
			Assert.fail("Bad SVN Url: " + repoUrl, e);
		}
		
		Long revision = Long.valueOf( -1 );
		try
		{
			revision = this.service.getLatestRevision();
		} 
		catch (SVNException e)
		{
			Assert.fail("Failed to get latest revision: " + e.getMessage(), e);
		}
		
		Assert.assertNotNull( revision );
		
		Assert.assertTrue( revision > 0 );
	}

}
