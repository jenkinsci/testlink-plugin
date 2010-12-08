/**
 * 
 */
package hudson.plugins.testlink.executor;

import hudson.AbortException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Launcher.ProcStarter;
import hudson.Util;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.util.ArgumentListBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.TestLinkAPIException;
import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionType;
import br.eti.kinoshita.testlinkjavaapi.model.ResponseDetails;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.model.TestProject;

/**
 * @author Bruno P. Kinoshita
 */
public class AutomatedTestExecutor 
{

	protected BuildListener listener;
	protected AbstractBuild<?, ?> hudsonBuild;
	protected Launcher launcher;
	protected TestLinkAPI api;

	protected String[] customFieldsNames;
	protected Map<String,String> hudsonBuildEnvironmentVariables;
	protected Boolean transactional;
	protected String testCommand;

	private List<TestCase> automatedTestCases;
	private Boolean failure;
	
	// Environment Variables names.
	private static final String TESTLINK_TESTCASE_PREFIX = "TESTLINK.TESTCASE.";
	private static final String TESTLINK_TESTCASE_ID_ENVVAR = "TESTLINK.TESTCASE.ID";
	private static final String TESTLINK_TESTCASE_NAME_ENVVAR = "TESTLINK.TESTCASE.NAME";
	private static final String TESTLINK_TESTCASE_TESTSUITE_ID_ENVVAR = "TESTLINK.TESTCASE.TESTSUITEID";
	private static final String TESTLINK_TESTCASE_TESTPROJECT_ID = "TESTLINK.TESTCASE.TESTPROJECTID";
	private static final String TESTLINK_TESTCASE_AUTHOR_ENVVAR = "TESTLINK.TESTCASE.AUTHOR";
	private static final String TESTLINK_TESTCASE_SUMMARY_ENVVAR = "TESTLINK.TESTCASE.SUMMARY";
	private static final String TESTLINK_BUILD_NAME_ENVVAR = "TESTLINK.BUILD.NAME";
	private static final String TESTLINK_TESTPLAN_NAME_ENVVAR = "TESTLINK.TESTPLAN.NAME";
	private static final String TESTLINK_TESTPROJECT_NAME_ENVVAR = "TESTLINK.TESTPROJECT.NAME";

	private static final String TEMPORARY_FILE_PREFIX = "testlink_temporary_";
	
	/**
	 * The name of the Test Project. When the job is executed this property is 
	 * used to build the TestProject object from TestLink using the 
	 * Java API.
	 */
	private String testProjectName;
	
	/**
	 * The name of the Test Plan. When the job is executed this property is 
	 * used to build the TestPlan object from TestLink using the 
	 * Java API.
	 */
	private String testPlanName;
	
	/**
	 * The name of the Build. If the job is using SVN latest revision this 
	 * property may change during the job execution.
	 */
	private String buildName;
	
	/**
	 * The Id of the Test Project.
	 */
	private TestProject testProject;
	
	/**
	 * The Test Plan.
	 */
	private TestPlan testPlan;
	
	/**
	 * The Build used in the tests. The Build is created automatically 
	 * in case the Build does not exist in TestLink yet.
	 */
	private Build build;
	
	/**
	 * The default note that TestLink plug-in adds to the Build if this is  
	 * created by the plug-in. It does not updates the Build Note if it is 
	 * existing and created by someone else.
	 */
	private static final String BUILD_NOTES = "Build created automatically with Hudson TestLink plug-in.";
	private static final String WINDOWS_SCRIPT_EXTENSION = ".bat";
	private static final String UNIX_SCRIPT_EXTENSION = ".sh";
	private static final int READ_WRITE_PERMISSION = 777;
	private static final String WINDOWS_SCRIPT_HEADER = "@ECHO OFF";

	public AutomatedTestExecutor(
			BuildListener listener, 
			AbstractBuild<?, ?> hudsonBuild, 
			Launcher launcher, 
			TestLinkAPI api, 
			String testPlanName, 
			String testProjectName,
			String buildName, 
			String[] customFieldsNames, 
			Map<String,String> hudsonBuildEnvironmentVariables, 
			Boolean transactional, 
			String testCommand)
	{
		this.listener = listener;
		this.hudsonBuild = hudsonBuild;
		this.launcher = launcher;
		this.api = api;
		this.testPlanName = testPlanName;
		this.testProjectName = testProjectName;
		this.buildName = buildName;
		this.customFieldsNames = customFieldsNames;
		this.hudsonBuildEnvironmentVariables = hudsonBuildEnvironmentVariables;
		this.transactional = transactional;
		this.testCommand = testCommand;

		this.automatedTestCases = new ArrayList<TestCase>();
		failure = Boolean.FALSE;
	}
	
	public TestProject getTestProject() {
		return testProject;
	}

	public TestPlan getTestPlan() {
		return testPlan;
	}

	public Build getBuild() {
		return build;
	}

