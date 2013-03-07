//////////////////////////////////////////////////////////////////
//                                                              //
// TimeUtil - Time utility functions                            //
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

public class TimeUtil
{
  public static long MS_IN_SEC = 1000;
  public static long MS_IN_MIN = MS_IN_SEC*60;
  public static long MS_IN_HOUR = MS_IN_MIN*60;
  public static long MS_IN_DAY = MS_IN_HOUR*24;

  public static String arrayToString(double[] d)
  {
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    for (int i = 0 ; i < d.length-1 ; i++)
    {
      sb.append(d[i]);
      sb.append(",");
    }
    sb.append(d[d.length-1]);
    sb.append("]");
    return sb.toString();
  }

  public static String msToElapsedTime(long ms)
  {
    long days = ms / MS_IN_DAY;
    long hours = ms % MS_IN_DAY / MS_IN_HOUR;
    long mins = ms % MS_IN_DAY % MS_IN_HOUR / MS_IN_MIN;
    long secs = ms % MS_IN_DAY % MS_IN_HOUR % MS_IN_MIN / MS_IN_SEC;
    long msecs = ms % MS_IN_DAY % MS_IN_HOUR % MS_IN_MIN % MS_IN_SEC;
    return ""+days+"d:"+hours+"h:"+mins+"m:"+secs+"."+msecs+"s";
  }
}
