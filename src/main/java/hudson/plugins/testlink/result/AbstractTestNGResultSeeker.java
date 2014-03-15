/*
 * The MIT License
 *
 * Copyright (c) <2012> <Bruno P. Kinoshita>
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

import hudson.FilePath;
import hudson.FilePath.FileCallable;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.plugins.testlink.TestLinkSite;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;






import org.apache.commons.lang.StringUtils;

import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.Attachment;
import br.eti.kinoshita.testlinkjavaapi.util.TestLinkAPIException;

import com.tupilabs.testng.parser.Suite;
import com.tupilabs.testng.parser.TestNGParser;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 3.1
 */
public abstract class AbstractTestNGResultSeeker extends ResultSeeker {

	private static final long serialVersionUID = -1017414394764084125L;
	
	public static final String PASS = "PASS";
	public static final String FAIL = "FAIL";
	public static final String SKIP = "SKIP";
	
	public static final String TEXT_XML_CONTENT_TYPE = "text/xml";
	public static final String TEXT_TXT_CONTENT_TYPE = "text/plain";
	public static final String TEXT_PDF_CONTENT_TYPE = "application/pdf";

	protected final TestNGParser parser = new TestNGParser();
	
	private boolean attachTestNGXML = false;
	
	private boolean attachPdfReport= false;
	
	private boolean attachTestSourcePage = false;
	
	private String testCasesReportFolder="";
	
	private boolean markSkippedTestAsBlocked = false;
	
	
	/**
	 * @param includePattern
	 * @param keyCustomField
	 * @param attachTestNGXML
	 * @param markSkippedTestAsBlocked
	 * @param includeNotes
	 */
	public AbstractTestNGResultSeeker(String includePattern, String keyCustomField, String keywordsExecutedFilter, boolean attachTestNGXML, boolean markSkippedTestAsBlocked, boolean includeNotes) {
		super(includePattern, keyCustomField, keywordsExecutedFilter, includeNotes);
		this.attachTestNGXML = attachTestNGXML;
		this.markSkippedTestAsBlocked = markSkippedTestAsBlocked;
	}

	public void setAttachTestNGXML(boolean attachTestNGXML) {
		this.attachTestNGXML = attachTestNGXML;
	}
	
	public boolean isAttachTestNGXML() {
		return attachTestNGXML;
	}
	

	public boolean isAttachPdfReport() {
		return attachPdfReport;
	}

	public void setAttachPdfReport(boolean attachPdfReport) {
		this.attachPdfReport = attachPdfReport;
	}
	
	public void setAttachTestSourcePage(boolean attachTestSourcePage) {
		this.attachTestSourcePage = attachTestSourcePage;
	}

	public boolean isAttachTestSourcePage() {
		return attachTestSourcePage;
	}
	
	public void setMarkSkippedTestAsBlocked(boolean markSkippedTestAsBlocked) {
		this.markSkippedTestAsBlocked = markSkippedTestAsBlocked;
	}
	
	public boolean isMarkSkippedTestAsBlocked() {
		return markSkippedTestAsBlocked;
	}
	
	public String getTestCasesReportFolder() {
		return testCasesReportFolder;
	}

	public void setTestCasesReportFolder(String testCasesReportFolder) {
		this.testCasesReportFolder = testCasesReportFolder;
	}
	
