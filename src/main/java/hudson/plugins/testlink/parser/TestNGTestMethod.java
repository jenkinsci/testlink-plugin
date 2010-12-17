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
package hudson.plugins.testlink.parser;

import java.io.Serializable;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.0
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
