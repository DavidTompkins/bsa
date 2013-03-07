//////////////////////////////////////////////////////////////////
//                                                              //
// BasePhenotypeImpl - Base abstract Phenotype implementation   //
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

import javax.persistence.*;

import org.dt.bsa.genetic.Phenotype;
import org.dt.bsa.genetic.EvolutionException;
import org.dt.bsa.util.HibernateUtil;
import org.dt.bsa.util.BSAException;

@Entity
@Table(name="phenotypes")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type", discriminatorType=DiscriminatorType.STRING)
public abstract class BasePhenotypeImpl
implements Phenotype
{
  public BasePhenotypeImpl() { }

  public BasePhenotypeImpl(String description)
  {
    this.description = description;
    this.createdAt = new Date();
  }

  public BasePhenotypeImpl(int id)
  throws BSAException
  {
    HibernateUtil.load(this, id);
  }

  @Transient
  public abstract float getFitness() throws EvolutionException, BSAException;

  public void cleanup() { } // override to release expensive resources

  @Transient
  public String toString()
  {
    return "Phenotype("+this.getClass().getName()+"):id="+this.getId();
  }

  //
  // Hibernate data
  //

  protected int id;
  protected String description;
  protected Date createdAt;
  protected int totalSamples;
  protected int totalCorrect;
  protected int totalFalsePositive;
  protected float meanClassError;
  protected float meanCorrectProbability;
  protected float meanCorrectVariance;
  protected float meanCorrectPairwiseVariance;
  protected float meanIncorrectProbability;
  protected float meanIncorrectVariance;
  protected float meanIncorrectPairwiseVariance;

  @Id
  @GeneratedValue
  public int getId() { return this.id; }
  public void setId(int id) { this.id = id; }

  @Basic
  public String getDescription() { return this.description; }
  public void setDescription(String description) { this.description = description; }

  @Basic
  public Date getCreatedAt() { return this.createdAt; }
  public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

  @Basic
  public int getTotalSamples() { return this.totalSamples; }
  public void setTotalSamples(int totalSamples) { this.totalSamples = totalSamples; }

  @Basic
  public int getTotalCorrect() { return this.totalCorrect; }
  public void setTotalCorrect(int totalCorrect) { this.totalCorrect = totalCorrect; }

  @Basic
  public int getTotalFalsePositive() { return this.totalFalsePositive; }
  public void setTotalFalsePositive(int totalFalsePositive) { this.totalFalsePositive = totalFalsePositive; }

  @Basic
  public float getMeanClassError() { return this.meanClassError; }
  public void setMeanClassError(float meanClassError) { this.meanClassError = meanClassError; }

  @Basic
  public float getMeanCorrectProbability() { return this.meanCorrectProbability; }
  public void setMeanCorrectProbability(float meanCorrectProbability) { this.meanCorrectProbability = meanCorrectProbability; }

  @Basic
  public float getMeanCorrectVariance() { return this.meanCorrectVariance; }
  public void setMeanCorrectVariance(float meanCorrectVariance) { this.meanCorrectVariance = meanCorrectVariance; }

  @Basic
  public float getMeanCorrectPairwiseVariance() { return this.meanCorrectPairwiseVariance; }
  public void setMeanCorrectPairwiseVariance(float meanCorrectPairwiseVariance) { this.meanCorrectPairwiseVariance = meanCorrectPairwiseVariance; }

  @Basic
  public float getMeanIncorrectProbability() { return this.meanIncorrectProbability; }
  public void setMeanIncorrectProbability(float meanIncorrectProbability) { this.meanIncorrectProbability = meanIncorrectProbability; }

  @Basic
  public float getMeanIncorrectVariance() { return this.meanIncorrectVariance; }
  public void setMeanIncorrectVariance(float meanIncorrectVariance) { this.meanIncorrectVariance = meanIncorrectVariance; }

  @Basic
  public float getMeanIncorrectPairwiseVariance() { return this.meanIncorrectPairwiseVariance; }
  public void setMeanIncorrectPairwiseVariance(float meanIncorrectPairwiseVariance) { this.meanIncorrectPairwiseVariance = meanIncorrectPairwiseVariance; }

  @Transient
  public void save()
  throws BSAException
  {
    HibernateUtil.save(this);
  }
}
