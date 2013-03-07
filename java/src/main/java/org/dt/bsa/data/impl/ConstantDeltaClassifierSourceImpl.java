//////////////////////////////////////////////////////////////////
//                                                              //
// ConstantDeltaClassifierSourceImpl                            //
//                                                              //
// f(x): f(x+1) = f(x) + delta for constant delta               //
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

public class ConstantDeltaClassifierSourceImpl
extends TimeSeriesClassifierSourceImpl
{
  public static final double CONSTANT_DELTA = 0.10;
  public static final double INITIAL_VALUE = 0.10;

  public ConstantDeltaClassifierSourceImpl(int numSamples, int frameSize, int forwardOffset, int numClasses)
  throws SourceException
  {
    super(frameSize, numClasses);
    init(numSamples, forwardOffset);
  }

  public double getTimeSeriesValue(int index)
  {
    if (index == 0)
      return INITIAL_VALUE;

    return ((double)1.0 + CONSTANT_DELTA)*getTimeSeriesValue(index-1);
  }

  public String getSourceDescription() { return "ConstantDeltaClassifierSource"; }
}
