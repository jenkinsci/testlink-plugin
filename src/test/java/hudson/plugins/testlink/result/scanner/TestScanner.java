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
package hudson.plugins.testlink.result.scanner;

import hudson.model.BuildListener;
import hudson.model.StreamBuildListener;
import hudson.plugins.testlink.result.parser.junit.TestJUnitParser;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.codehaus.plexus.util.StringOutputStream;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests the file scanner.
 *
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
public class TestScanner
{
	
	@Test
	public void testScanner()
	{
		Scanner scanner = new Scanner();
		
		ClassLoader cl = TestJUnitParser.class.getClassLoader();
		URL url = cl.getResource("hudson/plugins/testlink/result/scanner");
		File directory = new File( url.getFile() );
		
		StringOutputStream sos = new StringOutputStream();
		BuildListener listener = new StreamBuildListener(sos);
		
		try
		{
			String[] files = scanner.scan(directory, "*.class", listener);
			Assert.assertTrue( files.length == 1 );
			
			Assert.assertTrue( files[0].contains("TestScanner.class") );
		} catch (IOException e)
		{
			Assert.fail( "Failed to scan directory: " + directory, e );
		}
		
	}
	
	@Test
	public void testScannerWithoutIncludePattern()
	{
		Scanner scanner = new Scanner();
		
		ClassLoader cl = TestJUnitParser.class.getClassLoader();
		URL url = cl.getResource("hudson/plugins/testlink/result/scanner");
		File directory = new File( url.getFile() );
		
		StringOutputStream sos = new StringOutputStream();
		BuildListener listener = new StreamBuildListener(sos);
		
		try
		{
			String[] files = scanner.scan(directory, null, listener);
			Assert.assertTrue( files.length == 0 );
		} catch (IOException e)
		{
			Assert.fail( "Failed to scan directory: " + directory, e );
		}
		
	}
	
	@Test(expectedExceptions=IOException.class)
	public void testScannerUsingAFileInsteadOfDirectory() 
	throws IOException
	{
		Scanner scanner = new Scanner();
		
		ClassLoader cl = TestJUnitParser.class.getClassLoader();
		URL url = cl.getResource("hudson/plugins/testlink/result/scanner/TestScanner.class");
		File directory = new File( url.getFile() );
		
		StringOutputStream sos = new StringOutputStream();
		BuildListener listener = new StreamBuildListener(sos);
		
		scanner.scan(directory, "*.class", listener);
		
	}

}
