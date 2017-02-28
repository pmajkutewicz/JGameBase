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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jgamebase.Const;
import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public final class Update {

  private static boolean isError = false;
  private static double currentVersion = 0.0;

  public static boolean updateFrom(final File exportDir, final double startVersion) {
    isError = false;
    currentVersion = startVersion;
    List<Table> tables = Table.getValuesAsList(startVersion);

    tables = readTables(exportDir, tables);

    while ((currentVersion < Db.NEEDED_VERSION) && !isError) {
      tables = updateTablesFrom(tables, currentVersion);
    }

    if (!isError) {
      writeTables(exportDir, tables);
    }

    // did an error occur?
    return isError;
  }

  private static List<Table> updateTablesFrom(final List<Table> tables, final double startVersion) {
    if (startVersion == 2.5) {
      return update2_5To2_6(tables);
    }

    if (startVersion == 2.6) {
      return update2_6To2_8(tables);
    }

    isError = true;
    return tables;
  }

  private static List<Table> update2_5To2_6(final List<Table> tables) {
    final int targetMajor = 2;
    final int targetMinor = 6;
    List<String[]> data;
    log.info("Updating from 2.5 to " + targetMajor + "." + targetMinor + "...");

    for (final Table table : tables) {
      switch (table) {
        case CONFIG:
          table.setData(transformColumns(table.getData(), String.valueOf(targetMajor),
              String.valueOf(targetMinor), 2, 3, 4));
          break;
        case GAMES:
          table.setData(transformColumns(table.getData(), 0, 1, 2, 4, 5, 6, 7, 8, 9, 10, 11, 12,
              13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 29, 31, 32, 33, 34, 35, 37, 38,
              39, 40, 41, 42, 43, 44, 45, 46, 47, 3, 36, 26, 27, 28, 30, "1", "1", "1", "1", "",
              "", "", "", "2", "2", "2", "0"));
          break;
        case GENRES:
          table.setData(transformColumns(table.getData(), 0, 2, 1));
          break;
        case MUSICIANS:
          table.setData(transformColumns(table.getData(), 0, 4, 1, 2, 3));
          break;
        case VIEWDATA:
          table.setData(transformColumns(table.getData(), 0, 1, 2, 3, 4, 5, 8, 9, 10, ""));
          break;
        case VIEWFILTERS:
          table.setData(transformColumns(table.getData(), 0, "+", 2, 3, 4, 5, 6, 7, 8));
          break;
      }
    }

    // Artists
    tables.add(Table.ARTISTS);
    data = new ArrayList<String[]>();
    data.add(new String[] { "1", "(Unknown)" });
    data.add(new String[] { "2", "(None)" });
    Table.ARTISTS.setData(data);

    // Developers
    tables.add(Table.DEVELOPERS);
    data = new ArrayList<String[]>();
    data.add(new String[] { "1", "(Unknown)" });
    data.add(new String[] { "2", "(None)" });
    Table.DEVELOPERS.setData(data);

    // Licenses
    tables.add(Table.LICENSES);
    data = new ArrayList<String[]>();
    data.add(new String[] { "1", "(Unknown)" });
    data.add(new String[] { "2", "Charityware" });
    data.add(new String[] { "3", "Commercial" });
    data.add(new String[] { "4", "Coverdisk" });
    data.add(new String[] { "5", "Game & Mini Mag" });
    data.add(new String[] { "6", "Giftware" });
    data.add(new String[] { "7", "Hidden Sub Game" });
    data.add(new String[] { "8", "Interpreted" });
    data.add(new String[] { "9", "Licenseware" });
    data.add(new String[] { "10", "Loading Sub Game" });
    data.add(new String[] { "11", "Open Source Conversion" });
    data.add(new String[] { "12", "PD/Freeware" });
    data.add(new String[] { "13", "Promotional" });
    data.add(new String[] { "14", "Reverse Engineered Modification" });
    data.add(new String[] { "15", "ShareWare" });
    Table.LICENSES.setData(data);

    // Rarities
    tables.add(Table.RARITIES);
    data = new ArrayList<String[]>();
    data.add(new String[] { "1", "(Unknown)" });
    data.add(new String[] { "2", "jgamebase.plugins.extractor.sevenzip.Common as mud" });
    data.add(new String[] { "3", "One version common, other versions rare" });
    data.add(new String[] { "4", "Rare" });
    data.add(new String[] { "5", "Extremely Rare" });
    data.add(new String[] { "6", "Finished/reviewed but not found/released" });
    Table.RARITIES.setData(data);

    currentVersion = Double.valueOf(targetMajor + "." + targetMinor).doubleValue();
    log.info("Update to " + currentVersion + " successfull.\n");
    return tables;
  }

  private static List<Table> update2_6To2_8(final List<Table> tables) {
    final int targetMajor = 2;
    final int targetMinor = 8;
    log.info("Updating from 2.5 to " + targetMajor + "." + targetMinor + "...");

    for (final Table table : tables) {
      switch (table) {
        case CONFIG:
          table.setData(transformColumns(table.getData(), "2", "8", 2, 3, 4, "", ""));
          break;
        case EXTRAS:
          table.setData(transformColumns(table.getData(), 0, 1, 2, 3, 4, 5, 6, 7, ""));
          break;
        case GAMES:
          table.setData(transformColumns(table.getData(), 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
              13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33,
              34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54,
              55, 56, 57, 58, 59, "0"));
          break;
        case VIEWDATA:
          table.setData(transformColumns(table.getData(), 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, "0"));
          break;
        case VIEWFILTERS:
          table.setData(transformColumns(table.getData(), 0, "+", 2, 3, 4, 5, 6, 7, 8));
          break;
      }
    }

    currentVersion = Double.valueOf(targetMajor + "." + targetMinor).doubleValue();
    log.info("Update to " + currentVersion + " successfull.\n");
    return tables;
  }

  private static List<Table> readTables(final File dir, final List<Table> tables) {
    File file;

    log.info("Starting loading of files for database...");

    for (final Table table : tables) {
      file = new File(dir, table.getName() + ".csv");
      try {
        log.info("  Loading data from file '" + table.getName() + ".csv'.");

        final CSVReader reader = new CSVReader(new FileReader(file), CSVParser.DEFAULT_SEPARATOR,
            CSVParser.DEFAULT_QUOTE_CHARACTER, Const.CSV_ESCAPE_CHAR);
        final List<String[]> data = reader.readAll();
        table.setData(data);

        log.info("  File '" + table.getName() + ".csv': " + table.getData().size()
            + " rows loaded.\n");
      } catch (final IOException e) {
        e.printStackTrace();
      }
    }

    log.info("Loading of database files successfully finished.\n");

    return tables;
  }

  private static void writeTables(final File dir, final List<Table> tables) {
    File file;

    log.info("Writing of upgraded files for database...");

    for (final Table table : tables) {
      file = new File(dir, table.getName() + ".csv");
      try {
        log.info("  Writing data to file '" + table.getName() + ".csv'.");

        final CSVWriter writer = new CSVWriter(new FileWriter(file));
        writer.writeAll(table.getData());
        writer.close();

        log.info("  File '" + table.getName() + ".csv': " + table.getData().size()
            + " rows written.\n");
      } catch (final IOException e) {
        e.printStackTrace();
      }
    }

    log.info("Writing of database files successfully finished.");
  }

  public static List<String[]> transformColumns(final List<String[]> data,
      final Object... columnIndices) {
    if (data.size() == 0) {
      return data;
    }

    // iterate over each row
    for (int i = 0; i < data.size(); i++) {
      // convert current row
      final String[] row = data.get(i);
      final String[] newRow = new String[columnIndices.length];

      // iterate over each new column
      for (int j = 0; j < columnIndices.length; j++) {
        // if integer, use as column index
        if (columnIndices[j] instanceof Integer) {
          final int column = ((Integer) columnIndices[j]).intValue();
          if (column >= 0) {
            // if positive, copy column
            newRow[j] = row[column];
          } else {
            // if negative, fill column with row number
            newRow[j] = String.valueOf(i);
          }
        } else {
          final String value = columnIndices[j].toString();
          if (value.equals("+")) {
            // fill with original value or "0" if original value is ""
            // helps with integer values ",,"
            newRow[j] = (row[j].toString().isEmpty()) ? "0" : row[j].toString();
          } else {
            // fill with given value
            newRow[j] = value;
          }
        }

        // store converted row in data
        data.set(i, newRow);
      }
    }

    return data;
  }

}
