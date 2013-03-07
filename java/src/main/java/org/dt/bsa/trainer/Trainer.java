//////////////////////////////////////////////////////////////////
//                                                              //
// Trainer - Thread runnable network trainer                    //
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

import java.util.HashMap;

import org.dt.bsa.network.Network;
import org.dt.bsa.data.Source;

public abstract class Trainer
implements Runnable
{
  public static final String REPORT_INITIATED = "Training initiated";
  public static final String REPORT_IN_PROGRESS = "Training in progress";
  public static final String REPORT_ERROR = "Training error";

  protected Network network;
  protected Source source;
  protected String label;
  protected boolean complete;
  protected long elapsedTime;
  protected String report;
  protected HashMap results;

  public Trainer(Network network, Source source, String label)
  {
    this.network = network;
    this.source = source;
    this.label = label;
    this.complete = false;
    this.report = this.label+":"+REPORT_INITIATED;
    this.results = new HashMap();
    this.network.setTrainingDescription(this.source.getSourceDescription());
  }

  public abstract long train() throws Exception;
  public abstract String generateReport() throws Exception;

  public Network getNetwork() { return this.network; }
  public Source getSource() { return this.source; }
  public String getLabel() { return this.label; }
  public boolean isComplete() { return this.complete; }
  public long getElapsedTime() { return this.elapsedTime; }
  public String getReport() { return this.report; }
  public HashMap getResults() { return this.results; }

  public void cleanup()
  {
    this.network = null;
    this.source = null;
  }

  public void run()
  {
    this.report = this.label+":"+REPORT_IN_PROGRESS;
    try { this.elapsedTime = this.train(); }
    catch (Exception e)
    {
      this.report = this.label+":"+REPORT_ERROR+":"+e.getMessage();
      return;
    }
    this.complete = true;
    try { this.report = this.generateReport(); }
    catch (Exception e)
    {
      this.report = this.label+":"+REPORT_ERROR+":"+e.getMessage();
      return;
    }
  }
}
