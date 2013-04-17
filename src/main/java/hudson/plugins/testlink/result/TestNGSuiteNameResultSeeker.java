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
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.plugins.testlink.TestLinkSite;
import hudson.plugins.testlink.util.Messages;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;

import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionStatus;

import com.tupilabs.testng.parser.Suite;
import com.tupilabs.testng.parser.Test;
import com.tupilabs.testng.parser.TestMethod;
import com.tupilabs.testng.parser.TestNGParser;

/**
 * <p>Seeks for test results matching each TestNG Suite name with the key 
 * custom field.</p>
 * 
 * <p>Skips TestNG Suite that were disabled.</p>
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 3.1
 */
public class TestNGSuiteNameResultSeeker extends AbstractTestNGResultSeeker {

	private static final long serialVersionUID = 3998602647639013614L;
	
	private final TestNGParser parser = new TestNGParser();
	
	/**
	 * @param includePattern
	 * @param keyCustomField
	 * @param attachTestNGXML
	 * @param markSkippedTestAsBlocked
	 */
	@DataBoundConstructor
	public TestNGSuiteNameResultSeeker(String includePattern, String keyCustomField, boolean attachTestNGXML, boolean markSkippedTestAsBlocked, boolean includeNotes) {
		super(includePattern, keyCustomField, attachTestNGXML, markSkippedTestAsBlocked, includeNotes);
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
			return "TestNG suite name"; // TBD: i18n
		}
	}

	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.result.ResultSeeker#seekAndUpdate(hudson.plugins.testlink.result.TestCaseWrapper<?>[], hudson.model.AbstractBuild, hudson.Launcher, hudson.model.BuildListener, hudson.plugins.testlink.TestLinkSite, hudson.plugins.testlink.result.Report)
	 */
	@Override
	public void seek(TestCaseWrapper[] automatedTestCases, AbstractBuild<?, ?> build, Launcher launcher, final BuildListener listener, TestLinkSite testlink) throws ResultSeekerException {
		listener.getLogger().println( Messages.Results_TestNG_LookingForTestSuites() );
		try {
			final List<Suite> suites = build.getWorkspace().act(new FilePath.FileCallable<List<Suite>>() {
				private static final long serialVersionUID = 1L;

				private List<Suite> suites = new ArrayList<Suite>();
				
				public List<Suite> invoke(File workspace, VirtualChannel channel)
						throws IOException, InterruptedException {
					final String[] xmls = TestNGSuiteNameResultSeeker.this.scan(workspace, includePattern, listener);
					
					for(String xml : xmls) {
						final File input = new File(workspace, xml);
						Suite suite = parser.parse(input);
						suites.add(suite);
					}
					
					return suites;
				}
			});
			for(Suite suite : suites) {
				for(TestCaseWrapper automatedTestCase : automatedTestCases) {
					final String[] commaSeparatedValues = automatedTestCase.getKeyCustomFieldValues(this.keyCustomField);
					for(String value : commaSeparatedValues) {
						if(suite.getName().equals(value)) {
							ExecutionStatus status = this.getExecutionStatus(suite);
							if(status != ExecutionStatus.NOT_RUN) {
								automatedTestCase.addCustomFieldAndStatus(value, status);
							}
							
							if(this.isIncludeNotes()) {
								final String notes = this.getTestNGNotes(suite);
								automatedTestCase.appendNotes(notes);
							}
							
							super.handleResult(automatedTestCase, build, listener, testlink, status, suite);
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
	 * @param suite
	 * @return
	 */
	private ExecutionStatus getExecutionStatus(Suite suite) {
		for( Test test : suite.getTests() )	{
			for(com.tupilabs.testng.parser.Class clazz : test.getClasses()) {
				for( TestMethod method : clazz.getTestMethods() ) {
					if(method.getStatus().equals(FAIL)) {
						return ExecutionStatus.FAILED; 
					} else if(method.getStatus().equals(SKIP)) {
						if(this.isMarkSkippedTestAsBlocked()) { 
							return ExecutionStatus.BLOCKED;
						} else {
							return ExecutionStatus.NOT_RUN;
						}
					}
				}
			}
		}
		return ExecutionStatus.PASSED;
	}

	/**
	 * Retrieves notes for TestNG suite.
	 * 
	 * @param suite TestNG suite.
	 * @return notes for TestNG suite and test class.
	 */
	private String getTestNGNotes( Suite suite )
	{
		StringBuilder notes = new StringBuilder();
		
		notes.append( 
				Messages.Results_TestNG_NotesForSuite(
						suite.getName(), 
						suite.getDurationMs(), 
						suite.getStartedAt(), 
						suite.getFinishedAt(), suite.getTests().size() 
				)
		);
		
		return notes.toString();
	}
	
}
