// CHECKSTYLE:OFF

package hudson.plugins.testlink.util;

import org.jvnet.localizer.Localizable;
import org.jvnet.localizer.ResourceBundleHolder;

@SuppressWarnings({
    "",
    "PMD"
})
public class Messages {

    private final static ResourceBundleHolder holder = ResourceBundleHolder.get(Messages.class);

    /**
     * Test command execution failed: {0}.
     * 
     */
    public static String TestLinkBuilder_TestCommandError(Object arg1) {
        return holder.format("TestLinkBuilder.TestCommandError", arg1);
    }

    /**
     * Test command execution failed: {0}.
     * 
     */
    public static Localizable _TestLinkBuilder_TestCommandError(Object arg1) {
        return new Localizable(holder, "TestLinkBuilder.TestCommandError", arg1);
    }

    /**
     * TAP file [{0}].
     * 
     */
    public static String Results_TAP_AttachmentDescription(Object arg1) {
        return holder.format("Results.TAP.AttachmentDescription", arg1);
    }

    /**
     * TAP file [{0}].
     * 
     */
    public static Localizable _Results_TAP_AttachmentDescription(Object arg1) {
        return new Localizable(holder, "Results.TAP.AttachmentDescription", arg1);
    }

    /**
     * Looking for a test result that matches with key custom field [{0}] => [{1}].
     * 
     */
    public static String Results_TAP_LookingForTestResults(Object arg1, Object arg2) {
        return holder.format("Results.TAP.LookingForTestResults", arg1, arg2);
    }

    /**
     * Looking for a test result that matches with key custom field [{0}] => [{1}].
     * 
     */
    public static Localizable _Results_TAP_LookingForTestResults(Object arg1, Object arg2) {
        return new Localizable(holder, "Results.TAP.LookingForTestResults", arg1, arg2);
    }

    /**
     * Could not transform {0} report. Please report this issue to the plugin author.
     * 
     */
    public static String TestLinkBuilder_Parser_SAX_CouldNotTransform(Object arg1) {
        return holder.format("TestLinkBuilder.Parser.SAX.CouldNotTransform", arg1);
    }

    /**
     * Could not transform {0} report. Please report this issue to the plugin author.
     * 
     */
    public static Localizable _TestLinkBuilder_Parser_SAX_CouldNotTransform(Object arg1) {
        return new Localizable(holder, "TestLinkBuilder.Parser.SAX.CouldNotTransform", arg1);
    }

    /**
     * Could not intialize the XML parser. Please report this issue to the plugin author.
     * 
     */
    public static String TestLinkBuilder_Parser_SAX_CouldNotIntializeXMLParser() {
        return holder.format("TestLinkBuilder.Parser.SAX.CouldNotIntializeXMLParser");
    }

    /**
     * Could not intialize the XML parser. Please report this issue to the plugin author.
     * 
     */
    public static Localizable _TestLinkBuilder_Parser_SAX_CouldNotIntializeXMLParser() {
        return new Localizable(holder, "TestLinkBuilder.Parser.SAX.CouldNotIntializeXMLParser");
    }

    /**
     * Failed to parse TestNG XML [{0}]: {1}.
     * 
     */
    public static String Results_TestNG_ParsingFail(Object arg1, Object arg2) {
        return holder.format("Results.TestNG.ParsingFail", arg1, arg2);
    }

    /**
     * Failed to parse TestNG XML [{0}]: {1}.
     * 
     */
    public static Localizable _Results_TestNG_ParsingFail(Object arg1, Object arg2) {
        return new Localizable(holder, "Results.TestNG.ParsingFail", arg1, arg2);
    }

    /**
     * Using TestLink URL: {0}.
     * 
     */
    public static String TestLinkBuilder_UsedTLURL(Object arg1) {
        return holder.format("TestLinkBuilder.UsedTLURL", arg1);
    }

    /**
     * Using TestLink URL: {0}.
     * 
     */
    public static Localizable _TestLinkBuilder_UsedTLURL(Object arg1) {
        return new Localizable(holder, "TestLinkBuilder.UsedTLURL", arg1);
    }

    /**
     * Where {0}
     * 
     */
    public static String ReportSummary_Summary_Where(Object arg1) {
        return holder.format("ReportSummary.Summary.Where", arg1);
    }

    /**
     * Where {0}
     * 
     */
    public static Localizable _ReportSummary_Summary_Where(Object arg1) {
        return new Localizable(holder, "ReportSummary.Summary.Where", arg1);
    }

    /**
     * Retrieving list of custom fields for test case.
     * 
     */
    public static String TestLinkBuilder_Finder_RetrievingListOfCustomFields() {
        return holder.format("TestLinkBuilder.Finder.RetrievingListOfCustomFields");
    }

    /**
     * Retrieving list of custom fields for test case.
     * 
     */
    public static Localizable _TestLinkBuilder_Finder_RetrievingListOfCustomFields() {
        return new Localizable(holder, "TestLinkBuilder.Finder.RetrievingListOfCustomFields");
    }

    /**
     * A test failed in transactional execution. Skiping tests.
     * 
     */
    public static String TestLinkBuilder_TransactionalError() {
        return holder.format("TestLinkBuilder.TransactionalError");
    }

    /**
     * A test failed in transactional execution. Skiping tests.
     * 
     */
    public static Localizable _TestLinkBuilder_TransactionalError() {
        return new Localizable(holder, "TestLinkBuilder.TransactionalError");
    }

    /**
     * Version
     * 
     */
    public static String ReportSummary_Details_Version() {
        return holder.format("ReportSummary.Details.Version");
    }

    /**
     * Version
     * 
     */
    public static Localizable _ReportSummary_Details_Version() {
        return new Localizable(holder, "ReportSummary.Details.Version");
    }

    /**
     * Retrieving list of automated test cases from TestLink.
     * 
     */
    public static String TestLinkBuilder_Finder_RetrievingListOfAutomatedTestCases() {
        return holder.format("TestLinkBuilder.Finder.RetrievingListOfAutomatedTestCases");
    }

    /**
     * Retrieving list of automated test cases from TestLink.
     * 
     */
    public static Localizable _TestLinkBuilder_Finder_RetrievingListOfAutomatedTestCases() {
        return new Localizable(holder, "TestLinkBuilder.Finder.RetrievingListOfAutomatedTestCases");
    }

    /**
     * Veryfying JUnit test suite [{0}]. This suite contains [{1}] tests, [{2}] failures and [{3}] errors.
     * 
     */
    public static String Results_JUnit_VerifyingJUnitSuite(Object arg1, Object arg2, Object arg3, Object arg4) {
        return holder.format("Results.JUnit.VerifyingJUnitSuite", arg1, arg2, arg3, arg4);
    }

    /**
     * Veryfying JUnit test suite [{0}]. This suite contains [{1}] tests, [{2}] failures and [{3}] errors.
     * 
     */
    public static Localizable _Results_JUnit_VerifyingJUnitSuite(Object arg1, Object arg2, Object arg3, Object arg4) {
        return new Localizable(holder, "Results.JUnit.VerifyingJUnitSuite", arg1, arg2, arg3, arg4);
    }

    /**
     * Updating automated test case {0} with execution status {1}.
     * 
     */
    public static String TestLinkBuilder_Update_AutomatedTestCase(Object arg1, Object arg2) {
        return holder.format("TestLinkBuilder.Update.AutomatedTestCase", arg1, arg2);
    }

