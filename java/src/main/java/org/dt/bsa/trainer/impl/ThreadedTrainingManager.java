//////////////////////////////////////////////////////////////////
//                                                              //
// ThreadedTrainingManager                                      //
//                                                              //
// Unbounded Java Thread implementation of TrainingManager      //
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

package org.dt.bsa.trainer.impl;

import org.dt.bsa.trainer.Trainer;
import org.dt.bsa.trainer.TrainingManager;

import java.util.ArrayList;
import java.util.Iterator;

public class ThreadedTrainingManager
implements TrainingManager
{
  protected ThreadGroup threadGroup;
  protected ArrayList<Thread> threads;

  public ThreadedTrainingManager()
  {
    this.threadGroup = new ThreadGroup("BSA:ThreadedTrainingManager");
    this.threads = new ArrayList<Thread>();
  }

  public void add(Trainer trainer)
  {
    Thread t = new Thread(this.threadGroup, trainer, "BSA:Trainer["+this.threads.size()+"[");
    this.threads.add(t);
  }
  
  public void start()
  {
    Iterator<Thread> iterator = this.threads.iterator();
    while (iterator.hasNext())
      iterator.next().start();
  }

  public boolean isComplete()
  {
    return (this.threadGroup.activeCount() == 0);
  }

  public void waitForAll()
  {
    while (!this.isComplete())
    {
      try { Thread.sleep(1000); }
      catch (InterruptedException e) { }
    }
  }
}
