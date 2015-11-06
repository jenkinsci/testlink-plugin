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
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.plugins.testlink.TestLinkSite;

import java.util.Map;

import org.kohsuke.stapler.DataBoundConstructor;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.tap4j.util.StatusValues;

import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionStatus;

/**
 * <p>
 * Seeks for test results matching each TAP file name with the key custom field.
 * </p>
 * 
 * <p>
 * Skips TAP Streams that were skipped.
 * </p>
 * 
 * @author Javier Delgado - http://github.com/witokondoria
 * @since 3.9
 */

public class TAPFileNameMultiTestPointsResultSeeker extends AbstractTAPFileNameResultSeeker {

    private static final long serialVersionUID = 3068999690225000000L;

    @DataBoundConstructor
    public TAPFileNameMultiTestPointsResultSeeker(String includePattern, String keyCustomField,
            boolean attachTAPStream, boolean attachYAMLishAttachments, boolean includeNotes, boolean compareFullPath,
            boolean testpointsAsExecutions) {
        super(includePattern, keyCustomField, attachTAPStream, attachYAMLishAttachments, includeNotes, compareFullPath);

    }

    @Extension
    public static class DescriptorImpl extends ResultSeekerDescriptor {

        @Override
        public String getDisplayName() {
            return "TAP file name (test points as TestLink executions)"; // TBD: i18n
        }
    }

    @Override
    protected void updateTestCase(Map<String, TestSet> testSets, String key, TestCaseWrapper automatedTestCase,
            String value, AbstractBuild<?, ?> build, BuildListener listener, TestLinkSite testlink) {
        final TestSet testSet = testSets.get(key);
        int executionNumbers = testSet.getNumberOfTestResults();
        for (Integer i = 1; i <= executionNumbers; i++) {
            final TestResult result = testSet.getTestResult(i);
            if (result == null) // can get null results for test plan : n..m, n>1
                continue;
            ExecutionStatus status;
            if (result.getStatus().equals(StatusValues.OK)) {
                status = ExecutionStatus.PASSED;
            } else {
                status = ExecutionStatus.FAILED;
            }
            automatedTestCase.addCustomFieldAndStatus(value, status);

            if (this.isIncludeNotes()) {
                final String notes = super.getTapNotes(testSets.get(key));
                automatedTestCase.appendNotes(notes);
            }

            this.handleResult(automatedTestCase, build, listener, testlink, status, testSets, key);
        }
    }
}
