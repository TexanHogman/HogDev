/*
 * Created on Oct 19, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.hogdev.enterprise.beans;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * @author rick
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Stats implements Serializable
{    
    private static final long serialVersionUID = 8724176338260935720L;
    
	static Logger logger = Logger.getLogger(Stats.class);

	private static HashMap map = new HashMap();
	
	private ArrayList list;

	private Stats()
	{
		list = new ArrayList();
	}

	public synchronized static Stats getInstance(String name)
	{
		logger.debug("stats getInstance [" + name + "]");
		
		if(name == null || name.length() == 0)
			throw new IllegalArgumentException("name must be specified and can not be null");
			
		Stats stats = (Stats)map.get(name);
		if (stats == null)
		{
			try
			{
				FileInputStream istream = new FileInputStream(name);
				ObjectInputStream p = new ObjectInputStream(istream);
				stats = (Stats)p.readObject();
				istream.close();
			}
			catch (Exception e)
			{
				logger.warn("trying to de-serialize stats", e);
				stats = new Stats();
			}
			map.put(name, stats);
		}
		return stats;
	}
	
	private void save()
	{
		logger.debug("save stats");
		// go find my reference in the map
		Set keys = map.keySet();
		Iterator iter = keys.iterator();
		while(iter.hasNext())
		{
			String key = (String)iter.next();
			Stats stats = (Stats)map.get(key);
			if(stats == this)
			{
				save(key);
				break;
			}
		}
	}
	
	private void save(String name)
	{
		logger.debug("save stats [" + name + "]");
		try
		{
			FileOutputStream ostream = new FileOutputStream(name);
			ObjectOutputStream p = new ObjectOutputStream(ostream);
			p.writeObject(this);
			ostream.close();
		}
		catch (Exception e)
		{
			logger.warn("trying to serialize stats", e);
		}
		
	}

	public void start()
	{
		logger.debug("stats start");
		Stat stat = new Stat();
		stat.setStartDate(new Date());
		list.add(stat);
	}

	public void stop()
	{
		logger.debug("stats stop");
		if (list.size() > 0)
		{
			Stat stat = (Stat)list.get(list.size() - 1);
			stat.setStopDate(new Date());
		}
		
		save();
	}

	public void hit()
	{
		logger.debug("stats hit");
		if (list.size() > 0)
		{
			Stat stat = (Stat)list.get(list.size() - 1);
			stat.setHits(stat.getHits() + 1);
		}
	}
	
	/**
	 * @return
	 */
	public long getHits()
	{
		long hits = 0;
		for(int i = 0; i < list.size(); i++)
		{
			Stat stat = (Stat)list.get(i);
			hits += stat.getHits();
		}
		return hits;
	}

	/**
	 * @return
	 */
	public long getHitAndReturn()
	{
	    hit();
	    return getHits();
	}

	/**
	 * @return
	 */
	public Date getStartDate()
	{
		Date dt = null;
		if (list.size() > 0)
		{
			Stat stat = (Stat)list.get(0);
			dt = stat.getStartDate();
		}
		return dt;
	}

}
