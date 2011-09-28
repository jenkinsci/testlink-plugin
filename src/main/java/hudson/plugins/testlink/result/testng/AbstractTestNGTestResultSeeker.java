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
import hudson.plugins.testlink.result.TestResultSeeker;
import hudson.plugins.testlink.util.Messages;

import java.io.File;
import java.io.IOException;

import br.eti.kinoshita.testlinkjavaapi.model.Attachment;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.5
 */
public abstract class AbstractTestNGTestResultSeeker<T>
extends TestResultSeeker<T>
{

	private static final long serialVersionUID = 8343628541429393341L;

	public AbstractTestNGTestResultSeeker(String includePattern,
			TestCase[] automatedTestCases, String keyCustomFieldName,
			BuildListener listener)
	{
		super(includePattern, automatedTestCases, keyCustomFieldName, listener);
	}
	
	/**
	 * Retrieves attachments for TestNG test cases.
	 * 
	 * @param testNGReportFile TestNG file.
	 * @return attachments
	 */
	protected Attachment getTestNGAttachment( 
		File testNGReportFile 
	)
	throws IOException
	{
		Attachment attachment = new Attachment();
		
		String fileContent = this.getBase64FileContent(testNGReportFile );
		attachment.setContent( fileContent );
		attachment.setDescription( Messages.Results_TestNG_AttachmentDescription( testNGReportFile.getName() ) );
		attachment.setFileName( testNGReportFile.getName() );
		attachment.setFileSize( testNGReportFile.length() );
		attachment.setTitle( testNGReportFile.getName() );
		attachment.setFileType("text/xml");
		
		return attachment;
	}
	
}
