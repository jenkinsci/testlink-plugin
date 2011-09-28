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
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

/**
 * Uses test results seekers to find results.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
@SuppressWarnings("rawtypes")
public class TestResultsCallable 
implements FileCallable<Map<Integer, TestCaseWrapper>>
{

	private static final long serialVersionUID = -7027180358578453354L;

	/**
	 * List of test results seekers.
	 */
	private List<TestResultSeeker<?>> testResultsSeekers;
	
	/**
	 * Adds a test result seeker.
	 */
	public void addTestResultSeeker( TestResultSeeker<?> testResultSeeker )
	{
		this.testResultsSeekers.add ( testResultSeeker );
	}
	
	/**
	 * Gets test result seekers. 
	 */
	public List<TestResultSeeker<?>> getTestResultSeekers()
	{
		return this.testResultsSeekers;
	}
	
	/**
	 * Default constructor. 
	 */
	public TestResultsCallable()
	{
		super();
		
		this.testResultsSeekers = new LinkedList<TestResultSeeker<?>>();
	}
	
	/**
	 * Seeks test results in a given directory. It will seek for JUnit, TestNG 
	 * and TAP test results.
	 * 
	 * @param directory directory to seek for test results.
	 * @return list of test results.
	 */
	@SuppressWarnings({ "unchecked" })
	public Map<Integer, TestCaseWrapper> seekTestResults( File directory ) 
	throws TestResultSeekerException
	{
		final Map<Integer, TestCaseWrapper> testResults = new LinkedHashMap<Integer, TestCaseWrapper>();
		
		for( TestResultSeeker testResultSeeker : this.testResultsSeekers )
		{
			final Map<Integer, TestCaseWrapper> results = testResultSeeker.seek( directory );
			
			testResults.putAll( results );
		}
		
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
	public Map<Integer, TestCaseWrapper> invoke( File f, VirtualChannel channel )
			throws IOException, InterruptedException
	{
		return this.seekTestResults(f);
	}
	
}
