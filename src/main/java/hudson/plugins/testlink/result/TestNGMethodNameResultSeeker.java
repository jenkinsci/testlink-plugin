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
import hudson.plugins.testlink.testng.Suite;
import hudson.plugins.testlink.testng.Test;
import hudson.plugins.testlink.testng.TestMethod;
import hudson.plugins.testlink.testng.TestNGParser;
import hudson.plugins.testlink.util.Messages;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;

/**
 * <p>Seeks for test results matching each TestNG Method name with the key 
 * custom field.</p>
 * 
 * <p>Skips TestNG Method that were disabled.</p>
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 3.1
 */
public class TestNGMethodNameResultSeeker extends AbstractTestNGResultSeeker {

	private static final long serialVersionUID = 3299323955206953910L;
	
	private final TestNGParser parser = new TestNGParser();
	
	/**
	 * @param includePattern
	 * @param keyCustomField
	 * @param attachTestNGXML
	 */
	@DataBoundConstructor
	public TestNGMethodNameResultSeeker(String includePattern, String keyCustomField, boolean attachTestNGXML) {
		super(includePattern, keyCustomField, attachTestNGXML);
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
			return "TestNG method name"; // TBD: i18n
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
					final String[] xmls = TestNGMethodNameResultSeeker.this.scan(workspace, includePattern, listener);
					
					for(String xml : xmls) {
						final File input = new File(workspace, xml);
						Suite suite = parser.parse(input);
						suites.add(suite);
					}
					
					return suites;
				}
			});
			for(Suite suite : suites) {
				for(Test test : suite.getTests() ) {
					for(hudson.plugins.testlink.testng.Class  clazz : test.getClasses()) {
						for(TestMethod method : clazz.getTestMethods()) {
							for(TestCaseWrapper automatedTestCase : automatedTestCases) {
								final String qualifiedName = clazz.getName()+'#'+method.getName();
								final String[] commaSeparatedValues = this.split(this.getKeyCustomFieldValue(automatedTestCase.getCustomFields(), this.keyCustomField));
								for(String value : commaSeparatedValues) {
									if(qualifiedName.equals(value)) {
										ExecutionStatus status = this.getExecutionStatus(method);
										automatedTestCase.addCustomFieldAndStatus(value, status);
										
										super.handleResult(automatedTestCase, build, listener, testlink, status, suite);
									}
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
	 * @param suite
	 * @return
	 */
	private ExecutionStatus getExecutionStatus(TestMethod method) {
		if ( StringUtils.isNotBlank(method.getStatus()) && !method.getStatus().equals("PASS")) {
			return ExecutionStatus.FAILED; // It's enough, one single failed is enough to invalidate a test suite
		} else {
			return ExecutionStatus.PASSED;
		}
	}

	/**
	 * Retrieves notes for TestNG suite.
	 * 
	 * @param suite TestNG suite.
	 * @return notes for TestNG suite and test class.
	 */
	protected String getTestNGNotes( Suite suite )
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
