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
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.plugins.testlink.model.TestLinkParser;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Recorder;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * <p>An extension point to execute a post build report generation for CCM.</p>
 * 
 * <p>It defines {@link TestLinkProjectAction} as Project Action and 
 * {@link TestLinkBuildAction} as an action for each build.</p>
 * 
 * <p>This publisher is not executed when the build status is ABORTED or 
 * FAILURE.</p>
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 02/09/2010
 */
@SuppressWarnings("unchecked")
public class TestLinkPublisher 
extends Recorder 
implements Serializable
{

	/**
	 * Descriptor of this publisher
	 */
	@Extension
	public static final TestLinkPublisherDescriptor DESCRIPTOR = new TestLinkPublisherDescriptor();
	
	/**
	 * No args constructor as this published does not need any parameter
	 */
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
	
	/**
	 * TestLink plug-in doesn't need to continue if the build's status is 
	 * ABORTED or FAILURE.
	 * 
	 * @param result
	 * @return true if build status is not ABORTED or FAILURE.
	 */
	protected boolean canContinue(final Result result) {
        return result != Result.ABORTED && result != Result.FAILURE;
    }
	
	/* (non-Javadoc)
	 * @see hudson.tasks.BuildStepCompatibilityLayer#perform(hudson.model.AbstractBuild, hudson.Launcher, hudson.model.BuildListener)
	 */
	@Override
	public boolean perform( AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener ) throws InterruptedException, IOException
	{
		listener.getLogger().println("Performing TestLink publisher...");
		// if build's status is not ABORTED or FAILURE
		if ( this.canContinue(build.getResult()) )
		{
			FilePath workspace = build.getWorkspace();
			PrintStream logger = listener.getLogger();
			
			TestLinkParser parser = new TestLinkParser(logger);
			TestLinkReport report;
			
			listener.getLogger().println("Generating report...");
			try{
                report = workspace.act(parser);
            
            }catch(IOException ioe){
                ioe.printStackTrace(logger);
                return false;
            
            }catch(InterruptedException ie){
                ie.printStackTrace(logger);
                return false;
            }
            
            TestLinkResult result = new TestLinkResult(report, build);
            TestLinkBuildAction buildAction = new TestLinkBuildAction(build, result);
            build.addAction( buildAction );
            
		} else {
			listener.getLogger().println("Canceling TestLink publisher. Wrong project status.");
		}
		return true;
	}

}
