/**
 * 
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
