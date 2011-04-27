/*
 * The MIT License
 *
 * Copyright (c) <2011> <Bruno P. Kinoshita>
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

import hudson.model.Action;
import hudson.plugins.testlink.util.Messages;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
public class AbstractTestLinkProjectAction 
implements Action
{

	public static final String ICON_FILE_NAME = "/plugin/testlink/icons/testlink-24.png";
	public static final String URL_NAME = "testLinkResult";
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see hudson.model.Action#getDisplayName()
	 */
	public String getDisplayName()
	{
		return Messages.TestLinkProjectAction_DisplayName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hudson.model.Action#getIconFileName()
	 */
	public String getIconFileName()
	{
		return ICON_FILE_NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hudson.model.Action#getUrlName()
	 */
	public String getUrlName()
	{
		return URL_NAME;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see hudson.search.SearchItem#getSearchUrl()
	 */
	public String getSearchUrl()
	{
		return URL_NAME;
	}

}
