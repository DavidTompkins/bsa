//////////////////////////////////////////////////////////////////
//                                                              //
// BoundedRandomSourceImpl - manages a set of random samples    //
//                           from another source                //
//                                                              //
// David Tompkins - 8/27/2007                                   //
//                                                              //
// http://dt.org/                                               //
//                                                              //
// Copyright (c) 2007 by David Tompkins.                        //
//                                                              //
//////////////////////////////////////////////////////////////////
//                                                              //
// This program is free software; you can redistribute it       //
// and/or modify it under the terms of the GNU General Public   //
// License as published by the Free Software Foundation.        //
//                                                              //
// This program is distributed in the hope that it will be      //
// useful, but WITHOUT ANY WARRANTY; without even the implied   //
// warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR      //
// PURPOSE. See the GNU General Public License for more details //
//                                                              //
// You should have received a copy of the GNU General Public    //
// License along with this program; if not, write to the Free   //
// Software Foundation, Inc., 59 Temple Place, Suite 330,       //
// Boston, MA 02111-1307 USA                                    //
//                                                              //
//////////////////////////////////////////////////////////////////

package org.dt.bsa.data.impl;

import org.dt.bsa.data.Source;
import org.dt.bsa.data.Sample;
import org.dt.bsa.data.SourceException;

import java.util.Iterator;

public class BoundedRandomSourceImpl
implements Source
{
  protected Source delegateSource;
  protected int numSamples;

  public BoundedRandomSourceImpl(Source delegateSource, int numSamples)
  {
    this.delegateSource = delegateSource;
    this.numSamples = numSamples;
  }
  
  public int size() { return this.numSamples; }
  
  public Iterator<Sample> iterator() { return new BoundedRandomSourceIterator(this.delegateSource, this.numSamples); }

  public void addSample(Sample sample)
  throws SourceException
  {
    throw new SourceException("BoundedRandomSourceImpl:addSample:BoundedRandomSourceImpl does not support the addSample() operation");
  }

  public Sample getRandomSample() { return this.delegateSource.getRandomSample(); }
  public int getInputDimension() { return this.delegateSource.getInputDimension(); }
  public int getOutputDimension() { return this.delegateSource.getOutputDimension(); }
  public String getSourceDescription() { return "BoundedRandomSource:"+this.delegateSource.getSourceDescription(); }
  public boolean isFalsePositive(Sample actual, Sample predicted) { return false; }
  public void cleanup() { this.delegateSource.cleanup(); }

  class BoundedRandomSourceIterator
  implements Iterator<Sample>
  {
    protected Source source;
    protected int numSamples;
    protected int count;

    public BoundedRandomSourceIterator(Source source, int numSamples)
    {
      this.source = source;
      this.numSamples = numSamples;
      this.count = 0;
    }

    public boolean hasNext()
    {
      return (this.count < this.numSamples);
    }

    public Sample next()
    {
      this.count++;
      return source.getRandomSample();
    }

    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
}
