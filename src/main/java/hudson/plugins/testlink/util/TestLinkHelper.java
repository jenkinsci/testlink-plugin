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
package hudson.plugins.testlink.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.TestCaseStep;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.model.TestProject;
import hudson.EnvVars;
import hudson.Util;
import hudson.model.BuildListener;
import hudson.plugins.testlink.Report;
import hudson.plugins.testlink.TestLinkBuildAction;
import hudson.plugins.testlink.result.TestCaseWrapper;
import hudson.util.VariableResolver;

/**
 * Helper methods for TestLink.
 * @author Bruno P. Kinoshita
 * @since 2.0
 */
public final class TestLinkHelper {
	
	// Environment Variables names.
	private static final String TESTLINK_TESTCASE_PREFIX = "TESTLINK_TESTCASE_";
	private static final String TESTLINK_TESTPLAN_PREFIX = "TESTLINK_TESTPLAN_";
	private static final String TESTLINK_BUILD_PREFIX = "TESTLINK_BUILD_";
	private static final String TESTLINK_TESTCASE_STEP_PREFIX = "TESTLINK_TESTCASE_STEP_";
	private static final String TESTLINK_TESTCASE_ID_ENVVAR = "TESTLINK_TESTCASE_ID";
	private static final String TESTLINK_TESTCASE_NAME_ENVVAR = "TESTLINK_TESTCASE_NAME";
	private static final String TESTLINK_TESTCASE_TESTSUITE_ID_ENVVAR = "TESTLINK_TESTCASE_TESTSUITEID";
	private static final String TESTLINK_TESTCASE_TESTPROJECT_ID = "TESTLINK_TESTCASE_TESTPROJECTID";
	private static final String TESTLINK_TESTCASE_AUTHOR_ENVVAR = "TESTLINK_TESTCASE_AUTHOR";
	private static final String TESTLINK_TESTCASE_SUMMARY_ENVVAR = "TESTLINK_TESTCASE_SUMMARY";
	private static final String TESTLINK_TESTCASE_PLATFORM_ENVVAR = "TESTLINK_TESTCASE_PLATFORM";
	private static final String TESTLINK_BUILD_NAME_ENVVAR = "TESTLINK_BUILD_NAME";
	private static final String TESTLINK_TESTPLAN_NAME_ENVVAR = "TESTLINK_TESTPLAN_NAME";
	private static final String TESTLINK_TESTPROJECT_NAME_ENVVAR = "TESTLINK_TESTPROJECT_NAME";
	private static final String TESTLINK_TESTCASE_EXTERNAL_ID_ENVVAR = "TESTLINK_TESTCASE_EXTERNAL_ID";

	// Used for HTTP basic auth
	private static final String BASIC_HTTP_PASSWORD = "basicPassword";
	
	// For other utility methods
	/**
     * Comma constant for custom fields separated with delimiter.
     */
    private static final String COMMA = ",";

	/**
	 * Default hidden constructor for a helper class.
	 */
	private TestLinkHelper() {
		super();
	}
	
	/**
	 * Retrieves the text for an execution status wrapped in html tags that add 
	 * color to the text. Green for sucess, yellow for blocked, gray for not ran 
	 * and red for failed. If the plug-in supports the locale the text will be 
	 * translated automatically.
	 * 
	 * @param executionStatus the execution status.
	 * @return the text wrapped in html tags that add color to the text.
	 */
	public static String getExecutionStatusTextColored(ExecutionStatus executionStatus) {
		String executionStatusTextColored = "Undefined";
		if (executionStatus == ExecutionStatus.FAILED){
			executionStatusTextColored = "<span style='color: red'>Failed</span>";
		} else if (executionStatus == ExecutionStatus.PASSED) {
			executionStatusTextColored = "<span style='color: green'>Passed</span>";
		} else if (executionStatus == ExecutionStatus.BLOCKED) {
			executionStatusTextColored = "<span style='color: yellow'>Blocked</span>";
		} else if (executionStatus == ExecutionStatus.NOT_RUN) {
			executionStatusTextColored = "<span style='color: gray'>Not Run</span>";
		}
		return executionStatusTextColored;
	}
	
