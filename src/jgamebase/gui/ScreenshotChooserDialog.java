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

import static jgamebase.Const.ICON_GS_FILE;
import static jgamebase.Const.ICON_GS_FOLDER;
import static jgamebase.Const.ICON_GS_FOLDER_OPEN;
import static jgamebase.Const.ICON_GS_GRAPHIC;
import static jgamebase.Const.log;

import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import jgamebase.Const.CloseAction;
import jgamebase.db.model.Game;
import jgamebase.model.FileExtensions;
import jgamebase.model.Paths;
import jgamebase.tools.FileTools;
import jgamebase.tools.ListerTools;

/**
 * The GameBase GUI.
 * 
 * @author F. Gerbig (fgerbig@users.sourceforge.net)
 */
public class ScreenshotChooserDialog extends javax.swing.JDialog {

  /**
	 * 
	 */
  private static final long serialVersionUID = 3397467278697607752L;

  static class PathSelection {
    private final int indentation;

    private final String path;

    public PathSelection(final int indentation, final String path) {
      this.indentation = indentation;
      this.path = Paths.pathEndingWithSeparator(path);
    }

    public int getIndentation() {
      return indentation;
    }

    public String getPath() {
      return path;
    }
  }

  static class PathSelectionRenderer extends JLabel implements ListCellRenderer {

    private static final long serialVersionUID = -1237522124336543453L;

    public PathSelectionRenderer() {
      setOpaque(true);
      setHorizontalAlignment(LEFT);
      setVerticalAlignment(CENTER);
    }

    @Override
    public Component getListCellRendererComponent(final JList list, final Object value,
        final int index, final boolean isSelected, final boolean cellHasFocus) {
      if (isSelected || cellHasFocus) {
        setBackground(list.getSelectionBackground());
        setForeground(list.getSelectionForeground());
      } else {
        setBackground(list.getBackground());
        setForeground(list.getForeground());
      }

      setFont(list.getFont());

      if (value instanceof PathSelection) {
        if (isSelected || cellHasFocus) {
          setIcon(ICON_GS_FOLDER_OPEN);
        } else {
          setIcon(ICON_GS_FOLDER);
        }

        final PathSelection selection = (PathSelection) value;
        String pathName = selection.getPath();
        final int indentation = selection.getIndentation();

        pathName = pathName.substring(0, pathName.length() - 1);
        if (pathName.indexOf(File.separator) != -1) {
          pathName = pathName.substring(pathName.lastIndexOf(File.separator), pathName.length());
        }

        pathName = Paths.pathStartingWithoutSeparator(pathName);
        setText(pathName);

        setBorder(new EmptyBorder(new Insets(0, indentation * 10, 0, 0)));
      }

      return this;
    }
  }

  static class FileSelectionRenderer extends JLabel implements TableCellRenderer {

    private static final long serialVersionUID = 96832660663632311L;

    private static final String graphicExtensions = "jpg;png;gif;tif;bmp";

    public FileSelectionRenderer() {
      setOpaque(true);
      setHorizontalAlignment(LEFT);
      setVerticalAlignment(CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value,
        final boolean isSelected, final boolean hasFocus, final int row, final int column) {
      if (isSelected || hasFocus) {
        setBackground(table.getSelectionBackground());
        setForeground(table.getSelectionForeground());
      } else {
        setBackground(table.getBackground());
        setForeground(table.getForeground());
      }

      setFont(table.getFont());
      if (value instanceof String) {
        setIcon(ICON_GS_FILE);

        String filename = (String) value;
        final String extension = FileTools.getExtension(filename);
        final String filenameWithoutExtension = FileTools.removeExtension(filename);

        if (graphicExtensions.indexOf(extension.toLowerCase()) != -1) {
          setIcon(ICON_GS_GRAPHIC);
        }

        setFont(getFont().deriveFont(Font.BOLD));

        if ((filename.indexOf("_") != -1)
            && (filename.lastIndexOf("_") == (filenameWithoutExtension.length() - 2))) {

          final char c = filenameWithoutExtension.charAt(filenameWithoutExtension.length() - 1);
          if ((c == '1') || (c == '2') || (c == '3') || (c == '4') || (c == '5') || (c == '6')
              || (c == '7') || (c == '8') || (c == '9')) {
            setFont(getFont().deriveFont(Font.PLAIN));
          }
        }

        filename = Paths.pathEndingWithoutSeparator(filename);
        if (filename.indexOf(File.separator) != -1) {
          filename = filename.substring(filename.lastIndexOf(File.separator), filename.length());
        }

        filename = Paths.pathStartingWithoutSeparator(filename);
        setText(filename);

      }

      return this;
    }
  }

