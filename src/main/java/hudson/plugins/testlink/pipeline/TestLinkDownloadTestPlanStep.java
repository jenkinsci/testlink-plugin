package hudson.plugins.testlink.pipeline;

import hudson.Extension;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.Serializable;

/**
 * Created by azikha01 on 11/10/2016.
 */
public class TestLinkDownloadTestPlanStep extends AbstractStepImpl implements Serializable {
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

    @DataBoundSetter
    private String customFields = null;

    @DataBoundSetter
    private String testPlanFileName = null;

    @DataBoundConstructor
    public TestLinkDownloadTestPlanStep(){

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

    public String getCustomFields(){ return customFields; }

    public String getTestPlanFileName() {return testPlanFileName; }

    @Extension
    public static final class DescriptorImpl extends AbstractStepDescriptorImpl {
        public static final String STEP_NAME = "testLinkDownloadTestPlan";

        public DescriptorImpl(){
            super(TestLinkDownloadTestPlanExecution.class);

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
