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
import com.google.gdata.util.AuthenticationException;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Add server form for the multiple GSA dashboard, on which the user can add
 * GSA server.
 * 
 * 
 */
public class DashAddServerForm extends JFrame {

  private static final Logger logger = Logger.getLogger(
      DashAddServerForm.class.getName());

  /** The containers for the visual controls */
  private JFrame frame;
  private Container pane;

  /** The layout managers and their constraint classes */
  private GridBagLayout layout;
  private GridBagConstraints constraints;

  /** The visual controls */
  private JButton buttonAdd = new JButton("Add");
  private JButton buttonClose = new JButton("Close");
  private JLabel labelAddress = new JLabel("GSA Server Address:");
  private JLabel labelUsername = new JLabel("Username:");
  private JLabel labelPassword = new JLabel("Password:");
  private JTextField textAddress = new JTextField(12);
  private JTextField textUsername = new JTextField(10);
  private JPasswordField textPassword = new JPasswordField(10);

  /** The table data model */
  private GsaTableModel tableData;

  /**
   * Listener inner class for the Close button, which will close this form.
   */
  private ActionListener closeListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent event) {
      frame.dispose();
    }
  };

  /**
   * Listener inner class for the Add button, which will add the new server.
   */
  private ActionListener addListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent event) {
      String address = textAddress.getText();
      String username = textUsername.getText();
      String password = new String(textPassword.getPassword());
      try {
        GsaClient client = new GsaClient(address, username, password);
        tableData.addServer(client);
        frame.dispose();
      } catch (AuthenticationException ex) {
        // Do not want to re-throw the exception here, just pop out a warning.
        logger.log(Level.SEVERE, ex.getMessage());
        JOptionPane.showMessageDialog(null, ex.getMessage());
      }
    }    
  };

  /**
   * Listener inner class to clean up during window closing.
   * Currently, there are no clean-up action, so this listener is just
   * a place-holder.
   */
  private WindowAdapter windowListener = new WindowAdapter() {
    @Override
    public void windowClosing(WindowEvent event) {

    }
  };

  /**
   * Constructs an instance of this form and displays it in the GUI.
   */
  public DashAddServerForm(GsaTableModel tableData) {
    this.tableData = tableData;
    initControls();
    frame.setVisible(true);
    frame.setAlwaysOnTop(true);
    frame.pack();
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
    frame = new JFrame("Add Server");
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

    frame.addWindowListener(windowListener);

    // button listeners
    buttonAdd.addActionListener(addListener);
    buttonClose.addActionListener(closeListener);

    addControl(labelAddress, 1, 1, true, false);
    addControl(textAddress, 1, 1, true, true);
    addControl(labelUsername, 1, 1, true, false);
    addControl(textUsername, 1, 1, true, true);
    addControl(labelPassword, 1, 1, true, false);
    addControl(textPassword, 1, 1, true, true);
    addControl(buttonAdd, 1, 1, false, false);
    addControl(buttonClose, 1, 1, false, true);
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

}
