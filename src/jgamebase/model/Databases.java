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

package jgamebase.model;

import static jgamebase.Const.log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JOptionPane;

import jgamebase.Const;
import jgamebase.JGameBase;
import jgamebase.db.Db;
import jgamebase.gui.Gui;
import jgamebase.plugins.Extractor;
import jgamebase.tools.FileTools;
import jgamebase.tools.IniFileManager;
import jgamebase.tools.ListerTools;
import jgamebase.tools.TempDir;

public class Databases {

  public static final String DB_INI_FILENAME = "db.ini";

  private static List<Database> dbList = null;

  private static Database current;

  public static void setList(final List<Database> databases) {
    dbList = databases;
  }

  public static List<Database> getList() {
    return dbList;
  }

  public static void setCurrent(final Database currentDatabase) {
    current = currentDatabase;
    Db.init(Databases.getCurrent().getName(), false);
  }

  public static Database getCurrent() {
    return current;
  }

  private static void copyDatabases() {
    // list dirs in program directory
    if (JGameBase.option_debug) {
      log.info("Looking for databases to copy in base directory '" + Const.GBDIR_RO + "'...");
    }

    final List<String> dirs_ro = ListerTools.list_Dirs_Files_Paths(Const.GBDIR_RO.toString(), true,
        false, true);
    boolean possible_db;

    for (final String dir : dirs_ro) {
      final String subdir = Paths.removePath(dir);

      if (JGameBase.option_debug) {
        log.info("Looking for database in directory '" + subdir + "'");
      }
      
      // check no known subdirs
      if (!isKnownGBSubdir(subdir)) {

        possible_db = false;

        // is there a db dir in the sub dir?
        final File dbdir = new File(dir, Const.DATABASE_DIRNAME);

        if ((dbdir.exists() && dbdir.isDirectory())) {
          possible_db = true;
        } else {
          final List<String> mdbs = new FileExtensions("mdb").getMatching(ListerTools
              .list_Dirs_Files_Paths(dir, false, true, false));
          if (mdbs.size() > 0) {
            possible_db = true;
          }
        }

        if (possible_db) {
          final File dir_ro = new File(dir);
          final File dir_rw = FileTools.replacePath(dir_ro, Const.GBDIR_RO, Const.GBDIR_RW);

          // copy db?
          if (!dir_rw.exists()) {
            log.info("Found database in \"" + dir_ro + "\", copying to \"" + dir_rw + "\",");
            StringBuilder s = new StringBuilder("  (excluding sub directories ");
            for (int i = 0; i < Const.DB_SUBDIRS.length; i++) {
              s.append("\"").append(Const.DB_SUBDIRS[i]).append("\"");
              if (i < (Const.DB_SUBDIRS.length - 1)) {
                s.append(", ");
              }
            }
            log.info(s.append(").").toString());
            log.info("");
            try {
              // copy excluding db sub directories
              FileTools.copyDir(dir_ro, dir_rw, Const.DB_SUBDIRS);
            } catch (final Exception e) {
              e.printStackTrace();
            }

            // create not copied db sub directories
            for (final String element : Const.DB_SUBDIRS) {
              new File(dir_rw, element).mkdir();
            }
          }
        }
      }
    }
  }

  /**
   * @param subdir
   * @return true if the subdir is an known subdir of jgb
   */
  private static boolean isKnownGBSubdir(final String subdir) {
    return subdir.equals("Artwork") || subdir.equals("bin") || subdir.equals("Docs")
        || subdir.equals("javadoc") || subdir.equals("lib") || subdir.equals("Overlays")
        || subdir.equals("src");
  }

  private static void copyDatabaseNames() {
    // if necessary copy database names
    final File src = new File(Const.GBDIR_RO, DB_INI_FILENAME);
    final File dst = new File(Const.GBDIR_RW, DB_INI_FILENAME);

    if ((!dst.exists()) || (dst.length() == 0)) {
      try {
        log.info("Copying database names from \"" + src + "\" to \"" + dst + "\".\n");
        FileTools.copyFile(src, dst);
      } catch (final IOException e) {
        e.printStackTrace();
      }
    }
  }

