package com.hogdev.widgets;

import java.awt.BorderLayout;
import java.io.Serializable;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;


public class StatusPanel extends JPanel implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 7182301708929234941L;
    JLabel lblStatus;
    JProgressBar progressBar;
    int increment = 0;

    public StatusPanel()
    {
        super();

        lblStatus = new JLabel("Status:");
        progressBar = new JProgressBar();

        setLayout(new BorderLayout());

        add("Center", lblStatus);

        JPanel pnl = new JPanel();
        pnl.setLayout(new BorderLayout());
        pnl.add("East", progressBar);
        pnl.add("West", new JLabel(""));

        add("East", progressBar);
    }

    public String getStatus()
    {
        return (lblStatus.getText());
    }

    public void setStatus(String strStatus)
    {
        lblStatus.setText(strStatus);
    }

    /**
     *
     * setCompleteValue set's the value at which the progress
     * bar will register 100%.
     *
     * @param complete The integer representing the complete number
     *
     **/
    public void setCompleteValue(int complete)
    {
        progressBar.setMaximum(complete);
    }

    /**
     *
     * incrementValue will increment the progress bar by
     * the amount specified.
     *
     * @param increment The value to increment by.
     *
     **/
    public void incrementValue()
    {
        progressBar.setValue(++increment);
    }

    /**
     *
     * clearProgressBar will set the progress bar to 0, and
     * clear it out.
     *
     * @param increment The value to increment by.
     *
     **/
    public void clearProgressBar()
    {
        this.increment = 0;

        progressBar.setValue(0);
    }


}
