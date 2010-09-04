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
	private String category;
	private String file;
	private String resultStatus;
	
	public TestLinkTestCase()
	{
		super();
	}

	public TestLinkTestCase(int id, int planId, int buildId, String category,
			String file, String resultStatus)
	{
		super();
		this.id = id;
		this.planId = planId;
		this.buildId = buildId;
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
	public String toString()
	{
		return "TestCase [buildId=" + buildId + ", category=" + category
				+ ", file=" + file + ", id=" + id + ", planId=" + planId
				+ ", resultStatus=" + resultStatus + "]";
	}
	
	public String toXml()
	{
		return 
			"<testcase>\n" +
			"\t<id>" + id + "</id>\n" +
			"\t<planId>" + planId + "</planId>\n" + 
			"\t<buildId>" + buildId + "</buildId>\n" + 
			"\t<category>"+ category +"</category>\n" +
			"\t<file>"+ file +"</file>\n" +
			"\t<resultStatus>"+ resultStatus +"</resultStatus>\n" +
			"</testcase>\n"
		;
	}
	
}
