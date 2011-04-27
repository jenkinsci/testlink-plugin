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
import java.util.LinkedList;
import java.util.List;

/**
 * Represents the <testcase> tag. This tag is child of the <testsuite> tag.
 *
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
public class TestCase 
implements Serializable
{

	private static final long serialVersionUID = 1L;

	/**
	 * The classname attribute.
	 */
	private String className;
	
	/**
	 * The name attribute.
	 */
	private String name;
	
	/**
	 * The time attribute.
	 */
	private String time;
	
	/**
	 * List of <failure> tags.
	 */
	private List<Failure> failures;
	
	/**
	 * List of <error> tags.
	 */
	private List<Error> errors;
	
	/**
	 * Default constructor. Initializes the <failure> and the 
	 * <error> tag lists.
	 */
	public TestCase()
	{
		super();
		
		this.failures = new LinkedList<Failure>();
		this.errors = new LinkedList<Error>();
	}

	/**
	 * Retrieves the classname.
	 * 
	 * @return the classname.
	 */
	public String getClassName() 
	{
		return className;
	}

	/**
	 * Sets the classname.
	 * 
	 * @param className the classname.
	 */
	public void setClassName( String className ) 
	{
		this.className = className;
	}

	/**
	 * Retrieves the name.
	 * 
	 * @return the name.
	 */
	public String getName() 
	{
		return name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name the name.
	 */
	public void setName( String name ) 
	{
		this.name = name;
	}

	/**
	 * Retrieves the time.
	 * 
	 * @return the time.
	 */
	public String getTime() 
	{
		return time;
	}

	/**
	 * Sets the time.
	 * 
	 * @param time the time.
	 */
	public void setTime( String time ) 
	{
		this.time = time;
	}

	/**
	 * Retrieves the list of <failure> tags.
	 * 
	 * @return the list of <failure> tags.
	 */
	public List<Failure> getFailures() 
	{
		return failures;
	}

	/**
	 * Adds a failure into the list of <failure> tags.
	 * 
	 * @param failure the failure.
	 * @return true if added successfully, otherwise false.
	 */
	public boolean addFailure( Failure failure )
	{
		return this.failures.add( failure );
	}
	
	/**
	 * Retrives the list of <error> tags.
	 * 
	 * @return the list of <error> tags.
	 */
	public List<Error> getErrors() 
	{
		return errors;
	}
	
	/**
	 * Adds an error into the list of <error> tags.
	 * 
	 * @param error the error.
	 * @return true if added successfully, otherwise false.
	 */
	public boolean addError( Error error )
	{
		return this.errors.add( error );
	}
	
	/**
	 * Removes a failure from the list of <failure> tags.
	 * 
	 * @param failure the failure.
	 * @return true if removed successfully, otherwise false.
	 */
	public boolean removeFailure( Failure failure )
	{
		return this.failures.remove( failure );
	}
	
	/**
	 * Removes an error from the list of <error> tags.
	 * 
	 * @param error the error.
	 * @return true if removed sucessfully, otherwise false.
	 */
	public boolean removeError( Error error )
	{
		return this.errors.remove( error );
	}
	
}
