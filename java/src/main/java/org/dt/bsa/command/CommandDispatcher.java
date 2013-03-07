//////////////////////////////////////////////////////////////////
//                                                              //
// CommandDispatcher - command line dispatcher                  //
//                                                              //
// David Tompkins - 11/3/2007                                   //
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

package org.dt.bsa.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dt.bsa.data.Sample;
import org.dt.bsa.data.impl.TickerClassifierSourceImpl;
import org.dt.bsa.data.impl.StandardizedInputsTransform;
import org.dt.bsa.network.Network;
import org.dt.bsa.network.impl.NativeNetworkImpl;
import org.dt.bsa.genetic.EvolutionManager;
import org.dt.bsa.genetic.impl.TickerClassifierSourceFactory;
import org.dt.bsa.genetic.impl.ThreadedEvolutionManager;
import org.dt.bsa.genetic.impl.NetworkGenomeFactory;
import org.dt.bsa.genetic.impl.NetworkGenome;
import org.dt.bsa.genetic.impl.NetworkPhenotype;
import org.dt.bsa.util.HibernateUtil;

public class CommandDispatcher
{
  protected int DEFAULT_NUM_THREADS = 7;

  final Logger log = LoggerFactory.getLogger(CommandDispatcher.class);

  public CommandDispatcher() { }

  protected void setUp()
  throws Exception
  {
    HibernateUtil.init();
  }

  protected void tearDown()
  throws Exception
  {
    HibernateUtil.shutdown();
  }

  protected void evolve(String ticker)
  throws Exception
  {
    int numThreads = DEFAULT_NUM_THREADS;
    EvolutionManager ev = new ThreadedEvolutionManager(new NetworkGenomeFactory(new TickerClassifierSourceFactory(ticker)), numThreads, "evolve:TickerClassifierSource:"+ticker);
    ev.evolve();
  }
  
  protected void completeEvolution(int id)
  throws Exception
  {
    ThreadedEvolutionManager em = new ThreadedEvolutionManager(id);
    em.setStatus(ThreadedEvolutionManager.STATUS_COMPLETE_REQUESTED);
    em.save();
  }
  
  protected void generalize(int id, String ticker)
  throws Exception
  {
    ThreadedEvolutionManager em = new ThreadedEvolutionManager(id);
    NetworkPhenotype phenotype = (NetworkPhenotype)em.getMostFitPhenotype();
    Network network = phenotype.getNetwork();
    System.out.println("Evolution:"+id+":phenotype:"+phenotype.getId()+":network:"+network.getId());
    TickerClassifierSourceImpl source = (TickerClassifierSourceImpl)(new StandardizedInputsTransform()).transform(new TickerClassifierSourceImpl(ticker, network.getInputDimension(), NetworkGenome.FORWARD_OFFSET, network.getOutputDimension()));
    Sample sample = source.getInputSampleForDate(phenotype.getCreatedAt());
    sample = network.predict(sample);
    int maxIndex = source.getOutputMaxIndex(sample);
    double percentChange = source.getPercentChangeForOutputClass(maxIndex) * 100.0;
    System.out.println("Prediction from "+source.getInputSampleDateForDate(phenotype.getCreatedAt())+" for "+source.getDateForForwardOffset(phenotype.getCreatedAt(), NetworkGenome.FORWARD_OFFSET)+":percent change:"+percentChange+"%:probability:"+sample.getOutputs()[maxIndex]);
  }

  public void printUsage()
  {
    System.out.println("Usage: CommandDispatcher command arg1 arg2 ...");
    System.out.println("");
    System.out.println("Commands:");
    System.out.println("");
    System.out.println("evolve -- Initiates an evolution. Requires a ticker symbol string.");
    System.out.println("complete -- Requests completion of a running evolution. Requires an evolution ID.");
    System.out.println("generalize -- Displays generalized predictions for a completed evolution. Requires an evolution ID and a ticker symbol string.");
  }

  public static void main(String[] args)
  throws Exception
  {
    CommandDispatcher cd = new CommandDispatcher();
    cd.setUp();

    if (args.length == 0)
    {
      cd.printUsage();
      cd.tearDown();
      System.exit(1);
    }

    if ((args[0].equals("evolve") || args[0].equals("e")) && (args.length == 2))
    {
       cd.evolve(args[1]);
    }
    else if ((args[0].equals("complete") || args[0].equals("c")) && (args.length == 2))
    {
       cd.completeEvolution(Integer.parseInt(args[1]));
    }
    else if ((args[0].equals("generalize") || args[0].equals("g")) && (args.length == 3))
    {
       cd.generalize(Integer.parseInt(args[1]), args[2]);
    }
    else
    {
      cd.printUsage();
      cd.tearDown();
      System.exit(1);
    }

    cd.tearDown();
  }
}
