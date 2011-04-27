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
package hudson.plugins.testlink.parser.tap;

import hudson.plugins.testlink.parser.Parser;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.lang.NotImplementedException;

import br.eti.kinoshita.tap4j.consumer.TapConsumer;
import br.eti.kinoshita.tap4j.consumer.TapConsumerException;
import br.eti.kinoshita.tap4j.consumer.TapConsumerFactory;
import br.eti.kinoshita.tap4j.model.TestSet;
import br.eti.kinoshita.tap4j.parser.ParserException;

/**
 * The TAP Parser.
 * 
 * @author Bruno P. Kinoshita
 * @since 2.0
 */
public class TAPParser 
extends Parser<TestSet>
{

	private static final long serialVersionUID = 5036738457730949105L;
	
	/**
	 * Default constructor. Initializes the TAP Consumer.
	 */
	public TAPParser() 
	{
		super();
	}

	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.parser.Parser#parse(java.io.InputStream)
	 */
	@Override
	public TestSet parse( InputStream inputStream ) 
	throws ParserException
	{
		throw new NotImplementedException( "TAP parser does not parse Input Streams, only files." );
	}
	
	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.parser.Parser#parse(java.io.File)
	 */
	@Override
	public TestSet parse( File file ) 
	throws ParserException 
	{
		TestSet testSet = null;
		
		TapConsumer tapConsumer = TapConsumerFactory.makeTap13YamlConsumer();
		
		try
		{
			testSet = tapConsumer.load( file );
		} 
		catch ( TapConsumerException tce )
		{
			throw new ParserException( "Failed to parse TAP file '"+file+"'.", tce );
		}
		
		return testSet;
	}
	
	/* (non-Javadoc)
	 * @see hudson.plugins.testlink.parser.Parser#getName()
	 */
	@Override
	public String getName() 
	{
		return "TAP";
	}

}
