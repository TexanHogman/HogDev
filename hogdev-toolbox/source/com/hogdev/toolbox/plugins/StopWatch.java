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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;

import com.hogdev.toolbox.ToolBoxPlugin;
import com.hogdev.widgets.TimeDisplay;

public class StopWatch extends ToolBoxPlugin
{
    /**
     * 
     */
    private static final long serialVersionUID = 4051487259360641113L;

    Timer timer;

    JTextArea ta;

    JScrollPane sp;

    JButton btnStartStop;

    JButton btnLapReset;

    long lStart, lStop, lLap;

    int iLapNum;

    TimeDisplay tdTotal;

    TimeDisplay tdLap;

    public static void main(String[] args)
    {
    }

    public StopWatch()
    {
        super();
    }

    public JPanel getGUI()
    {
        setTitle("Stop Watch");
        setClosable(true);
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);

        JPanel pnl = new JPanel();
        pnl.setLayout(new BorderLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        GridBagLayout gbl = new GridBagLayout();

        tdTotal = new TimeDisplay(new SimpleDateFormat("HH:mm:ss.S"),
                Color.white, new Font("Times New Roman", Font.BOLD, 40),
                Color.black, 0);
        tdLap = new TimeDisplay(new SimpleDateFormat("mm:ss.SSS"), Color.white,
                new Font("Times New Roman", Font.BOLD, 40), Color.black, 0);

        JPanel pnlFields = new JPanel(gbl);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbl.setConstraints(tdTotal, gbc);
        pnlFields.add(tdTotal);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbl.setConstraints(tdLap, gbc);
        pnlFields.add(tdLap);

        ta = new JTextArea();
        ta.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ta.setEditable(false);

        sp = new JScrollPane(ta);

        btnStartStop = new JButton("Start");
        btnStartStop.addActionListener(this);

        btnLapReset = new JButton("Reset");
        btnLapReset.addActionListener(this);

        JPanel pnlButtons = new JPanel();
        pnlButtons.add(btnStartStop);
        pnlButtons.add(btnLapReset);

        pnl.add("North", pnlFields);
        pnl.add("Center", sp);
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
        return "Stop Watch";
    }

    public Dimension getSize()
    {
        return new Dimension(400, 300);
    }

    public static Icon getToolBarIcon()
    {
        return new ImageIcon("stopwatch.gif");
    }

    public void actionPerformed(ActionEvent event)
    {
        if (event.getSource() == btnStartStop)
        {
            if (btnStartStop.getText().equals("Start"))
            {
                lStart = System.currentTimeMillis();
                lLap = lStart;
                iLapNum = 0;
                btnStartStop.setText("Stop");
                btnLapReset.setText("Lap");
                timer.start();
            }
            else
            {
                lStop = System.currentTimeMillis();
                timer.stop();

                btnStartStop.setText("Start");
                btnStartStop.setEnabled(false);
                btnLapReset.setText("Reset");

                tdTotal.setTime(lStop - lStart);
                doLap(lStop);
            }
        }
        else if (event.getSource() == btnLapReset)
        {
            if (btnLapReset.getText().equals("Lap"))
            {
                long lTemp = System.currentTimeMillis();
                doLap(lTemp);
            }
            else
            {
                lStart = 0;
                lLap = 0;
                iLapNum = 0;
                tdLap.setTime(lLap);
                tdTotal.setTime(lStart);
                btnStartStop.setEnabled(true);
                ta.setText("");
            }
        }
        super.actionPerformed(event);
    }

    private void doLap(long lTime)
    {
        long lDelta = lTime - lLap;
        tdLap.setTime(lDelta);

        iLapNum++;
        ta.append("# " + iLapNum + " start time = " + (lLap - lStart)
                + " stop time = " + (lTime - lStart) + " delta = " + lDelta
                + "\n");
        lLap = lTime;
    }

    class TimerListener implements ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
            tdTotal.setTime(System.currentTimeMillis() - lStart);
        }
    }
}
