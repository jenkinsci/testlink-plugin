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
 * @since 02/09/2010
 */
package hudson.plugins.testlink;

import hudson.model.AbstractBuild;

import java.io.Serializable;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 02/09/2010
 */
public class TestLinkResult 
implements Serializable
{
	
	private TestLinkReport report;
	private AbstractBuild<?, ?> build;

	public TestLinkResult(TestLinkReport report, AbstractBuild<?, ?> build)
	{
		this.report = report;
		this.build = build;
	}
	
	public AbstractBuild<?, ?> getOwner()
	{
		return this.build;
	}
	
	public TestLinkReport getReport()
	{
		return this.report;
	}
	
}
