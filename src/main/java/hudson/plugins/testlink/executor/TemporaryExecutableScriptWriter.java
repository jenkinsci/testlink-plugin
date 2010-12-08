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
 * @since 03/12/2010
 */
package hudson.plugins.testlink.executor;

import hudson.FilePath.FileCallable;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 03/12/2010
 */
public class TemporaryExecutableScriptWriter 
implements FileCallable<Boolean>
{

	private String scriptPath;
	private Boolean isUnix;
	private String testCommand;
	
	private static final String WINDOWS_SCRIPT_HEADER = "@ECHO OFF";
	
	public TemporaryExecutableScriptWriter( String scriptPath, Boolean isUnix, String testCommand )
	{
		this.scriptPath = scriptPath;
		this.isUnix = isUnix;
		this.testCommand = testCommand;
		
	}
	
	public Boolean writeTestCommandToTestScript() 
	throws IOException, InterruptedException
	{
		FileWriter fileWriter = null;
		
		try
		{
			fileWriter = new FileWriter( scriptPath );
			
			if ( ! isUnix )
			{
				fileWriter.write( WINDOWS_SCRIPT_HEADER + System.getProperty("line.separator") );
			}
			
			fileWriter.write( this.testCommand );
			fileWriter.flush();
		}
		finally 
		{
			if ( fileWriter != null )
			{
				fileWriter.close();
			}
		}
		
		return Boolean.TRUE;
	}
	
	/* (non-Javadoc)
	 * @see hudson.FilePath.FileCallable#invoke(java.io.File, hudson.remoting.VirtualChannel)
	 */
	public Boolean invoke( File f, VirtualChannel channel ) 
	throws IOException, InterruptedException
	{
		return this.writeTestCommandToTestScript();		
	}

}
