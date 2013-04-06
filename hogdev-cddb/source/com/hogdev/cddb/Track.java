package com.hogdev.cddb;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

public class Track extends CDInfo
{
    String strDiscId;
    int iTrackNum;

    static PreparedStatement stmtI;
    static PreparedStatement stmtS;
    static PreparedStatement stmtD;

    public Track()
    {
    }

    public void addExt(TrackExt trackext)
    {
        trackext.setDiscId(strDiscId);
        trackext.setTrackNum(iTrackNum);
        vecExt.addElement(trackext);
    }

    public int getTrackNum()
    {
        return this.iTrackNum;
    }

    public void setTrackNum(int iTrackNum)
    {
        this.iTrackNum = iTrackNum;
    }

    public String getDiscId()
    {
        return this.strDiscId;
    }

    public void setDiscId(String strDiscId)
    {
        this.strDiscId = strDiscId;
    }

    public String toString()
    {
        String strRet = "=====TRACK=====\n";

        strRet += "DISC ID = " + strDiscId + "\n";
        strRet += "TRACK NUM = " + String.valueOf(iTrackNum) + "\n";
        strRet += super.toString();

        return strRet;
    }

    public void toDB() throws SQLException
    {
        int iIndex = 1;

        if (strDiscId != null && strDiscId.length() > 0)
            stmtI.setString(iIndex++, strDiscId);
        else
            stmtI.setNull(iIndex++, Types.VARCHAR);

        stmtI.setInt(iIndex++, iTrackNum);

        if (strTitle != null && strTitle.length() > 0)
            stmtI.setString(iIndex++, strTitle);
        else
            stmtI.setNull(iIndex++, Types.VARCHAR);

        stmtI.setTimestamp(iIndex++, new Timestamp(System.currentTimeMillis()));

        stmtI.executeUpdate();

        super.toDB();
    }
}

