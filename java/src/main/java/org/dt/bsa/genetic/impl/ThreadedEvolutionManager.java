//////////////////////////////////////////////////////////////////
//                                                              //
// ThreadedEvolutionManager - Multithread EvolutionManager      //
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

import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.CountDownLatch;

import javax.persistence.*;

import org.dt.bsa.genetic.Genome;
import org.dt.bsa.genetic.Phenotype;
import org.dt.bsa.genetic.GenomeFactory;
import org.dt.bsa.genetic.Generation;
import org.dt.bsa.genetic.GenerationFactory;
import org.dt.bsa.genetic.EvolutionManager;
import org.dt.bsa.genetic.EvolutionException;
import org.dt.bsa.util.BSAException;

@Entity
@DiscriminatorValue("threaded")
public class ThreadedEvolutionManager
extends BasicEvolutionManager
{
  protected int numThreads;
  protected float fitnessHighest;
  protected int indexMostFit;

  public ThreadedEvolutionManager() { super(); }

  public ThreadedEvolutionManager(int id)
  throws BSAException
  {
    super(id);
  }

  public ThreadedEvolutionManager(GenomeFactory genomeFactory, int numThreads, String description)
  {
    super(genomeFactory, description);
    this.numThreads = numThreads;
  }

  public ThreadedEvolutionManager(GenomeFactory genomeFactory, GenerationFactory generationFactory, int numThreads, String description)
  {
    super(genomeFactory, generationFactory, description);
    this.numThreads = numThreads;
  }

  public int evaluatePhenotypes(Generation generation)
  throws EvolutionException
  {
    this.indexMostFit = -1;
    this.fitnessHighest = Phenotype.FITNESS_MINIMUM;
    ExecutorService service = Executors.newFixedThreadPool(numThreads);
    CountDownLatch countDownLatch = new CountDownLatch(this.population.length);

    for (int i = 0 ; i < population.length ; i++)
      service.execute(new CreatePhenotypeWorker(this.population, this.phenotypes, this.fitness, i, countDownLatch, this));
      
    try { countDownLatch.await(); }
    catch (InterruptedException e) { throw new EvolutionException("ThreadedEvolutionManager:await():interrupted"); }
    service.shutdown();
    return indexMostFit;
  }

  protected synchronized boolean isMostFit(int index, float fitness)
  {
    log.info("ThreadedEvolutionManager:isMostFit:index="+index+", fitness="+fitness+", indexMostFit="+indexMostFit+", fitnessHighest="+this.fitnessHighest);

    if (fitness > fitnessHighest)
    {
      this.fitnessHighest = fitness;
      
      // allow GC to clean up phenotypes that we don't need to keep (like previous most fit phenotype)
      if (this.indexMostFit != -1)
	this.phenotypes[this.indexMostFit] = null;

      this.indexMostFit = index;
      log.info("ThreadedEvolutionManager:isMostFit:updated, indexMostFit="+indexMostFit+", fitnessHighest="+fitnessHighest);
      return true;
    }

    return false;
  }
  
  public class CreatePhenotypeWorker
  implements Runnable
  {
    protected Genome[] population;
    protected Phenotype[] phenotypes;
    protected float[] fitness;
    protected int index;
    protected CountDownLatch countDownLatch;
    protected ThreadedEvolutionManager threadedEvolutionManager;

    public CreatePhenotypeWorker(Genome[] population, Phenotype[] phenotypes, float[] fitness, int index, CountDownLatch countDownLatch, ThreadedEvolutionManager threadedEvolutionManager)
    {
      this.population = population;
      this.phenotypes = phenotypes;
      this.fitness = fitness;
      this.index = index;
      this.countDownLatch = countDownLatch;
      this.threadedEvolutionManager = threadedEvolutionManager;
    }

    public void run()
    {
      try
      {
        phenotypes[index] = population[index].createPhenotype();
        fitness[index] = phenotypes[index].getFitness();

	threadedEvolutionManager.log.info("CreatePhenotypeWorker:index="+index+", fitness="+fitness[index]);

	// allow GC to clean up phenotypes that we don't need to keep
	if (!threadedEvolutionManager.isMostFit(index, fitness[index]))
	{
	  phenotypes[index].cleanup();
	  phenotypes[index] = null;
        }

        countDownLatch.countDown();
      }
      catch (Exception e)
      {
        log.error("ThreadedEvolutionManager:CreatePhenotypeWorker:exception:"+e.getMessage());
      }
    }
  }
}
