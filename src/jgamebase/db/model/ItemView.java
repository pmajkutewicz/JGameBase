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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.SortOrder;

import jgamebase.db.Db;
import jgamebase.gui.ColumnHeader;

public class ItemView implements Cloneable, Comparable<ItemView> {

  private static final long serialVersionUID = 5846102476547722542L;

  // mode
  public static final int MODE_AND = 0;

  public static final int MODE_OR = 1;

  // included tables
  public static final int INCLUDE_GAMES = 0;

  public static final int INCLUDE_MUSIC = 1;

  public static final int INCLUDE_BOTH = 2;

  public static final int INCLUDE_NOTHING = 3;

  // type
  public static final int TYPE_NORMAL = 0;

  public static final int TYPE_SYSTEM = 1;

  public static final int TYPE_QUICK = 2;

  // Fields
  private int id;

  private int mode;

  private String name;

  private int filterCount;

  private int include;

  private int sortColumn;

  private int _sortOrder;

  private String selectedItem;

  private String extraColumns;

  private int ordinal;

  private Set<ItemViewFilter> filters = new HashSet<ItemViewFilter>();

  private int type;

  protected List<Item> data = null;

  private Set<ItemViewColumn> columns = new HashSet<ItemViewColumn>();

  // Constructors
  public ItemView() {
    setColumnVisibility(Arrays
        .asList(ColumnHeader.NAME.getName(), ColumnHeader.YEAR.getName(),
          ColumnHeader.PUBLISHER.getName(), ColumnHeader.GENRE.getName()));
  }

  private ItemViewColumn[] getColumnsAsArray() {
    final ItemViewColumn[] columnsArray = new ItemViewColumn[ColumnHeader.size()];

    // copy all columns to their places in the array
    for (final ItemViewColumn column : columns) {
      columnsArray[column.getModelIndex()] = column;
    }

    // fill empty places in the array
    for (int i = 0; i < columnsArray.length; i++) {
      if (columnsArray[i] == null) {
        columnsArray[i] = new ItemViewColumn(getId(), i);
      }
    }

    return columnsArray;
  }

