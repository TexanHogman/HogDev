package com.hogdev.racecontrol;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import com.hogdev.toolbox.ToolBoxPlugin;
import com.hogdev.widgets.TimeDisplay;

public class RaceStopWatch extends ToolBoxPlugin implements IREventListener
{
	private static final String columnNames[] = { "Lap #", "Average", "Rider Lap #", "Rider Average", "Delta", "Lap Time", "Fastest Lap?" };
	static int iTeamNum_ = 1;
	private static final long serialVersionUID = -7939553013535915182L;
	public static final String START = "Start";
	public static final String STOP = "Stop";
	public static final String RESET = "Reset";
	public static final String LAP = "Lap";
	public static final String EXPORT = "Save";
	public static final String RIDER = "Rider";
	Timer timer1;
	Timer timer2;
	Timer timer3;
	JTable table;
	JScrollPane sp;
	long lStart;
	long lStop;
	long lLap;
	long lElapsed;
	long lRiderStart;
	int iLapNum;
	int iRiderLapNum;
	int iMinLap;
	TimeDisplay tdCU;
	TimeDisplay tdRunner;
	TimeDisplay tdLap;
	String strKey;
	RaceStopWatchTableModel myModel;
	int iKeyCode;
	boolean bRunning;
	boolean bSound;
	JButton btnStartStop;
	JButton btnLapReset;
	JButton btnExport;
	JButton btnRider;
	JMenuItem miSave;
	JMenuItem miMinLap;
	JMenuItem miKey;
	JMenuItem miIR;
	JMenuItem miSounds;
	JMenuItem miName;
	JRadioButton radNone;
	JRadioButton rad10Progress;
	JRadioButton rad5Progress;
	JRadioButton rad10Alert;
	JRadioButton rad5Alert;
	IRLearnDialog irDlg;
	String learnedCode;
	TableColumn[] tc;
	JCheckBoxMenuItem chkMulti;
	ArrayList laplisteners = new ArrayList();

	public RaceStopWatch(boolean irSelected)
	{
		super();
		bRunning = false;
		bSound = true;
		setName("Timer - " + iTeamNum_++);

		iKeyCode = 0;
		createMenu();
		miIR.setEnabled(irSelected);
	}

	public void addLapListener(LapListener listener)
	{
		laplisteners.add(listener);
	}

	public void removeLapListener(LapListener listener)
	{
		laplisteners.remove(listener);
	}

	private String[] getColumnNames()
	{
		return columnNames;
	}

	public void setKeyCode(int iKeyCode)
	{
		this.iKeyCode = iKeyCode;
	}

	public void setMinLap(int iMinLap)
	{
		this.iMinLap = iMinLap;
	}

