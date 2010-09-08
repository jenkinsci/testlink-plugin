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

import hudson.CopyOnWrite;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.Launcher.ProcStarter;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.plugins.testlink.model.TestLinkParser;
import hudson.plugins.testlink.model.TestLinkTestCase;
import hudson.tasks.Builder;
import hudson.tasks.Maven;
import hudson.tasks.Maven.MavenInstallation;
import hudson.util.ArgumentListBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.util.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import testlink.api.java.client.TestLinkAPIClient;
import testlink.api.java.client.TestLinkAPIConst;
import testlink.api.java.client.TestLinkAPIException;
import testlink.api.java.client.TestLinkAPIResults;

/**
 * <p>
 * A builder to add a TestLink build step.
 * </p>
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 30/08/2010
 */
public class TestLinkBuilder 
extends Builder
{

	/**
	 * <p>The instance of the TestLink API client.</p> 
	 * 
	 * <p>Please refer to the API website for further information on it. 
	 * <a href="http://code.google.com/p/dbfacade-testlink-rpc-api/">
	 * http://code.google.com/p/dbfacade-testlink-rpc-api/</a></p>
	 */
	private TestLinkAPIClient testLinkClient = null;
	
	/**
	 * The name of the TestLink installation.
	 */
	private final String testLinkName;
	
	/**
	 * The name of the Test Project.
	 */
	private final String projectName;
	
	private Integer projectId;
	
	/**
	 * The name of the Test Plan.
	 */
	private final String testPlanName;
	
	private Integer testPlanId;

	/**
	 * The name of the Build.
	 */
	private final String buildName;
	
	/**
	 * The ID of the Build used in the tests. This ID is created automatically 
	 * in case the Build does not exist. This parameter is not passed by the 
	 * user. Hudson TestLink plug-in only stores its value after the Build 
	 * is created.
	 */
	private Integer buildId;
	
	/**
	 * Information related to the SVN that TestLink plug-in should use to 
	 * retrieve the latest revision.
	 */
	@CopyOnWrite
	private final TestLinkLatestRevisionInfo latestRevisionInfo;
	
	/**
	 * The directory of the Maven test Project. It can be absolute or relative 
	 * to the project Workspace.
	 */
	private final String mavenTestProjectDirectory;
	
	/**
	 * Maven installation name. It uses a cross plug-in reference to acess 
	 * the Maven installations from Hudson global configuration. 
	 * Thanks for sonar plug-in for the nice example.
	 */
	private final String mavenInstallationName;
	private final String mavenTestProjectGoal;
	
	private String mavenExecutable;
	
	private final Boolean transactional;
	
	/**
	 * The default maven goal to run.
	 */
	private static final String DEFAULT_MAVEN_GOAL = "test";
	
	/**
	 * The Descriptor of this Builder.
	 */
	@Extension 
	public static final TestLinkBuilderDescriptor DESCRIPTOR = new TestLinkBuilderDescriptor();

	/**
	 * The default note that TestLink plug-in adds to the Build if this is  
	 * created by the plug-in. It does not updates the Build Note if it is 
	 * existing and created by someone else.
	 */
	private static final String BUILD_NOTES = "Build created automatically with Hudson TestLink plug-in.";
	private static final String TEST_CASE_EXECUTION_NOTES = "Test executed by Hudson TestLink plug-in.";
	
	private String testCategoryCustomField = null;
	private String testFileCustomField = null;
	
	private String testCaseCategory;
	private String testSuiteCategory;
	
	/**
	 * This constructor is bound to a stapler request. All parameters here are 
	 * passed by Hudson.
	 * 
	 * @param testLinkName Name of the TestLink installation.
	 * @param projectName Name of the TestLink Test Project.
	 * @param testPlanName Name of the Test Plan.
	 * @param buildName Name of the Build.
	 * @param latestRevisionInfo Information bout the latest revistion from SVN.
	 * @param mavenTestProjectDirectory Directory of the Maven test project.
	 * @param mavenInstallationName Name of the Maven installation.
	 * @param mavenTestProjectGoal Goal that maven runs to execute the tests.
	 * @param transactional Whether this is a transactional build or not.
	 */
	@DataBoundConstructor
	public TestLinkBuilder(
		String testLinkName, 
		String projectName, 
		String testPlanName, 
		String buildName, 
		TestLinkLatestRevisionInfo latestRevisionInfo, 
		String mavenTestProjectDirectory, 
		String mavenInstallationName,
		String mavenTestProjectGoal, 
		Boolean transactional
	)
	{
		this.testLinkName = testLinkName;
		this.projectName = projectName;
		this.testPlanName = testPlanName;
		this.buildName = buildName;
		this.latestRevisionInfo = latestRevisionInfo;
		this.mavenTestProjectDirectory = mavenTestProjectDirectory;
		this.mavenInstallationName = mavenInstallationName;
		this.mavenTestProjectGoal = mavenTestProjectGoal;
		this.transactional = transactional;		
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
	public String getProjectName()
	{
		return this.projectName;
	}
	
	/**
	 * @return Id of TestLink Test Project
	 */
	public Integer getProjectId() 
	{
		return this.projectId;
	}
	
	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public void setTestPlanId(Integer testPlanId) {
		this.testPlanId = testPlanId;
	}
	
	/**
	 * @return Test Plan Name
	 */
	public String getTestPlanName()
	{
		return this.testPlanName;
	}
	
	/**
	 * @return Id of the Test Plan
	 */
	public Integer getTestPlanId() 
	{
		return this.testPlanId;
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
	
	public Integer getBuildId()
	{
		return this.buildId;
	}
	
	public void setBuildId(Integer id)
	{
		this.buildId = id;
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
	 * @return Directory of the maven test project
	 */
	public String getMavenTestProjectDirectory()
	{
		return this.mavenTestProjectDirectory;
	}
	
	/**
	 * Returns the name of the current Maven installation chosen to execute 
	 * the test project. 
	 * 
	 * @return Current Maven installation to execute the test project
	 */
	public String getMavenInstallationName()
	{
		return this.mavenInstallationName;
	}
	
	/**
	 * References Maven installations in Hudson. It is a cross plug-in 
	 * reference.
	 * 
	 * @return List of Maven installations. 
	 */
	public List<MavenInstallation> getMavenInstallations()
	{
		return Arrays.asList(Hudson.getInstance().getDescriptorByType(Maven.DescriptorImpl.class).getInstallations());
	}
	
	/**
	 * @return The goal to call in the maven test project
	 */
	public String getMavenTestProjectGoal()
	{
		if ( this.mavenTestProjectGoal == null )
		{
			return DEFAULT_MAVEN_GOAL;
		}
		return this.mavenTestProjectGoal;
	}
	
	public String getMavenExecutable() {
		return mavenExecutable;
	}

	public void setMavenExecutable(String mavenExecutable) {
		this.mavenExecutable = mavenExecutable;
	}
	
	public boolean validMaven()
	{
		return new File(this.mavenExecutable).exists();
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
	 * Returns {@link TestLinkBuilder} descriptor.
	 * @return Build Descriptor
	 */
	@Override
	public Descriptor<Builder> getDescriptor()
	{
		return DESCRIPTOR;
	}
	
	/**
	 * <p>Called when the job is executed.</p>
	 * 
	 * <p>It downloads information from TestLink using testlink-java-api. 
	 * The information gathered is sufficient to execute a maven test project 
	 * with automated tests.</p>
	 * 
	 * <p>Later the output xml is processed by another extension point, 
	 * the {@link TestLinkPublisher}.</p>
	 */
	@Override
	public boolean perform( 
			AbstractBuild<?, ?> build, 
			Launcher launcher,
			BuildListener listener ) 
	throws InterruptedException, IOException
	{
		
		// TestLink installation
		listener.getLogger().println("Preparing TestLink client API");
		TestLinkBuilderInstallation installation = 
			DESCRIPTOR.getInstallationByTestLinkName(this.testLinkName);
		if ( installation == null )
		{
			listener.fatalError("Invalid TestLink installation");
			return false;
		}
		this.testLinkClient = new TestLinkAPIClient(
			installation.getDevKey(), 
			installation.getUrl()
		);
		
		setupCustomFields(installation);
		
		// Verifying Maven installation
		if ( ! verifyMaven( launcher ) )
		{
			listener.fatalError("Invalid Maven executable. Please check your maven installation or refer to documentation.");
			return false;
		}
		
		// Details about the test parameters (plan, build, project)
		listener.getLogger().println("Updating TestLink parameters");
		try 
		{
			this.updateTestLinkParameters();
		}
		catch (TestLinkAPIException e) 
		{
			e.printStackTrace(listener.fatalError("Invalid TestLink parameters: " + e.getMessage()));
			return false;
		}
		
		listener.getLogger().println("Retrieving list of automated test cases");
		List<TestLinkTestCase> automatedTests = new ArrayList<TestLinkTestCase>();
		try 
		{
			this.retrieveListOfAutomatedTests( automatedTests, listener );
		}
		catch (TestLinkAPIException e) 
		{
			e.printStackTrace(listener.fatalError("Error retrieving list of automated test cases: " + e.getMessage()));
			return false;
		}
		
		boolean failure = false;
		for( TestLinkTestCase testCase : automatedTests )
		{
			if ( this.transactional && failure )
			{
				testCase.setResultStatus(TestLinkAPIConst.TEST_BLOCKED);
			} 
			else 
			{
				failure = this.executeTestCase( testCase, build, listener, launcher );
			}
			
			try
			{
				this.updateTestCaseResultStatus( testCase );
			} 
			catch ( TestLinkAPIException e )
			{
				e.printStackTrace( listener.fatalError("Error updating Test Case status: " + e.getMessage()) );
				return false;
			}
		}
		
		FilePath workspace = build.getWorkspace();
		File reportFile = new File(workspace.getRemote(), TestLinkParser.RESULT_FILE_NAME);
		try
		{
			this.writeTestLinkReportFile(automatedTests, reportFile);
		} catch ( IOException ioe )
		{
			Util.displayIOException(ioe, listener);
			ioe.printStackTrace( listener.fatalError("Error writing testlink.xml report: " + ioe.getMessage()) );
			return false;
		}
		
		// end
		return true;
	}

	/**
	 * Set up the custom fields values.
	 * 
	 * @param installation
	 */
	private void setupCustomFields(TestLinkBuilderInstallation installation) 
	{
		this.testFileCustomField = installation.getTestFileCustomField();
		this.testCategoryCustomField = installation.getTestCategoryCustomField();
		this.testCaseCategory = installation.getTestCaseCategory();
		this.testSuiteCategory = installation.getTestSuiteCategory();
	}

	/**
	 * 
	 */
	private boolean verifyMaven(Launcher launcher) 
	{
		List<Maven.MavenInstallation> mavenInstallations = this.getMavenInstallations();
		for(Maven.MavenInstallation inst : mavenInstallations)
		{
			if ( inst.getName().equals(mavenInstallationName))
			{
				try
				{
					this.mavenExecutable = inst.getExecutable(launcher);
				} catch (Exception e)
				{
					this.mavenExecutable = mavenInstallationName + 
						System.getProperty("file.separator") + 
						"bin" +
						System.getProperty("file.separator") + 
						"mvn";
				}
				return true;
			}			
		}
		return false;
	}

	/**
	 * Retrieves the details about the execution of the tests on TestLink (
	 * Test Plan Name, Test Plan Id
	 * @return
	 */
	private void updateTestLinkParameters() 
	throws TestLinkAPIException
	{

		TestLinkAPIResults projects = this.testLinkClient.getProjects();
		Object oProjectID = projects.getValueByName(0, TestLinkAPIConst.API_RESULT_IDENTIFIER);
		Integer projectID = Integer.parseInt(oProjectID.toString());
		this.setProjectId( projectID );
		
		TestLinkAPIResults projectTestPlans = 
			this.testLinkClient.getProjectTestPlans(projectName);
		Object o = projectTestPlans.getValueByName(0, TestLinkAPIConst.API_RESULT_IDENTIFIER);
		Integer planID = Integer.parseInt ( o.toString() );
		this.setTestPlanId(planID);
		
		// Creating Build or Retrieving existing one
		Integer buildID = this.testLinkClient.createBuild(
				planID, 
				buildName, 
				BUILD_NOTES);
		this.setBuildId(buildID);

	}

	/**
	 * Retrieves a list of the test cases marked as automated in TestLink
	 * given Test Plan ID.
	 * 
	 * @param automatedTests List to hold all automated tests
	 * @param listener Hudson Build listener
	 * @return List of Automated Test Cases
	 */
	private void retrieveListOfAutomatedTests(
			List<TestLinkTestCase> automatedTests, BuildListener listener ) 
	throws TestLinkAPIException
	{
		
		// Executes a query in TL to find all Test Cases linked to a Test Plan
		TestLinkAPIResults results = this.testLinkClient.getCasesForTestPlan( this.getTestPlanId() );
		int resultsSize = results.size();
		
		// for each tc found
		for ( int i = 0 ; i < resultsSize ; ++i )
		{
			Map<?, ?> result = results.getData(i);
			Object o = result.get(TestLinkAPIConst.API_RESULT_EXEC_TYPE);
			if ( o != null )
			{
				// Check if the execution type is auto
				String executionType = (String)o;
				if ( TestLinkAPIConst.TESTCASE_EXECUTION_TYPE_AUTO
						.equals(executionType) )
				{
					// convert to plug-in TC object
					TestLinkTestCase tc = this.convertMapToTestCase( result );
					// add to the list of tcs
					
					if ( this.validTestCase( tc ) )
					{
						automatedTests.add(tc);
					} else 
					{
						listener.getLogger().println("Invalid automated Test Case found: " + tc);
					}
				}
			}
		}
	}
	
	
	/**
	 * @param tc
	 * @return
	 */
	private boolean validTestCase(TestLinkTestCase tc) 
	{
		// if category and file are not empty
		if ( 
				! StringUtils.isEmpty(tc.getCategory()) && 
				! StringUtils.isEmpty(tc.getFile()))
		{
			// if category has one of the valid values
			if ( 
					tc.getCategory().equals( testCaseCategory) || 
					tc.getCategory().equals( testSuiteCategory ) )
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Converts a map returned from testlink-java-api to a TestLinkTestCase.
	 * 
	 * @param testCase Map returned from testlink-java-api
	 * @return Test Case
	 * @throws TestLinkAPIException 
	 */
	private TestLinkTestCase convertMapToTestCase(Map<?, ?> testCase) 
	throws TestLinkAPIException 
	{
		
		TestLinkTestCase tc = new TestLinkTestCase();
		tc.setPlanId(this.getTestPlanId());
		tc.setBuildId(this.getBuildId());
		
		// Test Case ID
		int testCaseId = Integer.parseInt( testCase.get(TestLinkAPIConst.API_RESULT_TC_INTERNAL_ID).toString() );
		TestLinkAPIResults apiResults = 
			this.testLinkClient.getTestCaseCustomFieldDesignValue(
					testCaseId,
					this.getProjectId(), 
					this.testCategoryCustomField, 
					"full");
		tc.setId(testCaseId);
		
		// Test Case Category (suite or test case)
		String testCaseCategory = 
			apiResults.getValueByName(0, "value").toString();
		tc.setCategory(testCaseCategory);	
		
		// Test Case File (testng suite xml, testng test case file)
		apiResults  = 
			this.testLinkClient.getTestCaseCustomFieldDesignValue(
					testCaseId,
					this.getProjectId(), 
					this.testFileCustomField, 
					"full");
		String testCaseFile = 
			apiResults.getValueByName(0, "value").toString();
		tc.setFile(testCaseFile);
		
		return tc;
	}
	
	/**
	 * <p>Executes an automated test case. It calls the chosen Maven passing the 
	 * test project pom.xml and adding the test file as parameter for the 
	 * test goal.</p>
	 * 
	 * <p>Example of caller command: mvn -f <test_project_directory>/pom.xml 
	 * <test_goal> -D(test=<test_file> | suiteXmlFiles=<test_file>)</p>
	 * 
	 * @param tc Test Case
	 * @param build Hudson Build
	 * @param listener Hudson Build listener
	 * @param launcher Hudson Launcher
	 */
	private boolean executeTestCase(
			TestLinkTestCase tc, 
			AbstractBuild<?, ?> build, 
			BuildListener listener, 
			Launcher launcher) 
	{
		String mavenExecutable = this.getMavenExecutable();
		
		// List of arguments passed for command line
		ArgumentListBuilder args = new ArgumentListBuilder();
		
		args.add( mavenExecutable );
		//args.add("-DMAVEN_OPTS=\"-Xmx1024m -Xms512m -XX:MaxPermSize=256m -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=45663,server=y,suspend=n\"");
		args.add("-f", this.mavenTestProjectDirectory + 
				System.getProperty("file.separator") + 
				"pom.xml");
		if ( tc.getCategory().equals( testCaseCategory ))
		{
			args.add("test", "-Dtest=" + tc.getFile());
		} else if ( tc.getCategory().equals( testSuiteCategory ) )
		{
			args.add("test", "-Dtests=" + tc.getFile());
		} else {
			listener.fatalError("Invalid test category: " + tc);
			return false;
		}
		args.add("&&","exit","%%ERRORLEVEL%%");
		
		// Try to execute the command
        listener.getLogger().println("Executing command: "+args.toStringWithQuote());
        
        int exitCode = -1;
        try 
        {
            Map<String,String> env = build.getEnvironment(listener);
            
            ProcStarter ps = launcher.launch();
            ps.cmds(args);
            ps.envs(env);
            ps.stdout(listener);
            ps.pwd(build.getModuleRoot());
            
            exitCode = ps.join();
            
        } catch (IOException e) {
            Util.displayIOException(e,listener);
            e.printStackTrace( listener.fatalError("Command execution failed") );
        } catch (InterruptedException e) {
        	e.printStackTrace( listener.fatalError("Command execution failed") );
		}
        
        tc.setResultStatus( this.getTestLinkTCStatus( exitCode ) );
        
        return tc.getResultStatus().equals(TestLinkAPIConst.TEST_FAILED);
	}
	
	/**
	 * Returns the equivalent string status in TestLink for an exit code.
	 * 
	 * @param exitCode Execution command line exit code
	 * @return
	 */
	private String getTestLinkTCStatus( int exitCode )
	{
		if ( exitCode == 0 )
			return TestLinkAPIConst.TEST_PASSED;
		return TestLinkAPIConst.TEST_FAILED;
	}
	
	/**
	 * Updates the Test Case status in Test Link. 
	 * 
	 * @param tc Test Case
	 * @throws TestLinkAPIException 
	 */
	private void updateTestCaseResultStatus(TestLinkTestCase tc) 
	throws TestLinkAPIException 
	{
		this.testLinkClient.reportTestCaseResult(
				tc.getPlanId(), 
				tc.getId(), 
				tc.getBuildId(), 
				TEST_CASE_EXECUTION_NOTES, 
				tc.getResultStatus());
	}
	
	/**
	 * Writes TestLink report file.
	 * 
	 * @param automatedTests List of Test Cases executed.
	 * @param reportFile File to write to
	 * @throws IOException 
	 */
	private void writeTestLinkReportFile( 
			final List<TestLinkTestCase> automatedTests, 
			File reportFile )
	throws IOException 
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("<testlink>\n");
		for ( TestLinkTestCase tc : automatedTests )
		{
			buffer.append(tc.toXml());
		}
		buffer.append("</testlink>\n");
		
		FileWriter writer = new FileWriter( reportFile );
		writer.append(buffer.toString());
		writer.flush();
		writer.close();
	}
	
}
