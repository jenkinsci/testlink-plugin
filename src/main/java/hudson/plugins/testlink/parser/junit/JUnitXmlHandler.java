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
package hudson.plugins.testlink.parser.junit;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The JUnit XML Handler.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.0
 */
public class JUnitXmlHandler 
extends DefaultHandler 
implements Serializable
{

	private static final long serialVersionUID = -8190196525823302864L;
	
	/**
	 * The temporary value of the body of a tag. (<tag>$body</tag>)
	 */
	private StringBuilder tempVal;
	
	/**
	 * Test Suite.
	 */
	private TestSuite testSuite;
	
	/**
	 * Test Case.
	 */
	private TestCase testCase;
	
	/**
	 * Failure.
	 */
	private Failure failure;
	
	/**
	 * Error.
	 */
	private Error error;
	
	/**
	 * Default constructor.
	 */
	public JUnitXmlHandler()
	{
		super();
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
		if ( "testsuite".equals(qName) )
		{	
			tempVal = new StringBuilder();
			testSuite = new TestSuite();
			
			Long errors = 0L;
			String errorsValue = attributes.getValue("errors");
			if ( StringUtils.isNotBlank( errorsValue ) ) 
			{
				errors = Long.parseLong ( errorsValue );
			}
			testSuite.setErrors( errors );
			
			Long failures = 0L;
			String failuresValue = attributes.getValue("failures");
			if ( StringUtils.isNotBlank( failuresValue ) )
			{
				failures = Long.parseLong( failuresValue );
			}
			testSuite.setFailures( failures );
			testSuite.setHostname( attributes.getValue("hostname") );
			testSuite.setName( attributes.getValue("name") );
			testSuite.setSystemErr( attributes.getValue("") );
			testSuite.setSystemOut( attributes.getValue("") );
			testSuite.setTests( attributes.getValue("tests") );
			testSuite.setTime( attributes.getValue("time") );
			testSuite.setTimestamp( attributes.getValue("timestamp") );
		}
		else if ( "testcase".equals(qName) )
		{
			tempVal = new StringBuilder();
			testCase = new TestCase();
			
			testCase.setClassName( attributes.getValue("classname") );
			testCase.setName( attributes.getValue("name") );
			testCase.setTime( attributes.getValue("time") );
		}
		else if ( "failure".equals(qName) )
		{
			tempVal = new StringBuilder();
			failure = new Failure();
			failure.setMessage( attributes.getValue("message") );
			failure.setType( attributes.getValue("type") );
		}
		else if ( "error".equals(qName) )
		{
			tempVal = new StringBuilder();
			error = new Error();
			error.setMessage( attributes.getValue("message") );
			error.setType( attributes.getValue("type") );
		}
		else if ( "system-out".equals(qName) )
		{
			tempVal = new StringBuilder();
		}
		else if ( "system-err".equals(qName) )
		{
			tempVal = new StringBuilder();
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
		tempVal.append( ch, start, length );
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
		if ( "testcase".equals(qName) )
		{
			testSuite.addTestCase( testCase );
		}
		else if ( "failure".equals(qName) )
		{
			if ( testCase != null )
			{
				failure.setText( tempVal.toString() );
				testCase.addFailure( failure );
			}
			else
			{
				testSuite.addFailure( failure );
			}
		}
		else if ( "error".equals(qName) )
		{
			if ( testCase != null )
			{
				error.setText( tempVal.toString() );
				testCase.addError( error );
			}
			else
			{
				testSuite.addError( error );
			}
		}
		else if ( "system-out".equals(qName) )
		{
			testSuite.setSystemOut( tempVal.toString() );
		}
		else if ( "system-err".equals(qName) )
		{
			testSuite.setSystemErr( tempVal.toString() );
		}
	}
	
	/**
	 * Retrieves the parsed Test Suite.
	 * 
	 * @return the parsed Test Suite or null if no Test Suite was found.
	 */
	public TestSuite getSuite()
	{
		return this.testSuite;
	}
	
}
