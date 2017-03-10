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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

import jgamebase.Const;
import jgamebase.db.Db;
import jgamebase.db.model.Game;
import jgamebase.db.model.Item;
import jgamebase.db.model.ItemViewFilter;
import jgamebase.db.model.Language;
import jgamebase.db.model.Musician;
import jgamebase.db.model.Programmer;
import jgamebase.db.model.Publisher;
import jgamebase.db.model.Selection;

/**
 * The GameBase GUI.
 * 
 * @author F. Gerbig (fgerbig@users.sourceforge.net)
 */
public class GameInfoDialog extends javax.swing.JDialog {
  /**
	 * 
	 */
  private static final long serialVersionUID = 2644364686750465903L;

  List<Item> list;

  Game game;

  boolean isList;

  Publisher publisher;

  Programmer programmer;

  Musician musician;

  Language language;

  public GameInfoDialog(final Game game) {
    isList = false;
    this.game = game;

    initComponents();
    this.setSize(400, 480);

    selectIncludes(true); // select all fields
    propertiesPane.remove(0); // hide includePane

    yearComboBox.setModel(new DefaultComboBoxModel(new ItemViewFilter("Year",
        ItemViewFilter.SELECTOR_OTHER, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Years", "YE_Id", "", "").getSelections().toArray()));
    for (int i = 0; i < yearComboBox.getModel().getSize(); i++) {
      if (((Selection) yearComboBox.getModel().getElementAt(i)).getValue().equals(
          String.valueOf(game.getYear().getId()))) {
        yearComboBox.setSelectedIndex(i);
      }
    }

    genreComboBox.setModel(new DefaultComboBoxModel(new ItemViewFilter("Genre",
        ItemViewFilter.SELECTOR_OTHER, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Genres", "GE_Id", "", "").getSelections().toArray()));
    for (int i = 0; i < genreComboBox.getModel().getSize(); i++) {
      if (((Selection) genreComboBox.getModel().getElementAt(i)).getValue().equals(
          String.valueOf(game.getGenre().getId()))) {
        genreComboBox.setSelectedIndex(i);
      }
    }

    controlComboBox.setModel(new DefaultComboBoxModel(Const.FORDISPLAY_CONTROL));
    controlComboBox.setSelectedIndex(game.getControl());

    publisher = game.getPublisher();
    programmer = game.getProgrammer();
    musician = game.getMusician();
    language = game.getLanguage();

    // init fields
    publisherTextField.setText(publisher.getName());
    programmerTextField.setText(programmer.getName());
    musicianTextField.setText(musician.getNameForDisplay());
    languageTextField.setText(language.getName());

    if (game.getPlayersMin() == game.getPlayersMax()) {
      noOfPlayersRadioButton.setSelected(true);
      noOfPlayersTextField.setEditable(true);
      noOfPlayersMinTextField.setEditable(false);
      noOfPlayersMaxTextField.setEditable(false);
      noOfPlayersTextField.setText(Integer.toString(game.getPlayersMin()));
    } else {
      noOfPlayersRangeRadioButton.setSelected(true);
      noOfPlayersTextField.setEditable(false);
      noOfPlayersMinTextField.setEditable(true);
      noOfPlayersMaxTextField.setEditable(true);
      noOfPlayersMinTextField.setText(Integer.toString(game.getPlayersMin()));
      noOfPlayersMaxTextField.setText(Integer.toString(game.getPlayersMax()));
    }
    simultaneousPlayCheckBox.setSelected(game.getIsSimultaneouslyPlayable());
    commentTextField.setText(game.getComment());

    setLocationRelativeTo(null);
    setVisible(true);
  }

  public GameInfoDialog(final List<Item> list) {
    isList = true;
    this.list = list;
    initComponents();
    this.setSize(400, 480);

    selectIncludes(false); // select none fields

    yearComboBox.setModel(new DefaultComboBoxModel(new ItemViewFilter("Year",
        ItemViewFilter.SELECTOR_OTHER, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Years", "YE_Id", "", "").getSelections().toArray()));
    genreComboBox.setModel(new DefaultComboBoxModel(new ItemViewFilter("Genre",
        ItemViewFilter.SELECTOR_OTHER, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Genres", "GE_Id", "", "").getSelections().toArray()));
    controlComboBox.setModel(new DefaultComboBoxModel(Const.FORDISPLAY_CONTROL));

    // init fields
    publisher = Db.getPublisherById(Publisher.NEUTRAL_ID);
    programmer = Db.getProgrammerById(Programmer.NEUTRAL_ID);
    musician = Db.getMusicianById(Musician.NEUTRAL_ID);
    language = Db.getLanguageById(Language.NEUTRAL_ID);

    // set fields to a default value (empty)
    publisherTextField.setText("");
    programmerTextField.setText("");
    musicianTextField.setText("");
    languageTextField.setText("");

    // overwrite the default if a value exists
    if (publisher != null) {
      publisherTextField.setText(publisher.getName());
    }
    if (programmer != null) {
      programmerTextField.setText(programmer.getName());
    }
    if (musician != null) {
      musicianTextField.setText(musician.getNameForDisplay());
    }
    if (language != null) {
      languageTextField.setText(language.getName());
    }

    setVisible(true);
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JPanel chooseLanguagePanel;
  private javax.swing.JPanel chooseMusicianPanel;
  private javax.swing.JPanel chooseProgrammerPanel;
  private javax.swing.JPanel choosePublisherPanel;
  private javax.swing.JCheckBox commentCheckbox;
  private javax.swing.JTextField commentTextField;
  private javax.swing.JCheckBox controlCheckbox;
  private javax.swing.JComboBox controlComboBox;
  private javax.swing.JCheckBox genreCheckbox;
  private javax.swing.JComboBox genreComboBox;
  private javax.swing.JLabel jLabel9;
  private javax.swing.JButton languageButton;
  private javax.swing.JCheckBox languageCheckbox;
  private javax.swing.JTextField languageTextField;
  private javax.swing.JList languagesList;
  private javax.swing.JButton musicianButton;
  private javax.swing.JCheckBox musicianCheckbox;
  private javax.swing.JTextField musicianTextField;
  private javax.swing.JList musiciansList;
  private javax.swing.JCheckBox noOfPlayersCheckbox;
  private javax.swing.JTextField noOfPlayersMaxTextField;
  private javax.swing.JTextField noOfPlayersMinTextField;
  private javax.swing.JRadioButton noOfPlayersRadioButton;
  private javax.swing.JRadioButton noOfPlayersRangeRadioButton;
  private javax.swing.JTextField noOfPlayersTextField;
  private javax.swing.JButton programmerButton;
  private javax.swing.JCheckBox programmerCheckbox;
  private javax.swing.JTextField programmerTextField;
  private javax.swing.JList programmersList;
  private javax.swing.JTabbedPane propertiesPane;
  private javax.swing.JButton publisherButton;
  private javax.swing.JCheckBox publisherCheckbox;
  private javax.swing.JTextField publisherTextField;
  private javax.swing.JList publishersList;
  private javax.swing.JCheckBox simultaneousPlayCheckBox;
  private javax.swing.JCheckBox yearCheckbox;
  private javax.swing.JComboBox yearComboBox;

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

    final javax.swing.ButtonGroup noOfPlayersButtonGroup = new javax.swing.ButtonGroup();
    choosePublisherPanel = new javax.swing.JPanel();
    final javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
    publishersList = new javax.swing.JList();
    final javax.swing.JButton newPublisherButton = new javax.swing.JButton();
    final javax.swing.JButton editPublisherButton = new javax.swing.JButton();
    chooseProgrammerPanel = new javax.swing.JPanel();
    final javax.swing.JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
    programmersList = new javax.swing.JList();
    final javax.swing.JButton newProgrammerButton = new javax.swing.JButton();
    final javax.swing.JButton editProgrammerButton = new javax.swing.JButton();
    chooseMusicianPanel = new javax.swing.JPanel();
    final javax.swing.JScrollPane jScrollPane3 = new javax.swing.JScrollPane();
    musiciansList = new javax.swing.JList();
    final javax.swing.JButton newMusicianButton = new javax.swing.JButton();
    final javax.swing.JButton editMusicianButton = new javax.swing.JButton();
    chooseLanguagePanel = new javax.swing.JPanel();
    final javax.swing.JScrollPane jScrollPane4 = new javax.swing.JScrollPane();
    languagesList = new javax.swing.JList();
    final javax.swing.JButton newLanguageButton = new javax.swing.JButton();
    final javax.swing.JButton editLanguageButton = new javax.swing.JButton();
    propertiesPane = new javax.swing.JTabbedPane();
    final javax.swing.JPanel includePanel = new javax.swing.JPanel();
    final javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
    yearCheckbox = new javax.swing.JCheckBox();
    genreCheckbox = new javax.swing.JCheckBox();
    publisherCheckbox = new javax.swing.JCheckBox();
    noOfPlayersCheckbox = new javax.swing.JCheckBox();
    programmerCheckbox = new javax.swing.JCheckBox();
    controlCheckbox = new javax.swing.JCheckBox();
    musicianCheckbox = new javax.swing.JCheckBox();
    commentCheckbox = new javax.swing.JCheckBox();
    languageCheckbox = new javax.swing.JCheckBox();
    final javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
    final javax.swing.JButton includeAllButton = new javax.swing.JButton();
    final javax.swing.JButton includeNoneButton = new javax.swing.JButton();
    final javax.swing.JPanel fieldsPanel = new javax.swing.JPanel();
    final javax.swing.JLabel publisherLabel = new javax.swing.JLabel();
    yearComboBox = new javax.swing.JComboBox();
    final javax.swing.JLabel yearLabel = new javax.swing.JLabel();
    publisherTextField = new javax.swing.JTextField();
    publisherButton = new javax.swing.JButton();
    programmerTextField = new javax.swing.JTextField();
    programmerButton = new javax.swing.JButton();
    final javax.swing.JLabel programmerLabel = new javax.swing.JLabel();
    final javax.swing.JLabel musicianLabel = new javax.swing.JLabel();
    musicianTextField = new javax.swing.JTextField();
    musicianButton = new javax.swing.JButton();
    final javax.swing.JLabel languageLabel = new javax.swing.JLabel();
    languageTextField = new javax.swing.JTextField();
    languageButton = new javax.swing.JButton();
    final javax.swing.JLabel genreLabel = new javax.swing.JLabel();
    genreComboBox = new javax.swing.JComboBox();
    final javax.swing.JLabel controlLabel = new javax.swing.JLabel();
    controlComboBox = new javax.swing.JComboBox();
    final javax.swing.JLabel noOfPlayersLabel = new javax.swing.JLabel();
    final javax.swing.JPanel jPanel3 = new javax.swing.JPanel();
    noOfPlayersRadioButton = new javax.swing.JRadioButton();
    noOfPlayersTextField = new javax.swing.JTextField();
    final javax.swing.JLabel jLabel11 = new javax.swing.JLabel();
    noOfPlayersRangeRadioButton = new javax.swing.JRadioButton();
    noOfPlayersMinTextField = new javax.swing.JTextField();
    jLabel9 = new javax.swing.JLabel();
    noOfPlayersMaxTextField = new javax.swing.JTextField();
    simultaneousPlayCheckBox = new javax.swing.JCheckBox();
    final javax.swing.JLabel commentLabel = new javax.swing.JLabel();
    commentTextField = new javax.swing.JTextField();
    final javax.swing.JPanel OkCancelPanel = new javax.swing.JPanel();
    final javax.swing.JPanel jPanel9 = new javax.swing.JPanel();
    final javax.swing.JButton okButton = new javax.swing.JButton();
    final javax.swing.JButton cancelButton = new javax.swing.JButton();

    choosePublisherPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Publishers"));
    choosePublisherPanel.setLayout(new java.awt.GridBagLayout());

    publishersList.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
    publishersList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    jScrollPane1.setViewportView(publishersList);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridheight = 5;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.insets = new java.awt.Insets(3, 5, 5, 5);
    choosePublisherPanel.add(jScrollPane1, gridBagConstraints);

    newPublisherButton.setMnemonic('N');
    newPublisherButton.setText("New");
    newPublisherButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        newPublisherActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 5);
    choosePublisherPanel.add(newPublisherButton, gridBagConstraints);

    editPublisherButton.setMnemonic('E');
    editPublisherButton.setText("Edit");
    editPublisherButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        editPublisherActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
    choosePublisherPanel.add(editPublisherButton, gridBagConstraints);

    chooseProgrammerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Programmers"));
    chooseProgrammerPanel.setLayout(new java.awt.GridBagLayout());

    programmersList.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
    programmersList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    jScrollPane2.setViewportView(programmersList);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridheight = 5;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.insets = new java.awt.Insets(3, 5, 5, 5);
    chooseProgrammerPanel.add(jScrollPane2, gridBagConstraints);

    newProgrammerButton.setMnemonic('N');
    newProgrammerButton.setText("New");
    newProgrammerButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        newProgrammerActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 5);
    chooseProgrammerPanel.add(newProgrammerButton, gridBagConstraints);

    editProgrammerButton.setMnemonic('E');
    editProgrammerButton.setText("Edit");
    editProgrammerButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        editProgrammerActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
    chooseProgrammerPanel.add(editProgrammerButton, gridBagConstraints);

    chooseMusicianPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Musicians"));
    chooseMusicianPanel.setLayout(new java.awt.GridBagLayout());

    musiciansList.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
    musiciansList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    jScrollPane3.setViewportView(musiciansList);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridheight = 5;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.insets = new java.awt.Insets(3, 5, 5, 5);
    chooseMusicianPanel.add(jScrollPane3, gridBagConstraints);

    newMusicianButton.setMnemonic('N');
    newMusicianButton.setText("New");
    newMusicianButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        newMusicianActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 5);
    chooseMusicianPanel.add(newMusicianButton, gridBagConstraints);

    editMusicianButton.setMnemonic('E');
    editMusicianButton.setText("Edit");
    editMusicianButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        editMusicianActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
    chooseMusicianPanel.add(editMusicianButton, gridBagConstraints);

    chooseLanguagePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Languages"));
    chooseLanguagePanel.setLayout(new java.awt.GridBagLayout());

    languagesList.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
    languagesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    jScrollPane4.setViewportView(languagesList);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridheight = 5;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.insets = new java.awt.Insets(3, 5, 5, 5);
    chooseLanguagePanel.add(jScrollPane4, gridBagConstraints);

    newLanguageButton.setMnemonic('N');
    newLanguageButton.setText("New");
    newLanguageButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        newLanguageActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 5);
    chooseLanguagePanel.add(newLanguageButton, gridBagConstraints);

    editLanguageButton.setMnemonic('E');
    editLanguageButton.setText("Edit");
    editLanguageButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        editLanguageActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
    chooseLanguagePanel.add(editLanguageButton, gridBagConstraints);

    setTitle("Game Info Properties...");
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

    yearCheckbox.setMnemonic('Y');
    yearCheckbox.setText("Year");
    yearCheckbox.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        includesChangedActionPerformed(evt);
      }
    });
    jPanel1.add(yearCheckbox);

    genreCheckbox.setMnemonic('G');
    genreCheckbox.setText("Genre");
    genreCheckbox.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        includesChangedActionPerformed(evt);
      }
    });
    jPanel1.add(genreCheckbox);

    publisherCheckbox.setMnemonic('P');
    publisherCheckbox.setText("Publisher");
    publisherCheckbox.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        includesChangedActionPerformed(evt);
      }
    });
    jPanel1.add(publisherCheckbox);

