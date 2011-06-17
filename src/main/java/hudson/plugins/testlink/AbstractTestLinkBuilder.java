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
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.plugins.testlink.result.ReportFilesPatterns;
import hudson.plugins.testlink.result.TestLinkReport;
import hudson.plugins.testlink.util.ExecutionOrderComparator;
import hudson.plugins.testlink.util.Messages;
import hudson.tasks.Builder;
import hudson.util.VariableResolver;

import java.util.Arrays;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.model.TestProject;

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
	 * The name of the Test Project. When the job is executed this property is 
	 * used to build the TestProject object from TestLink using the 
	 * Java API.
	 */
	protected final String testProjectName;
	
	/**
	 * The name of the Test Plan. When the job is executed this property is 
	 * used to build the TestPlan object from TestLink using the 
	 * Java API.
	 */
	protected final String testPlanName;
	
	/**
	 * The name of the Build. If the job is using SVN latest revision this 
	 * property may change during the job execution.
	 */
	protected String buildName;
	
	/**
	 * Comma separated list of custom fields to download from TestLink.
	 */
	protected final String customFields;
	
	/**
	 * The test command to be executed independently of how many tests we have. 
	 * With this command we are able to execute a test suite, for instance. 
	 */
	protected final String singleTestCommand;
	
	/**
	 * The test command to be executed for each Test Case found. We will 
	 * execute this command passing the Test Case information as environment 
	 * variables.
	 */
	protected final String iterativeTestCommand;
	
	/**
	 * Name of the Key Custom Field.
	 */
	protected final String keyCustomField;
	
	/**
	 * Whether this build has a transactional execution of tests or not. If a 
	 * job has transactional execution it means that in case any Test Case 
	 * execution fails the remaining Test Cases will be marked as <b>Blocked</b>
	 * . If you update TestLink status then the Test Case status will be 
	 * set to Blocked. Otherwise this information will be available only in 
	 * Hudson.
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
	 * Command executed before iterating all test cases.
	 */
	protected final String beforeIteratingAllTestCasesCommand;
	
	/**
	 * Command executed after iterating all test cases.
	 */
	protected final String afterIteratingAllTestCasesCommand;
	
	/**
	 * Report files patterns.
	 */
	protected final ReportFilesPatterns reportFilesPatterns;
	
	/* --- Other members --- */
	
	/**
	 * Used to sort test cases marked as automated.
	 */
	private final ExecutionOrderComparator executionOrderComparator = new ExecutionOrderComparator();
	
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
	 * @param iterativeTestCommand Test Command to execute for each Automated Test Case.
	 * @param keyCustomField Automated Test Case key custom field.
	 * @param transactional Whether the build's execution is transactional or not.
	 * @param failedTestsMarkBuildAsUnstable Whether failed tests mark the build as unstable or not.
	 * @param junitXmlReportFilesPattern Pattern for JUnit report files.
	 * @param testNGXmlReportFilesPattern Pattern for TestNG report files.
	 * @param tapStreamReportFilesPattern Pattern for TAP report files.
	 * @param beforeIteratingAllTestCasesCommand Command executed before iterating all test cases.
	 * @param afterIteratingAllTestCasesCommand Command executed after iterating all test cases.
	 */
	public AbstractTestLinkBuilder(
		String testLinkName, 
		String testProjectName, 
		String testPlanName, 
		String buildName, 
		String customFields, 
		String singleTestCommand, 
		String iterativeTestCommand, 
		String keyCustomField, 
		Boolean transactional, 
		Boolean failedTestsMarkBuildAsUnstable, 
		String junitXmlReportFilesPattern, 
		String testNGXmlReportFilesPattern, 
		String tapStreamReportFilesPattern, 
		String beforeIteratingAllTestCasesCommand, 
		String afterIteratingAllTestCasesCommand
	)
	{
		super();
		this.testLinkName = testLinkName;
		this.testProjectName = testProjectName;
		this.testPlanName = testPlanName;
		this.buildName = buildName;
		this.customFields = customFields;
		this.singleTestCommand = singleTestCommand;
		this.iterativeTestCommand = iterativeTestCommand;
		this.keyCustomField = keyCustomField;
		this.transactional = transactional;
		this.failedTestsMarkBuildAsFailure = failedTestsMarkBuildAsUnstable;
		
		this.reportFilesPatterns = new ReportFilesPatterns(
				junitXmlReportFilesPattern, 
				testNGXmlReportFilesPattern, 
				tapStreamReportFilesPattern);

		this.beforeIteratingAllTestCasesCommand = beforeIteratingAllTestCasesCommand;
		this.afterIteratingAllTestCasesCommand = afterIteratingAllTestCasesCommand;
	}
	
	/**
	 * @return Test Link Installation Name
	 */
	public String getTestLinkName()
	{
		return this.testLinkName;
	}
	
	/**
	 * @return Test Project Name
	 */
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
	
	/**
	 * @return Test Plan Name
	 */
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
	
	/**
	 * Ignored if it is marked to use SVN latest revision.
	 * 
	 * @return Name of the Build to create or use in TestLink
	 */
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
	
	/**
	 * @return Single Test Command.
	 */
	public String getSingleTestCommand()
	{
		return this.singleTestCommand;
	}
	
	/**
	 * @return Iterative Test Command.
	 */
	public String getIterativeTestCommand()
	{
		return this.iterativeTestCommand;
	}
	
	/**
	 * @return Comma separated list of custom fields
	 */
	public String getCustomFields()
	{
		return this.customFields;
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
	 * Retrieves key custom field.
	 * 
	 * @return Key custom field.
	 */
	public String getKeyCustomField() 
	{
		return keyCustomField;
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

	public String getBeforeIteratingAllTestCasesCommand()
	{
		return beforeIteratingAllTestCasesCommand;
	}
	
	public String getAfterIteratingAllTestCasesCommand()
	{
		return afterIteratingAllTestCasesCommand;
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
	
	protected void sortAutomatedTestCases( TestCase[] automatedTestCases, BuildListener listener )
	{
		listener.getLogger().println( Messages.TestLinkBuilder_SortingTestCases() );
		Arrays.sort( automatedTestCases, this.executionOrderComparator );
	}

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
	
	/**
	 * Creates the report for the build execution. 
	 * 
	 * @param build TestLink build.
	 * @param testPlan TestLink test plan. 
	 * @param testProject TestLink test project.
	 * @param automatedTestCases Array of TestLink automated test cases.
	 * @return report.
	 */
	protected TestLinkReport createReport( Build build, TestPlan testPlan,
			TestProject testProject, TestCase[] automatedTestCases )
	{
		final TestLinkReport report = new TestLinkReport( build, testPlan, testProject );
		
		for( int i = 0 ; automatedTestCases != null && i < automatedTestCases.length ; ++i )
		{
			final TestCase testCase = automatedTestCases [ i ];
			report.addTestCase ( testCase );
		}
		
		return report;
	}
	

	/**
	 * Updates Build status as UNSTABLE if it contains failed tests. If the 
	 * user checks the option failed tests mark build as failure, then it updates 
	 * Build status as FAILURE.
	 * 
	 * @param build Build.
	 */
	protected void updateBuildStatus( int testFailures, AbstractBuild<?, ?> build )
	{
		if ( testFailures > 0 )
		{
			if ( this.failedTestsMarkBuildAsFailure != null && this.failedTestsMarkBuildAsFailure )
			{
				build.setResult( Result.FAILURE );
			}
			else
			{
				build.setResult( Result.UNSTABLE );
			}
		}
	}
	
}
