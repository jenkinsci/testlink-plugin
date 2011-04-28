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
import hudson.plugins.testlink.result.TestCaseWrapper;
import hudson.plugins.testlink.util.Messages;
import hudson.plugins.testlink.util.TestLinkHelper;

import java.net.URL;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.TestLinkAPIException;
import br.eti.kinoshita.testlinkjavaapi.model.Attachment;
import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;
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
public class TestLinkHandler
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
	 * Default constructor. Initializes the TestLink API and the Build 
	 * Listener.
	 * 
	 * @param url TestLink URL (usually something like http://localhost/testlink/lib/api/xml-rpc.php
	 * @param devKey the developer key 
	 * @param buildNotes 
	 * @param buildName 
	 * @param testPlanName 
	 * @param testProjectName 
	 * @param listener the Hudson Build listener
	 */
	public TestLinkHandler( URL url, String devKey, String testProjectName, String testPlanName, String buildName, String buildNotes, BuildListener listener ) 
	{
		this.api = new TestLinkAPI( url, devKey );
		this.listener = listener;
		
		this.init ( testProjectName, testPlanName, buildName, buildNotes );
	}
	
	/**
	 * @param testProjectName
	 * @param testPlanName
	 * @param buildName
	 * @param buildNotes
	 */
	private void init( 
			String testProjectName, 
			String testPlanName,
			String buildName, 
			String buildNotes )
	{
		// TestLink details (project, plan, build).
		listener.getLogger().println( Messages.TestLinkBuilder_Finder_RetrievingDetails() );
		
		this.testProject = api.getTestProjectByName( testProjectName );
		this.testPlan 	 = api.getTestPlanByName( testPlanName, testProjectName );
		
		// Creating Build or Retrieving existing one
		this.build = api.createBuild( this.testPlan.getId(), buildName, buildNotes);
		
		listener.getLogger().println( "Using TestLink Test Project: ["+this.testProject.getName()+"], ID: ["+this.testProject.getId()+"]." );
		listener.getLogger().println( "Using TestLink Test Plan: ["+this.testPlan.getName()+"], ID: ["+this.testPlan.getId()+"]." );
		listener.getLogger().println( "Using TestLink Build: ["+this.build.getName()+"], ID: ["+this.build.getId()+"]." );
		
		listener.getLogger().println();
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
	
	/**
	 * Updates the test cases status for a list of TestResult's. Besides 
	 * updating the status this method also uploads the attachments of the 
	 * TestResult's.
	 * 
	 * @param wrappedTestCases List of TestResults
	 * @throws TestLinkAPIException
	 */
	public void updateTestCasesAndUploadAttachments( Set<TestCaseWrapper> wrappedTestCases ) 
	throws TestLinkAPIException
	{
		if ( CollectionUtils.isNotEmpty( wrappedTestCases ) )
		{
			listener.getLogger().println( Messages.TestLinkBuilder_Update_AutomatedTestCases( wrappedTestCases.size() ) );
			
			// Update TestLink Test Status
			for( TestCaseWrapper testResult : wrappedTestCases )
			{
				
				TestCase testCase = testResult.getTestCase();
				
				listener.getLogger().println( Messages.TestLinkBuilder_Update_AutomatedTestCase(testCase.getName(), TestLinkHelper.getExecutionStatusText( testCase.getExecutionStatus() )) );
				
				// Update Test Case status
				ReportTCResultResponse reportTCResultResponse = api.reportTCResult(
						testCase.getId(), 
						testCase.getInternalId(), 
						testPlan.getId(), 
						testCase.getExecutionStatus(), 
						build.getId(), 
						build.getName(), 
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
					api.uploadExecutionAttachment(
							reportTCResultResponse.getExecutionId(), 
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

	/**
	 * @param customFieldsNames Array of custom fields names.
	 * @return Array of automated test cases with custom fields.
	 */
	public TestCase[] retrieveAutomatedTestCasesWithCustomFields( String[] customFieldsNames ) 
	
	{
		final TestCase[] testCases = this.api.getTestCasesForTestPlan(
				getTestPlan().getId(), 
				null, 
				null, 
				null, 
				null,
				null, 
				null, 
				null, 
				ExecutionType.AUTOMATED, 
				null);			
		
		listener.getLogger().println( "Found ["+testCases.length+"] TestLink Automated Test Cases." );
		
		this.setProjectIDExecutionStatusAndCustomFields( testCases, customFieldsNames );
		
		return testCases;
	}
	
	/**
	 * Sets the project ID and custom fields into Test Cases.
	 * 
	 * @param testCases Array of TestLink test cases.
	 * @param customFieldsNames Array of custom fields names.
	 */
	private void setProjectIDExecutionStatusAndCustomFields( TestCase[] testCases, String[] customFieldsNames )
	{
		if ( testCases.length > 0 )
		{
			listener.getLogger().println();
		}
		for ( TestCase testCase : testCases ) 
		{
			listener.getLogger().println( Messages.TestLinkBuilder_Finder_FoundAutomatedTestCase( testCase ) );			
			testCase.setTestProjectId( this.testProject.getId() );
			
			testCase.setExecutionStatus( ExecutionStatus.NOT_RUN );
			
			// Retrieve list of custom fields for TC
			listener.getLogger().println( Messages.TestLinkBuilder_Finder_RetrievingListOfCustomFields() );
			this.retrieveAndSetCustomFields( testCase, customFieldsNames );
			
			listener.getLogger().println();
		}
	}
	
	/**
	 * Retrieve and sets custom fields for a given Test Case.
	 * 
	 * @param testCase Test Case.
	 * @param customFieldsNames Names of custom fields.
	 */
	private void retrieveAndSetCustomFields( TestCase testCase, String[] customFieldsNames ) 
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
	
}