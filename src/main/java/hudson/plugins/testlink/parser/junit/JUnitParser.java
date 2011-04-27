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

import hudson.plugins.testlink.parser.Parser;
import hudson.plugins.testlink.util.Messages;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import br.eti.kinoshita.tap4j.parser.ParserException;

/**
 * JUnit Parser. Returns a TestSuite.
 * 
 * @see TestSuite
 *
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.0
 */
public class JUnitParser 
extends Parser<TestSuite>
{

	private static final long serialVersionUID = -7796590517576772260L;
	
	/**
	 * The JUnit XML Handler.
	 */
	private JUnitXmlHandler handler;
	
	/**
	 * Default constructor. Initializes the JUnit XML Handler.
	 */
	public JUnitParser()
	{
		super();
		this.handler = new JUnitXmlHandler();
	}
	
	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.parser.Parser#parse(java.io.InputStream)
	 */
	@Override
	public TestSuite parse( InputStream inputStream ) 
	throws ParserException
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(false);
		try {
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (ParserConfigurationException e) {
        } catch (SAXNotRecognizedException e) {
        } catch (SAXNotSupportedException e) {
        }
        
        SAXParser parser = null; 
        	
        try 
        {
			parser = factory.newSAXParser();
			parser.parse(inputStream, this.handler );
		} 
    	catch (ParserConfigurationException e) 
		{
			throw new ParserException( e );
		}	
    	catch (SAXException e) 
		{
    		throw new ParserException( e );
		} 
		catch (IOException e) 
		{
			throw new ParserException( e );
		}
        
        TestSuite suite = this.handler.getSuite();
        
        this.validateJUnitTestSuite( suite );
        
        return suite;
	}

	/**
	 * Validates a JUnit Test Suite.
	 * 
	 * @param suite JUnit Test Suite.
	 */
	protected void validateJUnitTestSuite( TestSuite suite )
	throws ParserException
	{
		Long errors = suite.getErrors();
		Long failures = suite.getFailures();
		
		long foundFailures = 0;
		long foundErrors = 0;
		
		for( TestCase testCase : suite.getTestCases() )
		{
			foundFailures 	+= 	testCase.getFailures().size();
			foundErrors 	+= 	testCase.getErrors().size();
		}
		
		final Iterator<Error> suiteErrorsIterator = suite.getErrorsList().iterator();
		while ( suiteErrorsIterator.hasNext() )
		{
			suiteErrorsIterator.next();
			foundErrors 	+= 1;
		}
		
		final Iterator<Failure> suiteFailuresIterator = suite.getFailuresList().iterator();
		while ( suiteFailuresIterator.hasNext() )
		{
			suiteFailuresIterator.next();
			foundFailures 	+= 1;
		}
		
		if ( foundFailures != failures )
		{
			throw new ParserException( Messages.TestLinkBuilder_Parser_JUnit_InvalidNumberOfFailures( failures, foundFailures ) );
		}
		
		if ( foundErrors != errors)
		{
			throw new ParserException( Messages.TestLinkBuilder_Parser_JUnit_InvalidNumberOfErrors( errors, foundErrors ) );
		}
		
	}

	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.parser.Parser#getParserName()
	 */
	@Override
	public String getName() 
	{
		return "JUnit";
	}
	
}
