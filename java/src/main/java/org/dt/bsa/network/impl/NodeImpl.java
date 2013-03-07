//////////////////////////////////////////////////////////////////
//                                                              //
// NodeImpl - Base Network Node implementation                  //
//                                                              //
// David Tompkins - 4/25/2007                                   //
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dt.bsa.network.Node;
import org.dt.bsa.util.MathUtil;
import org.dt.bsa.util.Random;

public class NodeImpl
implements Node
{
  final Logger log = LoggerFactory.getLogger(NodeImpl.class);
  public static final Random random = Random.getInstance();

  protected double[] weights;

  public NodeImpl(int dimension)
  {
    this(dimension, true);
  }

  public NodeImpl(int dimension, boolean randomInit)
  {
    this.weights = new double[dimension];

    if (!randomInit)
      return;

    // initialize the weights with random values
    for (int i = 0 ; i < this.weights.length ; i++)
      this.weights[i] = 0.5 - (random.nextDouble() / 1.0);
  }

  public NodeImpl(org.dt.bsa.xml.Node node)
  {
    this.weights = new double[node.getInputDimension()];
    for (int i = 0 ; i < this.weights.length ; i++)
      this.weights[i] = node.getWeights().getWeightArray(i);
  }

  public double predict(double[] inputs)
  {
    if (inputs.length != this.weights.length)
    {
      log.error("Node:predict():dimension mismatch");
      System.exit(1);
    }

    double sum = 0;
    for (int i = 0 ; i < this.weights.length ; i++)
      sum += this.weights[i]*inputs[i];
    return MathUtil.sigmoid(sum);
  }

  public org.dt.bsa.xml.Node toXml()
  {
    org.dt.bsa.xml.Node node = org.dt.bsa.xml.Node.Factory.newInstance();
    node.setInputDimension(this.weights.length);
    node.addNewWeights();
    for (int i = 0 ; i < this.weights.length ; i++)
      node.getWeights().insertWeight(i, this.weights[i]);
    return node;
  }

  public int getInputDimension() { return this.weights.length; }
  public double[] getWeights() { return this.weights; }
  public double getWeight(int index) { return this.weights[index]; }
  public void setWeight(int index, double weight) { this.weights[index] = weight; }
  public void setWeights(double[] newWeights) { this.weights = newWeights; }
}
