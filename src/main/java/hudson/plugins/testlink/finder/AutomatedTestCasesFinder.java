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
package hudson.plugins.testlink.finder;

import hudson.model.BuildListener;
import hudson.plugins.testlink.Messages;

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
 * @since 2.0
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
	
	private static final String BUILD_NOTES = Messages.TestLinkBuilder_Build_Notes();
	
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
		listener.getLogger().println( Messages.TestLinkBuilder_Finder_RetrievingDetails() );
		
		this.retrieveTestLinkData();
		
		listener.getLogger().println( Messages.TestLinkBuilder_Finder_RetrievingListOfAutomatedTestCases() );
		
		final TestCase[] testCases = this.api.getTestCasesForTestPlan(
				this.testPlan.getId(), null, null, null, null, null, null, null, 
				ExecutionType.AUTOMATED, null);
		
		for (int i = 0; i < testCases.length; i++) 
		{
			listener.getLogger().println( Messages.TestLinkBuilder_Finder_FoundAutomatedTestCase( testCases[i] ) );			
			testCases[i].setTestProjectId( this.testProject.getId() );
			
			// Retrieve list of custom fields for TC
			listener.getLogger().println( Messages.TestLinkBuilder_Finder_RetrievingListOfCustomFields() );
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
				
				listener.getLogger().println( Messages.TestLinkBuilder_Finder_RetrievingCustomField( customFieldName ) );
				
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
					
					listener.getLogger().println( Messages.TestLinkBuilder_Finder_CustomFieldNameAndValue( customFieldName, customField.getValue()) );
				} 
				catch (TestLinkAPIException e)
				{
					listener.getLogger().println( Messages.TestLinkBuilder_Finder_FailedToRetrieveCustomField(customFieldName, testCase.toString(), e.getMessage()) );
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
