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

import hudson.CopyOnWrite;
import hudson.model.Descriptor;
import hudson.tasks.Builder;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 31/08/2010
 */
public class TestLinkBuilderDescriptor 
extends Descriptor<Builder>
{

	private static final String DISPLAY_NAME = "Invoke Testlink";
	
	@CopyOnWrite
	private volatile TestLinkBuilderInstallation[] installations = 
		new TestLinkBuilderInstallation[0];
	
	public TestLinkBuilderDescriptor()
	{
		super(TestLinkBuilder.class);
		load();
	}

	@Override
	public String getDisplayName()
	{
		return DISPLAY_NAME;
	}
	
	public TestLinkBuilderInstallation[] getInstallations()
	{
		return this.installations;
	}
	
	public TestLinkBuilderInstallation getInstallationByTestLinkName(
		String testLinkName
	)
	{
		TestLinkBuilderInstallation installation = null;
		if ( this.installations != null && this.installations.length > 0 )
		{
			for(TestLinkBuilderInstallation tempInst : this.installations )
			{
				if ( tempInst.getName().equals(testLinkName))
				{
					return tempInst;
				}
			}
		}
		return installation;
	}
	
	@Override
	public boolean configure( StaplerRequest req, JSONObject json )
	throws hudson.model.Descriptor.FormException
	{
		this.installations = 
			req.bindParametersToList(
					TestLinkBuilderInstallation.class,
					"TestLink.").toArray(new TestLinkBuilderInstallation[0]);
		save();
		return true;
	}
	
}
