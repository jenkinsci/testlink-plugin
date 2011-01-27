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
 * @since 27/01/2011
 */
package hudson.plugins.testlink.executor;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests the temporary executable script writer.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
public class TestTemporaryExecutableScriptWriter
{
	
	@Test
	public void testExecutableScriptForWindows()
	{
		
		File temporaryScript = new File( "test-output", "testExecutableScript.tmp");
		try
		{
			Assert.assertTrue( temporaryScript.createNewFile() );
		} 
		catch (Exception e1)
		{
			Assert.fail("Failed to create temporary script file '"+temporaryScript+"'", e1);
		}
		
		TemporaryExecutableScriptWriter writer = 
			new TemporaryExecutableScriptWriter(
					temporaryScript.getAbsolutePath(),
					Boolean.FALSE, 
					"dir");
		try
		{
			Assert.assertTrue( writer.invoke(null, null) );
		} catch (IOException e)
		{
			Assert.fail("Failed to write test command to test script", e);
		} catch (InterruptedException e)
		{
			Assert.fail("Failed to write test command to test script", e);
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
					Assert.fail("Failed to write test command to test script", e);
				}
			}
		}
	}
	
	@Test
	public void testExecutableScriptForUnix()
	{
		
		File temporaryScript = new File( "test-output", "testExecutableScript.tmp");
		try
		{
			Assert.assertTrue( temporaryScript.createNewFile() );
		} 
		catch (Exception e1)
		{
			Assert.fail("Failed to create temporary script file '"+temporaryScript+"'", e1);
		}
		
		TemporaryExecutableScriptWriter writer = 
			new TemporaryExecutableScriptWriter(
					temporaryScript.getAbsolutePath(),
					Boolean.TRUE, 
					"ls");
		try
		{
			Assert.assertTrue( writer.invoke(null, null) );
		} catch (IOException e)
		{
			Assert.fail("Failed to write test command to test script", e);
		} catch (InterruptedException e)
		{
			Assert.fail("Failed to write test command to test script", e);
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
					Assert.fail("Failed to write test command to test script", e);
				}
			}
		}
	}

}
