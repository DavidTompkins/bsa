//////////////////////////////////////////////////////////////////
//                                                              //
// TickerSampleImpl - Time series ticker data sample            //
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

import java.util.Date;
import java.math.BigInteger;

import javax.persistence.*;

@Entity
@Table(name="samples")
public class TickerSampleImpl
{
  public TickerSampleImpl() { }

  protected int id;
  protected Date sampleDate;
  protected float adjClosePrice;
  protected BigInteger volume;

  @Id
  @GeneratedValue
  public int getId() { return this.id; }
  public void setId(int id) { this.id = id; }

  @Basic
  public Date getSampleDate() { return this.sampleDate; }
  public void setSampleDate(Date sampleDate) { this.sampleDate = sampleDate; }

  @Basic
  public float getAdjClosePrice() { return this.adjClosePrice; }
  public void setAdjClosePrice(float adjClosePrice) { this.adjClosePrice = adjClosePrice; }

  @Basic
  public BigInteger getVolume() { return this.volume; }
  public void setVolume(BigInteger volume) { this.volume = volume; }
}
