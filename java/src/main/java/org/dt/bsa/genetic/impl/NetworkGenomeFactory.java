//////////////////////////////////////////////////////////////////
//                                                              //
// NetworkGenomeFactory - factory for NetworkGenome objects     //
//                                                              //
// David Tompkins - 8/9/2007                                    //
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

package org.dt.bsa.genetic.impl;

import org.dt.bsa.genetic.Genome;
import org.dt.bsa.genetic.GenomeFactory;
import org.dt.bsa.genetic.SourceFactory;

public class NetworkGenomeFactory
implements GenomeFactory
{
  public static final int DEFAULT_PREDICT_SAMPLES = 1000;

  protected SourceFactory sourceFactory;

  public NetworkGenomeFactory(SourceFactory sourceFactory) { this.sourceFactory = sourceFactory; }

  public Genome getRandomGenome()
  {
    return new NetworkGenome(this.sourceFactory, DEFAULT_PREDICT_SAMPLES);
  }
}
