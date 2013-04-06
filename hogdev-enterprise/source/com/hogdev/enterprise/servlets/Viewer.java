package com.hogdev.enterprise.servlets;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.hogdev.enterprise.Constants;
import com.hogdev.enterprise.beans.Resource;
import com.hogdev.enterprise.utils.ResourceUtil;
import com.hogdev.util.NonHiddenFileFilter;

/**
 * @version 	1.0
 * @author
 */
public class Viewer extends BaseRelative
{
	/**
     * 
     */
    private static final long serialVersionUID = -4138441470510027735L;
    static Logger logger = Logger.getLogger(Viewer.class);

	public void doPost(
		HttpServletRequest request,
		HttpServletResponse response)
		throws ServletException, IOException
	{
		doGet(request, response);
	}
	/**
	* @see javax.servlet.http.HttpServlet#void (javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	*/
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		logger.debug("==> execute()");
		try
		{
			String strPath = request.getPathInfo();
			if (strPath == null || strPath.length() == 0)
				strPath = request.getParameter("path");

			if (strPath == null || strPath.length() == 0)
				strPath = ResourceUtil.FILE_SEP;

			String strFullPath = baseDir + strPath;
			File file = new File(strFullPath);

			// see if this exist on the file system
			Resource resource = ResourceUtil.buildResource(baseDir, strPath);
			if(resource == null)
			{
				logger.error(strFullPath + " does not exist");
				request.getRequestDispatcher("/error.jsp").forward(
					request,
					response);
				return;
			}
			request.setAttribute(Constants.RESOURCE_KEY, resource);
			
			int i = strPath.lastIndexOf(ResourceUtil.FILE_SEP);
			String strParentPath = null;
			if (i != -1)
				strParentPath = strPath.substring(0, i);
			else
				strParentPath = ResourceUtil.FILE_SEP;

			request.setAttribute(Constants.RESOURCE_UP_KEY, ResourceUtil.buildResource(baseDir, strParentPath));
			
			if (resource.getType() == Constants.DIR)
			{
				File[] children = file.listFiles(new NonHiddenFileFilter());
				Arrays.sort(children);

				ArrayList al = new ArrayList();
				
				for (i = 0; children != null && i < children.length; i++)
				{
					al.add(ResourceUtil.buildResource(baseDir, strPath + ResourceUtil.FILE_SEP + children[i].getName()));
				}

				request.setAttribute(Constants.RESOURCE_LIST_KEY, al);
				
				int iView = Constants.UNDEFINED;
				String strView =
					(String)request.getParameter(Constants.VIEW_KEY);
				if (strView != null)
				{
					try
					{
						iView = Integer.parseInt(strView);
						logger.debug("requested view is " + iView);

						// save off for later
						request.getSession().setAttribute(
							Constants.HINT_VIEW_KEY,
							new Integer(iView));
					}
					catch (NumberFormatException e)
					{
						logger.error(e);
					}
				}
				Integer hintView = (Integer)request.getSession().getAttribute(Constants.HINT_VIEW_KEY);
				int iHintView = hintView != null ? hintView.intValue() : Constants.UNDEFINED;
				logger.debug("hint view is " + iHintView);
				
				if (iView == Constants.UNDEFINED)
					iView = ResourceUtil.determineSuggestedView(al, iHintView);

				switch (iView)
				{
					case Constants.THUMBNAILS :
						request.getRequestDispatcher(
							"/thumbnails.jsp").forward(
							request,
							response);
						break;
					case Constants.DETAILS :
						request.getRequestDispatcher("/details.jsp").forward(
							request,
							response);
						break;
					case Constants.LIST :
					default :
						request.getRequestDispatcher("/list.jsp").forward(
							request,
							response);
						break;
				}
			}
			else
			{
				File parent = file.getParentFile();
				File[] siblings = parent.listFiles(new NonHiddenFileFilter());
				Arrays.sort(siblings);

				// find previous and next
				for (i = 0; siblings != null && i < siblings.length; i++)
				{
					if (siblings[i].equals(file))
						break;
				}

				if (i >= siblings.length)
				{
					// did not find it so error
					request.getRequestDispatcher("/error.jsp").forward(
						request,
						response);
					return;
				}
				else
				{
					if (i != 0)
					{
						resource = ResourceUtil.buildResource(baseDir, strParentPath + ResourceUtil.FILE_SEP + siblings[i-1].getName());
						request.setAttribute(
							Constants.RESOURCE_BACK_KEY,
							resource);
					}
					if (i < siblings.length - 1)
					{
						resource = ResourceUtil.buildResource(baseDir, strParentPath + ResourceUtil.FILE_SEP + siblings[i+1].getName());
						request.setAttribute(
							Constants.RESOURCE_FORWARD_KEY,
							resource);
					}

				}
				request.getRequestDispatcher("/walker.jsp").forward(
					request,
					response);
				return;
			}
			return;
		}
		finally
		{
			logger.debug("<== execute()");
		}
	}

}
