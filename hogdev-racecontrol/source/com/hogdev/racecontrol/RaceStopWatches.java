package com.hogdev.racecontrol;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.text.SimpleDateFormat;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import com.hogdev.toolbox.ToolBoxPlugin;

public class RaceStopWatches extends ToolBoxPlugin implements LapListener
{
    private static final long serialVersionUID = 3540565729486646336L;
    private static final String columnNames[] = { "ID", "Name", "Lap #", "Lap Time", "Fastest Lap #", "Fastest Lap Time", "Fastest Lap?"};
    JTable table;
    JScrollPane sp;
    RaceStopWatchesTableModel myModel;
    TableColumn[] tc;

    public RaceStopWatches()
    {
	super();
	setName("Summary");
    }

    private String[] getColumnNames()
    {
	return columnNames;
    }

    public JPanel getGUI()
    {
	setClosable(false);
	setResizable(true);
	setMaximizable(true);
	setIconifiable(true);
	JPanel pnl = new JPanel();
	pnl.setLayout(new BorderLayout());

	myModel = new RaceStopWatchesTableModel(getColumnNames());
	table = new JTable(myModel);
	table.setPreferredScrollableViewportSize(new Dimension(50, 50));
	TimerDisplayRenderer rend = new TimerDisplayRenderer();
	rend.setDateFormat(new SimpleDateFormat("H:mm:ss.SSS"));
	rend.setForeground(table.getForeground());
	rend.setBackground(table.getBackground());
	rend.setFont(table.getFont());
	TimerDisplayRenderer rendWithColor = new TimerDisplayRenderer();
	rendWithColor.setDateFormat(new SimpleDateFormat("H:mm:ss.SSS"));
	rendWithColor.setForeground(table.getForeground());
	rendWithColor.setBackground(table.getBackground());
	rendWithColor.setFont(table.getFont());
	rendWithColor.setChangeColor(true);
	TimerDisplayRenderer rendBestWithColor = new TimerDisplayRenderer();
	rendBestWithColor.setDateFormat(new SimpleDateFormat("H:mm:ss.SSS"));
	rendBestWithColor.setForeground(table.getForeground());
	rendBestWithColor.setBackground(table.getBackground());
	rendBestWithColor.setFont(table.getFont());
	rendBestWithColor.setChangeBestColor(true);
	DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
	renderer.setHorizontalAlignment(0);
	table.getColumnModel().getColumn(0).setCellRenderer(renderer);
	table.getColumnModel().getColumn(1).setCellRenderer(renderer);
	table.getColumnModel().getColumn(2).setCellRenderer(renderer);
	table.getColumnModel().getColumn(3).setCellRenderer(rend);
	table.getColumnModel().getColumn(4).setCellRenderer(renderer);
	table.getColumnModel().getColumn(5).setCellRenderer(rendBestWithColor);
	sp = new JScrollPane(table);
	pnl.add("Center", sp);

	tc = new TableColumn[3];
	tc[0] = table.getColumnModel().getColumn(0);
	tc[1] = table.getColumnModel().getColumn(4);
	tc[2] = table.getColumnModel().getColumn(6);
	table.removeColumn(tc[0]);
	table.removeColumn(tc[1]);
	table.removeColumn(tc[2]);
	myModel.setVisible(0, false);
	myModel.setVisible(4, false);
	myModel.setVisible(6, false);

	return pnl;
    }

    public static Boolean isSingleInstance()
    {
	return new Boolean(true);
    }

    public static String getMenuText()
    {
	return "Summary";
    }

    public Dimension getSize()
    {
	return new Dimension(400, 300);
    }

    public static Icon getToolBarIcon()
    {
	return new ImageIcon("stopwatch.gif");
    }

    public void actionPerformed(ActionEvent event)
    {
	super.actionPerformed(event);
    }

    private void doTitle()
    {
	String strTitle = getName();
	setTitle(strTitle);
    }

    private void createMenu()
    {
    }

    public void internalFrameOpened(InternalFrameEvent event)
    {
	doTitle();
    }

    @Override
    public void lap(int id, String name, Object[] data)
    {
	Object row[] = { id, name, data[0], data[5], data[0],data[5], false };
	myModel.updateRow(row);
    }
    
    public void remove(int id)
    {
	// find our row
	myModel.deleteRow(id);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
	if(evt.getPropertyName().equals("timername"))
	    myModel.updateName(evt.getSource().hashCode(), (String)evt.getNewValue());
	
	super.propertyChange(evt);
    }

}
