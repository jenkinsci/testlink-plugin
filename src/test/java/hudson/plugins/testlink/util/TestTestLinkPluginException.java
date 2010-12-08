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
 * @since 02/12/2010
 */
package hudson.plugins.testlink.util;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 02/12/2010
 */
public class TestTestLinkPluginException
{

	private TestLinkPluginException exception;
	
	@Test(testName="Test TestLink Plug-in Exception")
	public void testTestLinkPluginException()
	{
		this.exception = new TestLinkPluginException();
		
		Assert.assertNotNull( this.exception );
		
		this.exception = new TestLinkPluginException("Lamen");
		
		Assert.assertNotNull( this.exception );
		Assert.assertEquals( this.exception.getMessage(), "Lamen" );
		
		this.exception = new TestLinkPluginException( new NullPointerException() );
		
		Assert.assertNotNull( this.exception );
		Assert.assertNotNull( this.exception.getCause() );
		
		this.exception = new TestLinkPluginException("Lamen", new NullPointerException() );
		
		Assert.assertNotNull( this.exception );
		Assert.assertNotNull( this.exception.getCause() );
		Assert.assertEquals( this.exception.getMessage(), "Lamen" );
	}
	
}
