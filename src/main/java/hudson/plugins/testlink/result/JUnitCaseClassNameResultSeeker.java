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

import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;

/**
 * <p>Seeks for test results matching each JUnit Case Result class name with 
 * the key custom field.</p>
 * 
 * <p>Skips JUnit Case Results that were disabled.</p>
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 3.1
 */
public class JUnitCaseClassNameResultSeeker extends ResultSeeker {

	private static final long serialVersionUID = 9109479462341395475L;
	
	/**
	 * JUnit parser.
	 */
	private final JUnitParser parser = new JUnitParser(false);
	
	/**
	 * @param includePattern Include pattern used when looking for results
	 * @param keyCustomField Key custom field to match against the results
	 */
	@DataBoundConstructor
	public JUnitCaseClassNameResultSeeker(String includePattern, String keyCustomField) {
		super(includePattern, keyCustomField);
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
			return "JUnit case class name result seeker"; // TBD: i18n
		}
	}

	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.result.ResultSeeker#seekAndUpdate(hudson.plugins.testlink.result.TestCaseWrapper<?>[], hudson.model.AbstractBuild, hudson.Launcher, hudson.model.BuildListener, hudson.plugins.testlink.TestLinkSite, hudson.plugins.testlink.result.Report)
	 */
	@Override
	public void seek(TestCaseWrapper[] automatedTestCases,
			AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener, TestLinkSite testlink, Report report)
			throws ResultSeekerException {
		listener.getLogger().println( Messages.Results_JUnit_LookingForTestClasses() ); // i18n
		try {
			final TestResult testResult = parser.parse(this.includePattern, build, launcher, listener);
			
			for(SuiteResult suiteResult : testResult.getSuites()) {
				for(CaseResult caseResult : suiteResult.getCases()) {
					for(TestCaseWrapper automatedTestCase : automatedTestCases) {
						final String[] commaSeparatedValues = this.split(automatedTestCase.getKeyCustomFieldValue());
						for(String value : commaSeparatedValues) {
							if(caseResult.getClassName().equals(value)) {
								ExecutionStatus status = this.getExecutionStatus(caseResult);
								automatedTestCase.addCustomFieldAndStatus(value, status);
								if(automatedTestCase.getExecutionStatus() != ExecutionStatus.NOT_RUN) {
									testlink.updateTestCase(automatedTestCase);
								}
							}
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
	protected String getJUnitNotes( CaseResult testCase )
	{
		StringBuilder notes = new StringBuilder();
		// FIXME: fix the notes
		notes.append( 
				Messages.Results_JUnit_NotesForTestCase(
						testCase.getName(), 
						testCase.getClassName(), 
						testCase.getSkipCount(), 
						testCase.getFailCount(), 
						testCase.getSuiteResult().getTimestamp())
		);
		
		return notes.toString();
	}

}
