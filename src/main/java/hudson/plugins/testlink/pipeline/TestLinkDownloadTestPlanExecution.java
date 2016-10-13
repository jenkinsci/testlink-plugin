package hudson.plugins.testlink.pipeline;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionType;
import br.eti.kinoshita.testlinkjavaapi.constants.ResponseDetails;
import br.eti.kinoshita.testlinkjavaapi.constants.TestCaseDetails;
import br.eti.kinoshita.testlinkjavaapi.model.*;
import com.google.inject.Inject;
import com.thoughtworks.xstream.io.json.JsonWriter;
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
import jdk.nashorn.internal.ir.debug.JSONWriter;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by azikha01 on 11/10/2016.
 */
public class TestLinkDownloadTestPlanExecution extends AbstractSynchronousNonBlockingStepExecution<Void>{

    @Inject
    private transient TestLinkDownloadTestPlanStep config;

    @StepContextParameter
    private transient TaskListener listener;

    @StepContextParameter
    private transient Launcher launcher;

    @StepContextParameter
    private transient Run<?, ?> build;

    @Override
    protected Void run() throws Exception{
        if (!(new File(config.getTestPlanFileName())).isAbsolute()){
            throw new Exception("Test plan path not absolute: " + config.getTestPlanFileName());
        }
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

        final URL url = new URL(testLinkUrl);
        TestLinkAPI api = new TestLinkAPI(url, testLinkDevKey);
        final TestProject testProject = api.getTestProjectByName(config.getTestProjectName());
        final TestPlan testPlan = api.getTestPlanByName(config.getTestPlanName(), config.getTestProjectName());

        String [] parts = config.getCustomFields().split(",");
        String [] customFields = new String[parts.length];
        for (int i = 0; i < parts.length; i++){
            customFields[i] = parts[i].trim();
        }

        JSONObject testPlanJson = new JSONObject();
        testPlanJson.put("name", testPlan.getName());
        testPlanJson.put("id", testPlan.getId());
        testPlanJson.put("project_name", testProject.getName());
        testPlanJson.put("project_id", testProject.getId());

        JSONArray testCasesJson = new JSONArray();

        final TestCase[] testCases = api.getTestCasesForTestPlan(
                testPlan.getId(),
                null,
                null,
                null,
                null,
                null,
                null,
                null, // execute status
                ExecutionType.AUTOMATED,
                Boolean.TRUE,
                TestCaseDetails.FULL);

        for( final TestCase testCase : testCases ) {
            JSONObject testCaseJson = new JSONObject();
            testCaseJson.put("name", testCase.getName());
            testCaseJson.put("id", testCase.getId());
            JSONArray customFieldsJson = new JSONArray();

            testCase.setTestProjectId(testProject.getId());
            testCase.setExecutionStatus(ExecutionStatus.NOT_RUN);
            if (customFields != null) {
                for(String customFieldName : customFields) {
                    final CustomField customField = api.getTestCaseCustomFieldDesignValue(
                            testCase.getId(),
                            null, /* testCaseExternalId */
                            testCase.getVersion(),
                            testProject.getId(),
                            customFieldName,
                            ResponseDetails.FULL);
                    testCase.getCustomFields().add(customField);
                    JSONObject customFieldJson = new JSONObject();
                    customFieldJson.put("name", customField.getName());
                    customFieldJson.put("value", customField.getValue());
                    customFieldsJson.add(customFieldJson);
                }
                testCaseJson.put("custom_fields", customFieldsJson);
            }
            listener.getLogger().println("Test Case: " + testCase.toString());
            testCasesJson.add(testCaseJson);
        }
        testPlanJson.put("test_cases", testCasesJson);
        testPlanJson.write(new FileWriter(config.getTestPlanFileName()));

        return null;
    }
}
