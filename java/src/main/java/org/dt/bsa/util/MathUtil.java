//////////////////////////////////////////////////////////////////
//                                                              //
// MathUtil - Math utility functions                            //
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

package org.dt.bsa.util;

public class MathUtil
{
  public static double variance(double[] d)
  {
    // The variance is equal to the mean of the squares minus the square of the mean

    // Mean of squares
    double sum = 0.0;
    for (int i = 0 ; i < d.length ; i++)
      sum += Math.pow(d[i], 2);
    double meanSquares = sum / (double)d.length;

    // Square of mean
    sum = 0.0;
    for (int i = 0 ; i < d.length ; i++)
      sum += d[i];
    double squareMean = Math.pow(sum / (double)d.length, 2);

    return meanSquares - squareMean;
  }

  public static double pairwiseVariance(double[] d, int index)
  {
    // The mean of the squares of the differences between the index element and all other elements

    double sum = 0.0;
    for (int i = 0 ; i < d.length ; i++)
    {
      if (i == index)
	continue;
      
      sum += Math.pow(d[i] - d[index], 2);
    }
    return sum / (double)(d.length - 1);
  }

  public static double sigmoid(double input)
  {
    return (1.0 / (1.0 + Math.exp(-input)));
  }

  public static int findMaxValueIndex(double[] d)
  {
    int index = 0;
    double maxVal = d[0];
    for (int i = 0 ; i < d.length ; i++)
    {
      if (d[i] > maxVal)
      {
	index = i;
	maxVal = d[i];
      }
    }
    return index;
  }
}
