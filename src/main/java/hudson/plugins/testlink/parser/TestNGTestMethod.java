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
 * @since 01/12/2010
 */
package hudson.plugins.testlink.parser;

import java.io.Serializable;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 01/12/2010
 */
public class TestNGTestMethod 
implements Serializable
{

	private String status;
	private String signature;
	private String name;
	private Boolean isConfig;
	private Long duration;
	private String startedAt;
	private String finishedAt;
	public TestNGTestMethod()
	{
		super();
	}
	public TestNGTestMethod(String status, String signature, String name,
			Boolean isConfig, Long duration, String startedAt, String finishedAt)
	{
		super();
		this.status = status;
		this.signature = signature;
		this.name = name;
		this.isConfig = isConfig;
		this.duration = duration;
		this.startedAt = startedAt;
		this.finishedAt = finishedAt;
	}
	public String getStatus()
	{
		return status;
	}
	public void setStatus( String status )
	{
		this.status = status;
	}
	public String getSignature()
	{
		return signature;
	}
	public void setSignature( String signature )
	{
		this.signature = signature;
	}
	public String getName()
	{
		return name;
	}
	public void setName( String name )
	{
		this.name = name;
	}
	public Boolean getIsConfig()
	{
		return isConfig;
	}
	public void setIsConfig( Boolean isConfig )
	{
		this.isConfig = isConfig;
	}
	public Long getDuration()
	{
		return duration;
	}
	public void setDuration( Long duration )
	{
		this.duration = duration;
	}
	public String getStartedAt()
	{
		return startedAt;
	}
	public void setStartedAt( String startedAt )
	{
		this.startedAt = startedAt;
	}
	public String getFinishedAt()
	{
		return finishedAt;
	}
	public void setFinishedAt( String finishedAt )
	{
		this.finishedAt = finishedAt;
	}
	@Override
	public String toString()
	{
		return "TestNGMethod [status=" + status + ", signature=" + signature
				+ ", name=" + name + ", isConfig=" + isConfig + ", duration="
				+ duration + ", startedAt=" + startedAt + ", finishedAt="
				+ finishedAt + "]";
	}
	
}
