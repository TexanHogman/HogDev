package com.hogdev.racecontrol;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.DefaultFocusManager;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.lirc.LIRCClient;
import org.lirc.LIRCEvent;
import org.lirc.LIRCException;
import org.lirc.LIRCListener;

import com.hogdev.toolbox.ToolBoxPlugin;

public class RaceControl extends JApplet implements WindowListener, ActionListener, MenuListener, Serializable, KeyListener, InternalFrameListener, LIRCListener
{
	private static final long serialVersionUID = 6260430337069521213L;
	public final static int WIDTH = 1024;
	public final static int HEIGHT = 768;
	public static final String START = "Start All (S)";
	public static final String STOP = "Stop All (S)";
	public static final String RESET = "Reset All (R)";
	public static final long SEC = 1000L;
	public static final long MIN = 60000L;
	JDesktopPane dp;
	RacePanel rp;
	JPanel pnlMain;
	JPanel pnlButtons;
	JMenuItem miExit;
	JMenuItem miCloseAll;
	JCheckBoxMenuItem miSummary;
	JMenuItem miClose;
	JMenuItem miMinAll;
	JMenuItem miResAll;
	JMenuItem miLapTimerLoad;
	JMenuItem miLapTimerNew;
	JMenuItem miLapTimerSave;
	JMenuItem miAbout;
	JButton btnStartStop;
	JButton btnReset;
	JCheckBoxMenuItem chkEnable;
	RaceStopWatches summary;
	javax.swing.UIManager.LookAndFeelInfo lafi[];
	ArrayList sdListeners = new ArrayList();
	final static Object lock = new Object();
	LIRCClient client;
	boolean bCTRL;

