//////////////////////////////////////////////////////////////////
//                                                              //
// StandardizedInputsTransform - performs a standardization     //
//                               of the Source Sample inputs.   //
//                                                              //
// David Tompkins - 11/7/2007                                   //
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

package org.dt.bsa.data.impl;

import org.dt.bsa.data.Transform;
import org.dt.bsa.data.Source;
import org.dt.bsa.data.Sample;

public class StandardizedInputsTransform
implements Transform
{
  /*

  From http://www.faqs.org/faqs/ai-faq/neural-nets/part2/

  Notation:

   X = value of the raw input variable X for the ith training case
    i
   
   S = standardized value corresponding to X
    i                                       i
   
   N = number of training cases

                           
  Standardize X  to mean 0 and standard deviation 1:
             i   

          sum X
           i   i   
   mean = ------
             N
   
                              2
                sum( X - mean)
                 i    i
   std  = sqrt( --------------- )
                     N - 1
                           

       X  - mean
        i
   S = ---------
    i     std

                           
  Standardize X  to midrange 0 and range 2:
             i   

              max X  +  min X
               i   i     i   i
   midrange = ----------------
                     2


   range = max X  -  min X
            i   i     i   i


       X  - midrange
        i
   S = -------------
    i     range / 2
  
  */

  public StandardizedInputsTransform() { }

  public Source transform(Source source)
  {
    // Calculate mean
    double sum = 0.0;
    int count = 0;
    for (Sample s : source)
    {
      for (int i = 0 ; i < s.getInputDimension() ; i++)
      {
        sum += s.getInputs()[i];
	count++;
      }
    }
    double mean = sum / (double)count;

    // Calculate standard deviation
    sum = 0.0;
    for (Sample s : source)
    {
      for (int i = 0 ; i < s.getInputDimension() ; i++)
        sum += Math.pow((s.getInputs()[i] - mean), 2);
    }
    double standardDeviation = Math.sqrt(sum / (double)(count - 1));
    
    // Standardize input values
    for (Sample s : source)
    {
      for (int i = 0 ; i < s.getInputDimension() ; i++)
        s.getInputs()[i] = (s.getInputs()[i] - mean) / standardDeviation;
    }

    return source;
  }
}
