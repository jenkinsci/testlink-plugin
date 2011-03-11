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
package hudson.plugins.testlink;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.plugins.testlink.util.Messages;
import hudson.util.ChartUtil;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Helper class for TestLink Graphs.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.6
 */
public class TestLinkGraphHelper
{

	/**
	 * Default constructor is hidden as this is a helper class.
	 */
	private TestLinkGraphHelper()
	{
		super();
	}
	
	/**
	 * Create a dataset for the trend graph.
	 * 
	 * @param project Jenkins Project.
	 * @return Graph data.
	 */
	public static CategoryDataset createDataSetForProject( AbstractProject<?,?> project ) 
	{
		final List<Number> values = new ArrayList<Number>();
		final List<String> rows = new ArrayList<String>();
		final List<NumberOnlyBuildLabel> columns = new ArrayList<NumberOnlyBuildLabel>();

		for ( 
				AbstractBuild<?, ?> build = project.getLastBuild(); 
				build != null; 
				build = build.getPreviousBuild() ) 
		{
			final TestLinkBuildAction action = build.getAction( TestLinkBuildAction.class );

			Number testsTotal 	= 0;
			Number testsPassed	= 0;
			Number testsFailed	= 0;
			Number testsBlocked = 0;
			
			if ( action != null ) 
			{
				testsTotal = action.getResult().getReport().getTestsTotal();
				testsPassed = action.getResult().getReport().getTestsPassed();
				testsFailed = action.getResult().getReport().getTestsFailed();
				testsBlocked = action.getResult().getReport().getTestsBlocked();
			}

			// default 'zero value' must be set over zero to circumvent
			// JFreeChart stacked area rendering problem with zero values
			
			if ( testsTotal.intValue() < 1 )
			{
				testsTotal = 0.01f;
			}
			
			if ( testsPassed.intValue() < 1 )
			{
				testsPassed = 0.01f;
			}
			
			if ( testsFailed.intValue() < 1 )
			{
				testsFailed = 0.01f;
			}
			
			if ( testsBlocked.intValue() < 1 )
			{
				testsBlocked = 0.01f;
			}

			final ChartUtil.NumberOnlyBuildLabel label = 
				new ChartUtil.NumberOnlyBuildLabel( build );
			
			values.add( testsFailed );
			rows.add( Messages.ChartUtil_FailedTests() );
			columns.add( label );
			
			values.add( testsPassed );
			rows.add( Messages.ChartUtil_PassedTests() );
			columns.add( label );

			values.add( testsTotal );
			rows.add( Messages.ChartUtil_TotalTests() );
			columns.add( label );
			
			values.add( testsBlocked );
			rows.add( Messages.ChartUtil_BlockedTests() );
			columns.add( label );
		}

		// Code from DataSetBuilder, reversed row order for passed tests to go
		// first into dataset for nicer order when rendered in chart
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		final TreeSet<String> rowSet = new TreeSet<String>(rows);
		final TreeSet<ChartUtil.NumberOnlyBuildLabel> colSet = new TreeSet<ChartUtil.NumberOnlyBuildLabel>(
				columns);

		final Comparable<?>[] _rows = rowSet.toArray(new Comparable[rowSet.size()]);
		final Comparable<?>[] _cols = colSet.toArray(new Comparable[colSet.size()]);

		// insert rows and columns in the right order, reverse rows
		
		for (int i = _rows.length - 1; i >= 0; i--)
		{
			dataset.setValue(null, _rows[i], _cols[0]);
		}
		
		for (Comparable<?> c : _cols)
		{
			dataset.setValue(null, _rows[0], c);
		}

		for (int i = 0; i < values.size(); i++)
		{
			dataset.addValue(values.get(i), rows.get(i), columns.get(i));
		}
		
		return dataset;
	}
	
}
