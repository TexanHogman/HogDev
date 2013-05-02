package com.hogdev.racecontrol;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.hogdev.widgets.TimeDisplay;

public class RacePanel extends JPanel implements ActionListener
{
    /**
     * 
     */
    private static final long serialVersionUID = -2240057485939477363L;
    public long lTotalMillis = 0 * RaceControl.MIN;
    Timer timer1;
    Timer timer2;
    long lStart = 0, lStop = 0;

    com.hogdev.widgets.Clock clock;

    TimeDisplay tdCD;
    TimeDisplay tdCU;

    JButton btnUp, btnDown;

    boolean bRunning = false;

    public RacePanel()
    {
        setLayout(new BorderLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        GridBagLayout gbl = new GridBagLayout();

        clock = new com.hogdev.widgets.Clock(new SimpleDateFormat("h:mm:ss:a"), SystemColor.control, new Font("Times New Roman", Font.BOLD, 40), Color.black);
        clock.setBorder(BorderFactory.createLineBorder(Color.gray));

        tdCU = new TimeDisplay(new SimpleDateFormat("H:mm:ss"), SystemColor.control, new Font("Times New Roman", Font.BOLD, 40), Color.black, 0);
        tdCU.setBorder(BorderFactory.createLineBorder(Color.gray));

//		tdCD = new TimeDisplay(new SimpleDateFormat("H:mm:ss"), Color.white, new Font("Times New Roman", Font.BOLD, 40), Color.black, lTotalMillis);
//		tdCD.setBorder(BorderFactory.createLineBorder(Color.gray));

        tdCD = new TimeDisplay(new SimpleDateFormat("H:mm:ss"), SystemColor.control, new Font("Times New Roman", Font.BOLD, 40), Color.black, 0);
        tdCD.setBorder(BorderFactory.createLineBorder(Color.gray));

        JPanel pnlUpDown = new JPanel();
        pnlUpDown.setLayout(new GridLayout(2, 1));

        btnUp = new JButton("+");
        btnUp.setMargin(new Insets(0, 0, 0, 0));
        btnUp.addActionListener(this);

        btnDown = new JButton("-");
        btnDown.setMargin(new Insets(0, 0, 0, 0));
        btnDown.addActionListener(this);

        pnlUpDown.add(btnUp);
        pnlUpDown.add(btnDown);

        JPanel pnlFields = new JPanel(gbl);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbl.setConstraints(tdCU, gbc);
        pnlFields.add(tdCU);

        gbc.anchor = GridBagConstraints.CENTER;
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

        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbl.setConstraints(pnlUpDown, gbc);
        pnlFields.add(pnlUpDown);
/*
		gbc.anchor  	= GridBagConstraints.WEST;
		gbc.insets = new Insets(0,0,0,0);
		gbc.weightx 	= 1;
		gbc.weighty 	= 1;
		gbc.gridheight	= 1;
		gbc.gridwidth	= 1;
		gbc.fill    	= GridBagConstraints.BOTH;
		gbl.setConstraints(pnl, gbc);
		pnlFields.add(pnl);
*/
        add("Center", pnlFields);

        timer1 = new Timer(1000, new CountDownTimerListener(tdCD));
        timer1.addActionListener(new CountUpTimerListener(tdCU));
    }

    private void start()
    {
        if (lStart == 0)
            lStart = System.currentTimeMillis();
        else
            lStart = System.currentTimeMillis() - (lStop - lStart);

        timer1.start();
        bRunning = true;
    }

    private void stop()
    {
        lStop = System.currentTimeMillis();
        timer1.stop();
        bRunning = false;
    }

    private void reset()
    {
        tdCD.setTime(lTotalMillis);
        tdCD.setForeground(Color.black);
        tdCU.setTime(0);
        tdCU.setForeground(Color.black);
        lStart = 0;
        bRunning = false;
    }

    public void actionPerformed(ActionEvent event)
    {
        if (event.getActionCommand().equals(RaceControl.START))
        {
            start();
        }
        else if (event.getActionCommand().equals(RaceControl.STOP))
        {
            stop();
        }
        else if (event.getActionCommand().equals(RaceControl.RESET))
        {
            reset();
        }
        else if (event.getSource() == btnUp)
        {
            lTotalMillis += 30 * RaceControl.MIN;
            if (!bRunning)
            {
                // stopped
                long lTemp = System.currentTimeMillis();
                if (lStart != 0)
                    tdCD.setTime(lStart + lTotalMillis - lTemp);
                else
                    tdCD.setTime(lTotalMillis);
            }
        }
        else if (event.getSource() == btnDown)
        {
            lTotalMillis -= 30 * RaceControl.MIN;
            if (!bRunning)
            {
                // stopped
                long lTemp = System.currentTimeMillis();
                if (lStart != 0)
                    tdCD.setTime(lStart + lTotalMillis - lTemp);
                else
                    tdCD.setTime(lTotalMillis);
            }

        }
    }

    class TimerListener implements ActionListener
    {
        TimeDisplay td_;

        public TimerListener(TimeDisplay td)
        {
            td_ = td;
        }

        public void actionPerformed(ActionEvent event)
        {
            long lTemp = System.currentTimeMillis();
            td_.setTime(lTemp);
        }
    }

    class CountUpTimerListener extends TimerListener
    {
        public CountUpTimerListener(TimeDisplay td)
        {
            super(td);
        }

        public void actionPerformed(ActionEvent event)
        {
            long lTemp = System.currentTimeMillis() - lStart;
            td_.setTime(lTemp);
        }
    }

    class CountDownTimerListener extends TimerListener
    {
        public CountDownTimerListener(TimeDisplay td)
        {
            super(td);
        }

        public void actionPerformed(ActionEvent event)
        {
            long lTemp = lStart + lTotalMillis - System.currentTimeMillis() + RaceControl.SEC;
            td_.setTime(lTemp);

            if (lTemp < 15 * RaceControl.MIN && lTemp > 0)
            // change color to blue
                td_.setForeground(Color.red);
            else if (lTemp < 30 * RaceControl.MIN && lTemp > 0)
            // change color to blue
                td_.setForeground(Color.blue);
            else
                td_.setForeground(Color.black);

        }
    }

}
