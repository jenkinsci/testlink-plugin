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
 * @since 19/11/2010
 */
package hudson.plugins.testlink.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.eti.kinoshita.testlinkjavaapi.model.Attachment;
import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 19/11/2010
 */
public class TestResult 
implements Serializable
{

	private TestCase testCase;
	private Build build;
	private TestPlan testPlan;
	private List<Attachment> attachments;
	private String notes;
	
	public TestResult(
		TestCase testCase, 
		Build build, 
		TestPlan testPlan
	)
	{
		this.testCase = testCase;
		this.build = build;
		this.testPlan = testPlan;
		
		this.attachments = new ArrayList<Attachment>();
	}
	
	public void addAttachment(Attachment attachment)
	{
		this.attachments.add( attachment );
	}

	public TestCase getTestCase()
	{
		return testCase;
	}

	public void setTestCase( TestCase testCase )
	{
		this.testCase = testCase;
	}

	public Build getBuild()
	{
		return build;
	}

	public void setBuild( Build build )
	{
		this.build = build;
	}

	public TestPlan getTestPlan()
	{
		return testPlan;
	}

	public void setTestPlan( TestPlan testPlan )
	{
		this.testPlan = testPlan;
	}

	public List<Attachment> getAttachments()
	{
		return attachments;
	}

	public String getNotes()
	{
		return notes;
	}

	public void setNotes( String notes )
	{
		this.notes = notes;
	}

	@Override
	public String toString()
	{
		return "TestResult [testCase=" + testCase + ", build=" + build
				+ ", testPlan=" + testPlan + ", attachments=" + attachments
				+ ", notes=" + notes + "]";
	}
	
}
