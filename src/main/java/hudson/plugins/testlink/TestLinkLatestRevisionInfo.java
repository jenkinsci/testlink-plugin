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
 * @since 01/09/2010
 */
package hudson.plugins.testlink;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 01/09/2010
 */
public class TestLinkLatestRevisionInfo
{

	private String svnUrl;
	private String svnUser;
	private String svnPassword;
	
	@DataBoundConstructor
	public TestLinkLatestRevisionInfo(
			String svnUrl, 
			String svnUser,
			String svnPassword)
	{
		super();
		this.svnUrl = svnUrl;
		this.svnUser = svnUser;
		this.svnPassword = svnPassword;
	}
	
	public String getSvnUrl()
	{
		return svnUrl;
	}

	public String getSvnUser()
	{
		return svnUser;
	}

	public String getSvnPassword()
	{
		return svnPassword;
	}

	
}
