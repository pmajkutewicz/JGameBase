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
import java.sql.ResultSet;
import java.util.List;

import jgamebase.Const;
import jgamebase.JGameBase;
import jgamebase.db.filter.BooleanAsDigit_ExportFilter;
import jgamebase.db.filter.DigitAsBoolean_ImportFilter;
import jgamebase.gui.Gui;
import jgamebase.model.Databases;
import jgamebase.tools.FileTools;

import org.hibernate.Session;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.ImportFilter;
import com.healthmarketscience.jackcess.ImportUtil;
import com.healthmarketscience.jackcess.JetFormat;
import com.healthmarketscience.jackcess.SimpleImportFilter;

public class Export {

  public static void mdb2Csv(final File file) {
    try {
      final File dir = new File(file.getParentFile(), Const.EXPORT_DIRNAME);

      File dataFile;
      List<String[]> data;

      // clear export directory
      if (dir.exists()) {
        FileTools.deleteAll(dir);
      }
      dir.mkdirs();

      // set character encoding (for me this has to be 8850-1)
      System.setProperty(Database.CHARSET_PROPERTY_PREFIX + JetFormat.VERSION_3, "ISO-8859-1");
      // open database
      final Database mdb = Database.open(file, true);
      // export access database to csv
      // ExportUtil.exportAll(mdb, dir, "csv", false, ",", '"', new
      // BooleanAsDigit_ExportFilter());
      com.healthmarketscience.jackcess.ExportUtil.exportAll(mdb, dir, "csv", false, ",", '"',
          new BooleanAsDigit_ExportFilter());

      // add key to viewfilters
      dataFile = new File(dir, Table.VIEWFILTERS.getName() + ".csv");
      data = new CSVReader(new FileReader(dataFile)).readAll(); // no
                                                                // backslashes
                                                                // => no special
                                                                // treatment
      data = Update.transformColumns(data, -1, 0, 1, 2, 3, 4, 5, 6, 7);
      final CSVWriter writer = new CSVWriter(new FileWriter(dataFile));
      writer.writeAll(data);
      writer.close();

      // check if csv files need to be updated
      dataFile = new File(dir, Table.CONFIG.getName() + ".csv");
      data = new CSVReader(new FileReader(dataFile)).readAll(); // no
                                                                // backslashes
                                                                // => no special
                                                                // treatment
      double version = 0.0;
      try {
        version = Double.valueOf(data.get(0)[0] + "." + data.get(0)[1]).doubleValue();
      } catch (final Exception e1) {
        e1.printStackTrace();
      }

      if (version == 0.0) {
        log.info("Warning: Could not read database version.");
        Gui.displayWarningDialog("Could not read database version.");
      } else if (version < Db.NEEDED_VERSION) {
        log.info("\nFound database in version " + version + ", but need version "
            + Db.NEEDED_VERSION + ": trying to update it...\n");

        final boolean errorOccured = Update.updateFrom(dir, version);

        if (errorOccured) {
          log.info("ERROR: Could not update database to version " + Db.NEEDED_VERSION + ".");
          Gui.displayErrorDialog("Could not update database to version " + Db.NEEDED_VERSION + ".");
          JGameBase.quit();
        }
      }

    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  public static void db2Csv(final List<String> tableNames) throws Exception {
    final File exportPath = Databases.getCurrent().getCleanExportPath();

    log.info("Starting export of database: " + Databases.getCurrent().getName());
    final Session session = Db.getSession();

    for (final String table : tableNames) {
      final File csvFile = new File(exportPath, table + ".csv");

      // delete CSV file (export works only for non existing files)
      if ((csvFile.exists()) && (!csvFile.delete())) {
        final String error = "Can't delete CSV file '" + csvFile + "'!";
        throw new Exception(error);
      }

      // export
      log.info("  Exporting data from table '" + table.toUpperCase() + "' into csv file '"
          + csvFile + "'.");

      final CallableStatement cs = session.connection().prepareCall(
          "CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE(?,?,?,?,?,?)");
      cs.setString(1, null); // schema name
      cs.setString(2, table.toUpperCase());
      cs.setString(3, csvFile.getAbsolutePath());
      cs.setString(4, null); // column delimiter null => ,
      cs.setString(5, null); // character delimiter null => "
      cs.setString(6, null); // codeset null = default
      cs.execute();
      cs.close();
      log.info("  Table '" + table + "': "
          + session.createSQLQuery("select count(*) from " + table).uniqueResult()
          + " rows exported.\n");
    }

    log.info("Export of database successfully finished.");
  }

  public static void db2Mdb() throws Exception {

    final String name = Databases.getCurrent().getName();
    final File mdbTemplateFile = new File(Const.GBDIR_RO, "Empty.mdb");
    final File mdbFile = new File(Databases.getCurrent().getCleanExportPath(), name + ".mdb");

    log.info("Starting export of database '" + name + "' to '" + mdbFile + "'");

    FileTools.copyFile(mdbTemplateFile, mdbFile);

    final com.healthmarketscience.jackcess.Database mdbDatabase = com.healthmarketscience.jackcess.Database
        .open(mdbFile, false, false);

    ResultSet srs;
    ImportFilter filter;
    for (final Table table : Table.getValuesAsList()) {
      log.info("  Exporting table '" + table.getName() + "'.");

      // data
      switch (table) {
        case VIEWFILTERS:
          srs = Db
              .getSession()
              .connection()
              .createStatement()
              .executeQuery(
                  "SELECT VW_ID, FIELDTABLE, FIELDNAME, OPERATOR, CLAUSETYPE, CLAUSEDATA, MUSICFIELDNAME, MUSICFIELDTABLE FROM "
                      + table.getName());
          break;

        default:
          srs = Db.getSession().connection().createStatement()
              .executeQuery("SELECT * FROM " + table.getName());
          break;
      }

      // import filter
      switch (table) {
        case MUSIC:
          filter = new DigitAsBoolean_ImportFilter(4, 5, 6);
          break;

        case GAMES:
          filter = new DigitAsBoolean_ImportFilter(17, 18, 19, 22, 23, 30, 31);
          break;

        case EXTRAS:
          filter = new DigitAsBoolean_ImportFilter(6);
          break;

        default:
          filter = SimpleImportFilter.INSTANCE;
          break;
      }

      ImportUtil.importResultSet(srs, mdbDatabase, table.getName(), filter, true);
    }

    mdbDatabase.close();

    log.info("Export of database successfully finished.");
  }

}
