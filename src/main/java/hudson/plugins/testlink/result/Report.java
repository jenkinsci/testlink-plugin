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

import java.io.Serializable;

/**
 * Build Report.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.0
 */
public class Report implements Serializable {

	private static final long serialVersionUID = -6536222071547639906L;

	private int passed;
	private int failed;
	private int blocked;
	private int notRun;

	private int buildId;
	private String buildName;

	/**
	 * Default constructor.
	 */
	public Report() {
		super();
	}

	/**
	 * @return the tests total
	 */
	public int getTestsTotal() {
		return passed + failed + blocked + notRun;
	}

	/**
	 * @return the passed
	 */
	public int getPassed() {
		return passed;
	}

	/**
	 * @param passed
	 *            the passed to set
	 */
	public void setPassed(int passed) {
		this.passed = passed;
	}

	/**
	 * @return the failed
	 */
	public int getFailed() {
		return failed;
	}

	/**
	 * @param failed
	 *            the failed to set
	 */
	public void setFailed(int failed) {
		this.failed = failed;
	}

	/**
	 * @return the blocked
	 */
	public int getBlocked() {
		return blocked;
	}

	/**
	 * @param blocked
	 *            the blocked to set
	 */
	public void setBlocked(int blocked) {
		this.blocked = blocked;
	}

	/**
	 * @return the notRun
	 */
	public int getNotRun() {
		return notRun;
	}

	/**
	 * @param notRun
	 *            the notRun to set
	 */
	public void setNotRun(int notRun) {
		this.notRun = notRun;
	}

	/**
	 * @return the buildId
	 */
	public int getBuildId() {
		return buildId;
	}

	/**
	 * @param buildId
	 *            the buildId to set
	 */
	public void setBuildId(int buildId) {
		this.buildId = buildId;
	}

	/**
	 * @return the buildName
	 */
	public String getBuildName() {
		return buildName;
	}

	/**
	 * @param buildName
	 *            the buildName to set
	 */
	public void setBuildName(String buildName) {
		this.buildName = buildName;
	}

}
