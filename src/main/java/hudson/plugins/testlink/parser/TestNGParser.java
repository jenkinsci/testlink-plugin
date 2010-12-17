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

import hudson.model.BuildListener;
import hudson.plugins.testlink.Messages;
import hudson.plugins.testlink.model.TestLinkReport;
import hudson.plugins.testlink.model.TestResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.kohsuke.stapler.framework.io.IOException2;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import br.eti.kinoshita.testlinkjavaapi.model.Attachment;

/**
 * @author Bruno P. Kinoshita
 * @since 2.0
 */
public class TestNGParser
extends Parser
{

	private TestNGXmlHandler xmlHandler;
	
	public TestNGParser(
			TestLinkReport report, 
			String keyCustomField, 
			BuildListener listener, String 
			includePattern) 
	{
		super(listener, includePattern);
		xmlHandler = new TestNGXmlHandler( report, keyCustomField );
	}

	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.parser.Parser#getName()
	 */
	@Override
	public String getName() {
		return "TestNG";
	}
	
	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.parser.Parser#parseFile(java.io.File)
	 */
	@Override
	protected List<TestResult> parseFile(File file) 
	throws IOException 
	{
		final List<TestResult> testResults = new ArrayList<TestResult>();
		FileInputStream is = null;
		TestResult foundTestResult = null;
		
		try
		{
			is = new FileInputStream( file );
			try
			{
				foundTestResult = this.parse( is );
			}
			catch (SAXException se)
			{
				throw new IOException2( Messages.TestLinkBuilder_Parser_SAX_CouldNotTransform( getName() ) );
			} 
			catch (ParserConfigurationException pe)
			{
				throw new IOException2( Messages.TestLinkBuilder_Parser_SAX_CouldNotIntializeXMLParser() );
			}
			if ( foundTestResult != null )
			{
				testResults.add( foundTestResult );
			}
		}
		finally 
		{
			if ( is != null )
			{
				is.close();
			}
		}
		
		if ( foundTestResult != null )
		{
			Attachment attachment;
			try
			{
				attachment = this.getAttachment( foundTestResult.getTestCase().getVersionId(), file );
			}
			catch (MessagingException e)
			{
				throw new IOException( Messages.TestLinkBuilder_Parser_AttachmentError( getName(), e.getMessage()) ) ;
			}
			foundTestResult.addAttachment( attachment );
		}	
		
		return testResults;
	}
	
	/**
	 * @param is
	 * @param report 
	 * @param testResults
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	protected TestResult parse( InputStream is ) 
	throws SAXException, IOException, ParserConfigurationException
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(false);
		try {
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (ParserConfigurationException e) {
        } catch (SAXNotRecognizedException e) {
        } catch (SAXNotSupportedException e) {
        }
        
        SAXParser parser = factory.newSAXParser();
        
        parser.parse(is, this.xmlHandler );
        
        TestResult foundTestResult = this.xmlHandler.getTestResult();
        
        return foundTestResult;
        
	}
	
}
