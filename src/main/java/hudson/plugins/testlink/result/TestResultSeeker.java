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
package hudson.plugins.testlink.result;

import hudson.FilePath.FileCallable;
import hudson.model.BuildListener;
import hudson.plugins.testlink.result.parser.Parser;
import hudson.plugins.testlink.result.parser.junit.JUnitParser;
import hudson.plugins.testlink.result.parser.junit.TestSuite;
import hudson.plugins.testlink.result.parser.tap.TAPParser;
import hudson.plugins.testlink.result.parser.testng.Class;
import hudson.plugins.testlink.result.parser.testng.Suite;
import hudson.plugins.testlink.result.parser.testng.Test;
import hudson.plugins.testlink.result.parser.testng.TestMethod;
import hudson.plugins.testlink.result.parser.testng.TestNGParser;
import hudson.plugins.testlink.result.scanner.Scanner;
import hudson.plugins.testlink.util.Messages;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.eti.kinoshita.tap4j.model.TestSet;
import br.eti.kinoshita.tap4j.parser.ParserException;
import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;

/**
 * Seeks for Test Results using a Scanner and Parsers.
 * 
 * @see {@link Scanner}
 * @see {@link Parser}
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
public class TestResultSeeker 
implements FileCallable<List<TestResult>>
{

	private static final long serialVersionUID = 1L;

	/**
	 * The TestLink Report object.
	 */
	private TestLinkReport report;
	
	/**
	 * The Key Custom Field Name.
	 */
	private String keyCustomFieldName;
	
	/**
	 * The ReportFilesPattern object.
	 * 
	 * @see {@link ReportFilesPatterns}
	 */
	private ReportFilesPatterns reportFilesPattern;
	
	/**
	 * The Hudson Build listener.
	 */
	private BuildListener listener;
	
	/**
	 * The scanner.
	 */
	private Scanner scanner;
	
	/**
	 * The JUnit parser.
	 */
	private JUnitParser junitParser;
	
	/**
	 * The TestNG parser.
	 */
	private TestNGParser testNGParser;
	
	/**
	 * The TAP parser.
	 */
	private TAPParser tapParser;

	/**
	 * Default constructor. Initializes the List of automated test cases, 
	 * the key custom field name, the ReportFilesPattern object, 
	 * the Hudson Build listener, the scanner and the parsers.
	 * 
	 * @param automatedTestCases The list of automated test cases.
	 * @param keyCustomField The name of the Key Custom Field.
	 * @param reportFilesPatterns The report files patterns.
	 * @param listener The Hudson Build listener.
	 */
	public TestResultSeeker( 
		TestLinkReport report, 
		String keyCustomFieldName, 
		ReportFilesPatterns reportFilesPatterns, 
		BuildListener listener			
	)
	{
		super();
		
		this.report = report;
		this.keyCustomFieldName = keyCustomFieldName;
		this.reportFilesPattern = reportFilesPatterns;
		this.listener = listener;
		
		this.scanner = new Scanner();
		
		this.junitParser = new JUnitParser();
		this.testNGParser = new TestNGParser();
		this.tapParser = new TAPParser();
	}
	
	public List<TestResult> seekTestResults( File directory ) 
	{
		final List<TestResult> testResults = new LinkedList<TestResult>();
		
		try
		{
			this.seekJUnitTestResults( directory, testResults );
			
			this.seekTestNGTestResults( directory, testResults );
			
			this.seekTapTestResults( directory, testResults );
		} 
		catch ( IOException ioe )
		{
			listener.getLogger().println( Messages.TestLinkBuilder_FailedToOpenReportFile() );
			ioe.printStackTrace( listener.getLogger() );
		}
		
		return testResults;
	}

	/**
	 * Seeks for JUnit test results in a given directory.
	 * 
	 * @param directory the directory to look at.
	 * @param results the list of Test Results
	 * @throws IOException 
	 */
	protected void seekJUnitTestResults( File directory, List<TestResult> results ) 
	throws IOException, ParserException
	{
		String junitIncludes = this.reportFilesPattern.getJunitXmlReportFilesPattern();
		
		if ( StringUtils.isNotBlank ( junitIncludes ) )
		{
			String[] junitReports = this.scanner.scan( directory, junitIncludes, listener );
			
			for( String junitReport : junitReports )
			{
				// For each JUnit report...
				File junitReportFile = new File ( directory, junitReport );
				// We parse the report to retrieve a TestSuite object
				TestSuite testSuite = this.junitParser.parse( junitReportFile );
				
				String testSuiteName = testSuite.getName();
				if ( StringUtils.isNotBlank( testSuiteName ) )
				{
					for ( TestCase testCase : this.report.getTestCases() )
					{
						// For each automated test case
						for ( CustomField customField : testCase.getCustomFields() )
						{
							// We search for the key custom field
							// If the key custom field value is equal to the 
							// test suite name, then we have a Test Result to update
							if ( 
								customField.getName().equals( this.keyCustomFieldName)
								&& 
								testSuiteName.equals(customField.getValue())
							)
							{
								ExecutionStatus status = this.getJUnitExecutionStatus( testSuite );
								testCase.setExecutionStatus( status );
								TestResult testResult = new TestResult(testCase, report.getBuild(), report.getTestPlan());
								results.add( testResult );
								break;
							}
						}
					}
				}
				
				//listener.getLogger().println( Messages.TestLinkBuilder_ShowFoundTestResults(foundResults.length) );
				//listener.getLogger().println( Messages.TestLinkBuilder_NoTestResultsFound() );
			}
		}
	}
	
	/**
	 * Retrieves a Execution Status from a JUnit TestSuite. If the TestSuite 
	 * contains failures or errors it returns failed, otherwise it returns true. 
	 * 
	 * @param testSuite The JUnit TestSuite.
	 * @return TestLink Execution Status.
	 */
	protected ExecutionStatus getJUnitExecutionStatus( TestSuite testSuite )
	{
		ExecutionStatus status = ExecutionStatus.PASSED;
		String errors = testSuite.getErrors();
		if ( StringUtils.isNotBlank( errors ) )
		{
			Long errorsNumber = Long.parseLong( errors );
			if ( errorsNumber > 0 )
			{
				status = ExecutionStatus.FAILED;
			}
		}
		String failures = testSuite.getFailures();
		if ( status != ExecutionStatus.FAILED && StringUtils.isNotBlank( failures ) )
		{
			Long failuresNumber = Long.parseLong( failures );
			if ( failuresNumber > 0 )
			{
				status = ExecutionStatus.FAILED;
			}
		}
		
		return status;
	}

	/**
	 * Seeks for TestNG test results in a given directory.
	 * 
	 * @param directory the directory to look at.
	 * @param results the list of Test Results
	 * @throws IOException 
	 */
	protected void seekTestNGTestResults( File directory, List<TestResult> results ) 
	throws IOException, ParserException
	{
		String testNGIncludes = this.reportFilesPattern.getTestNGXmlReportFilesPattern();

		if ( StringUtils.isNotEmpty ( testNGIncludes ) )
		{
			String[] testNGReports = this.scanner.scan( directory, testNGIncludes, listener );
			
			for( String testNGReport : testNGReports )
			{
				// For each TestNG report...
				File testNGReportFile = new File ( directory, testNGReport );
				// We parse the report to retrieve a Suite object
				Suite suite = this.testNGParser.parse( testNGReportFile );
				
				if ( suite.getTests().size() > 0 )
				{
					for ( Test test : suite.getTests() )
					{
						for ( Class clazz : test.getClasses() )
						{
							String clazzName = clazz.getName();
							if ( StringUtils.isNotBlank( clazzName ) )
							{
								for ( TestCase testCase : this.report.getTestCases() )
								{
									
									// For each custom field
									for ( CustomField customField : testCase.getCustomFields() )
									{
										// We search for the key custom field
										// If the key custom field value is equal to the 
										// test class name, then we have a Test Result to update
										if ( 
											customField.getName().equals( this.keyCustomFieldName)
											&& 
											clazzName.equals(customField.getValue())
										)
										{
											ExecutionStatus status = this.getTestNGExecutionStatus( clazz );
											testCase.setExecutionStatus( status );
											TestResult testResult = new TestResult(testCase, report.getBuild(), report.getTestPlan());
											results.add( testResult );
											break;
										}
									}
								}
							}
						}
					}
				}
				
				//listener.getLogger().println( Messages.TestLinkBuilder_ShowFoundTestResults(foundResults.length) );
				//listener.getLogger().println( Messages.TestLinkBuilder_NoTestResultsFound() );
			}
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
	 * Seeks for TAP test results in a given directory.
	 * 
	 * @param directory the directory to look at.
	 * @param results the list of Test Results
	 * @throws IOException 
	 */
	protected void seekTapTestResults( File directory, List<TestResult> results )
	throws IOException, ParserException
	{
		String tapIncludes = this.reportFilesPattern.getTapStreamReportFilesPattern();
		
		if ( StringUtils.isNotBlank( tapIncludes ) )
		{
			String[] tapReports = this.scanner.scan( directory, tapIncludes, listener );
			for ( String tapReport : tapReports )
			{
				File tapReportFile = new File ( directory, tapReport );
				TestSet testSet = this.tapParser.parse( tapReportFile );
				
				String tapFileNameWithoutExtension = tapReportFile.getName();
				
				int extensionIndex = tapFileNameWithoutExtension.lastIndexOf('.');
				if ( extensionIndex != -1 )
				{
					tapFileNameWithoutExtension = tapFileNameWithoutExtension.substring(0, tapFileNameWithoutExtension.lastIndexOf('.'));
				}
				
				for ( TestCase testCase : this.report.getTestCases() )
				{
					// For each automated test case
					for ( CustomField customField : testCase.getCustomFields() )
					{
						// We search for the key custom field
						// If the key custom field value is equal to the 
						// test suite name, then we have a Test Result to update
						if ( 
							customField.getName().equals( this.keyCustomFieldName)
							&& 
							tapFileNameWithoutExtension.equals(customField.getValue())
						)
						{
							ExecutionStatus status = this.getTAPExecutionStatus( testSet );
							testCase.setExecutionStatus( status );
							TestResult testResult = new TestResult(testCase, report.getBuild(), report.getTestPlan());
							results.add( testResult );
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Retrieves the TestLink Execution Status from a TAP Test Set. Returns 
	 * failed only when the test set contains at least one not ok statement.
	 * 
	 * @param testSet the TAP TestSet.
	 * @return failed only when the test set contains at least one not ok statement, otherwise it will return passed.
	 */
	protected ExecutionStatus getTAPExecutionStatus( TestSet testSet )
	{
		ExecutionStatus status = ExecutionStatus.PASSED;
		
		if ( testSet.containsNotOk() )
		{
			status = ExecutionStatus.FAILED;
		}
		
		return status;
	}

	/* (non-Javadoc)
	 * @see hudson.FilePath.FileCallable#invoke(java.io.File, hudson.remoting.VirtualChannel)
	 */
	public List<TestResult> invoke( File f, VirtualChannel channel )
			throws IOException, InterruptedException
	{
		return this.seekTestResults(f);
	}
	
}
