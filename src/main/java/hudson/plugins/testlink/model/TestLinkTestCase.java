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
 * @since 02/09/2010
 */
package hudson.plugins.testlink.model;

import java.io.Serializable;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 02/09/2010
 */
public class TestLinkTestCase 
implements Serializable
{
	
	private int id;
	private int planId;
	private int buildId;
	private int projectId;
	private String category;
	private String file;
	private String resultStatus;
	
	public TestLinkTestCase()
	{
		super();
	}

	public TestLinkTestCase(int id, int planId, int buildId, int projectId, String category,
			String file, String resultStatus)
	{
		super();
		this.id = id;
		this.planId = planId;
		this.buildId = buildId;
		this.projectId = projectId;
		this.category = category;
		this.file = file;
		this.resultStatus = resultStatus;
	}

	public int getId()
	{
		return id;
	}

	public void setId( int id )
	{
		this.id = id;
	}

	public int getPlanId()
	{
		return planId;
	}

	public void setPlanId( int planId )
	{
		this.planId = planId;
	}

	public int getBuildId()
	{
		return buildId;
	}

	public void setBuildId( int buildId )
	{
		this.buildId = buildId;
	}
	
	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public String getCategory()
	{
		return category;
	}

	public void setCategory( String category )
	{
		this.category = category;
	}

	public String getFile()
	{
		return file;
	}

	public void setFile( String file )
	{
		this.file = file;
	}

	public String getResultStatus()
	{
		return resultStatus;
	}

	public void setResultStatus( String resultStatus )
	{
		this.resultStatus = resultStatus;
	}
	
	@Override
	public String toString() {
		return "TestLinkTestCase [buildId=" + buildId + ", category="
				+ category + ", file=" + file + ", id=" + id + ", planId="
				+ planId + ", projectId=" + projectId + ", resultStatus="
				+ resultStatus + "]";
	}

	public String toXml()
	{
		return 
			"\t<testcase>\n" +
			"\t\t<id>" + id + "</id>\n" +
			"\t\t<planId>" + planId + "</planId>\n" + 
			"\t\t<buildId>" + buildId + "</buildId>\n" + 
			"\t\t<projectId>" + projectId + "</projectId>\n" + 
			"\t\t<category>"+ category +"</category>\n" +
			"\t\t<file>"+ file +"</file>\n" +
			"\t\t<resultStatus>"+ resultStatus +"</resultStatus>\n" +
			"\t</testcase>\n"
		;
	}
	
}
