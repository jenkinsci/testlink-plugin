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
package hudson.plugins.testlink.result;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.FreeStyleBuild;
import hudson.model.AbstractBuild;
import hudson.model.FreeStyleProject;
import hudson.plugins.testlink.TestLinkSiteFake;
import hudson.tasks.Builder;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.FileUtils;
import org.jvnet.hudson.test.HudsonTestCase;

import br.eti.kinoshita.testlinkjavaapi.model.CustomField;
import br.eti.kinoshita.testlinkjavaapi.model.ExecutionStatus;

/**
 * Tests ResultSeeker with JUnit Case name.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
public class TestJUnitCaseNameResultSeeker 
extends HudsonTestCase
{
	private static FreeStyleProject project;
	private final static TestCaseWrapper[] tcs = new TestCaseWrapper[2];
	private static Report report = new Report();
	private static TestLinkSiteFake testlink = new TestLinkSiteFake(null, null, null, null);
	
	private final static String KEY_CUSTOM_FIELD = "testCustomField";
	
	private static final class JUnitParserTestBuilder extends Builder implements Serializable {

		private JUnitCaseNameResultSeeker seeker;
		
		private static final long serialVersionUID = 1901057963549424589L;
		
		/* (non-Javadoc)
		 * @see hudson.tasks.BuildStepCompatibilityLayer#perform(hudson.model.AbstractBuild, hudson.Launcher, hudson.model.BuildListener)
		 */
		@Override
		public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
				BuildListener listener) throws InterruptedException,
				IOException {
			for (FilePath f : build.getWorkspace().list()) {
                f.touch(System.currentTimeMillis());
            }
			
            seeker = new JUnitCaseNameResultSeeker("TEST-*.xml", KEY_CUSTOM_FIELD);
            seeker.seek(tcs, build, launcher, listener, testlink, report);
            return (seeker != null);
		}
		
	}
	
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
		
		File workspaceFile = new File(temp, "hudson/plugins/testlink/result/junit/");
		
		if(!(workspaceFile.mkdirs())) {
			throw new IOException("Could not create temp workspace " + temp);
		}
		
		ClassLoader cl = TestJUnitCaseNameResultSeeker.class.getClassLoader();
		URL url = cl.getResource("hudson/plugins/testlink/result/junit/");
		File junitDir = new File( url.getFile() );
		
		FileUtils.copyDirectory(junitDir, workspaceFile);
		
		project.setCustomWorkspace(workspaceFile.getAbsolutePath());
		
		project.getBuildersList().add(new JUnitParserTestBuilder());
		testlink = new TestLinkSiteFake(null, null, null, null);
		
		TestCaseWrapper tc = new TestCaseWrapper(new String[]{KEY_CUSTOM_FIELD});
		CustomField cf = new CustomField();
		
		tc = new TestCaseWrapper(new String[]{KEY_CUSTOM_FIELD});
		cf = new CustomField();
		cf.setName( KEY_CUSTOM_FIELD );
		cf.setValue("testVoid");
		tc.getCustomFields().add(cf);
		tc.setId(1);
		tc.setKeyCustomFieldValue(cf.getValue());
		tcs[0] = tc;
		
		tc = new TestCaseWrapper(new String[]{KEY_CUSTOM_FIELD});
		cf = new CustomField();
		cf.setName( KEY_CUSTOM_FIELD );
		cf.setValue("Consultation");
		tc.getCustomFields().add(cf);
		tc.setId(2);
		tc.setKeyCustomFieldValue(cf.getValue());
		tcs[1] = tc;
	}

	//@LocalData
	public void testJUnitCaseNameResultSeeker() throws IOException, InterruptedException, ExecutionException, TimeoutException
	{
		//build = project.scheduleBuild2(0).get(100, TimeUnit.MINUTES);
		
		FreeStyleBuild build = project.scheduleBuild2(0).get(100, TimeUnit.MINUTES);
        assertNotNull(build);
        
		assertNotNull(report);
		
		assertEquals( 3, report.getTestsTotal() );
		// TODO organize directories, XMLs and rewrite this test
	}
	
	
}
