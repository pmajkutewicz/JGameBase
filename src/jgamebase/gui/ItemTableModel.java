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

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.RowSorter.SortKey;
import javax.swing.table.AbstractTableModel;

import jgamebase.JGameBase;
import jgamebase.db.Db;
import jgamebase.db.model.Game;
import jgamebase.db.model.Item;
import jgamebase.db.model.ItemView;
import jgamebase.db.model.Music;
import jgamebase.gui.sortwrapper.SortWrapper_Highscore;
import jgamebase.gui.sortwrapper.SortWrapper_LastPlayed;
import jgamebase.gui.sortwrapper.SortWrapper_Length;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.TableColumnExt;

public class ItemTableModel extends AbstractTableModel {

  private static final long serialVersionUID = -8744154898299054717L;
  private static Game dummyGame = new Game();

  private ItemView currentView;

  public ItemTableModel(final ItemView view) {
    setView(view);
  }

  public synchronized void saveOrUpdateView() {
    Db.saveOrUpdateAll(currentView.getData());
  }

  public synchronized void setView(final ItemView newView) {
    if ((!JGameBase.isGuiInitialized()) || (newView == null) || (newView.getName() == null)
        || (newView.getName().isEmpty()) || (newView.equals(currentView))) {
      return; // nothing to do...
    }

    // now the GUI is initialized

    // set busy cursor
    JGameBase.getGui().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    // don't load info via plugin from music file
    Music.setAllowReadingInfoFromFile(false);

    saveGuiParametersToView(currentView);
    currentView = newView;

    // update table
    fireTableDataChanged();

    updateEditRemoveViewEnabled();

    loadGuiParametersFromView(newView);

    // enable loading info via plugin from music file
    Music.setAllowReadingInfoFromFile(true);

    // set default cursor
    JGameBase.getGui().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
  }

  protected synchronized void saveGuiParametersToView(final ItemView view) {

    if ((!JGameBase.isGuiInitialized()) || (view == null) || (view.getName() == null)
        || (view.getName().isEmpty())) {
      return; // Gui not properly initialized
    }

    // save visible columns in currentView
    view.setColumnVisibility(JGameBase.getGui().getColumnVisibility());

    // save column widths in currentView
    view.setColumnWidth(JGameBase.getGui().getColumnWidth());

    // save selected item
    final Item item = JGameBase.getGui().getSelectedItem();
    if (item != null) {
      view.setSelectedItem(item.getStringId());
    } else {
      view.setSelectedItem(""); // default to first item
    }

    // save which row is sorted in what order
    final List<? extends SortKey> sortKeys = JGameBase.getGui().itemTable.getRowSorter()
        .getSortKeys();
    if (sortKeys.size() > 0) {
      final SortKey sortKey = sortKeys.get(0);
      view.setSortColumn(sortKey.getColumn());
      view.setSortOrder(sortKey.getSortOrder());
    }
  }

  private synchronized void loadGuiParametersFromView(final ItemView view) {
    if ((!JGameBase.isGuiInitialized()) || (view == null) || (view.getName() == null)
        || (view.getName().isEmpty())) {
      return; // Gui not initialized
    }

    // set visible columns
    setColumnVisibility(view.getColumnVisibility());

    // set column widths in currentView in db
    setColumnWidth(view.getColumnWidth());

    // restore selected item
    JGameBase.getGui().selectItem(view.getSelectedItem(), false);

    // set which row is sorted in what order
    final List<SortKey> sortKeys = new ArrayList<SortKey>();
    sortKeys.add(new SortKey(view.getSortColumn(), view.getSortOrder()));
    JGameBase.getGui().itemTable.getRowSorter().setSortKeys(sortKeys);
  }

