//////////////////////////////////////////////////////////////////
//                                                              //
// TickerClassifierSourceImpl                                   //
//                                                              //
// Ticker data classifier data source implementation            //
//                                                              //
// David Tompkins - 10/25/2007                                  //
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

import java.util.Set;
import java.util.Date;

import org.dt.bsa.data.Sample;
import org.dt.bsa.data.SourceException;
import org.dt.bsa.util.HibernateUtil;
import org.dt.bsa.util.BSAException;

public class TickerClassifierSourceImpl
extends TimeSeriesClassifierSourceImpl
{
  protected String ticker;
  protected TickerSampleImpl[] tickerSamples;

  public TickerClassifierSourceImpl(String ticker, int frameSize, int forwardOffset, int numClasses)
  throws SourceException, BSAException
  {
    super(frameSize, numClasses);
    this.ticker = ticker;
    this.tickerSamples = TickerImpl.getTickerSamplesArray(ticker);
    init(this.tickerSamples.length, forwardOffset);
    //log.info("TickerClassifierSourceImpl:"+ticker+":built "+samples.size()+" samples from "+numSamples+" data points; frameSize="+frameSize+", forwardOffset="+forwardOffset);
  }

  public TickerClassifierSourceImpl(String ticker, int frameSize, int forwardOffset, int numClasses, double maxPercent)
  throws SourceException, BSAException
  {
    super(frameSize, numClasses, maxPercent);
    this.ticker = ticker;
    this.tickerSamples = TickerImpl.getTickerSamplesArray(ticker);
    init(this.tickerSamples.length, forwardOffset);
    //log.info("TickerClassifierSourceImpl:"+ticker+":built "+samples.size()+" samples from "+numSamples+" data points; frameSize="+frameSize+", forwardOffset="+forwardOffset);
  }

  public double getTimeSeriesValue(int index)
  {
    return this.tickerSamples[index].getAdjClosePrice();
  }

  public Sample getInputSampleForDate(Date date)
  {
    for (int i = 1+this.frameSize ; i < this.tickerSamples.length ; i++)
    {
      if (date.compareTo(this.tickerSamples[i].getSampleDate()) < 0)
	return getSample(i-1-this.frameSize, false); // found first sample after specified date
    }
    return getSample(this.tickerSamples.length-this.frameSize, false); // return last possible sample
  }

  public Date getInputSampleDateForDate(Date date)
  {
    for (int i = 1+this.frameSize ; i < this.tickerSamples.length ; i++)
    {
      if (date.compareTo(this.tickerSamples[i].getSampleDate()) < 0)
	return this.tickerSamples[i].getSampleDate();
    }
    return this.tickerSamples[this.tickerSamples.length-1].getSampleDate();
  }

  public Date getDateForForwardOffset(Date date, int offset)
  {
    for (int i = 0 ; i < this.tickerSamples.length ; i++)
    {
      if (date.compareTo(this.tickerSamples[i].getSampleDate()) < 0)
      {
	if ((i+offset) >= this.tickerSamples.length)
	  return null;

	return this.tickerSamples[i+offset].getSampleDate();
      }
    }
    return null;
  }

  public String getSourceDescription() { return "TickerClassifierSource:"+ticker; }

  public TickerSampleImpl[] getTickerSamples() { return this.tickerSamples; }
}
