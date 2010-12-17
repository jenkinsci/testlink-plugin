/**
 * 
 */
package hudson.plugins.testlink.util;

import hudson.plugins.testlink.Messages;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;

/**
 * @author Bruno P. Kinoshita
 *
 */
public class TestLinkHelper 
{

	/**
	 * @param executionStatus
	 * @return
	 */
	public static String getExecutionStatusText(ExecutionStatus executionStatus) 
	{
		String executionStatusText = Messages.TestLinkBuilder_ExecutionStatus_Undefined();
		if ( executionStatus == ExecutionStatus.FAILED )
		{
			executionStatusText = Messages.TestLinkBuilder_ExecutionStatus_Failed();
		}
		if ( executionStatus == ExecutionStatus.PASSED )
		{
			executionStatusText = Messages.TestLinkBuilder_ExecutionStatus_Passed();
		}
		if ( executionStatus == ExecutionStatus.BLOCKED )
		{
			executionStatusText = Messages.TestLinkBuilder_ExecutionStatus_Blocked();
		}
		if ( executionStatus == ExecutionStatus.NOT_RUN )
		{
			executionStatusText = Messages.TestLinkBuilder_ExecutionStatus_NotRun();
		}
		return executionStatusText;
	}
	
	/**
	 * @param executionStatus
	 * @return
	 */
	public static String getExecutionStatusTextColored(ExecutionStatus executionStatus) 
	{
		String executionStatusTextColored = 
			Messages.TestLinkBuilder_ExecutionStatus_Undefined();
		if ( executionStatus == ExecutionStatus.FAILED )
		{
			executionStatusTextColored = "<span style='color: red'>"+Messages.TestLinkBuilder_ExecutionStatus_Failed()+"</span>";
		}
		if ( executionStatus == ExecutionStatus.PASSED )
		{
			executionStatusTextColored = "<span style='color: green'>"+Messages.TestLinkBuilder_ExecutionStatus_Passed()+"</span>";
		}
		if ( executionStatus == ExecutionStatus.BLOCKED )
		{
			executionStatusTextColored = "<span style='color: yellow'>"+Messages.TestLinkBuilder_ExecutionStatus_Blocked()+"</span>";
		}
		if ( executionStatus == ExecutionStatus.NOT_RUN )
		{
			executionStatusTextColored = "<span style='color: gray'>"+Messages.TestLinkBuilder_ExecutionStatus_NotRun()+"</span>";
		}
		return executionStatusTextColored;
	}
	
}
