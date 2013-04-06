/*
 * Created on Jan 29, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.hogdev.enterprise.servlets;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import com.hogdev.enterprise.Constants;

/**
 * @author 
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class BaseRelative extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static Logger logger = Logger.getLogger(BaseRelative.class);
	
	protected String baseDir;
	
	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init() throws ServletException
	{
		super.init();

		String strBaseDir = (String)System.getProperty(Constants.BASE_DIR);
		if (strBaseDir == null)
			throw new UnavailableException("base dir is not set");

		File file = new File(strBaseDir);
		if (!file.exists() || !file.isDirectory())
			throw new UnavailableException("base dir is invalid");
		else
		{
			try
			{
				baseDir = file.getCanonicalPath();
			}
			catch (IOException e)
			{
				logger.error(e);	
				baseDir = strBaseDir;
			}
		}
	}

}
