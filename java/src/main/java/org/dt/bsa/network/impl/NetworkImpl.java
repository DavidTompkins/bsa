//////////////////////////////////////////////////////////////////
//                                                              //
// NetworkImpl - Base Network implementation                    //
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

import org.dt.bsa.network.Network;
import org.dt.bsa.network.NetworkException;
import org.dt.bsa.network.Layer;
import org.dt.bsa.data.Sample;
import org.dt.bsa.data.Source;
import org.dt.bsa.util.BSAException;

import java.util.Iterator;

import javax.persistence.*;

@Entity
@DiscriminatorValue("standard")
public class NetworkImpl
extends BaseNetworkImpl
implements Network
{
  protected Layer hiddenLayer;
  protected Layer outputLayer;

  public NetworkImpl() { super(); }

  public NetworkImpl(int inputDimension, int hiddenDimension, int outputDimension)
  {
    this(inputDimension, hiddenDimension, outputDimension, true);
  }

  public NetworkImpl(int inputDimension, int hiddenDimension, int outputDimension, boolean randomInit)
  {
    super(inputDimension, hiddenDimension, outputDimension);

    this.hiddenLayer = createLayer(this.hiddenDimension, this.inputDimension, randomInit);
    this.outputLayer = createLayer(this.outputDimension, this.hiddenDimension, randomInit);
  }

  public NetworkImpl(org.dt.bsa.xml.Network network)
  {
    super(network);
    fromXml(network);
  }

  public NetworkImpl(int id)
  throws BSAException
  {
    super(id);
  }

  public void fromXml(org.dt.bsa.xml.Network network)
  {
    super.fromXml(network);

    this.hiddenLayer = createLayer(network.getLayers().getLayerArray(HIDDEN_LAYER_INDEX));
    this.outputLayer = createLayer(network.getLayers().getLayerArray(OUTPUT_LAYER_INDEX));
  }

  protected Layer createLayer(int outputDimension, int inputDimension, boolean randomInit) { return new LayerImpl(outputDimension, inputDimension, randomInit); }
  protected Layer createLayer(org.dt.bsa.xml.Layer layer) { return new LayerImpl(layer); }

  public Sample predict(Sample sample)
  {
    double[] hiddenLayerOutputs = hiddenLayer.predict(sample.getInputs());
    double[] outputLayerOutputs = outputLayer.predict(hiddenLayerOutputs);
    sample.setOutputs(outputLayerOutputs);
    return sample;
  }
  
  public double train(Sample sample)
  throws NetworkException
  {
    if ((sample.getInputDimension() != this.getInputDimension()) || (sample.getOutputDimension() != this.getOutputDimension()))
      throw new NetworkException("NetworkImpl:train:sample dimensions do not match network dimensions:("+sample.getInputDimension()+","+sample.getOutputDimension()+")("+this.getInputDimension()+","+this.getOutputDimension()+")");

    double[] inputs = sample.getInputs();
    double[] outputs = sample.getOutputs();

    double[] hiddenLayerOutputs = hiddenLayer.predict(inputs);
    double[] outputLayerOutputs = outputLayer.predict(hiddenLayerOutputs);

    double mse = 0.0; // mean squared error
    double eta = this.getLearningRate(); // learning rate (eta) for the network

    double[] outputError = new double[outputLayer.getOutputDimension()];
    double[] hiddenError = new double[hiddenLayer.getOutputDimension()];

    // Calculate output differences and mean squared error
    for (int i = 0 ; i < outputs.length ; i++)
    {
      outputError[i] = outputs[i] - outputLayerOutputs[i];
      mse += (outputError[i] * outputError[i]);
      outputError[i] *= outputLayerOutputs[i] * (1 - outputLayerOutputs[i]);
    }

    // Calculate hidden layer error terms
    for (int i = 0 ; i < hiddenLayer.getOutputDimension() ; i++)
    {
      double sum = 0.0;
      for (int ii = 0 ; ii < outputLayer.getOutputDimension() ; ii++)
        sum += outputError[ii] * outputLayer.getNode(ii).getWeight(i);
      hiddenError[i] = sum * hiddenLayerOutputs[i] * (1 - hiddenLayerOutputs[i]);
    }

    // Update output weights
    for (int i = 0 ; i < outputLayer.getOutputDimension() ; i++)
      for (int ii = 0 ; ii < hiddenLayer.getOutputDimension() ; ii++)
        outputLayer.getNode(i).setWeight(ii, outputLayer.getNode(i).getWeight(ii) + (eta * outputError[i] * hiddenLayerOutputs[ii]));

    // Update hidden weights
    for (int i = 0 ; i < hiddenLayer.getOutputDimension() ; i++)
      for (int ii = 0 ; ii < getInputDimension() ; ii++)
        hiddenLayer.getNode(i).setWeight(ii, hiddenLayer.getNode(i).getWeight(ii) + (eta * hiddenError[i] * inputs[ii]));

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
    network.getLayers().setLayerArray(HIDDEN_LAYER_INDEX, this.hiddenLayer.toXml());
    network.getLayers().insertNewLayer(OUTPUT_LAYER_INDEX);
    network.getLayers().setLayerArray(OUTPUT_LAYER_INDEX, this.outputLayer.toXml());
    return network;
  }
}
