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

import hudson.model.BuildListener;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

/**
 * Seeks for Test Results.
 *
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.2
 */
public abstract class TestResultSeeker 
implements Serializable
{

	private static final long serialVersionUID = 6476036912489515690L;
	
	protected final TestLinkReport report;
	protected final String keyCustomFieldName;
	protected final BuildListener listener;
	
	/**
	 * Default constructor.
	 * 
	 * @param report TestLink report.
	 * @param keyCustomFieldName Name of the Key Custom Field.
	 * @param listener Hudson Build listener.
	 */
	public TestResultSeeker( 
		TestLinkReport report, 
		String keyCustomFieldName, 
		BuildListener listener)
	{
		super();
		
		this.report = report;
		this.keyCustomFieldName = keyCustomFieldName;
		this.listener = listener;
	}
	
	/**
	 * Seeks for Test Results in a directory. It tries to match the 
	 * includePattern with files in this directory.
	 * 
	 * @param directory Directory to look for test results
	 * @param includePattern Include pattern
	 * @throws TestResultSeekerException
	 */
	public abstract Set<TestResult> seek( 
			File directory, 
			String includePattern )
	throws TestResultSeekerException;
	
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
	
}
