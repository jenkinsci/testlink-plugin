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

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.FreeStyleProject;
import hudson.plugins.testlink.TestLinkSiteFake;
import hudson.tasks.Builder;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.jvnet.hudson.test.HudsonTestCase;

/**
 * Utility class for testing Result Seekers.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 3.1
 */
public abstract class ResultSeekerTestCase extends HudsonTestCase {

	protected FreeStyleProject project;
	protected TestLinkSiteFake testlink = new TestLinkSiteFake();
	
	/**
	 * @return array of automated test cases retrieved from TestLink.
	 */
	public abstract TestCaseWrapper[] getAutomatedTestCases();
	
	/**
	 * {@link ResultSeeker}
	 * @return Result Seeker.
	 */
	public abstract ResultSeeker getResultSeeker();
	
	/**
	 * @return directory with result files.
	 */
	public abstract String getResultsDirectory();
	
	/**
	 * @return the pattern to look for results. 
	 */
	public abstract String getResultsPattern();
	
	protected void setUp() throws Exception {
		super.setUp();
		
		project = createFreeStyleProject();
		File temp = File.createTempFile("resultseeker", Long.toString(System.nanoTime()));
		
		if(!(temp.delete())) {
			throw new IOException("Could not delete temp directory " + temp);
		}
		
		if(!(temp.mkdir())) {
			throw new IOException("Could not create temp directory " + temp);
		}
		
		File workspaceFile = new File(temp, getResultsDirectory());
		
		if(!(workspaceFile.mkdirs())) {
			throw new IOException("Could not create temp workspace " + temp);
		}
		
		ClassLoader cl = ResultSeekerTestCase.class.getClassLoader();
		URL url = cl.getResource(getResultsDirectory());
		File junitDir = new File( url.getFile() );
		
		FileUtils.copyDirectory(junitDir, workspaceFile);
		
		project.setCustomWorkspace(workspaceFile.getAbsolutePath());
		
		project.getBuildersList().add(new ResultSeekerBuilder(getResultSeeker(), getAutomatedTestCases(), testlink));
	}
}

/**
 * A builder that invokes a result builder.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 3.1
 */
class ResultSeekerBuilder extends Builder implements Serializable {

	private static final long serialVersionUID = 3497104063426101764L;
	
	private ResultSeeker seeker;
	private TestCaseWrapper[] tcs;
	private TestLinkSiteFake testlink;
	
	public ResultSeekerBuilder(ResultSeeker seeker, TestCaseWrapper[] tcs, TestLinkSiteFake testlink) {
		this.seeker = seeker;
		this.tcs = tcs;
		this.testlink = testlink;
	}
	
	/* (non-Javadoc)
	 * @see hudson.tasks.BuildStepCompatibilityLayer#perform(hudson.model.AbstractBuild, hudson.Launcher, hudson.model.BuildListener)
	 */
	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener) throws InterruptedException,
			IOException {
		for (FilePath f : build.getWorkspace().list()) {
			traverseDirectory(f);
        }
		
        seeker.seek(tcs, build, launcher, listener, testlink);
        return (seeker != null);
	}
	private static void traverseDirectory(FilePath f) throws InterruptedException, IOException {
		if (f.isDirectory()) {
			for (FilePath subdir : f.list()) {
				if (subdir.isDirectory()) {
					traverseDirectory(subdir);
				}
				subdir.touch(System.currentTimeMillis());
			}
		}
        f.touch(System.currentTimeMillis());		
	}
}
