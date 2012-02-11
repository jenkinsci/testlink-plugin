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
 * Casa Verde - S�o Paulo - SP. Brazil.
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

import hudson.plugins.testlink.result.parser.junit.issue8531.TestIssue8531;
import hudson.plugins.testlink.testng.junit.JUnitParser;
import hudson.plugins.testlink.testng.junit.TestSuite;

import java.io.File;
import java.net.URL;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.jvnet.hudson.test.Bug;

/**
 * Tests the JUnit parser for issue 8683.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
@Bug(8683)
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
		
		List<TestSuite> testSuites = null;
		try
		{
			testSuites = this.parser.parse( junitFile );
		} 
		catch (Exception e)
		{
			Assert.fail("Failed to parse JUnit xml report '"+junitFile+"'.");
		}
		
		Assert.assertNotNull( testSuites );
		
		Assert.assertTrue( testSuites.size() > 0 );
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
		catch ( hudson.plugins.testlink.testng.ParserException parserException )
		{
			
		}
	}
	
}
