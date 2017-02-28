/*
 * Copyright (C) 2006-2014 F. Gerbig (fgerbig@users.sourceforge.net)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package jgamebase.gui;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.table.AbstractTableModel;

import jgamebase.Const.CloseAction;
import jgamebase.db.model.ItemView;
import jgamebase.db.model.ItemViewFilter;
import jgamebase.db.model.Selection;

/**
 * Part of the GameBase GUI.
 * 
 * @author F. Gerbig (fgerbig@users.sourceforge.net)
 */
public class ViewDialog extends JDialog {

  /**
	 * 
	 */
  private static final long serialVersionUID = 3116509294496520164L;

  static class FilterRule {

    private final String name;

    private final List<ItemViewFilter> filters = new ArrayList<ItemViewFilter>();

    public FilterRule(final String name) {
      this.name = name;
    }

    public List<ItemViewFilter> getFilters() {
      return filters;
    }

    public String getName() {
      return name;
    }

    public void add(final String name, final int filterSelector, final int clauseType,
        final int operator, final String gameFieldTable, final String gameFieldName,
        final String musicFieldTable, final String musicFieldName) {
      filters.add(new ItemViewFilter(name, filterSelector, clauseType, operator, gameFieldTable,
          gameFieldName, musicFieldTable, musicFieldName));
    }

    @Override
    public String toString() {
      return name;
    }
  }

  class FilterTableModel extends AbstractTableModel {

    /**
		 * 
		 */
    private static final long serialVersionUID = 5917540689766594578L;

    private List<ItemViewFilter> data = new ArrayList<ItemViewFilter>();

    public void set(final List<ItemViewFilter> newData) {
      data = newData;
      fireTableDataChanged();
    }

    public void add(final ItemViewFilter filter) {
      data.add(filter);
      fireTableDataChanged();
    }

    public List<ItemViewFilter> get() {
      return data;
    }

    public ItemViewFilter getRow(final int row) {
      if ((row >= 0) && (row < data.size())) {
        return (data.get(row));
      }
      return null;
    }

    public void setRow(final int row, final ItemViewFilter filter) {
      data.set(row, filter);
      fireTableDataChanged();
    }

    @Override
    public int getColumnCount() {
      return 3;
    }

    @Override
    public int getRowCount() {
      return data.size();
    }

    @Override
    public String getColumnName(final int col) {
      switch (col) {
        case 0:
          return "Field";
        case 1:
          return "Operator";
        case 2:
          return "Filter Data";
      }
      return "";
    }

    @Override
    public Object getValueAt(final int row, final int col) {
      if (data.get(row) instanceof ItemViewFilter) {
        final ItemViewFilter filter = data.get(row);

        final FilterRule rule = getTemplateRule(filter);
        final ItemViewFilter template = getTemplateFilter(filter);

        switch (col) {
          case 0:
            if (rule == null) {
              return filter.getGameField() + " (no template found)";
            } else {
              return rule.getName();
            }

          case 1:
            if (template == null) {
              return filter.getClauseType() + " (no template found)";
            } else {
              return template.getName();
            }

          case 2:
            String data = null;

            if (filter != null) {
              if (filter.getSelector() == ItemViewFilter.SELECTOR_NONE) {
                // nothing to display
                return "";
              }

              if (filter.getSelector() == ItemViewFilter.SELECTOR_TEXT) {
                // display data "as is"
                return "\"" + filter.getClauseData() + "\"";
              }

              // get possible selections
              final List<Selection> selections = filter.getSelections();

              // find name of selection matching this value
              for (final Selection selection : selections) {
                if (filter.getClauseData().equals(selection.getValue())) {
                  // found
                  data = selection.getName();
                }
              }

            }

            if (data == null) { // fallback
              data = filter.getClauseData();
            }

            return data;
        }
      }

      return "";
    }

    @Override
    public Class<? extends Object> getColumnClass(final int c) {
      return getValueAt(0, c).getClass();
    }
  }

  private FilterRule getTemplateRule(final ItemViewFilter filter) {
    // search all rules
    for (final FilterRule templateRule : filterRules) {
      // search all filters in the rule
      for (final ItemViewFilter templateFilter : templateRule.getFilters()) {

        // if this is the template the current filter has been created
        // with
        if ((filter.getClauseType() == templateFilter.getClauseType())
            && (filter.getOperator() == templateFilter.getOperator())
            && (filter.getGameTable().equals(templateFilter.getGameTable()))
            && (filter.getGameField().equals(templateFilter.getGameField()))
            && (filter.getMusicTable().equals(templateFilter.getMusicTable()))
            && (filter.getMusicField().equals(templateFilter.getMusicField()))) {
          return templateRule;
        }
      }
    }
    return null;
  }

  private ItemViewFilter getTemplateFilter(final ItemViewFilter filter) {
    // search all rules
    for (final FilterRule templateRule : filterRules) {
      // search all filters in the rule
      for (final ItemViewFilter templateFilter : templateRule.getFilters()) {

        // if this is the template the current filter has been created
        // with
        if ((filter.getClauseType() == templateFilter.getClauseType())
            && (filter.getOperator() == templateFilter.getOperator())
            && (filter.getGameTable().equals(templateFilter.getGameTable()))
            && (filter.getGameField().equals(templateFilter.getGameField()))
            && (filter.getMusicTable().equals(templateFilter.getMusicTable()))
            && (filter.getMusicField().equals(templateFilter.getMusicField()))) {
          return templateFilter;
        }
      }
    }
    return null;
  }

  private static final String DESCRIPTION_CONTAINS = "contains text";

  private static final String DESCRIPTION_NOTCONTAINS = "excludes text";

  private static final String DESCRIPTION_STARTSWITH = "starts with text";

  private static final String DESCRIPTION_ENDWITH = "ends with text";

  private static final String DESCRIPTION_FILLED = "field is filled";

  private static final String DESCRIPTION_EMPTY = "field is empty";

  private static final String DESCRIPTION_EXISTS = "file exists";

  private static final String DESCRIPTION_MISSING = "file does not exist";

  private static final String DESCRIPTION_EQUAL = "is";

  private static final String DESCRIPTION_NOTEQUAL = "is not";

  private static final String DESCRIPTION_BEFORE = "is before";

  private static final String DESCRIPTION_AFTER = "is after";

  private static final String DESCRIPTION_LESSTHAN = "is Less than";

  private static final String DESCRIPTION_MORETHAN = "is more than";

  private List<FilterRule> filterRules;

  private ItemView view = null;

  private CloseAction closeAction = CloseAction.CANCEL;

  private ItemViewFilter filter;

  private CloseAction filterDialogCloseAction = CloseAction.CANCEL;

