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
package hudson.plugins.testlink;

import hudson.plugins.testlink.result.TestCaseWrapper;
import hudson.plugins.testlink.util.TestLinkHelper;

import java.lang.reflect.Constructor;
import java.util.Locale;

import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionType;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;

/**
 * Tests the ReportSummary class.
 *
 * @see {@link ResultsSummary}
 *
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
public class TestReportSummary 
extends junit.framework.TestCase
{

	/**
	 * Prepares for the tests.
	 */
	public void setUp()
	{
		Locale.setDefault(new Locale("en", "US"));
	}
	
	/**
	 * Tests the hidden constructor of the ReportSummary class.
	 */
	public void testConstructor()
	{
		try
		{
			final Constructor<?> c = TestLinkHelper.class.getDeclaredConstructors()[0];
			c.setAccessible(true);
			final Object o = c.newInstance((Object[]) null);

			assertNotNull(o);
		}
		catch (Exception e)
		{
			fail("Failed to instantiate constructor: " + e.getMessage());
		}
	}
	
	/**
	 * Tests getPlusSignal() method.
	 */
	public void getPlusSignal()
	{
		String plusSignal = TestLinkHelper.getPlusSignal(1, 0);
		assertEquals( plusSignal, " (+1)" );
		
		plusSignal = TestLinkHelper.getPlusSignal(0, 1);
		assertEquals( plusSignal, "" );
	}
	
	/**
	 * Tests the createReportSummary() method with a previous TestLinkReport.
	 */
	public void testSummary()
	{
		Report report = new Report(1, "My build");
		
		report.setPassed(1);
		
		report.setFailed(1);
		
		report.setBlocked(1);
		
		String reportSummary = TestLinkHelper.createReportSummary(report, null);
		assertNotNull(reportSummary);
		assertEquals(reportSummary, "<p><b>TestLink build ID: 1</b></p><p><b>TestLink build name: My build</b></p><p><a href=\"testLinkResult\">Total of 3 tests</a>. Where 1 passed, 1 failed, 1 were blocked and 0 were not executed.</p>");
	}
	
	public void testSummaryWithTestlink()
	{
		Report report = new Report(1, "My build");
		
		report.setPassed(1);
		
		report.setFailed(1);
		
		report.setBlocked(1);
		String url="http://host:port/testlink";
		report.setLinkResults(url);
		report.setTestPlanId(1);
		
		String reportSummary = TestLinkHelper.createReportSummary(report, null);
		assertNotNull(reportSummary);
		assertEquals(reportSummary, "<link rel='stylesheet' type='text/css' href='/plugin/testlink/css/testlink.css'/><div id='testlinkMenu'><ul><b>Show in Testlink:</b><li class='testlink-button'><a target='blank' style='color: green' href=http://host:port/testlink/lib/results/resultsTC.php?format=0&tplan_id=1>Matriz resultados</a></li><li class='testlink-button'><a target='blank' style='color: red' href=http://host:port/testlink/lib/results/resultsByStatus.php?type=f&format=0&tplan_id=1>Errores</a></li><li class='testlink-button'><a target='blank' style='color: gray' href=http://host:port/testlink/lib/results/resultsByStatus.php?type=n&format=0&tplan_id=1>No ejecutado</a></li><li class='testlink-button'><a target='blank' style='color: rgb(187, 187, 60)' href=http://host:port/testlink/lib/results/resultsByStatus.php?type=b&format=0&tplan_id=1>Bloqueado</a></li></ul></div><br/><p><b>TestLink build ID: 1</b></p><p><b>TestLink build name: My build</b></p><p><a href=\"testLinkResult\">Total of 3 tests</a>. Where 1 passed, 1 failed, 1 were blocked and 0 were not executed.</p>");
	}
	
	/**
	 * Tests the createReportSummary() method with a previous TestLinkReport.
	 */
	public void testSummaryWithPrevious()
	{
		Report report = new Report(1, "My build");
		
		report.setPassed(1);
		report.setFailed(1);
		report.setBlocked(1);
		
		Report previous = new Report(1, "My build");
		
		previous.setPassed(1);
		previous.setFailed(1);
		
		String reportSummary = TestLinkHelper.createReportSummary(report, previous);
		assertNotNull(reportSummary);
		assertEquals(reportSummary, "<p><b>TestLink build ID: 1</b></p><p><b>TestLink build name: My build</b></p><p><a href=\"testLinkResult\">Total of 3 (+1) tests</a>. Where 1 passed, 1 failed, 1 (+1) were blocked and 0 were not executed.</p>");
	}
	
	/**
	 * Tests the createReportSummaryDetails() method.
	 */
	public void testSummaryDetails()
	{
		Report report = new Report(1, null);
		
		TestCase testCase1 = new TestCase(1, "tc1", 1, 1, "kinow", "No summary", null, "", null, ExecutionType.AUTOMATED, null, 1, 1, null, false, null, 1, 1, null, null, ExecutionStatus.PASSED, null, null);
		TestCase testCase2 = new TestCase(2, "tc2", 2, 2, "kinow", "No summary", null, "", null, ExecutionType.AUTOMATED, null, 2, 2, null, false, null, 2, 2, null, null, ExecutionStatus.FAILED, null, null);
		TestCase testCase3 = new TestCase(3, "tc3", 3, 3, "kinow", "No summary", null, "", null, ExecutionType.AUTOMATED, null, 3, 3, null, false, null, 3, 3, null, null, ExecutionStatus.BLOCKED, null, null);
		testCase1.setFullExternalId("1");
		testCase2.setFullExternalId("2");
		testCase3.setFullExternalId("3");
		
		TestCaseWrapper tc1 = new TestCaseWrapper(testCase1);
		TestCaseWrapper tc2 = new TestCaseWrapper(testCase2);
		TestCaseWrapper tc3 = new TestCaseWrapper(testCase3);
		
		report.addTestCase(tc1);
		report.addTestCase(tc2);
		report.addTestCase(tc3);
		
		Report previous = new Report(1, null);
		
		previous.addTestCase(tc1);
		previous.addTestCase(tc2);
		
		String reportSummaryDetails = TestLinkHelper.createReportSummaryDetails(report, previous);
		assertNotNull(reportSummaryDetails);
		
		String expectedDetails = ""+
		"<p>List of test cases and execution result status</p><table border=\"1\">\n" +
"<tr><th>Test case ID</th><th>Test case external ID</th><th>Version</th><th>Name</th><th>Test project ID</th><th>Execution status</th></tr>\n" +
"<tr>\n" +
"<td>1</td><td>1</td><td>1</td><td>tc1</td><td>1</td><td><span style='color: green'>Passed</span></td>\n" +
"</tr>\n" +
"<tr>\n" +
"<td>2</td><td>2</td><td>2</td><td>tc2</td><td>2</td><td><span style='color: red'>Failed</span></td>\n" +
"</tr>\n" +
"<tr>\n" +
"<td>3</td><td>3</td><td>3</td><td>tc3</td><td>3</td><td><span style='color: yellow'>Blocked</span></td>\n" +
"</tr>\n" +
"</table>";
		
		assertEquals(expectedDetails, reportSummaryDetails);
	}
	
}
