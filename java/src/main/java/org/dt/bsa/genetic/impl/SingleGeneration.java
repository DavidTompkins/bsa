//////////////////////////////////////////////////////////////////
//                                                              //
// SingleGeneration - Basic genetic algorithm implementation    //
//                                                              //
// David Tompkins - 9/25/2007                                   //
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

import org.dt.bsa.genetic.Generation;
import org.dt.bsa.genetic.Genome;
import org.dt.bsa.genetic.Phenotype;
import org.dt.bsa.genetic.EvolutionManager;
import org.dt.bsa.genetic.EvolutionException;

import org.dt.bsa.util.HibernateUtil;
import org.dt.bsa.util.BSAException;
import org.dt.bsa.util.Random;

@Entity
@DiscriminatorValue("single")
public class SingleGeneration
extends BaseGenerationImpl
implements Generation
{
  protected static final Random random = Random.getInstance();

  public SingleGeneration() { super(); }

  public SingleGeneration(int iteration, String description)
  {
    super(iteration, description);
  }
  
  public float execute(EvolutionManager evolutionManager, Genome[] population, Genome[] children, Phenotype[] phenotypes, float[] fitness)
  throws EvolutionException, BSAException
  {
    int populationSize = evolutionManager.getPopulationSize();

    long startTime = System.currentTimeMillis();
    this.setStartAt(new Date(startTime));
    this.status = STATUS_RUNNING;
    this.save();

    int indexMostFit = evolutionManager.evaluatePhenotypes(this);
    log.info("SingleGeneration:evaluatePhenotypes() complete, indexMostFit = "+indexMostFit+", hashcode = "+population[indexMostFit].hashCode());
    phenotypes[indexMostFit].save();
    log.info("SingleGeneration:phenotype[indexMostFit] saved, id = "+phenotypes[indexMostFit].getId());
    this.setPhenotype(phenotypes[indexMostFit]);

    // Get fitness stats
    float totalFitness = 0.0F;
    float fitnessHighest = Phenotype.FITNESS_MINIMUM;
    float fitnessLowest = Phenotype.FITNESS_MAXIMUM;
    for (int i = 0 ; i < populationSize ; i++)
    {
      if (fitness[i] > fitnessHighest)
        fitnessHighest = fitness[i];

      if (fitness[i] < fitnessLowest)
        fitnessLowest = fitness[i];
            
      totalFitness += fitness[i];
    }
    float fitnessAverage = totalFitness / (float)populationSize;
    
    long endTime = System.currentTimeMillis();
    this.setEndAt(new Date(endTime));
    this.status = STATUS_COMPLETE;
    this.save();

    log.info("Generation " + this.iteration + "\nMost fit genotype:\n" + (population[indexMostFit]) + "Most fit phenotype:\n" + (phenotypes[indexMostFit])  + ", fitness = " + fitnessHighest + "\naverage fitness = " + fitnessAverage + ", iteration elapsed time = " + ((endTime - startTime)/1000.0) + " sec");

    if (evolutionManager.getFitnessScaling())
    {
      // scale fitness values and reclaculate total fitness
	
      if (fitnessLowest < Phenotype.FITNESS_STEP_SIZE)
        fitnessLowest = Phenotype.FITNESS_STEP_SIZE;

      totalFitness = 0.0F;
      for (int i = 0 ; i < populationSize ; i++)
      {
        fitness[i] -= fitnessLowest;
        fitness[i] *= fitness[i];
        totalFitness += fitness[i];
      }   
    }
        
    // if elitist selection, replace first item with best
    int start = 0;
    if (evolutionManager.getElitism())
    {
      children[0] = population[indexMostFit];
      start = 1;
    }
        
    // create new population
    for (int i = start ; i < populationSize ; i++)
    {
      // Roulette selection: first parent
      float selection = (this.random.nextFloat() * totalFitness);
      int father = 0;
            
      while (selection > fitness[father])
        selection -= fitness[father++];
           
      // crossover reproduction
      if (this.random.nextFloat() <= evolutionManager.getCrossoverRate())
      {
        // Roulette selection: second parent
        selection = (this.random.nextFloat() * totalFitness);
        int mother = 0;
                
        while (selection > fitness[mother])
          selection -= fitness[mother++];
         
        children[i] = population[father].clone().crossover(population[mother]);
      }
      else
      {
        // one parent, no crossover reproduction
        children[i] = population[father].clone();
      }
            
      // mutation
      if (this.random.nextFloat() < evolutionManager.getMutationRate())
        children[i].mutate();
    }

    return fitnessHighest;
  }
}
