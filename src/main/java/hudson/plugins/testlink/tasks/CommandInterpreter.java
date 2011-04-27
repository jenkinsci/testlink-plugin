/*
 * The MIT License
 *
 * Copyright (c) <2011> <Bruno P. Kinoshita>
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
package hudson.plugins.testlink.tasks;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.FilePath.FileCallable;
import hudson.Launcher;
import hudson.Util;
import hudson.model.BuildListener;
import hudson.plugins.testlink.util.Messages;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;

/**
 * Original class: https://github.com/jenkinsci/jenkins/blob/master/core/src/main/java/hudson/tasks/CommandInterpreter.java
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.2.1
 */
public abstract class CommandInterpreter 
implements FileCallable<Boolean>
{
	
	private static final long serialVersionUID = 8618440862720090679L;
	
	protected final String command;
	private final EnvVars envVars;
	private final BuildListener listener;
	
	public CommandInterpreter( String command, EnvVars envVars, BuildListener listener )
	{
		this.command = command;
		this.envVars = envVars;
		this.listener = listener;
	}

	public final String getCommand()
	{
		return command;
	}
	
	public Boolean execute( File workspace )
	throws InterruptedException
	{
		FilePath ws = new FilePath( workspace );
		FilePath script = null;
		
		try
		{
			try
			{
				script = createScriptFile ( ws );
			}
			catch ( IOException e )
			{
				Util.displayIOException(e, listener);
				e.printStackTrace( listener.fatalError(Messages.TestLinkBuilder_TestCommandError(e.getMessage())) );
				return false;
			}
			
			int r;
			try
			{
				Launcher launcher = ws.createLauncher( listener );
				r = launcher.launch().cmds( buildCommandLine( script ) ).envs( envVars ).stdout( listener ).pwd( ws ).join(); 
 			}
			catch ( IOException e )
			{
				Util.displayIOException(e, listener);
				e.printStackTrace( listener.fatalError(Messages.TestLinkBuilder_TestCommandError(e.getMessage())) );
				r = -1;
			}
			return r==0;
		}
		finally
		{
			try
			{
				if ( script != null )
				{
					script.delete();
				}
			} catch (IOException e)
			{
				Util.displayIOException(e, listener);
				e.printStackTrace( listener.fatalError(Messages.TestLinkBuilder_DeleteTempArchiveError(script.getRemote())) );
			}
		}
	}
	
	public FilePath createScriptFile( FilePath dir )
	throws IOException, InterruptedException
	{
		return dir.createTextTempFile( "testlink_plugin", getFileExtension(), getContents(), false );
	}
	
	public abstract String[] buildCommandLine( FilePath script );
	
	protected abstract String getContents();
	
	protected abstract String getFileExtension();
	
	/* (non-Javadoc)
	 * @see hudson.FilePath.FileCallable#invoke(java.io.File, hudson.remoting.VirtualChannel)
	 */
	public Boolean invoke( File f, VirtualChannel channel ) throws IOException,
			InterruptedException
	{
		return this.execute( f );
	}
	
}