  private static void importDatabases() {
    // list all subdirs of jgamebase
    final List<String> dirs = ListerTools.list_Dirs_Files_Paths(Const.GBDIR_RW.toString(), true,
        false, true);

    // iterate over subdirs checking for files to import
    for (final String dir : dirs) {
      final String subdir = Paths.removePath(dir);

      // check no known subdirs
      if (!subdir.equals("Artwork") && !subdir.equals("bin") && !subdir.equals("Docs")
          && !subdir.equals("javadoc") && !subdir.equals("lib") && !subdir.equals("src")) {
        // is there a db dir in the sub dir?
        final File dbdir = new File(dir, Const.DATABASE_DIRNAME);
        if (!(dbdir.exists() && dbdir.isDirectory())) {
          // no "Database" subdir, search for *.mdb
          System.out.println("Looking for Access databases to import in '" + dir + "'");
          final List<String> potentialMdbs = new FileExtensions("mdb").getMatching(ListerTools.list_Dirs_Files_Paths(dir, false, true, false));
          final List<String> mdbs = new ArrayList<String>();
          
          // skip database templates "Empty*"
          for (String mdb: potentialMdbs) {
            if (!mdb.toLowerCase().startsWith("empty")) {
              mdbs.add(mdb);
            }
          }
          
          // more than one access database in subdir?
          if (mdbs.size() > 1) {
            log.info("Found more than one unimported access database in dir '" + dir
                + "'.\nWill NOT import (please remove additional access databases).");
            Gui.displayWarningDialog("Found more than one unimported access database in dir '"
                + dir + "'.\nWill NOT import (please remove additional access databases).");
          }

          // found exactly one access database => ask to import
          if (mdbs.size() == 1) {
            final String mdb = mdbs.get(0);
            log.info("Found unimported access database '" + new File(dir, mdb) + "'.");

            if (JOptionPane.showConfirmDialog(null, "Found access database '" + new File(dir, mdb)
                + "'.\nTry to import ?", "jGameBase", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
              Gui.importAccessToDatabase(new File(dir, mdb));
            }
          }
        }
      }
    }
    System.out.println();
  }

  public static void init() {

    if (Const.FHS) {
      copyDatabases();
      copyDatabaseNames();
    }

    importDatabases();

    // after all access databases have been imported look for databases

    // list all subdirs of jgamebase
    final List<String> dirs = ListerTools.list_Dirs_Files_Paths(Const.GBDIR_RW.toString(), true,
        false, true);

    dbList = new ArrayList<Database>();
    // iterate over subdirs checking for databases
    for (final String dir : dirs) {
      final String dirname = Paths.removePath(dir);

      // check no known subdirs
      if (!dirname.equals("gamebase") && !dirname.equals("lib") && !dirname.equals("Artwork")) {
        // is there a db dir in the sub dir?
        final File dbdir = new File(dir, Const.DATABASE_DIRNAME);
        if (dbdir.exists() && dbdir.isDirectory()) {
          log.info("Found database '" + dirname + "'.");
          final Database database = new Database(dirname);
          dbList.add(database);
        }
      }
    }

    // try to find a db name for display
    // 1. look in local db.ini
    // 2. look in global db.ini
    // 3. use db name with underscores substituted by spaces
    try {
      final File globalDbIniFile = new File(Const.GBDIR_RW, DB_INI_FILENAME);
      IniFileManager globalDbIni = null;

      // try to read global db.ini
      if (globalDbIniFile.exists()) {
        globalDbIni = new IniFileManager(globalDbIniFile);
      }

      for (final Database db : dbList) {
        String displayName = "";

        // try to read local db.ini
        final File localDbIniFile = new File(db.getPath(), DB_INI_FILENAME);
        if (localDbIniFile.exists()) {
          final IniFileManager localDbIni = new IniFileManager(localDbIniFile.toString());
          displayName = localDbIni.get("DB_NAME", "NAME");
        }

        // if no display name yet, try global db.ini
        if (((displayName == null) || (displayName.isEmpty())) && (globalDbIni != null)) {
          displayName = globalDbIni.get("DB_NAMES", db.getName());
        }

        // if still no display name, construct one
        if ((displayName == null) || (displayName.isEmpty())) {
          displayName = db.getName().replaceAll("_", " ");
        }

        db.setDisplayName(displayName);
      }
    } catch (final IOException e) {
      e.printStackTrace();
    }

    // sort databases by display name
    Collections.sort(dbList, new Comparator<Database>() {
      @Override
      public int compare(final Database d1, final Database d2) {
        return d1.getDisplayName().compareToIgnoreCase(d2.getDisplayName());
      }
    });
  }

  // don't let anyone instantiate this class
  private Databases() {
  }

}