	/* (non-Javadoc)
	 * @see hudson.FilePath.FileCallable#invoke(java.io.File, hudson.remoting.VirtualChannel)
	 */
	public List<TestCase> executeAutomatedTests () 
	throws IOException, InterruptedException
	{

		// Retrieve TCs and filter for automated test cases
		listener.getLogger().println("Retrieving list of automated test cases");

		int numberOfExecutedTestCases = 0;
		
		try 
		{
			// Create objects TestPlan, TestProject and Build
			this.updateTestLinkData();

			final TestCase[] testCases = this.api.getTestCasesForTestPlan(
					this.testPlan.getId(), null, null, null, null, null, null, null, 
					ExecutionType.AUTOMATED, null);
			
			for (int i = 0; i < testCases.length; i++) 
			{
				testCases[i].setTestProjectId( this.testProject.getId() );
				
				if ( this.failure && this.transactional )
				{
					listener.getLogger().println("A test failed in transactional execution. Skiping tests.");
					testCases[i].setExecutionStatus(ExecutionStatus.BLOCKED);
				} 
				else
				{
					numberOfExecutedTestCases += 1;
					
					// Retrieve list of custom fields for TC
					listener.getLogger().println("Automated test case found");
					listener.getLogger().println("Retrieving list of custom fields for test case");
					this.retrieveListOfCustomFields( testCases[i] );

					final EnvVars buildEnvironmentVariables = this.buildEnvironmentVariables( testCases[i] ); 
					hudsonBuild.getEnvironment(listener).putAll(buildEnvironmentVariables);
					Integer exitCode = this.executeTestCommand ( buildEnvironmentVariables, testCases[i] );
					if ( exitCode != 0 )
					{
						this.failure = true;
					}
					
				}
				
				this.automatedTestCases.add(testCases[i]);
			}
			
		} 
		catch (TestLinkAPIException e) 
		{
			e.printStackTrace( listener.getLogger() );
			throw new AbortException("Failed to execute automated test cases: " + e.getMessage());	
		}
		
		this.printSummaryOfExecution( automatedTestCases.size(), numberOfExecutedTestCases );

		return this.automatedTestCases;
	}

	/**
	 * Retrieves the details about Test Project, Test Pland and Build from 
	 * TestLink using TestLink Java API.
	 */
	protected void updateTestLinkData() 
	throws TestLinkAPIException
	{
		// TestLink details (project, plan, build).
		listener.getLogger().println("Retrieving TestLink details about " +
				"Test Project, Test Plan and Build.");
		TestProject testProject = this.api.getTestProjectByName( this.testProjectName );
		this.testProject = testProject;
		
		TestPlan testPlan = 
			this.api.getTestPlanByName(this.testPlanName, this.testProjectName );
		
		this.testPlan = testPlan;
		
		// Creating Build or Retrieving existing one
		Build build = this.api.createBuild(
				this.testPlan.getId(), 
				this.buildName, 
				BUILD_NOTES);
		this.build =  build;
		
	}
	
	/**
	 * @param testCase
	 */
	protected void retrieveListOfCustomFields( TestCase testCase ) 
	{
		if ( this.customFieldsNames != null && customFieldsNames.length > 0 )
		{
			for (int i = 0; i < customFieldsNames.length; i++)
			{
				String customFieldName = customFieldsNames[i];

				try
				{
					CustomField customField = this.api.getTestCaseCustomFieldDesignValue(
							testCase.getId(), 
							null, 
							testCase.getVersion(), 
							testCase.getTestProjectId(), 
							customFieldName, 
							ResponseDetails.FULL);

					testCase.getCustomFields().add( customField );
				} 
				catch (TestLinkAPIException e)
				{
					listener.getLogger().println("Failed to retrieve custom field " + customFieldName + " for Test Case " + testCase.toString());
					e.printStackTrace( listener.getLogger() );
				}

			}
		}
	}

	/**
	 * @param testCase
	 * @return Environment Vars
	 */
	protected EnvVars buildEnvironmentVariables( TestCase testCase ) 
	{
		// Build environment variables list
		listener.getLogger().println("Creating list of environment variables for test case execution");
		Map<String, String> testLinkEnvironmentVariables = this.createTestLinkEnvironmentVariables( testCase );

		// Merge with build environment variables list
		listener.getLogger().println("Merging build environment variables with TestLink environment variables");
		hudsonBuildEnvironmentVariables.putAll( testLinkEnvironmentVariables );

		final EnvVars buildEnvironment = new EnvVars( hudsonBuildEnvironmentVariables );
		return buildEnvironment;
	}

