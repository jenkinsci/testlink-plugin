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
package hudson.plugins.testlink.result.parser;

import hudson.plugins.testlink.parser.tap.TAPParser;

import java.io.File;

import br.eti.kinoshita.tap4j.parser.ParserException;

import junit.framework.TestCase;

/**
 *
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.1
 */
public class TestParseFileNotFound 
extends TestCase
{
	
	/**
	 * The TAP Parser.
	 */
	private TAPParser parser;
	
	/**
	 * Initializes the TAP Parser.
	 */
	public void setUp()
	{
		this.parser = new TAPParser();
	}
	
	public void testParseFileNotFound()
	{
		assertEquals(this.parser.getName(), "TAP");
		try
		{
			this.parser.parse( new File("test.non.existent.file") );
			fail("Not supposed to get here.");
		}
		catch (ParserException p) 
		{
			assertNotNull(p);
		}
	}

}
