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
 * @since 31/08/2010
 */
package hudson.plugins.testlink;

import java.io.Serializable;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Represents the TestLink installation in Hudson global configuration. 
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TestLinkBuilderInstallation 
implements Serializable
{

	/**
	 * Name of the installation
	 */
	private String name;
	
	/**
	 * TestLink URL
	 */
	private String url;
	
	/**
	 * A valid user dev key
	 */
	private String devKey;
	
	@DataBoundConstructor
	public TestLinkBuilderInstallation(
		String name, 
		String url, 
		String devKey
	)
	{
		this.name = name;
		this.url = url;
		this.devKey = devKey;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public String getUrl()
	{
		return this.url;
	}
	
	public String getDevKey()
	{
		return this.devKey;
	}
	
}
