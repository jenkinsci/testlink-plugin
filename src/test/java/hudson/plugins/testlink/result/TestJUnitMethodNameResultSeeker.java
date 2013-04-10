/*
 * The MIT License
 *
 * Copyright (c) <2012> <Bruno P. Kinoshita>
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
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 0.1
 */
public class TestJUnitMethodNameResultSeeker extends ResultSeekerTestCase {

	private static final String KEY_CUSTOM_FIELD = "testCustomField";
	
	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.result.ResultSeekerTestCase#getAutomatedTestCases()
	 */
	@Override
	public TestCaseWrapper[] getAutomatedTestCases() {
		final TestCaseWrapper[] tcs = new TestCaseWrapper[2];

		TestCaseWrapper tc = new TestCaseWrapper();
		CustomField cf = new CustomField();
		cf = new CustomField();
		cf.setName(KEY_CUSTOM_FIELD);
		cf.setValue("br.eti.kinoshita.junit.SampleTest#methodname");
		tc.getCustomFields().add(cf);
		tc.setId(1);
		tcs[0] = tc;

		tc = new TestCaseWrapper();
		cf = new CustomField();
		cf.setName(KEY_CUSTOM_FIELD);
		cf.setValue("br.eti.kinoshita.junit.SampleTest#methodname2");
		tc.getCustomFields().add(cf);
		tc.setId(2);
		tcs[1] = tc;

		return tcs;
	}

	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.result.ResultSeekerTestCase#getResultSeeker()
	 */
	@Override
	public ResultSeeker getResultSeeker() {
		return new JUnitMethodNameResultSeeker(getResultsPattern(), KEY_CUSTOM_FIELD, false, false);
	}

	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.result.ResultSeekerTestCase#getResultsDirectory()
	 */
	@Override
	public String getResultsDirectory() {
		return "hudson/plugins/testlink/result";
	}

	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.result.ResultSeekerTestCase#getResultsPattern()
	 */
	@Override
	public String getResultsPattern() {
		return "**/TEST-*.xml";
	}
	
	public void testJUnitMethodNameResultSeekerStrategy() throws Exception {
		buildAndAssertSuccess(project);
		
		assertEquals(4, testlink.getReport().getTestsTotal());
	}

}
