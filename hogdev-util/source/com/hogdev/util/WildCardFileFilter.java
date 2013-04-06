package com.hogdev.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

//*************************************************************************************
//This class is a FilenameFilter that uses  "*"  as wildcards.
//*************************************************************************************

//The filter uses the  "*"  wildcards in the usual way.
//Giving a WildCardFileFilter object the following input String :
//ex1)  *fil*.*a*  will make it accept files with the string "fil" somewhere in their name
//      and a character "a" somewhere in their extension,
//ex2)  *fil.*a  will make it accept files which name ends with the string "fil" and which
//      extension ends with a character "a",
//ex3)  fil*.*  will make it accept files which names begin with the string "fil" and can have
//      any extension.

public class WildCardFileFilter implements FilenameFilter, FileFilter,
        Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1631002342070033201L;

    private final int EMPTY = 0;

    private final int BEGIN = 1;

    private final int MIDDLE = 2;

    private final int END = 3;

    private int fn_search;

    private int ext_search;

    private int len_out_ext;

    private int len_out_fn;

    private int ew_fileext;

    private int ew_filename;

    private int rm_fileext;

    private int rm_filename;

    private String out_fn;

    private String out_ext;

    private String pattern;

    // The constructor takes a String input as an argument.
    // It will break the input into two parts if it discovers a dot in the input
    // :
    // -before the dot, the filtername
    // -after the dot, the filterextension

    public WildCardFileFilter(String input)
    {
        pattern = input;
        String in_fn;
        String in_ext;
        int from = 0;

        // The filterinput will be broken into two parts that will each act as a
        // filter
        // on its own: a filtername and a filterextension.

        // Check for the number of dots in the filterinput.
        // If there is more than one dot then after tokenizing the string:
        // - the last token will be the filterextension
        // - all previous tokens are concatenated and the dots that occurred
        // are put back in place. This string of concatenated tokens will be the
        // filtername.
        // If there is no dot at all, then the filtername equals the filterinput
        // and the filterextension is empty.
        // *******************************************************************************************
        StringTokenizer dot_st = new StringTokenizer(input, ".");
        int fp = dot_st.countTokens();
        Vector v = new Vector();
        for (int i = 0; i < fp; i++)
        {
            try
            {
                String t = dot_st.nextToken();
                v.addElement(t);
            }
            catch (NoSuchElementException e1)
            {
            } // just don't add this element to the vector, nothing more
            // needed
        }

        // If the vector is empty, then there were no dots in the filterinput,
        // the filtername equals the filterinput and there is no
        // filterextension.
        if (v.isEmpty())
        {
            in_fn = input;
            in_ext = "";
        }

        // If the vector is not empty compute filtername and filterextension.
        else
        {
            if (v.size() > 1)
            {
                if (v.size() > 2)
                {
                    try
                    {
                        in_ext = (String) v.lastElement();
                    }
                    catch (NoSuchElementException e2)
                    {
                        in_ext = "";
                    }

                    String buf = "";
                    for (int i = 0; i < v.size() - 1; i++)
                    {
                        buf += (String) v.elementAt(i) + ".";
                    }

                    // omit the last dot
                    try
                    {
                        in_fn = buf.substring(0, buf.length() - 1);
                    }
                    catch (StringIndexOutOfBoundsException e3)
                    {
                        in_fn = "";
                    }
                }
                else
                {
                    in_fn = (String) v.elementAt(0);
                    in_ext = (String) v.elementAt(1);
                }
            }
            else
            {
                in_ext = "";
                in_fn = (String) v.elementAt(0);
            }
        }

        // Process the filtername.
        // Examine in_fn to find out how many * there are, and where they are.
        // A user can enter a third star or more stars in the filter
        // but this won' t make much sense. This is dealt with later.
        int fn_s1;
        int fn_s2;
        int fn_stars = 0;

        // search the first two stars
        fn_s1 = in_fn.indexOf("*");
        fn_s2 = fn_s1 + 1;
        if (fn_s1 != -1)
        {
            fn_stars++;
            from = fn_s1 + 1;
            fn_s2 = in_fn.indexOf("*", from);
            if (fn_s2 != -1)
            {
                fn_stars++;
                from = fn_s2 + 1;

                // search for more stars, if there are
                if (from < in_fn.length())
                {
                    while (from < in_fn.length())
                    {
                        int s = in_fn.indexOf("*", from);
                        if (s != -1)
                        {
                            fn_stars++;
                            from = s + 1;
                        }
                        else break;
                    }
                }
            }
        }

        try
        {
            StringTokenizer name_st = new StringTokenizer(in_fn, "*");
            switch (fn_stars)
            {
            case 0:
                out_fn = in_fn;
                fn_search = BEGIN;
                break;

            case 1:
                if (in_fn.equals("*"))
                {
                    fn_search = EMPTY;
                    out_fn = "";
                }
                else if (in_fn.endsWith("*"))
                {
                    fn_search = BEGIN;
                    out_fn = name_st.nextToken();
                }
                else if (in_fn.startsWith("*"))
                {
                    fn_search = END;
                    out_fn = name_st.nextToken();
                }
                else
                {
                    fn_search = EMPTY;
                    out_fn = "";
                }
                break;

            case 2:
                // Here we deal with a third or more stars in in_fn.
                // They don't make much sense so we decide to take the string
                // between
                // the first star and the second as the out_fn and ignore any
                // more stars.
                if (in_fn.regionMatches(0, "**", 0, 2))
                {
                    fn_search = EMPTY;
                    out_fn = "";
                }
                else if ((in_fn.startsWith("*")) && (in_fn.endsWith("*")))
                {
                    fn_search = MIDDLE;
                    out_fn = name_st.nextToken();
                }
                else if (!(in_fn.startsWith("*")) || !(in_fn.endsWith("*")))
                {
                    fn_search = MIDDLE;
                    out_fn = in_fn.substring(fn_s1 + 1, fn_s2);
                }
                else
                {
                    fn_search = EMPTY;
                    out_fn = "";
                }
                break;

            default:
                fn_search = EMPTY;
                out_fn = "";
                break;
            }
            len_out_fn = out_fn.length();
        }
        catch (NoSuchElementException e1)
        {
            out_fn = "";
            len_out_fn = 0;
        }

        // Process the filterextension.
        // Examine in_ext to find out how many * there are, and where they are.
        // A user can enter a third star or more stars in the filter
        // but this won' t make much sense. This is dealt with later.
        int ext_s1;
        int ext_s2;
        int ext_stars = 0;

        // search the first two stars
        ext_s1 = in_ext.indexOf("*");
        ext_s2 = ext_s1 + 1;
        if (ext_s1 != -1)
        {
            ext_stars++;
            from = ext_s1 + 1;
            ext_s2 = in_ext.indexOf("*", from);
            if (ext_s2 != -1)
            {
                ext_stars++;
                from = ext_s2 + 1;

                // search for more stars, if there are
                if (from < in_ext.length())
                {
                    while (from < in_ext.length())
                    {
                        int s = in_ext.indexOf("*", from);
                        if (s != -1)
                        {
                            ext_stars++;
                            from = s + 1;
                        }
                        else break;
                    }
                }
            }
        }

        try
        {
            StringTokenizer ext_st = new StringTokenizer(in_ext, "*");
            switch (ext_stars)
            {
            case 0:
                ext_search = BEGIN;
                out_ext = in_ext;
                break;
            case 1:
                if (in_ext.equals("*"))
                {
                    ext_search = EMPTY;
                    out_ext = "";
                }
                else if (in_ext.endsWith("*"))
                {
                    ext_search = BEGIN;
                    out_ext = ext_st.nextToken();
                }
                else if (in_ext.startsWith("*"))
                {
                    ext_search = END;
                    out_ext = ext_st.nextToken();
                }
                else
                {
                    ext_search = EMPTY;
                    out_ext = "";
                }
                break;

            case 2:
                // Here we deal with a third or more stars in in_ext.
                // They don't make much sense so we decide to take the string
                // between
                // the first star and the second as the out_ext and ignore any
                // more stars.
                if (in_ext.regionMatches(0, "**", 0, 2))
                {
                    ext_search = EMPTY;
                    out_ext = "";
                }
                else if ((in_ext.startsWith("*")) && (in_ext.endsWith("*")))
                {
                    ext_search = MIDDLE;
                    out_ext = ext_st.nextToken();
                }
                else if (!(in_ext.startsWith("*")) || !(in_ext.endsWith("*")))
                {
                    ext_search = MIDDLE;
                    out_ext = in_ext.substring(ext_s1 + 1, ext_s2);
                }
                else
                {
                    ext_search = EMPTY;
                    out_ext = "";
                }
                break;

            default:
                ext_search = EMPTY;
                out_ext = "";
                break;
            }

            len_out_ext = out_ext.length();
        }
        catch (NoSuchElementException e2)
        {
            out_ext = "";
            len_out_ext = 0;
        }
    }

    // This accept() method will be called from a java.awt.FileDialog object's
    // setFilenameFilter() method or a java.io.File object's list() method.
    // The arguments of this accept() method are provided by the calling object.
    // The argument String name is the filename to be filtered, in later
    // comments in this
    // method, it is called the "targetfilename".
    public boolean accept(File dir, String name)
    {
        boolean acceptfile = true;
        String filename;
        String fileext;

        if (dir == null) return accept(name);

        // The targetfilename will be broken into two parts that will be
        // filtered separately :
        // a filename and a file-extension.

        // Check for the number of dots in the targetfilename.
        // If there is more than one dot then after tokenizing the string:
        // - the last token will be the file-extension
        // - all previous tokens are concatenated and the dots that occurred
        // are put back in place. This string of concatenated tokens will be the
        // filename.
        // If there is no dot at all, then the filename equals the
        // targetfilename
        // and the file-extension is empty.
        // *******************************************************************************************
        StringTokenizer breakname = new StringTokenizer(name, ".");
        int fp = breakname.countTokens();
        Vector v = new Vector();
        for (int i = 0; i < fp; i++)
        {
            try
            {
                String t = breakname.nextToken();
                v.addElement(t);
            }
            catch (NoSuchElementException e1)
            {
            } // just don't add this element to the vector, nothing more
            // needed
        }

        // If the vector is empty, then there were no dots in the
        // targetfilename,
        // the filename equals the targetfilename and there is no
        // file-extension.
        if (v.isEmpty())
        {
            filename = name;
            fileext = "";
        }

        // If the vector is not empty compute filename and file-extension.
        else
        {
            if (v.size() > 1)
            {
                if (v.size() > 2)
                {
                    try
                    {
                        fileext = (String) v.lastElement();
                    }
                    catch (NoSuchElementException e2)
                    {
                        fileext = "";
                    }
                    String buf = "";
                    for (int i = 0; i < v.size() - 1; i++)
                    {
                        buf += (String) v.elementAt(i) + ".";
                    }
                    // omit the last dot
                    try
                    {
                        filename = buf.substring(0, buf.length() - 1);
                    }
                    catch (StringIndexOutOfBoundsException e3)
                    {
                        filename = "";
                    }
                }
                else
                {
                    filename = (String) v.elementAt(0);
                    fileext = (String) v.elementAt(1);
                }
            }
            else
            {
                fileext = "";
                filename = (String) v.elementAt(0);
            }
        }

        // Compute the parameters used for endsWith searches in the filename and
        // file-extension.
        if (filename.equals(""))
        {
            ew_filename = 0;
        }
        else ew_filename = filename.length() - len_out_fn;

        if (fileext.equals(""))
        {
            ew_fileext = 0;
        }
        else ew_fileext = fileext.length() - len_out_ext;

        // Compute the parameters used for regionMatches searches in the
        // filename and file-extension.
        // This computation is also a first for a filename and file-extension to
        // match the filter.
        // If it turns out that they don't match at this point, set the
        // parameter to the end of the string.
        // Note both the upper- and lowercase s to make the filter
        // case-independent.
        if (filename.equals(""))
        {
            rm_filename = 0;
        }
        else
        {
            rm_filename = filename.indexOf(out_fn.toLowerCase());
            if (rm_filename == -1)
            {
                rm_filename = filename.indexOf(out_fn.toUpperCase());
                if (rm_filename == -1) rm_filename = filename.length();// if no
                                                                        // match,
                                                                        // set
                // parameter to end of
                // string
            }
        }
        if (fileext.equals(""))
        {
            rm_fileext = 0;
        }
        else
        {
            rm_fileext = fileext.indexOf(out_ext.toLowerCase());
            if (rm_fileext == -1)
            {
                rm_fileext = fileext.indexOf(out_ext.toUpperCase());
                if (rm_fileext == -1) rm_fileext = fileext.length();// if no
                                                                    // match,
                                                                    // set
                // parameter to end of
                // string
            }
        }

        // actually accept or reject the targetfilenames
        // **********************************************************************************
        // if targetfilename is a directory then accept it and return true
        if ((new File(dir, name)).isDirectory()) return true;

        // if targetfilename is a file, do the filtering on both the filename
        // and the file-extension
        else
        {
            // if the filter input was empty then accept all targetfilenames
            if ((out_fn.equals("")) && (out_ext.equals("")))
            {
                acceptfile = true;
            }
            // if the filter input was not empty then accept only
            // targetfilenames that match the filter
            else if ((!out_fn.equals("")) && (out_ext.equals("")))
            {
                switch (fn_search)
                {
                case BEGIN:
                    if (filename.regionMatches(true, 0, out_fn, 0, len_out_fn))
                    {
                        acceptfile = true;
                    }
                    else acceptfile = false;
                    break;

                case MIDDLE:
                    if (filename.regionMatches(true, rm_filename, out_fn, 0,
                            len_out_fn))
                    {
                        acceptfile = true;
                    }
                    else acceptfile = false;
                    break;

                case END:
                    if (filename.regionMatches(true, ew_filename, out_fn, 0,
                            len_out_fn))
                    {
                        acceptfile = true;
                    }
                    else acceptfile = false;
                    break;

                default:
                    if (filename.startsWith(out_fn)) acceptfile = true;
                    else acceptfile = false;
                    break;
                }
            }
            else if ((out_fn.equals("")) && (!out_ext.equals("")))
            {
                switch (ext_search)
                {
                case BEGIN:
                    if (fileext.regionMatches(true, 0, out_ext, 0, len_out_ext))
                    {
                        acceptfile = true;
                    }
                    else acceptfile = false;
                    break;

                case MIDDLE:
                    if (fileext.regionMatches(true, rm_fileext, out_ext, 0,
                            len_out_ext))
                    {
                        acceptfile = true;
                    }
                    else acceptfile = false;
                    break;

                case END:
                    if (fileext.regionMatches(true, ew_fileext, out_ext, 0,
                            len_out_ext))
                    {
                        acceptfile = true;
                    }
                    else acceptfile = false;
                    break;

                default:
                    if (fileext.startsWith(out_ext)) acceptfile = true;
                    else acceptfile = false;
                    break;
                }
            }
            else if ((!out_fn.equals("")) && (!out_ext.equals("")))
            {
                switch (fn_search)
                {
                case BEGIN:
                    switch (ext_search)
                    {
                    case BEGIN:
                        if ((filename.regionMatches(true, 0, out_fn, 0,
                                len_out_fn))
                                && (fileext.regionMatches(true, 0, out_ext, 0,
                                        len_out_ext)))
                        {
                            acceptfile = true;
                        }
                        else acceptfile = false;
                        break;

                    case MIDDLE:
                        if ((filename.regionMatches(true, 0, out_fn, 0,
                                len_out_fn))
                                && (fileext.regionMatches(true, rm_fileext,
                                        out_ext, 0, len_out_ext)))
                        {
                            acceptfile = true;
                        }
                        else acceptfile = false;
                        break;

                    case END:
                        if ((filename.regionMatches(true, 0, out_fn, 0,
                                len_out_fn))
                                && (fileext.regionMatches(true, ew_fileext,
                                        out_ext, 0, len_out_ext)))
                        {
                            acceptfile = true;
                        }
                        else acceptfile = false;
                        break;

                    default:
                        if ((filename.regionMatches(true, 0, out_fn, 0,
                                len_out_fn))
                                && (fileext.startsWith(out_ext)))
                        {
                            acceptfile = true;
                        }
                        else acceptfile = false;
                        break;
                    }
                    break;

                case MIDDLE:
                    switch (ext_search)
                    {
                    case BEGIN:
                        if ((filename.regionMatches(true, rm_filename, out_fn,
                                0, len_out_fn))
                                && (fileext.regionMatches(true, 0, out_ext, 0,
                                        len_out_ext)))
                        {
                            acceptfile = true;
                        }
                        else acceptfile = false;
                        break;

                    case MIDDLE:
                        if ((filename.regionMatches(true, rm_filename, out_fn,
                                0, len_out_fn))
                                && (fileext.regionMatches(true, rm_fileext,
                                        out_ext, 0, len_out_ext)))
                        {
                            acceptfile = true;
                        }
                        else acceptfile = false;
                        break;

                    case END:
                        if ((filename.regionMatches(true, rm_filename, out_fn,
                                0, len_out_fn))
                                && (fileext.regionMatches(true, ew_fileext,
                                        out_ext, 0, len_out_ext)))
                        {
                            acceptfile = true;
                        }
                        else acceptfile = false;
                        break;

                    default:
                        if ((filename.regionMatches(true, rm_filename, out_fn,
                                0, len_out_fn))
                                && (fileext.startsWith(out_ext)))
                        {
                            acceptfile = true;
                        }
                        else acceptfile = false;
                        break;
                    }
                    break;

                case END:
                    switch (ext_search)
                    {
                    case BEGIN:
                        if ((filename.regionMatches(true, ew_filename, out_fn,
                                0, len_out_fn))
                                && (fileext.regionMatches(true, 0, out_ext, 0,
                                        len_out_ext)))
                        {
                            acceptfile = true;
                        }
                        else acceptfile = false;
                        break;

                    case MIDDLE:
                        if ((filename.regionMatches(true, ew_filename, out_fn,
                                0, len_out_fn))
                                && (fileext.regionMatches(true, rm_fileext,
                                        out_ext, 0, len_out_ext)))
                        {
                            acceptfile = true;
                        }
                        else acceptfile = false;
                        break;

                    case END:
                        if ((filename.regionMatches(true, ew_filename, out_fn,
                                0, len_out_fn))
                                && (fileext.regionMatches(true, ew_fileext,
                                        out_ext, 0, len_out_ext)))
                        {
                            acceptfile = true;
                        }
                        else acceptfile = false;
                        break;

                    default:
                        if ((filename.regionMatches(true, ew_filename, out_fn,
                                0, len_out_fn))
                                && (fileext.startsWith(out_ext)))
                        {
                            acceptfile = true;
                        }
                        else acceptfile = false;
                        break;
                    }
                    break;

                default:
                    switch (ext_search)
                    {
                    case BEGIN:
                        if ((filename.startsWith(out_fn))
                                && (fileext.regionMatches(true, 0, out_ext, 0,
                                        len_out_ext)))
                        {
                            acceptfile = true;
                        }
                        else acceptfile = false;
                        break;

                    case MIDDLE:
                        if ((filename.startsWith(out_fn))
                                && (fileext.regionMatches(true, rm_fileext,
                                        out_ext, 0, len_out_ext)))
                        {
                            acceptfile = true;
                        }
                        else acceptfile = false;
                        break;

                    case END:
                        if ((filename.startsWith(out_fn))
                                && (fileext.regionMatches(true, ew_fileext,
                                        out_ext, 0, len_out_ext)))
                        {
                            acceptfile = true;
                        }
                        else acceptfile = false;
                        break;

                    default:
                        if ((filename.startsWith(out_fn))
                                && (fileext.startsWith(out_ext)))
                        {
                            acceptfile = true;
                        }
                        else acceptfile = false;
                        break;
                    }
                }
            }
            return acceptfile;
        }
    }

    // This accept() method will be called from a java.awt.FileDialog object's
    // setFilenameFilter() method or a java.io.File object's list() method.
    // The arguments of this accept() method are provided by the calling object.
    // The argument String name is the filename to be filtered, in later
    // comments in this
    // method, it is called the "targetfilename".

    public boolean accept(String name)
    {
        boolean acceptfile = true;
        String filename;
        String fileext;

        // The targetfilename will be broken into two parts that will be
        // filtered separately :
        // a filename and a file-extension.

        // Check for the number of dots in the targetfilename.
        // If there is more than one dot then after tokenizing the string:
        // - the last token will be the file-extension
        // - all previous tokens are concatenated and the dots that occurred
        // are put back in place. This string of concatenated tokens will be the
        // filename.
        // If there is no dot at all, then the filename equals the
        // targetfilename
        // and the file-extension is empty.
        // *******************************************************************************************
        StringTokenizer breakname = new StringTokenizer(name, ".");
        int fp = breakname.countTokens();
        Vector v = new Vector();
        for (int i = 0; i < fp; i++)
        {
            try
            {
                String t = breakname.nextToken();
                v.addElement(t);
            }
            catch (NoSuchElementException e1)
            {
                ;
            } // just don't add this element to the vector, nothing more
            // needed
        }

        // If the vector is empty, then there were no dots in the
        // targetfilename,
        // the filename equals the targetfilename and there is no
        // file-extension.
        if (v.isEmpty())
        {
            filename = name;
            fileext = "";
        }
        // If the vector is not empty compute filename and file-extension.
        else
        {
            if (v.size() > 1)
            {
                if (v.size() > 2)
                {
                    try
                    {
                        fileext = (String) v.lastElement();
                    }
                    catch (NoSuchElementException e2)
                    {
                        fileext = "";
                    }
                    String buf = "";
                    for (int i = 0; i < v.size() - 1; i++)
                    {
                        buf += (String) v.elementAt(i) + ".";
                    }
                    // omit the last dot
                    try
                    {
                        filename = buf.substring(0, buf.length() - 1);
                    }
                    catch (StringIndexOutOfBoundsException e3)
                    {
                        filename = "";
                    }
                }
                else
                {
                    filename = (String) v.elementAt(0);
                    fileext = (String) v.elementAt(1);
                }
            }
            else
            {
                fileext = "";
                filename = (String) v.elementAt(0);
            }
        }

        // Compute the parameters used for endsWith searches in the filename and
        // file-extension.
        if (filename.equals(""))
        {
            ew_filename = 0;
        }
        else ew_filename = filename.length() - len_out_fn;

        if (fileext.equals(""))
        {
            ew_fileext = 0;
        }
        else ew_fileext = fileext.length() - len_out_ext;

        // Compute the parameters used for regionMatches searches in the
        // filename and file-extension.
        // This computation is also a first for a filename and file-extension to
        // match the filter.
        // If it turns out that they don't match at this point, set the
        // parameter to the end of the string.
        // Note both the upper- and lowercase s to make the filter
        // case-independent.
        if (filename.equals(""))
        {
            rm_filename = 0;
        }
        else
        {
            rm_filename = filename.indexOf(out_fn.toLowerCase());
            if (rm_filename == -1)
            {
                rm_filename = filename.indexOf(out_fn.toUpperCase());
                if (rm_filename == -1) rm_filename = filename.length();// if no
                                                                        // match,
                                                                        // set
                // parameter to end of
                // string
            }
        }

        if (fileext.equals(""))
        {
            rm_fileext = 0;
        }
        else
        {
            rm_fileext = fileext.indexOf(out_ext.toLowerCase());
            if (rm_fileext == -1)
            {
                rm_fileext = fileext.indexOf(out_ext.toUpperCase());
                if (rm_fileext == -1) rm_fileext = fileext.length();// if no
                                                                    // match,
                                                                    // set
                // parameter to end of
                // string
            }
        }

        // actually accept or reject the targetfilenames
        // **********************************************************************************
        // if targetfilename is a directory then accept it and return true
        // if ((new File(dir, name)).isDirectory()) return true;
        if (false)
        {
            return true;
        }
        // if targetfilename is a file, do the filtering on both the filename
        // and the file-extension
        else
        {
            // if the filter input was empty then accept all targetfilenames
            if ((out_fn.equals("")) && (out_ext.equals("")))
            {
                acceptfile = true;
            }
            // if the filter input was not empty then accept only
            // targetfilenames that match the filter
            else if ((!out_fn.equals("")) && (out_ext.equals("")))
            {
                switch (fn_search)
                {
                case BEGIN:
                    if (filename.regionMatches(true, 0, out_fn, 0, len_out_fn))
                    {
                        acceptfile = true;
                    }
                    else acceptfile = false;
                    break;
                case MIDDLE:
                    if (filename.regionMatches(true, rm_filename, out_fn, 0,
                            len_out_fn))
                    {
                        acceptfile = true;
                    }
                    else acceptfile = false;
                    break;
                case END:
                    if (filename.regionMatches(true, ew_filename, out_fn, 0,
                            len_out_fn))
                    {
                        acceptfile = true;
                    }
                    else acceptfile = false;
                    break;
                default:
                    if (filename.startsWith(out_fn)) acceptfile = true;
                    else acceptfile = false;
                    break;
                }
            }

            else if ((out_fn.equals("")) && (!out_ext.equals("")))
            {
                switch (ext_search)
                {
                case BEGIN:
                    if (fileext.regionMatches(true, 0, out_ext, 0, len_out_ext))
                    {
                        acceptfile = true;
                    }
                    else acceptfile = false;
                    break;
                case MIDDLE:
                    if (fileext.regionMatches(true, rm_fileext, out_ext, 0,
                            len_out_ext))
                    {
                        acceptfile = true;
                    }
                    else acceptfile = false;
                    break;
                case END:
                    if (fileext.regionMatches(true, ew_fileext, out_ext, 0,
                            len_out_ext))
                    {
                        acceptfile = true;
                    }
                    else acceptfile = false;
                    break;
                default:
                    if (fileext.startsWith(out_ext)) acceptfile = true;
                    else acceptfile = false;
                    break;
                }
            }

            else if ((!out_fn.equals("")) && (!out_ext.equals("")))
            {
                switch (fn_search)
                {
                case BEGIN:
                    switch (ext_search)
                    {
                    case BEGIN:
                        if ((filename.regionMatches(true, 0, out_fn, 0,
                                len_out_fn))
                                && (fileext.regionMatches(true, 0, out_ext, 0,
                                        len_out_ext)))
                        {
                            acceptfile = true;
                        }
                        else acceptfile = false;
                        break;
                    case MIDDLE:
                        if ((filename.regionMatches(true, 0, out_fn, 0,
                                len_out_fn))
                                && (fileext.regionMatches(true, rm_fileext,
                                        out_ext, 0, len_out_ext)))
                        {
                            acceptfile = true;
                        }
                        else acceptfile = false;
                        break;
                    case END:
                        if ((filename.regionMatches(true, 0, out_fn, 0,
                                len_out_fn))
                                && (fileext.regionMatches(true, ew_fileext,
                                        out_ext, 0, len_out_ext)))
                        {
                            acceptfile = true;
                        }
                        else acceptfile = false;
                        break;
                    default:
                        if ((filename.regionMatches(true, 0, out_fn, 0,
                                len_out_fn))
                                && (fileext.startsWith(out_ext)))
                        {
                            acceptfile = true;
                        }
                        else acceptfile = false;
                        break;
                    }
                    break;
                case MIDDLE:
                    switch (ext_search)
                    {
                    case BEGIN:
                        if ((filename.regionMatches(true, rm_filename, out_fn,
                                0, len_out_fn))
                                && (fileext.regionMatches(true, 0, out_ext, 0,
                                        len_out_ext)))
                        {
                            acceptfile = true;
                        }
                        else acceptfile = false;
                        break;
                    case MIDDLE:
                        if ((filename.regionMatches(true, rm_filename, out_fn,
                                0, len_out_fn))
                                && (fileext.regionMatches(true, rm_fileext,
                                        out_ext, 0, len_out_ext)))
                        {
                            acceptfile = true;
                        }
                        else acceptfile = false;
                        break;
                    case END:
                        if ((filename.regionMatches(true, rm_filename, out_fn,
                                0, len_out_fn))
                                && (fileext.regionMatches(true, ew_fileext,
                                        out_ext, 0, len_out_ext)))
                        {
                            acceptfile = true;
                        }
                        else acceptfile = false;
                        break;
                    default:
                        if ((filename.regionMatches(true, rm_filename, out_fn,
                                0, len_out_fn))
                                && (fileext.startsWith(out_ext)))
                        {
                            acceptfile = true;
                        }
                        else acceptfile = false;
                        break;
                    }
                    break;
                case END:
                    switch (ext_search)
                    {
                    case BEGIN:
                        if ((filename.regionMatches(true, ew_filename, out_fn,
                                0, len_out_fn))
                                && (fileext.regionMatches(true, 0, out_ext, 0,
                                        len_out_ext)))
                        {
                            acceptfile = true;
                        }
                        else acceptfile = false;
                        break;
                    case MIDDLE:
                        if ((filename.regionMatches(true, ew_filename, out_fn,
                                0, len_out_fn))
                                && (fileext.regionMatches(true, rm_fileext,
                                        out_ext, 0, len_out_ext)))
                        {
                            acceptfile = true;
                        }
                        else acceptfile = false;
                        break;
                    case END:
                        if ((filename.regionMatches(true, ew_filename, out_fn,
                                0, len_out_fn))
                                && (fileext.regionMatches(true, ew_fileext,
                                        out_ext, 0, len_out_ext)))
                        {
                            acceptfile = true;
                        }
                        else acceptfile = false;
                        break;
                    default:
                        if ((filename.regionMatches(true, ew_filename, out_fn,
                                0, len_out_fn))
                                && (fileext.startsWith(out_ext)))
                        {
                            acceptfile = true;
                        }
                        else acceptfile = false;
                        break;
                    }
                    break;
                default:
                    switch (ext_search)
                    {
                    case BEGIN:
                        if ((filename.startsWith(out_fn))
                                && (fileext.regionMatches(true, 0, out_ext, 0,
                                        len_out_ext)))
                        {
                            acceptfile = true;
                        }
                        else acceptfile = false;
                        break;
                    case MIDDLE:
                        if ((filename.startsWith(out_fn))
                                && (fileext.regionMatches(true, rm_fileext,
                                        out_ext, 0, len_out_ext)))
                        {
                            acceptfile = true;
                        }
                        else acceptfile = false;
                        break;
                    case END:
                        if ((filename.startsWith(out_fn))
                                && (fileext.regionMatches(true, ew_fileext,
                                        out_ext, 0, len_out_ext)))
                        {
                            acceptfile = true;
                        }
                        else acceptfile = false;
                        break;
                    default:
                        if ((filename.startsWith(out_fn))
                                && (fileext.startsWith(out_ext)))
                        {
                            acceptfile = true;
                        }
                        else acceptfile = false;
                        break;
                    }
                }
            }
            return acceptfile;
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.FileFilter#accept(java.io.File)
     */
    public boolean accept(File pathname)
    {
        return accept(pathname.getParentFile(), pathname.getName());
    } // end accept

    public String toString()
    {
        // TODO Auto-generated method stub
        return "WildCardFileFilter [" + pattern + "]";
    }

}
