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
package hudson.plugins.testlink;

import hudson.tasks.junit.JUnitParser;
import hudson.tasks.junit.TestResult;
import hudson.model.Run;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.HashMap;

import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.DirectoryScanner;
import hudson.Util;
import jenkins.MasterToSlaveFileCallable;
import hudson.remoting.VirtualChannel;
import org.dom4j.io.SAXReader;
import hudson.util.io.ParserConfigurator;
import org.dom4j.Document;
import org.dom4j.Element;
import java.util.List;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by azikha01 on 29/07/2016.
 */
public class TestLinkJunitWrapper extends JUnitParser {
    private Map<String, Map<String, String>> customFields = null;
    private PrintStream logger = null;
    private static final Logger LOGGER = Logger.getLogger("hudson.plugins.testlink");

    public TestLinkJunitWrapper(boolean keepLongStdio, boolean allowEmptyResults) {
        super(keepLongStdio, allowEmptyResults);
    }

    public TestResult parseResult(String testResultLocations, Run<?, ?> build, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {

        logger = listener.getLogger();
        TestResult r = super.parseResult(testResultLocations, build, workspace, launcher, listener);

        /* Second parse of files to find Test Case custom field values */
        this.customFields = (Map<String, Map<String, String>>)workspace.act(new TestLinkJunitWrapper.ParseResultCallable(testResultLocations, logger));
        Iterator it = customFields.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            logger.println("Test Case " + pair.getKey());
            Map<String, String> cfs = (Map<String, String>)pair.getValue();
            Iterator itt = cfs.entrySet().iterator();
            while (itt.hasNext()) {
                Map.Entry pairr = (Map.Entry)itt.next();
                logger.println("\tCustom field = " + pairr.getKey() + " value = " + pairr.getValue());
            }
        }
        return r;
    }

    private static final class ParseResultCallable extends MasterToSlaveFileCallable<Map<String, Map<String, String>>> {
        private final String testResults;
        private final PrintStream logger;

        private ParseResultCallable(String testResults, PrintStream logger) {
            this.testResults = testResults;
            this.logger = logger;
        }

        public Map<String, Map<String, String>> invoke(File ws, VirtualChannel channel) throws IOException {
            FileSet fs = Util.createFileSet(ws, this.testResults);
            DirectoryScanner ds = fs.getDirectoryScanner();
            Map<String, Map<String, String>> customFields = new HashMap<String, Map<String, String>>();
            String[] files = ds.getIncludedFiles();
            if(files.length > 0) {
                String[] reportFiles = ds.getIncludedFiles();
                File baseDir = ds.getBasedir();

                int len$ = reportFiles.length;

                for(int f = 0; f < len$; ++f) {
                    String value = reportFiles[f];
                    File reportFile = new File(baseDir, value);

                    try {
                        this.parseCustomFields(reportFile, customFields);
                    } catch (org.dom4j.DocumentException e) {
                        throw new IOException(e);
                    }
                }

            }

            return customFields;
        }

        private void parseCustomFields (File reportFile, Map<String, Map<String, String>> customFields) throws org.dom4j.DocumentException {
            String xmlReport = reportFile.getName();
            SAXReader saxReader = new SAXReader();
            Document result = saxReader.read(reportFile);
            Element root = result.getRootElement();
            List testCases = root.elements("testcase");

            for(Iterator stdout = testCases.iterator(); stdout.hasNext();) {
                Element tc = (Element)stdout.next();
                String m = tc.attributeValue("classname");
                Map<String, String> cfs = new HashMap<String, String>();
                // Get other attributes and extract custom fields
                List children = tc.elements();
                for (Iterator child = children.iterator(); child.hasNext();){
                    // get tag and text
                    Element childe = (Element)child.next();
                    // exclude Junit defined names like error, failure, stdin, stdout
                    if (childe.getName().equals("skipped") ||
                            childe.getName().equals("error") ||
                            childe.getName().equals("failure") ||
                            childe.getName().equals("system-out") ||
                            childe.getName().equals("system-err")
                            ) {
                        continue;
                    }
                    cfs.put(childe.getName(), childe.getText());
                    // what should we do with these custom fields
                    customFields.put(m, cfs);
                }
            }
        }
    }

    public Map<String, Map<String, String>> getCustomFields (){ return customFields;}
}
