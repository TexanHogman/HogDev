package com.hogdev.racecontrol;


public class RaceStopWatchesTableModel extends RaceStopWatchTableModel
{
    RaceStopWatchesTableModel(String[] cols)
    {
	super(cols);
	// TODO Auto-generated constructor stub
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public void addRow(Object newrow[])
    {
	// last column is hidden
	Object temp[][] = new Object[data.length + 1][getColumnCount()];
	System.arraycopy(((Object) (data)), 0, ((Object) (temp)), 1, data.length);
	System.arraycopy(((Object) (newrow)), 0, ((Object) (temp[0])), 0, newrow.length);
	data = temp;

	fireTableRowsInserted(data.length + 1, data.length + 1);
    }

    public void deleteRow(int id)
    {
	for (int i = 0; i < data.length; i++)
	{
	    if (((Integer) data[i][0]).intValue() == id)
	    {
		Object temp[][] = new Object[data.length - 1][getColumnCount()];
		System.arraycopy(((Object) (data)), 0, ((Object) (temp)), 0, i);
		if (i < data.length - 1)
		    System.arraycopy(((Object) (data)), i + 1, ((Object) (temp)), i, data.length - 1);
		data = temp;
		fireTableRowsDeleted(i, i);
		colorBestLap();
		break;
	    }
	}
    }
    
    public void updateName(int id, String name)
    {
	for (int i = 0; i < data.length; i++)
	{
	    Object[] currentRow = data[i];
	    if (((Integer) currentRow[0]).intValue() == id)
	    {
		currentRow[1] = name;
		updateRow(currentRow);
		break;
	    }
	}
    }

    public void updateRow(Object newrow[])
    {
	boolean found = false;

	for (int i = 0; i < data.length; i++)
	{
	    Object[] currentRow = data[i];
	    if (((Integer) currentRow[0]).intValue() == ((Integer) newrow[0]).intValue())
	    {
		long lastBest = ((Long) currentRow[5]).longValue();
		if (lastBest > 0 && lastBest < ((Long) newrow[5]).longValue())
		{
		    newrow[5] = currentRow[5];
		}

		data[i] = newrow;
		found = true;

		colorBestLap();

		fireTableRowsUpdated(i, i);
		break;
	    }
	}
	if (!found)
	    addRow(newrow);
    }

    private void colorBestLap()
    {
	// find best lap time
	int bestlap = -1;
	long besttime = Long.MAX_VALUE;
	for (int x = 0; x < data.length; x++)
	{
	    Object[] row = data[x];
	    // reset
	    if (((Boolean) row[6]).booleanValue())
	    {
		row[6] = new Boolean(false);
		fireTableRowsUpdated(x, x);
	    }
	    long l = ((Long) row[5]).longValue();
	    if (l > 0 && l < besttime)
	    {
		bestlap = x;
		besttime = l;
	    }
	}

	if (bestlap >= 0)
	{
	    data[bestlap][6] = true;
	    fireTableRowsUpdated(bestlap, bestlap);
	}
    }
}
