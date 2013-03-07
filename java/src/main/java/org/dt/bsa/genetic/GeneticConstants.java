//////////////////////////////////////////////////////////////////
//                                                              //
// GeneticConstants - general constants for the genetic package //
//                                                              //
// David Tompkins - 9/29/2007                                   //
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

package org.dt.bsa.genetic;

public interface GeneticConstants
{
  // Status constants
  public static final int STATUS_INITIALIZED = 0;
  public static final int STATUS_RUNNING = 1;
  public static final int STATUS_COMPLETE = 2;
  public static final int STATUS_ERROR = 3;
  public static final int STATUS_COMPLETE_REQUESTED = 10;

  // Evolution constants
  public static final int DEFAULT_POPULATION_SIZE = 100;
  public static final int DEFAULT_NUM_GENERATIONS = 100;
  public static final float DEFAULT_CROSSOVER_RATE = 1.0F;
  public static final float DEFAULT_MUTATION_RATE = 0.1F;
  public static final boolean DEFAULT_ELITISM_ENABLED = true;
  public static final boolean DEFAULT_FITNESS_SCALAING_ENABLED = true;
}
