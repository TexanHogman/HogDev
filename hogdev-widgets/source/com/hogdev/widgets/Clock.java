package com.hogdev.widgets;

import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;

import javax.swing.JFrame;

public class Clock extends TimeDisplay implements ActionListener
{
    /**
     * 
     */
    private static final long serialVersionUID = 7355250123582130520L;
    private javax.swing.Timer m_timer;

    public static void main(String args[])
    {
        Clock hog = new Clock();

        JFrame f = new JFrame("");
        f.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });
        f.getContentPane().add("Center", hog);
        f.pack();
        f.setSize(hog.getPreferredSize());
        f.show();
    }

    public Clock()
    {
        this(new SimpleDateFormat("h:mm:ss:a"), SystemColor.menu, new Font("Times New Roman", Font.BOLD, 40), Color.black);
    }

    public Clock(SimpleDateFormat sdf, Color bgColor, Font font, Color fontColor)
    {
        super(sdf, bgColor, font, fontColor, System.currentTimeMillis());
        iOffset = 0;
        startTimer();
    }

    protected void startTimer()
    {
        m_timer = new javax.swing.Timer(1000, this);
        m_timer.start();
    }

    public void actionPerformed(ActionEvent event)
    {
        setTime(System.currentTimeMillis());
        repaint();
    }
}
