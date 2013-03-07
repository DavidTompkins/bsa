//////////////////////////////////////////////////////////////////
//                                                              //
// BlackBoxGenome - Implementation of the BlackBox genome       //
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

import org.dt.bsa.genetic.Genome;
import org.dt.bsa.genetic.Generation;
import org.dt.bsa.genetic.Phenotype;
import org.dt.bsa.genetic.EvolutionException;
import org.dt.bsa.util.Random;

public class BlackBoxGenome
implements Genome
{
  protected static final Random random = Random.getInstance();

  protected int chromosome;
  protected Phenotype phenotype = null;

  public BlackBoxGenome()
  {
    this.chromosome = random.nextInt();
  }

  public BlackBoxGenome(int chromosome)
  {
    this.chromosome = chromosome;
  }

  public int getChromosome() { return this.chromosome; }

  public Genome clone()
  {
    return new BlackBoxGenome(this.chromosome);
  }

  public Genome crossover(Genome mate)
  throws EvolutionException
  {
    if (!(mate instanceof BlackBoxGenome))
      throw new EvolutionException("BlackBoxGenome:crossover:error:unable to mate with object of type "+mate.getClass().getName());

    int mask = 0xFFFF << (int)(random.nextFloat() * 32.0F);
    return new BlackBoxGenome((this.chromosome & mask) | (((BlackBoxGenome)mate).getChromosome() & (~mask)));
  }

  public void mutate()
  throws EvolutionException
  {
    // select bit to be changed
    int mask = 1 << (int)(random.nextFloat() * 32.0F);
                
    // flip the bit
    if ((this.chromosome & mask) != 0L)
      this.chromosome &= ~mask;
    else
      this.chromosome |= mask;
  }

  public Phenotype createPhenotype()
  throws EvolutionException
  {
    if (this.phenotype != null)
      return this.phenotype;

    this.phenotype = new BlackBoxPhenotype(this);
    return this.phenotype;
  }

  public String toString()
  {
    return ""+this.chromosome+"\n";
  }
}
