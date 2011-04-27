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

/**
 * Represents the <failure> tag of the JUnit xml report. This tag is child of 
 * the <testcase> tag.
 *
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
public class Failure 
implements Serializable
{

	private static final long serialVersionUID = 583068401113333722L;
	
	/**
	 * Message attribute.
	 */
	private String message;
	
	/**
	 * Type attribute.
	 */
	private String type;
	
	/**
	 * The text of the tag.
	 */
	private String text;
	
	/**
	 * Default constructor.
	 */
	public Failure() 
	{
		super();
	}

	/**
	 * Retrieves the message.
	 * 
	 * @return the message.
	 */
	public String getMessage() 
	{
		return message;
	}

	/**
	 * Sets the message.
	 * 
	 * @param message the message.
	 */
	public void setMessage( String message ) 
	{
		this.message = message;
	}

	/**
	 * Retrieves the type.
	 * 
	 * @return the type.
	 */
	public String getType() 
	{
		return type;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type the type.
	 */
	public void setType( String type ) 
	{
		this.type = type;
	}

	/**
	 * Retrieves the text.
	 * 
	 * @return the text.
	 */
	public String getText() 
	{
		return text;
	}

	/**
	 * Sets the text.
	 * 
	 * @param text the text.
	 */
	public void setText( String text ) 
	{
		this.text = text;
	}
	
}
