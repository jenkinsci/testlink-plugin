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
package hudson.plugins.testlink.tasks;

import hudson.EnvVars;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.plugins.testlink.util.Messages;

import java.io.IOException;

/**
 * This class is responsible for executing commands. These commands are used 
 * to execute the tests retrieved from TestLink. 
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.4
 */
public class CommandExecutor
{

	/**
	 * Hidden to be a utility class.
	 */
	private CommandExecutor()
	{
		super();
	}
	
	/**
	 * Executes a single command.
	 * 
	 * @param command command.
	 * @param isUnix whether it is a unix node or a windows one.
	 * @param envVars environment variables.
	 * 
	 * @return <code>true</code> if all the command was executed correctly, 
	 * <code>false</code> otherwise.
	 */
	public static boolean executeCommand( 
		AbstractBuild<?, ?> build, 
		BuildListener listener,
		boolean isUnix,
		EnvVars envVars, 
		String command )
	{
		boolean r = Boolean.FALSE;
		
		CommandInterpreter cmd = null;
		
		try
		{
			if ( isUnix )
			{
				cmd = new Shell( command, envVars, listener );
			}
			else
			{
				cmd = new BatchFile( command, envVars, listener );
			}
			
			r = build.getWorkspace().act( cmd );
		}  
        catch (InterruptedException e) 
        {
        	e.printStackTrace( listener.fatalError(Messages.TestLinkBuilder_TestCommandError(e.getMessage())) );
        	r = false;
        } catch (IOException e)
		{
        	e.printStackTrace( listener.fatalError(Messages.TestLinkBuilder_TestCommandError(e.getMessage())) );
        	r = false;
		} 
		
		return r;
	}
	
	/**
	 * Executes a command with a pre-action and a post-action. Useful for 
	 * executing actions before and after your test command.
	 * 
	 * @param beforeCommand pre-action.
	 * @param command command.
	 * @param afterCommand post-action.
	 * @param isUnix whether it is a unix node or a windows one.
	 * @param envVars environment variables.
	 * 
	 * @return <code>true</code> if all three commands were executed correctly, 
	 * <code>false</code> otherwise.
	 */
	public static boolean executeCommand( 
		AbstractBuild<?, ?> build, 
		BuildListener listener,
		boolean isUnix,
		EnvVars envVars, 
		String beforeCommand, 
		String command, 
		String afterCommand
	)
	{
		boolean beforeCommandStatus = executeCommand( build, listener, isUnix, envVars, beforeCommand );
		
		boolean commandStatus = executeCommand( build, listener, isUnix, envVars, command );
		
		boolean afterCommandStatus = executeCommand( build, listener, isUnix, envVars, afterCommand );
		
		return beforeCommandStatus && commandStatus && afterCommandStatus;
	}
	
}
