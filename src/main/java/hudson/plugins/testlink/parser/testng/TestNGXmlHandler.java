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

	private static final long serialVersionUID = -7393574325643071292L;
	
	/**
	 * Suite.
	 */
	private Suite suite;
	
	/**
	 * Test.
	 */
	private Test test;
	
	/**
	 * Class.
	 */
	private Class clazz;
	
	/**
	 * Test method.
	 */
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
		if ( "suite".equals(qName) )
		{
			suite = new Suite();
			
			suite.setDurationMs( attributes.getValue("duration-ms") );
			suite.setFinishedAt( attributes.getValue("finished-at") );
			suite.setStartedAt( attributes.getValue("started-at") );
			suite.setName( attributes.getValue("name") );
		} 
		else if ( "test".equals( qName ) )
		{
			test = new Test();
			
			test.setDurationMs( attributes.getValue("duration-ms") );
			test.setFinishedAt( attributes.getValue("finished-at") );
			test.setStartedAt( attributes.getValue("started-at") );
			test.setName( attributes.getValue("name") );
		}
		else if ( "class".equals( qName ) ) 
		{
			clazz = new Class();
			
			clazz.setName( attributes.getValue("name") );
		}
		else if ( "test-method".equals( qName ) )
		{
			testMethod = new TestMethod();
			
			testMethod.setDurationMs( attributes.getValue("duration-ms") );
			testMethod.setFinishedAt( attributes.getValue("finished-at") );
			testMethod.setStartedAt( attributes.getValue("started-at") );
			testMethod.setName( attributes.getValue("name") );
			testMethod.setIsConfig( attributes.getValue("is-config") );
			testMethod.setSignature( attributes.getValue("signature") );
			testMethod.setStatus( attributes.getValue("status") );
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
		if ( "test".equals( qName ) )
		{
			suite.addTest( test );
		}
		else if ( "class".equals( qName ) ) 
		{
			test.addClass( clazz );
		}
		else if ( "test-method".equals( qName ) )
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
