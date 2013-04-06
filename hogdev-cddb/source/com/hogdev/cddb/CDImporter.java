package com.hogdev.cddb;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import com.hogdev.util.GeneralUtils;

import de.vdheide.mp3.MP3File;

public class CDImporter implements Runnable, PropertyChangeListener
{
    String strDriver;
    String strConnect;
    String strUserId;
    String strPwd;
    boolean bVerbose;
    boolean bPromptForIndex;
    int iDirIndex = 0;

    PreparedStatement pstmtMaxIndex;
    Writer wr;

    // for notifying listeners
    private PropertyChangeSupport listeners;

    public static void main(String[] args)
    {
        while (true)
        {
            System.out.println(getUsage());
            String str = GeneralUtils.getKeyboardInput("Enter your arguments here: ");
            StringTokenizer st = new StringTokenizer(str);
            ArrayList al = new ArrayList();
            while (st.hasMoreTokens())
            {
                al.add(st.nextToken());
            }

            args = (String[]) al.toArray(new String[0]);

            int i = 0, j;
            String arg;
            char flag;
            boolean bPromptForIndex = false;
            boolean bVerbose = false;
            boolean bOk = true;

            while (i < args.length && args[i].startsWith("-"))
            {
                arg = args[i++];

                for (j = 1; j < arg.length(); j++)
                {
                    flag = arg.charAt(j);
                    switch (flag)
                    {
                        case 'q':
                        case 'Q':
                            System.exit(0);
                            break;

                        case 'v':
                        case 'V':
                            bVerbose = true;
                            break;

                        case 'p':
                        case 'P':
                            bPromptForIndex = true;
                            break;

                        case 'H':
                        case 'h':
                        case '?':
                            System.out.println(getUsage());
                            bOk = false;
                            break;

                        default:
                            System.err.println("CDImporter: illegal option " + flag);
                            bOk = false;
                            break;
                    }
                }
            }

            if (bOk)
            {
                CDImporter x = new CDImporter();
                x.setVerbose(bVerbose);
                x.setUserId(null);
                x.setPwd(null);
                x.setDriver("sun.jdbc.odbc.JdbcOdbcDriver");
                x.setConnect("jdbc:odbc:CDDB");
                x.setPromptForIndex(bPromptForIndex);

                x.process();
            }
        }
    }

    public CDImporter()
    {
        listeners = new PropertyChangeSupport(this);
    }

    public static String getUsage()
    {
        return "CDImporter\n" +
                " flags:\n" +
                "  -q quit the program\n" +
                "  -p prompt for next index\n" +
                "  -v verbose mode";
    }


    public boolean getVerbose()
    {
        return this.bVerbose;
    }

    public void setVerbose(boolean bVerbose)
    {
        this.bVerbose = bVerbose;
    }

    public boolean getPromptForIndex()
    {
        return this.bPromptForIndex;
    }

    public void setPromptForIndex(boolean bPromptForIndex)
    {
        this.bPromptForIndex = bPromptForIndex;
    }

    public String getConnect()
    {
        return this.strConnect;
    }

    public void setConnect(String strConnect)
    {
        this.strConnect = strConnect;
    }

    public String getDriver()
    {
        return this.strDriver;
    }

    public void setDriver(String strDriver)
    {
        this.strDriver = strDriver;
    }

    public String getPwd()
    {
        return this.strPwd;
    }

    public void setPwd(String strPwd)
    {
        this.strPwd = strPwd;
    }

    public String getUserId()
    {
        return this.strUserId;
    }

    public void setUserId(String strUserId)
    {
        this.strUserId = strUserId;
    }

