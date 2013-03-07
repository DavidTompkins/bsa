//////////////////////////////////////////////////////////////////
//                                                              //
// BinaryGenome - Implementation of a binary Genome that uses   //
//                Gray coding of the bits and allows fragments  //
//                of the total string to contain parameter      //
//                values.                                       //
//                                                              //
// David Tompkins - 8/8/2007                                    //
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

import java.util.BitSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dt.bsa.genetic.Genome;
import org.dt.bsa.genetic.Phenotype;
import org.dt.bsa.genetic.EvolutionException;
import org.dt.bsa.util.Random;

public abstract class BinaryGenome
implements Genome
{
  // The bit string has the following form:
  // 
  // bitstring: 1111010101
  // index    : 0123456789
  //
  // Subsets of the bit string may contain gray-encoded binary numbers
  //
  // Offset and length are measured from the left end of the bit string

  final Logger log = LoggerFactory.getLogger(BinaryGenome.class);
  protected static final Random random = Random.getInstance();

  protected BitSet bits;
  protected int size;

  public BinaryGenome(int size)
  {
    this.bits = new BitSet(size);
    this.bits.clear();
    this.size = size;
  }

  public BinaryGenome(int size, BitSet bits)
  {
    this.bits = (BitSet)bits.clone();
    this.size = size;
  }

  public BinaryGenome(BinaryGenome binaryGenome)
  {
    this.bits = (BitSet)binaryGenome.getBitSet().clone();
    this.size = binaryGenome.getSize();
  }

  public BinaryGenome(String bitString)
  {
    this(bitString.length());
    for (int i = 0 ; i < this.size ; i++)
      this.bits.set(i, (bitString.charAt(i)=='1'));
  }

  public BitSet getBitSet() { return this.bits; }

  public int getSize() { return this.size; }

  public int getNumberForRange(int offset, int length)
  {
    BitSet grayBits = this.bits.get(offset, offset+length);
    return grayDecode(grayBits, length);
  }

  public void setNumberForRange(int binaryNumber, int offset, int length)
  throws EvolutionException
  {
    if (binaryNumber > ((0x1 << length)-1))
      throw new EvolutionException("BinaryGenome:setNumberForRange:number ("+binaryNumber+") is greater than max value for binary length ("+length+":"+((0x1 << length)-1)+")");

    // Gray encode the binaryNumber into bits
    BitSet grayBits = grayEncode(binaryNumber, length);

    // Copy gray-encoded bits into the bits string
    for (int i = 0 ; i < length ; i++)
      this.bits.set(offset+i, grayBits.get(i));
  }
  
  protected BitSet grayEncode(int binaryNumber, int length)
  {
    // A java int is a 32-bit signed, twos-complement encoded number.
    // Need to convert to unsigned format, then Gray encode the bits.

    // Convert to unsigned binary format
    BitSet unsignedBits = new BitSet(length);
    for (int i = 0 ; i < length ; i++)
    {
      int mask = 0x1 << (length-1-i);
      unsignedBits.set(i, ((binaryNumber & mask) > 0));
    }

    // Gray encode the bits
    BitSet grayBits = new BitSet(length);
    grayBits.set(0, unsignedBits.get(0));
    for (int i = 1 ; i < length ; i++)
      grayBits.set(i, (unsignedBits.get(i) ^ unsignedBits.get(i-1)));

    //log.info("GrayEncode: "+intToBinaryString(binaryNumber, length)+" -> "+bitSetToString(unsignedBits, length)+" -> "+bitSetToString(grayBits, length));
    return grayBits;
  }

  protected int grayDecode(BitSet grayBits, int length)
  {
    // Gray decode the bits
    BitSet unsignedBits = (BitSet)grayBits.clone();
    for (int i = 1 ; i < length ; i++)
      unsignedBits.set(i, (grayBits.get(i) ^ unsignedBits.get(i-1)));

    // Convert the unsigned bits back into a 32-bit signed, twos-complement java integer
    int binaryNumber = 0;
    for (int i = 0 ; i < length ; i++)
    {
      int mask = 0x1 << (length-1-i);
      if (unsignedBits.get(i))
	binaryNumber |= mask;
      else
	binaryNumber &= ~mask;
    }

    //log.info("GrayDecode: "+bitSetToString(grayBits, length)+" -> "+intToBinaryString(binaryNumber, length));
    return binaryNumber;
  }

  public Genome crossover(Genome mate)
  throws EvolutionException
  {
    if (!(mate instanceof BinaryGenome) || (((BinaryGenome)mate).getSize() != this.size))
      throw new EvolutionException("BinaryGenome:crossover:error:unable to mate with object of type "+mate.getClass().getName());

    int cross = random.nextInt(this.size);

    BinaryGenome offspring = (BinaryGenome)this.clone();
    for (int i = 0 ; i < cross ; i++)
      offspring.getBitSet().set(i, this.bits.get(i));
    for (int i = cross ; i < this.size ; i++)
      offspring.getBitSet().set(i, ((BinaryGenome)mate).getBitSet().get(i));

    return offspring;
  }

  public void mutate()
  throws EvolutionException
  {
    // select bit to be changed
    int index = random.nextInt(this.size);
    this.bits.flip(index);
  }

  protected void randomizeBits()
  {
    for (int i = 0 ; i < this.size ; i++)
      this.bits.set(i, random.nextBoolean());
  }

  public abstract Phenotype createPhenotype() throws EvolutionException;
  public abstract Genome clone();

  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    for (int i = 0 ; i < this.size ; i++)
      sb.append((this.bits.get(i)?"1":"0"));
    sb.append("]");
    return "BinaryGenome:"+sb.toString();
  }

  protected String intToBinaryString(int number, int length)
  {
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    for (int i = 0 ; i < length ; i++)
    {
      int mask = 0x1 << (length-1-i);
      sb.append((((number & mask) > 0)?"1":"0"));
    }
    sb.append("]");
    return sb.toString();
  }

  protected String bitSetToString(BitSet bitSet, int length)
  {
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    for (int i = 0 ; i < length ; i++)
      sb.append((bitSet.get(i)?"1":"0"));
    sb.append("]");
    return sb.toString();
  }
}
