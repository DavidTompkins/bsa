//////////////////////////////////////////////////////////////////
//                                                              //
// network.c - native Network implementations                   //
//                                                              //
// David Tompkins - 7/19/2007                                   //
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

#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>

#ifdef USE_AMD_MATH_LIB
#include <acml.h>
#endif

#include "network.h"

#ifdef __cplusplus
extern "C" {
#endif

Network **networks = NULL;
pthread_mutex_t lock = PTHREAD_MUTEX_INITIALIZER;

int
registerNetwork()
{
  pthread_mutex_lock(&lock);

  if (networks == NULL)
  {
    networks = (Network **)malloc(MAX_NETWORKS * sizeof(Network *));
    for (int i = 0 ; i < MAX_NETWORKS ; i++)
      networks[i] = NULL;
  }

  for (int i = 0 ; i < MAX_NETWORKS ; i++)
  {
    if (networks[i] == NULL)
    {
      networks[i] = (Network *)malloc(sizeof(Network));
      pthread_mutex_unlock(&lock);
      return i;
    }
  }

  pthread_mutex_unlock(&lock);
  return NO_NETWORK_AVAILABLE;
}

JNIEXPORT jint JNICALL
Java_org_dt_bsa_network_impl_NativeNetworkImpl_registerNetwork
(JNIEnv * env, jobject jobj, jint jinputDimension, jint jhiddenDimension, jint joutputDimension, jdouble jlearningRate, jdoubleArray jhiddenLayer, jdoubleArray joutputLayer)
{
  int networkId = registerNetwork();

  if (networkId == NO_NETWORK_AVAILABLE)
    return NO_NETWORK_AVAILABLE;

  Network *network = networks[networkId];

  network->inputDimension = jinputDimension;
  network->hiddenDimension = jhiddenDimension;
  network->outputDimension = joutputDimension;
  network->learningRate = jlearningRate;

  // store network weights in C space
  network->hiddenLayer = env->GetDoubleArrayElements(jhiddenLayer, 0);
  network->outputLayer = env->GetDoubleArrayElements(joutputLayer, 0);

  return networkId;
}

JNIEXPORT void JNICALL
Java_org_dt_bsa_network_impl_NativeNetworkImpl_retrieveNetwork
(JNIEnv * env, jobject jobj, jint networkId, jdoubleArray jhiddenLayer, jdoubleArray joutputLayer)
{
  Network *network = networks[networkId];

  // return network weights to java space
  env->ReleaseDoubleArrayElements(jhiddenLayer, network->hiddenLayer, 0);
  env->ReleaseDoubleArrayElements(joutputLayer, network->outputLayer, 0);
}

JNIEXPORT void JNICALL
Java_org_dt_bsa_network_impl_NativeNetworkImpl_removeNetwork
(JNIEnv * env, jobject jobj, jint networkId)
{
  Network *network = networks[networkId];

  if (network == NULL)
    return;

  free(network);
  networks[networkId] = NULL;
}

JNIEXPORT jdoubleArray JNICALL
Java_org_dt_bsa_network_impl_NativeNetworkImpl_predictNative
(JNIEnv * env, jobject jobj, jint networkId, jdoubleArray jinputs)
{
  Network *network = networks[networkId];

  jdouble* inputs = env->GetDoubleArrayElements(jinputs, 0);

  double *hiddenLayerOutputs = (double *)malloc(network->hiddenDimension * sizeof(double));;
  register double *p1 = network->hiddenLayer;
  register double *p2 = hiddenLayerOutputs;
  for (int node = 0 ; node < network->hiddenDimension ; node++)
  {
    double sum = 0.0;
    register double *p3 = inputs;
    for (int weight = 0 ; weight < network->inputDimension ; weight++)
#ifdef USE_AMD_MATH_LIB
      sum = ddot(network->inputDimension, p1, 1, p3, 1);
    p1 += network->inputDimension;
#else
      sum += (*(p1++))*(*(p3++));
#endif
      //sum += hiddenLayer[node*inputDimension+weight]*inputs[weight];
    (*(p2++)) = SIGMOID(sum);
  }

  double *outputLayerOutputs = (double *)malloc(network->outputDimension * sizeof(double));;
  p1 = network->outputLayer;
  p2 = outputLayerOutputs;
  for (int node = 0 ; node < network->outputDimension ; node++)
  {
    double sum = 0.0;
    register double *p3 = hiddenLayerOutputs;
    for (int weight = 0 ; weight < network->hiddenDimension ; weight++)
#ifdef USE_AMD_MATH_LIB
      sum = ddot(network->hiddenDimension, p1, 1, p3, 1);
    p1 += network->hiddenDimension;
#else
      sum += (*(p1++))*(*(p3++));
#endif
      //sum += outputLayer[node*hiddenDimension+weight]*hiddenLayerOutputs[weight];
    (*(p2++)) = SIGMOID(sum);
  }
  
  env->ReleaseDoubleArrayElements(jinputs, inputs, 0);

  // Return values to java space
  jdoubleArray outputs = env->NewDoubleArray(network->outputDimension);
  env->SetDoubleArrayRegion(outputs, 0, network->outputDimension, outputLayerOutputs);

  // free heap memory
  free(hiddenLayerOutputs);
  free(outputLayerOutputs);

  return outputs;
}

double
train(Network *network, double *inputs, double *outputs)
{
  double *hiddenLayerOutputs = (double *)malloc(network->hiddenDimension * sizeof(double));;
  register double *p1 = network->hiddenLayer;
  register double *p2 = hiddenLayerOutputs;
  for (int node = 0 ; node < network->hiddenDimension ; node++)
  {
    register double sum = 0.0;
    register double *p3 = inputs;
    for (int weight = 0 ; weight < network->inputDimension ; weight++)
#ifdef USE_AMD_MATH_LIB
      sum = ddot(network->inputDimension, p1, 1, p3, 1);
    p1 += network->inputDimension;
#else
      sum += (*(p1++))*(*(p3++));
#endif
      //sum += hiddenLayer[node*inputDimension+weight]*inputs[weight];
    (*(p2++)) = SIGMOID(sum);
  }

  double *outputLayerOutputs = (double *)malloc(network->outputDimension * sizeof(double));;
  p1 = network->outputLayer;
  p2 = outputLayerOutputs;
  for (int node = 0 ; node < network->outputDimension ; node++)
  {
    register double sum = 0.0;
    register double *p3 = hiddenLayerOutputs;
    for (int weight = 0 ; weight < network->hiddenDimension ; weight++)
#ifdef USE_AMD_MATH_LIB
      sum = ddot(network->hiddenDimension, p1, 1, p3, 1);
    p1 += network->hiddenDimension;
#else
      sum += (*(p1++))*(*(p3++));
#endif
      //sum += outputLayer[node*hiddenDimension+weight]*hiddenLayerOutputs[weight];
    (*(p2++)) = SIGMOID(sum);
  }
  
  double mse = 0.0; // mean squared error

  register double eta = network->learningRate; // learning rate (eta) for the network

  double *outputError = (double *)malloc(network->outputDimension * sizeof(double));
  double *hiddenError = (double *)malloc(network->hiddenDimension * sizeof(double));

  // Calculate output differences and mean squared error
  for (int i = 0 ; i < network->outputDimension ; i++)
  {
    outputError[i] = outputs[i] - outputLayerOutputs[i];
    mse += (outputError[i] * outputError[i]);
    outputError[i] *= outputLayerOutputs[i] * (1 - outputLayerOutputs[i]);
  }
  
  // Calculate hidden layer error terms
  for (int i = 0 ; i < network->hiddenDimension ; i++)
  {
    double sum = 0.0;
    for (int ii = 0 ; ii < network->outputDimension ; ii++)
      sum += outputError[ii] * network->outputLayer[ii*network->hiddenDimension+i];
    hiddenError[i] = sum * hiddenLayerOutputs[i] * (1 - hiddenLayerOutputs[i]);
  }
  
  // Update output weights
  p1 = network->outputLayer;
  for (int i = 0 ; i < network->outputDimension ; i++)
    for (int ii = 0 ; ii < network->hiddenDimension ; ii++)
      (*(p1++)) += (eta * outputError[i] * hiddenLayerOutputs[ii]);
      //outputLayer[i*hiddenDimension+ii] = outputLayer[i*hiddenDimension+ii] + (eta * outputError[i] * hiddenLayerOutputs[ii]);
  
  // Update hidden weights
  p1 = network->hiddenLayer;
  for (int i = 0 ; i < network->hiddenDimension ; i++)
    for (int ii = 0 ; ii < network->inputDimension ; ii++)
      (*(p1++)) += (eta * hiddenError[i] * inputs[ii]);
      //hiddenLayer[i*inputDimension+ii] = hiddenLayer[i*inputDimension+ii] + (eta * hiddenError[i] * inputs[ii]);
  
  // free heap memory
  free(hiddenLayerOutputs);
  free(outputLayerOutputs);
  free(hiddenError);
  free(outputError);

  // return mean squared error
  return mse;
}

JNIEXPORT jdouble JNICALL Java_org_dt_bsa_network_impl_NativeNetworkImpl_trainNative__ILjava_nio_DoubleBuffer_2Ljava_nio_DoubleBuffer_2I
(JNIEnv * env, jobject jobj, jint networkId, jobject jinputs, jobject joutputs, jint size)
{
  printf("double size is %d\n", (int)sizeof(double));
  Network *network = networks[networkId];

  jdouble *inputs = (jdouble*)env->GetDirectBufferAddress(jinputs);
  jdouble *outputs = (jdouble*)env->GetDirectBufferAddress(joutputs);

  double mse = 0.0;
  double *inputsFrame = inputs;
  double *outputsFrame = outputs;
  for (int i = 0 ; i < size ; i++)
  {
    mse += train(network, inputsFrame, outputsFrame);
    inputsFrame += network->inputDimension;
    outputsFrame += network->outputDimension;
  }
  
  return mse;
}

JNIEXPORT jdouble JNICALL
Java_org_dt_bsa_network_impl_NativeNetworkImpl_trainNative__I_3D_3D
(JNIEnv * env, jobject jobj, jint networkId, jdoubleArray jinputs, jdoubleArray joutputs)
{
  Network *network = networks[networkId];

  jdouble* inputs = env->GetDoubleArrayElements(jinputs, 0);
  jdouble* outputs = env->GetDoubleArrayElements(joutputs, 0);

  double mse = train(network, inputs, outputs);
  
  // Return values to java space
  env->ReleaseDoubleArrayElements(jinputs, inputs, 0);
  env->ReleaseDoubleArrayElements(joutputs, outputs, 0);

  return mse;
}

#ifdef __cplusplus
}
#endif
