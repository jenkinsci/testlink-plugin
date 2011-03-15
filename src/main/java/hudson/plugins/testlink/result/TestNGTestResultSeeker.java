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
package hudson.plugins.testlink.result;

import hudson.model.BuildListener;
import hudson.plugins.testlink.result.parser.testng.Class;
import hudson.plugins.testlink.result.parser.testng.Suite;
import hudson.plugins.testlink.result.parser.testng.Test;
import hudson.plugins.testlink.result.parser.testng.TestMethod;
import hudson.plugins.testlink.result.parser.testng.TestNGParser;
import hudson.plugins.testlink.result.scanner.Scanner;
import hudson.plugins.testlink.util.ParserException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.eti.kinoshita.testlinkjavaapi.model.Attachment;
import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;

/**
 * This class is responsible 
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1.1
 */
public class TestNGTestResultSeeker 
extends TestResultSeeker
{

	protected final TestNGParser parser = new TestNGParser();
	
	/**
	 * Constructor.
	 * 
	 * @param report TestLink Report.
	 * @param keyCustomFieldName Name of the Key custom field.
	 * @param listener Hudson Build listener.
	 */
	public TestNGTestResultSeeker(TestLinkReport report,
			String keyCustomFieldName, BuildListener listener)
	{
		super(report, keyCustomFieldName, listener);
	}

	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.result.TestResultSeeker#seek(java.io.File, java.lang.String)
	 */
	@Override
	public List<TestResult> seek( File directory, String includePattern )
			throws TestResultSeekerException
	{
		final List<TestResult> results = new ArrayList<TestResult>();
		
		if ( StringUtils.isBlank(includePattern) ) // skip TestNG
		{
			listener.getLogger().println( "Empty TestNG include pattern. Skipping TestNG test results." );
		}
		else
		{
			try
			{
				final Scanner scanner = new Scanner();
				
				String[] testNGReports = scanner.scan(directory, includePattern, listener);
				
				listener.getLogger().println( "Found ["+testNGReports.length+"] TestNG reports." );
				
				this.doTestNGReports( directory, testNGReports, results );
			} 
			catch (IOException e)
			{
				throw new TestResultSeekerException( "IO Error scanning for include pattern ["+includePattern+"]: " + e.getMessage(), e );
			}
			catch( Throwable t ) 
			{
				throw new TestResultSeekerException( "Unkown internal error. Please, open an issue in Jenkins JIRA with the complete stack trace.", t );
			}
		}
		
		return results;
	}

	/**
	 * Parses TestNG report files to look for Test Results of TestLink 
	 * Automated Test Cases.
	 * 
	 * @param directory Directory where to search for.
	 * @param testNGReports Array of TestNG report files.
	 * @param testResults List of Test Results.
	 */
	protected void doTestNGReports( 
		File directory, 
		String[] testNGReports, 
		List<TestResult> testResults)
	{
		
		for ( int i = 0 ; i < testNGReports.length ; ++i )
		{
			listener.getLogger().println( "Parsing ["+testNGReports[i]+"]." );
			
			File testNGFile = new File(directory, testNGReports[i]);
			
			try
			{
				final Suite testNGSuite = parser.parse( testNGFile );
				
				this.doTestNGSuite( testNGSuite, testNGFile, testResults );
			}
			catch ( ParserException e )
			{
				listener.getLogger().println( "Failed to parse TestNG report ["+testNGFile+"]: " + e.getMessage() );
				e.printStackTrace( listener.getLogger() );
			}
		}
	}
	
	/**
	 * Inspects a TestNG test suite looking for test results for the automated 
	 * test cases in TestLink. When it finds a test result, this test result 
	 * is added to the List of Test Results.
	 * 
	 * @param testNGSuite TestNG test suite.
	 * @param testNGFile TestNG file (added as an attachment for each test result 
	 * 				    found).
	 * @param testResults List of Test Results.
	 */
	protected void doTestNGSuite( 
		Suite testNGSuite, 
		File testNGFile, 
		List<TestResult> testResults ) 
	{
		listener.getLogger().println( "Inspecting TestNG suite ["+testNGSuite.getName()+"]. This suite contains ["+testNGSuite.getTests()+"] tests." );
		
		final List<Test> testNGTests = testNGSuite.getTests();
		
		for( Test testNGTest : testNGTests )
		{
			final List<hudson.plugins.testlink.result.parser.testng.Class> classes = 
				testNGTest.getClasses();
			
			listener.getLogger().println( "Processing TestNG test ["+testNGTest.getName()+"]. This test contains ["+classes.size()+"] test classes." );
			
			for ( hudson.plugins.testlink.result.parser.testng.Class clazz : classes )
			{
				listener.getLogger().println( "Processing TestNG test class ["+clazz.getName() +"].");
				
				final TestResult testResult = this.doFindTestResult( testNGSuite, clazz, testNGFile );
				
				if ( testResult != null )
				{
					listener.getLogger().println( "Found TestLink Automated Test Case result in TestNG test ["+testNGTest.getName()+"], class ["+clazz.getName()+"]. Status: ["+testResult.getTestCase().getExecutionStatus().toString()+"]." );
					testResults.add( testResult );
				}
				else
				{
					listener.getLogger().println( "Could not find test TestLink Automated Test Case result in TestNG test ["+testNGTest.getName()+"], class ["+clazz.getName()+"]." );
				}
			}
			
		}
	}
	
	/**
	 * Tries to find the Test Result for a given TestNG test class. This method 
	 * utilizes the testNGFile to add it as an attachment to the test result.
	 * 
	 * @param testNGSuite TestNG Suite.
	 * @param clazz TestNG test class.
	 * @param testNGFile TestNG test file.
	 * @return a Test Result or <code>null</code> if unable to find it.
	 */
	protected TestResult doFindTestResult( Suite testNGSuite, Class clazz, File testNGFile )
	{
		final String testNGTestClassName = clazz.getName();
		
		final List<br.eti.kinoshita.testlinkjavaapi.model.TestCase> testLinkTestCases =
			this.report.getTestCases();
		
		listener.getLogger().println( "Looking for a TestLink Automated Test Case custom field with value equals ["+testNGTestClassName+"]." );
		
		for ( br.eti.kinoshita.testlinkjavaapi.model.TestCase testLinkTestCase : testLinkTestCases )
		{
			final List<CustomField> customFields = testLinkTestCase.getCustomFields();
			
			for( CustomField customField : customFields )
			{
				final String customFieldValue = customField.getValue();
				Boolean isKeyCustomField = customField.getName().equals(keyCustomFieldName);
				
				if ( isKeyCustomField && testNGTestClassName.equals( customFieldValue ) )
				{
					ExecutionStatus status = this.getTestNGExecutionStatus( clazz );
					testLinkTestCase.setExecutionStatus( status );
					TestResult testResult = new TestResult(testLinkTestCase, report.getBuild(), report.getTestPlan());
					
					String notes = this.getTestNGNotes( testNGSuite, clazz );
					
					try
					{
						Attachment testNGAttachment = this.getTestNGAttachment( testResult.getTestCase().getVersionId(), testNGFile );
						testResult.addAttachment( testNGAttachment );
					}
					catch ( IOException ioe )
					{
						notes += "\n\nFailed to add TestNG attachment to this test case execution. Error message: " + ioe.getMessage();
						ioe.printStackTrace( listener.getLogger() );
					}
					
					testResult.setNotes( notes );
					return testResult;
				} // endif
			} //end for custom fields
		} // end for testlink test cases
		
		return null;
	}
	
	/**
	 * Retrieves notes for TestNG suite and test class.
	 * 
	 * @param suite TestNG suite.
	 * @param clazz TestNG test class.
	 * @return notes for TestNG suite and test class.
	 */
	protected String getTestNGNotes( Suite suite, Class clazz )
	{
		StringBuilder notes = new StringBuilder();
		
		notes.append( "name: " );
		notes.append( suite.getName() + "\n" );
		
		notes.append( "duration in ms: " );
		notes.append( suite.getDurationMs() + "\n" );
		
		notes.append( "started at: " );
		notes.append( suite.getStartedAt() + "\n" );
		
		notes.append( "finished at: " );
		notes.append( suite.getFinishedAt() + "\n" );
		
		notes.append( "number of tests: " );
		notes.append( suite.getTests().size() + "\n" );
		
		notes.append( "---------------\n" );
		
		notes.append( "class name: " );
		notes.append( clazz.getName() + "\n" );
		
		notes.append( "number of methods: " );
		notes.append( clazz.getTestMethods().size() + "\n" );
		
		for( TestMethod method : clazz.getTestMethods() )
		{
			notes.append( "  name: " );
			notes.append( method.getName() + "\n" );
			
			notes.append( "  config?: " );
			notes.append( method.getIsConfig() + "\n" );
			
			notes.append( "  signature: " );
			notes.append( method.getSignature() + "\n" );
			
			notes.append( "  status: " );
			notes.append( method.getStatus() + "\n" );
			
			notes.append( "  duration in ms: " );
			notes.append( method.getDurationMs() + "\n" );
			
			notes.append( "  started at: " );
			notes.append( method.getStartedAt() + "\n" );
			
			notes.append( "  finished at: " );
			notes.append( method.getFinishedAt() + "\n" );
		}
		
		return notes.toString();
	}

	/**
	 * Retrieves the Execution Status for a TestNG test class. It is done 
	 * iterating over all the class methods. If a method has the status 
	 * FAIL, then we return the Execution Status failed, otherwise passed.
	 * 
	 * @param clazz The TestNG Test class.
	 * @return passed if the TestNG Test class contains no test methods with 
	 * status equals FAIL, otherwise failed.
	 */
	protected ExecutionStatus getTestNGExecutionStatus( Class clazz )
	{
		ExecutionStatus status = ExecutionStatus.PASSED;
		
		for( TestMethod method : clazz.getTestMethods() )
		{
			if ( StringUtils.isNotBlank(method.getStatus()) && !method.getStatus().equals("PASS"))
			{
				status = ExecutionStatus.FAILED;
				break; // It's enough, one single failed is enough to invalidate a test class
			}
		}
		
		return status;
	}
	
	/**
	 * Retrieves attachments for TestNG test cases.
	 * 
	 * @param versionId version ID of the TestLink test case.
	 * @param testNGReportFile TestNG file.
	 * @return attachments
	 */
	protected Attachment getTestNGAttachment( 
		Integer versionId,
		File testNGReportFile 
	)
	throws IOException
	{
		Attachment attachment = new Attachment();
		
		String fileContent = this.getBase64FileContent(testNGReportFile );
		attachment.setContent( fileContent );
		attachment.setDescription( "TestNG report file " + testNGReportFile.getName() );
		attachment.setFileName( testNGReportFile.getName() );
		attachment.setFileSize( testNGReportFile.length() );
		attachment.setTitle( testNGReportFile.getName() );
		attachment.setFileType("text/xml");
		
		return attachment;
	}
	
}