    public int process()
    {
        listeners.firePropertyChange("status", "", "starting");

        if (wr == null)
            wr = new BufferedWriter(new OutputStreamWriter(System.out));

        int iRet = 0;

        println("CDImporter.process");
        println("  UserId = " + strUserId);
        println("  Pwd = " + strPwd);
        println("  Driver = " + strDriver);
        println("  Connect = " + strConnect);
        println("  Prompt = " + bPromptForIndex);

        try
        {
            Connection con = connectToDB();

            Hashtable ht = new Hashtable();

            Vector vecMp3 = new Vector();

            fromMP3(ht);

            // sort it
            Object[] da = (ht.values().toArray());
            Arrays.sort(da, new Comparator()
            {
                public int compare(Object o, Object o1)
                {
                    if (o instanceof Disc && o1 instanceof Disc)
                        return (((Disc) o).getArtist() + ((Disc) o).getTitle()).toLowerCase().compareTo((((Disc) o1).getArtist() + ((Disc) o1).getTitle()).toLowerCase());
                    else
                        return 0;
                }
            });

            for (int i = 0; i < da.length; i++)
            {
                vecMp3.add(da[i]);
            }

            Vector vecDB = fromDB();

            println("Combining MP3 and DB data");

            // see if file has bigger max index
            int iIndex = 0;
            for (int i = 0; i < vecDB.size(); i++)
            {
                Disc disc = (Disc) vecDB.elementAt(i);
                if (disc.getDiscIndex() > iIndex)
                    iIndex = disc.getDiscIndex();
            }

            // see if file has bigger max index
            for (int i = 0; i < vecMp3.size(); i++)
            {
                Disc disc = (Disc) vecMp3.elementAt(i);
                if (disc.getDiscIndex() > iIndex)
                    iIndex = disc.getDiscIndex();
            }

            for (int i = 0; i < vecMp3.size(); i++)
            {
                boolean bFound = false;
                Disc disc = (Disc) vecMp3.elementAt(i);
                for (int j = 0; !bFound && j < vecDB.size(); j++)
                {
                    Disc d = (Disc) vecDB.elementAt(j);
                    if (d.getDiscId().equals(disc.getDiscId()))
                        bFound = true;
                }

                if (!bFound)
                {
                    // here is where I should assign a new index
                    if (disc.getDiscIndex() == 0)
                    {
                        // set disc data to index of my turntable
                        if (bPromptForIndex)
                        {
                            String strData = getKeyboardInput("(Found in MP3) Enter index for " + disc.getArtist() + ":" + disc.getTitle() + " [" + (iIndex + 1) + "]");
                            if (strData != null && strData.length() > 0)
                            {
                                try
                                {
                                    int ii = Integer.parseInt(strData);
                                    disc.setDiscIndex(ii);
                                    if (ii > iIndex)
                                        iIndex = ii;
                                } catch (Exception e)
                                {
                                    iIndex++;
                                    disc.setDiscIndex(iIndex);
                                }
                            }
                            else
                            {
                                iIndex++;
                                disc.setDiscIndex(iIndex);
                            }
                        }
                        else
                        {
                            iIndex++;
                            disc.setDiscIndex(iIndex);
                        }
                    }

                    // check here to see if the user set an index that already exists meaning to overwrite
                    boolean bFound2 = false;
                    for (int j2 = 0; bPromptForIndex && !bFound2 && j2 < vecDB.size(); j2++)
                    {
                        Disc d2 = (Disc) vecDB.elementAt(j2);
                        if (d2.getDiscIndex() == disc.getDiscIndex())
                        {
                            bFound2 = true;
                            vecDB.setElementAt(disc, j2);
                        }
                    }
                    if (!bFound2)
                    {
                        vecDB.add(disc);
                    }
                }
            }

            wipeDB(con);
            toDB(vecDB);

            disconnectFromDB(con);
        } catch (ClassNotFoundException cnfe)
        {
            println(cnfe.toString(), true);
        } catch (SQLException se)
        {
            println(se.toString(), true);
        } catch (FileNotFoundException fnfe)
        {
            println(fnfe.toString(), true);
        } catch (IOException ioe)
        {
            println(ioe.toString(), true);
        } catch (Exception e)
        {
            println(e.toString(), true);
            e.printStackTrace();
        } finally
        {
        }

        listeners.firePropertyChange("status", "", "stopping");
        return iRet;
    }

    private void println(String str)
    {
        println(str, bVerbose);
    }

    private void println(String str, boolean bVerbose)
    {
        if (bVerbose)
        {
            try
            {
                wr.write(str + "\n");
                wr.flush();
            } catch (Exception e)
            {
                System.out.println(e);
            }
        }
    }

