package com.hogdev.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Vector;

public class MP3Playlist
{
    boolean bLocal = true;

    public static void main(String[] args)
    {
        MP3Playlist mp3l = new MP3Playlist();
        mp3l.process();
    }

    // properties
    protected String m_strDir;

    protected WildCardFileFilter wcff;

    public MP3Playlist()
    {
        this(".");
    }

    public MP3Playlist(String strDir)
    {
        try
        {
            m_strDir = new File(strDir).getCanonicalPath();
            m_strDir += File.separator;
            wcff = new WildCardFileFilter("*.mp3");
        }
        catch (IOException e)
        {
            e.printStackTrace(); // To change body of catch statement use
            // Options | File Templates.
        }
    }

    public static String getUsage()
    {
        return "MP3Playlist\n"
                + " desc:  used to create mp3 playlist of music files located in the\n"
                + "        current and child folders.\n"
                + " syntax: java com.hogge.util.MP3Playlist";
    }

    public void process()
    {
        File flCurrentDir = new File(m_strDir);
        if (flCurrentDir.exists())
        {
            process(flCurrentDir);
        }
        else
        {
            System.out.println(m_strDir + " does not exist");
        }
    }

    // recursive
    private Vector process(File fileDir)
    {
        System.out.println("Processing " + fileDir.getPath());
        Vector vec = new Vector();
        try
        {
            String strPlayListName = fileDir.getName() + ".m3u";

            File fl = new File(fileDir, strPlayListName);
            BufferedWriter bw = new BufferedWriter(new FileWriter(fl));

            File fa[] = fileDir.listFiles((FileFilter) wcff);
            Arrays.sort(fa);
            for (int i = 0; i < fa.length; i++)
            {
                if (fa[i].isDirectory())
                {
                    // not now!
                }
                else
                {
                    // add to playlist
                    String strName = fa[i].getName();
                    strName = URLEncoder.encode(strName);
                    bw.write(strName);
                    bw.newLine();

                    strName = URLEncoder.encode(fa[i].getParentFile().getName()
                            + File.separator)
                            + strName;
                    vec.add(strName);
                }
            }

            for (int i = 0; i < fa.length; i++)
            {
                if (fa[i].isDirectory())
                {
                    Vector vec2 = process(fa[i]);
                    for (int j = 0; j < vec2.size(); j++)
                    {
                        String strName = (String) vec2.elementAt(j);
                        bw.write(strName);
                        bw.newLine();
                        String strDirName = fileDir.getName();
                        strDirName = URLEncoder.encode(strDirName
                                + File.separator);

                        vec.add(strDirName + strName);
                    }
                }
                else
                {
                    // alredy done
                }
            }
            bw.flush();
            bw.close();

            if (fl.length() == 0) fl.delete();
        }
        catch (IOException ioe)
        {
            System.out.println(ioe);
        }
        return vec;
    }

    public String getDir()
    {
        return m_strDir;
    }

    public void setDir(String strDir)
    {
        m_strDir = strDir;
    }
}