    /**
     * Updating automated test case {0} with execution status {1}.
     * 
     */
    public static Localizable _TestLinkBuilder_Update_AutomatedTestCase(Object arg1, Object arg2) {
        return new Localizable(holder, "TestLinkBuilder.Update.AutomatedTestCase", arg1, arg2);
    }

    /**
     * Parsing JUnit XML [{0}].
     * 
     */
    public static String Results_TestNG_Parsing(Object arg1) {
        return holder.format("Results.TestNG.Parsing", arg1);
    }

    /**
     * Parsing JUnit XML [{0}].
     * 
     */
    public static Localizable _Results_TestNG_Parsing(Object arg1) {
        return new Localizable(holder, "Results.TestNG.Parsing", arg1);
    }

    /**
     * Execution status
     * 
     */
    public static String ReportSummary_Details_ExecutionStatus() {
        return holder.format("ReportSummary.Details.ExecutionStatus");
    }

    /**
     * Execution status
     * 
     */
    public static Localizable _ReportSummary_Details_ExecutionStatus() {
        return new Localizable(holder, "ReportSummary.Details.ExecutionStatus");
    }

    /**
     * Verifying TestLink test case [{0}], ID [{1}].
     * 
     */
    public static String Results_TestNG_VerifyingTestLinkTestCase(Object arg1, Object arg2) {
        return holder.format("Results.TestNG.VerifyingTestLinkTestCase", arg1, arg2);
    }

    /**
     * Verifying TestLink test case [{0}], ID [{1}].
     * 
     */
    public static Localizable _Results_TestNG_VerifyingTestLinkTestCase(Object arg1, Object arg2) {
        return new Localizable(holder, "Results.TestNG.VerifyingTestLinkTestCase", arg1, arg2);
    }

    /**
     * Custom fields in this TestLink test case [{0}].
     * 
     */
    public static String Results_TAP_ListOfCustomFields(Object arg1) {
        return holder.format("Results.TAP.ListOfCustomFields", arg1);
    }

    /**
     * Custom fields in this TestLink test case [{0}].
     * 
     */
    public static Localizable _Results_TAP_ListOfCustomFields(Object arg1) {
        return new Localizable(holder, "Results.TAP.ListOfCustomFields", arg1);
    }

    /**
     * Found [{0}] TAP file(s).
     * 
     */
    public static String Results_TAP_NumberOfReportsFound(Object arg1) {
        return holder.format("Results.TAP.NumberOfReportsFound", arg1);
    }

    /**
     * Found [{0}] TAP file(s).
     * 
     */
    public static Localizable _Results_TAP_NumberOfReportsFound(Object arg1) {
        return new Localizable(holder, "Results.TAP.NumberOfReportsFound", arg1);
    }

    /**
     * Executing iterative test command: [{0}].
     * 
     */
    public static String TestLinkBuilder_ExecutingIterativeTestCommand(Object arg1) {
        return holder.format("TestLinkBuilder.ExecutingIterativeTestCommand", arg1);
    }

    /**
     * Executing iterative test command: [{0}].
     * 
     */
    public static Localizable _TestLinkBuilder_ExecutingIterativeTestCommand(Object arg1) {
        return new Localizable(holder, "TestLinkBuilder.ExecutingIterativeTestCommand", arg1);
    }

    /**
     * TestLink results
     * 
     */
    public static String TestLinkProjectAction_DisplayName() {
        return holder.format("TestLinkProjectAction.DisplayName");
    }

    /**
     * TestLink results
     * 
     */
    public static Localizable _TestLinkProjectAction_DisplayName() {
        return new Localizable(holder, "TestLinkProjectAction.DisplayName");
    }

    /**
     * 
     * 
     * Failed to add TAP attachments to this test case execution. Error message: {0}.
     * 
     */
    public static String Results_TAP_AddAttachmentsFail(Object arg1) {
        return holder.format("Results.TAP.AddAttachmentsFail", arg1);
    }

    /**
     * 
     * 
     * Failed to add TAP attachments to this test case execution. Error message: {0}.
     * 
     */
    public static Localizable _Results_TAP_AddAttachmentsFail(Object arg1) {
        return new Localizable(holder, "Results.TAP.AddAttachmentsFail", arg1);
    }

    /**
     * Failed to read TAP report file content and convert to Base64: {1}.
     * 
     */
    public static String TestLinkBuilder_Parser_TAP_AttachmentError(Object arg1, Object arg2) {
        return holder.format("TestLinkBuilder.Parser.TAP.AttachmentError", arg1, arg2);
    }

    /**
     * Failed to read TAP report file content and convert to Base64: {1}.
     * 
     */
    public static Localizable _TestLinkBuilder_Parser_TAP_AttachmentError(Object arg1, Object arg2) {
        return new Localizable(holder, "TestLinkBuilder.Parser.TAP.AttachmentError", arg1, arg2);
    }

    /**
     * Failed to update TestLink test results: {0}.
     * 
     */
    public static String TestLinkBuilder_FailedToUpdateTL(Object arg1) {
        return holder.format("TestLinkBuilder.FailedToUpdateTL", arg1);
    }

    /**
     * Failed to update TestLink test results: {0}.
     * 
     */
    public static Localizable _TestLinkBuilder_FailedToUpdateTL(Object arg1) {
        return new Localizable(holder, "TestLinkBuilder.FailedToUpdateTL", arg1);
    }

    /**
     * Parsing TAP file [{0}].
     * 
     */
    public static String Results_TAP_Parsing(Object arg1) {
        return holder.format("Results.TAP.Parsing", arg1);
    }

    /**
     * Parsing TAP file [{0}].
     * 
     */
    public static Localizable _Results_TAP_Parsing(Object arg1) {
        return new Localizable(holder, "Results.TAP.Parsing", arg1);
    }

    /**
     * Transactional execution failed.
     * 
     */
    public static String TestLinkBuilder_TransactionalExecutionFailedNotes() {
        return holder.format("TestLinkBuilder.TransactionalExecutionFailedNotes");
    }

    /**
     * Transactional execution failed.
     * 
     */
    public static Localizable _TestLinkBuilder_TransactionalExecutionFailedNotes() {
        return new Localizable(holder, "TestLinkBuilder.TransactionalExecutionFailedNotes");
    }

    /**
     * Setting system property {0}, value {1}.
     * 
     */
    public static String TestLinkBuilder_SettingSystemProperty(Object arg1, Object arg2) {
        return holder.format("TestLinkBuilder.SettingSystemProperty", arg1, arg2);
    }

    /**
     * Setting system property {0}, value {1}.
     * 
     */
    public static Localizable _TestLinkBuilder_SettingSystemProperty(Object arg1, Object arg2) {
        return new Localizable(holder, "TestLinkBuilder.SettingSystemProperty", arg1, arg2);
    }

    /**
     * Missing JUnit test class name.
     * 
     */
    public static String Results_JUnit_MissingJUnitTestClassName() {
        return holder.format("Results.JUnit.MissingJUnitTestClassName");
    }

    /**
     * Missing JUnit test class name.
     * 
     */
    public static Localizable _Results_JUnit_MissingJUnitTestClassName() {
        return new Localizable(holder, "Results.JUnit.MissingJUnitTestClassName");
    }

    /**
     * {0} file found. Parsing file to extract test results.
     * 
     */
    public static String TestLinkBuilder_Parser_FileFound(Object arg1) {
        return holder.format("TestLinkBuilder.Parser.FileFound", arg1);
    }

