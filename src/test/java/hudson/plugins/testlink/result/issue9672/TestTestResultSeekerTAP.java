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
package hudson.plugins.testlink.result.issue9672;

import hudson.plugins.testlink.result.ResultSeeker;
import hudson.plugins.testlink.result.ResultSeekerTestCase;
import hudson.plugins.testlink.result.TestCaseWrapper;
import hudson.plugins.testlink.result.TAPFileNameResultSeeker;

import org.jvnet.hudson.test.Bug;

import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;

/**
 * Tests TestResultSeeker with TAP.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.5
 */
@Bug(9672)
public class TestTestResultSeekerTAP extends ResultSeekerTestCase {

	private final static String KEY_CUSTOM_FIELD = "testCustomField";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * hudson.plugins.testlink.result.ResultSeekerTestCase#getResultsPattern()
	 */
	@Override
	public String getResultsPattern() {
		return "*.tap";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * hudson.plugins.testlink.result.ResultSeekerTestCase#getResultsDirectory()
	 */
	@Override
	public String getResultsDirectory() {
		return "hudson/plugins/testlink/result/tap/issue9672/";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * hudson.plugins.testlink.result.ResultSeekerTestCase#getResultSeeker()
	 */
	@Override
	public ResultSeeker getResultSeeker() {
		return new TAPFileNameResultSeeker(getResultsPattern(), KEY_CUSTOM_FIELD, false, false);
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
		final TestCaseWrapper[] tcs = new TestCaseWrapper[2];

		TestCaseWrapper tc = new TestCaseWrapper();
		CustomField cf = new CustomField();
		cf.setName(KEY_CUSTOM_FIELD);
		cf.setValue("A, B");
		tc.getCustomFields().add(cf);
		tc.setId(1);
		tcs[0] = tc;

		tc = new TestCaseWrapper();
		cf = new CustomField();
		cf.setName(KEY_CUSTOM_FIELD);
		cf.setValue("A, K");
		tc.getCustomFields().add(cf);
		tc.setId(2);
		tcs[1] = tc;

		return tcs;
	}

	public void testTwoTestSetsAandB() throws Exception {
		buildAndAssertSuccess(project);

		assertEquals(2, testlink.getReport().getTestsTotal());
		assertEquals(ExecutionStatus.NOT_RUN, testlink.getTestCases().get(0)
				.getExecutionStatus());
	}

	public void testTwoTestSetsAandNonExistentK() throws Exception {
		buildAndAssertSuccess(project);

		assertEquals(2, testlink.getReport().getTestsTotal());
		assertEquals(ExecutionStatus.PASSED, testlink.getTestCases().get(1)
				.getExecutionStatus());
	}

}
