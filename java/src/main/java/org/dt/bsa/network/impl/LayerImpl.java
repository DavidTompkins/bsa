//////////////////////////////////////////////////////////////////
//                                                              //
// LayerImpl - Base Network Layer implementation                //
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

import org.dt.bsa.network.Layer;
import org.dt.bsa.network.Node;

public class LayerImpl
implements Layer
{
  protected Node[] nodes;
  protected int inputDimension;

  public LayerImpl(int outputDimension, int inputDimension)
  {
    this(outputDimension, inputDimension, true);
  }

  public LayerImpl(int outputDimension, int inputDimension, boolean randomInit)
  {
    this.inputDimension = inputDimension;
    this.nodes = new Node[outputDimension];
    for (int i = 0 ; i < this.nodes.length ; i++)
      this.nodes[i] = createNode(this.inputDimension, randomInit);
  }

  public LayerImpl(org.dt.bsa.xml.Layer layer)
  {
    this.inputDimension = layer.getInputDimension();
    this.nodes = new Node[layer.getOutputDimension()];
    for (int i = 0 ; i < this.nodes.length ; i++)
      this.nodes[i] = createNode(layer.getNodes().getNodeArray(i));
  }

  protected Node createNode(int inputDimension, boolean randomInit) { return new NodeImpl(inputDimension, randomInit); }
  protected Node createNode(org.dt.bsa.xml.Node node) { return new NodeImpl(node); }

  public double[] predict(double[] inputs)
  {
    double[] outputs = new double[this.nodes.length];
    for (int i = 0 ; i < this.nodes.length ; i++)
      outputs[i] = this.nodes[i].predict(inputs);
    return outputs;
  }

  public int getInputDimension() { return this.inputDimension; }
  public int getOutputDimension() { return this.nodes.length; }
  public Node[] getNodes() { return this.nodes; }
  public Node getNode(int index) { return this.nodes[index]; }

  public org.dt.bsa.xml.Layer toXml()
  {
    org.dt.bsa.xml.Layer layer = org.dt.bsa.xml.Layer.Factory.newInstance();
    layer.setInputDimension(this.inputDimension);
    layer.setOutputDimension(this.nodes.length);
    layer.addNewNodes();
    for (int i = 0 ; i < this.nodes.length ; i++)
    {
      layer.getNodes().insertNewNode(i);
      layer.getNodes().setNodeArray(i, this.nodes[i].toXml());
    } 
    return layer;
  }
}
