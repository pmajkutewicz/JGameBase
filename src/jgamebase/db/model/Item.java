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

import javax.swing.Icon;

public interface Item extends Comparable<Item> {

  /**
   * @return Returns the id.
   */
  public abstract int getId();

  /**
   * @return Returns the id as String.
   */
  public abstract String getStringId();

  /**
   * @return Returns the icon.
   */
  public abstract Icon getIcon();

  /**
   * @return Returns the name.
   */
  public abstract String getName();

  public abstract void setName(String name);

  public abstract String getFilename();

  public abstract Musician getMusician();

  public abstract boolean getIsFavourite();

  public abstract boolean getIsAdult();

  public abstract void setIsFavourite(boolean isFavourite);

  public abstract void play();

  @Override
  public int compareTo(Item other);

  public String createId();

}