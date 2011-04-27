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
import java.util.LinkedList;
import java.util.List;

/**
 * Represents the <test> tag. This tag is child of the <suite> tag.
 *
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
public class Test
implements Serializable
{

	private static final long serialVersionUID = 7310326353515207431L;
	
	/**
	 * The name attribute.
	 */
	private String name;
	
	/**
	 * The duration-ms attribute.
	 */
	private String durationMs;
	
	/**
	 * The started-at attribute.
	 */
	private String startedAt;
	
	/**
	 * The finished-at attribute.
	 */
	private String finishedAt;
	
	/**
	 * The list of <class> tags.
	 */
	private List<Class> classes;
	
	/**
	 * Default constructor. Initializes the list of <class> tags.
	 */
	public Test()
	{
		super();
		
		this.classes = new LinkedList<Class>();
	}

	/**
	 * Retrieves the name.
	 * 
	 * @return the name
	 */
	public String getName() 
	{
		return name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name the name to set
	 */
	public void setName( String name ) 
	{
		this.name = name;
	}
	
	/**
	 * Retrieves the duration in ms.
	 * 
	 * @return the duration in ms
	 */
	public String getDurationMs() 
	{
		return durationMs;
	}

	/**
	 * Sets the duration in ms.
	 * 
	 * @param durationMs the duration to set
	 */
	public void setDurationMs( String durationMs ) 
	{
		this.durationMs = durationMs;
	}
	
	/**
	 * Retrieves the startedAt.
	 * 
	 * @return the startedAt
	 */
	public String getStartedAt() 
	{
		return startedAt;
	}

	/**
	 * Sets the startedAt.
	 * 
	 * @param startedAt the startedAt to set
	 */
	public void setStartedAt( String startedAt ) 
	{
		this.startedAt = startedAt;
	}

	/**
	 * Retrieves the finishedAt.
	 * 
	 * @return the finishedAt
	 */
	public String getFinishedAt() 
	{
		return finishedAt;
	}

	/**
	 * Sets the finishedAt.
	 * 
	 * @param finishedAt the finishedAt to set
	 */
	public void setFinishedAt( String finishedAt ) 
	{
		this.finishedAt = finishedAt;
	}

	/**
	 * Retrieves the list of <class> tags.
	 * 
	 * @return the classes
	 */
	public List<Class> getClasses() 
	{
		return classes;
	}
	
	/**
	 * Adds a class into the list of <class> tags.
	 * 
	 * @param clazz the class
	 * @return true if added sucessfully, otherwise false.
	 */
	public boolean addClass( Class clazz )
	{
		return this.classes.add ( clazz );
	}
	
	/**
	 * Removes a class from the list of <class> tags.
	 * 
	 * @param clazz the class
	 * @return true if removed sucessfully, otherwise false.
	 */
	public boolean removeClass( Class clazz )
	{
		return this.classes.remove ( clazz );
	}
	
}
