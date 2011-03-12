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

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;

/**
 * Tests the temporary executable script writer.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
public class TestTemporaryExecutableScriptWriter 
extends TestCase
{
	
	public void testExecutableScriptForWindows()
	{
		
		File temporaryScript = new File( "target", "testExecutableScript.tmp");
		try
		{
			assertTrue( temporaryScript.createNewFile() );
		} 
		catch (Exception e1)
		{
			fail("Failed to create temporary script file '"+temporaryScript+"': " + e1.getMessage());
		}
		
		TemporaryExecutableScriptWriter writer = 
			new TemporaryExecutableScriptWriter(
					temporaryScript.getAbsolutePath(),
					Boolean.FALSE, 
					"dir");
		try
		{
			assertTrue( writer.invoke(null, null) );
		} catch (IOException e)
		{
			fail("Failed to write test command to test script: " + e.getMessage());
		} catch (InterruptedException e)
		{
			fail("Failed to write test command to test script: " + e.getMessage());
		}
		finally
		{
			if ( temporaryScript != null )
			{
				try
				{
					FileUtils.forceDelete(temporaryScript);
				} 
				catch (IOException e)
				{
					fail("Failed to write test command to test script: " + e.getMessage());
				}
			}
		}
	}
	
	public void testExecutableScriptForUnix()
	{
		
		File temporaryScript = new File( "target", "testExecutableScript.tmp");
		try
		{
			assertTrue( temporaryScript.createNewFile() );
		} 
		catch (Exception e1)
		{
			fail("Failed to create temporary script file '"+temporaryScript+"': " + e1.getMessage());
		}
		
		TemporaryExecutableScriptWriter writer = 
			new TemporaryExecutableScriptWriter(
					temporaryScript.getAbsolutePath(),
					Boolean.TRUE, 
					"ls");
		try
		{
			assertTrue( writer.invoke(null, null) );
		} catch (IOException e)
		{
			fail("Failed to write test command to test script: " + e.getMessage());
		} catch (InterruptedException e)
		{
			fail("Failed to write test command to test script: " + e.getMessage());
		}
		finally
		{
			if ( temporaryScript != null )
			{
				try
				{
					FileUtils.forceDelete(temporaryScript);
				} 
				catch (IOException e)
				{
					fail("Failed to write test command to test script: " + e.getMessage());
				}
			}
		}
	}

}
