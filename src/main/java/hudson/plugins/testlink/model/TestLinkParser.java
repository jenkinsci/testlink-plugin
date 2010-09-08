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

import hudson.FilePath;
import hudson.plugins.testlink.TestLinkReport;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.kohsuke.stapler.framework.io.IOException2;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/** 
 * <p>Parser of the output of TestLinkBuilder. Uses a SAX Parser. Sweet as!</p> 
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 04/09/2010
 */
public class TestLinkParser 
implements FilePath.FileCallable<TestLinkReport>
{

	public static final String RESULT_FILE_NAME = "testlink.xml";

	private boolean LOG_ENABLED = false;
	
	private transient PrintStream logger;

	public TestLinkParser(PrintStream logger) {
		super();
		this.logger = logger;
	}

	/* (non-Javadoc)
	 * @see hudson.FilePath.FileCallable#invoke(java.io.File, hudson.remoting.VirtualChannel)
	 */
	public TestLinkReport invoke(File workspace, VirtualChannel channel) throws IOException,
			InterruptedException {
		TestLinkReport report = new TestLinkReport();
		
		this.parse( workspace, RESULT_FILE_NAME, report);

		return report;
	}

	/**
	 * @param workspace
	 * @param channel
	 * @param report
	 */
	private void parse(File workspace, String fileName, TestLinkReport report)
	throws IOException{
		java.io.File file = new java.io.File(workspace, fileName);
        InputStream in = new FileInputStream(file);
        this.parse(in, report);
        in.close();
	}

	/**
	 * Parses testlink.xml file.
	 * 
	 * @param in
	 * @param report
	 */
	private void parse(InputStream in, TestLinkReport report) 
	throws IOException 
	{
	
		
		if ( LOG_ENABLED && logger != null )
		{
			logger.println("Parsing testlink.xml file...");
		}
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(false);
		try {
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (ParserConfigurationException e) {
        } catch (SAXNotRecognizedException e) {
        } catch (SAXNotSupportedException e) {
        }
        
        try {
            SAXParser parser = factory.newSAXParser();
            TestLinkXmlHandler handler = new TestLinkXmlHandler();
            
            parser.parse(in, handler);
            TestLink testLink  = handler.getTestLink();
            
            report.setBuildId( testLink.getBuildId() );
            report.setBuildName( testLink.getBuildName() );
            
            List<TestLinkTestCase> testCases = testLink.getTestCases();    		
    		
            for ( TestLinkTestCase tc : testCases )
            {
            	report.addTestCase(tc);
            }
            
        } catch (ParserConfigurationException e) {
            throw new IOException2("Cannot parse testlink results", e);
        } catch (SAXException e) {
            throw new IOException2("Cannot parse testlink results", e);
        }	

	}
	
}
