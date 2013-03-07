//////////////////////////////////////////////////////////////////
//                                                              //
// EvolutionManager - Manages a genetic optimization sequence   //
//                                                              //
// David Tompkins - 8/7/2007                                    //
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

import org.dt.bsa.util.BSAException;

public interface EvolutionManager
extends Runnable
{
  void evolve() throws EvolutionException, BSAException;
  int evaluatePhenotypes(Generation generation) throws EvolutionException, BSAException;
  int getPopulationSize();
  void setPopulationSize(int populationSize);
  int getNumGenerations();
  void setNumGenerations(int numGenerations);
  float getCrossoverRate();
  void setCrossoverRate(float crossoverRate);
  public float getMutationRate();
  public void setMutationRate(float mutationRate);
  boolean getElitism();
  void setElitism(boolean elitismEnabled);
  boolean getFitnessScaling();
  void setFitnessScaling(boolean fitnessScalingEnabled);
  int getId();
  void save() throws BSAException;
  void refresh() throws BSAException;
}