	/**
	 * @param automatedTestCase
	 * @param build
	 * @param listener
	 * @param testlink
	 * @param status
	 * @param suiteResult
	 */
	protected void handleResult(TestCaseWrapper automatedTestCase, AbstractBuild<?, ?> build, BuildListener listener, TestLinkSite testlink, ExecutionStatus status, final Suite suiteResult) {
		if(automatedTestCase.getExecutionStatus(this.keyCustomField) != ExecutionStatus.NOT_RUN) {
			try {
				updateTestlink(automatedTestCase, build, testlink, suiteResult);
				
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
	 * @param automatedTestCase
	 * @param build
	 * @param testlink
	 * @param suiteResult
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void updateTestlink(TestCaseWrapper automatedTestCase, AbstractBuild<?, ?> build, TestLinkSite testlink,
			final Suite suiteResult) throws IOException, InterruptedException {
		final int executionId = testlink.updateTestCase(automatedTestCase);
		
		if(executionId > 0 && this.isAttachTestNGXML()) {
			Attachment xmlAttachment = buildAttachment(build, suiteResult.getFile(),TEXT_XML_CONTENT_TYPE); 
			testlink.uploadAttachment(executionId, xmlAttachment);
		}
		
		if(executionId > 0 && this.isAttachTestSourcePage()) {
			
			String reportFullFileName = getReportFullFileName(build, automatedTestCase); 
			
			if (StringUtils.isNotEmpty(reportFullFileName)){
				Attachment txtAttachment = buildAttachment(build, reportFullFileName + ".txt", TEXT_TXT_CONTENT_TYPE); 
				testlink.uploadAttachment(executionId, txtAttachment);
			}
			
		}
		
		if(executionId > 0 && this.isAttachPdfReport()) {
			
			String reportFullFileName = getReportFullFileName(build, automatedTestCase); 
			
			if (StringUtils.isNotEmpty(reportFullFileName)){
				Attachment pdfAttachment = buildAttachment(build, reportFullFileName + ".pdf", TEXT_PDF_CONTENT_TYPE); 
				testlink.uploadAttachment(executionId, pdfAttachment);
			}
			
		}
	}
	

	/**
	 * @param build
	 * @param suiteResult
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private String getReportFullFileName(AbstractBuild<?, ?> build, final TestCaseWrapper automatedTestCase) throws IOException,
			InterruptedException {

		// Get the name		
		String relativePath = "";
		if (StringUtils.isNotEmpty(this.testCasesReportFolder)){
			relativePath =this.testCasesReportFolder+ "/";
		}
		if (StringUtils.isNotEmpty(automatedTestCase.getPlatform())){
			relativePath = relativePath + automatedTestCase.getPlatform()+ "/";
		}
		String customFieldValue = automatedTestCase.getKeyCustomFieldValue(this.keyCustomField);
		String fileToUpload = customFieldValue.replace("#", "_");
		fileToUpload =  relativePath + fileToUpload;
	
		// Get the full path
		FilePath fp = new FilePath(build.getWorkspace(), fileToUpload);
		String result = fp.act(new PathFileCallable());
		return result;
	};
	
	
	
	/**
	 * @param build
	 * @param suiteResult
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private Attachment buildAttachment(AbstractBuild<?, ?> build, final String fileResult, String contentType) throws IOException,
			InterruptedException {
		
		FilePath fp = new FilePath(build.getWorkspace(), fileResult);
		Attachment attachment = fp.act(new BuildAttachmentFileCallable(fileResult,contentType));
		return attachment;
	}
	
	
	
	/**
	 * @author s2o
	 *
	 */
	private static class PathFileCallable implements FileCallable<String> {

		private static final long serialVersionUID = 6373621466401478661L;

		public String invoke(File file, VirtualChannel channel) throws IOException, InterruptedException {
		    if (file.getAbsoluteFile().exists()){
		      return file.getAbsoluteFile().getPath();
		    } else {
		      return "";
		    }
		  }		
	}


	/**
	 * @author s2o
	 *
	 */
	private static class BuildAttachmentFileCallable implements FileCallable<Attachment> {
		
		private static final long serialVersionUID = 2926421342182432160L;
		private String fileResult;
		private String contentType;

		public BuildAttachmentFileCallable(String fileResult, String contentType){
			this.fileResult=fileResult;
			this.contentType=contentType;
		}
		  public Attachment invoke(File file, VirtualChannel channel) throws IOException, InterruptedException {
			  File reportFile = new File(fileResult);
				final Attachment attachment = new Attachment();
				attachment.setContent(AbstractTestNGResultSeeker.getBase64FileContent(reportFile));
				attachment.setDescription(reportFile.getName());
				attachment.setFileName(reportFile.getName());
				attachment.setFileSize(reportFile.length());
				attachment.setFileType(contentType);
				attachment.setTitle(reportFile.getName());
				return attachment;
		  }		
	}

	
}
