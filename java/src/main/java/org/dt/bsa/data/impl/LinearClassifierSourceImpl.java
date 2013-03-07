//////////////////////////////////////////////////////////////////
//                                                              //
// LinearClassifierSourceImpl                                   //
//                                                              //
// f(x) = x classifier data source implementation               //
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

public class LinearClassifierSourceImpl
extends TimeSeriesClassifierSourceImpl
{
  public LinearClassifierSourceImpl(int numSamples, int frameSize, int forwardOffset, int numClasses)
  throws SourceException
  {
    super(frameSize, numClasses);
    init(numSamples, forwardOffset);
  }

  public double getTimeSeriesValue(int index)
  {
    return (double)(index)/(double)this.numSamples;
  }

  public String getSourceDescription() { return "LinearClassifierSource"; }
}
