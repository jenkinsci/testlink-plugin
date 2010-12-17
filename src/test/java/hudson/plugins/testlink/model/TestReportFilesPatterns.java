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
package hudson.plugins.testlink.model;

import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import hudson.plugins.testlink.model.ReportFilesPatterns;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.0
 */
public class TestReportFilesPatterns
{

	private ReportFilesPatterns patterns;
	
	@BeforeClass
	public void setUp()
	{
		this.patterns = new ReportFilesPatterns();
	}
	
	@Test(testName="Test Getters and Setters")
	public void testGettersAndSetters()
	{
		Assert.assertNull( patterns.getJunitXmlReportFilesPattern() );
		String junitXmlReportFilesPattern = "**/TEST-*.xml";
		patterns.setJunitXmlReportFilesPattern( junitXmlReportFilesPattern );
		Assert.assertNotNull( patterns.getJunitXmlReportFilesPattern() );
		Assert.assertEquals( patterns.getJunitXmlReportFilesPattern(), junitXmlReportFilesPattern );
		
		Assert.assertNull( patterns.getTestNGXmlReportFilesPattern() );
		String testNgXmlReportFilesPattern = "**/testng-results.xml";
		patterns.setTestNGXmlReportFilesPattern( testNgXmlReportFilesPattern );
		Assert.assertNotNull( patterns.getTestNGXmlReportFilesPattern() );
		Assert.assertEquals( patterns.getTestNGXmlReportFilesPattern(), testNgXmlReportFilesPattern );
		
		Assert.assertNull( patterns.getTapStreamReportFilesPattern() );
		String tapReportFilesPattern = "**/*.tap";
		patterns.setTapStreamReportFilesPattern( tapReportFilesPattern );
		Assert.assertNotNull( patterns.getTapStreamReportFilesPattern() );
		Assert.assertEquals( patterns.getTapStreamReportFilesPattern(), tapReportFilesPattern );		
	}
	
	@Test(testName="Test ReportFilesPatterns toString()")
	public void testToString()
	{
		Assert.assertTrue( patterns.toString().startsWith( "TestReportDirectories [junitXmlReportFilesPattern=" ));
	}
	
}
