//////////////////////////////////////////////////////////////////
//                                                              //
// Generation - one generation within an evolution sequence     //
//                                                              //
// David Tompkins - 9/25/2007                                   //
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

import java.util.Date;

import org.dt.bsa.util.BSAException;

public interface Generation
{
  float execute(EvolutionManager evolutionManager, Genome[] population, Genome[] children, Phenotype[] phenotypes, float[] fitness)
  throws EvolutionException, BSAException;

  void setEvolutionManager(EvolutionManager evolutionManager);
  void setPhenotype(Phenotype phenotype);
  int getId();
  int getIteration();
  void setIteration(int iteration);
  String getDescription();
  void setDescription(String description);
  Date getStartAt();
  void setStartAt(Date startAt);
  Date getEndAt();
  void setEndAt(Date endAt);
}
