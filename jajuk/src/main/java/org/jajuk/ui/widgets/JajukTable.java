/*
 *  Jajuk
 *  Copyright (C) 2003 The Jajuk Team
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * $Revision$
 */

package org.jajuk.ui.widgets;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.jajuk.base.Item;
import org.jajuk.events.JajukEvent;
import org.jajuk.events.JajukEvents;
import org.jajuk.events.ObservationManager;
import org.jajuk.events.Observer;
import org.jajuk.ui.helpers.ILaunchCommand;
import org.jajuk.ui.helpers.JajukCellRenderer;
import org.jajuk.ui.helpers.JajukTableModel;
import org.jajuk.ui.helpers.TableTransferHandler;
import org.jajuk.util.Conf;
import org.jajuk.util.Const;
import org.jajuk.util.UtilString;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.DefaultTableColumnModelExt;
import org.jdesktop.swingx.table.TableColumnExt;

/**
 * JXTable with following features:
 * <p>
 * Remembers columns visibility
 * <p>
 * Tooltips on each cell
 * <p>
 * Maintain a table of selected rows
 * <p>
 * Bring a menu displayed on right click
 */
public class JajukTable extends JXTable implements Observer, TableColumnModelListener,
    ListSelectionListener, java.awt.event.MouseListener {

  private static final long serialVersionUID = 1L;

  private final String sConf;

  /** User Selection* */
  private final List<Item> selection;

  private final JPopupMenu jmenu;

  /** Specific action on double click */
  private ILaunchCommand command;

  /** Model refreshing flag */
  private volatile boolean acceptColumnsEvents = false;

  private boolean storeColumnMagin = false;

  private static final DateFormat FORMATTER = UtilString.getLocaleDateFormatter();

  /** Stores the last index of column move to* */
  private int lastToIndex = 0;

  /**
   * Constructor
   * 
   * @param model
   *          : model to use
   * @param bSortable
   *          : is this table sortable
   * @sConf: configuration variable used to store columns conf
   */
  public JajukTable(TableModel model, boolean bSortable, String sConf) {
    super(model);
    acceptColumnsEvents = true;
    this.sConf = sConf;
    selection = new ArrayList<Item>();
    jmenu = new JPopupMenu();
    setShowGrid(false);
    init(bSortable);
    // Force to use Jajuk cell render for all columns, except for boolean
    // that should use default renderer (checkbox)
    for (TableColumn col : getColumns()) {
      col.setCellRenderer(new JajukCellRenderer());
    }
    // Listen for row selection
    getSelectionModel().addListSelectionListener(this);
    // Listen for clicks
    addMouseListener(this);

    ObservationManager.register(this);
  }

  /**
   * Constructor
   * 
   * @param model
   *          : model to use
   * @sConf: configuration variable used to store columns conf
   */
  public JajukTable(TableModel model, String sConf) {
    this(model, true, sConf);
  }

  private void init(boolean bSortable) {
    super.setSortable(bSortable);
    super.setColumnControlVisible(true);
  }

  /**
   * Select columns to show colsToShow list of columns id to keep
   */
  @SuppressWarnings("unchecked")
  public void showColumns(List<String> colsToShow) {
    boolean acceptColumnsEventsSave = acceptColumnsEvents;
    // Ignore columns event during these actions
    acceptColumnsEvents = false;
    Iterator it = ((DefaultTableColumnModelExt) getColumnModel()).getColumns(false).iterator();
    while (it.hasNext()) {
      TableColumnExt col = (TableColumnExt) it.next();
      if (!colsToShow.contains(((JajukTableModel) getModel()).getIdentifier(col.getModelIndex()))) {
        col.setVisible(false);
      }
    }
    reorderColumns();
    acceptColumnsEvents = acceptColumnsEventsSave;
  }

  /*
   * Reorder columns order according to given conf
   */
  private void reorderColumns() {
    storeColumnMagin = false;
    // Build the index array
    List<String> index = new ArrayList<String>(10);
    StringTokenizer st = new StringTokenizer(Conf.getString(this.sConf), ",");
    while (st.hasMoreTokens()) {
      index.add(st.nextToken());
    }
    // Now reorder the columns: remove all columns and re-add them according the
    // new order
    JajukTableModel model = (JajukTableModel) getModel();
    Map<String, TableColumn> map = new HashMap<String, TableColumn>();
    List<TableColumn> initialColumns = getColumns(true);
    for (TableColumn column : initialColumns) {
      map.put(model.getIdentifier(column.getModelIndex()), column);
      getColumnModel().removeColumn(column);
    }
    for (String sID : index) {
      TableColumn col = map.get(sID);
      if (col != null) {
        // Col can be null after user created a new custom property
        getColumnModel().addColumn(col);
      }
    }
    // Now add unvisible columns so they are available in table column selector
    // at after the visible ones
    for (TableColumn column : initialColumns) {
      if (!index.contains(model.getIdentifier(column.getModelIndex()))) {
        getColumnModel().addColumn(column);
      }
    }

    // set stored column width
    int arm = getAutoResizeMode();
    setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    for (int currentColumnIndex = 0; currentColumnIndex < getColumnModel().getColumnCount(); currentColumnIndex++) {
      TableColumn tableColumn = getColumnModel().getColumn(currentColumnIndex);

      if (Conf.containsProperty(getConfKeyForColumnWidth(tableColumn))) {
        tableColumn.setPreferredWidth(Conf.getInt(getConfKeyForColumnWidth(tableColumn)));
      }
    }
    setAutoResizeMode(arm);
    // now allow storing every margin
    storeColumnMagin = true;

    // must be done here and not before we add columns
    if (Conf.containsProperty(getConfKeyForIsHorizontalScrollable())) {
      setHorizontalScrollEnabled(Conf.getBoolean(getConfKeyForIsHorizontalScrollable()));
    } else {
      setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }
  }

  /**
   * 
   * @return list of visible columns names as string
   * @param Name
   *          of the configuration key giving configuration
   */
  public List<String> getColumnsConf() {
    List<String> alOut = new ArrayList<String>(10);
    String value = Conf.getString(sConf);
    StringTokenizer st = new StringTokenizer(value, ",");
    while (st.hasMoreTokens()) {
      alOut.add(st.nextToken());
    }
    return alOut;
  }

  /**
   * Add a new property into columns conf
   * 
   * @param property
   */
  public void addColumnIntoConf(String property) {
    if (sConf == null) {
      return;
    }
    List<String> alOut = getColumnsConf();
    if (!alOut.contains(property)) {
      String value = Conf.getString(sConf);
      Conf.setProperty(sConf, value + "," + property);
    }
  }

  /**
   * Remove a property from columns conf
   * 
   * @param property
   */
  public void removeColumnFromConf(String property) {
    if (sConf == null) {
      return;
    }
    List<String> alOut = getColumnsConf();
    alOut.remove(property);
    Conf.setProperty(sConf, getColumnsConf(alOut));
  }

  private void columnChange() {
    // ignore this column change when reloading
    // model
    if (acceptColumnsEvents) {
      // If a property is given to store the column, create the new columns
      // configuration
      if (this.sConf != null) {
        createColumnsConf();
      }
      // Force table rebuilding
      Properties details = new Properties();
      details.put(Const.DETAIL_CONTENT, this);
      ObservationManager.notify(new JajukEvent(JajukEvents.VIEW_REFRESH_REQUEST, details));
    }
  }

  @Override
  public void columnAdded(TableColumnModelEvent evt) {
    super.columnAdded(evt);
    columnChange();
  }

  @Override
  public void columnRemoved(TableColumnModelEvent evt) {
    super.columnRemoved(evt);
    columnChange();
  }

  @Override
  public void columnMoved(TableColumnModelEvent evt) {
    super.columnMoved(evt);
    /*
     * We ignore events if last to index is still the same for performances
     * reasons (this event doesn't come with a isAdjusting() method)
     */
    if (acceptColumnsEvents && evt.getToIndex() != lastToIndex) {
      lastToIndex = evt.getToIndex();
      if (this.sConf != null) {
        createColumnsConf();
      }
    }
  }

  /**
   * 
   * Create the jtable visible columns conf
   * 
   */
  public void createColumnsConf() {
    StringBuilder sb = new StringBuilder();
    int cols = getColumnCount(false);
    for (int i = 0; i < cols; i++) {
      String sIdentifier = ((JajukTableModel) getModel())
          .getIdentifier(convertColumnIndexToModel(i));
      sb.append(sIdentifier + ",");
    }
    String value;
    // remove last comma
    if (sb.length() > 0) {
      value = sb.substring(0, sb.length() - 1);
    } else {
      value = sb.toString();
    }
    Conf.setProperty(sConf, value);
  }

  /**
   * 
   * @return columns configuration from given list of columns identifiers
   * 
   */
  private String getColumnsConf(List<String> alCol) {
    StringBuilder sb = new StringBuilder();
    Iterator<String> it = alCol.iterator();
    while (it.hasNext()) {
      sb.append(it.next() + ",");
    }
    // remove last comma
    if (sb.length() > 0) {
      return sb.substring(0, sb.length() - 1);
    } else {
      return sb.toString();
    }
  }

  /**
   * add tooltips to each cell
   */
  @Override
  public String getToolTipText(MouseEvent e) {
    java.awt.Point p = e.getPoint();
    int rowIndex = rowAtPoint(p);
    int colIndex = columnAtPoint(p);
    if (rowIndex < 0 || colIndex < 0) {
      return null;
    }
    Object o = getModel().getValueAt(convertRowIndexToModel(rowIndex),
        convertColumnIndexToModel(colIndex));
    if (o == null) {
      return null;
    } else if (o instanceof IconLabel) {
      return ((IconLabel) o).getTooltip();
    } else if (o instanceof Date) {
      return FORMATTER.format((Date) o);
    } else {
      return o.toString();
    }
  }

  /**
   * Select a list of rows
   * 
   * @param indexes
   *          list of row indexes to be selected
   */
  public void setSelectedRows(int[] indexes) {
    for (int element : indexes) {
      addRowSelectionInterval(element, element);
    }
  }

  @Override
  public void valueChanged(ListSelectionEvent e) {
    // Ignore adjusting event
    if (e.getValueIsAdjusting()) {
      return;
    }
    // Make sure this table uses a Jajuk table model
    if (!(getModel() instanceof JajukTableModel)) {
      return;
    }
    JajukTableModel model = (JajukTableModel) getModel();
    selection.clear();
    int[] rows = getSelectedRows();
    for (int element : rows) {
      Item o = model.getItemAt(convertRowIndexToModel(element));
      selection.add(o);
    }
    ObservationManager.notify(new JajukEvent(JajukEvents.TABLE_SELECTION_CHANGED));
  }

  public List<Item> getSelection() {
    return this.selection;
  }

  public void handlePopup(final MouseEvent e) {
    int iSelectedRow = rowAtPoint(e.getPoint());
    // Store real row index
    TableTransferHandler.setSelectedRow(iSelectedRow);
    // right click on a selected node set if none or 1 node is
    // selected, a right click on another node
    // select it if more than 1, we keep selection and display a
    // popup for them
    if (getSelectedRowCount() < 2) {
      getSelectionModel().setSelectionInterval(iSelectedRow, iSelectedRow);
    }
    // Use getMenu() here, do not use jmenu directly as we want to enable all
    // items before though getMenu() method
    getMenu().show(this, e.getX(), e.getY());
  }

  /**
   * Return generic popup menu for items in a table. <br>
   * All items are forced to enable state
   * 
   * @TODO : this is probably not a good idea to force menu items to enable
   * 
   * @return generic popup menu for items in a table
   */
  public JPopupMenu getMenu() {
    Component[] components = this.jmenu.getComponents();
    for (Component component2 : components) {
      component2.setEnabled(true);
    }
    return this.jmenu;
  }

  /**
   * Return generic popup menu for items in a table. <br>
   * The provided list allow to disable some items
   * 
   * @param indexToDisable
   *          list of integer of indexes of items to disable
   * @return generic popup menu for items in a table with filter
   */
  public JPopupMenu getMenu(List<Integer> indexToDisable) {
    Component[] components = this.jmenu.getComponents();
    int index = 0;
    for (Component component2 : components) {
      if (component2 instanceof JMenuItem || component2 instanceof JMenu) {
        // disable the item if its index is in the index list to disable
        component2.setEnabled(!indexToDisable.contains(index));
        index++;
      }
    }
    return this.jmenu;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
   */
  public void mouseClicked(MouseEvent e) {
    // nothing to do here for now
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
   */
  public void mouseEntered(MouseEvent e) {
    // nothing to do here for now
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
   */
  public void mouseExited(MouseEvent e) {
    // nothing to do here for now
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
   */
  public void mousePressed(MouseEvent e) {
    if (e.isPopupTrigger()) {
      handlePopup(e);
    } else if (command != null && (e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) == 0) {
      command.launch(e.getClickCount());
      int iSelectedRow = rowAtPoint(e.getPoint());
      // Store real row index for drag and drop
      TableTransferHandler.setSelectedRow(iSelectedRow);
    }
  }

  public void mouseReleased(MouseEvent e) {
    if (e.isPopupTrigger()) {
      handlePopup(e);
    }
  }

  public ILaunchCommand getCommand() {
    return this.command;
  }

  public void setCommand(ILaunchCommand command) {
    this.command = command;
  }

  public void setAcceptColumnsEvents(boolean acceptColumnsEvents) {
    this.acceptColumnsEvents = acceptColumnsEvents;
  }

  @Override
  public void columnMarginChanged(ChangeEvent e) {

    if (storeColumnMagin && isVisible()) {
      // store column margin
      DefaultTableColumnModelExt tableColumnModel = (DefaultTableColumnModelExt) e.getSource();

      for (int currentColumnIndex = 0; currentColumnIndex < tableColumnModel.getColumnCount(); currentColumnIndex++) {
        TableColumn tableColumn = tableColumnModel.getColumn(currentColumnIndex);
        Conf.setProperty(getConfKeyForColumnWidth(tableColumn), Integer.toString(tableColumn.getWidth()));
      }
    }
    // don't forget to inform our super class
    super.columnMarginChanged(e);
  }

  private String getConfKeyForColumnWidth(TableColumn tableColumn) {

    String tableID = getTableId();

    String identifier = tableColumn.getIdentifier().toString();
    if (identifier.isEmpty()) {
      identifier = "noColumnName";
    }
    return tableID + "." + identifier + ".width";

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jajuk.events.Observer#getRegistrationKeys()
   */
  public Set<JajukEvents> getRegistrationKeys() {
    Set<JajukEvents> eventSubjectSet = new HashSet<JajukEvents>();
    eventSubjectSet.add(JajukEvents.EXITING);
    return eventSubjectSet;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jajuk.events.Observer#update(org.jajuk.events.JajukEvent)
   */
  public void update(JajukEvent event) {
    JajukEvents subject = event.getSubject();
    if (JajukEvents.EXITING.equals(subject)) {
      Conf.setProperty(getConfKeyForIsHorizontalScrollable(), Boolean
          .toString(isHorizontalScrollEnabled()));
    }
  }

  private String getConfKeyForIsHorizontalScrollable() {
    return getTableId() + ".is_horizontal_scrollable";
  }

  /**
   * 
   */
  private String getTableId() {
    String tableID = sConf;
    if (tableID == null) {
      tableID = "jajuk.table";
    }
    return tableID;
  }
}
