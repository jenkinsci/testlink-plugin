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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.CredentialsStore;
import com.cloudbees.plugins.credentials.domains.Domain;

import hudson.util.Secret;
import jenkins.model.Jenkins;

/**
 * Tests the TestLinkBuilderInstallation class.
 *
 * @see {@link TestLinkInstallation}
 *
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
public class TestTestLinkBuilderInstallation 
{

    @Rule
    public JenkinsRule r = new JenkinsRule();

	/**
	 * Tests with a TestLinkBuilderInstallation object.
	 * @throws IOException when adding a credentials, if it fails
	 */
    @Test
	public void testInstallation() throws IOException
	{
        final String credentialsId = "tl-test-api-key";
        final CredentialsStore store = CredentialsProvider.lookupStores(r.getInstance()).iterator().next();
        final Secret secret = Secret.fromString("068848");
        final StringCredentials credentials = new StringCredentialsImpl(
                CredentialsScope.GLOBAL,
                credentialsId,
                "Test devKey used for unit tests",
                secret);
        store.addCredentials(Domain.global(), credentials);
        final TestLinkInstallation inst = 
			new TestLinkInstallation(
					"TestLink 1.9.1", 
					"http://localhost/testlink-1.9.1/lib/api/xml-rpc.php", 
					credentialsId,
					"");

		assertEquals(inst.getName(), "TestLink 1.9.1");
		assertEquals(inst.getUrl(), "http://localhost/testlink-1.9.1/lib/api/xml-rpc.php");
		assertEquals(inst.getDevKey(), "068848");
	}

}
