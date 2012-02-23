/*
 * The MIT License
 *
 * Copyright (c) <2011> <Bruno P. Kinoshita>
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
package hudson.plugins.testlink.util;

import hudson.plugins.testlink.result.TestCaseWrapper;

import java.util.Arrays;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TestExecutionOrderComparator extends TestCase {

	private ExecutionOrderComparator comparator;

	private TestCaseWrapper tc1 = new TestCaseWrapper();

	private TestCaseWrapper tc2 = new TestCaseWrapper();

	private TestCaseWrapper tc3 = new TestCaseWrapper();

	TestCaseWrapper[] arr = new TestCaseWrapper[3];

	public void setUp() {
		this.comparator = new ExecutionOrderComparator();

		tc1.setExecutionOrder(1);
		tc2.setExecutionOrder(1);
		tc3.setExecutionOrder(1);

		this.arr[0] = tc1;
		this.arr[1] = tc2;
		this.arr[2] = tc3;
	}

	public void testOrderingNulls() {
		TestCaseWrapper[] arrOfNulls = new TestCaseWrapper[2];
		try {
			Arrays.sort(arrOfNulls, this.comparator);
		} catch (NullPointerException npe) {
			Assert.assertTrue(System.currentTimeMillis() > 0);
		}
	}

	public void testOrdering() {
		Arrays.sort(arr, this.comparator);

		Assert.assertEquals(arr[0], tc1);
		Assert.assertEquals(arr[1], tc2);
		Assert.assertEquals(arr[2], tc3);

		tc2.setExecutionOrder(1);
		tc1.setExecutionOrder(2);
		tc3.setExecutionOrder(3);

		Arrays.sort(arr, this.comparator);

		Assert.assertEquals(arr[0], tc2);
		Assert.assertEquals(arr[1], tc1);
		Assert.assertEquals(arr[2], tc3);

	}

}
