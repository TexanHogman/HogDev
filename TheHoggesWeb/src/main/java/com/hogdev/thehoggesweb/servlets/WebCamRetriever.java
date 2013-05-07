/*
 * Created on Aug 2, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.hogdev.thehoggesweb.servlets;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.hogdev.enterprise.servlets.Retriever;

/**
 * @author rick
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WebCamRetriever extends Retriever
{
    /**
     * 
     */
    private static final long serialVersionUID = -5116316384772718791L;

    static Logger logger = Logger.getLogger(WebCamRetriever.class);

    File folder;

    private static String LAST_FILE_NAME = "last_file";

    public void init() throws ServletException
    {
        super.init();
        
        folder = new File(baseDir + File.separator + "webcams" + File.separator + "Camera1");
        
        if (!folder.exists() || !folder.isDirectory())
            throw new UnavailableException("webcam dir is invalid");
    }

    /**
     * @see javax.servlet.http.HttpServlet#void
     *      (javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        logger.debug("new session: " + session.isNew());
        
        File file = null;
        
        // keep track of the last file sent and try not to send the same one again
        String lastFileName = (String)session.getAttribute(LAST_FILE_NAME);
        logger.debug("last file for this session: " + lastFileName);
        
        long start = System.currentTimeMillis();
        while (true)
        {
	        File[] files = folder.listFiles();
	        if (files.length > 0)
	        {
	            Arrays.sort(files, new Comparator()
	            {
	
	                public int compare(Object o1, Object o2)
	                {
	                    // order desc.
	                    return new Long(((File) o2).lastModified())
	                            .compareTo(new Long(((File) o1).lastModified()));
	                }
	            });
	        }
	        else
	        {
	            response.sendError(HttpServletResponse.SC_NOT_FOUND);
	            return;
	        }
	        file = files[0];
	        
	        // wait up to 5 seconds for next file
	        if(lastFileName == null || !file.getCanonicalPath().equals(lastFileName) || System.currentTimeMillis() - start > 5000)
	        {
	            request.getSession().setAttribute(LAST_FILE_NAME, file.getCanonicalPath());
	            break;
	        }
            else
            {
                try
                {
                    logger.info("waiting to hopefully send user new file");
                    Thread.sleep(500);
                }
                catch (InterruptedException ignore)
                {
                }
            }
        }

        response.setContentType(getMimeType(file));

        OutputStream os = response.getOutputStream();
        getResource(file, os);
        // TODO do I need to close the servlet output stream?
    }

}
