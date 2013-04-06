package com.hogdev.toolbox.plugins;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.Date;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.InternalFrameEvent;

import com.hogdev.toolbox.ToolBoxPlugin;
import com.hogdev.util.SearchAndReplaceOld;

public class SaR extends ToolBoxPlugin
{
    /**
     * 
     */
    private static final long serialVersionUID = 8124842254021489499L;

    SearchAndReplaceOld searchReplace;

    JTextArea ta;

    JScrollPane sp;

    JButton btnSearch;

    JButton btnReplace;

    JButton btnStop;

    JButton btnBrowse;

    JButton btnClear;

    JTextField tfFileName;

    JTextField tfSearch;

    JTextField tfReplace;

    JTextField tfStartIn;

    JCheckBox ckRecurse;

    JCheckBox ckMatchCase;

    public static void main(String[] args)
    {
    }

    public SaR()
    {
        super();
        searchReplace = new SearchAndReplaceOld();
        searchReplace.addPropertyChangeListener(this);
    }

    public JPanel getGUI()
    {
        setTitle("Search and Replace");
        setClosable(true);
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);

        JPanel pnl = new JPanel();
        pnl.setLayout(new BorderLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        GridBagLayout gbl = new GridBagLayout();

        JLabel lblFileName = new JLabel("Named:");
        tfFileName = new JTextField(10);
        ckRecurse = new JCheckBox("Include subfolders");
        ckMatchCase = new JCheckBox("Match Case");
        tfFileName.addKeyListener(this);
        JLabel lblSearch = new JLabel("Find:");
        tfSearch = new JTextField(10);
        tfSearch.addKeyListener(this);
        JLabel lblReplace = new JLabel("Replace:");
        tfReplace = new JTextField(10);
        btnBrowse = new JButton("...");
        btnBrowse.setMargin(new Insets(0, 0, 0, 0));
        btnBrowse.addActionListener(this);
        JLabel lblStartIn = new JLabel("Start In:");
        tfStartIn = new JTextField(10);

        JPanel pnlFields = new JPanel(gbl);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(3, 3, 3, 3);

        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbl.setConstraints(lblFileName, gbc);
        pnlFields.add(lblFileName);

        gbc.weightx = 1;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbl.setConstraints(tfFileName, gbc);
        pnlFields.add(tfFileName);

        gbc.weightx = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbl.setConstraints(ckMatchCase, gbc);
        pnlFields.add(ckMatchCase);

        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbl.setConstraints(lblSearch, gbc);
        pnlFields.add(lblSearch);

        gbc.weightx = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbl.setConstraints(tfSearch, gbc);
        pnlFields.add(tfSearch);

        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbl.setConstraints(lblReplace, gbc);
        pnlFields.add(lblReplace);

        gbc.weightx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbl.setConstraints(tfReplace, gbc);
        pnlFields.add(tfReplace);

        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbl.setConstraints(lblStartIn, gbc);
        pnlFields.add(lblStartIn);

        gbc.weightx = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbl.setConstraints(tfStartIn, gbc);
        pnlFields.add(tfStartIn);

        gbc.weightx = 0;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbl.setConstraints(btnBrowse, gbc);
        pnlFields.add(btnBrowse);

        gbc.weightx = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbl.setConstraints(ckRecurse, gbc);
        pnlFields.add(ckRecurse);

        ta = new JTextArea();
        ta.setEditable(false);

        sp = new JScrollPane(ta);

        btnSearch = new JButton("Search");
        btnSearch.addActionListener(this);

        btnReplace = new JButton("Replace");
        btnReplace.addActionListener(this);

        btnStop = new JButton("Stop");
        btnStop.addActionListener(this);

        btnClear = new JButton("Clear");
        btnClear.addActionListener(this);

        JPanel pnlButtons = new JPanel();
        pnlButtons.add(btnSearch);
        pnlButtons.add(btnReplace);
        pnlButtons.add(btnStop);
        pnlButtons.add(btnClear);

        pnl.add("North", pnlFields);
        pnl.add("Center", sp);
        pnl.add("South", pnlButtons);

        initFields();

        return pnl;
    }

