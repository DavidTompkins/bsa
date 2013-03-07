//////////////////////////////////////////////////////////////////
//                                                              //
// Node - Network Node interface                                //
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

public interface Node
{
  /**
   * Predicts the Node output for the specified inputs.
   */
  double predict(double[] inputs);

  /**
   * Returns the input dimension of the Node
   */
  int getInputDimension();

  /**
   * Returns the input weights of this Node in an array
   */
  double[] getWeights();
  
  /**
   * Returns the input weight for the input specified by the index
   */
  double getWeight(int index);

  /**
   * Sets the input weights of this Node
   */
  void setWeights(double[] weights);

  /**
   * Sets the input weight for the input specified by the index
   */
  void setWeight(int index, double weight);

  /**
   * Returns the persisted XML representation of this Node
   */
  org.dt.bsa.xml.Node toXml();
}
