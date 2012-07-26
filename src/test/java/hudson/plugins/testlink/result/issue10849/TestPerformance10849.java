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
package hudson.plugins.testlink.result.issue10849;

import hudson.plugins.testlink.result.JUnitCaseNameResultSeeker;
import hudson.plugins.testlink.result.ResultSeeker;
import hudson.plugins.testlink.result.ResultSeekerTestCase;
import hudson.plugins.testlink.result.TestCaseWrapper;

import org.junit.Assert;
import org.jvnet.hudson.test.Bug;

import br.eti.kinoshita.testlinkjavaapi.model.CustomField;

/**
 * Tests for issue 10849. In this issue, the user reported
 * 
 * @author Bruno P. Kinoshita
 */
@Bug(10849)
public class TestPerformance10849 extends ResultSeekerTestCase {

	private static final String KEY_CUSTOM_FIELD = "testCustomField";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * hudson.plugins.testlink.result.ResultSeekerTestCase#getResultsPattern()
	 */
	@Override
	public String getResultsPattern() {
		return "**/TEST*.xml";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * hudson.plugins.testlink.result.ResultSeekerTestCase#getResultsDirectory()
	 */
	@Override
	public String getResultsDirectory() {
		return "hudson/plugins/testlink/result/junit/issue10849/";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * hudson.plugins.testlink.result.ResultSeekerTestCase#getResultSeeker()
	 */
	@Override
	public ResultSeeker getResultSeeker() {
		return new JUnitCaseNameResultSeeker(getResultsPattern(),
				KEY_CUSTOM_FIELD, false, false);
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
		TestCaseWrapper[] tcs = new TestCaseWrapper[100];
		for (int i = 0; i < 100; ++i) {
			TestCaseWrapper tc = new TestCaseWrapper();
			CustomField cf = new CustomField();
			cf.setName(KEY_CUSTOM_FIELD);
			cf.setValue("testAOPLogging"); // TBD: create a AlwaysAddResultSeeker or something similar
			tc.setId((i + 1));
			tc.setName("TC for issue 10849");
			tc.getCustomFields().add(cf);
			tcs[i] = tc;
		}
		return tcs;
	}

	public void testPerformance10849() throws Exception {
		long start = System.currentTimeMillis();
		buildAndAssertSuccess(project);
		assertTrue(testlink.getReport().getTestsTotal() > 0);
		long end = System.currentTimeMillis();

		System.out.println("Took: " + (end - start));

		Assert.assertTrue((end - start) < 5000);
	}

}