  // Property accessors
  public void setId(final int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public void setMode(final int mode) {
    // if mode changes set cached data to null
    if (this.mode != mode) {
      clearCache();
    }
    this.mode = mode;
  }

  public int getMode() {
    return mode;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getName() {
    return name == null ? "" : name;
  }

  private void setFilterCount(final int filterCount) {
    this.filterCount = filterCount;
  }

  public int getFilterCount() {
    return filterCount;
  }

  public void setInclude(final int include) {
    // if includes changes set cached data to null
    if (this.include != include) {
      clearCache();
    }
    this.include = include;
  }

  public int getInclude() {
    return include;
  }

  // needed for hibernate
  @SuppressWarnings("unused")
  private void setListViewType(final int listViewType) {
    // we only support view type details
  }

  // needed for hibernate
  @SuppressWarnings("unused")
  private int getListViewType() {
    return 3; // we only support view type "Details"
  }

  public void setSortColumn(final int sortColumn) {
    this.sortColumn = sortColumn;
  }

  public int getSortColumn() {
    return sortColumn;
  }

  public void setSortOrder(final SortOrder sortOrder) {
    set_sortOrder(sortOrder.ordinal());
  }

  private void set_sortOrder(final int _sortOrder) {
    this._sortOrder = _sortOrder;
  }

  public SortOrder getSortOrder() {
    return (SortOrder.values())[get_sortOrder()];
  }

  private int get_sortOrder() {
    return _sortOrder;
  }

  public void setExtraColumns(final String extraColumns) {
    this.extraColumns = extraColumns;
  }

  public String getExtraColumns() {
    return extraColumns == null ? "" : extraColumns;
  }

  private void setOrdinal(final int ordinal) {
    this.ordinal = ordinal;
  }

  private int getOrdinal() {
    return ordinal;
  }

  public Set<ItemViewFilter> getFilters() {
    return filters;
  }

  public void setFilters(final Set<ItemViewFilter> filters) {
    if (filters != null) {
      this.filters = filters;
      setFilterCount(filters.size());
      for (final ItemViewFilter filter : filters) {
        filter.setViewId(getId());
      }
    } else {
      this.filters = new HashSet<ItemViewFilter>();
      setFilterCount(0);
    }
    // filters changed, clear cached data
    clearCache();
  }

  public void addFilter(final ItemViewFilter filter) {
    filter.setViewId(getId());
    final Set<ItemViewFilter> filters = getFilters();
    filters.add(filter);
    setFilters(filters);
  }

  private Set<ItemViewColumn> getColumns() {
    return columns;
  }

  public void setColumns(final Set<ItemViewColumn> columns) {
    this.columns = columns;
    for (final ItemViewColumn column : columns) {
      column.setViewId(getId());
    }
  }

  public int getType() {
    return type;
  }

  public void setType(final int type) {
    this.type = type;
  }

  public void setColumnVisibility(final List<String> columnNames) {
    final ItemViewColumn[] columnsArray = getColumnsAsArray();

    // hide all columns
    for (final ItemViewColumn element : columnsArray) {
      element.setViewIndex(-1);
    }

    // set viewIndex of columns
    int viewIndex = 0;
    for (final String name : columnNames) {
      final int modelIndex = getModelIndex(name);
      final ItemViewColumn column = columnsArray[modelIndex];
      column.setViewIndex(viewIndex);
      viewIndex++;
    }

    columns = new HashSet<ItemViewColumn>(Arrays.asList(columnsArray));
  }

  private int getModelIndex(final String name) {
    int modelIndex = -1;

    for (int i = 0; i < ColumnHeader.size(); i++) {
      if (name.equals(ColumnHeader.nameAt(i))) {
        modelIndex = i;
      }
    }
    return modelIndex;
  }

  public List<String> getColumnVisibility() {
    final List<String> columnNames = new ArrayList<String>();
    final ItemViewColumn[] columnsArray = getColumnsAsArray();

    for (int viewIndex = 0; viewIndex < columnsArray.length; viewIndex++) {
      // find column with this viewIndex
      for (int i = 0; i < columnsArray.length; i++) {
        final ItemViewColumn column = columnsArray[i];
        if (column.getViewIndex() == viewIndex) {
          columnNames.add(ColumnHeader.nameAt(i));
        }
      }
    }

    return columnNames;
  }

  public List<Integer> getColumnWidth() {
    final List<Integer> columnWidth = new ArrayList<Integer>();
    final ItemViewColumn[] columnsArray = getColumnsAsArray();

    for (int viewIndex = 0; viewIndex < columnsArray.length; viewIndex++) {
      // find column with this viewIndex
      for (final ItemViewColumn column : columnsArray) {
        if (column.getViewIndex() == viewIndex) {
          columnWidth.add(column.getWidth());
        }
      }
    }

    return columnWidth;
  }

  public void setColumnWidth(final List<Integer> columnWidth) {
    final ItemViewColumn[] columnsArray = getColumnsAsArray();

    // set viewIndex of columns
    int viewIndex = 0;
    for (final String name : getColumnVisibility()) {
      final int modelIndex = getModelIndex(name);
      final ItemViewColumn column = columnsArray[modelIndex];
      column.setWidth(columnWidth.get(viewIndex));
      viewIndex++;
    }

    columns = new HashSet<ItemViewColumn>(Arrays.asList(columnsArray));
  }

  @Override
  public String toString() {
    String name = getName();
    if (data != null) {
      name += " (" + data.size() + ")";
    }
    return name;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public synchronized boolean isLoaded() {
    return (data != null);
  }

  public synchronized List<Item> getData() {
    if (!isLoaded()) {
      data = Db.getItems(this);
    }
    return data;
  }

  public synchronized void clearCache() {
    data = null;
  }

  public String getSelectedItem() {
    return (selectedItem == null) ? "" : selectedItem;
  }

  public void setSelectedItem(final String selectedItem) {
    this.selectedItem = (selectedItem == null) ? "" : selectedItem;
  }

  @Override
  public int compareTo(final ItemView other) {
    return getName().compareTo(other.getName());
  }
}
