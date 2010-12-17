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

import java.io.Serializable;

/**
 * @author Bruno P. Kinoshita
 * @since 1.2
 */
public class ReportFilesPatterns 
implements Serializable
{

	/**
	 * JUnit XML report files pattern.
	 */
	private String junitXmlReportFilesPattern;
	
	/**
	 * TestNG XML report files pattern.
	 */
	private String testNGXmlReportFilesPattern;
	
	/**
	 * TAP Streams report files pattern.
	 */
	private String tapStreamReportFilesPattern;
	
	public ReportFilesPatterns() 
	{
		super();
	}

	public String getJunitXmlReportFilesPattern()
	{
		return junitXmlReportFilesPattern;
	}

	public void setJunitXmlReportFilesPattern( String junitXmlReportFilesPattern )
	{
		this.junitXmlReportFilesPattern = junitXmlReportFilesPattern;
	}

	public String getTestNGXmlReportFilesPattern()
	{
		return testNGXmlReportFilesPattern;
	}

	public void setTestNGXmlReportFilesPattern( String testNGXmlReportFilesPattern )
	{
		this.testNGXmlReportFilesPattern = testNGXmlReportFilesPattern;
	}

	public String getTapStreamReportFilesPattern()
	{
		return tapStreamReportFilesPattern;
	}

	public void setTapStreamReportFilesPattern( String tapStreamReportFilesPattern )
	{
		this.tapStreamReportFilesPattern = tapStreamReportFilesPattern;
	}

	@Override
	public String toString()
	{
		return "TestReportDirectories [junitXmlReportFilesPattern="
				+ junitXmlReportFilesPattern + ", testNGXmlReportFilesPattern="
				+ testNGXmlReportFilesPattern
				+ ", tapStreamReportFilesPattern="
				+ tapStreamReportFilesPattern + "]";
	}
	
}
