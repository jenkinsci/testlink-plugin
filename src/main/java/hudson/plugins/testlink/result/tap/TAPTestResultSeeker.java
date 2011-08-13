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
package hudson.plugins.testlink.result.tap;

import hudson.model.BuildListener;
import hudson.plugins.testlink.parser.ParserException;
import hudson.plugins.testlink.parser.tap.TAPParser;
import hudson.plugins.testlink.result.TestCaseWrapper;
import hudson.plugins.testlink.result.TestLinkReport;
import hudson.plugins.testlink.result.TestResultSeeker;
import hudson.plugins.testlink.result.TestResultSeekerException;
import hudson.plugins.testlink.util.Messages;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.tap4j.model.Directive;
import org.tap4j.model.Plan;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.tap4j.util.DirectiveValues;
import org.tap4j.util.StatusValues;

import br.eti.kinoshita.testlinkjavaapi.model.Attachment;
import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;

/**
 * Seeks for test results of TAP test sets.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.5
 */
public class TAPTestResultSeeker<T extends TestSet> 
extends TestResultSeeker<TestSet>
{
	
	private static final long serialVersionUID = -652872928488835064L;

	protected final TAPParser parser = new TAPParser();
	
	protected final Map<Integer, TestCaseWrapper<TestSet>> results = new LinkedHashMap<Integer, TestCaseWrapper<TestSet>>();
	
	/**
	 * @param includePattern
	 * @param report
	 * @param keyCustomFieldName
	 * @param listener
	 */
	public TAPTestResultSeeker(String includePattern, TestLinkReport report,
			String keyCustomFieldName, BuildListener listener)
	{
		super(includePattern, report, keyCustomFieldName, listener);
	}
	
	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.result.TestResultSeeker#seek(java.io.File, java.lang.String)
	 */
	@Override
	public Map<Integer, TestCaseWrapper<TestSet>> seek( File directory )
			throws TestResultSeekerException
	{
		listener.getLogger().println( Messages.Results_TAP_LookingForTestSets() );
		listener.getLogger().println();
		
		if ( StringUtils.isBlank(includePattern) ) // skip TAP
		{
			listener.getLogger().println( Messages.Results_TAP_NoPattern() );
			listener.getLogger().println();
		}
		else
		{
			try
			{
				String[] tapReports = this.scan(directory, includePattern, listener);
				
				listener.getLogger().println( Messages.Results_TAP_NumberOfReportsFound( tapReports.length ) );
				listener.getLogger().println();
				
				this.doTAPReports( directory, tapReports );
			} 
			catch (IOException e)
			{
				throw new TestResultSeekerException( Messages.Results_TAP_IOException( includePattern, e.getMessage() ), e );
			}
			catch( Throwable t ) 
			{
				throw new TestResultSeekerException( Messages.Results_TAP_UnkownInternalError(), t );
			}
		}
		
		return results;
	}

	/**
	 * Parses TAP report files to look for Test Results of TestLink 
	 * Automated Test Cases.
	 * 
	 * @param directory Directory where to search for.
	 * @param tapReports Array of TAP report files.
	 */
	protected void doTAPReports( 
		File directory, 
		String[] tapReports )
	{
		
		for ( int i = 0 ; i < tapReports.length ; ++i )
		{
			listener.getLogger().println( Messages.Results_TAP_Parsing( tapReports[i] ) );
			listener.getLogger().println();
			
			File tapFile = new File(directory, tapReports[i]);
			
			try
			{
				final TestSet tapTestSet = parser.parse( tapFile );
				
				this.doTAPTestSet( tapTestSet, tapFile );
			}
			catch ( ParserException e )
			{
				listener.getLogger().println( Messages.Results_TAP_ParsingFail( tapFile, e.getMessage() ) );
				e.printStackTrace( listener.getLogger() );
				listener.getLogger().println();
			}
		}
	}
	
	/**
	 * Inspects a TAP test set looking for test results for the automated 
	 * test cases in TestLink. When it finds a test result, this test result 
	 * is added to the List of Test Results.
	 * 
	 * @param tapTestSet TAP test set.
	 * @param tapFile TAP file (added as an attachment for each test result 
	 * 				    found).
	 */
	protected void doTAPTestSet( 
		TestSet tapTestSet, 
		File tapFile )
	{
		listener.getLogger().println( Messages.Results_TAP_VerifyingTapSet( tapTestSet.getNumberOfTestResults() ) );
		listener.getLogger().println();
		
		String tapFileNameWithoutExtension = tapFile.getName();
		
		int extensionIndex = tapFileNameWithoutExtension.lastIndexOf('.');
		if ( extensionIndex != -1 )
		{
			tapFileNameWithoutExtension = tapFileNameWithoutExtension.substring(0, tapFileNameWithoutExtension.lastIndexOf('.'));
		}
		
		listener.getLogger().println( Messages.Results_TAP_LookingForTestResults( keyCustomFieldName, tapFileNameWithoutExtension ) );
		listener.getLogger().println();
		
		for ( br.eti.kinoshita.testlinkjavaapi.model.TestCase testLinkTestCase : this.report.getTestCases().values() )
		{
			listener.getLogger().println( Messages.Results_TAP_VerifyingTestLinkTestCase( testLinkTestCase.getName(), testLinkTestCase.getId() ) );
		
			this.findTestResult( tapFileNameWithoutExtension, tapTestSet, testLinkTestCase, tapFile );
		
			listener.getLogger().println();
		}
		
	}
	
	/**
	 * Looks for test results in a TAP Test Set test case.
	 */
	protected void findTestResult( 
		String tapFileNameWithoutExtension,
		TestSet tapTestSet, 
		TestCase testLinkTestCase, 
		File tapFile )
	{
		
		final List<CustomField> customFields = testLinkTestCase.getCustomFields();
		listener.getLogger().println( Messages.Results_TAP_ListOfCustomFields( customFields ) );
		
		final CustomField keyCustomField = this.getKeyCustomField( customFields );
		if ( keyCustomField != null ) 
		{
			final String[] commaSeparatedValues = this.split ( keyCustomField.getValue() );
			
			for ( String value : commaSeparatedValues )
			{
				if ( tapFileNameWithoutExtension.equals(value) && ExecutionStatus.BLOCKED != testLinkTestCase.getExecutionStatus() )
				{
					final TestCaseWrapper<TestSet> testResult = new TestCaseWrapper<TestSet>( testLinkTestCase, commaSeparatedValues, tapTestSet );
					
					final ExecutionStatus status = this.getTapExecutionStatus( tapTestSet );
					testResult.addCustomFieldAndStatus(value, status);
					
					String notes = this.getTapNotes( tapTestSet );
					
					try
					{
						List<Attachment> tapAttachments = this.getTapAttachments( testResult.getTestCase().getVersionId(), tapFile, tapTestSet );
						
						for( Attachment attachment : tapAttachments )
						{
							testResult.addAttachment( attachment );
						}
					}
					catch ( IOException ioe )
					{
						notes += Messages.Results_TAP_AddAttachmentsFail( ioe.getMessage() );
						ioe.printStackTrace( listener.getLogger() );
					}
					
					testResult.appendNotes( notes );
					
					this.addOrUpdate( testResult, tapFileNameWithoutExtension );
					
				}
			}
		}
	}

	/**
	 * Adds a test result to the map of test results. If the entry already 
	 * exists, then it is updated (notes, attachments and statuses).
	 */
	protected void addOrUpdate( TestCaseWrapper<TestSet> testResult, String tapFileNameWithoutExtension )
	{
		final TestCaseWrapper<TestSet> temp = this.results.get(testResult.getId());
		
		TestSet origin = testResult.getOrigin();
		listener.getLogger().println( Messages.Results_JUnit_TestResultsFound( testResult.getName(), testResult.getId(), origin, tapFileNameWithoutExtension, testResult.getTestCase().getExecutionStatus().toString() ) );
		
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
		
		if ( isSkipped( testSet)  )
		{
			status = ExecutionStatus.BLOCKED;
		}		
		else if ( isFailed( testSet ) )
		{
			status = ExecutionStatus.FAILED;
		}
		
		return status;
	}
	
	/**
	 * Checks if a test set contains a plan with skip directive or any test case
	 * with the same.
	 */
	private boolean isSkipped( TestSet testSet )
	{
		boolean r = false;
		
		if ( testSet.getPlan().isSkip() )
		{
			r = true;
		}
		else
		{
			for( TestResult testResult : testSet.getTestResults() )
			{
				final Directive directive = testResult.getDirective();
				if ( directive != null && directive.getDirectiveValue() == DirectiveValues.SKIP )
				{
					r = true;
					break;
				}
			}
		}
		return r;
	}

	/**
	 * Checks if a test set contains not ok's, bail out!'s or a TO-DO directive.
	 */
	private boolean isFailed( TestSet testSet )
	{
		boolean r = false;
		
		if ( testSet.containsNotOk() || testSet.containsBailOut() )
		{
			r = true;
		}
		else
		{
			for( TestResult testResult : testSet.getTestResults() )
			{
				final Directive directive = testResult.getDirective();
				if ( directive != null && directive.getDirectiveValue() == DirectiveValues.TODO )
				{
					r = true;
					break;
				}
			}
		}
		
		return r;
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
		attachment.setDescription( Messages.Results_TAP_AttachmentDescription( tapReportFile.getName() ) );
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
		List<Attachment> attachments = new LinkedList<Attachment>();
		
		Plan plan = testSet.getPlan();
		Map<String, Object> diagnostic = plan.getDiagnostic();
		
		this.extractAttachments ( attachments, diagnostic );
		
		for ( org.tap4j.model.TestResult testResult : testSet.getTestResults() )
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
	
}
