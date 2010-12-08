/**
 * 
 */
package hudson.plugins.testlink.util;

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
		if ( executionStatus == ExecutionStatus.FAILED )
		{
			return "Failed";
		}
		if ( executionStatus == ExecutionStatus.PASSED )
		{
			return "Passed";
		}
		if ( executionStatus == ExecutionStatus.BLOCKED )
		{
			return "Blocked";
		}
		if ( executionStatus == ExecutionStatus.NOT_RUN )
		{
			return "Not Run";
		}
		return "Undefined";
	}
	
	/**
	 * @param executionStatus
	 * @return
	 */
	public static String getExecutionStatusTextColored(ExecutionStatus executionStatus) 
	{
		if ( executionStatus == ExecutionStatus.FAILED )
		{
			return "<span style='color: red'>Failed</span>";
		}
		if ( executionStatus == ExecutionStatus.PASSED )
		{
			return "<span style='color: green'>Passed</span>";
		}
		if ( executionStatus == ExecutionStatus.BLOCKED )
		{
			return "<span style='color: yellow'>Blocked</span>";
		}
		if ( executionStatus == ExecutionStatus.NOT_RUN )
		{
			return "<span style='color: gray'>Not Run</span>";
		}
		return "Undefined";
	}
	
}
