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

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

import jgamebase.Const;
import jgamebase.db.Db;
import jgamebase.db.model.Cracker;
import jgamebase.db.model.Game;
import jgamebase.db.model.Item;
import jgamebase.db.model.ItemViewFilter;
import jgamebase.db.model.Selection;

/**
 * The GameBase GUI.
 * 
 * @author F. Gerbig (fgerbig@users.sourceforge.net)
 */
public class VersionInfoDialog extends javax.swing.JDialog {
  /**
	 * 
	 */
  private static final long serialVersionUID = -7743402150436285214L;

  List<Item> list;

  Game game;

  boolean isList;

  Cracker cracker;

  public VersionInfoDialog(final Game game) {
    isList = false;
    this.game = game;

    initComponents();
    this.setSize(400, 480);

    selectIncludes(true); // select all fields
    propertiesPane.remove(0); // hide includePane

    cracker = game.getCracker();
    crackerTextField.setText(cracker.getName());

    noOfTrainersComboBox.setModel(new DefaultComboBoxModel(new ItemViewFilter("Trainers",
        ItemViewFilter.SELECTOR_OTHER, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Games", "V_Trainers", "", "").getSelections().toArray()));
    for (int i = 0; i < noOfTrainersComboBox.getModel().getSize(); i++) {
      if (((Selection) noOfTrainersComboBox.getModel().getElementAt(i)).getValue().equals(
          String.valueOf(game.getTrainer()))) {
        noOfTrainersComboBox.setSelectedIndex(i);
      }
    }

    palNtscComboBox.setModel(new DefaultComboBoxModel(Const.FORDISPLAY_PALNTSC));
    palNtscComboBox.setSelectedIndex(game.getPalNtsc());

    lengthTextField.setText(String.valueOf(Math.abs(game.getLength())));
    lengthComboBox.setModel(new DefaultComboBoxModel(Const.FORDISPLAY_LENGTHTYPE));
    lengthComboBox.setSelectedIndex(game.getLengthType());

    highScoreSaverCheckbox.setSelected(game.getHasHighscoreSaver());
    loadingScreenCheckbox.setSelected(game.getHasLoadingScreen());
    includedDocsCheckbox.setSelected(game.getHasIncludedDocs());
    trueDriveEmulationCheckbox.setSelected(game.getNeedsTruedriveEmu());

    versionCommentTextField.setText(game.getVersionComment());

    setLocationRelativeTo(null);
    setVisible(true);
  }

  public VersionInfoDialog(final List<Item> list) {
    isList = true;
    this.list = list;
    initComponents();
    this.setSize(400, 480);

    selectIncludes(false); // select none fields

    cracker = Db.getCrackerById(Cracker.NEUTRAL_ID);
    crackerTextField.setText(cracker.getName());

    noOfTrainersComboBox.setModel(new DefaultComboBoxModel(new ItemViewFilter("Cracker",
        ItemViewFilter.SELECTOR_OTHER, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Games", "V_Trainers", "", "").getSelections().toArray()));
    palNtscComboBox.setModel(new DefaultComboBoxModel(Const.FORDISPLAY_PALNTSC));
    palNtscComboBox.setSelectedIndex(3);
    lengthComboBox.setModel(new DefaultComboBoxModel(Const.FORDISPLAY_LENGTHTYPE));

    setVisible(true);
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JPanel chooseCrackerPanel;
  private javax.swing.JButton crackerButton;
  private javax.swing.JTextField crackerTextField;
  private javax.swing.JList crackersList;
  private javax.swing.JCheckBox highScoreSaverCheckbox;
  private javax.swing.JCheckBox includeCrackerCheckbox;
  private javax.swing.JCheckBox includeHighScoreSaverCheckbox;
  private javax.swing.JCheckBox includeIncludedDocsCheckbox;
  private javax.swing.JCheckBox includeLengthCheckbox;
  private javax.swing.JCheckBox includeLoadingScreenCheckbox;
  private javax.swing.JCheckBox includeNoOfTrainersCheckbox;
  private javax.swing.JCheckBox includePalNtscCheckbox;
  private javax.swing.JCheckBox includeTrueDriveEmulationCheckbox;
  private javax.swing.JCheckBox includeVersionCommentCheckbox;
  private javax.swing.JCheckBox includedDocsCheckbox;
  private javax.swing.JComboBox lengthComboBox;
  private javax.swing.JTextField lengthTextField;
  private javax.swing.JCheckBox loadingScreenCheckbox;
  private javax.swing.JComboBox noOfTrainersComboBox;
  private javax.swing.JComboBox palNtscComboBox;
  private javax.swing.JTabbedPane propertiesPane;
  private javax.swing.JCheckBox trueDriveEmulationCheckbox;
  private javax.swing.JTextField versionCommentTextField;

  // End of variables declaration//GEN-END:variables

  /**
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed"
  // <editor-fold defaultstate="collapsed"
  // desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    chooseCrackerPanel = new javax.swing.JPanel();
    final javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
    crackersList = new javax.swing.JList();
    final javax.swing.JButton newCrackerButton = new javax.swing.JButton();
    final javax.swing.JButton editCrackerButton = new javax.swing.JButton();
    propertiesPane = new javax.swing.JTabbedPane();
    final javax.swing.JPanel includePanel = new javax.swing.JPanel();
    final javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
    includeCrackerCheckbox = new javax.swing.JCheckBox();
    includeTrueDriveEmulationCheckbox = new javax.swing.JCheckBox();
    includeNoOfTrainersCheckbox = new javax.swing.JCheckBox();
    includeLoadingScreenCheckbox = new javax.swing.JCheckBox();
    includeHighScoreSaverCheckbox = new javax.swing.JCheckBox();
    includeIncludedDocsCheckbox = new javax.swing.JCheckBox();
    includeLengthCheckbox = new javax.swing.JCheckBox();
    includeVersionCommentCheckbox = new javax.swing.JCheckBox();
    includePalNtscCheckbox = new javax.swing.JCheckBox();
    final javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
    final javax.swing.JButton includeAllButton = new javax.swing.JButton();
    final javax.swing.JButton includeNoneButton = new javax.swing.JButton();
    final javax.swing.JPanel fieldsPanel = new javax.swing.JPanel();
    final javax.swing.JLabel crackerLabel = new javax.swing.JLabel();
    noOfTrainersComboBox = new javax.swing.JComboBox();
    final javax.swing.JLabel noOfTrainersLabel = new javax.swing.JLabel();
    crackerTextField = new javax.swing.JTextField();
    crackerButton = new javax.swing.JButton();
    final javax.swing.JLabel lengthLabel = new javax.swing.JLabel();
    final javax.swing.JLabel palNtscLabel = new javax.swing.JLabel();
    palNtscComboBox = new javax.swing.JComboBox();
    highScoreSaverCheckbox = new javax.swing.JCheckBox();
    final javax.swing.JLabel versionCommentLabel = new javax.swing.JLabel();
    versionCommentTextField = new javax.swing.JTextField();
    loadingScreenCheckbox = new javax.swing.JCheckBox();
    includedDocsCheckbox = new javax.swing.JCheckBox();
    trueDriveEmulationCheckbox = new javax.swing.JCheckBox();
    lengthTextField = new javax.swing.JTextField();
    lengthComboBox = new javax.swing.JComboBox();
    final javax.swing.JPanel jPanel3 = new javax.swing.JPanel();
    final javax.swing.JPanel OkCancelPanel = new javax.swing.JPanel();
    final javax.swing.JPanel jPanel9 = new javax.swing.JPanel();
    final javax.swing.JButton okButton = new javax.swing.JButton();
    final javax.swing.JButton cancelButton = new javax.swing.JButton();

    chooseCrackerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Publishers"));
    chooseCrackerPanel.setLayout(new java.awt.GridBagLayout());

    crackersList.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
    crackersList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    jScrollPane1.setViewportView(crackersList);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridheight = 5;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.insets = new java.awt.Insets(3, 5, 5, 5);
    chooseCrackerPanel.add(jScrollPane1, gridBagConstraints);

    newCrackerButton.setMnemonic('N');
    newCrackerButton.setText("New");
    newCrackerButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        newCrackerActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 5);
    chooseCrackerPanel.add(newCrackerButton, gridBagConstraints);

    editCrackerButton.setMnemonic('E');
    editCrackerButton.setText("Edit");
    editCrackerButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        editCrackerActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
    chooseCrackerPanel.add(editCrackerButton, gridBagConstraints);

    setTitle("Version Info Properties...");
    setModal(true);
    setResizable(false);
    addWindowListener(new java.awt.event.WindowAdapter() {
      @Override
      public void windowClosing(final java.awt.event.WindowEvent evt) {
        exitFormWindowClosing(evt);
      }
    });

    includePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
    includePanel.setLayout(new java.awt.GridLayout(2, 1));

    jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Fields to update"));
    jPanel1.setLayout(new java.awt.GridLayout(5, 2));

    includeCrackerCheckbox.setMnemonic('C');
    includeCrackerCheckbox.setText("Cracker");
    includeCrackerCheckbox.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        includesChangedActionPerformed(evt);
      }
    });
    jPanel1.add(includeCrackerCheckbox);

    includeTrueDriveEmulationCheckbox.setMnemonic('T');
    includeTrueDriveEmulationCheckbox.setText("True Drive Emulation");
    includeTrueDriveEmulationCheckbox.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        includesChangedActionPerformed(evt);
      }
    });
    jPanel1.add(includeTrueDriveEmulationCheckbox);

    includeNoOfTrainersCheckbox.setMnemonic('N');
    includeNoOfTrainersCheckbox.setText("Number of Trainers");
    includeNoOfTrainersCheckbox.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        includesChangedActionPerformed(evt);
      }
    });
    jPanel1.add(includeNoOfTrainersCheckbox);

    includeLoadingScreenCheckbox.setMnemonic('L');
    includeLoadingScreenCheckbox.setText("Loading Screen");
    includeLoadingScreenCheckbox.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        includesChangedActionPerformed(evt);
      }
    });
    jPanel1.add(includeLoadingScreenCheckbox);

    includeHighScoreSaverCheckbox.setMnemonic('H');
    includeHighScoreSaverCheckbox.setText("High Score Saver");
    includeHighScoreSaverCheckbox.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        includesChangedActionPerformed(evt);
      }
    });
    jPanel1.add(includeHighScoreSaverCheckbox);

    includeIncludedDocsCheckbox.setMnemonic('I');
    includeIncludedDocsCheckbox.setText("Included Docs");
    includeIncludedDocsCheckbox.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        includesChangedActionPerformed(evt);
      }
    });
    jPanel1.add(includeIncludedDocsCheckbox);

    includeLengthCheckbox.setMnemonic('G');
    includeLengthCheckbox.setText("Game Length");
    includeLengthCheckbox.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        includesChangedActionPerformed(evt);
      }
    });
    jPanel1.add(includeLengthCheckbox);

    includeVersionCommentCheckbox.setMnemonic('e');
    includeVersionCommentCheckbox.setText("Version Comment");
    includeVersionCommentCheckbox.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        includesChangedActionPerformed(evt);
      }
    });
    jPanel1.add(includeVersionCommentCheckbox);

    includePalNtscCheckbox.setMnemonic('P');
    includePalNtscCheckbox.setText("PAL/NTSC");
    includePalNtscCheckbox.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        includesChangedActionPerformed(evt);
      }
    });
    jPanel1.add(includePalNtscCheckbox);

    includePanel.add(jPanel1);

    jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

    includeAllButton.setMnemonic('A');
    includeAllButton.setText("Select All");
    includeAllButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        selectAllIncludesActionPerformed(evt);
      }
    });
    jPanel2.add(includeAllButton);

    includeNoneButton.setMnemonic('N');
    includeNoneButton.setText("Select None");
    includeNoneButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        selectNoneIncludesActionPerformed(evt);
      }
    });
    jPanel2.add(includeNoneButton);

    includePanel.add(jPanel2);

    propertiesPane.addTab("Include", includePanel);

    fieldsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
    fieldsPanel.setLayout(new java.awt.GridBagLayout());

    crackerLabel.setDisplayedMnemonic('r');
    crackerLabel.setLabelFor(crackerButton);
    crackerLabel.setText("Cracker:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    fieldsPanel.add(crackerLabel, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 15, 5);
    fieldsPanel.add(noOfTrainersComboBox, gridBagConstraints);

    noOfTrainersLabel.setDisplayedMnemonic('N');
    noOfTrainersLabel.setLabelFor(noOfTrainersComboBox);
    noOfTrainersLabel.setText("No. of Trainers");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    fieldsPanel.add(noOfTrainersLabel, gridBagConstraints);

    crackerTextField.setEditable(false);
    crackerTextField.setFocusable(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 15, 0);
    fieldsPanel.add(crackerTextField, gridBagConstraints);

    crackerButton.setText("...");
    crackerButton.setMargin(new java.awt.Insets(0, 10, 0, 10));
    crackerButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        chooseOrEditCrackerActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 15, 0);
    fieldsPanel.add(crackerButton, gridBagConstraints);

    lengthLabel.setDisplayedMnemonic('G');
    lengthLabel.setLabelFor(lengthTextField);
    lengthLabel.setText("Game Length:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    fieldsPanel.add(lengthLabel, gridBagConstraints);

    palNtscLabel.setDisplayedMnemonic('P');
    palNtscLabel.setLabelFor(palNtscComboBox);
    palNtscLabel.setText("PAL/NTSC:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 50, 0, 0);
    fieldsPanel.add(palNtscLabel, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(0, 50, 15, 0);
    fieldsPanel.add(palNtscComboBox, gridBagConstraints);

    highScoreSaverCheckbox.setMnemonic('H');
    highScoreSaverCheckbox.setText("High Score Saver");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
    fieldsPanel.add(highScoreSaverCheckbox, gridBagConstraints);

    versionCommentLabel.setDisplayedMnemonic('V');
    versionCommentLabel.setLabelFor(versionCommentTextField);
    versionCommentLabel.setText("Version Comment:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 10;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    fieldsPanel.add(versionCommentLabel, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 11;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    fieldsPanel.add(versionCommentTextField, gridBagConstraints);

    loadingScreenCheckbox.setMnemonic('L');
    loadingScreenCheckbox.setText("Loading Screen");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
    fieldsPanel.add(loadingScreenCheckbox, gridBagConstraints);

    includedDocsCheckbox.setMnemonic('D');
    includedDocsCheckbox.setText("Included Docs");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 8;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
    fieldsPanel.add(includedDocsCheckbox, gridBagConstraints);

    trueDriveEmulationCheckbox.setMnemonic('T');
    trueDriveEmulationCheckbox.setText("True Drive Emulation");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 9;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 15, 0);
    fieldsPanel.add(trueDriveEmulationCheckbox, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 15, 5);
    fieldsPanel.add(lengthTextField, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 15, 50);
    fieldsPanel.add(lengthComboBox, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 13;
    gridBagConstraints.weighty = 1.0;
    fieldsPanel.add(jPanel3, gridBagConstraints);

    propertiesPane.addTab("Fields", fieldsPanel);

    getContentPane().add(propertiesPane, java.awt.BorderLayout.CENTER);

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

  private void chooseOrEditCrackerActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_chooseOrEditCrackerActionPerformed
    crackersList.setModel(new ListListModel(new ItemViewFilter("Cracker",
        ItemViewFilter.SELECTOR_DB, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Crackers", "CR_Id", "", "").getSelections()));
    for (int i = 0; i < crackersList.getModel().getSize(); i++) {
      if (((Selection) crackersList.getModel().getElementAt(i)).getValue().equals(
          String.valueOf(cracker.getId()))) {
        crackersList.setSelectedIndex(i);
        crackersList.ensureIndexIsVisible(i);
      }
    }

    if (JOptionPane.showOptionDialog(this, chooseCrackerPanel, "Select Cracker...",
        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null) == JOptionPane.OK_OPTION) {

      cracker = Db.getCrackerById(Integer.parseInt(((Selection) crackersList.getSelectedValue())
          .getValue()));
      crackerTextField.setText(cracker.getName());
    }
  }// GEN-LAST:event_chooseOrEditCrackerActionPerformed

  private void newCrackerActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_newCrackerActionPerformed
    final String s = (String) JOptionPane.showInputDialog(this, "Name of Cracker", "New Cracker",
        JOptionPane.PLAIN_MESSAGE, null, null, null);

    if ((s != null) && !s.isEmpty()) {
      cracker = new Cracker();
      cracker.setName(s);
      Db.saveOrUpdate(cracker);

      crackersList.setModel(new ListListModel(new ItemViewFilter("Cracker",
          ItemViewFilter.SELECTOR_DB, ItemViewFilter.CLAUSETYPE_DBFIELD,
          ItemViewFilter.OPERATOR_EQUAL, "Crackers", "CR_Id", "", "").getSelections()));
      for (int i = 0; i < crackersList.getModel().getSize(); i++) {
        if (((Selection) crackersList.getModel().getElementAt(i)).getValue().equals(
            String.valueOf(cracker.getId()))) {
          crackersList.setSelectedIndex(i);
          crackersList.ensureIndexIsVisible(i);
        }
      }

      crackerTextField.setText(s);
    }

  }// GEN-LAST:event_newCrackerActionPerformed

  private void includesChangedActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_includesChangedActionPerformed
    includesChanged();
  }// GEN-LAST:event_includesChangedActionPerformed

  private void selectNoneIncludesActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_selectNoneIncludesActionPerformed
    selectIncludes(false);
  }// GEN-LAST:event_selectNoneIncludesActionPerformed

  private void editCrackerActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_editCrackerActionPerformed
    final Selection current = (Selection) crackersList.getSelectedValue();

    final String s = (String) JOptionPane.showInputDialog(this, "Name of Cracker", "Edit Cracker",
        JOptionPane.PLAIN_MESSAGE, null, null, current.getName());

    if ((s != null) && (!s.isEmpty()) && (!s.equals(current.toString()))) {
      cracker = Db.getCrackerById(Integer.parseInt(current.getValue()));
      cracker.setName(s);
      Db.saveOrUpdate(cracker);

      crackersList.setModel(new ListListModel(new ItemViewFilter("Cracker",
          ItemViewFilter.SELECTOR_DB, ItemViewFilter.CLAUSETYPE_DBFIELD,
          ItemViewFilter.OPERATOR_EQUAL, "Crackers", "CR_Id", "", "").getSelections()));
      for (int i = 0; i < crackersList.getModel().getSize(); i++) {
        if (((Selection) crackersList.getModel().getElementAt(i)).getValue().equals(
            String.valueOf(cracker.getId()))) {
          crackersList.setSelectedIndex(i);
          crackersList.ensureIndexIsVisible(i);
        }
      }

      crackerTextField.setText(s);
    }
  }// GEN-LAST:event_editCrackerActionPerformed

  private void cancelActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cancelActionPerformed
    dispose();
  }// GEN-LAST:event_cancelActionPerformed

  private void selectAllIncludesActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_selectAllIncludesActionPerformed
    selectIncludes(true);
  }// GEN-LAST:event_selectAllIncludesActionPerformed

  private void selectIncludes(final boolean b) {
    includeCrackerCheckbox.setSelected(b);
    includeNoOfTrainersCheckbox.setSelected(b);
    includePalNtscCheckbox.setSelected(b);
    includeLengthCheckbox.setSelected(b);
    includeHighScoreSaverCheckbox.setSelected(b);
    includeLoadingScreenCheckbox.setSelected(b);
    includeIncludedDocsCheckbox.setSelected(b);
    includeTrueDriveEmulationCheckbox.setSelected(b);
    includeVersionCommentCheckbox.setSelected(b);
    includesChanged();
  }

  private void includesChanged() {
    crackerTextField.setEnabled(includeCrackerCheckbox.isSelected());
    crackerTextField.setBackground(includeCrackerCheckbox.isSelected() ? new java.awt.Color(255,
        255, 204) : javax.swing.UIManager.getDefaults().getColor("TextField.inactiveBackground"));
    crackerButton.setEnabled(includeCrackerCheckbox.isSelected());

    noOfTrainersComboBox.setEnabled(includeNoOfTrainersCheckbox.isSelected());

    palNtscComboBox.setEnabled(includePalNtscCheckbox.isSelected());

    lengthTextField.setEnabled(includeLengthCheckbox.isSelected());
    lengthComboBox.setEnabled(includeLengthCheckbox.isSelected());

    highScoreSaverCheckbox.setEnabled(includeHighScoreSaverCheckbox.isSelected());
    loadingScreenCheckbox.setEnabled(includeLoadingScreenCheckbox.isSelected());
    includedDocsCheckbox.setEnabled(includeIncludedDocsCheckbox.isSelected());
    trueDriveEmulationCheckbox.setEnabled(includeTrueDriveEmulationCheckbox.isSelected());

    versionCommentTextField.setEditable(includeVersionCommentCheckbox.isSelected());
  }

  private void okActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_okActionPerformed
    if (!isList) {
      list = new ArrayList<Item>();
      list.add(game);
    }

    for (final Item item : list) {

      if (item instanceof Game) {
        final Game game = (Game) item;

        if (includeCrackerCheckbox.isSelected()) {
          game.setCracker(cracker);
        }

        if (includeNoOfTrainersCheckbox.isSelected()) {
          game.setTrainer(Integer.parseInt(((Selection) noOfTrainersComboBox.getSelectedItem())
              .getValue()));
        }

        if (includePalNtscCheckbox.isSelected()) {
          game.setPalNtsc(palNtscComboBox.getSelectedIndex());
        }

        if (includeLengthCheckbox.isSelected()) {
          game.setLength(Integer.parseInt(lengthTextField.getText()));
          game.setLengthType(lengthComboBox.getSelectedIndex());
        }

        if (includeHighScoreSaverCheckbox.isSelected()) {
          game.setHasHighscoreSaver(highScoreSaverCheckbox.isSelected());
        }

        if (includeLoadingScreenCheckbox.isSelected()) {
          game.setHasLoadingScreen(loadingScreenCheckbox.isSelected());
        }

        if (includeIncludedDocsCheckbox.isSelected()) {
          game.setHasIncludedDocs(includedDocsCheckbox.isSelected());
        }

        if (includeTrueDriveEmulationCheckbox.isSelected()) {
          game.setNeedsTruedriveEmu(trueDriveEmulationCheckbox.isSelected());
        }

        if (includeVersionCommentCheckbox.isSelected()) {
          game.setVersionComment(versionCommentTextField.getText());
        }

        Db.saveOrUpdate(game);
      }
    }
    dispose();
  }// GEN-LAST:event_okActionPerformed

  private void exitFormWindowClosing(final java.awt.event.WindowEvent evt) {// GEN-FIRST:event_exitFormWindowClosing
    dispose();
  }// GEN-LAST:event_exitFormWindowClosing
}