	public static void main(String args[])
	{
		final JFrame frame = new JFrame();
		final JWindow splash = new JWindow(frame);
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		URL url = RaceControl.class.getResource("splash.jpg");
		if (url != null)
		{
			Image img = Toolkit.getDefaultToolkit().getImage(url);
			splash.getContentPane().add(new JLabel(new ImageIcon(img)));
			splash.pack();
			splash.setLocation(screenSize.width / 2 - (splash.getWidth() / 2), screenSize.height / 2 - (splash.getHeight() / 2));

			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					splash.setVisible(true);
				}
			});
		}

		frame.setTitle("Race Control v2.1");
		RaceControl rs = new RaceControl();
		frame.addWindowListener(rs);
		rs.init();
		frame.getContentPane().add(rs);
		frame.pack();
		frame.setSize(RaceControl.WIDTH, RaceControl.HEIGHT);
		frame.setLocation(screenSize.width / 2 - (frame.getWidth() / 2), screenSize.height / 2 - (frame.getHeight() / 2));
		frame.setVisible(true);

		if(url != null)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					try
					{
						Thread.sleep(1000);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace(); // To change body of catch statement
						// use Options | File Templates.
					}
					splash.setVisible(false);
				}
			});
		}
	}

	public RaceControl()
	{
		DefaultFocusManager dfocus = new DefaultFocusManager();
		KeyboardFocusManager.setCurrentKeyboardFocusManager(dfocus);
		dfocus.addKeyEventDispatcher(new KeyEventDispatcher()
		{
			public boolean dispatchKeyEvent(KeyEvent e)
			{
				switch (e.getID())
				{
				case KeyEvent.KEY_PRESSED:
					KeyListener[] listeners = getKeyListeners();
					for (int i = 0; i < listeners.length; i++)
					{
						listeners[i].keyPressed(e);
					}
					break;
				case KeyEvent.KEY_RELEASED:
					listeners = getKeyListeners();
					for (int i = 0; i < listeners.length; i++)
					{
						listeners[i].keyReleased(e);
					}
					break;
				case KeyEvent.KEY_TYPED:
					listeners = getKeyListeners();
					for (int i = 0; i < listeners.length; i++)
					{
						listeners[i].keyTyped(e);
					}
					break;
				}
				return false;
			}

		});
	}

	public void destroy()
	{
	}

	public void init()
	{
		getContentPane().setLayout(new BorderLayout());
		pnlMain = new JPanel();
		pnlMain.setLayout(new BorderLayout());
		getContentPane().add("Center", pnlMain);
		createMenu();
		createGUI();
		setSize(800, 600);
		loadSettings();
		setVisible(true);
	}

	private void createMenu()
	{
		JMenu mFile = new JMenu("File");
		mFile.setMnemonic('F');
		miLapTimerNew = new JMenuItem("New Timer");
		miLapTimerNew.addActionListener(this);
		miLapTimerNew.setMnemonic('N');
		miLapTimerNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		mFile.add(miLapTimerNew);
		chkEnable = new JCheckBoxMenuItem("Infrared");
		chkEnable.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
		chkEnable.setMnemonic('E');
		chkEnable.addActionListener(this);
		mFile.add(chkEnable);
		miExit = new JMenuItem("Exit");
		miExit.addActionListener(this);
		miExit.setMnemonic('X');
		miExit.setAccelerator(KeyStroke.getKeyStroke(115, InputEvent.ALT_MASK));
		mFile.add(miExit);
		JMenu mWindow = new JMenu("Window");
		mWindow.setMnemonic('W');
		mWindow.addMenuListener(this);
		miSummary = new JCheckBoxMenuItem("Summary");
		miSummary.addActionListener(this);
		miSummary.setMnemonic('L');
		miSummary.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		mWindow.add(miSummary);
		miClose = new JMenuItem("Close");
		miClose.addActionListener(this);
		miClose.setMnemonic('L');
		miClose.setAccelerator(KeyStroke.getKeyStroke(115, InputEvent.CTRL_MASK));
		mWindow.add(miClose);
		miCloseAll = new JMenuItem("Close All");
		miCloseAll.addActionListener(this);
		miCloseAll.setMnemonic('A');
		miCloseAll.setAccelerator(KeyStroke.getKeyStroke(65, InputEvent.CTRL_MASK));
		mWindow.add(miCloseAll);
		miMinAll = new JMenuItem("Minimize All");
		miMinAll.addActionListener(this);
		miMinAll.setMnemonic('M');
		miMinAll.setAccelerator(KeyStroke.getKeyStroke(77, InputEvent.CTRL_MASK));
		mWindow.add(miMinAll);
		miResAll = new JMenuItem("Restore All");
		miResAll.addActionListener(this);
		miResAll.setMnemonic('R');
		miResAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
		mWindow.add(miResAll);
		JMenu menuLAF = new JMenu("Look and Feel");
		menuLAF.setMnemonic('L');
		ButtonGroup bg = new ButtonGroup();
		lafi = UIManager.getInstalledLookAndFeels();
		JRadioButtonMenuItem def = null;
		for (int i = 0; i < lafi.length; i++)
		{
			JRadioButtonMenuItem rb = new JRadioButtonMenuItem(lafi[i].getName());
			rb.addActionListener(this);
			if (UIManager.getSystemLookAndFeelClassName().equals(lafi[i].getClassName()))
				def = rb;
			rb.setMnemonic(lafi[i].getName().charAt(0));
			bg.add(rb);
			menuLAF.add(rb);
		}
		mWindow.add(menuLAF);

		miLapTimerLoad = new JMenuItem("Load...");
		miLapTimerLoad.addActionListener(this);
		miLapTimerLoad.setMnemonic('L');
		miLapTimerLoad.setAccelerator(KeyStroke.getKeyStroke(76, 2));
		miLapTimerSave = new JMenuItem("Save...");
		miLapTimerSave.addActionListener(this);
		miLapTimerSave.setMnemonic('S');
		miLapTimerSave.setAccelerator(KeyStroke.getKeyStroke(83, 2));
		JMenu mHelp = new JMenu("Help");
		mHelp.setMnemonic('H');
		miAbout = new JMenuItem("About...");
		miAbout.addActionListener(this);
		miAbout.setMnemonic('U');
		miAbout.setAccelerator(KeyStroke.getKeyStroke(85, 2));
		mHelp.add(miAbout);
		JMenuBar mbMenuBar = new JMenuBar();
		mbMenuBar.add(mFile);
		// mbMenuBar.add(mView);
		mbMenuBar.add(mWindow);
		// mbMenuBar.add(mLapTimer);
		mbMenuBar.add(mHelp);
		setJMenuBar(mbMenuBar);
		if (def != null)
			def.doClick();
	}

	private void createGUI()
	{
		dp = new JDesktopPane();
		URL url = RaceControl.class.getResource("background.jpg");
		if (url != null)
		{
			Image img = Toolkit.getDefaultToolkit().getImage(url);
			final ImageIcon ii = new ImageIcon(img);
			final ImageDisplay id = new ImageDisplay(ii.getImage());
			dp.add(id, JLayeredPane.FRAME_CONTENT_LAYER);
			id.setSize(id.getPreferredSize());

			dp.addComponentListener(new ComponentListener()
			{
				public void componentResized(final ComponentEvent e)
				{
					int height = e.getComponent().getHeight();
					int width = e.getComponent().getWidth();
					id.setSize(width, height);
				}

				public void componentHidden(ComponentEvent e)
				{
				}

				public void componentShown(ComponentEvent e)
				{
				}

				public void componentMoved(ComponentEvent e)
				{
				}
			});
		}

		rp = new RacePanel();
		pnlMain.add("North", rp);
		btnStartStop = new JButton("Start All (S)");
		btnStartStop.addActionListener(this);
		btnStartStop.addActionListener(rp);
		btnReset = new JButton("Reset All (R)");
		btnReset.addActionListener(this);
		btnReset.addActionListener(rp);
		pnlButtons = new JPanel();
		pnlButtons.add(btnStartStop);
		pnlButtons.add(btnReset);
		pnlMain.add("Center", dp);
		pnlMain.add("South", pnlButtons);

		addKeyListener(this);

		summary = new RaceStopWatches();
		summary.addInternalFrameListener(this);
		dp.add(summary);
		summary.setVisible(false);
	}

	public void windowClosed(WindowEvent windowevent)
	{
	}

	public void windowIconified(WindowEvent windowevent)
	{
	}

	public void windowDeactivated(WindowEvent windowevent)
	{
	}

	public void windowDeiconified(WindowEvent windowevent)
	{
	}

	public void windowActivated(WindowEvent windowevent)
	{
	}

	public void windowClosing(WindowEvent event)
	{
		miExit.doClick();
	}

	public void windowOpened(WindowEvent event)
	{
	}

	public void actionPerformed(ActionEvent event)
	{
		String strCommand = event.getActionCommand();
		if (event.getSource() == chkEnable)
		{
			if (chkEnable.isSelected())
			{
				try
				{
					if (client == null)
					{
						client = new LIRCClient();
					}
					client.addLIRCListener(this);
				}
				catch (LIRCException e)
				{
					JOptionPane.showMessageDialog(this, "Error occured communicating with LIRC.  Is the native application installed?", "IR Error", JOptionPane.ERROR_MESSAGE);
					chkEnable.setSelected(false);
				}
			}
			else
			{
				if (client != null)
					client.removeLIRCListener(this);
			}
		}
		else if (event.getSource() == btnStartStop)
		{
			if (btnStartStop.getText().equals("Start All (S)"))
			{
				btnStartStop.setText("Stop All (S)");
				btnReset.setEnabled(false);
				java.awt.Component comp[] = pnlButtons.getComponents();
				for (int i = 0; i < comp.length && (comp[i] instanceof JButton); i++)
				{
					JButton button = (JButton) comp[i];
					if (button.getText().startsWith("Lap"))
						button.setEnabled(true);
				}

			}
			else
			{
				btnStartStop.setText("Start All (S)");
				btnReset.setEnabled(true);
				java.awt.Component comp[] = pnlButtons.getComponents();
				for (int i = 0; i < comp.length && (comp[i] instanceof JButton); i++)
				{
					JButton button = (JButton) comp[i];
					if (button.getText().startsWith("Lap"))
						button.setEnabled(false);
				}

			}
		}
		else if (event.getSource() != btnReset)
			if (event.getSource() == miExit)
			{
				if (saveSettings())
					System.exit(0);
			}
			else if (event.getSource() == miSummary)
			{
				if (miSummary.isSelected())
				{
					summary.setVisible(true);
				}
				else
				{
					summary.setVisible(false);
				}
			}
			else if (event.getSource() == miClose)
			{
				JInternalFrame frames[] = dp.getAllFrames();
				if (frames.length > 0)
				{
					if(frames[0] instanceof RaceStopWatches)
						miSummary.doClick();						
					else
						frames[0].dispose();
				}
			}
			else if (event.getSource() == miCloseAll)
			{
				JInternalFrame frames[] = dp.getAllFrames();
				for (int i = 0; i < frames.length; i++)
				{
					if(frames[i] instanceof RaceStopWatches)
						miSummary.doClick();						
					else
						frames[i].dispose();
				}
			}
			else if (event.getSource() == miMinAll)
			{
				JInternalFrame frames[] = dp.getAllFrames();
				for (int i = 0; i < frames.length; i++)
					try
					{
						if (!frames[i].isIcon())
							frames[i].setIcon(true);
					}
					catch (Exception e)
					{
					}

			}
			else if (event.getSource() == miResAll)
			{
				JInternalFrame frames[] = dp.getAllFrames();
				for (int i = 0; i < frames.length; i++)
					try
					{
						if (frames[i].isIcon())
							frames[i].setIcon(false);
					}
					catch (Exception e)
					{
					}

			}
			else if (event.getSource() == miAbout)
				JOptionPane.showMessageDialog(this, "This application was designed and developed by Rick Hogge.\nFor purchasing information please email rick.hogge@gmail.com.", "About Race Control",
						JOptionPane.INFORMATION_MESSAGE);
			else if (event.getSource() == miLapTimerNew)
			{
				try
				{
					RaceStopWatch pi = new RaceStopWatch(chkEnable.isSelected());
					pi.addInternalFrameListener(this);
					chkEnable.addActionListener(pi);
					Object row[] = { new Integer(0), new Long(0), new Integer(0), new Long(0), new Long(0), new Long(0), new Boolean(false) };
					summary.lap(pi.hashCode(), pi.getName(), row);
					pi.addLapListener(summary);
					pi.addPropertyChangeListener(summary);
					dp.add(pi);
					pi.setVisible(true);
					if (!btnStartStop.getText().equals("Start All (S)"))
						pi.actionPerformed(new ActionEvent(btnStartStop, 1001, "Start All (S)"));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			else if (event.getSource() != miLapTimerLoad && event.getSource() != miLapTimerSave)
			{
				for (int i = 0; i < lafi.length; i++)
				{
					if (!strCommand.equals(lafi[i].getName()))
						continue;
					try
					{
						UIManager.setLookAndFeel(lafi[i].getClassName());
						SwingUtilities.updateComponentTreeUI(this);
					}
					catch (Exception e)
					{
					}
					break;
				}

			}
	}

	public void menuCanceled(MenuEvent menuevent)
	{
	}

	public void menuDeselected(MenuEvent menuevent)
	{
	}

	public void menuSelected(MenuEvent menuevent)
	{
	}

	private boolean loadSettings()
	{
		return true;
	}

	private boolean saveSettings()
	{
		return true;
	}

	public void keyReleased(KeyEvent event)
	{
		if (event.getKeyCode() == KeyEvent.VK_CONTROL)
			bCTRL = false;
		
		if(bCTRL) return;
		
		switch (event.getKeyCode())
		{
		case KeyEvent.VK_S: // 'S'
			btnStartStop.doClick();
			break;

		case KeyEvent.VK_R:
			btnReset.doClick();
			break;
		}
	}

	public void keyPressed(KeyEvent keyevent)
	{
		if (keyevent.getKeyCode() == KeyEvent.VK_CONTROL)
			bCTRL = true;
	}

	public void keyTyped(KeyEvent keyevent)
	{
	}

	public void internalFrameClosed(InternalFrameEvent event)
	{
		Class cls = event.getInternalFrame().getClass();
		if (cls == RaceStopWatches.class)
		{

		}
		else
		{
			ToolBoxPlugin plugin = (ToolBoxPlugin) event.getSource();
			btnStartStop.removeActionListener(plugin);
			btnReset.removeActionListener(plugin);
			removeKeyListener(plugin);
			removeIREventListener((IREventListener) plugin);

			// remove from Summary screen
			summary.remove(plugin.hashCode());
		}
		requestFocus();
	}

	public void internalFrameIconified(InternalFrameEvent internalframeevent)
	{
	}

	public void internalFrameDeactivated(InternalFrameEvent internalframeevent)
	{
	}

	public void internalFrameDeiconified(InternalFrameEvent internalframeevent)
	{
	}

	public void internalFrameActivated(InternalFrameEvent internalframeevent)
	{
	}

	public void internalFrameClosing(InternalFrameEvent internalframeevent)
	{
	}

	public void internalFrameOpened(InternalFrameEvent event)
	{
		Class cls = event.getInternalFrame().getClass();
		if (cls == RaceStopWatches.class)
		{

		}
		else
		{
			ToolBoxPlugin plugin = (ToolBoxPlugin) event.getSource();
			btnStartStop.addActionListener(plugin);
			btnReset.addActionListener(plugin);
			addKeyListener(plugin);
			addIREventListener((IREventListener) plugin);
		}
		requestFocus();
	}

	private void addIREventListener(IREventListener listener)
	{
		sdListeners.add(listener);
	}

	private void removeIREventListener(IREventListener listener)
	{
		sdListeners.remove(listener);
	}

	public void received(LIRCEvent event)
	{
		IREvent irevent = new IREvent(System.currentTimeMillis(), event);
		for (int i = 0; i < sdListeners.size(); i++)
			((IREventListener) sdListeners.get(i)).irEventReceived(irevent);
	}

}
