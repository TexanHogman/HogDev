/*
 * Created on Oct 19, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.hogdev.enterprise.servlets;

import java.io.BufferedOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.hogdev.enterprise.Constants;
import com.hogdev.enterprise.beans.Stats;

/**
 * @author rick
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class StatsServlet extends HttpServlet
{
	/**
     * 
     */
    private static final long serialVersionUID = -9009557043466911819L;
    
    static Logger logger = Logger.getLogger(StatsServlet.class);
	
	/* (non-Javadoc)
	 * @see javax.servlet.Servlet#destroy()
	 */
	public void destroy()
	{
		Stats stats = (Stats)getServletContext().getAttribute("stats");
		if(stats != null)
		    stats.stop();
		
		super.destroy();
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init() throws ServletException
	{
		String stats_name = getInitParameter(Constants.STATS);
		
		if (stats_name == null)
			throw new UnavailableException("stats name is not set");
		
		Stats stats = Stats.getInstance(stats_name);
		stats.start();
		
		getServletContext().setAttribute("stats", stats);
		
		super.init();
	}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	logger.debug("doGet");
        Stats stats = (Stats)getServletContext().getAttribute("stats");
        response.setContentType("text/plain");
        BufferedOutputStream bs =
            new BufferedOutputStream(response.getOutputStream());
        bs.write(String.valueOf(stats.getHits()).getBytes());
        bs.flush();
    }
}
