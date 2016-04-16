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

import java.util.List;

import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.plugins.testlink.result.ResultSeeker;
import hudson.plugins.testlink.util.ExecutionOrderComparator;
import hudson.tasks.BuildStep;
import hudson.tasks.Builder;

/**
 * Contains basic logic for a Builder for TestLink plug-in. This class was created to reduce complexity and reduce the
 * length of the code present in the Builder itself.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.4
 */
public class AbstractTestLinkBuilder extends Builder {

    /* --- Job properties --- */
    /**
     * The name of the TestLink installation.
     */
    protected final String testLinkName;
    /**
     * The name of the Test Project.
     */
    protected final String testProjectName;
    /**
     * The name of the Test Plan.
     */
    protected final String testPlanName;
    /**
     * The name of the Build.
     */
    protected String buildName;
    /**
     * The platform name.
     */
    protected final String platformName;
    /**
     * Comma separated list of custom fields to download from TestLink.
     */
    protected final String customFields;

    /**
     * Comma separated list of test plan custom fields to download from TestLink.
     */
    protected final String testPlanCustomFields;

    /**
     * List of build steps that are executed only once per job execution.
     */
    protected final List<BuildStep> singleBuildSteps;

    /**
     * List of build steps that are executed before iterating all test cases.
     */
    protected final List<BuildStep> beforeIteratingAllTestCasesBuildSteps;

    /**
     * List of build steps that are executed for each test case.
     */
    protected final List<BuildStep> iterativeBuildSteps;

    /**
     * List of build steps that are executed after iterating all test cases.
     */
    protected final List<BuildStep> afterIteratingAllTestCasesBuildSteps;

    /**
     * If this property is true, not more build steps are executed for this Build.
     */
    protected final Boolean transactional;

    /**
     * If the plug-in should mark the Build in Jenkins as failure if it contains failed tests.
     */
    protected final Boolean failedTestsMarkBuildAsFailure;

    /**
     * Fail the build if no test results are present.
     */
    protected final Boolean failIfNoResults;

    /**
     * Create failure if any of the tests are set as not-run
     */
    protected final Boolean failOnNotRun;

    /* --- Other members --- */

    /**
     * Used to sort test cases marked as automated.
     */
    protected final ExecutionOrderComparator executionOrderComparator = new ExecutionOrderComparator();

    /**
     * Flag to check if any failure happened.
     */
    protected boolean failure = false;

    /**
     * Results seekers.
     */
    private List<ResultSeeker> resultSeekers;

    /**
     * Create a AbstractTestLinkBuilder.
     */
    public AbstractTestLinkBuilder(String testLinkName, String testProjectName, String testPlanName, 
            String platformName, String buildName, String customFields, String testPlanCustomFields, List<BuildStep> singleBuildSteps,
            List<BuildStep> beforeIteratingAllTestCasesBuildSteps, List<BuildStep> iterativeBuildSteps,
            List<BuildStep> afterIteratingAllTestCasesBuildSteps, Boolean transactional,
            Boolean failedTestsMarkBuildAsFailure, Boolean failIfNoResults, Boolean failOnNotRun,
            List<ResultSeeker> resultSeekers) {
        super();
        this.testLinkName = testLinkName;
        this.testProjectName = testProjectName;
        this.testPlanName = testPlanName;
        this.platformName = platformName;
        this.buildName = buildName;
        this.customFields = customFields;
        this.testPlanCustomFields = testPlanCustomFields;
        this.singleBuildSteps = singleBuildSteps;
        this.beforeIteratingAllTestCasesBuildSteps = beforeIteratingAllTestCasesBuildSteps;
        this.iterativeBuildSteps = iterativeBuildSteps;
        this.afterIteratingAllTestCasesBuildSteps = afterIteratingAllTestCasesBuildSteps;
        this.transactional = transactional;
        this.failedTestsMarkBuildAsFailure = failedTestsMarkBuildAsFailure;
        this.failIfNoResults = failIfNoResults;
        this.failOnNotRun = failOnNotRun;
        this.resultSeekers = resultSeekers;
    }

    /**
     * Create a AbstractTestLinkBuilder.
     * @deprecated to add test plan custom fields
     */
    public AbstractTestLinkBuilder(String testLinkName, String testProjectName, String testPlanName, 
            String platformName, String buildName, String customFields, List<BuildStep> singleBuildSteps,
            List<BuildStep> beforeIteratingAllTestCasesBuildSteps, List<BuildStep> iterativeBuildSteps,
            List<BuildStep> afterIteratingAllTestCasesBuildSteps, Boolean transactional,
            Boolean failedTestsMarkBuildAsFailure, Boolean failIfNoResults, Boolean failOnNotRun,
            List<ResultSeeker> resultSeekers) {
        this(testLinkName, testProjectName, testPlanName, platformName, buildName, customFields,
                /*testPlanCustomFields*/ null, singleBuildSteps, beforeIteratingAllTestCasesBuildSteps,
                iterativeBuildSteps, afterIteratingAllTestCasesBuildSteps, transactional, failedTestsMarkBuildAsFailure,
                failIfNoResults, failOnNotRun, resultSeekers);
    }

