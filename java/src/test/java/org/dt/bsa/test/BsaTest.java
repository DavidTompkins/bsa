//////////////////////////////////////////////////////////////////
//                                                              //
// BsaTest - JUnit test suite for BSA                           //
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

package org.dt.bsa.test;

import org.dt.bsa.network.Network;
import org.dt.bsa.network.impl.NetworkImpl;
import org.dt.bsa.network.impl.BaseNetworkImpl;
import org.dt.bsa.network.impl.NativeNetworkImpl;
import org.dt.bsa.network.impl.InlineNetworkImpl;
import org.dt.bsa.network.impl.ThreadedNetworkImpl;
import org.dt.bsa.data.Sample;
import org.dt.bsa.data.Source;
import org.dt.bsa.data.impl.SampleImpl;
import org.dt.bsa.data.impl.SourceImpl;
import org.dt.bsa.data.impl.SinSourceImpl;
import org.dt.bsa.data.impl.TickerSampleImpl;
import org.dt.bsa.data.impl.SinClassifierSourceImpl;
import org.dt.bsa.data.impl.TickerClassifierSourceImpl;
import org.dt.bsa.data.impl.LinearClassifierSourceImpl;
import org.dt.bsa.data.impl.ParabolicClassifierSourceImpl;
import org.dt.bsa.data.impl.ConstantDeltaClassifierSourceImpl;
import org.dt.bsa.trainer.Trainer;
import org.dt.bsa.trainer.TrainingManager;
import org.dt.bsa.trainer.impl.BatchTrainer;
import org.dt.bsa.trainer.impl.ThreadedTrainingManager;
import org.dt.bsa.genetic.Phenotype;
import org.dt.bsa.genetic.Genome;
import org.dt.bsa.genetic.EvolutionManager;
import org.dt.bsa.genetic.impl.BasicEvolutionManager;
import org.dt.bsa.genetic.impl.ThreadedEvolutionManager;
import org.dt.bsa.genetic.impl.BlackBoxGenomeFactory;
import org.dt.bsa.genetic.impl.BlackBoxBinaryGenomeFactory;
import org.dt.bsa.genetic.impl.NetworkGenomeFactory;
import org.dt.bsa.genetic.impl.BinaryGenome;
import org.dt.bsa.genetic.impl.SinClassifierSourceFactory;
import org.dt.bsa.genetic.impl.TickerClassifierSourceFactory;
import org.dt.bsa.util.MathUtil;
import org.dt.bsa.util.TimeUtil;
import org.dt.bsa.util.HibernateUtil;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;
  
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BsaTest
{
  final Logger log = LoggerFactory.getLogger(BsaTest.class);

  final int DEFAULT_NUM_THREADS = 8;

  @BeforeClass
  public static void setUp()
  throws Exception
  {
    HibernateUtil.init();
  }

  @AfterClass
  public static void tearDown()
  throws Exception
  {
    HibernateUtil.shutdown();
  }

  // Network XML Persistence
  //suite.addTest(new Test("testPersistence"));

  // Database Network Persistence
  //suite.addTest(new Test("testHibernate"));

  // Basic XOR training suite
  //suite.addTest(new Test("testTraining"));
  //suite.addTest(new Test("testTrainingNative"));
  //suite.addTest(new Test("testTrainingInline"));
  
  // Basic training -- function data sources
  //suite.addTest(new Test("testTrainingSinSource"));
  //suite.addTest(new Test("testTrainingSinClassifierSource"));
  //suite.addTest(new Test("testTrainingLinearClassifierSource"));
  //suite.addTest(new Test("testTrainingLinearClassifierSourceNative"));
  //suite.addTest(new Test("testTrainingParabolicClassifierSource"));
  //suite.addTest(new Test("testTrainingConstantDeltaClassifierSource"));
  
  // Batch training
  //suite.addTest(new Test("batchTrainingSinClassifierSource"));
  //suite.addTest(new Test("batchNativeTrainingSinClassifierSource"));
  //suite.addTest(new Test("batchInlineTrainingSinClassifierSource"));
  //suite.addTest(new Test("batchThreadedTrainingSinClassifierSource"));
  
  // Validation 
  //suite.addTest(new Test("validateNetworks"));
    
  // Ticker data source testing
  //suite.addTest(new Test("tickerSourceTest"));
  
  // Multithreaded Batch Training
  //suite.addTest(new Test("multiThreadBatchTrainingSinClassifierSource"));
  //suite.addTest(new Test("multiThreadBatchNativeTrainingSinClassifierSource"));
  
  // Multithreaded Batch Training
  //suite.addTest(new Test("multiThreadIterativeBatchTrainingSinClassifierSource"));
  //suite.addTest(new Test("multiThreadIterativeBatchNativeTrainingSinClassifierSource"));
  
  // Genetic evolution black box testing
  //suite.addTest(new Test("geneticBinaryGenomeTest"));
  //suite.addTest(new Test("geneticBlackBoxGenomeTest"));
  //suite.addTest(new Test("geneticNetworkGenomeTest"));
  
  //suite.addTest(new Test("geneticThreadedBlackBoxGenomeTest"));
  //suite.addTest(new Test("geneticThreadedBlackBoxBinaryGenomeTest"));
  
  //suite.addTest(new Test("geneticThreadedNetworkGenomeTest_SinClassifierSource"));
  //suite.addTest(new Test("geneticThreadedNetworkGenomeTest_TickerClassifierSource"));
 
  @Test
  public void testTraining()
  throws Exception
  {
    int inputDimension  = 3;
    int hiddenDimension = 3;
    int outputDimension = 3;
    Network network = new NetworkImpl(inputDimension, hiddenDimension, outputDimension);
    basicTraining(network, "Single Sample (java)");
  }

  @Test
  public void testTrainingNative()
  throws Exception
  {
    int inputDimension  = 3;
    int hiddenDimension = 3;
    int outputDimension = 3;
    Network network = new NativeNetworkImpl(inputDimension, hiddenDimension, outputDimension);
    basicTraining(network, "Single Sample (native)");
  }

  @Test
  public void testTrainingInline()
  throws Exception
  {
    int inputDimension  = 3;
    int hiddenDimension = 3;
    int outputDimension = 3;
    Network network = new InlineNetworkImpl(inputDimension, hiddenDimension, outputDimension);
    basicTraining(network, "Single Sample (inline)");
  }

  protected void basicTraining(Network network, String label)
  throws Exception
  {
    double[] inputs  = new double[] { 0.25, 0.75, 0.25 };
    double[] outputs = new double[] { 0.75, 0.25, 0.75 };
    Sample sample = new SampleImpl(inputs, outputs);
    trainingRunLoop(network, sample, label);
  }

  protected void basicTraining2(Network network, String label, double[] inputs, double[] outputs)
  throws Exception
  {
    Sample sample = new SampleImpl(inputs, outputs);
    trainingRunLoop(network, sample, label);
  }

  @Test
  public void validateNetworks()
  throws Exception
  {
    int inputDimension  = 3;
    int hiddenDimension = 8;
    int outputDimension = 5;
    int numThreads = DEFAULT_NUM_THREADS;
    double[] inputs  = new double[] { 0.25, 0.75, 0.25 };
    double[] outputs = new double[] { 0.75, 0.25, 0.75, 0.25, 0.75 };

    Network network = new NetworkImpl(inputDimension, hiddenDimension, outputDimension, false);
    basicTraining2(network, "Single Sample (java)", inputs, outputs);
    Network network2 = new InlineNetworkImpl(inputDimension, hiddenDimension, outputDimension, false);
    basicTraining2(network2, "Single Sample (inline)", inputs, outputs);
    Network network3 = new NativeNetworkImpl(inputDimension, hiddenDimension, outputDimension, false);
    basicTraining2(network3, "Single Sample (native)", inputs, outputs);
    Network network4 = new ThreadedNetworkImpl(numThreads, inputDimension, hiddenDimension, outputDimension, false);
    basicTraining2(network4, "Single Sample (threaded)", inputs, outputs);
    
    log.info("Compare java vs. inline: " + network.toString().equals(network2.toString()));
    log.info("Compare java vs. native: " + network.toString().equals(network3.toString()));
    log.info("Compare java vs. threaded: " + network.toString().equals(network4.toString()));

    //log.info("---------------------------");
    //log.info(network.toString());
    //log.info("---------------------------");
    //log.info(network2.toString());
    //log.info("---------------------------");
    //log.info(network3.toString());
    //log.info("---------------------------");
    //log.info(network4.toString());
    //log.info("---------------------------");
  }

  @Test
  public void testTrainingSinSource()
  throws Exception
  {
    int inputDimension  = 20;
    int hiddenDimension = 1000;
    int outputDimension = 1;
    Network network = new NetworkImpl(inputDimension, hiddenDimension, outputDimension);

    int numSamples = 1000;
    int frameSize = inputDimension;
    int forwardOffset = 5;
    Source source = new SinSourceImpl(numSamples, frameSize, forwardOffset);
    
    trainingRunRandom(network, source, "SinDataSource");
  }

  @Test
  public void testTrainingSinClassifierSource()
  throws Exception
  {
    int inputDimension  = 20;
    int hiddenDimension = 500;
    int outputDimension = 200; //200 1% bins
    Network network = new NetworkImpl(inputDimension, hiddenDimension, outputDimension);

    int numSamples = 1000;
    int frameSize = inputDimension;
    int forwardOffset = 5;
    int numClasses = outputDimension;
    Source source = new SinClassifierSourceImpl(numSamples, frameSize, forwardOffset, numClasses);
    
    trainingRunClassifier(network, source, "SinClassifierDataSource");
  }

  @Test
  public void testTrainingLinearClassifierSource()
  throws Exception
  {
    int inputDimension  = 10;
    int hiddenDimension = 500;
    int outputDimension = 40; //40 5% bins
    Network network = new NetworkImpl(inputDimension, hiddenDimension, outputDimension);

    int numSamples = 100;
    int frameSize = inputDimension;
    int forwardOffset = 5;
    int numClasses = outputDimension;
    Source source = new LinearClassifierSourceImpl(numSamples, frameSize, forwardOffset, numClasses);
    
    trainingRunClassifier(network, source, "LinearClassifierDataSource");
  }
 
  @Test
  public void testTrainingLinearClassifierSourceNative()
  throws Exception
  {
    int inputDimension  = 10;
    int hiddenDimension = 500;
    int outputDimension = 40; //40 5% bins
    Network network = new NativeNetworkImpl(inputDimension, hiddenDimension, outputDimension);

    int numSamples = 100;
    int frameSize = inputDimension;
    int forwardOffset = 5;
    int numClasses = outputDimension;
    Source source = new LinearClassifierSourceImpl(numSamples, frameSize, forwardOffset, numClasses);
    
    trainingRunClassifier(network, source, "LinearClassifierDataSourceNative");
  }

  @Test
  public void testTrainingParabolicClassifierSource()
  throws Exception
  {
    int inputDimension  = 10;
    int hiddenDimension = 500;
    int outputDimension = 40; //40 5% bins
    Network network = new NetworkImpl(inputDimension, hiddenDimension, outputDimension);

    int numSamples = 100;
    int frameSize = inputDimension;
    int forwardOffset = 5;
    int numClasses = outputDimension;
    Source source = new ParabolicClassifierSourceImpl(numSamples, frameSize, forwardOffset, numClasses);
    
    trainingRunClassifier(network, source, "ParabolicClassifierDataSource");
  }

  @Test
  public void testTrainingConstantDeltaClassifierSource()
  throws Exception
  {
    int inputDimension  = 10;
    int hiddenDimension = 500;
    int outputDimension = 40; //40 5% bins
    Network network = new NetworkImpl(inputDimension, hiddenDimension, outputDimension);

    int numSamples = 100;
    int frameSize = inputDimension;
    int forwardOffset = 5;
    int numClasses = outputDimension;
    Source source = new ConstantDeltaClassifierSourceImpl(numSamples, frameSize, forwardOffset, numClasses);
    
    trainingRunClassifier(network, source, "ConstantDeltaClassifierDataSource");
  }

  @Test
  public void batchTrainingSinClassifierSource()
  throws Exception
  {
    int inputDimension  = 20;
    int hiddenDimension = 400;
    int outputDimension = 200; //200 1% bins
    Network network = new NetworkImpl(inputDimension, hiddenDimension, outputDimension);

    int numSamples = 180;
    int frameSize = inputDimension;
    int forwardOffset = 5;
    int numClasses = outputDimension;
    Source source = new SinClassifierSourceImpl(numSamples, frameSize, forwardOffset, numClasses);
    
    trainingRunBatch(network, source, "Batch: SinClassifierDataSource (java)");
  }

  @Test
  public void batchInlineTrainingSinClassifierSource()
  throws Exception
  {
    int inputDimension  = 20;
    int hiddenDimension = 400;
    int outputDimension = 200; //200 1% bins
    Network network = new InlineNetworkImpl(inputDimension, hiddenDimension, outputDimension);

    int numSamples = 180;
    int frameSize = inputDimension;
    int forwardOffset = 5;
    int numClasses = outputDimension;
    Source source = new SinClassifierSourceImpl(numSamples, frameSize, forwardOffset, numClasses);
    
    trainingRunBatch(network, source, "Batch: SinClassifierDataSource (inline)");
  }

  @Test
  public void batchNativeTrainingSinClassifierSource()
  throws Exception
  {
    int inputDimension  = 20;
    int hiddenDimension = 400;
    int outputDimension = 200; //200 1% bins
    Network network = new NativeNetworkImpl(inputDimension, hiddenDimension, outputDimension);

    int numSamples = 180;
    int frameSize = inputDimension;
    int forwardOffset = 5;
    int numClasses = outputDimension;
    Source source = new SinClassifierSourceImpl(numSamples, frameSize, forwardOffset, numClasses);
    
    trainingRunBatch(network, source, "Batch: SinClassifierDataSource (native)");
  }

  @Test
  public void batchThreadedTrainingSinClassifierSource()
  throws Exception
  {
    int inputDimension  = 20;
    int hiddenDimension = 400;
    int outputDimension = 200; //200 1% bins
    int numThreads = DEFAULT_NUM_THREADS;
    Network network = new ThreadedNetworkImpl(numThreads, inputDimension, hiddenDimension, outputDimension);

    int numSamples = 180;
    int frameSize = inputDimension;
    int forwardOffset = 5;
    int numClasses = outputDimension;
    Source source = new SinClassifierSourceImpl(numSamples, frameSize, forwardOffset, numClasses);
    
    trainingRunBatch(network, source, "Batch: SinClassifierDataSource (threaded)");
  }

  @Test
  public void multiThreadBatchTrainingSinClassifierSource()
  throws Exception
  {
    int inputDimension  = 20;
    int hiddenDimension = 400;
    int outputDimension = 200; //200 1% bins
    
    int numSamples = 180;
    int frameSize = inputDimension;
    int forwardOffset = 5;
    int numClasses = outputDimension;

    Source source = new SinClassifierSourceImpl(numSamples, frameSize, forwardOffset, numClasses);
    
    TrainingManager manager = new ThreadedTrainingManager();

    int THREADS = 2;
    Network[] networks = new Network[THREADS];
    Trainer[] trainers = new Trainer[THREADS];
    for (int i = 0 ; i < THREADS ; i++)
    {
      networks[i] = new InlineNetworkImpl(inputDimension, hiddenDimension, outputDimension);
      trainers[i] = new BatchTrainer(networks[i], source, "MultiThreadTest["+i+"]");
      manager.add(trainers[i]);
    }
    
    log.info("");
    log.info("-----------------------------------------");
    log.info("Multithreaded Batch Run: Inline");
    log.info("-----------------------------------------");
    log.info("Network Dimensions: ("+inputDimension+":"+hiddenDimension+":"+outputDimension+")");
    log.info("-----------------------------------------");

    manager.start();
    manager.waitForAll();

    for (int i = 0 ; i < THREADS ; i++)
      log.info(trainers[i].getReport());
  }
 
  @Test
  public void multiThreadIterativeBatchTrainingSinClassifierSource()
  throws Exception
  {
    int NUM_ITERATIONS = 100;
    for (int i = 0 ; i < NUM_ITERATIONS ; i++)
      multiThreadBatchTrainingSinClassifierSource();
  }

  @Test
  public void multiThreadBatchNativeTrainingSinClassifierSource()
  throws Exception
  {
    int inputDimension  = 20;
    int hiddenDimension = 400;
    int outputDimension = 200; //200 1% bins

    int NUM_TRAINING_SAMPLES = 50000;

    int numSamples = 180;
    int frameSize = inputDimension;
    int forwardOffset = 5;
    int numClasses = outputDimension;

    Source source = new SinClassifierSourceImpl(numSamples, frameSize, forwardOffset, numClasses);
    
    TrainingManager manager = new ThreadedTrainingManager();

    int THREADS = 3;
    Network[] networks = new Network[THREADS];
    Trainer[] trainers = new Trainer[THREADS];
    for (int i = 0 ; i < THREADS ; i++)
    {
      networks[i] = new NativeNetworkImpl(inputDimension, hiddenDimension, outputDimension);
      trainers[i] = new BatchTrainer(networks[i], source, "MultiThreadTest["+i+"]", NUM_TRAINING_SAMPLES);
      manager.add(trainers[i]);
    }
    
    log.info("");
    log.info("-----------------------------------------");
    log.info("Multithreaded Batch Run: Native");
    log.info("-----------------------------------------");
    log.info("Network Dimensions: ("+inputDimension+":"+hiddenDimension+":"+outputDimension+")");
    log.info("-----------------------------------------");

    manager.start();
    manager.waitForAll();

    for (int i = 0 ; i < THREADS ; i++)
    {
      log.info(trainers[i].getReport());
      networks[i].shutdown();
    }
  }
 
  @Test
  public void multiThreadIterativeBatchNativeTrainingSinClassifierSource()
  throws Exception
  {
    int NUM_ITERATIONS = 100;
    for (int i = 0 ; i < NUM_ITERATIONS ; i++)
      multiThreadBatchNativeTrainingSinClassifierSource();
  }
  
  protected void trainingRunLoop(Network network, Sample sample, String label)
  throws Exception
  {
    long start = System.currentTimeMillis();

    log.info("");
    log.info("-----------------------------------------");
    log.info("Loop Training Run: "+label);
    log.info("-----------------------------------------");
    log.info("Network Dimensions: ("+network.getInputDimension()+":"+network.getHiddenDimension()+":"+network.getOutputDimension()+")");
    log.info("Inputs : "+TimeUtil.arrayToString(sample.getInputs()));
    log.info("Outputs: "+TimeUtil.arrayToString(sample.getOutputs()));
    log.info("-----------------------------------------");
    //log.info(network.toString());
    //log.info("-----------------------------------------");
    
    log.info("Processing...");

    for (int i = 0 ; i < 1000 ; i ++)
    {
      double mse = network.train(sample);
      //log.info("train():mse=" + mse);
    }

    Sample sample2 = new SampleImpl();
    sample2.setInputs(sample.getInputs());
    sample2 = network.predict(sample2);
      
    log.info("-----------------------------------------");
    log.info("Inputs : "+TimeUtil.arrayToString(sample2.getInputs()));
    log.info("Outputs: "+TimeUtil.arrayToString(sample2.getOutputs()));
    log.info("-----------------------------------------");
    
    long finish = System.currentTimeMillis();
    
    log.info("Total Elasped Time: "+(finish-start)+"ms");
    log.info("-----------------------------------------");
  }
  
  protected void trainingRunRandom(Network network, Source source, String label)
  throws Exception
  {
    long start = System.currentTimeMillis();

    log.info("");
    log.info("-----------------------------------------");
    log.info("Random Sample Training Run: "+label);
    log.info("-----------------------------------------");
    log.info("Network Dimensions: ("+network.getInputDimension()+":"+network.getHiddenDimension()+":"+network.getOutputDimension()+")");
    log.info("-----------------------------------------");
    //log.info(network.toString());
    //log.info("-----------------------------------------");
    
    log.info("Training...");

    double MAX_MSE = 0.000000000005; // Maximum mean squared error
    int MAX_TRAINING_SAMPLES = 100000; // Maximum number of samples that will be trained, regardless of MSE

    double mse = 1.0;
    int count = 0;

    while ((mse > MAX_MSE) && (count++ < MAX_TRAINING_SAMPLES))
      mse = network.train(source.getRandomSample());
    
    log.info("Training complete:"+count+" samples, mse="+mse);

    Sample sample = source.getRandomSample();
    Sample sample2 = new SampleImpl();
    sample2.setInputs(sample.getInputs());
    sample2 = network.predict(sample2);
      
    log.info("-----------------------------------------");
    log.info("Prediction test:");
    log.info("Inputs : "+TimeUtil.arrayToString(sample.getInputs()));
    log.info("Actual Outputs: "+TimeUtil.arrayToString(sample.getOutputs()));
    log.info("Predicted Outputs: "+TimeUtil.arrayToString(sample2.getOutputs()));
    log.info("-----------------------------------------");
    
    long finish = System.currentTimeMillis();
    
    log.info("Total Elasped Time: "+(finish-start)+"ms");
    log.info("-----------------------------------------");
  }
  
  protected void trainingRunClassifier(Network network, Source source, String label)
  throws Exception
  {
    long start = System.currentTimeMillis();

    log.info("");
    log.info("-----------------------------------------");
    log.info("Classifier Training Run: "+label);
    log.info("-----------------------------------------");
    log.info("Network Dimensions: ("+network.getInputDimension()+":"+network.getHiddenDimension()+":"+network.getOutputDimension()+")");
    log.info("-----------------------------------------");
    //log.info(network.toString());
    //log.info("-----------------------------------------");
    
    log.info("Training...");

    int MAX_TRAINING_SAMPLES = 50000; // Maximum number of samples that will be trained, regardless of MSE
    int STEP_SZE = 1000; // for logging

    double mse = 0.0;
    int count = 0;

    while (count++ < MAX_TRAINING_SAMPLES)
    {
      mse = network.train(source.getRandomSample());
      if (count % STEP_SZE == 0)
      log.info(""+count+"...");
    }
    
    log.info("Training complete:"+count+" samples");

    Sample sample = source.getRandomSample();
    Sample sample2 = new SampleImpl();
    sample2.setInputs(sample.getInputs());
    sample2 = network.predict(sample2);
      
    log.info("-----------------------------------------");
    log.info("Prediction test:");
    log.info("Inputs : "+TimeUtil.arrayToString(sample.getInputs()));
    log.info("Actual Outputs: "+TimeUtil.arrayToString(sample.getOutputs()));
    log.info("Predicted Outputs: "+TimeUtil.arrayToString(sample2.getOutputs()));
    log.info("Actual Outputs: "+TimeUtil.arrayToString(sample.getOutputs()));
    log.info("Predicted Outputs: "+TimeUtil.arrayToString(sample2.getOutputs()));
    log.info("Actual class:"+MathUtil.findMaxValueIndex(sample.getOutputs()));
    log.info("Predicted class:"+MathUtil.findMaxValueIndex(sample2.getOutputs()));
    log.info("Actual Class Probability:"+sample.getOutputs()[MathUtil.findMaxValueIndex(sample.getOutputs())]);
    log.info("Predicted Class Probability:"+sample2.getOutputs()[MathUtil.findMaxValueIndex(sample2.getOutputs())]);
    log.info("-----------------------------------------");
    
    long finish = System.currentTimeMillis();
    
    log.info("Total Elasped Time: "+(finish-start)+"ms");
    log.info("-----------------------------------------");
  }
  
  protected void trainingRunBatch(Network network, Source source, String label)
  throws Exception
  {
    long start = System.currentTimeMillis();

    log.info("");
    log.info("-----------------------------------------");
    log.info("Batch Run: "+label);
    log.info("-----------------------------------------");
    log.info("Network Dimensions: ("+network.getInputDimension()+":"+network.getHiddenDimension()+":"+network.getOutputDimension()+")");
    log.info("-----------------------------------------");
    //log.info(network.toString());
    //log.info("-----------------------------------------");

    int NUM_ITERATIONS = 100;
    int MAX_TRAINING_SAMPLES = 50000; // Maximum number of samples that will be trained, regardless of MSE
    int iterations = 0;
    int matches = 0;
    int error = 0;
    long elapsedTime = 0L;

    while (iterations++ < NUM_ITERATIONS)
    {
      long iterationStart = System.currentTimeMillis();

      double mse = 0.0;
      int count = 0;

      while (count++ < MAX_TRAINING_SAMPLES)
        mse = network.train(source.getRandomSample());
    
      Sample sample = source.getRandomSample();
      Sample sample2 = new SampleImpl();
      sample2.setInputs(sample.getInputs());
      sample2 = network.predict(sample2);

      int actualClass = MathUtil.findMaxValueIndex(sample.getOutputs());
      int predictedClass = MathUtil.findMaxValueIndex(sample2.getOutputs());

      matches += ((actualClass == predictedClass) ? 1 : 0);

      error += Math.abs(actualClass - predictedClass);
      
      long iterationFinish = System.currentTimeMillis();

      elapsedTime += (iterationFinish - iterationStart);
      
      log.info("Iteration:"+iterations);
      log.info("--");
      log.info("Actual class:"+actualClass);
      log.info("Predicted class:"+predictedClass);
      log.info("--");
      log.info("Actual Class Probability:"+sample.getOutputs()[MathUtil.findMaxValueIndex(sample.getOutputs())]);
      log.info("Predicted Class Probability:"+sample2.getOutputs()[MathUtil.findMaxValueIndex(sample2.getOutputs())]);
      log.info("--");
      log.info("Actual variance:"+MathUtil.variance(sample.getOutputs()));
      log.info("Predicted variance:"+MathUtil.variance(sample2.getOutputs()));
      log.info("--");
      log.info("Actual pairwise variance:"+MathUtil.pairwiseVariance(sample.getOutputs(), actualClass));
      log.info("Predicted pairwise variance:"+MathUtil.pairwiseVariance(sample2.getOutputs(), predictedClass));
      log.info("--");
      log.info("Iteration Elapsed Time: "+(iterationFinish - iterationStart)+"ms");
      log.info("-----------------------------------------");
    }
    
    long finish = System.currentTimeMillis();
    
    log.info("Total Elapsed Time: "+(finish-start)+"ms");
    log.info("Mean Iteration Elapsed Time: "+((double)elapsedTime/(double)NUM_ITERATIONS)+"ms");
    log.info("Total Matches: "+matches);
    log.info("Mean error:"+((double)error/(double)NUM_ITERATIONS));
    log.info("-----------------------------------------");
  }

  @Test
  public void testPersistence()
  throws Exception
  {
    int inputDimension  = 3;
    int hiddenDimension = 3;
    int outputDimension = 3;

    double[] inputs  = new double[] { 0.25, 0.75, 0.25};

    Network network = new NativeNetworkImpl(inputDimension, hiddenDimension, outputDimension);

    Sample sample = new SampleImpl();
    sample.setInputs(inputs);
    sample = network.predict(sample);

    log.info("-----------------------------------------");
    log.info("Network Dimensions: ("+inputDimension+":"+hiddenDimension+":"+outputDimension+")");
    log.info("Inputs : "+TimeUtil.arrayToString(inputs));
    log.info("Outputs: "+TimeUtil.arrayToString(sample.getOutputs()));
    log.info("-----------------------------------------");
    log.info(network.toString());
    log.info("-----------------------------------------");

    Network network2 = new NativeNetworkImpl(org.dt.bsa.xml.Network.Factory.parse(new java.io.StringReader(network.toString())));

    sample = network2.predict(sample);

    log.info("-----------------------------------------");
    log.info("Inputs : "+TimeUtil.arrayToString(inputs));
    log.info("Outputs: "+TimeUtil.arrayToString(sample.getOutputs()));
    log.info("-----------------------------------------");
    log.info(network2.toString());
    log.info("-----------------------------------------");

    log.info("Compare XML: " + network.toString().equals(network2.toString()));
  }

  @Test
  public void testHibernate()
  throws Exception
  {
    int inputDimension  = 3;
    int hiddenDimension = 8;
    int outputDimension = 4;

    double[] inputs  = new double[] { 0.25, 0.75, 0.25};

    Network network = new NativeNetworkImpl(inputDimension, hiddenDimension, outputDimension);

    Sample sample = new SampleImpl();
    sample.setInputs(inputs);
    sample = network.predict(sample);

    log.info("-----------------------------------------");
    log.info("Network Dimensions: ("+inputDimension+":"+hiddenDimension+":"+outputDimension+")");
    log.info("Inputs : "+TimeUtil.arrayToString(inputs));
    log.info("Outputs: "+TimeUtil.arrayToString(sample.getOutputs()));
    log.info("-----------------------------------------");
    log.info(network.toString());
    log.info("-----------------------------------------");

    network.save();
    log.info("Network saved, id="+network.getId());
    log.info("-----------------------------------------");

    Network network2 = new NativeNetworkImpl(network.getId());

    sample = network2.predict(sample);

    log.info("-----------------------------------------");
    log.info("Inputs : "+TimeUtil.arrayToString(inputs));
    log.info("Outputs: "+TimeUtil.arrayToString(sample.getOutputs()));
    log.info("-----------------------------------------");
    log.info(network2.toString());
    log.info("-----------------------------------------");

    log.info("Compare XML: " + network.toString().equals(network2.toString()));
  }

  @Test
  public void geneticBlackBoxGenomeTest()
  throws Exception
  {
    EvolutionManager ev = new BasicEvolutionManager(new BlackBoxGenomeFactory(), "geneticBlackBoxGenomeTest");
    ev.evolve();
  }

  @Test
  public void geneticThreadedBlackBoxGenomeTest()
  throws Exception
  {
    int numThreads = DEFAULT_NUM_THREADS;
    EvolutionManager ev = new ThreadedEvolutionManager(new BlackBoxGenomeFactory(), numThreads, "geneticThreadedBlackBoxGenomeTest");
    ev.evolve();
  }

  @Test
  public void geneticThreadedBlackBoxBinaryGenomeTest()
  throws Exception
  {
    int numThreads = DEFAULT_NUM_THREADS;
    EvolutionManager ev = new ThreadedEvolutionManager(new BlackBoxBinaryGenomeFactory(), numThreads, "geneticThreadedBlackBoxBinaryGenomeTest");
    ev.evolve();
  }

  @Test
  public void geneticNetworkGenomeTest()
  throws Exception
  {
    EvolutionManager ev = new BasicEvolutionManager(new NetworkGenomeFactory(new SinClassifierSourceFactory()), "geneticNetworkGenomeTest");
    ev.evolve();
  }

  @Test
  public void geneticThreadedNetworkGenomeTest_SinClassifierSource()
  throws Exception
  {
    int numThreads = DEFAULT_NUM_THREADS;
    EvolutionManager ev = new ThreadedEvolutionManager(new NetworkGenomeFactory(new SinClassifierSourceFactory()), numThreads, "geneticThreadedNetworkGenomeTest_SinClassifierSource");
    ev.setPopulationSize(6);
    ev.setNumGenerations(2);
    ev.evolve();
  }

  @Test
  public void geneticThreadedNetworkGenomeTest_TickerClassifierSource()
  throws Exception
  {
    int numThreads = DEFAULT_NUM_THREADS;
    String ticker = "AAPL";
    //String ticker = "spy";
    //String ticker = "^gspc";
    EvolutionManager ev = new ThreadedEvolutionManager(new NetworkGenomeFactory(new TickerClassifierSourceFactory(ticker)), numThreads, "geneticThreadedNetworkGenomeTest_TickerClassifierSource:"+ticker);
    //ev.setPopulationSize(6);
    //ev.setNumGenerations(2);
    ev.evolve();
  }

  @Test
  public void geneticBinaryGenomeTest()
  throws Exception
  {
    BinaryGenome g = new BinaryGenome(11) { public Genome clone() { return null; } public Phenotype createPhenotype() { return null; } };;
    System.out.println("\n"+g.toString());

    g.setNumberForRange(3, 1, 3);
    System.out.println("setNumberForRange(3,1,3):");
    System.out.println(g.toString());
    System.out.println("getNumberForRange(1,3):"+g.getNumberForRange(1,3));

    g.setNumberForRange(9, 6, 4);
    System.out.println("setNumberForRange(9,6,4):");
    System.out.println(g.toString());
    System.out.println("getNumberForRange(6,4):"+g.getNumberForRange(6,4));
  }

  @Test
  public void tickerSourceTest()
  throws Exception
  {
    //String ticker = "TEST";
    //int frameSize = 2;
    //int forwardOffset = 2;
    //int numClasses = 200;

    String ticker = "AAPL";
    int frameSize = 20;
    int forwardOffset = 5;
    int numClasses = 200;

    Calendar cal = new GregorianCalendar();
    cal.set(1985, Calendar.JANUARY, 16);
    Date date = cal.getTime();

    TickerClassifierSourceImpl source = new TickerClassifierSourceImpl(ticker, frameSize, forwardOffset, numClasses);
    TickerSampleImpl[] samples = source.getTickerSamples();

    log.info("---------------------------");
    log.info("Raw ticker data for "+ticker+":"+frameSize+":"+forwardOffset+":"+numClasses);
    for (int i = 0 ; i < samples.length ; i++)
      log.info("["+i+"]:"+samples[i].getSampleDate()+":"+samples[i].getAdjClosePrice());

    log.info("---------------------------");
    log.info("Classifier samples");
    int i = 0;
    for (Sample s : source)
    {
      StringBuffer sb = new StringBuffer();
      sb.append("["+(i++)+"]:[");
      for (int ii = 0 ; ii < s.getInputDimension() ; ii++)
	sb.append(s.getInputs()[ii]+",");
      sb.append("]:[");
      for (int ii = 0 ; ii < s.getOutputDimension() ; ii++)
	if (s.getOutputs()[ii]==1.0)
	{
	  sb.append(ii);
	  break;
	}
      sb.append("]");
      log.info(sb.toString());
    }

    log.info("---------------------------");
    log.info("Sample for date: "+date);
    Sample s = source.getInputSampleForDate(date);
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    for (int ii = 0 ; ii < s.getInputDimension() ; ii++)
      sb.append(s.getInputs()[ii]+",");
    sb.append("]");
    log.info(sb.toString());
  }
}