    public Writer getWriter()
    {
        return this.wr;
    }

    public void setWriter(Writer wr)
    {
        this.wr = wr;
    }

    public void run()
    {
        process();
    }

    public void addPropertyChangeListener(PropertyChangeListener l)
    {
        listeners.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l)
    {
        listeners.removePropertyChangeListener(l);
    }

    public void propertyChange(PropertyChangeEvent evt)
    {
        System.out.println(evt.getNewValue());
    }

    public static String getKeyboardInput(String strPrompt)
    {
        String strReturn = null;

        if (strPrompt != null)
        {
            System.out.print(strPrompt + " ");
        }

        try
        {
            // max input size 256
            byte[] baInput = new byte[256];
            int iRead = System.in.read(baInput);
            //			System.out.println("read " + iRead + " bytes from the keyboard");
            // dont want cr/lf
            strReturn = new String(baInput, 0, iRead - 2);
        } catch (IOException ioe)
        {
            System.out.println("IOException caught while processing keyboard input" + ioe);
        }
        return strReturn;

    }

    private Vector fromDB() throws Exception
    {
        println("Reading from Database");

        Vector vec = new Vector();

        ResultSet rsDisc = Disc.stmtS.executeQuery();
        while (rsDisc.next())
        {
            int i = 1;

            Disc disc = new Disc();
            disc.setDiscId(rsDisc.getString(i++));
            disc.setEntryType(rsDisc.getInt(i++));
            disc.setCategory(rsDisc.getString(i++));
            disc.setArtist(rsDisc.getString(i++));
            disc.setTitle(rsDisc.getString(i++));
            disc.setNumTracks(rsDisc.getInt(i++));
            disc.setDiscIndex(rsDisc.getInt(i++));
            disc.setModifyDateTime(rsDisc.getTimestamp(i++));

            DiscExt.stmtS.setString(1, disc.getDiscId());
            ResultSet rsDiscExt = DiscExt.stmtS.executeQuery();
            while (rsDiscExt.next())
            {
                int j = 1;
                DiscExt discext = new DiscExt();
                discext.setDiscId(rsDiscExt.getString(j++));
                discext.setExtNum(rsDiscExt.getInt(j++));
                discext.setExt(rsDiscExt.getString(j++));
                discext.setModifyDateTime(rsDiscExt.getTimestamp(j++));

                disc.addExt(discext);
            }
            rsDiscExt.close();

            Track.stmtS.setString(1, disc.getDiscId());
            ResultSet rsTrack = Track.stmtS.executeQuery();
            while (rsTrack.next())
            {
                int k = 1;
                Track track = new Track();
                track.setDiscId(rsTrack.getString(k++));
                track.setTrackNum(rsTrack.getInt(k++) - 1);
                track.setTitle(rsTrack.getString(k++));
                track.setModifyDateTime(rsTrack.getTimestamp(k++));
                disc.addTrack(track);

                TrackExt.stmtS.setString(1, track.getDiscId());
                TrackExt.stmtS.setInt(2, track.getTrackNum());
                ResultSet rsTrackExt = TrackExt.stmtS.executeQuery();
                while (rsTrackExt.next())
                {
                    int l = 1;
                    TrackExt trackext = new TrackExt();
                    trackext.setDiscId(rsTrackExt.getString(l++));
                    trackext.setTrackNum(rsTrackExt.getInt(l++));
                    trackext.setExtNum(rsTrackExt.getInt(l++));
                    trackext.setExt(rsTrackExt.getString(l++));
                    trackext.setModifyDateTime(rsTrackExt.getTimestamp(l++));

                    track.addExt(trackext);
                }
                rsTrackExt.close();
            }
            rsTrack.close();

            vec.add(disc);
        }
        rsDisc.close();

        return vec;
    }

    private void fromMP3(Hashtable ht) throws Exception
    {
        println("Reading from mp3 files");

        build(ht, new File("."));
    }

