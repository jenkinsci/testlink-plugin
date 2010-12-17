/**
 *	 __                                        
 *	/\ \      __                               
 *	\ \ \/'\ /\_\    ___     ___   __  __  __  
 *	 \ \ , < \/\ \ /' _ `\  / __`\/\ \/\ \/\ \ 
 *	  \ \ \\`\\ \ \/\ \/\ \/\ \L\ \ \ \_/ \_/ \
 *	   \ \_\ \_\ \_\ \_\ \_\ \____/\ \___x___/'
 *	    \/_/\/_/\/_/\/_/\/_/\/___/  \/__//__/  
 *                                          
 * Copyright (c) 1999-present Kinow
 * Casa Verde - São Paulo - SP. Brazil.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Kinow ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Kinow.                                      
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 16/08/2010
 */
package hudson.plugins.testlink;

import hudson.plugins.testlink.model.TestLinkReport;
import hudson.plugins.testlink.util.TestLinkHelper;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;


/**
 * Helper class that creates report summary.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 08/09/2010
 */
public class ReportSummary {

	/**
	 * Creates Report Summary.
	 * 
	 * @param report TestLink Report
	 * @param previous Previous TestLink Report
	 * @return Report Summary
	 */
	public static String createReportSummary(
			TestLinkReport report,
			TestLinkReport previous) 
	{
		StringBuilder builder = new StringBuilder();
		builder.append("<p><b>"+Messages.ReportSummary_Summary_BuildID(report.getBuild().getId())+"</b></p>");
		builder.append("<p><b>"+Messages.ReportSummary_Summary_BuildName(report.getBuild().getName())+"</b></p>");
		builder.append("<p><a href=\"" + TestLinkBuildAction.URL_NAME + "\">");
        builder.append( Messages.ReportSummary_Summary_TotalOf( report.getTestsTotal() ) );
        if(previous != null){
            printDifference(
            		report.getTestsTotal(),
            		previous.getTestsTotal(), 
            		builder);
        }
        builder.append( " " +  Messages.ReportSummary_Summary_Tests() );
        builder.append("</a>");
        builder.append( " " + Messages.ReportSummary_Summary_Where( report.getTestsPassed() ) );
        if(previous != null){
            printDifference(
            		report.getTestsPassed(), 
            		previous.getTestsPassed(), 
            		builder);
        }
        builder.append( " " + Messages.ReportSummary_Summary_TestsPassed( report.getTestsFailed() ) );
        if(previous != null){
            printDifference(
            		report.getTestsFailed(),
            		previous.getTestsFailed(),
            		builder);
        }
        builder.append( " " + Messages.ReportSummary_Summary_TestsFailed(report.getTestsBlocked()) );
        if(previous != null){
            printDifference(
            		report.getTestsBlocked(),
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
        	
        	// TBD: colors depending on status
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
	 * @param builder StrinbBuilder that acts as a buffer
	 */
	private static void printDifference(int current, int previous, StringBuilder builder){
		int difference = current - previous;
        
		if(difference > 0)
        {
			builder.append(" (");
            
			builder.append('+');
			
			builder.append(difference);
	        builder.append(")");
        }
        
    }

}

