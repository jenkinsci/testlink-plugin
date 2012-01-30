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
import hudson.plugins.testlink.result.ReportFilesPatterns;
import hudson.plugins.testlink.util.ExecutionOrderComparator;
import hudson.tasks.BuildStep;
import hudson.tasks.Builder;
import hudson.util.VariableResolver;

import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

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
	 * Name of the Key Custom Field. This must be one of the custom fields in 
	 * the property customFields;
	 */
	protected final String keyCustomField;
	
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
	
	/*
	 * Test life cycle commands. With these hooks you can execute command before 
	 * the single test command, after the single test command, before the 
	 * iterative test command and after the test command.
	 */
	
	/**
	 * Report files patterns.
	 */
	protected final ReportFilesPatterns reportFilesPatterns;
	
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
	 * This constructor is bound to a stapler request. All parameters here are 
	 * passed by Jenkins.
	 * 
	 * @param testLinkName TestLink Installation name.
	 * @param testProjectName TestLink Test Project name.
	 * @param testPlanName TestLink Test Plan name.
	 * @param buildName TestLink Build name.
	 * @param customFields TestLink comma-separated list of Custom Fields.
	 * @param singleBuildSteps List of build steps to execute once for all automated test cases.
	 * @param iterativeBuildSteps List of build steps to execute for each Automated Test Case.
	 * @param keyCustomField Automated Test Case key custom field.
	 * @param transactional Whether the build's execution is transactional or not.
	 * @param failedTestsMarkBuildAsUnstable Whether failed tests mark the build as unstable or not.
	 * @param junitXmlReportFilesPattern Pattern for JUnit report files.
	 * @param testNGXmlReportFilesPattern Pattern for TestNG report files.
	 * @param tapStreamReportFilesPattern Pattern for TAP report files.
	 * @param beforeIteratingAllTestCasesBuildSteps Command executed before iterating all test cases.
	 * @param afterIteratingAllTestCasesBuildSteps Command executed after iterating all test cases.
	 */
	public AbstractTestLinkBuilder(
		String testLinkName, 
		String testProjectName, 
		String testPlanName, 
		String buildName, 
		String customFields, 
		String keyCustomField, 
		List<BuildStep> singleBuildSteps, 
		List<BuildStep> beforeIteratingAllTestCasesBuildSteps, 
		List<BuildStep> iterativeBuildSteps, 
		List<BuildStep> afterIteratingAllTestCasesBuildSteps, 
		Boolean transactional, 
		Boolean failedTestsMarkBuildAsUnstable, 
		String junitXmlReportFilesPattern, 
		String testNGXmlReportFilesPattern, 
		String tapStreamReportFilesPattern
	)
	{
		super();
		this.testLinkName = testLinkName;
		this.testProjectName = testProjectName;
		this.testPlanName = testPlanName;
		this.buildName = buildName;
		this.customFields = customFields;
		this.keyCustomField = keyCustomField;
		this.singleBuildSteps = singleBuildSteps;
		this.beforeIteratingAllTestCasesBuildSteps = beforeIteratingAllTestCasesBuildSteps;
		this.iterativeBuildSteps = iterativeBuildSteps;
		this.afterIteratingAllTestCasesBuildSteps = afterIteratingAllTestCasesBuildSteps;
		this.transactional = transactional;
		this.failedTestsMarkBuildAsFailure = failedTestsMarkBuildAsUnstable;
		
		this.reportFilesPatterns = new ReportFilesPatterns(
				junitXmlReportFilesPattern, 
				testNGXmlReportFilesPattern, 
				tapStreamReportFilesPattern);
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
	 * Expands test project name job configuration property, replacing environment 
	 * variables with Jenkins+System values.
	 * 
	 * @param variableResolver Jenkins Build Variable Resolver.
	 * @param envVars Jenkins Build Environment Variables.
	 * @return Expanded test project name job configuration property.
	 */
	public String expandTestProjectName( VariableResolver<String> variableResolver, EnvVars envVars )
	{
		return Util.replaceMacro(envVars.expand(getTestProjectName()), variableResolver);
	}
	
	public String getTestPlanName()
	{
		return this.testPlanName;
	}
	
	/**
	 * Expands test plan name job configuration property, replacing environment 
	 * variables with Jenkins+System values.
	 * 
	 * @param variableResolver Jenkins Build Variable Resolver.
	 * @param envVars Jenkins Build Environment Variables.
	 * @return Expanded test plan name job configuration property.
	 */
	public String expandTestPlanName( VariableResolver<String> variableResolver, EnvVars envVars )
	{
		return Util.replaceMacro(envVars.expand(getTestPlanName()), variableResolver);
	}
	
	public String getBuildName()
	{
		return this.buildName;
	}
	
	/**
	 * Expands build name job configuration property, replacing environment 
	 * variables with Jenkins+System values.
	 * 
	 * @param variableResolver Jenkins Build Variable Resolver.
	 * @param envVars Jenkins Build Environment Variables.
	 * @return Expanded build name job configuration property.
	 */
	public String expandBuildName( VariableResolver<String> variableResolver, EnvVars envVars )
	{
		return Util.replaceMacro(envVars.expand(getBuildName()), variableResolver);
	}
	
	public String getCustomFields()
	{
		return this.customFields;
	}
	
	public String getKeyCustomField() 
	{
		return keyCustomField;
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
	public Boolean getFailedTestsMarkBuildAsFailure()
	{
		return failedTestsMarkBuildAsFailure;
	}

	/**
	 * Returns report files patterns for JUnit, TestNG and TAP.
	 * 
	 * @return Report files patterns.
	 */
	public ReportFilesPatterns getReportFilesPatterns() 
	{
		return reportFilesPatterns;
	}

	public String getJunitXmlReportFilesPattern()
	{
		return reportFilesPatterns.getJunitXmlReportFilesPattern();
	}
	
	public String getTestNGXmlReportFilesPattern()
	{
		return reportFilesPatterns.getTestNGXmlReportFilesPattern();
	}
	
	public String getTapStreamReportFilesPattern()
	{
		return reportFilesPatterns.getTapStreamReportFilesPattern();
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
	 * 
	 * @return Array of custom fields names.
	 */
	protected String[] createArrayOfCustomFieldsNames()
	{
		String[] customFieldNamesArray = new String[0];
		String customFields = this.getCustomFields();
		
		if( StringUtils.isNotBlank( customFields ) )
		{
			StringTokenizer tokenizer = new StringTokenizer(customFields, ",");
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
	
}
