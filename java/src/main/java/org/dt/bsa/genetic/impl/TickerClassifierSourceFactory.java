//////////////////////////////////////////////////////////////////
//                                                              //
// TickerClassifierSourceFactory - factory for                  //
//                                 TickerClassifierSource       //
//                                 objects                      //
//                                                              //
// David Tompkins - 10/28/2007                                  //
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

package org.dt.bsa.genetic.impl;

import org.dt.bsa.data.Source;
import org.dt.bsa.data.SourceException;
import org.dt.bsa.data.impl.TickerClassifierSourceImpl;
import org.dt.bsa.data.impl.StandardizedInputsTransform;
import org.dt.bsa.genetic.SourceFactory;
import org.dt.bsa.genetic.Genome;
import org.dt.bsa.util.BSAException;

public class TickerClassifierSourceFactory
implements SourceFactory
{
  public static final double CLASSIFIER_MAX_PERCENT = 0.2;

  protected String ticker;

  public TickerClassifierSourceFactory(String ticker) { this.ticker = ticker; }

  public Source getSource(Genome genome)
  throws SourceException
  {
    if (!(genome instanceof NetworkGenome))
      throw new SourceException("TickerClassifierSourceFactory:getSource():genome parameter must be a subclass of NetworkGenome");

    NetworkGenome g = (NetworkGenome)genome;
    try
    {
      return (new StandardizedInputsTransform()).transform(new TickerClassifierSourceImpl(this.ticker, g.getFrameSize(), g.getForwardOffset(), g.getNumClasses()));
      //return (new StandardizedInputsTransform()).transform(new TickerClassifierSourceImpl(this.ticker, g.getFrameSize(), g.getForwardOffset(), g.getNumClasses(), CLASSIFIER_MAX_PERCENT));
    }
    catch (BSAException e)
    {
      throw new SourceException(e.getMessage());
    }
  }
}
