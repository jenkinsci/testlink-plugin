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
package hudson.plugins.testlink.result;

import br.eti.kinoshita.testlinkjavaapi.model.CustomField;

/**
 * Tests ResultSeeker with JUnit suite name.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.5
 */
public class TestJUnitSuiteNameResultSeeker extends ResultSeekerTestCase {

	private final static String KEY_CUSTOM_FIELD = "testCustomField";
	
	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.result.ResultSeekerTestCaseWrapper#getResultsPattern()
	 */
	@Override
	public String getResultsPattern() {
		return "TEST-TestJUnitTestSuiteSeeker*.xml";
	}
	
	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.result.ResultSeekerTestCaseWrapper#getResultsDirectory()
	 */
	@Override
	public String getResultsDirectory() {
		return "hudson/plugins/testlink/result/junit/";
	}
	
	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.result.ResultSeekerTestCaseWrapper#getAutomatedTestCaseWrappers()
	 */
	@Override
	public TestCaseWrapper[] getAutomatedTestCases() {
		TestCaseWrapper[] tcs = new TestCaseWrapper[9];

		TestCaseWrapper tc = new TestCaseWrapper();
		CustomField cf = new CustomField();
		cf.setName(KEY_CUSTOM_FIELD);
		cf.setValue("suiteA");
		tc.getCustomFields().add(cf);
		tc.setId(1);
		tcs[0] = tc;

		tc = new TestCaseWrapper();
		cf = new CustomField();
		cf.setName(KEY_CUSTOM_FIELD);
		cf.setValue("suiteA, suiteB");
		tc.getCustomFields().add(cf);
		tc.setId(2);
		tcs[1] = tc;

		tc = new TestCaseWrapper();
		cf = new CustomField();
		cf.setName(KEY_CUSTOM_FIELD);
		cf.setValue("suiteB, suiteC");
		tc.getCustomFields().add(cf);
		tc.setId(3);
		tcs[2] = tc;

		tc = new TestCaseWrapper();
		cf = new CustomField();
		cf.setName(KEY_CUSTOM_FIELD);
		cf.setValue("suiteA, suiteB, suiteC");
		tc.getCustomFields().add(cf);
		tc.setId(4);
		tcs[3] = tc;

		tc = new TestCaseWrapper();
		cf = new CustomField();
		cf.setName(KEY_CUSTOM_FIELD);
		cf.setValue("suiteA, suiteB, suiteD");
		tc.getCustomFields().add(cf);
		tc.setId(5);
		tcs[4] = tc;

		tc = new TestCaseWrapper();
		cf = new CustomField();
		cf.setName(KEY_CUSTOM_FIELD);
		cf.setValue("suiteA, suiteB, suiteC");
		tc.getCustomFields().add(cf);
		tc.setId(6);
		tcs[5] = tc;

		tc = new TestCaseWrapper();
		cf = new CustomField();
		cf.setName(KEY_CUSTOM_FIELD);
		cf.setValue("suiteC, suiteY");
		tc.getCustomFields().add(cf);
		tc.setId(7);
		tcs[6] = tc;

		tc = new TestCaseWrapper();
		cf = new CustomField();
		cf.setName(KEY_CUSTOM_FIELD);
		cf.setValue("suiteA, suiteX");
		tc.getCustomFields().add(cf);
		tc.setId(8);
		tcs[7] = tc;

		tc = new TestCaseWrapper();
		cf = new CustomField();
		cf.setName(KEY_CUSTOM_FIELD);
		cf.setValue("suiteA, suiteK");
		tc.getCustomFields().add(cf);
		tc.setId(9);
		tcs[8] = tc;
		
		return tcs;
	}
	
	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.result.ResultSeekerTestCaseWrapper#getResultSeeker()
	 */
	@Override
	public ResultSeeker getResultSeeker() {
		return new JUnitSuiteNameResultSeeker(getResultsPattern(), KEY_CUSTOM_FIELD, false, false);
	}

	public void testManyTestLinkTestCaseWrappersToManyJUnitSuitesInManyFiles() throws Exception {
		buildAndAssertSuccess(project);
		
		assertTrue("Wrong number of results", testlink.getReport().getTestsTotal() == 7);

		//assertTrue(testlink.getReport().get(1).getExecutionStatus() == ExecutionStatus.FAILED);

		//assertTrue(found.get(2).getExecutionStatus() == ExecutionStatus.FAILED);
		//assertTrue(found.get(3).getExecutionStatus() == ExecutionStatus.PASSED);
		//assertTrue(found.get(4).getExecutionStatus() == ExecutionStatus.FAILED);
		//assertTrue(found.get(5).getExecutionStatus() == ExecutionStatus.NOT_RUN);
		//assertTrue(found.get(6).getExecutionStatus() == ExecutionStatus.FAILED);
		//assertTrue(found.get(7).getExecutionStatus() == ExecutionStatus.PASSED);
		//assertTrue(found.get(8).getExecutionStatus() == ExecutionStatus.FAILED);
		//assertTrue(found.get(9).getExecutionStatus() == ExecutionStatus.NOT_RUN);
	}

}
