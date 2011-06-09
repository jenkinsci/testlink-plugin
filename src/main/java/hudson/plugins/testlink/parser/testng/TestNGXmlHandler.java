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
package hudson.plugins.testlink.parser.testng;

import java.io.Serializable;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The TestNG XML Handler.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.0
 */
public class TestNGXmlHandler 
extends DefaultHandler 
implements Serializable
{

	private static final String SUITE = "suite";
	private static final String TEST_METHOD = "test-method";
	private static final String CLAZZ = "class";
	private static final String TEST = "test";
	private static final String STATUS = "status";
	private static final String SIGNATURE = "signature";
	private static final String IS_CONFIG = "is-config";
	private static final String NAME = "name";
	private static final String STARTED_AT = "started-at";
	private static final String FINISHED_AT = "finished-at";
	private static final String DURATION_MS = "duration-ms";

	private static final long serialVersionUID = -7393574325643071292L;

	private Suite suite;
	private Test test;
	private Class clazz;
	private TestMethod testMethod;
	
	/**
	 * Default constructor.
	 */
	public TestNGXmlHandler()
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
		if ( SUITE.equals(qName) )
		{
			suite = new Suite();
			
			suite.setDurationMs( attributes.getValue( DURATION_MS ) );
			suite.setFinishedAt( attributes.getValue( FINISHED_AT ) );
			suite.setStartedAt( attributes.getValue(STARTED_AT) );
			suite.setName( attributes.getValue( NAME ) );
		} 
		else if ( TEST.equals( qName ) )
		{
			test = new Test();
			
			test.setDurationMs( attributes.getValue( DURATION_MS ) );
			test.setFinishedAt( attributes.getValue( FINISHED_AT ) );
			test.setStartedAt( attributes.getValue( STARTED_AT ) );
			test.setName( attributes.getValue( NAME ) );
		}
		else if ( CLAZZ.equals( qName ) ) 
		{
			clazz = new Class();
			
			clazz.setName( attributes.getValue( NAME ) );
		}
		else if ( TEST_METHOD.equals( qName ) )
		{
			testMethod = new TestMethod();
			
			testMethod.setDurationMs( attributes.getValue( DURATION_MS ) );
			testMethod.setFinishedAt( attributes.getValue( FINISHED_AT ) );
			testMethod.setStartedAt( attributes.getValue( STARTED_AT ) );
			testMethod.setName( attributes.getValue( NAME ) );
			testMethod.setIsConfig( attributes.getValue( IS_CONFIG ) );
			testMethod.setSignature( attributes.getValue( SIGNATURE ) );
			testMethod.setStatus( attributes.getValue( STATUS ) );
		}
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement( String uri, 
			String localName, 
			String qName )
			throws SAXException
	{
		if ( TEST.equals( qName ) )
		{
			suite.addTest( test );
		}
		else if ( CLAZZ.equals( qName ) ) 
		{
			test.addClass( clazz );
		}
		else if ( TEST_METHOD.equals( qName ) )
		{
			clazz.addTestMethod( testMethod );
		}
	}
	
	/**
	 * Retrieves the parsed Suite.
	 * 
	 * @return the parsed Suite.
	 */
	public Suite getSuite()
	{
		return this.suite;
	}
	
}
