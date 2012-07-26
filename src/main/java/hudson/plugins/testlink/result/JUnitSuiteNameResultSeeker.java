/*
 * The MIT License
 *
 * Copyright (c) <2012> <Bruno P. Kinoshita>
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
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.plugins.testlink.TestLinkSite;
import hudson.plugins.testlink.util.Messages;
import hudson.tasks.junit.JUnitParser;
import hudson.tasks.junit.SuiteResult;
import hudson.tasks.junit.TestResult;
import hudson.tasks.junit.CaseResult;

import java.io.IOException;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;

import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionStatus;

/**
 * <p>Seeks for test results matching each JUnit Suite Result name with the key 
 * custom field.</p>
 * 
 * <p>Skips JUnit Suite Results that were disabled.</p>
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 3.1
 */
public class JUnitSuiteNameResultSeeker extends AbstractJUnitResultSeeker {

	private static final long serialVersionUID = -969559401334833078L;

	/**
	 * @param includePattern Include pattern used when looking for results
	 * @param keyCustomField Key custom field to match against the results
	 */
	@DataBoundConstructor
	public JUnitSuiteNameResultSeeker(String includePattern, String keyCustomField, boolean attachJUnitXML, boolean includeNotes) {
		super(includePattern, keyCustomField, attachJUnitXML, includeNotes);
	}

	@Extension
	public static class DescriptorImpl extends ResultSeekerDescriptor {
		/*
		 * (non-Javadoc)
		 * 
		 * @see hudson.model.Descriptor#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return "JUnit suite name"; // TBD: i18n
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * hudson.plugins.testlink.result.ResultSeeker#seekAndUpdate(java.io.File,
	 * hudson.model.BuildListener, hudson.plugins.testlink.TestLinkSite,
	 * hudson.plugins.testlink.result.Report)
	 */
	@Override
	public void seek(TestCaseWrapper[] automatedTestCases,AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener, TestLinkSite testlink) throws ResultSeekerException {
		listener.getLogger().println( Messages.Results_JUnit_LookingForTestSuites() );
		try {
			final JUnitParser parser = new JUnitParser(false);
			final TestResult testResult = parser.parse(this.includePattern, build, launcher, listener);
			
			for(SuiteResult suiteResult : testResult.getSuites()) {
				for(TestCaseWrapper automatedTestCase : automatedTestCases) {
					final String[] commaSeparatedValues = automatedTestCase.getKeyCustomFieldValues(this.keyCustomField);
					for(String value : commaSeparatedValues) {
						if(suiteResult.getName().equals(value)) {
							ExecutionStatus status = this.getExecutionStatus(suiteResult);
							automatedTestCase.addCustomFieldAndStatus(value, status);
							
							if(this.isIncludeNotes()) {
								final String notes = this.getJUnitNotes(suiteResult);
								automatedTestCase.appendNotes(notes);
							}
							
							super.handleResult(automatedTestCase, build, listener, testlink, suiteResult);
						}
					}
				}
			}
		} catch (IOException e) {
			throw new ResultSeekerException(e);
		} catch (InterruptedException e) {
			throw new ResultSeekerException(e);
		}
	}
	
	private ExecutionStatus getExecutionStatus(SuiteResult suiteResult) {
		List<CaseResult> cases = suiteResult.getCases();
		for(CaseResult caseResult : cases) {
			if(!caseResult.isPassed() && !caseResult.isSkipped()) { // Any error, invalidates the suite result
				return ExecutionStatus.FAILED;
			}
		}
		return ExecutionStatus.PASSED;
	}
	
	private String getJUnitNotes( SuiteResult testSuite )
	{
		final StringBuilder notes = new StringBuilder();
		notes.append(
				Messages.Results_JUnit_NotesForTestSuite(
						testSuite.getName(), 
						testSuite.getStderr(), 
						testSuite.getStdout(), 
						Integer.toString(testSuite.getCases().size()), 
						Double.toString(testSuite.getDuration()), 
						testSuite.getTimestamp()
		));
		
		return notes.toString();
	}

}
