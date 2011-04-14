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

import hudson.model.BuildListener;
import hudson.plugins.testlink.result.TestResult;
import hudson.plugins.testlink.util.Messages;
import hudson.plugins.testlink.util.TestLinkHelper;

import java.net.URL;
import java.util.Set;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.TestLinkAPIException;
import br.eti.kinoshita.testlinkjavaapi.model.Attachment;
import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionType;
import br.eti.kinoshita.testlinkjavaapi.model.ReportTCResultResponse;
import br.eti.kinoshita.testlinkjavaapi.model.ResponseDetails;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.model.TestProject;

/**
 * A class to handle all requests sent to TestLink from the plug-in.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
public class TestLinkService
{

	/**
	 * The TestLink API object.
	 */
	private TestLinkAPI api;
	
	/**
	 * Hudson Build listener.
	 */
	private BuildListener listener;
	
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
	 * Used to refer to the executions table in the TestLink db schema. Bug 
	 * fixed in the API, when the next release is available we will remove it.
	 */
	private static final String EXECUTIONS_TABLE = "executions";
	
	/**
	 * Default constructor. Initializes the TestLink API and the Build 
	 * Listener.
	 * 
	 * @param url TestLink URL (usually something like http://localhost/testlink/lib/api/xml-rpc.php
	 * @param devKey the developer key 
	 * @param listener the Hudson Build listener
	 */
	public TestLinkService( URL url, String devKey, BuildListener listener ) 
	{
		this.api = new TestLinkAPI( url, devKey );
		
		this.listener = listener;
	}

	/**
	 * Finds automated test cases for a given Test Plan name. First it will 
	 * retrieve the Test Project, Test Plan and Build from TestLink. A new Build 
	 * will be created if there is no Build with this name.
	 * 
	 * @param testProjectName the name of the Test Project
	 * @param testPlanName the name of the Test Plan
	 * @param buildName the name of the Build
	 * @param buildNotes the notes for the Build
	 * @param customFieldsNames array with the name of the custom fields to be retrieved from TestLink and injected into the Test Cases
	 * @return array of Test Cases with Execution Type equals Automated
	 * @throws TestLinkAPIException
	 */
	public TestCase[] initializeTestLinkAndFindAutomatedTestCases( 
		String testProjectName, 
		String testPlanName, 
		String buildName, 
		String buildNotes, 
		String[] customFieldsNames
	) 
	throws TestLinkAPIException
	{
		// TestLink details (project, plan, build).
		listener.getLogger().println( Messages.TestLinkBuilder_Finder_RetrievingDetails() );
		this.retrieveTestLinkData( testProjectName, testPlanName, buildName, buildNotes );
		
		listener.getLogger().println( "Using TestLink Test Project: ["+this.testProject.getName()+"], ID: ["+this.testProject.getId()+"]." );
		listener.getLogger().println( "Using TestLink Test Plan: ["+this.testPlan.getName()+"], ID: ["+this.testPlan.getId()+"]." );
		listener.getLogger().println( "Using TestLink Build: ["+this.build.getName()+"], ID: ["+this.build.getId()+"]." );
		
		listener.getLogger().println();
		
		listener.getLogger().println( Messages.TestLinkBuilder_Finder_RetrievingListOfAutomatedTestCases() );
		final TestCase[] testCases = this.retrieveAutomatedTestCases( this.testPlan.getId() );			
		
		listener.getLogger().println( "Found ["+testCases.length+"] TestLink Automated Test Cases." );
		
		this.setProjectIDAndCustomFields( testCases, customFieldsNames );
		
		return testCases;
	}
	
	/**
	 * Updates the test cases status for a list of TestResult's. Besides 
	 * updating the status this method also uploads the attachments of the 
	 * TestResult's.
	 * 
	 * @param testResults List of TestResults
	 * @throws TestLinkAPIException
	 */
	public void updateTestCasesAndUploadAttachments( Set<TestResult> testResults ) 
	throws TestLinkAPIException
	{
		if ( testResults.size() > 0 )
		{
			listener.getLogger().println( Messages.TestLinkBuilder_Update_AutomatedTestCases( testResults.size() ) );
			
			// Update TestLink Test Status
			for( TestResult testResult : testResults )
			{
				TestCase testCase = testResult.getTestCase();
				listener.getLogger().println( Messages.TestLinkBuilder_Update_AutomatedTestCase(testCase.getName(), TestLinkHelper.getExecutionStatusText( testCase.getExecutionStatus() )) );
				
				// Update Test Case status
				ReportTCResultResponse reportTCResultResponse = api.reportTCResult(
						testCase.getId(), 
						testCase.getInternalId(), 
						testResult.getTestPlan().getId(), 
						testCase.getExecutionStatus(), 
						testResult.getBuild().getId(), 
						testResult.getBuild().getName(), 
						testResult.getNotes(), 
						null, // guess
						null, // bug id
						null, // platform id 
						null, // platform name
						null, // custom fields
						null);
				
				for ( Attachment attachment : testResult.getAttachments() )
				{
					listener.getLogger().println( Messages.TestLinkBuilder_Upload_ExecutionAttachment(reportTCResultResponse.getExecutionId(), attachment.getFileName()) );
					api.uploadAttachment(
							reportTCResultResponse.getExecutionId(), 
							EXECUTIONS_TABLE, // TBD: replace with TestLinkTables enum value
							attachment.getTitle(), 
							attachment.getDescription(), 
							attachment.getFileName(), 
							attachment.getFileType(), 
							attachment.getContent());
				}
			} 
		}
		else
		{
			listener.getLogger().println(Messages.TestLinkBuilder_Update_Skipped());
		}
		
		listener.getLogger().println();
	}
	
	/**
	 * Retrieves the TestProject, testPlan and Build from TestLink and 
	 * stores a local reference in the object. These objects retrieved from 
	 * TestLink will be used later to retrieve the automated test cases.
	 * 
	 * @param testProjectName the test project name
	 * @param testPlanName the test plan name
	 * @param buildName the build name
	 * @param buildNotes the notes of the build
	 */
	private void retrieveTestLinkData( String testProjectName, String testPlanName, String buildName, String buildNotes )
	throws TestLinkAPIException
	{
		TestProject testProject = this.api.getTestProjectByName( testProjectName );
		this.testProject = testProject;
		
		TestPlan testPlan = 
			this.api.getTestPlanByName(testPlanName, testProjectName );
		
		this.testPlan = testPlan;
		
		// Creating Build or Retrieving existing one
		Build build = this.api.createBuild(
				this.testPlan.getId(), 
				buildName, 
				buildNotes);
		this.build =  build;
	}
	
	/**
	 * Retrieves list of automated test cases for a given test plan ID. The 
	 * test cases do not have any custom field linked to it yet though.
	 * 
	 * @param testPlanId The ID of the Test Plan
	 * @return List of Automated Test Cases for the given Test Plan ID
	 */
	private TestCase[] retrieveAutomatedTestCases( Integer testPlanId ) 
	throws TestLinkAPIException
	{
		TestCase[] testCases = this.api.getTestCasesForTestPlan(
				testPlanId, 
				null, 
				null, 
				null, 
				null,
				null, 
				null, 
				null, 
				ExecutionType.AUTOMATED, 
				null);
		return testCases;
	}

	/**
	 * Sets the project ID and custom fields in the Test Case.
	 * 
	 * @param testCases
	 * @param customFieldsNames
	 */
	private void setProjectIDAndCustomFields( TestCase[] testCases, String[] customFieldsNames )
	{
		if ( testCases.length > 0 )
		{
			listener.getLogger().println();
		}
		for ( TestCase testCase : testCases ) 
		{
			listener.getLogger().println( Messages.TestLinkBuilder_Finder_FoundAutomatedTestCase( testCase ) );			
			testCase.setTestProjectId( this.testProject.getId() );
			
			// Retrieve list of custom fields for TC
			listener.getLogger().println( Messages.TestLinkBuilder_Finder_RetrievingListOfCustomFields() );
			this.setCustomFields( testCase, customFieldsNames );
			
			listener.getLogger().println();
		}
	}
	
	/**
	 * Retrieves
	 * 
	 * @param testCase TestLink automated test case
	 * @param customFieldsNames array of custom fields names
	 */
	private void setCustomFields( TestCase testCase, String[] customFieldsNames ) 
	{
		if ( customFieldsNames != null && customFieldsNames.length > 0 )
		{
			for (String customFieldName : customFieldsNames )
			{
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

	/**
	 * Retrieves the TestLink API.
	 * 
	 * @return the TestLink API.
	 */
	public TestLinkAPI getApi()
	{
		return api;
	}

	/**
	 * Retrieves the Test Project.
	 * 
	 * @return the Test Project.
	 */
	public TestProject getTestProject()
	{
		return testProject;
	}

	/**
	 * Retrieves the Test Plan.
	 * 
	 * @return the Test Plan.
	 */
	public TestPlan getTestPlan()
	{
		return testPlan;
	}

	/**
	 * Retrieves the Build.
	 * 
	 * @return the Build.
	 */
	public Build getBuild()
	{
		return build;
	}
	
}