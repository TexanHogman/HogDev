package com.hogdev.cddb;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

public class Disc extends CDInfo
{
    static PreparedStatement stmtI;
    static PreparedStatement stmtS;
    static PreparedStatement stmtD;

    String strDiscId;
    int iEntryType = 0;
    String strCategory;
    String strArtist;
    String strYear;
    int iNumTracks = 0;
    Hashtable htTracks;
    int iDiscIndex = 0;

    public Disc()
    {
        htTracks = new Hashtable();
    }

    public void addExt(DiscExt discext)
    {
        discext.setDiscId(getDiscId());
        vecExt.addElement(discext);
    }

    public void setTitle(String strTitle)
    {
        super.setTitle(strTitle);
    }

    public void addTrack(Track track)
    {
        track.setDiscId(getDiscId());
        htTracks.put(new Integer(track.getTrackNum()), track);
    }

    public Track getTrack(int iTrackNum)
    {
        return (Track) htTracks.get(new Integer(iTrackNum));
    }

    public int getDiscIndex()
    {
        return this.iDiscIndex;
    }

    public void setDiscIndex(int iDiscIndex)
    {
        this.iDiscIndex = iDiscIndex;
    }

    public int getEntryType()
    {
        return this.iEntryType;
    }

    public void setEntryType(int iEntryType)
    {
        this.iEntryType = iEntryType;
    }

    public int getNumTracks()
    {
        return this.iNumTracks;
    }

    public void setNumTracks(int iNumTracks)
    {
        this.iNumTracks = iNumTracks;
    }

    public String getArtist()
    {
        return this.strArtist;
    }

    public void setArtist(String strArtist)
    {
        this.strArtist = strArtist;
    }

    public String getYear()
    {
        return this.strYear;
    }

    public void setYear(String strYear)
    {
        this.strYear = strYear;
    }

    public String getCategory()
    {
        return this.strCategory;
    }

    public void setCategory(String strCategory)
    {
        this.strCategory = strCategory;
    }

    public String getDiscId()
    {
        // new algorithim for disc id
        return strDiscId;
    }

    public void setDiscId(String strDiscId)
    {
        this.strDiscId = strDiscId;
    }

    public String toString()
    {
        String strRet = "=====DISC=====\n";

        strRet += "DISC ID = " + getDiscId() + "\n";
        strRet += "ENTRY TYPE = " + String.valueOf(iEntryType) + "\n";
        strRet += "CATEGORY = " + strCategory + "\n";
        strRet += "ARTIST = " + strArtist + "\n";
        strRet += "YEAR = " + strYear + "\n";
        strRet += "NUM TRACKS = " + String.valueOf(iNumTracks) + "\n";

        // enumeration tracks
        Enumeration enumeration = htTracks.elements();
        while (enumeration.hasMoreElements())
        {
            Track track = (Track) enumeration.nextElement();
            strRet += track.toString();
        }

        strRet += super.toString();

        return strRet;
    }

    public void toDB() throws SQLException
    {
        int iIndex = 1;

        if (getDiscId() != null && getDiscId().length() > 0)
            stmtI.setString(iIndex++, getDiscId());
        else
            stmtI.setNull(iIndex++, Types.VARCHAR);

        stmtI.setInt(iIndex++, iEntryType);

        if (strCategory != null && strCategory.length() > 0)
            stmtI.setString(iIndex++, strCategory);
        else
            stmtI.setNull(iIndex++, Types.VARCHAR);

        if (strArtist != null && strArtist.length() > 0)
            stmtI.setString(iIndex++, strArtist);
        else
            stmtI.setNull(iIndex++, Types.VARCHAR);

        if (strTitle != null && strTitle.length() > 0)
            stmtI.setString(iIndex++, strTitle);
        else
            stmtI.setNull(iIndex++, Types.VARCHAR);

        stmtI.setInt(iIndex++, iNumTracks);
        stmtI.setInt(iIndex++, iDiscIndex);

        stmtI.setTimestamp(iIndex++, new Timestamp(System.currentTimeMillis()));

        stmtI.executeUpdate();

        // enumeration tracks
        Enumeration enumeration = htTracks.elements();
        while (enumeration.hasMoreElements())
        {
            Track track = (Track) enumeration.nextElement();
            track.toDB();
        }

        super.toDB();
    }

    private void resetTrackIDS()
    {
        Iterator iter = htTracks.keySet().iterator();
        while (iter.hasNext())
        {
            Track track = (Track) htTracks.get(iter.next());
            track.setDiscId(getDiscId());
        }
    }
}
