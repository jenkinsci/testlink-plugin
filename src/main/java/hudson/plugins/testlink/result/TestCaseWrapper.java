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
import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionType;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestCaseStep;
import br.eti.kinoshita.testlinkjavaapi.model.TestImportance;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.0
 */
public class TestCaseWrapper<T> extends TestCase implements Serializable
{

	private final T origin;

	private static final long serialVersionUID = -3580223939886620157L;

	private List<Attachment> attachments;
	private StringBuilder notes;
	private String platform = null;

	private final Map<String, ExecutionStatus> customFieldAndStatus;
	private final String[] customFieldsNames;

	private TestCase testCase;

	public TestCaseWrapper(TestCase testCase, String[] customFieldsNames,
			T origin)
	{
		this.testCase = testCase;
		this.notes = new StringBuilder();
		this.attachments = new LinkedList<Attachment>();
		this.customFieldAndStatus = new LinkedHashMap<String, ExecutionStatus>();
		if (customFieldsNames == null)
		{
			this.customFieldsNames = new String[0];
		} else
		{
			this.customFieldsNames = customFieldsNames;
		}
		this.origin = origin;
	}

	public T getOrigin()
	{
		return this.origin;
	}

	public String[] getCustomFieldsNames()
	{
		return this.customFieldsNames;
	}

	public void addCustomFieldAndStatus( String customField,
			ExecutionStatus executionStatus )
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.eti.kinoshita.testlinkjavaapi.model.TestCase#getExecutionStatus()
	 */
	@Override
	public ExecutionStatus getExecutionStatus()
	{
		ExecutionStatus status = ExecutionStatus.NOT_RUN;
		if (customFieldAndStatus.size() > 0
				&& customFieldAndStatus.size() == customFieldsNames.length)
		{
			status = ExecutionStatus.PASSED;
			for (ExecutionStatus reportedStatus : customFieldAndStatus.values())
			{
				if (reportedStatus == ExecutionStatus.FAILED
						|| reportedStatus == ExecutionStatus.BLOCKED)
				{
					status = reportedStatus;
					break;
				}
			}
		}
		return status;
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
		this.notes.append(notes);
	}

	public String getPlatform()
	{
		return this.platform;
	}