  public void setColumnVisibility(List<String> columnsToShow) {
    final JXTable itemTable = JGameBase.getGui().itemTable;

    // if no columns, set default
    if (columnsToShow.size() == 0) {
      columnsToShow = new ArrayList<String>(Arrays.asList(new String[] {
          ColumnHeader.NAME.getName(), ColumnHeader.YEAR.getName(),
          ColumnHeader.PUBLISHER.getName() }));
    }

    // hide all columns
    for (int i = 0; i < ColumnHeader.size(); i++) {
      final TableColumnExt column = itemTable.getColumnExt(ColumnHeader.nameAt(i));
      column.setVisible(false);
    }

    // show columns
    for (final String header : columnsToShow) {
      final TableColumnExt column = itemTable.getColumnExt(header);
      column.setVisible(true);
    }

    // order columns
    itemTable.setColumnSequence(columnsToShow.toArray());

    // set horizontal scrollbar if more than 5 columns
    itemTable.setHorizontalScrollEnabled(columnsToShow.size() > 5);
  }

  public void setColumnWidth(final List<Integer> columnsToShow) {
    final JXTable itemTable = JGameBase.getGui().itemTable;

    // handle columns
    for (int i = 0; i < columnsToShow.size(); i++) {
      final TableColumnExt column = itemTable.getColumnExt(i);
      final int width = columnsToShow.get(i);

      if (width != -1) {
        // set width that currentView column specifies
        column.setWidth(width);
      } else {
        // currentView column has no stored width, let table decide
        itemTable.packColumn(i, 0);
      }

    }
  }

