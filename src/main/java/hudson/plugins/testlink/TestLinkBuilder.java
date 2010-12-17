/**
 *	 __                                        
 *	/\ \      __                               
 *	\ \ \/'\ /\_\    ___     ___   __  __  __  
 *	 \ \ , < \/\ \ /' _ `\  / __`\/\ \/\ \/\ \ 
 *	  \ \ \\`\\ \ \/\ \/\ \/\ \L\ \ \ \_/ \_/ \
 *	   \ \_\ \_\ \_\ \_\ \_\ \____/\ \___x___/'
 *	    \/_/\/_/\/_/\/_/\/_/\/___/  \/__//__/  
 *                                          
 * Copyright (c) 1999-present Kinow
 * Casa Verde - São Paulo - SP. Brazil.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Kinow ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Kinow.                                      
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 30/08/2010
 */
package hudson.plugins.testlink;

import hudson.AbortException;
import hudson.CopyOnWrite;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Launcher.ProcStarter;
import hudson.Util;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.plugins.testlink.executor.TemporaryExecutableScriptWriter;
import hudson.plugins.testlink.finder.AutomatedTestCasesFinder;
import hudson.plugins.testlink.model.ReportFilesPatterns;
import hudson.plugins.testlink.model.TestLinkLatestRevisionInfo;
import hudson.plugins.testlink.model.TestLinkReport;
import hudson.plugins.testlink.model.TestResult;
import hudson.plugins.testlink.parser.JUnitParser;
import hudson.plugins.testlink.parser.Parser;
import hudson.plugins.testlink.parser.TAPParser;
import hudson.plugins.testlink.parser.TestNGParser;
import hudson.plugins.testlink.svn.SVNLatestRevisionService;
import hudson.plugins.testlink.updater.TestLinkTestStatusUpdater;
import hudson.tasks.Builder;
import hudson.util.ArgumentListBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.tmatesoft.svn.core.SVNException;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
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
	
	/**
	 * The instance of the TestLink API client.
	 */
	private TestLinkAPI api = null;

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

	private static final String TEMPORARY_FILE_PREFIX = "testlink_temporary_";
		
	/**
	 * The default note that TestLink plug-in adds to the Build if this is  
	 * created by the plug-in. It does not updates the Build Note if it is 
	 * existing and created by someone else.
	 */
	//private static final String BUILD_NOTES = "Build created automatically with Hudson TestLink plug-in.";
	private static final String WINDOWS_SCRIPT_EXTENSION = ".bat";
	private static final String UNIX_SCRIPT_EXTENSION = ".sh";
	private static final int READ_WRITE_PERMISSION = 0777;
	
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
	 * <p>After this step the output of the tests is parsed by {@link Parser}</p>
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
		
		try 
		{
			this.api = 
				new TestLinkAPI( installation.getUrl(), installation.getDevKey() );
			listener.getLogger().println( Messages.TestLinkBuilder_UsedTLURL(installation.getUrl()) );
		} 
		catch (MalformedURLException mue) 
		{
			final String message = Messages.TestLinkBuilder_InvalidTLURL( installation.getUrl() );
			listener.fatalError( message );
			throw new AbortException( message );
		}
		
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
			} 
			catch (SVNException e)
			{
				e.printStackTrace( listener.fatalError(Messages.TestLinkBuilder_SVNError(e.getMessage())) );
				throw new AbortException();
			}
		}
		
		final String[] customFieldsNames = this.getListOfCustomFieldsNames();
		
		final AutomatedTestCasesFinder finder = new AutomatedTestCasesFinder(
				listener, 
				this.api,
				customFieldsNames, 
				testProjectName, 
				testPlanName, 
				buildName);
		
		List<TestCase> automatedTestCases = null;
		try
		{
			automatedTestCases = finder.findAutomatedTestCases();
		} 
		catch (TestLinkAPIException e)
		{
			e.printStackTrace( listener.fatalError(e.getMessage()) );
			throw new AbortException(e.getMessage());
		}
		
		// Execute single test command
		listener.getLogger().println(Messages.TestLinkBuilder_ExecutingSingleTestCommand());
		final EnvVars singleTestEnvironmentVariables = new EnvVars();
		Integer singleTestCommandExitCode = this.executeTestCommand( 
				singleTestEnvironmentVariables, 
				build, 
				launcher, 
				listener, 
				singleTestCommand);
		if ( singleTestCommandExitCode != 0 )
		{
			this.failure = true;
		}
		
		for ( TestCase automatedTestCase : automatedTestCases )
		{
			if ( this.failure  && this.transactional )
			{
				listener.getLogger().println(Messages.TestLinkBuilder_TransactionalError());
				automatedTestCase.setExecutionStatus(ExecutionStatus.BLOCKED);
			} 
			else
			{
				final EnvVars buildEnvironmentVariables = this.buildEnvironmentVariables( automatedTestCase, listener, finder.getTestProject(), finder.getTestPlan(), finder.getBuild() ); 
				//build.getEnvironment(listener).putAll(buildEnvironmentVariables);
				buildEnvironmentVariables.putAll( build.getEnvironment( listener ) );
				final Integer iterativeTestCommandExitCode = this.executeTestCommand( 
						buildEnvironmentVariables, 
						build, 
						launcher, 
						listener, 
						iterativeTestCommand);
				if ( iterativeTestCommandExitCode != 0 )
				{
					this.failure = true;
				}
				
			}
		}
		
		// Create list of report files patterns
		final ReportFilesPatterns reportFilesPatterns = this.getReportPatterns();
		
		// Create report object
		final TestLinkReport report = new TestLinkReport();
		report.setBuild( finder.getBuild() );
		report.setTestPlan( finder.getTestPlan() );
		report.setTestProject( finder.getTestProject() );
		report.getTestCases().addAll ( automatedTestCases );
		
		// Create the parsers
		final List<Parser> parsers = 
			this.createListOfParsers( report, reportFilesPatterns, listener );
		
		// Create list of test results
		final List<TestResult> testResults = new ArrayList<TestResult>();
		
		// Extract test results using parsers	
		for( final Parser parser : parsers )
		{
			// only call the parser if it is enabled
			if ( parser.isEnabled() )
			{
				try
				{
					TestResult[] foundResults = null;
					
					foundResults = build.getWorkspace().act( parser );
					
					if ( foundResults != null && foundResults.length > 0   )
					{
						listener.getLogger().println(Messages.TestLinkBuilder_ShowFoundTestResults(foundResults.length) );
						for ( int i = 0 ; i < foundResults.length ; ++i )
						{
							if ( foundResults[i] != null )
							{
								testResults.add( foundResults[i] );
							}
						}
					} 
					else
					{
						listener.getLogger().println( Messages.TestLinkBuilder_NoTestResultsFound() );
					}
				}
				catch (IOException e)
				{
					listener.getLogger().println( Messages.TestLinkBuilder_FailedToOpenReportFile() );
					e.printStackTrace( listener.getLogger() );
				} 
			}
		}
		
		// Add blocked tests to the test results list
		for( TestCase testCase : automatedTestCases )
		{
			if ( testCase.getExecutionStatus() == ExecutionStatus.BLOCKED )
			{
				TestResult blockedTestResult = new TestResult(testCase, finder.getBuild(), finder.getTestPlan());
				testResults.add( blockedTestResult );
			}
		}
		
		// Update TestLink with test results
		final TestLinkTestStatusUpdater updater = new TestLinkTestStatusUpdater();
		
		try 
		{
			updater.updateTestCases(api, listener.getLogger(), testResults);
		} 
		catch (TestLinkAPIException tlae) 
		{
			tlae.printStackTrace( listener.fatalError( Messages.TestLinkBuilder_FailedToUpdateTL(tlae.getMessage()) ) );
			throw new AbortException ( tlae.getMessage() );
		}
		
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
		
		if( ! StringUtils.isEmpty( customFields ) )
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
	 * 
	 * @param testCase
	 * @param listener
	 * @param testProject
	 * @param testPlan
	 * @param build
	 * @return
	 */
	protected EnvVars buildEnvironmentVariables( TestCase testCase, BuildListener listener, TestProject testProject, TestPlan testPlan, Build build ) 
	{
		// Build environment variables list
		listener.getLogger().println(Messages.TestLinkBuilder_CreatingEnvVars());
		Map<String, String> testLinkEnvironmentVariables = this.createTestLinkEnvironmentVariables( testCase, testProject, testPlan, build );

		// Merge with build environment variables list
		listener.getLogger().println(Messages.TestLinkBuilder_MergingEnvVars());

		final EnvVars buildEnvironment = new EnvVars( testLinkEnvironmentVariables );
		return buildEnvironment;
	}
	
	/**
	 * 
	 * @param testCase
	 * @param testProject
	 * @param testPlan
	 * @param build
	 * @return
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
	 * @param buildEnvironmentVariables Map of Environment Variables
	 * @param testCase Test Case
	 * @param hudsonBuild Hudson Build instance
	 * @param launcher Hudson Build instance's launcher
	 * @param listener Hudson Build instance's listener
	 * @param command Command to execute
	 * @return Integer representing the process exit code
	 */
	protected Integer executeTestCommand( 
		EnvVars buildEnvironmentVariables,
		AbstractBuild<?, ?> hudsonBuild, 
		Launcher launcher, 
		BuildListener listener, 
		String command) 
	{
		
		int exitCode = -1;

		FilePath temporaryExecutableScript = null;
		
		try
		{
			temporaryExecutableScript = this.createTemporaryExecutableScript( hudsonBuild, launcher, command );
			ArgumentListBuilder args = new ArgumentListBuilder();
			args.add( temporaryExecutableScript.getRemote() );
			if ( ! launcher.isUnix() )
			{
				args.add("&&","exit","%%ERRORLEVEL%%");
			}
            ProcStarter ps = launcher.launch();
            ps.envs( buildEnvironmentVariables );
            ps.cmds( args );
            ps.stdout( listener );
            ps.pwd( hudsonBuild.getModuleRoot() ); 
            
            listener.getLogger().println(Messages.TestLinkBuilder_ExecutingIterativeTestCommand());
            //exitCode = ps.join();
            exitCode = launcher.launch( ps ).join();
		}  
		catch (IOException e)
        {
            Util.displayIOException(e,listener);
            e.printStackTrace( listener.fatalError(Messages.TestLinkBuilder_TestCommandError(e.getMessage())) );
            failure = true;
        } 
        catch (InterruptedException e) 
        {
        	e.printStackTrace( listener.fatalError(Messages.TestLinkBuilder_TestCommandError(e.getMessage())) );
        	failure = true;
        } 
        // Destroy temporary file.
        finally 
        {
        	if ( temporaryExecutableScript != null )
			{
				try 
				{
					temporaryExecutableScript.delete();
				} 
				catch (IOException e)
				{
					e.printStackTrace( listener.fatalError(Messages.TestLinkBuilder_DeleteTempArchiveError(temporaryExecutableScript)) );
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace( listener.fatalError(Messages.TestLinkBuilder_DeleteTempArchiveError(temporaryExecutableScript)) );
				}
			}
        }

		
		return exitCode;

	}
	
	/**
	 * Creates a temporary executable script with the test command inside it
	 * 
	 * @param hudsonBuild
	 * @param launcher
	 * @param command
	 * @return FilePath object referring to the temporary executable script.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	protected FilePath createTemporaryExecutableScript( 
			AbstractBuild<?, ?> hudsonBuild, 
			Launcher launcher, 
			String command ) 
	throws IOException, InterruptedException
	{
		FilePath temporaryExecutableScript = null;
		
		String temporaryFileSuffix = WINDOWS_SCRIPT_EXTENSION; 
		if ( launcher.isUnix() ) 
		{
			temporaryFileSuffix = UNIX_SCRIPT_EXTENSION; 
		}
		
		temporaryExecutableScript = 
			hudsonBuild.getWorkspace().createTempFile(TEMPORARY_FILE_PREFIX, temporaryFileSuffix);
		//temporaryExecutableScript.chmod(READ_WRITE_PERMISSION); 
		temporaryExecutableScript.chmod(READ_WRITE_PERMISSION);
		
		TemporaryExecutableScriptWriter scriptCreator = 
			new TemporaryExecutableScriptWriter(
					temporaryExecutableScript.getRemote(), 
					launcher.isUnix(), 
					command );
		
		hudsonBuild.getWorkspace().act( scriptCreator );
		    	
    	return temporaryExecutableScript;
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
	
	/**
	 * Creates list of parsers.
	 * 
	 * @param report TestLink Plugin Report object
	 * @param reportFilesPatterns Report files patterns
	 * @param ps Print Steam to log events.
	 * @return List of Parsers
	 */
	private List<Parser> createListOfParsers(
			TestLinkReport report, 
			ReportFilesPatterns reportFilesPatterns, 
			BuildListener listener) 
	{
		List<Parser> parsers = new ArrayList<Parser>();
		final Parser junitParser = new JUnitParser( 
				report, 
				keyCustomField,
				listener, 
				reportFilesPatterns.getJunitXmlReportFilesPattern() );
		final Parser testNGParser = new TestNGParser(
				report, 
				keyCustomField, 
				listener, 
				reportFilesPatterns.getTestNGXmlReportFilesPattern() );
		final Parser tapParser = new TAPParser( 
				report, 
				keyCustomField, 
				listener, 
				reportFilesPatterns.getTapStreamReportFilesPattern() );
		parsers.add( junitParser );
		parsers.add( testNGParser );
		parsers.add( tapParser );
		return parsers;
	}
	
}
