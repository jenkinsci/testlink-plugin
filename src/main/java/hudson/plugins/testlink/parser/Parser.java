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


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * An abstract parser.
 *
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.0
 */
public abstract class Parser<T> 
implements Serializable
{

	private static final long serialVersionUID = -5589493877839837838L;

	/**
	 * Retrives the name of the parser.
	 * 
	 * @return the name of the parser.
	 */
	public abstract String getName();
	
	/**
	 * Parses the content of an input stream and returns a different object 
	 * depending on the type of the parser.
	 *   
	 * @param inputStream the input stream.
	 * @return Resulting object.
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public abstract T parse( InputStream inputStream ) 
	throws ParserException;
	
	/**
	 * Parses the content of the file. This method only creates an input stream 
	 * and then calls the {@link Parser#parse(InputStream)} method. 
	 *  
	 * @param file the file.
	 * @return Resulting object.
	 * @throws Exception
	 */
	public T parse( File file )
	throws ParserException
	{
		FileInputStream fileInputStream = null;
		T 				resultingObject = null;
		
		try
		{
			fileInputStream = new FileInputStream( file );
			resultingObject = this.parse( fileInputStream );
			if ( resultingObject == null )
			{
				throw new ParserException("Invalid " + getName() + " file: " + file.getAbsolutePath());
			}
		} 
		catch (FileNotFoundException e) 
		{
			throw new ParserException("File not found: '" + file + "'.", e);
		}
		catch ( Throwable t )
		{
			throw new ParserException("Unkown internal error: " + t.getMessage(), t);
		}
		finally
		{
			if ( fileInputStream != null )
			{
				try 
				{
					fileInputStream.close();
				} 
				catch (IOException e) 
				{
					throw new ParserException("Failed to close input stream.", e);
				}
			}
		}
		
		return resultingObject;
	}
	
}
