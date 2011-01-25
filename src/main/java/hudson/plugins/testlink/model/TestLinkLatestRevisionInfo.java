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
package hudson.plugins.testlink.model;

import java.io.Serializable;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Class to store information regarding SVN Revision.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.2
 */
public class TestLinkLatestRevisionInfo 
implements Serializable
{

	private static final long serialVersionUID = -8531299209124605920L;

	/**
	 * SVN repository URL
	 */
	private String svnUrl;
	
	/**
	 * SVN username
	 */
	private String svnUser; 
	
	/**
	 * SVN password
	 */
	private String svnPassword;
	
	@DataBoundConstructor
	public TestLinkLatestRevisionInfo(
			String svnUrl, 
			String svnUser,
			String svnPassword)
	{
		super();
		this.svnUrl = svnUrl;
		this.svnUser = svnUser;
		this.svnPassword = svnPassword;
	}
	
	public String getSvnUrl()
	{
		return svnUrl;
	}

	public String getSvnUser()
	{
		return svnUser;
	}

	public String getSvnPassword()
	{
		return svnPassword;
	}

	
}
