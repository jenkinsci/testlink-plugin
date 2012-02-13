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

import hudson.plugins.testlink.result.TestCaseWrapper;

import java.util.LinkedList;
import java.util.List;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.model.TestProject;

/**
 * Fake TestLinkSite, used for testing.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 3.1
 */
public class TestLinkSiteFake extends TestLinkSite {

	protected List<TestCaseWrapper> testCases = new LinkedList<TestCaseWrapper>();
	
	/**
	 * {@inheritDoc}
	 */
	public TestLinkSiteFake(TestLinkAPI api, TestProject testProject,
			TestPlan testPlan, Build build) {
		super(api, testProject, testPlan, build);
	}

	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.TestLinkSite#updateTestCases(java.util.Collection)
	 */
	@Override
	public void updateTestCase(TestCaseWrapper testCase) {
		// OK, do nothing
		testCases.add(testCase);
	}
	
	/**
	 * @return the testCases
	 */
	public List<TestCaseWrapper> getTestCases() {
		return testCases;
	}
	
}
