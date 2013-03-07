//////////////////////////////////////////////////////////////////
//                                                              //
// HibernateUtil - Hibernate utility functions                  //
//                                                              //
// David Tompkins - 8/15/2007                                   //
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

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import org.dt.bsa.network.impl.BaseNetworkImpl;

public class HibernateUtil
{
  final static Logger log = LoggerFactory.getLogger(HibernateUtil.class);

  protected static SessionFactory sessionFactory = null;
  protected static ServiceRegistry serviceRegistry = null;

  public static void init()
  {
    try
    {
      Configuration configuration = new Configuration();
      configuration.setNamingStrategy(ImprovedNamingStrategy.INSTANCE);
      configuration.configure();
      serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();        
      sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    }
    catch (Throwable ex)
    {
      log.error("HibernateUtil:init error:"+ex.getMessage());
      throw new ExceptionInInitializerError(ex);
    }
  }

  public static void shutdown()
  {
    HibernateUtil.getSessionFactory().close();
  }

  public static SessionFactory getSessionFactory()
  {
    if (sessionFactory == null)
      init();

    return sessionFactory;
  }

  public static List query(String queryString, String[] args)
  throws BSAException
  {
    Session session = getSessionFactory().openSession();

    try
    {
      Query query = session.createQuery(queryString);
      for (int i = 0 ; i < args.length ; i++)
	query.setString(i, args[i]);
      return query.list();
    }
    catch (Exception e)
    {
      log.error("HibernateUtil:query:exception:"+e.getMessage());
      throw new BSAException(e.getMessage());
    }
    finally
    {
      session.close();
    }
  }

  public static List query(String queryString, Map<String,String> args)
  throws BSAException
  {
    Session session = getSessionFactory().openSession();

    try
    {
      Query query = session.createQuery(queryString);
      for (String namedParam : args.keySet())
	query.setString(namedParam, args.get(namedParam));
      return query.list();
    }
    catch (Exception e)
    {
      log.error("HibernateUtil:query:exception:"+e.getMessage());
      throw new BSAException(e.getMessage());
    }
    finally
    {
      session.close();
    }
  }

  public static void save(Object object)
  throws BSAException
  {
    Session session = getSessionFactory().openSession();
    Transaction transaction = null;

    try
    {
      transaction = session.beginTransaction();
      session.saveOrUpdate(object);
      transaction.commit();
      session.evict(object);
    }
    catch (Exception e)
    {
      if (transaction != null)
	transaction.rollback();
      log.error("HibernateUtil:save:exception:"+e.getMessage());
      throw new BSAException(e.getMessage());
    }
    finally
    {
      session.close();
    }
  }

  public static void refresh(Object object)
  throws BSAException
  {
    Session session = getSessionFactory().openSession();
    Transaction transaction = null;

    try
    {
      transaction = session.beginTransaction();
      session.refresh(object);
      transaction.commit();
      session.evict(object);
    }
    catch (Exception e)
    {
      if (transaction != null)
	transaction.rollback();
      log.error("HibernateUtil:refresh:exception:"+e.getMessage());
      throw new BSAException(e.getMessage());
    }
    finally
    {
      session.close();
    }
  }

  public static void load(Object object, int id)
  throws BSAException
  {
    Session session = getSessionFactory().openSession();
    
    try
    {
      session.load(object, new Integer(id));
      session.evict(object);
    }
    catch (Exception e)
    {
      log.error("HibernateUtil:load:exception for id="+id+":"+e.getMessage());
      throw new BSAException(e.getMessage());
    }
    finally
    {
      session.close();
    }
  }
}
