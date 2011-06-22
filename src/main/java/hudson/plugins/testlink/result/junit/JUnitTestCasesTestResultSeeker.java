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
package hudson.plugins.testlink.result.junit;

import hudson.model.BuildListener;
import hudson.plugins.testlink.parser.ParserException;
import hudson.plugins.testlink.parser.junit.JUnitParser;
import hudson.plugins.testlink.parser.junit.TestCase;
import hudson.plugins.testlink.parser.junit.TestSuite;
import hudson.plugins.testlink.result.TestCaseWrapper;
import hudson.plugins.testlink.result.TestLinkReport;
import hudson.plugins.testlink.result.TestResultSeekerException;
import hudson.plugins.testlink.util.Messages;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import br.eti.kinoshita.testlinkjavaapi.model.Attachment;
import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;

/**
 * Seeks for test results of JUnit test cases.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.5
 */
public class JUnitTestCasesTestResultSeeker<T extends TestCase> 
extends AbstractJUnitTestResultSeeker<TestCase>
{
	
	private static final long serialVersionUID = 7775155353548789211L;

	protected final JUnitParser parser = new JUnitParser();
	
	/**
	 * Map of Wrappers for TestLink Test Cases.
	 */
	protected final Map<Integer, TestCaseWrapper<TestCase>> results = new LinkedHashMap<Integer, TestCaseWrapper<TestCase>>();
	
	/**
	 * Stores a list of JUnit test cases that one or more methods failed.
	 */
	protected final Set<String> failedTestCases = new HashSet<String>();
	
	public JUnitTestCasesTestResultSeeker(
		String includePattern,
		TestLinkReport report, 
		String keyCustomFieldName,
		BuildListener listener
	)
	{
		super(includePattern, report, keyCustomFieldName, listener);
	}
	
	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.result.TestResultSeeker#seek(java.io.File, java.lang.String, hudson.plugins.testlink.result.TestLinkReport, hudson.model.BuildListener)
	 */
	public Map<Integer, TestCaseWrapper<TestCase>> seek( File directory ) 
	throws TestResultSeekerException
	{
		// TBD: i18n
		listener.getLogger().println( "Looking for JUnit test cases test results.\n" );
		
		if ( StringUtils.isBlank(includePattern) ) // skip JUnit
		{
			listener.getLogger().println( Messages.Results_JUnit_NoPattern() );
			listener.getLogger().println();
		}
		else
		{
			try
			{
				String[] junitReports = this.scan(directory, includePattern, listener);
				
				listener.getLogger().println( Messages.Results_JUnit_NumberOfReportsFound(junitReports.length ) );
				listener.getLogger().println();
				
				this.processJUnitReports( directory, junitReports );
			} 
			catch (IOException e)
			{
				throw new TestResultSeekerException( Messages.Results_JUnit_IOException( includePattern, e.getMessage() ), e );
			}
			catch( Throwable t ) 
			{
				throw new TestResultSeekerException( Messages.Results_JUnit_UnkownInternalError(), t );
			}
		}
		
		return this.results;
		
	}
	
	/**
	 * Processes all JUnit test result files.
	 */
	protected void processJUnitReports( 
		File directory, 
		String[] junitReports 
	)
	{
		for ( int i = 0 ; i < junitReports.length ; ++i )
		{
			listener.getLogger().println( Messages.Results_JUnit_Parsing( junitReports[i] ) );
			listener.getLogger().println();
			
			final File junitFile = new File(directory, junitReports[i]);
			
			try
			{
				final List<TestSuite> junitSuites = parser.parse( junitFile );
				
				for ( TestSuite junitSuite : junitSuites )
				{
					this.processJUnitSuite( junitSuite, junitFile );
				}
			}
			catch ( ParserException e )
			{
				listener.getLogger().println( Messages.Results_JUnit_ParsingFail(junitFile, e.getMessage() ) );
				e.printStackTrace( listener.getLogger() );
				listener.getLogger().println();
			}
		}
	}
	
	/**
	 * Finds a test result for a given JUnit suite.
	 */
	protected void processJUnitSuite( TestSuite junitSuite, File junitFile )
	{
		listener.getLogger().println( Messages.Results_JUnit_VerifyingJUnitSuite( junitSuite.getName(), junitSuite.getTests(), junitSuite.getFailures(), junitSuite.getErrors() ) );
		listener.getLogger().println();
		
		final List<TestCase> testCases = junitSuite.getTestCases();
		
		for ( TestCase testCase : testCases )
		{
			this.processJUnitTestCase( testCase, junitFile );
		}
	}
		
	/**
	 * Processes JUnit test case.
	 */
	protected void processJUnitTestCase( TestCase junitTestCase, File junitFile )
	{
		final String testClassOrTestName = this.getTestClassOrTestName( junitTestCase );
		
		if ( ! StringUtils.isBlank( testClassOrTestName ) )
		{
			final Collection<br.eti.kinoshita.testlinkjavaapi.model.TestCase> testLinkTestCases =
				this.report.getTestCases().values();
			
			listener.getLogger().println();
			listener.getLogger().println( Messages.Results_JUnit_LookingForTestResults( keyCustomFieldName, testClassOrTestName ) );
			listener.getLogger().println();
			
			final Iterator<br.eti.kinoshita.testlinkjavaapi.model.TestCase> iter = testLinkTestCases.iterator();
			while( iter.hasNext() )
			{
				final br.eti.kinoshita.testlinkjavaapi.model.TestCase testLinkTestCase = iter.next();
				listener.getLogger().println( Messages.Results_JUnit_VerifyingTestLinkTestCase( testLinkTestCase.getName(), testLinkTestCase.getId() ) );
				
				this.findTestResults( junitTestCase, testClassOrTestName, testLinkTestCase, junitFile );
				
				listener.getLogger().println();
			}
		}
	}

	/**
	 * Looks for test results in a JUnit test case.
	 */
	protected void findTestResults( TestCase junitTestCase, String testClassOrTestName, br.eti.kinoshita.testlinkjavaapi.model.TestCase testLinkTestCase, File junitFile ) 
	{
		final List<CustomField> customFields = testLinkTestCase.getCustomFields();
		listener.getLogger().println( Messages.Results_JUnit_ListOfCustomFields( customFields ) );
		
		final CustomField keyCustomField = this.getKeyCustomField( customFields );
		if ( keyCustomField != null )
		{
			final String[] commaSeparatedValues = this.split ( keyCustomField.getValue() );
			
			for ( String value : commaSeparatedValues )
			{
				if ( testClassOrTestName.equals( value ) && ExecutionStatus.BLOCKED != testLinkTestCase.getExecutionStatus())
				{
					final TestCaseWrapper<TestCase> testResult = new TestCaseWrapper<TestCase>( testLinkTestCase, commaSeparatedValues, junitTestCase );
					
					final ExecutionStatus status = this.getJUnitExecutionStatus( junitTestCase, testClassOrTestName );
					testResult.addCustomFieldAndStatus(value, status);
					
					String notes = this.getJUnitNotes( junitTestCase );
					
					try
					{
						final Attachment junitAttachment = this.getJUnitAttachment( junitFile );
						testResult.addAttachment( junitAttachment );
					}
					catch ( IOException ioe )
					{
						notes += Messages.Results_JUnit_AddAttachmentsFail( ioe.getMessage() );
						ioe.printStackTrace( listener.getLogger() );
					}
					
					testResult.appendNotes( notes );
					
					this.addOrUpdate( testResult );
				}
			}
		}
	}
	
	/**
	 * Adds or updates test result.
	 */
	protected void addOrUpdate( TestCaseWrapper<TestCase> testResult )
	{
		final TestCaseWrapper<TestCase> temp = this.results.get(testResult.getId());
		
		TestCase origin = testResult.getOrigin();
		listener.getLogger().println( Messages.Results_JUnit_TestResultsFound( testResult.getName(), testResult.getId(), origin, origin.getName(), testResult.getTestCase().getExecutionStatus().toString() ) );
		
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
	 * Retrieves the key value for a JUnit test. First it tries to use the 
	 * test class name. If it is <code>null</code>, then it returns the 
	 * test name.
	 * 
	 * @param junitTestCase JUnit test.
	 * @return Test Class or Test Name value.
	 */
	protected String getTestClassOrTestName( TestCase junitTestCase )
	{
		String keyValue = junitTestCase.getClassName();
		
		if ( StringUtils.isBlank( keyValue ) )
		{
			keyValue = junitTestCase.getName();
		}
		
		return keyValue;
	}

	/**
	 * Retrieves the Execution Status of the JUnit test.
	 * 
	 * @param testCase JUnit test.
	 * @param testClassOrTestName 
	 * @return the Execution Status of the JUnit test.
	 */
	protected ExecutionStatus getJUnitExecutionStatus( TestCase testCase, String testClassOrTestName )
	{
		ExecutionStatus status = ExecutionStatus.FAILED;
		if ( (testCase.getFailures().size() + testCase.getErrors().size()) <= 0 )
		{
			if ( ! failedTestCases.contains( testClassOrTestName ) )
			{
				status = ExecutionStatus.PASSED;
			}
		}
		else
		{
			failedTestCases.add( testClassOrTestName );
		}
		return status;
	}
	
	/**
	 * Retrieves the Notes about the JUnit test.
	 * 
	 * @param testCase JUnit test.
	 * @return Notes about the JUnit test.
	 */
	protected String getJUnitNotes( TestCase testCase )
	{
		StringBuilder notes = new StringBuilder();
		
		notes.append( "name: " );
		notes.append( testCase.getName()+ "\n" );
		
		notes.append( "classname: " );
		notes.append( testCase.getClassName() + "\n" );
		
		notes.append( "errors: " );
		notes.append( testCase.getErrors().size() + "\n" );
		
		notes.append( "failures: " );
		notes.append( testCase.getFailures().size() + "\n" );
		
		notes.append( "time: " );
		notes.append( testCase.getTime()+ "\n" );
		
		return notes.toString();
	}
	
}
