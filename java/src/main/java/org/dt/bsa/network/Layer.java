//////////////////////////////////////////////////////////////////
//                                                              //
// Layer - Network Layer interface                              //
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

public interface Layer
{
  /**
   * Predicts the layer outputs for the specified inputs.
   */
  double[] predict(double[] inputs);

  /**
   * Returns the input dimension of the Layer
   */
  int getInputDimension();

  /**
   * Returns the output dimension of the Layer
   */
  int getOutputDimension();

  /**
   * Returns the Nodes of this Layer in an array
   */
  Node[] getNodes();
  
  /**
   * Returns the Node specified by the index
   */
  Node getNode(int index);

  /**
   * Returns the persisted XML representation of this Layer
   */
  org.dt.bsa.xml.Layer toXml();
}
