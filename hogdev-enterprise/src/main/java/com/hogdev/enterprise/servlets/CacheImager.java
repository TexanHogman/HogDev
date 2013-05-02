/*
 * Created on Nov 17, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.hogdev.enterprise.servlets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;

import com.hogdev.enterprise.Constants;

/**
 * @author
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CacheImager extends Imager
{
    /**
     * 
     */
    private static final long serialVersionUID = 8543358399573080955L;
    protected String cacheDir;

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.GenericServlet#init()
     */
    public void init() throws ServletException
    {
	super.init();

	cacheDir = (String) System.getProperty(Constants.CACHE_DIR);
	if (cacheDir == null)
	    throw new UnavailableException("cache dir is not set");

	File file = new File(cacheDir);
	if (!file.exists() || !file.isDirectory())
	    throw new UnavailableException("cache dir is invalid");

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hogdev.thehoggesweb.servlets.Imager#getImage(int, float,
     *      java.io.File, java.io.OutputStream)
     */
    protected void getImage(int size, int width, int height, float quality, File file, OutputStream os) throws IOException
    {
	String canPath = file.getCanonicalPath();
	String path = canPath.substring(baseDir.length());

	String cachePath = null;
	// check to size if I have this already created at this size
	int i = path.lastIndexOf(File.separator);
	if (i > 0 && i < path.length() - 1)
	    cachePath = path.substring(0, i + 1) + size + "_" + quality + "_" + path.substring(i + 1);
	else
	    cachePath = File.separator + size + "_" + quality + "_" + path;

	cachePath = cacheDir + cachePath;

	File fileCache = new File(cachePath);
	if (file.isDirectory())
	{
	    return;
	}
	// TODO add check for timestamps to make sure source has not been
	// updated
	else if (!fileCache.exists() || fileCache.length() == 0 || file.lastModified() > fileCache.lastModified())
	{
	    synchronized (this)
	    {
		fileCache.getParentFile().mkdirs();
	    }

	    OutputStream fos = new FileOutputStream(fileCache);
	    super.getImage(size, width, height, quality, file, fos);
	    fos.close();
	}

	// -1 for size will ignore the quality and just return image without
	// resize
	super.getImage(-1, -1, -1, -1, fileCache, os);
    }

}
