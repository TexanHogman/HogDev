/*
 * Created on Oct 22, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.hogdev.enterprise.beans;

import java.io.Serializable;
import java.util.Date;

/**
 * @author rick
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Stat implements Serializable
{
	/**
     * 
     */
    private static final long serialVersionUID = 89478813719925694L;
    private Date startDate;
	private Date stopDate;
	private long hits;

	/**
	 * @return
	 */
	public long getHits()
	{
		return hits;
	}

	/**
	 * @return
	 */
	public Date getStartDate()
	{
		return startDate;
	}

	/**
	 * @return
	 */
	public Date getStopDate()
	{
		return stopDate;
	}

	/**
	 * @param l
	 */
	public void setHits(long l)
	{
		hits = l;
	}

	/**
	 * @param date
	 */
	public void setStartDate(Date date)
	{
		startDate = date;
	}

	/**
	 * @param date
	 */
	public void setStopDate(Date date)
	{
		stopDate = date;
	}

}
