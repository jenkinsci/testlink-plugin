package hudson.plugins.testlink.pipeline;

import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.util.TestLinkAPIException;
import com.google.inject.Inject;
import hudson.AbortException;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.testlink.*;
import hudson.plugins.testlink.result.JUnitCaseClassNameResultSeeker;
import hudson.plugins.testlink.result.ResultSeeker;
import hudson.plugins.testlink.result.ResultSeekerException;
import hudson.plugins.testlink.result.TestCaseWrapper;
import hudson.tasks.BuildStep;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by azikha01 on 11/10/2016.
 */
public class TestLinkUploadTestResultsExecution extends AbstractSynchronousNonBlockingStepExecution<Void> {

    @Inject
    private transient TestLinkUploadTestResultsStep config;

    @StepContextParameter
    private transient TaskListener listener;

    @StepContextParameter
    private transient Launcher launcher;

    @StepContextParameter
    private transient Run<?, ?> build;

    @StepContextParameter
    private transient FilePath workspace;

    @Override
    protected Void run() throws Exception{
        listener.getLogger().println(config.getTestLinkName() + " \n" +
                config.getTestProjectName() + " \n" +
                config.getTestPlanName() + " \n" +
                config.getTestPlanCustomFields() + " \n" +
                config.getBuildName() + " \n" +
                config.getBuildCustomFields() + " \n" +
                config.getCustomFields() + " \n" +
                config.getTestProjectName() + " \n" +
                config.getResultsFilePattern() + " \n"
        );

        TestLinkBuilderDescriptor DESCRIPTOR = new TestLinkBuilderDescriptor();
        final TestLinkInstallation installation = DESCRIPTOR
                .getInstallationByTestLinkName(config.getTestLinkName());
        if (installation == null) {
            throw new AbortException("Failed to find TestLink installation: " + config.getTestLinkName());
        }

        final TestLinkSite testLinkSite;
        final String testLinkUrl = installation.getUrl();
        final String testLinkDevKey = installation.getDevKey();

        TestLinkBuilder builder = new TestLinkBuilder(config.getTestLinkName(),
                config.getTestProjectName(),
                config.getTestPlanName(),
                config.getPlatformName(),
                config.getBuildName(),
                config.getBuildCustomFields(),
                config.getCustomFields(),
                config.getTestPlanCustomFields(),
                false,
                false,
                false,
                false,
                new ArrayList<BuildStep>(),
                new ArrayList<BuildStep>(),
                new ArrayList<BuildStep>(),
                new ArrayList<BuildStep>(),
                false, true, false, false, new ArrayList< ResultSeeker>()
        );
        testLinkSite = builder.getTestLinkSite(testLinkUrl,
                testLinkDevKey,
                config.getTestProjectName(),
                config.getTestPlanName(),
                config.getPlatformName(),
                config.getBuildName(),
                config.getBuildCustomFields(),
                config.getBuildNotes());

        ArrayList<String> customFieldsList = new ArrayList<String>();
        for (String cf : config.getCustomFields().split(",")){
            customFieldsList.add(cf.trim());
        }
        String [] customFieldArray = new String[customFieldsList.size()];
        for (int i = 0;  i < customFieldsList.size(); i++){
            customFieldArray[i] = customFieldsList.get(i);
        }
        TestCase[] testCases = testLinkSite.getAutomatedTestCases(customFieldArray);
        List<TestCaseWrapper> automatedTestCases = new ArrayList<TestCaseWrapper>();
        for(TestCase testCase : testCases) {
            TestCaseWrapper wrapper = new TestCaseWrapper(testCase);
            automatedTestCases.add(wrapper);
        }
        TestCaseWrapper[] automatedTestCaseWrapper =  automatedTestCases.toArray(new TestCaseWrapper[0]);
        try {
            ResultSeeker rs = new JUnitCaseClassNameResultSeeker(config.getResultsFilePattern(),
                    config.getKeyCustomField(), false, true);
            rs.seek(automatedTestCaseWrapper, build, workspace, launcher, listener, testLinkSite);
        } catch (ResultSeekerException trse) {
            trse.printStackTrace(listener.fatalError(trse.getMessage()));
            throw new AbortException(trse.getMessage());
        } catch (TestLinkAPIException tlae) {
            tlae.printStackTrace(listener.fatalError(tlae.getMessage()));
            throw new AbortException(tlae.getMessage());
        }

        return null;
    }
}
