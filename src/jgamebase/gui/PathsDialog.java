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

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;

import jgamebase.Const;
import jgamebase.JGameBase;
import jgamebase.model.Paths;
import jgamebase.tools.ListerTools;
import jgamebase.tools.StringTools;

/**
 * The GameBase GUI.
 * 
 * @author F. Gerbig (fgerbig@users.sourceforge.net)
 */
public class PathsDialog extends javax.swing.JDialog {

  /**
	 *
	 */
  private static final long serialVersionUID = 4374804155028317874L;

  public PathsDialog() {
    initComponents();
    this.setSize(400, 200);
    setLocationRelativeTo(null);
    setVisible(true);
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  protected javax.swing.JList extraPathsList;
  protected javax.swing.JList gamePathsList;
  protected javax.swing.JList musicPathsList;
  protected javax.swing.JTextField pathField;
  protected javax.swing.JLabel pathLabel;
  protected javax.swing.JPanel pathPanel;
  protected javax.swing.JList photoPathsList;
  protected javax.swing.JList screenshotPathsList;

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

    pathPanel = new javax.swing.JPanel();
    pathLabel = new javax.swing.JLabel();
    pathField = new javax.swing.JTextField();
    final javax.swing.JButton pathButton = new javax.swing.JButton();
    final javax.swing.JTabbedPane pathPane = new javax.swing.JTabbedPane();
    final javax.swing.JPanel gamePaths = new javax.swing.JPanel();
    final javax.swing.JPanel gamePathsPanel = new javax.swing.JPanel();
    final javax.swing.JButton gamePathsAddButton = new javax.swing.JButton();
    final javax.swing.JButton gamePathsRemoveButton = new javax.swing.JButton();
    final javax.swing.JButton gamePathsUpButton = new javax.swing.JButton();
    final javax.swing.JButton gamePathsDownButton = new javax.swing.JButton();
    final javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
    gamePathsList = new javax.swing.JList();
    final javax.swing.JPanel gameDefaultPanel = new javax.swing.JPanel();
    final javax.swing.JLabel gamePathsDefaultRWLabel = new javax.swing.JLabel();
    final javax.swing.JLabel gamePathsDefaultROLabel = new javax.swing.JLabel();
    final javax.swing.JPanel musicPaths = new javax.swing.JPanel();
    final javax.swing.JPanel musicPathsPanel = new javax.swing.JPanel();
    final javax.swing.JButton musicPathsAddButton = new javax.swing.JButton();
    final javax.swing.JButton musicPathsRemoveButton = new javax.swing.JButton();
    final javax.swing.JButton musicPathsUpButton = new javax.swing.JButton();
    final javax.swing.JButton musicPathsDownButton = new javax.swing.JButton();
    final javax.swing.JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
    musicPathsList = new javax.swing.JList();
    final javax.swing.JPanel musicDefaultPanel = new javax.swing.JPanel();
    final javax.swing.JLabel musicPathsDefaultRWLabel = new javax.swing.JLabel();
    final javax.swing.JLabel musicPathsDefaultROLabel = new javax.swing.JLabel();
    final javax.swing.JPanel screenshotPaths = new javax.swing.JPanel();
    final javax.swing.JPanel screenshotPathsPanel = new javax.swing.JPanel();
    final javax.swing.JButton screenshotPathsAddButton = new javax.swing.JButton();
    final javax.swing.JButton screenshotPathsRemoveButton = new javax.swing.JButton();
    final javax.swing.JButton screenshotPathsUpButton = new javax.swing.JButton();
    final javax.swing.JButton screenshotPathsDownButton = new javax.swing.JButton();
    final javax.swing.JScrollPane jScrollPane3 = new javax.swing.JScrollPane();
    screenshotPathsList = new javax.swing.JList();
    final javax.swing.JPanel screenshotDefaultPanel = new javax.swing.JPanel();
    final javax.swing.JLabel screenshotPathsDefaultRWLabel = new javax.swing.JLabel();
    final javax.swing.JLabel screenshotPathsDefaultROLabel = new javax.swing.JLabel();
    final javax.swing.JPanel extraPaths = new javax.swing.JPanel();
    final javax.swing.JPanel extraPathsPanel = new javax.swing.JPanel();
    final javax.swing.JButton extraPathsAddButton = new javax.swing.JButton();
    final javax.swing.JButton extraPathsRemoveButton = new javax.swing.JButton();
    final javax.swing.JButton extraPathsUpButton = new javax.swing.JButton();
    final javax.swing.JButton extraPathsDownButton = new javax.swing.JButton();
    final javax.swing.JScrollPane jScrollPane4 = new javax.swing.JScrollPane();
    extraPathsList = new javax.swing.JList();
    final javax.swing.JPanel extraDefaultPanel = new javax.swing.JPanel();
    final javax.swing.JLabel extraPathsDefaultRWLabel = new javax.swing.JLabel();
    final javax.swing.JLabel extraPathsDefaultROLabel = new javax.swing.JLabel();
    final javax.swing.JPanel photoPaths = new javax.swing.JPanel();
    final javax.swing.JPanel photoPathsPanel = new javax.swing.JPanel();
    final javax.swing.JButton photoPathsAddButton = new javax.swing.JButton();
    final javax.swing.JButton photoPathsRemoveButton = new javax.swing.JButton();
    final javax.swing.JButton photoPathsUpButton = new javax.swing.JButton();
    final javax.swing.JButton photoPathsDownButton = new javax.swing.JButton();
    final javax.swing.JScrollPane jScrollPane5 = new javax.swing.JScrollPane();
    photoPathsList = new javax.swing.JList();
    final javax.swing.JPanel photoDefaultPanel = new javax.swing.JPanel();
    final javax.swing.JLabel photoPathsDefaultRWLabel = new javax.swing.JLabel();
    final javax.swing.JLabel photoPathsDefaultROLabel = new javax.swing.JLabel();
    final javax.swing.JPanel OkCancelPanel = new javax.swing.JPanel();
    final javax.swing.JPanel jPanel9 = new javax.swing.JPanel();
    final javax.swing.JButton okButton = new javax.swing.JButton();
    final javax.swing.JButton cancelButton = new javax.swing.JButton();

    pathPanel.setMaximumSize(new java.awt.Dimension(344, 25));
    pathPanel.setPreferredSize(new java.awt.Dimension(344, 25));
    pathPanel.setLayout(new java.awt.GridBagLayout());

    pathLabel.setText("Path:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    pathPanel.add(pathLabel, gridBagConstraints);

    pathField.setMinimumSize(new java.awt.Dimension(320, 20));
    pathField.setPreferredSize(new java.awt.Dimension(320, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    pathPanel.add(pathField, gridBagConstraints);

    pathButton.setText("...");
    pathButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        try {
          final JFileChooser fileChooser = new JFileChooser();
          fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

          if (!pathField.getText().isEmpty()) {
            fileChooser.setCurrentDirectory(new File(pathField.getText()));
          }

          if (fileChooser.showOpenDialog(null) == javax.swing.JFileChooser.APPROVE_OPTION) {
            pathField.setText(fileChooser.getSelectedFile().getPath());
          }
        } catch (final Exception e) {
          e.printStackTrace();
        }
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    pathPanel.add(pathButton, gridBagConstraints);

    setTitle("Paths");
    setMinimumSize(new java.awt.Dimension(400, 280));
    setModal(true);
    addWindowListener(new java.awt.event.WindowAdapter() {
      @Override
      public void windowClosing(final java.awt.event.WindowEvent evt) {
        exitFormWindowClosing(evt);
      }
    });

    gamePaths.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
    gamePaths.setLayout(new java.awt.BorderLayout());

    gamePathsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

    gamePathsAddButton.setToolTipText("Add new path.");
    gamePathsAddButton.setBorder(null);
    gamePathsAddButton.setIcon(Const.ICON_PATHSELECTOR_ADD);
    gamePathsAddButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        gamePathsAddActionPerformed(evt);
      }
    });
    gamePathsPanel.add(gamePathsAddButton);

    gamePathsRemoveButton.setToolTipText("Remove selected path.");
    gamePathsRemoveButton.setBorder(null);
    gamePathsRemoveButton.setIcon(Const.ICON_PATHSELECTOR_REMOVE);
    gamePathsRemoveButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        gamePathsRemoveActionPerformed(evt);
      }
    });
    gamePathsPanel.add(gamePathsRemoveButton);

    gamePathsUpButton.setToolTipText("Move selected path up.");
    gamePathsUpButton.setBorder(null);
    gamePathsUpButton.setIcon(Const.ICON_PATHSELECTOR_UP);
    gamePathsUpButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        gamePathsUpActionPerformed(evt);
      }
    });
    gamePathsPanel.add(gamePathsUpButton);

    gamePathsDownButton.setToolTipText("Move selected path down.");
    gamePathsDownButton.setBorder(null);
    gamePathsDownButton.setIcon(Const.ICON_PATHSELECTOR_DOWN);
    gamePathsDownButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        gamePathsDownActionPerformed(evt);
      }
    });
    gamePathsPanel.add(gamePathsDownButton);

    gamePaths.add(gamePathsPanel, java.awt.BorderLayout.NORTH);

    gamePathsList.setModel(new ListListModel(ListerTools.fileListToStringList(Paths.getGamePath()
        .get())));
    gamePathsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    if (gamePathsList.getModel().getSize() > 0) {
      gamePathsList.setSelectedIndex(0);
    }
    gamePathsList.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(final java.awt.event.MouseEvent evt) {
        gamePathsListMouseClicked(evt);
      }
    });
    jScrollPane1.setViewportView(gamePathsList);

    gamePaths.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    gameDefaultPanel.setLayout(new java.awt.GridLayout(2, 1));

    if (Const.FHS) {
      gamePathsDefaultRWLabel.setToolTipText("The writable default path.");
    } else {
      gamePathsDefaultRWLabel.setToolTipText("The default path.");
    }
    gamePathsDefaultRWLabel.setText(Paths.getGamePath().getDefault_rw().toString());
    gameDefaultPanel.add(gamePathsDefaultRWLabel);

    if (Const.FHS) {
      gamePathsDefaultROLabel.setToolTipText("The read-only default path.");
      gamePathsDefaultROLabel.setText(Paths.getGamePath().getDefault_ro().toString());
    }
    gameDefaultPanel.add(gamePathsDefaultROLabel);

    gamePaths.add(gameDefaultPanel, java.awt.BorderLayout.SOUTH);

    pathPane.addTab("Games", gamePaths);

    musicPaths.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
    musicPaths.setLayout(new java.awt.BorderLayout());

    musicPathsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

    musicPathsAddButton.setToolTipText("Add new path.");
    musicPathsAddButton.setBorder(null);
    musicPathsAddButton.setIcon(Const.ICON_PATHSELECTOR_ADD);
    musicPathsAddButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        musicPathsAddActionPerformed(evt);
      }
    });
    musicPathsPanel.add(musicPathsAddButton);

    musicPathsRemoveButton.setToolTipText("Remove selected path.");
    musicPathsRemoveButton.setBorder(null);
    musicPathsRemoveButton.setIcon(Const.ICON_PATHSELECTOR_REMOVE);
    musicPathsRemoveButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        musicPathsRemoveActionPerformed(evt);
      }
    });
    musicPathsPanel.add(musicPathsRemoveButton);

    musicPathsUpButton.setToolTipText("Move selected path up.");
    musicPathsUpButton.setBorder(null);
    musicPathsUpButton.setIcon(Const.ICON_PATHSELECTOR_UP);
    musicPathsUpButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        musicPathsUpActionPerformed(evt);
      }
    });
    musicPathsPanel.add(musicPathsUpButton);

    musicPathsDownButton.setToolTipText("Move selected path down.");
    musicPathsDownButton.setBorder(null);
    musicPathsDownButton.setIcon(Const.ICON_PATHSELECTOR_DOWN);
    musicPathsDownButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        musicPathsDownActionPerformed(evt);
      }
    });
    musicPathsPanel.add(musicPathsDownButton);

    musicPaths.add(musicPathsPanel, java.awt.BorderLayout.NORTH);

    musicPathsList.setModel(new ListListModel(ListerTools.fileListToStringList(Paths.getMusicPath()
        .get())));
    musicPathsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    if (musicPathsList.getModel().getSize() > 0) {
      musicPathsList.setSelectedIndex(0);
    }
    musicPathsList.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(final java.awt.event.MouseEvent evt) {
        musicPathsListMouseClicked(evt);
      }
    });
    jScrollPane2.setViewportView(musicPathsList);

    musicPaths.add(jScrollPane2, java.awt.BorderLayout.CENTER);

    musicDefaultPanel.setLayout(new java.awt.GridLayout(2, 1));

    if (Const.FHS) {
      musicPathsDefaultRWLabel.setToolTipText("The writable default path.");
    } else {
      musicPathsDefaultRWLabel.setToolTipText("The default path.");
    }
    musicPathsDefaultRWLabel.setText(Paths.getMusicPath().getDefault_rw().toString());
    musicDefaultPanel.add(musicPathsDefaultRWLabel);

    if (Const.FHS) {
      musicPathsDefaultROLabel.setToolTipText("The read-only default path.");
      musicPathsDefaultROLabel.setText(Paths.getMusicPath().getDefault_ro().toString());
    }
    musicDefaultPanel.add(musicPathsDefaultROLabel);

    musicPaths.add(musicDefaultPanel, java.awt.BorderLayout.SOUTH);

    pathPane.addTab("Music", musicPaths);

    screenshotPaths.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
    screenshotPaths.setLayout(new java.awt.BorderLayout());

    screenshotPathsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

    screenshotPathsAddButton.setToolTipText("Add new path.");
    screenshotPathsAddButton.setBorder(null);
    screenshotPathsAddButton.setIcon(Const.ICON_PATHSELECTOR_ADD);
    screenshotPathsAddButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        screenshotPathsAddActionPerformed(evt);
      }
    });
    screenshotPathsPanel.add(screenshotPathsAddButton);

    screenshotPathsRemoveButton.setToolTipText("Remove selected path.");
    screenshotPathsRemoveButton.setBorder(null);
    screenshotPathsRemoveButton.setIcon(Const.ICON_PATHSELECTOR_REMOVE);
    screenshotPathsRemoveButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        screenshotPathsRemoveActionPerformed(evt);
      }
    });
    screenshotPathsPanel.add(screenshotPathsRemoveButton);

    screenshotPathsUpButton.setToolTipText("Move selected path up.");
    screenshotPathsUpButton.setBorder(null);
    screenshotPathsUpButton.setIcon(Const.ICON_PATHSELECTOR_UP);
    screenshotPathsUpButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        screenshotPathsUpActionPerformed(evt);
      }
    });
    screenshotPathsPanel.add(screenshotPathsUpButton);

    screenshotPathsDownButton.setToolTipText("Move selected path down.");
    screenshotPathsDownButton.setBorder(null);
    screenshotPathsDownButton.setIcon(Const.ICON_PATHSELECTOR_DOWN);
    screenshotPathsDownButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        screenshotPathsDownActionPerformed(evt);
      }
    });
    screenshotPathsPanel.add(screenshotPathsDownButton);

    screenshotPaths.add(screenshotPathsPanel, java.awt.BorderLayout.NORTH);

    screenshotPathsList.setModel(new ListListModel(ListerTools.fileListToStringList(Paths
        .getScreenshotPath().get())));
    screenshotPathsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    screenshotPathsList.setMinimumSize(new java.awt.Dimension(400, 100));
    if (screenshotPathsList.getModel().getSize() > 0) {
      screenshotPathsList.setSelectedIndex(0);
    }
    screenshotPathsList.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(final java.awt.event.MouseEvent evt) {
        screenshotPathsListMouseClicked(evt);
      }
    });
    jScrollPane3.setViewportView(screenshotPathsList);

    screenshotPaths.add(jScrollPane3, java.awt.BorderLayout.CENTER);

    screenshotDefaultPanel.setLayout(new java.awt.GridLayout(2, 1));

    if (Const.FHS) {
      screenshotPathsDefaultRWLabel.setToolTipText("The writable default path.");
    } else {
      screenshotPathsDefaultRWLabel.setToolTipText("The default path.");
    }
    screenshotPathsDefaultRWLabel.setText(Paths.getScreenshotPath().getDefault_rw().toString());
    screenshotDefaultPanel.add(screenshotPathsDefaultRWLabel);

    if (Const.FHS) {
      screenshotPathsDefaultROLabel.setToolTipText("The read-only default path.");
      screenshotPathsDefaultROLabel.setText(Paths.getScreenshotPath().getDefault_ro().toString());
    }
    screenshotDefaultPanel.add(screenshotPathsDefaultROLabel);

    screenshotPaths.add(screenshotDefaultPanel, java.awt.BorderLayout.SOUTH);

    pathPane.addTab("Screenshots", screenshotPaths);

    extraPaths.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
    extraPaths.setLayout(new java.awt.BorderLayout());

    extraPathsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

    extraPathsAddButton.setToolTipText("Add new path.");
    extraPathsAddButton.setBorder(null);
    extraPathsAddButton.setIcon(Const.ICON_PATHSELECTOR_ADD);
    extraPathsAddButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        extraPathsAddActionPerformed(evt);
      }
    });
    extraPathsPanel.add(extraPathsAddButton);

    extraPathsRemoveButton.setIcon(Const.ICON_PATHSELECTOR_REMOVE);
    extraPathsRemoveButton.setToolTipText("Remove selected path.");
    extraPathsRemoveButton.setBorder(null);
    extraPathsRemoveButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        extraPathsRemoveActionPerformed(evt);
      }
    });
    extraPathsPanel.add(extraPathsRemoveButton);

    extraPathsUpButton.setToolTipText("Move selected path up.");
    extraPathsUpButton.setBorder(null);
    extraPathsUpButton.setIcon(Const.ICON_PATHSELECTOR_UP);
    extraPathsUpButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        extraPathsUpActionPerformed(evt);
      }
    });
    extraPathsPanel.add(extraPathsUpButton);

    extraPathsDownButton.setToolTipText("Move selected path down.");
    extraPathsDownButton.setBorder(null);
    extraPathsDownButton.setIcon(Const.ICON_PATHSELECTOR_DOWN);
    extraPathsDownButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        extraPathsDownActionPerformed(evt);
      }
    });
    extraPathsPanel.add(extraPathsDownButton);

    extraPaths.add(extraPathsPanel, java.awt.BorderLayout.NORTH);

    extraPathsList.setModel(new ListListModel(ListerTools.fileListToStringList(Paths.getExtraPath()
        .get())));
    extraPathsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    if (extraPathsList.getModel().getSize() > 0) {
      extraPathsList.setSelectedIndex(0);
    }
    extraPathsList.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(final java.awt.event.MouseEvent evt) {
        extraPathsListMouseClicked(evt);
      }
    });
    jScrollPane4.setViewportView(extraPathsList);

    extraPaths.add(jScrollPane4, java.awt.BorderLayout.CENTER);

    extraDefaultPanel.setLayout(new java.awt.GridLayout(2, 1));

    if (Const.FHS) {
      extraPathsDefaultRWLabel.setToolTipText("The writable default path.");
    } else {
      extraPathsDefaultRWLabel.setToolTipText("The default path.");
    }
    extraPathsDefaultRWLabel.setText(Paths.getExtraPath().getDefault_rw().toString());
    extraDefaultPanel.add(extraPathsDefaultRWLabel);

    if (Const.FHS) {
      extraPathsDefaultROLabel.setToolTipText("The read-only default path.");
      extraPathsDefaultROLabel.setText(Paths.getExtraPath().getDefault_ro().toString());
    }
    extraDefaultPanel.add(extraPathsDefaultROLabel);

    extraPaths.add(extraDefaultPanel, java.awt.BorderLayout.SOUTH);

    pathPane.addTab("Extras", extraPaths);

    photoPaths.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
    photoPaths.setLayout(new java.awt.BorderLayout());

    photoPathsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

    photoPathsAddButton.setToolTipText("Add new path.");
    photoPathsAddButton.setBorder(null);
    photoPathsAddButton.setIcon(Const.ICON_PATHSELECTOR_ADD);
    photoPathsAddButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        photoPathsAddActionPerformed(evt);
      }
    });
    photoPathsPanel.add(photoPathsAddButton);

    photoPathsRemoveButton.setToolTipText("Remove selected path.");
    photoPathsRemoveButton.setBorder(null);
    photoPathsRemoveButton.setIcon(Const.ICON_PATHSELECTOR_REMOVE);
    photoPathsRemoveButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        photoPathsRemoveActionPerformed(evt);
      }
    });
    photoPathsPanel.add(photoPathsRemoveButton);

    photoPathsUpButton.setToolTipText("Move selected path up.");
    photoPathsUpButton.setBorder(null);
    photoPathsUpButton.setIcon(Const.ICON_PATHSELECTOR_UP);
    photoPathsUpButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        photoPathsUpActionPerformed(evt);
      }
    });
    photoPathsPanel.add(photoPathsUpButton);

    photoPathsDownButton.setToolTipText("Move selected path down.");
    photoPathsDownButton.setBorder(null);
    photoPathsDownButton.setIcon(Const.ICON_PATHSELECTOR_DOWN);
    photoPathsDownButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        photoPathsDownActionPerformed(evt);
      }
    });
    photoPathsPanel.add(photoPathsDownButton);

    photoPaths.add(photoPathsPanel, java.awt.BorderLayout.NORTH);

    photoPathsList.setModel(new ListListModel(ListerTools.fileListToStringList(Paths.getPhotoPath()
        .get())));
    photoPathsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    if (photoPathsList.getModel().getSize() > 0) {
      photoPathsList.setSelectedIndex(0);
    }
    photoPathsList.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(final java.awt.event.MouseEvent evt) {
        photoPathsListMouseClicked(evt);
      }
    });
    jScrollPane5.setViewportView(photoPathsList);

    photoPaths.add(jScrollPane5, java.awt.BorderLayout.CENTER);

    photoDefaultPanel.setLayout(new java.awt.GridLayout(2, 1));

    if (Const.FHS) {
      photoPathsDefaultRWLabel.setToolTipText("The writable default path.");
    } else {
      photoPathsDefaultRWLabel.setToolTipText("The default path.");
    }
    photoPathsDefaultRWLabel.setText(Paths.getPhotoPath().getDefault_rw().toString());
    photoDefaultPanel.add(photoPathsDefaultRWLabel);

    if (Const.FHS) {
      photoPathsDefaultROLabel.setToolTipText("The read-only default path.");
      photoPathsDefaultROLabel.setText(Paths.getPhotoPath().getDefault_ro().toString());
    }
    photoDefaultPanel.add(photoPathsDefaultROLabel);

    photoPaths.add(photoDefaultPanel, java.awt.BorderLayout.SOUTH);

    pathPane.addTab("Photos", photoPaths);

    getContentPane().add(pathPane, java.awt.BorderLayout.CENTER);

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

    final java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    setBounds((screenSize.width - 410) / 2, (screenSize.height - 230) / 2, 410, 230);
  }// </editor-fold>//GEN-END:initComponents

  private void musicPathsAddActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_musicPathsAddActionPerformed
    addPath(musicPathsList, "music");
  }// GEN-LAST:event_musicPathsAddActionPerformed

  private void musicPathsRemoveActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_musicPathsRemoveActionPerformed
    ListListMethods.removeSelected(musicPathsList);
  }// GEN-LAST:event_musicPathsRemoveActionPerformed

  private void musicPathsUpActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_musicPathsUpActionPerformed
    ListListMethods.moveSelectedUp(musicPathsList);
  }// GEN-LAST:event_musicPathsUpActionPerformed

  private void musicPathsDownActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_musicPathsDownActionPerformed
    ListListMethods.moveSelectedDown(musicPathsList);
  }// GEN-LAST:event_musicPathsDownActionPerformed

  private void musicPathsListMouseClicked(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_musicPathsListMouseClicked
    if (Gui.doubleClick(evt)) {
      editPath(musicPathsList, "music");
    }
  }// GEN-LAST:event_musicPathsListMouseClicked

  private void photoPathsAddActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_photoPathsAddActionPerformed
    addPath(photoPathsList, "photo");
  }// GEN-LAST:event_photoPathsAddActionPerformed

  private void photoPathsRemoveActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_photoPathsRemoveActionPerformed
    ListListMethods.removeSelected(photoPathsList);
  }// GEN-LAST:event_photoPathsRemoveActionPerformed

  private void photoPathsUpActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_photoPathsUpActionPerformed
    ListListMethods.moveSelectedUp(photoPathsList);
  }// GEN-LAST:event_photoPathsUpActionPerformed

  private void photoPathsDownActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_photoPathsDownActionPerformed
    ListListMethods.moveSelectedDown(photoPathsList);
  }// GEN-LAST:event_photoPathsDownActionPerformed

  private void photoPathsListMouseClicked(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_photoPathsListMouseClicked
    if (Gui.doubleClick(evt)) {
      editPath(photoPathsList, "photo");
    }
  }// GEN-LAST:event_photoPathsListMouseClicked

  private void extraPathsListMouseClicked(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_extraPathsListMouseClicked
    if (Gui.doubleClick(evt)) {
      editPath(extraPathsList, "extra");
    }
  }// GEN-LAST:event_extraPathsListMouseClicked

  private void extraPathsDownActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_extraPathsDownActionPerformed
    ListListMethods.moveSelectedDown(extraPathsList);
  }// GEN-LAST:event_extraPathsDownActionPerformed

  private void extraPathsUpActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_extraPathsUpActionPerformed
    ListListMethods.moveSelectedUp(extraPathsList);
  }// GEN-LAST:event_extraPathsUpActionPerformed

  private void extraPathsRemoveActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_extraPathsRemoveActionPerformed
    ListListMethods.removeSelected(extraPathsList);
  }// GEN-LAST:event_extraPathsRemoveActionPerformed

  private void extraPathsAddActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_extraPathsAddActionPerformed
    addPath(extraPathsList, "extra");
  }// GEN-LAST:event_extraPathsAddActionPerformed

  private void screenshotPathsListMouseClicked(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_screenshotPathsListMouseClicked
    if (Gui.doubleClick(evt)) {
      editPath(screenshotPathsList, "screenshot");
    }
  }// GEN-LAST:event_screenshotPathsListMouseClicked

  private void screenshotPathsDownActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_screenshotPathsDownActionPerformed
    ListListMethods.moveSelectedDown(screenshotPathsList);
  }// GEN-LAST:event_screenshotPathsDownActionPerformed

  private void screenshotPathsUpActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_screenshotPathsUpActionPerformed
    ListListMethods.moveSelectedUp(screenshotPathsList);
  }// GEN-LAST:event_screenshotPathsUpActionPerformed

  private void screenshotPathsRemoveActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_screenshotPathsRemoveActionPerformed
    ListListMethods.removeSelected(screenshotPathsList);
  }// GEN-LAST:event_screenshotPathsRemoveActionPerformed

  private void screenshotPathsAddActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_screenshotPathsAddActionPerformed
    addPath(screenshotPathsList, "screenshot");
  }// GEN-LAST:event_screenshotPathsAddActionPerformed

  private void cancelActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cancelActionPerformed
    dispose();
  }// GEN-LAST:event_cancelActionPerformed

  @SuppressWarnings("unchecked")
  private void okActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_okActionPerformed
    Paths.getGamePath().set(
        ListerTools.stringListToFileList(((ListListModel) gamePathsList.getModel()).get()));
    Paths.getMusicPath().set(
        ListerTools.stringListToFileList(((ListListModel) musicPathsList.getModel()).get()));
    Paths.getScreenshotPath().set(
        ListerTools.stringListToFileList(((ListListModel) screenshotPathsList.getModel()).get()));
    Paths.getExtraPath().set(
        ListerTools.stringListToFileList(((ListListModel) extraPathsList.getModel()).get()));
    Paths.getPhotoPath().set(
        ListerTools.stringListToFileList(((ListListModel) photoPathsList.getModel()).get()));
    Paths.writeToIniFile();
    dispose();
  }// GEN-LAST:event_okActionPerformed

  private void gamePathsDownActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_gamePathsDownActionPerformed
    ListListMethods.moveSelectedDown(gamePathsList);
  }// GEN-LAST:event_gamePathsDownActionPerformed

  private void gamePathsUpActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_gamePathsUpActionPerformed
    ListListMethods.moveSelectedUp(gamePathsList);
  }// GEN-LAST:event_gamePathsUpActionPerformed

  private void gamePathsRemoveActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_gamePathsRemoveActionPerformed
    ListListMethods.removeSelected(gamePathsList);
  }// GEN-LAST:event_gamePathsRemoveActionPerformed

  private void gamePathsListMouseClicked(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_gamePathsListMouseClicked
    if (Gui.doubleClick(evt)) {
      editPath(gamePathsList, "game");
    }
  }// GEN-LAST:event_gamePathsListMouseClicked

  private void editPath(final JList list, final String name) {
    final int row = list.getSelectedIndex();

    if ((row >= 0) && (row < list.getModel().getSize())) {
      pathLabel.setText(StringTools.startWithUpperCase(name) + " path:");
      pathField.setText((String) list.getSelectedValue());

      if (JOptionPane.showOptionDialog(JGameBase.getGui(), pathPanel, "Edit " + name + " path",
          JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null) == JOptionPane.OK_OPTION) {

        ((ListListModel) list.getModel()).set(row, pathField.getText());
      }
    }
  }

  private void gamePathsAddActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_gamePathsAddActionPerformed
    addPath(gamePathsList, "game");
  }// GEN-LAST:event_gamePathsAddActionPerformed

  private void addPath(final JList list, final String name) {
    pathLabel.setText(StringTools.startWithUpperCase(name) + " path:");
    pathField.setText("");

    if (JOptionPane.showOptionDialog(JGameBase.getGui(), pathPanel, "Add " + name + " path",
        JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null) == JOptionPane.OK_OPTION) {

      ((ListListModel) list.getModel()).addElement(pathField.getText());
    }
  }

  private void exitFormWindowClosing(final java.awt.event.WindowEvent evt) {// GEN-FIRST:event_exitFormWindowClosing
    dispose();
  }// GEN-LAST:event_exitFormWindowClosing
}