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

package jgamebase.db.model;

import java.util.ArrayList;
import java.util.List;

public class Selection {

  private String name;

  private String value;

  // Constructors
  /** default constructor */
  public Selection() {
  }

  public Selection(final String name, final String value) {
    this.name = name;
    this.value = value;
  }

  public Selection(final String name, final int value) {
    this(name, value + "");
  }

  public static List<Selection> createSelections(final String[] strings) {
    final List<Selection> selections = new ArrayList<Selection>();

    for (int i = 0; i < strings.length; i++) {
      final String string = strings[i];
      if (!string.isEmpty()) {
        selections.add(new Selection(string, i));
      }
    }
    return selections;
  }

  public static List<Selection> createSelections(final int start, final int stop) {
    final List<Selection> selections = new ArrayList<Selection>();

    for (int i = start; i <= stop; i++) {
      selections.add(new Selection(i + "", i + ""));
    }
    return selections;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(final String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return name;
  }
}
