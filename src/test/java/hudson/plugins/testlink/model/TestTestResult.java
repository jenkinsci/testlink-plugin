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
 * @since 29/11/2010
 */
package hudson.plugins.testlink.model;

import hudson.plugins.testlink.model.TestResult;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import br.eti.kinoshita.testlinkjavaapi.model.Attachment;
import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 29/11/2010
 */
public class TestTestResult 
{

	protected TestCase testCase;
	protected Build build;
	protected TestPlan testPlan;
	
	protected TestResult testResult;
	
	@BeforeClass
	protected void setUp()
	{
		testCase = new TestCase();
		testCase.setId( 100 );
		build = new Build();
		build.setId( 100 );
		testPlan = new TestPlan();
		testPlan.setId( 100 );
		testResult = new TestResult(testCase, build, testPlan);
	}
	
	@Test(testName="Test TestResult TestCase")
	public void testTestResultTestCase()
	{
		Assert.assertNotNull( testResult.getTestCase() );
		
		Assert.assertTrue( testResult.getTestCase().getId() == 100 );
		
		TestCase testCase2 = new TestCase();
		testCase2.setId( 101 );
		testResult.setTestCase( testCase2 );
		
		Assert.assertTrue( testResult.getTestCase().getId() == 101 );
	}
	
	@Test(testName="Test TestResult Build")
	public void testTestResultBuild()
	{
		Assert.assertNotNull( testResult.getBuild() );
		
		Assert.assertTrue( testResult.getBuild().getId() == 100 );
		
		Build build2 = new Build();
		build2.setId( 101 );
		testResult.setBuild( build2 );
		
		Assert.assertTrue( testResult.getBuild().getId() == 101 );
		
	}
	
	@Test(testName="Test TestResult TestPlan")
	public void testTestResultTestPlan()
	{
		Assert.assertNotNull( testResult.getTestPlan() );
		
		Assert.assertTrue( testResult.getTestPlan().getId() == 100 );
		
		TestPlan testPlan2 = new TestPlan();
		testPlan2.setId( 101 );
		testResult.setTestPlan( testPlan2 );
		
		Assert.assertTrue( testResult.getTestPlan().getId() == 101 );
	}
	
	@Test(testName="Test TestResult notes")
	public void testTestResultNotes()
	{
		Assert.assertNull( this.testResult.getNotes() );
		
		String newNotes = "Home sweet home";
		this.testResult.setNotes( newNotes );
		
		Assert.assertNotNull( this.testResult.getNotes() );
	}
	
	@Test(testName="Test TestResult Attachments")
	public void testTestResultAttachments()
	{
		Assert.assertNotNull( testResult.getAttachments() );
		
		Assert.assertEquals ( testResult.getAttachments().size(), 0 );
		
		Attachment attachment = new Attachment();
		testResult.getAttachments().add( attachment );
		
		Assert.assertEquals( testResult.getAttachments().size(), 1 );
		
		attachment = new Attachment();
		testResult.addAttachment( attachment );
		
		Assert.assertEquals( testResult.getAttachments().size(), 2 );
	}
	
	@Test(testName="Test TestResult toString() method")
	public void testTestResultToString()
	{
		String toStringResult = testResult.toString();
		
		Assert.assertNotNull( toStringResult );
		
		Assert.assertTrue( toStringResult.startsWith("TestResult [testCase=") );
	}
	
}
