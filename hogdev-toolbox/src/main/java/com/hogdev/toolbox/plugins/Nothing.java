package com.hogdev.toolbox.plugins;

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.hogdev.toolbox.ToolBoxPlugin;

public class Nothing extends ToolBoxPlugin
{
    /**
     * 
     */
    private static final long serialVersionUID = 716015786644607836L;

    public static void main(String[] args)
    {
    }

    public Nothing()
    {
        super();
    }

    public JPanel getGUI()
    {
        setTitle("Nothing");

        JPanel pnl = new JPanel();
        pnl.add(new JLabel("Nothing"));

        return pnl;
    }

    public static String getMenuText()
    {
        return "Nothing";
    }

    public Dimension getSize()
    {
        return new Dimension(100, 100);
    }

    public static Icon getToolBarIcon()
    {
        return new ImageIcon("putFolder.gif");
    }

}