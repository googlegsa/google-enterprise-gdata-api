/* Copyright (c) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package sample.dashboard;

import com.google.enterprise.apis.client.GsaClient;
import com.google.enterprise.apis.client.GsaEntry;
import com.google.enterprise.apis.client.Terms;
import com.google.gdata.util.ServiceException;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 * Main form form the multiple GSA dashboard.
 * 
 * The main form consist of a grid, which is used to both control and display
 * a list of GData-enabled GSAs.
 * The bank of buttons and input boxes under the grid is used to issue commands
 * to the GSAs which were selected in the "SELECTED" column of the main grid.
 * 
 * 
 */
public class DashMainForm extends JFrame {

  private static final int[] COLUMN_SIZES = {300, 60, 60, 100, 100, 100, 100};
  private static final String[] COLUMN_HEADERS = {"GSA Hostname/IP", "Select", "Pause",
                                                  "Max Doc Limit", "Found Docs", "Served Docs",
                                                  "Crawl Rate"};
  private static final Logger logger = Logger.getLogger(
      DashMainForm.class.getName());

  /** The containers for the visual controls */
  private JFrame frame;
  private Container pane;
  private JPanel tablePanel = new JPanel();

  /** The layout managers and their constraint classes */
  private GridBagLayout layout;
  private GridBagConstraints constraints;

  /** The visual controls */
  private JTable listTable = new JTable(200, 300);
  private JButton buttonAddServer = new JButton("Add Server");
  private JButton buttonPauseCrawl = new JButton("Pause");
  private JButton buttonResumeCrawl = new JButton("Resume");
  private JButton buttonResetIndex = new JButton("Reset");
  private JButton buttonRemoveClient = new JButton("Remove");
  private JButton buttonSelectAll = new JButton("All");
  private JButton buttonSelectNone = new JButton("None");
  private JButton buttonUpdateMaxUrl = new JButton("Update");
  private JLabel labelMaxUrl = new JLabel("Max URL:");
  private JTextField textMaxUrl = new JTextField(10);

  /** The table data model */
  private GsaTableModel tableData = new GsaTableModel();

