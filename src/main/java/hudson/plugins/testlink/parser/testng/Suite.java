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
 * Represents the <suite> tag. 
 *
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
public class Suite 
implements Serializable
{

	private static final long serialVersionUID = 4396701906087157712L;
	
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
	 * List of <test> tags.
	 */
	private List<Test> tests;
	
	/**
	 * Default constructor. Initializes the list of <test> tags.
	 */
	public Suite()
	{
		super();
		
		this.tests = new LinkedList<Test>();
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
	 * @return the durationMs
	 */
	public String getDurationMs()
	{
		return durationMs;
	}

	/**
	 * Sets the duration in ms.
	 * 
	 * @param durationMs the durationMs to set
	 */
	public void setDurationMs( String durationMs )
	{
		this.durationMs = durationMs;
	}

	/**
	 * Retrieves the started at.
	 * 
	 * @return the startedAt
	 */
	public String getStartedAt()
	{
		return startedAt;
	}

	/**
	 * Sets the started at.
	 * 
	 * @param startedAt the startedAt to set
	 */
	public void setStartedAt( String startedAt )
	{
		this.startedAt = startedAt;
	}

	/**
	 * Retrieves the finished at.
	 * 
	 * @return the finishedAt
	 */
	public String getFinishedAt()
	{
		return finishedAt;
	}

	/**
	 * Sets the finished at.
	 * 
	 * @param finishedAt the finishedAt to set
	 */
	public void setFinishedAt( String finishedAt )
	{
		this.finishedAt = finishedAt;
	}

	/**
	 * Retrieves the list of <test> tags.
	 * 
	 * @return the tests
	 */
	public List<Test> getTests()
	{
		return tests;
	}
	
	/**
	 * Adds a test into the list of <test> tags.
	 * 
	 * @param test the test.
	 * @return true if added sucessfully, otherwise false.
	 */
	public boolean addTest( Test test )
	{
		return this.tests.add( test );
	}

	/**
	 * Removes a test from the list of <test> tags.
	 * 
	 * @param test the test.
	 * @return true if removed sucessfully, otherwise false.
	 */
	public boolean removeTest( Test test )
	{
		return this.tests.remove ( test );
	}
	
}
