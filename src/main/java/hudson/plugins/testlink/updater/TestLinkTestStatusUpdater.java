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
 * Kinow ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Kinow.                                      
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 22/11/2010
 */
package hudson.plugins.testlink.updater;

import hudson.plugins.testlink.model.TestResult;
import hudson.plugins.testlink.util.TestLinkHelper;

import java.io.PrintStream;
import java.util.List;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.TestLinkAPIException;
import br.eti.kinoshita.testlinkjavaapi.model.Attachment;
import br.eti.kinoshita.testlinkjavaapi.model.ReportTCResultResponse;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 22/11/2010
 */
public class TestLinkTestStatusUpdater 
{
	private static final String EXECUTIONS_TABLE = "executions";
	
	/* (non-Javadoc)
	 * @see hudson.FilePath.FileCallable#invoke(java.io.File, hudson.remoting.VirtualChannel)
	 */
	public void updateTestCases( TestLinkAPI api, PrintStream ps, List<TestResult> testResults ) 
	throws TestLinkAPIException
	{
		if ( testResults.size() > 0 )
		{
			ps.println("Updating " + testResults.size() + " test case(s) execution status");
			// Update TestLink Test Status
			for( TestResult testResult : testResults )
			{
				TestCase testCase = testResult.getTestCase();
				ps.println("Updating automated test case " + testCase.getName() + " with execution status " + TestLinkHelper.getExecutionStatusText( testCase.getExecutionStatus() ) );
				// Update Test Case status
				ReportTCResultResponse reportTCResultResponse = api.reportTCResult(
						testCase.getId(), 
						testCase.getInternalId(), 
						testResult.getTestPlan().getId(), 
						testCase.getExecutionStatus(), 
						testResult.getBuild().getId(), 
						testResult.getBuild().getName(), 
						testResult.getNotes(), 
						null, // guess
						null, // bug id
						null, // platform id 
						null, // platform name
						null, // custom fields
						null);
				
				for ( Attachment attachment : testResult.getAttachments() )
				{
					ps.println("Uploading execution " + 
							reportTCResultResponse.getExecutionId() + " attachment " + 
							attachment.getFileName());
					api.uploadAttachment(
							reportTCResultResponse.getExecutionId(), 
							EXECUTIONS_TABLE, // TBD: replace with TestLinkTables enum value
							attachment.getTitle(), 
							attachment.getDescription(), 
							attachment.getFileName(), 
							attachment.getFileType(), 
							attachment.getContent());
				}
			} 
		}
		else
		{
			ps.println("Skipping update test case execution status. Nothing found.");
		}
	}
	
}
