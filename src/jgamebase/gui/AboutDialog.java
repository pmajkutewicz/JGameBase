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

import static jgamebase.Const.log;

import java.io.File;
import java.io.IOException;

import jgamebase.Const;
import jgamebase.db.Db;
import jgamebase.db.model.Game;
import jgamebase.model.Databases;
import jgamebase.model.Preferences;
import jgamebase.tools.FileTools;

/**
 * @author F. Gerbig (fgerbig@users.sourceforge.net)
 */
public class AboutDialog extends javax.swing.JDialog {

  /** Creates new form AboutDialog */

  private static final long serialVersionUID = -3466345422382821256L;

  public AboutDialog(final java.awt.Frame parent, final boolean modal) {
    super(parent, modal);
    initComponents();
    aboutIcon.setIcon(Const.ICON_JGAMEBASE);
    aboutName.setText(Databases.getCurrent().getDisplayName());
    aboutSince.setText(Preferences.get(Preferences.ABOUT_SINCE));
    aboutGamesPlayed.setText(Preferences.get(Preferences.ABOUT_GAMES_PLAYED));
    aboutMusicPlayed.setText(Preferences.get(Preferences.ABOUT_MUSIC_PLAYED));
    
    aboutGameMostPlayed.setText("");
    int maxTimesPlayed = Db.getMaxTimesPlayed();
    if (maxTimesPlayed > 0) {
      Game gameMostPlayed = Db.getGameByTimesPlayed(maxTimesPlayed);
      aboutGameMostPlayed.setText(gameMostPlayed.getName() + " (" + maxTimesPlayed + " times)");
    }

    // read history file
    String history = "";
    try {
      history = FileTools.readFileAsString("History.txt");
    } catch (final IOException e) {
    }

    if ((history != null) && (!history.isEmpty())) {
      versionHistory.setText(history);
    } else {
      versionHistory.setText("Sorry no history file 'History.txt' found.");
    }
    versionHistory.setCaretPosition(0);

    final String iconFilename = new File(Databases.getCurrent().getPath(),
        "Gfx/AboutScreen_120x200.gif").toString();
    if (new File(iconFilename).exists()) {
      gamebaseIcon.setIcon(new javax.swing.ImageIcon(iconFilename));
    } else {
      log.warn("No icon '" + iconFilename + "' for database " + Databases.getCurrent().getName()
          + " found.");
    }

    gamebaseName.setText(Databases.getCurrent().getDisplayName());

    // read credits file
    String dbCredits = "";
    try {
      dbCredits = FileTools.readFileAsString(new File(Databases.getCurrent().getPath(),
          "Credits.txt").toString());
    } catch (final IOException e) {
    }
    if ((dbCredits != null) && (!dbCredits.isEmpty())) {
      gamebaseAbout.setText(dbCredits);
    } else {
      gamebaseAbout.setText("Sorry no credits file 'Credits.txt' in database directory found.");
    }
    gamebaseAbout.setCaretPosition(0);

    credits.setCaretPosition(0);
    license.setCaretPosition(0);
    this.setSize(480, 280);
    setLocationRelativeTo(null);
    setVisible(true);

  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed"
  // desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    final javax.swing.JTabbedPane jTabbedPane1 = new javax.swing.JTabbedPane();
    final javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
    final javax.swing.JPanel jPanel6 = new javax.swing.JPanel();
    aboutIcon = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
    final javax.swing.JPanel jPanel7 = new javax.swing.JPanel();
    final javax.swing.JLabel jLabel5 = new javax.swing.JLabel();
    aboutName = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel7 = new javax.swing.JLabel();
    aboutSince = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel9 = new javax.swing.JLabel();
    aboutGamesPlayed = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel11 = new javax.swing.JLabel();
    aboutMusicPlayed = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel13 = new javax.swing.JLabel();
    aboutGameMostPlayed = new javax.swing.JLabel();
    final javax.swing.JPanel jPanel8 = new javax.swing.JPanel();
    final javax.swing.JLabel gbCopyright = new javax.swing.JLabel();
    final javax.swing.JLabel jgbCopyright = new javax.swing.JLabel();
    final javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
    final javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
    versionHistory = new javax.swing.JTextArea();
    final javax.swing.JPanel jPanel3 = new javax.swing.JPanel();
    final javax.swing.JPanel jPanel9 = new javax.swing.JPanel();
    gamebaseIcon = new javax.swing.JLabel();
    gamebaseName = new javax.swing.JLabel();
    final javax.swing.JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
    gamebaseAbout = new javax.swing.JTextArea();
    final javax.swing.JPanel jPanel4 = new javax.swing.JPanel();
    final javax.swing.JScrollPane jScrollPane3 = new javax.swing.JScrollPane();
    credits = new javax.swing.JTextArea();
    final javax.swing.JPanel jPanel5 = new javax.swing.JPanel();
    final javax.swing.JScrollPane jScrollPane4 = new javax.swing.JScrollPane();
    license = new javax.swing.JTextArea();

    setTitle("About jGameBase...");
    setModal(true);
    setResizable(false);
    addWindowListener(new java.awt.event.WindowAdapter() {
      @Override
      public void windowClosing(final java.awt.event.WindowEvent evt) {
        formWindowClosing(evt);
      }
    });

    jTabbedPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

    jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
    jPanel1.setLayout(new java.awt.BorderLayout());

    jPanel6.setLayout(new java.awt.GridBagLayout());
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridheight = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
    jPanel6.add(aboutIcon, gridBagConstraints);

    jLabel2.setFont(new java.awt.Font("Dialog", 1, 14));
    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText("jGameBase");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    jPanel6.add(jLabel2, gridBagConstraints);

    jLabel3.setFont(new java.awt.Font("Dialog", 0, 12));
    jLabel3.setText("The Universal OS-Independent Retro-Gaming Frontend");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
    jPanel6.add(jLabel3, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridheight = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    jPanel6.add(jLabel4, gridBagConstraints);

    jPanel1.add(jPanel6, java.awt.BorderLayout.NORTH);

    jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    jPanel7.setLayout(new java.awt.GridBagLayout());

    jLabel5.setText("GameBase:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(5, 3, 3, 0);
    jPanel7.add(jLabel5, gridBagConstraints);

    aboutName.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(5, 0, 3, 3);
    jPanel7.add(aboutName, gridBagConstraints);

    jLabel7.setFont(new java.awt.Font("Dialog", 0, 12));
    jLabel7.setText("Since:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 3, 0);
    jPanel7.add(jLabel7, gridBagConstraints);

    aboutSince.setFont(new java.awt.Font("Dialog", 0, 12));
    aboutSince.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 3);
    jPanel7.add(aboutSince, gridBagConstraints);

    jLabel9.setFont(new java.awt.Font("Dialog", 0, 12));
    jLabel9.setText("Total games played:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 3, 0);
    jPanel7.add(jLabel9, gridBagConstraints);

    aboutGamesPlayed.setFont(new java.awt.Font("Dialog", 0, 12));
    aboutGamesPlayed.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 3);
    jPanel7.add(aboutGamesPlayed, gridBagConstraints);

    jLabel11.setFont(new java.awt.Font("Dialog", 0, 12));
    jLabel11.setText("Total music played:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 3, 0);
    jPanel7.add(jLabel11, gridBagConstraints);

    aboutMusicPlayed.setFont(new java.awt.Font("Dialog", 0, 12));
    aboutMusicPlayed.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 3);
    jPanel7.add(aboutMusicPlayed, gridBagConstraints);

    jLabel13.setFont(new java.awt.Font("Dialog", 0, 12));
    jLabel13.setText("Most played game:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 5, 0);
    jPanel7.add(jLabel13, gridBagConstraints);

    aboutGameMostPlayed.setFont(new java.awt.Font("Dialog", 0, 12));
    aboutGameMostPlayed.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 3);
    jPanel7.add(aboutGameMostPlayed, gridBagConstraints);

    jPanel1.add(jPanel7, java.awt.BorderLayout.CENTER);

    jPanel8.setLayout(new java.awt.GridBagLayout());

    gbCopyright.setFont(new java.awt.Font("Dialog", 0, 12));
    gbCopyright.setText("GameBase Copyright © 1999 - 2010 James Burrows");
    gbCopyright.setText("GameBase Copyright © 1999 - " + Const.DATE_YEAR + " James Burrows");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(5, 0, 3, 0);
    jPanel8.add(gbCopyright, gridBagConstraints);

    jgbCopyright.setFont(new java.awt.Font("Dialog", 0, 12));
    jgbCopyright.setText("jGameBase Copyright © 2006 - 2010 Frank Gerbig");
    jgbCopyright.setText("jGameBase Copyright © 2006 - " + Const.DATE_YEAR + " Frank Gerbig");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
    jPanel8.add(jgbCopyright, gridBagConstraints);

    jPanel1.add(jPanel8, java.awt.BorderLayout.SOUTH);

    jTabbedPane1.addTab("About", jPanel1);

    jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
    jPanel2.setLayout(new java.awt.GridBagLayout());

    jScrollPane1
        .setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    jScrollPane1
        .setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

    versionHistory.setBackground(javax.swing.UIManager.getDefaults().getColor(
        "TextField.inactiveBackground"));
    versionHistory.setEditable(false);
    versionHistory.setLineWrap(true);
    jScrollPane1.setViewportView(versionHistory);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    jPanel2.add(jScrollPane1, gridBagConstraints);

    jTabbedPane1.addTab("Version History", jPanel2);

    jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
    jPanel3.setLayout(new java.awt.GridBagLayout());

    jPanel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    jPanel9.setLayout(new java.awt.GridLayout(1, 1));
    jPanel9.add(gamebaseIcon);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridheight = 2;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
    jPanel3.add(jPanel9, gridBagConstraints);

    gamebaseName.setFont(new java.awt.Font("Dialog", 1, 14));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
    jPanel3.add(gamebaseName, gridBagConstraints);

    jScrollPane2
        .setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    jScrollPane2
        .setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

    gamebaseAbout.setBackground(javax.swing.UIManager.getDefaults().getColor(
        "TextField.inactiveBackground"));
    gamebaseAbout.setLineWrap(true);
    jScrollPane2.setViewportView(gamebaseAbout);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    jPanel3.add(jScrollPane2, gridBagConstraints);

    jTabbedPane1.addTab("GameBase", jPanel3);

    jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
    jPanel4.setLayout(new java.awt.GridBagLayout());

    jScrollPane3
        .setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    jScrollPane3
        .setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

    credits.setBackground(java.awt.Color.black);
    credits.setEditable(false);
    credits.setFont(new java.awt.Font("Dialog", 1, 12));
    credits.setForeground(java.awt.Color.white);
    credits.setLineWrap(true);
    credits
        .setText("James Burrows  & Frank Gerbig present \n\n== jGameBase ==\nThe Universal Emulator Frontend and Database Utility\n\nConcept and design by James Burrows\nJava version by Frank Gerbig\nArtwork by Jacek Bogucki\n\nBeta Testers: \nThe GB64 Team, Jacek Bogucki, Allan Hartigan and Shane Holding.\n\n\nGreets go out to: \nThe GB64 Team, The HVSC Team, Peter Sanden, Per Bolmstedt, Kwed, Red Snapper, Adam Lorentzon, Matthew Allen, Mahoney, and everyone else who has supported GameBase and jGameBase!\n\n\nNo Speccy users were harmed during the making of this program.\n\n\n");
    credits.setWrapStyleWord(true);
    credits.setText(credits.getText() + "GameBase Copyright © 1999-" + Const.DATE_YEAR
        + " James Burrows\n");
    credits.setText(credits.getText() + "jGameBase Copyright © 2006-" + Const.DATE_YEAR
        + " Frank Gerbig\n");
    credits.setText(credits.getText() + "All rights reserved.\n");
    jScrollPane3.setViewportView(credits);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    jPanel4.add(jScrollPane3, gridBagConstraints);

    jTabbedPane1.addTab("Credits", jPanel4);

    jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
    jPanel5.setLayout(new java.awt.GridBagLayout());

    jScrollPane4
        .setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    jScrollPane4
        .setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

    license.setBackground(javax.swing.UIManager.getDefaults().getColor(
        "TextField.inactiveBackground"));
    license.setEditable(false);
    license.setLineWrap(true);
    license
        .setText("jGameBase is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. jGameBase is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the files \"License.txt\" and \"gpl-3.0.txt\" in the jGameBase directory for more details.\n");
    license.setWrapStyleWord(true);
    jScrollPane4.setViewportView(license);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    jPanel5.add(jScrollPane4, gridBagConstraints);

    jTabbedPane1.addTab("License", jPanel5);

    getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);

    final java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    setBounds((screenSize.width - 400) / 2, (screenSize.height - 300) / 2, 400, 300);
  }// </editor-fold>//GEN-END:initComponents

  private void formWindowClosing(final java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowClosing
    dispose();
  }// GEN-LAST:event_formWindowClosing

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JLabel aboutGameMostPlayed;
  private javax.swing.JLabel aboutGamesPlayed;
  private javax.swing.JLabel aboutIcon;
  private javax.swing.JLabel aboutMusicPlayed;
  private javax.swing.JLabel aboutName;
  private javax.swing.JLabel aboutSince;
  private javax.swing.JTextArea credits;
  private javax.swing.JTextArea gamebaseAbout;
  private javax.swing.JLabel gamebaseIcon;
  private javax.swing.JLabel gamebaseName;
  private javax.swing.JTextArea license;
  private javax.swing.JTextArea versionHistory;
  // End of variables declaration//GEN-END:variables

}
