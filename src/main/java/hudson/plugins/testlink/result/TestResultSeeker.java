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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import br.eti.kinoshita.tap4j.model.Plan;
import br.eti.kinoshita.tap4j.model.TestSet;
import br.eti.kinoshita.tap4j.parser.ParserException;
import br.eti.kinoshita.testlinkjavaapi.model.Attachment;
import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;

/**
 * Seeks for Test Results using a Scanner and Parsers.
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
	 * @param report TestLink report.
	 * @param keyCustomFieldName The name of the Key Custom Field.
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
	
	/**
	 * Seeks test results in a given directory. It will seek for JUnit, TestNG 
	 * and TAP test results.
	 * 
	 * @param directory directory to seek for test results.
	 * @return list of test results.
	 */
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
		
		if ( testResults.size() > 0 )
		{
			listener.getLogger().println( Messages.TestLinkBuilder_ShowFoundTestResults(testResults.size()) );
		}
		else
		{
			listener.getLogger().println( Messages.TestLinkBuilder_NoTestResultsFound() );
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
								
								String notes = this.getJUnitNotes( testSuite );
								
								try
								{
									Attachment junitAttachment = this.getJUnitAttachment( testResult.getTestCase().getVersionId(), junitReportFile );
									testResult.addAttachment( junitAttachment );
								}
								catch ( IOException ioe )
								{
									notes += "\n\nFailed to add JUnit attachment to this test case execution. Error message: " + ioe.getMessage();
									ioe.printStackTrace( listener.getLogger() );
								}
								
								testResult.setNotes( notes );
								results.add( testResult );
								break;
							}
						}
					}
				}
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
	 * Retrieves notes about JUnit test result.
	 * 
	 * @param testSuite JUnit test suite
	 * @return notes about JUnit test result.
	 */
	protected String getJUnitNotes( TestSuite testSuite )
	{
		StringBuilder notes = new StringBuilder();
		
		notes.append( "name: " );
		notes.append( testSuite.getName()+ "\n" );
		
		notes.append( "hostname: " );
		notes.append( testSuite.getHostname() + "\n" );
		
		notes.append( "tests: " );
		notes.append( testSuite.getTests() + "\n" );
		
		notes.append( "errors: " );
		notes.append( testSuite.getErrors() + "\n" );
		
		notes.append( "failures: " );
		notes.append( testSuite.getFailures() + "\n" );
		
		notes.append( "time: " );
		notes.append( testSuite.getTime()+ "\n" );
		
		notes.append( "timestamp: " );
		notes.append( testSuite.getTimestamp()+ "\n" );
		
		notes.append( "system-out: " );
		notes.append( testSuite.getSystemOut()+ "\n" );
		
		notes.append( "system-err: " );
		notes.append( testSuite.getSystemErr()+ "\n" );
		
		return notes.toString();
	}
	
	/**
	 * Retrieves the JUnit report file as attachment for TestLink.
	 * 
	 * @param versionId TestLink Test Case version ID.
	 * @param junitReportFile JUnit report file.
	 * @return attachment for TestLink.
	 */
	protected Attachment getJUnitAttachment( Integer versionId,
			File junitReportFile ) 
	throws IOException
	{
		Attachment attachment = new Attachment();
		
		String fileContent = this.getBase64FileContent(junitReportFile );
		attachment.setContent( fileContent );
		attachment.setDescription( "JUnit report file " + junitReportFile.getName() );
		attachment.setFileName( junitReportFile.getName() );
		attachment.setFileSize( junitReportFile.length() );
		attachment.setTitle( junitReportFile.getName() );
		attachment.setFileType("text/xml");
		
		return attachment;
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
											String notes = this.getTestNGNotes( suite, clazz );
											
											try
											{
												Attachment testNGAttachment = this.getTestNGAttachment( testResult.getTestCase().getVersionId(), testNGReportFile );
												testResult.addAttachment( testNGAttachment );
											}
											catch ( IOException ioe )
											{
												notes += "\n\nFailed to add TestNG attachment to this test case execution. Error message: " + ioe.getMessage();
												ioe.printStackTrace( listener.getLogger() );
											}
											
											testResult.setNotes( notes );
											
											results.add( testResult );
											break;
										}
									}
								}
							}
						}
					}
				}
			}
		}
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
							ExecutionStatus status = this.getTapExecutionStatus( testSet );
							testCase.setExecutionStatus( status );
							TestResult testResult = new TestResult(testCase, report.getBuild(), report.getTestPlan());
							
							String notes = this.getTapNotes( testSet );
							
							try
							{
								List<Attachment> tapAttachments = this.getTapAttachments( testResult.getTestCase().getVersionId(), tapReportFile, testSet );
								
								for( Attachment attachment : tapAttachments )
								{
									testResult.addAttachment( attachment );
								}
							}
							catch ( IOException ioe )
							{
								notes += "\n\nFailed to add TAP attachments to this test case execution. Error message: " + ioe.getMessage();
								ioe.printStackTrace( listener.getLogger() );
							}
							
							testResult.setNotes( notes );
							results.add( testResult );
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Retrieves notes for a TAP test set.
	 * 
	 * @param testSet TAP test set.
	 * @return notes for a TAP test set.
	 */
	protected String getTapNotes( TestSet testSet )
	{
		StringBuilder notes = new StringBuilder();
		
		notes.append( testSet.toString() );
		
		return notes.toString();
	}

	/**
	 * Retrieves the TestLink Execution Status from a TAP Test Set. Returns 
	 * failed only when the test set contains at least one not ok statement.
	 * 
	 * @param testSet the TAP TestSet.
	 * @return failed only when the test set contains at least one not ok statement, otherwise it will return passed.
	 */
	protected ExecutionStatus getTapExecutionStatus( TestSet testSet )
	{
		ExecutionStatus status = ExecutionStatus.PASSED;
		
		if ( testSet.containsNotOk() )
		{
			status = ExecutionStatus.FAILED;
		}
		
		return status;
	}
	
	/**
	 * Retrieves list of TAP Attachments. Besides the TAP stream file itself, 
	 * this method also adds all extension / Files to this list.
	 * 
	 * @param versionId TestLink TestCase version id.
	 * @param tapReportFile TAP Report file.
	 * @param testSet TAP Test Set.
	 * @return TAP Attachments.
	 */
	protected List<Attachment> getTapAttachments( Integer versionId, File tapReportFile, TestSet testSet )
	throws IOException
	{
		
		List<Attachment> attachments = this.retrieveListOfTapAttachments( testSet );
		
		Attachment attachment = new Attachment();
		
		String fileContent = this.getBase64FileContent( tapReportFile );
		attachment.setContent( fileContent );
		attachment.setDescription( "TAP report file " + tapReportFile.getName() );
		attachment.setFileName( tapReportFile.getName() );
		attachment.setFileSize( tapReportFile.length() );
		attachment.setTitle( tapReportFile.getName() );
		attachment.setFileType("text/plan");
		
		attachments.add( attachment );
		
		return attachments;
	}
	
	/**
	 * Retrieves list of attachments from a TAP Test Set by 
	 * using its YAMLish data.
	 * 
	 * @param testSet TAP Test Set.
	 * @return List of attachments.
	 * @throws IOException 
	 */
	protected List<Attachment> retrieveListOfTapAttachments( TestSet testSet ) throws IOException
	{
		List<Attachment> attachments = new ArrayList<Attachment>();
		
		Plan plan = testSet.getPlan();
		Map<String, Object> diagnostic = plan.getDiagnostic();
		
		this.extractAttachments ( attachments, diagnostic );
		
		for ( br.eti.kinoshita.tap4j.model.TestResult testResult : testSet.getTestResults() )
		{
			this.extractAttachments(attachments, testResult.getDiagnostic());
		}
		
		return attachments;
	}

	/**
	 * Extracts attachments from a TAP diagnostic and adds into a list of 
	 * attachments.
	 * 
	 * @param attachments List of attachments
	 * @param diagnostic TAP diagnostic
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	protected void extractAttachments( 
		List<Attachment> attachments,
		Map<String, Object> diagnostic 
		) 
	throws IOException
	{
		Object extensions = diagnostic.get( "extensions" );
		if ( extensions != null && extensions instanceof Map<?, ?>)
		{
			Map<String, Object> extensionsMap = (Map<String, Object>)extensions;
			Object files = extensionsMap.get("Files");
			if ( files != null && files instanceof Map<?, ?>)
			{
				Map<String, Object> filesMap = (Map<String, Object>)files;
				Set<Entry<String, Object>> filesMapEntrySet = filesMap.entrySet();
				Iterator<Entry<String, Object>> iterator = filesMapEntrySet.iterator();
				
				while( iterator != null && iterator.hasNext() )
				{
					Entry<String, Object> filesMapEntry = iterator.next();
					Object entryObject = filesMapEntry.getValue();
					
					if ( entryObject != null && entryObject instanceof Map<?, ?>)
					{
						Map<String, Object> entryObjectMap = (Map<String, Object>)entryObject;
						
						Object oFileContent = entryObjectMap.get("File-Content");
						if ( oFileContent != null )
						{
							String fileContent = ""+oFileContent;
							
							Attachment attachment = new Attachment();
							
							attachment.setContent( fileContent );
							
							try
							{
								attachment.setFileSize( Long.parseLong( ""+entryObjectMap.get("File-Size") ) );
							}
							catch ( NumberFormatException nfe )
							{}
							
							attachment.setFileName( ""+entryObjectMap.get("File-Name") );
							attachment.setTitle( ""+entryObjectMap.get("File-Title") );
							attachment.setDescription( ""+entryObjectMap.get("File-Description") );
							attachment.setFileType( ""+entryObjectMap.get("File-Type") );
							
							attachments.add( attachment );
							
						}
						else 
						{
							Object fileLocation = entryObjectMap.get("File-Location");
							String fileLocationText = ""+fileLocation;
							File file = new File( fileLocationText );
							
							if ( file.exists() )
							{
								Attachment attachment = new Attachment();
								
								Object oContent = entryObjectMap.get("File-Content");
								if ( oContent != null )
								{
									attachment.setContent( ""+oContent );
									try
									{
										attachment.setFileSize( Long.parseLong( ""+entryObjectMap.get("File-Size") ) );
									}
									catch ( NumberFormatException nfe )
									{
										attachment.setFileSize( file.length() );
									}
								}
								else
								{
									String fileContent = this.getBase64FileContent( file );
									attachment.setContent( fileContent );
									attachment.setFileSize( file.length() );
								}
								
								attachment.setFileName( ""+entryObjectMap.get("File-Name") );
								attachment.setTitle( ""+entryObjectMap.get("File-Title") );
								attachment.setDescription( ""+entryObjectMap.get("File-Description") );
								attachment.setFileType( ""+entryObjectMap.get("File-Location") );
								
								attachments.add( attachment );
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Retrieves the file content encoded in Base64.
	 * 
	 * @param file file to read the content.
	 * @return file content encoded in Base64.
	 * @throws IOException 
	 */
	protected String getBase64FileContent( File file ) 
	throws IOException
	{
		byte[] fileData = FileUtils.readFileToByteArray(file);
		return Base64.encodeBase64String( fileData );
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
