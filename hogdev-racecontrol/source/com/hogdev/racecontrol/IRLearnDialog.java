package com.hogdev.racecontrol;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.lirc.LIRCEvent;

/* 1.4 example used by DialogDemo.java. */
class IRLearnDialog extends JDialog implements ActionListener, IREventListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JButton btnOk;
	JButton btnCancel;
	JTextArea taInd;
	JLabel lbl;
	byte[] data;
	int count = 0;
	LIRCEvent event;

	public IRLearnDialog(Frame parent, String teamName)
	{
		super(parent, true);
		setTitle("IR Learn for " + teamName);
		getContentPane().setLayout(new BorderLayout());
		JPanel pnlMain = new JPanel();
		pnlMain.setLayout(new BorderLayout());
		getContentPane().add("Center", pnlMain);
		JPanel pnlNorth = new JPanel();
		pnlNorth.setLayout(new FlowLayout());
		lbl = new JLabel("Ready to receive infrared signal");
		pnlNorth.add(lbl);
		pnlMain.add("North", pnlNorth);
		btnOk = new JButton("Ok");
		btnOk.setEnabled(false);
		btnCancel = new JButton("Cancel");
		btnOk.addActionListener(this);
		btnCancel.addActionListener(this);
		JPanel pnlButtons = new JPanel();
		pnlButtons.add(btnOk);
		pnlButtons.add(btnCancel);
		taInd = new JTextArea();
		taInd.setBackground(Color.BLACK);
		taInd.setForeground(Color.GREEN);
		taInd.setFont(new Font("Courier", 1, 10));
		taInd.setEditable(false);
		taInd.setColumns(50);
		taInd.setRows(15);
		// taInd.setLineWrap(true);
		JScrollPane sp = new JScrollPane(taInd, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pnlMain.add("Center", sp);

		pnlMain.add("South", pnlButtons);
		pack();
		// setSize(200, 125);
		setLocationRelativeTo(parent);
	}

	/** This method handles events for the text field. */
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == btnOk)
		{
			setVisible(false);
		}
		else if (e.getSource() == btnCancel)
		{
			event = null;
			setVisible(false);
		}
	}

	public void irEventReceived(IREvent sde)
	{
		taInd.append(sde.getEvent().toString() + "\n");
		taInd.setCaretPosition(taInd.getDocument().getLength());

		if (!btnOk.isEnabled())
		{
			lbl.setText("Learning signal.......");
		}

		// do I have enough to make a unique signal?
		if (++count >= 10)
		{
			lbl.setText("Signal learned!");
			btnOk.setEnabled(true);
			event = sde.getEvent();
		}
	}

	public String getIRCode()
	{
		String ret = null;
		if (event != null)
		{
			ret = event.getRemote() + ":" + event.getName();
		}
		return ret;

	}
}
