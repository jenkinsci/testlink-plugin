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

import hudson.EnvVars;
import hudson.model.BuildListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.model.TestProject;

/**
 * Helper methods for TestLink.
 * 
 * @author Bruno P. Kinoshita
 * @since 2.0
 */
public final class TestLinkHelper 
{
	
	// Environment Variables names.
	private static final String TESTLINK_TESTCASE_PREFIX = "TESTLINK_TESTCASE_";
	private static final String TESTLINK_TESTCASE_ID_ENVVAR = "TESTLINK_TESTCASE_ID";
	private static final String TESTLINK_TESTCASE_NAME_ENVVAR = "TESTLINK_TESTCASE_NAME";
	private static final String TESTLINK_TESTCASE_TESTSUITE_ID_ENVVAR = "TESTLINK_TESTCASE_TESTSUITEID";
	private static final String TESTLINK_TESTCASE_TESTPROJECT_ID = "TESTLINK_TESTCASE_TESTPROJECTID";
	private static final String TESTLINK_TESTCASE_AUTHOR_ENVVAR = "TESTLINK_TESTCASE_AUTHOR";
	private static final String TESTLINK_TESTCASE_SUMMARY_ENVVAR = "TESTLINK_TESTCASE_SUMMARY";
	private static final String TESTLINK_BUILD_NAME_ENVVAR = "TESTLINK_BUILD_NAME";
	private static final String TESTLINK_TESTPLAN_NAME_ENVVAR = "TESTLINK_TESTPLAN_NAME";
	private static final String TESTLINK_TESTPROJECT_NAME_ENVVAR = "TESTLINK_TESTPROJECT_NAME";
	
	// Used for HTTP basic auth
	private static final String BASIC_HTTP_PASSWORD = "basicPassword";

	/**
	 * Default hidden constructor for a helper class.
	 */
	private TestLinkHelper()
	{
		super();
	}