    /**
     * {0} file found. Parsing file to extract test results.
     * 
     */
    public static Localizable _TestLinkBuilder_Parser_FileFound(Object arg1) {
        return new Localizable(holder, "TestLinkBuilder.Parser.FileFound", arg1);
    }

    /**
     * tests were blocked.
     * 
     */
    public static String ReportSummary_Summary_TestsBlocked() {
        return holder.format("ReportSummary.Summary.TestsBlocked");
    }

    /**
     * tests were blocked.
     * 
     */
    public static Localizable _ReportSummary_Summary_TestsBlocked() {
        return new Localizable(holder, "ReportSummary.Summary.TestsBlocked");
    }

    /**
     * Looking for the test results of TestLink test cases.
     * 
     */
    public static String Results_LookingForTestResults() {
        return holder.format("Results.LookingForTestResults");
    }

    /**
     * Looking for the test results of TestLink test cases.
     * 
     */
    public static Localizable _Results_LookingForTestResults() {
        return new Localizable(holder, "Results.LookingForTestResults");
    }

    /**
     * Found [{0}] TestNG XML(s).
     * 
     */
    public static String Results_TestNG_NumberOfReportsFound(Object arg1) {
        return holder.format("Results.TestNG.NumberOfReportsFound", arg1);
    }

    /**
     * Found [{0}] TestNG XML(s).
     * 
     */
    public static Localizable _Results_TestNG_NumberOfReportsFound(Object arg1) {
        return new Localizable(holder, "Results.TestNG.NumberOfReportsFound", arg1);
    }

    /**
     * Build number
     * 
     */
    public static String ChartUtil_BuildNumber() {
        return holder.format("ChartUtil.BuildNumber");
    }

    /**
     * Build number
     * 
     */
    public static Localizable _ChartUtil_BuildNumber() {
        return new Localizable(holder, "ChartUtil.BuildNumber");
    }

    /**
     * Sorting automated test cases by TestLink test plan execution order.
     * 
     */
    public static String TestLinkBuilder_SortingTestCases() {
        return holder.format("TestLinkBuilder.SortingTestCases");
    }

    /**
     * Sorting automated test cases by TestLink test plan execution order.
     * 
     */
    public static Localizable _TestLinkBuilder_SortingTestCases() {
        return new Localizable(holder, "TestLinkBuilder.SortingTestCases");
    }

    /**
     * Found a test result for TestLink test case [{0}], ID [{1}], in JUnit test case [{2}], class [{3}. Status [{4}].
     * 
     */
    public static String Results_JUnit_TestResultsFound(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        return holder.format("Results.JUnit.TestResultsFound", arg1, arg2, arg3, arg4, arg5);
    }

    /**
     * Found a test result for TestLink test case [{0}], ID [{1}], in JUnit test case [{2}], class [{3}. Status [{4}].
     * 
     */
    public static Localizable _Results_JUnit_TestResultsFound(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        return new Localizable(holder, "Results.JUnit.TestResultsFound", arg1, arg2, arg3, arg4, arg5);
    }

    /**
     * Found TestLink test case: {0}.
     * 
     */
    public static String TestLinkBuilder_Finder_FoundAutomatedTestCase(Object arg1) {
        return holder.format("TestLinkBuilder.Finder.FoundAutomatedTestCase", arg1);
    }

    /**
     * Found TestLink test case: {0}.
     * 
     */
    public static Localizable _TestLinkBuilder_Finder_FoundAutomatedTestCase(Object arg1) {
        return new Localizable(holder, "TestLinkBuilder.Finder.FoundAutomatedTestCase", arg1);
    }

    /**
     * Merging build environment variables with TestLink environment variables.
     * 
     */
    public static String TestLinkBuilder_MergingEnvVars() {
        return holder.format("TestLinkBuilder.MergingEnvVars");
    }

    /**
     * Merging build environment variables with TestLink environment variables.
     * 
     */
    public static Localizable _TestLinkBuilder_MergingEnvVars() {
        return new Localizable(holder, "TestLinkBuilder.MergingEnvVars");
    }

    /**
     * Passed
     * 
     */
    public static String TestLinkBuilder_ExecutionStatus_Passed() {
        return holder.format("TestLinkBuilder.ExecutionStatus.Passed");
    }

    /**
     * Passed
     * 
     */
    public static Localizable _TestLinkBuilder_ExecutionStatus_Passed() {
        return new Localizable(holder, "TestLinkBuilder.ExecutionStatus.Passed");
    }

    /**
     * Could not find a test result in JUnit XML [{0}], test [{1}], class [{2}].
     * 
     */
    public static String Results_JUnit_NoTestResultFound(Object arg1, Object arg2, Object arg3) {
        return holder.format("Results.JUnit.NoTestResultFound", arg1, arg2, arg3);
    }

    /**
     * Could not find a test result in JUnit XML [{0}], test [{1}], class [{2}].
     * 
     */
    public static Localizable _Results_JUnit_NoTestResultFound(Object arg1, Object arg2, Object arg3) {
        return new Localizable(holder, "Results.JUnit.NoTestResultFound", arg1, arg2, arg3);
    }

    /**
     * tests passed, {0}
     * 
     */
    public static String ReportSummary_Summary_TestsPassed(Object arg1) {
        return holder.format("ReportSummary.Summary.TestsPassed", arg1);
    }

    /**
     * tests passed, {0}
     * 
     */
    public static Localizable _ReportSummary_Summary_TestsPassed(Object arg1) {
        return new Localizable(holder, "ReportSummary.Summary.TestsPassed", arg1);
    }

    /**
     * Updating {0} test case(s) execution status.
     * 
     */
    public static String TestLinkBuilder_Update_AutomatedTestCases(Object arg1) {
        return holder.format("TestLinkBuilder.Update.AutomatedTestCases", arg1);
    }

    /**
     * Updating {0} test case(s) execution status.
     * 
     */
    public static Localizable _TestLinkBuilder_Update_AutomatedTestCases(Object arg1) {
        return new Localizable(holder, "TestLinkBuilder.Update.AutomatedTestCases", arg1);
    }

    /**
     * Total tests
     * 
     */
    public static String ChartUtil_TotalTests() {
        return holder.format("ChartUtil.TotalTests");
    }

    /**
     * Total tests
     * 
     */
    public static Localizable _ChartUtil_TotalTests() {
        return new Localizable(holder, "ChartUtil.TotalTests");
    }

    /**
     * Unkown internal error. Please, open an issue in Jenkins JIRA with the complete stack trace. If possible, try including the TAP file(s) too.
     * 
     */
    public static String Results_TAP_UnkownInternalError() {
        return holder.format("Results.TAP.UnkownInternalError");
    }

    /**
     * Unkown internal error. Please, open an issue in Jenkins JIRA with the complete stack trace. If possible, try including the TAP file(s) too.
     * 
     */
    public static Localizable _Results_TAP_UnkownInternalError() {
        return new Localizable(holder, "Results.TAP.UnkownInternalError");
    }

    /**
     * Verifying TestLink test case [{0}], ID [{1}].
     * 
     */
    public static String Results_JUnit_VerifyingTestLinkTestCase(Object arg1, Object arg2) {
        return holder.format("Results.JUnit.VerifyingTestLinkTestCase", arg1, arg2);
    }

    /**
     * Verifying TestLink test case [{0}], ID [{1}].
     * 
     */
    public static Localizable _Results_JUnit_VerifyingTestLinkTestCase(Object arg1, Object arg2) {
        return new Localizable(holder, "Results.JUnit.VerifyingTestLinkTestCase", arg1, arg2);
    }

