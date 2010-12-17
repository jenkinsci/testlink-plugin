/* 
 * The MIT License
 * 
 * Copyright (c) 2010 Bruno P. Kinoshita <http://www.kinoshita.eti.br>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.plugins.testlink.executor;

import hudson.FilePath.FileCallable;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.0
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
