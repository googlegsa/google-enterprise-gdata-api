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
import com.google.enterprise.apis.client.GsaFeed;
import com.google.enterprise.apis.client.GsaEntry;
import com.google.enterprise.apis.client.Terms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import javax.swing.table.AbstractTableModel;

/**
 * Table model for the datagrid in DashMainForm.
 * 
 * Normally, the table model for a data grid can be implemented as inner class of a swing form.
 * However, this particular table model is complicated enough (e.g. it even has its own inner 
 * classes, accesses external servers, and has a self-updating thread) that it warrants being
 * its own class.
 * 
 * 
 */
public class GsaTableModel extends AbstractTableModel {

  private static final long serialVersionUID = 1L;
  private static final int ROW_COUNT = 25;
  private static final Long ERROR_VALUE_LONG = new Long(-99999);
  private static final Double ERROR_VALUE_DOUBLE = new Double(-99999.9999);

  private static final Logger logger =
      Logger.getLogger(GsaTableModel.class.getName());

  private static final int HOST_COLUMN_INDEX = 0;
  private static final int SELECTION_BOX_COLUMN_INDEX = 1;
  private static final int CRAWL_STATUS_COLUMN_INDEX = 2;
  private static final int DOC_LIMIT_COLUMN_INDEX = 3;
  private static final int DOCS_FOUND_COLUMN_INDEX = 4;
  private static final int DOCS_SERVED_COLUMN_INDEX = 5;
  private static final int CRAWL_RATE_COLUMN_INDEX = 6;

  /** List of properties for each column, order as per the INDEX constants above */
  private static List<ColumnProps> columnPropList = new ArrayList<ColumnProps>() {
    {
      // column 0 properties: Host Name/IP Address
      add(new ColumnProps(null, null, null, String.class));
      // column 1 properties: Selection checkbox
      add(new ColumnProps(null, null, null, Boolean.class));
      // column 2 properties: Crawl status
      add(new ColumnProps(Terms.FEED_COMMAND,
                          Terms.ENTRY_PAUSE_CRAWL,
                          Terms.PROPERTY_PAUSE_CRAWL,
                          Boolean.class));
      // column 3 properties: Max URL
      add(new ColumnProps(Terms.FEED_CONFIG,
                          Terms.ENTRY_HOST_LOAD,
                          Terms.PROPERTY_MAX_URLS,
                          Long.class));
      // column 4 properties: URLs found
      add(new ColumnProps(Terms.FEED_STATUS,
                          Terms.ENTRY_DOCUMENT_STATUS,
                          Terms.PROPERTY_FOUND_URLS,
                          Long.class));
      // column 5 properties: URLs served
      add(new ColumnProps(Terms.FEED_STATUS,
                          Terms.ENTRY_DOCUMENT_STATUS,
                          Terms.PROPERTY_SERVED_URLS,
                          Long.class));
      // column 6 properties: Crawl rate
      add(new ColumnProps(Terms.FEED_STATUS,
                          Terms.ENTRY_DOCUMENT_STATUS,
                          Terms.PROPERTY_CRAWL_PAGES_PER_SECOND,
                          Double.class));
    }
  };

  /** Back-end data for the table model */
  private volatile List<RowData> rowDataList;

  /** Separate the column headers (UI), from the rest of the column properties (data) */
  private List<String> columnHeaderList;

  /**
   * Constructs a table model, which also starts the thread to periodically
   * refresh the data stored in the model.
   */
  public GsaTableModel() {
    rowDataList = new ArrayList<RowData>();
    logger.info("Number of columns: " + String.valueOf(columnPropList.size()));
    columnHeaderList = new ArrayList<String>(columnPropList.size());
    for (int i = 0; i < columnPropList.size(); i++) {
      columnHeaderList.add(null);
    }
    logger.info("Size of Created header list: " + String.valueOf(columnHeaderList.size()));
  }

  @Override
  public int getRowCount() {
    return ROW_COUNT;
  }

  @Override
  public int getColumnCount() {
    return columnPropList.size();
  }

  /**
   * Sets the column header title.
   * 
   * @param col 0-based column index for which to set title
   * @param value column title to set
   */
  public void setColumnName(int col, String value) {
    columnHeaderList.set(col, value); 
  }

  @Override
  public String getColumnName(int col) {
    return columnHeaderList.get(col);
  }

