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
package hudson.plugins.testlink.result;

import br.eti.kinoshita.testlinkjavaapi.model.CustomField;

/**
 * Tests ResultSeeker with TAP file name.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
public class TestTAPFileNameResultSeeker extends ResultSeekerTestCase
{
	
	private static final String KEY_CUSTOM_FIELD = "testCustomField";
	
	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.result.ResultSeekerTestCase#getResultsDirectory()
	 */
	@Override
	public String getResultsDirectory() {
		return "hudson/plugins/testlink/result/tap/";
	}
	
	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.result.ResultSeekerTestCase#getResultsPattern()
	 */
	@Override
	public String getResultsPattern() {
		return "*.tap";
	}
	
	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.result.ResultSeekerTestCase#getResultSeeker()
	 */
	@Override
	public ResultSeeker getResultSeeker() {
		return new TAPFileNameResultSeeker(getResultsPattern(), KEY_CUSTOM_FIELD, false, false, false, false, false);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * hudson.plugins.testlink.result.ResultSeekerTestCase#getAutomatedTestCases
	 * ()
	 */
	@Override
	public TestCaseWrapper[] getAutomatedTestCases()
	{
		final TestCaseWrapper[] tcs = new TestCaseWrapper[3];
		
		TestCaseWrapper tc = new TestCaseWrapper();
		CustomField cf = new CustomField();
		cf.setName(KEY_CUSTOM_FIELD);
		cf.setValue("br.eti.kinoshita.tap.SampleTest");
		tc.getCustomFields().add(cf);
		tc.setId(1);
		tcs[0] = tc;
		
		
		tc = new TestCaseWrapper();
		cf = new CustomField();
		cf.setName(KEY_CUSTOM_FIELD);
		cf.setValue("br.eti.kinoshita.tap.SampleTest2");
		tc.getCustomFields().add(cf);
		tc.setId(2);
		tcs[1] = tc;
		
		tc = new TestCaseWrapper();
		cf = new CustomField();
		cf.setName(KEY_CUSTOM_FIELD);
		cf.setValue("br.eti.kinoshita.tap.SampleTest3");
		tc.getCustomFields().add(cf);
		tc.setId(3);
		tcs[2] = tc;
		
		return tcs;
	}

	public void testTestResultSeekerTAPOne() throws Exception {
		buildAndAssertSuccess(project);
		
		assertEquals(3, testlink.getReport().getTestsTotal());
		//assertTrue( found.get(1).getExecutionStatus() == ExecutionStatus.PASSED );
	}
	
	public void testTestResultSeekerTAPThree() throws Exception {
		buildAndAssertSuccess(project);
		
		assertEquals(3, testlink.getReport().getTestsTotal());
		//assertTrue( found.get(2).getExecutionStatus() == ExecutionStatus.FAILED );
		//assertTrue( found.get(3).getExecutionStatus() == ExecutionStatus.FAILED );
	}
	
}
