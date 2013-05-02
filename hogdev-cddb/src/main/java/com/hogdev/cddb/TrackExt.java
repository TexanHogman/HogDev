package com.hogdev.cddb;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

public class TrackExt extends DiscExt
{
    static PreparedStatement stmtI;
    static PreparedStatement stmtS;
    static PreparedStatement stmtD;

    int iTrackNum;

    public int getTrackNum()
    {
        return this.iTrackNum;
    }

    public void setTrackNum(int iTrackNum)
    {
        this.iTrackNum = iTrackNum;
    }

    public String toString()
    {
        String strRet = "=====TRACK EXT=====\n";

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
        stmtI.setInt(iIndex++, iExtNum);

        if (strExt != null && strExt.length() > 0)
            stmtI.setString(iIndex++, strExt);
        else
            stmtI.setNull(iIndex++, Types.VARCHAR);

        stmtI.setTimestamp(iIndex++, new Timestamp(System.currentTimeMillis()));

        stmtI.executeUpdate();
    }
}

