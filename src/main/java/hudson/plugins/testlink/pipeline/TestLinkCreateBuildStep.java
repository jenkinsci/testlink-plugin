package hudson.plugins.testlink.pipeline;

import java.io.Serializable;

import hudson.Extension;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import static hudson.plugins.testlink.pipeline.TestLinkCreateBuildStep.DescriptorImpl.STEP_NAME;

/**
 * Created by azikha01 on 11/10/2016.
 */
public class TestLinkCreateBuildStep extends AbstractStepImpl implements Serializable {
    private static final long serialVersionUID = 1L;

    /* --- Step properties --- */
    /**
     * The name of the TestLink installation.
     */
    @DataBoundSetter
    private String testLinkName = "(Default)";
    /**
     * The name of the Test Project.
     */
    @DataBoundSetter
    private String testProjectName = null;
    /**
     * The name of the Test Plan.
     */
    @DataBoundSetter
    private String testPlanName = null;
    /**
     * The name of the Build.
     */
    @DataBoundSetter
    private String buildName = null;
    /**
     * The Build custom fields.
     */
    @DataBoundSetter
    private String buildCustomFields = null;
    /**
     * The platform name.
     */
    @DataBoundSetter
    private String platformName = null;
    /**
     * Comma separated list of custom fields to download from TestLink.
     */
    @DataBoundSetter
    private String customFields = null;

    /**
     * Comma separated list of test plan custom fields to download from TestLink.
     */
    @DataBoundSetter
    private String testPlanCustomFields = null;

    /**
     * Build Notes
     */
    @DataBoundSetter
    private String buildNotes = null;

    @DataBoundConstructor
    public TestLinkCreateBuildStep(){

    }

    public String getTestLinkName(){
        return testLinkName;
    }

    public String getTestProjectName(){
        return testProjectName;
    }

    public String getTestPlanName(){
        return testPlanName;
    }

    public String getBuildName(){
        return buildName;
    }

    public String getBuildCustomFields(){
        return buildCustomFields;
    }

    public String getPlatformName() {
        return platformName;
    }

    public String getCustomFields(){
        return customFields;
    }

    public String getTestPlanCustomFields(){
        return testPlanCustomFields;
    }

    public String getBuildNotes(){
        return buildNotes;
    }

    @Extension
    public static final class DescriptorImpl extends AbstractStepDescriptorImpl {
        public static final String STEP_NAME = "testLinkCreateBuild";

        public DescriptorImpl(){
            super(TestLinkCreateBuildExecution.class);

        }

        @Override
        public String getFunctionName(){
            return STEP_NAME;
        }

        @Override
        public String getDisplayName(){
            return STEP_NAME;
        }
    }
}
