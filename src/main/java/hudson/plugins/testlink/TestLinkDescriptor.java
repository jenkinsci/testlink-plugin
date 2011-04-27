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

import hudson.CopyOnWrite;
import hudson.model.Descriptor;
import hudson.plugins.testlink.util.Messages;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TestLinkDescriptor 
extends Descriptor<Builder>
{

	private static final String DISPLAY_NAME = "Invoke TestLink";
	
	@CopyOnWrite
	private volatile TestLinkInstallation[] installations = 
		new TestLinkInstallation[0];
	
	public TestLinkDescriptor()
	{
		super(TestLinkBuilder.class);
		load();
	}

	@Override
	public String getDisplayName()
	{
		return DISPLAY_NAME;
	}
	
	/**
	 * @return List of TestLink installations
	 */
	public TestLinkInstallation[] getInstallations()
	{
		return this.installations;
	}
	
	public TestLinkInstallation getInstallationByTestLinkName(
		String testLinkName
	)
	{
		TestLinkInstallation installation = null;
		if ( this.installations != null && this.installations.length > 0 )
		{
			for(TestLinkInstallation tempInst : this.installations )
			{
				if ( tempInst.getName().equals(testLinkName))
				{
					return tempInst;
				}
			}
		}
		return installation;
	}
	
	@Override
	public boolean configure( StaplerRequest req, JSONObject json )
	throws hudson.model.Descriptor.FormException
	{
		this.installations = 
			req.bindParametersToList(
					TestLinkInstallation.class,
					"TestLink.").toArray(new TestLinkInstallation[0]);
		save();
		return true;
	}
	
	/* 
	 * --- Validation methods ---
	 */
	public FormValidation doCheckMandatory(@QueryParameter String value)
	{
		FormValidation returnValue = FormValidation.ok();
		if ( StringUtils.isBlank( value ) )
		{
			returnValue = FormValidation.error( Messages.TestLinkBuilder_MandatoryProperty() );
		}
		return returnValue;
	}

}