    /**
     * 
     * 
     * Failed to add TestNG attachments to this test case execution. Error message: {0}.
     * 
     */
    public static String Results_TestNG_AddAttachmentsFail(Object arg1) {
        return holder.format("Results.TestNG.AddAttachmentsFail", arg1);
    }

    /**
     * 
     * 
     * Failed to add TestNG attachments to this test case execution. Error message: {0}.
     * 
     */
    public static Localizable _Results_TestNG_AddAttachmentsFail(Object arg1) {
        return new Localizable(holder, "Results.TestNG.AddAttachmentsFail", arg1);
    }

    /**
     * Number of test cases
     * 
     */
    public static String ChartUtil_NumberOfTestCases() {
        return holder.format("ChartUtil.NumberOfTestCases");
    }

    /**
     * Number of test cases
     * 
     */
    public static Localizable _ChartUtil_NumberOfTestCases() {
        return new Localizable(holder, "ChartUtil.NumberOfTestCases");
    }

    /**
     * {0} report file for automated test case.
     * 
     */
    public static String TestLinkBuilder_Parser_AttachmentDescription(Object arg1) {
        return holder.format("TestLinkBuilder.Parser.AttachmentDescription", arg1);
    }

    /**
     * {0} report file for automated test case.
     * 
     */
    public static Localizable _TestLinkBuilder_Parser_AttachmentDescription(Object arg1) {
        return new Localizable(holder, "TestLinkBuilder.Parser.AttachmentDescription", arg1);
    }

    /**
     * 
     * 
     * Failed to add JUnit attachments to this test case execution. Error message: {0}.
     * 
     */
    public static String Results_JUnit_AddAttachmentsFail(Object arg1) {
        return holder.format("Results.JUnit.AddAttachmentsFail", arg1);
    }

    /**
     * 
     * 
     * Failed to add JUnit attachments to this test case execution. Error message: {0}.
     * 
     */
    public static Localizable _Results_JUnit_AddAttachmentsFail(Object arg1) {
        return new Localizable(holder, "Results.JUnit.AddAttachmentsFail", arg1);
    }

    /**
     * Found [{0}] JUnit XML(s).
     * 
     */
    public static String Results_JUnit_NumberOfReportsFound(Object arg1) {
        return holder.format("Results.JUnit.NumberOfReportsFound", arg1);
    }

    /**
     * Found [{0}] JUnit XML(s).
     * 
     */
    public static Localizable _Results_JUnit_NumberOfReportsFound(Object arg1) {
        return new Localizable(holder, "Results.JUnit.NumberOfReportsFound", arg1);
    }

    /**
     * Unkown internal error. Please, open an issue in Jenkins JIRA with the complete stack trace. If possible, try including the JUnit XML(s) too.
     * 
     */
    public static String Results_JUnit_UnkownInternalError() {
        return holder.format("Results.JUnit.UnkownInternalError");
    }

    /**
     * Unkown internal error. Please, open an issue in Jenkins JIRA with the complete stack trace. If possible, try including the JUnit XML(s) too.
     * 
     */
    public static Localizable _Results_JUnit_UnkownInternalError() {
        return new Localizable(holder, "Results.JUnit.UnkownInternalError");
    }

    /**
     * Build created automatically with TestLink Jenkins Plug-in.
     * 
     */
    public static String TestLinkBuilder_Build_Notes() {
        return holder.format("TestLinkBuilder.Build.Notes");
    }

    /**
     * Build created automatically with TestLink Jenkins Plug-in.
     * 
     */
    public static Localizable _TestLinkBuilder_Build_Notes() {
        return new Localizable(holder, "TestLinkBuilder.Build.Notes");
    }

    /**
     * Failed to retrieve custom field {0} for test case {1}. Reason: {2}.
     * 
     */
    public static String TestLinkBuilder_Finder_FailedToRetrieveCustomField(Object arg1, Object arg2, Object arg3) {
        return holder.format("TestLinkBuilder.Finder.FailedToRetrieveCustomField", arg1, arg2, arg3);
    }

    /**
     * Failed to retrieve custom field {0} for test case {1}. Reason: {2}.
     * 
     */
    public static Localizable _TestLinkBuilder_Finder_FailedToRetrieveCustomField(Object arg1, Object arg2, Object arg3) {
        return new Localizable(holder, "TestLinkBuilder.Finder.FailedToRetrieveCustomField", arg1, arg2, arg3);
    }

    /**
     * This property is mandatory.
     * 
     */
    public static String TestLinkBuilder_MandatoryProperty() {
        return holder.format("TestLinkBuilder.MandatoryProperty");
    }

    /**
     * This property is mandatory.
     * 
     */
    public static Localizable _TestLinkBuilder_MandatoryProperty() {
        return new Localizable(holder, "TestLinkBuilder.MandatoryProperty");
    }

    /**
     * An error occured while trying to retrieve the test results: {0}.
     * 
     */
    public static String Results_ErrorToLookForTestResults(Object arg1) {
        return holder.format("Results.ErrorToLookForTestResults", arg1);
    }

    /**
     * An error occured while trying to retrieve the test results: {0}.
     * 
     */
    public static Localizable _Results_ErrorToLookForTestResults(Object arg1) {
        return new Localizable(holder, "Results.ErrorToLookForTestResults", arg1);
    }

    /**
     * Custom fields in this TestLink test case [{0}].
     * 
     */
    public static String Results_TestNG_ListOfCustomFields(Object arg1) {
        return holder.format("Results.TestNG.ListOfCustomFields", arg1);
    }

    /**
     * Custom fields in this TestLink test case [{0}].
     * 
     */
    public static Localizable _Results_TestNG_ListOfCustomFields(Object arg1) {
        return new Localizable(holder, "Results.TestNG.ListOfCustomFields", arg1);
    }

    /**
     * Invalid number of errors found in JUnit suite. Expected {0} but got {1}!
     * 
     */
    public static String TestLinkBuilder_Parser_JUnit_InvalidNumberOfErrors(Object arg1, Object arg2) {
        return holder.format("TestLinkBuilder.Parser.JUnit.InvalidNumberOfErrors", arg1, arg2);
    }

    /**
     * Invalid number of errors found in JUnit suite. Expected {0} but got {1}!
     * 
     */
    public static Localizable _TestLinkBuilder_Parser_JUnit_InvalidNumberOfErrors(Object arg1, Object arg2) {
        return new Localizable(holder, "TestLinkBuilder.Parser.JUnit.InvalidNumberOfErrors", arg1, arg2);
    }

    /**
     * Custom fields in this TestLink test case [{0}].
     * 
     */
    public static String Results_JUnit_ListOfCustomFields(Object arg1) {
        return holder.format("Results.JUnit.ListOfCustomFields", arg1);
    }

    /**
     * Custom fields in this TestLink test case [{0}].
     * 
     */
    public static Localizable _Results_JUnit_ListOfCustomFields(Object arg1) {
        return new Localizable(holder, "Results.JUnit.ListOfCustomFields", arg1);
    }

    /**
     * Passed tests
     * 
     */
    public static String ChartUtil_PassedTests() {
        return holder.format("ChartUtil.PassedTests");
    }

    /**
     * Passed tests
     * 
     */
    public static Localizable _ChartUtil_PassedTests() {
        return new Localizable(holder, "ChartUtil.PassedTests");
    }

