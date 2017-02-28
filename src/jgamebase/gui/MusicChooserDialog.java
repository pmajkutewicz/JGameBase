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
import jgamebase.db.model.Game;
import jgamebase.db.model.Music;
import jgamebase.model.Emulators;
import jgamebase.model.FileExtensions;
import jgamebase.model.Paths;
import jgamebase.model.Plugins;
import jgamebase.plugins.MusicInfo;
import jgamebase.tools.FileTools;
import jgamebase.tools.ListerTools;

/**
 * The GameBase GUI.
 * 
 * @author F. Gerbig (fgerbig@users.sourceforge.net)
 */
public class MusicChooserDialog extends javax.swing.JDialog {

  /**
	 * 
	 */
  private static final long serialVersionUID = -1441411640408078030L;

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

    private static final long serialVersionUID = 321736731032204969L;

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

    private static final long serialVersionUID = -7853030169895254991L;

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

  private final Object[] fileFilters = { new FileFilter("All files", "*"),
      new FileFilter("Only music files", Emulators.getSupportedMusicExtensions().toString()) };

  public MusicChooserDialog() {
    init();
    setVisible(true);
  }

  public MusicChooserDialog(final Game game) {
    String filename = "";

    init();

    // file not found => no path to set
    if (Paths.getMusicPath().exists(new File(game.getMusicFilename()))) {
      path = Paths.getMusicPath().toString() + game.getMusicFilename();
      filename = Paths.removePath(game.getMusicFilename());
      path = path.substring(0, path.length() - filename.length());
    }

    fillPathSelectionList();
    fillFileSelectionList();

    // select correct file
    final List files = ((ListListModel) fileSelectionList.getModel()).get();
    int i = 0;
    for (final Iterator iter = files.iterator(); iter.hasNext(); i++) {
      final String filenameFromList = (String) iter.next();
      if (filename.equals(filenameFromList)) {
        fileSelectionList.setSelectedIndex(i);
      }
    }

    setVisible(true);
  }

  public MusicChooserDialog(final Music music) {
    String filename = "";

    init();

    // file not found => no path to set
    if (!Paths.getMusicPath().exists(new File(music.getFilename()))) {
      path = Paths.getMusicPath().toString() + music.getFilename();
      filename = Paths.removePath(music.getFilename());
      path = path.substring(0, path.length() - filename.length());
    }

    fillPathSelectionList();
    fillFileSelectionList();

    // select correct file
    final List files = ((ListListModel) fileSelectionList.getModel()).get();
    int i = 0;
    for (final Iterator iter = files.iterator(); iter.hasNext(); i++) {
      final String filenameFromList = (String) iter.next();
      if (filename.equals(filenameFromList)) {
        fileSelectionList.setSelectedIndex(i);
      }
    }

    setVisible(true);
  }

  private void init() {
    initComponents();
    this.setSize(480, 480);

    pathSelectionComboBox.setModel(new DefaultComboBoxModel(ListerTools.fileListToStringList(
        Paths.getMusicPath().getWithDefault()).toArray()));
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

    setTestEnabled(false);
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
  private javax.swing.JLabel musicAuthor;
  private javax.swing.JLabel musicCopyright;
  private javax.swing.JLabel musicName;
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
    final javax.swing.JPanel pathSelectionPanel = new javax.swing.JPanel();
    pathSelectionComboBox = new javax.swing.JComboBox();
    final javax.swing.JButton jButton2 = new javax.swing.JButton();
    final javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
    pathSelectionList = new javax.swing.JList();
    final javax.swing.JPanel fileSelectionPanel = new javax.swing.JPanel();
    fileFilterComboBox = new javax.swing.JComboBox();
    final javax.swing.JButton fileSelectionButton = new javax.swing.JButton();
    final javax.swing.JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
    fileSelectionList = new javax.swing.JList();
    final javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
    final javax.swing.JPanel musicPanel = new javax.swing.JPanel();
    final javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
    musicName = new javax.swing.JLabel();
    musicAuthor = new javax.swing.JLabel();
    musicCopyright = new javax.swing.JLabel();
    final javax.swing.JPanel fileActionPanel = new javax.swing.JPanel();
    testButton = new javax.swing.JButton();
    final javax.swing.JPanel OkCancelPanel = new javax.swing.JPanel();
    final javax.swing.JPanel jPanel9 = new javax.swing.JPanel();
    final javax.swing.JButton okButton = new javax.swing.JButton();
    final javax.swing.JButton cancelButton = new javax.swing.JButton();

    setTitle("Music Selector");
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

    pathSelectionComboBox.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        pathSelection_ActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 3, 2);
    pathSelectionPanel.add(pathSelectionComboBox, gridBagConstraints);

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

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    mainPanel.add(fileSelectionPanel, gridBagConstraints);

    musicPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Music-File Info"));
    musicPanel.setLayout(new java.awt.GridBagLayout());

    jLabel2.setText("Name");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 3, 10);
    musicPanel.add(jLabel2, gridBagConstraints);

    jLabel3.setText("Author");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 3, 10);
    musicPanel.add(jLabel3, gridBagConstraints);

    jLabel4.setText("Copyright");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 10);
    musicPanel.add(jLabel4, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(5, 0, 3, 5);
    musicPanel.add(musicName, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 5);
    musicPanel.add(musicAuthor, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
    musicPanel.add(musicCopyright, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    mainPanel.add(musicPanel, gridBagConstraints);

    testButton.setMnemonic('T');
    testButton.setText("Test Music");
    testButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        testMusic_ActionPerformed(evt);
      }
    });
    fileActionPanel.add(testButton);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    mainPanel.add(fileActionPanel, gridBagConstraints);

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

  private void pathSelection_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_pathSelection_ActionPerformed
    if ((pathSelectionComboBox.getSelectedItem() != null)
        && (pathSelectionComboBox.getSelectedItem() instanceof String)) {
      path = (String) pathSelectionComboBox.getSelectedItem();
    }
    fillPathSelectionList();
  }// GEN-LAST:event_pathSelection_ActionPerformed

  private void fileFilterSelected_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_fileFilterSelected_ActionPerformed
    fillFileSelectionList();
  }// GEN-LAST:event_fileFilterSelected_ActionPerformed

  private void okActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_okActionPerformed
    closeAction = CloseAction.OK;
    dispose();
  }// GEN-LAST:event_okActionPerformed

  private void testMusic_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_testMusic_ActionPerformed
    final Music music = new Music();
    music.setName("TEST");

    log.info("");
    log.info("Filename:      " + getFilename());

    music.setFilename(getFilename());

    music.play();

    // Db.delete(music); // set music to transient
  }// GEN-LAST:event_testMusic_ActionPerformed

  protected String getFilename() {
    if ((fileSelectionList.getSelectedValue() != null)
        && (fileSelectionList.getSelectedValue() instanceof String)) {
      return Paths.pathStartingWithoutSeparator(Paths
          .pathEndingWithSeparator(getPathWithoutBasePath())
          + (String) fileSelectionList.getSelectedValue());
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
    fillFileSelectionList();
  }// GEN-LAST:event_pathSelected_ValueChanged

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
        Paths.getMusicPath().getWithDefault()).toArray()));
    fillPathSelectionList();
    fillFileSelectionList();
  }// GEN-LAST:event_editPaths_ActionPerformed

  private void fileSelected_ValueChanged(final javax.swing.event.ListSelectionEvent evt) {// GEN-FIRST:event_fileSelected_ValueChanged
    MusicInfo musicInfo;

    if ((fileSelectionList.getSelectedValue() != null)
        && (fileSelectionList.getSelectedValue() instanceof String)) {
      final String filename = (String) fileSelectionList.getSelectedValue();
      final String extension = FileTools.getExtension(filename);

      musicName.setText("");
      musicAuthor.setText("");
      musicCopyright.setText("");

      if (Emulators.getSupportedMusicExtensions().contains(extension)) {
        // Music
        log.info(filename + ": STATUS_MUSIC");

        try {
          musicInfo = Plugins.getMusicInfoForExtension(extension);
          musicInfo.load(new File(path, filename));
        } catch (final IOException ioe) {
          ioe.printStackTrace();
          musicInfo = null;
        }

        if (musicInfo != null) {
          musicName.setText(musicInfo.getName());
          musicAuthor.setText(musicInfo.getAuthor());
          musicCopyright.setText(musicInfo.getCopyright());
        }

        // disable contents and item panel
        setTestEnabled(true);

      } else {
        // NOTHING
        log.info(filename + ": STATUS_NOTHING");

        setTestEnabled(false);
      }

    }
  }// GEN-LAST:event_fileSelected_ValueChanged

  private void setTestEnabled(final boolean enabled) {
    testButton.setEnabled(enabled);
  }

  public CloseAction getCloseAction() {
    return closeAction;
  }
}
