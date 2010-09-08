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
 * @since 04/09/2010
 */
package hudson.plugins.testlink.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity used by {@link TestLinkParser} to represent the root node of 
 * TestLink results file.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 04/09/2010
 */
public class TestLink 
implements Serializable 
{

	/**
	 * List of metrics present in the XML file.
	 */
	private List<TestLinkTestCase> testCases;
	
	private String buildName;
	private Integer buildId;

	public TestLink() {
		super();
		this.testCases = new ArrayList<TestLinkTestCase>();
		this.buildId = -1;
	}

	public List<TestLinkTestCase> getTestCases() {
		return testCases;
	}

	public void setTestCases(List<TestLinkTestCase> testCases) {
		this.testCases = testCases;
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

	@Override
	public String toString()
	{
		return "TestLink [buildId=" + buildId + ", buildName=" + buildName
				+ ", testCases=" + testCases + "]";
	}
	
}
