/*
 * Created on Nov 11, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.hogdev.enterprise.beans;

import java.io.File;
import java.util.Date;

import com.hogdev.enterprise.utils.ResourceUtil;
import com.hogdev.util.GeneralUtils;

/**
 * @author 
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Resource
{
	private String name;
	private int type;
	private String desc;
	private String path;
	private String thumbPath;
	private Date lastUpdate;
	private long size;
	private String caption;
	 
	/**
	 * @return
	 */
	public String getDesc()
	{
		return desc;
	}

	/**
	 * @return
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return
	 */
	public int getType()
	{
		return type;
	}

	/**
	 * @param string
	 */
	public void setDesc(String string)
	{
		desc = string;
	}

	/**
	 * @param string
	 */
	public void setName(String string)
	{
		name = string;
	}

	/**
	 * @param i
	 */
	public void setType(int i)
	{
		type = i;
	}

	/**
	 * @return
	 */
	public String getPath()
	{
		return path;
	}

	/**
	 * @param string
	 */
	public void setPath(String string)
	{
		path = GeneralUtils.stringReplace(string, File.separator, ResourceUtil.FILE_SEP);
	}

	/**
	 * @return
	 */
	public Date getLastUpdate()
	{
		return lastUpdate;
	}

	/**
	 * @param date
	 */
	public void setLastUpdate(Date date)
	{
		lastUpdate = date;
	}

	/**
	 * @return
	 */
	public long getSize()
	{
		return size;
	}

	/**
	 * @param l
	 */
	public void setSize(long l)
	{
		size = l;
	}

	/**
	 * @return
	 */
	public String getThumbPath()
	{
		return thumbPath;
	}

	/**
	 * @param string
	 */
	public void setThumbPath(String string)
	{
		thumbPath = GeneralUtils.stringReplace(string, File.separator, ResourceUtil.FILE_SEP);
	}

	/**
	 * @return
	 */
	public String getCaption()
	{
		return caption;
	}

	/**
	 * @param string
	 */
	public void setCaption(String string)
	{
		caption = string;
	}

}
