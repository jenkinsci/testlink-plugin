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

import hudson.FilePath.FileCallable;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.plugins.testlink.TestLinkSite;
import hudson.plugins.testlink.testng.Suite;
import hudson.plugins.testlink.testng.TestNGParser;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPIException;
import br.eti.kinoshita.testlinkjavaapi.model.Attachment;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 3.1
 */
public abstract class AbstractTestNGResultSeeker extends ResultSeeker {

	private static final long serialVersionUID = -5069755890860922333L;
	
	protected static final String PASS = "PASS";
	protected static final String FAIL = "FAIL";
	protected static final String SKIP = "SKIP";
	
	private static final String TEXT_XML_CONTENT_TYPE = "text/xml";

	protected final TestNGParser parser = new TestNGParser();
	
	private boolean attachTestNGXML = false;
	
	private boolean markSkippedTestAsBlocked = false;
	
	public AbstractTestNGResultSeeker(String includePattern, String keyCustomField, boolean attachTestNGXML, boolean markSkippedTestAsBlocked) {
		super(includePattern, keyCustomField);
		this.attachTestNGXML = attachTestNGXML;
		this.markSkippedTestAsBlocked = markSkippedTestAsBlocked;
	}

	public void setAttachTestNGXML(boolean attachTestNGXML) {
		this.attachTestNGXML = attachTestNGXML;
	}
	
	public boolean isAttachTestNGXML() {
		return attachTestNGXML;
	}
	
	public void setMarkSkippedTestAsBlocked(boolean markSkippedTestAsBlocked) {
		this.markSkippedTestAsBlocked = markSkippedTestAsBlocked;
	}
	
	public boolean isMarkSkippedTestAsBlocked() {
		return markSkippedTestAsBlocked;
	}
	
	protected void handleResult(TestCaseWrapper automatedTestCase, AbstractBuild<?, ?> build, BuildListener listener, TestLinkSite testlink, ExecutionStatus status, final Suite suiteResult) {
		if(automatedTestCase.getExecutionStatus(this.keyCustomField) != ExecutionStatus.NOT_RUN) {
			try {
				final int executionId = testlink.updateTestCase(automatedTestCase);
				
				if(executionId > 0 && this.isAttachTestNGXML()) {
					Attachment attachment = build.getWorkspace().act( new FileCallable<Attachment>() {

						private static final long serialVersionUID = -5411683541842375558L;

						public Attachment invoke(File f,
								VirtualChannel channel)
								throws IOException,
								InterruptedException {
							
							File reportFile = new File(suiteResult.getFile());
							final Attachment attachment = new Attachment();
							attachment.setContent(AbstractTestNGResultSeeker.this.getBase64FileContent(reportFile));
							attachment.setDescription(reportFile.getName());
							attachment.setFileName(reportFile.getName());
							attachment.setFileSize(reportFile.length());
							attachment.setFileType(TEXT_XML_CONTENT_TYPE);
							attachment.setTitle(reportFile.getName());
							
							return attachment;
						}
					});
					testlink.uploadAttachment(executionId, attachment);
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
	
}
