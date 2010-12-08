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
 * @since 22/11/2010
 */
package hudson.plugins.testlink.scanner;

import hudson.Util;
import hudson.model.BuildListener;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 22/11/2010
 */
public class Scanner 
implements Serializable
{
	
	protected BuildListener listener;
	
	public Scanner( BuildListener listener )
	{
		super();
		this.listener = listener;
	}
	
	public String[] scanForTestResults( File baseDir, String includes ) 
	throws IOException
	{
		
		if ( StringUtils.isBlank( includes ) )
		{
			return new String[0];
		}
		
		String[] fileNames = new String[0];
		
		FileSet fs = null;
		try
		{
			fs = Util.createFileSet(baseDir, includes);
		}
		catch ( Exception e ) // TBD: find out which exception is thrown
		{
			e.printStackTrace( listener.getLogger() );
			throw new IOException( "Failed to open base directory to look for reports: " + e.getMessage(), e );
		}
		
		DirectoryScanner ds = fs.getDirectoryScanner();
		
		fileNames = ds.getIncludedFiles();
		
		return fileNames;
		
	}
	
}
