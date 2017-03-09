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

import static jgamebase.Const.ICON_GS_COMPRESSED;
import static jgamebase.Const.ICON_GS_FILE;
import static jgamebase.Const.ICON_GS_FOLDER;
import static jgamebase.Const.ICON_GS_FOLDER_OPEN;
import static jgamebase.Const.ICON_GS_GAME;
import static jgamebase.Const.ICON_GS_IMAGE;
import static jgamebase.Const.ICON_GS_MUSIC;
import static jgamebase.Const.log;

import java.awt.Component;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

import jgamebase.Const.CloseAction;
import jgamebase.db.Db;
import jgamebase.db.model.Game;
import jgamebase.model.Emulators;
import jgamebase.model.FileExtensions;
import jgamebase.model.Paths;
import jgamebase.model.Plugins;
import jgamebase.plugins.DiskInfo;
import jgamebase.plugins.Extractor;
import jgamebase.tools.FileTools;
import jgamebase.tools.ListerTools;

/**
 * The GameBase GUI.
 * 
 * @author F. Gerbig (fgerbig@users.sourceforge.net)
 */
public class GameChooserDialog extends javax.swing.JDialog {

  /**
	 * 
	 */
  private static final long serialVersionUID = -5736953616975309056L;

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

    private static final long serialVersionUID = 297347628128200626L;

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

  static class FileSelectionRenderer extends JLabel implements ListCellRenderer {

    private static final long serialVersionUID = 8719778811629472539L;