	/**
	 * <p>Defines TestLink Java API Properties. Following is the list of available 
	 * properties.</p>
	 * 
	 * <ul>
	 *  	<li>xmlrpc.basicEncoding</li>
 	 *  	<li>xmlrpc.basicPassword</li>
 	 *  	<li>xmlrpc.basicUsername</li>
 	 *  	<li>xmlrpc.connectionTimeout</li>
 	 *  	<li>xmlrpc.contentLengthOptional</li>
 	 *  	<li>xmlrpc.enabledForExceptions</li>
 	 *  	<li>xmlrpc.encoding</li>
 	 *  	<li>xmlrpc.gzipCompression</li>
 	 *  	<li>xmlrpc.gzipRequesting</li>
 	 *  	<li>xmlrpc.replyTimeout</li>
 	 *  	<li>xmlrpc.userAgent</li>
	 * </ul>
	 * 
	 * @param testLinkJavaAPIProperties
	 * @param listener Jenkins Build listener
	 */
	public static void setTestLinkJavaAPIProperties(String testLinkJavaAPIProperties, BuildListener listener) {
		if (StringUtils.isNotBlank(testLinkJavaAPIProperties)) {
			final StringTokenizer tokenizer = new StringTokenizer( testLinkJavaAPIProperties, "," );
			
			if (tokenizer.countTokens() > 0) {
				while (tokenizer.hasMoreTokens()) {
					String systemProperty = tokenizer.nextToken();
					maybeAddSystemProperty(systemProperty, listener);
				}
			}
		}
	}
	
