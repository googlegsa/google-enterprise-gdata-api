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

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Auxiliary form for the multiple GSA dashboard, on which the user can view
 * and modify various GSA configurations.
 * 
 * 
 */
public class DashAuxForm extends JFrame {

  private static final Logger logger = Logger.getLogger(
      DashAuxForm.class.getName());
  
  /** The containers for the visual controls */
  private JFrame frame;
  private Container pane;
  
  /** The layout managers and their constraint classes */
  private GridBagLayout layout;
  private GridBagConstraints constraints;
  
  /** The visual controls */
  private JButton buttonSave = new JButton("Save");
  private JButton buttonClose = new JButton("Close");
  
  private ContentData contentStartUrl = new ContentData(6, 30,
      "Start Crawling from the Following URLS:");
  private ContentData contentCrawlUrl = new ContentData(6, 30,
      "Follow and Crawl Only URLs with the Following Patterns:");
  private ContentData contentDoNotCrawl = new ContentData(6, 30,
      "Do Not Crawl URLs with the Following Patterns:");
  private ContentData contentCrawlFrequently = new ContentData(6, 30,
      "Crawl Frequently:");
  private ContentData contentCrawlInfrequently = new ContentData(6, 30,
      "Crawl Infrequently:");
  private ContentData contentForceRecrawl = new ContentData(6, 30,
      "Always Force Recrawl:");
  private ContentData contentEventLog = new ContentData(30, 30,
      "Event Log:");
 
