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

import java.io.Serializable;

/**
 * @author Bruno P. Kinoshita
 * @since 1.2
 */
public class ReportFilesPatterns 
implements Serializable
{

	private static final long serialVersionUID = -1546548797599894951L;

	/**
	 * JUnit XML report files pattern.
	 */
	private final String junitXmlReportFilesPattern;
	
	/**
	 * TestNG XML report files pattern.
	 */
	private final String testNGXmlReportFilesPattern;
	
	/**
	 * TAP Streams report files pattern.
	 */
	private final String tapStreamReportFilesPattern;
	
	public ReportFilesPatterns( 
			String junitXmlReportFilesPattern, 
			String testNGXmlReportFilesPattern, 
			String tapStreamReportFilesPattern ) 
	{
		super();
		this.junitXmlReportFilesPattern = junitXmlReportFilesPattern;
		this.testNGXmlReportFilesPattern = testNGXmlReportFilesPattern;
		this.tapStreamReportFilesPattern = tapStreamReportFilesPattern;
	}

	public String getJunitXmlReportFilesPattern()
	{
		return this.junitXmlReportFilesPattern;
	}

	public String getTestNGXmlReportFilesPattern()
	{
		return this.testNGXmlReportFilesPattern;
	}

	public String getTapStreamReportFilesPattern()
	{
		return this.tapStreamReportFilesPattern;
	}

}
