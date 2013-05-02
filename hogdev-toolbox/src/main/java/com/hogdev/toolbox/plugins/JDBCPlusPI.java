package com.hogdev.toolbox.plugins;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.io.Writer;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.hogdev.toolbox.ToolBoxPlugin;
import com.hogdev.util.JDBCPlus;

public class JDBCPlusPI extends ToolBoxPlugin
{
    /**
     * 
     */
    private static final long serialVersionUID = -4466725972796967269L;

    JTextArea ta;

    JScrollPane sp;

    JButton btnStart;

    JButton btnStop;

    JButton btnBrowse;

    JButton btnClear;

    JTextField tfUserID;

    JTextField tfPwd;

    JTextField tfDriver;

    JTextField tfConnect;

    JTextField tfQuery;

    JCheckBox ckRecurse;

    JCheckBox ckMatchCase;

    public static void main(String[] args)
    {
    }

    public JDBCPlusPI()
    {
        super();
    }

    public JPanel getGUI()
    {
        setTitle("JDBC Plus");
        setClosable(true);
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);

        JPanel pnl = new JPanel();
        pnl.setLayout(new BorderLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        GridBagLayout gbl = new GridBagLayout();

        JLabel lblUserID = new JLabel("User ID:");
        tfUserID = new JTextField(10);
        tfUserID.addKeyListener(this);
        JLabel lblPwd = new JLabel("Password:");
        tfPwd = new JTextField(10);
        tfPwd.addKeyListener(this);
        JLabel lblDriver = new JLabel("Driver:");
        tfDriver = new JTextField(10);
        tfDriver.addKeyListener(this);
        JLabel lblConnect = new JLabel("Connect:");
        tfConnect = new JTextField(10);
        tfConnect.addKeyListener(this);
        JLabel lblQuery = new JLabel("Query:");
        tfQuery = new JTextField(20);
        tfQuery.addKeyListener(this);

        JPanel pnlFields = new JPanel(gbl);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(3, 3, 3, 3);

        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbl.setConstraints(lblUserID, gbc);
        pnlFields.add(lblUserID);

        gbc.weightx = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbl.setConstraints(tfUserID, gbc);
        pnlFields.add(tfUserID);

        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbl.setConstraints(lblPwd, gbc);
        pnlFields.add(lblPwd);

        gbc.weightx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbl.setConstraints(tfPwd, gbc);
        pnlFields.add(tfPwd);

        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbl.setConstraints(lblDriver, gbc);
        pnlFields.add(lblDriver);

        gbc.weightx = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbl.setConstraints(tfDriver, gbc);
        pnlFields.add(tfDriver);

        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbl.setConstraints(lblConnect, gbc);
        pnlFields.add(lblConnect);

        gbc.weightx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbl.setConstraints(tfConnect, gbc);
        pnlFields.add(tfConnect);

        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbl.setConstraints(lblQuery, gbc);
        pnlFields.add(lblQuery);

        gbc.weightx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbl.setConstraints(tfQuery, gbc);
        pnlFields.add(tfQuery);

        ta = new JTextArea();
        ta.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ta.setEditable(false);

        sp = new JScrollPane(ta);

        btnStart = new JButton("Start");
        btnStart.addActionListener(this);

        btnStop = new JButton("Stop");
        btnStop.addActionListener(this);

        btnClear = new JButton("Clear");
        btnClear.addActionListener(this);

        JPanel pnlButtons = new JPanel();
        pnlButtons.add(btnStart);
        pnlButtons.add(btnStop);
        pnlButtons.add(btnClear);

        pnl.add("North", pnlFields);
        pnl.add("Center", sp);
        pnl.add("South", pnlButtons);

        initFields();

        return pnl;
    }

    public static Boolean isSingleInstance()
    {
        return new Boolean(false);
    }

    public static String getMenuText()
    {
        return "JDBC Plus";
    }

    public Dimension getSize()
    {
        return new Dimension(300, 300);
    }

    public static Icon getToolBarIcon()
    {
        return new ImageIcon("jdbc.gif");
    }

    public void propertyChange(PropertyChangeEvent evt)
    {
        if (evt.getPropertyName().equals("status"))
        {
            if (evt.getNewValue().equals("starting"))
            {
                btnStart.setEnabled(false);
                btnClear.setEnabled(false);
                btnStop.setEnabled(true);
            }
            else if (evt.getNewValue().equals("stopping"))
            {
                btnStart.setEnabled(true);
                btnClear.setEnabled(true);
                btnStop.setEnabled(false);
                tfQuery.addKeyListener(this);
            }
        }
        super.propertyChange(evt);
    }

    public void actionPerformed(ActionEvent event)
    {
        if (event.getSource() == btnStart)
        {
            ta.setText("");
            JDBCPlus x = new JDBCPlus();
            x.addPropertyChangeListener(this);
            x.setVerbose(true);
            x.setUserId(tfUserID.getText());
            x.setPwd(tfPwd.getText());
            x.setDriver(tfDriver.getText());
            x.setConnect(tfConnect.getText());
            x.setRepeat(1);
            x.setTimings(false);
            x.setQuery(tfQuery.getText());
            x.setWriter(new JDBCWriter());
            Thread th = new Thread(x);
            th.start();
        }
        else if (event.getSource() == btnStop)
        {
            //			if ( searchReplace != null )
            {
                ta.append("Stopping search, Please Wait..." + "\n");
                //				searchReplace.setStop( true );
            }
        }
        else if (event.getSource() == btnClear)
        {
            initFields();
        }
        super.actionPerformed(event);
    }

    protected void initFields()
    {
        tfUserID.setText("");
        tfPwd.setText("");
        tfDriver.setText("");
        tfConnect.setText("");
        tfQuery.setText("");
        ta.setText("");
        btnStart.setEnabled(false);
        btnStop.setEnabled(false);
    }

    public void keyReleased(KeyEvent event)
    {
        if (event.getSource() == tfQuery)
        {
            if (tfQuery.getText() != null && tfQuery.getText().length() > 0)
            {
                btnStart.setEnabled(true);
            }
            else
            {
                btnStart.setEnabled(false);
            }
        }
        super.keyReleased(event);
    }

    class JDBCWriter extends Writer
    {
        public void close()
        {
        }

        public void flush()
        {
        }

        public void write(char[] data, int off, int len)
        {
            ta.append(new String(data, off, len));
        }
    }
}
