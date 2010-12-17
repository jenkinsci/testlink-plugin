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
package hudson.plugins.testlink.svn;

import java.net.MalformedURLException;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

/**
 * Helper class to access SVN.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.2
 */
public class SVNLatestRevisionService 
{

	private final String repoUrl;
	private final String userName;
	private final String password;
	
	/**
	 * @param repoUrl SVN repository URL
	 * @param userName SVN username
	 * @param password SVN password
	 * @throws MalformedURLException
	 */
	public SVNLatestRevisionService( 
			String repoUrl, 
			String userName, 
			String password ) 
	throws MalformedURLException
	{
		this.repoUrl = repoUrl;
		this.userName = userName;
		this.password = password;
		DAVRepositoryFactory.setup();
		SVNRepositoryFactoryImpl.setup();
	}
	
	/**
	 * @return The SVN repository URL
	 */
	public String getRepositoryURL()
	{
		return this.repoUrl;
	}
	
	/**
	 * Retrieves the latest revision of a repository.
	 * 
	 * @return Latest Revision of a repository
	 * @throws SVNException
	 */
	public long getLatestRevision() 
	throws SVNException
	{
		SVNRepository repository = null;
		final SVNURL svnURL = SVNURL.parseURIEncoded(this.getRepositoryURL());
		repository = SVNRepositoryFactory.create( svnURL );
		
		if ( this.userName != null )
		{
			ISVNAuthenticationManager authManager = 
				new BasicAuthenticationManager(userName, password);
			repository.setAuthenticationManager( authManager );
		}
		
		final SVNDirEntry entry = repository.getDir("", -1, false, null);
		
		return entry.getRevision();
	}

}
