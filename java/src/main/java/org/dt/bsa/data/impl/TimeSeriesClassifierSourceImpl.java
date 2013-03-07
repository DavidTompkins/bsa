//////////////////////////////////////////////////////////////////
//                                                              //
// TimeSeriesClassifierSourceImpl                               //
//                                                              //
// Time Series Classifier data source implementation            //
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dt.bsa.data.Sample;
import org.dt.bsa.data.SourceException;
import org.dt.bsa.util.MathUtil;

public abstract class TimeSeriesClassifierSourceImpl
extends ClassifierSourceImpl
{
  public static final double DEFAULT_MAX_PERCENT = 1.0; // 100%

  final Logger log = LoggerFactory.getLogger(TimeSeriesClassifierSourceImpl.class);

  protected int numSamples;
  protected int frameSize;
  protected int forwardOffset;
  protected int numClasses;
  protected double maxPercent;

  public TimeSeriesClassifierSourceImpl(int frameSize, int numClasses, double maxPercent)
  throws SourceException
  {
    super(frameSize, numClasses);
    this.frameSize = frameSize;
    this.numClasses = numClasses;
    this.maxPercent = maxPercent;
  }
  
  public TimeSeriesClassifierSourceImpl(int frameSize, int numClasses)
  throws SourceException
  {
    this(frameSize, numClasses, DEFAULT_MAX_PERCENT);
  }

  protected void init(int numSamples, int forwardOffset)
  throws SourceException
  {
    this.numSamples = numSamples;
    this.forwardOffset = forwardOffset;

    for (int i = 1 ; i < this.numSamples-(this.frameSize-1+this.forwardOffset) ; i++)
      this.addSample(this.getSample(i, true));
  }

  protected Sample getSample(int index, boolean includeOutputs)
  {
    double[] inputs = new double[this.frameSize];
    double[] inputsDelta = new double[this.frameSize];
    double[] outputs = new double[this.numClasses];

    for (int i = 0 ; i < this.frameSize ; i++)
    {
      inputs[i] = getTimeSeriesValue(index+i);
      inputsDelta[i] = ((i == 0) ? getDelta(inputs[i], getTimeSeriesValue(index+i-1)) : getDelta(inputs[i], inputs[i-1]));
    }

    if (!includeOutputs)
      return new SampleImpl(inputsDelta, null);

    int classIndex = getOutputClassForValue(getTimeSeriesValue(index+this.frameSize-1+this.forwardOffset), inputs);

    for (int i = 0 ; i < this.numClasses ; i++)
      outputs[i] = 0.0;
    outputs[classIndex] = 1.0;
      
    return new SampleImpl(inputsDelta, outputs);
  }

  public abstract double getTimeSeriesValue(int index);

  public int getOutputClassForValue(double outputValue, double[] inputs)
  {
    // The default behavior is to determine the % gain/loss of the outputValue
    // relative to the final input value. The space from [-maxPercent%,maxPercent%] is partitioned
    // into N bins, where N == numClasses, and the call membership is determined from
    // the bin in which the % change resides.

    double delta = getDelta(outputValue, inputs[inputs.length-1]);
   
    int classIndex = (int)Math.floor((delta - -this.maxPercent) / (2.0 * this.maxPercent / (double)numClasses));

    // catch corner case when delta == 1.0
    if (classIndex == numClasses) classIndex -=1;

    //log.info("-----------------------------");
    //log.info("getOutputClassForValue:inputs:"+Log.arrayToString(inputs));
    //log.info("getOutputClassForValue:output:"+outputValue);
    //log.info("getOutputClassForValue:delta:"+delta);
    //log.info("getOutputClassForValue:classIndex:"+classIndex);
    //log.info("-----------------------------");

    return classIndex;
  }

  public double getPercentChangeForOutputClass(int outputIndex)
  {
    return (-this.maxPercent + ((2.0 * this.maxPercent) * ((double)outputIndex / (double)(numClasses-1))));
  }

  public boolean isFalsePositive(Sample actual, Sample predicted)
  {
    int actualClass = MathUtil.findMaxValueIndex(actual.getOutputs());
    int predictedClass = MathUtil.findMaxValueIndex(predicted.getOutputs());
    int median = numClasses / 2;

    if (((actualClass < median) && (predictedClass >= median)) ||
        ((actualClass >= median) && (predictedClass < median)))
      return true;

    return false;
  }

  protected double getDelta(double outputValue, double inputValue)
  {
    double delta = (outputValue - inputValue) / inputValue;
    
    // delta should be constrained to [-maxPercent,maxPercent]
    if (delta > this.maxPercent) delta = this.maxPercent;
    if (delta < -this.maxPercent) delta = -this.maxPercent;

    return delta;
  }

  public String getSourceDescription() { return "TimeSeriesClassifierSource"; }
}