  /**
   * Overrides the TableModel getter used to paint the grid.
   * 
   * Columns 2, 3, 4, 5, 6 are the data retrieved from the GSA using GData API calls,
   * while column 0, although stored as a GsaClient in the data model, is returned
   * as the hostname/address used when creating the GsaClient.
   * Column 1 is the user selection box input, and therefore is not read from the GSA.
   *
   * @param row the row index of the value to retrieve
   * @param col the column index of the value to retrieve
   * @return a generic object representing/wrapping the value stored in the model
   */
  @Override
  public Object getValueAt(int row, int col) {
    if ((row < 0) || (row >= rowDataList.size())) {
      return null;
    }
    RowData rowData = rowDataList.get(row);
    if (rowData == null) {
      throw new NullPointerException();
    }
    switch (col) {
      case HOST_COLUMN_INDEX:
        if (rowData.client == null) {
          throw new NullPointerException();
        }
        return rowData.client.getAddress();
      case SELECTION_BOX_COLUMN_INDEX:
        return rowData.isSelected;
      case CRAWL_STATUS_COLUMN_INDEX:
        return rowData.isPaused;
      case DOC_LIMIT_COLUMN_INDEX:
        return rowData.docLimit;
      case DOCS_FOUND_COLUMN_INDEX:
        return rowData.docsFound;
      case DOCS_SERVED_COLUMN_INDEX:
        return rowData.docsServed;
      case CRAWL_RATE_COLUMN_INDEX:
        return rowData.crawlRate;
      default:
        return null;
    }
  }
  
  /**
   * Overrides the getter to fetch the object class for each column data type,
   * in order for the grid renderer to function properly.
   */
  @Override
  public Class<?> getColumnClass(int col) {
    return columnPropList.get(col).colClass;
  }

  /**
   * Used by the control to lock specific cells, rows, or column of the grid.
   * 
   * Only the second (selection box) columns of the data is editable by the user/GUI.
   */
  @Override
  public boolean isCellEditable(int row, int col) {
    // column 1 can be edited to select the GSA you want to modify
    return (col == SELECTION_BOX_COLUMN_INDEX);
  }

  /**
   * Overrides the setter that updates the data stored in the datamodel,
   * based on changes happening in the GUI.
   * 
   * Note that we only allow value updates for data in column 1 (an unbound selection box).
   */
  @Override
  public void setValueAt(Object value, int row, int col) {
    if (value == null) {
      throw new NullPointerException();
    }
    // only set the selection flag if this row has a GSA client
    if (col == SELECTION_BOX_COLUMN_INDEX && row < rowDataList.size()) {
        rowDataList.get(row).isSelected = (Boolean) value;
        fireTableRowsUpdated(row, row);
    }
  }

  /**
   * Adds new GSA server.
   * @param client the GsaClient connecting to the GSA server
   */
  public void addServer(GsaClient client) {
    // move it to after the last row with data in it
    rowDataList.add(new RowData(client));
    int row = rowDataList.size() - 1;
    logger.info("Row " + String.valueOf(row) + " updated");
    // since there will be latency in GData call, refresh the hostname/address first
    fireTableRowsUpdated(row, row);
  }

  /**
   * Returns the total number of servers
   * @return the total number of servers
   */
  public int countServer() {
    return rowDataList.size();
  }

  /**
   * Gets the GsaClient corresponding to the row
   * @param row the row number of GsaClient
   * @return the GsaClient connecting to the GSA server
   */
  public GsaClient getServer(int row) {
    if (row >= 0 && row < rowDataList.size()) {
      return rowDataList.get(row).client;
    } else {
      return null;
    }
  }

  /**
   * Returns a list of GsaClients with its corresponding selection box clicked.
   * 
   * @return a collection of GsaClients, with their "Selected" status set to true
   */
  public List<GsaClient> getSelectedClients() {
    List<GsaClient> returnVal = new ArrayList<GsaClient>();
    
    for (RowData rowData : rowDataList) {
      if ((rowData.isSelected != null) && rowData.isSelected) {
        returnVal.add(rowData.client);
      }
    }
    return returnVal;
  }
  
  /**
   * Removes selected GSAs from monitoring.
   */
  public void removeRows() {
    for (Iterator<RowData> i = rowDataList.iterator(); i.hasNext(); ) {
      RowData rowData = i.next();
      if ((rowData.isSelected != null) && rowData.isSelected) {
        rowData.finished = true;
        i.remove();
      }
    }
    fireTableDataChanged();
  }

  /**
   * Sets or clears all selection boxes on rows with GsaClients entered.
   * 
   * @param status true to set all selection boxes, false to clear all selection boxes
   */
  public void selectAllRows(boolean status) {
    for (RowData rowData : rowDataList) {
      
      // Boolean class is anticipated by the grid
      rowData.isSelected = Boolean.valueOf(status);
    }
    fireTableDataChanged();
  }

  /**
   * Sets all values inside the input row data to the error value, a large negative scalar.
   *  
   * @param rowData row data class to set to error value
   */
  public void setToErrorValues(RowData rowData) {
    rowData.docLimit = ERROR_VALUE_LONG;
    rowData.docsFound = ERROR_VALUE_LONG;
    rowData.docsServed = ERROR_VALUE_LONG;
    rowData.crawlRate = ERROR_VALUE_DOUBLE;
  }

