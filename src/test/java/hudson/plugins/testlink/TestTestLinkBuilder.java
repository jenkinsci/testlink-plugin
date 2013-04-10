/*
 * The MIT License
 *
 * Copyright (c) <2011> <Bruno P. Kinoshita>
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

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import hudson.EnvVars;
import hudson.tasks.BuildStep;
import hudson.tasks.Shell;
import hudson.util.VariableResolver;
import hudson.util.VariableResolver.ByMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.jvnet.hudson.test.Bug;
import org.jvnet.hudson.test.HudsonTestCase;

import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionStatus;

/**
 * Tests TestLinkBuilder class.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
public class TestTestLinkBuilder 
extends HudsonTestCase
{

	private TestLinkBuilder builder = null;	
	
	public void setUp() 
	throws Exception
	{
		super.setUp();
		
		builder = new TestLinkBuilder(
				"No testlink", 
				"No project",
				"No plan", 
				"No build", 
				"class, time, sample-job-$BUILD_ID", 
				Boolean.FALSE,
				Boolean.FALSE,
				Boolean.FALSE,
				Boolean.FALSE,
				null, 
				null, 
				null, 
				null, 
				Boolean.FALSE, 
				Boolean.FALSE,  
				Boolean.FALSE, 
				null);
	}
	
	/**
	 * Tests the generated list of custom fields.
	 */
	@Bug(13173)
	public void testListOfCustomFields()
	{
		EnvVars envVars = new EnvVars();
		@SuppressWarnings({ "unchecked", "rawtypes" })
		VariableResolver<String> varRes = new ByMap.ByMap(envVars);
		
		envVars.put("BUILD_ID", "1");
		String[] customFieldsNames = builder.createArrayOfCustomFieldsNames(varRes, envVars);
		
		assertNotNull( customFieldsNames );
		assertTrue( customFieldsNames.length == 3 );
		assertEquals( customFieldsNames[0], "class" );
		assertEquals( customFieldsNames[1], "time" );
		assertEquals( customFieldsNames[2], "sample-job-1" );
	}
	
	public void testNull()
	{
		builder = new TestLinkBuilder(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null );
		
		assertNotNull( builder );
		
		assertNull( builder.getTestLinkName() );
		
		assertNull( builder.getTestProjectName() );
		
		assertNull( builder.getTestPlanName() );
		
		assertNull( builder.getBuildName() );
		
		assertNull( builder.getSingleBuildSteps() );
		
		assertNull( builder.getBeforeIteratingAllTestCasesBuildSteps() );
		
		assertNull( builder.getIterativeBuildSteps() );
		
		assertNull( builder.getAfterIteratingAllTestCasesBuildSteps() );
		
		assertNull( builder.getCustomFields() );
		
		assertNull( builder.getTransactional() );
		
		assertNull( builder.getFailIfNoResults() );
		
	}
	
	/**
	 * Tests getters methods.
	 */
	public void testGetters()
	{
		
		Shell shell = new Shell("ls -la");
		List<BuildStep> singleBuildSteps = new ArrayList<BuildStep>();
		singleBuildSteps.add(shell);
		
		builder = new TestLinkBuilder(
			"No testlink", 
			"No project",
			"No plan", 
			"No build", 
			"class, time", 
			Boolean.FALSE,
			Boolean.FALSE,
			Boolean.FALSE,
			Boolean.FALSE,
			singleBuildSteps, 
			null, 
			null, 
			null, 
			Boolean.FALSE, 
			Boolean.FALSE,  
			Boolean.FALSE, 
			null);
		
		assertNotNull( hudson );
		//FreeStyleProject project = new FreeStyleProject(hudson, "No project");
		//assertNotNull ( (AbstractProject<?, ?>)builder.getProjectAction(project) );
		
		assertNotNull( builder.getTestLinkName() );
		assertEquals( builder.getTestLinkName(), "No testlink" );
		
		assertNotNull( builder.getTestProjectName() );
		assertEquals( builder.getTestProjectName(), "No project" );
		
		assertNotNull( builder.getTestPlanName() );
		assertEquals( builder.getTestPlanName(), "No plan" );
		
		assertNotNull( builder.getBuildName() );
		assertEquals( builder.getBuildName(), "No build" );
		
		assertNotNull( builder.getSingleBuildSteps() );
		assertEquals( builder.getSingleBuildSteps() , singleBuildSteps);
		assertEquals( builder.getSingleBuildSteps().size(), 1);
		
		assertNotNull( builder.getCustomFields() );
		assertEquals( builder.getCustomFields(), "class, time" );
		
		assertFalse( builder.getTransactional() );
		assertFalse( builder.getFailIfNoResults() );
		
	}

	@Test
	public void getExecutionStatusesShouldReturnAllWhenAllAreFalse() {
		builder = new TestLinkBuilder(null, null, null, null, null, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, null, null, null, null, null, null, null, null );
		Set<ExecutionStatus> statuses = builder.getExecutionStatuses();
		assertThat(statuses, hasSize(4));
		assertThat(
				statuses,
				contains(ExecutionStatus.NOT_RUN, ExecutionStatus.PASSED,
						ExecutionStatus.FAILED, ExecutionStatus.BLOCKED));
	}
	
	@Test
	public void getExecutionStatusesShouldReturnAllWhenAllAreNull() {
		builder = new TestLinkBuilder(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null );
		Set<ExecutionStatus> statuses = builder.getExecutionStatuses();
		assertThat(statuses, hasSize(4));
		assertThat(
				statuses,
				contains(ExecutionStatus.NOT_RUN, ExecutionStatus.PASSED,
						ExecutionStatus.FAILED, ExecutionStatus.BLOCKED));
	}
	
	@Test
	public void getExecutionStatusesShouldReturnSomeWhenSomeAreTrue() {
		builder = new TestLinkBuilder(null, null, null, null, null, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, null, null, null, null, null, null, null, null );
		Set<ExecutionStatus> statuses = builder.getExecutionStatuses();
		assertThat(statuses, hasSize(2));
		assertThat(
				statuses,
				contains(ExecutionStatus.NOT_RUN, ExecutionStatus.FAILED));
	}
	
	@Test
	public void getExecutionStatusesShouldOneSomeWhenNotRunIsTrue() {
		builder = new TestLinkBuilder(null, null, null, null, null, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, null, null, null, null, null, null, null, null );
		Set<ExecutionStatus> statuses = builder.getExecutionStatuses();
		assertThat(statuses, hasSize(1));
		assertThat(
				statuses,
				contains(ExecutionStatus.NOT_RUN));
	}
	
	@Test
	public void getExecutionStatusesShouldOneSomeWhenPassedIsTrue() {
		builder = new TestLinkBuilder(null, null, null, null, null, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, null, null, null, null, null, null, null, null );
		Set<ExecutionStatus> statuses = builder.getExecutionStatuses();
		assertThat(statuses, hasSize(1));
		assertThat(
				statuses,
				contains(ExecutionStatus.PASSED));
	}
	
	@Test
	public void getExecutionStatusesShouldOneSomeWhenFailedIsTrue() {
		builder = new TestLinkBuilder(null, null, null, null, null, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, null, null, null, null, null, null, null, null );
		Set<ExecutionStatus> statuses = builder.getExecutionStatuses();
		assertThat(statuses, hasSize(1));
		assertThat(
				statuses,
				contains(ExecutionStatus.FAILED));
	}
	
	@Test
	public void getExecutionStatusesShouldOneSomeWhenBlockedIsTrue() {
		builder = new TestLinkBuilder(null, null, null, null, null, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, null, null, null, null, null, null, null, null );
		Set<ExecutionStatus> statuses = builder.getExecutionStatuses();
		assertThat(statuses, hasSize(1));
		assertThat(
				statuses,
				contains(ExecutionStatus.BLOCKED));
	}
}
