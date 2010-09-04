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
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.plugins.testlink.model.TestLinkExecutionDetails;
import hudson.plugins.testlink.model.TestLinkTestCase;
import hudson.tasks.Builder;
import hudson.tasks.Maven;
import hudson.tasks.Maven.MavenInstallation;
import hudson.util.ArgumentListBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
	
	/**
	 * The name of the Test Plan.
	 */
	private final String testPlanName;
	
	/**
	 * The name of the Build.
	 */
	private final String buildName;
	
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
	private static final String BUILD_NOTES = "Build create automatically with Hudson.";
	
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
		
		final TestLinkExecutionDetails executionDetails = new TestLinkExecutionDetails();
		
		List<TestLinkTestCase> automatedTests = new ArrayList<TestLinkTestCase>();
		
		this.retrieveListOfAutomatedTests( automatedTests );
		
		for( TestLinkTestCase testCase : automatedTests )
		{
			this.executeTestCase( testCase );
			this.updateTestCaseResultStatus ( testCase );
		}
		
		this.writeReport(automatedTests);
		
		// end
		
		try 
		{
			// project ID
			listener.getLogger().println("Retrieving Project ID");
			TestLinkAPIResults projects = this.testLinkClient.getProjects();
			if ( projects == null )
			{
				listener.fatalError("TestLink has no project configured yet.");
				return false;
			}
			Object oProjectID = projects.getValueByName(0, "id");
			Integer projectID = Integer.parseInt(oProjectID.toString());
			listener.getLogger().println("Project ID: " + projectID);
			
			// test plan ID
			listener.getLogger().println("Retrieving Test Plan ID for" +
					" project: " + projectName);
			TestLinkAPIResults projectTestPlans = 
				this.testLinkClient.getProjectTestPlans(projectName);
			Object o = projectTestPlans.getValueByName(0, "id");
			Integer planID = Integer.parseInt ( o.toString() );
			listener.getLogger().println("Test Plan ID: " + planID);
			
			// Creating Build
			Integer buildID = this.testLinkClient.createBuild(
					planID, 
					buildName, 
					BUILD_NOTES);
			
			// Retrieving automated test cases
			List<Map<?, ?>> testCases = this.getAutomatedTestCases(planID);
			listener.getLogger().println("Found " + testCases.size() +
					" automated test cases for build: " + buildID);
			
			if ( testCases.size() > 0 )
			{
				// Executing automated tests
				listener.getLogger().println("Executing automated tests");
				try 
				{
					this.executeTestCasesAndUpdateStatus( projectID, planID, buildID, testCases, false, build, launcher, listener, executedTestCases );
				} catch ( Exception e )
				{
					this.executeTestCasesAndUpdateStatus( projectID, planID, buildID, testCases, true, build, launcher, listener, executedTestCases );
				}
			}
			
			listener.getLogger().println("Writing testlink execution xml report");
			
			listener.getLogger().println("End of automated test execution");
			
		} catch ( Exception apie) 
		{
			listener.fatalError("Failed to execute TestLink: " + apie.getMessage(), apie);
			return false;
		}
		
		return true;
	}
	
	/**
	 * @param automatedTests
	 */
	private void retrieveListOfAutomatedTests(
			List<TestLinkTestCase> automatedTests )
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * Retrieves a list of the test cases marked as automated in TestLink
	 * given Test Plan ID.
	 * 
	 * @param planID Test Plan ID
	 * @return List of Automated Test Cases
	 */
	private List<Map<?, ?>> getAutomatedTestCases( Integer planID ) 
	throws TestLinkAPIException
	{
		List<Map<?, ?>> testCases = new ArrayList<Map<?,?>>();
		final TestLinkAPIResults results;
		
		results = this.testLinkClient.getCasesForTestPlan(planID);
		
		int resultsSize = results.size();
		for ( int i = 0 ; i < resultsSize ; ++i )
		{
			Map<?, ?> result = results.getData(i);
			Object o = result.get("execution_type");
			if ( o != null )
			{
				String executionType = (String)o;
				if ( TestLinkAPIConst.TESTCASE_EXECUTION_TYPE_AUTO
						.equals(executionType) )
				{
					testCases.add(result);
				}
			}
		}
		return testCases;
	}
	
	/**
	 * Executes the Test Case and then updates its status in TestLink. 
	 * 
	 * @param planID ID of Test Plan
	 * @param buildID ID of Build
	 * @param testCases Map containing Test Cases
	 * @param build Hudson Build
	 * @param launcher Hudson Launcher
	 * @param listener Hudson Build Listener
	 */
	private void executeTestCasesAndUpdateStatus( 
			Integer projectID,
			Integer planID, 
			Integer buildID, 
			List<Map<?, ?>> testCases, 
			boolean forceBlockedStatus, 
			AbstractBuild<?, ?> build, 
			Launcher launcher, 
			BuildListener listener, 
			List<TestLinkTestCase> resultTestCases) 
	throws TestLinkAPIException, Exception
	{
		
		// If true the execution updates test status with blocked
		boolean blockAllTests = forceBlockedStatus;
		
		Iterator<Map<?, ?>> testCasesIterator = testCases.iterator();
		while ( testCasesIterator.hasNext() )
		{
			final Map<?, ?> testCase = testCasesIterator.next();
			final int testCaseId = Integer.parseInt( testCase.get("tc_id").toString() );
			
			final TestLinkAPIResults categoryResults = 
				this.testLinkClient.getTestCaseCustomFieldDesignValue(
						testCaseId,
						projectID, 
						"AutomatedTestCategory", 
						"full");
			
			final String testCaseCategory = 
				categoryResults.getValueByName(0, "value").toString();
				
			final TestLinkAPIResults fileResults = 
				this.testLinkClient.getTestCaseCustomFieldDesignValue(
						testCaseId,
						projectID, 
						"AutomatedTestFile", 
						"full");
			final String testCaseFile = 
				fileResults.getValueByName(0, "value").toString();
			
			TestLinkTestCase tc = new TestLinkTestCase();
			tc.setId(testCaseId);
			tc.setBuildId(buildID);
			tc.setPlanId(planID);
			tc.setCategory(testCaseCategory);
			tc.setFile(testCaseFile);
			
			// Update test case status
			if ( blockAllTests )
			{
				tc.setResultStatus(TestLinkAPIConst.TEST_BLOCKED);
				this.updateTestStatus(
						tc, 
						listener);
				
				this.report.addTestCase(tc);
			} 
			else
			{
				// Executes Test Case
				int exitCode = this.executeTestCase( planID, buildID, testCaseId, testCaseCategory, testCaseFile, build, launcher, listener );
				
				final String testResultStatus = getTestLinkTCStatus(exitCode);
				listener.getLogger().println("Updating TC " + testCaseId + " with status " + testResultStatus);
				
				tc.setResultStatus(testResultStatus);
				this.updateTestStatus(
						tc,
						listener);
				
				if ( transactional && ! testResultStatus.equals(TestLinkAPIConst.TEST_PASSED) )
				{
					listener.getLogger().println("Transactional test execution failed. Stoping test execution.");
					blockAllTests = true;
				}
			}
			
			resultTestCases.add(tc);
			
		}
	}
	
	/**
	 * @param exitCode
	 * @return
	 */
	private String getTestLinkTCStatus( int exitCode )
	{
		if ( exitCode == 0 )
			return TestLinkAPIConst.TEST_PASSED;
		return TestLinkAPIConst.TEST_FAILED;
	}

	/**
	 * <p>Executes an automated test case. It calls the chosen Maven passing the 
	 * test project pom.xml and adding the test file as parameter for the 
	 * test goal.</p>
	 * 
	 * <p>Example of caller command: mvn -f <test_project_directory>/pom.xml 
	 * <test_goal> -D(test=<test_file> | suiteXmlFiles=<test_file>)</p>
	 * 
	 * @param planID ID of Test Plan
	 * @param buildID ID of Build
	 * @param testCaseId ID of Test Case
	 * @param testCaseCategory Test Case category (suite or test case)
	 * @param testCaseFile Test Case file
	 * @param build 
	 * @param launcher Hudson Launcher
	 * @param listener Hudson Build Listener 
	 */
	private int executeTestCase(
		Integer planID, 
		Integer buildID,
		int testCaseId, 
		String testCaseCategory, 
		String testCaseFile, 
		AbstractBuild<?, ?> build, 
		Launcher launcher, 
		BuildListener listener 
	) 
	throws Exception
	{
		listener.getLogger().println("Executing test case " + testCaseId);
		
		String mavenExecutable = null;
		
		List<Maven.MavenInstallation> mavenInstallations = this.getMavenInstallations();
		for(Maven.MavenInstallation inst : mavenInstallations)
		{
			if ( inst.getName().equals(mavenInstallationName))
			{
				try
				{
					mavenExecutable = inst.getExecutable(launcher);
				} catch (Exception e)
				{
					mavenExecutable = mavenInstallationName + 
						System.getProperty("file.separator") + 
						"bin" +
						System.getProperty("file.separator") + 
						"mvn";
				}
			}			
		}
		
		if ( mavenExecutable == null )
		{
			throw new Exception("Invalid maven installation");
		}
		
		ArgumentListBuilder args = new ArgumentListBuilder();
		args.add( mavenExecutable );
		args.add("-f", this.mavenTestProjectDirectory + 
				System.getProperty("file.separator") + 
				"pom.xml");
		args.add("test", "-Dtest="+testCaseFile);
		args.add("&&","exit","%%ERRORLEVEL%%");
		
		// Try to execute the command
        listener.getLogger().println("Executing command: "+args.toStringWithQuote());
        
        int exitCode = -1;
        try 
        {
            Map<String,String> env = build.getEnvironment(listener);
            exitCode = launcher.launch().cmds(args).envs(env).stdout(listener).pwd(build.getModuleRoot()).join();
        } catch (IOException e) {
            Util.displayIOException(e,listener);
            e.printStackTrace( listener.fatalError("Command execution failed") );
        }
        return exitCode;
		
	}
	
	/**
	 * Updates test status based on error code. The error code is the status of 
	 * the maven test project execution.
	 * 
	 * @param testPlanId Test Plan ID.
	 * @param buildId Build ID.
	 * @param testCaseId Test Case ID.
	 * @param errorCode Error code of maven execution.
	 * @throws AutomationException 
	 */
	private void updateTestStatus( 
			TestLinkTestCase tc, 
			BuildListener listener) 
	throws TestLinkAPIException
	{
		
		final String execNotes = "Test executed by Hudson TestLink plug-in.";
		listener.getLogger().println("Updating test plan ["+tc.getPlanId()+"]," +
		" build id ["+ tc.getBuildId() +"], test case id ["+tc.getId()+"], " +
				"exec notes [" + execNotes + "] and testResultStatus [" +tc.getResultStatus()+ "]");
		this.testLinkClient.reportTestCaseResult(
				tc.getPlanId(), 
				tc.getId(), 
				tc.getBuildId(), 
				execNotes, 
				tc.getResultStatus());
		
		this.report.addTestCase(tc);

	}
	
}