  /** The GSA Client for this form */
  private GsaClient gsaClient;

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
   * Listener inner class for the Save button, which will save the updated
   * configuration settings modified in this form.
   */
  private ActionListener saveListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent event) {
      try {
        logger.info("Trying to update crawl URL entries.");
        String startUrls = contentStartUrl.text.getText();
        if (startUrls == null) {
          startUrls = "";
        }
        logger.info("Start URL: " + startUrls);
        String crawlUrls = contentCrawlUrl.text.getText();
        if (crawlUrls == null) {
          crawlUrls = "";
        }
        logger.info("Crawl URL: " + crawlUrls);
        String doNotCrawlUrls = contentDoNotCrawl.text.getText();
        if (doNotCrawlUrls == null) {
          doNotCrawlUrls = "";
        }
        logger.info("Do Not Crawl URL: " + doNotCrawlUrls);
        String crawlUrlId = gsaClient.gsaUrlStr() + "/" + Terms.FEED_CONFIG +
            "/" + Terms.ENTRY_CRAWL_URLS;
        GsaEntry crawlUrlEntry = new GsaEntry();
        crawlUrlEntry.setId(crawlUrlId);
        crawlUrlEntry.addGsaContent(Terms.PROPERTY_START_URLS, startUrls);
        crawlUrlEntry.addGsaContent(Terms.PROPERTY_FOLLOW_URLS, crawlUrls);
        crawlUrlEntry.addGsaContent(Terms.PROPERTY_DO_NOT_CRAWL_URLS, doNotCrawlUrls);
        gsaClient.updateEntry(Terms.FEED_CONFIG, Terms.ENTRY_CRAWL_URLS, crawlUrlEntry);

        logger.info("Trying to update Freshness Tuning entries.");
        String crawlFrequently = contentCrawlFrequently.text.getText();
        if (crawlFrequently == null) {
          crawlFrequently = "";
        }
        logger.info("Crawl Frequently: " + crawlFrequently);
        String crawlInfrequently = contentCrawlInfrequently.text.getText();
        if (crawlInfrequently == null) {
          crawlInfrequently = "";
        }
        logger.info("Crawl Infrequently: " + crawlInfrequently);
        String forceRecrawl = contentForceRecrawl.text.getText();
        if (forceRecrawl == null) {
          forceRecrawl = "";
        }
        logger.info("Force Recrawl: " + forceRecrawl);
        String freshnessTuningId = gsaClient.gsaUrlStr() + "/" + Terms.FEED_CONFIG +
            "/" + Terms.ENTRY_FRESHNESS;
        GsaEntry freshnessTuningEntry = new GsaEntry();
        freshnessTuningEntry.setId(freshnessTuningId);
        freshnessTuningEntry.addGsaContent(Terms.PROPERTY_FREQUENT_URLS, crawlFrequently);
        freshnessTuningEntry.addGsaContent(Terms.PROPERTY_ARCHIVE_URLS, crawlInfrequently);
        freshnessTuningEntry.addGsaContent(Terms.PROPERTY_FORCE_URLS, forceRecrawl);
        gsaClient.updateEntry(Terms.FEED_CONFIG, Terms.ENTRY_FRESHNESS, freshnessTuningEntry);

        logger.info("URL entries updated");
        JOptionPane.showMessageDialog(null, "URL Patterns updated");
      } catch (ServiceException ex) {
        
        // Do not want to re-throw the exception here, just pop out a warning.
        logger.log(Level.SEVERE, ex.getMessage());
        JOptionPane.showMessageDialog(null, ex.getMessage());
      } catch (IOException ex) {
        
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
  public DashAuxForm(GsaClient gsaClient) {
    this.gsaClient = gsaClient;
    initControls();
    frame.setSize(1080, 550);
    frame.setVisible(true);
    updateGsaInfo();
  }

  /**
   * Update the screen display information using this instance of GSA Client.
   */
  private void updateGsaInfo() {
    try {
      GsaEntry crawlUrlEntry = gsaClient.getEntry(Terms.FEED_CONFIG, Terms.ENTRY_CRAWL_URLS);
      String startUrl = crawlUrlEntry.getGsaContent(Terms.PROPERTY_START_URLS);
      if (startUrl == null) {
        startUrl = "";
      }
      contentStartUrl.text.setText(startUrl);
      String crawlUrl = crawlUrlEntry.getGsaContent(Terms.PROPERTY_FOLLOW_URLS);
      if (crawlUrl == null) {
        crawlUrl = "";
      }
      contentCrawlUrl.text.setText(crawlUrl);
      String doNotCrawlUrl = crawlUrlEntry.getGsaContent(Terms.PROPERTY_DO_NOT_CRAWL_URLS);
      if (doNotCrawlUrl == null) {
        doNotCrawlUrl = "";
      }
      contentDoNotCrawl.text.setText(doNotCrawlUrl);

      
      GsaEntry freshnessTuningEntry = gsaClient.getEntry(Terms.FEED_CONFIG,
          Terms.ENTRY_FRESHNESS);
      String crawlFrequentlyUrl = freshnessTuningEntry.getGsaContent(Terms.PROPERTY_FREQUENT_URLS);
      if (crawlFrequentlyUrl == null) {
        crawlFrequentlyUrl = "";
      }
      contentCrawlFrequently.text.setText(crawlFrequentlyUrl);
      String crawlInfrequentlyUrl =
          freshnessTuningEntry.getGsaContent(Terms.PROPERTY_ARCHIVE_URLS);
      if (crawlInfrequentlyUrl == null) {
        crawlInfrequentlyUrl = "";
      }
      contentCrawlInfrequently.text.setText(crawlInfrequentlyUrl);
      String forceRecrawlUrl = freshnessTuningEntry.getGsaContent(Terms.PROPERTY_FORCE_URLS);
      if (forceRecrawlUrl == null) {
        forceRecrawlUrl = "";
      }
      contentForceRecrawl.text.setText(forceRecrawlUrl);

      GsaEntry eventEntry = gsaClient.getEntry(Terms.FEED_LOGS,
                                               Terms.ENTRY_EVENT_LOG);
      contentEventLog.text.setText(eventEntry.getGsaContent(Terms.PROPERTY_LOG_CONTENT));
    } catch (ServiceException ex) {
      logger.severe(ex.getMessage());
      JOptionPane.showMessageDialog(null, ex.getMessage());
    } catch (IOException ex) {
      logger.severe(ex.getMessage());
      JOptionPane.showMessageDialog(null, ex.getMessage());
    }
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

    frame = new JFrame("GSA Auxiliary Info");
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
    buttonSave.addActionListener(saveListener);
    buttonClose.addActionListener(closeListener);
    
    contentEventLog.text.setEditable(false);

    addControl(contentStartUrl.label, 1, 1, true, false, false);
    addControl(contentCrawlFrequently.label, 1, 1, true, false, false);
    addControl(contentEventLog.label, 1, 1, true, false, true);

    addControl(contentStartUrl.scroll, 1, 1, true, true, false);
    addControl(contentCrawlFrequently.scroll, 1, 1, true, true, false);
    addControl(contentEventLog.scroll, 1, GridBagConstraints.REMAINDER, true, true, true);
    
    addControl(contentCrawlUrl.label, 1, 1, true, false, false);
    addControl(contentCrawlInfrequently.label, 1, 1, true, false, true);
    addControl(contentCrawlUrl.scroll, 1, 1, true, true, false);
    addControl(contentCrawlInfrequently.scroll, 1, 1, true, true, true);

    addControl(contentDoNotCrawl.label, 1, 1, true, false, false);
    addControl(contentForceRecrawl.label, 1, 1, true, false, true);
    addControl(contentDoNotCrawl.scroll, 1, 1, true, true, false);
    addControl(contentForceRecrawl.scroll, 1, 1, true, true, true);
    
    addControl(buttonSave, 1, 1, true, false, false);
    addControl(buttonClose, 1, 1, true, false, true);
  }

  /**
   * Adds controls into the GridBagLayout.  Used in the window control initialization.
   * 
   * @param component the visual component to be laid out.
   * @param width the width, in grid count, the component should occupy.
   * @param height the height, in grid count, the component should occupy.
   * @param isFillRow whether the component should fill the remaining columns of its row.
   * @param isNewLine whether the layout manager should move to a new line after this component.
   */
  protected void addControl(Component component, int width, int height, 
                            boolean isFillRow, boolean isFillCol, boolean isNewLine) {
    constraints.gridwidth = width;
    constraints.gridheight = height;

    if (isFillRow && isFillCol) {
      constraints.fill = GridBagConstraints.BOTH;
    } else if (isFillRow) {
      constraints.fill = GridBagConstraints.HORIZONTAL;
    } else if (isFillCol) {
      constraints.fill = GridBagConstraints.VERTICAL;
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
   * private inner class used as each content element aggregator.
   */
  private class ContentData {
    public JLabel label;
    public JTextArea text;
    public JScrollPane scroll;
    
    public ContentData(int rows, int columns, String name) {
      label = new JLabel();
      label.setText(name);
      text = new JTextArea(rows, columns);
      scroll = new JScrollPane(text);
    }
  }

}
