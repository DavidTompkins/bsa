//////////////////////////////////////////////////////////////////
//                                                              //
// ClassifierSourceImpl - Classifier data source implementation //
//                                                              //
// David Tompkins - 6/27/2007                                   //
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

import org.dt.bsa.data.Sample;
import org.dt.bsa.data.SourceException;

public class ClassifierSourceImpl
extends SourceImpl
{
  public ClassifierSourceImpl(int inputDimension, int outputDimension)
  {
    super(inputDimension, outputDimension);
  }

  public void addSample(Sample sample)
  throws SourceException
  {
    if ((sample.getInputDimension() != this.inputDimension) || (sample.getOutputDimension() != this.outputDimension))
      throw new SourceException("SourceImpl:addSample:sample dimensions do not match source dimensions:("+sample.getInputDimension()+","+sample.getOutputDimension()+")("+this.inputDimension+","+this.outputDimension+")");

    // enforce the classifer property that one and only one of the outputs is 1.0, and all other are 0.0 (sum = 1.0)
    double sum = 0.0;
    for (int i = 0 ; i < sample.getOutputDimension() ; i++)
      sum += sample.getOutputs()[i];
    if (sum != 1.0)
      throw new SourceException("ClassifierSourceImpl:addSample:sum of outputs does not equal 1.0");

    samples.add(sample);
  }

  public int getOutputMaxIndex(Sample sample)
  {
    double max = sample.getOutputs()[0];
    int index = 0;
    for (int i = 1 ; i < sample.getOutputDimension() ; i++)
      if (sample.getOutputs()[i] > max)
      {
	index = i;
	max = sample.getOutputs()[i];
      }
    return index;
  }

  public String getSourceDescription() { return "ClassifierSource"; }
}