    /**
     * Unkown internal error. Please, open an issue in Jenkins JIRA with the complete stack trace. If possible, try including the TestNG XML(s) too.
     * 
     */
    public static String Results_TestNG_UnkownInternalError() {
        return holder.format("Results.TestNG.UnkownInternalError");
    }

    /**
     * Unkown internal error. Please, open an issue in Jenkins JIRA with the complete stack trace. If possible, try including the TestNG XML(s) too.
     * 
     */
    public static Localizable _Results_TestNG_UnkownInternalError() {
        return new Localizable(holder, "Results.TestNG.UnkownInternalError");
    }

    /**
     * Looking for a test result that matches with key custom field [{0}] => [{1}].
     * 
     */
    public static String Results_JUnit_LookingForTestResults(Object arg1, Object arg2) {
        return holder.format("Results.JUnit.LookingForTestResults", arg1, arg2);
    }

    /**
     * Looking for a test result that matches with key custom field [{0}] => [{1}].
     * 
     */
    public static Localizable _Results_JUnit_LookingForTestResults(Object arg1, Object arg2) {
        return new Localizable(holder, "Results.JUnit.LookingForTestResults", arg1, arg2);
    }

    /**
     * Undefined
     * 
     */
    public static String TestLinkBuilder_ExecutionStatus_Undefined() {
        return holder.format("TestLinkBuilder.ExecutionStatus.Undefined");
    }

    /**
     * Undefined
     * 
     */
    public static Localizable _TestLinkBuilder_ExecutionStatus_Undefined() {
        return new Localizable(holder, "TestLinkBuilder.ExecutionStatus.Undefined");
    }

    /**
     * Uploading test execution {0} attachment {1}.
     * 
     */
    public static String TestLinkBuilder_Upload_ExecutionAttachment(Object arg1, Object arg2) {
        return holder.format("TestLinkBuilder.Upload.ExecutionAttachment", arg1, arg2);
    }

    /**
     * Uploading test execution {0} attachment {1}.
     * 
     */
    public static Localizable _TestLinkBuilder_Upload_ExecutionAttachment(Object arg1, Object arg2) {
        return new Localizable(holder, "TestLinkBuilder.Upload.ExecutionAttachment", arg1, arg2);
    }

    /**
     * Found a test result for TestLink test case [{0}], ID [{1}], in TAP file [{2}]. Status [{3}].
     * 
     */
    public static String Results_TAP_TestResultsFound(Object arg1, Object arg2, Object arg3, Object arg4) {
        return holder.format("Results.TAP.TestResultsFound", arg1, arg2, arg3, arg4);
    }

    /**
     * Found a test result for TestLink test case [{0}], ID [{1}], in TAP file [{2}]. Status [{3}].
     * 
     */
    public static Localizable _Results_TAP_TestResultsFound(Object arg1, Object arg2, Object arg3, Object arg4) {
        return new Localizable(holder, "Results.TAP.TestResultsFound", arg1, arg2, arg3, arg4);
    }

    /**
     * Could not find a test result in TAP file [{0}].
     * 
     */
    public static String Results_TAP_NoTestResultFound(Object arg1) {
        return holder.format("Results.TAP.NoTestResultFound", arg1);
    }

    /**
     * Could not find a test result in TAP file [{0}].
     * 
     */
    public static Localizable _Results_TAP_NoTestResultFound(Object arg1) {
        return new Localizable(holder, "Results.TAP.NoTestResultFound", arg1);
    }

    /**
     * Verifying TestNG test suite [{0}]. This test suite contains [{1}] test cases.
     * 
     */
    public static String Results_TestNG_VerifyingTestNGTestSuite(Object arg1, Object arg2) {
        return holder.format("Results.TestNG.VerifyingTestNGTestSuite", arg1, arg2);
    }

    /**
     * Verifying TestNG test suite [{0}]. This test suite contains [{1}] test cases.
     * 
     */
    public static Localizable _Results_TestNG_VerifyingTestNGTestSuite(Object arg1, Object arg2) {
        return new Localizable(holder, "Results.TestNG.VerifyingTestNGTestSuite", arg1, arg2);
    }

    /**
     * Executing single test command: [{0}].
     * 
     */
    public static String TestLinkBuilder_ExecutingSingleTestCommand(Object arg1) {
        return holder.format("TestLinkBuilder.ExecutingSingleTestCommand", arg1);
    }

    /**
     * Executing single test command: [{0}].
     * 
     */
    public static Localizable _TestLinkBuilder_ExecutingSingleTestCommand(Object arg1) {
        return new Localizable(holder, "TestLinkBuilder.ExecutingSingleTestCommand", arg1);
    }

    /**
     * Verifying TAP test set. This test set contains [{0}] test result(s).
     * 
     */
    public static String Results_TAP_VerifyingTapSet(Object arg1) {
        return holder.format("Results.TAP.VerifyingTapSet", arg1);
    }

    /**
     * Verifying TAP test set. This test set contains [{0}] test result(s).
     * 
     */
    public static Localizable _Results_TAP_VerifyingTapSet(Object arg1) {
        return new Localizable(holder, "Results.TAP.VerifyingTapSet", arg1);
    }

    /**
     * Creating list of environment variables for test case execution.
     * 
     */
    public static String TestLinkBuilder_CreatingEnvVars() {
        return holder.format("TestLinkBuilder.CreatingEnvVars");
    }

    /**
     * Creating list of environment variables for test case execution.
     * 
     */
    public static Localizable _TestLinkBuilder_CreatingEnvVars() {
        return new Localizable(holder, "TestLinkBuilder.CreatingEnvVars");
    }

    /**
     * Found {0} test result(s).
     * 
     */
    public static String TestLinkBuilder_ShowFoundTestResults(Object arg1) {
        return holder.format("TestLinkBuilder.ShowFoundTestResults", arg1);
    }

    /**
     * Found {0} test result(s).
     * 
     */
    public static Localizable _TestLinkBuilder_ShowFoundTestResults(Object arg1) {
        return new Localizable(holder, "TestLinkBuilder.ShowFoundTestResults", arg1);
    }

    /**
     * List of test cases and execution result status
     * 
     */
    public static String ReportSummary_Details_Header() {
        return holder.format("ReportSummary.Details.Header");
    }

    /**
     * List of test cases and execution result status
     * 
     */
    public static Localizable _ReportSummary_Details_Header() {
        return new Localizable(holder, "ReportSummary.Details.Header");
    }

    /**
     * Failed tests
     * 
     */
    public static String ChartUtil_FailedTests() {
        return holder.format("ChartUtil.FailedTests");
    }

    /**
     * Failed tests
     * 
     */
    public static Localizable _ChartUtil_FailedTests() {
        return new Localizable(holder, "ChartUtil.FailedTests");
    }

    /**
     * Skipping update test case execution status. Nothing found.
     * 
     */
    public static String TestLinkBuilder_Update_Skipped() {
        return holder.format("TestLinkBuilder.Update.Skipped");
    }

    /**
     * Skipping update test case execution status. Nothing found.
     * 
     */
    public static Localizable _TestLinkBuilder_Update_Skipped() {
        return new Localizable(holder, "TestLinkBuilder.Update.Skipped");
    }

    /**
     * Retrieving custom field {0}.
     * 
     */
    public static String TestLinkBuilder_Finder_RetrievingCustomField(Object arg1) {
        return holder.format("TestLinkBuilder.Finder.RetrievingCustomField", arg1);
    }

