package com.hogdev.cddb;

import java.sql.SQLException;
import java.sql.Timestamp;

public abstract class Ext
{
    String strDiscId;
    int iExtNum;
    String strExt;
    Timestamp tsModifyDateTime;

    public int getExtNum()
    {
        return this.iExtNum;
    }

    public void setExtNum(int iExtNum)
    {
        this.iExtNum = iExtNum;
    }

    public String getDiscId()
    {
        return this.strDiscId;
    }

    public void setDiscId(String strDiscId)
    {
        this.strDiscId = strDiscId;
    }

    public String getExt()
    {
        return this.strExt;
    }

    public void setExt(String strExt)
    {
        this.strExt = strExt;
    }

    public Timestamp getModifyDateTime()
    {
        return this.tsModifyDateTime;
    }

    public void setModifyDateTime(Timestamp tsModifyDateTime)
    {
        this.tsModifyDateTime = tsModifyDateTime;
    }

    public abstract String toString();

    public abstract void toDB() throws SQLException;
}

