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

import hudson.Extension;
import hudson.FilePath;
import hudson.FilePath.FileCallable;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.plugins.testlink.TestLinkSite;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.kohsuke.stapler.DataBoundConstructor;
import org.tap4j.consumer.TapConsumer;
import org.tap4j.consumer.TapConsumerFactory;
import org.tap4j.model.Directive;
import org.tap4j.model.Plan;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.tap4j.util.DirectiveValues;

import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.Attachment;
import br.eti.kinoshita.testlinkjavaapi.util.TestLinkAPIException;

/**
 * <p>Seeks for test results matching each TAP file name with the key 
 * custom field.</p>
 * 
 * <p>Skips TAP Streams that were skipped.</p>
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 3.1
 */
public class TAPFileNameResultSeeker extends ResultSeeker {
	
	private static final long serialVersionUID = 3068999690225092293L;

	protected static final String TEXT_PLAIN_CONTENT_TYPE = "text/plain";
	
	private boolean attachTAPStream = false;
	private boolean attachYAMLishAttachments = false;
	private Boolean compareFullPath = false;
	
	/**
	 * @param includePattern
	 * @param keyCustomField
	 * @param attachTAPStream
	 * @param attachYAMLishAttachments
	 */
	@DataBoundConstructor
	public TAPFileNameResultSeeker(String includePattern, String keyCustomField, boolean attachTAPStream, boolean attachYAMLishAttachments, boolean includeNotes, Boolean compareFullPath) {
		super(includePattern, keyCustomField, includeNotes);
		this.attachTAPStream = attachTAPStream;
		this.attachYAMLishAttachments = attachYAMLishAttachments;
		this.compareFullPath = compareFullPath;
	}
	
	public void setAttachTAPStream(boolean attachTAPStream) {
		this.attachTAPStream = attachTAPStream;
	}
	
	public boolean isAttachTAPStream() {
		return attachTAPStream;
	}
	
	public void setAttachYAMLishAttachments(boolean attachYAMLishAttachments) {
		this.attachYAMLishAttachments = attachYAMLishAttachments;
	}
	
	public boolean isAttachYAMLishAttachments() {
		return attachYAMLishAttachments;
	}
	
	public Boolean isCompareFullPath() {
	    if (compareFullPath == null) 
	        compareFullPath = false;
        return compareFullPath;
    }

    public void setCompareFullPath(Boolean compareFullPath) {
        this.compareFullPath = compareFullPath;
    }

    @Extension
	public static class DescriptorImpl extends ResultSeekerDescriptor {
		/*
		 * (non-Javadoc)
		 * 
		 * @see hudson.model.Descriptor#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return "TAP file name"; // TBD: i18n
		}
	}

	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.result.ResultSeeker#seekAndUpdate(hudson.plugins.testlink.result.TestCaseWrapper<?>[], hudson.model.AbstractBuild, hudson.Launcher, hudson.model.BuildListener, hudson.plugins.testlink.TestLinkSite, hudson.plugins.testlink.result.Report)
	 */
	@Override
	public void seek(final TestCaseWrapper[] automatedTestCases, AbstractBuild<?, ?> build, Launcher launcher, final BuildListener listener, TestLinkSite testlink) throws ResultSeekerException {
		
		try {
			final Map<String, TestSet> testSets = build.getWorkspace().act(new FilePath.FileCallable<Map<String, TestSet>>() {
				private static final long serialVersionUID = 1L;

				private Map<String, TestSet> testSets;
				
				public Map<String, TestSet> invoke(File workspace, VirtualChannel channel)
						throws IOException, InterruptedException {
					final String[] tapFiles = TAPFileNameResultSeeker.this.scan(workspace, includePattern, listener);
					
					testSets = new HashMap<String, TestSet>(tapFiles.length);
					
					for(String tapFile : tapFiles) {
						final File input = new File(workspace, tapFile);
						final TapConsumer tapConsumer = TapConsumerFactory.makeTap13YamlConsumer();
						final TestSet testSet = tapConsumer.load(input);
						testSets.put(tapFile, testSet);
					}
					
					return testSets;
				}
			});
			
			for(String key : testSets.keySet()) {
				for(TestCaseWrapper automatedTestCase : automatedTestCases) {
					final String[] commaSeparatedValues = automatedTestCase.getKeyCustomFieldValues(this.keyCustomField);
					for(String value : commaSeparatedValues) {
						String tapFileNameWithoutExtension = key;
						int leftIndex = 0;
						if (!this.isCompareFullPath()) {
						    int lastIndex = tapFileNameWithoutExtension.lastIndexOf('/');
						    if (lastIndex > 0)
						        leftIndex = lastIndex+1;
						}
						int extensionIndex = tapFileNameWithoutExtension.lastIndexOf('.');
						if ( extensionIndex != -1 ) {
							tapFileNameWithoutExtension = tapFileNameWithoutExtension.substring(leftIndex, tapFileNameWithoutExtension.lastIndexOf('.'));
						}
						if(tapFileNameWithoutExtension.equals(value)) {
							final ExecutionStatus status = this.getExecutionStatus(testSets.get(key));
							automatedTestCase.addCustomFieldAndStatus(value, status);
							
							if(this.isIncludeNotes()) {
								final String notes = this.getTapNotes(testSets.get(key));
								automatedTestCase.appendNotes(notes);
							}
							
							this.handleResult(automatedTestCase, build, listener, testlink, status, testSets, key);
						}
					}
				}
			}
		} catch (IOException e) {
			throw new ResultSeekerException(e);
		} catch (InterruptedException e) {
			throw new ResultSeekerException(e);
		}
	}

