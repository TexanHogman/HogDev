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
import javax.swing.Timer;

import com.hogdev.toolbox.ToolBoxPlugin;
import com.hogdev.widgets.TimeDisplay;

public class RaceControl extends ToolBoxPlugin
{
    /**
     * 
     */
    private static final long serialVersionUID = -4074319203152297485L;

    public final static long lTotalMillis = 10 * 60 * 60 * 1000;

    Timer timer;

    JButton btnStartStop;

    JButton btnLapReset;

    long lStart = 0, lStop = 0;

    com.hogdev.widgets.Clock clock;

    TimeDisplay tdCD;

    TimeDisplay tdLap;

    public static void main(String[] args)
    {
    }

    public RaceControl()
    {
        super();
    }

    public JPanel getGUI()
    {
        setTitle("Race Control");
        setClosable(true);
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);

        JPanel pnl = new JPanel();
        pnl.setLayout(new BorderLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        GridBagLayout gbl = new GridBagLayout();

        clock = new com.hogdev.widgets.Clock();
        tdCD = new TimeDisplay(new SimpleDateFormat("HH:mm:ss"), Color.white,
                new Font("Times New Roman", Font.BOLD, 40), Color.black,
                lTotalMillis);

        JPanel pnlFields = new JPanel(gbl);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbl.setConstraints(clock, gbc);
        pnlFields.add(clock);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbl.setConstraints(tdCD, gbc);
        pnlFields.add(tdCD);

        btnStartStop = new JButton("Start");
        btnStartStop.addActionListener(this);

        btnLapReset = new JButton("Reset");
        btnLapReset.addActionListener(this);

        JPanel pnlButtons = new JPanel();
        pnlButtons.add(btnStartStop);
        pnlButtons.add(btnLapReset);

        pnl.add("Center", pnlFields);
        pnl.add("South", pnlButtons);

        timer = new Timer(100, new TimerListener());

        return pnl;
    }

    public static Boolean isSingleInstance()
    {
        return new Boolean(false);
    }

    public static String getMenuText()
    {
        return "Race Control";
    }

    public Dimension getSize()
    {
        return new Dimension(400, 300);
    }

    public static Icon getToolBarIcon()
    {
        return new ImageIcon("racecontrol.gif");
    }

    public void actionPerformed(ActionEvent event)
    {
        if (event.getSource() == btnStartStop)
        {
            if (btnStartStop.getText().equals("Start"))
            {
                if (lStart == 0)
                    lStart = System.currentTimeMillis();
                else
                    lStart = System.currentTimeMillis() - (lStop - lStart);

                btnStartStop.setText("Stop");
                timer.start();
            }
            else
            {
                lStop = System.currentTimeMillis();
                timer.stop();

                btnStartStop.setText("Start");
            }
        }
        else if (event.getSource() == btnLapReset)
        {
            tdCD.setTime(lTotalMillis);
        }
        super.actionPerformed(event);
    }

    class TimerListener implements ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
            long lTemp = lStart + lTotalMillis - System.currentTimeMillis();
            tdCD.setTime(lTemp);
        }
    }
}