  public ViewDialog(final ItemView view, final String title) {
    super();
    initFieldRules();
    init();

    setTitle(title);

    try {
      this.view = (ItemView) view.clone();
    } catch (final CloneNotSupportedException e) {
      e.printStackTrace();
    }

    // name
    viewTitle.setText(view.getName());

    // filter mode
    if (view.getMode() == ItemView.MODE_AND) {
      filterModeAndRadioButton.setSelected(true);
    } else {
      filterModeOrRadioButton.setSelected(true);
    }

    final FilterTableModel data = (FilterTableModel) (filterTable.getModel());
    data.set(new ArrayList<ItemViewFilter>(view.getFilters()));
    filterTable.getSelectionModel().setSelectionInterval(0, 0);

    // included tables
    if (view.getInclude() == ItemView.INCLUDE_GAMES) {
      includeGamesRadioButton.setSelected(true);
    } else if (view.getInclude() == ItemView.INCLUDE_MUSIC) {
      includeMusicRadioButton.setSelected(true);
    } else {
      includeBothRadioButton.setSelected(true);
    }

    setLocationRelativeTo(null);
    setVisible(true);
  }

  protected void init() {
    initComponents();
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JComboBox filterData;
  private javax.swing.JLabel filterDataLabel;
  protected javax.swing.JDialog filterDialog;
  private javax.swing.JComboBox filterField;
  private javax.swing.JRadioButton filterModeAndRadioButton;
  private javax.swing.JRadioButton filterModeOrRadioButton;
  private javax.swing.JList filterOperator;
  private javax.swing.JTable filterTable;
  private javax.swing.JRadioButton includeBothRadioButton;
  private javax.swing.JRadioButton includeGamesRadioButton;
  private javax.swing.JRadioButton includeMusicRadioButton;
  private javax.swing.JTextField viewTitle;

  // End of variables declaration//GEN-END:variables

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc=" Generated Code
  // <editor-fold defaultstate="collapsed" desc=" Generated Code
  // <editor-fold defaultstate="collapsed"
  // <editor-fold defaultstate="collapsed"
  // desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    final javax.swing.ButtonGroup filterModeButtonGroup = new javax.swing.ButtonGroup();
    final javax.swing.ButtonGroup includeTablesButtonGroup = new javax.swing.ButtonGroup();
    filterDialog = new javax.swing.JDialog();
    final javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
    filterField = new javax.swing.JComboBox();
    final javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
    final javax.swing.JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
    filterOperator = new javax.swing.JList();
    filterDataLabel = new javax.swing.JLabel();
    filterData = new javax.swing.JComboBox();
    final javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
    final javax.swing.JButton filterOkButton = new javax.swing.JButton();
    final javax.swing.JButton filterCancelButton = new javax.swing.JButton();
    final javax.swing.JPanel titlePanel = new javax.swing.JPanel();
    final javax.swing.JLabel viewTitleLabel = new javax.swing.JLabel();
    viewTitle = new javax.swing.JTextField();
    final javax.swing.JPanel filterModePanel = new javax.swing.JPanel();
    filterModeAndRadioButton = new javax.swing.JRadioButton();
    filterModeOrRadioButton = new javax.swing.JRadioButton();
    final javax.swing.JPanel filterPanel = new javax.swing.JPanel();
    final javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
    filterTable = new javax.swing.JTable();
    final javax.swing.JPanel filtersButtonPanel = new javax.swing.JPanel();
    final javax.swing.JButton addFilter = new javax.swing.JButton();
    final javax.swing.JButton editFilter = new javax.swing.JButton();
    final javax.swing.JButton removeFilter = new javax.swing.JButton();
    final javax.swing.JButton clearFilterButton = new javax.swing.JButton();
    final javax.swing.JPanel bottomPanel = new javax.swing.JPanel();
    final javax.swing.JPanel includeTablesPanel = new javax.swing.JPanel();
    includeGamesRadioButton = new javax.swing.JRadioButton();
    includeMusicRadioButton = new javax.swing.JRadioButton();
    includeBothRadioButton = new javax.swing.JRadioButton();
    final javax.swing.JPanel fillPanel = new javax.swing.JPanel();
    final javax.swing.JPanel OkCancelPanel = new javax.swing.JPanel();
    final javax.swing.JPanel jPanel9 = new javax.swing.JPanel();
    final javax.swing.JButton okButton = new javax.swing.JButton();
    final javax.swing.JButton cancelButton = new javax.swing.JButton();

    filterDialog.setTitle("Edit Filter");
    filterDialog.setModal(true);
    filterDialog.setResizable(false);
    filterDialog.setSize(new Dimension(260, 340));
    filterDialog.getContentPane().setLayout(new java.awt.GridBagLayout());

    jLabel1.setText("Field:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(4, 5, 4, 5);
    filterDialog.getContentPane().add(jLabel1, gridBagConstraints);

    filterField.setModel(new javax.swing.DefaultComboBoxModel(filterRules.toArray()));
    filterField.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        filterFieldActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(4, 5, 4, 5);
    filterDialog.getContentPane().add(filterField, gridBagConstraints);

    jLabel2.setText("Operator:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(4, 5, 4, 5);
    filterDialog.getContentPane().add(jLabel2, gridBagConstraints);

    jScrollPane2.setMinimumSize(new java.awt.Dimension(250, 120));
    jScrollPane2.setPreferredSize(new java.awt.Dimension(250, 120));

    filterOperator.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    filterOperator.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
      @Override
      public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
        filterOperatorValueChanged(evt);
      }
    });
    jScrollPane2.setViewportView(filterOperator);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.insets = new java.awt.Insets(4, 5, 4, 5);
    filterDialog.getContentPane().add(jScrollPane2, gridBagConstraints);

