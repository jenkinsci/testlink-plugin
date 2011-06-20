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
package hudson.plugins.testlink.result;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import br.eti.kinoshita.testlinkjavaapi.model.Attachment;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.0
 */
public class TestCaseWrapper<T> implements Serializable
{

	private final T origin;
	
	private static final long serialVersionUID = -3580223939886620157L;

	private TestCase testCase;
	private List<Attachment> attachments;
	private StringBuilder notes;
	private String platform = null;

	private final Map<String, ExecutionStatus> customFieldAndStatus;
	private final String[] customFields;
	
	public TestCaseWrapper( TestCase testCase, String[] customFields, T origin )
	{
		this.testCase = testCase;
		this.notes = new StringBuilder();
		this.attachments = new LinkedList<Attachment>();
		this.customFieldAndStatus = new LinkedHashMap<String, ExecutionStatus>();
		if( customFields == null )
		{
			this.customFields = new String[0];
		}
		else
		{
			this.customFields = customFields;
		}
		this.origin = origin;
	}
	
	public T getOrigin()
	{
		return this.origin;
	}
	
	public String[] getCustomFields()
	{
		return this.customFields;
	}

	public void addCustomFieldAndStatus(String customField, ExecutionStatus executionStatus)
	{
		this.customFieldAndStatus.put(customField, executionStatus);
	}

	public Map<String, ExecutionStatus> getCustomFieldAndStatus()
	{
		return customFieldAndStatus;
	}
	
	public void addAttachment( Attachment attachment )
	{
		this.attachments.add(attachment);
	}

	public TestCase getTestCase()
	{
		testCase.setExecutionStatus( ExecutionStatus.NOT_RUN );
		
		if ( customFieldAndStatus.size() > 0 && customFieldAndStatus.size() == customFields.length )
		{
			ExecutionStatus status = ExecutionStatus.PASSED;
			for( ExecutionStatus reportedStatus : customFieldAndStatus.values() )
			{
				if ( reportedStatus == ExecutionStatus.FAILED )
				{
					status = ExecutionStatus.FAILED;
					break;
				}
			}
			testCase.setExecutionStatus( status );
		}
		return testCase;
	}

	public void setTestCase( TestCase testCase )
	{
		this.testCase = testCase;
	}

	public List<Attachment> getAttachments()
	{
		return attachments;
	}

	public String getNotes()
	{
		return notes.toString();
	}

	public void appendNotes( String notes )
	{
		this.notes.append( notes );
	}

	public String getPlatform()
	{
		return this.platform;
	}

	public void setPlatform( String platform )
	{
		this.platform = platform;
	}

	public Integer getId()
	{
		return this.testCase.getId();
	}
	
	public String getName()
	{
		return this.testCase.getName();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals( Object obj )
	{
		boolean equals = false;
		if (obj != null && obj instanceof TestCaseWrapper<?>)
		{
			TestCaseWrapper<?> testResult = ((TestCaseWrapper<?>) obj);
			TestCase tc = testResult.getTestCase();
			TestCase thisTc = this.getTestCase();
			if ( testResult.getOrigin().equals(this.getOrigin()) && tc != null && thisTc != null)
			{
				equals = tc.getId() == thisTc.getId();
			}
		}
		return equals;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int id = 0;
		TestCase thisTc = this.getTestCase();
		if (thisTc != null)
		{
			id = thisTc.getId();
		}
		return id;
	}

	@Override
	public String toString()
	{
		return "TestCaseWrapper [testCase=" + testCase + ", attachments="
				+ attachments + ", notes=" + notes + ", platform=" + platform
				+ ", customFieldAndStatus=" + customFieldAndStatus + "]";
	}

}
