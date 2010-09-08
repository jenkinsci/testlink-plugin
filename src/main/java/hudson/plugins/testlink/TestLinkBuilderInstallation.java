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
 * @since 31/08/2010
 */
package hudson.plugins.testlink;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Represents the TestLink installation in Hudson global configuration. 
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 31/08/2010
 */
public class TestLinkBuilderInstallation
{

	/**
	 * Name of the installation
	 */
	private String name;
	
	/**
	 * TestLink URL
	 */
	private String url;
	
	/**
	 * A valid user dev key
	 */
	private String devKey;
	
	/**
	 * Name of the custom field for the Test File
	 */
	private String testFileCustomField;
	
	/**
	 * Name of the custom field for Test Category
	 */
	private String testCategoryCustomField;
	
	/**
	 * Test Case category value
	 */
	private String testCaseCategory;
	
	/**
	 * Test Suite category value
	 */
	private String testSuiteCategory;
	
	@DataBoundConstructor
	public TestLinkBuilderInstallation(
		String name, 
		String url, 
		String devKey, 
		String testFileCustomField, 
		String testCategoryCustomField, 
		String testCaseCategory, 
		String testSuiteCategory
	)
	{
		this.name = name;
		this.url = url;
		this.devKey = devKey;
		this.testFileCustomField = testFileCustomField;
		this.testCategoryCustomField = testCategoryCustomField;
		this.testCaseCategory = testCaseCategory;
		this.testSuiteCategory = testSuiteCategory;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public String getUrl()
	{
		return this.url;
	}
	
	public String getDevKey()
	{
		return this.devKey;
	}

	public String getTestFileCustomField() 
	{
		return testFileCustomField;
	}

	public String getTestCategoryCustomField() 
	{
		return testCategoryCustomField;
	}

	public String getTestCaseCategory() 
	{
		return testCaseCategory;
	}

	public String getTestSuiteCategory() 
	{
		return testSuiteCategory;
	}
	
}
