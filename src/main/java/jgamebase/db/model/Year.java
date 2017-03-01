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

import jgamebase.Const;

public class Year {

  private static final long serialVersionUID = -4072960964254844603L;

  public static final int NEUTRAL_ID = 1;

  // Fields
  private int id;

  private int nameId;

  // Constructors
  /** default constructor */
  public Year() {
  }

  /** constructor with id */
  public Year(final int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public void setId(final int id) {
    this.id = id;
  }

  public int getNameId() {
    return nameId;
  }

  public void setNameId(final int name) {
    nameId = name;
  }

  public String getName() {
    String name = "";

    if ((getNameId() >= 9991) && (getNameId() <= 9999)) {
      name = Const.FORDISPLAY_YEAR[getNameId() - 9990];
    } else if ((getNameId() >= Const.YEAR_EARLIEST) && (getNameId() <= Const.YEAR_LATEST)) {
      name = Const.FORDISPLAY_YEAR[(getNameId() - Const.YEAR_EARLIEST) + 10];
    }

    return name;
  }
}