    filterDataLabel.setText("Filter Data:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(4, 5, 4, 5);
    filterDialog.getContentPane().add(filterDataLabel, gridBagConstraints);

    filterData.setEditable(true);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(4, 5, 4, 5);
    filterDialog.getContentPane().add(filterData, gridBagConstraints);

    filterOkButton.setText("OK");
    filterOkButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        filterOkActionPerformed(evt);
      }
    });
    jPanel1.add(filterOkButton);

    filterCancelButton.setText("Cancel");
    filterCancelButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        filterCancelActionPerformed(evt);
      }
    });
    jPanel1.add(filterCancelButton);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.insets = new java.awt.Insets(4, 5, 4, 5);
    filterDialog.getContentPane().add(jPanel1, gridBagConstraints);

    setModal(true);
    addWindowListener(new java.awt.event.WindowAdapter() {
      @Override
      public void windowClosing(final java.awt.event.WindowEvent evt) {
        exitFormWindowClosing(evt);
      }
    });
    getContentPane().setLayout(
        new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

    viewTitleLabel.setDisplayedMnemonic('T');
    viewTitleLabel.setLabelFor(viewTitle);
    viewTitleLabel.setText("View Title:");
    titlePanel.add(viewTitleLabel);

    viewTitle.setMinimumSize(new java.awt.Dimension(80, 19));
    viewTitle.setPreferredSize(new java.awt.Dimension(200, 19));
    titlePanel.add(viewTitle);

    getContentPane().add(titlePanel);

    filterModePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Filter Mode"));
    filterModePanel.setLayout(new java.awt.GridLayout(1, 2));

    filterModeButtonGroup.add(filterModeAndRadioButton);
    filterModeAndRadioButton.setMnemonic('f');
    filterModeAndRadioButton.setText("All filters must match");
    filterModePanel.add(filterModeAndRadioButton);

    filterModeButtonGroup.add(filterModeOrRadioButton);
    filterModeOrRadioButton.setMnemonic('n');
    filterModeOrRadioButton.setText("Any filter can match");
    filterModePanel.add(filterModeOrRadioButton);

    getContentPane().add(filterModePanel);

    filterPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("View Filters"));
    filterPanel.setLayout(new javax.swing.BoxLayout(filterPanel, javax.swing.BoxLayout.LINE_AXIS));

    jScrollPane1.setPreferredSize(new java.awt.Dimension(400, 100));

    filterTable.setModel(new FilterTableModel());
    jScrollPane1.setViewportView(filterTable);

    filterPanel.add(jScrollPane1);

    filtersButtonPanel.setLayout(new java.awt.GridBagLayout());

    addFilter.setMnemonic('A');
    addFilter.setText("Add");
    addFilter.setNextFocusableComponent(editFilter);
    addFilter.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        addFilterActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
    filtersButtonPanel.add(addFilter, gridBagConstraints);

    editFilter.setMnemonic('E');
    editFilter.setText("Edit");
    editFilter.setNextFocusableComponent(removeFilter);
    editFilter.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        editFilterActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
    filtersButtonPanel.add(editFilter, gridBagConstraints);

    removeFilter.setMnemonic('R');
    removeFilter.setText("Remove");
    removeFilter.setNextFocusableComponent(clearFilterButton);
    removeFilter.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        removeFilterActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
    filtersButtonPanel.add(removeFilter, gridBagConstraints);

    clearFilterButton.setMnemonic('l');
    clearFilterButton.setText("Clear");
    clearFilterButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        clearFilterActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
    filtersButtonPanel.add(clearFilterButton, gridBagConstraints);

    filterPanel.add(filtersButtonPanel);

    getContentPane().add(filterPanel);

    bottomPanel.setLayout(new java.awt.GridLayout(1, 3));

    includeTablesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Include Tables"));
    includeTablesPanel.setLayout(new javax.swing.BoxLayout(includeTablesPanel,
        javax.swing.BoxLayout.Y_AXIS));

    includeTablesButtonGroup.add(includeGamesRadioButton);
    includeGamesRadioButton.setMnemonic('G');
    includeGamesRadioButton.setText("Games Table only");
    includeTablesPanel.add(includeGamesRadioButton);

    includeTablesButtonGroup.add(includeMusicRadioButton);
    includeMusicRadioButton.setMnemonic('M');
    includeMusicRadioButton.setText("Music Table only");
    includeTablesPanel.add(includeMusicRadioButton);

    includeTablesButtonGroup.add(includeBothRadioButton);
    includeBothRadioButton.setMnemonic('B');
    includeBothRadioButton.setText("Both Tables");
    includeTablesPanel.add(includeBothRadioButton);

    bottomPanel.add(includeTablesPanel);
    bottomPanel.add(fillPanel);

    OkCancelPanel.setLayout(new java.awt.BorderLayout());

    jPanel9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

    okButton.setMnemonic('O');
    okButton.setText("OK");
    okButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        okActionPerformed(evt);
      }
    });
    jPanel9.add(okButton);

    cancelButton.setMnemonic('C');
    cancelButton.setText("Cancel");
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        cancelActionPerformed(evt);
      }
    });
    jPanel9.add(cancelButton);

    OkCancelPanel.add(jPanel9, java.awt.BorderLayout.SOUTH);

    bottomPanel.add(OkCancelPanel);

    getContentPane().add(bottomPanel);

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void filterOperatorValueChanged(final javax.swing.event.ListSelectionEvent evt) {// GEN-FIRST:event_filterOperatorValueChanged
    if ((filterOperator.getSelectedIndex() < 0)
        || (filterOperator.getSelectedIndex() > filterOperator.getMaxSelectionIndex())) {
      filterOperator.setSelectedIndex(0);
    }

    final ItemViewFilter filter = (ItemViewFilter) filterOperator.getSelectedValue();

    // hide text box and label
    filterDataLabel.setEnabled(false);
    filterData.setModel(new DefaultComboBoxModel());
    filterData.setEnabled(false);
    filterData.setEditable(false);

    if (filter.getSelector() == ItemViewFilter.SELECTOR_NONE) {
      return; // no filter data
    }

    filterDataLabel.setEnabled(true);
    filterData.setEnabled(true);

    if (filter.getSelector() == ItemViewFilter.SELECTOR_TEXT) {
      filterData.setEditable(true);
      return; // you can enter filter data
    }

    filterData.setModel(new DefaultComboBoxModel(filter.getSelections().toArray()));
  }// GEN-LAST:event_filterOperatorValueChanged

  private void filterFieldActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_filterFieldActionPerformed
    // filter field selected => display operators
    final FilterRule rule = (FilterRule) filterField.getSelectedItem();
    final DefaultListModel model = new DefaultListModel();

    for (final ItemViewFilter filter : rule.getFilters()) {
      model.addElement(filter);
    }

    filterOperator.setModel(model);
    filterOperator.setSelectedIndex(0);
  }// GEN-LAST:event_filterFieldActionPerformed

  private void filterCancelActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_filterCancelActionPerformed
    filterDialog.setVisible(false);
    filter = null;
    filterDialogCloseAction = CloseAction.CANCEL;
  }// GEN-LAST:event_filterCancelActionPerformed

  private void filterOkActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_filterOkActionPerformed
    String data = "";

    filterDialog.setVisible(false);
    filterDialogCloseAction = CloseAction.OK;

    filter = (ItemViewFilter) filterOperator.getSelectedValue();

    if (filter.getSelector() != ItemViewFilter.SELECTOR_NONE) {
      if (filter.getSelector() == ItemViewFilter.SELECTOR_TEXT) {
        data = (String) filterData.getSelectedItem();
      } else {
        data = ((Selection) filterData.getSelectedItem()).getValue();
      }

      try {
        filter = (ItemViewFilter) filter.clone();
        filter.setClauseData(data);
      } catch (final CloneNotSupportedException e) {
        e.printStackTrace();
      }
    }
  }// GEN-LAST:event_filterOkActionPerformed

  private void clearFilterActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_clearFilterActionPerformed
    ((FilterTableModel) filterTable.getModel()).set(new ArrayList<ItemViewFilter>());
  }// GEN-LAST:event_clearFilterActionPerformed

  private void removeFilterActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_removeFilterActionPerformed
    final List<ItemViewFilter> data = ((FilterTableModel) filterTable.getModel()).get();
    final int row = filterTable.getSelectedRow();

    if ((row >= 0) && (row < data.size())) {
      data.remove(filterTable.getSelectedRow());
    }

    ((FilterTableModel) filterTable.getModel()).set(data);
  }// GEN-LAST:event_removeFilterActionPerformed

  private void editFilterActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_editFilterActionPerformed
    if ((filterTable.getSelectedRow() >= 0)
        || (filterTable.getSelectedRow() < filterTable.getRowCount())) {
      final FilterTableModel model = ((FilterTableModel) filterTable.getModel());
      final int row = filterTable.getSelectedRow();

      // if there is no filter return
      if (model.getRow(row) == null) {
        return;
      }

      // get the selected filter
      filter = model.getRow(row);

      // select the correct rule
      final FilterRule rule = getTemplateRule(filter);
      filterField.setSelectedItem(rule);

      //
      final ItemViewFilter template = getTemplateFilter(filter);
      filterOperator.setSelectedValue(template, true);

      // select the correct filter data
      if (template.getSelector() != ItemViewFilter.SELECTOR_NONE) {
        if (template.getSelector() == ItemViewFilter.SELECTOR_TEXT) {
          filterData.setSelectedItem(filter.getClauseData());
        } else {
          for (int i = 0; i < filterData.getModel().getSize(); i++) {
            if (((Selection) filterData.getModel().getElementAt(i)).getValue().equals(
                filter.getClauseData())) {
              filterData.setSelectedIndex(i);
            }
          }

        }
      }
      // filterData
      filterDialog.setVisible(true);

      if (filterDialogCloseAction == CloseAction.OK) {
        model.setRow(row, filter);
      }
    }
  }// GEN-LAST:event_editFilterActionPerformed

  private void addFilterActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_addFilterActionPerformed
    filter = null;
    filterField.setSelectedIndex(0);
    filterDialog.setVisible(true); // changes filter

    // filter is a global variable changed via filterDialog
    // it's ok to test (filter != null)
    if ((filterDialogCloseAction == CloseAction.OK) && (filter != null)) {
      ((FilterTableModel) filterTable.getModel()).add(filter);
    }
  }// GEN-LAST:event_addFilterActionPerformed

  private void cancelActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cancelActionPerformed
    closeAction = CloseAction.CANCEL;
    setVisible(false);
  }// GEN-LAST:event_cancelActionPerformed

  private void okActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_okActionPerformed
    closeAction = CloseAction.OK;
    setVisible(false);
  }// GEN-LAST:event_okActionPerformed

  private void exitFormWindowClosing(final java.awt.event.WindowEvent evt) {// GEN-FIRST:event_exitFormWindowClosing
    closeAction = CloseAction.CANCEL;
    setVisible(false);
  }// GEN-LAST:event_exitFormWindowClosing

  public CloseAction getCloseAction() {
    return closeAction;
  }

  public ItemView getView() {

    // name
    view.setName(viewTitle.getText());

    // filter mode
    if (filterModeAndRadioButton.isSelected()) {
      view.setMode(ItemView.MODE_AND);
    } else {
      view.setMode(ItemView.MODE_OR);
    }

    // filters
    // convert view filter containers to view filters
    final FilterTableModel data = (FilterTableModel) (filterTable.getModel());
    view.setFilters(new HashSet<ItemViewFilter>(data.get()));

    // included tables
    if (includeGamesRadioButton.isSelected()) {
      view.setInclude(ItemView.INCLUDE_GAMES);
    } else if (includeMusicRadioButton.isSelected()) {
      view.setInclude(ItemView.INCLUDE_MUSIC);
    } else {
      view.setInclude(ItemView.INCLUDE_BOTH);
    }

    return view;
  }

  // Loads the fields into the Fields listbox
  // and fills the Fields array for convenience
  private void initFieldRules() {
    filterRules = new ArrayList<FilterRule>();

    FilterRule rule;

    rule = new FilterRule("Name");
    rule.add(DESCRIPTION_CONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_EQUAL, "Games", "Name",
        "Music", "Name");
    rule.add(DESCRIPTION_NOTCONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_NOTEQUAL, "Games", "Name",
        "Music", "Name");
    rule.add(DESCRIPTION_STARTSWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_STARTSWITH, "Games",
        "Name", "Music", "Name");
    rule.add(DESCRIPTION_ENDWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_ENDSWITH, "Games", "Name",
        "Music", "Name");
    filterRules.add(rule);

    rule = new FilterRule("Publisher");
    rule.add(DESCRIPTION_CONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_EQUAL, "Publishers",
        "Publisher", "", "");
    rule.add(DESCRIPTION_NOTCONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_NOTEQUAL, "Publishers",
        "Publisher", "", "");
    rule.add(DESCRIPTION_STARTSWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_STARTSWITH, "Publishers",
        "Publisher", "", "");
    rule.add(DESCRIPTION_ENDWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_ENDSWITH, "Publishers",
        "Publisher", "", "");
    rule.add(DESCRIPTION_EQUAL, ItemViewFilter.SELECTOR_DB, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Publishers", "PU_Id", "", "");
    rule.add(DESCRIPTION_NOTEQUAL, ItemViewFilter.SELECTOR_DB, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_NOTEQUAL, "Publishers", "PU_Id", "", "");
    filterRules.add(rule);

    rule = new FilterRule("Year Published");
    rule.add(DESCRIPTION_EQUAL, ItemViewFilter.SELECTOR_OTHER, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Years", "YE_Id", "", "");
    rule.add(DESCRIPTION_NOTEQUAL, ItemViewFilter.SELECTOR_OTHER,
        ItemViewFilter.CLAUSETYPE_DBFIELD, ItemViewFilter.OPERATOR_NOTEQUAL, "Years", "YE_Id", "",
        "");
    rule.add(DESCRIPTION_AFTER, ItemViewFilter.SELECTOR_OTHER, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_AFTER, "Years", "Year", "", "");
    rule.add(DESCRIPTION_BEFORE, ItemViewFilter.SELECTOR_OTHER, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_BEFORE, "Years", "Year", "", "");
    filterRules.add(rule);

    rule = new FilterRule("Programmer");
    rule.add(DESCRIPTION_CONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_EQUAL, "Programmers",
        "Programmer", "", "");
    rule.add(DESCRIPTION_NOTCONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_NOTEQUAL, "Programmers",
        "Programmer", "", "");
    rule.add(DESCRIPTION_STARTSWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_STARTSWITH, "Programmers",
        "Programmer", "", "");
    rule.add(DESCRIPTION_ENDWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_ENDSWITH, "Programmers",
        "Programmer", "", "");
    rule.add(DESCRIPTION_EQUAL, ItemViewFilter.SELECTOR_DB, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Programmers", "PR_Id", "", "");
    rule.add(DESCRIPTION_NOTEQUAL, ItemViewFilter.SELECTOR_DB, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_NOTEQUAL, "Programmers", "PR_Id", "", "");
    filterRules.add(rule);

    rule = new FilterRule("Musician");
    rule.add(DESCRIPTION_CONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_EQUAL, "Musicians",
        "Musician", "Musicians", "Musician");
    rule.add(DESCRIPTION_NOTCONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_NOTEQUAL, "Musicians",
        "Musician", "Musicians", "Musician");
    rule.add(DESCRIPTION_STARTSWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_STARTSWITH, "Musicians",
        "Musician", "Musicians", "Musician");
    rule.add(DESCRIPTION_ENDWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_ENDSWITH, "Musicians",
        "Musician", "Musicians", "Musician");
    rule.add(DESCRIPTION_EQUAL, ItemViewFilter.SELECTOR_DB, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Musicians", "MU_Id", "Musicians", "MU_Id");
    rule.add(DESCRIPTION_NOTEQUAL, ItemViewFilter.SELECTOR_DB, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_NOTEQUAL, "Musicians", "MU_Id", "Musicians", "MU_Id");
    filterRules.add(rule);

    rule = new FilterRule("Genre");
    rule.add(DESCRIPTION_CONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_EQUAL, "Genres", "Genre",
        "", "");
    rule.add(DESCRIPTION_NOTCONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_NOTEQUAL, "Genres",
        "Genre", "", "");
    rule.add(DESCRIPTION_EQUAL, ItemViewFilter.SELECTOR_OTHER, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Genres", "GE_Id", "", "");
    rule.add(DESCRIPTION_NOTEQUAL, ItemViewFilter.SELECTOR_OTHER,
        ItemViewFilter.CLAUSETYPE_DBFIELD, ItemViewFilter.OPERATOR_NOTEQUAL, "Genres", "GE_Id", "",
        "");
    filterRules.add(rule);

    rule = new FilterRule("Parent Genre");
    rule.add(DESCRIPTION_CONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_EQUAL, "PGenres",
        "ParentGenre", "", "");
    rule.add(DESCRIPTION_NOTCONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_NOTEQUAL, "PGenres",
        "ParentGenre", "", "");
    rule.add(DESCRIPTION_EQUAL, ItemViewFilter.SELECTOR_DB, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "PGenres", "PG_Id", "", "");
    rule.add(DESCRIPTION_NOTEQUAL, ItemViewFilter.SELECTOR_DB, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_NOTEQUAL, "PGenres", "PG_Id", "", "");
    filterRules.add(rule);

    rule = new FilterRule("No. of Players");
    rule.add(DESCRIPTION_EQUAL, ItemViewFilter.SELECTOR_OTHER, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Games", "PlayersFrom", "", "");
    rule.add(DESCRIPTION_NOTEQUAL, ItemViewFilter.SELECTOR_OTHER,
        ItemViewFilter.CLAUSETYPE_DBFIELD, ItemViewFilter.OPERATOR_NOTEQUAL, "Games",
        "PlayersFrom", "", "");
    rule.add(DESCRIPTION_LESSTHAN, ItemViewFilter.SELECTOR_OTHER,
        ItemViewFilter.CLAUSETYPE_DBFIELD, ItemViewFilter.OPERATOR_LESSTHAN, "Games", "PlayersTo",
        "", "");
    rule.add(DESCRIPTION_MORETHAN, ItemViewFilter.SELECTOR_OTHER,
        ItemViewFilter.CLAUSETYPE_DBFIELD, ItemViewFilter.OPERATOR_MORETHAN, "Games",
        "PlayersFrom", "", "");
    filterRules.add(rule);

    rule = new FilterRule("Simultaneous Play");
    rule.add(DESCRIPTION_EQUAL, ItemViewFilter.SELECTOR_YESNO, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Games", "PlayersSim", "", "");
    filterRules.add(rule);

    rule = new FilterRule("Language");
    rule.add(DESCRIPTION_CONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_EQUAL, "Languages",
        "Language", "", "");
    rule.add(DESCRIPTION_NOTCONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_NOTEQUAL, "Languages",
        "Language", "", "");
    rule.add(DESCRIPTION_STARTSWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_STARTSWITH, "Languages",
        "Language", "", "");
    rule.add(DESCRIPTION_ENDWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_ENDSWITH, "Languages",
        "Language", "", "");
    rule.add(DESCRIPTION_EQUAL, ItemViewFilter.SELECTOR_DB, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Languages", "LA_Id", "", "");
    rule.add(DESCRIPTION_NOTEQUAL, ItemViewFilter.SELECTOR_DB, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_NOTEQUAL, "Languages", "LA_Id", "", "");
    filterRules.add(rule);

    rule = new FilterRule("Has Prequel");
    rule.add(DESCRIPTION_EQUAL, ItemViewFilter.SELECTOR_YESNO, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Games", "Prequel", "", "");
    filterRules.add(rule);

    rule = new FilterRule("Has Sequel");
    rule.add(DESCRIPTION_EQUAL, ItemViewFilter.SELECTOR_YESNO, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Games", "Sequel", "", "");
    filterRules.add(rule);

    rule = new FilterRule("Has Related Game");
    rule.add(DESCRIPTION_EQUAL, ItemViewFilter.SELECTOR_YESNO, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Games", "Related", "", "");
    filterRules.add(rule);

    rule = new FilterRule("Personal Comment");
    rule.add(DESCRIPTION_FILLED, ItemViewFilter.SELECTOR_NONE, ItemViewFilter.CLAUSETYPE_FILLED,
        ItemViewFilter.OPERATOR_EQUAL, "Games", "Comment", "", "");
    rule.add(DESCRIPTION_EMPTY, ItemViewFilter.SELECTOR_NONE, ItemViewFilter.CLAUSETYPE_FILLED,
        ItemViewFilter.OPERATOR_NOTEQUAL, "Games", "Comment", "", "");
    rule.add(DESCRIPTION_CONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_EQUAL, "Games", "Comment",
        "", "");
    rule.add(DESCRIPTION_NOTCONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_NOTEQUAL, "Games",
        "Comment", "", "");
    rule.add(DESCRIPTION_STARTSWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_STARTSWITH, "Games",
        "Comment", "", "");
    rule.add(DESCRIPTION_ENDWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_ENDSWITH, "Games",
        "Comment", "", "");
    filterRules.add(rule);

    rule = new FilterRule("Game Filename");
    rule.add(DESCRIPTION_EXISTS, ItemViewFilter.SELECTOR_NONE, ItemViewFilter.CLAUSETYPE_EXISTS,
        ItemViewFilter.OPERATOR_EQUAL, "Games", "FA", "", "");
    rule.add(DESCRIPTION_MISSING, ItemViewFilter.SELECTOR_NONE, ItemViewFilter.CLAUSETYPE_EXISTS,
        ItemViewFilter.OPERATOR_NOTEQUAL, "Games", "FA", "", "");
    rule.add(DESCRIPTION_FILLED, ItemViewFilter.SELECTOR_NONE, ItemViewFilter.CLAUSETYPE_FILLED,
        ItemViewFilter.OPERATOR_EQUAL, "Games", "Filename", "", "");
    rule.add(DESCRIPTION_EMPTY, ItemViewFilter.SELECTOR_NONE, ItemViewFilter.CLAUSETYPE_FILLED,
        ItemViewFilter.OPERATOR_NOTEQUAL, "Games", "Filename", "", "");
    rule.add(DESCRIPTION_CONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_EQUAL, "Games", "Filename",
        "", "");
    rule.add(DESCRIPTION_NOTCONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_NOTEQUAL, "Games",
        "Filename", "", "");
    rule.add(DESCRIPTION_STARTSWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_STARTSWITH, "Games",
        "Filename", "", "");
    rule.add(DESCRIPTION_ENDWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_ENDSWITH, "Games",
        "Filename", "", "");
    filterRules.add(rule);

    rule = new FilterRule("Music Filename");
    rule.add(DESCRIPTION_EXISTS, ItemViewFilter.SELECTOR_NONE, ItemViewFilter.CLAUSETYPE_EXISTS,
        ItemViewFilter.OPERATOR_EQUAL, "Games", "SA", "Music", "SA");
    rule.add(DESCRIPTION_MISSING, ItemViewFilter.SELECTOR_NONE, ItemViewFilter.CLAUSETYPE_EXISTS,
        ItemViewFilter.OPERATOR_NOTEQUAL, "Games", "SA", "Music", "SA");
    rule.add(DESCRIPTION_FILLED, ItemViewFilter.SELECTOR_NONE, ItemViewFilter.CLAUSETYPE_FILLED,
        ItemViewFilter.OPERATOR_EQUAL, "Games", "SidFilename", "Music", "Filename");
    rule.add(DESCRIPTION_EMPTY, ItemViewFilter.SELECTOR_NONE, ItemViewFilter.CLAUSETYPE_FILLED,
        ItemViewFilter.OPERATOR_NOTEQUAL, "Games", "SidFilename", "Music", "Filename");
    rule.add(DESCRIPTION_CONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_EQUAL, "Games",
        "SidFilename", "Music", "Filename");
    rule.add(DESCRIPTION_NOTCONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_NOTEQUAL, "Games",
        "SidFilename", "Music", "Filename");
    rule.add(DESCRIPTION_STARTSWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_STARTSWITH, "Games",
        "SidFilename", "Music", "Filename");
    rule.add(DESCRIPTION_ENDWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_ENDSWITH, "Games",
        "SidFilename", "Music", "Filename");
    filterRules.add(rule);

    rule = new FilterRule("Screenshot Filename");
    rule.add(DESCRIPTION_FILLED, ItemViewFilter.SELECTOR_NONE, ItemViewFilter.CLAUSETYPE_FILLED,
        ItemViewFilter.OPERATOR_EQUAL, "Games", "ScrnShotFilename", "", "");
    rule.add(DESCRIPTION_EMPTY, ItemViewFilter.SELECTOR_NONE, ItemViewFilter.CLAUSETYPE_FILLED,
        ItemViewFilter.OPERATOR_NOTEQUAL, "Games", "ScrnShotFilename", "", "");
    rule.add(DESCRIPTION_CONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_EQUAL, "Games",
        "ScrnShotFilename", "", "");
    rule.add(DESCRIPTION_NOTCONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_NOTEQUAL, "Games",
        "ScrnShotFilename", "", "");
    rule.add(DESCRIPTION_STARTSWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_STARTSWITH, "Games",
        "ScrnShotFilename", "", "");
    rule.add(DESCRIPTION_ENDWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_ENDSWITH, "Games",
        "ScrnShotFilename", "", "");
    filterRules.add(rule);

    rule = new FilterRule("Cracker");
    rule.add(DESCRIPTION_CONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_EQUAL, "Crackers",
        "Cracker", "", "");
    rule.add(DESCRIPTION_NOTCONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_NOTEQUAL, "Crackers",
        "Cracker", "", "");
    rule.add(DESCRIPTION_STARTSWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_STARTSWITH, "Crackers",
        "Cracker", "", "");
    rule.add(DESCRIPTION_ENDWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_ENDSWITH, "Crackers",
        "Cracker", "", "");
    rule.add(DESCRIPTION_EQUAL, ItemViewFilter.SELECTOR_DB, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Crackers", "CR_Id", "", "");
    rule.add(DESCRIPTION_NOTEQUAL, ItemViewFilter.SELECTOR_DB, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_NOTEQUAL, "Crackers", "CR_Id", "", "");
    filterRules.add(rule);

    rule = new FilterRule("No. of Trainers");
    rule.add(DESCRIPTION_EQUAL, ItemViewFilter.SELECTOR_OTHER, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Games", "V_Trainers", "", "");
    rule.add(DESCRIPTION_NOTEQUAL, ItemViewFilter.SELECTOR_OTHER,
        ItemViewFilter.CLAUSETYPE_DBFIELD, ItemViewFilter.OPERATOR_NOTEQUAL, "Games", "V_Trainers",
        "", "");
    rule.add(DESCRIPTION_MORETHAN, ItemViewFilter.SELECTOR_OTHER,
        ItemViewFilter.CLAUSETYPE_DBFIELD, ItemViewFilter.OPERATOR_MORETHAN, "Games", "V_Trainers",
        "", "");
    rule.add(DESCRIPTION_LESSTHAN, ItemViewFilter.SELECTOR_OTHER,
        ItemViewFilter.CLAUSETYPE_DBFIELD, ItemViewFilter.OPERATOR_LESSTHAN, "Games", "V_Trainers",
        "", "");
    filterRules.add(rule);

    rule = new FilterRule("Loading Screen");
    rule.add(DESCRIPTION_EQUAL, ItemViewFilter.SELECTOR_YESNO, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Games", "V_LoadingScreen", "", "");

    filterRules.add(rule);

    rule = new FilterRule("High Score Saver");
    rule.add(DESCRIPTION_EQUAL, ItemViewFilter.SELECTOR_YESNO, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Games", "V_HighScoreSaver", "", "");
    filterRules.add(rule);

    rule = new FilterRule("Included Docs");
    rule.add(DESCRIPTION_EQUAL, ItemViewFilter.SELECTOR_YESNO, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Games", "V_IncludedDocs", "", "");
    filterRules.add(rule);

    rule = new FilterRule("PAL/NTSC");
    rule.add(DESCRIPTION_EQUAL, ItemViewFilter.SELECTOR_OTHER, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Games", "V_PalNTSC", "", "");
    rule.add(DESCRIPTION_NOTEQUAL, ItemViewFilter.SELECTOR_OTHER,
        ItemViewFilter.CLAUSETYPE_DBFIELD, ItemViewFilter.OPERATOR_NOTEQUAL, "Games", "V_PalNTSC",
        "", "");
    filterRules.add(rule);

    rule = new FilterRule("True Drive Emul");
    rule.add(DESCRIPTION_EQUAL, ItemViewFilter.SELECTOR_YESNO, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Games", "V_TrueDriveEmu", "", "");
    filterRules.add(rule);

    rule = new FilterRule("Game Length");
    rule.add(DESCRIPTION_EQUAL, ItemViewFilter.SELECTOR_OTHER, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Games", "V_Length", "", "");
    rule.add(DESCRIPTION_NOTEQUAL, ItemViewFilter.SELECTOR_OTHER,
        ItemViewFilter.CLAUSETYPE_DBFIELD, ItemViewFilter.OPERATOR_NOTEQUAL, "Games", "V_Length",
        "", "");
    rule.add(DESCRIPTION_MORETHAN, ItemViewFilter.SELECTOR_OTHER,
        ItemViewFilter.CLAUSETYPE_DBFIELD, ItemViewFilter.OPERATOR_MORETHAN, "Games", "V_Length",
        "", "");
    rule.add(DESCRIPTION_LESSTHAN, ItemViewFilter.SELECTOR_OTHER,
        ItemViewFilter.CLAUSETYPE_DBFIELD, ItemViewFilter.OPERATOR_LESSTHAN, "Games", "V_Length",
        "", "");
    filterRules.add(rule);

    rule = new FilterRule("Game Length Type");
    rule.add(DESCRIPTION_EQUAL, ItemViewFilter.SELECTOR_OTHER, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Games", "V_LengthType", "", "");
    rule.add(DESCRIPTION_NOTEQUAL, ItemViewFilter.SELECTOR_OTHER,
        ItemViewFilter.CLAUSETYPE_DBFIELD, ItemViewFilter.OPERATOR_NOTEQUAL, "Games",
        "V_LengthType", "", "");
    filterRules.add(rule);

    rule = new FilterRule("Version Comment");
    rule.add(DESCRIPTION_FILLED, ItemViewFilter.SELECTOR_NONE, ItemViewFilter.CLAUSETYPE_FILLED,
        ItemViewFilter.OPERATOR_EQUAL, "Games", "V_Comment", "", "");
    rule.add(DESCRIPTION_EMPTY, ItemViewFilter.SELECTOR_NONE, ItemViewFilter.CLAUSETYPE_FILLED,
        ItemViewFilter.OPERATOR_NOTEQUAL, "Games", "V_Comment", "", "");
    rule.add(DESCRIPTION_CONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_EQUAL, "Games",
        "V_Comment", "", "");
    rule.add(DESCRIPTION_NOTCONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_NOTEQUAL, "Games",
        "V_Comment", "", "");
    rule.add(DESCRIPTION_STARTSWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_STARTSWITH, "Games",
        "V_Comment", "", "");
    rule.add(DESCRIPTION_ENDWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_ENDSWITH, "Games",
        "V_Comment", "", "");

    filterRules.add(rule);

    rule = new FilterRule("Game-File Version");
    rule.add(DESCRIPTION_EQUAL, ItemViewFilter.SELECTOR_OTHER, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Games", "Version", "", "");
    rule.add(DESCRIPTION_NOTEQUAL, ItemViewFilter.SELECTOR_OTHER,
        ItemViewFilter.CLAUSETYPE_DBFIELD, ItemViewFilter.OPERATOR_NOTEQUAL, "Games", "Version",
        "", "");
    rule.add(DESCRIPTION_MORETHAN, ItemViewFilter.SELECTOR_OTHER,
        ItemViewFilter.CLAUSETYPE_DBFIELD, ItemViewFilter.OPERATOR_MORETHAN, "Games", "Version",
        "", "");
    rule.add(DESCRIPTION_LESSTHAN, ItemViewFilter.SELECTOR_OTHER,
        ItemViewFilter.CLAUSETYPE_DBFIELD, ItemViewFilter.OPERATOR_LESSTHAN, "Games", "Version",
        "", "");
    filterRules.add(rule);

    rule = new FilterRule("Difficulty");
    rule.add(DESCRIPTION_EQUAL, ItemViewFilter.SELECTOR_DB, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Difficulty", "DI_Id", "", "");
    rule.add(DESCRIPTION_NOTEQUAL, ItemViewFilter.SELECTOR_DB, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_NOTEQUAL, "Difficulty", "DI_Id", "", "");
    filterRules.add(rule);

    rule = new FilterRule("High Score");
    rule.add(DESCRIPTION_FILLED, ItemViewFilter.SELECTOR_NONE, ItemViewFilter.CLAUSETYPE_FILLED,
        ItemViewFilter.OPERATOR_EQUAL, "Games", "HighScore", "", "");
    rule.add(DESCRIPTION_EMPTY, ItemViewFilter.SELECTOR_NONE, ItemViewFilter.CLAUSETYPE_FILLED,
        ItemViewFilter.OPERATOR_NOTEQUAL, "Games", "HighScore", "", "");
    rule.add(DESCRIPTION_CONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_EQUAL, "Games",
        "HighScore", "", "");
    rule.add(DESCRIPTION_NOTCONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_NOTEQUAL, "Games",
        "HighScore", "", "");
    rule.add(DESCRIPTION_STARTSWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_STARTSWITH, "Games",
        "HighScore", "", "");
    rule.add(DESCRIPTION_ENDWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_ENDSWITH, "Games",
        "HighScore", "", "");
    filterRules.add(rule);

    rule = new FilterRule("Favourite");
    rule.add(DESCRIPTION_EQUAL, ItemViewFilter.SELECTOR_YESNO, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Games", "Fav", "Music", "SFav");
    filterRules.add(rule);

    rule = new FilterRule("Game Has Extras");
    rule.add(DESCRIPTION_EQUAL, ItemViewFilter.SELECTOR_YESNO, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Games", "Extras", "", "");
    filterRules.add(rule);

    rule = new FilterRule("Extras Name");
    rule.add(DESCRIPTION_CONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_EQUAL, "Extras", "Name",
        "", "");
    rule.add(DESCRIPTION_NOTCONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_NOTEQUAL, "Extras", "Name",
        "", "");
    rule.add(DESCRIPTION_STARTSWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_STARTSWITH, "Extras",
        "Name", "", "");
    rule.add(DESCRIPTION_ENDWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_ENDSWITH, "Extras", "Name",
        "", "");
    filterRules.add(rule);

    rule = new FilterRule("Classic");
    rule.add(DESCRIPTION_EQUAL, ItemViewFilter.SELECTOR_YESNO, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Games", "Classic", "", "");
    filterRules.add(rule);

    rule = new FilterRule("Rating");
    rule.add(DESCRIPTION_EQUAL, ItemViewFilter.SELECTOR_OTHER, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Games", "Rating", "", "");
    rule.add(DESCRIPTION_NOTEQUAL, ItemViewFilter.SELECTOR_OTHER,
        ItemViewFilter.CLAUSETYPE_DBFIELD, ItemViewFilter.OPERATOR_NOTEQUAL, "Games", "Rating", "",
        "");
    rule.add(DESCRIPTION_MORETHAN, ItemViewFilter.SELECTOR_OTHER,
        ItemViewFilter.CLAUSETYPE_DBFIELD, ItemViewFilter.OPERATOR_MORETHAN, "Games", "Rating", "",
        "");
    rule.add(DESCRIPTION_LESSTHAN, ItemViewFilter.SELECTOR_OTHER,
        ItemViewFilter.CLAUSETYPE_DBFIELD, ItemViewFilter.OPERATOR_LESSTHAN, "Games", "Rating", "",
        "");
    filterRules.add(rule);

    rule = new FilterRule("Control");
    rule.add(DESCRIPTION_EQUAL, ItemViewFilter.SELECTOR_OTHER, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Games", "Control", "", "");
    rule.add(DESCRIPTION_NOTEQUAL, ItemViewFilter.SELECTOR_OTHER,
        ItemViewFilter.CLAUSETYPE_DBFIELD, ItemViewFilter.OPERATOR_NOTEQUAL, "Games", "Control",
        "", "");
    filterRules.add(rule);

    rule = new FilterRule("Notes");
    rule.add(DESCRIPTION_FILLED, ItemViewFilter.SELECTOR_NONE, ItemViewFilter.CLAUSETYPE_FILLED,
        ItemViewFilter.OPERATOR_EQUAL, "Games", "MemoText", "", "");
    rule.add(DESCRIPTION_EMPTY, ItemViewFilter.SELECTOR_NONE, ItemViewFilter.CLAUSETYPE_FILLED,
        ItemViewFilter.OPERATOR_NOTEQUAL, "Games", "MemoText", "", "");
    rule.add(DESCRIPTION_CONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_EQUAL, "Games", "MemoText",
        "", "");
    rule.add(DESCRIPTION_NOTCONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_NOTEQUAL, "Games",
        "MemoText", "", "");
    rule.add(DESCRIPTION_STARTSWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_STARTSWITH, "Games",
        "MemoText", "", "");
    rule.add(DESCRIPTION_ENDWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_ENDSWITH, "Games",
        "MemoText", "", "");
    filterRules.add(rule);

    rule = new FilterRule("Musician Group");
    rule.add(DESCRIPTION_FILLED, ItemViewFilter.SELECTOR_NONE, ItemViewFilter.CLAUSETYPE_FILLED,
        ItemViewFilter.OPERATOR_EQUAL, "Musicians", "Grp", "Musicians", "Grp");
    rule.add(DESCRIPTION_EMPTY, ItemViewFilter.SELECTOR_NONE, ItemViewFilter.CLAUSETYPE_FILLED,
        ItemViewFilter.OPERATOR_NOTEQUAL, "Musicians", "Grp", "Musicians", "Grp");
    rule.add(DESCRIPTION_CONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_EQUAL, "Musicians", "Grp",
        "Musicians", "Grp");
    rule.add(DESCRIPTION_NOTCONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_NOTEQUAL, "Musicians",
        "Grp", "Musicians", "Grp");
    rule.add(DESCRIPTION_STARTSWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_STARTSWITH, "Musicians",
        "Grp", "Musicians", "Grp");
    rule.add(DESCRIPTION_ENDWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_ENDSWITH, "Musicians",
        "Grp", "Musicians", "Grp");
    filterRules.add(rule);

    rule = new FilterRule("Musician Nickname");
    rule.add(DESCRIPTION_FILLED, ItemViewFilter.SELECTOR_NONE, ItemViewFilter.CLAUSETYPE_FILLED,
        ItemViewFilter.OPERATOR_EQUAL, "Musicians", "Nick", "Musicians", "Nick");
    rule.add(DESCRIPTION_EMPTY, ItemViewFilter.SELECTOR_NONE, ItemViewFilter.CLAUSETYPE_FILLED,
        ItemViewFilter.OPERATOR_NOTEQUAL, "Musicians", "Nick", "Musicians", "Nick");
    rule.add(DESCRIPTION_CONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_EQUAL, "Musicians", "Nick",
        "Musicians", "Nick");
    rule.add(DESCRIPTION_NOTCONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_NOTEQUAL, "Musicians",
        "Nick", "Musicians", "Nick");
    rule.add(DESCRIPTION_STARTSWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_STARTSWITH, "Musicians",
        "Nick", "Musicians", "Nick");
    rule.add(DESCRIPTION_ENDWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_ENDSWITH, "Musicians",
        "Nick", "Musicians", "Nick");
    filterRules.add(rule);

    rule = new FilterRule("Musician Photo");
    rule.add(DESCRIPTION_FILLED, ItemViewFilter.SELECTOR_NONE, ItemViewFilter.CLAUSETYPE_FILLED,
        ItemViewFilter.OPERATOR_EQUAL, "Musicians", "Photo", "Musicians", "Photo");
    rule.add(DESCRIPTION_EMPTY, ItemViewFilter.SELECTOR_NONE, ItemViewFilter.CLAUSETYPE_FILLED,
        ItemViewFilter.OPERATOR_NOTEQUAL, "Musicians", "Photo", "Musicians", "Photo");
    rule.add(DESCRIPTION_CONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_EQUAL, "Musicians",
        "Photo", "Musicians", "Photo");
    rule.add(DESCRIPTION_NOTCONTAINS, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_NOTEQUAL, "Musicians",
        "Photo", "Musicians", "Photo");
    rule.add(DESCRIPTION_STARTSWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_STARTSWITH, "Musicians",
        "Photo", "Musicians", "Photo");
    rule.add(DESCRIPTION_ENDWITH, ItemViewFilter.SELECTOR_TEXT,
        ItemViewFilter.CLAUSETYPE_CONTAINSTEXT, ItemViewFilter.OPERATOR_ENDSWITH, "Musicians",
        "Photo", "Musicians", "Photo");
    filterRules.add(rule);

    rule = new FilterRule("Adult");
    rule.add(DESCRIPTION_EQUAL, ItemViewFilter.SELECTOR_YESNO, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Games", "Adult", "Music", "Adult");
    filterRules.add(rule);
  }
}