  static class FileFilter {
    private final String name;

    private final String extensions;

    public FileFilter(final String name, final String extensions) {
      this.name = name;
      this.extensions = extensions;
    }

    public String getExtensions() {
      return extensions;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  private String path;

  private boolean pathAdjusting = false;

  private CloseAction closeAction = CloseAction.CANCEL;

  private final Object[] fileFilters = { new FileFilter("All files", "*"),
      new FileFilter("Only graphic files", "jpg;png;gif;tif;bmp") };

  public ScreenshotChooserDialog(final Game game) {
    init();

    // file found
    if (Paths.getScreenshotPath().exists(new File(game.getScreenshotFilename()))) {
      final String filename = game.getScreenshotFilename();
      path = (Paths.getScreenshotPath().findAndWarn(new File(filename)) != null) ? Paths
          .getScreenshotPath().findAndWarn(new File(filename)).getPath() : "";
      selectedFilename = Paths.removePath(game.getScreenshotFilename());
      path = path.substring(0, path.length() - selectedFilename.length());

      // get paths in combobox
      final Object[] basePaths = ListerTools.fileListToStringList(
          Paths.getScreenshotPath().getWithDefault()).toArray();
      // select correct basepath
      final String basePath = Paths.getScreenshotPath()
          .whichBasePath(new File(game.getScreenshotFilename())).getPath();
      for (int i = 0; i < basePaths.length; i++) {
        if (basePath.equals(Paths.pathEndingWithSeparator((String) basePaths[i]))) {
          pathAdjusting = true;
          pathSelectionComboBox.setSelectedIndex(i);
          pathAdjusting = false;
        }
      }
    }

    fillPathSelectionList();
    fillFileSelectionTable();
    setLocationRelativeTo(null);
    setVisible(true);
  }

  private void init() {
    initComponents();
    this.setSize(480, 640);

    pathSelectionComboBox.setModel(new DefaultComboBoxModel(ListerTools.fileListToStringList(
        Paths.getScreenshotPath().getWithDefault()).toArray()));
    pathSelectionList.setCellRenderer(new PathSelectionRenderer());
    pathSelectionList.setModel(new ListListModel());

    path = "";
    if ((pathSelectionComboBox.getSelectedItem() != null)
        && (pathSelectionComboBox.getSelectedItem() instanceof String)) {
      path = (String) pathSelectionComboBox.getSelectedItem();
    }

    fillPathSelectionList();

    fileFilterComboBox.setModel(new DefaultComboBoxModel(fileFilters));
    fileSelectionTable.setDefaultRenderer(Object.class, new FileSelectionRenderer());
    fillFileSelectionTable();
  }

  private String getBasePath() {
    if ((pathSelectionComboBox.getSelectedItem() != null)
        && (pathSelectionComboBox.getSelectedItem() instanceof String)) {
      return Paths.pathEndingWithSeparator((String) pathSelectionComboBox.getSelectedItem());
    }

    return "";
  }

  private String getPathWithoutBasePath() {
    if (getBasePath().isEmpty() || path.isEmpty() || (getBasePath().length() >= path.length())) {
      return "";
    }
    return path.substring(getBasePath().length());
  }

  private void fillPathSelectionList() {
    final List paths = new ArrayList();
    int indentation = 0;

    pathAdjusting = true;

    // add base directory
    paths.add(new PathSelection(0, getBasePath()));
    indentation++;

    // add subpaths
    String dirPath = "";
    for (final StringTokenizer st = new StringTokenizer(getPathWithoutBasePath(), File.separator); st
        .hasMoreTokens();) {
      dirPath += Paths.pathEndingWithSeparator(st.nextToken());
      if (new File(getBasePath() + dirPath).isDirectory()) {
        indentation++;
        paths.add(new PathSelection(indentation, getBasePath()
            + Paths.pathStartingWithoutSeparator(dirPath)));
      }
    }

    indentation++;

    // list subdirs
    final List dirs = ListerTools.list_Dirs_Files_Paths(path, true, false, true);

    // sort subdirs
    Collections.sort(dirs, new Comparator() {
      @Override
      public int compare(final Object o1, final Object o2) {
        return (FileTools.removeExtension(Paths.removePath(((String) o1).toLowerCase()))
            .compareTo(FileTools.removeExtension(Paths.removePath(((String) o2).toLowerCase()))));
      }
    });

    // add subdirs
    for (final Iterator iter = dirs.iterator(); iter.hasNext();) {
      final String dir = (String) iter.next();
      paths.add(new PathSelection(indentation, dir));
    }

    // fill list
    ((ListListModel) pathSelectionList.getModel()).set(paths);

    // select correct list item
    for (int i = 0; i < paths.size(); i++) {
      if (path.startsWith(((PathSelection) pathSelectionList.getModel().getElementAt(i)).getPath())) {
        pathSelectionList.setSelectedIndex(i);
      }
    }

    pathSelectionList.setSelectedValue(pathSelectionList.getSelectedValue(), true);

    pathAdjusting = false;
  }

  private void fillFileSelectionTable() {
    // list files
    final List<String> files = new FileExtensions(
        ((FileFilter) fileFilterComboBox.getSelectedItem()).getExtensions())
        .getMatching(ListerTools.list_Dirs_Files_Paths(path, false, true, false));

    // sort list
    Collections.sort(files, new Comparator() {
      @Override
      public int compare(final Object o1, final Object o2) {
        return (FileTools.removeExtension(Paths.removePath(((String) o1).toLowerCase()))
            .compareTo(FileTools.removeExtension(Paths.removePath(((String) o2).toLowerCase()))));
      }
    });

    // fill list
    final Vector<String> headings = new Vector<String>();
    headings.addElement("Filename");

    final Vector<Vector> data = new Vector<Vector>();
    for (final Object element : files) {
      final String filename = (String) element;
      final Vector<String> row = new Vector<String>();
      row.addElement(filename);
      data.addElement(row);
    }

    ((DefaultTableModel) fileSelectionTable.getModel()).setDataVector(data, headings);

    // select correct file
    for (int row = 0; row < fileSelectionTable.getRowCount(); row++) {
      final String filenameFromTable = (String) fileSelectionTable.getValueAt(row, 0);
      if (selectedFilename.equals(filenameFromTable)) {
        final ListSelectionModel rowSM = fileSelectionTable.getSelectionModel();
        rowSM.setSelectionInterval(row, row);
        // scroll to selected row
        fileSelectionTable.scrollRectToVisible(fileSelectionTable.getCellRect(row, 0, true));
      }
    }
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JComboBox fileFilterComboBox;
  private javax.swing.JTable fileSelectionTable;
  private javax.swing.JComboBox pathSelectionComboBox;
  private javax.swing.JList pathSelectionList;
  private javax.swing.JLabel preview;
  // End of variables declaration//GEN-END:variables
  protected String selectedFilename = "";

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc=" Generated Code
  // <editor-fold defaultstate="collapsed" desc=" Generated Code
  // <editor-fold defaultstate="collapsed" desc=" Generated Code
  // <editor-fold defaultstate="collapsed"
  // <editor-fold defaultstate="collapsed"
  // desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    final javax.swing.JPanel mainPanel = new javax.swing.JPanel();
    final javax.swing.JPanel pathSelectionPanel = new javax.swing.JPanel();
    pathSelectionComboBox = new javax.swing.JComboBox();
    final javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
    pathSelectionList = new javax.swing.JList();
    final javax.swing.JButton jButton2 = new javax.swing.JButton();
    final javax.swing.JPanel fileSelectionPanel = new javax.swing.JPanel();
    fileFilterComboBox = new javax.swing.JComboBox();
    final javax.swing.JButton fileSelectionButton = new javax.swing.JButton();
    final javax.swing.JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
    fileSelectionTable = new javax.swing.JTable();
    final javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
    final javax.swing.JPanel previewPanel = new javax.swing.JPanel();
    preview = new javax.swing.JLabel();
    final javax.swing.JPanel OkCancelPanel = new javax.swing.JPanel();
    final javax.swing.JPanel jPanel9 = new javax.swing.JPanel();
    final javax.swing.JButton okButton = new javax.swing.JButton();
    final javax.swing.JButton cancelButton = new javax.swing.JButton();

    setTitle("Screenshot Selector");
    setModal(true);
    addWindowListener(new java.awt.event.WindowAdapter() {
      @Override
      public void windowClosing(final java.awt.event.WindowEvent evt) {
        exitFormWindowClosing(evt);
      }
    });

    mainPanel.setLayout(new java.awt.GridBagLayout());

    pathSelectionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("1. Select Path"));
    pathSelectionPanel.setLayout(new java.awt.GridBagLayout());
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 3, 2);
    pathSelectionPanel.add(pathSelectionComboBox, gridBagConstraints);

