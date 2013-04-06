package com.hogdev.racecontrol;

import javax.swing.table.AbstractTableModel;

public class RaceStopWatchTableModel extends AbstractTableModel
{

	public Object data[][];
	boolean[] hidden;
	String[] columnNames;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void clear()
	{
		Object temp[][] = new Object[0][];
		data = temp;
		fireTableDataChanged();
	}

	public void addRow(Object newrow[])
	{
		// last column is hidden
		Object temp[][] = new Object[data.length + 1][getColumnCount()];
		System.arraycopy(((Object) (data)), 0, ((Object) (temp)), 1, data.length);
		System.arraycopy(((Object) (newrow)), 0, ((Object) (temp[0])), 0, newrow.length);
		data = temp;

		// find best lap time
		int bestlap = 0;
		long besttime = Long.MAX_VALUE;
		for (int i = 0; i < data.length; i++)
		{
			Object[] row = data[i];
			//reset
			if(((Boolean)row[6]).booleanValue())
				row[6] = new Boolean(false);
			Long l = (Long) row[5];
			if (l.longValue() < besttime)
			{
				bestlap = i;
				besttime = l.longValue();
			}
		}

		data[bestlap][6] = true;
		fireTableRowsInserted(data.length + 1, data.length + 1);
	}

	public int getColumnCount()
	{
		return columnNames.length;
	}

	public int getRowCount()
	{
		return data.length;
	}

	public String getColumnName(int col)
	{
		if (hidden[col])
			col++;
		return columnNames[col];
	}

	public Object getValueAt(int row, int col)
	{
		if (hidden[col])
			col++;
		return data[row][col];
	}

	public void setVisible(int col, boolean visible)
	{
		this.hidden[col] = !visible;
	}

	RaceStopWatchTableModel(String[] cols)
	{
		data = new Object[0][];
		hidden = new boolean[cols.length];
		columnNames = cols;
	}
}
