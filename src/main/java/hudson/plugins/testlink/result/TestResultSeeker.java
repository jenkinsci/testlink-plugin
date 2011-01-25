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
import hudson.plugins.testlink.result.parser.Parser;
import hudson.plugins.testlink.result.parser.junit.JUnitParser;
import hudson.plugins.testlink.result.parser.junit.TestSuite;
import hudson.plugins.testlink.result.parser.tap.TAPParser;
import hudson.plugins.testlink.result.parser.testng.Suite;
import hudson.plugins.testlink.result.parser.testng.TestNGParser;
import hudson.plugins.testlink.result.scanner.Scanner;
import hudson.plugins.testlink.util.Messages;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.eti.kinoshita.tap4j.model.TestSet;

/**
 * Seeks for Test Results using a Scanner and Parsers.
 * 
 * @see {@link Scanner}
 * @see {@link Parser}
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
public class TestResultSeeker
{

	/**
	 * The ReportFilesPattern object.
	 * 
	 * @see {@link ReportFilesPatterns}
	 */
	private ReportFilesPatterns reportFilesPattern;
	
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
	 * Default constructor. Initializes the ReportFilesPattern object, the 
	 * scanner and the parsers.
	 * 
	 * @param reportFilesPatterns The report files patterns
	 */
	public TestResultSeeker( ReportFilesPatterns reportFilesPatterns )
	{
		super();
		
		this.reportFilesPattern = reportFilesPatterns;
		this.scanner = new Scanner();
		
		this.junitParser = new JUnitParser();
		this.testNGParser = new TestNGParser();
		this.tapParser = new TAPParser();
	}
	
	public List<TestResult> seekTestResults( File directory, BuildListener listener ) 
	{
		final List<TestResult> testResults = new LinkedList<TestResult>();
		
		try
		{
			this.seekJUnitTestResults( directory, listener );
			
			this.seekTestNGTestResults( directory, listener );
			
			this.seekTapTestResults( directory, listener );
		} 
		catch ( IOException ioe )
		{
			listener.getLogger().println( Messages.TestLinkBuilder_FailedToOpenReportFile() );
			ioe.printStackTrace( listener.getLogger() );
		}
		
		return testResults;
	}

	/**
	 * Seeks for JUnit test results in a given directory.
	 * 
	 * @param directory the directory to look at.
	 * @param listener the Hudson Build listener.
	 * @throws IOException 
	 */
	private void seekJUnitTestResults( File directory, BuildListener listener ) 
	throws IOException
	{
		String junitIncludes = this.reportFilesPattern.getJunitXmlReportFilesPattern();
		
		if ( StringUtils.isNotEmpty ( junitIncludes ) )
		{
			String[] junitReports = this.scanner.scan( directory, junitIncludes, listener );
			
			for( String junitReport : junitReports )
			{
				File junitReportFile = new File ( junitReport );
				TestSuite testSuite = this.junitParser.parse( junitReportFile );
				
				//listener.getLogger().println( Messages.TestLinkBuilder_ShowFoundTestResults(foundResults.length) );
				//listener.getLogger().println( Messages.TestLinkBuilder_NoTestResultsFound() );
			}
		}
	}
	
	/**
	 * Seeks for TestNG test results in a given directory.
	 * 
	 * @param directory the directory to look at.
	 * @param listener the Hudson Build listener.
	 * @throws IOException 
	 */
	private void seekTestNGTestResults( File directory, BuildListener listener ) 
	throws IOException
	{
		String testNgIncludes = this.reportFilesPattern.getTestNGXmlReportFilesPattern();
		
		if ( StringUtils.isNotEmpty( testNgIncludes ) )
		{
			String[] testNgReports = this.scanner.scan( directory, testNgIncludes, listener );
			for ( String testNGReport : testNgReports )
			{
				File testNgReportFile = new File( testNGReport );
				Suite suite = this.testNGParser.parse( testNgReportFile );
				
			}
		}
	}
	
	/**
	 * Seeks for TAP test results in a given directory.
	 * 
	 * @param directory the directory to look at.
	 * @param listener the Hudson Build listener.
	 * @throws IOException 
	 */
	private void seekTapTestResults( File directory, BuildListener listener )
	throws IOException
	{
		String tapIncludes = this.reportFilesPattern.getTapStreamReportFilesPattern();
		
		if ( StringUtils.isNotEmpty( tapIncludes ) )
		{
			String[] tapReports = this.scanner.scan( directory, tapIncludes, listener );
			for ( String tapReport : tapReports )
			{
				File tapReportFile = new File ( tapReport );
				TestSet testSet = this.tapParser.parse( tapReportFile );
				
			}
		}
	}
	
}