	private void handleResult(TestCaseWrapper automatedTestCase, final AbstractBuild<?, ?> build, BuildListener listener, TestLinkSite testlink, ExecutionStatus status, final Map<String, TestSet> testSets, final String key) {
		if(automatedTestCase.getExecutionStatus(this.keyCustomField) != ExecutionStatus.NOT_RUN) {
			String platform = this.retrievePlatform(testSets.get(key));
			automatedTestCase.setPlatform(platform);
			
			try {
				final int executionId = testlink.updateTestCase(automatedTestCase);
				
				if(executionId > 0 && this.isAttachTAPStream()) {
					final String remoteWs = build.getWorkspace().getRemote();
					List<Attachment> attachments = build.getWorkspace().act( new FileCallable<List<Attachment>>() {
						
						private static final long serialVersionUID = -5411683541842375558L;

						List<Attachment> attachments = new ArrayList<Attachment>();
						
						public List<Attachment> invoke(File f, VirtualChannel channel) 
								throws IOException, InterruptedException {
							
							File reportFile = new File(remoteWs, key);
							final Attachment attachment = new Attachment();
							attachment.setContent(TAPFileNameResultSeeker.this.getBase64FileContent(reportFile));
							attachment.setDescription(reportFile.getName());
							attachment.setFileName(reportFile.getName());
							attachment.setFileSize(reportFile.length());
							attachment.setFileType(TEXT_PLAIN_CONTENT_TYPE);
							attachment.setTitle(reportFile.getName());
							attachments.add(attachment);
							
							if(TAPFileNameResultSeeker.this.isAttachYAMLishAttachments()) {
								attachments.addAll(TAPFileNameResultSeeker.this.retrieveListOfTapAttachments(testSets.get(key)));
							}
							
							return attachments;
						}
					});
					for(Attachment attachment : attachments) {
						testlink.uploadAttachment(executionId, attachment);
					}
				}
			} catch ( TestLinkAPIException te ) {
				build.setResult(Result.UNSTABLE);
				te.printStackTrace(listener.getLogger());
			} catch (IOException e) {
				build.setResult(Result.UNSTABLE);
				e.printStackTrace(listener.getLogger());
			} catch (InterruptedException e) {
				build.setResult(Result.UNSTABLE);
				e.printStackTrace(listener.getLogger());
			}
		}
	}

	/**
	 * @param testSet
	 * @return
	 */
	private ExecutionStatus getExecutionStatus(TestSet testSet) {
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
	 * Retrieves notes for a TAP test set.
	 * 
	 * @param testSet TAP test set.
	 * @return notes for a TAP test set.
	 */
	private String getTapNotes( TestSet testSet )
	{
		StringBuilder notes = new StringBuilder();
		
		notes.append( testSet.toString() );
		
		return notes.toString();
	}
	
	/**
	 * Retrieves the TestLink platform.
	 * 
	 * @param tapTestSet TAP test set.
	 * @return TestLink platform.
	 */
	private String retrievePlatform( TestSet tapTestSet )
	{
		String platform = null;
		
		Plan plan = tapTestSet.getPlan();
		Map<String, Object> planDiagnostic = plan.getDiagnostic();
		
		platform = this.extractPlatform( planDiagnostic );
		
		if ( platform == null ) 
		{
			for ( TestResult testResult : tapTestSet.getTestResults() )
			{
				Map<String, Object> diagnostic = testResult.getDiagnostic();
				platform = this.extractPlatform( diagnostic );
				if ( platform != null )
				{
					break;
				}
			}
		}
		
		return platform;
	}
	
	/**
	 * @param diagnostic
	 * @return TestLink Platform if present, {@code null} otherwise
	 */
	@SuppressWarnings("unchecked")
	private String extractPlatform( Map<String, Object> diagnostic )
	{
		String platform = null;
		Object extensions = diagnostic.get( "extensions" );
		if ( extensions != null && extensions instanceof Map<?, ?> )
		{
			Map<String, Object> extensionsInfo = (Map<String, Object>)extensions;
			Object testlink = extensionsInfo.get( "TestLink" );
			if ( testlink != null && testlink instanceof Map<?, ?>)
			{
				Map<String, Object> testLinkInfo = (Map<String, Object>)testlink;
				Object o = testLinkInfo.get("Platform");
				if(o == null) 
				{
					o = testLinkInfo.get("platform");
				}
				if ( o != null && o instanceof String )
				{
					platform = (String)o;
				}
			}
		}
		return platform;
	}
	
	/**
	 * Retrieves list of attachments from a TAP Test Set by 
	 * using its YAMLish data.
	 * 
	 * @param testSet TAP Test Set.
	 * @return List of attachments.
	 * @throws IOException 
	 */
	private List<Attachment> retrieveListOfTapAttachments( TestSet testSet ) throws IOException
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
	private void extractAttachments( 
		List<Attachment> attachments,
		Map<String, Object> diagnostic 
		) 
	throws IOException
	{
		final Object extensions = diagnostic.get( "extensions" );
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