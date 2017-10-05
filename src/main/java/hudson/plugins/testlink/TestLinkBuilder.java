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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.Platform;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.model.TestProject;
import br.eti.kinoshita.testlinkjavaapi.util.TestLinkAPIException;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.model.EnvironmentContributingAction;
import hudson.model.Result;
import hudson.plugins.testlink.result.ResultSeeker;
import hudson.plugins.testlink.result.ResultSeekerException;
import hudson.plugins.testlink.result.TestCaseWrapper;
import hudson.plugins.testlink.util.Messages;
import hudson.plugins.testlink.util.TestLinkHelper;
import hudson.tasks.BuildStep;
import hudson.tasks.Builder;

/**
 * A builder to add a TestLink build step.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TestLinkBuilder extends AbstractTestLinkBuilder {

	private static final Logger LOGGER = Logger.getLogger("hudson.plugins.testlink");
	
	/**
	 * The Descriptor of this Builder. It contains the TestLink installation.
	 */
	@Extension
	public static final TestLinkBuilderDescriptor DESCRIPTOR = new TestLinkBuilderDescriptor();

	/**
	 * Kept here for backward compatibility. Don't add new fields.
	 * @deprecated
	 */
    public TestLinkBuilder(String testLinkName, String testProjectName,
            String testPlanName, String buildName, String customFields,
            Boolean executionStatusNotRun, Boolean executionStatusPassed,
            Boolean executionStatusFailed, Boolean executionStatusBlocked,
            List<BuildStep> singleBuildSteps,
            List<BuildStep> beforeIteratingAllTestCasesBuildSteps,
            List<BuildStep> iterativeBuildSteps,
            List<BuildStep> afterIteratingAllTestCasesBuildSteps,
            Boolean transactional, Boolean failedTestsMarkBuildAsFailure,
            Boolean failIfNoResults, List<ResultSeeker> resultSeekers) {
        this(testLinkName, testProjectName, testPlanName, buildName,
                null, customFields, executionStatusNotRun, executionStatusPassed,
                executionStatusFailed, executionStatusBlocked, singleBuildSteps,
                beforeIteratingAllTestCasesBuildSteps, iterativeBuildSteps,
                afterIteratingAllTestCasesBuildSteps, transactional,
                failedTestsMarkBuildAsFailure, failIfNoResults, false, resultSeekers);
    }
	
    /**
     * Kept here for backward compatibility. Don't add new fields.
     * @deprecated
     */
    public TestLinkBuilder(String testLinkName, String testProjectName,
            String testPlanName, String buildName, String customFields,
            Boolean executionStatusNotRun, Boolean executionStatusPassed,
            Boolean executionStatusFailed, Boolean executionStatusBlocked,
            List<BuildStep> singleBuildSteps,
            List<BuildStep> beforeIteratingAllTestCasesBuildSteps,
            List<BuildStep> iterativeBuildSteps,
            List<BuildStep> afterIteratingAllTestCasesBuildSteps,
            Boolean transactional, Boolean failedTestsMarkBuildAsFailure,
            Boolean failIfNoResults, Boolean failOnNotRun, List<ResultSeeker> resultSeekers) {
        super(testLinkName, testProjectName, testPlanName, buildName, null, 
                customFields, executionStatusNotRun, executionStatusPassed,
                executionStatusFailed, executionStatusBlocked, singleBuildSteps,
                beforeIteratingAllTestCasesBuildSteps, iterativeBuildSteps,
                afterIteratingAllTestCasesBuildSteps, transactional,
                failedTestsMarkBuildAsFailure, failIfNoResults, failOnNotRun, resultSeekers);
    }

    /**
     * Kept here for backward compatibility. Don't add new fields.
     * @deprecated to add test plan custom fields
     */
    public TestLinkBuilder(String testLinkName, String testProjectName,
            String testPlanName, String platformName, String buildName, String customFields,
            Boolean executionStatusNotRun, Boolean executionStatusPassed,
            Boolean executionStatusFailed, Boolean executionStatusBlocked,
            List<BuildStep> singleBuildSteps,
            List<BuildStep> beforeIteratingAllTestCasesBuildSteps,
            List<BuildStep> iterativeBuildSteps,
            List<BuildStep> afterIteratingAllTestCasesBuildSteps,
            Boolean transactional, Boolean failedTestsMarkBuildAsFailure,
            Boolean failIfNoResults, Boolean failOnNotRun, List<ResultSeeker> resultSeekers) {
        super(testLinkName, testProjectName, testPlanName, platformName, buildName,
                customFields, singleBuildSteps, beforeIteratingAllTestCasesBuildSteps, iterativeBuildSteps,
                afterIteratingAllTestCasesBuildSteps, transactional, failedTestsMarkBuildAsFailure, 
                failIfNoResults, failOnNotRun, resultSeekers);
    }

	@DataBoundConstructor
	public TestLinkBuilder(String testLinkName, String testProjectName,
			String testPlanName, String platformName, String buildName, String customFields, String testPlanCustomFields,
			Boolean executionStatusNotRun, Boolean executionStatusPassed,
			Boolean executionStatusFailed, Boolean executionStatusBlocked,
			List<BuildStep> singleBuildSteps,
			List<BuildStep> beforeIteratingAllTestCasesBuildSteps,
			List<BuildStep> iterativeBuildSteps,
			List<BuildStep> afterIteratingAllTestCasesBuildSteps,
			Boolean transactional, Boolean failedTestsMarkBuildAsFailure,
			Boolean failIfNoResults, Boolean failOnNotRun, List<ResultSeeker> resultSeekers) {
		super(testLinkName, testProjectName, testPlanName, platformName, buildName,
				customFields, testPlanCustomFields, singleBuildSteps, beforeIteratingAllTestCasesBuildSteps, iterativeBuildSteps,
				afterIteratingAllTestCasesBuildSteps, transactional, failedTestsMarkBuildAsFailure, 
				failIfNoResults, failOnNotRun, resultSeekers);
	}

	/**
	 * Called when the job is executed.
	 */
	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener) throws InterruptedException, IOException {
		
		LOGGER.log(Level.INFO, "TestLink builder started");
		
		this.failure = false;

		// TestLink installation
		listener.getLogger().println(Messages.TestLinkBuilder_PreparingTLAPI());
		final TestLinkInstallation installation = DESCRIPTOR
				.getInstallationByTestLinkName(this.testLinkName);
		if (installation == null) {
			throw new AbortException(Messages.TestLinkBuilder_InvalidTLAPI());
		}

		TestLinkHelper.setTestLinkJavaAPIProperties(installation.getTestLinkJavaAPIProperties(), listener);

		final TestLinkSite testLinkSite;
		final TestCaseWrapper[] automatedTestCases;
		final String testLinkUrl = installation.getUrl();
		final String testLinkDevKey = installation.getDevKey();
		TestPlan testPlan;
		listener.getLogger().println(Messages.TestLinkBuilder_UsedTLURL(testLinkUrl));

		try {
			final String testProjectName = TestLinkHelper.expandVariable(build.getBuildVariableResolver(),
					build.getEnvironment(listener), getTestProjectName());
			final String testPlanName = TestLinkHelper.expandVariable(build.getBuildVariableResolver(),
					build.getEnvironment(listener), getTestPlanName());
			final String platformName = TestLinkHelper.expandVariable(build.getBuildVariableResolver(),
					build.getEnvironment(listener), getPlatformName());
			final String buildName = TestLinkHelper.expandVariable(build.getBuildVariableResolver(),
					build.getEnvironment(listener), getBuildName());
			final String buildNotes = Messages.TestLinkBuilder_Build_Notes();
			if(LOGGER.isLoggable(Level.FINE)) {
				LOGGER.log(Level.FINE, "TestLink project name: ["+testProjectName+"]");
				LOGGER.log(Level.FINE, "TestLink plan name: ["+testPlanName+"]");
				LOGGER.log(Level.FINE, "TestLink platform name: ["+platformName+"]");
				LOGGER.log(Level.FINE, "TestLink build name: ["+buildName+"]");
				LOGGER.log(Level.FINE, "TestLink build notes: ["+buildNotes+"]");
			}
			// TestLink Site object
			testLinkSite = this.getTestLinkSite(testLinkUrl, testLinkDevKey, testProjectName, testPlanName, platformName, buildName, buildNotes);
			
			if (StringUtils.isNotBlank(platformName) && testLinkSite.getPlatform() == null) 
			    listener.getLogger().println(Messages.TestLinkBuilder_PlatformNotFound(platformName));
			
			final String[] testCaseCustomFieldsNames = TestLinkHelper.createArrayOfCustomFieldsNames(build.getBuildVariableResolver(), build.getEnvironment(listener), this.getCustomFields());
			// Array of automated test cases
			TestCase[] testCases = testLinkSite.getAutomatedTestCases(testCaseCustomFieldsNames);

			// Retrieve custom fields in test plan
			final String[] testPlanCustomFieldsNames = TestLinkHelper.createArrayOfCustomFieldsNames(build.getBuildVariableResolver(), build.getEnvironment(listener), this.getTestPlanCustomFields());
			testPlan = testLinkSite.getTestPlanWithCustomFields(testPlanCustomFieldsNames);

			// Transforms test cases into test case wrappers
			automatedTestCases = this.transform(testCases);
			
			testCases = null;

			listener.getLogger().println(Messages.TestLinkBuilder_ShowFoundAutomatedTestCases(automatedTestCases.length));

			// Sorts test cases by each execution order (this info comes from
			// TestLink)
			listener.getLogger().println(Messages.TestLinkBuilder_SortingTestCases());
			Arrays.sort(automatedTestCases, this.executionOrderComparator);
		} catch (MalformedURLException mue) {
			mue.printStackTrace(listener.fatalError(mue.getMessage()));
			throw new AbortException(Messages.TestLinkBuilder_InvalidTLURL(testLinkUrl));
		} catch (TestLinkAPIException e) {
			e.printStackTrace(listener.fatalError(e.getMessage()));
			throw new AbortException(Messages.TestLinkBuilder_TestLinkCommunicationError());
		}
		
		for(TestCaseWrapper tcw : automatedTestCases) {
			testLinkSite.getReport().addTestCase(tcw);
			if(LOGGER.isLoggable(Level.FINE)) {
				LOGGER.log(Level.FINE, "TestLink automated test case ID [" + tcw.getId() + "], name [" + tcw.getName() + "], platform [" + tcw.getPlatform() + "]");
			}
		}
		
		listener.getLogger().println(Messages.TestLinkBuilder_ExecutingSingleBuildSteps());
		this.executeSingleBuildSteps(automatedTestCases.length, testPlan, testLinkSite, build, launcher, listener);

		listener.getLogger().println(Messages.TestLinkBuilder_ExecutingIterativeBuildSteps());
		this.executeIterativeBuildSteps(automatedTestCases, testPlan, testLinkSite, build, launcher, listener);

		// Here we search for test results. The return if a wrapped Test Case
		// that
		// contains attachments, platform and notes.
		try {
			listener.getLogger().println(Messages.Results_LookingForTestResults());
			
			if(getResultSeekers() != null) {
				for (ResultSeeker resultSeeker : getResultSeekers()) {
					LOGGER.log(Level.INFO, "Seeking test results. Using: " + resultSeeker.getDescriptor().getDisplayName());
					resultSeeker.seek(automatedTestCases, build, launcher, listener, testLinkSite);
				}
			}
		} catch (ResultSeekerException trse) {
			trse.printStackTrace(listener.fatalError(trse.getMessage()));
			throw new AbortException(Messages.Results_ErrorToLookForTestResults(trse.getMessage()));
		} catch (TestLinkAPIException tlae) {
			tlae.printStackTrace(listener.fatalError(tlae.getMessage()));
			throw new AbortException(Messages.TestLinkBuilder_FailedToUpdateTL(tlae.getMessage()));
		}

		// This report is used to generate the graphs and to store the list of
		// test cases with each found status.
		final Report report = testLinkSite.getReport();
		report.tally();
		
		listener.getLogger().println(Messages.TestLinkBuilder_ShowFoundTestResults(report.getTestsTotal()));
		
		final TestLinkResult result = new TestLinkResult(report, build);
		final TestLinkBuildAction buildAction = new TestLinkBuildAction(build, result);
		build.addAction(buildAction);
		
		if(report.getTestsTotal() <= 0 && this.getFailIfNoResults() == Boolean.TRUE) {
			listener.getLogger().println("No test results found. Setting the build result as FAILURE.");
			build.setResult(Result.FAILURE);
		} else if (report.getFailed() > 0) {
			if (this.failedTestsMarkBuildAsFailure != null && this.failedTestsMarkBuildAsFailure) {
			    listener.getLogger().println("There are failed tests, setting the build result as FAILURE.");
				build.setResult(Result.FAILURE);
			} else {
			    listener.getLogger().println("There are failed tests, setting the build result as UNSTABLE.");
				build.setResult(Result.UNSTABLE);
			}
		} else if (this.getFailOnNotRun() != null && this.getFailOnNotRun() && report.getNotRun() > 0) {
		    listener.getLogger().println("There are not run tests, setting the build result as FAILURE.");
		    build.setResult(Result.FAILURE);
		}

		LOGGER.log(Level.INFO, "TestLink builder finished");
		
		// end
		return Boolean.TRUE;
	}

	/**
	 * @param testCases
	 * @return
	 */
	private TestCaseWrapper[] transform(TestCase[] testCases) {
		if(testCases == null || testCases.length == 0) {
			return new TestCaseWrapper[0];
		}
		
		List<TestCaseWrapper> automatedTestCases = new ArrayList<TestCaseWrapper>();
		for(TestCase testCase : testCases) {
			TestCaseWrapper wrapper = new TestCaseWrapper(testCase);
			automatedTestCases.add(wrapper);
		}
		return automatedTestCases.toArray(new TestCaseWrapper[0]);
	}

	/**
	 * Gets object to interact with TestLink site.
	 * 
	 * @throws MalformedURLException
	 */
	public TestLinkSite getTestLinkSite(String testLinkUrl, String testLinkDevKey, 
	        String testProjectName, String testPlanName, String platformName, 
	        String buildName, String buildNotes) throws MalformedURLException {
		final TestLinkAPI api;
		final URL url = new URL(testLinkUrl);
		api = new TestLinkAPI(url, testLinkDevKey);

		final TestProject testProject = api.getTestProjectByName(testProjectName);
		final TestPlan testPlan = api.getTestPlanByName(testPlanName, testProjectName);

		Platform platform = null;
		if (StringUtils.isNotBlank(platformName)){
			final Platform platforms[] = api.getProjectPlatforms(testProject.getId());		
			for (Platform p : platforms) {
				if (p.getName().equals(platformName)) {
				    platform = p;
					break;
				}
			}
		}

		final Build build = api.createBuild(testPlan.getId(), buildName, buildNotes);
		return new TestLinkSite(api, testProject, testPlan, platform, build);
	}

	/**
	 * Executes the list of single build steps.
	 *
	 * @param numberOfTests number of tests 
	 * @param testLinkSite TestLink site
	 * @param build Jenkins build.
	 * @param launcher job launcher
	 * @param listener build listener
	 * @throws IOException
	 * @throws InterruptedException
	 */
	protected void executeSingleBuildSteps(int numberOfTests, TestPlan testPlan, TestLinkSite testLinkSite, AbstractBuild<?, ?> build,
			Launcher launcher, BuildListener listener) throws IOException,
			InterruptedException {
		if (singleBuildSteps != null) {
			for (BuildStep b : singleBuildSteps) {
			    final EnvVars iterativeEnvVars = TestLinkHelper.buildTestCaseEnvVars(
			            numberOfTests, 
                        testLinkSite.getTestProject(),
                        testPlan,
                        testLinkSite.getBuild(), 
                        listener);
                build.addAction(new EnvironmentContributingAction() {
                    public void buildEnvVars(AbstractBuild<?, ?> build, EnvVars env) {
                        env.putAll(iterativeEnvVars);
                    }
                    public String getUrlName() {
                        return null;
                    }
                    public String getIconFileName() {
                        return null;
                    }
                    public String getDisplayName() {
                        return null;
                    }
                });
				final boolean success = b.perform(build, launcher, listener);
				if (!success) {
					this.failure = Boolean.TRUE;
				}
			}
		}
	}

	/**
	 * <p>
	 * Executes iterative build steps. For each automated test case found in the
	 * array of automated test cases, this method executes the iterative builds
	 * steps using Jenkins objects.
	 * </p>
	 * 
	 * @param automatedTestCases
	 *            array of automated test cases
	 * @param testLinkSite
	 *            The TestLink Site object
	 * @param launcher
	 * @param listener
	 * @throws InterruptedException
	 * @throws IOException
	 */
	protected void executeIterativeBuildSteps(TestCaseWrapper[] automatedTestCases,
											  TestPlan testPlan,
			TestLinkSite testLinkSite, AbstractBuild<?, ?> build,
			Launcher launcher, BuildListener listener) throws IOException,
			InterruptedException {

		if (beforeIteratingAllTestCasesBuildSteps != null) {
			for (BuildStep b : beforeIteratingAllTestCasesBuildSteps) {
				final boolean success = b.perform(build, launcher, listener);
				if (!success) {
					this.failure = Boolean.TRUE;
				}
			}
		}

		for (TestCaseWrapper automatedTestCase : automatedTestCases) {
			if (this.failure && this.transactional) {
				automatedTestCase.setExecutionStatus(ExecutionStatus.BLOCKED);
			} else {
				if (iterativeBuildSteps != null) {
					final EnvVars iterativeEnvVars = TestLinkHelper.buildTestCaseEnvVars(automatedTestCase,
									testLinkSite.getTestProject(),
									testPlan,
									testLinkSite.getBuild(), listener);

					build.addAction(new EnvironmentContributingAction() {
						public void buildEnvVars(AbstractBuild<?, ?> build, EnvVars env) {
							env.putAll(iterativeEnvVars);
						}
						public String getUrlName() {
							return null;
						}
						public String getIconFileName() {
							return null;
						}
						public String getDisplayName() {
							return null;
						}
					});
					for (BuildStep b : iterativeBuildSteps) {
						final boolean success = b.perform(build, launcher, listener);
						if (!success) {
							this.failure = Boolean.TRUE;
						}
					}
				}
			}
		}

		if (afterIteratingAllTestCasesBuildSteps != null) {
			for (BuildStep b : afterIteratingAllTestCasesBuildSteps) {
				final boolean success = b.perform(build, launcher, listener);
				if (!success) {
					this.failure = Boolean.TRUE;
				}
			}
		}
	}
	
	@Override
	public Descriptor<Builder> getDescriptor() {
	    return (TestLinkBuilderDescriptor) super.getDescriptor();
	}
}
