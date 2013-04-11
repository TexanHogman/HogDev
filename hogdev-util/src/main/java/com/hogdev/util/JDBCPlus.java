package com.hogdev.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

public class JDBCPlus implements Runnable, PropertyChangeListener
{
    public final static int DEFAULT_COL_SIZE = 40;

    String strDriver;

    String strConnect;

    String strUserId;

    String strPwd;

    String strQuery;

    boolean bVerbose;

    boolean bTimings;

    boolean bOutputData;

    int iRepeat = 1;

    int iColSize = DEFAULT_COL_SIZE;

    Hashtable htTimings;

    Writer wr;

    // for notifying listeners
    private PropertyChangeSupport listeners;

    public static void main(String[] args)
    {
        // default to execute one time
        int iRepeat = 1;
        int iColSize = DEFAULT_COL_SIZE;

        int i = 0, j;
        String arg;
        char flag;
        String strUserID = null;
        String strPassword = null;
        String strJDBCDriver = null;
        String strJDBCConnect = null;
        String strFileName = null;
        boolean bReplaceFlag = false;
        boolean bRecurse = false;
        boolean bVerbose = false;
        boolean bTimings = false;

        while (i < args.length && args[i].startsWith("-"))
        {
            arg = args[i++];

            // use this type of check for arguments that require arguments
            if (arg.equalsIgnoreCase("-r"))
            {
                if (i < args.length)
                {
                    iRepeat = Integer.parseInt(args[i++]);
                }
                else System.err
                        .println("-r requires a repeat # of times value");
            }

            // use this type of check for arguments that require arguments
            else if (arg.equalsIgnoreCase("-u"))
            {
                if (i < args.length)
                {
                    strUserID = args[i++];
                }
                else System.err.println("-u requires a user id");
            }

            // use this type of check for arguments that require arguments
            else if (arg.equalsIgnoreCase("-p"))
            {
                if (i < args.length)
                {
                    strPassword = args[i++];
                }
                else System.err.println("-p requires a password");
            }

            // use this type of check for arguments that require arguments
            else if (arg.equalsIgnoreCase("-d"))
            {
                if (i < args.length)
                {
                    strJDBCDriver = args[i++];
                }
                else System.err.println("-d requires a jdbc database driver");
            }

            // use this type of check for arguments that require arguments
            else if (arg.equalsIgnoreCase("-c"))
            {
                if (i < args.length)
                {
                    strJDBCConnect = args[i++];
                }
                else System.err
                        .println("-c requires a jdbc driver connection string");
            }

            // use this type of check for arguments that require arguments
            else if (arg.equalsIgnoreCase("-f"))
            {
                if (i < args.length)
                {
                    strFileName = args[i++];
                }
                else System.err.println("-f requires a file name");
            }

            // use this type of check for arguments that require arguments
            else if (arg.equalsIgnoreCase("-s"))
            {
                if (i < args.length)
                {
                    iColSize = Integer.parseInt(args[i++]);
                }
                else System.err.println("-s requires a column size");
            }

            // use this type of check for a series of flag arguments
            else
            {
                for (j = 1; j < arg.length(); j++)
                {
                    flag = arg.charAt(j);
                    switch (flag)
                    {
                    case 'v':
                    case 'V':
                        bVerbose = true;
                        break;

                    case 't':
                    case 'T':
                        bTimings = true;
                        break;

                    case 'H':
                    case 'h':
                    case '?':
                        System.err.println("");
                        System.err.println("	-verbose: verbose mode");
                        System.err.println("	-r:       repeat <# time>");
                        System.err.println("	-t:       calculate timings");
                        System.err.println("	-f:       file name for output");
                        System.err.println("	-s:       column size");
                        System.err.println("	-u:       database user id");
                        System.err.println("	-p:       database password");
                        System.err.println("	-d:       jdbc database driver");
                        System.err
                                .println("	-c:       jdbc database driver connection string");
                        System.err.println("	-?:       this screen");
                        System.err.println("	SQL statement");
                        System.err.println("");
                        break;

                    default:
                        System.err.println("JDBCPlus: illegal option " + flag);
                        break;
                    }
                }
            }
        }

        if (i == args.length)
        {
            System.err.println(JDBCPlus.getUsage());
        }
        else
        {
            String strQuery = args[i];
            if (strQuery == null)
            {
                System.err.println("JDBCPlus: query must be specified");
            }
            else
            {
                JDBCPlus x = new JDBCPlus();
                x.setVerbose(bVerbose);
                x.setUserId(strUserID);
                x.setPwd(strPassword);
                x.setDriver(strJDBCDriver);
                x.setConnect(strJDBCConnect);
                x.setRepeat(iRepeat);
                x.setTimings(bTimings);
                x.setQuery(strQuery);
                x.setColSize(iColSize);

                if (strFileName != null)
                {
                    File file = new File(strFileName);
                    try
                    {
                        x.setWriter(new BufferedWriter(new OutputStreamWriter(
                                new FileOutputStream(file))));
                        x.process();
                    }
                    catch (IOException ioe)
                    {
                        System.out.println(ioe);
                    }
                }
                else
                {
                    x.process();
                }

            }
        }
    }

    public JDBCPlus()
    {
        htTimings = new Hashtable();
        listeners = new PropertyChangeSupport(this);
    }

    public static String getUsage()
    {
        return "JDBCPlus\n" + " desc:  database query tool\n"
                + " syntax: java com.hogge.util.JDBCPlus\n" + " flags: TBD ";
    }

