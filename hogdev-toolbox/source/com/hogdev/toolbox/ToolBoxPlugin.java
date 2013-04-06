package com.hogdev.toolbox;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

public abstract class ToolBoxPlugin extends JInternalFrame implements
        PropertyChangeListener, ActionListener, KeyListener,
        InternalFrameListener
{
    protected JPanel pnlPlugin;

    public ToolBoxPlugin()
    {
        super();

        pnlPlugin = getGUI();
        getContentPane().add(pnlPlugin);
        pack();

        addInternalFrameListener(this);

        setSize(getSize());
    }

    public void propertyChange(PropertyChangeEvent evt)
    {
    }

    public void actionPerformed(ActionEvent event)
    {
    }

    public void internalFrameClosed(InternalFrameEvent event)
    {
    }

    public void internalFrameIconified(InternalFrameEvent event)
    {
    }

    public void internalFrameDeactivated(InternalFrameEvent event)
    {
    }

    public void internalFrameDeiconified(InternalFrameEvent event)
    {
    }

    public void internalFrameActivated(InternalFrameEvent event)
    {
    }

    public void internalFrameClosing(InternalFrameEvent event)
    {
    }

    public void internalFrameOpened(InternalFrameEvent event)
    {
    }

    public void keyReleased(KeyEvent event)
    {
    }

    public void keyPressed(KeyEvent event)
    {
    }

    public void keyTyped(KeyEvent event)
    {
    }

    public static Boolean isSingleInstance()
    {
        return new Boolean(true);
    }

    public static String getMenuText()
    {
        return new String("ToolBoxPlugin");
    }

    public static Icon getToolBarIcon()
    {
        return null;
    }

    // If this is not overridden than I will get the preferred size
    public Dimension getSize()
    {
        return pnlPlugin.getPreferredSize();
    }

    public abstract JPanel getGUI();
}