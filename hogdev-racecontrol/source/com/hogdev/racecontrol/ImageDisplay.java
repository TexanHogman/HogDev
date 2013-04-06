package com.hogdev.racecontrol;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JComponent;

/**
 * Created by IntelliJ IDEA.
 * User: 
 * Date: May 14, 2003
 * Time: 9:47:05 PM
 * To change this template use Options | File Templates.
 */
public class ImageDisplay extends JComponent
{
    /**
     * 
     */
    private static final long serialVersionUID = -6905982136193111702L;

    public ImageDisplay(Image img)
    {
        if (img != null)
        {
            img_ = img;
            setPreferredSize(new Dimension(img_.getWidth(null), img_.getHeight(null)));
        }
    }

    public void paint(Graphics g)
    {
        if (img_ != null)
        {
            Dimension dim = getSize();
            g.drawImage(img_, 0, 0, (int) dim.getWidth(), (int) dim.getHeight(), null);
        }
    }

    Image img_;
}