    /**
     * Retrieving custom field {0}.
     * 
     */
    public static Localizable _TestLinkBuilder_Finder_RetrievingCustomField(Object arg1) {
        return new Localizable(holder, "TestLinkBuilder.Finder.RetrievingCustomField", arg1);
    }

    /**
     * Test project ID
     * 
     */
    public static String ReportSummary_Details_TestProjectId() {
        return holder.format("ReportSummary.Details.TestProjectId");
    }

    /**
     * Test project ID
     * 
     */
    public static Localizable _ReportSummary_Details_TestProjectId() {
        return new Localizable(holder, "ReportSummary.Details.TestProjectId");
    }

    /**
     * JUnit XML [{0}].
     * 
     */
    public static String Results_JUnit_AttachmentDescription(Object arg1) {
        return holder.format("Results.JUnit.AttachmentDescription", arg1);
    }

    /**
     * JUnit XML [{0}].
     * 
     */
    public static Localizable _Results_JUnit_AttachmentDescription(Object arg1) {
        return new Localizable(holder, "Results.JUnit.AttachmentDescription", arg1);
    }

    /**
     * Blank iterative test command. Skipping iterative test command execution.
     * 
     */
    public static String TestLinkBuilder_BlankIterativeTestCommand() {
        return holder.format("TestLinkBuilder.BlankIterativeTestCommand");
    }

    /**
     * Blank iterative test command. Skipping iterative test command execution.
     * 
     */
    public static Localizable _TestLinkBuilder_BlankIterativeTestCommand() {
        return new Localizable(holder, "TestLinkBuilder.BlankIterativeTestCommand");
    }

    /**
     * Error communicating with TestLink. Check your TestLink configuration.
     * 
     */
    public static String TestLinkBuilder_TestLinkCommunicationError() {
        return holder.format("TestLinkBuilder.TestLinkCommunicationError");
    }

    /**
     * Error communicating with TestLink. Check your TestLink configuration.
     * 
     */
    public static Localizable _TestLinkBuilder_TestLinkCommunicationError() {
        return new Localizable(holder, "TestLinkBuilder.TestLinkCommunicationError");
    }

    /**
     * Verifying JUnit test [{0}].
     * 
     */
    public static String Results_JUnit_VerifyingJUnitTest(Object arg1) {
        return holder.format("Results.JUnit.VerifyingJUnitTest", arg1);
    }

    /**
     * Verifying JUnit test [{0}].
     * 
     */
    public static Localizable _Results_JUnit_VerifyingJUnitTest(Object arg1) {
        return new Localizable(holder, "Results.JUnit.VerifyingJUnitTest", arg1);
    }

    /**
     * Invalid attachment found in TAP diagnostic: {0}.
     * 
     */
    public static String TestLinkBuilder_Parser_TAP_DiagnosticAttachmentError(Object arg1) {
        return holder.format("TestLinkBuilder.Parser.TAP.DiagnosticAttachmentError", arg1);
    }

    /**
     * Invalid attachment found in TAP diagnostic: {0}.
     * 
     */
    public static Localizable _TestLinkBuilder_Parser_TAP_DiagnosticAttachmentError(Object arg1) {
        return new Localizable(holder, "TestLinkBuilder.Parser.TAP.DiagnosticAttachmentError", arg1);
    }

    /**
     * Looking for a test result that matches with key custom field [{0}] => [{1}].
     * 
     */
    public static String Results_TestNG_LookingForTestResults(Object arg1, Object arg2) {
        return holder.format("Results.TestNG.LookingForTestResults", arg1, arg2);
    }

    /**
     * Looking for a test result that matches with key custom field [{0}] => [{1}].
     * 
     */
    public static Localizable _Results_TestNG_LookingForTestResults(Object arg1, Object arg2) {
        return new Localizable(holder, "Results.TestNG.LookingForTestResults", arg1, arg2);
    }

    /**
     * Verifying TestNG test class [{0}].
     * 
     */
    public static String Results_TestNG_VerifyingTestNGTestClass(Object arg1) {
        return holder.format("Results.TestNG.VerifyingTestNGTestClass", arg1);
    }

    /**
     * Verifying TestNG test class [{0}].
     * 
     */
    public static Localizable _Results_TestNG_VerifyingTestNGTestClass(Object arg1) {
        return new Localizable(holder, "Results.TestNG.VerifyingTestNGTestClass", arg1);
    }

    /**
     * Blank single test command. Skipping single test command execution.
     * 
     */
    public static String TestLinkBuilder_BlankSingleTestCommand() {
        return holder.format("TestLinkBuilder.BlankSingleTestCommand");
    }

    /**
     * Blank single test command. Skipping single test command execution.
     * 
     */
    public static Localizable _TestLinkBuilder_BlankSingleTestCommand() {
        return new Localizable(holder, "TestLinkBuilder.BlankSingleTestCommand");
    }

    /**
     * tests.
     * 
     */
    public static String ReportSummary_Summary_Tests() {
        return holder.format("ReportSummary.Summary.Tests");
    }

    /**
     * tests.
     * 
     */
    public static Localizable _ReportSummary_Summary_Tests() {
        return new Localizable(holder, "ReportSummary.Summary.Tests");
    }

    /**
     * Preparing TestLink client API.
     * 
     */
    public static String TestLinkBuilder_PreparingTLAPI() {
        return holder.format("TestLinkBuilder.PreparingTLAPI");
    }

    /**
     * Preparing TestLink client API.
     * 
     */
    public static Localizable _TestLinkBuilder_PreparingTLAPI() {
        return new Localizable(holder, "TestLinkBuilder.PreparingTLAPI");
    }

    /**
     * Failed to open base directory to look for reports: {0}.
     * 
     */
    public static String TestLinkBuilder_Scanner_FailedToOpenBaseDirectory(Object arg1) {
        return holder.format("TestLinkBuilder.Scanner.FailedToOpenBaseDirectory", arg1);
    }

    /**
     * Failed to open base directory to look for reports: {0}.
     * 
     */
    public static Localizable _TestLinkBuilder_Scanner_FailedToOpenBaseDirectory(Object arg1) {
        return new Localizable(holder, "TestLinkBuilder.Scanner.FailedToOpenBaseDirectory", arg1);
    }

    /**
     * Verifying TestLink test case [{0}], ID [{1}].
     * 
     */
    public static String Results_TAP_VerifyingTestLinkTestCase(Object arg1, Object arg2) {
        return holder.format("Results.TAP.VerifyingTestLinkTestCase", arg1, arg2);
    }

    /**
     * Verifying TestLink test case [{0}], ID [{1}].
     * 
     */
    public static Localizable _Results_TAP_VerifyingTestLinkTestCase(Object arg1, Object arg2) {
        return new Localizable(holder, "Results.TAP.VerifyingTestLinkTestCase", arg1, arg2);
    }

    /**
     * Found a test result for TestLink test case [{0}], ID [{1}], in TestNG test case [{2}], class [{3}. Status [{4}].
     * 
     */
    public static String Results_TestNG_TestResultsFound(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        return holder.format("Results.TestNG.TestResultsFound", arg1, arg2, arg3, arg4, arg5);
    }

    /**
     * Found a test result for TestLink test case [{0}], ID [{1}], in TestNG test case [{2}], class [{3}. Status [{4}].
     * 
     */
    public static Localizable _Results_TestNG_TestResultsFound(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        return new Localizable(holder, "Results.TestNG.TestResultsFound", arg1, arg2, arg3, arg4, arg5);
    }

