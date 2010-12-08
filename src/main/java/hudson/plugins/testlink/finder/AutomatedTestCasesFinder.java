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
 * @since 03/12/2010
 */
package hudson.plugins.testlink.finder;

import hudson.model.BuildListener;

import java.util.ArrayList;
import java.util.List;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.TestLinkAPIException;
import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionType;
import br.eti.kinoshita.testlinkjavaapi.model.ResponseDetails;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.model.TestProject;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 03/12/2010
 */
public class AutomatedTestCasesFinder
{

	/**
	 * Hudson Build listener.
	 */
	private BuildListener listener;
	
	/**
	 * TestLink API.
	 */
	private TestLinkAPI api;

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
	 * Array of custom fields names.
	 */
	protected String[] customFieldsNames;
	
	private static final String BUILD_NOTES = "Build created automatically with Hudson TestLink plug-in.";
	
	public AutomatedTestCasesFinder( 
		BuildListener listener, 
		TestLinkAPI api, 
		String[] customFieldsNames, 
		String testProjectName, 
		String testPlanName, 
		String buildName
	) 
	{
		this.listener = listener;
		this.api = api;
		this.customFieldsNames = customFieldsNames;
		this.testProjectName = testProjectName;
		this.testPlanName = testPlanName;
		this.buildName = buildName;
	}
	
	public List<TestCase> findAutomatedTestCases() 
	throws TestLinkAPIException
	{
		
		final List<TestCase> foundAutomatedTestCases = new ArrayList<TestCase>();
		
		// TestLink details (project, plan, build).
		listener.getLogger().println("Retrieving TestLink details about " +
				"Test Project, Test Plan and Build.");
		
		this.retrieveTestLinkData();
		
		listener.getLogger().println("Retrieving list of automated test cases from TestLink");
		
		final TestCase[] testCases = this.api.getTestCasesForTestPlan(
				this.testPlan.getId(), null, null, null, null, null, null, null, 
				ExecutionType.AUTOMATED, null);
		
		for (int i = 0; i < testCases.length; i++) 
		{
			listener.getLogger().println("Found TestLink Test Case: " + testCases[i] );			
			testCases[i].setTestProjectId( this.testProject.getId() );
			
			// Retrieve list of custom fields for TC
			listener.getLogger().println("Automated test case found. Retrieving list of custom fields for test case");
			this.retrieveListOfCustomFields( testCases[i] );
			
			foundAutomatedTestCases.add(testCases[i]);
		}
		
		return foundAutomatedTestCases;
	}
	
	/**
	 * Retrieves the details about Test Project, Test Pland and Build from 
	 * TestLink using TestLink Java API.
	 */
	protected void retrieveTestLinkData() 
	throws TestLinkAPIException
	{
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
				
				listener.getLogger().println( "Retrieving custom field " + customFieldName );
				
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
					
					listener.getLogger().println("Custom field " + customFieldName + " value: " + customField.getValue() );
				} 
				catch (TestLinkAPIException e)
				{
					listener.getLogger().println("Failed to retrieve custom field " + customFieldName + " for Test Case " + testCase.toString());
					e.printStackTrace( listener.getLogger() );
				}

			}
		}
	}
	
	public TestProject getTestProject()
	{
		return testProject;
	}

	public TestPlan getTestPlan()
	{
		return testPlan;
	}

	public Build getBuild()
	{
		return build;
	}
	
}
