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

public enum ColumnHeader {

  NAME("Name"), PUBLISHER("Publisher"), YEAR("Year"), PROGRAMMER("Programmer"), MUSICIAN("Musician"), GENRE(
      "Genre"), PLAYERS("# Players"), CONTROL("Control"), LANGUAGE("Language"), COMMENT("Comment"), FILENAME(
      "Filename"), CRACKER("Cracker"), TRAINER("# Trainers"), LENGTH("Length"), TRUE_DRIVE(
      "True Drive"), SAVES_HIGHSCORE("Saves Highscore"), PAL_NTSC("PAL/NTSC"), INCLUDES_DOCS("Docs"), LOADINGSCREEN(
      "Loading Screen"), VERSION_COMMENT("Version Comment"), MUSIC_FILENAME("Music Filename"), HIGHSCORE(
      "Highscore"), DIFFICULTY("Difficulty"), TIMES_PLAYED("Times Played"), LAST_PLAYED(
      "Last Played"), RATING("Rating");

  private final String name;

  public static List<String> getNames() {
    final List<String> names = new ArrayList<String>();

    for (final ColumnHeader header : values()) {
      names.add(header.getName());
    }

    return names;
  }

  public static int size() {
    return values().length;
  }

  public static ColumnHeader valueAt(final int index) {
    return values()[index];
  }

  public static String nameAt(final int index) {
    return values()[index].getName();
  }

  public static int indexOf(final ColumnHeader header) {
    return header.ordinal();
  }

  public static int indexOf(final String name) {
    int index = 0;

    for (final ColumnHeader header : values()) {
      if (name.equals(header.getName())) {
        return index;
      }
      index++;
    }

    return -1; // not found
  }

  ColumnHeader(final String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }

}