    public boolean getVerbose()
    {
        return this.bVerbose;
    }

    public void setVerbose(boolean bVerbose)
    {
        this.bVerbose = bVerbose;
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

    public String getQuery()
    {
        return this.strQuery;
    }

    public void setQuery(String strQuery)
    {
        this.strQuery = strQuery;
    }

    public String getUserId()
    {
        return this.strUserId;
    }

    public void setUserId(String strUserId)
    {
        this.strUserId = strUserId;
    }

    public int getRepeat()
    {
        return this.iRepeat;
    }

    public void setRepeat(int iRepeat)
    {
        this.iRepeat = iRepeat;
    }

    private void startTimer(String str)
    {
        if (bTimings && str != null)
        {
            htTimings.put(str, new Long(System.currentTimeMillis()));
        }
    }

    private void stopTimer(String str)
    {
        if (bTimings && str != null)
        {
            Long lStart = (Long) htTimings.get(str);
            if (lStart != null)
            {
                long lStop = System.currentTimeMillis();
                println("Elapsed time for " + str + " = "
                        + String.valueOf((lStop - lStart.longValue()))
                        + " milliseconds");
            }
            else
            {
                println("Stop without start found on timer", true);
            }
        }
    }

    public int process()
    {
        listeners.firePropertyChange("status", "", "starting");

        if (wr == null) wr = new BufferedWriter(new OutputStreamWriter(
                System.out));

        int iRet = 0;

        println("JDBCPlus.process");
        println("  UserId = " + strUserId);
        println("  Pwd = " + strPwd);
        println("  Driver = " + strDriver);
        println("  Connect = " + strConnect);
        println("  Query = " + strQuery);
        println("  Repeat Query = " + iRepeat);

        Connection con = null;
        try
        {
            // make connection to db
            // Attempt to connect to a driver. Each one
            // of the registered drivers will be loaded until
            // one is found that can process this URL
            Class.forName(strDriver);
            startTimer("DB Connection");
            con = DriverManager.getConnection(strConnect, strUserId, strPwd);
            stopTimer("DB Connection");

            for (int i = 0; i < iRepeat; i++)
            {
                startTimer("Loop# " + i);

                startTimer("DB Statement");
                Statement stmt = con.createStatement();
                stopTimer("DB Statement");

                startTimer("DB Execute");
                ResultSet rs = stmt.executeQuery(strQuery);
                stopTimer("DB Execute");

                startTimer("DB Read RS");
                ResultSetMetaData rsmd = rs.getMetaData();

                StringBuffer line = new StringBuffer();
                // NOT ZERO BASED
                for (int j = 1; j <= rsmd.getColumnCount(); j++)
                {
                    if (rsmd.getColumnName(j).length() < iColSize)
                    {
                        line.append(rsmd.getColumnName(j));
                        for (int k = rsmd.getColumnLabel(j).length(); k < iColSize; k++)
                        {
                            line.append(" ");
                        }
                    }
                    else
                    {
                        line.append(rsmd.getColumnLabel(j).substring(0,
                                iColSize));
                    }
                    line.append(" ");
                }

                // delete last space
                line.deleteCharAt(line.length() - 1);

                println(line.toString());

                // underline
                for (int j = 0; j < line.length(); j++)
                {
                    // if(line.charAt(j) != ' ')
                    line.replace(j, j + 1, "-");
                }

                println(line.toString());

                int iRows = 0;
                while (rs.next())
                {
                    iRows++;
                    line = new StringBuffer();
                    for (int j = 1; j <= rsmd.getColumnCount(); j++)
                    {
                        Object obj = rs.getObject(j);
                        String strData = "";
                        if (obj != null)
                        {
                            strData = obj.toString();
                        }

                        if (strData.length() < iColSize)
                        {
                            line.append(strData);
                            for (int k = strData.length(); k < iColSize; k++)
                            {
                                line.append(" ");
                            }
                        }
                        else
                        {
                            line.append(strData.substring(0, iColSize));
                        }
                        line.append(" ");
                    }
                    // delete last space
                    line.deleteCharAt(line.length() - 1);
                    println(line.toString());
                }

                println("");
                println(iRows + " row(s) returned");

                stopTimer("DB Read RS");

                startTimer("RS close");
                rs.close();
                stopTimer("RS close");

                startTimer("STMT close");
                stmt.close();
                stopTimer("STMT close");

                stopTimer("Loop# " + i);
            }
        }
        catch (ClassNotFoundException cnfe)
        {
            println(cnfe.toString(), true);
        }
        catch (SQLException se)
        {
            println(se.toString(), true);
        }
        finally
        {
            // close db connection
            try
            {
                if (con != null) con.close();
            }
            catch (Exception e)
            {
                println(e.toString(), true);
            }
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
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
        }
    }

    public boolean getTimings()
    {
        return this.bTimings;
    }

    public void setTimings(boolean bTimings)
    {
        this.bTimings = bTimings;
        if (this.bTimings)
        {
            htTimings = new Hashtable();
        }
    }

    public int getColSize()
    {
        return this.iColSize;
    }

    public void setColSize(int iColSize)
    {
        this.iColSize = iColSize;
    }

    public Writer getWriter()
    {
        return this.wr;
    }

    public void setWriter(Writer wr)
    {
        this.wr = wr;
    }

    @Override
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

    @Override
	public void propertyChange(PropertyChangeEvent evt)
    {
        System.out.println(evt.getNewValue());
    }

}
