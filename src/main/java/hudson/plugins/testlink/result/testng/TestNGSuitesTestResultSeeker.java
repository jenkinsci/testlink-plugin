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
package hudson.plugins.testlink.result.testng;

import hudson.model.BuildListener;
import hudson.plugins.testlink.parser.ParserException;
import hudson.plugins.testlink.parser.testng.Suite;
import hudson.plugins.testlink.parser.testng.Test;
import hudson.plugins.testlink.parser.testng.TestMethod;
import hudson.plugins.testlink.parser.testng.TestNGParser;
import hudson.plugins.testlink.result.TestCaseWrapper;
import hudson.plugins.testlink.result.TestResultSeekerException;
import hudson.plugins.testlink.util.Messages;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import br.eti.kinoshita.testlinkjavaapi.model.Attachment;
import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;

/**
 * Seeks for test results of TestNG suites.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.5
 */
public class TestNGSuitesTestResultSeeker<T extends Suite>
extends AbstractTestNGTestResultSeeker<Suite>
{

	private static final long serialVersionUID = -3492359249081599662L;

	protected final TestNGParser parser = new TestNGParser();
	
	protected final Map<Integer, TestCaseWrapper<Suite>> results = new LinkedHashMap<Integer, TestCaseWrapper<Suite>>();
	
	public TestNGSuitesTestResultSeeker(String includePattern,
			TestCase[] automatedTestCases, String keyCustomFieldName,
			BuildListener listener)
	{
		super(includePattern, automatedTestCases, keyCustomFieldName, listener);
	}

	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.result.TestResultSeeker#seek(java.io.File)
	 */
	@Override
	public Map<Integer, TestCaseWrapper<Suite>> seek( File directory )
			throws TestResultSeekerException
	{
		listener.getLogger().println( Messages.Results_TestNG_LookingForTestSuites() );
		listener.getLogger().println();
		
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
				
				this.processTestNGReports( directory, testNGReports );
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
		
		return this.results;
	}

	/**
	 * Processes TestNG reports.
	 */
	protected void processTestNGReports( File directory, String[] testNGReports )
	{
		for ( int i = 0 ; i < testNGReports.length ; ++i )
		{
			listener.getLogger().println( Messages.Results_TestNG_Parsing( testNGReports[i] ) );
			listener.getLogger().println();
			
			File testNGFile = new File(directory, testNGReports[i]);
			
			try
			{
				final Suite testNGSuite = parser.parse( testNGFile );
				
				this.processTestNGSuite( testNGSuite, testNGFile );
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
	 * Processes TestNG Suite.
	 */
	protected void processTestNGSuite( Suite testNGSuite, File testNGFile )
	{
		final String suiteName = testNGSuite.getName();
		
		if ( ! StringUtils.isBlank( suiteName ) )
		{
			listener.getLogger().println( Messages.Results_TestNG_LookingForTestResults( keyCustomFieldName, suiteName ) );
			listener.getLogger().println();
			
			for(TestCase testLinkTestCase : automatedTestCases )
			{
				listener.getLogger().println( Messages.Results_TestNG_VerifyingTestLinkTestCase( testLinkTestCase.getName(), testLinkTestCase.getId() ) );
				
				this.findTestResults( testNGSuite, testLinkTestCase, testNGFile );
				
				listener.getLogger().println();
			}
		}
	}

	/**
	 * Looks for test results in a TestNG suite.
	 */
	protected void findTestResults( Suite testNGSuite, TestCase testLinkTestCase,
			File testNGFile )
	{
		final List<CustomField> customFields = testLinkTestCase.getCustomFields();
		listener.getLogger().println( Messages.Results_TestNG_ListOfCustomFields( customFields ) );
		
		final CustomField keyCustomField = this.getKeyCustomField( customFields );
		if ( keyCustomField != null ) 
		{
		
			final String[] commaSeparatedValues = this.split ( keyCustomField.getValue() );
			
			for ( String value : commaSeparatedValues )
			{
				if ( testNGSuite.getName().equals( value ) && ExecutionStatus.BLOCKED != testLinkTestCase.getExecutionStatus())
				{
					final TestCaseWrapper<Suite> testResult = new TestCaseWrapper<Suite>( testLinkTestCase, commaSeparatedValues, testNGSuite );
					
					final ExecutionStatus status = this.getTestNGExecutionStatus( testNGSuite );
					testResult.addCustomFieldAndStatus(value, status);
					
					String notes = this.getTestNGNotes( testNGSuite );
					
					try
					{
						final Attachment testNGAttachment = this.getTestNGAttachment( testNGFile );
						testResult.addAttachment( testNGAttachment );
					}
					catch ( IOException ioe )
					{
						notes += Messages.Results_TestNG_AddAttachmentsFail( ioe.getMessage() );
						ioe.printStackTrace( listener.getLogger() );
					}
					
					testResult.appendNotes( notes );
					
					this.addOrUpdate( testResult );
				}
			}
		}
	}

	/**
	 * Adds a test result to the map of test results. If the entry already 
	 * exists, then it is updated (notes, attachments and statuses).
	 */
	protected void addOrUpdate( TestCaseWrapper<Suite> testResult )
	{
		final TestCaseWrapper<Suite> temp = this.results.get(testResult.getId());
		
		Suite origin = testResult.getOrigin();
		listener.getLogger().println( Messages.Results_TestNG_TestResultsFound( testResult.getName(), testResult.getId(), origin, origin.getName(), testResult.getExecutionStatus().toString() ) );
		
		if ( temp == null )
		{
			this.results.put(testResult.getId(), testResult);
		}
		else
		{
			temp.appendNotes( testResult.getNotes() );
			for( Attachment attachment : testResult.getAttachments() )
			{
				temp.addAttachment(attachment);
			}
			temp.getCustomFieldAndStatus().putAll( testResult.getCustomFieldAndStatus() );
		}
	}

	/**
	 * Retrieves notes for TestNG suite.
	 * 
	 * @param suite TestNG suite.
	 * @return notes for TestNG suite and test class.
	 */
	protected String getTestNGNotes( Suite suite )
	{
		StringBuilder notes = new StringBuilder();
		
		notes.append( 
				Messages.Results_TestNG_NotesForSuite(
						suite.getName(), 
						suite.getDurationMs(), 
						suite.getStartedAt(), 
						suite.getFinishedAt(), suite.getTests().size() 
				)
		);
		
		return notes.toString();
	}

	/**
	 * Retrieves the Execution Status for a TestNG test class. It is done 
	 * iterating over all the class methods. If a method has the status 
	 * FAIL, then we return the Execution Status failed, otherwise passed.
	 * 
	 * @param suite The TestNG Test suite.
	 * @return passed if the TestNG Test suite contains no test classes with 
	 * status equals FAIL, otherwise failed.
	 */
	protected ExecutionStatus getTestNGExecutionStatus( Suite suite )
	{
		ExecutionStatus status = ExecutionStatus.PASSED;
		
		for( Test test : suite.getTests() )
		{
			for( hudson.plugins.testlink.parser.testng.Class clazz : test.getClasses() )
			{
				for( TestMethod method : clazz.getTestMethods() )
				{
					if ( StringUtils.isNotBlank(method.getStatus()) && !method.getStatus().equals("PASS"))
					{
						status = ExecutionStatus.FAILED;
						return status; // It's enough, one single failed is enough to invalidate a test suite
					}
				}
			}
		}
		
		return status;
	}
	
}