    noOfPlayersCheckbox.setMnemonic('N');
    noOfPlayersCheckbox.setText("Number of Players");
    noOfPlayersCheckbox.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        includesChangedActionPerformed(evt);
      }
    });
    jPanel1.add(noOfPlayersCheckbox);

    programmerCheckbox.setMnemonic('r');
    programmerCheckbox.setText("Programmer");
    programmerCheckbox.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        includesChangedActionPerformed(evt);
      }
    });
    jPanel1.add(programmerCheckbox);

    controlCheckbox.setMnemonic('t');
    controlCheckbox.setText("Control");
    controlCheckbox.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        includesChangedActionPerformed(evt);
      }
    });
    jPanel1.add(controlCheckbox);

    musicianCheckbox.setMnemonic('M');
    musicianCheckbox.setText("Musician");
    musicianCheckbox.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        includesChangedActionPerformed(evt);
      }
    });
    jPanel1.add(musicianCheckbox);

    commentCheckbox.setMnemonic('e');
    commentCheckbox.setText("Comment");
    commentCheckbox.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        includesChangedActionPerformed(evt);
      }
    });
    jPanel1.add(commentCheckbox);

    languageCheckbox.setMnemonic('L');
    languageCheckbox.setText("Language");
    languageCheckbox.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        includesChangedActionPerformed(evt);
      }
    });
    jPanel1.add(languageCheckbox);

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
    fieldsPanel.setMinimumSize(new java.awt.Dimension(339, 360));
    fieldsPanel.setPreferredSize(new java.awt.Dimension(329, 360));
    fieldsPanel.setLayout(new java.awt.GridBagLayout());

    publisherLabel.setDisplayedMnemonic('P');
    publisherLabel.setLabelFor(publisherButton);
    publisherLabel.setText("Publisher:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(5, 0, 1, 0);
    fieldsPanel.add(publisherLabel, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
    fieldsPanel.add(yearComboBox, gridBagConstraints);

    yearLabel.setDisplayedMnemonic('Y');
    yearLabel.setLabelFor(yearComboBox);
    yearLabel.setText("Year:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(5, 0, 1, 0);
    fieldsPanel.add(yearLabel, gridBagConstraints);

    publisherTextField.setEditable(false);
    publisherTextField.setFocusable(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
    fieldsPanel.add(publisherTextField, gridBagConstraints);

    publisherButton.setText("...");
    publisherButton.setMargin(new java.awt.Insets(0, 10, 0, 10));
    publisherButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        chooseOrEditPublisherActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    fieldsPanel.add(publisherButton, gridBagConstraints);

    programmerTextField.setEditable(false);
    programmerTextField.setFocusable(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
    fieldsPanel.add(programmerTextField, gridBagConstraints);

    programmerButton.setText("...");
    programmerButton.setMargin(new java.awt.Insets(0, 10, 0, 10));
    programmerButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        chooseOrEditProgrammerActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 3;
    fieldsPanel.add(programmerButton, gridBagConstraints);

    programmerLabel.setDisplayedMnemonic('r');
    programmerLabel.setLabelFor(programmerButton);
    programmerLabel.setText("Programmer:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(5, 0, 1, 0);
    fieldsPanel.add(programmerLabel, gridBagConstraints);

    musicianLabel.setDisplayedMnemonic('M');
    musicianLabel.setLabelFor(musicianButton);
    musicianLabel.setText("Musician:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(5, 0, 1, 0);
    fieldsPanel.add(musicianLabel, gridBagConstraints);

    musicianTextField.setEditable(false);
    musicianTextField.setFocusable(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
    fieldsPanel.add(musicianTextField, gridBagConstraints);

    musicianButton.setText("...");
    musicianButton.setMargin(new java.awt.Insets(0, 10, 0, 10));
    musicianButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        chooseOrEditMusicianActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 5;
    fieldsPanel.add(musicianButton, gridBagConstraints);

    languageLabel.setDisplayedMnemonic('L');
    languageLabel.setLabelFor(languageButton);
    languageLabel.setText("Language:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(5, 0, 1, 0);
    fieldsPanel.add(languageLabel, gridBagConstraints);

    languageTextField.setEditable(false);
    languageTextField.setFocusable(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
    fieldsPanel.add(languageTextField, gridBagConstraints);

    languageButton.setText("...");
    languageButton.setMargin(new java.awt.Insets(0, 10, 0, 10));
    languageButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        chooseOrEditLanguageActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 7;
    fieldsPanel.add(languageButton, gridBagConstraints);

    genreLabel.setDisplayedMnemonic('G');
    genreLabel.setLabelFor(genreComboBox);
    genreLabel.setText("Genre:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 8;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(5, 0, 1, 0);
    fieldsPanel.add(genreLabel, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 9;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    fieldsPanel.add(genreComboBox, gridBagConstraints);

    controlLabel.setDisplayedMnemonic('t');
    controlLabel.setLabelFor(controlComboBox);
    controlLabel.setText("Control:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 10;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(5, 0, 1, 0);
    fieldsPanel.add(controlLabel, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 11;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    fieldsPanel.add(controlComboBox, gridBagConstraints);

    noOfPlayersLabel.setDisplayedMnemonic('N');
    noOfPlayersLabel.setLabelFor(noOfPlayersRadioButton);
    noOfPlayersLabel.setText("No. of Players:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 12;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(5, 0, 1, 0);
    fieldsPanel.add(noOfPlayersLabel, gridBagConstraints);

    jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

    noOfPlayersButtonGroup.add(noOfPlayersRadioButton);
    noOfPlayersRadioButton.setSelected(true);
    noOfPlayersRadioButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        noOfPlayersSingleValueActionPerformed(evt);
      }
    });
    jPanel3.add(noOfPlayersRadioButton);

    noOfPlayersTextField.setEditable(false);
    noOfPlayersTextField.setMinimumSize(new java.awt.Dimension(30, 19));
    noOfPlayersTextField.setPreferredSize(new java.awt.Dimension(20, 19));
    jPanel3.add(noOfPlayersTextField);

    jLabel11.setText("   ");
    jPanel3.add(jLabel11);

    noOfPlayersButtonGroup.add(noOfPlayersRangeRadioButton);
    noOfPlayersRangeRadioButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        noOfPlayersRangeActionPerformed(evt);
      }
    });
    jPanel3.add(noOfPlayersRangeRadioButton);

    noOfPlayersMinTextField.setEditable(false);
    noOfPlayersMinTextField.setMinimumSize(new java.awt.Dimension(20, 19));
    noOfPlayersMinTextField.setPreferredSize(new java.awt.Dimension(20, 19));
    jPanel3.add(noOfPlayersMinTextField);

    jLabel9.setText(" - ");
    jLabel9.setFocusable(false);
    jPanel3.add(jLabel9);

    noOfPlayersMaxTextField.setEditable(false);
    noOfPlayersMaxTextField.setMinimumSize(new java.awt.Dimension(20, 19));
    noOfPlayersMaxTextField.setPreferredSize(new java.awt.Dimension(20, 19));
    jPanel3.add(noOfPlayersMaxTextField);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 13;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
    fieldsPanel.add(jPanel3, gridBagConstraints);

    simultaneousPlayCheckBox.setMnemonic('S');
    simultaneousPlayCheckBox.setText("Simultaneous Play");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 13;
    fieldsPanel.add(simultaneousPlayCheckBox, gridBagConstraints);

    commentLabel.setDisplayedMnemonic('e');
    commentLabel.setLabelFor(commentTextField);
    commentLabel.setText("Comment:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 14;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(5, 0, 1, 0);
    fieldsPanel.add(commentLabel, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 15;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    fieldsPanel.add(commentTextField, gridBagConstraints);

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

  private void chooseOrEditLanguageActionPerformed(final java.awt.event.ActionEvent evt) {
    languagesList.setModel(new ListListModel(new ItemViewFilter("Language",
        ItemViewFilter.SELECTOR_DB, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Languages", "LA_Id", "", "").getSelections()));// GEN-FIRST:event_chooseOrEditLanguageActionPerformed
    for (int i = 0; i < languagesList.getModel().getSize(); i++) {
      if (((Selection) languagesList.getModel().getElementAt(i)).getValue().equals(
          String.valueOf(language.getId()))) {
        languagesList.setSelectedIndex(i);
        languagesList.ensureIndexIsVisible(i);
      }
    }

    if (JOptionPane.showOptionDialog(this, chooseLanguagePanel, "Select Language...",
        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null) == JOptionPane.OK_OPTION) {

      language = Db.getLanguageById(Integer.parseInt(((Selection) languagesList.getSelectedValue())
          .getValue()));
      languageTextField.setText(language.getName());
    }
  }// GEN-LAST:event_chooseOrEditLanguageActionPerformed

  private void chooseOrEditPublisherActionPerformed(final java.awt.event.ActionEvent evt) {
    publishersList.setModel(new ListListModel(new ItemViewFilter("Publisher",
        ItemViewFilter.SELECTOR_DB, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Publishers", "PU_Id", "", "").getSelections()));// GEN-FIRST:event_chooseOrEditPublisherActionPerformed
    for (int i = 0; i < publishersList.getModel().getSize(); i++) {
      if (((Selection) publishersList.getModel().getElementAt(i)).getValue().equals(
          String.valueOf(publisher.getId()))) {
        publishersList.setSelectedIndex(i);
        publishersList.ensureIndexIsVisible(i);
      }
    }

    if (JOptionPane.showOptionDialog(this, choosePublisherPanel, "Select Publisher...",
        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null) == JOptionPane.OK_OPTION) {

      publisher = Db.getPublisherById(Integer.parseInt(((Selection) publishersList
          .getSelectedValue()).getValue()));
      publisherTextField.setText(publisher.getName());
    }
  }// GEN-LAST:event_chooseOrEditPublisherActionPerformed

  private void editMusicianActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_editMusicianActionPerformed
    final Musician newMusician = new MusicianDialog(Db.getMusicianById(Integer
        .parseInt(((Selection) musiciansList.getSelectedValue()).getValue()))).get();

    if (newMusician != null) {
      musician = newMusician;
      Db.saveOrUpdate(musician);

      musiciansList.setModel(new ListListModel(new ItemViewFilter("Musician",
          ItemViewFilter.SELECTOR_DB, ItemViewFilter.CLAUSETYPE_DBFIELD,
          ItemViewFilter.OPERATOR_EQUAL, "Musicians", "MU_Id", "Musicians", "MU_Id")
          .getSelections()));
      for (int i = 0; i < musiciansList.getModel().getSize(); i++) {
        if (((Selection) musiciansList.getModel().getElementAt(i)).getValue().equals(
            String.valueOf(musician.getId()))) {
          musiciansList.setSelectedIndex(i);
          musiciansList.ensureIndexIsVisible(i);
        }
      }

      musicianTextField.setText(musician.getName());
    }
  }// GEN-LAST:event_editMusicianActionPerformed

  private void newPublisherActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_newPublisherActionPerformed
    final String s = (String) JOptionPane.showInputDialog(this, "Name of Publisher",
        "New Publisher", JOptionPane.PLAIN_MESSAGE, null, null, null);

    if ((s != null) && !s.isEmpty()) {
      publisher = new Publisher();
      publisher.setName(s);
      Db.saveOrUpdate(publisher);

      publishersList.setModel(new ListListModel(new ItemViewFilter("Publisher",
          ItemViewFilter.SELECTOR_DB, ItemViewFilter.CLAUSETYPE_DBFIELD,
          ItemViewFilter.OPERATOR_EQUAL, "Publishers", "PU_Id", "", "").getSelections()));
      for (int i = 0; i < publishersList.getModel().getSize(); i++) {
        if (((Selection) publishersList.getModel().getElementAt(i)).getValue().equals(
            String.valueOf(publisher.getId()))) {
          publishersList.setSelectedIndex(i);
          publishersList.ensureIndexIsVisible(i);
        }
      }

      publisherTextField.setText(s);
    }

  }// GEN-LAST:event_newPublisherActionPerformed

  private void includesChangedActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_includesChangedActionPerformed
    includesChanged();
  }// GEN-LAST:event_includesChangedActionPerformed

  private void newLanguageActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_newLanguageActionPerformed
    final String s = (String) JOptionPane.showInputDialog(this, "Name of Language", "New Language",
        JOptionPane.PLAIN_MESSAGE, null, null, null);

    if ((s != null) && !s.isEmpty()) {
      language = new Language();
      language.setName(s);
      Db.saveOrUpdate(language);

      languagesList.setModel(new ListListModel(new ItemViewFilter("Language",
          ItemViewFilter.SELECTOR_DB, ItemViewFilter.CLAUSETYPE_DBFIELD,
          ItemViewFilter.OPERATOR_EQUAL, "Languages", "LA_Id", "", "").getSelections()));
      for (int i = 0; i < languagesList.getModel().getSize(); i++) {
        if (((Selection) languagesList.getModel().getElementAt(i)).getValue().equals(
            String.valueOf(language.getId()))) {
          languagesList.setSelectedIndex(i);
          languagesList.ensureIndexIsVisible(i);
        }
      }

      languageTextField.setText(s);
    }
  }// GEN-LAST:event_newLanguageActionPerformed

  private void editProgrammerActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_editProgrammerActionPerformed
    final Selection current = (Selection) programmersList.getSelectedValue();

    final String s = (String) JOptionPane.showInputDialog(this, "Name of Programmer",
        "Edit Programmer", JOptionPane.PLAIN_MESSAGE, null, null, current.getName());

    if ((s != null) && !s.isEmpty() && !s.equals(current.toString())) {
      programmer = Db.getProgrammerById(Integer.parseInt(current.getValue()));
      programmer.setName(s);
      Db.saveOrUpdate(programmer);

      programmersList.setModel(new ListListModel(new ItemViewFilter("Programmer",
          ItemViewFilter.SELECTOR_DB, ItemViewFilter.CLAUSETYPE_DBFIELD,
          ItemViewFilter.OPERATOR_EQUAL, "Programmers", "PR_Id", "", "").getSelections()));
      for (int i = 0; i < programmersList.getModel().getSize(); i++) {
        if (((Selection) programmersList.getModel().getElementAt(i)).getValue().equals(
            String.valueOf(programmer.getId()))) {
          programmersList.setSelectedIndex(i);
          programmersList.ensureIndexIsVisible(i);
        }
      }

      programmerTextField.setText(s);
    }
  }// GEN-LAST:event_editProgrammerActionPerformed

  private void newMusicianActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_newMusicianActionPerformed
    final Musician newMusician = new MusicianDialog().get();

    if (newMusician != null) {
      musician = newMusician;
      Db.saveOrUpdate(musician);

      musiciansList.setModel(new ListListModel(new ItemViewFilter("Musician",
          ItemViewFilter.SELECTOR_DB, ItemViewFilter.CLAUSETYPE_DBFIELD,
          ItemViewFilter.OPERATOR_EQUAL, "Musicians", "MU_Id", "Musicians", "MU_Id")
          .getSelections()));
      for (int i = 0; i < musiciansList.getModel().getSize(); i++) {
        if (((Selection) musiciansList.getModel().getElementAt(i)).getValue().equals(
            String.valueOf(musician.getId()))) {
          musiciansList.setSelectedIndex(i);
          musiciansList.ensureIndexIsVisible(i);
        }
      }

      musicianTextField.setText(musician.getName());
    }
  }// GEN-LAST:event_newMusicianActionPerformed

  private void selectNoneIncludesActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_selectNoneIncludesActionPerformed
    selectIncludes(false);
  }// GEN-LAST:event_selectNoneIncludesActionPerformed

  private void noOfPlayersSingleValueActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_noOfPlayersSingleValueActionPerformed
    noOfPlayersTextField.setEditable(true);
    noOfPlayersMinTextField.setEditable(false);
    noOfPlayersMinTextField.setText("");
    noOfPlayersMaxTextField.setEditable(false);
    noOfPlayersMaxTextField.setText("");
  }// GEN-LAST:event_noOfPlayersSingleValueActionPerformed

  private void editPublisherActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_editPublisherActionPerformed
    final Selection current = (Selection) publishersList.getSelectedValue();

    final String s = (String) JOptionPane.showInputDialog(this, "Name of Publisher",
        "Edit Publisher", JOptionPane.PLAIN_MESSAGE, null, null, current.getName());

    if ((s != null) && (!s.isEmpty()) && (!s.equals(current.toString()))) {
      publisher = Db.getPublisherById(Integer.parseInt(current.getValue()));
      publisher.setName(s);
      Db.saveOrUpdate(publisher);

      publishersList.setModel(new ListListModel(new ItemViewFilter("Publisher",
          ItemViewFilter.SELECTOR_DB, ItemViewFilter.CLAUSETYPE_DBFIELD,
          ItemViewFilter.OPERATOR_EQUAL, "Publishers", "PU_Id", "", "").getSelections()));
      for (int i = 0; i < publishersList.getModel().getSize(); i++) {
        if (((Selection) publishersList.getModel().getElementAt(i)).getValue().equals(
            String.valueOf(publisher.getId()))) {
          publishersList.setSelectedIndex(i);
          publishersList.ensureIndexIsVisible(i);
        }
      }

      publisherTextField.setText(s);
    }
  }// GEN-LAST:event_editPublisherActionPerformed

  private void newProgrammerActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_newProgrammerActionPerformed
    final String s = (String) JOptionPane.showInputDialog(this, "Name of Programmer",
        "New Programmer", JOptionPane.PLAIN_MESSAGE, null, null, null);

    if ((s != null) && !s.isEmpty()) {
      programmer = new Programmer();
      programmer.setName(s);
      Db.saveOrUpdate(programmer);

      programmersList.setModel(new ListListModel(new ItemViewFilter("Programmer",
          ItemViewFilter.SELECTOR_DB, ItemViewFilter.CLAUSETYPE_DBFIELD,
          ItemViewFilter.OPERATOR_EQUAL, "Programmers", "PR_Id", "", "").getSelections()));
      for (int i = 0; i < programmersList.getModel().getSize(); i++) {
        if (((Selection) programmersList.getModel().getElementAt(i)).getValue().equals(
            String.valueOf(programmer.getId()))) {
          programmersList.setSelectedIndex(i);
          programmersList.ensureIndexIsVisible(i);
        }
      }

      programmerTextField.setText(s);
    }
  }// GEN-LAST:event_newProgrammerActionPerformed

  private void chooseOrEditMusicianActionPerformed(final java.awt.event.ActionEvent evt) {
    musiciansList
        .setModel(new ListListModel(new ItemViewFilter("Musician", ItemViewFilter.SELECTOR_DB,
            ItemViewFilter.CLAUSETYPE_DBFIELD, ItemViewFilter.OPERATOR_EQUAL, "Musicians", "MU_Id",
            "Musicians", "MU_Id").getSelections()));
    for (int i = 0; i < musiciansList.getModel().getSize(); i++) {
      if (((Selection) musiciansList.getModel().getElementAt(i)).getValue().equals(
          String.valueOf(musician.getId()))) {
        musiciansList.setSelectedIndex(i);
        musiciansList.ensureIndexIsVisible(i);
      }
    }

    if (JOptionPane.showOptionDialog(this, chooseMusicianPanel, "Select Musician...",
        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null) == JOptionPane.OK_OPTION) {

      musician = Db.getMusicianById(Integer.parseInt(((Selection) musiciansList.getSelectedValue())
          .getValue()));
      musicianTextField.setText(musician.getName());
    }
  }

  private void noOfPlayersRangeActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_noOfPlayersRangeActionPerformed
    noOfPlayersTextField.setEditable(false);
    noOfPlayersTextField.setText("");
    noOfPlayersMinTextField.setEditable(true);
    noOfPlayersMaxTextField.setEditable(true);
  }// GEN-LAST:event_noOfPlayersRangeActionPerformed

  private void chooseOrEditProgrammerActionPerformed(final java.awt.event.ActionEvent evt) {
    programmersList.setModel(new ListListModel(new ItemViewFilter("Programmer",
        ItemViewFilter.SELECTOR_DB, ItemViewFilter.CLAUSETYPE_DBFIELD,
        ItemViewFilter.OPERATOR_EQUAL, "Programmers", "PR_Id", "", "").getSelections()));// GEN-FIRST:event_chooseOrEditProgrammerActionPerformed
    for (int i = 0; i < programmersList.getModel().getSize(); i++) {
      if (((Selection) programmersList.getModel().getElementAt(i)).getValue().equals(
          String.valueOf(programmer.getId()))) {
        programmersList.setSelectedIndex(i);
        programmersList.ensureIndexIsVisible(i);
      }
    }

    if (JOptionPane.showOptionDialog(this, chooseProgrammerPanel, "Select Programmer...",
        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null) == JOptionPane.OK_OPTION) {

      programmer = Db.getProgrammerById(Integer.parseInt(((Selection) programmersList
          .getSelectedValue()).getValue()));
      programmerTextField.setText(programmer.getName());
    }
  }// GEN-LAST:event_chooseOrEditProgrammerActionPerformed

  private void cancelActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cancelActionPerformed
    dispose();
  }// GEN-LAST:event_cancelActionPerformed

  private void selectAllIncludesActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_selectAllIncludesActionPerformed
    selectIncludes(true);
  }// GEN-LAST:event_selectAllIncludesActionPerformed

  private void editLanguageActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_editLanguageActionPerformed
    final Selection current = (Selection) languagesList.getSelectedValue();

    final String s = (String) JOptionPane.showInputDialog(this, "Name of Language",
        "Edit Language", JOptionPane.PLAIN_MESSAGE, null, null, current.getName());

    if ((s != null) && !s.isEmpty() && !s.equals(current.toString())) {
      language = Db.getLanguageById(Integer.parseInt(current.getValue()));
      language.setName(s);
      Db.saveOrUpdate(language);

      languagesList.setModel(new ListListModel(new ItemViewFilter("Language",
          ItemViewFilter.SELECTOR_DB, ItemViewFilter.CLAUSETYPE_DBFIELD,
          ItemViewFilter.OPERATOR_EQUAL, "Languages", "LA_Id", "", "").getSelections()));
      for (int i = 0; i < languagesList.getModel().getSize(); i++) {
        if (((Selection) languagesList.getModel().getElementAt(i)).getValue().equals(
            String.valueOf(language.getId()))) {
          languagesList.setSelectedIndex(i);
          languagesList.ensureIndexIsVisible(i);
        }
      }

      languageTextField.setText(s);
    }
  }// GEN-LAST:event_editLanguageActionPerformed

  private void selectIncludes(final boolean b) {
    yearCheckbox.setSelected(b);
    genreCheckbox.setSelected(b);
    publisherCheckbox.setSelected(b);
    noOfPlayersCheckbox.setSelected(b);
    programmerCheckbox.setSelected(b);
    controlCheckbox.setSelected(b);
    musicianCheckbox.setSelected(b);
    commentCheckbox.setSelected(b);
    languageCheckbox.setSelected(b);
    includesChanged();
  }

  private void includesChanged() {
    yearComboBox.setEnabled(yearCheckbox.isSelected());

    publisherTextField.setEnabled(publisherCheckbox.isSelected());
    // publisherTextField.setBackground(publisherCheckbox.isSelected() ? new
    // java.awt.Color(255, 255, 204) :
    // javax.swing.UIManager.getDefaults().getColor("TextField.inactiveBackground"));
    publisherButton.setEnabled(publisherCheckbox.isSelected());
    // TODO
    publisherTextField.setBackground(Color.BLUE);
    publisherTextField.updateUI();

    programmerTextField.setEnabled(programmerCheckbox.isSelected());
    programmerTextField.setBackground(programmerCheckbox.isSelected() ? new java.awt.Color(255,
        255, 204) : javax.swing.UIManager.getDefaults().getColor("TextField.inactiveBackground"));
    programmerButton.setEnabled(programmerCheckbox.isSelected());

    musicianTextField.setEnabled(musicianCheckbox.isSelected());
    musicianTextField.setBackground(musicianCheckbox.isSelected() ? new java.awt.Color(255, 255,
        204) : javax.swing.UIManager.getDefaults().getColor("TextField.inactiveBackground"));
    musicianButton.setEnabled(musicianCheckbox.isSelected());

    languageTextField.setEnabled(languageCheckbox.isSelected());
    languageTextField.setBackground(languageCheckbox.isSelected() ? new java.awt.Color(255, 255,
        204) : javax.swing.UIManager.getDefaults().getColor("TextField.inactiveBackground"));
    languageButton.setEnabled(languageCheckbox.isSelected());

    genreComboBox.setEnabled(genreCheckbox.isSelected());

    controlComboBox.setEnabled(controlCheckbox.isSelected());

    noOfPlayersRadioButton.setEnabled(noOfPlayersCheckbox.isSelected());
    noOfPlayersRangeRadioButton.setEnabled(noOfPlayersCheckbox.isSelected());
    simultaneousPlayCheckBox.setEnabled(noOfPlayersCheckbox.isSelected());

    noOfPlayersTextField.setEditable(noOfPlayersCheckbox.isSelected()
        && noOfPlayersRadioButton.isSelected());
    noOfPlayersMinTextField.setEditable(noOfPlayersCheckbox.isSelected()
        && !noOfPlayersRadioButton.isSelected());
    noOfPlayersMaxTextField.setEditable(noOfPlayersCheckbox.isSelected()
        && !noOfPlayersRadioButton.isSelected());

    commentTextField.setEditable(commentCheckbox.isSelected());
  }

  private void okActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_okActionPerformed
    if (!isList) {
      list = new ArrayList<Item>();
      list.add(game);
    }

    for (final Item item : list) {

      if (item instanceof Game) {
        final Game game = (Game) item;

        if (yearCheckbox.isSelected()) {
          game.setYear(Db.getYearById(Integer.parseInt(((Selection) yearComboBox.getSelectedItem())
              .getValue())));
        }

        if (publisherCheckbox.isSelected()) {
          game.setPublisher(publisher);
        }

        if (programmerCheckbox.isSelected()) {
          game.setProgrammer(programmer);
        }

        if (musicianCheckbox.isSelected()) {
          game.setMusician(musician);
        }

        if (languageCheckbox.isSelected()) {
          game.setLanguage(language);
        }

        if (genreCheckbox.isSelected()) {
          game.setGenre(Db.getGenreById(Integer.parseInt(((Selection) genreComboBox
              .getSelectedItem()).getValue())));
        }

        if (controlCheckbox.isSelected()) {
          game.setControl(controlComboBox.getSelectedIndex());
        }

        if (noOfPlayersCheckbox.isSelected()) {
          if (noOfPlayersRadioButton.isSelected()) {
            game.setPlayersMin(Integer.parseInt(noOfPlayersTextField.getText()));
            game.setPlayersMax(Integer.parseInt(noOfPlayersTextField.getText()));
          } else {
            game.setPlayersMin(Integer.parseInt(noOfPlayersMinTextField.getText()));
            game.setPlayersMax(Integer.parseInt(noOfPlayersMaxTextField.getText()));
          }
          game.setIsSimultaneouslyPlayable(simultaneousPlayCheckBox.isSelected());
        }

        if (commentCheckbox.isSelected()) {
          game.setComment(commentTextField.getText());
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