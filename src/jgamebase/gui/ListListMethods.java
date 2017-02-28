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
import java.util.Collections;
import java.util.List;

import javax.swing.JList;

public class ListListMethods {

  public static List moveSelectedDown(final JList list) {
    final ListListModel model = ((ListListModel) list.getModel());
    if (!model.isEmpty()) {
      final int selectedIndex = list.getSelectedIndex();
      if (selectedIndex < (model.getSize() - 1)) {
        final List<Object> data = model.get();
        final Object temp = data.get(selectedIndex + 1);
        data.set(selectedIndex + 1, data.get(selectedIndex));
        data.set(selectedIndex, temp);
        model.set(data);
        list.setSelectedIndex(selectedIndex + 1);
        list.setSelectedValue(list.getSelectedValue(), true);
      }
    }
    return model.get();
  }

  public static List moveSelectedUp(final JList list) {
    final ListListModel model = ((ListListModel) list.getModel());
    if (!model.isEmpty()) {
      final int selectedIndex = list.getSelectedIndex();
      if (selectedIndex > 0) {
        final List<Object> data = model.get();
        final Object temp = data.get(selectedIndex - 1);
        data.set(selectedIndex - 1, data.get(selectedIndex));
        data.set(selectedIndex, temp);
        model.set(data);
        list.setSelectedIndex(selectedIndex - 1);
        list.setSelectedValue(list.getSelectedValue(), true);
      }
    }
    return model.get();
  }

  public static List removeSelected(final JList list) {
    final ListListModel model = ((ListListModel) list.getModel());
    if (!model.isEmpty()) {
      final int row = list.getSelectedIndex();

      final int selectedIndex = list.getSelectedIndex();
      model.remove(selectedIndex);

      // select
      if (!model.isEmpty()) {
        if ((row >= 0) && (row < list.getModel().getSize())) {
          list.setSelectedIndex(row);
        } else {
          list.setSelectedIndex(list.getModel().getSize() - 1);
        }
      }
    }
    return model.get();
  }

  public static void moveSelected(final JList src, final JList dst) {
    final Object selected = src.getSelectedValue();
    if (selected != null) {
      ListListMethods.removeSelected(src);
      ((ListListModel) dst.getModel()).addElement(selected);
    }
  }

  public static void moveAll(final JList src, final JList dst) {
    final ListListModel srcModel = (ListListModel) src.getModel();
    final ListListModel dstModel = (ListListModel) dst.getModel();

    if (!srcModel.isEmpty()) {
      List srcData = srcModel.get();
      final List dstData = dstModel.get();
      dstData.addAll(srcData);
      srcData = new ArrayList();
      srcModel.set(srcData);
      dstModel.set(dstData);
    }
  }

  public static List sort(final JList list) {
    final ListListModel model = ((ListListModel) list.getModel());
    if (!model.isEmpty()) {
      final List data = model.get();
      Collections.sort(data);
      model.set(data);
    }
    return model.get();
  }
}