    /**
     * Could not find a test result in TestNG XML [{0}], test [{1}], class [{2}].
     * 
     */
    public static String Results_TestNG_NoTestResultFound(Object arg1, Object arg2, Object arg3) {
        return holder.format("Results.TestNG.NoTestResultFound", arg1, arg2, arg3);
    }

    /**
     * Could not find a test result in TestNG XML [{0}], test [{1}], class [{2}].
     * 
     */
    public static Localizable _Results_TestNG_NoTestResultFound(Object arg1, Object arg2, Object arg3) {
        return new Localizable(holder, "Results.TestNG.NoTestResultFound", arg1, arg2, arg3);
    }

    /**
     * Failed
     * 
     */
    public static String TestLinkBuilder_ExecutionStatus_Failed() {
        return holder.format("TestLinkBuilder.ExecutionStatus.Failed");
    }

    /**
     * Failed
     * 
     */
    public static Localizable _TestLinkBuilder_ExecutionStatus_Failed() {
        return new Localizable(holder, "TestLinkBuilder.ExecutionStatus.Failed");
    }

    /**
     * Failed to read TestNG report file content and convert to Base64: {1}.
     * 
     */
    public static String TestLinkBuilder_Parser_AttachmentError(Object arg1, Object arg2) {
        return holder.format("TestLinkBuilder.Parser.AttachmentError", arg1, arg2);
    }

    /**
     * Failed to read TestNG report file content and convert to Base64: {1}.
     * 
     */
    public static Localizable _TestLinkBuilder_Parser_AttachmentError(Object arg1, Object arg2) {
        return new Localizable(holder, "TestLinkBuilder.Parser.AttachmentError", arg1, arg2);
    }

    /**
     * tests failed and {0}
     * 
     */
    public static String ReportSummary_Summary_TestsFailed(Object arg1) {
        return holder.format("ReportSummary.Summary.TestsFailed", arg1);
    }

    /**
     * tests failed and {0}
     * 
     */
    public static Localizable _ReportSummary_Summary_TestsFailed(Object arg1) {
        return new Localizable(holder, "ReportSummary.Summary.TestsFailed", arg1);
    }

    /**
     * TestLink build name: {0}
     * 
     */
    public static String ReportSummary_Summary_BuildName(Object arg1) {
        return holder.format("ReportSummary.Summary.BuildName", arg1);
    }

    /**
     * TestLink build name: {0}
     * 
     */
    public static Localizable _ReportSummary_Summary_BuildName(Object arg1) {
        return new Localizable(holder, "ReportSummary.Summary.BuildName", arg1);
    }

    /**
     * IO error scanning for include pattern [{0}]: {1}.
     * 
     */
    public static String Results_TAP_IOException(Object arg1, Object arg2) {
        return holder.format("Results.TAP.IOException", arg1, arg2);
    }

    /**
     * IO error scanning for include pattern [{0}]: {1}.
     * 
     */
    public static Localizable _Results_TAP_IOException(Object arg1, Object arg2) {
        return new Localizable(holder, "Results.TAP.IOException", arg1, arg2);
    }

    /**
     * Invalid TestLink installation.
     * 
     */
    public static String TestLinkBuilder_InvalidTLAPI() {
        return holder.format("TestLinkBuilder.InvalidTLAPI");
    }

    /**
     * Invalid TestLink installation.
     * 
     */
    public static Localizable _TestLinkBuilder_InvalidTLAPI() {
        return new Localizable(holder, "TestLinkBuilder.InvalidTLAPI");
    }

    /**
     * TestNG XML [{0}].
     * 
     */
    public static String Results_TestNG_AttachmentDescription(Object arg1) {
        return holder.format("Results.TestNG.AttachmentDescription", arg1);
    }

    /**
     * TestNG XML [{0}].
     * 
     */
    public static Localizable _Results_TestNG_AttachmentDescription(Object arg1) {
        return new Localizable(holder, "Results.TestNG.AttachmentDescription", arg1);
    }

    /**
     * No test results found.
     * 
     */
    public static String TestLinkBuilder_NoTestResultsFound() {
        return holder.format("TestLinkBuilder.NoTestResultsFound");
    }

    /**
     * No test results found.
     * 
     */
    public static Localizable _TestLinkBuilder_NoTestResultsFound() {
        return new Localizable(holder, "TestLinkBuilder.NoTestResultsFound");
    }

    /**
     * Blocked tests
     * 
     */
    public static String ChartUtil_BlockedTests() {
        return holder.format("ChartUtil.BlockedTests");
    }

    /**
     * Blocked tests
     * 
     */
    public static Localizable _ChartUtil_BlockedTests() {
        return new Localizable(holder, "ChartUtil.BlockedTests");
    }

    /**
     * Invalid TestLink URL: {0}.
     * 
     */
    public static String TestLinkBuilder_InvalidTLURL(Object arg1) {
        return holder.format("TestLinkBuilder.InvalidTLURL", arg1);
    }

    /**
     * Invalid TestLink URL: {0}.
     * 
     */
    public static Localizable _TestLinkBuilder_InvalidTLURL(Object arg1) {
        return new Localizable(holder, "TestLinkBuilder.InvalidTLURL", arg1);
    }

    /**
     * Not Run
     * 
     */
    public static String TestLinkBuilder_ExecutionStatus_NotRun() {
        return holder.format("TestLinkBuilder.ExecutionStatus.NotRun");
    }

    /**
     * Not Run
     * 
     */
    public static Localizable _TestLinkBuilder_ExecutionStatus_NotRun() {
        return new Localizable(holder, "TestLinkBuilder.ExecutionStatus.NotRun");
    }

    /**
     * Blocked
     * 
     */
    public static String TestLinkBuilder_ExecutionStatus_Blocked() {
        return holder.format("TestLinkBuilder.ExecutionStatus.Blocked");
    }

    /**
     * Blocked
     * 
     */
    public static Localizable _TestLinkBuilder_ExecutionStatus_Blocked() {
        return new Localizable(holder, "TestLinkBuilder.ExecutionStatus.Blocked");
    }

    /**
     * Invalid number of failures found in JUnit suite. Expected {0} but got {1}!
     * 
     */
    public static String TestLinkBuilder_Parser_JUnit_InvalidNumberOfFailures(Object arg1, Object arg2) {
        return holder.format("TestLinkBuilder.Parser.JUnit.InvalidNumberOfFailures", arg1, arg2);
    }

    /**
     * Invalid number of failures found in JUnit suite. Expected {0} but got {1}!
     * 
     */
    public static Localizable _TestLinkBuilder_Parser_JUnit_InvalidNumberOfFailures(Object arg1, Object arg2) {
        return new Localizable(holder, "TestLinkBuilder.Parser.JUnit.InvalidNumberOfFailures", arg1, arg2);
    }

    /**
     * Scanning for {0} files in {1}. Include pattern: {2}.
     * 
     */
    public static String TestLinkBuilder_Parser_ScanForFiles(Object arg1, Object arg2, Object arg3) {
        return holder.format("TestLinkBuilder.Parser.ScanForFiles", arg1, arg2, arg3);
    }

    /**
     * Scanning for {0} files in {1}. Include pattern: {2}.
     * 
     */
    public static Localizable _TestLinkBuilder_Parser_ScanForFiles(Object arg1, Object arg2, Object arg3) {
        return new Localizable(holder, "TestLinkBuilder.Parser.ScanForFiles", arg1, arg2, arg3);
    }

