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
public class NonHiddenWCFileFilter implements FileFilter
{
    WildCardFileFilter wcf;

    NonHiddenFileFilter nhf;

    String pattern;

    public NonHiddenWCFileFilter(String str)
    {
        pattern = str;
        wcf = new WildCardFileFilter(str);
        nhf = new NonHiddenFileFilter();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.FileFilter#accept(java.io.File)
     */
    public boolean accept(File file)
    {
        return nhf.accept(file)
                && wcf.accept(file.getParentFile(), file.getName());
    }

    public String toString()
    {
        // TODO Auto-generated method stub
        return "NonHiddenWCFileFilter [" + pattern + "]";
    }

}
