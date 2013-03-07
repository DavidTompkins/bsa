//////////////////////////////////////////////////////////////////
//                                                              //
// BatchTrainer - Batch mode Thread runnable network trainer    //
//                                                              //
// David Tompkins - 7/2/2007                                    //
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

package org.dt.bsa.trainer.impl;

import java.util.HashMap;

import org.dt.bsa.network.Network;
import org.dt.bsa.data.Source;
import org.dt.bsa.data.Sample;
import org.dt.bsa.data.impl.SampleImpl;
import org.dt.bsa.data.impl.BoundedRandomSourceImpl;
import org.dt.bsa.trainer.Trainer;
import org.dt.bsa.util.MathUtil;

public class BatchTrainer
extends Trainer
{
  public static final int DEFAULT_TRAINING_SAMPLES = 50000;
  public static final int DEFAULT_PREDICT_SAMPLES = 100;
    
  public static final String TOTAL_PREDICTION_SAMPLES = "totalPredictionSamples";
  public static final String TOTAL_CORRECT_PREDICTIONS = "totalCorrectPredictions";
  public static final String TOTAL_FALSE_POSITIVE = "totalFalsePositive";
  public static final String MEAN_ERROR_CLASS_DISTANCE = "meanErrorClassDistance";
  public static final String MEAN_CORRECT_PROBABILITY = "meanCorrectProbability";
  public static final String MEAN_CORRECT_VARIANCE = "meanCorrectVariance";
  public static final String MEAN_CORRECT_PAIRWISE_VARIANCE = "meanCorrectPairwiseVariance";
  public static final String MEAN_INCORRECT_PROBABILITY = "meanIncorrectProbability";
  public static final String MEAN_INCORRECT_VARIANCE = "meanIncorrectVariance";
  public static final String MEAN_INCORRECT_PAIRWISE_VARIANCE = "meanIncorrectPairwiseVariance";

  protected int numSamples;
  protected int numPredictSamples;

  protected boolean initialized = false;
    
  protected int countCorrect = 0;
  protected int falsePositive = 0;
  protected int classError = 0;
  protected double meanCorrectProbability = 0.0;
  protected double meanCorrectVariance = 0.0;
  protected double meanCorrectPairwiseVariance = 0.0;
  protected double meanIncorrectProbability = 0.0;
  protected double meanIncorrectVariance = 0.0;
  protected double meanIncorrectPairwiseVariance = 0.0;

  public BatchTrainer(Network network, Source source, String label, int numSamples, int numPredictSamples)
  {
    super(network, source, label);
    this.numSamples = numSamples;
    this.numPredictSamples = numPredictSamples;
    this.network.setTrainingDescription("BatchTrainer:"+this.numSamples+":"+this.source.getSourceDescription());
  }

  public BatchTrainer(Network network, Source source, String label)
  {
    this(network, source, label, DEFAULT_TRAINING_SAMPLES, DEFAULT_PREDICT_SAMPLES);
  }

  public BatchTrainer(Network network, Source source, String label, int numSamples)
  {
    this(network, source, label, numSamples, DEFAULT_PREDICT_SAMPLES);
  }
  
  public long train()
  throws Exception
  {
    long start = System.currentTimeMillis();

    /*
    BoundedRandomSourceImpl source = new BoundedRandomSourceImpl(this.getSource(), this.numSamples);
    this.getNetwork().train(source);
    */

    int count = 0;
    while (count++ < this.numSamples)
      this.getNetwork().train(this.getSource().getRandomSample());

    return (System.currentTimeMillis() - start);
  }

  protected void test()
  throws Exception
  {
    for (int i = 0 ; i < this.numPredictSamples ; i++)
    {
      Sample actual = this.getSource().getRandomSample();
      Sample predicted = new SampleImpl();
      predicted.setInputs(actual.getInputs());
      predicted = this.getNetwork().predict(predicted);
      int actualClass = MathUtil.findMaxValueIndex(actual.getOutputs());
      int predictedClass = MathUtil.findMaxValueIndex(predicted.getOutputs());

      if (actualClass == predictedClass)
      {
	countCorrect++;
	meanCorrectProbability += predicted.getOutputs()[MathUtil.findMaxValueIndex(predicted.getOutputs())];
	meanCorrectVariance += MathUtil.variance(predicted.getOutputs());
	meanCorrectPairwiseVariance += MathUtil.pairwiseVariance(predicted.getOutputs(), predictedClass);
      }
      else
      {
	classError += Math.abs(actualClass-predictedClass);
	meanIncorrectProbability += predicted.getOutputs()[MathUtil.findMaxValueIndex(predicted.getOutputs())];
	meanIncorrectVariance += MathUtil.variance(predicted.getOutputs());
	meanIncorrectPairwiseVariance += MathUtil.pairwiseVariance(predicted.getOutputs(), predictedClass);
	falsePositive += (this.getSource().isFalsePositive(actual, predicted) ? 1 : 0);
      }
    }

    this.results.put(TOTAL_PREDICTION_SAMPLES, new Integer(this.numPredictSamples));
    this.results.put(TOTAL_CORRECT_PREDICTIONS, new Integer(countCorrect));
    this.results.put(TOTAL_FALSE_POSITIVE, new Integer(falsePositive));
    this.results.put(MEAN_ERROR_CLASS_DISTANCE, new Double((double)classError/(double)(this.numPredictSamples-countCorrect)));
    this.results.put(MEAN_CORRECT_PROBABILITY, new Double((meanCorrectProbability/(double)countCorrect)));
    this.results.put(MEAN_CORRECT_VARIANCE, new Double((meanCorrectVariance/(double)countCorrect)));
    this.results.put(MEAN_CORRECT_PAIRWISE_VARIANCE, new Double((meanCorrectPairwiseVariance/(double)countCorrect)));
    this.results.put(MEAN_INCORRECT_PROBABILITY, new Double((meanIncorrectProbability/(double)countCorrect)));
    this.results.put(MEAN_INCORRECT_VARIANCE, new Double((meanIncorrectVariance/(double)countCorrect)));
    this.results.put(MEAN_INCORRECT_PAIRWISE_VARIANCE, new Double((meanIncorrectPairwiseVariance/(double)countCorrect)));

    this.initialized = true;
  }

  public String generateReport()
  throws Exception
  {
    if (!this.initialized)
      test();

    StringBuffer sb = new StringBuffer();

    sb.append("-----------------------------------------\n");
    sb.append("BatchTrainer: "+this.getLabel()+"\n");
    sb.append("-----------------------------------------\n");
    sb.append("Network Dimensions: ("+this.getNetwork().getInputDimension()+":"+this.getNetwork().getHiddenDimension()+":"+this.getNetwork().getOutputDimension()+")\n");
    sb.append("-----------------------------------------\n");
    //sb.append(this.getNetwork().toString()\n);
    //sb.append("-----------------------------------------\n");

    sb.append("Total prediction samples:"+this.numPredictSamples+"\n");
    sb.append("Total correct predictions:"+countCorrect+"\n");
    sb.append("Total false positives:"+falsePositive+"\n");
    sb.append("Mean error class distance:"+(double)classError/(double)(this.numPredictSamples-countCorrect)+"\n");
    sb.append("-----------------------------------------\n");
    sb.append("Mean correct probability(1.0):"+(meanCorrectProbability/(double)countCorrect)+"\n");
    sb.append("Mean correct variance(0.004975):"+(meanCorrectVariance/(double)countCorrect)+"\n");
    sb.append("Mean correct pairwise variance(1.0):"+(meanCorrectPairwiseVariance/(double)countCorrect)+"\n");
    sb.append("-----------------------------------------\n");
    sb.append("Mean incorrect probability(1.0):"+(meanIncorrectProbability/(double)(this.numPredictSamples-countCorrect))+"\n");
    sb.append("Mean incorrect variance(0.004975):"+(meanIncorrectVariance/(double)(this.numPredictSamples-countCorrect))+"\n");
    sb.append("Mean incorrect pairwise variance(1.0):"+(meanIncorrectPairwiseVariance/(double)(this.numPredictSamples-countCorrect))+"\n");
    sb.append("-----------------------------------------\n");
    sb.append("Iteration Elapsed Time: "+this.getElapsedTime()+"ms\n");
    sb.append("-----------------------------------------\n");
  
    return sb.toString();
  }
}
