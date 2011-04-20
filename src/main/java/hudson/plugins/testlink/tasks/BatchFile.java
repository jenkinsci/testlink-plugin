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

import hudson.FilePath;

/**
 * Original class: https://github.com/jenkinsci/jenkins/blob/master/core/src/main/java/hudson/tasks/BatchFile.java#L48
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.2.1
 */
public class BatchFile 
extends CommandInterpreter
{

	public BatchFile( String command )
	{
		super( command );
	}
	
	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.tasks.CommandInterpreter#buildCommandLine(hudson.FilePath)
	 */
	@Override
	public String[] buildCommandLine( FilePath script )
	{
		return new String[] {"cmd", "/c", "call", script.getRemote()};
	}
	
	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.tasks.CommandInterpreter#getContents()
	 */
	@Override
	protected String getContents()
	{
		return command + "\r\nexit %ERRORLEVEL%";
	}
	
	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.tasks.CommandInterpreter#getFileExtension()
	 */
	@Override
	protected String getFileExtension()
	{
		return ".bat";
	}
	
}
