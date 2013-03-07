//////////////////////////////////////////////////////////////////
//                                                              //
// BlackBoxPhenotype - Implementation of the BlackBox phenotype //
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

package org.dt.bsa.genetic.impl;

import javax.persistence.*;

import org.dt.bsa.genetic.Phenotype;
import org.dt.bsa.genetic.EvolutionException;
import org.dt.bsa.util.BSAException;

@Entity
@DiscriminatorValue("black_box_phenotype")
public class BlackBoxPhenotype
extends BasePhenotypeImpl
implements Phenotype
{
  public static final int secret = 299792458;

  protected BlackBoxGenome genome;

  public BlackBoxPhenotype() { super(); }

  public BlackBoxPhenotype(BlackBoxGenome genome)
  {
    super("BlackBoxPhenotype");
    this.genome = genome;
  }

  @Transient
  public float getFitness()
  throws EvolutionException, BSAException
  {
    int fitness = 0;
    int mask = 1;

    // count matching bits between the chomosome and the secret
    for (int i = 0 ; i < 32 ; i++)
    {
      if ((this.genome.getChromosome() & mask) == (secret & mask))
        fitness++;
    
      mask <<= 1;
    }

    this.setTotalSamples(32);
    this.setTotalCorrect(fitness);

    return (float)fitness/32.0F;
  }
}