    /**
     * This constructor is bound to a stapler request. All parameters here are passed by Jenkins.
     * 
     * @param testLinkName TestLink Installation name.
     * @param testProjectName TestLink Test Project name.
     * @param testPlanName TestLink Test Plan name.
     * @param platformName TestLink Platform name.
     * @param buildName TestLink Build name.
     * @param customFields TestLink comma-separated list of Custom Fields.
     * @param keyCustomField Key custom field.
     * @param singleBuildSteps List of build steps to execute once for all automated test cases.
     * @param beforeIteratingAllTestCasesBuildSteps Command executed before iterating all test cases.
     * @param iterativeBuildSteps List of build steps to execute for each Automated Test Case.
     * @param afterIteratingAllTestCasesBuildSteps Command executed after iterating all test cases.
     * @param transactional Whether the build's execution is transactional or not.
     * @param failedTestsMarkBuildAsFailure Whether failed tests mark the build as failure or not.
     * @param failIfNoResults If true marks the build as FAILURE.
     * @param resultSeekers List of result seekers.
     * @deprecated
     */
    public AbstractTestLinkBuilder(String testLinkName, String testProjectName, String testPlanName,
            String platformName, String buildName, String customFields, Boolean executionStatusNotRun,
            Boolean executionStatusPassed, Boolean executionStatusFailed, Boolean executionStatusBlocked,
            List<BuildStep> singleBuildSteps, List<BuildStep> beforeIteratingAllTestCasesBuildSteps,
            List<BuildStep> iterativeBuildSteps, List<BuildStep> afterIteratingAllTestCasesBuildSteps,
            Boolean transactional, Boolean failedTestsMarkBuildAsFailure, Boolean failIfNoResults,
            Boolean failOnNotRun, List<ResultSeeker> resultSeekers) {
        this(testLinkName, testProjectName, testPlanName, platformName, buildName, customFields,
             /*testPlanCustomFields*/ null, singleBuildSteps, beforeIteratingAllTestCasesBuildSteps,
             iterativeBuildSteps, afterIteratingAllTestCasesBuildSteps, transactional, failedTestsMarkBuildAsFailure,
             failIfNoResults, failOnNotRun, resultSeekers);
    }

    public String getTestLinkName() {
        return this.testLinkName;
    }

    public String getTestProjectName() {
        return this.testProjectName;
    }

    public String getTestPlanName() {
        return this.testPlanName;
    }

    public String getPlatformName() {
        return this.platformName;
    }

    public String getBuildName() {
        return this.buildName;
    }

    public String getCustomFields() {
        return this.customFields;
    }

    public String getTestPlanCustomFields(){
        return this.testPlanCustomFields;
    }

    /**
     * @deprecated
     */
    public Boolean getExecutionStatusNotRun() {
        return null;
    }

    /**
     * @deprecated
     */
    public Boolean getExecutionStatusPassed() {
        return null;
    }

    /**
     * @deprecated
     */
    public Boolean getExecutionStatusFailed() {
        return null;
    }

    /**
     * @deprecated
     */
    public Boolean getExecutionStatusBlocked() {
        return null;
    }

    public List<BuildStep> getSingleBuildSteps() {
        return this.singleBuildSteps;
    }

    public List<BuildStep> getBeforeIteratingAllTestCasesBuildSteps() {
        return beforeIteratingAllTestCasesBuildSteps;
    }

    public List<BuildStep> getIterativeBuildSteps() {
        return this.iterativeBuildSteps;
    }

    public List<BuildStep> getAfterIteratingAllTestCasesBuildSteps() {
        return afterIteratingAllTestCasesBuildSteps;
    }

    /**
     * Returns whether it is a transactional build or not. A transactional build stops executing once a test fails. All
     * tests must succeed or it won't finish its execution and will mark all remaining tests with Blocked status.
     * 
     * @return If the build step should be transactional or not
     */
    public Boolean getTransactional() {
        return this.transactional;
    }

    /**
     * @return the failedTestsMarkBuildAsUnstable
     */
    public Boolean getFailedTestsMarkBuildAsUnstable() {
        return failedTestsMarkBuildAsFailure;
    }

    /**
     * @return the failIfNoResults
     */
    public Boolean getFailIfNoResults() {
        return failIfNoResults;
    }

    /**
     * @return the failOnNotRun
     */
    public Boolean getFailOnNotRun() {
        return failOnNotRun;
    }

    /**
     * @return the resultSeekers
     */
    public List<ResultSeeker> getResultSeekers() {
        return resultSeekers;
    }

    /**
     * @param resultSeekers the resultSeekers to set
     */
    public void setResultSeekers(List<ResultSeeker> resultSeekers) {
        this.resultSeekers = resultSeekers;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hudson.tasks.BuildStepCompatibilityLayer#getProjectAction(hudson.model.AbstractProject)
     */
    @Override
    public Action getProjectAction(AbstractProject<?, ?> project) {
        return new TestLinkProjectAction(project);
    }

}
