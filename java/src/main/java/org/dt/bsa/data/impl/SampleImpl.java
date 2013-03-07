//////////////////////////////////////////////////////////////////
//                                                              //
// SampleImpl - base data sample implementation                 //
//                                                              //
// David Tompkins - 4/27/2007                                   //
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

package org.dt.bsa.data.impl;

import org.dt.bsa.data.Sample;

public class SampleImpl
implements Sample
{
  protected double[] inputs = null;
  protected double[] outputs = null;

  public SampleImpl() { }

  public SampleImpl(double[] inputs, double[] outputs)
  {
    this.inputs = inputs;
    this.outputs = outputs;
  }
  public double[] getInputs() { return this.inputs; }
  public double[] getOutputs() { return this.outputs; }
  public void setInputs(double[] inputs) { this.inputs = inputs; }
  public void setOutputs(double[] outputs) { this.outputs = outputs; }
  public int getInputDimension() { return ((this.inputs==null)?0:this.inputs.length); }
  public int getOutputDimension() { return ((this.outputs==null)?0:this.outputs.length); }
}
