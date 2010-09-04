/**
 *	 __                                        
 *	/\ \      __                               
 *	\ \ \/'\ /\_\    ___     ___   __  __  __  
 *	 \ \ , < \/\ \ /' _ `\  / __`\/\ \/\ \/\ \ 
 *	  \ \ \\`\\ \ \/\ \/\ \/\ \L\ \ \ \_/ \_/ \
 *	   \ \_\ \_\ \_\ \_\ \_\ \____/\ \___x___/'
 *	    \/_/\/_/\/_/\/_/\/_/\/___/  \/__//__/  
 *                                          
 * Copyright (c) 1999-present Kinow
 * Casa Verde - São Paulo - SP. Brazil.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Kinow ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Kinow.                                      
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 04/09/2010
 */
package hudson.plugins.testlink.model;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XML Handler used by the {@link TestLinkParser}, a SAX Parser.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 04/09/2010
 */
public class TestLinkXmlHandler 
extends DefaultHandler 
{

	private TestLink testLink;
	private TestLinkTestCase tempTestCase;
	private String tempVal;
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startDocument()
	 */
	@Override
	public void startDocument() 
	throws SAXException 
	{
		this.testLink = new TestLink();
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endDocument()
	 */
	@Override
	public void endDocument() 
	throws SAXException 
	{
		super.endDocument();
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
		if ( "testcase".equals(qName) )
		{
			tempTestCase = new TestLinkTestCase();
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
	public void endElement(String uri, String localName, String qName)
	throws SAXException 
	{
		if ( "id".equals(qName) )
		{
			tempTestCase.setId( Integer.parseInt(tempVal) );
		} else if ("planId".equals(qName))
		{
			tempTestCase.setPlanId( Integer.parseInt(tempVal) );
		} else if ("buildId".equals(qName))
		{
			tempTestCase.setBuildId( Integer.parseInt(tempVal) );
		} else if ("projectId".equals(qName))
		{
			tempTestCase.setProjectId( Integer.parseInt(tempVal) );
		} else if ("category".equals(qName))
		{
			tempTestCase.setCategory( tempVal );
		} else if ("file".equals(qName))
		{
			tempTestCase.setFile( tempVal );
		} else if ("resultStatus".equals(qName))
		{
			tempTestCase.setResultStatus( tempVal );
		} else if ("testcase".equals(qName))
		{
			this.testLink.getTestCases().add(tempTestCase);
		}
	}
	
	public TestLink getTestLink()
	{
		return this.testLink;
	}
	
}
