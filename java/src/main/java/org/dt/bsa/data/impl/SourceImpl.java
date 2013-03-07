//////////////////////////////////////////////////////////////////
//                                                              //
// SourceImpl - base data source implementation                 //
//                                                              //
// David Tompkins - 4/26/2007                                   //
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
import org.dt.bsa.util.Random;

import java.util.ArrayList;
import java.util.Iterator;

public class SourceImpl
implements Source
{
  public static final Random random = Random.getInstance();

  protected int inputDimension;
  protected int outputDimension;

  protected ArrayList<Sample> samples = null;

  public SourceImpl(int inputDimension, int outputDimension)
  {
    this.inputDimension = inputDimension;
    this.outputDimension = outputDimension;
    this.samples = new ArrayList();
  }

  public void addSample(Sample sample)
  throws SourceException
  {
    if ((sample.getInputDimension() == this.inputDimension) && (sample.getOutputDimension() == this.outputDimension))
    {
      samples.add(sample);
      return;
    }

    throw new SourceException("SourceImpl:addSample:sample dimensions do not match source dimensions:("+sample.getInputDimension()+","+sample.getOutputDimension()+")("+this.inputDimension+","+this.outputDimension+")");
  }

  public Sample getRandomSample() { return samples.get(random.nextInt(samples.size())); }
  public Iterator<Sample> iterator() { return samples.iterator(); }
  public int size() { return samples.size(); }
  public int getInputDimension() { return this.inputDimension; }
  public int getOutputDimension() { return this.outputDimension; }
  public String getSourceDescription() { return "Source"; }
  public boolean isFalsePositive(Sample actual, Sample predicted) { return false; }
  public void cleanup() { this.samples.clear(); }
}
