package com.hogdev.enterprise.servlets;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hogdev.enterprise.Constants;
import com.hogdev.util.GeneralUtils;
import com.hogdev.util.NonHiddenWCFileFilter;

/**
 * @version 	1.0
 * @author
 */
public class RandomImager extends Imager 
{
	/**
     * 
     */
    private static final long serialVersionUID = -4471884398234694448L;
    final FileFilter filter = new NonHiddenWCFileFilter("*.jpg");
	 
	/**
	* @see com.hogdev.thehoggesweb.servlets.Imager#void (HttpServletRequest, HttpServletResponse)
	*/
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		String strReturnLast = request.getParameter(Constants.RETURN_LAST_KEY);
		boolean bReturnLast = false;
		if(strReturnLast != null )
			bReturnLast = Boolean.valueOf(strReturnLast).booleanValue();
			
		File randFile = null;
		if(bReturnLast)
		{
			randFile = (File)request.getSession().getAttribute(Constants.RETURN_LAST_KEY);
		}
		
		if(randFile == null)
		{
			File flBaseDir = new File(baseDir);
			ArrayList al = GeneralUtils.cacheCollectAll(flBaseDir, filter);
			if(al.size() > 0)
			{
				double db = Math.floor((Math.random() * al.size()));
				randFile = (File)al.get((int)db);
				request.getSession().setAttribute(Constants.RETURN_LAST_KEY, randFile);
			}
			else
			{
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
		}
		
		String strSize = request.getParameter(Constants.SIZE_KEY);
		String strQuality = request.getParameter(Constants.QUALITY_KEY);
		String canPath = randFile.getCanonicalPath();
		if(!canPath.toLowerCase().startsWith(baseDir.toLowerCase()))
		{
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return; 
		}
		
		String path = canPath.substring(baseDir.length());
//		String strForward = "/imager?" + Constants.PATH_KEY + "=" + path;
		String strForward = "/imager" + path;
        strForward = GeneralUtils.stringReplace(strForward, File.separator, "/");
		if(strSize != null) 
		{
			strForward += "?" + Constants.SIZE_KEY + "=" + strSize;
			if(strQuality != null) 
				strForward += "&" + Constants.QUALITY_KEY + "=" + strQuality;
		}
		else if(strQuality != null) 
			strForward += "?" + Constants.QUALITY_KEY + "=" + strQuality;
			
		getServletContext().getRequestDispatcher(strForward).forward(request, response);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init() throws ServletException
	{
		super.init();
		
		File flBaseDir = new File(baseDir);
		GeneralUtils.cacheCollectAll(flBaseDir, filter);
	}

}
