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

import hudson.DescriptorExtensionList;
import hudson.Launcher;
import hudson.Util;
import hudson.model.BuildListener;
import hudson.model.Describable;
import hudson.model.AbstractBuild;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.Node;
import hudson.plugins.testlink.TestLinkSite;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;

import br.eti.kinoshita.testlinkjavaapi.model.CustomField;

/**
 * Seeks for Results.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.2
 */
public abstract class ResultSeeker implements Serializable, Describable<ResultSeeker>, Comparable<ResultSeeker> {

	private static final long serialVersionUID = 3609106615463455486L;
	
	/**
	 * Include pattern used when looking for results.
	 */
	protected final String includePattern;
	
	/**
	 * Key custom field.
	 */
	protected final String keyCustomField;

	/**
	 * Creates a result seeker passing a ant-like pattern to look for results.
	 * 
	 * @param includePattern Include pattern when looking for results.
	 */
	public ResultSeeker(String includePattern, String keyCustomField) {
		super();
		this.includePattern = includePattern;
		this.keyCustomField = keyCustomField;
	}
	
	/**
	 * @return the includePattern
	 */
	public String getIncludePattern() {
		return includePattern;
	}
	
	/**
	 * @return the keyCustomField
	 */
	public String getKeyCustomField() {
		return keyCustomField;
	}
	
	protected String getKeyCustomFieldValue(List<CustomField> customFields, String keyCustomFieldName) {
		String keyCustomFieldValue = null;
		for(CustomField customField : customFields) {
			if(customField.getName().equals(keyCustomFieldName)) {
				keyCustomFieldValue = customField.getValue();
				break;
			}
		}
		return keyCustomFieldValue;
	}

	/*
	 * (non-Javadoc)
	 * @see hudson.model.Describable#getDescriptor()
	 */
	public ResultSeekerDescriptor getDescriptor() {
		return (ResultSeekerDescriptor) Hudson.getInstance().getDescriptor(getClass());
	}

	public static DescriptorExtensionList<ResultSeeker, Descriptor<ResultSeeker>> all() {
		return Hudson.getInstance().<ResultSeeker, Descriptor<ResultSeeker>> getDescriptorList(ResultSeeker.class);
	}

	public static DescriptorExtensionList<ResultSeeker, Descriptor<ResultSeeker>> allExcept(
			Node current) {
		return Hudson.getInstance().<ResultSeeker, Descriptor<ResultSeeker>> getDescriptorList(ResultSeeker.class);
	}

	/**
	 * <p>Seeks for Test Results in a directory. It tries to match the
	 * includePattern with files in this directory.</p>
	 * 
	 * <p>It looks for results using the include pattern, but this value 
	 * is matched within the workspace. It means that your result files have 
	 * to be relative to your workspace.</p>
	 * 
	 * <p>For each result found, it is automatically updated in TestLink, and 
	 * the Report is updated.</p>
	 * 
	 * @param automatedTestcases Automated test cases
	 * @param workspace Build workspace, used when looking for results using the include pattern
	 * @param listener Build listener for logging
	 * @param testlink TestLink site for updating test status
	 * @throws ResultSeekerException
	 */
	public abstract void seek(TestCaseWrapper[] automatedTestCases, AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener, TestLinkSite testlink) throws ResultSeekerException;

	/**
	 * Retrieves the file content encoded in Base64.
	 * 
	 * @param file
	 *            file to read the content.
	 * @return file content encoded in Base64.
	 * @throws IOException
	 */
	protected String getBase64FileContent(File file) throws IOException {
		byte[] fileData = FileUtils.readFileToByteArray(file);
		return Base64.encodeBase64String(fileData);
	}

	/**
	 * Splits a String by comma and gets an array of Strings.
	 */
	protected String[] split(String input) {
		if (StringUtils.isBlank(input)) {
			return new String[0];
		}

		StringTokenizer tokenizer = new StringTokenizer(input, ",");

		String[] values = new String[tokenizer.countTokens()];

		for (int i = 0; tokenizer.hasMoreTokens(); i++) {
			values[i] = tokenizer.nextToken().trim();
		}

		return values;
	}

	/**
	 * Scans a directory for files matching the includes pattern.
	 * 
	 * @param directory
	 *            the directory to scan.
	 * @param includes
	 *            the includes pattern.
	 * @param listener
	 *            Hudson Build listener.
	 * @return array of strings of paths for files that match the includes
	 *         pattern in the directory.
	 * @throws IOException
	 */
	protected String[] scan(final File directory, final String includes, final BuildListener listener) throws IOException {
		String[] fileNames = new String[0];

		if (StringUtils.isNotBlank(includes)) {
			FileSet fs = null;

			try {
				fs = Util.createFileSet(directory, includes);

				DirectoryScanner ds = fs.getDirectoryScanner();
				fileNames = ds.getIncludedFiles();
			} catch (BuildException e) {
				e.printStackTrace(listener.getLogger());
				throw new IOException(e);
			}
		}

		return fileNames;

	}

	/**
	 * Gets the key custom field out of a list using the key custom field name.
	 */
	protected CustomField getKeyCustomField(List<CustomField> customFields, String keyCustomFieldName) {
		CustomField customField = null;

		for (CustomField cf : customFields) {
			boolean isKeyCustomField = cf.getName().equals(keyCustomFieldName);

			if (isKeyCustomField) {
				customField = cf;
				break;
			}

		}
		return customField;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(ResultSeeker o) {
		return o != null ? this.getDescriptor().getDisplayName().compareTo(o.getDescriptor().getDisplayName()) : 0;
	}

}
