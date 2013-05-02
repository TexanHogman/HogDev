package com.hogdev.cddb;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Vector;

public class CDInfo
{
    String strTitle;
    Timestamp tsModifyDateTime;
    Vector vecExt;

    public CDInfo()
    {
        vecExt = new Vector();
    }

    public String getTitle()
    {
        return this.strTitle;
    }

    public void setTitle(String strTitle)
    {
        this.strTitle = strTitle;
    }

    public Timestamp getModifyDateTime()
    {
        return this.tsModifyDateTime;
    }

    public void setModifyDateTime(Timestamp tsModifyDateTime)
    {
        this.tsModifyDateTime = tsModifyDateTime;
    }

    public Vector getExt()
    {
        return this.vecExt;
    }

    public void setExt(Vector vecExt)
    {
        this.vecExt = vecExt;
    }

    public String toString()
    {
        String strRet = "=====CDINFO=====\n";

        strRet += "TITLE = " + strTitle + "\n";
        strRet += "TS = " + tsModifyDateTime + "\n";

        // ext
        for (int i = 0; i < vecExt.size(); i++)
        {
            Ext ext = (Ext) vecExt.elementAt(i);
            strRet += ext.toString();
        }

        return strRet;
    }

    public void toDB() throws SQLException
    {
        // ext
        for (int i = 0; i < vecExt.size(); i++)
        {
            Ext ext = (Ext) vecExt.elementAt(i);
            ext.toDB();
        }
    }
}
