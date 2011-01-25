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
package hudson.plugins.testlink.scanner;

import hudson.Util;
import hudson.model.BuildListener;
import hudson.plugins.testlink.Messages;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.0
 */
public class Scanner 
implements Serializable
{
	
	protected BuildListener listener;
	
	public Scanner( BuildListener listener )
	{
		super();
		this.listener = listener;
	}
	
	public String[] scanForTestResults( File baseDir, String includes ) 
	throws IOException
	{
		
		if ( StringUtils.isBlank( includes ) )
		{
			return new String[0];
		}
		
		String[] fileNames = new String[0];
		
		FileSet fs = null;
		try
		{
			fs = Util.createFileSet(baseDir, includes);
		}
		catch ( Exception e ) // TBD: find out which exception is thrown
		{
			e.printStackTrace( listener.getLogger() );
			throw new IOException( Messages.TestLinkBuilder_Scanner_FailedToOpenBaseDirectory(e.getMessage()), e );
		}
		
		DirectoryScanner ds = fs.getDirectoryScanner();
		
		fileNames = ds.getIncludedFiles();
		
		return fileNames;
		
	}
	
}
