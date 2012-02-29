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


/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.0
 */
public class TestTestLinkReport 
extends junit.framework.TestCase
{

	private Report report;
	
	public void setUp()
	{
		this.report = new Report(100, null);
	}
	
	public void testTesLinkReportGettersAndSetters()
	{
		assertTrue( this.report.getBuildId() == 100 );
	}
	
	public void testReportNumbers()
	{
		assertTrue( report.getBlocked() == 0 );
		assertTrue( report.getFailed() == 0 );
		assertTrue( report.getPassed() == 0 );
		assertTrue( report.getTestsTotal() == 0 );
		
		report.setBlocked(1);
		
		assertTrue( report.getBlocked() == 1 );
		assertTrue( report.getFailed() == 0 );
		assertTrue( report.getPassed() == 0 );
		assertTrue( report.getTestsTotal() == 1 );
		
		report.setFailed(1);
		
		assertTrue( report.getBlocked() == 1 );
		assertTrue( report.getFailed() == 1 );
		assertTrue( report.getPassed() == 0 );
		assertTrue( report.getTestsTotal() == 2 );
		
		report.setPassed(1);
		
		assertTrue( report.getBlocked() == 1 );
		assertTrue( report.getFailed() == 1 );
		assertTrue( report.getPassed() == 1 );
		assertTrue( report.getTestsTotal() == 3 );
		
		report.setPassed(report.getPassed()+1);
		
		assertTrue( report.getBlocked() == 1 );
		assertTrue( report.getFailed() == 1 );
		assertTrue( report.getPassed() == 2 );
		assertTrue( report.getTestsTotal() == 4 );
		
		report.setPassed(report.getPassed()+1);
		
		assertTrue( report.getBlocked() == 1 );
		assertTrue( report.getFailed() == 1 );
		assertTrue( report.getPassed() == 3 );
		assertTrue( report.getTestsTotal() == 5 );
	}
}
