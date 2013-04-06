package com.hogdev.racecontrol;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import com.hogdev.widgets.TimeDisplay;

public class TimerDisplayRenderer extends TimeDisplay implements TableCellRenderer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3348632210590748464L;

	public void setChangeColor(boolean bChangeColor)
	{
		this.bChangeColor_ = bChangeColor;
	}

	public void setChangeBestColor(boolean bChangeColor)
	{
		this.bChangeBestColor_ = bChangeColor;
	}

	private boolean bChangeColor_;
	private boolean bChangeBestColor_;

	// We need a place to store the color the JLabel should be returned
	// to after its foreground and background colors have been set
	// to the selection background color.
	// These vars will be made protected when their names are finalized.
	private Color unselectedForeground;
	private Color unselectedBackground;

	public TimerDisplayRenderer()
	{
		super();
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		if (isSelected)
		{
			super.setForeground(table.getSelectionForeground());
			super.setBackground(table.getSelectionBackground());
		}
		else
		{
			super.setForeground((unselectedForeground != null) ? unselectedForeground : table.getForeground());
			super.setBackground((unselectedBackground != null) ? unselectedBackground : table.getBackground());

			if (value instanceof Long)
			{
				long l = ((Long) value).longValue();

				setTime(Math.abs(l));

				if (bChangeColor_)
				{
					if (l < 0)
						setForeground(Color.GREEN);
					else if (l > 0)
						setForeground(Color.RED);
					else
						setForeground(Color.BLACK);

				}
				if (bChangeBestColor_)
				{
					RaceStopWatchTableModel model = (RaceStopWatchTableModel)table.getModel();
					int x = model.getColumnCount() - 1;
					model.setVisible(x, true);
					Boolean b = (Boolean) model.getValueAt(row, x);
					model.setVisible(x, false);
					if (b.booleanValue())
					{
						setForeground(Color.BLUE);
					}
					else
					{
						setForeground(Color.BLACK);
						
					}
				}
			}
		}

		return this;
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 */
	public void validate()
	{
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 */
	public void revalidate()
	{
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 */
	public void repaint(long tm, int x, int y, int width, int height)
	{
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 */
	public void repaint(Rectangle r)
	{
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 */
	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue)
	{
		// Strings get interned...
		if (propertyName == "text")
		{
			super.firePropertyChange(propertyName, oldValue, newValue);
		}
	}

	/**
	 * Overrides <code>JComponent.setForeground</code> to assign the
	 * unselected-foreground color to the specified color.
	 * 
	 * @param c
	 *            set the foreground color to this value
	 */
	public void setForeground(Color c)
	{
		super.setForeground(c);
		unselectedForeground = c;
	}

	/**
	 * Overrides <code>JComponent.setBackground</code> to assign the
	 * unselected-background color to the specified color.
	 * 
	 * @param c
	 *            set the background color to this value
	 */
	public void setBackground(Color c)
	{
		super.setBackground(c);
		unselectedBackground = c;
	}

}