	/**
	 * Retrieves the text for an execution status. If the plug-in supports 
	 * the locale the text will be translated automatically.
	 * 
	 * @param executionStatus the execution status.
	 * @return the text.
	 */
	public static String getExecutionStatusText( ExecutionStatus executionStatus ) 
	{
		String executionStatusText = Messages.TestLinkBuilder_ExecutionStatus_Undefined();
		if ( executionStatus == ExecutionStatus.FAILED )
		{
			executionStatusText = Messages.TestLinkBuilder_ExecutionStatus_Failed();
		}
		if ( executionStatus == ExecutionStatus.PASSED )
		{
			executionStatusText = Messages.TestLinkBuilder_ExecutionStatus_Passed();
		}
		if ( executionStatus == ExecutionStatus.BLOCKED )
		{
			executionStatusText = Messages.TestLinkBuilder_ExecutionStatus_Blocked();
		}
		if ( executionStatus == ExecutionStatus.NOT_RUN )
		{
			executionStatusText = Messages.TestLinkBuilder_ExecutionStatus_NotRun();
		}
		return executionStatusText;
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
	public static String getExecutionStatusTextColored( ExecutionStatus executionStatus ) 
	{
		String executionStatusTextColored = 
			Messages.TestLinkBuilder_ExecutionStatus_Undefined();
		if ( executionStatus == ExecutionStatus.FAILED )
		{
			executionStatusTextColored = "<span style='color: red'>"+Messages.TestLinkBuilder_ExecutionStatus_Failed()+"</span>";
		}
		if ( executionStatus == ExecutionStatus.PASSED )
		{
			executionStatusTextColored = "<span style='color: green'>"+Messages.TestLinkBuilder_ExecutionStatus_Passed()+"</span>";
		}
		if ( executionStatus == ExecutionStatus.BLOCKED )
		{
			executionStatusTextColored = "<span style='color: yellow'>"+Messages.TestLinkBuilder_ExecutionStatus_Blocked()+"</span>";
		}
		if ( executionStatus == ExecutionStatus.NOT_RUN )
		{
			executionStatusTextColored = "<span style='color: gray'>"+Messages.TestLinkBuilder_ExecutionStatus_NotRun()+"</span>";
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
	public static void setTestLinkJavaAPIProperties( String testLinkJavaAPIProperties, BuildListener listener )
	{
		if ( StringUtils.isNotBlank( testLinkJavaAPIProperties ) )
		{
			final StringTokenizer tokenizer = new StringTokenizer( testLinkJavaAPIProperties, "," );
			
			if ( tokenizer.countTokens() > 0 )
			{
				while ( tokenizer.hasMoreTokens() )
				{
					String systemProperty = tokenizer.nextToken();
					maybeAddSystemProperty( systemProperty, listener );
				}
			}
		}
	}
	
	/**
	 * Maybe adds a system property if it is in format <key>=<value>.
	 * 
	 * @param systemProperty System property entry in format <key>=<value>.
	 * @param listener Jenkins Build listener
	 */
	public static void maybeAddSystemProperty( String systemProperty, BuildListener listener )
	{
		final StringTokenizer tokenizer = new StringTokenizer( systemProperty, "=:");
		if ( tokenizer.countTokens() == 2 )
		{
			final String key 	= tokenizer.nextToken();
			final String value	= tokenizer.nextToken();
			
			if ( StringUtils.isNotBlank( key ) && StringUtils.isNotBlank( value ) )
			{
				if ( key.contains(BASIC_HTTP_PASSWORD))
				{
					listener.getLogger().println( Messages.TestLinkBuilder_SettingSystemProperty(key, "********") );
				}
				else
				{
					listener.getLogger().println( Messages.TestLinkBuilder_SettingSystemProperty(key, value) );
				}
				try
				{
					System.setProperty(key, value);
				} 
				catch ( SecurityException se )
				{
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
	public static Map<String, String> createTestLinkEnvironmentVariables( TestCase testCase, TestProject testProject, TestPlan testPlan, Build build ) 
	{
		Map<String, String> testLinkEnvVar = new HashMap<String, String>();
		
		testLinkEnvVar.put( TESTLINK_TESTCASE_ID_ENVVAR, ""+testCase.getId() );
		testLinkEnvVar.put( TESTLINK_TESTCASE_NAME_ENVVAR, ""+testCase.getName() );
		testLinkEnvVar.put( TESTLINK_TESTCASE_TESTSUITE_ID_ENVVAR, ""+testCase.getTestSuiteId() );
		testLinkEnvVar.put( TESTLINK_TESTCASE_TESTPROJECT_ID, ""+testCase.getTestProjectId() );
		testLinkEnvVar.put( TESTLINK_TESTCASE_AUTHOR_ENVVAR, ""+testCase.getAuthorLogin() );
		testLinkEnvVar.put( TESTLINK_TESTCASE_SUMMARY_ENVVAR, testCase.getSummary() );
		testLinkEnvVar.put( TESTLINK_BUILD_NAME_ENVVAR, build.getName() );
		testLinkEnvVar.put( TESTLINK_TESTPLAN_NAME_ENVVAR, testPlan.getName() );
		testLinkEnvVar.put( TESTLINK_TESTPROJECT_NAME_ENVVAR, testProject.getName() );
		
		List<CustomField> customFields = testCase.getCustomFields();
		for ( CustomField customField : customFields )
		{
			addCustomFieldEnvironmentVariableName( customField, testLinkEnvVar );
		}
		
		return testLinkEnvVar;
	}
	
	/**
	 * <p>Formats a custom field into an environment variable. It appends 
	 * TESTLINK_TESTCASE in front of the environment variable name.</p>
	 * 
	 * <p>So, for example, the custom field which name is Sample  Custom Field and 
	 * value is <b>Sample Value</b>, will be added into the environment variables 
	 * as TESTLINK_TESTCASE_SAMPLE__CUSTOM_FIELD="Sample Value" (note for the double spaces).</p>
	 * 
	 * <p>If the custom's value contains commas (,), then this method splits the 
	 * value and, for each token found, it creates a new environment variable 
	 * appending a numeric index after its name</p>
	 * 
	 * <p>So, for example, the custom field which name is Sample Custom Field and 
	 * value is <b>Sample Value 1, Sample Value 2</b>, will generate three 
	 * environment variables: TESTLINK_TESTCASE_SAMPLE_CUSTOM_FIELD="Sample Value 1, Sample Value 2", 
	 * TESTLINK_TESTCASE_SAMPLE_CUSTOM_FIELD_0="Sample Value 1" and 
	 * TESTLINK_TESTCASE_SAMPLE_CUSTOM_FIELD_1="Sample Value 2".</p> 
	 * 
	 * @param customField The custom field
	 * @param testLinkEnvVar TestLink envVars
	 * @return Formatted name for a environment variable
	 */
	public static void addCustomFieldEnvironmentVariableName(CustomField customField, Map<String, String> testLinkEnvVar) 
	{
		String customFieldName = customField.getName();
		String customFieldValue = customField.getValue();
		
		customFieldName = customFieldName.toUpperCase(); // uppercase
		customFieldName = customFieldName.trim(); // trim
		customFieldName = TESTLINK_TESTCASE_PREFIX + customFieldName; // add prefix
		customFieldName = customFieldName.replaceAll( "\\s+", "_" ); // replace white spaces
		
		testLinkEnvVar.put(customFieldName, customFieldValue);
		
		if ( StringUtils.isNotBlank( customFieldValue ) ) 
		{
			StringTokenizer tokenizer = new StringTokenizer( customFieldValue, "," );
			if ( tokenizer.countTokens() > 1 )
			{
				int index = 0;
				while ( tokenizer.hasMoreTokens() )
				{
					String token = tokenizer.nextToken();
					token = token.trim();
					
					customFieldName = customField.getName();
					customFieldName = customFieldName.toUpperCase(); // uppercase
					customFieldName = customFieldName.trim(); // trim
					
					String tokenName = TESTLINK_TESTCASE_PREFIX + customFieldName + "_" + index; // add prefix
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
	public static EnvVars buildTestCaseEnvVars( TestCase testCase, TestProject testProject, TestPlan testPlan, Build build, BuildListener listener ) 
	{
		// Build environment variables list
		listener.getLogger().println(Messages.TestLinkBuilder_CreatingEnvVars());
		Map<String, String> testLinkEnvironmentVariables = TestLinkHelper.createTestLinkEnvironmentVariables( testCase, testProject, testPlan, build );

		// Merge with build environment variables list
		listener.getLogger().println(Messages.TestLinkBuilder_MergingEnvVars());
		listener.getLogger().println();
		
		final EnvVars buildEnvironment = new EnvVars( testLinkEnvironmentVariables );
		return buildEnvironment;
	}
	
}