	/**
	 * Maybe adds a system property if it is in format (@code key=value}.
	 * 
	 * @param systemProperty System property entry in format {@code key=value}.
	 * @param listener Jenkins Build listener
	 */
	public static void maybeAddSystemProperty(String systemProperty, BuildListener listener) {
		final StringTokenizer tokenizer = new StringTokenizer( systemProperty, "=:");
		if (tokenizer.countTokens() == 2) {
			final String key 	= tokenizer.nextToken();
			final String value	= tokenizer.nextToken();
			
			if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
				if (key.contains(BASIC_HTTP_PASSWORD)) {
					listener.getLogger().println(Messages.TestLinkBuilder_SettingSystemProperty(key, "********"));
				} else {
					listener.getLogger().println( Messages.TestLinkBuilder_SettingSystemProperty(key, value) );
				}
				
				try {
					System.setProperty(key, value);
				} catch ( SecurityException se ) {
					se.printStackTrace( listener.getLogger() );
				}
			
			}
		}
	}
	
	/**
	 * Creates a Map (name, value) of environment variables for a TestLink Test Case.
	 * 
	 * @param testCase TestLink test Case.
	 * @param testProject TestLink Test Project.
	 * @param testPlan TestLink Test Plan.
	 * @param build TestLink Build.
	 * @return Map (name, value) of environment variables.
	 */
	public static Map<String, String> createTestLinkEnvironmentVariables(TestCaseWrapper testCase, TestProject testProject, TestPlan testPlan, Build build) {
		Map<String, String> testLinkEnvVar = new HashMap<String, String>();
		
		testLinkEnvVar.put( TESTLINK_TESTCASE_ID_ENVVAR, ""+testCase.getId() );
		testLinkEnvVar.put( TESTLINK_TESTCASE_NAME_ENVVAR, StringUtils.defaultIfEmpty(testCase.getName(), ""));
		testLinkEnvVar.put( TESTLINK_TESTCASE_TESTSUITE_ID_ENVVAR, ""+testCase.getTestSuiteId() );
		testLinkEnvVar.put( TESTLINK_TESTCASE_TESTPROJECT_ID, ""+testCase.getTestProjectId() );
		testLinkEnvVar.put( TESTLINK_TESTCASE_AUTHOR_ENVVAR, ""+testCase.getAuthorLogin() );
		testLinkEnvVar.put( TESTLINK_TESTCASE_SUMMARY_ENVVAR, StringUtils.defaultIfEmpty(testCase.getSummary(), "") );
		testLinkEnvVar.put( TESTLINK_TESTCASE_PLATFORM_ENVVAR, StringUtils.defaultIfEmpty(testCase.getPlatform(), "") );
		testLinkEnvVar.put( TESTLINK_BUILD_NAME_ENVVAR, StringUtils.defaultIfEmpty(build.getName(), ""));
		testLinkEnvVar.put( TESTLINK_TESTPLAN_NAME_ENVVAR, StringUtils.defaultIfEmpty(testPlan.getName(), ""));
		testLinkEnvVar.put( TESTLINK_TESTPROJECT_NAME_ENVVAR, StringUtils.defaultIfEmpty(testProject.getName(), ""));
                testLinkEnvVar.put( TESTLINK_TESTCASE_EXTERNAL_ID_ENVVAR, StringUtils.defaultIfEmpty(testCase.getFullExternalId(), ""));

		List<CustomField> testCaseCustomFields = testCase.getCustomFields();
		for (CustomField customField : testCaseCustomFields) {
			addCustomFieldEnvironmentVariableName(customField, testLinkEnvVar, TESTLINK_TESTCASE_PREFIX);
		}

		List<CustomField> testPlanCustomFields = testPlan.getCustomFields();

		for(CustomField customField: testPlanCustomFields){
		    addCustomFieldEnvironmentVariableName(customField, testLinkEnvVar, TESTLINK_TESTPLAN_PREFIX);
		}

		List<TestCaseStep> steps = testCase.getSteps();
		testLinkEnvVar.put(TESTLINK_TESTCASE_STEP_PREFIX + "TOTAL", Integer.toString(steps.size()));
		for (TestCaseStep step : steps) {
			String action = step.getActions();
			testLinkEnvVar.put(TESTLINK_TESTCASE_STEP_PREFIX + step.getNumber() + "_ACTION", action);
			
			String expected = step.getExpectedResults();
			testLinkEnvVar.put(TESTLINK_TESTCASE_STEP_PREFIX + step.getNumber() + "_EXPECTED", expected);
		}
		return testLinkEnvVar;
	}
	
	/**
     * Creates a Map (name, value) of environment variables for a TestLink Test Case.
     * 
     * @param testProject TestLink Test Project.
     * @param testPlan TestLink Test Plan.
     * @param build TestLink Build.
     * @return Map (name, value) of environment variables.
     */
    public static Map<String, String> createTestLinkEnvironmentVariables(int numberOfTests, TestProject testProject, TestPlan testPlan, Build build) {
        Map<String, String> testLinkEnvVar = new HashMap<String, String>();
        
        testLinkEnvVar.put(TESTLINK_BUILD_NAME_ENVVAR, StringUtils.defaultIfEmpty(build.getName(), ""));
        testLinkEnvVar.put(TESTLINK_TESTPLAN_NAME_ENVVAR, StringUtils.defaultIfEmpty(testPlan.getName(), ""));
        testLinkEnvVar.put(TESTLINK_TESTPROJECT_NAME_ENVVAR, StringUtils.defaultIfEmpty(testProject.getName(), ""));
        
        testLinkEnvVar.put(TESTLINK_TESTCASE_PREFIX + "TOTAL", Integer.toString(numberOfTests));
        return testLinkEnvVar;
    }
	
	/**
	 * <p>Formats a custom field into an environment variable. It appends 
	 * the ${prefix} in front of the environment variable name.</p>
	 * 
	 * <p>So, for example, the custom field which name is Sample  Custom Field,
	 * value is <b>Sample Value</b>, and prefix is TESTLINK_TESTCASE. It will be added into the environment variables
	 * as TESTLINK_TESTCASE_SAMPLE__CUSTOM_FIELD="Sample Value" (note for the double spaces).</p>
	 * 
	 * <p>If the custom's value contains commas (,), then this method splits the
	 * value and, for each token found, it creates a new environment variable
	 * appending a numeric index after its name</p>
	 * 
	 * <p>So, for example, the custom field which name is Sample Custom Field, 
	 * value is <b>Sample Value 1, Sample Value 2</b>, and prefix is TESTLINK_TESTCASE. It will generate three
	 * environment variables: TESTLINK_TESTCASE_SAMPLE_CUSTOM_FIELD="Sample Value 1, Sample Value 2",
	 * TESTLINK_TESTCASE_SAMPLE_CUSTOM_FIELD_0="Sample Value 1" and
	 * TESTLINK_TESTCASE_SAMPLE_CUSTOM_FIELD_1="Sample Value 2".</p> 
	 * 
	 * @param customField The custom field
	 * @param testLinkEnvVar TestLink envVars
	 * @param prefix the variables name prefix
	 */
	public static void addCustomFieldEnvironmentVariableName(CustomField customField, Map<String,
	        String> testLinkEnvVar, String prefix) 
	{
		String customFieldName = customField.getName();
		String customFieldValue = customField.getValue();
		
		customFieldName = customFieldName.toUpperCase(); // uppercase
		customFieldName = customFieldName.trim(); // trim
		customFieldName = prefix + customFieldName; // add prefix
		customFieldName = customFieldName.replaceAll( "\\s+", "_" ); // replace white spaces
		
		testLinkEnvVar.put(customFieldName, customFieldValue);
		
		if (StringUtils.isNotBlank( customFieldValue )) {
			StringTokenizer tokenizer = new StringTokenizer(customFieldValue, ",");
			if (tokenizer.countTokens() > 1) {
				int index = 0;
				while (tokenizer.hasMoreTokens()) {
					String token = tokenizer.nextToken();
					token = token.trim();
					
					customFieldName = customField.getName();
					customFieldName = customFieldName.toUpperCase(); // uppercase
					customFieldName = customFieldName.trim(); // trim
					
					String tokenName = prefix + customFieldName + "_" + index; // add prefix
					tokenName = tokenName.replaceAll( "\\s+", "_" ); // replace white spaces
					
					testLinkEnvVar.put(tokenName, token);
					++index;
				}
			}
		}
	}

	/**
	 * Creates EnvVars for a TestLink Test Case.
	 * 
	 * @param testCase TestLink test Case
	 * @param testProject TestLink Test Project
	 * @param testPlan TestLink Test Plan
	 * @param build TestLink Build
	 * @param listener Hudson Build Listener
	 * @return EnvVars (environment variables)
	 */
	public static EnvVars buildTestCaseEnvVars(TestCaseWrapper testCase, TestProject testProject, TestPlan testPlan, Build build, BuildListener listener)	{
		// Build environment variables list
		Map<String, String> testLinkEnvironmentVariables = TestLinkHelper.createTestLinkEnvironmentVariables( testCase, testProject, testPlan, build );
		// Merge with build environment variables list
		listener.getLogger().println(Messages.TestLinkBuilder_MergingEnvVars());
		final EnvVars buildEnvironment = new EnvVars( testLinkEnvironmentVariables );
		return buildEnvironment;
	}
	
	/**
     * Creates EnvVars for TestLink entities.
     * 
     * @param numberOfTests number of tests
     * @param testProject TestLink Test Project
     * @param testPlan TestLink Test Plan
     * @param build TestLink Build
     * @param listener Hudson Build Listener
     * @return EnvVars (environment variables)
     */
    public static EnvVars buildTestCaseEnvVars(int numberOfTests, TestProject testProject, TestPlan testPlan, Build build, BuildListener listener)   {
        // Build environment variables list
        Map<String, String> testLinkEnvironmentVariables = TestLinkHelper.createTestLinkEnvironmentVariables(numberOfTests, testProject, testPlan, build);
        // Merge with build environment variables list
        listener.getLogger().println(Messages.TestLinkBuilder_MergingEnvVars());
        final EnvVars buildEnvironment = new EnvVars( testLinkEnvironmentVariables );
        return buildEnvironment;
    }
	
	/**
	 * Creates Report Summary.
	 * 
	 * @param testLinkReport TestLink Report
	 * @param previous Previous TestLink Report
	 * @return Report Summary
	 */
	public static String createReportSummary(Report testLinkReport, Report previous) {
		StringBuilder builder = new StringBuilder();
		builder.append("<p><b>"+Messages.ReportSummary_Summary_BuildID(testLinkReport.getBuildId())+"</b></p>");
		builder.append("<p><b>"+Messages.ReportSummary_Summary_BuildName(testLinkReport.getBuildName())+"</b></p>");
		builder.append("<p><a href=\"" + TestLinkBuildAction.URL_NAME + "\">");
		
		Integer total = testLinkReport.getTestsTotal();
		Integer previousTotal = previous != null ? previous.getTestsTotal() : total;
		Integer passed = testLinkReport.getPassed();
		Integer previousPassed = previous != null ? previous.getPassed() : passed;
		Integer failed = testLinkReport.getFailed();
		Integer previousFailed = previous != null ? previous.getFailed() : failed;
		Integer blocked = testLinkReport.getBlocked();
		Integer previousBlocked = previous != null ? previous.getBlocked() : blocked;
		Integer notRun = testLinkReport.getNotRun();
		Integer previousNotRun = previous != null ? previous.getNotRun() : notRun;
		
		builder.append( Messages.ReportSummary_Summary_Text(
			 total + getPlusSignal(total, previousTotal), 
			 passed + getPlusSignal(passed, previousPassed), 
			 failed + getPlusSignal(failed, previousFailed), 
			 blocked + getPlusSignal(blocked, previousBlocked), 
			 notRun + getPlusSignal(notRun, previousNotRun)
		) );
		
        builder.append("</p>");
		
		return builder.toString();
	}

	/**
	 * Creates detailed Report Summary.
	 * 
	 * @param report TestLink report
	 * @param previous Previous TestLink report
	 * @return Detailed Report Summary
	 */
	public static String createReportSummaryDetails(Report report, Report previous) {
		final StringBuilder builder = new StringBuilder();

		builder.append("<p>"+Messages.ReportSummary_Details_Header()+"</p>");
		builder.append("<table border=\"1\">\n");
		builder.append("<tr><th>");
		builder.append(Messages.ReportSummary_Details_TestCaseId() );
		builder.append("</th><th>");
		builder.append(Messages.ReportSummary_Details_TestCaseExternalId() );
		builder.append("</th><th>");
		builder.append(Messages.ReportSummary_Details_Version() );
		builder.append("</th><th>");
		builder.append(Messages.ReportSummary_Details_Name());
		builder.append("</th><th>");
		builder.append(Messages.ReportSummary_Details_Platform());
		builder.append("</th><th>");
		builder.append(Messages.ReportSummary_Details_TestProjectId());
		builder.append("</th><th>");
		builder.append(Messages.ReportSummary_Details_ExecutionStatus());
		builder.append("</th></tr>\n");
		
        for(TestCaseWrapper tc: report.getTestCases() )
        {
        	builder.append("<tr>\n");
        	
        	builder.append("<td>"+tc.getId()+"</td>");
        	builder.append("<td>"+tc.getFullExternalId()+"</td>");
        	builder.append("<td>"+tc.getVersion()+"</td>");
        	builder.append("<td>"+tc.getName()+"</td>");
        	builder.append("<td>"+tc.getPlatform()+"</td>");
        	builder.append("<td>"+tc.getTestProjectId()+"</td>");
    		builder.append("<td>"+TestLinkHelper.getExecutionStatusTextColored( tc.getExecutionStatus() )+"</td>\n");
        	
        	builder.append("</tr>\n");
        }
        
        builder.append("</table>");
        return builder.toString();
	}

	

	/**
	 * Prints the difference between two int values, showing a plus sign if the 
	 * current number is greater than the previous. 
	 * 
	 * @param current Current value
	 * @param previous Previous value
	 */
	public static String getPlusSignal(int current, int previous) {
		int difference = current - previous;
        return difference > 0 ? " (+"+difference+")" : "";
    }

	/**
     * Create an array of custom fields names using the Job configuration data.
     *
     * @param variableResolver Jenkins variable resolver
     * @param envVars Jenkins environment variables
     * @param customFields String containinig a comma separated list of custom fields
     * @return String[] of custom fields names.
     */
	public static String[] createArrayOfCustomFieldsNames(final VariableResolver<String> variableResolver,
            final EnvVars envVars, String customFields) {
        String[] customFieldNamesArray = new String[0];
        final String expandedCustomFields = expandVariable(variableResolver, envVars, customFields);

        if (StringUtils.isNotBlank(expandedCustomFields)) {
            StringTokenizer tokenizer = new StringTokenizer(expandedCustomFields, COMMA);
            if (tokenizer.countTokens() > 0) {
                customFieldNamesArray = new String[tokenizer.countTokens()];
                int index = 0;
                while (tokenizer.hasMoreTokens()) {
                    String customFieldName = tokenizer.nextToken();
                    customFieldName = customFieldName.trim();
                    customFieldNamesArray[index] = customFieldName;
                    index = index + 1;
                }
            }
        }

        return customFieldNamesArray;
    }

    /**
     * Expands a text variable like BUILD-$VAR replacing the $VAR part with a environment variable that matches its
     * name, minus $.
     *
     * @param variableResolver Jenkins Build Variable Resolver.
     * @param envVars Jenkins Build Environment Variables.
     * @param variable Variable value (includes mask).
     * @return Expanded test project name job configuration property.
     */
    public static String expandVariable(VariableResolver<String> variableResolver, EnvVars envVars, String variable) {
        return Util.replaceMacro(envVars.expand(variable), variableResolver);
    }

}
