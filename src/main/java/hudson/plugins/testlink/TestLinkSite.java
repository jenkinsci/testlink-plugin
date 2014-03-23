/*
 * The MIT License
 *
 * Copyright (c) <2011> <Bruno P. Kinoshita>
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
package hudson.plugins.testlink;

import hudson.plugins.testlink.result.TestCaseWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionType;
import br.eti.kinoshita.testlinkjavaapi.constants.ResponseDetails;
import br.eti.kinoshita.testlinkjavaapi.constants.TestCaseDetails;
import br.eti.kinoshita.testlinkjavaapi.model.Attachment;
import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.Platform;
import br.eti.kinoshita.testlinkjavaapi.model.ReportTCResultResponse;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.model.TestProject;

/**
 * Immutable object that represents the TestLink site with a Test Project, a Test Plan and a Build.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 3.0
 */
public class TestLinkSite {

	protected final TestLinkAPI api;
	protected final TestProject testProject;
	protected final TestPlan testPlan;
	protected final Platform platform;
	protected final Build build;
	protected final Report report;
    private int parallelRequest=1;
	/**
	 * Kept for backward compatibility. Don't add fields.
	 * 
	 * @deprecated
	 */
	public TestLinkSite(TestLinkAPI api, TestProject testProject, TestPlan testPlan, Build build) {
		this(api, testProject, testPlan, null, build);
	}

	/**
	 * @param api
	 *            TestLink Java API object
	 * @param testProject
	 *            TestLink Test Project
	 * @param testPlan
	 *            TestLink Test Plan
	 * @param platform
	 *            TestLink platform
	 * @param build
	 *            TestLink Build
	 */
	public TestLinkSite(TestLinkAPI api, TestProject testProject, TestPlan testPlan, Platform platform, Build build) {
		super();
		this.api = api;
		this.testProject = testProject;
		this.testPlan = testPlan;
		this.platform = platform;
		this.build = build;
		if (build != null) {
			report = new Report(build.getId() == null ? 0 : build.getId(), build.getName());
		} else {
			report = new Report(0, null);
		}
	}

	/**
	 * @return the TestLink Java API object
	 */
	public TestLinkAPI getApi() {
		return api;
	}

	/**
	 * @return the testProject
	 */
	public TestProject getTestProject() {
		return testProject;
	}

	/**
	 * @return the testPlan
	 */
	public TestPlan getTestPlan() {
		return testPlan;
	}

	/**
	 * @return the platform
	 */
	public Platform getPlatform() {
		return platform;
	}

	/**
	 * @return the build
	 */
	public Build getBuild() {
		return build;
	}

	/**
	 * @return the report
	 */
	public Report getReport() {
		return report;
	}

	/**
	 * @param parallelRequest
	 */
	public void setParallelRequest(int parallelRequest) {
		this.parallelRequest = parallelRequest;
	}

	
	/**
	 * @return
	 */
	public int getParallelRequest() {
		return parallelRequest;
	}

	
	/**
	 * @param customFieldsNames
	 *            Array of custom fields names
	 * @param keywords
	 *            , separated e.g.: database,performance
	 * @return Array of automated test cases with custom fields
	 */
	public TestCase[] getAutomatedTestCases(final String[] customFieldsNames, String keywords) {
		final TestCase[] testCases = this.api.getTestCasesForTestPlan(getTestPlan().getId(), null, null, null,
				keywords, null, null, null, // execute status
				ExecutionType.AUTOMATED, Boolean.TRUE, TestCaseDetails.FULL);

		final String[] keywordsList;
		if (StringUtils.isBlank(keywords)) {
			keywordsList = new String[0];
		} else {
			keywordsList = TestCaseWrapper.split(keywords);
		}

		ExecutorService executor = Executors.newFixedThreadPool(parallelRequest);
		for (final TestCase testCase : testCases) {
			Runnable worker = new UpdateTestCaseDefinitionWorker(customFieldsNames, keywordsList, testCase);
            executor.execute(worker);
		}

		executor.shutdown();
        // Wait until all threads are finish
        while (!executor.isTerminated()) {
        }
        
		return testCases;
	}

	/**
	 * Updates the test cases status in TestLink (note and status) and uploads any existing attachments.
	 * 
	 * @param testCases
	 *            Test Cases
	 */
	public int updateTestCase(TestCaseWrapper testCase) {
		int executionId = 0;
		Integer platformId = null;
		String platformName = null;

		if (testCase.getPlatform() == null) {
			if (platform != null) {
				platformId = platform.getId(); // platform id
				platformName = platform.getName(); // platform name
			}
		} else {
			// platformId is set to null
			platformName = testCase.getPlatform(); // platform name
		}

		if (testCase.getExecutionStatus() != null && !ExecutionStatus.NOT_RUN.equals(testCase.getExecutionStatus())) {
			// Update Test Case status
			final ReportTCResultResponse reportTCResultResponse = api.reportTCResult(testCase.getId(),
					testCase.getInternalId(), testPlan.getId(), testCase.getExecutionStatus(), build.getId(),
					build.getName(), testCase.getNotes(), null, // guess
					null, // bug id
					platformId, // platform id
					platformName, // platform name
					null, // custom fields
					null);

			switch (testCase.getExecutionStatus()) {
			case PASSED:
				report.setPassed(report.getPassed() + 1);
				break;
			case FAILED:
				report.setFailed(report.getFailed() + 1);
				break;
			case BLOCKED:
				report.setBlocked(report.getBlocked() + 1);
				break;
			default:
				break;
			}

			executionId = reportTCResultResponse.getExecutionId();
			// report.addTestCase(testCase);
		}

		return executionId;
	}

	/**
	 * @param executionId
	 * @param attachment
	 * @return
	 */
	public Attachment uploadAttachment(int executionId, Attachment attachment) {
		return api.uploadExecutionAttachment(executionId, attachment.getTitle(), attachment.getDescription(),
				attachment.getFileName(), attachment.getFileType(), attachment.getContent());
	}

	/**
	 *
	 */
	class UpdateTestCaseDefinitionWorker implements Runnable {
		private String[] customFieldsNames;
		private TestCase testCase;
		private String[] keywordsList;

		UpdateTestCaseDefinitionWorker(String[] customFieldsNames, String[] keywordsList, final TestCase testCase) {
			this.customFieldsNames=customFieldsNames;
			this.keywordsList=keywordsList;
			this.testCase=testCase;
		}

		public void run() {
			testCase.setTestProjectId(getTestProject().getId());
			testCase.setExecutionStatus(ExecutionStatus.NOT_RUN);
			if (customFieldsNames != null) {
				for (String customFieldName : customFieldsNames) {
					final CustomField customField = api.getTestCaseCustomFieldDesignValue(testCase.getId(), null, /* testCaseExternalId */
							testCase.getVersion(), testCase.getTestProjectId(), customFieldName, ResponseDetails.FULL);
					testCase.getCustomFields().add(customField);
				}
			}
			if (keywordsList.length > 0) {
				// TODO waiting for next version of API
				// List<String> testCaseKeywords = api.getTestCaseKeywords(testCase.getTestProjectId(), testCase.getId());
				// testCase.setKeywords(testCaseKeywords);
			}
		}

	}
	
}
