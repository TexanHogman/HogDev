/*
 * Created on Jan 16, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.hogdev.util;

import java.io.File;
import java.io.FileFilter;

/**
 * @author
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NonHiddenFileFilter implements FileFilter
{

    /*
     * (non-Javadoc)
     * 
     * @see java.io.FileFilter#accept(java.io.File)
     */
    @Override
	public boolean accept(File file)
    {
        return !file.isHidden();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
	public String toString()
    {
        // TODO Auto-generated method stub
        return "NonHiddenFileFilter";
    }

}
