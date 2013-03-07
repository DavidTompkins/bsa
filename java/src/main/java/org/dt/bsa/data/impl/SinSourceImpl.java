//////////////////////////////////////////////////////////////////
//                                                              //
// SinSourceImpl - f(x) = sin(x) data source implementation     //
//                                                              //
// David Tompkins - 6/26/2007                                   //
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

import org.dt.bsa.data.SourceException;

public class SinSourceImpl
extends SourceImpl
{
  public SinSourceImpl(int numSamples, int frameSize, int forwardOffset)
  throws SourceException
  {
    super(frameSize, 1);

    for (int i = 0 ; i < numSamples ; i++)
    {
      double[] inputs = new double[frameSize];
      double[] outputs = new double[1];

      for (int ii = 0 ; ii < frameSize ; ii++)
	inputs[ii] = Math.sin((double)(i+ii)/(double)numSamples*2.0*Math.PI);

      outputs[0] = Math.sin((double)(i+frameSize+forwardOffset)/(double)numSamples*2.0*Math.PI);

      this.addSample(new SampleImpl(inputs, outputs));
    }
  }

  public String getSourceDescription() { return "SinSource"; }
}
