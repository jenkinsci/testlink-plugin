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

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.0
 */
public class TestTestLinkPluginException
{

	private TestLinkPluginException exception;
	
	@Test(testName="Test TestLink Plug-in Exception")
	public void testTestLinkPluginException()
	{
		this.exception = new TestLinkPluginException();
		
		Assert.assertNotNull( this.exception );
		
		this.exception = new TestLinkPluginException("Lamen");
		
		Assert.assertNotNull( this.exception );
		Assert.assertEquals( this.exception.getMessage(), "Lamen" );
		
		this.exception = new TestLinkPluginException( new NullPointerException() );
		
		Assert.assertNotNull( this.exception );
		Assert.assertNotNull( this.exception.getCause() );
		
		this.exception = new TestLinkPluginException("Lamen", new NullPointerException() );
		
		Assert.assertNotNull( this.exception );
		Assert.assertNotNull( this.exception.getCause() );
		Assert.assertEquals( this.exception.getMessage(), "Lamen" );
	}
	
}
