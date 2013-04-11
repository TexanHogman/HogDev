package com.hogdev.widgets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JComponent;

public class TimeDisplay extends JComponent
{
    /**
     * 
     */
    private static final long serialVersionUID = 2035825765747360050L;

    protected SimpleDateFormat m_DateFormat;
    // end bean properties

    int iOffset = 0;
    long lTime = 0;

    private int m_iMaxAscent;

    public TimeDisplay()
    {
        this(System.currentTimeMillis());
    }

    public TimeDisplay(Long lTime)
    {
        this(lTime.longValue());
    }

    public TimeDisplay(long lTime)
    {
        this(new SimpleDateFormat("H:mm:ss.SSS"), Color.white, new Font("Times New Roman", Font.BOLD, 40), Color.blue, lTime);
    }

    public TimeDisplay(SimpleDateFormat sdf, Color bgColor, Font font, Color fontColor, long lTime)
    {
        super();

        setDateFormat(sdf);
        setFont(font);
        setBackground(bgColor);
        setForeground(fontColor);

        // try to make a sample time string so I can get the preferred size
        Timestamp ts = Timestamp.valueOf("2000-12-31 12:30:60.999999999");
        String dateString = m_DateFormat.format(ts);

        Rectangle2D rect = getFont().getStringBounds(dateString, new FontRenderContext(getFont().getTransform(), false, false));

        int iX = (int) rect.getWidth();
        int iY = (int) rect.getHeight();
        setPreferredSize(new Dimension(iX, iY));

        iOffset = Calendar.getInstance().get(Calendar.ZONE_OFFSET);

        setTime(lTime);
    }

    public void paint(Graphics g)
    {
        // This works but is ugly
        Color cTemp = getForeground();
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(cTemp);

        String dateString = toString();
        Rectangle2D rect = getFont().getStringBounds(dateString, new FontRenderContext(getFont().getTransform(), false, false));
        int iX = (int) rect.getWidth();
        int iY = (int) rect.getHeight();

        iX = (getWidth() - iX) / 2;
        iY = (getHeight() - iY) / 2 + m_iMaxAscent;

        g.drawString(dateString, iX, iY);

        super.paint(g);
    }

    public String toString()
    {
	long lCorrected = lTime - iOffset;
//	System.out.println(lCorrected);
        java.util.Date dt = new java.util.Date(lCorrected);
        String dateString = m_DateFormat.format(dt);
//        System.out.println(dateString);

        //hack to fix millis
        String strPattern = m_DateFormat.toPattern();
        int i = strPattern.indexOf(".S");
        if (i != -1)
        {
            // get past first dot
            i++;
            int j = 1;
            if (strPattern.length() > i + 1 && strPattern.charAt(++i) == 'S')
                j++;
            if (strPattern.length() > i + 1 && strPattern.charAt(++i) == 'S')
                j++;

            int k = dateString.lastIndexOf(".");
            k = dateString.length() - k - 1;
            while (k < j)
            {
                dateString += "0";
                k++;
            }
            while (k > j)
            {
                dateString = dateString.substring(0, dateString.length() - 1);
                k--;
            }
        }
        if(strPattern.startsWith("H:") && dateString.startsWith("0:"))
        {
        	dateString = dateString.substring(2);
        }
        return dateString;
    }


    public SimpleDateFormat getDateFormat()
    {
        return m_DateFormat;
    }

    private void setBeanSize()
    {
        // size is dependent on font and date format
        if (getFont() != null && m_DateFormat != null)
        {
            // try to make a sample time string so I can get the preferred size
            Timestamp ts = Timestamp.valueOf("2000-12-31 12:30:60.000000000");
            String dateString = m_DateFormat.format(ts);
            Rectangle2D rect = getFont().getStringBounds(dateString, new FontRenderContext(getFont().getTransform(), false, false));

            int iX = (int) rect.getWidth();
            int iY = (int) rect.getHeight();

            // add pading
            Dimension dim = new Dimension(iX, iY + 10);
            setPreferredSize(dim);
            setMinimumSize(dim);
        }
    }

    public void setDateFormat(SimpleDateFormat dateFormat)
    {
        m_DateFormat = dateFormat;

        // size is dependent on font and date format
        setBeanSize();
    }


    public void setFont(Font font)
    {
        super.setFont(font);

        // size is dependent on font and date format
        setBeanSize();

        FontMetrics fm = getFontMetrics(getFont());
        m_iMaxAscent = fm.getMaxAscent();
    }

    public void setTime(long l)
    {
        lTime = l;
        repaint();
    }
    
    public static void main(String[] args)
    {
	TimeDisplay td = new TimeDisplay();
	td.setTime(21700000);
	System.out.println(td.toString());
    }

}