    public FileSelectionRenderer() {
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

      if (value instanceof String) {
        setIcon(ICON_GS_FILE);

        String filename = (String) value;
        final String extension = FileTools.getExtension(filename);

        if (Emulators.getSupportedMusicExtensions().contains(extension)) {
          setIcon(ICON_GS_MUSIC);
        }

        if (Emulators.getSupportedGameExtensions().contains(extension)) {
          setIcon(ICON_GS_GAME);
        }

        if (Plugins.existsDiskInfoForExtension(extension)) {
          setIcon(ICON_GS_IMAGE);
        }

        if (Plugins.existsExtractorForExtension(extension)) {
          setIcon(ICON_GS_COMPRESSED);
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

  private final Object[] fileFilters = {
      new FileFilter("All files", "*"),
      new FileFilter("Only compressed files", Plugins.getSupportedExtractorExtension()),
      new FileFilter("Only image files", Plugins.getSupportedDiskInfoExtension()),
      new FileFilter("Only game and image files", Emulators.getSupportedGameExtensions().toString()) };

  private static final int STATUS_NOTHING = 0;

  private static final int STATUS_PACKED = 1;

  private static final int STATUS_IMAGE = 2;

  private static final int STATUS_GAME = 3;

  private int status = STATUS_NOTHING;

  public GameChooserDialog() {
    init();
    setVisible(true);
  }

  @SuppressWarnings("unchecked")
  public GameChooserDialog(final Game game) {
    init();

    path = "";
    String filename = "";
    // file found => fill in details
    if (Paths.getGamePath().exists(new File(game.getFilename()))) {
      final String filename1 = game.getFilename();
      path = (Paths.getGamePath().findAndWarn(new File(filename1)) != null) ? Paths.getGamePath()
          .findAndWarn(new File(filename1)).getPath() : "";
      filename = Paths.removePath(game.getFilename());
      path = path.substring(0, path.length() - filename.length());
    }

    // get paths in combobox
    final Object[] basePaths = ListerTools.fileListToStringList(
        Paths.getGamePath().getWithDefault()).toArray();
    // select correct basepath
    if (Paths.getGamePath().whichBasePath(new File(game.getFilename())) != null) {
      final String basePath = Paths.getGamePath().whichBasePath(new File(game.getFilename()))
          .getPath();
      for (int i = 0; i < basePaths.length; i++) {
        if (basePath.equals(Paths.pathEndingWithSeparator((String) basePaths[i]))) {
          pathAdjusting = true;
          pathSelectionComboBox.setSelectedIndex(i);
          pathAdjusting = false;
        }
      }
    }

    fillPathSelectionList();
    fillFileSelectionList();

    // select correct file
    final List<String> files = ((ListListModel) fileSelectionList.getModel()).get();
    int i = 0;
    for (final Iterator<String> iter = files.iterator(); iter.hasNext(); i++) {
      final String filenameFromList = iter.next();
      if (filename.equals(filenameFromList)) {
        fileSelectionList.setSelectedIndex(i);
      }
    }

    if (status == STATUS_IMAGE) {
      // select file
      itemSelectionList.setSelectedIndex(game.getFilenameIndex());
    } else if (status == STATUS_PACKED) {
      // select image

      final List<String> list = ((ListListModel) packageContentsSelectionList.getModel()).get();
      int j = 0;
      for (final Iterator<String> iter = list.iterator(); iter.hasNext(); j++) {
        final String image = Paths.removePath(iter.next());
        if (game.getFileToRun().equals(image)) {

          packageContentsSelectionList.setSelectedIndex(j);
        }
      }
      // select file
      itemSelectionList.setSelectedIndex(game.getFilenameIndex());
    }

    setLocationRelativeTo(null);
    setVisible(true);
  }

  private void init() {
    initComponents();
    this.setSize(640, 480);

    pathSelectionComboBox.setModel(new DefaultComboBoxModel(ListerTools.fileListToStringList(
        Paths.getGamePath().getWithDefault()).toArray()));
    pathSelectionList.setCellRenderer(new PathSelectionRenderer());
    pathSelectionList.setModel(new ListListModel());

    path = "";
    if ((pathSelectionComboBox.getSelectedItem() != null)
        && (pathSelectionComboBox.getSelectedItem() instanceof String)) {
      path = (String) pathSelectionComboBox.getSelectedItem();
    }

    fillPathSelectionList();

    fileFilterComboBox.setModel(new DefaultComboBoxModel(fileFilters));
    fileSelectionList.setCellRenderer(new FileSelectionRenderer());
    fileSelectionList.setModel(new ListListModel());

    fillFileSelectionList();

    setPackageEnabled(false);
    setItemEnabled(false);
    setTestEnabled(false);

    packageContentsSelectionList.setCellRenderer(new FileSelectionRenderer());
    packageContentsSelectionList.setModel(new ListListModel());
    itemSelectionList.setModel(new ListListModel());
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

  @SuppressWarnings("unchecked")
  private void fillPathSelectionList() {
    final List<PathSelection> paths = new ArrayList<PathSelection>();
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
    List<String> dirs = new ArrayList<String>();
    try {
      dirs = ListerTools.list_Dirs_Files_Paths(path, true, false, true);
    } catch (final Exception e) {
      // if path does not exist, there are no subdirs...
    }

    // sort subdirs
    Collections.sort(dirs, new Comparator() {
      @Override
      public int compare(final Object o1, final Object o2) {
        return (FileTools.removeExtension(Paths.removePath(((String) o1).toLowerCase()))
            .compareTo(FileTools.removeExtension(Paths.removePath(((String) o2).toLowerCase()))));
      }
    });

    // add subdirs
    for (final String dir : dirs) {
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

  @SuppressWarnings("unchecked")
  private void fillFileSelectionList() {

    // list files
    List<String> files = new ArrayList<String>();
    try {
      files = new FileExtensions(
          ((FileFilter) fileFilterComboBox.getSelectedItem()).getExtensions())
          .getMatching(ListerTools.list_Dirs_Files_Paths(path, false, true, false));
    } catch (final Exception e) {
      // no files
    }

    // sort list
    Collections.sort(files, new Comparator() {
      @Override
      public int compare(final Object o1, final Object o2) {
        return (FileTools.removeExtension(Paths.removePath(((String) o1).toLowerCase()))
            .compareTo(FileTools.removeExtension(Paths.removePath(((String) o2).toLowerCase()))));
      }
    });

    // fill list
    ((ListListModel) fileSelectionList.getModel()).set(files);
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JComboBox fileFilterComboBox;
  private javax.swing.JList fileSelectionList;
  private javax.swing.JLabel itemLabelBottom;
  private javax.swing.JLabel itemLabelTop;
  private javax.swing.JList itemSelectionList;
  private javax.swing.JPanel itemSelectionPanel;
  private javax.swing.JList packageContentsSelectionList;
  private javax.swing.JPanel packageContentsSelectionPanel;
  private javax.swing.JComboBox pathSelectionComboBox;
  private javax.swing.JList pathSelectionList;
  private javax.swing.JButton testButton;

  // End of variables declaration//GEN-END:variables

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
    final javax.swing.JPanel mainFileSelectionPanel = new javax.swing.JPanel();
    final javax.swing.JPanel pathSelectionPanel = new javax.swing.JPanel();
    pathSelectionComboBox = new javax.swing.JComboBox();
    final javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
    pathSelectionList = new javax.swing.JList();
    final javax.swing.JButton jButton2 = new javax.swing.JButton();
    final javax.swing.JPanel fileSelectionPanel = new javax.swing.JPanel();
    fileFilterComboBox = new javax.swing.JComboBox();
    final javax.swing.JButton fileSelectionButton = new javax.swing.JButton();
    final javax.swing.JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
    fileSelectionList = new javax.swing.JList();
    final javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
    packageContentsSelectionPanel = new javax.swing.JPanel();
    final javax.swing.JScrollPane jScrollPane3 = new javax.swing.JScrollPane();
    packageContentsSelectionList = new javax.swing.JList();
    itemSelectionPanel = new javax.swing.JPanel();
    itemLabelTop = new javax.swing.JLabel();
    final javax.swing.JScrollPane jScrollPane4 = new javax.swing.JScrollPane();
    itemSelectionList = new javax.swing.JList();
    itemLabelBottom = new javax.swing.JLabel();
    final javax.swing.JPanel mainFileActionPanel = new javax.swing.JPanel();
    testButton = new javax.swing.JButton();
    final javax.swing.JPanel OkCancelPanel = new javax.swing.JPanel();
    final javax.swing.JPanel jPanel9 = new javax.swing.JPanel();
    final javax.swing.JButton okButton = new javax.swing.JButton();
    final javax.swing.JButton cancelButton = new javax.swing.JButton();

    setTitle("Game Selector");
    setModal(true);
    addWindowListener(new java.awt.event.WindowAdapter() {
      @Override
      public void windowClosing(final java.awt.event.WindowEvent evt) {
        exitFormWindowClosing(evt);
      }
    });

    mainPanel.setLayout(new java.awt.BorderLayout());

    mainFileSelectionPanel.setLayout(new java.awt.GridLayout(2, 2, 5, 5));

    pathSelectionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("1. Select Path"));
    pathSelectionPanel.setLayout(new java.awt.GridBagLayout());

    pathSelectionComboBox.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        pathChoosen_ActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
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

    mainFileSelectionPanel.add(pathSelectionPanel);

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

    fileSelectionList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    fileSelectionList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
      @Override
      public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
        fileSelected_ValueChanged(evt);
      }
    });
    jScrollPane2.setViewportView(fileSelectionList);

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

    mainFileSelectionPanel.add(fileSelectionPanel);

    packageContentsSelectionPanel.setBorder(javax.swing.BorderFactory
        .createTitledBorder("3. Select Contents"));
    packageContentsSelectionPanel.setLayout(new java.awt.GridBagLayout());

    packageContentsSelectionList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    packageContentsSelectionList
        .addListSelectionListener(new javax.swing.event.ListSelectionListener() {
          @Override
          public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
            packageContentsSelected_ValueChanged(evt);
          }
        });
    jScrollPane3.setViewportView(packageContentsSelectionList);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
    packageContentsSelectionPanel.add(jScrollPane3, gridBagConstraints);

    mainFileSelectionPanel.add(packageContentsSelectionPanel);

    itemSelectionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("4. Select Game"));
    itemSelectionPanel.setLayout(new java.awt.GridBagLayout());

    itemLabelTop.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 3, 5);
    itemSelectionPanel.add(itemLabelTop, gridBagConstraints);

