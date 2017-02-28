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

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import jgamebase.Const;
import jgamebase.db.Db;
import jgamebase.tools.GemusToJs;
import jgamebase.tools.TempDir;

/**
 * The ToolBox GUI.
 * 
 * @author F. Gerbig (fgerbig@users.sourceforge.net)
 */
public class ToolboxGui extends javax.swing.JFrame {

  /**
	 *
	 */
  private static final long serialVersionUID = -3626362724285446947L;

  File file = null;

  public ToolboxGui() {
    initComponents();
    setLocationRelativeTo(null);
    setVisible(true);
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  javax.swing.JTextArea outputArea;

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

    final javax.swing.JPanel mainPanel = new javax.swing.JPanel();
    final javax.swing.JScrollPane mainScrollPane = new javax.swing.JScrollPane();
    outputArea = new javax.swing.JTextArea();
    final javax.swing.JMenuBar menuBar = new javax.swing.JMenuBar();
    final javax.swing.JMenu fileMenu = new javax.swing.JMenu();
    final javax.swing.JSeparator fileMenu_Separator2 = new javax.swing.JSeparator();
    final javax.swing.JMenuItem fileMenu_Exit = new javax.swing.JMenuItem();
    final javax.swing.JMenu importMenu = new javax.swing.JMenu();
    final javax.swing.JMenuItem importMenuItem1 = new javax.swing.JMenuItem();
    final javax.swing.JMenuItem importMenuItem2 = new javax.swing.JMenuItem();
    final javax.swing.JMenuItem importMenuItem3 = new javax.swing.JMenuItem();
    final javax.swing.JMenuItem importMenuItem4 = new javax.swing.JMenuItem();
    final javax.swing.JMenuItem importMenuItem5 = new javax.swing.JMenuItem();
    final javax.swing.JMenuItem importMenuItem6 = new javax.swing.JMenuItem();
    final javax.swing.JMenuItem importMenuItem7 = new javax.swing.JMenuItem();
    final javax.swing.JMenuItem importMenuItem8 = new javax.swing.JMenuItem();
    final javax.swing.JMenu toolsMenu = new javax.swing.JMenu();
    final javax.swing.JMenuItem toolsMenu_reorg = new javax.swing.JMenuItem();
    final javax.swing.JMenuItem toolsMenu_gemusToJs = new javax.swing.JMenuItem();
    final javax.swing.JMenu helpMenu = new javax.swing.JMenu();
    final javax.swing.JMenuItem helpMenu_About = new javax.swing.JMenuItem();

    setTitle(Const.NAME_TOOLBOX);
    setIconImage(Const.IMAGE_TOOLBOX);
    setMinimumSize(new java.awt.Dimension(320, 240));
    addWindowListener(new java.awt.event.WindowAdapter() {
      @Override
      public void windowClosing(final java.awt.event.WindowEvent evt) {
        exitFormWindowClosing(evt);
      }
    });

    mainPanel.setLayout(new java.awt.BorderLayout());

    outputArea.setBackground(new java.awt.Color(237, 236, 236));
    outputArea.setColumns(20);
    outputArea.setEditable(false);
    outputArea.setRows(5);
    mainScrollPane.setViewportView(outputArea);

    mainPanel.add(mainScrollPane, java.awt.BorderLayout.CENTER);

    getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

    fileMenu.setText("File");

    fileMenu_Separator2.setPreferredSize(new java.awt.Dimension(2, 2));
    fileMenu.add(fileMenu_Separator2);

    fileMenu_Exit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4,
        java.awt.event.InputEvent.ALT_MASK));
    fileMenu_Exit.setText("Exit");
    fileMenu_Exit.setToolTipText("Exits the program.");
    fileMenu_Exit.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        quitActionPerformed(evt);
      }
    });
    fileMenu.add(fileMenu_Exit);

    menuBar.add(fileMenu);

    importMenu.setText("Import");

    importMenuItem1.setText("1 Get Access file");
    importMenuItem1
        .setToolTipText("Get Access file to import (the file has to be located in a direct subdirectory of jGameBase 'jgb/<DB>/*.mdb').");
    importMenuItem1.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        importStep1_ActionPerformed(evt);
      }
    });
    importMenu.add(importMenuItem1);

    importMenuItem2.setText("2 Create export directory");
    importMenuItem2.setToolTipText("Create the export directory 'jgb/<DB>/Export'.");
    importMenuItem2.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        importStep2_ActionPerformed(evt);
      }
    });
    importMenu.add(importMenuItem2);

    importMenuItem3.setText("3 Create export script");
    importMenuItem3.setToolTipText("Create export script 'jgb/<DB>/Export/export.sh'.");
    importMenuItem3.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        importStep3_ActionPerformed(evt);
      }
    });
    importMenu.add(importMenuItem3);

    importMenuItem4.setText("4 Execute export script");
    importMenuItem4
        .setToolTipText("Export from Access to csv (execute script 'jgb/<DB>/Export/export.sh').");
    importMenuItem4.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        importStep4_ActionPerformed(evt);
      }
    });
    importMenu.add(importMenuItem4);

    importMenuItem5.setText("5 Add key to ViewFilters");
    importMenuItem5
        .setToolTipText("Adds a primary key column to the file 'jgb/<DB>/Export/ViewFilters.csv'.");
    importMenuItem5.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        importStep5_ActionPerformed(evt);
      }
    });
    importMenu.add(importMenuItem5);

    importMenuItem6.setText("6 Create empty database");
    importMenuItem6.setToolTipText("Create derby db in 'jgb/<DB>/Database'.");
    importMenuItem6.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        importStep6_ActionPerformed(evt);
      }
    });
    importMenu.add(importMenuItem6);

    importMenuItem7.setText("7 Import database");
    importMenuItem7.setToolTipText("Import csv files from 'jgb/<DB>/Export/*.csv' to derby db.");
    importMenuItem7.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        importStep7_ActionPerformed(evt);
      }
    });
    importMenu.add(importMenuItem7);

    importMenuItem8.setText("8 Remove export directory");
    importMenuItem8.setToolTipText("Remove the export directory 'jgb/<DB>/Export'.");
    importMenuItem8.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        importStep8_ActionPerformed(evt);
      }
    });
    importMenu.add(importMenuItem8);

    menuBar.add(importMenu);

    toolsMenu.setText("Tools");

    toolsMenu_reorg.setText("Reorganize");
    toolsMenu_reorg.setToolTipText("Reorganize the current database.");
    toolsMenu_reorg.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        reorgActionPerformed(evt);
      }
    });
    toolsMenu.add(toolsMenu_reorg);

    toolsMenu_gemusToJs.setText("Gemus to JavaScript");
    toolsMenu_gemusToJs.setToolTipText("Convert a GEMUS script to JavaScript");
    toolsMenu_gemusToJs.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        gemusToJsActionPerformed(evt);
      }
    });
    toolsMenu.add(toolsMenu_gemusToJs);

    menuBar.add(toolsMenu);

    helpMenu.setText("Help");

    helpMenu_About.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A,
        java.awt.event.InputEvent.CTRL_MASK));
    helpMenu_About.setText("About ToolBox");
    helpMenu_About.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        about_ActionPerformed(evt);
      }
    });
    helpMenu.add(helpMenu_About);

    menuBar.add(helpMenu);

    setJMenuBar(menuBar);

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void importStep1_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_importStep1_ActionPerformed
    println("#1 Get Access file to import (the file has to be located in a direct subdirectory of jGameBase 'jgb/<DB>/*.mdb').");

    try {
      // select file to import
      final JFileChooser fileChooser = new JFileChooser();
      fileChooser.addChoosableFileFilter(new FileFilter() {
        @Override
        public boolean accept(final File f) {
          // show all directories
          if (f.isDirectory()) {
            return true;
          }

          // show all files with the right extension
          if (f.getAbsolutePath().toLowerCase().endsWith(".mdb")) {
            return true;
          }

          // hide everything else (files with a wrong extension)
          return false;
        }

        @Override
        public String getDescription() {
          return "MS Access files (*.mdb)";
        }

      });
      fileChooser.setCurrentDirectory(Const.GBDIR_RW);

      if (fileChooser.showOpenDialog(null) == javax.swing.JFileChooser.APPROVE_OPTION) {
        file = fileChooser.getSelectedFile();
      }
    } catch (final Exception e) {
      e.printStackTrace();
      Gui.displayErrorDialog("Error while getting Access file '" + file.getName() + "'.");
    }

    println("  Access file to import ='" + file.getAbsolutePath() + "'.");
    println();

  }// GEN-LAST:event_importStep1_ActionPerformed

  private void importStep2_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_importStep2_ActionPerformed
    println("#2 Create the export directory 'jgb/<DB>/Export'.");
    if (isFileOk()) {
      // Mdb2Derby.createExportDirectory(file);
      println("  Export directory '" + file.getParentFile().getAbsolutePath()
          + "/Export/' created.");
    }
    println();
  }// GEN-LAST:event_importStep2_ActionPerformed

  private void importStep3_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_importStep3_ActionPerformed
    println("#3 Create export script 'jgb/<DB>/Export/export.sh'.");
    if (isFileOk()) {
      // Mdb2Derby.createExportScript(file);
      println("  Export script '" + file.getParentFile().getAbsolutePath() + "/Export/' created.");
    }
    println();
  }// GEN-LAST:event_importStep3_ActionPerformed

  private void importStep4_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_importStep4_ActionPerformed
    println("#4 Export from Access to csv (execute script 'jgb/<DB>/Export/export.sh').");
    if (isFileOk()) {
      try {
        // Mdb2Derby.executeExportScript(file);
      } catch (final Exception e) {
        e.printStackTrace();
      }
      println("  Export script '" + file.getParentFile().getAbsolutePath() + "/Export/' executed.");
    }
    println();
  }// GEN-LAST:event_importStep4_ActionPerformed

  // addKeyToViewFilters
  private void importStep5_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_importStep5_ActionPerformed
    println("#5 Add a primary key column to the file 'jgb/<DB>/Export/ViewFilters.csv'.");
    if (isFileOk()) {
      try {
        // Mdb2Derby.addKeyToViewFilters(file);
      } catch (final Exception e) {
        e.printStackTrace();
      }
      println("  Primary key column added to '" + file.getParentFile().getAbsolutePath()
          + "/Export/Viewfilters.csv" + "'.");
    }
    println();
  }// GEN-LAST:event_importStep5_ActionPerformed

  private void importStep6_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_importStep6_ActionPerformed
    println("#6 Create derby db in 'jgb/<DB>/Database'.");
    if (isFileOk()) {
      // open new db for creation
      Db.init(file.getParentFile().getName(), true);
      println("  Derby database '" + file.getParentFile().getAbsolutePath() + "/Database' created.");
    }
    println();
  }// GEN-LAST:event_importStep6_ActionPerformed

  private void importStep7_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_importStep7_ActionPerformed
    println("#7 Import csv files from 'jgb/<DB>/Export/*.csv' to derby db.");
    if (isFileOk()) {
      if (isFileOk()) {
        try {
          // import DB
          // Mdb2Derby.importDb(file);

          // close database
          Db.close();

        } catch (final Exception e) {
          e.printStackTrace();
        }
        println("  Import finished.");
      }
    }
    println();
  }// GEN-LAST:event_importStep7_ActionPerformed

  private void importStep8_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_importStep8_ActionPerformed
    println("#8 Remove the export directory 'jgb/<DB>/Export'.");
    if (isFileOk()) {
      if (isFileOk()) {
        // Mdb2Derby.removeExportDirectory(file);
        println("  Export directory '" + file.getParentFile().getAbsolutePath()
            + "/Export/' removed.");
      }
    }
    println();
  }// GEN-LAST:event_importStep8_ActionPerformed

  private void reorgActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_reorgActionPerformed
    Db.reorganize();
  }// GEN-LAST:event_reorgActionPerformed

  private void gemusToJsActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_gemusToJsActionPerformed
    try {
      final JFileChooser fileChooser = new JFileChooser();
      fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

      fileChooser.setCurrentDirectory(Const.GBDIR_RW);

      if (fileChooser.showOpenDialog(null) == javax.swing.JFileChooser.APPROVE_OPTION) {
        GemusToJs.gemusToJs(fileChooser.getSelectedFile());
      }
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }// GEN-LAST:event_gemusToJsActionPerformed

  private void about_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_about_ActionPerformed
    Gui.displayInformationDialog("This is " + Const.TOOLBOX_VERSION
        + ".\nUse it to perform additional database tasks for " + Const.NAME_JGAMEBASE + ".");
  }// GEN-LAST:event_about_ActionPerformed

  private void quitActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_quitActionPerformed
    quit();
  }// GEN-LAST:event_quitActionPerformed

  private void exitFormWindowClosing(final java.awt.event.WindowEvent evt) {// GEN-FIRST:event_exitFormWindowClosing
    quit();
  }// GEN-LAST:event_exitFormWindowClosing

  private boolean isFileOk() {
    if ((file != null) && (file.exists())) {
      return true;
    }
    println("You must first select an Access database '*.mdb' (import step #1)");
    return false;
  }

  private void quit() {
    TempDir.removePath();

    log.info(Const.NAME_TOOLBOX + " finished.");
    System.exit(0); // quit program

  }

  private void print(final String message) {
    final Document doc = outputArea.getDocument();
    try {
      doc.insertString(doc.getLength(), message, null);
    } catch (final BadLocationException e) {
      e.printStackTrace();
    }
  }

  private void println(final String message) {
    print(message);
    println();
  }

  private void println() {
    print(System.getProperty("line.separator"));
  }
}
