//////////////////////////////////////////////////////////////////
//                                                              //
// NativeNetworkImpl - Base Network native implementation       //
//                                                              //
// David Tompkins - 4/29/2007                                   //
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

import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

@Entity
@DiscriminatorValue("native")
public class NativeNetworkImpl
extends BaseNetworkImpl
implements Network
{
  static { System.loadLibrary("bsa"); }

  public static final int NO_NETWORK_AVAILABLE = -1;

  public static final Random random = Random.getInstance();

  protected double[] hiddenLayer;
  protected double[] outputLayer;

  protected int networkId;
  protected boolean training;

  public NativeNetworkImpl() { super(); }

  public NativeNetworkImpl(int inputDimension, int hiddenDimension, int outputDimension)
  {
    this(inputDimension, hiddenDimension, outputDimension, DEFAULT_LEARNING_RATE, true);
  }

  public NativeNetworkImpl(int inputDimension, int hiddenDimension, int outputDimension, double learningRate)
  {
    this(inputDimension, hiddenDimension, outputDimension, learningRate, true);
  }

  public NativeNetworkImpl(int inputDimension, int hiddenDimension, int outputDimension, boolean randomInit)
  {
    this(inputDimension, hiddenDimension, outputDimension, DEFAULT_LEARNING_RATE, randomInit);
  }

  public NativeNetworkImpl(int inputDimension, int hiddenDimension, int outputDimension, double learningRate, boolean randomInit)
  {
    super(inputDimension, hiddenDimension, outputDimension, learningRate);

    this.hiddenLayer = new double[this.hiddenDimension*this.inputDimension];
    this.outputLayer = new double[this.outputDimension*this.hiddenDimension];
    this.training = false;
    this.networkId = NO_NETWORK_AVAILABLE;

    if (!randomInit)
      return;
    
    // initialize the weights with random values
    for (int i = 0 ; i < this.hiddenDimension ; i++)
      for (int ii = 0 ; ii < this.inputDimension ; ii++)
        this.hiddenLayer[i*this.inputDimension+ii] = 0.5 - (random.nextDouble() / 1.0);
    for (int i = 0 ; i < this.outputDimension ; i++)
      for (int ii = 0 ; ii < this.hiddenDimension ; ii++)
        this.outputLayer[i*this.hiddenDimension+ii] = 0.5 - (random.nextDouble() / 1.0);
  }

  public NativeNetworkImpl(org.dt.bsa.xml.Network network)
  {
    super(network);
    fromXml(network);
  }

  public NativeNetworkImpl(int id)
  throws BSAException
  {
    super(id);
  }

  @Transient
  public void fromXml(org.dt.bsa.xml.Network network)
  {
    super.fromXml(network);

    this.hiddenLayer = new double[this.hiddenDimension*this.inputDimension];
    this.outputLayer = new double[this.outputDimension*this.hiddenDimension];
    this.training = false;
    this.networkId = NO_NETWORK_AVAILABLE;

    org.dt.bsa.xml.Layer layer = network.getLayers().getLayerArray(HIDDEN_LAYER_INDEX);
    for (int ii = 0 ; ii < this.inputDimension ; ii++)
      for (int i = 0 ; i < this.hiddenDimension ; i++)
        this.hiddenLayer[ii*this.hiddenDimension+i] = layer.getNodes().getNodeArray(ii).getWeights().getWeightArray(i);
    
    layer = network.getLayers().getLayerArray(OUTPUT_LAYER_INDEX);
    for (int ii = 0 ; ii < this.hiddenDimension ; ii++)
      for (int i = 0 ; i < this.outputDimension ; i++)
        this.outputLayer[ii*this.outputDimension+i] = layer.getNodes().getNodeArray(ii).getWeights().getWeightArray(i);
  }

  public void shutdown()
  throws NetworkException
  {
    retrieveNetwork(this.networkId, this.hiddenLayer, this.outputLayer);
    removeNetwork(this.networkId);
    this.training = false;
  }

  @Transient
  public Sample predict(Sample sample)
  throws NetworkException
  {
    if ((sample.getInputDimension() != this.getInputDimension()) || ((sample.getOutputDimension() != 0) && (sample.getOutputDimension() != this.getOutputDimension())))
      throw new NetworkException("NativeNetworkImpl:predit:sample dimensions do not match network dimensions:("+sample.getInputDimension()+","+sample.getOutputDimension()+")("+this.getInputDimension()+","+this.getOutputDimension()+")");
    
    checkNetworkStatus(); 
    double[] outputs = predictNative(this.networkId, sample.getInputs());
    sample.setOutputs(outputs);
    return sample;
  }

  @Transient
  public double train(Sample sample)
  throws NetworkException
  {
    if ((sample.getInputDimension() != this.getInputDimension()) || (sample.getOutputDimension() != this.getOutputDimension()))
      throw new NetworkException("NativeNetworkImpl:train:sample dimensions do not match network dimensions:("+sample.getInputDimension()+","+sample.getOutputDimension()+")("+this.getInputDimension()+","+this.getOutputDimension()+")");
    
    checkNetworkStatus();
    return trainNative(this.networkId, sample.getInputs(), sample.getOutputs());
  }
 
  @Transient
  public double train(Source source)
  throws NetworkException
  {
    if ((source.getInputDimension() != this.getInputDimension()) || (source.getOutputDimension() != this.getOutputDimension()))
      throw new NetworkException("NativeNetworkImpl:train:source dimensions do not match network dimensions:("+source.getInputDimension()+","+source.getOutputDimension()+")("+this.getInputDimension()+","+this.getOutputDimension()+")");
    
    checkNetworkStatus();

    // format samples for efficient transfer to the JNI space

    int doubleSize = Double.SIZE / 8;
    ByteBuffer inputs_bytes = ByteBuffer.allocateDirect(source.getInputDimension() * source.size() * doubleSize).order(ByteOrder.nativeOrder());
    ByteBuffer outputs_bytes = ByteBuffer.allocateDirect(source.getOutputDimension() * source.size() * doubleSize).order(ByteOrder.nativeOrder());
    DoubleBuffer inputs = inputs_bytes.asDoubleBuffer();
    DoubleBuffer outputs = outputs_bytes.asDoubleBuffer();

    System.out.println("double size is:"+doubleSize);
    System.out.println("buffers are direct:"+inputs.isDirect()+":"+outputs.isDirect());
    System.out.println("native byte order is:"+ByteOrder.nativeOrder());

    for (Sample sample : source)
    {
      inputs.put(sample.getInputs(), 0, source.getInputDimension());
      outputs.put(sample.getOutputs(), 0, source.getOutputDimension());
    }

    return trainNative(this.networkId, inputs, outputs, source.size());
  }

  @Transient
  public org.dt.bsa.xml.Network toXml()
  {
    if (training)
    {
      try { shutdown(); }
      catch (NetworkException e) { log.error("NativeNetworkImpl:toXml:exception:"+e.getMessage()); }
    }

    org.dt.bsa.xml.Network network = super.toXml();
    network.addNewLayers();
    network.getLayers().insertNewLayer(HIDDEN_LAYER_INDEX);
    network.getLayers().setLayerArray(HIDDEN_LAYER_INDEX, layerToXml(this.hiddenLayer, inputDimension, hiddenDimension));
    network.getLayers().insertNewLayer(OUTPUT_LAYER_INDEX);
    network.getLayers().setLayerArray(OUTPUT_LAYER_INDEX, layerToXml(this.outputLayer, hiddenDimension, outputDimension));
    return network;
  }

  @Transient
  public org.dt.bsa.xml.Layer layerToXml(double[] nodes, int inputDimension, int outputDimension)
  {
    org.dt.bsa.xml.Layer layer = org.dt.bsa.xml.Layer.Factory.newInstance();
    layer.setInputDimension(inputDimension);
    layer.setOutputDimension(outputDimension);
    layer.addNewNodes();
    for (int i = 0 ; i < inputDimension ; i++)
    {
      layer.getNodes().insertNewNode(i);
      layer.getNodes().setNodeArray(i, nodeToXml(nodes, i*outputDimension, outputDimension));
    } 
    return layer;
  }

  @Transient
  public org.dt.bsa.xml.Node nodeToXml(double[] weights, int offset, int weightDimension)
  {
    org.dt.bsa.xml.Node node = org.dt.bsa.xml.Node.Factory.newInstance();
    node.setInputDimension(weightDimension);
    node.addNewWeights();
    for (int i = 0 ; i < weightDimension ; i++)
      node.getWeights().insertWeight(i, weights[offset+i]);
    return node;
  }

  protected void checkNetworkStatus()
  throws NetworkException
  {
    if (!this.training)
    {
      this.networkId = registerNetwork(this.inputDimension, this.hiddenDimension, this.outputDimension, this.learningRate, this.hiddenLayer, this.outputLayer);
      if (this.networkId == NO_NETWORK_AVAILABLE)
	throw new NetworkException("NativeNetworkImpl:checkNetworkStatus:all native networks in use");
      this.training = true;
    }
  }

  protected native int registerNetwork(int inputDimension, int hiddenDimension, int outputDimension, double learningRate, double[] hiddenLayer, double[] outputLayer);
  protected native void retrieveNetwork(int networkId, double[] hiddenlayer, double[] outputLayer);
  protected native void removeNetwork(int networkId);
  protected native double[] predictNative(int networkId, double[] inputs);
  protected native double trainNative(int networkId, double[] inputs, double[] outputs);
  protected native double trainNative(int networkId, DoubleBuffer inputs, DoubleBuffer outputs, int size);
}
