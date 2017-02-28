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

import javax.swing.JOptionPane;

import jgamebase.db.Db;
import jgamebase.db.model.Item;
import jgamebase.db.model.ItemViewFilter;
import jgamebase.db.model.Music;
import jgamebase.db.model.Musician;
import jgamebase.db.model.Selection;

/**
 * The MusicBase GUI.
 * 
 * @author F. Gerbig (fgerbig@users.sourceforge.net)
 */
public class MusicInfoDialog extends javax.swing.JDialog {
  /**
	 * 
	 */
  private static final long serialVersionUID = 9195909492712571939L;

  List<Item> list;

  Music music;

  boolean isList;

  Musician musician;

  public MusicInfoDialog(final Music music) {
    isList = false;
    this.music = music;

    initComponents();
    this.setSize(400, 480);

    selectIncludes(true); // select all fields
    propertiesPane.remove(0); // hide includePane

    musician = music.getMusician();
    musicianTextField.setText(musician.getNameForDisplay());

    favouriteMusicCheckbox.setSelected(music.getIsFavourite());
    adultMusicCheckBox.setSelected(music.getIsAdult());

    setVisible(true);
  }

  public MusicInfoDialog(final List<Item> list) {
    isList = true;
    this.list = list;
    initComponents();
    this.setSize(400, 480);

    selectIncludes(false); // select none fields

    musician = Db.getMusicianById(Musician.NEUTRAL_ID);
    musicianTextField.setText(musician.getNameForDisplay());
    setLocationRelativeTo(null);
    setVisible(true);
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JCheckBox adultMusicCheckBox;
  private javax.swing.JPanel chooseMusicianPanel;
  private javax.swing.JCheckBox favouriteMusicCheckbox;
  private javax.swing.JCheckBox includeAdultMusicCheckbox;
  private javax.swing.JCheckBox includeFavouriteMusicCheckbox;
  private javax.swing.JCheckBox includeMusicianCheckbox;
  private javax.swing.JButton musicianButton;
  private javax.swing.JTextField musicianTextField;
  private javax.swing.JList musiciansList;
  private javax.swing.JTabbedPane propertiesPane;

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

    chooseMusicianPanel = new javax.swing.JPanel();
    final javax.swing.JScrollPane jScrollPane3 = new javax.swing.JScrollPane();
    musiciansList = new javax.swing.JList();
    final javax.swing.JButton newMusicianButton = new javax.swing.JButton();
    final javax.swing.JButton editMusicianButton = new javax.swing.JButton();
    propertiesPane = new javax.swing.JTabbedPane();
    final javax.swing.JPanel includePanel = new javax.swing.JPanel();
    final javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
    includeMusicianCheckbox = new javax.swing.JCheckBox();
    includeFavouriteMusicCheckbox = new javax.swing.JCheckBox();
    includeAdultMusicCheckbox = new javax.swing.JCheckBox();
    final javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
    final javax.swing.JButton includeAllButton = new javax.swing.JButton();
    final javax.swing.JButton includeNoneButton = new javax.swing.JButton();
    final javax.swing.JPanel fieldsPanel = new javax.swing.JPanel();
    final javax.swing.JLabel musicianLabel = new javax.swing.JLabel();
    musicianTextField = new javax.swing.JTextField();
    musicianButton = new javax.swing.JButton();
    adultMusicCheckBox = new javax.swing.JCheckBox();
    favouriteMusicCheckbox = new javax.swing.JCheckBox();
    final javax.swing.JPanel jPanel3 = new javax.swing.JPanel();
    final javax.swing.JPanel OkCancelPanel = new javax.swing.JPanel();
    final javax.swing.JPanel jPanel9 = new javax.swing.JPanel();
    final javax.swing.JButton okButton = new javax.swing.JButton();
    final javax.swing.JButton cancelButton = new javax.swing.JButton();

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

    setTitle("Music Info Properties...");
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
    jPanel1.setLayout(new java.awt.GridLayout(5, 1));

    includeMusicianCheckbox.setMnemonic('M');
    includeMusicianCheckbox.setText("Musician");
    includeMusicianCheckbox.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        includesChangedActionPerformed(evt);
      }
    });
    jPanel1.add(includeMusicianCheckbox);

    includeFavouriteMusicCheckbox.setMnemonic('v');
    includeFavouriteMusicCheckbox.setText("Favourite Music");
    includeFavouriteMusicCheckbox.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        includesChangedActionPerformed(evt);
      }
    });
    jPanel1.add(includeFavouriteMusicCheckbox);

    includeAdultMusicCheckbox.setMnemonic('u');
    includeAdultMusicCheckbox.setText("Adult Music");
    includeAdultMusicCheckbox.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        includesChangedActionPerformed(evt);
      }
    });
    jPanel1.add(includeAdultMusicCheckbox);

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

    musicianLabel.setDisplayedMnemonic('M');
    musicianLabel.setLabelFor(musicianButton);
    musicianLabel.setText("Musician:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    fieldsPanel.add(musicianLabel, gridBagConstraints);

    musicianTextField.setEditable(false);
    musicianTextField.setFocusable(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
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
    gridBagConstraints.gridy = 1;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
    fieldsPanel.add(musicianButton, gridBagConstraints);

    adultMusicCheckBox.setMnemonic('u');
    adultMusicCheckBox.setText("Adult Music");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
    fieldsPanel.add(adultMusicCheckBox, gridBagConstraints);

    favouriteMusicCheckbox.setMnemonic('v');
    favouriteMusicCheckbox.setText("Favourite Music");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    fieldsPanel.add(favouriteMusicCheckbox, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
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

  private void chooseOrEditMusicianActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_chooseOrEditMusicianActionPerformed
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
  }// GEN-LAST:event_chooseOrEditMusicianActionPerformed

  private void includesChangedActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_includesChangedActionPerformed
    includesChanged();
  }// GEN-LAST:event_includesChangedActionPerformed

  private void selectAllIncludesActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_selectAllIncludesActionPerformed
    selectIncludes(true);
  }// GEN-LAST:event_selectAllIncludesActionPerformed

  private void selectNoneIncludesActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_selectNoneIncludesActionPerformed
    selectIncludes(false);
  }// GEN-LAST:event_selectNoneIncludesActionPerformed

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

  private void cancelActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cancelActionPerformed
    dispose();
  }// GEN-LAST:event_cancelActionPerformed

  private void selectIncludes(final boolean b) {
    includeMusicianCheckbox.setSelected(b);
    includeFavouriteMusicCheckbox.setSelected(b);
    includeAdultMusicCheckbox.setSelected(b);
    includesChanged();
  }

  private void includesChanged() {
    musicianTextField.setEnabled(includeMusicianCheckbox.isSelected());
    musicianTextField.setBackground(includeMusicianCheckbox.isSelected() ? new java.awt.Color(255,
        255, 204) : javax.swing.UIManager.getDefaults().getColor("TextField.inactiveBackground"));
    musicianButton.setEnabled(includeMusicianCheckbox.isSelected());

    favouriteMusicCheckbox.setEnabled(includeFavouriteMusicCheckbox.isSelected());
    adultMusicCheckBox.setEnabled(includeAdultMusicCheckbox.isSelected());
  }

  private void okActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_okActionPerformed
    if (!isList) {
      list = new ArrayList<Item>();
      list.add(music);
    }

    for (final Item item : list) {

      if (item instanceof Music) {
        final Music music = (Music) item;

        if (includeMusicianCheckbox.isSelected()) {
          music.setMusician(musician);
        }

        if (includeFavouriteMusicCheckbox.isSelected()) {
          music.setIsFavourite(favouriteMusicCheckbox.isSelected());
        }

        if (includeAdultMusicCheckbox.isSelected()) {
          music.setIsAdult(adultMusicCheckBox.isSelected());

        }

        Db.saveOrUpdate(music);
      }
    }
    dispose();
  }// GEN-LAST:event_okActionPerformed

  private void exitFormWindowClosing(final java.awt.event.WindowEvent evt) {// GEN-FIRST:event_exitFormWindowClosing
    dispose();
  }// GEN-LAST:event_exitFormWindowClosing
}