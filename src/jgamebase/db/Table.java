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

import java.util.ArrayList;
import java.util.List;

public enum Table {

  CONFIG("Config", 2.5), CRACKERS("Crackers", 2.5), DIFFICULTY("Difficulty", 2.5), PGENRES(
      "PGenres", 2.5), GENRES("Genres", 2.5), LANGUAGES("Languages", 2.5), MUSICIANS("Musicians",
      2.5), PROGRAMMERS("Programmers", 2.5), PUBLISHERS("Publishers", 2.5), YEARS("Years", 2.5), MUSIC(
      "Music", 2.5), VIEWDATA("ViewData", 2.5), VIEWFILTERS("ViewFilters", 2.5), ARTISTS("Artists",
      2.6), DEVELOPERS("Developers", 2.6), LICENSES("Licenses", 2.6), RARITIES("Rarities", 2.6), GAMES(
      "Games", 2.5), EXTRAS("Extras", 2.5);

  private final String name;
  private final double sinceVersion;

  private List<String[]> data;

  public static Table[] getValues(final double version) {
    return getValuesAsList(version).toArray(new Table[0]);
  }

  public static List<Table> getValuesAsList() {
    final List<Table> tables = new ArrayList<Table>();

    for (final Table table : values()) {
      tables.add(table);
    }

    return tables;
  }

  public static List<Table> getValuesAsList(final double version) {
    final List<Table> tables = new ArrayList<Table>();

    for (int i = 0; i < values().length; i++) {
      if (valueAt(i).isInVersion(version)) {
        tables.add(valueAt(i));
      }
    }

    return tables;
  }

  public static List<String> getNames() {
    final List<String> names = new ArrayList<String>();

    for (final Table table : values()) {
      names.add(table.getName());
    }

    return names;
  }

  public static List<String> getNames(final double version) {
    final List<String> names = new ArrayList<String>();

    for (final Table table : values()) {
      if (table.isInVersion(version)) {
        names.add(table.getName());
      }
    }

    return names;
  }

  public static int size() {
    return values().length;
  }

  public static Table valueAt(final int index) {
    return values()[index];
  }

  public static Table getByName(final String name) {
    for (final Table table : values()) {
      if (table.getName().equalsIgnoreCase(name)) {
        return table;
      }
    }
    return null;
  }

  public static String nameAt(final int index) {
    return values()[index].getName();
  }

  public static int indexOf(final Table table) {
    return table.ordinal();
  }

  public static int indexOf(final String name) {
    int index = 0;

    for (final Table header : values()) {
      if (name.equals(header.getName())) {
        return index;
      }
      index++;
    }

    return -1; // not found
  }

  Table(final String name, final double sinceVersion) {
    this.name = name;
    this.sinceVersion = sinceVersion;
    data = new ArrayList<String[]>();
  }

  public String getName() {
    return name;
  }

  public double getSinceVersion() {
    return sinceVersion;
  }

  public boolean isInVersion(final double version) {
    return (getSinceVersion() <= version);
  }

  public List<String[]> getData() {
    return data;
  }

  public void setData(final List<String[]> data) {
    this.data = data;
  }

  @Override
  public String toString() {
    return name;
  }

}
