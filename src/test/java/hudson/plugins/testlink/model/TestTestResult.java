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
 * @since 2.0
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
