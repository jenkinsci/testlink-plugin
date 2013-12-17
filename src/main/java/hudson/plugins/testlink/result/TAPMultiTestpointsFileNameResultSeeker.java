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
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.kohsuke.stapler.DataBoundConstructor;
import org.tap4j.consumer.TapConsumer;
import org.tap4j.consumer.TapConsumerFactory;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;

import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionStatus;

/**
 * <p>Seeks for test results matching each TAP file name with the key 
 * custom field.</p>
 * 
 * <p>Skips TAP Streams that were skipped.</p>
 * 
 * @author Javier Delgado - http://github.com/witokondoria
 * @since 3.9
 */

public class TAPMultiTestpointsFileNameResultSeeker extends AbstractTAPResultSeeker {
	
	private static final long serialVersionUID = 3068999690225000000L;
	
	
	@DataBoundConstructor
	public TAPMultiTestpointsFileNameResultSeeker(String includePattern,
			String keyCustomField, boolean attachTAPStream,
			boolean attachYAMLishAttachments, boolean includeNotes,
			boolean compareFullPath, boolean testpointsAsExecutions) {
		super(includePattern, keyCustomField, attachTAPStream,
				attachYAMLishAttachments, includeNotes, compareFullPath);
		
	}

	@Extension
	public static class DescriptorImpl extends ResultSeekerDescriptor {

		@Override
		public String getDisplayName() {
			return "TAP file name (test points as TestLink executions)"; // TBD: i18n
		}
	}
	
	@Override
	public void seek(final TestCaseWrapper[] automatedTestCases, AbstractBuild<?, ?> build, Launcher launcher, final BuildListener listener, TestLinkSite testlink) throws ResultSeekerException {
		
		try {
			final Map<String, TestSet> testSets = build.getWorkspace().act(new FilePath.FileCallable<Map<String, TestSet>>() {
				private static final long serialVersionUID = 1L;

				private Map<String, TestSet> testSets;
				
				public Map<String, TestSet> invoke(File workspace, VirtualChannel channel)
						throws IOException, InterruptedException {
					final String[] tapFiles = TAPMultiTestpointsFileNameResultSeeker.this.scan(workspace, includePattern, listener);
					
					testSets = new HashMap<String, TestSet>(tapFiles.length);
					
					for(String tapFile : tapFiles) {
						final File input = new File(workspace, tapFile);
						final TapConsumer tapConsumer = TapConsumerFactory.makeTap13YamlConsumer();
						final TestSet testSet = tapConsumer.load(input);
						testSets.put(tapFile, testSet);
					}
					
					return testSets;
				}
			});
			
			for(String key : testSets.keySet()) {
				for(TestCaseWrapper automatedTestCase : automatedTestCases) {
					final String[] commaSeparatedValues = automatedTestCase.getKeyCustomFieldValues(this.keyCustomField);
					for(String value : commaSeparatedValues) {
						String tapFileNameWithoutExtension = key;
						int leftIndex = 0;
						if (!this.isCompareFullPath()) {
						    int lastIndex = tapFileNameWithoutExtension.lastIndexOf(File.separator);
						    if (lastIndex > 0)
						        leftIndex = lastIndex+1;
						}
						int extensionIndex = tapFileNameWithoutExtension.lastIndexOf('.');
						if ( extensionIndex != -1 ) {
							tapFileNameWithoutExtension = tapFileNameWithoutExtension.substring(leftIndex, tapFileNameWithoutExtension.lastIndexOf('.'));
						}
						if(tapFileNameWithoutExtension.equals(value)) {												
							final TestSet testSet = testSets.get(key);
							Integer executionNumbers = testSet.getNumberOfTestResults();
							for (Integer i=1; i <= executionNumbers; i++){
								final TestResult result = testSet.getTestResult(i);
								ExecutionStatus status;
								if (result.getStatus().toString().toUpperCase().equals("OK")) {
									status = ExecutionStatus.PASSED;
								}
								else {
									status = ExecutionStatus.FAILED;
								}
								automatedTestCase.addCustomFieldAndStatus(value, status);
								
								if(this.isIncludeNotes()) {
									final String notes = super.getTapNotes(testSets.get(key));
									automatedTestCase.appendNotes(notes);
								}
								
								this.handleResult(automatedTestCase, build, listener, testlink, status, testSets, key);
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
}