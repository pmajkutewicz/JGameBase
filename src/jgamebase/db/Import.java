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

package jgamebase.db;

import static jgamebase.Const.log;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import jgamebase.Const;
import jgamebase.gui.Gui;
import jgamebase.model.Databases;
import jgamebase.tools.FileTools;

import org.hibernate.Session;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class Import {

  public static void csv2Db() throws Exception {
    // get current db path
    final String dbName = Databases.getCurrent().getName();

    // get current db path
    final File dbPath = Databases.getCurrent().getPath();

    // close current database
    Db.close();

    // get file for database and for backup dir
    final File dbDir = new File(dbPath, Const.DATABASE_DIRNAME);
    final File dbBackupDir = new File(dbPath, Const.DATABASEBACKUP_DIRNAME);

    // Remove old backup
    if (dbBackupDir.exists()) {
      FileTools.deleteAll(dbBackupDir);
    }

    // copy database to backup
    // TODO dbDir.renameTo(dbBackupDir);

    // create new database
    Db.init(dbName, true);

    // import (and close) database
    csv2Db(new File(dbPath, Const.EXPORT_DIRNAME));

    // reopen database
    Db.init(dbName, false);
  }

  public static void csv2Db(final File file) throws Exception {
    final String dbPath = file.getParentFile().getAbsolutePath();

    log.info("Starting import of database: " + file.getParentFile().getName());
    final Session session = Db.getSession();

    for (final String table : Table.getNames()) {
      try {
        importTable(session, dbPath, table);
      } catch (final SQLException e) {
        log.info("");
        log.info("  Switching to alternate import method because of the following exception: " + e);
        importTableLineByLine(session, dbPath, table);
      }
      log.info("  Table '" + table + "': "
          + session.createSQLQuery("select count(*) from " + table).uniqueResult()
          + " rows imported.\n");
    }

    log.info("Import of database successfully finished.");

    // reorganize database to update statistics
    Db.reorganize();

    // close database
    Db.close();
  }

  private static void importTable(final Session session, final String dbPath, final String tableName)
      throws Exception {
    final String csvFilename = dbPath + "/" + Const.EXPORT_DIRNAME + "/" + tableName + ".csv";

    // check if csv file to import exists
    if (!new File(csvFilename).canRead()) {
      final String error = "CSV file '" + csvFilename + "' not found!";
      Gui.displayErrorDialog(error);
      throw new Exception(error);
    }

    // check if csv file to import is not empty
    if (new File(csvFilename).length() == 0) {
      final String warning = "CSV file '" + csvFilename + "' is empty!";

      if (!tableName.equals(Table.MUSIC.getName()) && !tableName.equals(Table.VIEWDATA.getName())
          && !tableName.equals(Table.VIEWFILTERS.getName())) {
        Gui.displayWarningDialog(warning);
      }
    } else { // import
      log.info("  Importing data from csv file '" + csvFilename + "' into table '"
          + tableName.toUpperCase() + "'.");
      _importTable(session, tableName, csvFilename);
    }
    session.flush();
  }

  private static void importTableLineByLine(final Session session, final String dbPath,
      final String tablename) throws Exception {
    final String csvFilename = dbPath + "/" + Const.EXPORT_DIRNAME + "/" + tablename + ".csv";

    // check if csv file to import exists
    if (!new File(csvFilename).canRead()) {
      final String error = "CSV file '" + csvFilename + "' not found!";
      Gui.displayErrorDialog(error);
      throw new Exception(error);
    }

    // check if csv file to import is not empty
    if (new File(csvFilename).length() == 0) {
      final String error = "CSV file '" + csvFilename + "' is empty!";

      if (!tablename.equals(Table.MUSIC.getName())) { // Music table may be
                                                      // empty
        Gui.displayErrorDialog(error);
        throw new Exception(error);
      }
    } else { // import
      final String singleLineFilename = csvFilename + ".sle"; // SingleLinE

      log.info("  Importing data from csv file '" + csvFilename + "' into table '"
          + tablename.toUpperCase() + "' line by line.");

      // parse data as CSV
      final CSVReader reader = new CSVReader(new FileReader(csvFilename),
          CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, Const.CSV_ESCAPE_CHAR);
      final List<String[]> lines = reader.readAll();

      // iterate over csv 'lines' (if the csv contains \n or \r this may be more
      // than one actual line)
      for (final String[] line : lines) {
        // write only one 'line' to a temporary file
        final CSVWriter writer = new CSVWriter(new FileWriter(singleLineFilename));
        writer.writeNext(line);
        writer.close();
        try {
          // import this file containing only one line of the original csv file
          _importTable(session, tablename, singleLineFilename);
        } catch (final SQLException e) {
          log.warn("");
          log.warn("  Skipping line:");
          log.warn(Arrays.toString(line));
          log.warn("  because of the following exception: " + e);
        }
        // delete temporary file
        new File(singleLineFilename).delete();
      }
    }
    session.flush();
  }

  private static void _importTable(final Session session, final String tableName,
      final String csvFilename) throws SQLException {
    final CallableStatement cs = session.connection().prepareCall(
        "CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE(?,?,?,?,?,?,?)");
    cs.setString(1, null); // schema name
    cs.setString(2, tableName.toUpperCase());
    cs.setString(3, csvFilename);
    cs.setString(4, null); // column delimiter null => ,
    cs.setString(5, null); // character delimiter null => "
    cs.setString(6, null); // codeset null = default
    cs.setShort(7, (short) 0); // 0: insert, 1: replace
    cs.execute();
    cs.close();
  }
}
