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
import hudson.plugins.testlink.parser.ParserException;
import hudson.plugins.testlink.parser.testng.Class;
import hudson.plugins.testlink.parser.testng.Suite;
import hudson.plugins.testlink.parser.testng.Test;
import hudson.plugins.testlink.parser.testng.TestMethod;
import hudson.plugins.testlink.parser.testng.TestNGParser;
import hudson.plugins.testlink.util.Messages;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import br.eti.kinoshita.testlinkjavaapi.model.Attachment;
import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;

/**
 * This class is responsible 
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1.1
 */
public class TestNGTestResultSeeker 
extends TestResultSeeker
{

	private static final long serialVersionUID = 125391193836506341L;
	
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
	public Set<TestCaseWrapper> seek( File directory, String includePattern )
			throws TestResultSeekerException
	{
		final Set<TestCaseWrapper> results = new HashSet<TestCaseWrapper>();
		
		if ( StringUtils.isBlank(includePattern) ) // skip TestNG
		{
			listener.getLogger().println( Messages.Results_TestNG_NoPattern() );
			listener.getLogger().println();
		}
		else
		{
			try
			{
				String[] testNGReports = this.scan(directory, includePattern, listener);
				
				listener.getLogger().println( Messages.Results_TestNG_NumberOfReportsFound( testNGReports.length ) );
				listener.getLogger().println();
				
				this.doTestNGReports( directory, testNGReports, results );
			} 
			catch (IOException e)
			{
				throw new TestResultSeekerException( Messages.Results_TestNG_IOException( includePattern, e.getMessage() ), e );
			}
			catch( Throwable t ) 
			{
				throw new TestResultSeekerException( Messages.Results_TestNG_UnkownInternalError(), t );
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
		Set<TestCaseWrapper> testResults)
	{
		
		for ( int i = 0 ; i < testNGReports.length ; ++i )
		{
			listener.getLogger().println( Messages.Results_TestNG_Parsing( testNGReports[i] ) );
			listener.getLogger().println();
			
			File testNGFile = new File(directory, testNGReports[i]);
			
			try
			{
				final Suite testNGSuite = parser.parse( testNGFile );
				
				this.doTestNGSuite( testNGSuite, testNGFile, testResults );
			}
			catch ( ParserException e )
			{
				listener.getLogger().println( Messages.Results_TestNG_ParsingFail( testNGFile, e.getMessage() ) );
				e.printStackTrace( listener.getLogger() );
				listener.getLogger().println();
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
		Set<TestCaseWrapper> testResults ) 
	{
		listener.getLogger().println( Messages.Results_TestNG_VerifyingTestNGTestSuite( testNGSuite.getName(), testNGSuite.getTests().size() ) );
		listener.getLogger().println();
		
		final List<Test> testNGTests = testNGSuite.getTests();
		
		for( Test testNGTest : testNGTests )
		{
			final List<hudson.plugins.testlink.parser.testng.Class> classes = 
				testNGTest.getClasses();
			
			listener.getLogger().println( Messages.Results_TestNG_VerifyingTestNGTest( testNGTest.getName(), classes.size() ));
			
			for ( hudson.plugins.testlink.parser.testng.Class clazz : classes )
			{
				listener.getLogger().println( Messages.Results_TestNG_VerifyingTestNGTestClass( clazz.getName() ) );
				
				final TestCaseWrapper testResult = this.doFindTestResult( testNGSuite, clazz, testNGFile );
				
				if ( testResult != null )
				{
					TestCase tc = testResult.getTestCase();
					listener.getLogger().println( Messages.Results_TestNG_TestResultsFound( tc.getName(), tc.getId(), testNGTest.getName(), clazz.getName(), testResult.getTestCase().getExecutionStatus().toString() ) );
					testResults.add( testResult );
				}
				else
				{
					listener.getLogger().println( Messages.Results_TestNG_NoTestResultFound( testNGFile.toString(), testNGTest.getName(), clazz.getName() ) );
				}
			}
			
			listener.getLogger().println();
			
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
	protected TestCaseWrapper doFindTestResult( Suite testNGSuite, Class clazz, File testNGFile )
	{
		final String testNGTestClassName = clazz.getName();
		
		final List<br.eti.kinoshita.testlinkjavaapi.model.TestCase> testLinkTestCases =
			this.report.getTestCases();
		
		listener.getLogger().println();
		listener.getLogger().println( Messages.Results_TestNG_LookingForTestResults( keyCustomFieldName, testNGTestClassName ) );
		listener.getLogger().println();
		
		for ( br.eti.kinoshita.testlinkjavaapi.model.TestCase testLinkTestCase : testLinkTestCases )
		{
			listener.getLogger().println( Messages.Results_TestNG_VerifyingTestLinkTestCase( testLinkTestCase.getName(), testLinkTestCase.getId() ) );
			
			final List<CustomField> customFields = testLinkTestCase.getCustomFields();
			
			listener.getLogger().println( Messages.Results_TestNG_ListOfCustomFields( customFields ) );
			
			for( CustomField customField : customFields )
			{
				final String customFieldValue = customField.getValue();
				Boolean isKeyCustomField = customField.getName().equals(keyCustomFieldName);
				
				if ( isKeyCustomField && testNGTestClassName.equals( customFieldValue ) )
				{
					
					if ( ExecutionStatus.BLOCKED != testLinkTestCase.getExecutionStatus() )
					{
						final ExecutionStatus status = this.getTestNGExecutionStatus( clazz );
						testLinkTestCase.setExecutionStatus( status );
						final TestCaseWrapper testResult = new TestCaseWrapper(testLinkTestCase );
						
						String notes = this.getTestNGNotes( testNGSuite, clazz );
						
						try
						{
							Attachment testNGAttachment = this.getTestNGAttachment( testResult.getTestCase().getVersionId(), testNGFile );
							testResult.addAttachment( testNGAttachment );
						}
						catch ( IOException ioe )
						{
							notes += Messages.Results_TestNG_AddAttachmentsFail( ioe.getMessage() );
							ioe.printStackTrace( listener.getLogger() );
						}
						
						testResult.setNotes( notes );
						return testResult;
					}
				} // endif
			} //end for custom fields
			
			listener.getLogger().println();
			
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
		attachment.setDescription( Messages.Results_TestNG_AttachmentDescription( testNGReportFile.getName() ) );
		attachment.setFileName( testNGReportFile.getName() );
		attachment.setFileSize( testNGReportFile.length() );
		attachment.setTitle( testNGReportFile.getName() );
		attachment.setFileType("text/xml");
		
		return attachment;
	}
	
}
