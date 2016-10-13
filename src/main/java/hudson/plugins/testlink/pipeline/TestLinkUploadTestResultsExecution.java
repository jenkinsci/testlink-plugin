package hudson.plugins.testlink.pipeline;

import com.google.inject.Inject;
import hudson.AbortException;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.testlink.TestLinkBuilder;
import hudson.plugins.testlink.TestLinkBuilderDescriptor;
import hudson.plugins.testlink.TestLinkInstallation;
import hudson.plugins.testlink.TestLinkSite;
import hudson.plugins.testlink.result.ResultSeeker;
import hudson.plugins.testlink.util.TestLinkHelper;
import hudson.tasks.BuildStep;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;

import java.util.ArrayList;

/**
 * Created by azikha01 on 11/10/2016.
 */
public class TestLinkUploadTestResultsExecution extends AbstractSynchronousNonBlockingStepExecution<Void>{

    @Inject
    private transient TestLinkUploadTestResultsStep config;

    @StepContextParameter
    private transient TaskListener listener;

    @StepContextParameter
    private transient Launcher launcher;

    @StepContextParameter
    private transient Run<?, ?> build;

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
                config.getResultsFilePath() + " \n"
        );

        TestLinkBuilderDescriptor DESCRIPTOR = new TestLinkBuilderDescriptor();
        final TestLinkInstallation installation = DESCRIPTOR
                .getInstallationByTestLinkName(config.getTestLinkName());
        if (installation == null) {
            throw new AbortException("Failed to find TestLink installation: " + config.getTestLinkName());
        }
        TestLinkHelper.setTestLinkJavaAPIProperties(installation.getTestLinkJavaAPIProperties(), listener);

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

        return null;
    }
}
