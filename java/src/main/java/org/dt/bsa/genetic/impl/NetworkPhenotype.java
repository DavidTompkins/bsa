//////////////////////////////////////////////////////////////////
//                                                              //
// NetworkPhenotype - Implementation of the Network phenotype   //
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

import java.util.HashMap;

import javax.persistence.*;

import org.dt.bsa.genetic.Phenotype;
import org.dt.bsa.genetic.EvolutionException;
import org.dt.bsa.network.Network;
import org.dt.bsa.network.NetworkException;
import org.dt.bsa.network.impl.NativeNetworkImpl;
import org.dt.bsa.network.impl.BaseNetworkImpl;
import org.dt.bsa.data.Source;
import org.dt.bsa.data.impl.SinClassifierSourceImpl;
import org.dt.bsa.data.impl.TickerClassifierSourceImpl;
import org.dt.bsa.trainer.Trainer;
import org.dt.bsa.trainer.impl.BatchTrainer;
import org.dt.bsa.util.HibernateUtil;
import org.dt.bsa.util.BSAException;

@Entity
@DiscriminatorValue("network_phenotype")
public class NetworkPhenotype
extends BasePhenotypeImpl
implements Phenotype
{
  protected Network network;
  protected Source source;
  protected Trainer trainer;
  protected boolean trained;

  public NetworkPhenotype() { super(); }

  public NetworkPhenotype(NetworkGenome genome)
  throws EvolutionException
  {
    super("NetworkPhenotype");

    try
    {
      this.source = genome.sourceFactory.getSource(genome);
      this.network = new NativeNetworkImpl(genome.getInputDimension(), genome.getHiddenDimension(), genome.getOutputDimension(), genome.getLearningRate());
      this.trainer = new BatchTrainer(this.network, this.source, "NetworkPhenotype["+genome.toString()+"]", genome.getNumTrainingSamples(), genome.getNumPredictSamples());
      this.trained = false;
    }
    catch (Exception e)
    {
      throw new EvolutionException(e.getMessage());
    }
  }
  
  public NetworkPhenotype(int id)
  throws BSAException
  {
    super(id);
  }

  @Transient
  public Network getNetwork()
  throws BSAException
  {
    if (this.network != null)
      return this.network;

    // load the network from the db
    HashMap<String,String> args = new HashMap<String,String>();
    args.put("phenotype_id", new Integer(this.id).toString());
    this.network = (NativeNetworkImpl)HibernateUtil.query("from org.dt.bsa.network.impl.BaseNetworkImpl as network where network.phenotypeId = :phenotype_id", args).get(0);
    return this.network;
  }

  @Transient
  public float getFitness()
  throws EvolutionException, BSAException
  {
    if (!this.trained)
    {
      try
      {
        this.trainer.run();
        this.network.shutdown();
      }
      catch (Exception e)
      {
        throw new EvolutionException(e.getMessage());
      }

      HashMap results = this.trainer.getResults();

      this.setTotalSamples(((Integer)results.get(BatchTrainer.TOTAL_PREDICTION_SAMPLES)).intValue());
      this.setTotalCorrect(((Integer)results.get(BatchTrainer.TOTAL_CORRECT_PREDICTIONS)).intValue());
      this.setTotalFalsePositive(((Integer)results.get(BatchTrainer.TOTAL_FALSE_POSITIVE)).intValue());
      this.setMeanClassError(((Double)results.get(BatchTrainer.MEAN_ERROR_CLASS_DISTANCE)).floatValue());
      this.setMeanCorrectProbability(((Double)results.get(BatchTrainer.MEAN_CORRECT_PROBABILITY)).floatValue());
      this.setMeanCorrectVariance(((Double)results.get(BatchTrainer.MEAN_CORRECT_VARIANCE)).floatValue());
      this.setMeanCorrectPairwiseVariance(((Double)results.get(BatchTrainer.MEAN_CORRECT_PAIRWISE_VARIANCE)).floatValue());
      this.setMeanIncorrectProbability(((Double)results.get(BatchTrainer.MEAN_INCORRECT_PROBABILITY)).floatValue());
      this.setMeanIncorrectVariance(((Double)results.get(BatchTrainer.MEAN_INCORRECT_VARIANCE)).floatValue());
      this.setMeanIncorrectPairwiseVariance(((Double)results.get(BatchTrainer.MEAN_INCORRECT_PAIRWISE_VARIANCE)).floatValue());
      
      this.trained = true;
    }

    return ((float)this.getTotalCorrect()) / ((float)this.getTotalSamples());
  }

  public void cleanup()
  {
    // Help for GC
    this.trainer.cleanup();
    this.source.cleanup();
    this.trainer = null;
    this.network = null;
    this.source = null;
  }

  public void save()
  throws BSAException
  {
    super.save();
    this.network.setPhenotype(this);
    HibernateUtil.save(this.network);
  }
}
