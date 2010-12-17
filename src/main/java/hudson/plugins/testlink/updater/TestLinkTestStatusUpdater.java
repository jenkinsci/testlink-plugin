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
package hudson.plugins.testlink.updater;

import hudson.plugins.testlink.Messages;
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
 * @since 2.0
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
			ps.println( Messages.TestLinkBuilder_Update_AutomatedTestCases( testResults.size() ) );
			// Update TestLink Test Status
			for( TestResult testResult : testResults )
			{
				TestCase testCase = testResult.getTestCase();
				ps.println( Messages.TestLinkBuilder_Update_AutomatedTestCase(testCase.getName(), TestLinkHelper.getExecutionStatusText( testCase.getExecutionStatus() )) );
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
					ps.println( Messages.TestLinkBuilder_Upload_ExecutionAttachment(reportTCResultResponse.getExecutionId(), attachment.getFileName()) );
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
			ps.println(Messages.TestLinkBuilder_Update_Skipped());
		}
	}
	
}
