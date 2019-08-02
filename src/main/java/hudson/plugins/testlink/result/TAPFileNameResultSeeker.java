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
package hudson.plugins.testlink.result;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.testlink.TestLinkSite;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * <p>Seeks for test results matching each TAP file name with the key 
 * custom field.</p>
 * 
 * <p>Skips TAP Streams that were skipped.</p>
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 3.1
 */
public class TAPFileNameResultSeeker extends AbstractTAPFileNameResultSeeker {
	
	private static final long serialVersionUID = 3068999690225092293L;
	
	@DataBoundConstructor
	public TAPFileNameResultSeeker(String includePattern,
			String keyCustomField, boolean attachTAPStream,
			boolean attachYAMLishAttachments, boolean includeNotes,
			Boolean compareFullPath) {
		super(includePattern, keyCustomField, attachTAPStream,
				attachYAMLishAttachments, includeNotes, compareFullPath);
	}

	@Extension
	public static class DescriptorImpl extends ResultSeekerDescriptor {

		@Override
		public String getDisplayName() {
			return "TAP file name"; // TBD: i18n
		}
	}
	
	@Override
	public void seek(final TestCaseWrapper[] automatedTestCases, Run<?, ?> build, FilePath workspace, Launcher launcher, final TaskListener listener, TestLinkSite testlink) throws ResultSeekerException {
		super.seek(automatedTestCases, build, workspace, launcher, listener, testlink);
	}
}