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
package hudson.plugins.testlink.parser.issue8531;

import hudson.tasks.junit.SuiteResult;
import hudson.tasks.junit.TestResult;
import hudson.tasks.junit.CaseResult;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.jvnet.hudson.test.Bug;

/**
 * Tests the JUnit parser for issue 8531.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
@Bug(8531)
public class TestIssue8531 
extends junit.framework.TestCase
{
	
	public void testJUnitParserIssue8531() throws IOException
	{
		ClassLoader cl = TestIssue8531.class.getClassLoader();
		URL url = cl.getResource("hudson/plugins/testlink/parser/issue8531/TEST-navigation_vers_les_massifs.xml");
		File junitFile = new File( url.getFile() );
		
		TestResult testResult = new TestResult();
		testResult.parse(junitFile);
		
		assertEquals( testResult.getSuites().size(), 1 );
		
		SuiteResult suite = testResult.getSuites().iterator().next();
		
		assertTrue( "Invalid number of test cases.", suite.getCases().size() == 2 );
		
		CaseResult testCase1 = suite.getCases().get(0);
		assertNotNull( testCase1 );
		assertTrue( testCase1.getClassName().equals("Navigation vers les massifs.Acceder par la liste") );
		assertTrue( testCase1.getName().equals("Acceder par la liste (outline example : | Massif du Mercantour | Massif du Mercantour |)") );
		
		CaseResult testCase2 = suite.getCases().get(1);
		assertNotNull( testCase2 );
		assertTrue( testCase2.getClassName().equals("Navigation vers les massifs.Acceder par la carte") );
		assertTrue( testCase2.getName().equals("Acceder par la carte (outline example : | 3 | Massif du Mercantour |)") );
		
	}
	
}
