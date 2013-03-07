//////////////////////////////////////////////////////////////////
//                                                              //
// InlineNetworkImpl - Base Network implementation              //
//                                                              //
// David Tompkins - 7/10/2007                                   //
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

import org.dt.bsa.network.Network;
import org.dt.bsa.network.NetworkException;
import org.dt.bsa.data.Sample;
import org.dt.bsa.data.Source;
import org.dt.bsa.util.MathUtil;
import org.dt.bsa.util.Random;
import org.dt.bsa.util.BSAException;

import java.util.Iterator;

import javax.persistence.*;

@Entity
@DiscriminatorValue("inline")
public class InlineNetworkImpl
extends BaseNetworkImpl
implements Network
{
  public static final Random random = Random.getInstance();

  protected double[][] hiddenLayer;
  protected double[][] outputLayer;

  public InlineNetworkImpl() { super(); }

  public InlineNetworkImpl(int inputDimension, int hiddenDimension, int outputDimension)
  {
    this(inputDimension, hiddenDimension, outputDimension, DEFAULT_LEARNING_RATE, true);
  }

  public InlineNetworkImpl(int inputDimension, int hiddenDimension, int outputDimension, double learningRate)
  {
    this(inputDimension, hiddenDimension, outputDimension, learningRate, true);
  }

  public InlineNetworkImpl(int inputDimension, int hiddenDimension, int outputDimension, boolean randomInit)
  {
    this(inputDimension, hiddenDimension, outputDimension, DEFAULT_LEARNING_RATE, randomInit);
  }

  public InlineNetworkImpl(int inputDimension, int hiddenDimension, int outputDimension, double learningRate, boolean randomInit)
  {
    super(inputDimension, hiddenDimension, outputDimension, learningRate);

    this.hiddenLayer = new double[this.hiddenDimension][this.inputDimension];
    this.outputLayer = new double[this.outputDimension][this.hiddenDimension];

    if (!randomInit)
      return;
    
    // initialize the weights with random values
    for (int i = 0 ; i < this.hiddenDimension ; i++)
      for (int ii = 0 ; ii < this.inputDimension ; ii++)
        this.hiddenLayer[i][ii] = 0.5 - (random.nextDouble() / 1.0);
    for (int i = 0 ; i < this.outputDimension ; i++)
      for (int ii = 0 ; ii < this.hiddenDimension ; ii++)
        this.outputLayer[i][ii] = 0.5 - (random.nextDouble() / 1.0);
  }

  public InlineNetworkImpl(org.dt.bsa.xml.Network network)
  {
    super(network);
    fromXml(network);
  }

  public InlineNetworkImpl(int id)
  throws BSAException
  {
    super(id);
  }

  public void fromXml(org.dt.bsa.xml.Network network)
  {
    super.fromXml(network);

    this.hiddenLayer = new double[this.hiddenDimension][this.inputDimension];
    this.outputLayer = new double[this.outputDimension][this.hiddenDimension];

    org.dt.bsa.xml.Layer layer = network.getLayers().getLayerArray(HIDDEN_LAYER_INDEX);
    for (int i = 0 ; i < this.hiddenDimension ; i++)
      for (int ii = 0 ; ii < this.inputDimension ; ii++)
        this.hiddenLayer[i][ii] = layer.getNodes().getNodeArray(i).getWeights().getWeightArray(ii);
    
    layer = network.getLayers().getLayerArray(OUTPUT_LAYER_INDEX);
    for (int i = 0 ; i < this.outputDimension ; i++)
      for (int ii = 0 ; ii < this.hiddenDimension ; ii++)
        this.outputLayer[i][ii] = layer.getNodes().getNodeArray(i).getWeights().getWeightArray(ii);
  }

  public Sample predict(Sample sample)
  throws NetworkException
  {
    if ((sample.getInputDimension() != this.getInputDimension()) || ((sample.getOutputDimension() != 0) && (sample.getOutputDimension() != this.getOutputDimension())))
      throw new NetworkException("InlineNetworkImpl:predit:sample dimensions do not match network dimensions:("+sample.getInputDimension()+","+sample.getOutputDimension()+")("+this.getInputDimension()+","+this.getOutputDimension()+")");

    double[] hiddenLayerOutputs = new double[this.hiddenDimension];
    for (int node = 0 ; node < this.hiddenDimension ; node++)
    {
      double sum = 0;
      for (int weight = 0 ; weight < this.inputDimension ; weight++)
        sum += this.hiddenLayer[node][weight]*sample.getInputs()[weight];
      hiddenLayerOutputs[node] = MathUtil.sigmoid(sum);
    }

    double[] outputLayerOutputs = new double[this.outputDimension];
    for (int node = 0 ; node < this.outputDimension ; node++)
    {
      double sum = 0;
      for (int weight = 0 ; weight < this.hiddenDimension ; weight++)
        sum += this.outputLayer[node][weight]*hiddenLayerOutputs[weight];
      outputLayerOutputs[node] = MathUtil.sigmoid(sum);
    }

    sample.setOutputs(outputLayerOutputs);
    return sample;
  }

  public double train(Sample sample)
  throws NetworkException
  {
    if ((sample.getInputDimension() != this.getInputDimension()) || (sample.getOutputDimension() != this.getOutputDimension()))
      throw new NetworkException("InlineNetworkImpl:train:sample dimensions do not match network dimensions:("+sample.getInputDimension()+","+sample.getOutputDimension()+")("+this.getInputDimension()+","+this.getOutputDimension()+")");

    double[] inputs = sample.getInputs();
    double[] outputs = sample.getOutputs();

    double[] hiddenLayerOutputs = new double[this.hiddenDimension];
    for (int node = 0 ; node < this.hiddenDimension ; node++)
    {
      double sum = 0;
      for (int weight = 0 ; weight < this.inputDimension ; weight++)
        sum += this.hiddenLayer[node][weight]*inputs[weight];
      hiddenLayerOutputs[node] = MathUtil.sigmoid(sum);
    }

    double[] outputLayerOutputs = new double[this.outputDimension];
    for (int node = 0 ; node < this.outputDimension ; node++)
    {
      double sum = 0;
      for (int weight = 0 ; weight < this.hiddenDimension ; weight++)
        sum += this.outputLayer[node][weight]*hiddenLayerOutputs[weight];
      outputLayerOutputs[node] = MathUtil.sigmoid(sum);
    }

    double mse = 0.0; // mean squared error
    double eta = this.getLearningRate(); // learning rate (eta) for the network

    double[] outputError = new double[this.outputDimension];
    double[] hiddenError = new double[this.hiddenDimension];

    // Calculate output differences and mean squared error
    for (int i = 0 ; i < outputs.length ; i++)
    {
      outputError[i] = outputs[i] - outputLayerOutputs[i];
      mse += (outputError[i] * outputError[i]);
      outputError[i] *= outputLayerOutputs[i] * (1 - outputLayerOutputs[i]);
    }

    // Calculate hidden layer error terms
    for (int i = 0 ; i < this.hiddenDimension ; i++)
    {
      double sum = 0.0;
      for (int ii = 0 ; ii < this.outputDimension ; ii++)
        sum += outputError[ii] * outputLayer[ii][i];
      hiddenError[i] = sum * hiddenLayerOutputs[i] * (1 - hiddenLayerOutputs[i]);
    }

    // Update output weights
    for (int i = 0 ; i < this.outputDimension ; i++)
      for (int ii = 0 ; ii < this.hiddenDimension ; ii++)
        outputLayer[i][ii] = outputLayer[i][ii] + (eta * outputError[i] * hiddenLayerOutputs[ii]);

    // Update hidden weights
    for (int i = 0 ; i < this.hiddenDimension ; i++)
      for (int ii = 0 ; ii < this.inputDimension ; ii++)
        hiddenLayer[i][ii] =  hiddenLayer[i][ii] + (eta * hiddenError[i] * inputs[ii]);

    // return mean squared error
    return mse;
  }
  
  public double train(Source source)
  throws NetworkException
  {
    double mse = 0.0; // mean squared error
    Iterator<Sample> iterator = source.iterator();
    while (iterator.hasNext())
      mse += train(iterator.next());
    return mse;
  }

  public org.dt.bsa.xml.Network toXml()
  {
    org.dt.bsa.xml.Network network = super.toXml();
    network.addNewLayers();
    network.getLayers().insertNewLayer(HIDDEN_LAYER_INDEX);
    network.getLayers().setLayerArray(HIDDEN_LAYER_INDEX, layerToXml(this.hiddenLayer));
    network.getLayers().insertNewLayer(OUTPUT_LAYER_INDEX);
    network.getLayers().setLayerArray(OUTPUT_LAYER_INDEX, layerToXml(this.outputLayer));
    return network;
  }

  public org.dt.bsa.xml.Layer layerToXml(double[][] nodes)
  {
    org.dt.bsa.xml.Layer layer = org.dt.bsa.xml.Layer.Factory.newInstance();
    layer.setInputDimension(nodes.length);
    layer.setOutputDimension(nodes[0].length);
    layer.addNewNodes();
    for (int i = 0 ; i < nodes.length ; i++)
    {
      layer.getNodes().insertNewNode(i);
      layer.getNodes().setNodeArray(i, nodeToXml(nodes[i]));
    } 
    return layer;
  }

  public org.dt.bsa.xml.Node nodeToXml(double[] weights)
  {
    org.dt.bsa.xml.Node node = org.dt.bsa.xml.Node.Factory.newInstance();
    node.setInputDimension(weights.length);
    node.addNewWeights();
    for (int i = 0 ; i < weights.length ; i++)
      node.getWeights().insertWeight(i, weights[i]);
    return node;
  }
}
