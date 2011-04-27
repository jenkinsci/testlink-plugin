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
import hudson.model.BuildListener;
import hudson.model.StreamBuildListener;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TestShell 
extends TestCase 
{

	private CommandInterpreter cmd;
	
	private final String command = "commandNotExistent7&";
	
	public void setUp()
	{
		BuildListener listener = new StreamBuildListener(new PrintStream(System.out), Charset.defaultCharset());
		this.cmd = new Shell( command, new EnvVars(), listener );
	}
	
	public void testShellFile()
	{
		FilePath tempScript = null;
		
		try
		{
			tempScript = this.cmd.createScriptFile(new FilePath(new File(".")));
		} 
		catch (IOException e)
		{
			Assert.fail( e.getMessage() );
		} 
		catch (InterruptedException e)
		{
			Assert.fail( e.getMessage() );
		}
		
		Assert.assertNotNull( tempScript );
		
		try
		{
			Assert.assertTrue( tempScript.exists() );
		} 
		catch (IOException e)
		{
			Assert.fail( e.getMessage() );
		} 
		catch (InterruptedException e)
		{
			Assert.fail( e.getMessage() );
		}
		
		try
		{
			Assert.assertTrue( tempScript.delete() );
		} 
		catch (IOException e)
		{
			Assert.fail( e.getMessage() );
		} 
		catch (InterruptedException e)
		{
			Assert.fail( e.getMessage() );
		}
		
	}
	
	public void testExecute()
	{
		try
		{
			Assert.assertFalse( this.cmd.execute(new File(".")));
		} 
		catch (InterruptedException e)
		{
			Assert.fail( e.getMessage() );
		}
	}
	
	public void testCommand()
	{
		Assert.assertEquals(this.command, this.cmd.getCommand() );
	}
	
}