	public void setPlatform( String platform )
	{
		this.platform = platform;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.eti.kinoshita.testlinkjavaapi.model.TestCase#getActionOnDuplicatedName
	 * ()
	 */
	@Override
	public String getActionOnDuplicatedName()
	{

		return this.testCase.getActionOnDuplicatedName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.eti.kinoshita.testlinkjavaapi.model.TestCase#getAuthorLogin()
	 */
	@Override
	public String getAuthorLogin()
	{

		return this.testCase.getAuthorLogin();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.eti.kinoshita.testlinkjavaapi.model.TestCase#getCheckDuplicatedName()
	 */
	@Override
	public Boolean getCheckDuplicatedName()
	{

		return this.testCase.getCheckDuplicatedName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.eti.kinoshita.testlinkjavaapi.model.TestCase#getCustomFields()
	 */
	@Override
	public List<CustomField> getCustomFields()
	{

		return this.testCase.getCustomFields();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.eti.kinoshita.testlinkjavaapi.model.TestCase#getExecutionOrder()
	 */
	@Override
	public Integer getExecutionOrder()
	{

		return this.testCase.getExecutionOrder();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.eti.kinoshita.testlinkjavaapi.model.TestCase#getExecutionType()
	 */
	@Override
	public ExecutionType getExecutionType()
	{

		return this.testCase.getExecutionType();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.eti.kinoshita.testlinkjavaapi.model.TestCase#getId()
	 */
	@Override
	public Integer getId()
	{

		return this.testCase.getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.eti.kinoshita.testlinkjavaapi.model.TestCase#getInternalId()
	 */
	@Override
	public Integer getInternalId()
	{

		return this.testCase.getInternalId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.eti.kinoshita.testlinkjavaapi.model.TestCase#getName()
	 */
	@Override
	public String getName()
	{

		return this.testCase.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.eti.kinoshita.testlinkjavaapi.model.TestCase#getOrder()
	 */
	@Override
	public Integer getOrder()
	{

		return this.testCase.getOrder();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.eti.kinoshita.testlinkjavaapi.model.TestCase#getParentId()
	 */
	@Override
	public Integer getParentId()
	{

		return this.testCase.getParentId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.eti.kinoshita.testlinkjavaapi.model.TestCase#getPreconditions()
	 */
	@Override
	public String getPreconditions()
	{

		return this.testCase.getPreconditions();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.eti.kinoshita.testlinkjavaapi.model.TestCase#getSteps()
	 */
	@Override
	public List<TestCaseStep> getSteps()
	{

		return this.testCase.getSteps();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.eti.kinoshita.testlinkjavaapi.model.TestCase#getSummary()
	 */
	@Override
	public String getSummary()
	{

		return this.testCase.getSummary();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.eti.kinoshita.testlinkjavaapi.model.TestCase#getTestImportance()
	 */
	@Override
	public TestImportance getTestImportance()
	{

		return this.testCase.getTestImportance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.eti.kinoshita.testlinkjavaapi.model.TestCase#getTestProjectId()
	 */
	@Override
	public Integer getTestProjectId()
	{

		return this.testCase.getTestProjectId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.eti.kinoshita.testlinkjavaapi.model.TestCase#getTestSuiteId()
	 */
	@Override
	public Integer getTestSuiteId()
	{

		return this.testCase.getTestSuiteId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.eti.kinoshita.testlinkjavaapi.model.TestCase#getVersion()
	 */
	@Override
	public Integer getVersion()
	{

		return this.testCase.getVersion();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.eti.kinoshita.testlinkjavaapi.model.TestCase#getVersionId()
	 */
	@Override
	public Integer getVersionId()
	{

		return this.testCase.getVersionId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.eti.kinoshita.testlinkjavaapi.model.TestCase#setActionOnDuplicatedName
	 * (java.lang.String)
	 */
	@Override
	public void setActionOnDuplicatedName( String actionOnDuplicatedName )
	{

		this.testCase.setActionOnDuplicatedName(actionOnDuplicatedName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.eti.kinoshita.testlinkjavaapi.model.TestCase#setAuthorLogin(java.lang
	 * .String)
	 */
	@Override
	public void setAuthorLogin( String authorLogin )
	{

		this.testCase.setAuthorLogin(authorLogin);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.eti.kinoshita.testlinkjavaapi.model.TestCase#setCheckDuplicatedName
	 * (java.lang.Boolean)
	 */
	@Override
	public void setCheckDuplicatedName( Boolean checkDuplicatedName )
	{

		this.testCase.setCheckDuplicatedName(checkDuplicatedName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.eti.kinoshita.testlinkjavaapi.model.TestCase#setCustomFields(java.
	 * util.List)
	 */
	@Override
	public void setCustomFields( List<CustomField> customFields )
	{

		this.testCase.setCustomFields(customFields);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.eti.kinoshita.testlinkjavaapi.model.TestCase#setExecutionOrder(java
	 * .lang.Integer)
	 */
	@Override
	public void setExecutionOrder( Integer executionOrder )
	{

		this.testCase.setExecutionOrder(executionOrder);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.eti.kinoshita.testlinkjavaapi.model.TestCase#setExecutionStatus(br
	 * .eti.kinoshita.testlinkjavaapi.model.ExecutionStatus)
	 */
	@Override
	public void setExecutionStatus( ExecutionStatus executionStatus )
	{

		this.testCase.setExecutionStatus(executionStatus);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.eti.kinoshita.testlinkjavaapi.model.TestCase#setExecutionType(br.eti
	 * .kinoshita.testlinkjavaapi.model.ExecutionType)
	 */
	@Override
	public void setExecutionType( ExecutionType executionType )
	{

		this.testCase.setExecutionType(executionType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.eti.kinoshita.testlinkjavaapi.model.TestCase#setId(java.lang.Integer)
	 */
	@Override
	public void setId( Integer id )
	{

		this.testCase.setId(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.eti.kinoshita.testlinkjavaapi.model.TestCase#setInternalId(java.lang
	 * .Integer)
	 */
	@Override
	public void setInternalId( Integer internalId )
	{

		this.testCase.setInternalId(internalId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.eti.kinoshita.testlinkjavaapi.model.TestCase#setName(java.lang.String)
	 */
	@Override
	public void setName( String name )
	{

		this.testCase.setName(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.eti.kinoshita.testlinkjavaapi.model.TestCase#setOrder(java.lang.Integer
	 * )
	 */
	@Override
	public void setOrder( Integer order )
	{

		this.testCase.setOrder(order);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.eti.kinoshita.testlinkjavaapi.model.TestCase#setParentId(java.lang
	 * .Integer)
	 */
	@Override
	public void setParentId( Integer parentId )
	{

		this.testCase.setParentId(parentId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.eti.kinoshita.testlinkjavaapi.model.TestCase#setPreconditions(java
	 * .lang.String)
	 */
	@Override
	public void setPreconditions( String preconditions )
	{

		this.testCase.setPreconditions(preconditions);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.eti.kinoshita.testlinkjavaapi.model.TestCase#setSteps(java.util.List)
	 */
	@Override
	public void setSteps( List<TestCaseStep> steps )
	{

		this.testCase.setSteps(steps);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.eti.kinoshita.testlinkjavaapi.model.TestCase#setSummary(java.lang.
	 * String)
	 */
	@Override
	public void setSummary( String summary )
	{

		this.testCase.setSummary(summary);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.eti.kinoshita.testlinkjavaapi.model.TestCase#setTestImportance(br.
	 * eti.kinoshita.testlinkjavaapi.model.TestImportance)
	 */
	@Override
	public void setTestImportance( TestImportance testImportance )
	{

		this.testCase.setTestImportance(testImportance);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.eti.kinoshita.testlinkjavaapi.model.TestCase#setTestProjectId(java
	 * .lang.Integer)
	 */
	@Override
	public void setTestProjectId( Integer testProjectId )
	{

		this.testCase.setTestProjectId(testProjectId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.eti.kinoshita.testlinkjavaapi.model.TestCase#setTestSuiteId(java.lang
	 * .Integer)
	 */
	@Override
	public void setTestSuiteId( Integer testSuiteId )
	{

		this.testCase.setTestSuiteId(testSuiteId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.eti.kinoshita.testlinkjavaapi.model.TestCase#setVersion(java.lang.
	 * Integer)
	 */
	@Override
	public void setVersion( Integer version )
	{

		this.testCase.setVersion(version);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.eti.kinoshita.testlinkjavaapi.model.TestCase#setVersionId(java.lang
	 * .Integer)
	 */
	@Override
	public void setVersionId( Integer versionId )
	{

		this.testCase.setVersionId(versionId);
	}

}