    itemSelectionList.setFont(new java.awt.Font("Monospaced", 1, 12));
    itemSelectionList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    jScrollPane4.setViewportView(itemSelectionList);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 3, 5);
    itemSelectionPanel.add(jScrollPane4, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
    itemSelectionPanel.add(itemLabelBottom, gridBagConstraints);

    mainFileSelectionPanel.add(itemSelectionPanel);

    mainPanel.add(mainFileSelectionPanel, java.awt.BorderLayout.CENTER);

    testButton.setMnemonic('T');
    testButton.setText("Test Game");
    testButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        testGame_ActionPerformed(evt);
      }
    });
    mainFileActionPanel.add(testButton);

    mainPanel.add(mainFileActionPanel, java.awt.BorderLayout.SOUTH);

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
    fillFileSelectionList();
  }// GEN-LAST:event_fileFilterSelected_ActionPerformed

  private void okActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_okActionPerformed
    closeAction = CloseAction.OK;
    dispose();
  }// GEN-LAST:event_okActionPerformed

  private void testGame_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_testGame_ActionPerformed
    final Game game = new Game();

    log.info("");
    log.info("Filename:      " + getGameFilename());
    log.info("FileToRun:     " + getGameFileToRun());
    log.info("FilenameIndex: " + getGameFilenameIndex());

    game.setFilename(getGameFilename());
    game.setFileToRun(getGameFileToRun());
    game.setFilenameIndex(getGameFilenameIndex());

    game.play();

    Db.delete(game); // set game to transient
  }// GEN-LAST:event_testGame_ActionPerformed

  protected String getGameName() {
    if ((itemSelectionList.getSelectedValue() != null)
        && (itemSelectionList.getSelectedValue() instanceof String)) {
      return (String) itemSelectionList.getSelectedValue();
    }
    return "";
  }

  protected String getGameFilename() {
    if ((fileSelectionList.getSelectedValue() != null)
        && (fileSelectionList.getSelectedValue() instanceof String)) {
      return Paths.pathStartingWithoutSeparator(Paths
          .pathEndingWithSeparator(getPathWithoutBasePath())
          + (String) fileSelectionList.getSelectedValue());
    }
    return "";
  }

  protected String getGameFileToRun() {
    if (status == STATUS_PACKED) {
      if ((packageContentsSelectionList.getSelectedValue() != null)
          && (packageContentsSelectionList.getSelectedValue() instanceof String)) {
        return Paths.removePath((String) packageContentsSelectionList.getSelectedValue());
      }
    }
    return "";
  }

  protected int getGameFilenameIndex() {
    if (status == STATUS_PACKED) {
      if ((packageContentsSelectionList.getSelectedValue() != null)
          && (packageContentsSelectionList.getSelectedValue() instanceof String)
          && Plugins.existsDiskInfoForExtension(FileTools
              .getExtension((String) packageContentsSelectionList.getSelectedValue()))) {
        return itemSelectionList.getSelectedIndex();
      }
    } else if (status == STATUS_IMAGE) {
      return itemSelectionList.getSelectedIndex();
    }
    return 0;
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
    fillFileSelectionList();
  }// GEN-LAST:event_pathSelected_ValueChanged

  private void packageContentsSelected_ValueChanged(final javax.swing.event.ListSelectionEvent evt) {// GEN-FIRST:event_packageContentsSelected_ValueChanged
    fillItemList();
  }// GEN-LAST:event_packageContentsSelected_ValueChanged

  private void refreshFiles_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_refreshFiles_ActionPerformed
    fillFileSelectionList();
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
        Paths.getGamePath().getWithDefault()).toArray()));
    fillPathSelectionList();
    fillFileSelectionList();
  }// GEN-LAST:event_editPaths_ActionPerformed

  private void pathChoosen_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_pathChoosen_ActionPerformed
    if (pathAdjusting) {
      return;
    }
    path = getBasePath();
    fillPathSelectionList();
    fillFileSelectionList();
  }// GEN-LAST:event_pathChoosen_ActionPerformed

  private void fileSelected_ValueChanged(final javax.swing.event.ListSelectionEvent evt) {// GEN-FIRST:event_fileSelected_ValueChanged
    if ((fileSelectionList.getSelectedValue() != null)
        && (fileSelectionList.getSelectedValue() instanceof String)) {
      final String filename = (String) fileSelectionList.getSelectedValue();
      final String extension = FileTools.getExtension(filename);

      if (Plugins.existsExtractorForExtension(extension)) {
        // PACKED
        status = STATUS_PACKED;
        log.info(filename + ": STATUS_PACKED");

        // enable contents and item panel
        setPackageEnabled(true);
        setItemEnabled(true);
        setTestEnabled(true);

        fillPackageList();

      } else if (Plugins.existsDiskInfoForExtension(extension)) {
        // IMAGE
        status = STATUS_IMAGE;
        log.info(filename + ": STATUS_IMAGE");

        // enable contents and item panel
        setPackageEnabled(false);
        setItemEnabled(true);
        setTestEnabled(true);

        fillItemList();

      } else if (Emulators.getSupportedGameExtensions().contains(extension)) {
        // GAME
        status = STATUS_GAME;
        log.info(filename + ": STATUS_GAME");

        // disable contents and item panel
        setPackageEnabled(false);
        setItemEnabled(false);
        setTestEnabled(true);

      } else {
        // NOTHING
        status = STATUS_NOTHING;
        log.info(filename + ": STATUS_NOTHING");

        setPackageEnabled(false);
        setItemEnabled(false);
        setTestEnabled(false);
      }

    }
  }// GEN-LAST:event_fileSelected_ValueChanged

  @SuppressWarnings({ "unchecked" })
  private void fillPackageList() {
    final List<String> namesOfSsupportedFiles = new ArrayList<String>();

    final String filename = Paths.pathEndingWithSeparator(path)
        + (String) fileSelectionList.getSelectedValue();

    // extract files
    List<String> filenames = new ArrayList<String>();

    try {
      final Extractor extractor = Plugins
          .getExtractorForExtension(FileTools.getExtension(filename));
      filenames = extractor.extractToCleanTempDir(filename);
    } catch (final IOException e) {
      e.printStackTrace();
    }

    // create list containing only supported files
    for (final String filename2 : filenames) {
      if (Emulators.getSupportedGameExtensions().contains(FileTools.getExtension(filename2))) {
        namesOfSsupportedFiles.add(filename2);
      }
    }

    // sort list of supported files
    Collections.sort(namesOfSsupportedFiles, new Comparator() {
      @Override
      public int compare(final Object o1, final Object o2) {
        return (FileTools.removeExtension(Paths.removePath(((String) o1).toLowerCase()))
            .compareTo(FileTools.removeExtension(Paths.removePath(((String) o2).toLowerCase()))));
      }
    });

    // display list of supported files
    ((ListListModel) packageContentsSelectionList.getModel()).set(namesOfSsupportedFiles);

    // select first file
    if (filenames.size() > 0) {
      packageContentsSelectionList.setSelectedIndex(0);
    }
  }

  private void fillItemList() {
    String filename = "";

    if (status == STATUS_PACKED) {
      filename = (String) packageContentsSelectionList.getSelectedValue();
    } else if (status == STATUS_IMAGE) {
      filename = Paths.pathEndingWithSeparator(path)
          + (String) fileSelectionList.getSelectedValue();
    } else {
      return;
    }

    if (Plugins.existsDiskInfoForExtension(FileTools.getExtension(filename))) {
      setItemEnabled(true);

      final DiskInfo diskInfo = Plugins.getDiskInfoForExtension(FileTools.getExtension(filename));
      try {
        diskInfo.load(filename);
        itemLabelTop.setText(diskInfo.getHeader());

        ((ListListModel) itemSelectionList.getModel()).set(Arrays.asList(diskInfo.getDirectory()));
        itemLabelBottom.setText(diskInfo.getFooter());

        if (diskInfo.getDirectory().length > 0) {
          itemSelectionList.setSelectedIndex(0);
        }
      } catch (final IOException e) {
        e.printStackTrace();
      }
    } else {
      setItemEnabled(false);
    }
  }

  private void setPackageEnabled(final boolean enabled) {
    packageContentsSelectionList.setEnabled(enabled);
    packageContentsSelectionList.setBackground(enabled ? javax.swing.UIManager.getDefaults()
        .getColor("List.background") : javax.swing.UIManager.getDefaults().getColor(
        "TextField.inactiveBackground"));
    packageContentsSelectionList.setForeground(enabled ? javax.swing.UIManager.getDefaults()
        .getColor("List.foreground") : javax.swing.UIManager.getDefaults().getColor(
        "TextField.inactiveForeground"));
    if (!enabled) {
      packageContentsSelectionList.setModel(new ListListModel());
    }
  }

  private void setItemEnabled(final boolean enabled) {
    itemLabelTop.setEnabled(enabled);
    itemSelectionList.setEnabled(enabled);
    itemSelectionList.setBackground(enabled ? javax.swing.UIManager.getDefaults().getColor(
        "List.background") : javax.swing.UIManager.getDefaults().getColor(
        "TextField.inactiveBackground"));
    itemSelectionList.setForeground(enabled ? javax.swing.UIManager.getDefaults().getColor(
        "List.foreground") : javax.swing.UIManager.getDefaults().getColor(
        "TextField.inactiveForeground"));
    itemLabelBottom.setEnabled(enabled);
    if (!enabled) {
      itemLabelTop.setText("");
      itemSelectionList.setModel(new ListListModel());
      itemLabelBottom.setText("");
    }
  }

  private void setTestEnabled(final boolean enabled) {
    testButton.setEnabled(enabled);
  }

  public CloseAction getCloseAction() {
    return closeAction;
  }
}
