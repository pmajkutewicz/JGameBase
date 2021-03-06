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

public class Genre {

  private static final long serialVersionUID = -6369455142436361042L;

  public static final int NEUTRAL_ID = 1;

  // Fields
  private int id;

  private String name;

  private ParentGenre parentGenre;

  // Constructors
  /** default constructor */
  public Genre() {
  }

  /** constructor with id */
  public Genre(final int id) {
    this.id = id;
  }

  // Property accessors
  /**
     * 
     */
  public int getId() {
    return id;
  }

  public void setId(final int id) {
    this.id = id;
  }

  /**
     * 
     */
  public String getName() {
    return name;
  }

  public void setName(final String genre) {
    name = genre;
  }

  /**
     * 
     */
  public ParentGenre getParentGenre() {
    return parentGenre;
  }

  public void setParentGenre(final ParentGenre parentGenre) {
    this.parentGenre = parentGenre;
  }
}