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
package hudson.plugins.testlink.util;

import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;

/**
 * @author Bruno P. Kinoshita
 * @since 2.0
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