    /**
     * Empty JUnit include pattern. Skipping JUnit test results.
     * 
     */
    public static String Results_JUnit_NoPattern() {
        return holder.format("Results.JUnit.NoPattern");
    }

    /**
     * Empty JUnit include pattern. Skipping JUnit test results.
     * 
     */
    public static Localizable _Results_JUnit_NoPattern() {
        return new Localizable(holder, "Results.JUnit.NoPattern");
    }

    /**
     * IO error scanning for include pattern [{0}]: {1}.
     * 
     */
    public static String Results_JUnit_IOException(Object arg1, Object arg2) {
        return holder.format("Results.JUnit.IOException", arg1, arg2);
    }

    /**
     * IO error scanning for include pattern [{0}]: {1}.
     * 
     */
    public static Localizable _Results_JUnit_IOException(Object arg1, Object arg2) {
        return new Localizable(holder, "Results.JUnit.IOException", arg1, arg2);
    }

    /**
     * Empty TestNG include pattern. Skipping TestNG test results.
     * 
     */
    public static String Results_TestNG_NoPattern() {
        return holder.format("Results.TestNG.NoPattern");
    }

    /**
     * Empty TestNG include pattern. Skipping TestNG test results.
     * 
     */
    public static Localizable _Results_TestNG_NoPattern() {
        return new Localizable(holder, "Results.TestNG.NoPattern");
    }

    /**
     * TestLink build ID: {0}
     * 
     */
    public static String ReportSummary_Summary_BuildID(Object arg1) {
        return holder.format("ReportSummary.Summary.BuildID", arg1);
    }

    /**
     * TestLink build ID: {0}
     * 
     */
    public static Localizable _ReportSummary_Summary_BuildID(Object arg1) {
        return new Localizable(holder, "ReportSummary.Summary.BuildID", arg1);
    }

    /**
     * Failed to parse TAP file [{0}]: {1}.
     * 
     */
    public static String Results_TAP_ParsingFail(Object arg1, Object arg2) {
        return holder.format("Results.TAP.ParsingFail", arg1, arg2);
    }

    /**
     * Failed to parse TAP file [{0}]: {1}.
     * 
     */
    public static Localizable _Results_TAP_ParsingFail(Object arg1, Object arg2) {
        return new Localizable(holder, "Results.TAP.ParsingFail", arg1, arg2);
    }

    /**
     * Total of {0}
     * 
     */
    public static String ReportSummary_Summary_TotalOf(Object arg1) {
        return holder.format("ReportSummary.Summary.TotalOf", arg1);
    }

    /**
     * Total of {0}
     * 
     */
    public static Localizable _ReportSummary_Summary_TotalOf(Object arg1) {
        return new Localizable(holder, "ReportSummary.Summary.TotalOf", arg1);
    }

    /**
     * Name
     * 
     */
    public static String ReportSummary_Details_Name() {
        return holder.format("ReportSummary.Details.Name");
    }

    /**
     * Name
     * 
     */
    public static Localizable _ReportSummary_Details_Name() {
        return new Localizable(holder, "ReportSummary.Details.Name");
    }

    /**
     * Parsing JUnit XML [{0}].
     * 
     */
    public static String Results_JUnit_Parsing(Object arg1) {
        return holder.format("Results.JUnit.Parsing", arg1);
    }

    /**
     * Parsing JUnit XML [{0}].
     * 
     */
    public static Localizable _Results_JUnit_Parsing(Object arg1) {
        return new Localizable(holder, "Results.JUnit.Parsing", arg1);
    }

    /**
     * Empty TAP include pattern. Skipping TAP test results.
     * 
     */
    public static String Results_TAP_NoPattern() {
        return holder.format("Results.TAP.NoPattern");
    }

    /**
     * Empty TAP include pattern. Skipping TAP test results.
     * 
     */
    public static Localizable _Results_TAP_NoPattern() {
        return new Localizable(holder, "Results.TAP.NoPattern");
    }

    /**
     * Error deleting temporary script {0}.
     * 
     */
    public static String TestLinkBuilder_DeleteTempArchiveError(Object arg1) {
        return holder.format("TestLinkBuilder.DeleteTempArchiveError", arg1);
    }

    /**
     * Error deleting temporary script {0}.
     * 
     */
    public static Localizable _TestLinkBuilder_DeleteTempArchiveError(Object arg1) {
        return new Localizable(holder, "TestLinkBuilder.DeleteTempArchiveError", arg1);
    }

    /**
     * Test case ID
     * 
     */
    public static String ReportSummary_Details_TestCaseId() {
        return holder.format("ReportSummary.Details.TestCaseId");
    }

    /**
     * Test case ID
     * 
     */
    public static Localizable _ReportSummary_Details_TestCaseId() {
        return new Localizable(holder, "ReportSummary.Details.TestCaseId");
    }

    /**
     * IO error scanning for include pattern [{0}]: {1}.
     * 
     */
    public static String Results_TestNG_IOException(Object arg1, Object arg2) {
        return holder.format("Results.TestNG.IOException", arg1, arg2);
    }

    /**
     * IO error scanning for include pattern [{0}]: {1}.
     * 
     */
    public static Localizable _Results_TestNG_IOException(Object arg1, Object arg2) {
        return new Localizable(holder, "Results.TestNG.IOException", arg1, arg2);
    }

    /**
     * Custom field {0} value: {1}.
     * 
     */
    public static String TestLinkBuilder_Finder_CustomFieldNameAndValue(Object arg1, Object arg2) {
        return holder.format("TestLinkBuilder.Finder.CustomFieldNameAndValue", arg1, arg2);
    }

    /**
     * Custom field {0} value: {1}.
     * 
     */
    public static Localizable _TestLinkBuilder_Finder_CustomFieldNameAndValue(Object arg1, Object arg2) {
        return new Localizable(holder, "TestLinkBuilder.Finder.CustomFieldNameAndValue", arg1, arg2);
    }

    /**
     * Failed to parse JUnit XML [{0}]: {1}.
     * 
     */
    public static String Results_JUnit_ParsingFail(Object arg1, Object arg2) {
        return holder.format("Results.JUnit.ParsingFail", arg1, arg2);
    }

    /**
     * Failed to parse JUnit XML [{0}]: {1}.
     * 
     */
    public static Localizable _Results_JUnit_ParsingFail(Object arg1, Object arg2) {
        return new Localizable(holder, "Results.JUnit.ParsingFail", arg1, arg2);
    }

    /**
     * Retrieving TestLink details about test project, test plan and build.
     * 
     */
    public static String TestLinkBuilder_Finder_RetrievingDetails() {
        return holder.format("TestLinkBuilder.Finder.RetrievingDetails");
    }

    /**
     * Retrieving TestLink details about test project, test plan and build.
     * 
     */
    public static Localizable _TestLinkBuilder_Finder_RetrievingDetails() {
        return new Localizable(holder, "TestLinkBuilder.Finder.RetrievingDetails");
    }

    /**
     * Verifying TestNG test [{0}]. This test contains [{1}] test classes.
     * 
     */
    public static String Results_TestNG_VerifyingTestNGTest(Object arg1, Object arg2) {
        return holder.format("Results.TestNG.VerifyingTestNGTest", arg1, arg2);
    }

    /**
     * Verifying TestNG test [{0}]. This test contains [{1}] test classes.
     * 
     */
    public static Localizable _Results_TestNG_VerifyingTestNGTest(Object arg1, Object arg2) {
        return new Localizable(holder, "Results.TestNG.VerifyingTestNGTest", arg1, arg2);
    }

}
