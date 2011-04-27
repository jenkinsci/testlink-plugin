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
 * @since 13/02/2011
 */
package hudson.plugins.testlink.result.parser.junit.issue8683;

import hudson.plugins.testlink.parser.junit.JUnitParser;
import hudson.plugins.testlink.parser.junit.TestSuite;
import hudson.plugins.testlink.result.parser.junit.issue8531.TestIssue8531;

import java.io.File;
import java.net.URL;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Tests the JUnit parser for issue 8683.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
public class TestIssue8683 
extends TestCase
{

	/**
	 * The JUnit parser.
	 */
	private JUnitParser parser;
	
	/**
	 * Initializes the JUnit parser.
	 */
	public void setUp()
	{
		this.parser = new JUnitParser();
	}
	
	public void testJUnitParserIssue8683ValidSuite()
	{
		Assert.assertEquals(this.parser.getName(), "JUnit");
		
		ClassLoader cl = TestIssue8531.class.getClassLoader();
		URL url = cl.getResource("hudson/plugins/testlink/result/parser/junit/issue8683/TEST-valid.xml");
		File junitFile = new File( url.getFile() );
		
		TestSuite testSuite = null;
		try
		{
			testSuite = this.parser.parse( junitFile );
		} 
		catch (Exception e)
		{
			Assert.fail("Failed to parse JUnit xml report '"+junitFile+"'.");
		}
		
		Assert.assertNotNull( testSuite );
	}
	
	public void testJUnitParserIssue8683InvalidSuite() 
	{
		Assert.assertEquals(this.parser.getName(), "JUnit");
		
		ClassLoader cl = TestIssue8531.class.getClassLoader();
		URL url = cl.getResource("hudson/plugins/testlink/result/parser/junit/issue8683/TEST-invalid.xml");
		File junitFile = new File( url.getFile() );
		
		try
		{
			this.parser.parse( junitFile );
			
			Assert.fail("Not supposed to get here.");
		} 
		catch ( hudson.plugins.testlink.parser.ParserException parserException )
		{
			
		}
	}
	
}
