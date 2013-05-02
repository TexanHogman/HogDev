package com.hogdev.enterprise.servlets;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hogdev.util.GeneralUtils;
import com.hogdev.util.NonHiddenWCFileFilter;

/**
 * @version 1.0
 * @author
 */
public class RandomImagerURL extends BaseRelative
{
    /**
     * 
     */
    private static final long serialVersionUID = -4471884398234694448L;

    final FileFilter filter = new NonHiddenWCFileFilter("*.jpg");
    File images = null;

    /**
     * @see com.hogdev.thehoggesweb.servlets.Imager#void (HttpServletRequest,
     *      HttpServletResponse)
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        File randFile = null;
        ArrayList al = GeneralUtils.cacheCollectAll(images, filter);
        if (al.size() > 0)
        {
            double db = Math.floor((Math.random() * al.size()));
            randFile = (File) al.get((int) db);
        }
        else
        {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String canPath = randFile.getCanonicalPath();
        if (!canPath.toLowerCase().startsWith(baseDir.toLowerCase()))
        {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String path = canPath.substring(baseDir.length());
        String strForward = "/imager" + path;
        strForward = GeneralUtils.stringReplace(strForward, File.separator, "/");
        response.setContentType("text/plain");
        BufferedOutputStream bs = new BufferedOutputStream(response.getOutputStream());
        bs.write(strForward.getBytes());
        bs.flush();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.GenericServlet#init()
     */
    public void init() throws ServletException
    {
        super.init();

        images = new File(baseDir + File.separator + "images");
        GeneralUtils.cacheCollectAll(images, filter);
    }

}