	/**
	 * @param testCase
	 * @return Map
	 */
	protected Map<String, String> createTestLinkEnvironmentVariables( TestCase testCase ) 
	{
		Map<String, String> testLinkEnvVar = new HashMap<String, String>();
		
		testLinkEnvVar.put( TESTLINK_TESTCASE_ID_ENVVAR, ""+testCase.getId() );
		testLinkEnvVar.put( TESTLINK_TESTCASE_NAME_ENVVAR, ""+testCase.getName() );
		testLinkEnvVar.put( TESTLINK_TESTCASE_TESTSUITE_ID_ENVVAR, ""+testCase.getTestSuiteId() );
		testLinkEnvVar.put( TESTLINK_TESTCASE_TESTPROJECT_ID, ""+testCase.getTestProjectId() );
		testLinkEnvVar.put( TESTLINK_TESTCASE_AUTHOR_ENVVAR, ""+testCase.getAuthorLogin() );
		testLinkEnvVar.put( TESTLINK_TESTCASE_SUMMARY_ENVVAR, testCase.getSummary() );
		testLinkEnvVar.put( TESTLINK_BUILD_NAME_ENVVAR, this.build.getName() );
		testLinkEnvVar.put( TESTLINK_TESTPLAN_NAME_ENVVAR, this.testPlan.getName() );
		testLinkEnvVar.put( TESTLINK_TESTPROJECT_NAME_ENVVAR, this.testProject.getName() );
		
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
	 * @param name
	 * @return
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
	 * @param buildEnvironmentVariables
	 * @param testCase
	 */
	protected Integer executeTestCommand( 
		EnvVars buildEnvironmentVariables,
		TestCase testCase) 
	{
		
		int exitCode = -1;

		if ( this.transactional && failure )
		{
			testCase.setExecutionStatus(ExecutionStatus.BLOCKED);
		} 
		else 
		{ 
			FilePath temporaryExecutableScript = null;
			
			try
			{
				temporaryExecutableScript = this.createTemporaryExecutableScript();
				ArgumentListBuilder args = new ArgumentListBuilder();
				args.add(new File(temporaryExecutableScript.getRemote() ).getAbsolutePath());
				if ( ! launcher.isUnix() )
				{
					args.add("&&","exit","%%ERRORLEVEL%%");
				}
	            ProcStarter ps = launcher.launch();
	            ps.envs( buildEnvironmentVariables );
	            ps.cmds( args );
	            ps.stdout( listener );
	            ps.pwd( hudsonBuild.getModuleRoot() ); 
	            
	            listener.getLogger().println("Executing test command");
	            exitCode = ps.join();
			}  
			catch (IOException e)
	        {
	            Util.displayIOException(e,listener);
	            e.printStackTrace( listener.fatalError("Test command execution failed") );
	            failure = true;
	        } 
	        catch (InterruptedException e) 
	        {
	        	e.printStackTrace( listener.fatalError("Test command execution failed") );
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
						e.printStackTrace( listener.error("Error deleting temporary script " + temporaryExecutableScript) );
					} 
					catch (InterruptedException e) 
					{
						e.printStackTrace( listener.error("Error deleting temporary script " + temporaryExecutableScript) );
					}
				}
	        }
	        
		}	
		
		return exitCode;

	}
	
	/**
	 * Creates a temporary executable script with the test command inside it.
	 * 
	 * @return FilePath object referring to the temporary executable script.
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	protected FilePath createTemporaryExecutableScript() 
	throws IOException, InterruptedException
	{
		FilePath temporaryExecutableScript = null;
		FileWriter fileWriter = null;
		String temporaryFileSuffix = WINDOWS_SCRIPT_EXTENSION; 
		if ( launcher.isUnix() ) 
		{
			temporaryFileSuffix = UNIX_SCRIPT_EXTENSION; 
		}
		
		temporaryExecutableScript = 
			hudsonBuild.getWorkspace().createTempFile(TEMPORARY_FILE_PREFIX, temporaryFileSuffix);
		temporaryExecutableScript.chmod(READ_WRITE_PERMISSION); 
		File remoteFile = new File( temporaryExecutableScript.getRemote() );
		
		try
		{
			fileWriter = new FileWriter( remoteFile );
			
			if ( ! launcher.isUnix() )
			{
				fileWriter.write( WINDOWS_SCRIPT_HEADER + System.getProperty("line.separator") );
			}
			
			fileWriter.write( this.testCommand );
			fileWriter.flush();
		}
		finally 
		{
			fileWriter.close();
		}    	
    	
    	return temporaryExecutableScript;
	}
	
	/**
	 * @param numberOfAutomatedTestCases
	 * @param numberOfExecutedTestCases 
	 */
	protected void printSummaryOfExecution(int numberOfAutomatedTestCases, int numberOfExecutedTestCases) 
	{
		if ( numberOfAutomatedTestCases <= 0 )
		{
			listener.getLogger().println("No automated tests found");
		} 
		else
		{
			listener.getLogger().println("Executed " + numberOfExecutedTestCases +" automated test(s) out of " + numberOfAutomatedTestCases + " test(s)");
		}
	}

}
