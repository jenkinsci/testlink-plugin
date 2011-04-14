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
import hudson.plugins.testlink.util.Messages;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

/**
 * Seeks for Test Results using a Test Result Seekers.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
public class TestResultsCallable 
implements FileCallable<Set<TestResult>>
{

	private static final long serialVersionUID = 1L;
	
	/**
	 * The ReportFilesPattern object.
	 */
	private ReportFilesPatterns reportFilesPattern;
	
	/**
	 * The Hudson Build listener.
	 */
	private BuildListener listener;
	
	/**
	 * JUnit Test Result Seeker.
	 */
	private JUnitTestResultSeeker junitTestResultSeeker;
	
	/**
	 * TestNG Test Result Seeker.
	 */
	private TestNGTestResultSeeker testNGTestResultSeeker;
	
	/**
	 * TAP Test Result Seeker.
	 */
	private TAPTestResultSeeker tapTestResultSeeker;
	
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
	public TestResultsCallable( 
		TestLinkReport report, 
		String keyCustomFieldName, 
		ReportFilesPatterns reportFilesPatterns, 
		BuildListener listener			
	)
	{
		super();
		
		this.reportFilesPattern = reportFilesPatterns;
		this.listener = listener;
		
		this.junitTestResultSeeker = new JUnitTestResultSeeker(report, keyCustomFieldName, listener);
		this.testNGTestResultSeeker = new TestNGTestResultSeeker(report, keyCustomFieldName, listener);
		this.tapTestResultSeeker = new TAPTestResultSeeker(report, keyCustomFieldName, listener);
	}
	
	/**
	 * Seeks test results in a given directory. It will seek for JUnit, TestNG 
	 * and TAP test results.
	 * 
	 * @param directory directory to seek for test results.
	 * @return list of test results.
	 */
	public Set<TestResult> seekTestResults( File directory ) 
	throws TestResultSeekerException
	{
		final Set<TestResult> testResults = new LinkedHashSet<TestResult>();
		
		Set<TestResult> junitResults = this.junitTestResultSeeker.seek(directory, this.reportFilesPattern.getJunitXmlReportFilesPattern());
		Set<TestResult> testNGResults = this.testNGTestResultSeeker.seek(directory, this.reportFilesPattern.getTestNGXmlReportFilesPattern());
		Set<TestResult> tapResults = this.tapTestResultSeeker.seek(directory, this.reportFilesPattern.getTapStreamReportFilesPattern());
		
		testResults.addAll(junitResults);
		testResults.addAll(testNGResults);
		testResults.addAll(tapResults);
		
		if ( testResults.size() > 0 )
		{
			listener.getLogger().println( Messages.TestLinkBuilder_ShowFoundTestResults(testResults.size()) );
		}
		else
		{
			listener.getLogger().println( Messages.TestLinkBuilder_NoTestResultsFound() );
		}
		listener.getLogger().println();
		
		return testResults;
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
	public Set<TestResult> invoke( File f, VirtualChannel channel )
			throws IOException, InterruptedException
	{
		return this.seekTestResults(f);
	}
	
}
