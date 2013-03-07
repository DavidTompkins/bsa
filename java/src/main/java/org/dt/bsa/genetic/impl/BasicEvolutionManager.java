//////////////////////////////////////////////////////////////////
//                                                              //
// BasicEvolutionManager - Basic genetic evolution algorithm    //
//                                                              //
// David Tompkins - 8/7/2007                                    //
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

import java.util.Date;

import javax.persistence.*;

import org.dt.bsa.genetic.Genome;
import org.dt.bsa.genetic.Phenotype;
import org.dt.bsa.genetic.Generation;
import org.dt.bsa.genetic.GenomeFactory;
import org.dt.bsa.genetic.GenerationFactory;
import org.dt.bsa.genetic.EvolutionManager;
import org.dt.bsa.genetic.EvolutionException;

import org.dt.bsa.util.Random;
import org.dt.bsa.util.TimeUtil;
import org.dt.bsa.util.BSAException;

@Entity
@DiscriminatorValue("basic")
public class BasicEvolutionManager
extends BaseEvolutionManagerImpl
implements EvolutionManager
{
  protected static final Random random = Random.getInstance();

  protected Genome[] population;
  protected Genome[] children;
  protected Phenotype[] phenotypes;
  protected float[] fitness;

  public BasicEvolutionManager() { super(); }
  
  public BasicEvolutionManager(int id)
  throws BSAException
  {
    super(id);
  }

  public BasicEvolutionManager(GenomeFactory genomeFactory, String description)
  {
    super(genomeFactory, new SingleGenerationFactory(), description);
  }
  
  public BasicEvolutionManager(GenomeFactory genomeFactory, GenerationFactory generationFactory, String description)
  {
    super(genomeFactory, generationFactory, description);
  }
  
  public int evaluatePhenotypes(Generation generation)
  throws EvolutionException, BSAException
  {
    int indexMostFit = -1; 
    float fitnessHighest = Phenotype.FITNESS_MINIMUM;

    for (int i = 0 ; i < this.populationSize ; i++)
    {
      this.phenotypes[i] = this.population[i].createPhenotype();
      this.fitness[i] = this.phenotypes[i].getFitness();
      if (this.fitness[i] > fitnessHighest)
      {
	fitnessHighest = this.fitness[i];
        indexMostFit = i;

	// eliminate unfit phenotypes (so they can be GC'd)
	for (int ii = 0 ; ii < i ; ii++)
	  this.phenotypes[ii] = null;
      }
    } 

    return indexMostFit;
  }

  public void evolve()
  throws EvolutionException, BSAException
  {
    // Adjust Invalid Parameters

    if (this.populationSize < 10)
      this.populationSize = 10;
    if (this.numGenerations < 1)
      this.numGenerations = 1;

    if (this.crossoverRate < 0.0F)
      this.crossoverRate = 0.0F;
    else if (this.crossoverRate > 1.0F)
      this.crossoverRate = 1.0F;

    if (this.mutationRate < 0.0F)
      this.mutationRate = 0.0F;
    else if (this.mutationRate > 1.0F)
      this.mutationRate = 1.0F;

    log.info("-----------------------------------------------\n" +
             "      start time: " + (new Date()) + "\n" +
             " population size: " + this.populationSize + "\n" +
             "# of generations: " + this.numGenerations + "\n" +
             "  crossover rate: " + this.crossoverRate * 100.0F + "%\n" +
             "   mutation rate: " + this.mutationRate * 100.0F + "%\n" +
             " scaling enabled: " + this.fitnessScalingEnabled + "\n" +
             " elitism enabled: " + this.elitismEnabled + "\n" +
             "-----------------------------------------------\n");

    this.population = new Genome[this.populationSize];
    this.children = new Genome[this.populationSize];
    this.phenotypes = new Phenotype[this.populationSize];
    this.fitness = new float[this.populationSize];

    long evolutionStartTime = System.currentTimeMillis();

    // create initial population
    for (int i = 0 ; i < this.populationSize ; i++)
      this.population[i] = this.genomeFactory.getRandomGenome();
    
    int current_iteration = 0;
    boolean complete_requested = false;

    this.setStartAt(new Date(evolutionStartTime));
    this.setStatus(STATUS_RUNNING);
    this.setIteration(current_iteration);
    this.save();
    
    while (!complete_requested && (current_iteration < numGenerations))
    {
      Generation generation = this.generationFactory.getGeneration(current_iteration, this.description);
      generation.setEvolutionManager(this);
      float fitnessHighest = generation.execute(this, this.population, this.children, this.phenotypes, this.fitness);

      if (fitnessHighest == Phenotype.FITNESS_MINIMUM)
        throw new EvolutionException("Error:zero total fitness, evolution terminated");
      
      if (fitnessHighest == Phenotype.FITNESS_MAXIMUM)
      {
        log.info("FITNESS_MAXIMUM found, evolution complete.");
        break;
      }

      // swap children created in Generation.execute() into population array
      Genome[] tmp = this.children;
      this.children = this.population;
      this.population = tmp;

      // clean Phenotype array for garbage collection
      for (int i = 0 ; i < this.phenotypes.length ; i++)
	this.phenotypes[i] = null;

      this.refresh();
      complete_requested = (this.getStatus() == STATUS_COMPLETE_REQUESTED);

      this.setIteration(++current_iteration);
      this.save();
    
      log.info("-------------------------------------------------------------------");
      log.info("Completed " + current_iteration + " generations:");
      long generationElapsedTime = generation.getEndAt().getTime() - generation.getStartAt().getTime();
      log.info("Generation elapsed time: "+TimeUtil.msToElapsedTime(generationElapsedTime));
      long evolutionElapsedTime = System.currentTimeMillis() - evolutionStartTime;
      log.info("Evolution Elapsed time: "+TimeUtil.msToElapsedTime(evolutionElapsedTime));
      long meanTimePerGeneration = evolutionElapsedTime/current_iteration;
      log.info("Mean time per generation: "+TimeUtil.msToElapsedTime(meanTimePerGeneration));
      long estimatedTimeRemaining = evolutionElapsedTime*(this.numGenerations-current_iteration)/current_iteration;
      log.info("Estimated time remaining: "+TimeUtil.msToElapsedTime(estimatedTimeRemaining));
      long estimatedTotalElapsedTime = evolutionElapsedTime*this.numGenerations/current_iteration;
      log.info("Estimated total elapsed time: "+TimeUtil.msToElapsedTime(estimatedTotalElapsedTime));
      log.info("-------------------------------------------------------------------");
    }

    long evolutionEndTime = System.currentTimeMillis();

    this.setEndAt(new Date(evolutionEndTime));
    this.setStatus(STATUS_COMPLETE);
    this.setIteration(current_iteration);
    this.save();

    long evolutionElapsedTime = evolutionEndTime - evolutionStartTime;
    log.info("Completed " + current_iteration + " generations, evolution complete. Total elapsed time: "+TimeUtil.msToElapsedTime(evolutionElapsedTime));
  }
}
