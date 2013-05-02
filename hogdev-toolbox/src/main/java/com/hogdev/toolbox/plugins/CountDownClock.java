package com.hogdev.toolbox.plugins;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

import com.hogdev.toolbox.ToolBoxPlugin;
import com.hogdev.widgets.TimeDisplay;

public class CountDownClock extends ToolBoxPlugin
{
    /**
     * 
     */
    private static final long serialVersionUID = 8056106066198370087L;

    public final static long lTotalMillis = 10 * 60 * 60 * 1000;

    Timer timer;

    JButton btnStartStop;

    JTextField txtTime;

    TimeDisplay tdTotal;

    long lStart;

    public static void main(String[] args)
    {
    }

    public CountDownClock()
    {
        super();
    }

    public JPanel getGUI()
    {
        setTitle("Count Down Clock");
        setClosable(true);
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);

        JPanel pnl = new JPanel();
        pnl.setLayout(new BorderLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        GridBagLayout gbl = new GridBagLayout();

        tdTotal = new TimeDisplay(new SimpleDateFormat("HH:mm:ss"),
                Color.white, new Font("Times New Roman", Font.BOLD, 40),
                Color.black, lTotalMillis);

        JPanel pnlFields = new JPanel(gbl);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbl.setConstraints(tdTotal, gbc);
        pnlFields.add(tdTotal);

        btnStartStop = new JButton("Start");
        btnStartStop.addActionListener(this);

        JPanel pnlButtons = new JPanel();
        pnlButtons.add(btnStartStop);

        pnl.add("Center", pnlFields);
        pnl.add("South", pnlButtons);

        timer = new Timer(1000, new TimerListener());

        return pnl;
    }

    public static Boolean isSingleInstance()
    {
        return new Boolean(false);
    }

    public static String getMenuText()
    {
        return "Count Down Clock";
    }

    public Dimension getSize()
    {
        return getPreferredSize();
    }

    public static Icon getToolBarIcon()
    {
        return new ImageIcon("countdownclock.gif");
    }

    public void actionPerformed(ActionEvent event)
    {
        if (event.getSource() == btnStartStop)
        {
            if (btnStartStop.getText().equals("Start"))
            {
                lStart = System.currentTimeMillis();
                btnStartStop.setText("Stop");
                timer.start();
            }
            else
            {
                timer.stop();

                btnStartStop.setText("Start");
            }
        }
        super.actionPerformed(event);
    }

    class TimerListener implements ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
            long lTemp = lStart + lTotalMillis - System.currentTimeMillis();
            tdTotal.setTime(lTemp);
        }
    }
}
