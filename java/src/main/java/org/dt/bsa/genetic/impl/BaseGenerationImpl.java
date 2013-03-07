//////////////////////////////////////////////////////////////////
//                                                              //
// BaseGenerationImpl - Base abstract Generation implementation //
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dt.bsa.genetic.GeneticConstants;
import org.dt.bsa.genetic.Genome;
import org.dt.bsa.genetic.Phenotype;
import org.dt.bsa.genetic.Generation;
import org.dt.bsa.genetic.EvolutionManager;
import org.dt.bsa.genetic.EvolutionException;
import org.dt.bsa.util.HibernateUtil;
import org.dt.bsa.util.BSAException;

@Entity
@Table(name="generations")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type", discriminatorType=DiscriminatorType.STRING)
public abstract class BaseGenerationImpl
implements Generation, GeneticConstants
{
  final Logger log = LoggerFactory.getLogger(BaseGenerationImpl.class);

  protected int iteration;

  public BaseGenerationImpl() { }

  public BaseGenerationImpl(int iteration, String description)
  {
    this.iteration = iteration;
    this.description = description;

    this.status = STATUS_INITIALIZED;
  }

  @Transient
  public abstract float execute(EvolutionManager evolutionManager, Genome[] population, Genome[] children, Phenotype[] phenotypes, float[] fitness) throws EvolutionException, BSAException;
  
  //
  // Hibernate data
  //

  protected int id;
  protected int status;
  protected int evolutionId;
  protected int phenotypeId;
  protected String description;
  protected Date startAt;
  protected Date endAt;

  @Transient
  public int getIteration() { return this.iteration; }
  @Transient
  public void setIteration(int iteration) { this.iteration = iteration; }

  @Id
  @GeneratedValue
  public int getId() { return this.id; }
  public void setId(int id) { this.id = id; }

  @Basic
  public int getStatus() { return this.status; }
  public void setStatus(int status) { this.status = status; }

  @Basic
  public int getEvolutionId() { return this.evolutionId; }
  public void setEvolutionId(int evolutionId) { this.evolutionId = evolutionId; }

  @Basic
  public int getPhenotypeId() { return this.phenotypeId; }
  public void setPhenotypeId(int phenotypeId) { this.phenotypeId = phenotypeId; }

  @Transient
  public void setPhenotype(Phenotype phenotype) { this.phenotypeId = phenotype.getId(); }

  @Transient
  public void setEvolutionManager(EvolutionManager evolutionManager) { this.evolutionId = evolutionManager.getId(); }

  @Basic
  public String getDescription() { return this.description; }
  public void setDescription(String description) { this.description = description; }

  @Basic
  public Date getStartAt() { return this.startAt; }
  public void setStartAt(Date startAt) { this.startAt = startAt; }

  @Basic
  public Date getEndAt() { return this.endAt; }
  public void setEndAt(Date endAt) { this.endAt = endAt; }

  @Transient
  public void save()
  throws BSAException
  {
    HibernateUtil.save(this);
  }
}
