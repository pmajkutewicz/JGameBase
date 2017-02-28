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

public class SortWrapper_Length extends SortWrapper {

  public SortWrapper_Length(final String s) {
    super(s);
  }

  @Override
  public int compareTo(final Object o) {
    if (o instanceof SortWrapper_Length) {
      // To compare length like "123 Blocks(s)" or "1 Disk(s)" the length is cut
      // in half at the first space.
      // The first parts are filled with zeroes to have the same length: "123",
      // "001"
      // The second parts are cut leaving only the first letter: "B", "D"
      // Now the parts are concatenated in reversed order: "B123", "D001"
      // These concatenated parts are sorted as Strings.
      String s1 = s;
      String s2 = ((SortWrapper_Length) o).s;

      final String beforeSpace1 = StringTools.beforeSpace(s1);
      final String beforeSpace2 = StringTools.beforeSpace(s2);

      s1 = StringTools.firstCharAsString(StringTools.afterSpace(s1))
          + StringTools.padZeroBefore(beforeSpace1, beforeSpace2.length());
      s2 = StringTools.firstCharAsString(StringTools.afterSpace(s2))
          + StringTools.padZeroBefore(beforeSpace2, beforeSpace1.length());

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
