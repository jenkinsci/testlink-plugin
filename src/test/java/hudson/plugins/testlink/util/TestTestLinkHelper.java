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

import java.lang.reflect.Constructor;
import java.util.Locale;

import junit.framework.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;

/**
 *
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 */
public class TestTestLinkHelper 
extends TestCase
{

	/**
	 * Defines the Locale to US
	 */
	public void setUp()
	{
		Locale.setDefault(new Locale("en", "US"));
		
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
	
	public void testExecutionStatusText()
	{
		ExecutionStatus status = ExecutionStatus.PASSED;
		String text = TestLinkHelper.getExecutionStatusText(status); 
		assertTrue( text.equals("Passed") );
			
		status = ExecutionStatus.FAILED;
		text = TestLinkHelper.getExecutionStatusText(status); 
		assertTrue( text.equals("Failed") );
		
		status = ExecutionStatus.NOT_RUN;
		text = TestLinkHelper.getExecutionStatusText(status); 
		assertTrue( text.equals("Not Run") );
		
		status = ExecutionStatus.BLOCKED;
		text = TestLinkHelper.getExecutionStatusText(status); 
		assertTrue( text.equals("Blocked") );
		
	}
	
	public void testColoredExecutionStatusText()
	{
		ExecutionStatus status = ExecutionStatus.PASSED;
		String text = TestLinkHelper.getExecutionStatusTextColored(status); 
		assertTrue( text.equals("<span style='color: green'>Passed</span>") );
			
		status = ExecutionStatus.FAILED;
		text = TestLinkHelper.getExecutionStatusTextColored(status); 
		assertTrue( text.equals("<span style='color: red'>Failed</span>") );
		
		status = ExecutionStatus.NOT_RUN;
		text = TestLinkHelper.getExecutionStatusTextColored(status); 
		assertTrue( text.equals("<span style='color: gray'>Not Run</span>") );
		
		status = ExecutionStatus.BLOCKED;
		text = TestLinkHelper.getExecutionStatusTextColored(status); 
		assertTrue( text.equals("<span style='color: yellow'>Blocked</span>") );
	}
	
}