  /**
   * Listener inner class for the Pause Crawl button.
   */
  private ActionListener pauseCrawlListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent event) {
      List<GsaClient> selectedList = tableData.getSelectedClients();
      for (GsaClient client : selectedList) {
        try {
          String id = client.gsaUrlStr() + "/" + Terms.FEED_COMMAND +
              "/" + Terms.ENTRY_PAUSE_CRAWL;
          GsaEntry entry = new GsaEntry();
          entry.setId(id);
          entry.addGsaContent(Terms.PROPERTY_PAUSE_CRAWL, "1");
          client.updateEntry(Terms.FEED_COMMAND, Terms.ENTRY_PAUSE_CRAWL, entry);
        } catch (ServiceException ex) {
          // Do not want to re-throw the exception here, just pop up a warning window.
          logger.log(Level.SEVERE, ex.getMessage());
          JOptionPane.showMessageDialog(null, ex.getMessage());
        } catch (IOException ex) {
          // Do not want to re-throw the exception here, just pop up a warning window.
          logger.log(Level.SEVERE, ex.getMessage());
          JOptionPane.showMessageDialog(null, ex.getMessage());
        }
      }
      tableData.selectAllRows(false);
    }
  };

  /**
   * Listener inner class for the Resume Crawl button.
   */
  private ActionListener resumeCrawlListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent event) {
      List<GsaClient> selectedList = tableData.getSelectedClients();
      for (GsaClient client : selectedList) {
        try {
          String id = client.gsaUrlStr() + "/" + Terms.FEED_COMMAND +
              "/" + Terms.ENTRY_PAUSE_CRAWL;
          GsaEntry entry = new GsaEntry();
          entry.setId(id);
          entry.addGsaContent(Terms.PROPERTY_PAUSE_CRAWL, "0");
          client.updateEntry(Terms.FEED_COMMAND, Terms.ENTRY_PAUSE_CRAWL, entry);
        } catch (ServiceException ex) {
          // Do not want to re-throw the exception here, just pop up a warning window.
          logger.log(Level.SEVERE, ex.getMessage());
          JOptionPane.showMessageDialog(null, ex.getMessage());
        } catch (IOException ex) {
          // Do not want to re-throw the exception here, just pop up a warning window.
          logger.log(Level.SEVERE, ex.getMessage());
          JOptionPane.showMessageDialog(null, ex.getMessage());
        }
      }
      tableData.selectAllRows(false);
    }
  };

  /**
   * Listener inner class for the Reset Index button.
   */
  private ActionListener resetIndexListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent event) {
      List<GsaClient> selectedList = tableData.getSelectedClients();
      for (GsaClient client : selectedList) {
        try {
          String id = client.gsaUrlStr() + "/" + Terms.FEED_COMMAND +
              "/" + Terms.ENTRY_RESET_INDEX;
          GsaEntry entry = new GsaEntry();
          entry.setId(id);
          entry.addGsaContent("resetIndex", "1");
          client.updateEntry(Terms.FEED_COMMAND, Terms.ENTRY_RESET_INDEX, entry);
        } catch (ServiceException ex) {
          // Do not want to re-throw the exception here, just pop up a warning window.
          logger.log(Level.SEVERE, ex.getMessage());
          JOptionPane.showMessageDialog(null, ex.getMessage());
        } catch (IOException ex) {
          // Do not want to re-throw the exception here, just pop up a warning window.
          logger.log(Level.SEVERE, ex.getMessage());
          JOptionPane.showMessageDialog(null, ex.getMessage());
        }
      }
      tableData.selectAllRows(false);
    }
  };

  /**
   * Listener inner class for the Update Max URL button.
   */
  private ActionListener updateMaxUrlListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent event) {
      List<GsaClient> selectedList = tableData.getSelectedClients();
      for (GsaClient client : selectedList) {
        try {
          String id = client.gsaUrlStr() + "/" + Terms.FEED_CONFIG +
              "/" + Terms.ENTRY_HOST_LOAD;
          GsaEntry entry = new GsaEntry();
          entry.setId(id);
          entry.addGsaContent(Terms.PROPERTY_MAX_URLS, textMaxUrl.getText());
          client.updateEntry(Terms.FEED_CONFIG, Terms.ENTRY_HOST_LOAD, entry);
        } catch (ServiceException ex) {
          // Do not want to re-throw the exception here, just pop up a warning window.
          logger.log(Level.SEVERE, ex.getMessage());
          JOptionPane.showMessageDialog(null, ex.getMessage());
        } catch (IOException ex) {
          // Do not want to re-throw the exception here, just pop up a warning window.
          logger.log(Level.SEVERE, ex.getMessage());
          JOptionPane.showMessageDialog(null, ex.getMessage());
        }
      }
      textMaxUrl.setText("");
      tableData.selectAllRows(false);
    }
  };

  /**
   * Listener inner class for the remove client button.
   */
  private ActionListener removeClientListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent event) {
      tableData.removeRows();
    }
  };

  /**
   * Listener inner class for the select all client button.
   */
  private ActionListener selectAllListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent event) {
      tableData.selectAllRows(true);
    }    
  };

  /**
   * Listener inner class for the select none client button.
   */
  private ActionListener selectNoneListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent event) {
      tableData.selectAllRows(false);
    }    
  };

  /**
   * Listener inner class for the add server button.
   */
  private ActionListener addServerListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent event) {
      new DashAddServerForm(tableData);
    }    
  };

  /**
   * Adapter inner class which creates the auxiliary window upon double click of a grid row.
   */
  private MouseAdapter tableMouseAdapter = new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
      if (e.getClickCount() == 2) {
        int row = listTable.getSelectedRow();
        logger.info("Row clicked: " + String.valueOf(row));
        GsaClient client = tableData.getServer(row);
        if (client != null) {
          logger.info("Host selected: " + client.getAddress());
          new DashAuxForm(client);
        }
      }
    }
  };

  /**
   * Listener inner class to clean up during window closing
   */
  private WindowAdapter windowListener = new WindowAdapter() {
    @Override
    public void windowClosing(WindowEvent event) {

    }
  };

  /**
   * Constructs the main display form for this java application.
   */
  public DashMainForm() {
    super();

    initControls();
    frame.pack();
    frame.setVisible(true);
  }

  /**
   * Lays out labels and controls on the window.
   * 
   * The grid uses the BorderLayout manager to lay out the header and the data,
   * while the entire form (window) uses the GridBagLayout manager, similar to
   * the function and usage of the TABLE, TD, TR tags in HTML.
   * The action listeners for the buttons are also initialized here.
   */
  protected void initControls() {

    frame = new JFrame("GSA Dashboard");
    pane = frame.getContentPane();
    layout = new GridBagLayout();
    pane.setLayout(layout);
    constraints = new GridBagConstraints();

    constraints.insets = new Insets(2, 6, 2, 6);
    constraints.fill = GridBagConstraints.NONE;
    constraints.anchor = GridBagConstraints.FIRST_LINE_START;
    constraints.weightx = 1;
    constraints.weighty = 1;
    constraints.gridx = 0;
    constraints.gridy = 0;

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.addWindowListener(windowListener);
    frame.setResizable(false);

    for (int i = 0; i < COLUMN_HEADERS.length; i++) {
      tableData.setColumnName(i, COLUMN_HEADERS[i]);
    }

    listTable.setModel(tableData);
    Font oldFont = listTable.getTableHeader().getFont();
    Font newFont = new Font(oldFont.getFontName(), Font.BOLD, 12);
    listTable.getTableHeader().setFont(newFont);
    listTable.setFont(newFont);
    for (int i = 0; i < COLUMN_SIZES.length; i++) {
      listTable.getColumnModel().getColumn(i).setPreferredWidth(COLUMN_SIZES[i]);
    }
    listTable.addMouseListener(tableMouseAdapter);

    tablePanel.setLayout(new BorderLayout());
    tablePanel.add(listTable.getTableHeader(), BorderLayout.PAGE_START);
    tablePanel.add(listTable, BorderLayout.PAGE_END);

    // button listeners
    buttonAddServer.addActionListener(addServerListener);
    buttonPauseCrawl.addActionListener(pauseCrawlListener);
    buttonResumeCrawl.addActionListener(resumeCrawlListener);
    buttonResetIndex.addActionListener(resetIndexListener);
    buttonUpdateMaxUrl.addActionListener(updateMaxUrlListener);
    buttonRemoveClient.addActionListener(removeClientListener);
    buttonSelectAll.addActionListener(selectAllListener);
    buttonSelectNone.addActionListener(selectNoneListener);
    
    // start laying out the components
    addControl(tablePanel, 10, 1, true, true);

    addControl(buttonPauseCrawl, 1, 1, true, false);
    addControl(buttonResumeCrawl, 1, 1, true, false);
    addControl(buttonResetIndex, 1, 1, true, false);
    addControl(buttonRemoveClient, 1, 1, true, false);
    addControl(buttonSelectAll, 1, 1, true, false);
    addControl(buttonSelectNone, 1, 1, true, true);

    addControl(labelMaxUrl, 1, 1, true, false);
    addControl(textMaxUrl, 1, 1, true, false);
    addControl(buttonUpdateMaxUrl, 1, 1, true, false);
    addControl(buttonAddServer, 1, 1, true, true);
    
  }

  /**
   * Addes controls into the GridBagLayout used in the window control initialization.
   * 
   * @param component the visual component to be laid out
   * @param width the width, in grid count, the component should occupy
   * @param height the height, in grid count, the component should occupy
   * @param isFillRow whether the component should fill the remaining columns of its row
   * @param isNewLine whether the layout manager should move to a new line after this component
   */
  protected void addControl(Component component, int width, int height, 
                            boolean isFillRow, boolean isNewLine) {
    constraints.gridwidth = width;
    constraints.gridheight = height;

    if (isFillRow) {
      constraints.fill = GridBagConstraints.HORIZONTAL;
    } else {
      constraints.fill = GridBagConstraints.NONE;
    }

    pane.add(component, constraints);
    
    // re-calculation of constraints after component layout
    if (isNewLine) {
        constraints.gridx = 0;
        constraints.gridy++;
    } else {
        constraints.gridx += width;
    }
  }
  
  /**
   * Executes the program of this entire jar file.
   * 
   * @param args any external arguments passed from the command line
   */
  public static void main(String[] args) {
    DashMainForm obj = new DashMainForm();
    logger.info("Main form instantianted.");
  }

}
