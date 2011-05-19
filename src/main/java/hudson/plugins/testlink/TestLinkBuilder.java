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
import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.Util;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.plugins.testlink.result.ReportFilesPatterns;
import hudson.plugins.testlink.result.TestCaseWrapper;
import hudson.plugins.testlink.result.TestLinkReport;
import hudson.plugins.testlink.result.TestResultSeekerException;
import hudson.plugins.testlink.result.TestResultsCallable;
import hudson.plugins.testlink.tasks.BatchFile;
import hudson.plugins.testlink.tasks.CommandInterpreter;
import hudson.plugins.testlink.tasks.Shell;
import hudson.plugins.testlink.util.ExecutionOrderComparator;
import hudson.plugins.testlink.util.Messages;
import hudson.tasks.Builder;
import hudson.util.VariableResolver;

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
	 * Report files patterns.
	 */
	private final ReportFilesPatterns reportFilesPatterns;
	
	/* --- Object properties --- */
	
	/**
	 * Flag to check if any failure happened.
	 */
	private boolean failure = false;
	
	/**
	 * Used to sort test cases marked as automated.
	 */
	private final ExecutionOrderComparator executionOrderComparator = new ExecutionOrderComparator();
	
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
	 * @param keyCustomField Automated Test Case key custom field. 
	 * @param junitXmlReportFilesPattern Pattern for JUnit report files.
	 * @param testNGXmlReportFilesPattern Pattern for TestNG report files.
	 * @param tapStreamReportFilesPattern Pattern for TAP report files.
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
		String keyCustomField, 
		String junitXmlReportFilesPattern, 
		String testNGXmlReportFilesPattern, 
		String tapStreamReportFilesPattern
	)
	{
		this.testLinkName = testLinkName;
		this.testProjectName = testProjectName;
		this.testPlanName = testPlanName;
		this.buildName = buildName;
		this.customFields = customFields;
		this.singleTestCommand = singleTestCommand;
		this.iterativeTestCommand = iterativeTestCommand;
		this.transactional = transactional;
		this.keyCustomField = keyCustomField;
		
		this.reportFilesPatterns = new ReportFilesPatterns(
				junitXmlReportFilesPattern, 
				testNGXmlReportFilesPattern, 
				tapStreamReportFilesPattern);
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
		
		this.failure = false;
		
		final TestLinkHandler testLinkHandler = 
			this.createTestLinkHandler( 
				getTestProjectName(), 
				getTestPlanName(),
				expandBuildName( build.getBuildVariableResolver(), build.getEnvironment(listener) ), 
				Messages.TestLinkBuilder_Build_Notes(), 
				listener 
			);
		
		final String[] customFieldsNames 	= createarrayOfCustomFieldsNames();
		
		final TestCase[] automatedTestCases;
		
		try
		{
			automatedTestCases = testLinkHandler.retrieveAutomatedTestCasesWithCustomFields( customFieldsNames );
		} 
		catch (TestLinkAPIException e)
		{
			e.printStackTrace( listener.fatalError(e.getMessage()) );
			throw new AbortException( Messages.TestLinkBuilder_TestLinkCommunicationError() );
		}
		
		// Sorts test cases by each execution order (this info comes from TestLink)
		this.sortAutomatedTestCases( automatedTestCases, listener );
		
		// Execute single test command
		this.executeSingleTestCommand( build, launcher.isUnix(), listener );
		
		// Execute iterative test command for each automated test case
		this.executeIterativeTestCommand( 
			automatedTestCases, 
			testLinkHandler.getTestProject(),
			testLinkHandler.getTestPlan(), 
			testLinkHandler.getBuild(), 
			build, 
			launcher.isUnix(), 
			listener );
		
		// This report is used to generate the graphs and to store the list of 
		// test cases with each found status.
		final TestLinkReport report = this.createReport(
				testLinkHandler.getBuild(), 
				testLinkHandler.getTestPlan(), 
				testLinkHandler.getTestProject(), 
				automatedTestCases);
		
		// The object that searches for test results
		final TestResultsCallable testResultSeeker = 
			new TestResultsCallable(report, this.keyCustomField, reportFilesPatterns, listener);

		final Set<TestCaseWrapper> wrappedTestCases;
		
		// Here we search for test results. The return if a wrapped Test Case that 
		// contains attachments, platform and notes.
		try
		{
			wrappedTestCases = build.getWorkspace().act( testResultSeeker );
		}
		catch ( TestResultSeekerException trse )
		{
			trse.printStackTrace( listener.fatalError( trse.getMessage() ) );
			throw new AbortException(Messages.Results_ErrorToLookForTestResults( trse.getMessage() ));
		}
		
		report.verifyBlockedTestCases( wrappedTestCases );
		
		report.updateReport( wrappedTestCases );
		
		// Update TestLink with test results and uploads attachments
		try 
		{
			testLinkHandler.updateTestCasesAndUploadAttachments( wrappedTestCases );
		} 
		catch (TestLinkAPIException tlae) 
		{
			tlae.printStackTrace( listener.fatalError( Messages.TestLinkBuilder_FailedToUpdateTL(tlae.getMessage()) ) );
			throw new AbortException ( Messages.TestLinkBuilder_FailedToUpdateTL(tlae.getMessage()) );
		}
		
		final TestLinkResult result = new TestLinkResult(report, build);
        final TestLinkBuildAction buildAction = new TestLinkBuildAction(build, result);
        
        build.addAction( buildAction );
		
		// end
		return Boolean.TRUE;
	}
	
	protected TestLinkHandler createTestLinkHandler( 
			String testProjectName, 
			String testPlanName, 
			String buildName, 
			String buildNotes, 
			BuildListener listener ) 
	throws AbortException
	{
		final TestLinkHandler testLinkHandler;
		
		// TestLink installation.
		listener.getLogger().println( Messages.TestLinkBuilder_PreparingTLAPI() );
		
		final TestLinkInstallation installation = 
			DESCRIPTOR.getInstallationByTestLinkName( this.testLinkName );
		if ( installation == null )
		{
			throw new AbortException( Messages.TestLinkBuilder_InvalidTLAPI() );
		}
		
		listener.getLogger().println ( Messages.TestLinkBuilder_UsedTLURL( installation.getUrl()) );
		listener.getLogger().println();
		
		this.setTestLinkJavaAPIProperties( installation.getTestLinkJavaAPIProperties(), listener );
		
		final String testLinkUrl = installation.getUrl();
		final String testLinkDevKey = installation.getDevKey();
		
		try
		{
			final URL url = new URL( testLinkUrl );
			testLinkHandler = new TestLinkHandler( url, testLinkDevKey, testProjectName, testPlanName, buildName, buildNotes, listener );
		}
		catch (MalformedURLException mue) 
		{
			throw new AbortException( Messages.TestLinkBuilder_InvalidTLURL( testLinkUrl) );
		}
		
		return testLinkHandler;
	}
	
	/**
	 * <p>Defines TestLink Java API Properties. Following is the list of available 
	 * properties.</p>
	 * 
	 * <ul>
	 *  	<li>xmlrpc.basicEncoding</li>
 	 *  	<li>xmlrpc.basicPassword</li>
 	 *  	<li>xmlrpc.basicUsername</li>
 	 *  	<li>xmlrpc.connectionTimeout</li>
 	 *  	<li>xmlrpc.contentLengthOptional</li>
 	 *  	<li>xmlrpc.enabledForExceptions</li>
 	 *  	<li>xmlrpc.encoding</li>
 	 *  	<li>xmlrpc.gzipCompression</li>
 	 *  	<li>xmlrpc.gzipRequesting</li>
 	 *  	<li>xmlrpc.replyTimeout</li>
 	 *  	<li>xmlrpc.userAgent</li>
	 * </ul>
	 * 
	 * @param testLinkJavaAPIProperties
	 * @param listener Jenkins Build listener
	 */
	private void setTestLinkJavaAPIProperties( String testLinkJavaAPIProperties, BuildListener listener )
	{
		if ( StringUtils.isNotBlank( testLinkJavaAPIProperties ) )
		{
			final StringTokenizer tokenizer = new StringTokenizer( testLinkJavaAPIProperties, "," );
			
			if ( tokenizer.countTokens() > 0 )
			{
				while ( tokenizer.hasMoreTokens() )
				{
					String systemProperty = tokenizer.nextToken();
					this.maybeAddSystemProperty( systemProperty, listener );
				}
			}
		}
	}
	
	/**
	 * Maybe adds a system property if it is in format <key>=<value>.
	 * 
	 * @param systemProperty System property entry in format <key>=<value>.
	 * @param listener Jenkins Build listener
	 */
	private void maybeAddSystemProperty( String systemProperty, BuildListener listener )
	{
		final StringTokenizer tokenizer = new StringTokenizer( systemProperty, "=:");
		if ( tokenizer.countTokens() == 2 )
		{
			final String key 	= tokenizer.nextToken();
			final String value	= tokenizer.nextToken();
			
			if ( StringUtils.isNotBlank( key ) && StringUtils.isNotBlank( value ) )
			{
				if ( key.contains("basicPassword"))
				{
					listener.getLogger().println( Messages.TestLinkBuilder_SettingSystemProperty(key, "********") );
				}
				else
				{
					listener.getLogger().println( Messages.TestLinkBuilder_SettingSystemProperty(key, value) );
				}
				try
				{
					System.setProperty(key, value);
				} 
				catch ( SecurityException se )
				{
					se.printStackTrace();
				}
			
			}
		}
	}

	/**
	 * Creates array of custom fields names using the Job configuration data.
	 * 
	 * @return Array of custom fields names.
	 */
	protected String[] createarrayOfCustomFieldsNames()
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
	
	protected void sortAutomatedTestCases( TestCase[] automatedTestCases, BuildListener listener )
	{
		listener.getLogger().println( Messages.TestLinkBuilder_SortingTestCases() );
		Arrays.sort( automatedTestCases, this.executionOrderComparator );
	}

	/**
	 * Executes single test command.
	 * 
	 * @param build Jenkins build.
	 * @param isUnix Whether it is being built on Windows or nix
	 * @param listener Jenkins build listener.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	protected void executeSingleTestCommand(AbstractBuild<?, ?> build, boolean isUnix, BuildListener listener ) 
	throws IOException, InterruptedException
	{
		if ( StringUtils.isNotBlank( singleTestCommand ) )
		{
			listener.getLogger().println( Messages.TestLinkBuilder_ExecutingSingleTestCommand( this.singleTestCommand ) );
			listener.getLogger().println();
			
			EnvVars envVars = build.getEnvironment( listener );
			
			boolean success = this.executeTestCommand( 
					build, 
					isUnix, 
					listener, 
					singleTestCommand, 
					envVars);
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
	 * @param isUnix Whether it is a Windows or a Unix environment.
	 * @param listener Jenkins Listener.
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	protected void executeIterativeTestCommand( TestCase[] automatedTestCases, TestProject project, TestPlan testPlan, Build testLinkBuild, AbstractBuild<?, ?> build, boolean isUnix, BuildListener listener ) 
	throws IOException, InterruptedException 
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
					
					iterativeEnvVars.putAll( build.getEnvironment( listener ) );
					
					listener.getLogger().println( Messages.TestLinkBuilder_ExecutingIterativeTestCommand( this.iterativeTestCommand ) );
					listener.getLogger().println();
					
					// Execute iterative test command
					final boolean success = this.executeTestCommand( 
							build, 
							isUnix, 
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
	 * Executes a test command for a given test case.
	 * 
	 * @param build Jenkins Build instance
	 * @param isUnix Whether it is being built on Windows or nix
	 * @param listener Jenkins Build instance's listener
	 * @param command Command to execute
	 * @return Integer representing the process exit code
	 */
	protected boolean executeTestCommand( 
		AbstractBuild<?, ?> build, 
		boolean isUnix,  
		BuildListener listener, 
		String command, 
		EnvVars envVars) 
	{
		
		boolean r = false;
		
		CommandInterpreter cmd = null;
		
		try
		{
			if ( isUnix )
			{
				cmd = new Shell( command, envVars, listener );
			}
			else
			{
				cmd = new BatchFile( command, envVars, listener );
			}
			
			r = build.getWorkspace().act( cmd );
		}  
        catch (InterruptedException e) 
        {
        	e.printStackTrace( listener.fatalError(Messages.TestLinkBuilder_TestCommandError(e.getMessage())) );
        	r = false;
        } catch (IOException e)
		{
        	e.printStackTrace( listener.fatalError(Messages.TestLinkBuilder_TestCommandError(e.getMessage())) );
        	r = false;
		} 
		
		return r;

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
	protected String formatCustomFieldEnvironmentVariableName(String name) 
	{
		name = name.toUpperCase(); // uppercase
		name = name.trim(); // trim
		name = TESTLINK_TESTCASE_PREFIX + name; // add prefix
		name = name.replaceAll( "\\s+", "_" ); // replace white spaces
		return name;
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
	
}
