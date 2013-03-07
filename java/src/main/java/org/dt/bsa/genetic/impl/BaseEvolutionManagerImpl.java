//////////////////////////////////////////////////////////////////
//                                                              //
// BaseEvolutionManagerImpl - Base abstract EvolutionManager    //
//                                                              //
// David Tompkins - 9/23/2007                                   //
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
import java.util.HashMap;

import javax.persistence.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dt.bsa.genetic.GeneticConstants;
import org.dt.bsa.genetic.Phenotype;
import org.dt.bsa.genetic.Generation;
import org.dt.bsa.genetic.GenomeFactory;
import org.dt.bsa.genetic.GenerationFactory;
import org.dt.bsa.genetic.EvolutionManager;
import org.dt.bsa.genetic.EvolutionException;
import org.dt.bsa.util.HibernateUtil;
import org.dt.bsa.util.BSAException;

@Entity
@Table(name="evolutions")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type", discriminatorType=DiscriminatorType.STRING)
public abstract class BaseEvolutionManagerImpl
implements EvolutionManager, GeneticConstants
{
  final Logger log = LoggerFactory.getLogger(BaseEvolutionManagerImpl.class);

  protected GenomeFactory genomeFactory;
  protected GenerationFactory generationFactory;

  protected int populationSize;
  protected int numGenerations;
  protected float crossoverRate;
  protected float mutationRate;
  protected boolean elitismEnabled;
  protected boolean fitnessScalingEnabled;

  public BaseEvolutionManagerImpl() { }
  
  public BaseEvolutionManagerImpl(int id)
  throws BSAException
  {
    HibernateUtil.load(this, id);
  }

  public BaseEvolutionManagerImpl(GenomeFactory genomeFactory, GenerationFactory generationFactory, String description)
  {
    this.genomeFactory = genomeFactory;
    this.generationFactory = generationFactory;
    this.description = description;

    this.populationSize = DEFAULT_POPULATION_SIZE;
    this.numGenerations = DEFAULT_NUM_GENERATIONS;
    this.crossoverRate = DEFAULT_CROSSOVER_RATE;
    this.mutationRate = DEFAULT_MUTATION_RATE;
    this.elitismEnabled = DEFAULT_ELITISM_ENABLED;
    this.fitnessScalingEnabled = DEFAULT_FITNESS_SCALAING_ENABLED;

    this.status = STATUS_INITIALIZED;
  }

  @Transient
  public Phenotype getMostFitPhenotype()
  throws BSAException
  { 
    HashMap<String,String> args = new HashMap<String,String>();
    args.put("id", new Integer(this.id).toString());
    BaseGenerationImpl generation = (BaseGenerationImpl)HibernateUtil.query("from org.dt.bsa.genetic.impl.BaseGenerationImpl as generation where generation.evolutionId = :id order by endAt desc", args).get(0);

    args = new HashMap<String,String>();
    args.put("phenotype_id", new Integer(generation.getPhenotypeId()).toString());
    Phenotype phenotype = (Phenotype)HibernateUtil.query("from org.dt.bsa.genetic.impl.BasePhenotypeImpl as phenotype where phenotype.id = :phenotype_id", args).get(0);
    return phenotype;
  }
  
  @Transient
  public void run()
  {
    long startTime = System.currentTimeMillis();
    try { evolve(); }
    catch (EvolutionException e)
    {
      log.error("BaseEvolutionManagerImpl:run:EvolutionException:"+e.getMessage());
    }
    catch (BSAException e)
    {
      log.error("BaseEvolutionManagerImpl:run:BSAException:"+e.getMessage());
    }
    log.info("----------------------------");
    log.info("Total elapsed time: "+ ((System.currentTimeMillis() - startTime)/1000.0)+" sec");
    log.info("----------------------------");
  }

  @Transient
  public abstract void evolve() throws EvolutionException, BSAException;
  @Transient
  public abstract int evaluatePhenotypes(Generation generation) throws EvolutionException, BSAException;

  @Transient
  public int getPopulationSize() { return this.populationSize; }
  @Transient
  public void setPopulationSize(int populationSize) { this.populationSize = populationSize; }

  @Transient
  public int getNumGenerations() { return this.numGenerations; }
  @Transient
  public void setNumGenerations(int numGenerations) { this.numGenerations = numGenerations; }

  @Transient
  public float getCrossoverRate() { return this.crossoverRate; }
  @Transient
  public void setCrossoverRate(float crossoverRate) { this.crossoverRate = crossoverRate; }

  @Transient
  public float getMutationRate() { return this.mutationRate; }
  @Transient
  public void setMutationRate(float mutationRate) { this.mutationRate = mutationRate; }

  @Transient
  public boolean getElitism() { return this.elitismEnabled; }
  @Transient
  public void setElitism(boolean elitismEnabled) { this.elitismEnabled = elitismEnabled; }

  @Transient
  public boolean getFitnessScaling() { return this.fitnessScalingEnabled; }
  @Transient
  public void setFitnessScaling(boolean fitnessScalingEnabled) { this.fitnessScalingEnabled = fitnessScalingEnabled; }

  //
  // Hibernate data
  //

  protected int id;
  protected String description;
  protected Date startAt;
  protected Date endAt;
  protected int status;
  protected int iteration;

  @Id
  @GeneratedValue
  public int getId() { return this.id; }
  public void setId(int id) { this.id = id; }

  @Basic
  public String getDescription() { return this.description; }
  public void setDescription(String description) { this.description = description; }

  @Basic
  public Date getStartAt() { return this.startAt; }
  public void setStartAt(Date startAt) { this.startAt = startAt; }

  @Basic
  public Date getEndAt() { return this.endAt; }
  public void setEndAt(Date endAt) { this.endAt = endAt; }

  @Basic
  public int getStatus() { return this.status; }
  public void setStatus(int status) { this.status = status; }

  @Basic
  public int getIteration() { return this.iteration; }
  public void setIteration(int iteration) { this.iteration = iteration; }
 
  @Transient
  public void refresh()
  throws BSAException
  {
    HibernateUtil.refresh(this);
  }
 
  @Transient
  public void save()
  throws BSAException
  {
    HibernateUtil.save(this);
  }
}