    public void propertyChange(PropertyChangeEvent evt)
    {
        if (evt.getPropertyName().equals("info"))
        {
            ta.append((String) evt.getNewValue() + "\n");
        }
        else if (evt.getPropertyName().equals("status"))
        {
            if (evt.getNewValue().equals("Starting"))
            {
                btnSearch.setEnabled(false);
                btnReplace.setEnabled(false);
                btnClear.setEnabled(false);
                btnStop.setEnabled(true);
                ta.append("Search start." + new Date() + "\n");
            }
            else if (evt.getNewValue().equals("Stopping"))
            {
                btnSearch.setEnabled(true);
                btnReplace.setEnabled(true);
                btnClear.setEnabled(true);
                btnStop.setEnabled(false);
                ta.append("Search end." + new Date() + "\n");
                tfSearch.addKeyListener(this);
            }
            else
            {
                //				ta.append( (String)evt.getNewValue() + "\n" );
            }
        }
        super.propertyChange(evt);
    }

    public void actionPerformed(ActionEvent event)
    {
        if (event.getSource() == btnSearch || event.getSource() == btnReplace)
        {
            tfSearch.removeKeyListener(this);
            ta.setText("");

            searchReplace.setSearch(tfSearch.getText());
            searchReplace.setReplace(tfReplace.getText());
            searchReplace.setStartLocation(tfStartIn.getText());
            searchReplace.setRecurse(ckRecurse.isSelected());
            searchReplace.setMatchCase(ckMatchCase.isSelected());
            searchReplace.setPattern(tfFileName.getText());
            //			searchReplace.setVerbose( true );
            if (event.getSource() == btnSearch)
                searchReplace.startSearch();
            else if (event.getSource() == btnReplace)
                searchReplace.startReplace();
        }
        else if (event.getSource() == btnStop)
        {
            if (searchReplace != null)
            {
                ta.append("Stopping search, Please Wait..." + "\n");
                searchReplace.stop();
            }
        }
        else if (event.getSource() == btnBrowse)
        {
            //            JFileChooser fc = new JFileChooser();
            //			int retval = fc.showOpenDialog(null);
            //			if (retval == JFileChooser.APPROVE_OPTION)
            //			{
            //				File myFile = fc.getSelectedFile();
            //			}
        }
        else if (event.getSource() == btnClear)
        {
            initFields();
        }
        super.actionPerformed(event);
    }

    public void internalFrameClosing(InternalFrameEvent event)
    {
        if (btnStop.isEnabled())
            btnStop.doClick();

        super.internalFrameClosing(event);
    }

    public void keyReleased(KeyEvent event)
    {
        if (event.getSource() == tfSearch)
        {
            if (tfSearch.getText() != null && tfSearch.getText().length() > 0)
            {
                btnSearch.setEnabled(true);
                btnReplace.setEnabled(true);
            }
            else
            {
                btnSearch.setEnabled(false);
                btnReplace.setEnabled(false);
            }
        }
        super.keyReleased(event);
    }

    private void initFields()
    {
        tfSearch.setText("");
        tfReplace.setText("");
        tfFileName.setText("*.*");
        ckRecurse.setSelected(false);
        ckMatchCase.setSelected(false);
        // figure out the current directory
        String strStartIn = new File(".").getAbsolutePath();
        strStartIn = strStartIn.substring(0, strStartIn
                .lastIndexOf(File.separator));
        tfStartIn.setText(strStartIn);
        ta.setText("");
        btnSearch.setEnabled(false);
        btnReplace.setEnabled(false);
        btnStop.setEnabled(false);
    }

    public static Boolean isSingleInstance()
    {
        return new Boolean(false);
    }

    public static String getMenuText()
    {
        return "Search and Replace";
    }

    public static Icon getToolBarIcon()
    {
        return new ImageIcon("search.gif");
    }

    public Dimension getSize()
    {
        return new Dimension(400, 300);
    }

}