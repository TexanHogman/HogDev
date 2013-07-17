package com.hogdev.util;

import gnu.getopt.Getopt;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;

public class SearchAndReplaceOld implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 7598912677894692632L;

    // bean properties
    protected String strSearch_;

    protected String strReplace_;

    protected String strPattern_;

    protected String strStartLocation_;

    protected String strStatus_;

    protected String strInfo_;

    protected boolean bRecurse_;

    protected boolean bMatchCase_;

    protected boolean bStop_;

    private boolean bReplace_ = false;

    // for notifying listeners
    private PropertyChangeSupport listeners;

    public static void main(String[] args)
    {
        SearchAndReplaceOld sar = new SearchAndReplaceOld();
        sar.addPropertyChangeListener(new PropertyChangeListener()
        {
            @Override
			public void propertyChange(PropertyChangeEvent evt)
            {
                System.out.println(evt.getNewValue());
            }
        });

        // get local path
        String strStartIn = new File(".").getAbsolutePath();
        strStartIn = strStartIn.substring(0, strStartIn
                .lastIndexOf(File.separator));
        sar.setStartLocation(strStartIn);

        Getopt g = new Getopt("SearchAndReplace", args, "s:r:Rmp:");

        int c;
        boolean bError = false;

        while ((c = g.getopt()) != -1)
        {
            switch (c)
            {
            case 's':
                sar.setSearch(g.getOptarg());
                break;
            case 'r':
                sar.bReplace_ = true;
                sar.setReplace(g.getOptarg());
                break;
            case 'R':
                sar.setRecurse(true);
                break;
            case 'm':
                sar.setMatchCase(true);
                break;
            case 'p':
                sar.setPattern(g.getOptarg());
                break;
            case '?':
                bError = true;
                break;
            default:
                System.out.println("getopt() returned " + (char) c);
                break;
            }
        }

        if (bError)
        {
            printUsage();
        }
        else
        {
            if (sar.getSearch() == null || sar.getSearch().length() == 0) printUsage();
            else sar.process();
        }
    }

    public SearchAndReplaceOld()
    {
        strPattern_ = "*.*";
        strStatus_ = "";
        strInfo_ = "";
        strReplace_ = "";
        strSearch_ = "";
        strStartLocation_ = "";

        listeners = new PropertyChangeSupport(this);
    }

    public static void printUsage()
    {
        System.out.println("Usage: SearchAndReplace -s [-v] [-R] [-r] [-p]");
        System.out.println("\t -s search string");
        System.out.println("\t -R (Recursive)");
        System.out.println("\t -r replacement string");
        System.out.println("\t -p file name pattern [*.*]");
    }

    public void startReplace()
    {
        bReplace_ = true;
        start();
    }

    public void startSearch()
    {
        bReplace_ = false;
        start();
    }

    private void start()
    {
        bStop_ = false;
        Thread th = new Thread(new Runnable()
        {
            @Override
			public void run()
            {
                process();
            }
        });
        th.start();
    }

    private void process()
    {
        bStop_ = false;
        strInfo_ = "";
        strStatus_ = "";

        setStatus("Starting");

        // do not allow a blank or null source string
        if (strSearch_ == null || strSearch_.length() == 0)
        {
            setStatus("Source string is required.");
        }
        // do not allow a blank or null start in string
        else if (strStartLocation_ == null || strStartLocation_.length() == 0)
        {
            setStatus("Start In string is required.");
        }
        else
        {
            // we are ready to go now
            File file = new File(strStartLocation_);
            int iRc = processItem(file);
        }

        setStatus("Stopping");
    }

    private int processItem(File file)
    {
        int iRc = 0;
        if (file.isDirectory())
        {
            setStatus(file.getAbsolutePath());

            File[] fa_Children = file
                    .listFiles((FilenameFilter) new WildcardFileFilter(
                            strPattern_, IOCase.INSENSITIVE));
            for (int i = 0; fa_Children != null && i < fa_Children.length
                    && !bStop_; i++)
            {
                if (bRecurse_ || fa_Children[i].isFile()) processItem(fa_Children[i]);
            }
        }
        else
        {
            setStatus(file.getAbsolutePath());

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
                            if (bMatchCase_)
                            {
                                if (strBuffer.indexOf(strSearch_) != -1)
                                {
                                    bFound = true;
                                }
                            }
                            else
                            {
                                if (strBuffer.toLowerCase().indexOf(
                                        strSearch_.toLowerCase()) != -1)
                                {
                                    bFound = true;
                                }
                            }

                            if (bFound)
                            {
                                if (bReplace_)
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

                    if (bReplace_ && bFound)
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
                                    if (bMatchCase_)
                                    {
                                        iIndex = strBuffer.indexOf(strSearch_);
                                    }
                                    else
                                    {
                                        iIndex = strBuffer.toLowerCase()
                                                .indexOf(
                                                        strSearch_
                                                                .toLowerCase());
                                    }

                                    // need to check for multiple instances in
                                    // same line
                                    while (iIndex != -1)
                                    {
                                        setInfo(file.getAbsolutePath() + ": "
                                                + strBuffer);

                                        // replace text
                                        if (strReplace_ == null)
                                        {
                                            // must just want to delete text so
                                            // I will remove it
                                            strBuffer = strBuffer.substring(0,
                                                    iIndex)
                                                    + strBuffer
                                                            .substring(
                                                                    iIndex
                                                                            + strSearch_
                                                                                    .length(),
                                                                    strBuffer
                                                                            .length());
                                        }
                                        else
                                        {
                                            strBuffer = strBuffer.substring(0,
                                                    iIndex)
                                                    + strReplace_
                                                    + strBuffer
                                                            .substring(
                                                                    iIndex
                                                                            + strSearch_
                                                                                    .length(),
                                                                    strBuffer
                                                                            .length());
                                            iIndex += strReplace_.length();
                                        }

                                        // I am putting this in so that I don't
                                        // waste time if it is a match case
                                        if (bMatchCase_)
                                        {
                                            iIndex = strBuffer.indexOf(
                                                    strSearch_, iIndex);
                                        }
                                        else
                                        {
                                            iIndex = strBuffer
                                                    .toLowerCase()
                                                    .indexOf(
                                                            strSearch_
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

    // properties accessors
    public String getSearch()
    {
        return strSearch_;
    }

    public void setSearch(String strSearch)
    {
        strSearch_ = strSearch;
    }

    public String getReplace()
    {
        return strReplace_;
    }

    public void setReplace(String strReplace)
    {
        strReplace_ = strReplace;
    }

    public String getPattern()
    {
        return strPattern_;
    }

    public void setPattern(String strPattern)
    {
        strPattern_ = strPattern;
    }

    public String getStartLocation()
    {
        return strStartLocation_;
    }

    public void setStartLocation(String strStartIn)
    {
        strStartLocation_ = strStartIn;
    }

    public boolean getRecurse()
    {
        return bRecurse_;
    }

    public void setRecurse(boolean bRecurse)
    {
        bRecurse_ = bRecurse;
    }

    public boolean getMatchCase()
    {
        return bMatchCase_;
    }

    public void setMatchCase(boolean bMatchCase)
    {
        bMatchCase_ = bMatchCase;
    }

    public void stop()
    {
        bStop_ = true;
    }

    private void setStatus(String strStatus)
    {
        String strOld = strStatus_;
        strStatus_ = strStatus;
        listeners.firePropertyChange("status", strOld, strStatus);
    }

    public String getStatus()
    {
        return strStatus_;
    }

    private void setInfo(String strInfo)
    {
        String strOld = strInfo_;
        strInfo_ = strInfo;
        listeners.firePropertyChange("info", strOld, strInfo);
    }

    public String getInfo()
    {
        return strInfo_;
    }

    public void addPropertyChangeListener(PropertyChangeListener l)
    {
        listeners.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l)
    {
        listeners.removePropertyChangeListener(l);
    }

}
