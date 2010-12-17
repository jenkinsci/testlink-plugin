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
package hudson.plugins.testlink.parser;

import hudson.plugins.testlink.model.TestLinkReport;
import hudson.plugins.testlink.model.TestResult;

import java.io.Serializable;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.0
 */
public class JUnitXmlHandler 
extends DefaultHandler 
implements Serializable
{

	private String tempVal;
	private TestResult tempTestResult;
	private TestLinkReport report;
	private String keyCustomFieldName;
	private StringBuffer tempNotes;
	private Boolean hasFailure = Boolean.FALSE;
	private String testCaseName;
	private String testCaseClass;
	private String testCaseTime;
	
	public static final String TESTSUITE = "testsuite";
	private static final String CLASS_NAME_ATTRIBUTE = "name";
	private static final String FAILURES_ATTRIBUTE = "failures";
	private static final String HOSTNAME_ATTRIBUTE = "hostname";
	private static final String NUMBER_OF_TESTS_ATTRIBUTE = "tests";
	private static final String TIME_ATTRIBUTE = "time";
	private static final String TIMESTAMP_ATTRIBUTE = "timestamp";
	private static final Object SYSTEMERR = "system-err";
	private static final Object SYSTEMOUT = "system-out";
	private static final Object TESTCASE = "testcase";
	private static final Object FAILURE = "failure";
	
	public JUnitXmlHandler(TestLinkReport report, String keyCustomField )
	{
		this.report = report;
		this.keyCustomFieldName = keyCustomField;
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(
			String uri, 
			String localName, 
			String qName,
			Attributes attributes) 
	throws SAXException 
	{
		if ( TESTSUITE.equals(qName) )
		{			
			String className = attributes.getValue(CLASS_NAME_ATTRIBUTE);
			String failuresText = attributes.getValue(FAILURES_ATTRIBUTE);
			
			String hostname = attributes.getValue(HOSTNAME_ATTRIBUTE);
			String numberOfTests = attributes.getValue(NUMBER_OF_TESTS_ATTRIBUTE);
			String time = attributes.getValue( TIME_ATTRIBUTE );
			String timestamp = attributes.getValue( TIMESTAMP_ATTRIBUTE );
			
			TestCase found = null;
			for( TestCase testCase : this.report.getTestCases() )
			{
				for( CustomField customField : testCase.getCustomFields() )
				{
					if ( customField.getName().equalsIgnoreCase(this.keyCustomFieldName))
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
				Integer failures = Integer.parseInt(failuresText);
				
				if ( failures > 0 )
				{
					found.setExecutionStatus(ExecutionStatus.FAILED);
				}
				else 
				{
					found.setExecutionStatus( ExecutionStatus.PASSED );
				}
				
				tempNotes = new StringBuffer();
				tempNotes.append( "Hostname: " + hostname + "\n");
				tempNotes.append( "Number of methods: " + numberOfTests + "\n");
				tempNotes.append( "Failures: " + failures + "\n");
				tempNotes.append( "Time: " + time + "\n");
				tempNotes.append( "Timestamp: " + timestamp + "\n");
				
				//this.report.getTestCases().remove( found );
				this.tempTestResult = new TestResult(found, report.getBuild(), report.getTestPlan());
				
			}
		}
		else if ( TESTCASE.equals(qName) )
		{
			hasFailure = Boolean.FALSE;
			
			testCaseName = attributes.getValue("name");
			testCaseClass = attributes.getValue("classname");
			testCaseTime = attributes.getValue("time");
		}
		else if ( FAILURE.equals(qName) )
		{
			hasFailure = Boolean.TRUE;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(
			char[] ch, 
			int start, 
			int length)
	throws SAXException 
	{
		tempVal = new String(ch, start, length);
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(
			String uri, 
			String localName, 
			String qName )
	throws SAXException
	{
		if ( TESTSUITE.equals(qName) )
		{
			this.tempTestResult.setNotes( tempNotes.toString() );
		}
		else if ( TESTCASE.equals(qName) )
		{
			tempNotes.append("Test case [" + testCaseName + "] ");
			tempNotes.append("classname [" + testCaseClass + "] ");
			tempNotes.append("time [" + testCaseTime + "]");
			if ( hasFailure )
			{
				tempNotes.append(" FAILED");
			}
			else 
			{
				tempNotes.append(" PASSED");
			}
			tempNotes.append("\n");
		}
		else if ( SYSTEMOUT.equals(qName) )
		{
			tempNotes.append( "System out: " + tempVal + "\n" );
		}
		else if ( SYSTEMERR.equals(qName) )
		{
			tempNotes.append( "System err: " + tempVal + "\n" );
		}
	}
	
	public TestResult getTestResult()
	{
		return this.tempTestResult;
	}
	
}
