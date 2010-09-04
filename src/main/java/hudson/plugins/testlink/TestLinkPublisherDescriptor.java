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

import hudson.model.AbstractProject;
import hudson.model.Project;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 02/09/2010
 */
public class TestLinkPublisherDescriptor 
extends BuildStepDescriptor<Publisher>
{

	/**
	 * <p>Name that is shown in the build configuration screen. It appears 
	 * in the section "Post build actions".</p> 
	 */
	private static final String DISPLAY_NAME = "Publish TestLink Report";
	
	public TestLinkPublisherDescriptor()
	{
		super(TestLinkPublisher.class);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean isApplicable( Class<? extends AbstractProject> jobType )
	{
		return Project.class.isAssignableFrom(jobType);
	}

	/* (non-Javadoc)
	 * @see hudson.model.Descriptor#getDisplayName()
	 */
	@Override
	public String getDisplayName()
	{
		return DISPLAY_NAME;
	}

}
