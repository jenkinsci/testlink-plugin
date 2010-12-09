/**
 * 
 */
package hudson.plugins.testlink.parser;

import hudson.FilePath.FileCallable;
import hudson.model.BuildListener;
import hudson.plugins.testlink.model.TestResult;
import hudson.plugins.testlink.scanner.Scanner;
import hudson.plugins.testlink.util.TestLinkPluginException;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import br.eti.kinoshita.testlinkjavaapi.model.Attachment;
import br.eti.kinoshita.testlinkjavaapi.model.TestLinkTables;

/**
 * 
 * Abstract parser. Parses a directory looking for files that match a given 
 * pattern. Once a file is found it is passed to an abstract method that 
 * shall return a list of Test Results. Finally, all the values contained in 
 * the list are added to a master list and returned to the caller.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.0
 */
public abstract class Parser 
implements FileCallable<List<TestResult>>
{
	
	/**
	 * Pattern to search for files. (Ant-like pattern)
	 */
	protected String includePattern;
	
	/**
	 * Build Listener to log events.
	 */
	protected BuildListener listener;
	
	/**
	 * Scanner to get the list of included files.
	 */
	protected Scanner scanner;
	
	private Boolean enabled = Boolean.TRUE;
	
	/**
	 * @param ps Build Listener to log events.
	 * @param includePattern Pattern to include files.
	 */
	public Parser( BuildListener listener, String includePattern )
	{
		this.includePattern = includePattern;
		
		this.listener = listener;
		
		scanner = new Scanner( listener );
		
		if ( StringUtils.isBlank(includePattern) )
		{
			this.enabled = Boolean.FALSE;
		}
	}
	
	/**
	 * @return True when enabled, false otherwise.
	 */
	public Boolean isEnabled()
	{
		return this.enabled;
	}
	
	/**
	 * @return Name of the Parser (JUnit, TAP, TestNG, PHPUnit, etc)
	 */
	public abstract String getName();

	/**
	 * Parses a directory for files matching a given pattern.
	 * 
	 * @param baseDir 
	 * @return List of Test Results.
	 * @throws TestLinkPluginException
	 * @throws IOException
	 */
	public List<TestResult> parse( File baseDir ) 
	throws IOException
	{
		
		listener.getLogger().println("Scanning for " + getName() + " files in " + baseDir + ". Include pattern: " + this.includePattern);
		
		final List<TestResult> results = new ArrayList<TestResult>();
		final String[] files = scanner.scanForTestResults( baseDir, includePattern );
		
		for ( int i = 0 ; i < files.length ; ++i )
		{
			String fileName = files[i];
			File file = new File( baseDir, fileName );
			
			listener.getLogger().println( getName() + " file found. Parsing file to extract Test Results");
			List<TestResult> foundTestResults = this.parseFile ( file );
			
			results.addAll( foundTestResults );
		}
		
		return results;
	}

	/**
	 * Parses a single file.
	 * 
	 * @param file Given file to parse.
	 * @return List of TestResults found.
	 * @throws TestLinkPluginException
	 * @throws IOException
	 */
	protected abstract List<TestResult> parseFile(File file) 
	throws IOException;
	
	/* (non-Javadoc)
	 * @see hudson.FilePath.FileCallable#invoke(java.io.File, hudson.remoting.VirtualChannel)
	 */
	public List<TestResult> invoke( File file, VirtualChannel channel )
	throws IOException, InterruptedException 
	{
		return this.parse( file );
	}
	
	/**
	 * @param file
	 * @return
	 * @throws IOException 
	 * @throws MessagingException 
	 */
	protected Attachment getAttachment( Integer fkId, File file ) 
	throws IOException, MessagingException
	{
		Attachment attachment = new Attachment();
		String fileContent = this.getBase64FileContent( file );
		attachment.setContent( fileContent );
		attachment.setDescription( "JUnit report file for Automated Test Case" );
		attachment.setFileName( file.getName() );
		attachment.setFileSize( file.length() );
		attachment.setFkTable( TestLinkTables.nodesHierarchy.toString() );
		attachment.setFkId( fkId );
		attachment.setTitle( file.getName() );
		return attachment;
	}
	
	/**
	 * @param file
	 * @return
	 * @throws MessagingException 
	 */
	protected String getBase64FileContent( File file ) 
	throws IOException, MessagingException
	{
		byte[] fileData = FileUtils.readFileToByteArray(file);
		
		return Base64.encodeBase64String( fileData );
		
	}
	
}
