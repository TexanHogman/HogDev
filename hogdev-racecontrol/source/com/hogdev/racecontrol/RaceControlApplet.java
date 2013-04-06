package com.hogdev.racecontrol;

import javax.swing.JApplet;

public class RaceControlApplet extends JApplet
{
    /**
     * 
     */
    private static final long serialVersionUID = -8162010023020606357L;

    public static void main(String[] args)
    {
        RaceControlApplet rca = new RaceControlApplet();
    }

    public RaceControlApplet()
    {
        RaceControl rc = new RaceControl();
//		getContentPane().add(rc);
    }
}
