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

public class ItemViewColumn {

  // Fields
  private int id;

  private int viewId; // the view this column belongs to

  private int modelIndex = -1; // not set

  private int viewIndex = -1; // hidden

  private int width = -1; // not set

  private String filter = "";

  // Constructors
  /** default constructor */
  private ItemViewColumn() {
  }

  public ItemViewColumn(final int viewId, final int modelIndex) {
    this.viewId = viewId;
    this.modelIndex = modelIndex;
  }

  // Property accessors
  private int getId() {
    return id;
  }

  private void setId(final int id) {
    this.id = id;
  }

  protected void setViewId(final int viewId) {
    this.viewId = viewId;
  }

  private int getViewId() {
    return viewId;
  }

  public int getModelIndex() {
    return modelIndex;
  }

  public void setModelIndex(final int modelIndex) {
    this.modelIndex = modelIndex;
  }

  public int getViewIndex() {
    return viewIndex;
  }

  public void setViewIndex(final int viewIndex) {
    this.viewIndex = viewIndex;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(final int width) {
    this.width = width;
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(final String filter) {
    this.filter = filter;
  }
}
