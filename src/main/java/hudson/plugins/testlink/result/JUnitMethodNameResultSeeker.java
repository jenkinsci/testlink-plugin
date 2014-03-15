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

import org.kohsuke.stapler.DataBoundConstructor;

import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionStatus;

/**
 * <p>Seeks for test results matching each JUnit Case Result class name and method name with 
 * the key custom field.</p>
 * 
 * <p>Skips JUnit Case Results that were disabled.</p>
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @author Oliver Merkel - Merkel.Oliver at web.de
 * @author Michael van der Westhuizen - r1mikey at gmail.com
 * @since 3.3
 */
public class JUnitMethodNameResultSeeker extends AbstractJUnitResultSeeker {

	private static final long serialVersionUID = -9198823056268332288L;

	/**
	 * @param includePattern Include pattern used when looking for results
	 * @param keyCustomField Key custom field to match against the results
	 * @param keywordsExecutedFilter keywords of testcases to filter test results
	 * @param attachJUnitXML Bit that enables attaching result file to TestLink
	 * @param includeNotes
	 */
	@DataBoundConstructor
	public JUnitMethodNameResultSeeker(String includePattern, String keyCustomField, String keywordsExecutedFilter, boolean attachJUnitXML, boolean includeNotes) {
		super(includePattern, keyCustomField, keywordsExecutedFilter, attachJUnitXML, includeNotes);
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
			return "JUnit method name"; // TBD: i18n
		}
	}

	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.result.ResultSeeker#seekAndUpdate(hudson.plugins.testlink.result.TestCaseWrapper<?>[], hudson.model.AbstractBuild, hudson.Launcher, hudson.model.BuildListener, hudson.plugins.testlink.TestLinkSite, hudson.plugins.testlink.result.Report)
	 */
	@Override
	public void seek(TestCaseWrapper[] automatedTestCases, AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener, TestLinkSite testlink) throws ResultSeekerException {
		listener.getLogger().println( Messages.Results_JUnit_LookingForTestMethods() ); // i18n
		try {
			final JUnitParser parser = new JUnitParser(false);
			final TestResult testResult = parser.parse(this.includePattern, build, launcher, listener);
			
			for(final SuiteResult suiteResult : testResult.getSuites()) {
				for(CaseResult caseResult : suiteResult.getCases()) {
					final String methodName = caseResult.getClassName() + "#" + caseResult.getName();
					for(TestCaseWrapper automatedTestCase : automatedTestCases) {
						if (isInKeywordsFilter(automatedTestCase)) {
							haldleTestCase(build, listener, testlink, suiteResult, caseResult, methodName,
									automatedTestCase);
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

	/**
	 * @param build
	 * @param listener
	 * @param testlink
	 * @param suiteResult
	 * @param caseResult
	 * @param methodName
	 * @param automatedTestCase
	 */
	private void haldleTestCase(AbstractBuild<?, ?> build, BuildListener listener, TestLinkSite testlink,
			final SuiteResult suiteResult, CaseResult caseResult, final String methodName,
			TestCaseWrapper automatedTestCase) {
		final String[] commaSeparatedValues = automatedTestCase.getKeyCustomFieldValues(this.keyCustomField);
		for(String value : commaSeparatedValues) {
			if(! caseResult.isSkipped() && methodName.equals(value)) {
				final ExecutionStatus status = this.getExecutionStatus(caseResult);
				automatedTestCase.addCustomFieldAndStatus(value, status);
				
				if(this.isIncludeNotes()) {
					final String notes = this.getJUnitNotes(caseResult);
					automatedTestCase.appendNotes(notes);
				}
				
				super.handleResult(automatedTestCase, build, listener, testlink, suiteResult);
			}
		}
	}

	/**
	 * @param caseResult the case result
	 * @return NOT_RUN in case it is skipped, PASSED if it passed, and FAILED otherwise
	 */
	private ExecutionStatus getExecutionStatus(CaseResult caseResult) {
		if(caseResult.isSkipped()) {
			return ExecutionStatus.NOT_RUN;
		} else if(caseResult.isPassed()) {
			return ExecutionStatus.PASSED;
		} else {
			return ExecutionStatus.FAILED;
		}
	}
	
	/**
	 * Retrieves the Notes about the JUnit test.
	 * 
	 * @param testCase JUnit test.
	 * @return Notes about the JUnit test.
	 */
	private String getJUnitNotes( CaseResult testCase )
	{
		StringBuilder notes = new StringBuilder();
		notes.append( 
				Messages.Results_JUnit_NotesForTestMethod(
						testCase.getName(), 
						testCase.getClassName(), 
						testCase.getSkipCount(), 
						testCase.getFailCount(), 
						(testCase.getSuiteResult() != null ? testCase.getSuiteResult().getTimestamp() : null))
		);
		
		return notes.toString();
	}

}
