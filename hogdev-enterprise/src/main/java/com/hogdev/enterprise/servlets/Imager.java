package com.hogdev.enterprise.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.hogdev.enterprise.Constants;
import com.hogdev.enterprise.utils.ResourceUtil;
import com.hogdev.util.JImager;

/**
 * @version 1.0
 * @author
 */
public class Imager extends Retriever
{
    /**
     * 
     */
    private static final long serialVersionUID = -3041824051980288045L;

    static Logger logger = Logger.getLogger(Imager.class);

    /**
     * @see javax.servlet.http.HttpServlet#void
     *      (javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        int size = -1;
        int width = -1;
        int height = -1;
        float quality = .90f;

        // String path = request.getParameter(Constants.PATH_KEY);
        String path = request.getPathInfo();
        if (path == null)
        {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String strSize = request.getParameter(Constants.SIZE_KEY);
        if (strSize != null)
        {
            // see if it is first of the format WidthxHeight e.g 640x480
            strSize = strSize.toLowerCase();
            int i = strSize.indexOf('x');
            if (i != -1)
            {
                try
                {
                    width = Integer.parseInt(strSize.substring(0, i));
                    height = Integer.parseInt(strSize.substring(i + 1));
                }
                catch (NumberFormatException e)
                {
                    response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
                    return;
                }
            }
            else
            {
                try
                {
                    size = Integer.parseInt(strSize);
                }
                catch (NumberFormatException e)
                {
                    response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
                    return;
                }
            }
        }

        String strQuality = request.getParameter(Constants.QUALITY_KEY);
        if (strQuality != null)
        {
            try
            {
                quality = Float.parseFloat(strQuality);
            }
            catch (NumberFormatException e)
            {
                response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
                return;
            }
        }

        path = baseDir + path;

        File file = new File(path);
        if (!file.exists() || !file.isFile())
        {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (ResourceUtil.determineType(file) != Constants.IMAGE)
        {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType(getMimeType(file));

        OutputStream os = response.getOutputStream();
        getImage(size, width, height, quality, file, os);
        // TODO do I need to close the servlet output stream?
    }

    protected void getImage(int size, int width, int height, float quality, File file, OutputStream os)
            throws IOException
    {
        if ((size < 0 && (height < 0 || width < 0)) || !file.getName().toUpperCase().endsWith(".JPG"))
        {
            getResource(file, os);
        }
        else
        {
            InputStream is = new FileInputStream(file);
            JImager ji = new JImager();
            ji.setQuality(quality);
            ji.setSize(size);
            ji.setHeight(height);
            ji.setWidth(width);
            ji.doResize(is, os);
            is.close();
        }
        os.flush();
    }
}
