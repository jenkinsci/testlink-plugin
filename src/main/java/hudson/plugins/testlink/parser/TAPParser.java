/**
 * 
 */
package hudson.plugins.testlink.parser;

import hudson.model.BuildListener;
import hudson.plugins.testlink.model.TestLinkReport;
import hudson.plugins.testlink.model.TestResult;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.mail.MessagingException;

import org.kohsuke.stapler.framework.io.IOException2;

import br.eti.kinoshita.tap4j.consumer.Tap13Consumer;
import br.eti.kinoshita.tap4j.consumer.TapConsumer;
import br.eti.kinoshita.tap4j.consumer.TapParserException;
import br.eti.kinoshita.tap4j.model.AbstractTapElementDiagnostic;
import br.eti.kinoshita.tap4j.model.Plan;
import br.eti.kinoshita.testlinkjavaapi.model.Attachment;
import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;

/**
 * @author Bruno P. Kinoshita
 *
 */
public class TAPParser 
extends Parser
{

	protected TestLinkReport report;
	protected String keyCustomField;
	
	public TAPParser( 
			TestLinkReport report, 
			String keyCustomField, 
			BuildListener listener, String 
			includePattern) 
	{
		super(listener, includePattern);
		this.report = report;
		this.keyCustomField = keyCustomField;
	}

	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.parser.Parser#getName()
	 */
	@Override
	public String getName() {
		return "TAP";
	}

	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.parser.Parser#parseFile(java.io.File)
	 */
	@Override
	protected List<TestResult> parseFile(File file) 
	throws IOException 
	{
		final List<TestResult> testResults = new ArrayList<TestResult>();
		
		String className = file.getName();
		
		int extensionIndex = className.lastIndexOf('.');
		if ( extensionIndex != -1 )
		{
			className = className.substring(0, className.lastIndexOf('.'));
		}
		
		TestCase found = null;
		for( TestCase testCase : this.report.getTestCases() )
		{
			for( CustomField customField : testCase.getCustomFields() )
			{
				if ( customField.getName().equalsIgnoreCase(this.keyCustomField))
				{
					if  (className.equals(customField.getValue()))
					{
						found = testCase;
						break;
					}
				}
			}
		}
		if ( found != null )
		{
			
			TapConsumer consumer = new Tap13Consumer();
			
			try
			{
				consumer.parseFile( file );
			} 
			catch (TapParserException e)
			{
				throw new IOException2( e );
			}
			
			if ( consumer.containsNotOk() )
			{
				found.setExecutionStatus(ExecutionStatus.FAILED);
			}
			else 
			{
				found.setExecutionStatus( ExecutionStatus.PASSED );
			}
			
			String notes = "TAP Tests: " + consumer.getNumberOfTestResults() + 
					", TAP Plan: " + consumer.getPlan();
			
			//this.report.getTestCases().remove( found );
			TestResult testResult = new TestResult(found, report.getBuild(), report.getTestPlan());
			testResult.setNotes( notes );
			
			try 
			{
				testResult.getAttachments().addAll( this.getAttachments( consumer ) );
			}
			catch ( MessagingException e ) 
			{
				listener.getLogger().println( "Invalid attachment found in TAP diagnostic: " + e.getMessage() );
			}
			catch ( NullPointerException npe )
			{
				listener.getLogger().println( "Invalid attachment found in TAP diagnostic: " + npe.getMessage() );
			}
			
			// upload tap file
			Attachment attachment;
			try
			{
				attachment = this.getAttachment( found.getVersionId(), file );
			} 
			catch (MessagingException e)
			{
				throw new IOException("Failed to read JUnit report file content and convert to Base64: " +  e.getMessage(), e);
			}
			testResult.addAttachment( attachment );
			
			testResults.add( testResult );
		}
	
		return testResults;
		
	}

	/**
	 * @param consumer
	 * @param testResult
	 * @throws MessagingException 
	 * @throws IOException 
	 */
	private List<Attachment> getAttachments( TapConsumer consumer ) 
	throws IOException, MessagingException 
	{
		List<Attachment> returnValue = new ArrayList<Attachment>();
		Plan plan = consumer.getPlan();
		returnValue.addAll( this.getAttachments( plan ) );
		List<br.eti.kinoshita.tap4j.model.TestResult> trs = consumer.getTestResults();
		for ( br.eti.kinoshita.tap4j.model.TestResult result : trs )
		{
			returnValue.addAll( this.getAttachments( result ) );
		}
		return returnValue;
	}
	
	@SuppressWarnings("unchecked")
	public List<Attachment> getAttachments( 
			AbstractTapElementDiagnostic diagnostic ) 
	throws IOException, MessagingException
	{
		final List<Attachment> attachments = new ArrayList<Attachment>();
		final Map<String, Object> diag = diagnostic.getDiagnostic();
		
		if ( diag != null )
		{
			Object value = diag.get("extensions");
			
			if ( value != null && value instanceof Map<?, ?> )
			{
				Map<String, Object> extension = (Map<String, Object>)value;
				value = extension.get("Files");
				if ( value != null && value instanceof Map<?, ?> )
				{
					Map<String, Object> testLinkAttachments = (Map<String, Object>)value;
					Set<Entry<String, Object>> entrySet = testLinkAttachments.entrySet();
					
					for (Iterator<Entry<String, Object>> iterator = entrySet.iterator(); 
						iterator.hasNext(); ) 
					{
						Entry<String, Object> entry = iterator.next();
						if ( entry.getKey().equals("File") )
						{
							final Map<String, Object> attachmentMap = (Map<String, Object>) entry.getValue();
							final Attachment attachment = new Attachment();
							
							final String file = valueOrNull( attachmentMap.get("File-Location") );
							
							final File attachmentFile = new File( file );
							
							attachment.setContent( super.getBase64FileContent( attachmentFile ) );
							attachment.setFileName( attachmentFile.getName() );
							attachment.setFileSize( attachmentFile.length() );
							attachment.setFileType( valueOrNull(attachmentMap.get("File-Type")) );
							attachment.setTitle( valueOrNull(attachmentMap.get("File-Title")) );
							attachment.setDescription( valueOrNull(attachmentMap.get("File-Description")) );
							
							attachments.add( attachment );
						}
					}
				}
			}
		}
		return attachments;
	}

	/**
	 * @param object
	 * @return
	 */
	private String valueOrNull(Object object) 
	{
		String returnValue = null;
		if ( object != null )
		{
			returnValue = object.toString();
		}
		return returnValue;
	}


}
