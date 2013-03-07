//////////////////////////////////////////////////////////////////
//                                                              //
// ThreadedNetworkImpl - Threaded inline Network implementation //
//                                                              //
// David Tompkins - 7/16/2007                                   //
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
import org.dt.bsa.util.BSAException;

import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.CountDownLatch;

import javax.persistence.*;

@Entity
@DiscriminatorValue("threaded")
public class ThreadedNetworkImpl
extends InlineNetworkImpl
{
  protected int numThreads;
  protected ExecutorService service;

  public ThreadedNetworkImpl() { super(); }

  public ThreadedNetworkImpl(int numThreads, int inputDimension, int hiddenDimension, int outputDimension)
  {
    this(numThreads, inputDimension, hiddenDimension, outputDimension, DEFAULT_LEARNING_RATE, true);
  }

  public ThreadedNetworkImpl(int numThreads, int inputDimension, int hiddenDimension, int outputDimension, double learningRate)
  {
    this(numThreads, inputDimension, hiddenDimension, outputDimension, learningRate, true);
  }

  public ThreadedNetworkImpl(int numThreads, int inputDimension, int hiddenDimension, int outputDimension, boolean randomInit)
  {
    this(numThreads, inputDimension, hiddenDimension, outputDimension, DEFAULT_LEARNING_RATE, randomInit);
  }

  public ThreadedNetworkImpl(int numThreads, int inputDimension, int hiddenDimension, int outputDimension, double learningRate, boolean randomInit)
  {
    super(inputDimension, hiddenDimension, outputDimension, learningRate, randomInit);
    this.numThreads = numThreads;
    service = Executors.newFixedThreadPool(numThreads);
  }

  public ThreadedNetworkImpl(int numThreads, org.dt.bsa.xml.Network network)
  {
    super(network);
    this.numThreads = numThreads;
    service = Executors.newFixedThreadPool(numThreads);
  }
  
  public ThreadedNetworkImpl(int numThreads, int id)
  throws BSAException
  {
    super(id);
    this.numThreads = numThreads;
  }

  protected void finalize()
  throws Throwable
  {
    service.shutdown();
  }

  protected void waitForAll(CountDownLatch countDownLatch, String label)
  {
    //log.info("ThreadedNetworkImpl:waitForAll:"+label+":waiting...");
    try { countDownLatch.await(); }
    catch (InterruptedException e) { log.error("ThreadedNetworkImpl:waitForAll:interrupted:"+label); }
    //log.info("ThreadedNetworkImpl:waitForAll:"+label+":completed");
  }

  public Sample predict(Sample sample)
  throws NetworkException
  {
    if ((sample.getInputDimension() != this.getInputDimension()) || ((sample.getOutputDimension() != 0) && (sample.getOutputDimension() != this.getOutputDimension())))
      throw new NetworkException("ThreadedNetworkImpl:predit:sample dimensions do not match network dimensions:("+sample.getInputDimension()+","+sample.getOutputDimension()+")("+this.getInputDimension()+","+this.getOutputDimension()+")");

    CountDownLatch countDownLatch = new CountDownLatch(this.numThreads);
    double[] hiddenLayerOutputs = new double[this.hiddenDimension];
    int batchSize = this.hiddenDimension / this.numThreads;
    int index1 = 0;
    for (int thread = 0 ; thread < this.numThreads ; thread++)
    {
      int index2 = ((thread == (this.numThreads-1)) ? this.hiddenDimension : (index1+batchSize));
      service.execute(new PredictWorker(countDownLatch, index1, index2, sample.getInputs(), hiddenLayerOutputs, this.hiddenLayer)); //"BSA:ThreadedNetworkImpl:PredictWorker-HiddenLayer:"+index1+":"+index2);
      index1 += batchSize;
    }
    this.waitForAll(countDownLatch, "predict-hiddenlayer");

    /*
    for (int node = 0 ; node < this.hiddenDimension ; node++)
    {
      double sum = 0;
      for (int weight = 0 ; weight < this.inputDimension ; weight++)
        sum += this.hiddenLayer[node][weight]*sample.getInputs()[weight];
      hiddenLayerOutputs[node] = MathUtil.sigmoid(sum);
    }
    */

    countDownLatch = new CountDownLatch(this.numThreads);
    double[] outputLayerOutputs = new double[this.outputDimension];
    batchSize = this.outputDimension / this.numThreads;
    index1 = 0;
    for (int thread = 0 ; thread < this.numThreads ; thread++)
    {
      int index2 = ((thread == (this.numThreads-1)) ? this.outputDimension : (index1+batchSize));
      service.execute(new PredictWorker(countDownLatch, index1, index2, hiddenLayerOutputs, outputLayerOutputs, this.outputLayer)); //"BSA:ThreadedNetworkImpl:PredictWorker-OutputLayer:"+index1+":"+index2);
      index1 += batchSize;
    }
    this.waitForAll(countDownLatch, "predict-outputlayer");

    /*
    for (int node = 0 ; node < this.outputDimension ; node++)
    {
      double sum = 0;
      for (int weight = 0 ; weight < this.hiddenDimension ; weight++)
        sum += this.outputLayer[node][weight]*hiddenLayerOutputs[weight];
      outputLayerOutputs[node] = MathUtil.sigmoid(sum);
    }
    */

    sample.setOutputs(outputLayerOutputs);
    return sample;
  }

  public double train(Sample sample)
  throws NetworkException
  {
    if ((sample.getInputDimension() != this.getInputDimension()) || (sample.getOutputDimension() != this.getOutputDimension()))
      throw new NetworkException("ThreadedNetworkImpl:train:sample dimensions do not match network dimensions:("+sample.getInputDimension()+","+sample.getOutputDimension()+")("+this.getInputDimension()+","+this.getOutputDimension()+")");

    double eta = this.getLearningRate();

    CountDownLatch countDownLatch = new CountDownLatch(this.numThreads);
    double[] hiddenLayerOutputs = new double[this.hiddenDimension];
    int batchSize = this.hiddenDimension / this.numThreads;
    int index1 = 0;
    for (int thread = 0 ; thread < this.numThreads ; thread++)
    {
      int index2 = ((thread == (this.numThreads-1)) ? this.hiddenDimension : (index1+batchSize));
      service.execute(new PredictWorker(countDownLatch, index1, index2, sample.getInputs(), hiddenLayerOutputs, this.hiddenLayer)); //"BSA:ThreadedNetworkImpl:PredictWorker-HiddenLayer:"+index1+":"+index2);
      index1 += batchSize;
    }
    this.waitForAll(countDownLatch, "train-hiddenlayer");

    /*
    for (int node = 0 ; node < this.hiddenDimension ; node++)
    {
      double sum = 0;
      for (int weight = 0 ; weight < this.inputDimension ; weight++)
        sum += this.hiddenLayer[node][weight]*sample.getInputs()[weight];
      hiddenLayerOutputs[node] = MathUtil.sigmoid(sum);
    }
    */

    countDownLatch = new CountDownLatch(this.numThreads);
    double[] outputLayerOutputs = new double[this.outputDimension];
    batchSize = this.outputDimension / this.numThreads;
    index1 = 0;
    for (int thread = 0 ; thread < this.numThreads ; thread++)
    {
      int index2 = ((thread == (this.numThreads-1)) ? this.outputDimension : (index1+batchSize));
      service.execute(new PredictWorker(countDownLatch, index1, index2, hiddenLayerOutputs, outputLayerOutputs, this.outputLayer)); //"BSA:ThreadedNetworkImpl:PredictWorker-OutputLayer:"+index1+":"+index2);
      index1 += batchSize;
    }
    this.waitForAll(countDownLatch, "train-outputlayer");

    /*
    for (int node = 0 ; node < this.outputDimension ; node++)
    {
      double sum = 0;
      for (int weight = 0 ; weight < this.hiddenDimension ; weight++)
        sum += this.outputLayer[node][weight]*hiddenLayerOutputs[weight];
      outputLayerOutputs[node] = MathUtil.sigmoid(sum);
    }
    */

    double mse = 0.0; // mean squared error

    // Calculate output differences and mean squared error
    countDownLatch = new CountDownLatch(this.numThreads);
    double[] outputError = new double[this.outputDimension];
    double[] mse_per_thread = new double[this.numThreads];
    batchSize = this.outputDimension / this.numThreads;
    index1 = 0;
    for (int thread = 0 ; thread < this.numThreads ; thread++)
    {
      int index2 = ((thread == (this.numThreads-1)) ? this.outputDimension : (index1+batchSize));
      service.execute(new OutputErrorWorker(countDownLatch, index1, index2, outputError, sample.getOutputs(), outputLayerOutputs, mse_per_thread, thread)); //"BSA:ThreadedNetworkImpl:OutputErrorWorker:"+index1+":"+index2);
      index1 += batchSize;
    }
    this.waitForAll(countDownLatch, "train-outputerror");
    for (int thread = 0 ; thread < this.numThreads ; thread++)
      mse += mse_per_thread[thread];

    /*
    for (int i = 0 ; i < this.outputDimension ; i++)
    {
      outputError[i] = sample.getOutputs()[i] - outputLayerOutputs[i];
      mse += (outputError[i] * outputError[i]);
      outputError[i] *= outputLayerOutputs[i] * (1 - outputLayerOutputs[i]);
    }
    */

    // Calculate hidden layer error terms
    countDownLatch = new CountDownLatch(this.numThreads);
    double[] hiddenError = new double[this.hiddenDimension];
    batchSize = this.hiddenDimension / this.numThreads;
    index1 = 0;
    for (int thread = 0 ; thread < this.numThreads ; thread++)
    {
      int index2 = ((thread == (this.numThreads-1)) ? this.hiddenDimension : (index1+batchSize));
      service.execute(new HiddenErrorWorker(countDownLatch, index1, index2, hiddenError, hiddenLayerOutputs, outputError, outputLayer)); //"BSA:ThreadedNetworkImpl:HiddenErrorWorker:"+index1+":"+index2);
      index1 += batchSize;
    }
    this.waitForAll(countDownLatch, "train-hiddenerror");

    /*
    for (int i = 0 ; i < this.hiddenDimension ; i++)
    {
      double sum = 0.0;
      for (int ii = 0 ; ii < this.outputDimension ; ii++)
        sum += outputError[ii] * outputLayer[ii][i];
      hiddenError[i] = sum * hiddenLayerOutputs[i] * (1 - hiddenLayerOutputs[i]);
    }
    */

    // Update output weights
    countDownLatch = new CountDownLatch(this.numThreads);
    batchSize = this.outputDimension / this.numThreads;
    index1 = 0;
    for (int thread = 0 ; thread < this.numThreads ; thread++)
    {
      int index2 = ((thread == (this.numThreads-1)) ? this.outputDimension : (index1+batchSize));
      service.execute(new UpdateWeightsWorker(countDownLatch, index1, index2, outputLayer, outputError, hiddenLayerOutputs, eta)); //"BSA:ThreadedNetworkImpl:UpdateWeightsWorker-OutputLayer:"+index1+":"+index2);
      index1 += batchSize;
    }
    this.waitForAll(countDownLatch, "train-outputweights");
    /*
    for (int i = 0 ; i < this.outputDimension ; i++)
      for (int ii = 0 ; ii < this.hiddenDimension ; ii++)
        outputLayer[i][ii] = outputLayer[i][ii] + (eta * outputError[i] * hiddenLayerOutputs[ii]);
    */

    // Update hidden weights
    countDownLatch = new CountDownLatch(this.numThreads);
    batchSize = this.hiddenDimension / this.numThreads;
    index1 = 0;
    for (int thread = 0 ; thread < this.numThreads ; thread++)
    {
      int index2 = ((thread == (this.numThreads-1)) ? this.hiddenDimension : (index1+batchSize));
      service.execute(new UpdateWeightsWorker(countDownLatch, index1, index2, hiddenLayer, hiddenError, sample.getInputs(), eta)); //"BSA:ThreadedNetworkImpl:UpdateWeightsWorker-HiddenLayer:"+index1+":"+index2);
      index1 += batchSize;
    }
    this.waitForAll(countDownLatch, "train-hiddenweights");
    
    /*
    for (int i = 0 ; i < this.hiddenDimension ; i++)
      for (int ii = 0 ; ii < this.inputDimension ; ii++)
        hiddenLayer[i][ii] =  hiddenLayer[i][ii] + (eta * hiddenError[i] * sample.getInputs()[ii]);
    */

    // return mean squared error
    return mse;
  }

  class PredictWorker
  implements Runnable
  {
    protected int index1;
    protected int index2;
    protected double[] layerInputs;
    protected double[] layerOutputs;
    protected double[][] layerWeights;
    protected CountDownLatch countDownLatch;

    public PredictWorker(CountDownLatch countDownLatch, int index1, int index2, double[] layerInputs, double[] layerOutputs, double[][] layerWeights)
    {
      this.index1 = index1;
      this.index2 = index2;
      this.layerInputs = layerInputs;
      this.layerOutputs = layerOutputs;
      this.layerWeights = layerWeights;
      this.countDownLatch = countDownLatch;
      //log.info("PredictWorker:"+index1+":"+index2);
    }

    public void run()
    {
      for (int node = this.index1 ; node < this.index2 ; node++)
      {
        double sum = 0;
        for (int weight = 0 ; weight < this.layerWeights[0].length ; weight++)
          sum += this.layerWeights[node][weight]*layerInputs[weight];
        layerOutputs[node] = MathUtil.sigmoid(sum);
      }
      this.countDownLatch.countDown();
    }
  }

  class OutputErrorWorker
  implements Runnable
  {
    protected int index1;
    protected int index2;
    protected double[] outputError;
    protected double[] outputs;
    protected double[] outputLayerOutputs;
    protected double[] mse_per_thread;
    protected int threadNumber;
    protected CountDownLatch countDownLatch;

    public OutputErrorWorker(CountDownLatch countDownLatch, int index1, int index2, double[] outputError, double[] outputs, double[] outputLayerOutputs, double[] mse_per_thread, int threadNumber)
    {
      this.index1 = index1;
      this.index2 = index2;
      this.outputError = outputError;
      this.outputs = outputs;
      this.outputLayerOutputs = outputLayerOutputs;
      this.mse_per_thread = mse_per_thread;
      this.threadNumber = threadNumber;
      this.countDownLatch = countDownLatch;
      //log.info("OutputErrorWorker:"+index1+":"+index2);
    }

    public void run()
    {
      double mse = 0.0;
      for (int i = this.index1 ; i < this.index2 ; i++)
      {
        outputError[i] = outputs[i] - outputLayerOutputs[i];
        mse += (outputError[i] * outputError[i]);
        outputError[i] *= outputLayerOutputs[i] * (1 - outputLayerOutputs[i]);
      }
      this.mse_per_thread[this.threadNumber] = mse;
      this.countDownLatch.countDown();
    }
  }

  class HiddenErrorWorker
  implements Runnable
  {
    protected int index1;
    protected int index2;
    protected double[] hiddenError;
    protected double[] hiddenLayerOutputs;
    protected double[] outputError;
    protected double[][] outputLayer;
    protected CountDownLatch countDownLatch;

    public HiddenErrorWorker(CountDownLatch countDownLatch, int index1, int index2, double[] hiddenError, double[] hiddenLayerOutputs, double[] outputError, double[][] outputLayer)
    {
      this.index1 = index1;
      this.index2 = index2;
      this.hiddenError = hiddenError;
      this.hiddenLayerOutputs = hiddenLayerOutputs;
      this.outputError = outputError;
      this.outputLayer = outputLayer;
      this.countDownLatch = countDownLatch;
      //log.info("HiddenErrorWorker:"+index1+":"+index2);
    }

    public void run()
    {
      for (int i = this.index1 ; i < this.index2 ; i++)
      {
        double sum = 0.0;
        for (int ii = 0 ; ii < this.outputError.length ; ii++)
          sum += outputError[ii] * outputLayer[ii][i];
        hiddenError[i] = sum * hiddenLayerOutputs[i] * (1 - hiddenLayerOutputs[i]);
      }
      this.countDownLatch.countDown();
    }
  }

  class UpdateWeightsWorker
  implements Runnable
  {
    protected int index1;
    protected int index2;
    protected double[][] layerWeights;
    protected double[] layerError;
    protected double[] layerOutputs;
    protected double eta;
    protected CountDownLatch countDownLatch;

    public UpdateWeightsWorker(CountDownLatch countDownLatch, int index1, int index2, double[][] layerWeights, double[] layerError, double[] layerOutputs, double eta)
    {
      this.index1 = index1;
      this.index2 = index2;
      this.layerWeights = layerWeights;
      this.layerError = layerError;
      this.layerOutputs = layerOutputs;
      this.eta = eta;
      this.countDownLatch = countDownLatch;
      //log.info("UpdateWeightsWorker:"+index1+":"+index2);
    }

    public void run()
    {
      for (int i = this.index1 ; i < this.index2 ; i++)
        for (int ii = 0 ; ii < this.layerWeights[0].length ; ii++)
          layerWeights[i][ii] = layerWeights[i][ii] + (eta * layerError[i] * layerOutputs[ii]);
      this.countDownLatch.countDown();
    }
  }
}
