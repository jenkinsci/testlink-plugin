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
package hudson.plugins.testlink.result.junit;

import hudson.model.BuildListener;
import hudson.plugins.testlink.parser.ParserException;
import hudson.plugins.testlink.parser.junit.JUnitParser;
import hudson.plugins.testlink.parser.junit.TestSuite;
import hudson.plugins.testlink.result.TestCaseWrapper;
import hudson.plugins.testlink.result.TestLinkReport;
import hudson.plugins.testlink.result.TestResultSeekerException;
import hudson.plugins.testlink.util.Messages;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import br.eti.kinoshita.testlinkjavaapi.model.Attachment;
import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;

/**
 * Seeks for test results of JUnit test suites.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.5
 */
public class JUnitSuitesTestResultSeeker<T extends TestSuite>
extends AbstractJUnitTestResultSeeker<TestSuite>
{

	private static final long serialVersionUID = -7159671962913085600L;

	protected final JUnitParser parser = new JUnitParser();
	
	private final Map<Integer, TestCaseWrapper<TestSuite>> results = new LinkedHashMap<Integer, TestCaseWrapper<TestSuite>>();

	public JUnitSuitesTestResultSeeker(String includePattern,
			TestLinkReport report, String keyCustomFieldName,
			BuildListener listener)
	{
		super(includePattern, report, keyCustomFieldName, listener);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see hudson.plugins.testlink.result.TestResultSeeker#seek(java.io.File,
	 * java.lang.String)
	 */
	@Override
	public Map<Integer, TestCaseWrapper<TestSuite>> seek( File directory )
			throws TestResultSeekerException
	{
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
	 * Processes all JUnit reports.
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
	 * Processes JUnit suite.
	 */
	protected void processJUnitSuite( TestSuite junitSuite, File junitFile )
	{
		final String suiteName = junitSuite.getName();
		
		if ( ! StringUtils.isBlank( suiteName ) )
		{
			final Collection<br.eti.kinoshita.testlinkjavaapi.model.TestCase> testLinkTestCases =
				this.report.getTestCases().values();
			
			listener.getLogger().println();
			listener.getLogger().println( Messages.Results_JUnit_LookingForTestResults( keyCustomFieldName, suiteName ) );
			listener.getLogger().println();
			
			final Iterator<br.eti.kinoshita.testlinkjavaapi.model.TestCase> iter = testLinkTestCases.iterator();
			while( iter.hasNext() )
			{
				final br.eti.kinoshita.testlinkjavaapi.model.TestCase testLinkTestCase = iter.next();
				listener.getLogger().println( Messages.Results_JUnit_VerifyingTestLinkTestCase( testLinkTestCase.getName(), testLinkTestCase.getId() ) );
				
				this.findTestResults( junitSuite, testLinkTestCase, junitFile );
				
				listener.getLogger().println();
			}
		}
		
	}
	
	/**
	 * Looks for test results in a JUnit suite.
	 */
	protected void findTestResults( 
		TestSuite junitSuite,
		TestCase testLinkTestCase, 
		File junitFile )
	{
		final List<CustomField> customFields = testLinkTestCase.getCustomFields();
		listener.getLogger().println( Messages.Results_JUnit_ListOfCustomFields( customFields ) );
		
		final CustomField keyCustomField = this.getKeyCustomField( customFields );
		if ( keyCustomField != null ) 
		{
		
			final String[] commaSeparatedValues = this.split ( keyCustomField.getValue() );
			
			for ( String value : commaSeparatedValues )
			{
				if ( junitSuite.getName().equals( value ) && ExecutionStatus.BLOCKED != testLinkTestCase.getExecutionStatus())
				{
					final TestCaseWrapper<TestSuite> testResult = new TestCaseWrapper<TestSuite>( testLinkTestCase, commaSeparatedValues, junitSuite );
					
					final ExecutionStatus status = this.getJUnitExecutionStatus( junitSuite );
					testResult.addCustomFieldAndStatus(value, status);
					
					String notes = this.getJUnitNotes( junitSuite );
					
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
	 * Adds a test result to the map of test results. If the entry already 
	 * exists, then it is updated (notes, attachments and statuses).
	 */
	protected void addOrUpdate( TestCaseWrapper<TestSuite> testResult )
	{
		final TestCaseWrapper<TestSuite> temp = this.results.get(testResult.getId());
		
		TestSuite origin = testResult.getOrigin();
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
	 * Gets the execution status of the JUnit suite.
	 */
	protected ExecutionStatus getJUnitExecutionStatus( TestSuite testSuite )
	{
		ExecutionStatus status = ExecutionStatus.FAILED;
		if ( (testSuite.getFailures() + testSuite.getErrors()) <= 0 )
		{
			status = ExecutionStatus.PASSED;
		}
		return status;
	}
	
	/**
	 * Gets the notes about the JUnit suite.
	 */
	protected String getJUnitNotes( TestSuite testSuite )
	{
		StringBuilder notes = new StringBuilder();
		
		notes.append( "hostname: " );
		notes.append( testSuite.getHostname() + "\n" );
		
		notes.append( "name: " );
		notes.append( testSuite.getName() + "\n" );
		
		notes.append( "system err: " );
		notes.append( testSuite.getSystemErr() + "\n" );
		
		notes.append( "system out: " );
		notes.append( testSuite.getSystemOut() + "\n" );
		
		notes.append( "tests: " );
		notes.append( testSuite.getTests()+ "\n" );
		
		notes.append( "time: " );
		notes.append( testSuite.getTime()+ "\n" );
		
		notes.append( "timestamp: " );
		notes.append( testSuite.getTimestamp() + "\n" );
		
		notes.append( "errors: " );
		notes.append( testSuite.getErrors()+ "\n" );
		
		notes.append( "failures: " );
		notes.append( testSuite.getFailures() + "\n" );
		
		return notes.toString();
	}

}
