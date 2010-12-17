/**
 * 
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
 *
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
