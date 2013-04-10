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
package hudson.plugins.testlink;

import hudson.EnvVars;
import hudson.Util;
import hudson.model.Action;
import hudson.model.AbstractProject;
import hudson.plugins.testlink.result.ResultSeeker;
import hudson.plugins.testlink.util.ExecutionOrderComparator;
import hudson.tasks.BuildStep;
import hudson.tasks.Builder;
import hudson.util.VariableResolver;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionStatus;

/**
 * Contains basic logic for a Builder for TestLink plug-in. This class was 
 * created to reduce complexity and reduce the length of the code present 
 * in the Builder itself.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.4
 */
public class AbstractTestLinkBuilder
extends Builder
{

	/* --- Job properties --- */
	
	/**
	 * Comma constant for custom fields separated with delimiter.
	 */
	private static final String COMMA = ",";

	/**
	 * The name of the TestLink installation.
	 */
	protected final String testLinkName;
	
	/**
	 * The name of the Test Project.
	 */
	protected final String testProjectName;
	
	/**
	 * The name of the Test Plan.
	 */
	protected final String testPlanName;
	
	/**
	 * The name of the Build.
	 */
	protected String buildName;
	
	/**
	 * Comma separated list of custom fields to download from TestLink.
	 */
	protected final String customFields;
	
	/**
	 * Tests that have not been run.
	 */
	protected final Boolean executionStatusNotRun;
	
	/**
	 * Tests that have passed.
	 */
	protected final Boolean executionStatusPassed;
	
	/**
	 * Tests that have failed.
	 */
	protected final Boolean executionStatusFailed;
	
	/**
	 * Tests that are blocked.
	 */
	protected final Boolean executionStatusBlocked;
	
	/**
	 * List of build steps that are executed only once per job execution. 
	 */
	protected final List<BuildStep> singleBuildSteps;
	
	/**
	 * List of build steps that are executed before iterating all test cases.
	 */
	protected final List<BuildStep> beforeIteratingAllTestCasesBuildSteps;
	
	/**
	 * List of build steps that are executed for each test case.
	 */
	protected final List<BuildStep> iterativeBuildSteps;
	
	/**
	 * List of build steps that are executed after iterating all test cases.
	 */
	protected final List<BuildStep> afterIteratingAllTestCasesBuildSteps;
	
	/**
	 * If this property is true, not more build steps are executed for this 
	 * Build.
	 */
	protected final Boolean transactional;
	
	/**
	 * If the plug-in should mark the Build in Jenkins as failure if it 
	 * contains failed tests.
	 */
	protected final Boolean failedTestsMarkBuildAsFailure;
	
	/**
	 * Fail the build if no test results are present.
	 */
	protected final Boolean failIfNoResults;
	
	/*
	 * Test life cycle commands. With these hooks you can execute command before 
	 * the single test command, after the single test command, before the 
	 * iterative test command and after the test command.
	 */
	
	/* --- Other members --- */
	
	/**
	 * Used to sort test cases marked as automated.
	 */
	protected final ExecutionOrderComparator executionOrderComparator = new ExecutionOrderComparator();
	
	/**
	 * Flag to check if any failure happened.
	 */
	protected boolean failure = false;

	/**
	 * Results seekers.
	 */
	private List<ResultSeeker> resultSeekers;
	
	/**
	 * This constructor is bound to a stapler request. All parameters here are 
	 * passed by Jenkins.
	 * 
	 * @param testLinkName TestLink Installation name.
	 * @param testProjectName TestLink Test Project name.
	 * @param testPlanName TestLink Test Plan name.
	 * @param buildName TestLink Build name.
	 * @param customFields TestLink comma-separated list of Custom Fields.
	 * @param keyCustomField Key custom field.
	 * @param singleBuildSteps List of build steps to execute once for all automated test cases.
	 * @param beforeIteratingAllTestCasesBuildSteps Command executed before iterating all test cases.
	 * @param iterativeBuildSteps List of build steps to execute for each Automated Test Case.
	 * @param afterIteratingAllTestCasesBuildSteps Command executed after iterating all test cases.
	 * @param transactional Whether the build's execution is transactional or not.
	 * @param failedTestsMarkBuildAsFailure Whether failed tests mark the build as failure or not.
	 * @param failIfNoResults If true marks the build as FAILURE.
	 * @param resultSeekers List of result seekers.
	 */
	public AbstractTestLinkBuilder(
		String testLinkName, 
		String testProjectName, 
		String testPlanName, 
		String buildName, 
		String customFields, 
		Boolean executionStatusNotRun,
		Boolean executionStatusPassed,
		Boolean executionStatusFailed,
		Boolean executionStatusBlocked,
		List<BuildStep> singleBuildSteps, 
		List<BuildStep> beforeIteratingAllTestCasesBuildSteps, 
		List<BuildStep> iterativeBuildSteps, 
		List<BuildStep> afterIteratingAllTestCasesBuildSteps, 
		Boolean transactional, 
		Boolean failedTestsMarkBuildAsFailure, 
		Boolean failIfNoResults, 
		List<ResultSeeker> resultSeekers
	) {
		super();
		this.testLinkName = testLinkName;
		this.testProjectName = testProjectName;
		this.testPlanName = testPlanName;
		this.buildName = buildName;
		this.customFields = customFields;
		this.executionStatusNotRun = executionStatusNotRun;
		this.executionStatusPassed = executionStatusPassed;
		this.executionStatusFailed = executionStatusFailed;
		this.executionStatusBlocked = executionStatusBlocked;
		this.singleBuildSteps = singleBuildSteps;
		this.beforeIteratingAllTestCasesBuildSteps = beforeIteratingAllTestCasesBuildSteps;
		this.iterativeBuildSteps = iterativeBuildSteps;
		this.afterIteratingAllTestCasesBuildSteps = afterIteratingAllTestCasesBuildSteps;
		this.transactional = transactional;
		this.failedTestsMarkBuildAsFailure = failedTestsMarkBuildAsFailure;
		this.failIfNoResults = failIfNoResults;
		this.resultSeekers = resultSeekers;
	}
	
	public String getTestLinkName()
	{
		return this.testLinkName;
	}
	
	public String getTestProjectName()
	{
		return this.testProjectName;
	}
	
	/**
	 * Expands a text variable like BUILD-$VAR replacing the $VAR part with 
	 * a environment variable that matches its name, minus $.
	 * 
	 * @param variableResolver Jenkins Build Variable Resolver.
	 * @param envVars Jenkins Build Environment Variables.
	 * @param variable Variable value (includes mask).
	 * @return Expanded test project name job configuration property.
	 */
	public String expandVariable( VariableResolver<String> variableResolver, EnvVars envVars, String variable )
	{
		return Util.replaceMacro(envVars.expand(variable), variableResolver);
	}
	
	public String getTestPlanName()
	{
		return this.testPlanName;
	}
	
	public String getBuildName()
	{
		return this.buildName;
	}
	
	public String getCustomFields()
	{
		return this.customFields;
	}

	public Boolean getExecutionStatusNotRun() {
		return executionStatusNotRun;
	}

	public Boolean getExecutionStatusPassed() {
		return executionStatusPassed;
	}

	public Boolean getExecutionStatusFailed() {
		return executionStatusFailed;
	}

	public Boolean getExecutionStatusBlocked() {
		return executionStatusBlocked;
	}

	public List<BuildStep> getSingleBuildSteps()
	{
		return this.singleBuildSteps;
	}
	
	public List<BuildStep> getBeforeIteratingAllTestCasesBuildSteps()
	{
		return beforeIteratingAllTestCasesBuildSteps;
	}

	public List<BuildStep> getIterativeBuildSteps()
	{
		return this.iterativeBuildSteps;
	}
	
	public List<BuildStep> getAfterIteratingAllTestCasesBuildSteps()
	{
		return afterIteratingAllTestCasesBuildSteps;
	}
	
	/**
	 * Returns whether it is a transactional build or not. A transactional 
	 * build stops executing once a test fails. All tests must succeed or it 
	 * won't finish its execution and will mark all remaining tests with 
	 * Blocked status.
	 * 
	 * @return If the build step should be transactional or not
	 */
	public Boolean getTransactional()
	{
		return this.transactional;
	}
	
	/**
	 * @return the failedTestsMarkBuildAsUnstable
	 */
	public Boolean getFailedTestsMarkBuildAsUnstable()
	{
		return failedTestsMarkBuildAsFailure;
	}
	
	/**
	 * @return the failIfNoResults
	 */
	public Boolean getFailIfNoResults() {
		return failIfNoResults;
	}
	
	/**
	 * @return the resultSeekers
	 */
	public List<ResultSeeker> getResultSeekers() {
		return resultSeekers;
	}
	
	/**
	 * @param resultSeekers the resultSeekers to set
	 */
	public void setResultSeekers(List<ResultSeeker> resultSeekers) {
		this.resultSeekers = resultSeekers;
	}

	/* (non-Javadoc)
	 * @see hudson.tasks.BuildStepCompatibilityLayer#getProjectAction(hudson.model.AbstractProject)
	 */
	@Override
	public Action getProjectAction(AbstractProject<?, ?> project) 
	{
		return new TestLinkProjectAction(project);
	}
	
	/* --- Utility methods --- */

	/**
	 * Creates array of custom fields names using the Job configuration data.
	 * @param variableResolver Jenkins variable resolver
	 * @param envVars Jenkins environment variables
	 * 
	 * @return Array of custom fields names.
	 */
	protected String[] createArrayOfCustomFieldsNames(final VariableResolver<String> variableResolver, final EnvVars envVars)
	{
		String[] customFieldNamesArray = new String[0];
		String customFields = expandVariable(variableResolver, envVars, this.getCustomFields());
		
		if( StringUtils.isNotBlank( customFields ) )
		{
			StringTokenizer tokenizer = new StringTokenizer(customFields, COMMA);
			if ( tokenizer.countTokens() > 0 )
			{
				customFieldNamesArray = new String[ tokenizer.countTokens() ];
				int index = 0;
				while ( tokenizer.hasMoreTokens() )
				{
					String customFieldName = tokenizer.nextToken();
					customFieldName = customFieldName.trim();
					customFieldNamesArray[ index ] = customFieldName;
					index = index + 1;
				}
			}
		}
		
		return customFieldNamesArray;
	}

	/**
	 * Return a set of execution statuses that we are interested in. If none are
	 * true, then assume that job is to run tests of all execution statuses.
	 * 
	 * @return a set of execution statuses
	 */
	Set<ExecutionStatus> getExecutionStatuses() {
		Set<ExecutionStatus> statuses = new HashSet<ExecutionStatus>();
		if (Boolean.TRUE.equals(executionStatusNotRun)) {
			statuses.add(ExecutionStatus.NOT_RUN);
		}
		if (Boolean.TRUE.equals(executionStatusPassed)) {
			statuses.add(ExecutionStatus.PASSED);
		}
		if (Boolean.TRUE.equals(executionStatusFailed)) {
			statuses.add(ExecutionStatus.FAILED);
		}
		if (Boolean.TRUE.equals(executionStatusBlocked)) {
			statuses.add(ExecutionStatus.BLOCKED);
		}
		if (statuses.size() == 0) {
			statuses.add(ExecutionStatus.NOT_RUN);
			statuses.add(ExecutionStatus.PASSED);
			statuses.add(ExecutionStatus.FAILED);
			statuses.add(ExecutionStatus.BLOCKED);
		}
		return statuses;
	}
	
	
}
