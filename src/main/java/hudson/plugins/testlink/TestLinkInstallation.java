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
package hudson.plugins.testlink;

import java.io.Serializable;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Represents the TestLink installation in Hudson global configuration. 
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TestLinkInstallation 
implements Serializable
{

	private static final long serialVersionUID = -6254365355132610350L;

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
	 * TestLink Java API properties
	 */
	private String testLinkJavaAPIProperties;
	
	@DataBoundConstructor
	public TestLinkInstallation(
		String name, 
		String url, 
		String devKey, 
		String testLinkJavaAPIProperties
	)
	{
		this.name = name;
		this.url = url;
		this.devKey = devKey;
		this.testLinkJavaAPIProperties = testLinkJavaAPIProperties;
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

	public String getTestLinkJavaAPIProperties()
	{
		return testLinkJavaAPIProperties;
	}
	
}
