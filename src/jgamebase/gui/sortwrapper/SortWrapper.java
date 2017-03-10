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

package jgamebase.gui.sortwrapper;

public abstract class SortWrapper implements Comparable {

  protected String s = "";

  protected SortWrapper(final String s) {
    this.s = s;
  }

  @Override
  public String toString() {
    return s;
  }

  @Override
  public abstract int compareTo(Object o);

  @Override
  public boolean equals(final Object other) {
    if (other == null) {
      return false;
    }
    return s.equals(other.toString());
  }

  @Override
  public int hashCode() {
    return s.hashCode();
  }
}
