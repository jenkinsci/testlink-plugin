/* 
 * The MIT License
 * 
 * Copyright (c) 2012 Bruno P. Kinoshita <http://www.kinoshita.eti.br>
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
package hudson.plugins.testlink.result.issue11894;

import hudson.plugins.testlink.result.ResultSeeker;
import hudson.plugins.testlink.result.ResultSeekerTestCase;
import hudson.plugins.testlink.result.TestCaseWrapper;
import hudson.plugins.testlink.result.TestNGMethodNameResultSeeker;

import org.jvnet.hudson.test.Bug;

import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;

/**
 * Tests for issue 11894.
 *  
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 3.1
 */
@Bug(11894)
public class TestIssue11894 extends ResultSeekerTestCase {

	private final static String KEY_CUSTOM_FIELD = "testCustomField";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * hudson.plugins.testlink.result.ResultSeekerTestCase#getResultsDirectory()
	 */
	@Override
	public String getResultsDirectory() {
		return "hudson/plugins/testlink/result/issue11894/";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * hudson.plugins.testlink.result.ResultSeekerTestCase#getResultsPattern()
	 */
	@Override
	public String getResultsPattern() {
		return "testng*.xml";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * hudson.plugins.testlink.result.ResultSeekerTestCase#getResultSeeker()
	 */
	@Override
	public ResultSeeker getResultSeeker() {
		return new TestNGMethodNameResultSeeker(getResultsPattern(), KEY_CUSTOM_FIELD, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * hudson.plugins.testlink.result.ResultSeekerTestCase#getAutomatedTestCases
	 * ()
	 */
	@Override
	public TestCaseWrapper[] getAutomatedTestCases() {
		final TestCaseWrapper[] tcs = new TestCaseWrapper[3];

		TestCaseWrapper tc = new TestCaseWrapper();
		CustomField cf = new CustomField();
		cf.setName(KEY_CUSTOM_FIELD);
		cf.setValue("unit.testng.TestAccountCreation#test1");
		tc.getCustomFields().add(cf);
		tc.setId(1);
		tc.setKeyCustomFieldValue(cf.getValue());
		tcs[0] = tc;

		tc = new TestCaseWrapper();
		cf = new CustomField();
		cf.setName(KEY_CUSTOM_FIELD);
		cf.setValue("unit.testng.TestAccountCreation#test2");
		tc.getCustomFields().add(cf);
		tc.setId(2);
		tc.setKeyCustomFieldValue(cf.getValue());
		tcs[1] = tc;
		
		tc = new TestCaseWrapper();
		cf = new CustomField();
		cf.setName(KEY_CUSTOM_FIELD);
		cf.setValue("unit.testng.TestAccountCreation#test3");
		tc.getCustomFields().add(cf);
		tc.setId(3);
		tc.setKeyCustomFieldValue(cf.getValue());
		tcs[2] = tc;

		return tcs;
	}

	public void testTestResultSeekerThreeMethodsInDifferentClasses() throws Exception {
		buildAndAssertSuccess(project);
		
		assertEquals(3, testlink.getReport().getTestsTotal());
		assertEquals(ExecutionStatus.PASSED , testlink.getTestCases().get(0).getExecutionStatus());
		assertEquals(ExecutionStatus.FAILED , testlink.getTestCases().get(1).getExecutionStatus());
		assertEquals(Integer.valueOf(3), testlink.getTestCases().get(1).getId());
		assertEquals(ExecutionStatus.PASSED , testlink.getTestCases().get(2).getExecutionStatus());
	}

}
