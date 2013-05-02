package com.hogdev.toolbox;

import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import com.hogdev.widgets.StatusPanel;

public class ToolBox extends JFrame implements WindowListener, ActionListener,
        MenuListener, Serializable, KeyListener
{
    /**
     * 
     */
    private static final long serialVersionUID = -739490729002629363L;

    public final static int INITIAL_WIDTH = 400;

    public final static int INITIAL_HEIGHT = 200;

    JDesktopPane dp;

    JMenuItem miExit;

    JMenuItem miCloseAll;

    JMenuItem miClose;

    JMenuItem miMinAll;

    JMenu mWindow;

    JMenu mPlugins;

    JMenuItem miPluginNew;

    JPanel pnlMain;

    JCheckBoxMenuItem cbmiStatus;

    JToolBar tb;

    StatusPanel pnlStatus;

    HashMap hmClasses;

    HashMap hmButtonMenuXREF;

    LookAndFeelInfo[] lafi;

    public static void main(String[] args)
    {
        ToolBox rs = new ToolBox();
    }

    public ToolBox()
    {
        super();

        setTitle("Tool Box");

        addWindowListener(this);

        getContentPane().setLayout(new BorderLayout());

        pnlMain = new JPanel();
        pnlMain.setLayout(new BorderLayout());
        getContentPane().add("Center", pnlMain);

        createMenu();
        createToolBar();
        createGUI();

        pnlStatus = new StatusPanel();
        pnlMain.add("South", pnlStatus);

        pack();
        setSize(500, 500);

        loadSettings();

        show();

        hmClasses = new HashMap();
        hmButtonMenuXREF = new HashMap();

        loadPlugins();
    }

    private void createMenu()
    {
        addKeyListener(this);
        // FILE MENU
        JMenu mFile = new JMenu("File");
        mFile.setMnemonic('F');

        miExit = new JMenuItem("Exit");
        miExit.addActionListener(this);
        miExit.setMnemonic('X');
        miExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4,
                Event.ALT_MASK));
        mFile.add(miExit);

        // VIEW MENU

        JMenu mView = new JMenu("View");
        mView.setMnemonic('V');

        cbmiStatus = new JCheckBoxMenuItem("Status Bar");
        cbmiStatus.addActionListener(this);
        cbmiStatus.setMnemonic('S');
        cbmiStatus.setSelected(true);
        mView.add(cbmiStatus);

        mView.add(new JSeparator());

        JMenu menuLAF = new JMenu("Look and Feel");
        menuLAF.setMnemonic('L');

        ButtonGroup bg = new ButtonGroup();

        // get installed look and feels
        lafi = UIManager.getInstalledLookAndFeels();
        for (int i = 0; i < lafi.length; i++)
        {
            JRadioButtonMenuItem rb = new JRadioButtonMenuItem(lafi[i]
                    .getName());
            if (UIManager.getLookAndFeel().getName().equals(lafi[i].getName()))
                rb.setSelected(true);
            rb.addActionListener(this);
            rb.setMnemonic(lafi[i].getName().charAt(0));
            bg.add(rb);
            menuLAF.add(rb);
        }

        mView.add(menuLAF);

        // WINDOW MENU
        mWindow = new JMenu("Window");
        mWindow.setMnemonic('W');
        mWindow.addMenuListener(this);
        miClose = new JMenuItem("Close ");
        miClose.addActionListener(this);
        miClose.setMnemonic('L');
        miClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4,
                Event.CTRL_MASK));
        mWindow.add(miClose);

        miCloseAll = new JMenuItem("Close All");
        miCloseAll.addActionListener(this);
        miCloseAll.setMnemonic('A');
        miCloseAll.setAccelerator(KeyStroke.getKeyStroke('A', Event.CTRL_MASK));
        mWindow.add(miCloseAll);

        miMinAll = new JMenuItem("Minimize All");
        miMinAll.addActionListener(this);
        miMinAll.setMnemonic('M');
        miMinAll.setAccelerator(KeyStroke.getKeyStroke('M', Event.CTRL_MASK));
        mWindow.add(miMinAll);

        JMenuBar mbMenuBar = new JMenuBar();
        mbMenuBar.add(mFile);
        mbMenuBar.add(mView);
        mbMenuBar.add(mWindow);
        setJMenuBar(mbMenuBar);
        // load plugins

        // PLUGIN MENU
        mPlugins = new JMenu("Plugins");
        mPlugins.setMnemonic('P');

        miPluginNew = new JMenuItem("New");
        miPluginNew.addActionListener(this);
        miPluginNew.setMnemonic('N');
        miPluginNew
                .setAccelerator(KeyStroke.getKeyStroke('N', Event.CTRL_MASK));
        mPlugins.add(miPluginNew);

        mPlugins.add(new JSeparator());

        mbMenuBar.add(mPlugins);

    }

    private void createGUI()
    {
        dp = new JDesktopPane();
        pnlMain.add("Center", dp);
    }

    public void windowClosed(WindowEvent event)
    {
    }

    public void windowIconified(WindowEvent event)
    {
    }

    public void windowDeactivated(WindowEvent event)
    {
    }

    public void windowDeiconified(WindowEvent event)
    {
    }

    public void windowActivated(WindowEvent event)
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
        if (event.getSource() == miExit)
        {
            //save state information
            if (saveSettings())
                System.exit(0);
        }
        else if (event.getSource() == cbmiStatus)
        {
            if (cbmiStatus.isSelected())
            {
                pnlStatus.setVisible(true);
            }
            else
            {
                pnlStatus.setVisible(false);
            }
        }
        else if (event.getSource() == miClose)
        {
            JInternalFrame[] frames = dp.getAllFrames();
            if (frames.length > 0)
            {
                ((JInternalFrame) frames[0]).dispose();
                setStatus(((JInternalFrame) frames[0]).getTitle()
                        + " has been closed.");
            }
        }
        else if (event.getSource() == miCloseAll)
        {
            JInternalFrame[] frames = dp.getAllFrames();
            for (int i = 0; i < frames.length; i++)
            {
                setStatus(((JInternalFrame) frames[i]).getTitle()
                        + " has been closed.");
                frames[i].dispose();
            }
        }
        else if (event.getSource() == miMinAll)
        {
            JInternalFrame[] frames = dp.getAllFrames();
            for (int i = 0; i < frames.length; i++)
            {
                try
                {
                    if (!frames[i].isIcon())
                    {
                        frames[i].setIcon(true);
                        setStatus(((JInternalFrame) frames[i]).getTitle()
                                + " has been minimized.");
                    }
                }
                catch (Exception e)
                {
                }
            }
        }
        else if (event.getSource() == miPluginNew)
        {
            String inputValue = JOptionPane
                    .showInputDialog("Please input the plugin class name");
            if (inputValue != null)
            {
                try
                {
                    Class coToolBoxPlugin = Class.forName("ToolBoxPlugin");
                    Class clPlugin = Class.forName(inputValue);
                    if (coToolBoxPlugin.isAssignableFrom(clPlugin))
                    {
                        setStatus(clPlugin.getName());
                        Method method = clPlugin.getMethod("getMenuText", null);
                        Method method2 = clPlugin.getMethod("getToolBarIcon",
                                null);
                        String strName = (String) method.invoke(clPlugin, null);
                        JMenuItem mi = new JMenuItem(strName);
                        mi.setMnemonic(strName.charAt(0));
                        mi.setAccelerator(KeyStroke.getKeyStroke(strName
                                .charAt(0), Event.CTRL_MASK));
                        mi.addActionListener(this);
                        mPlugins.add(mi);

                        ImageIcon image = (ImageIcon) method2.invoke(clPlugin,
                                null);
                        if (image != null)
                        {
                            JButton button = new JButton(image);
                            button.setToolTipText(strName);
                            button.addActionListener(this);
                            tb.add(button);

                            // I will map the button to the right menuitem
                            hmButtonMenuXREF.put(button, mi);
                        }

                        hmClasses.put(strName, clPlugin);
                    }
                }
                catch (Exception e)
                {
                    System.out.println(e);
                }
            }
        }
        else if (event.getSource() instanceof JButton)
        {
            JMenuItem mi = (JMenuItem) hmButtonMenuXREF.get(event.getSource());
            if (mi != null)
                mi.doClick();
        }
        // always make this the else
        else
        {
            // first see if the user selected a plugin
            String strCommand = event.getActionCommand();
            Class clPlugin = (Class) hmClasses.get(strCommand);
            if (clPlugin != null)
            {
                try
                {
                    Method method = clPlugin
                            .getMethod("isSingleInstance", null);
                    Boolean bSingle = (Boolean) method.invoke(clPlugin, null);
                    boolean bFound = false;
                    if (bSingle.booleanValue())
                    {
                        JInternalFrame[] frames = dp.getAllFrames();
                        for (int i = 0; i < frames.length; i++)
                        {
                            if (clPlugin.isInstance(frames[i]))
                            {
                                if (frames[i].isIcon())
                                {
                                    frames[i].setIcon(false);
                                }
                                frames[i].setSelected(true);
                                setStatus("Existing " + strCommand
                                        + " found and selected.");
                                bFound = true;
                                break;
                            }
                        }
                    }
                    // either I did not find it our it is not a single instance
                    // plug in
                    if (!bFound)
                    {
                        // must need to create one
                        ToolBoxPlugin pi = (ToolBoxPlugin) clPlugin
                                .newInstance();
                        dp.add(pi);
                        pi.setVisible(true);
                        pi.setSelected(true);
                        setStatus("New " + strCommand + " created.");
                    }
                }
                catch (Exception e)
                {
                    setStatus("Could not create instance of " + strCommand);
                }
            }
            else
            {
                for (int i = 0; i < lafi.length; i++)
                {
                    if (strCommand.equals(lafi[i].getName()))
                    {
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
        }
    }

    public void menuCanceled(MenuEvent event)
    {
    }

    public void menuDeselected(MenuEvent event)
    {
    }

    public void menuSelected(MenuEvent event)
    {
        if (event.getSource() == mWindow)
        {
            if (dp.getComponentCountInLayer(dp.highestLayer()) > 0)
            {
                miClose.setEnabled(true);
                miCloseAll.setEnabled(true);
                miMinAll.setEnabled(true);
            }
            else
            {
                miClose.setEnabled(false);
                miCloseAll.setEnabled(false);
                miMinAll.setEnabled(false);
            }
        }
    }

    private void createToolBar()
    {
        tb = new JToolBar();
        tb.setFloatable(false);
        pnlMain.add("North", tb);
    }

    private void loadPlugins()
    {
        setStatus("Loading plugins.");
        //        File curDir = new File(".");
        //        String[] saFiles = curDir.list();
        String[] saFiles = { "CountDownClock.class", "JDBCPlusPI.class",
                "Nothing.class", "Clock.class", "RaceControl.class",
                "SaR.class", "StopWatch.class" };
        try
        {
            Class coToolBoxPlugin = Class
                    .forName("com.hogdev.toolbox.ToolBoxPlugin");
            pnlStatus.setCompleteValue(saFiles.length);
            for (int i = 0; i < saFiles.length; i++)
            {
                pnlStatus.incrementValue();
                if (saFiles[i].endsWith(".class"))
                {
                    try
                    {
                        Class clPlugin = Class
                                .forName("com.hogdev.toolbox.plugins."
                                        + saFiles[i].substring(0, saFiles[i]
                                                .lastIndexOf(".class")));
                        if (clPlugin != coToolBoxPlugin
                                && coToolBoxPlugin.isAssignableFrom(clPlugin))
                        {
                            setStatus(clPlugin.getName());
                            Method method = clPlugin.getMethod("getMenuText",
                                    null);
                            Method method2 = clPlugin.getMethod(
                                    "getToolBarIcon", null);
                            String strName = (String) method.invoke(clPlugin,
                                    null);
                            JMenuItem mi = new JMenuItem(strName);
                            mi.setMnemonic(strName.charAt(0));
                            mi.setAccelerator(KeyStroke.getKeyStroke(strName
                                    .charAt(0), Event.CTRL_MASK));
                            mi.addActionListener(this);
                            mPlugins.add(mi);

                            ImageIcon image = (ImageIcon) method2.invoke(
                                    clPlugin, null);
                            if (image != null)
                            {
                                JButton button = new JButton(image);
                                button.setToolTipText(strName);
                                button.addActionListener(this);
                                tb.add(button);

                                // I will map the button to the right menuitem
                                hmButtonMenuXREF.put(button, mi);
                            }

                            hmClasses.put(strName, clPlugin);
                        }
                    }
                    catch (Exception e)
                    {
                        System.out.println(e);
                    }
                }
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        setStatus(hmClasses.size() + " plugin(s) loaded.");
        pnlStatus.clearProgressBar();
    }

    private void setStatus(String strStatus)
    {
        pnlStatus.setStatus(strStatus);
    }

    private boolean loadSettings()
    {
        return true;
    }

    private boolean saveSettings()
    {
        return (true);
    }

    public void keyReleased(KeyEvent event)
    {
        System.out.println(event);
    }

    public void keyPressed(KeyEvent event)
    {
        System.out.println(event);
    }

    public void keyTyped(KeyEvent event)
    {
    }
}