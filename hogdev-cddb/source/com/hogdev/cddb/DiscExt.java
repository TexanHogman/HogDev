package com.hogdev.cddb;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

public class DiscExt extends Ext
{
    static PreparedStatement stmtI;
    static PreparedStatement stmtS;
    static PreparedStatement stmtD;

    public String toString()
    {
        String strRet = "=====DISC EXT=====\n";

        strRet += "DISC ID = " + strDiscId + "\n";
        strRet += "EXT NUM = " + String.valueOf(iExtNum) + "\n";
        strRet += "EXT = " + strExt + "\n";
        strRet += "TS = " + tsModifyDateTime + "\n";

        return strRet;
    }

    public void toDB() throws SQLException
    {
        int iIndex = 1;

        if (strDiscId != null && strDiscId.length() > 0)
            stmtI.setString(iIndex++, strDiscId);
        else
            stmtI.setNull(iIndex++, Types.VARCHAR);

        stmtI.setInt(iIndex++, iExtNum);

        if (strExt != null && strExt.length() > 0)
            stmtI.setString(iIndex++, strExt);
        else
            stmtI.setNull(iIndex++, Types.VARCHAR);

        stmtI.setTimestamp(iIndex++, new Timestamp(System.currentTimeMillis()));

        stmtI.executeUpdate();
    }

}

