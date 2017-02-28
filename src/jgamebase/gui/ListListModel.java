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

import javax.swing.DefaultListModel;
import javax.swing.JList;

class ListListModel extends DefaultListModel {
  /**
	 * 
	 */
  private static final long serialVersionUID = 2615993846866039864L;

  public ListListModel() {
    super();
  }

  public ListListModel(final List<?> list) {
    super();
    add(list);
  }

  public void add(final List<?> list) {
    for (final Object element : list) {
      addElement(element);
    }
  }

  public void set(final List<?> list) {
    removeAllElements();
    add(list);
  }

  @Override
  public void clear() {
    removeAllElements();
  }

  public List get() {
    final List list = new ArrayList();
    for (int i = 0; i < getSize(); i++) {
      list.add(get(i));
    }
    return list;
  }

  public static List get(final JList list) {
    return ((ListListModel) list.getModel()).get();
  }
}