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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import br.eti.kinoshita.testlinkjavaapi.model.Attachment;
import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.0
 */
public class TestCaseWrapper implements Serializable {

	private static final long serialVersionUID = 6109763747785922349L;

	/**
	 * A list of custom field and status, used to allow the user to use a comma
	 * separated list of custom fields. A result seeker is responsible for
	 * seeking the test results and, for each one found, add the custom field
	 * value and the status.
	 */
	private final Map<String, ExecutionStatus> customFieldAndStatus;

	/**
	 * List of attachments.
	 */
	private List<Attachment> attachments;

	/**
	 * Execution notes.
	 */
	private StringBuilder notes;

	/**
	 * Platform.
	 */
	private String platform = null;

	/**
	 * Array of custom fields names retrieved from TestLink.
	 */
	private final String[] customFieldsNames;

	/**
	 * Wrapped Automated Test Case.
	 */
	private TestCase testCase;

	private String keyCustomFieldValue;

	/**
	 * @param customFieldsNames
	 *            array of custom fields names.
	 */
	public TestCaseWrapper(String[] customFieldsNames) {
		this.testCase = new TestCase();
		this.notes = new StringBuilder();
		this.attachments = new LinkedList<Attachment>();
		this.customFieldAndStatus = new HashMap<String, ExecutionStatus>();
		if (customFieldsNames == null) {
			this.customFieldsNames = new String[0];
		} else {
			this.customFieldsNames = customFieldsNames;
		}
	}
	
	/**
	 * @param testCase
	 *            wrapped automated test case.
	 * @param customFieldsNames
	 *            array of custom fields names.
	 */
	public TestCaseWrapper(TestCase testCase, String[] customFieldsNames) {
		this.testCase = testCase;
		this.notes = new StringBuilder();
		this.attachments = new LinkedList<Attachment>();
		this.customFieldAndStatus = new HashMap<String, ExecutionStatus>();
		if (customFieldsNames == null) {
			this.customFieldsNames = new String[0];
		} else {
			this.customFieldsNames = customFieldsNames;
		}
	}

	/**
	 * @return array of custom fields names
	 */
	public String[] getCustomFieldsNames() {
		return this.customFieldsNames;
	}

	/**
	 * @return value of key custom field
	 */
	public String getKeyCustomFieldValue() {
		return this.keyCustomFieldValue;
	}
	
	public void setKeyCustomFieldValue(String keyCustomFieldValue) {
		this.keyCustomFieldValue = keyCustomFieldValue;
	}

	/**
	 * Add a custom field name and its execution status.
	 * 
	 * @param customField
	 *            custom field name
	 * @param executionStatus
	 *            execution status
	 */
	public void addCustomFieldAndStatus(String customField,
			ExecutionStatus executionStatus) {
		this.customFieldAndStatus.put(customField, executionStatus);
	}

	/**
	 * @return custom field name and execution status
	 */
	public Map<String, ExecutionStatus> getCustomFieldAndStatus() {
		return customFieldAndStatus;
	}

	/**
	 * Adds an attachment to this test case. Use it with caution, as it may case
	 * memory issues if you store many Attachments in memory. The content is
	 * saved as Base64 in memory.
	 * 
	 * @param attachment
	 */
	public void addAttachment(Attachment attachment) {
		this.attachments.add(attachment);
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public String getNotes() {
		return notes.toString();
	}

	public void appendNotes(String notes) {
		this.notes.append(notes);
	}

	public String getPlatform() {
		return this.platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public ExecutionStatus getExecutionStatus() {
		ExecutionStatus status = ExecutionStatus.NOT_RUN;
		if (customFieldAndStatus.size() > 0
				&& customFieldAndStatus.size() == customFieldsNames.length) {
			status = ExecutionStatus.PASSED;
			for (ExecutionStatus reportedStatus : customFieldAndStatus.values()) {
				if (reportedStatus == ExecutionStatus.FAILED
						|| reportedStatus == ExecutionStatus.BLOCKED) {
					status = reportedStatus;
					break;
				}
			}
		}
		return status;
	}

	public Integer getId() {
		return this.testCase.getId();
	}
	
	public void setId(Integer id) {
		this.testCase.setId(id);
	}

	public String getName() {

		return this.testCase.getName();
	}

	public void setCustomFields(List<CustomField> customFields) {

		this.testCase.setCustomFields(customFields);
	}
	
	public List<CustomField> getCustomFields() {
		return this.testCase.getCustomFields();
	}

	public void setExecutionStatus(ExecutionStatus executionStatus) {

		this.testCase.setExecutionStatus(executionStatus);
	}

	public void setName(String name) {

		this.testCase.setName(name);
	}

	public Integer getInternalId() {
		return testCase.getInternalId();
	}
	
	public void setInternalId(Integer internalId) {
		this.testCase.setInternalId(internalId);
	}

}
