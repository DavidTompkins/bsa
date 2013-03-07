//////////////////////////////////////////////////////////////////
//                                                              //
// Network - Network interface                                  //
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

package org.dt.bsa.network;

import org.dt.bsa.data.Source;
import org.dt.bsa.data.Sample;
import org.dt.bsa.genetic.Phenotype;
import org.dt.bsa.util.BSAException;

public interface Network
{
  /**
   * Maximum acceptable mean squared error
   */
  public static double MAX_MSE = 0.0000000005;

  /**
   * Training rate (eta) for gradient descent adjustment
   */
  public static double DEFAULT_LEARNING_RATE = 0.2;

  /**
   * Array index of hidden layer in layer arrays
   */
  public static int HIDDEN_LAYER_INDEX = 0;

  /**
   * Array index of output layer in layer arrays
   */
  public static int OUTPUT_LAYER_INDEX = 1;

  /**
   * Trains the network using the specified sample.
   * Returns the mean squared error of the outputs for the
   * specified sample.
   */
  double train(Sample sample) throws NetworkException;

  /**
   * Trains the network using the specified source.
   * Returns the total mean squared error of the outputs for the
   * specified source.
   */
  double train(Source source) throws NetworkException;
  
  /**
   * Predicts the network outputs for the specified sample.
   * The outputs are stored in the sample.
   */
  Sample predict(Sample sample) throws NetworkException;

  /**
   * Returns the input dimension of the Network
   */
  int getInputDimension();

  /**
   * Returns the hidden dimension of the Network
   */
  int getHiddenDimension();

  /**
   * Returns the output dimension of the Network
   */
  int getOutputDimension();

  /**
   * Returns the learning rate of the Network
   */
  double getLearningRate();

  /**
   * Returns the persisted XML representation of this Layer
   **/
  org.dt.bsa.xml.Network toXml();

  /**
   * Configures this network to match the specified XML representation of this Layer
   **/
  void fromXml(org.dt.bsa.xml.Network network);

  /**
   * Signals the network that it may free any resources it holds.
   **/
  void shutdown() throws NetworkException;

  /**
   * Signals the network to persist itself
   **/
  void save() throws BSAException;

  /**
   * Returns the globally unique network id
   **/
  int getId();

  /**
   * Associates this network with the specified Phenotype
   **/
  void setPhenotype(Phenotype phenotype);

  /**
   * Stores a description of of the training regimen
   **/
  void setTrainingDescription(String trainingDescription);
}
