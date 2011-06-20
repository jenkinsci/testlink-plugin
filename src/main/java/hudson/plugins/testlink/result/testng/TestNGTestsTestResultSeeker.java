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
import hudson.plugins.testlink.parser.testng.Class;
import hudson.plugins.testlink.parser.testng.Suite;
import hudson.plugins.testlink.parser.testng.Test;
import hudson.plugins.testlink.parser.testng.TestMethod;
import hudson.plugins.testlink.parser.testng.TestNGParser;
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
 * This class is responsible 
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.5
 */
public class TestNGTestsTestResultSeeker<T extends hudson.plugins.testlink.parser.testng.Class>
extends AbstractTestNGTestResultSeeker<hudson.plugins.testlink.parser.testng.Class>
{

	private static final long serialVersionUID = 4734537106225737934L;

	protected final TestNGParser parser = new TestNGParser();
	
	/**
	 * Map of Wrappers for TestLink Test Cases.
	 */
	private final Map<Integer, TestCaseWrapper<hudson.plugins.testlink.parser.testng.Class>> results = new LinkedHashMap<Integer, TestCaseWrapper<hudson.plugins.testlink.parser.testng.Class>>();
	
	public TestNGTestsTestResultSeeker(String includePattern, TestLinkReport report,
			String keyCustomFieldName, BuildListener listener)
	{
		super(includePattern, report, keyCustomFieldName, listener);
	}
	
	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.result.TestResultSeeker#seek(java.io.File, java.lang.String)
	 */
	@Override
	public Map<Integer, TestCaseWrapper<hudson.plugins.testlink.parser.testng.Class>> seek( File directory )
			throws TestResultSeekerException
	{
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
	protected void processTestNGReports( 
		File directory, 
		String[] testNGReports ) 
	{
		
		for ( int i = 0 ; i < testNGReports.length ; ++i )
		{
			listener.getLogger().println( Messages.Results_TestNG_Parsing( testNGReports[i] ) );
			listener.getLogger().println();
			
			final File testNGFile = new File(directory, testNGReports[i]);
			
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
	 * Processes a TestNG suite.
	 */
	protected void processTestNGSuite( 
		Suite testNGSuite, 
		File testNGFile
	)
	{
		listener.getLogger().println( Messages.Results_TestNG_VerifyingTestNGTestSuite( testNGSuite.getName(), testNGSuite.getTests().size() ) );
		listener.getLogger().println();
		
		final List<Test> testNGTests = testNGSuite.getTests();
		
		for( Test testNGTest : testNGTests )
		{
			this.processTestNGTest( testNGTest, testNGSuite, testNGFile );
		}
	}
	
	/**
	 * Processes a TestNG test.
	 */
	protected void processTestNGTest( Test testNGTest, Suite testNGSuite, File testNGFile )
	{
		final List<hudson.plugins.testlink.parser.testng.Class> classes = 
			testNGTest.getClasses();
		
		listener.getLogger().println( Messages.Results_TestNG_VerifyingTestNGTest( testNGTest.getName(), classes.size() ));
		
		for ( hudson.plugins.testlink.parser.testng.Class clazz : classes )
		{
			listener.getLogger().println( Messages.Results_TestNG_VerifyingTestNGTestClass( clazz.getName() ) );
			
			this.processTestClass( clazz, testNGSuite, testNGFile );
		}
		
		listener.getLogger().println();
	}

	/**
	 * Processes a TestNG test class.
	 */
	protected void processTestClass( hudson.plugins.testlink.parser.testng.Class clazz, Suite testNGSuite, File testNGFile )
	{
		final String testNGTestClassName = clazz.getName();
		
		if ( ! StringUtils.isBlank( testNGTestClassName ) )
		{
			final Collection<br.eti.kinoshita.testlinkjavaapi.model.TestCase> testLinkTestCases =
				this.report.getTestCases().values();
			
			listener.getLogger().println();
			listener.getLogger().println( Messages.Results_TestNG_LookingForTestResults( keyCustomFieldName, testNGTestClassName ) );
			listener.getLogger().println();
			
			final Iterator<br.eti.kinoshita.testlinkjavaapi.model.TestCase> iter = testLinkTestCases.iterator();
			while( iter.hasNext() )
			{
				final br.eti.kinoshita.testlinkjavaapi.model.TestCase testLinkTestCase = iter.next();
				listener.getLogger().println( Messages.Results_TestNG_VerifyingTestLinkTestCase( testLinkTestCase.getName(), testLinkTestCase.getId() ) );
			
				this.findTestResults( testNGSuite, clazz, testLinkTestCase, testNGFile );
			
				listener.getLogger().println();
			}
		}
	}

	/**
	 * Looks for test results in a TestNG test case.
	 */
	protected void findTestResults( Suite testNGSuite, hudson.plugins.testlink.parser.testng.Class clazz, TestCase testLinkTestCase, File testNGFile )
	{
		final List<CustomField> customFields = testLinkTestCase.getCustomFields();
		listener.getLogger().println( Messages.Results_TestNG_ListOfCustomFields( customFields ) );
		
		final CustomField keyCustomField = this.getKeyCustomField( customFields );
		if ( keyCustomField != null )
		{
			final String[] commaSeparatedValues = this.split ( keyCustomField.getValue() );
			
			for ( String value : commaSeparatedValues )
			{
				if ( clazz.getName().equals( value ) && ExecutionStatus.BLOCKED != testLinkTestCase.getExecutionStatus() )
				{
					final TestCaseWrapper<hudson.plugins.testlink.parser.testng.Class> testResult = 
						new TestCaseWrapper<hudson.plugins.testlink.parser.testng.Class>( testLinkTestCase, commaSeparatedValues, clazz );
					
					final ExecutionStatus status = this.getTestNGExecutionStatus( clazz );
					testResult.addCustomFieldAndStatus(value, status);
					
					String notes = this.getTestNGNotes( testNGSuite, clazz );
					
					try
					{
						Attachment testNGAttachment = this.getTestNGAttachment( testNGFile );
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
	 * Adds or updates a test result.
	 */
	protected void addOrUpdate( TestCaseWrapper<hudson.plugins.testlink.parser.testng.Class> testResult )
	{
		final TestCaseWrapper<hudson.plugins.testlink.parser.testng.Class> temp = 
			this.results.get(testResult.getId());
		
		hudson.plugins.testlink.parser.testng.Class origin = testResult.getOrigin();
		listener.getLogger().println( Messages.Results_TestNG_TestResultsFound( testResult.getName(), testResult.getId(), origin, origin.getName(), testResult.getTestCase().getExecutionStatus().toString() ) );
		
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
	 * Retrieves the Execution Status for a TestNG test class. It is done 
	 * iterating over all the class methods. If a method has the status 
	 * FAIL, then we return the Execution Status failed, otherwise passed.
	 * 
	 * @param clazz The TestNG Test class.
	 * @return passed if the TestNG Test class contains no test methods with 
	 * status equals FAIL, otherwise failed.
	 */
	protected ExecutionStatus getTestNGExecutionStatus( hudson.plugins.testlink.parser.testng.Class clazz )
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
	 * Retrieves notes for TestNG suite and test class.
	 * 
	 * @param suite TestNG suite.
	 * @param clazz TestNG test class.
	 * @return notes for TestNG suite and test class.
	 */
	protected String getTestNGNotes( Suite suite, hudson.plugins.testlink.parser.testng.Class clazz )
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
	
}
