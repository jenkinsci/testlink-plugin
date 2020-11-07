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

import java.io.Serializable;

import javax.annotation.CheckForNull;

import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerProxy;
import org.kohsuke.stapler.StaplerRequest;

import hudson.model.Run;
import hudson.plugins.testlink.util.TestLinkHelper;
import jenkins.model.RunAction2;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TestLinkBuildAction implements RunAction2, Serializable, StaplerProxy {

    private static final long serialVersionUID = -914904584770393909L;

    public static final String DISPLAY_NAME = "TestLink";
    public static final String ICON_FILE_NAME = "/plugin/testlink/icons/testlink-24.png";
    public static final String URL_NAME = "testLinkResult";

    private transient Run<?, ?> build;

    private final Report report;

    public TestLinkBuildAction(Report report) {
        this.report = report;
    }

    @Override
    public void onLoad(Run<?, ?> r) {
        this.build = r;
    }

    @Override
    public void onAttached(Run<?, ?> r) {
        this.build = r;
    }

    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    public String getIconFileName() {
        return ICON_FILE_NAME;
    }

    public String getUrlName() {
        return URL_NAME;
    }

    /**
     * Gets Run to which the action is attached.
     * @return Run instance
     * @since TODO
     */
    public Run<?, ?> getRun() {
        return build;
    }

    @Restricted(NoExternalUse.class) // only used from stapler/jelly
    @CheckForNull
    public Run<?,?> getOwningRun() {
        StaplerRequest req = Stapler.getCurrentRequest();
        if (req == null) {
            return null;
        }
        return req.findAncestorObject(Run.class);
    }

    public Report getReport() {
        return this.report;
    }

    /**
     * @return Previous TestLink report
     */
    private Report getPreviousReport() {
        TestLinkBuildAction previousResult = this.getPreviousAction();
        Report previousReport = null;
        if (previousResult != null) {
            previousReport = previousResult.getReport();
        }
        return previousReport;
    }

    /**
     * @return Previous Build Action
     */
    public TestLinkBuildAction getPreviousAction() {
        if (this.build != null) {
            Run<?, ?> previousBuild = this.build.getPreviousBuild();
            if (previousBuild != null) {
                return previousBuild.getAction(TestLinkBuildAction.class);
            }
        }
        return null;
    }

    /**
     * @return Report summary
     */
    public String getSummary() {
        return TestLinkHelper.createReportSummary(this.getReport(), this.getPreviousReport());
    }

    /**
     * @return Detailed Report summary
     */
    public String getDetails() {
        return TestLinkHelper.createReportSummaryDetails(this.getReport(), this.getPreviousReport());
    }

    @Override
    public Object getTarget() {
        return this;
    }

}
