//////////////////////////////////////////////////////////////////
//                                                              //
// RandomImpl - Random number generator implementation          //
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

package org.dt.bsa.util.impl;

import org.dt.bsa.util.Random;

public class RandomImpl
extends Random
{
   protected static java.util.Random random = new java.util.Random(System.currentTimeMillis());

   public RandomImpl() { }

   public double nextDouble() { return random.nextDouble(); }
   public float nextFloat() { return random.nextFloat(); }
   public boolean nextBoolean() { return random.nextBoolean(); }
   public int nextInt() { return random.nextInt(); }
   public int nextInt(int range) { return random.nextInt(range); }
}
