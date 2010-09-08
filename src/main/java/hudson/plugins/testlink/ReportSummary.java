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

import hudson.plugins.testlink.model.TestLinkTestCase;

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
		builder.append("<p><b>Build ID "+report.getBuildId()+"</b> - <b>Build Name ["+report.getBuildName()+"]</b></p>");
		builder.append("<p><a href=\"" + TestLinkBuildAction.URL_NAME + "\">Total of ");
        builder.append(report.getTestsTotal());
        if(previous != null){
            printDifference(
            		report.getTestsTotal(),
            		previous.getTestsTotal(), 
            		builder);
        }
        builder.append(" tests.</a> where ");
        builder.append(report.getTestsPassed());
        if(previous != null){
            printDifference(
            		report.getTestsPassed(), 
            		previous.getTestsPassed(), 
            		builder);
        }
        builder.append(" tests passed, ");
        builder.append(report.getTestsFailed());
        if(previous != null){
            printDifference(
            		report.getTestsFailed(),
            		previous.getTestsFailed(),
            		builder);
        }
        builder.append(" tests failed and ");
        builder.append(report.getTestsBlocked());
        if(previous != null){
            printDifference(
            		report.getTestsBlocked(),
            		previous.getTestsBlocked(),
            		builder);
        }
        builder.append(" tests were blocked.</p>");
		
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

		builder.append("<p>List of test cases and execution result status.</p>");
		builder.append("<table border=\"1\">\n");
		builder.append("<tr><th>Id</th><th>Plan Id</th><th>Build Id</th><th>Category</th><th>File</th><th>Result Status</th></tr>\n");
		
        for(TestLinkTestCase tc: report.getListOfTestCases()){
        	builder.append("<tr>\n");
        	
        	// TBD: colors depending on status
        	builder.append("<td>"+tc.getId()+"</td>");
        	builder.append("<td>"+tc.getPlanId()+"</td>");
        	builder.append("<td>"+tc.getBuildId()+"</td>");
        	builder.append("<td>"+tc.getCategory()+"</td>\n");
        	builder.append("<td>"+tc.getFile()+"</td>\n");
        	// TBD: show values as passed, blocked, etc
        	builder.append("<td>"+tc.getResultStatus()+"</td>\n");
        	
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
        builder.append(" (");

        if(difference >= 0){
            builder.append('+');
        }
        builder.append(difference);
        builder.append(")");
    }

}