  public synchronized void reloadView() {
    currentView.clearCache(); // clear currentView cache

    if (JGameBase.isGuiInitialized()) {
      JGameBase.getGui().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      updateEditRemoveViewEnabled();
    }

    // update table
    fireTableDataChanged();

    // setSelectedRow(0);

    if (JGameBase.isGuiInitialized()) {
      JGameBase.getGui().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
  }

  public void updateEditRemoveViewEnabled() {
    final boolean b = (currentView.getType() == ItemView.TYPE_NORMAL);

    JGameBase.getGui().viewPopupMenu_EditView.setEnabled(b);
    JGameBase.getGui().viewPopupMenu_RemoveView.setEnabled(b);
    JGameBase.getGui().viewMenu_EditView.setEnabled(b);
    JGameBase.getGui().viewMenu_RemoveView.setEnabled(b);
  }

  public void setSelectedRow(final int row) {
    if (JGameBase.isGuiInitialized() && (row >= 0) && (row < getRowCount())) {
      try {
        final JXTable itemTable = JGameBase.getGui().itemTable;
        final int viewRow = itemTable.convertRowIndexToView(row);
        itemTable.setRowSelectionInterval(viewRow, viewRow);
        // scroll so that the selected row is visible
        itemTable.scrollRectToVisible(itemTable.getCellRect(viewRow, 0, false));
      } catch (final Exception e) {
      }
    }
  }

  public int getSelectedRow() {
    if (!JGameBase.isGuiInitialized()) {
      return 0;
    }

    final int selectedRow = JGameBase.getGui().itemTable.getSelectedRow();

    if ((selectedRow >= 0) && (selectedRow < JGameBase.getGui().itemTable.getRowCount())) {
      return JGameBase.getGui().itemTable.convertRowIndexToModel(selectedRow);
    }

    return 0;
  }

  @Override
  public int getColumnCount() {
    return ColumnHeader.size();
  }

  @Override
  public int getRowCount() {
    if (currentView != null) {
      return currentView.getData().size();
    }
    return 0;
  }

  @Override
  public String getColumnName(final int col) {
    return ColumnHeader.nameAt(col);
  }

  @Override
  public Object getValueAt(final int row, final int col) {
    final ColumnHeader header = ColumnHeader.valueAt(col);
    final Item item = getItem(row);

    if (header == ColumnHeader.NAME) {
      return item; // icon with name
    }
    if (item instanceof Game) {
      return getValues((Game) item, col);
    }
    if (item instanceof Music) {
      return getValues((Music) item, col);
    }

    return "";
  }

  private Object getValues(final Game game, final int col) {
    final ColumnHeader header = ColumnHeader.valueAt(col);

    try {
      switch (header) {
        case PUBLISHER:
          return game.getPublisher().getName();
        case YEAR:
          return game.getYear().getName();
        case PROGRAMMER:
          return game.getProgrammer().getName();
        case MUSICIAN:
          return game.getMusician().getName();
        case GENRE:
          return game.getGenreForDisplay();
        case PLAYERS:
          return game.getPlayersForDisplay();
        case CONTROL:
          return game.getControlForDisplay();
        case LANGUAGE:
          return game.getLanguage().getName();
        case COMMENT:
          return game.getComment();
        case FILENAME:
          return game.getFilename();
        case CRACKER:
          return game.getCracker().getName();
        case TRAINER:
          return game.getTrainerForDisplay();
        case LENGTH:
          return new SortWrapper_Length(game.getLengthForDisplay());
        case TRUE_DRIVE:
          return game.getNeedsTruedriveEmu();
        case SAVES_HIGHSCORE:
          return game.getHasHighscoreSaver();
        case PAL_NTSC:
          return game.getPalNtscForDisplay();
        case INCLUDES_DOCS:
          return game.getHasIncludedDocs();
        case LOADINGSCREEN:
          return game.getHasLoadingScreen();
        case VERSION_COMMENT:
          return game.getVersionComment();
        case MUSIC_FILENAME:
          return game.getMusicFilename();
        case HIGHSCORE:
          return new SortWrapper_Highscore(game.getHighscoreForDisplay());
        case DIFFICULTY:
          return game.getDifficulty().getName();
        case TIMES_PLAYED:
          return game.getTimesPlayedForDisplay();
        case LAST_PLAYED:
          return new SortWrapper_LastPlayed(game.getDateLastPlayedForDisplay());
        case RATING:
          return game.getRatingForDisplay();
      }
    } catch (final Exception ignore) {
    }

    return "";
  }

  private Object getValues(final Music music, final int col) {
    final ColumnHeader header = ColumnHeader.valueAt(col);

    switch (header) {
      case PUBLISHER:
        return music.getPublisherForDisplay();
      case YEAR:
        return music.getYearForDisplay();
      case MUSICIAN:
        return music.getMusicianForDisplay();
      case MUSIC_FILENAME:
        return music.getFilename();

        // Booleans
      case INCLUDES_DOCS:
      case LOADINGSCREEN:
      case SAVES_HIGHSCORE:
      case TRUE_DRIVE:
        return false;

        // sort wrapper
      case LENGTH:
        return new SortWrapper_Length("");
      case HIGHSCORE:
        return new SortWrapper_Highscore("");
      case LAST_PLAYED:
        return new SortWrapper_LastPlayed("");
    }

    return "";
  }

  Item getItem(final int row) {
    Item item = new Game(); // workaround because Item is an interface
    try {
      item = currentView.getData().get(row);
    } catch (final Exception e) {
      // if there was an exception (e.g. ArrayIndexOutOfBounds) we simply
      // return the empty Item object
    }
    return item;
  }

  @Override
  public Class<? extends Object> getColumnClass(final int col) {
    final ColumnHeader header = ColumnHeader.valueAt(col);
    Object value;

    switch (header) {
      case NAME:
        value = getValueAt(0, col);
        break;

      case LENGTH:
        value = new SortWrapper_Length("");
        break;

      case HIGHSCORE:
        value = new SortWrapper_Highscore("");
        break;

      case LAST_PLAYED:
        value = new SortWrapper_LastPlayed("");
        break;

      default:
        value = getValues(dummyGame, col);
    }

    if (value == null) {
      // this is an ugly hack ;-(
      // but when you return "null", you get a NullPointerException
      value = "";
    }

    return value.getClass();
  }

  public int getIndexOf(final Object o) {
    return currentView.getData().indexOf(o);
  }

  public Iterator<Item> iterator() {
    return currentView.getData().iterator();
  }

}
