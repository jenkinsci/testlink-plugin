/**
 *	 __                                        
 *	/\ \      __                               
 *	\ \ \/'\ /\_\    ___     ___   __  __  __  
 *	 \ \ , < \/\ \ /' _ `\  / __`\/\ \/\ \/\ \ 
 *	  \ \ \\`\\ \ \/\ \/\ \/\ \L\ \ \ \_/ \_/ \
 *	   \ \_\ \_\ \_\ \_\ \_\ \____/\ \___x___/'
 *	    \/_/\/_/\/_/\/_/\/_/\/___/  \/__//__/  
 *                                          
 * Copyright (c) 1999-present Kinow
 * Casa Verde - São Paulo - SP. Brazil.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Kinow ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Kinow.                                      
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 17/08/2010
 */
package hudson.plugins.testlink;

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
 * @since 17/08/2010
 */
public class SVNHelper
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
	public SVNHelper( 
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