  /**
   * Retrieves all relevant values, based on ColumnProperties defined, from a GsaClient.
   * 
   * @param rowData row data class whose data to update
   */
  public void refreshRow(RowData rowData) {
    /*
     * Implementation Note: 
     * In order to avoid repeated GData calls to the GSA, which is the most expensive operation
     * in this function, the function attempts to cache the queried feeds in a map.
     * The entries in each feed were also themselves stored in a maps after the GData call,
     * in order to speed up subsequent refresh logic.  
     */
    Map<String, Map<String, GsaEntry>> feedMap = new HashMap<String, Map<String, GsaEntry>>();
    GsaClient client = rowData.client;

    try {
      for (int i = 0; i < columnPropList.size(); i++) {

        // do not refresh hostname column or the selection box column
        if ((i == HOST_COLUMN_INDEX) || (i == SELECTION_BOX_COLUMN_INDEX))
          continue;

        ColumnProps colProps = columnPropList.get(i);
        if ((colProps.feedName == null) || (colProps.entryId == null)) {
          continue;
        }

        if (!feedMap.containsKey(colProps.feedName)) {
          // Create another set in the map.
          GsaFeed feed = null;
          feed = client.getFeed(colProps.feedName);
          Map<String, GsaEntry> entryMap = new HashMap<String, GsaEntry>();
          for (GsaEntry entry : feed.getEntries()) {
            String entryID = entry.getGsaContent("entryID");
            entryMap.put(entryID, entry);
          }
          feedMap.put(colProps.feedName, entryMap);
        }

        String value = null;
        if (feedMap.containsKey(colProps.feedName)) {
          Map<String, GsaEntry> entryMap = feedMap.get(colProps.feedName);
          if (entryMap.containsKey(colProps.entryId)) {
            value = entryMap.get(colProps.entryId).getGsaContent(colProps.contentName);
          }
        }

        switch (i) {
          case CRAWL_STATUS_COLUMN_INDEX:
            rowData.isPaused = (value == null) ? null : (Integer.parseInt(value) != 0);
            break;
          case DOC_LIMIT_COLUMN_INDEX:
            rowData.docLimit = (value == null) ? null :
                (value.equals("") ? new Long(0) : new Long(value.replace(",", "")));
            break;
          case DOCS_FOUND_COLUMN_INDEX:
            rowData.docsFound = (value == null) ? null : new Long(value.replace(",", ""));
            break;
          case DOCS_SERVED_COLUMN_INDEX:
            rowData.docsServed = (value == null) ? null : new Long(value.replace(",", ""));
            break;
          case CRAWL_RATE_COLUMN_INDEX:
            rowData.crawlRate = (value == null) ? null : new Double(value.replace(",", ""));
            break;
        }
      }
    } catch (Exception ex) {
      /*
       * Note since this is a GUI refresh, to be invoked periodically by a thread
       * running in the background, throwing exception and causing termination of the thread
       * is not a good option.  Instead, when exceptions are encountered, all values in
       * the row being refreshed will be set to a large negative value.
       * 
       * The usage of the generic Exception class is also intentional, since the various possible
       * exceptions in this method are to be treated the same way, which is:
       * (1) log it, (2) set this row to large negative error values, (3) refresh the GUI.
       */
      logger.log(Level.SEVERE, ex.getMessage());
      setToErrorValues(rowData);
    } finally {
      fireTableDataChanged();
    }
  }

  /**
   * Retrieves all relevant values, based on ColumnProperties defined, from a GsaClient.
   * 
   * @param row the index of the row whose data to update
   */
  public void refreshRow(int row) {
    RowData rowData = rowDataList.get(row);
    refreshRow(rowData);
  }

  /**
   * private inner class used as data element aggregator.
   */
  private class RowData extends Thread {
    public GsaClient client;
    public Boolean isSelected;
    public Boolean isPaused;
    public Long docLimit;
    public Long docsFound;
    public Long docsServed;
    public Double crawlRate;
    public Boolean finished;
    
    public RowData(GsaClient client) {
      this.client = client;
      this.finished = false;
      this.start();
    }

    @Override
    public void run() {
      super.run();
      while(!finished) {
          logger.info("Refreshing client: " + client.getAddress());
          refreshRow(this);
        try {
          // sleep for 3 second
          sleep(3000);
        } catch (InterruptedException ex) {
          // Do nothing while being interrupted
          logger.severe(ex.toString());
        }
      }
    }
    
  }

  /**
   * private inner class used as column attribute aggregator.
   */
  private static class ColumnProps {
    public String feedName;
    public String entryId;
    public String contentName;
    public Class<?> colClass;
    
    public ColumnProps(String feedName, String entryId, String contentName, Class<?> columnClass) {
      this.feedName = feedName;
      this.entryId = entryId;
      this.contentName = contentName;
      this.colClass = columnClass;
    }
  }
  
}
