//////////////////////////////////////////////////////////////////
//                                                              //
// BaseNetworkImpl - Base abstract Network implementation       //
//                                                              //
// David Tompkins - 8/15/2007                                   //
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

package org.dt.bsa.network.impl;

import java.util.Date;

import javax.persistence.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dt.bsa.network.Network;
import org.dt.bsa.network.NetworkException;
import org.dt.bsa.genetic.Phenotype;
import org.dt.bsa.data.Sample;
import org.dt.bsa.data.Source;
import org.dt.bsa.util.HibernateUtil;
import org.dt.bsa.util.BSAException;

import org.apache.xmlbeans.XmlException;

@Entity
@Table(name="networks")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type", discriminatorType=DiscriminatorType.STRING)
public abstract class BaseNetworkImpl
implements Network
{
  final Logger log = LoggerFactory.getLogger(BaseNetworkImpl.class);

  protected int inputDimension;
  protected int hiddenDimension;
  protected int outputDimension;
  protected double learningRate;
  
  public String trainingDescription;

  public BaseNetworkImpl() { }
  
  public BaseNetworkImpl(int inputDimension, int hiddenDimension, int outputDimension, double learningRate)
  {
    this.inputDimension  = inputDimension;
    this.hiddenDimension = hiddenDimension;
    this.outputDimension = outputDimension;
    this.learningRate = learningRate;
    this.trainingDescription = "Untrained";
    this.createdAt = new Date();
  }

  public BaseNetworkImpl(int inputDimension, int hiddenDimension, int outputDimension)
  {
    this(inputDimension, hiddenDimension, outputDimension, DEFAULT_LEARNING_RATE);
  }

  public BaseNetworkImpl(org.dt.bsa.xml.Network network)
  {
    fromXml(network);
  }

  public BaseNetworkImpl(int id)
  throws BSAException
  {
    HibernateUtil.load(this, id);
  }

  @Transient
  public abstract Sample predict(Sample sample) throws NetworkException;
  @Transient
  public abstract double train(Sample sample) throws NetworkException;
  @Transient
  public abstract double train(Source source) throws NetworkException;

  @Transient
  public int getInputDimension() { return this.inputDimension; }
  @Transient
  public int getHiddenDimension() { return this.hiddenDimension; }
  @Transient
  public int getOutputDimension() { return this.outputDimension; }
  @Transient
  public double getLearningRate() { return this.learningRate; }
  
  @Transient
  public String getTrainingDescription() { return this.trainingDescription; }
  @Transient
  public void setTrainingDescription(String trainingDescription) { this.trainingDescription = trainingDescription; }
  
  @Transient
  public void fromXml(org.dt.bsa.xml.Network network)
  {
    this.inputDimension  = network.getInputDimension();
    this.hiddenDimension = network.getHiddenDimension();
    this.outputDimension = network.getOutputDimension();
    this.learningRate = network.getLearningRate();
    this.trainingDescription = network.getTrainingDescription();
  }

  @Transient
  public org.dt.bsa.xml.Network toXml()
  {
    org.dt.bsa.xml.Network network = org.dt.bsa.xml.Network.Factory.newInstance();
    network.setDescription(this.getClass().getName());
    network.setInputDimension(this.inputDimension);
    network.setHiddenDimension(this.hiddenDimension);
    network.setOutputDimension(this.outputDimension);
    network.setLearningRate(this.learningRate);
    network.setTrainingDescription(this.trainingDescription);
    return network;
  }

  @Transient
  public String toString()
  {
    return this.toXml().toString();
  }
  
  public void shutdown() throws NetworkException { }
 
  //
  // Hibernate data
  //

  protected int id;
  protected int phenotypeId;
  protected Date createdAt;

  @Id
  @GeneratedValue
  public int getId() { return this.id; }
  public void setId(int id) { this.id = id; }
 
  @Basic
  public int getPhenotypeId() { return this.phenotypeId; }
  public void setPhenotypeId(int phenotypeId) { this.phenotypeId = phenotypeId; }
  
  @Transient
  public void setPhenotype(Phenotype phenotype) { this.phenotypeId = phenotype.getId(); }

  @Basic
  public String getXml() { return this.toString(); }
  public void setXml(String xml)
  {
    try { this.fromXml(org.dt.bsa.xml.Network.Factory.parse(xml)); }
    catch (XmlException e) { log.error("BaseNetworkImpl:fromXml():parse error:"+e.getMessage()); }
  }

  @Basic
  public Date getCreatedAt() { return this.createdAt; }
  public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
  
  public void save()
  throws BSAException
  {
    HibernateUtil.save(this);
  }
}
