/**
 *	 __                                        
 *	/\ \      __                               
 *	\ \ \/'\ /\_\    ___     ___   __  __  __  
 *	 \ \ , < \/\ \ /' _ `\  / __`\/\ \/\ \/\ \ 
 *	  \ \ \\`\\ \ \/\ \/\ \/\ \L\ \ \ \_/ \_/ \
 *	   \ \_\ \_\ \_\ \_\ \_\ \____/\ \___x___/'
 *	    \/_/\/_/\/_/\/_/\/_/\/___/  \/__//__/  
 *                                          
 * Copyright (c) 1999-present Kinow
 * Casa Verde - São Paulo - SP. Brazil.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Kinow ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Kinow.                                      
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 03/09/2010
 */
package hudson.plugins.testlink.model;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 03/09/2010
 */
public class TestLinkExecutionDetails
{

	private String projectName;
	private Integer projectId;
	private String testPlanName;
	private Integer testPlanId;
	private String buildName;
	private Integer buildId;
	private Integer latestRevision;
	
	/**
	 * 
	 */
	public TestLinkExecutionDetails()
	{
	}

	public String getProjectName()
	{
		return projectName;
	}

	public void setProjectName( String projectName )
	{
		this.projectName = projectName;
	}

	public Integer getProjectId()
	{
		return projectId;
	}

	public void setProjectId( Integer projectId )
	{
		this.projectId = projectId;
	}

	public String getTestPlanName()
	{
		return testPlanName;
	}

	public void setTestPlanName( String testPlanName )
	{
		this.testPlanName = testPlanName;
	}

	public Integer getTestPlanId()
	{
		return testPlanId;
	}

	public void setTestPlanId( Integer testPlanId )
	{
		this.testPlanId = testPlanId;
	}

	public String getBuildName()
	{
		return buildName;
	}

	public void setBuildName( String buildName )
	{
		this.buildName = buildName;
	}

	public Integer getBuildId()
	{
		return buildId;
	}

	public void setBuildId( Integer buildId )
	{
		this.buildId = buildId;
	}

	public Integer getLatestRevision()
	{
		return latestRevision;
	}

	public void setLatestRevision( Integer latestRevision )
	{
		this.latestRevision = latestRevision;
	}

	@Override
	public String toString()
	{
		return "TestLinkExecutionDetails [buildId=" + buildId + ", buildName="
				+ buildName + ", latestRevision=" + latestRevision
				+ ", projectId=" + projectId + ", projectName=" + projectName
				+ ", testPlanId=" + testPlanId + ", testPlanName="
				+ testPlanName + "]";
	}
	
}
