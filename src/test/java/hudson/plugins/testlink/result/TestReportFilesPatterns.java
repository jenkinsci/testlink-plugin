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
package hudson.plugins.testlink.result;

import junit.framework.TestCase;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.0
 */
public class TestReportFilesPatterns 
extends TestCase
{

	private ReportFilesPatterns patterns;
	
	public void setUp()
	{
		this.patterns = new ReportFilesPatterns();
	}
	
	public void testGettersAndSetters()
	{
		assertNull( patterns.getJunitXmlReportFilesPattern() );
		String junitXmlReportFilesPattern = "**/TEST-*.xml";
		patterns.setJunitXmlReportFilesPattern( junitXmlReportFilesPattern );
		assertNotNull( patterns.getJunitXmlReportFilesPattern() );
		assertEquals( patterns.getJunitXmlReportFilesPattern(), junitXmlReportFilesPattern );
		
		assertNull( patterns.getTestNGXmlReportFilesPattern() );
		String testNgXmlReportFilesPattern = "**/testng-results.xml";
		patterns.setTestNGXmlReportFilesPattern( testNgXmlReportFilesPattern );
		assertNotNull( patterns.getTestNGXmlReportFilesPattern() );
		assertEquals( patterns.getTestNGXmlReportFilesPattern(), testNgXmlReportFilesPattern );
		
		assertNull( patterns.getTapStreamReportFilesPattern() );
		String tapReportFilesPattern = "**/*.tap";
		patterns.setTapStreamReportFilesPattern( tapReportFilesPattern );
		assertNotNull( patterns.getTapStreamReportFilesPattern() );
		assertEquals( patterns.getTapStreamReportFilesPattern(), tapReportFilesPattern );		
	}
	
	public void testToString()
	{
		assertTrue( patterns.toString().startsWith( "TestReportDirectories [junitXmlReportFilesPattern=" ));
	}
	
}
