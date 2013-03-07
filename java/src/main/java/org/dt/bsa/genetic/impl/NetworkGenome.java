//////////////////////////////////////////////////////////////////
//                                                              //
// NetworkGenome - Implementation of a Network Genome that      //
//                 encodes the configuration of the network     //
//                 within a BinaryGenome.                       //
//                                                              //
// David Tompkins - 8/9/2007                                    //
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

import org.dt.bsa.genetic.Genome;
import org.dt.bsa.genetic.Generation;
import org.dt.bsa.genetic.Phenotype;
import org.dt.bsa.genetic.EvolutionException;
import org.dt.bsa.genetic.SourceFactory;
import org.dt.bsa.trainer.impl.BatchTrainer;

public class NetworkGenome
extends BinaryGenome
{
  public static final double[] learning_rates = { 0.2, 0.3, 0.4, 0.5 };

  // inputDimenion: 6 bits (1-64)
  public static final int INPUT_DIMENSION_OFFSET = 0;
  public static final int INPUT_DIMENSION_LENGTH = 6;

  // hiddenDimension: 11 bits (1-2048)
  public static final int HIDDEN_DIMENSION_OFFSET = INPUT_DIMENSION_OFFSET+INPUT_DIMENSION_LENGTH;
  public static final int HIDDEN_DIMENSION_LENGTH = 11;

  // outputDimension: 200 (200 1% bin)
  public static final int OUTPUT_DIMENSION = 200;

  // numTrainingSamples: 10000 * 4 bits (1-16)
  public static final int NUM_TRAINING_SAMPLES_OFFSET = HIDDEN_DIMENSION_OFFSET+HIDDEN_DIMENSION_LENGTH;
  public static final int NUM_TRAINING_SAMPLES_LENGTH = 4;

  // forwardOffset: 3 bits (1-8)
  //public static final int FORWARD_OFFSET_OFFSET = NUM_TRAINING_SAMPLES_OFFSET+NUM_TRAINING_SAMPLES_LENGTH;
  //public static final int FORWARD_OFFSET_LENGTH = 3;
  public static final int FORWARD_OFFSET = 5;

  // learning rate: 2 bits (0-3, for learning rates [0.2,0.3,0.4,0.5]
  public static final int LEARNING_RATE_OFFSET = HIDDEN_DIMENSION_OFFSET+HIDDEN_DIMENSION_LENGTH+NUM_TRAINING_SAMPLES_LENGTH;
  public static final int LEARNING_RATE_LENGTH = 2;

  // total genome length in bits
  //public static final int GENOME_LENGTH = INPUT_DIMENSION_LENGTH + HIDDEN_DIMENSION_LENGTH + NUM_TRAINING_SAMPLES_LENGTH + FORWARD_OFFSET_LENGTH;
  //public static final int GENOME_LENGTH = INPUT_DIMENSION_LENGTH + HIDDEN_DIMENSION_LENGTH + NUM_TRAINING_SAMPLES_LENGTH;
  public static final int GENOME_LENGTH = INPUT_DIMENSION_LENGTH + HIDDEN_DIMENSION_LENGTH + NUM_TRAINING_SAMPLES_LENGTH + LEARNING_RATE_LENGTH;

  // numInputSamples (SinSource parameter)
  public static final int NUM_INPUT_SAMPLES = 180;

  protected int numPredictSamples;
  protected SourceFactory sourceFactory = null;
  protected Phenotype phenotype = null;

  public NetworkGenome(SourceFactory sourceFactory, int numPredictSamples)
  {
    super(GENOME_LENGTH);
    randomizeBits();
    this.numPredictSamples = numPredictSamples;
    this.sourceFactory = sourceFactory;
  }
  
  public NetworkGenome(SourceFactory sourceFactory)
  {
    this(sourceFactory, BatchTrainer.DEFAULT_PREDICT_SAMPLES);
  }

  public NetworkGenome(NetworkGenome genome)
  {
    super(genome);
    this.numPredictSamples = genome.numPredictSamples;
    this.sourceFactory = genome.sourceFactory;
  }

  public Genome clone()
  {
    return new NetworkGenome(this);
  }

  public Phenotype createPhenotype()
  throws EvolutionException
  {
    if (this.phenotype != null)
      return this.phenotype;

    this.phenotype = new NetworkPhenotype(this);
    return this.phenotype;
  }

  public int getInputDimension() { return this.getNumberForRange(INPUT_DIMENSION_OFFSET, INPUT_DIMENSION_LENGTH)+1; }
  public int getHiddenDimension() { return this.getNumberForRange(HIDDEN_DIMENSION_OFFSET, HIDDEN_DIMENSION_LENGTH)+1; }
  public int getOutputDimension() { return OUTPUT_DIMENSION; }
  public int getNumTrainingSamples() { return (this.getNumberForRange(NUM_TRAINING_SAMPLES_OFFSET, NUM_TRAINING_SAMPLES_LENGTH)+1)*10000; }
  public double getLearningRate() { return learning_rates[this.getNumberForRange(LEARNING_RATE_OFFSET, LEARNING_RATE_LENGTH)]; }
  public int getForwardOffset() { return FORWARD_OFFSET; }
  //public int getForwardOffset() { return this.getNumberForRange(FORWARD_OFFSET_OFFSET, FORWARD_OFFSET_LENGTH)+1; }
  public int getFrameSize() { return this.getInputDimension(); }
  public int getNumClasses() { return this.getOutputDimension(); }
  public int getNumInputSamples() { return NUM_INPUT_SAMPLES; }
  public int getNumPredictSamples() { return this.numPredictSamples; }

  public String toString()
  {
    return "NetworkGenome:\n"+
            "inputDimension    :"+getInputDimension()+"\n"+
	    "hiddenDimension   :"+getHiddenDimension()+"\n"+
	    "outputDimension   :"+getOutputDimension()+"\n"+
	    "numTrainingSamples:"+getNumTrainingSamples()+"\n"+
	    "forwardOffset     :"+getForwardOffset()+"\n"+
	    "frameSize         :"+getFrameSize()+"\n"+
	    "numClasses        :"+getNumClasses()+"\n"+
	    "learningRate      :"+getLearningRate()+"\n"+
	    "numInputSamples   :"+getNumInputSamples()+"\n"+
	    "numPredictSamples :"+getNumPredictSamples()+"\n"+
	    "BinaryGenome      :"+super.toString()+"\n"+
	    "Object Hash       :"+this.hashCode()+"\n";
  }
}
