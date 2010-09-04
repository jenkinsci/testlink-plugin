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

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Recorder;

import java.io.IOException;
import java.io.Serializable;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 02/09/2010
 */
public class TestLinkPublisher 
extends Recorder 
implements Serializable
{

	@Extension
	public static final TestLinkPublisherDescriptor DESCRIPTOR = new TestLinkPublisherDescriptor();
	
	@DataBoundConstructor
	public TestLinkPublisher()
	{
		super();
	}
	
	/* (non-Javadoc)
	 * @see hudson.tasks.BuildStep#getRequiredMonitorService()
	 */
	public BuildStepMonitor getRequiredMonitorService()
	{
		return BuildStepMonitor.BUILD;
	}
	
	/**
	 * Defines the project action.
	 */
	@Override
	public Action getProjectAction( AbstractProject<?, ?> project )
	{
		return new TestLinkProjectAction(project);
	}
	
	/* (non-Javadoc)
	 * @see hudson.tasks.BuildStepCompatibilityLayer#perform(hudson.model.AbstractBuild, hudson.Launcher, hudson.model.BuildListener)
	 */
	@Override
	public boolean perform( AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener ) throws InterruptedException, IOException
	{
		listener.getLogger().println("Performing TestLink publisher...");
		return true;
	}

}
