package com.hogdev.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;

public class SearchAndReplace implements Runnable, Serializable,
        PropertyChangeListener

{
    /**
     * 
     */
    private static final long serialVersionUID = -1421818896450805558L;

    protected String strSearch;

    protected String strReplace;

    protected String strFilter;

    protected String strStartIn;

    protected String strStatus;

    protected String strInfo;

    protected boolean bRecurse;

    protected boolean bMatchCase;

    protected boolean bReplaceFlag;

    protected boolean bVerbose;

    protected boolean bStop;

    // for notifying listeners
    private PropertyChangeSupport listeners;

    private WildCardFileFilter wcf;

    public static void main(String[] args)
    {
        int i = 0, j;
        String arg;
        char flag;
        String searchString = null;
        String replaceString = null;
        boolean bReplaceFlag = false;
        boolean bRecurse = false;
        boolean bVerbose = false;

        while (i < args.length && args[i].startsWith("-"))
        {
            arg = args[i++];

            // use this type of check for arguments that require arguments
            if (arg.equals("-verbose"))
            {
                bVerbose = true;
            }

            // use this type of check for arguments that require arguments
            else if (arg.equals("-s"))
            {
                if (i < args.length) searchString = args[i++];
                else System.err.println("-s requires a search string");
            }

            // use this type of check for arguments that require arguments
            else if (arg.equals("-r"))
            {
                if (i < args.length)
                {
                    replaceString = args[i++];
                    bReplaceFlag = true;
                }
                else System.err.println("-r requires a replace string");
            }

            // use this type of check for a series of flag arguments
            else
            {
                for (j = 1; j < arg.length(); j++)
                {
                    flag = arg.charAt(j);
                    switch (flag)
                    {
                    case 'R':
                        bRecurse = true;
                        break;

                    case 'h':
                    case '?':
                        System.err.println("");
                        System.err.println("	-verbose: verbose mode");
                        System.err.println("	-R:       recursive operation");
                        System.err
                                .println("	-r:       string used in replacement");
                        System.err.println("	-s:       string used in search");
                        System.err.println("	-h:       this screen");
                        System.err.println("	-?:       this screen");
                        System.err
                                .println("	filename: filename to search ( wildcards are allowed )");
                        System.err.println("");
                        break;

                    default:
                        System.err.println("SearchAndReplace: illegal option "
                                + flag);
                        break;
                    }
                }
            }
        }

        if (i == args.length)
        {
            System.err.println(SearchAndReplace.getUsage());
        }
        else
        {
            if (searchString == null)
            {
                searchString = args[i++];
            }

            String strFileName;
            if (i == args.length) strFileName = "*.*";
            else strFileName = args[i];

            SearchAndReplace sar = new SearchAndReplace();
            sar.addPropertyChangeListener(sar);

            // get local path
            String strStartIn = new File(".").getAbsolutePath();
            strStartIn = strStartIn.substring(0, strStartIn
                    .lastIndexOf(File.separator));
            sar.setStartIn(strStartIn);
            sar.setSearch(searchString);
            sar.setReplace(replaceString);
            sar.setRecurse(bRecurse);
            sar.setFilter(strFileName);
            sar.setVerbose(bVerbose);

            sar.doSearch();
        }
    }

    public SearchAndReplace()
    {
        strSearch = "";
        strReplace = "";
        strFilter = "*.*";
        strStartIn = "";
        strStatus = "";
        strInfo = "";
        bRecurse = false;
        bMatchCase = false;
        bReplaceFlag = false;
        bVerbose = false;
        bStop = false;
        listeners = new PropertyChangeSupport(this);
    }

    public static String getUsage()
    {
        return "SearhAndReplace\n"
                + " desc:  grep tool with replace ability\n"
                + " syntax: java com.hogge.util.SearchAndReplace [-verbose] [-R] [-r]\n"
                + "                                              -s \"search for this\" filename\n"
                + " flags:\n" + "  -verbose verbose mode\n"
                + "  -R recursive\n"
                + "  -r replace string (do not use for search only)\n"
                + "  -s search string\n"
                + "  filename wilcard file name filter";
    }

    public void doSearch()
    {
        bStop = false;
        strInfo = "";
        strStatus = "";

        listeners.firePropertyChange("search_status", "", "Starting");

        wcf = new WildCardFileFilter(strFilter);

        // do not allow a blank or null source string
        if (strSearch == null || strSearch.length() == 0)
        {
            setStatus("Source string is required.");
        }
        // do not allow a blank or null start in string
        else if (strStartIn == null || strStartIn.length() == 0)
        {
            setStatus("Start In string is required.");
        }
        else
        {
            // we are ready to go now
            File file = new File(strStartIn);
            int iRc = processItem(file);
        }

        listeners.firePropertyChange("search_status", "", "Stopping");
    }

    private int processItem(File file)
    {
        int iRc = 0;
        if (file.isDirectory())
        {
            if (bVerbose) setStatus(file.getAbsolutePath());

            File[] fa_Children = file.listFiles((FileFilter) wcf);
            for (int i = 0; i < fa_Children.length && !bStop; i++)
            {
                if (fa_Children[i].isDirectory() && bRecurse
                        || fa_Children[i].isFile()) processItem(fa_Children[i]);
            }
        }
        else
        {
            if (bVerbose) setStatus(file.getAbsolutePath());

            BufferedReader br = null;
            boolean bFound = false;
            // do the search
            try
            {
                br = new BufferedReader(new InputStreamReader(
                        new FileInputStream(file)));
                if (br != null)
                {
                    while (br.ready())
                    {
                        bFound = false;
                        String strBuffer = br.readLine();

                        if (strBuffer != null)
                        {
                            // I am putting this in so that I don't waste time
                            // if it is a match case
                            if (bMatchCase)
                            {
                                if (strBuffer.indexOf(strSearch) != -1)
                                {
                                    bFound = true;
                                }
                            }
                            else
                            {
                                if (strBuffer.toLowerCase().indexOf(
                                        strSearch.toLowerCase()) != -1)
                                {
                                    bFound = true;
                                }
                            }

                            if (bFound)
                            {
                                if (bReplaceFlag == true)
                                {
                                    // if I am doing a search AND replace then
                                    // once I find one instance of source break
                                    break;
                                }
                                else
                                {
                                    setInfo(file.getAbsolutePath() + ": "
                                            + strBuffer);
                                }
                            }
                        }
                    }

                    br.close();

                    if (bReplaceFlag && bFound)
                    {
                        br = new BufferedReader(new InputStreamReader(
                                new FileInputStream(file)));
                        File fileTmp = File.createTempFile("SearchAndReplace",
                                null);
                        BufferedWriter bw = new BufferedWriter(new FileWriter(
                                fileTmp));
                        fileTmp.deleteOnExit();

                        if (br != null)
                        {
                            while (br.ready())
                            {
                                String strBuffer = br.readLine();
                                if (strBuffer != null)
                                {
                                    int iIndex = -1;

                                    // I am putting this in so that I don't
                                    // waste time if it is a match case
                                    if (bMatchCase)
                                    {
                                        iIndex = strBuffer.indexOf(strSearch);
                                    }
                                    else
                                    {
                                        iIndex = strBuffer
                                                .toLowerCase()
                                                .indexOf(
                                                        strSearch.toLowerCase());
                                    }

                                    // need to check for multiple instances in
                                    // same line
                                    while (iIndex != -1)
                                    {
                                        setInfo(file.getAbsolutePath() + ": "
                                                + strBuffer);

                                        // replace text
                                        if (strReplace == null)
                                        {
                                            // must just want to delete text so
                                            // I will remove it
                                            strBuffer = strBuffer.substring(0,
                                                    iIndex)
                                                    + strBuffer
                                                            .substring(
                                                                    iIndex
                                                                            + strSearch
                                                                                    .length(),
                                                                    strBuffer
                                                                            .length());
                                        }
                                        else
                                        {
                                            strBuffer = strBuffer.substring(0,
                                                    iIndex)
                                                    + strReplace
                                                    + strBuffer
                                                            .substring(
                                                                    iIndex
                                                                            + strSearch
                                                                                    .length(),
                                                                    strBuffer
                                                                            .length());
                                            iIndex += strReplace.length();
                                        }

                                        // I am putting this in so that I don't
                                        // waste time if it is a match case
                                        if (bMatchCase)
                                        {
                                            iIndex = strBuffer.indexOf(
                                                    strSearch, iIndex);
                                        }
                                        else
                                        {
                                            iIndex = strBuffer
                                                    .toLowerCase()
                                                    .indexOf(
                                                            strSearch
                                                                    .toLowerCase(),
                                                            iIndex);
                                        }
                                    }
                                    if (bw != null)
                                    {
                                        bw.write(strBuffer, 0, strBuffer
                                                .length());
                                        bw.newLine();
                                    }
                                }
                            }
                            br.close();
                        }
                        if (bw != null)
                        {
                            bw.close();

                            // at this point we should have our updated file
                            if (file.delete())
                            {
                                fileTmp.renameTo(file);
                            }
                        }
                        fileTmp.delete();
                    }

                }
            }
            catch (FileNotFoundException ex)
            {
                setStatus(file.getAbsolutePath() + " not found.");
            }
            catch (IOException ex)
            {
                setStatus(file.getAbsolutePath() + " caused an IOException.");
            }
            finally
            {
            }
        }

        return iRc;
    }

    public String getSearch()
    {
        return strSearch;
    }

    public void setSearch(String strSearch)
    {
        this.strSearch = strSearch;
    }

    public String getReplace()
    {
        return strReplace;
    }

    public void setReplace(String strReplace)
    {
        this.strReplace = strReplace;
        setReplaceFlag(true);
    }

    public String getFilter()
    {
        return strFilter;
    }

    public void setFilter(String strFilter)
    {
        this.strFilter = strFilter;
    }

    public String getStartIn()
    {
        return strStartIn;
    }

    public void setStartIn(String strStartIn)
    {
        this.strStartIn = strStartIn;
    }

    public boolean getRecurse()
    {
        return bRecurse;
    }

    public void setRecurse(boolean bRecurse)
    {
        this.bRecurse = bRecurse;
    }

    public boolean getReplaceFlag()
    {
        return bReplaceFlag;
    }

    public void setReplaceFlag(boolean bReplaceFlag)
    {
        this.bReplaceFlag = bReplaceFlag;
    }

    public boolean getMatchCase()
    {
        return bMatchCase;
    }

    public void setMatchCase(boolean bMatchCase)
    {
        this.bMatchCase = bMatchCase;
    }

    public boolean getVerbose()
    {
        return bVerbose;
    }

    public void setVerbose(boolean bVerbose)
    {
        this.bVerbose = bVerbose;
    }

    public boolean getStop()
    {
        return bStop;
    }

    public void setStop(boolean bStop)
    {
        this.bStop = bStop;
    }

    public void setStatus(String strStatus)
    {
        listeners
                .firePropertyChange("search_status", this.strStatus, strStatus);
        this.strStatus = strStatus;
    }

    public String getStatus()
    {
        return strStatus;
    }

    public void setInfo(String strInfo)
    {
        listeners.firePropertyChange("search_info", this.strInfo, strInfo);
        this.strInfo = strInfo;
    }

    public String getInfo()
    {
        return strInfo;
    }

    public void addPropertyChangeListener(PropertyChangeListener l)
    {
        listeners.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l)
    {
        listeners.removePropertyChangeListener(l);
    }

    @Override
	public void propertyChange(PropertyChangeEvent evt)
    {
        System.out.println(evt.getNewValue());
    }

    @Override
	public void run()
    {
        doSearch();
    }

}