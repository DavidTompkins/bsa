//////////////////////////////////////////////////////////////////
//                                                              //
// BlackBoxBinaryPhenotype - Implementation of the BlackBox     //
//                           phenotype using a BinaryGenome     //
//                                                              //
// David Tompkins - 11/17/2007                                  //
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
@DiscriminatorValue("black_box_binary_phenotype")
public class BlackBoxBinaryPhenotype
extends BasePhenotypeImpl
implements Phenotype
{
  // secret is a 32-bit BitString
  public static final BinaryGenome secret = new BlackBoxBinaryGenome("01001110101000110011111000110001");

  protected BlackBoxBinaryGenome genome;

  public BlackBoxBinaryPhenotype() { super(); }

  public BlackBoxBinaryPhenotype(BlackBoxBinaryGenome genome)
  {
    super("BlackBoxBinaryPhenotype");
    this.genome = genome;
  }

  @Transient
  public float getFitness()
  throws EvolutionException, BSAException
  {
    int fitness = 0;

    // count matching bits between the BinaryGenome BitSet and the secret BinaryGenome
    for (int i = 0 ; i < secret.getSize() ; i++)
    {
      if (this.genome.getBitSet().get(i) == secret.getBitSet().get(i))
        fitness++;
    }

    this.setTotalSamples(secret.getSize());
    this.setTotalCorrect(fitness);

    return (float)fitness/(float)secret.getSize();
  }
}
