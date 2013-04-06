package com.hogdev.toolbox.plugins;

import javax.swing.JPanel;

public class JDBCPlusPIJawa extends JDBCPlusPI
{
    /**
     * 
     */
    private static final long serialVersionUID = -1380095004102733880L;

    public static String getMenuText()
    {
        return "JDBC Plus -- JAWA";
    }

    protected void initFields()
    {
        tfUserID.setText("jawa1");
        tfPwd.setText("bbdev1");
        tfDriver.setText("oracle.jdbc.driver.OracleDriver");
        tfConnect.setText("jdbc:oracle:thin:@jawa.globeset.com:1521:WG80");
        tfQuery.setText("");
        ta.setText("");
        btnStart.setEnabled(false);
        btnStop.setEnabled(false);
    }

    public JPanel getGUI()
    {
        JPanel pnl = super.getGUI();
        setTitle("JDBC Plus -- JAWA");
        return pnl;
    }

}
