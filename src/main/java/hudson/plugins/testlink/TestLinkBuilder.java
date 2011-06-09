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
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.plugins.testlink.result.TestCaseWrapper;
import hudson.plugins.testlink.result.TestLinkReport;
import hudson.plugins.testlink.result.TestResultSeekerException;
import hudson.plugins.testlink.result.TestResultsCallable;
import hudson.plugins.testlink.tasks.CommandExecutor;
import hudson.plugins.testlink.util.Messages;
import hudson.plugins.testlink.util.TestLinkHelper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPIException;
import br.eti.kinoshita.testlinkjavaapi.model.Build;
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
extends AbstractTestLinkBuilder
{

	/**
	 * The Descriptor of this Builder. It contains the TestLink installation.
	 */
	@Extension 
	public static final TestLinkBuilderDescriptor DESCRIPTOR = new TestLinkBuilderDescriptor();

	@DataBoundConstructor
	public TestLinkBuilder(
		String testLinkName, 
		String testProjectName, 
		String testPlanName, 
		String buildName, 
		String customFields, 
		String singleTestCommand, 
		String iterativeTestCommand, 
		String keyCustomField, 
		Boolean transactional, 
		Boolean failedTestsMarkBuildAsFailure, 
		String junitXmlReportFilesPattern, 
		String testNGXmlReportFilesPattern, 
		String tapStreamReportFilesPattern, 
		String beforeSingleTestCommand, 
		String afterSingleTestCommand, 
		String beforeIterativeTestCommand, 
		String afterIterativeTestCommand
	)
	{
		super(
			testLinkName, 
			testProjectName, 
			testPlanName, 
			buildName, 
			customFields, 
			singleTestCommand, 
			iterativeTestCommand, 
			keyCustomField, 
			transactional, 
			failedTestsMarkBuildAsFailure, 
			junitXmlReportFilesPattern, 
			testNGXmlReportFilesPattern, 
			tapStreamReportFilesPattern, 
			beforeSingleTestCommand, 
			afterSingleTestCommand, 
			beforeIterativeTestCommand, 
			afterIterativeTestCommand
		);
	}
	
	public Object readResolve()
	{
		return this;
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
				expandTestProjectName( build.getBuildVariableResolver(), build.getEnvironment(listener) ), 
				expandTestPlanName( build.getBuildVariableResolver(), build.getEnvironment(listener) ), 
				expandBuildName( build.getBuildVariableResolver(), build.getEnvironment(listener) ), 
				Messages.TestLinkBuilder_Build_Notes(), 
				listener 
			);
		
		final String[] customFieldsNames 	= createArrayOfCustomFieldsNames();
		
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
		
		this.updateBuildStatus( report.getTestsFailed(), build );
		
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
		
		TestLinkHelper.setTestLinkJavaAPIProperties( installation.getTestLinkJavaAPIProperties(), listener );
		
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
			
			final EnvVars envVars = build.getEnvironment( listener );
			
			final boolean success = CommandExecutor.executeCommand(build, listener, 
					isUnix, envVars, beforeSingleTestCommand, 
					singleTestCommand, afterSingleTestCommand); 
			
			this.failure = !success;
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
					
					automatedTestCase.setExecutionStatus( ExecutionStatus.BLOCKED );
				} 
				else
				{
					// Build environment variables
					final EnvVars iterativeEnvVars = TestLinkHelper.buildTestCaseEnvVars( automatedTestCase, project, testPlan, testLinkBuild, listener ); 
					
					iterativeEnvVars.putAll( build.getEnvironment( listener ) );
					
					listener.getLogger().println( Messages.TestLinkBuilder_ExecutingIterativeTestCommand( this.iterativeTestCommand ) );
					listener.getLogger().println();
					
					// Execute iterative test command with pre and post commands
					final boolean success = CommandExecutor.executeCommand( build, 
							listener, isUnix, iterativeEnvVars,
							beforeIterativeTestCommand, iterativeTestCommand, 
							afterIterativeTestCommand); 
					
					this.failure = ! success;
				}
			}
		}
		else
		{
			listener.getLogger().println( Messages.TestLinkBuilder_BlankIterativeTestCommand() );
			listener.getLogger().println();
		}
	}
	
}