	public JPanel getGUI()
	{
		setClosable(true);
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		JPanel pnl = new JPanel();
		pnl.setLayout(new BorderLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		GridBagLayout gbl = new GridBagLayout();
		tdRunner = new TimeDisplay(new SimpleDateFormat("H:mm:ss.S"), SystemColor.control, new Font("Times New Roman", 1, 28), Color.black, 0L);
		tdRunner.setBorder(BorderFactory.createLineBorder(Color.gray));
		tdLap = new TimeDisplay(new SimpleDateFormat("H:mm:ss.SSS"), SystemColor.control, new Font("Times New Roman", 1, 28), Color.black, 0L);
		tdLap.setBorder(BorderFactory.createLineBorder(Color.gray));
		tdCU = new TimeDisplay(new SimpleDateFormat("H:mm:ss"), SystemColor.control, new Font("Times New Roman", 1, 28), Color.black, 0L);
		tdCU.setBorder(BorderFactory.createLineBorder(Color.gray));
		JPanel pnlButtons = new JPanel();
		btnStartStop = new JButton("Start");
		btnStartStop.addActionListener(this);
		// hide these guys cause I don't think each stopwatch should stop start
		// itself.
		btnStartStop.setVisible(false);
		// changing logic to remove ability to start/reset each time
		// individually
		// btnLapReset = new JButton("Reset");
		btnLapReset = new JButton("Lap");
		btnLapReset.setEnabled(false);
		btnLapReset.addActionListener(this);
		btnExport = new JButton("Save");
		btnExport.addActionListener(this);
		pnlButtons.add(btnStartStop);
		pnlButtons.add(btnLapReset);
		btnRider = new JButton("Rider");
		btnRider.addActionListener(this);
		pnlButtons.add(btnRider);
		JPanel pnlFields = new JPanel(gbl);
		gbc.anchor = 13;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.weightx = 1.0D;
		gbc.weighty = 1.0D;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.fill = 1;
		gbl.setConstraints(tdCU, gbc);
		pnlFields.add(tdCU);
		gbc.anchor = 10;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.weightx = 1.0D;
		gbc.weighty = 1.0D;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.fill = 1;
		gbl.setConstraints(tdRunner, gbc);
		pnlFields.add(tdRunner);
		gbc.anchor = 17;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.weightx = 1.0D;
		gbc.weighty = 1.0D;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.fill = 1;
		gbl.setConstraints(tdLap, gbc);
		pnlFields.add(tdLap);
		myModel = new RaceStopWatchTableModel(getColumnNames());
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
		table.getColumnModel().getColumn(1).setCellRenderer(rend);
		table.getColumnModel().getColumn(2).setCellRenderer(renderer);
		table.getColumnModel().getColumn(3).setCellRenderer(rend);
		table.getColumnModel().getColumn(4).setCellRenderer(rendWithColor);
		table.getColumnModel().getColumn(5).setCellRenderer(rendBestWithColor);
		sp = new JScrollPane(table);
		pnl.add("North", pnlFields);
		pnl.add("Center", sp);
		pnl.add("South", pnlButtons);
		timer1 = new Timer(100, new TimerListener1());
		timer2 = new Timer(1000, new TimerListener2());
		timer3 = new Timer(1000, new TimerListener3());

		// hide these for now
		btnRider.setVisible(false);
		tc = new TableColumn[3];
		tc[0] = table.getColumnModel().getColumn(2);
		tc[1] = table.getColumnModel().getColumn(3);
		tc[2] = table.getColumnModel().getColumn(6);
		table.removeColumn(tc[0]);
		table.removeColumn(tc[1]);
		table.removeColumn(tc[2]);
		myModel.setVisible(2, false);
		myModel.setVisible(3, false);
		myModel.setVisible(6, false);

		tdCU.setVisible(false);

		return pnl;
	}

	public static Boolean isSingleInstance()
	{
		return new Boolean(false);
	}

	public static String getMenuText()
	{
		return "Stop Watch";
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
		KeyboardFocusManager focus = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		KeyboardFocusManager.setCurrentKeyboardFocusManager(null);

		if (event.getActionCommand().equals("Start All (S)"))
			doStart();
		else if (event.getActionCommand().equals("Stop All (S)"))
			doStop();
		else if (event.getActionCommand().startsWith("Lap"))
			doLap();
		else if (event.getActionCommand().equals("Reset All (R)"))
			doReset();
		// IR ENABLED
		else if (event.getActionCommand().equals("Infrared"))
		{
			if (((JCheckBoxMenuItem) event.getSource()).isSelected())
				miIR.setEnabled(true);
			else
				miIR.setEnabled(false);
		}
		else if (event.getSource() == btnStartStop)
			doStartStop();
		else if (event.getSource() == btnLapReset)
			doLapReset();
		else if (event.getSource() == btnExport || event.getSource() == miSave)
			doExport();
		else if (event.getSource() == miName)
		{
			String oldName = getName();
			String strLapTimerName = JOptionPane.showInputDialog(this, "Please input a team name");
			if (strLapTimerName != null && strLapTimerName.length() > 0)
			{
				setName(strLapTimerName);
				doTitle();
				firePropertyChange("timername", oldName, strLapTimerName);
			}
		}
		else if (event.getSource() == btnRider)
			doRider();
		else if (event.getSource() == miKey)
		{
			Object possibleValues[] = { "none", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "F11", "F12", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "Key Pad 1", "Key Pad 2",
					"Key Pad 3", "Key Pad 4", "Key Pad 5", "Key Pad 6", "Key Pad 7", "Key Pad 8", "Key Pad 9", "Key Pad 0", "Left", "Right", "Up", "Down", "Insert", "Delete", "Home", "End",
					"Page Up", "Page Down" };
			Object selectedValue = JOptionPane.showInputDialog(this, "Choose hot key for lap.", "Pick a key mapping", 3, null, possibleValues, possibleValues[0]);
			if (selectedValue != null)
			{
				strKey = (String) selectedValue;
				if (strKey.equals("none"))
				{
					strKey = null;
					iKeyCode = 0;
				}
				else if (strKey.equals("F1"))
					iKeyCode = 112;
				else if (strKey.equals("F2"))
					iKeyCode = 113;
				else if (strKey.equals("F3"))
					iKeyCode = 114;
				else if (strKey.equals("F4"))
					iKeyCode = 115;
				else if (strKey.equals("F5"))
					iKeyCode = 116;
				else if (strKey.equals("F6"))
					iKeyCode = 117;
				else if (strKey.equals("F7"))
					iKeyCode = 118;
				else if (strKey.equals("F8"))
					iKeyCode = 119;
				else if (strKey.equals("F9"))
					iKeyCode = 120;
				else if (strKey.equals("F10"))
					iKeyCode = 121;
				else if (strKey.equals("F11"))
					iKeyCode = 122;
				else if (strKey.equals("F12"))
					iKeyCode = 123;
				else if (strKey.equals("1"))
					iKeyCode = 49;
				else if (strKey.equals("2"))
					iKeyCode = 50;
				else if (strKey.equals("3"))
					iKeyCode = 51;
				else if (strKey.equals("4"))
					iKeyCode = 52;
				else if (strKey.equals("5"))
					iKeyCode = 53;
				else if (strKey.equals("6"))
					iKeyCode = 54;
				else if (strKey.equals("7"))
					iKeyCode = 55;
				else if (strKey.equals("8"))
					iKeyCode = 56;
				else if (strKey.equals("9"))
					iKeyCode = 57;
				else if (strKey.equals("0"))
					iKeyCode = 48;
				else if (strKey.equals("Key Pad 1"))
					iKeyCode = 97;
				else if (strKey.equals("Key Pad 2"))
					iKeyCode = 98;
				else if (strKey.equals("Key Pad 3"))
					iKeyCode = 99;
				else if (strKey.equals("Key Pad 4"))
					iKeyCode = 100;
				else if (strKey.equals("Key Pad 5"))
					iKeyCode = 101;
				else if (strKey.equals("Key Pad 6"))
					iKeyCode = 102;
				else if (strKey.equals("Key Pad 7"))
					iKeyCode = 103;
				else if (strKey.equals("Key Pad 8"))
					iKeyCode = 104;
				else if (strKey.equals("Key Pad 9"))
					iKeyCode = 105;
				else if (strKey.equals("Key Pad 0"))
					iKeyCode = 96;
				else if (strKey.equals("Left"))
					iKeyCode = 37;
				else if (strKey.equals("Right"))
					iKeyCode = 39;
				else if (strKey.equals("Up"))
					iKeyCode = 38;
				else if (strKey.equals("Down"))
					iKeyCode = 40;
				else if (strKey.equals("Insert"))
					iKeyCode = 155;
				else if (strKey.equals("Delete"))
					iKeyCode = 127;
				else if (strKey.equals("Home"))
					iKeyCode = 36;
				else if (strKey.equals("End"))
					iKeyCode = 35;
				else if (strKey.equals("Page Up"))
					iKeyCode = 33;
				else if (strKey.equals("Page Down"))
					iKeyCode = 34;
				doTitle();
			}
		}
		else if (event.getSource() == miMinLap)
		{
			String strMinLap = JOptionPane.showInputDialog(this, "Enter a minimum lap time in seconds, or 0 to clear");
			iMinLap = 0;
			while (strMinLap != null && strMinLap.length() > 0)
			{
				try
				{
					iMinLap = Integer.parseInt(strMinLap);
					break;
				}
				catch (NumberFormatException nfe)
				{
					strMinLap = JOptionPane.showInputDialog(this, "Enter a VALID number of seconds (number only)");
				}
			}
			doTitle();
		}
		else if (event.getSource() == miIR)
		{
			irDlg = new IRLearnDialog(JOptionPane.getFrameForComponent(this), getName());
			irDlg.setVisible(true);
			learnedCode = irDlg.getIRCode();
			doTitle();
			irDlg = null;
		}
		else if (event.getSource() == chkMulti)
		{
			if (chkMulti.isSelected())
			{
				btnRider.setVisible(true);
				table.addColumn(tc[0]);
				table.addColumn(tc[1]);
				table.moveColumn(4, 2);
				table.moveColumn(5, 3);
				myModel.setVisible(2, true);
				myModel.setVisible(3, true);
				tdCU.setVisible(true);
			}
			else
			{
				btnRider.setVisible(false);
				table.removeColumn(tc[0]);
				table.removeColumn(tc[1]);
				myModel.setVisible(2, false);
				myModel.setVisible(3, false);
				tdCU.setVisible(false);
			}
		}
		else if (event.getSource() == radNone || event.getSource() == rad10Progress || event.getSource() == rad5Progress || event.getSource() == rad10Alert || event.getSource() == rad5Alert)
			doTitle();

		KeyboardFocusManager.setCurrentKeyboardFocusManager(focus);

		super.actionPerformed(event);
	}

	private void doTitle()
	{
		String strTitle = getName();
		if (strKey != null)
			strTitle = strTitle + " -- HotKey (" + strKey + ")";
		if (iMinLap != 0)
			strTitle = strTitle + " -- MinLap (" + iMinLap + " secs)";
		if (!radNone.isSelected())
			strTitle = strTitle + " -- Sounds On";
		if (learnedCode != null && learnedCode.length() > 0)
			strTitle += " -- IR (" + learnedCode + ")";

		setTitle(strTitle);
	}

	private void doRider()
	{
		long lTemp = System.currentTimeMillis();
		lRiderStart = lTemp;
		iRiderLapNum = 0;
		tdCU.setForeground(Color.black);
	}

	private void doExport()
	{
		JFileChooser chooser = new JFileChooser();
		int returnVal = chooser.showSaveDialog(this);
		if (returnVal == 0)
		{
			boolean bExport = true;
			File file = chooser.getSelectedFile();
			if (file.exists() && JOptionPane.showConfirmDialog(this, "Overwrite existing file", "File exist do you wish to overwrite", 0) != 0)
				bExport = false;
			if (bExport)
				try
				{
					BufferedWriter bw = new BufferedWriter(new FileWriter(file));
					for (int i = 0; i < getColumnNames().length; i++)
					{
						bw.write(getColumnNames()[i]);
						if (i != 5)
							bw.write(",");
					}

					bw.newLine();
					TimeDisplay td = new TimeDisplay();
					td.setDateFormat(new SimpleDateFormat("mm:ss.SSS"));
					td.setForeground(table.getForeground());
					td.setBackground(table.getBackground());
					td.setFont(table.getFont());
					for (int j = 0; j < myModel.data.length; j++)
					{
						for (int i = 0; i < getColumnNames().length; i++)
						{
							Object obj = myModel.data[j][i];
							if (obj instanceof Long)
							{
								td.setTime(Math.abs(((Long) obj).longValue()));
								bw.write(td.toString());
							}
							else
							{
								bw.write(String.valueOf(obj));
							}
							if (i != (getColumnNames().length - 1))
								bw.write(",");
						}

						bw.newLine();
					}

					bw.flush();
					bw.close();
					JOptionPane.showInternalMessageDialog(this, "Data sucessfully exported to " + file.getAbsolutePath() + ".", "Information", 1);
				}
				catch (IOException ioe)
				{
					ioe.printStackTrace();
				}
		}
	}

	private void doStartStop()
	{
		if (bRunning)
			doStop();
		else
			doStart();
	}

	private void doLapReset()
	{
		if (bRunning)
			doLap();
		// else
		// doReset();
	}

	private void doReset()
	{
		lStart = 0L;
		lLap = 0L;
		lRiderStart = 0L;
		iLapNum = 0;
		iRiderLapNum = 0;
		lElapsed = 0L;
		tdLap.setTime(lLap);
		tdCU.setTime(lRiderStart);
		tdRunner.setTime(lStart);
		tdCU.setForeground(Color.black);
		myModel.clear();

		Object row[] = { new Integer(0), new Long(0), new Integer(0), new Long(0), new Long(0), new Long(0), new Boolean(false) };
		for (int i = 0; i < laplisteners.size(); i++)
		{
			((LapListener) laplisteners.get(i)).lap(hashCode(), getName(), row);
		}

	}

	private void doStop()
	{
		bRunning = false;
		if (lStart > 0L)
		{
			lStop = System.currentTimeMillis();
			timer1.stop();
			timer2.stop();
			tdRunner.setTime(lStop - lStart);
			doLap(lStop);
			timer3.stop();
			btnStartStop.setText("Start");
			// btnLapReset.setText("Reset");
			btnLapReset.setEnabled(false);
		}
	}

	private void doStart()
	{
		bRunning = true;
		long lTemp = System.currentTimeMillis();
		if (lStart == 0L)
		{
			lStart = lTemp;
			lLap = lRiderStart = lStart;
			iLapNum = 0;
			iRiderLapNum = 0;
			lElapsed = 0L;
		}
		else
		{
			lStart += lTemp - lStop;
			lLap = lRiderStart = lStart;
		}
		timer1.start();
		timer2.start();
		btnStartStop.setText("Stop");
		// btnLapReset.setText("Lap");
		btnLapReset.setEnabled(true);
	}

	private void doLap()
	{
		doLap(System.currentTimeMillis());
	}

	private boolean doLap(long lTime)
	{
		timer3.stop();
		if (lStart > 0L)
		{
			if (iMinLap > 0 && lTime - lLap < (long) (iMinLap * 1000))
			{
				Toolkit.getDefaultToolkit().beep();
				return false;
			}
			long lLastLap = lElapsed;
			lElapsed = lTime - lLap;
			tdLap.setTime(lElapsed);
			iLapNum++;
			iRiderLapNum++;
			long lDelta;
			if (lLastLap != 0L)
				lDelta = lElapsed - lLastLap;
			else
				lDelta = 0L;
			long lAverage = (lTime - lStart) / (long) iLapNum;
			long lRiderAverage = (lTime - lRiderStart) / (long) iRiderLapNum;
			Object row[] = { new Integer(iLapNum), new Long(lAverage), new Integer(iRiderLapNum), new Long(lRiderAverage), new Long(lDelta), new Long(lElapsed), new Boolean(false) };
			myModel.addRow(row);
			lLap = lTime;

			for (int i = 0; i < laplisteners.size(); i++)
			{
				((LapListener) laplisteners.get(i)).lap(hashCode(), getName(), row);
			}

			int iChime = 0;
			if (!radNone.isSelected())
				if (rad10Progress.isSelected() || rad10Alert.isSelected())
					iChime = 10000;
				else if (rad5Progress.isSelected() || rad5Alert.isSelected())
					iChime = 5000;
			if (iChime > 0 && lElapsed > (long) iChime)
			{
				int iDelay = (int) (lElapsed - (long) iChime);
				timer3.setInitialDelay(iDelay);
				timer3.setDelay(iDelay);
				timer3.start();
			}
		}
		return true;
	}

	public void keyPressed(KeyEvent event)
	{
		if (bRunning && iKeyCode != 0 && event.getKeyCode() == iKeyCode)
			doLap();
	}

	private void createMenu()
	{
		JMenu mOptions = new JMenu("Options");
		mOptions.setMnemonic('O');
		miName = new JMenuItem("Name...");
		miName.setMnemonic('N');
		miName.addActionListener(this);
		chkMulti = new JCheckBoxMenuItem("Multiple Riders");
		chkMulti.setMnemonic('M');
		chkMulti.addActionListener(this);
		mOptions.add(chkMulti);
		JMenu mSounds = new JMenu("Sounds");
		mSounds.setMnemonic('O');
		ButtonGroup bg = new ButtonGroup();
		radNone = new JRadioButton("None");
		radNone.setMnemonic('N');
		radNone.setSelected(true);
		radNone.setBackground(SystemColor.menu);
		radNone.addActionListener(this);
		rad10Progress = new JRadioButton("10 second progressive");
		rad10Progress.setBackground(SystemColor.menu);
		rad10Progress.addActionListener(this);
		rad5Progress = new JRadioButton("5 second progressive");
		rad5Progress.setBackground(SystemColor.menu);
		rad5Progress.addActionListener(this);
		rad10Alert = new JRadioButton("10 second alert");
		rad10Alert.setBackground(SystemColor.menu);
		rad10Alert.addActionListener(this);
		rad5Alert = new JRadioButton("5 second alert");
		rad5Alert.setBackground(SystemColor.menu);
		rad5Alert.addActionListener(this);
		bg.add(radNone);
		bg.add(rad10Progress);
		bg.add(rad5Progress);
		bg.add(rad10Alert);
		bg.add(rad5Alert);
		mSounds.add(radNone);
		mSounds.add(rad10Progress);
		mSounds.add(rad5Progress);
		mSounds.add(rad10Alert);
		mSounds.add(rad5Alert);
		miSave = new JMenuItem("Save...");
		miSave.setMnemonic('S');
		miSave.addActionListener(this);
		miKey = new JMenuItem("Hot Key...");
		miKey.setMnemonic('H');
		miKey.addActionListener(this);
		miIR = new JMenuItem("Learn infrared remote...");
		miIR.setMnemonic('L');
		miIR.addActionListener(this);
		miMinLap = new JMenuItem("Minimum Lap...");
		miMinLap.setMnemonic('M');
		miMinLap.addActionListener(this);
		mOptions.add(miName);
		mOptions.add(mSounds);
		mOptions.add(miMinLap);
		mOptions.add(miKey);
		mOptions.add(miIR);
		mOptions.add(miSave);
		JMenuBar mbMenuBar = new JMenuBar();
		mbMenuBar.add(mOptions);
		setJMenuBar(mbMenuBar);
	}

	public void internalFrameOpened(InternalFrameEvent event)
	{
		doTitle();
	}

	public void irEventReceived(IREvent sde)
	{
		if (irDlg != null)
			irDlg.irEventReceived(sde);

		// does this match my learned signal?
		String code = sde.getEvent().getRemote() + ":" + sde.getEvent().getName();
		// if I get a repeat > than one second after last lap than use it.
		if (bRunning && code.equals(learnedCode) && (sde.getEvent().getRepeat() == 0 || sde.getTime() - lLap > 1000))
		{
			doLap(sde.getTime());
		}
	}

	class TimerListener3 implements ActionListener
	{

		public void actionPerformed(ActionEvent event)
		{
			if (timer3.isRunning())
			{
				timer3.stop();
				long lTemp = System.currentTimeMillis() - lLap;
				long lRange = Math.abs(lElapsed - lTemp);
				if (rad10Progress.isSelected() || rad5Progress.isSelected())
				{
					int iStage1 = rad10Progress.isSelected() ? 10000 : 5000;
					int iStage2 = (int) ((double) iStage1 * 0.75D);
					int iStage3 = (int) ((double) iStage1 * 0.5D);
					int iStage4 = (int) ((double) iStage1 * 0.25D);
					if (lRange <= (long) iStage4)
						timer3.setInitialDelay(250);
					else if (lRange <= (long) iStage3)
						timer3.setInitialDelay(500);
					else if (lRange <= (long) iStage2)
						timer3.setInitialDelay(750);
					else if (lRange <= (long) iStage1)
						timer3.setInitialDelay(1000);
					else
						return;
					if (!radNone.isSelected())
						Toolkit.getDefaultToolkit().beep();
					timer3.start();
				}
				else if (!radNone.isSelected())
					Toolkit.getDefaultToolkit().beep();
			}
		}

		TimerListener3()
		{
		}
	}

	class TimerListener2 implements ActionListener
	{

		public void actionPerformed(ActionEvent event)
		{
			long lTemp = System.currentTimeMillis() - lRiderStart;
			tdCU.setTime(lTemp);
			if (lTemp > 0x36ee80L)
				tdCU.setForeground(Color.red);
			else if (lTemp > 0x2932e0L)
				tdCU.setForeground(Color.blue);
		}

		TimerListener2()
		{
		}
	}

	class TimerListener1 implements ActionListener
	{

		public void actionPerformed(ActionEvent event)
		{
			long lTime = System.currentTimeMillis() - lLap;
			tdRunner.setTime(lTime);
		}

		TimerListener1()
		{
		}
	}

}
