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
 * @since 30/11/2010
 */
package hudson.plugins.testlink.parser;

import hudson.plugins.testlink.model.TestLinkReport;
import hudson.plugins.testlink.model.TestResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 30/11/2010
 */
public class TestNGXmlHandler 
extends DefaultHandler 
implements Serializable
{

	private TestResult tempTestResult;
	private TestLinkReport report;
	private String keyCustomFieldName;
	private TestNGTestMethod tempTestMethod;
	private List<TestNGTestMethod> tempTestMethods;

	private static final String TEST = "test";
	private static final String CLASS = "class";
	private static final String NAME_ATTRIBUTE = "name";
	private static final String DURATION_MS_ATTRIBUTE = "duration-ms";
	private static final String STARTED_AT_ATTRIBUTE = "started-at";
	private static final String FINISHED_AT_ATTRIBUTE = "finished-at";
	private static final String METHOD = "test-method";
	private static final String IS_CONFIG_ATTRIBUTE = "is-config";
	private static final String SIGNATURE_ATTRIBUTE = "signature";
	private static final String STATUS_ATTRIBUTES = "status";
	private static final String PASS_STATUS = "PASS";
	
	private String testName = null;
	private String durationMs = null;
	private String startedAt = null;
	private String finishedAt = null;
	
	private String className = null;
	
	public TestNGXmlHandler(TestLinkReport report, String keyCustomFieldName )
	{
		this.report = report;
		this.keyCustomFieldName = keyCustomFieldName;
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(
			String uri, 
			String localName, 
			String qName,
			Attributes attributes) 
	throws SAXException 
	{
		if ( TEST.equals(qName) )
		{
			testName = attributes.getValue( NAME_ATTRIBUTE );
			durationMs = attributes.getValue( DURATION_MS_ATTRIBUTE );
			startedAt = attributes.getValue( STARTED_AT_ATTRIBUTE );
			finishedAt = attributes.getValue( FINISHED_AT_ATTRIBUTE );
		} 
		else if ( CLASS.equals( qName ) )
		{
			tempTestMethods = new ArrayList<TestNGTestMethod>();
			className = attributes.getValue( NAME_ATTRIBUTE );
		}
		else if ( METHOD.equals( qName ) ) 
		{
			this.tempTestMethod = new TestNGTestMethod();
			String durationValue = attributes.getValue( DURATION_MS_ATTRIBUTE );
			this.tempTestMethod.setDuration( Long.parseLong( durationValue ) );
			this.tempTestMethod.setFinishedAt( attributes.getValue( FINISHED_AT_ATTRIBUTE ));
			String isConfigValue = attributes.getValue( IS_CONFIG_ATTRIBUTE );
			this.tempTestMethod.setIsConfig( Boolean.valueOf( isConfigValue ) );
			this.tempTestMethod.setName( attributes.getValue( NAME_ATTRIBUTE ));
			this.tempTestMethod.setSignature( attributes.getValue( SIGNATURE_ATTRIBUTE ));
			this.tempTestMethod.setStartedAt( attributes.getValue( STARTED_AT_ATTRIBUTE ));
			this.tempTestMethod.setStatus( attributes.getValue( STATUS_ATTRIBUTES ));
			this.tempTestMethods.add( this.tempTestMethod );
		}
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement( String uri, 
			String localName, 
			String qName )
			throws SAXException
	{
		if ( METHOD.equals( qName ) ) 
		{
			TestCase found = null;
			for( TestCase testCase : this.report.getTestCases() )
			{
				for( CustomField customField : testCase.getCustomFields() )
				{
					if ( customField.getName().equalsIgnoreCase(this.keyCustomFieldName))
					{
						if  (className.equals(customField.getValue()))
						{
							found = testCase;
							break;
						}
					}
				}
			}
			if ( found != null )
			{
				Integer failures = 0;
				for( TestNGTestMethod method : tempTestMethods )
				{
					if ( ! method.getStatus().equals( PASS_STATUS ) )
					{
						failures = failures + 1;
						break;
					}
				}
				
				if ( failures > 0 )
				{
					found.setExecutionStatus(ExecutionStatus.FAILED);
				}
				else 
				{
					found.setExecutionStatus( ExecutionStatus.PASSED );
				}
				
				String notes = "TestNG Test name: " + testName + 
						", Number of methods: " + this.tempTestMethods.size() + 
						", Duration in ms: " + durationMs + 
						", Started at: " + startedAt + 
						", Finished at: " + finishedAt;
				
				//this.report.getTestCases().remove( found );
				this.tempTestResult = new TestResult(found, report.getBuild(), report.getTestPlan());
				this.tempTestResult.setNotes( notes );
			}
		}
	}
	
	public TestResult getTestResult()
	{
		return this.tempTestResult;
	}
	
}