    private void build(Hashtable ht, File file) throws Exception
    {
        if (file == null)
            throw new IllegalArgumentException("file object is null");

        File[] files = file.listFiles(new FileFilter()
        {
            public boolean accept(File pathname)
            {
                if (pathname.isDirectory() || pathname.getName().toLowerCase().endsWith("mp3"))
                    return true;
                else
                    return false;
            }
        });

        Arrays.sort(files, new Comparator()
        {
            public int compare(Object o, Object o1)
            {
                if (o instanceof File && o1 instanceof File)
                    return ((File) o).getName().compareTo(((File) o1).getName());
                else
                    return 0;
            }
        });

        boolean bAlbumMatch = true;
        boolean bArtistMatch = true;
        Disc disc = null;

        for (int i = 0; i < files.length; i++)
        {
            if (files[i].isDirectory())
            {
                println(files[i].getCanonicalPath());
                build(ht, files[i]);
            }
            else
            {
                try
                {
                    String dirAlbum = files[i].getCanonicalFile().getParentFile().getName();
                    String dirArtist = files[i].getCanonicalFile().getParentFile().getParentFile().getName();
                    MP3File mp3File = new MP3File(files[i].getCanonicalPath());
                    String album = mp3File.getAlbum().getTextContent();
                    String artist = mp3File.getArtist().getTextContent();
                    String band = mp3File.getBand().getTextContent();
                    String myid = mp3File.getCDIdentifier().getTextContent();
                    String genre = mp3File.getGenre().getTextContent();
                    String title = mp3File.getTitle().getTextContent();
                    String track = mp3File.getTrack().getTextContent();
                    String year = mp3File.getYear().getTextContent();
                    String pub = mp3File.getPublisher().getTextContent();
                    int trackindex = Integer.parseInt(track);

                    // use band as artist ssems to be the best choice
                    if(band != null)
                        artist = band;
                    
                    //String comment = mp3File.getComments().getTextContent();
                    if (myid == null || myid.length() == 0)
                    {
                        myid = artist + album;
                    }

                    // determine what dir name should look like
                    String temp = buildDirName(album);
                    // check file system structure
                    // make sure it at least start with the nmae the tag is limited to 30 characters
                    if (bAlbumMatch && !dirAlbum.equals(temp))
                    {
                        bAlbumMatch = false;
                        println("############# Album mismatch [\"" + dirAlbum + "\" should be \"" + temp + "\"]", true);
                        println("cd \"" + files[i].getCanonicalFile().getParentFile().getParentFile() + "\"", true);
                        println("ren \"" + dirAlbum + "\" \"" + temp + "\"", true);
                    }

                    temp = buildDirName(artist);
                    if (bArtistMatch && !dirArtist.equals(temp))
                    {
                        bArtistMatch = false;
                        println("%%%%%%%%%%%%% Artist mismatch [\"" + dirArtist + "\" should be \"" + temp + "\"]", true);
                        println("cd \"" + files[i].getCanonicalFile().getParentFile().getParentFile().getParentFile() + "\"", true);
                        println("ren \"" + dirArtist + "\" \"" + temp + "\"", true);
                    }

                    myid = String.valueOf(myid.hashCode());

                    //Disc disc = (Disc) ht.get(myid);
                    if (disc == null)
                    {
                        println("#" + ++iDirIndex + " = " + artist + ":" + dirAlbum, true);
                        disc = new Disc();

                        // if there is a mismatch always go with the file system name
                        disc.setTitle(album);
                        disc.setArtist(artist);

                        disc.setYear(year);
// no londer needed genereated in disc class
                        disc.setDiscId(myid);
                        disc.setNumTracks(0);
                        disc.setEntryType(0);
                        disc.setDiscIndex(0);

                        // convert genre to string is needed
                        if(genre != null && genre.startsWith("("))
                        {
                            String genreCode = genre.substring(1, genre.length() - 1);
                            byte b = Byte.parseByte(genreCode);
                            genre = Genre.getDesc(b);
                        }

                        disc.setCategory(genre);

                        ht.put(myid, disc);
                    }
                    disc.setNumTracks(disc.getNumTracks() + 1);

                    // if my artist name is different for this track then reset artist name to various
                    if (!disc.getArtist().equals(artist))
                        disc.setArtist("Various Artists");

                    Track trak = new Track();
                    trak.setTitle(title);
                    trak.setDiscId(disc.getDiscId());

                    if (trackindex == 0)
                        trackindex = disc.getNumTracks();

                    trak.setTrackNum(trackindex);

                    disc.addTrack(trak);

                    println("Adding " + files[i].getCanonicalPath());
                } catch (Exception e)
                {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    //continue;
                }
            }
        }
    }

