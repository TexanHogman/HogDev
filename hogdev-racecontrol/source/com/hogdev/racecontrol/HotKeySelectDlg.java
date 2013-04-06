/*
 * Created by IntelliJ IDEA.
 * User: 
 * Date: Sep 19, 2002
 * Time: 1:15:24 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.hogdev.racecontrol;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class HotKeySelectDlg extends JDialog
{
    /**
     * 
     */
    private static final long serialVersionUID = -6058088480911528120L;

    public HotKeySelectDlg(Component owner) //throws HeadlessException
    {
//        Window window = HotKeySelectDlg.getWindowForComponent(owner);
//        if (window instanceof Frame)
//        {
//            dialog = new JDialog((Frame) window, title, true);
//        }
//        else
//        {
//            dialog = new JDialog((Dialog) window, title, true);
//        }
//
//        getContentPane().add(new JLabel("TEST"));
    }

    public static Window getWindowForComponent(Component parentComponent)
            //  throws HeadlessException
    {
        if (parentComponent == null)
            return JOptionPane.getRootFrame();
        if (parentComponent instanceof Frame || parentComponent instanceof Dialog)
            return (Window) parentComponent;
        return HotKeySelectDlg.getWindowForComponent(parentComponent.getParent());
    }
}
