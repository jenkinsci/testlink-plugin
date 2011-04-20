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

import hudson.AbortException;
import hudson.CopyOnWrite;
import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.plugins.testlink.model.TestLinkLatestRevisionInfo;
import hudson.plugins.testlink.result.ReportFilesPatterns;
import hudson.plugins.testlink.result.TestLinkReport;
import hudson.plugins.testlink.result.TestResult;
import hudson.plugins.testlink.result.TestResultSeekerException;
import hudson.plugins.testlink.result.TestResultsCallable;
import hudson.plugins.testlink.svn.SVNLatestRevisionService;
import hudson.plugins.testlink.tasks.BatchFile;
import hudson.plugins.testlink.tasks.CommandInterpreter;
import hudson.plugins.testlink.tasks.Shell;
import hudson.plugins.testlink.util.ExecutionOrderComparator;
import hudson.plugins.testlink.util.Messages;
import hudson.tasks.Builder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.tmatesoft.svn.core.SVNException;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPIException;
import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.model.TestProject;

/**
 * <p>
 * A builder to add a TestLink build step.
 * </p>
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TestLinkBuilder 
extends Builder
{

	/* --- Job properties --- */
	
	/**
	 * The name of the TestLink installation.
	 */
	private final String testLinkName;
	
	/**
	 * The name of the Test Project. When the job is executed this property is 
	 * used to build the TestProject object from TestLink using the 
	 * Java API.
	 */
	private final String testProjectName;
	
	/**
	 * The name of the Test Plan. When the job is executed this property is 
	 * used to build the TestPlan object from TestLink using the 
	 * Java API.
	 */
	private final String testPlanName;
	
	/**
	 * The name of the Build. If the job is using SVN latest revision this 
	 * property may change during the job execution.
	 */
	private String buildName;
	
	/**
	 * Information related to the SVN that TestLink plug-in should use to 
	 * retrieve the latest revision.
	 */
	@CopyOnWrite
	private final TestLinkLatestRevisionInfo latestRevisionInfo;
	
	/**
	 * Comma separated list of custom fields to download from TestLink.
	 */
	private final String customFields;
	
	/**
	 * The test command to be executed independently of how many tests we have. 
	 * With this command we are able to execute a test suite, for instance. 
	 */
	private final String singleTestCommand;
	
	/**
	 * The test command to be executed for each Test Case found. We will 
	 * execute this command passing the Test Case information as environment 
	 * variables.
	 */
	private final String iterativeTestCommand;
	
	/**
	 * Whether this build has a transactional execution of tests or not. If a 
	 * job has transactional execution it means that in case any Test Case 
	 * execution fails the remaining Test Cases will be marked as <b>Blocked</b>
	 * . If you update TestLink status then the Test Case status will be 
	 * set to Blocked. Otherwise this information will be available only in 
	 * Hudson.
	 */
	private final Boolean transactional;
	
	/**
	 * Name of the Key Custom Field.
	 */
	private final String keyCustomField;
	
	/**
	 * JUnit XML reports directory.
	 */
	private final String junitReportFilesPattern;
	
	/**
	 * TestNG XML reports directory.
	 */
	private final String testNGReportFilesPattern;
	
	/**
	 * TAP Streams report directory.
	 */
	private final String tapReportFilesPattern;
	
	/* --- Object properties --- */
	
	private boolean failure = Boolean.FALSE;
	
	// Environment Variables names.
	private static final String TESTLINK_TESTCASE_PREFIX = "TESTLINK_TESTCASE_";
	private static final String TESTLINK_TESTCASE_ID_ENVVAR = "TESTLINK_TESTCASE_ID";
	private static final String TESTLINK_TESTCASE_NAME_ENVVAR = "TESTLINK_TESTCASE_NAME";
	private static final String TESTLINK_TESTCASE_TESTSUITE_ID_ENVVAR = "TESTLINK_TESTCASE_TESTSUITEID";
	private static final String TESTLINK_TESTCASE_TESTPROJECT_ID = "TESTLINK_TESTCASE_TESTPROJECTID";
	private static final String TESTLINK_TESTCASE_AUTHOR_ENVVAR = "TESTLINK_TESTCASE_AUTHOR";
	private static final String TESTLINK_TESTCASE_SUMMARY_ENVVAR = "TESTLINK_TESTCASE_SUMMARY";
	private static final String TESTLINK_BUILD_NAME_ENVVAR = "TESTLINK_BUILD_NAME";
	private static final String TESTLINK_TESTPLAN_NAME_ENVVAR = "TESTLINK_TESTPLAN_NAME";
	private static final String TESTLINK_TESTPROJECT_NAME_ENVVAR = "TESTLINK_TESTPROJECT_NAME";
	
	/**
	 * The Descriptor of this Builder. It contains the TestLink installation.
	 */
	@Extension 
	public static final TestLinkBuilderDescriptor DESCRIPTOR = new TestLinkBuilderDescriptor();

	/**
	 * This constructor is bound to a stapler request. All parameters here are 
	 * passed by Hudson.
	 * 
	 * @param testLinkName TestLink Installation name.
	 * @param testProjectName TestLink Test Project name.
	 * @param testPlanName TestLink Test Plan name.
	 * @param buildName TestLink Build name.
	 * @param customFields TestLink comma-separated list of Custom Fields.
	 * @param iterativeTestCommand Test Command to execute for each Automated Test Case.
	 * @param transactional Whether the build's execution is transactional or not.
	 * @param latestRevisionInfo Information on SVN latest revision.
	 * @param keyCustomField Automated Test Case key custom field. 
	 * @param junitReportFilesPattern Pattern for JUnit report files.
	 * @param testNGReportFilesPattern Pattern for TestNG report files.
	 * @param tapReportFilesPattern Pattern for TAP report files.
	 */
	@DataBoundConstructor
	public TestLinkBuilder(
		String testLinkName, 
		String testProjectName, 
		String testPlanName, 
		String buildName, 
		String customFields, 
		String singleTestCommand, 
		String iterativeTestCommand, 
		Boolean transactional, 
		TestLinkLatestRevisionInfo latestRevisionInfo, 
		String keyCustomField, 
		String junitReportFilesPattern, 		
		String testNGReportFilesPattern, 
		String tapReportFilesPattern
	)
	{
		this.testLinkName = testLinkName;
		this.testProjectName = testProjectName;
		this.testPlanName = testPlanName;
		this.buildName = buildName;
		this.latestRevisionInfo = latestRevisionInfo;
		this.customFields = customFields;
		this.singleTestCommand = singleTestCommand;
		this.iterativeTestCommand = iterativeTestCommand;
		this.transactional = transactional;
		this.keyCustomField = keyCustomField;
		this.junitReportFilesPattern = junitReportFilesPattern;
		this.testNGReportFilesPattern = testNGReportFilesPattern;
		this.tapReportFilesPattern = tapReportFilesPattern;
	}
	
	/* (non-Javadoc)
	 * @see hudson.tasks.BuildStepCompatibilityLayer#getProjectAction(hudson.model.AbstractProject)
	 */
	@Override
	public Action getProjectAction(AbstractProject<?, ?> project) 
	{
		return new TestLinkProjectAction(project);
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
	 * @return Test Plan Name
	 */
	public String getTestPlanName()
	{
		return this.testPlanName;
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
	 * @return {@link TestLinkLatestRevisionInfo} 
	 */
	public TestLinkLatestRevisionInfo getLatestRevisionInfo()
	{
		return this.latestRevisionInfo;
	}
	
	/**
	 * @return True when the builder should use the latest revision of the 
	 * system being tested as build name in TestLink. 
	 */
	public Boolean getLatestRevisionEnabled()
	{
		return this.latestRevisionInfo != null;
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
	
	public String getKeyCustomField() {
		return keyCustomField;
	}

	/**
	 * Returns test report directories (JUnit, TestNG, TAP, ...)
	 * 
	 * @return Test report directoriy
	 */
	public String getJunitReportFilesPattern() 
	{
		return junitReportFilesPattern;
	}

	/**
	 * Returns test report directories (JUnit, TestNG, TAP, ...)
	 * 
	 * @return Test report directoriy
	 */
	public String getTestNGReportFilesPattern() 
	{
		return testNGReportFilesPattern;
	}

	/**
	 * Returns test report directories (JUnit, TestNG, TAP, ...)
	 * 
	 * @return Test report directoriy
	 */
	public String getTapReportFilesPattern() 
	{
		return tapReportFilesPattern;
	}
	
//	@Override
//	public Descriptor<Builder> getDescriptor()
//	{
//		return DESCRIPTOR;
//	}
	// http://hudson.361315.n4.nabble.com/Saving-plugin-issue-td2236932.html

	/**
	 * <p>Called when the job is executed.</p>
	 * 
	 * <p>It downloads information from TestLink using testlink-java-api. 
	 * The information gathered is sufficient to execute a test command 
	 * that runs automated tests.</p>
	 * 
	 */
	@Override
	public boolean perform( AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener ) 
	throws InterruptedException, IOException
	{
		// TestLink installation.
		listener.getLogger().println( Messages.TestLinkBuilder_PreparingTLAPI() );
		
		TestLinkBuilderInstallation installation = 
			DESCRIPTOR.getInstallationByTestLinkName(this.testLinkName);
		if ( installation == null )
		{
			throw new AbortException( Messages.TestLinkBuilder_InvalidTLAPI() );
		}
		
		listener.getLogger().println ( Messages.TestLinkBuilder_UsedTLURL( installation.getUrl()) );
		listener.getLogger().println();
		
		// SVN revision information for Build.
		if ( this.getLatestRevisionEnabled() )
		{
			listener.getLogger().println( Messages.TestLinkBuilder_UsingSVNRevision() );
			
			SVNLatestRevisionService svn = new SVNLatestRevisionService(
					this.latestRevisionInfo.getSvnUrl(), 
					this.latestRevisionInfo.getSvnUser(),
					this.latestRevisionInfo.getSvnPassword());
			try
			{
				Long latestRevision = svn.getLatestRevision();
				this.buildName = Long.toString( latestRevision );
				listener.getLogger().println( Messages.TestLinkBuilder_ShowLatestRevision(this.latestRevisionInfo.getSvnUrl(), latestRevision) );
				listener.getLogger().println();
			} 
			catch (SVNException e)
			{
				e.printStackTrace( listener.fatalError(Messages.TestLinkBuilder_SVNError(e.getMessage())) );
				listener.getLogger().println();
				throw new AbortException( Messages.TestLinkBuilder_SVNError(e.getMessage()) );
			}
		}
		
		final String[] customFieldsNames = this.getListOfCustomFieldsNames();
		
		final String testLinkUrl = installation.getUrl();
		final String testLinkDevKey = installation.getDevKey();
		
		final TestLinkService testLinkSvc;
		try
		{
			URL url = new URL( testLinkUrl );
			testLinkSvc = new TestLinkService( url, testLinkDevKey, listener );
		}
		catch (MalformedURLException mue) 
		{
			throw new AbortException( Messages.TestLinkBuilder_InvalidTLURL( testLinkUrl) );
		}
		
		TestCase[] automatedTestCases = null;
		try
		{
			automatedTestCases = 
				testLinkSvc.initializeTestLinkAndFindAutomatedTestCases( 
					testProjectName, 
					testPlanName, 
					buildName, 
					Messages.TestLinkBuilder_Build_Notes(), 
					customFieldsNames
				);
		} 
		catch (TestLinkAPIException e)
		{
			e.printStackTrace( listener.fatalError(e.getMessage()) );
			throw new AbortException( Messages.TestLinkBuilder_TestLinkCommunicationError(testLinkUrl, testLinkDevKey) );
		}
		
		listener.getLogger().println( Messages.TestLinkBuilder_SortingTestCases() );
		final ExecutionOrderComparator executionOrderComparator = new ExecutionOrderComparator();
		Arrays.sort( automatedTestCases, executionOrderComparator );
		
		// Execute single test command
		this.executeSingleTestCommand( build, launcher, listener );
		
		// Execute iterative test command
		this.executeIterativeTestCommand( 
			automatedTestCases, 
			testLinkSvc.getTestProject(),
			testLinkSvc.getTestPlan(), 
			testLinkSvc.getBuild(), 
			build, 
			launcher, 
			listener );
		
		// Create list of report files patterns for TAP, TestNG and JUnit.
		final ReportFilesPatterns reportFilesPatterns = this.getReportPatterns();
		
		// Create report object
		final TestLinkReport report = 
			new TestLinkReport( 
				testLinkSvc.getBuild(), 
				testLinkSvc.getTestPlan(), 
				testLinkSvc.getTestProject());
		
		for( TestCase testCase : automatedTestCases )
		{
			report.getTestCases().add ( testCase );
		}
		
		// The object that searches for test results
		final TestResultsCallable testResultSeeker = 
			new TestResultsCallable(report, this.keyCustomField, reportFilesPatterns, listener);
		
		// Create list of test results
		Set<TestResult> testResults = null;
		
		try
		{
			listener.getLogger().println( Messages.Results_LookingForTestResults() );
			listener.getLogger().println();
			
			testResults = build.getWorkspace().act(testResultSeeker);
		}
		catch ( TestResultSeekerException trse )
		{
			listener.getLogger().println( Messages.Results_ErrorToLookForTestResults( trse.getMessage() ) );
			trse.printStackTrace( listener.fatalError( trse.getMessage() ) );
			listener.getLogger().println();
		}
		
		// Add blocked tests to the test results list
		for( TestCase testCase : automatedTestCases )
		{
			if ( testCase.getExecutionStatus() == ExecutionStatus.BLOCKED )
			{
				TestResult blockedTestResult = new TestResult(testCase, testLinkSvc.getBuild(), testLinkSvc.getTestPlan());
				testResults.add( blockedTestResult );
			}
		}
		
		// Update TestLink with test results and uploads attachments
		try 
		{
			testLinkSvc.updateTestCasesAndUploadAttachments( testResults );
		} 
		catch (TestLinkAPIException tlae) 
		{
			tlae.printStackTrace( listener.fatalError( Messages.TestLinkBuilder_FailedToUpdateTL(tlae.getMessage()) ) );
			throw new AbortException ( Messages.TestLinkBuilder_FailedToUpdateTL(tlae.getMessage()) );
		}
		
		// TBD: really necessary?
		report.getTestCases().clear();
		for( TestResult testResult : testResults)
		{
			report.getTestCases().add ( testResult.getTestCase() );
		}
		
		final TestLinkResult result = new TestLinkResult(report, build);
        final TestLinkBuildAction buildAction = new TestLinkBuildAction(build, result);
        
        build.addAction( buildAction );
		
		// end
		return Boolean.TRUE;
	}
	
	/**
	 * @return Array of custom fields names.
	 */
	protected String[] getListOfCustomFieldsNames()
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
	 * Executes single test command.
	 * 
	 * @param build Hudson Build.
	 * @param launcher Hudson Launcher.
	 * @param listener Hudson Build listener.
	 */
	protected void executeSingleTestCommand(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
	{
		if ( StringUtils.isNotBlank( singleTestCommand ) )
		{
			listener.getLogger().println( Messages.TestLinkBuilder_ExecutingSingleTestCommand( this.singleTestCommand ) );
			listener.getLogger().println();
			boolean success = this.executeTestCommand( 
					build, 
					launcher, 
					listener, 
					singleTestCommand, 
					null);
			if ( ! success )
			{
				this.failure = true;
			}
		}
		else
		{
			listener.getLogger().println( Messages.TestLinkBuilder_BlankSingleTestCommand() );
		}
		
		listener.getLogger().println();
	}
	
	/**
	 * <p>Executes iterative test command. For each automated test case found in the 
	 * array of automated test cases, this method executes the iterative command 
	 * using Hudson objects.</p>
	 * 
	 * <p>The objects of the TestLink Java API are used to create the 
	 * environment variables.</p>
	 * 
	 * @param automatedTestCases Array of automated test cases.
	 * @param project TestLink project.
	 * @param testPlan TestLink Test Plan.
	 * @param testLinkBuild TestLink Build.
	 * @param build Jenkins Build.
	 * @param launcher Jenkins Launcher.
	 * @param listener Jenkins Listener.
	 */
	protected void executeIterativeTestCommand( TestCase[] automatedTestCases, TestProject project, TestPlan testPlan, Build testLinkBuild, AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener ) 
	{
		if ( StringUtils.isNotBlank( iterativeTestCommand ) )
		{
			for ( TestCase automatedTestCase : automatedTestCases )
			{
				if ( this.failure  && this.transactional )
				{
					listener.getLogger().println(Messages.TestLinkBuilder_TransactionalError());
					listener.getLogger().println();
					
					automatedTestCase.setExecutionStatus(ExecutionStatus.BLOCKED);
				} 
				else
				{
					// Build environment variables
					final EnvVars iterativeEnvVars = this.buildIterativeEnvVars( automatedTestCase, project, testPlan, testLinkBuild, listener ); 
					
					listener.getLogger().println( Messages.TestLinkBuilder_ExecutingIterativeTestCommand( this.iterativeTestCommand ) );
					listener.getLogger().println();
					
					// Execute iterative test command
					final boolean success = this.executeTestCommand( 
							build, 
							launcher, 
							listener, 
							iterativeTestCommand, 
							iterativeEnvVars);
					
					if ( ! success )
					{
						this.failure = true;
					}
				}
			}
		}
		else
		{
			listener.getLogger().println( Messages.TestLinkBuilder_BlankIterativeTestCommand() );
			listener.getLogger().println();
		}
		
	}
	
	/**
	 * Creates EnvVars for a TestLink Test Case.
	 * 
	 * @param testCase TestLink test Case
	 * @param testProject TestLink Test Project
	 * @param testPlan TestLink Test Plan
	 * @param build TestLink Build
	 * @param listener Hudson Build Listener
	 * @return EnvVars (environment variables)
	 */
	protected EnvVars buildIterativeEnvVars( TestCase testCase, TestProject testProject, TestPlan testPlan, Build build, BuildListener listener ) 
	{
		// Build environment variables list
		listener.getLogger().println(Messages.TestLinkBuilder_CreatingEnvVars());
		Map<String, String> testLinkEnvironmentVariables = this.createTestLinkEnvironmentVariables( testCase, testProject, testPlan, build );

		// Merge with build environment variables list
		listener.getLogger().println(Messages.TestLinkBuilder_MergingEnvVars());
		listener.getLogger().println();
		
		final EnvVars buildEnvironment = new EnvVars( testLinkEnvironmentVariables );
		return buildEnvironment;
	}
	
	/**
	 * Creates a Map (name, value) of environment variables for a TestLink Test Case.
	 * 
	 * @param testCase TestLink test Case.
	 * @param testProject TestLink Test Project.
	 * @param testPlan TestLink Test Plan.
	 * @param build TestLink Build.
	 * @return Map (name, value) of environment variables.
	 */
	protected Map<String, String> createTestLinkEnvironmentVariables( TestCase testCase, TestProject testProject, TestPlan testPlan, Build build ) 
	{
		Map<String, String> testLinkEnvVar = new HashMap<String, String>();
		
		testLinkEnvVar.put( TESTLINK_TESTCASE_ID_ENVVAR, ""+testCase.getId() );
		testLinkEnvVar.put( TESTLINK_TESTCASE_NAME_ENVVAR, ""+testCase.getName() );
		testLinkEnvVar.put( TESTLINK_TESTCASE_TESTSUITE_ID_ENVVAR, ""+testCase.getTestSuiteId() );
		testLinkEnvVar.put( TESTLINK_TESTCASE_TESTPROJECT_ID, ""+testCase.getTestProjectId() );
		testLinkEnvVar.put( TESTLINK_TESTCASE_AUTHOR_ENVVAR, ""+testCase.getAuthorLogin() );
		testLinkEnvVar.put( TESTLINK_TESTCASE_SUMMARY_ENVVAR, testCase.getSummary() );
		testLinkEnvVar.put( TESTLINK_BUILD_NAME_ENVVAR, build.getName() );
		testLinkEnvVar.put( TESTLINK_TESTPLAN_NAME_ENVVAR, testPlan.getName() );
		testLinkEnvVar.put( TESTLINK_TESTPROJECT_NAME_ENVVAR, testProject.getName() );
		
		List<CustomField> customFields = testCase.getCustomFields();
		for (Iterator<CustomField> iterator = customFields.iterator(); iterator.hasNext();)
		{
			CustomField customField = iterator.next();
			String customFieldEnvVarName = this.formatCustomFieldEnvironmentVariableName( customField.getName() );
			testLinkEnvVar.put(customFieldEnvVarName , customField.getValue());
		}
		
		return testLinkEnvVar;
	}
	
	/**
	 * Formats a custom field's name into an environment variable. 
	 * 
	 * @param name The name of the custom field
	 * @return Formatted name for a environment variable
	 */
	private String formatCustomFieldEnvironmentVariableName(String name) 
	{
		name = name.toUpperCase(); // uppercase
		name = name.trim(); // trim
		name = TESTLINK_TESTCASE_PREFIX + name; // add prefix
		name = name.replaceAll( "\\s+", "_" ); // replace white spaces
		return name;
	}
	
	/**
	 * Executes a test command for a given test case.
	 * 
	 * @param build Jenkins Build instance
	 * @param launcher Jenkins Build instance's launcher
	 * @param listener Jenkins Build instance's listener
	 * @param command Command to execute
	 * @return Integer representing the process exit code
	 */
	protected boolean executeTestCommand( 
		AbstractBuild<?, ?> build, 
		Launcher launcher, 
		BuildListener listener, 
		String command, 
		EnvVars envVars) 
	{
		
		boolean r = false;
		
		CommandInterpreter cmd = null;
		
		try
		{
			if ( launcher.isUnix() )
			{
				cmd = new Shell( command );
			}
			else
			{
				cmd = new BatchFile( command );
			}
			
			r = cmd.execute( build, launcher, listener, envVars );
		}  
        catch (InterruptedException e) 
        {
        	e.printStackTrace( listener.fatalError(Messages.TestLinkBuilder_TestCommandError(e.getMessage())) );
        	r = false;
        } 
		
		return r;

	}

	/**
	 * Retrieves the object that contains all the test report files patterns.
	 * 
	 * @return report files patterns object.
	 * @see ReportFilesPatterns
	 */
	protected ReportFilesPatterns getReportPatterns()
	{
		final ReportFilesPatterns reportFilesPatterns = new ReportFilesPatterns();
		
		reportFilesPatterns.setJunitXmlReportFilesPattern( this.junitReportFilesPattern );
		reportFilesPatterns.setTestNGXmlReportFilesPattern( this.testNGReportFilesPattern );
		reportFilesPatterns.setTapStreamReportFilesPattern( this.tapReportFilesPattern );
		
		return reportFilesPatterns;
	}
	
}