    private void toDB(Vector vec) throws Exception
    {
        println("Writing to DB");

        for (int i = 0; i < vec.size(); i++)
        {
            Disc disc = (Disc) vec.elementAt(i);
            disc.toDB();
        }
    }

    private Connection connectToDB() throws Exception
    {
        // make connection to db
        // Attempt to connect to a driver.  Each one
        // of the registered drivers will be loaded until
        // one is found that can process this URL
        Class.forName(strDriver);
        Connection con = DriverManager.getConnection(strConnect,
                strUserId,
                strPwd);

        Disc.stmtI = con.prepareStatement("insert into disc (disc_id, entry_type, category, artist, disc_title, num_tracks, disc_index, modify_dt) values (?,?,?,?,?,?,?,?)");
        DiscExt.stmtI = con.prepareStatement("insert into disc_ext (disc_id, ext_num, ext, modify_dt) values (?,?,?,?)");
        Track.stmtI = con.prepareStatement("insert into track (disc_id, track_num, track_title, modify_dt) values (?,?,?,?)");
        TrackExt.stmtI = con.prepareStatement("insert into track_ext (disc_id, track_num, ext_num, ext, modify_dt) values (?,?,?,?,?)");

        Disc.stmtS = con.prepareStatement("select * from disc order by disc_index");
        DiscExt.stmtS = con.prepareStatement("select * from disc_ext where disc_id = ? order by ext_num");
        Track.stmtS = con.prepareStatement("select * from track where disc_id = ? order by track_num");
        TrackExt.stmtS = con.prepareStatement("select * from track_ext where disc_id = ? and track_num = ? order by ext_num");

        Disc.stmtD = con.prepareStatement("delete from disc where disc_id = ?");
        DiscExt.stmtD = con.prepareStatement("delete from disc_ext where disc_id = ? and ext_num = ?");
        Track.stmtD = con.prepareStatement("delete from track where disc_id = ? and track_num = ?");
        TrackExt.stmtD = con.prepareStatement("delete from track_ext where disc_id = ? and track_num = ? and ext_num = ?");

        pstmtMaxIndex = con.prepareStatement("select max(disc_index) from disc");

        return con;
    }

    private void disconnectFromDB(Connection con) throws Exception
    {
        Disc.stmtI.close();
        DiscExt.stmtI.close();
        Track.stmtI.close();
        TrackExt.stmtI.close();

        Disc.stmtS.close();
        DiscExt.stmtS.close();
        Track.stmtS.close();
        TrackExt.stmtS.close();

        Disc.stmtD.close();
        DiscExt.stmtD.close();
        Track.stmtD.close();
        TrackExt.stmtD.close();

        pstmtMaxIndex.close();

        con.close();
    }

    private void wipeDB(Connection con) throws Exception
    {
        println("Deleting DB info");
        Statement stmt = con.createStatement();

        stmt.executeUpdate("delete from track_ext");
        stmt.executeUpdate("delete from disc_ext");
        stmt.executeUpdate("delete from track");
        stmt.executeUpdate("delete from disc");

        stmt.close();
    }

    private String buildDirName(String str)
    {
        str = GeneralUtils.stringReplace(str, "\\", " ");
        str = GeneralUtils.stringReplace(str, "/", " ");
        str = GeneralUtils.stringReplace(str, ":", " ");
        str = GeneralUtils.stringReplace(str, "?", " ");
        str = GeneralUtils.stringReplace(str, "*", " ");
        str = GeneralUtils.stringReplace(str, "?", " ");
        str = GeneralUtils.stringReplace(str, "\"", " ");
        str = GeneralUtils.stringReplace(str, "<", " ");
        str = GeneralUtils.stringReplace(str, ">", " ");
        str = GeneralUtils.stringReplace(str, "|", " ");
//        str = GeneralUtils.stringReplace(str, ".", " ");
        return str.trim();
    }
}
