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
import hudson.Util;
import hudson.model.BuildListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Original class: https://github.com/jenkinsci/jenkins/blob/master/core/src/main/java/hudson/tasks/Shell.java
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.2.1
 */
public class Shell 
extends CommandInterpreter 
{

	private static final long serialVersionUID = 1926502324642635847L;

	/**
	 * @param command Command.
	 * @param envVars Environment Variables.
	 * @param listener Jenkins build listener.
	 */
	public Shell(String command, EnvVars envVars, BuildListener listener)
	{
		super(command, envVars, listener);
	}

	private static String fixCrLf( String s )
	{
		int idx;
		while( (idx = s.indexOf("\r\n")) != -1 )
		{
			s = s.substring(0, idx)+s.substring(idx+1);
		}
		return s;
	}
	
	private static String addCrForNonASCII(String s )
	{
		if(!s.startsWith("#!"))
		{
			if(s.indexOf('\n') != 0)
			{
				return "\n" + s;
			}
		}
		
		return s;
	}
	
	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.tasks.CommandInterpreter#buildCommandLine(hudson.FilePath)
	 */
	@Override
	public String[] buildCommandLine( FilePath script )
	{
		if( command.startsWith("#!") )
		{
			int end = command.indexOf('\n');
			if ( end < 0 )
			{
				end = command.length();
			}
			List<String> args = new ArrayList<String>();
			args.addAll(Arrays.asList(Util.tokenize(command.substring(0, end).trim())));
			args.add(script.getRemote());
			args.set(0, args.get(0).substring(2)); // trim off "#!"
			return args.toArray( new String[args.size()] );
		}
		else
		{
			return new String[] { "/bin/sh", "-xe", script.getRemote() };
		}
	}
	
	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.tasks.CommandInterpreter#getContents()
	 */
	@Override
	protected String getContents()
	{
		return addCrForNonASCII(fixCrLf(command));
	}
	
	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.tasks.CommandInterpreter#getFileExtension()
	 */
	@Override
	protected String getFileExtension()
	{
		return ".sh";
	}
	
}
