//////////////////////////////////////////////////////////////////
//                                                              //
// TrainingManager - Manages execution for a pool of Trainers   //
//                                                              //
// David Tompkins - 7/2/2007                                    //
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

package org.dt.bsa.trainer;

public interface TrainingManager
{
  /**
   * Adds a trainer instance to this TrainingManager
   */
  void add(Trainer trainer);

  /**
   * Commence execution of the trainers in this TrainingManager
   */
  void start();

  /**
   * Returns true if all trainers have completed
   */
  boolean isComplete();

  /**
   * Blocks until all trainers have completed
   */
  void waitForAll();
}