    pathSelectionList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    pathSelectionList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
      @Override
      public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
        pathSelected_ValueChanged(evt);
      }
    });
    jScrollPane1.setViewportView(pathSelectionList);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
    pathSelectionPanel.add(jScrollPane1, gridBagConstraints);

    jButton2.setMnemonic('E');
    jButton2.setText("Edit");
    jButton2.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        editPaths_ActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.insets = new java.awt.Insets(0, 2, 3, 5);
    pathSelectionPanel.add(jButton2, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 0.5;
    mainPanel.add(pathSelectionPanel, gridBagConstraints);

    fileSelectionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("2. Select File"));
    fileSelectionPanel.setLayout(new java.awt.GridBagLayout());

    fileFilterComboBox.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        fileFilterSelected_ActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 2);
    fileSelectionPanel.add(fileFilterComboBox, gridBagConstraints);

    fileSelectionButton.setMnemonic('R');
    fileSelectionButton.setText("Refresh");
    fileSelectionButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        refreshFiles_ActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.insets = new java.awt.Insets(0, 2, 3, 5);
    fileSelectionPanel.add(fileSelectionButton, gridBagConstraints);

    fileSelectionTable.setAutoCreateRowSorter(true);
    fileSelectionTable.setToolTipText("Edit to rename file.");
    fileSelectionTable.setFillsViewportHeight(true);
    fileSelectionTable.setShowHorizontalLines(false);
    fileSelectionTable.setShowVerticalLines(false);
    // Ask to be notified of selection changes.
    final ListSelectionModel rowSM = fileSelectionTable.getSelectionModel();
    rowSM.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(final ListSelectionEvent e) {
        // Ignore extra messages.
        if (e.getValueIsAdjusting()) {
          return;
        }

        final ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        if (lsm.isSelectionEmpty()) {
          // no rows are selected
        } else {
          final int selectedRow = lsm.getMinSelectionIndex();
          fileSelected(selectedRow);
        }
      }
    });

    // Ask to be notified of selection editing.
    final TableCellEditor editor = fileSelectionTable.getDefaultEditor(Object.class);
    editor.addCellEditorListener(new CellEditorListener() {
      @Override
      public void editingStopped(final ChangeEvent e) {
        fileRenamed();
      }

      @Override
      public void editingCanceled(final ChangeEvent e) {
      }
    });

    jScrollPane2.setViewportView(fileSelectionTable);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
    fileSelectionPanel.add(jScrollPane2, gridBagConstraints);

    jLabel1.setText("Filter:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 3, 2);
    fileSelectionPanel.add(jLabel1, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    mainPanel.add(fileSelectionPanel, gridBagConstraints);

    previewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Preview"));
    previewPanel.setLayout(new java.awt.GridBagLayout());

    preview.setMaximumSize(new java.awt.Dimension(320, 200));
    preview.setMinimumSize(new java.awt.Dimension(320, 200));
    preview.setPreferredSize(new java.awt.Dimension(320, 200));
    previewPanel.add(preview, new java.awt.GridBagConstraints());

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    mainPanel.add(previewPanel, gridBagConstraints);

    getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

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

    getContentPane().add(OkCancelPanel, java.awt.BorderLayout.SOUTH);

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void fileFilterSelected_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_fileFilterSelected_ActionPerformed
    fillFileSelectionTable();
  }// GEN-LAST:event_fileFilterSelected_ActionPerformed

  private void okActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_okActionPerformed
    closeAction = CloseAction.OK;
    dispose();
  }// GEN-LAST:event_okActionPerformed

  protected String getFilename() {
    final int selectedRow = fileSelectionTable.getSelectedRow();

    if ((fileSelectionTable.getValueAt(selectedRow, 0) != null)
        && (fileSelectionTable.getValueAt(selectedRow, 0) instanceof String)) {
      return Paths.pathStartingWithoutSeparator(Paths
          .pathEndingWithSeparator(getPathWithoutBasePath())
          + fileSelectionTable.getValueAt(selectedRow, 0));
    }
    return "";
  }

  private void pathSelected_ValueChanged(final javax.swing.event.ListSelectionEvent evt) {// GEN-FIRST:event_pathSelected_ValueChanged
    if (pathAdjusting) {
      return;
    }

    if ((pathSelectionList.getSelectedValue() != null)
        && (pathSelectionList.getSelectedValue() instanceof PathSelection)) {
      path = ((PathSelection) pathSelectionList.getSelectedValue()).getPath();
    }

    fillPathSelectionList();
    fillFileSelectionTable();
  }// GEN-LAST:event_pathSelected_ValueChanged

  private void refreshFiles_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_refreshFiles_ActionPerformed
    fillFileSelectionTable();
  }// GEN-LAST:event_refreshFiles_ActionPerformed

  private void exitFormWindowClosing(final java.awt.event.WindowEvent evt) {// GEN-FIRST:event_exitFormWindowClosing
    closeAction = CloseAction.CANCEL;
    dispose();
  }// GEN-LAST:event_exitFormWindowClosing

  private void cancelActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cancelActionPerformed
    closeAction = CloseAction.CANCEL;
    dispose();
  }// GEN-LAST:event_cancelActionPerformed

  private void editPaths_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_editPaths_ActionPerformed
    new PathsDialog(); // create and display dialog
    pathSelectionComboBox.setModel(new DefaultComboBoxModel(ListerTools.fileListToStringList(
        Paths.getScreenshotPath().getWithDefault()).toArray()));
    fillPathSelectionList();
    fillFileSelectionTable();
  }// GEN-LAST:event_editPaths_ActionPerformed

  public CloseAction getCloseAction() {
    return closeAction;
  }

  private void fileSelected(final int selectedRow) {
    if ((fileSelectionTable.getValueAt(selectedRow, 0) != null)
        && (fileSelectionTable.getValueAt(selectedRow, 0) instanceof String)) {
      selectedFilename = (String) fileSelectionTable.getValueAt(selectedRow, 0);
      preview.setIcon(new ImageIcon(path + selectedFilename));
    }
  }

  private void fileRenamed() {
    final int selectedRow = fileSelectionTable.getSelectedRow();
    if ((fileSelectionTable.getValueAt(selectedRow, 0) != null)
        && (fileSelectionTable.getValueAt(selectedRow, 0) instanceof String)) {
      final String newFilename = (String) fileSelectionTable.getValueAt(selectedRow, 0);

      final File oldFile = new File(path + selectedFilename);
      final File newFile = new File(path + newFilename);

      oldFile.renameTo(newFile);
      selectedFilename = newFilename;
      log.info(selectedFilename + "=>" + newFilename);

      fillFileSelectionTable();
    }
  }

}