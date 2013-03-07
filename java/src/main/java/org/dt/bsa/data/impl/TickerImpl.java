//////////////////////////////////////////////////////////////////
//                                                              //
// TickerImpl - Time series ticker data                         //
//                                                              //
// David Tompkins - 10/25/2007                                  //
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

import java.util.Set;
import java.util.HashMap;

import javax.persistence.*;

import org.dt.bsa.util.HibernateUtil;
import org.dt.bsa.util.BSAException;

@Entity
@Table(name="tickers")
public class TickerImpl
{
  protected static HashMap cache = new HashMap();

  public TickerImpl() { }

  protected int id;
  protected String name;
  protected Set<TickerSampleImpl> samples;

  @Id
  @GeneratedValue
  public int getId() { return this.id; }
  public void setId(int id) { this.id = id; }

  @Basic
  public String getName() { return this.name; }
  public void setName(String name) { this.name = name; }

  @OneToMany(targetEntity=TickerSampleImpl.class, fetch=FetchType.EAGER)
  @JoinColumn(name="ticker_id")
  @OrderBy("sampleDate ASC")
  public Set<TickerSampleImpl> getSamples() { return this.samples; }
  public void setSamples(Set<TickerSampleImpl> samples) { this.samples = samples; }

  @Transient
  public static TickerSampleImpl[] getTickerSamplesArray(String name)
  throws BSAException
  {
    TickerSampleImpl[] sampleArray = (TickerSampleImpl[])cache.get(name);
    if (sampleArray != null)
      return sampleArray;

    HashMap<String,String> args = new HashMap<String,String>();
    args.put("name", name);
    TickerImpl tickerImpl = (TickerImpl)HibernateUtil.query("from org.dt.bsa.data.impl.TickerImpl as ticker where ticker.name = :name", args).get(0);
    sampleArray = tickerImpl.getSamples().toArray(new TickerSampleImpl[1]);
    cache.put(name, sampleArray);
    return sampleArray;
  }
}
