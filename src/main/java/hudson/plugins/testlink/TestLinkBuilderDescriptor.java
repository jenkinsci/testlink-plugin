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
import hudson.init.InitMilestone;
import hudson.init.Initializer;
import hudson.model.Items;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.FreeStyleProject;
import hudson.plugins.testlink.result.ResultSeeker;
import hudson.plugins.testlink.util.Messages;
import hudson.tasks.BuildStep;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import com.tupilabs.testng.parser.Suite;
import com.tupilabs.testng.parser.TestNGParser;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TestLinkBuilderDescriptor extends BuildStepDescriptor<Builder> {

	// exposed for Jelly
	public final Class<TestLinkBuilder> testLinkBuildType = TestLinkBuilder.class;

	private static final String DISPLAY_NAME = "Invoke TestLink";

	@CopyOnWrite
	private volatile TestLinkInstallation[] installations = new TestLinkInstallation[0];

	public TestLinkBuilderDescriptor() {
		super(TestLinkBuilder.class);
		load();
	}

	@Override
	public String getDisplayName() {
		return DISPLAY_NAME;
	}

	/**
	 * @return List of TestLink installations
	 */
	public TestLinkInstallation[] getInstallations() {
		return this.installations;
	}

	public TestLinkInstallation getInstallationByTestLinkName(
			String testLinkName) {
		TestLinkInstallation installation = null;
		if (this.installations != null && this.installations.length > 0) {
			for (TestLinkInstallation tempInst : this.installations) {
				if (tempInst.getName().equals(testLinkName)) {
					return tempInst;
				}
			}
		}
		return installation;
	}

	@Override
	public boolean configure(StaplerRequest req, JSONObject json)
			throws hudson.model.Descriptor.FormException {
		this.installations = req.bindParametersToList(
				TestLinkInstallation.class, "TestLink.").toArray(
				new TestLinkInstallation[0]);
		save();
		return true;
	}

	// exposed for Jelly
	public List<Descriptor<? extends BuildStep>> getApplicableBuildSteps(
			AbstractProject<?, ?> p) {
		return getBuildSteps();
	}

	public List<Descriptor<? extends ResultSeeker>> getApplicableResultSeekers(
			AbstractProject<?, ?> p) {
		List<Descriptor<? extends ResultSeeker>> list = new LinkedList<Descriptor<? extends ResultSeeker>>();
		for (Descriptor<? extends ResultSeeker> rs : ResultSeeker.all()) {
			list.add(rs);
		}
		return list;
	}

	public static List<Descriptor<? extends BuildStep>> getBuildSteps() {
		List<Descriptor<? extends BuildStep>> list = new ArrayList<Descriptor<? extends BuildStep>>();
		addTo(Builder.all(), list);
		addTo(Publisher.all(), list);
		return list;
	}

	private static void addTo(
			List<? extends Descriptor<? extends BuildStep>> source,
			List<Descriptor<? extends BuildStep>> list) {
		for (Descriptor<? extends BuildStep> d : source) {
			if (d instanceof BuildStepDescriptor) {
				BuildStepDescriptor<?> bsd = (BuildStepDescriptor<?>) d;
				if (bsd.isApplicable(FreeStyleProject.class)) {
					list.add(d);
				}
			}
		}
	}

	/*
	 * --- Validation methods ---
	 */
	public FormValidation doCheckMandatory(@QueryParameter String value) {
		FormValidation returnValue = FormValidation.ok();
		if (StringUtils.isBlank(value)) {
			returnValue = FormValidation.error(Messages
					.TestLinkBuilder_MandatoryProperty());
		}
		return returnValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hudson.tasks.BuildStepDescriptor#isApplicable(java.lang.Class)
	 */
	@Override
	public boolean isApplicable(Class<? extends AbstractProject> jobType) {
		return Boolean.TRUE;
	}

	@Initializer(before=InitMilestone.PLUGINS_STARTED)
	public static void addAliases() {
	    Items.XSTREAM2.addCompatibilityAlias("hudson.plugins.testlink.testng.Suite", Suite.class);
	    Items.XSTREAM2.addCompatibilityAlias("hudson.plugins.testlink.testng.TestNGParser", TestNGParser.class);
	    Items.XSTREAM2.addCompatibilityAlias("hudson.plugins.testlink.testng.Test", com.tupilabs.testng.parser.Test.class);
	    Items.XSTREAM2.addCompatibilityAlias("hudson.plugins.testlink.testng.TestMethod", com.tupilabs.testng.parser.TestMethod.class);
	    Items.XSTREAM2.addCompatibilityAlias("hudson.plugins.testlink.testng.Class", com.tupilabs.testng.parser.Class.class);
	    Items.XSTREAM2.addCompatibilityAlias("hudson.plugins.testlink.testng.ParserException", com.tupilabs.testng.parser.ParserException.class);
	    Items.XSTREAM2.alias("hudson.plugins.testlink.testng.Class.list", java.util.LinkedList.class, java.util.LinkedHashSet.class);
	}

}
