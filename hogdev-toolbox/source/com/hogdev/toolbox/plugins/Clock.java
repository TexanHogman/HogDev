package com.hogdev.toolbox.plugins;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.hogdev.toolbox.ToolBoxPlugin;

public class Clock extends ToolBoxPlugin
{
    /**
     * 
     */
    private static final long serialVersionUID = 708119576914471714L;
    com.hogdev.widgets.Clock clock;

    public static void main(String[] args)
    {
    }

    public Clock()
    {
        super();
    }

    public JPanel getGUI()
    {
        setTitle("Clock");
        setClosable(true);
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);

        JPanel pnl = new JPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        GridBagLayout gbl = new GridBagLayout();
        pnl.setLayout(gbl);

        clock = new com.hogdev.widgets.Clock();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbl.setConstraints(clock, gbc);
        pnl.add(clock);

        return pnl;
    }

    public static String getMenuText()
    {
        return "Clock";
    }

    public Dimension getSize()
    {
        return clock.getPreferredSize();
    }

    public static Icon getToolBarIcon()
    {
        return new ImageIcon("clock.gif");
    }

}