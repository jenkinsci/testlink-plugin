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

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import hudson.plugins.testlink.model.TestLinkLatestRevisionInfo;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 02/12/2010
 */
public class TestTestLinkLatestRevisionInfo 
{

	private TestLinkLatestRevisionInfo latestRevisionInfo;
	
	private static final String SVN_URL = "svn://houston/project";
	private static final String SVN_USER = "user";
	private static final String SVN_PASS = "pass";
	
	@BeforeClass
	public void setUp()
	{
		this.latestRevisionInfo = 
			new TestLinkLatestRevisionInfo( SVN_URL, SVN_USER, SVN_PASS );
	}
	
	@Test(testName="Test getters and setters")
	public void testGetLatestRevisionInfo()
	{
		String url = this.latestRevisionInfo.getSvnUrl();
		String user = this.latestRevisionInfo.getSvnUser();
		String pass = this.latestRevisionInfo.getSvnPassword();
		
		Assert.assertEquals( SVN_URL, url );
		Assert.assertEquals( SVN_USER, user );
		Assert.assertEquals( SVN_PASS, pass );
	}
	
}
