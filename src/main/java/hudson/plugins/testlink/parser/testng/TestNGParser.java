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

import hudson.plugins.testlink.parser.Parser;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import br.eti.kinoshita.tap4j.parser.ParserException;

/**
 *
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 */
public class TestNGParser 
extends Parser<Suite>
{

	private static final long serialVersionUID = -7538241225523763422L;
	
	/**
	 * The TestNG XML Handler.
	 */
	private TestNGXmlHandler handler;
	
	/**
	 * Default constructor. Initializes the TestNG XML Handler.
	 */
	public TestNGParser()
	{
		super();
		this.handler = new TestNGXmlHandler();
	}
	
	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.parser.Parser#parse(java.io.InputStream)
	 */
	@Override
	public Suite parse( InputStream inputStream ) 
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
        
        Suite suite = this.handler.getSuite();
        
        return suite;
	}
	
	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.parser.Parser#getName()
	 */
	@Override
	public String getName() 
	{
		return "TestNG";
	}

}
