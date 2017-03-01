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

import jgamebase.tools.StringTools;

public class SortWrapper_Highscore extends SortWrapper {

  public SortWrapper_Highscore(final String s) {
    super(s);
  }

  @Override
  public int compareTo(final Object o) {
    if (o instanceof SortWrapper_Highscore) {
      // To compare highscores like "12345" or "12" are filled with zeroes to
      // have the same length: "12345", "00012"
      // These are sorted as Strings.
      String s1 = s;
      String s2 = ((SortWrapper_Highscore) o).s;

      s1 = StringTools.padZeroBefore(s1, s2.length());
      s2 = StringTools.padZeroBefore(s2, s1.length());

      return s1.compareTo(s2);
    }
    return 0;
  }

  @Override
  public boolean equals(final Object other) {
    return super.equals(other);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }
}
