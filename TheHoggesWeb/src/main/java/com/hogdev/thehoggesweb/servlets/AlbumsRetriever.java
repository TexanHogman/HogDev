/*
 * Created on Aug 2, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.hogdev.thehoggesweb.servlets;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.Logger;

import com.hogdev.enterprise.Constants;
import com.hogdev.enterprise.beans.Resource;
import com.hogdev.enterprise.servlets.Retriever;
import com.hogdev.enterprise.utils.ResourceUtil;
import com.hogdev.util.GeneralUtils;

/**
 * @author rick
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AlbumsRetriever extends Retriever
{
	/**
     * 
     */
    private static final long serialVersionUID = -7624818141462202425L;
    static Logger logger = Logger.getLogger(AlbumsRetriever.class);
	final FileFilter filter = new WildcardFileFilter("Folder.jpg", IOCase.INSENSITIVE);
    File music = null;
	
	public void init() throws ServletException
	{
		super.init();
		
		music = new File(baseDir + File.separator + "music");
		GeneralUtils.cacheCollectAll(music, filter);
	}
	
	
	/**
	* @see javax.servlet.http.HttpServlet#void (javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	*/
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		// see if this exist on the file system
		Resource resourceUp = ResourceUtil.buildResource(baseDir, "music");
		if(resourceUp == null)
		{
			logger.error("music does not exist");
			request.getRequestDispatcher("/error.do").forward(
				request,
				response);
			return;
		}
		request.setAttribute(Constants.RESOURCE_KEY, resourceUp);
		request.setAttribute(Constants.RESOURCE_UP_KEY, resourceUp);
		
		ArrayList al = GeneralUtils.cacheCollectAll(music, filter);
		
		ArrayList imageResources = new ArrayList();
		for(int i = 0; i < al.size(); i++)
		{
			File file =(File)al.get(i);
			String strPath = file.getPath().substring(baseDir.length());
			Resource resource = ResourceUtil.buildResource(baseDir, strPath);
			imageResources.add(resource);
		}
		
		ArrayList resources = new ArrayList();
		for(int i = 0; i < al.size(); i++)
		{
			File file =((File)al.get(i)).getParentFile();
			String strPath = file.getPath().substring(baseDir.length());
			Resource resource = ResourceUtil.buildResource(baseDir, strPath);
			//reset thumb to that of image
			resource.setThumbPath(((Resource)imageResources.get(i)).getThumbPath());
			resources.add(resource);
		}
		
		request.setAttribute(Constants.RESOURCE_LIST_KEY, resources);
		request.getRequestDispatcher(
			"/thumbnails.do").forward(
			request,
			response);
	}
	
	
}
