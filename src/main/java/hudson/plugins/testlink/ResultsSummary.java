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

import hudson.plugins.testlink.result.TestLinkReport;
import hudson.plugins.testlink.util.Messages;
import hudson.plugins.testlink.util.TestLinkHelper;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;


/**
 * Helper class that creates report summary.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class ResultsSummary 
{

	/**
	 * Hidden constructor.
	 */
	private ResultsSummary()
	{
		super();
	}
	
	/**
	 * Creates Report Summary.
	 * 
	 * @param testLinkReport TestLink Report
	 * @param previous Previous TestLink Report
	 * @return Report Summary
	 */
	public static String createReportSummary(
			TestLinkReport testLinkReport,
			TestLinkReport previous) 
	{
		StringBuilder builder = new StringBuilder();
		builder.append("<p><b>"+Messages.ReportSummary_Summary_BuildID(testLinkReport.getBuild().getId())+"</b></p>");
		builder.append("<p><b>"+Messages.ReportSummary_Summary_BuildName(testLinkReport.getBuild().getName())+"</b></p>");
		builder.append("<p><a href=\"" + TestLinkBuildAction.URL_NAME + "\">");
        builder.append( Messages.ReportSummary_Summary_TotalOf( testLinkReport.getTestsTotal() ) );
        if(previous != null){
            printDifference(
            		testLinkReport.getTestsTotal(),
            		previous.getTestsTotal(), 
            		builder);
        }
        builder.append( " " +  Messages.ReportSummary_Summary_Tests() );
        builder.append("</a>");
        builder.append( " " + Messages.ReportSummary_Summary_Where( testLinkReport.getTestsPassed() ) );
        if(previous != null){
            printDifference(
            		testLinkReport.getTestsPassed(), 
            		previous.getTestsPassed(), 
            		builder);
        }
        builder.append( " " + Messages.ReportSummary_Summary_TestsPassed( testLinkReport.getTestsFailed() ) );
        if(previous != null){
            printDifference(
            		testLinkReport.getTestsFailed(),
            		previous.getTestsFailed(),
            		builder);
        }
        builder.append( " " + Messages.ReportSummary_Summary_TestsFailed(testLinkReport.getTestsBlocked()) );
        if(previous != null){
            printDifference(
            		testLinkReport.getTestsBlocked(),
            		previous.getTestsBlocked(),
            		builder);
        }
        builder.append( " " + Messages.ReportSummary_Summary_TestsBlocked() );
        builder.append("</p>");
		
		return builder.toString();
	}

	/**
	 * Creates detailed Report Summary.
	 * 
	 * @param report TestLink report
	 * @param previous Previous TestLink report
	 * @return Detailed Report Summary
	 */
	public static String createReportSummaryDetails(
			TestLinkReport report,
			TestLinkReport previous) 
	{
		StringBuilder builder = new StringBuilder();

		builder.append("<p>"+Messages.ReportSummary_Details_Header()+"</p>");
		builder.append("<table border=\"1\">\n");
		builder.append("<tr><th>");
		builder.append(Messages.ReportSummary_Details_TestCaseId() );
		builder.append("</th><th>");
		builder.append(Messages.ReportSummary_Details_Version() );
		builder.append("</th><th>");
		builder.append(Messages.ReportSummary_Details_Name());
		builder.append("</th><th>");
		builder.append(Messages.ReportSummary_Details_TestProjectId());
		builder.append("</th><th>");
		builder.append(Messages.ReportSummary_Details_ExecutionStatus());
		builder.append("</th></tr>\n");
		
        for(TestCase tc: report.getTestCases() )
        {
        	builder.append("<tr>\n");
        	
        	builder.append("<td>"+tc.getId()+"</td>");
        	builder.append("<td>"+tc.getVersion()+"</td>");
        	builder.append("<td>"+tc.getName()+"</td>");
        	builder.append("<td>"+tc.getTestProjectId()+"</td>");
    		builder.append("<td>"+TestLinkHelper.getExecutionStatusTextColored( tc.getExecutionStatus() )+"</td>\n");
        	
        	builder.append("</tr>\n");
        }
        
        builder.append("</table>");
        return builder.toString();
	}

	

	/**
	 * Prints the difference between two int values, showing a plus sign if the 
	 * current number is greater than the previous. 
	 * 
	 * @param current Current value
	 * @param previous Previous value
	 * @param buffer StrinbBuilder that acts as a buffer
	 */
	protected static void printDifference(int current, int previous, StringBuilder buffer){
		int difference = current - previous;
        
		if(difference > 0)
        {
			buffer.append(" (");
            
			buffer.append('+');
			
			buffer.append(difference);
	        buffer.append(")");
        }
        
    }

}

