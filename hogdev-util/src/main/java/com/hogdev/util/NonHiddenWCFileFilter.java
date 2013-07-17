/*
 * Created on Jan 16, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.hogdev.util;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;

/**
 * @author
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NonHiddenWCFileFilter implements FileFilter
{
    WildcardFileFilter wcf;

    NonHiddenFileFilter nhf;

    String pattern;

    public NonHiddenWCFileFilter(String str)
    {
        pattern = str;
        wcf = new WildcardFileFilter(str, IOCase.INSENSITIVE);
        nhf = new NonHiddenFileFilter();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.FileFilter#accept(java.io.File)
     */
    @Override
	public boolean accept(File file)
    {
        return nhf.accept(file)
                && wcf.accept(file.getParentFile(), file.getName());
    }

    @Override
	public String toString()
    {
        // TODO Auto-generated method stub
        return "NonHiddenWCFileFilter [" + pattern + "]";
    }

}
