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

import java.util.StringTokenizer;

public class SortWrapper_LastPlayed extends SortWrapper {

  public SortWrapper_LastPlayed(final String s) {
    super(s);
  }

  @Override
  public int compareTo(final Object o) {
    if (o instanceof SortWrapper_LastPlayed) {
      // To compare dates like "01.01.2001" or "31.12.2010" they are cut at the
      // dots and
      // reassembled in reverse order: "20010101" "20101231"
      // These are sorted as Strings.
      final String s1 = toDate(s);
      final String s2 = toDate(((SortWrapper_LastPlayed) o).s);

      return s1.compareTo(s2);
    }
    return 0;
  }

  private static String toDate(final String s) {
    if ((s == null)) {
      return "";
    }

    final String t = s.trim();
    final StringTokenizer st = new StringTokenizer(t, ".");

    String u = "";
    while (st.hasMoreTokens()) {
      u = st.nextToken() + u;
    }

    return u;
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
