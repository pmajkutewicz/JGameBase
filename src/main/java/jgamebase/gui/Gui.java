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

import static jgamebase.Const.log;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SortOrder;
import javax.swing.UIManager;

import jgamebase.Const;
import jgamebase.JGameBase;
import jgamebase.db.Db;
import jgamebase.db.Export;
import jgamebase.db.Import;
import jgamebase.db.model.ItemView;
import jgamebase.db.model.ItemViewDuplicateUrls;
import jgamebase.db.model.ItemViewFilter;
import jgamebase.model.Emulators;
import jgamebase.model.Preferences;
import jgamebase.tools.FileTools;

/**
 * The graphical user interface (GUI).
 * 
 * @author F. Gerbig (fgerbig@users.sourceforge.net)
 */
public class Gui extends GuiMethods {

  /**
	 * 
	 */
  private static final long serialVersionUID = -2837268004930852853L;

  public static final int VIEW_ALL_GAMES = 0;

  public static final int VIEW_AVAILABLE_GAMES = 1;

  public static final int VIEW_ALL_MUSIC = 2;

  public static final int VIEW_AVAILABLE_MUSIC = 3;

  public static final int VIEW_FAVOURITES = 4;

  // variables for progress bar during access import
  private static ProgressMonitor progressMonitor;

  private static final int step_width = 100;

  private static final String CANCEL_MSG = "CANCEL";

  /**
   * Initializes a new GameBase GUI.
   */
  public Gui() {
    ItemView view;

    initViews();
    init();

    // set minimum width of year column
    itemTable.getColumnExt(ColumnHeader.YEAR.getName()).setMinWidth(40);

    // restore window position and size
    this.setBounds(Preferences.getInt(Preferences.WINDOW_POSITION_X),
        Preferences.getInt(Preferences.WINDOW_POSITION_Y),
        Preferences.getInt(Preferences.WINDOW_SIZE_X),
        Preferences.getInt(Preferences.WINDOW_SIZE_Y));

    // restore category dialog position and size
    categoriesDialog.setBounds(Preferences.getInt(Preferences.CATEGORY_POSITION_X),
        Preferences.getInt(Preferences.CATEGORY_POSITION_Y),
        Preferences.getInt(Preferences.CATEGORY_SIZE_X),
        Preferences.getInt(Preferences.CATEGORY_SIZE_Y));

    // restore selected view
    try {
      filterViewCombobox.setSelectedIndex(Preferences.getInt(Preferences.SELECTED_VIEW));
      view = (ItemView) filterViewCombobox.getSelectedItem();
    } catch (final Exception e) {
      view = (ItemView) filterViewCombobox.getItemAt(0);
    }
    view.setSelectedItem(Preferences.get(Preferences.SELECTED_ITEM));
    ((ItemTableModel) itemTable.getModel()).setView(view);

    // restore sound on classics
    toolsMenu_PlaySoundOnClassics.setSelected(Preferences.is(Preferences.SOUND_ON_CLASSICS));
    status5.setIcon(Preferences.is(Preferences.SOUND_ON_CLASSICS) ? Const.ICON_SOUND_ON
        : Const.ICON_SOUND_OFF);

    // restore hardware joystick
    toolsMenu_HardwareJoystick.setSelected(Preferences.is(Preferences.HARDWARE_JOYSTICK));
    status6.setIcon(Preferences.is(Preferences.HARDWARE_JOYSTICK) ? Const.ICON_JOYSTICK_ON
        : Const.ICON_JOYSTICK_OFF);

    // restore adult filter
    toolsMenu_AdultFilter.setSelected(Preferences.is(Preferences.ADULT_FILTER));

    // restore display game details
    viewMenu_GameDetails.setSelected(Preferences.is(Preferences.DISPLAY_DETAILS));
    infoPanel.setVisible(Preferences.is(Preferences.DISPLAY_DETAILS));
    // mainPane.resetToPreferredSizes();

    updateItemTable();
  }

  public static void setLookAndFeel() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      // UIManager.put("Table.background", new Color(237, 236, 235));
    } catch (final Exception ex) {
      log.info("could not load look and feel " + ex);
    }
  }

  /**
   * returns an existing view from the list, or <code>null</code> if none exists.
   * @param list A list of views.
   * @param title The title to search for.
   * @return The found view, or <code>null</code> otherwise.
   */
  protected ItemView getExistingViewByTitle(List<ItemView> list, String title) {
    
    for (ItemView view : list) {
      if (view.getName().equals(title)) {
        return view;
      }
    }
      
    return null;
  }
  
  
  public void initViews() {
    ItemView view;

    dummyView = new ItemView();
    dummyView.setInclude(ItemView.INCLUDE_NOTHING);
    dummyView.setSortOrder(SortOrder.ASCENDING);

    final List<ItemView> dbSystemViews = Db.getSystemViews();

    view = getExistingViewByTitle(dbSystemViews, Const.VIEWNAME_ALL_GAMES);
    if (view == null) {
      view = new ItemView();
      view.setName(Const.VIEWNAME_ALL_GAMES);
      view.setType(ItemView.TYPE_SYSTEM);
      view.setInclude(ItemView.INCLUDE_GAMES);
      view.setSortOrder(SortOrder.ASCENDING);
    }
    views.add(view);

    view = getExistingViewByTitle(dbSystemViews, Const.VIEWNAME_AVAILABLE_GAMES);
    if (view == null) {
      view = new ItemView();
      view.setName(Const.VIEWNAME_AVAILABLE_GAMES);
      view.setType(ItemView.TYPE_SYSTEM);
      view.setInclude(ItemView.INCLUDE_GAMES);
      view.setSortOrder(SortOrder.ASCENDING);
      view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_EXISTS,
          ItemViewFilter.OPERATOR_EQUAL, "Games", "FA", "", "", ""));
    }
    views.add(view);

    view = getExistingViewByTitle(dbSystemViews, Const.VIEWNAME_ALL_MUSIC);
    if (view == null) {
      view = new ItemView();
      view.setName(Const.VIEWNAME_ALL_MUSIC);
      view.setType(ItemView.TYPE_SYSTEM);
      view.setInclude(ItemView.INCLUDE_BOTH);
      view.setSortOrder(SortOrder.ASCENDING);
      view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_FILLED,
          ItemViewFilter.OPERATOR_EQUAL, "Games", "SIDFILENAME", "Music", "FILENAME", ""));
      view.setColumnVisibility(new ArrayList<String>(Arrays.asList(new String[] {
          ColumnHeader.NAME.getName(), ColumnHeader.YEAR.getName(),
          ColumnHeader.PUBLISHER.getName(), ColumnHeader.MUSICIAN.getName() })));
    }
    views.add(view);

    view = getExistingViewByTitle(dbSystemViews, Const.VIEWNAME_AVAILABLE_MUSIC);
    if (view == null) {
      view = new ItemView();
      view.setName(Const.VIEWNAME_AVAILABLE_MUSIC);
      view.setType(ItemView.TYPE_SYSTEM);
      view.setInclude(ItemView.INCLUDE_BOTH);
      view.setSortOrder(SortOrder.ASCENDING);
      view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_EXISTS,
          ItemViewFilter.OPERATOR_EQUAL, "Games", "SA", "Music", "SA", ""));
      view.setColumnVisibility(new ArrayList<String>(Arrays.asList(new String[] {
          ColumnHeader.NAME.getName(), ColumnHeader.YEAR.getName(),
          ColumnHeader.PUBLISHER.getName(), ColumnHeader.MUSICIAN.getName() })));
    }
    views.add(view);

    view = getExistingViewByTitle(dbSystemViews, Const.VIEWNAME_FAVOURITES);
    if (view == null) {
      view = new ItemView();
      view.setName(Const.VIEWNAME_FAVOURITES);
      view.setType(ItemView.TYPE_SYSTEM);
      view.setInclude(ItemView.INCLUDE_BOTH);
      view.setSortOrder(SortOrder.ASCENDING);
      view.setColumnVisibility(new ArrayList<String>(Arrays.asList(new String[] {
          ColumnHeader.NAME.getName(), ColumnHeader.YEAR.getName(),
          ColumnHeader.PUBLISHER.getName(), ColumnHeader.PROGRAMMER.getName(),
          ColumnHeader.MUSICIAN.getName() })));
      view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_DBFIELD,
          ItemViewFilter.OPERATOR_EQUAL, "Games", "Fav", "Music", "SFav", "-1"));
    }
    views.add(view);

    if (JGameBase.option_dangerous) {
      view = new ItemViewDuplicateUrls();
      view.setName("<All Games with URLs and double Name/Publisher>");
      view.setType(ItemView.TYPE_SYSTEM);
      view.setInclude(ItemView.INCLUDE_GAMES);
      view.setSortOrder(SortOrder.ASCENDING);
      views.add(view);
    }

    final List<ItemView> dbNormalViews = Db.getNormalViews();
    if ((dbNormalViews != null) && (dbNormalViews.size() > 0)) {
      views.addAll(dbNormalViews);
    }
  }

  public static boolean singleClick(final java.awt.event.MouseEvent evt) {
    return ((evt.getButton() == MouseEvent.BUTTON1) && (evt.getClickCount() == 1));
  }

  public static boolean doubleClick(final java.awt.event.MouseEvent evt) {
    return ((evt.getButton() == MouseEvent.BUTTON1) && (evt.getClickCount() == 2));
  }

  public static boolean rightClick(final java.awt.event.MouseEvent evt) {
    return ((evt.getButton() == MouseEvent.BUTTON3) && (evt.getClickCount() == 1));
  }

  /**
   * Displays the help dialog.
   */
  public static void displayHelpDialog() {
    JOptionPane.showMessageDialog(null, Const.HELP_MSG, "HELP", JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Displays the usage dialog.
   */
  public static void displayUsageDialog() {
    JOptionPane.showMessageDialog(null, Const.USAGE_MSG, "USAGE", JOptionPane.WARNING_MESSAGE);
  }

  /**
   * Displays an error message.
   * 
   * @param s
   *          The error message.
   */
  public static void displayErrorDialog(final String s) {
    log.error(s);
    if (!JGameBase.nogui) {
        JOptionPane.showMessageDialog(null, s, "ERROR", JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Displays a warning message.
   * 
   * @param s
   *          The warning message.
   */
  public static void displayWarningDialog(final String s) {
    log.warn(s);
    if (!JGameBase.nogui) {
        JOptionPane.showMessageDialog(null, s, "Warning", JOptionPane.WARNING_MESSAGE);
    }
  }

  /**
   * Displays an info message.
   * 
   * @param s
   *          The info message.
   */
  public static void displayInformationDialog(final String s) {
    log.info(s);
    if (!JGameBase.nogui) {
        JOptionPane.showMessageDialog(null, s, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
  }

  /**
   * Displays an conformation question.
   * 
   * @param s
   *          The info message.
   */
  public static int displayConfirmationDialog(final String s) {
    return JOptionPane.showConfirmDialog(null, s, "jGameBase", JOptionPane.YES_NO_OPTION);
  }

  public ItemTableModel getItemTableModel() {
    return ((ItemTableModel) itemTable.getModel());
  }

  @Override
  public void quit() {
    // set wait cursor
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    // stop playing music
    Emulators.stopMusicRunning();

    // save window position
    Preferences.set(Preferences.WINDOW_POSITION_X, getX());
    Preferences.set(Preferences.WINDOW_POSITION_Y, getY());
    // save window size
    Preferences.set(Preferences.WINDOW_SIZE_X, getWidth());
    Preferences.set(Preferences.WINDOW_SIZE_Y, getHeight());

    // save category dialog position
    Preferences.set(Preferences.CATEGORY_POSITION_X, categoriesDialog.getX());
    Preferences.set(Preferences.CATEGORY_POSITION_Y, categoriesDialog.getY());
    // save category dialog size
    Preferences.set(Preferences.CATEGORY_SIZE_X, categoriesDialog.getWidth());
    Preferences.set(Preferences.CATEGORY_SIZE_Y, categoriesDialog.getHeight());

    // save sidebar width
    Preferences.set(Preferences.SIDEBAR_WIDTH, getSidebarWidth());
    Preferences.set(Preferences.SIDEBAR_H_DIVIDER, sidePanel_HSplitPane.getDividerLocation());
    Preferences.set(Preferences.SIDEBAR_V_DIVIDER, sidePanel_VSplitPane.getDividerLocation());

    final ItemView currentView = (ItemView) filterViewCombobox.getSelectedItem();

    // a quickview will not be saved to the database
    // so select view ALL_GAMES at new start
    if (currentView.getType() == ItemView.TYPE_QUICK) {
      Preferences.set(Preferences.SELECTED_VIEW, Gui.VIEW_ALL_GAMES);
    }

    // save selected view
    Preferences.set(Preferences.SELECTED_VIEW, filterViewCombobox.getSelectedIndex());
    ((ItemTableModel) itemTable.getModel()).saveGuiParametersToView(currentView);

    // save selected item
    Preferences.set(Preferences.SELECTED_ITEM, currentView.getSelectedItem());

    // save all changed, normal views to database
    final List<ItemView> changedViews = new java.util.ArrayList<ItemView>();

    for (final ItemView view : views) {
      if ((view.getType() != ItemView.TYPE_QUICK) && (view.isLoaded())) {
        changedViews.add(view);
      }
    }

    if (changedViews.size() > 0) {
      Db.saveOrUpdateAll(changedViews);
    }
  }

  public static void importAccessToDatabase(final File file) {
    try {
      final int steps = 3;
      int step = 0;
      Gui.progressMonitor = new ProgressMonitor(null, "Importing Access database '"
          + file.getName() + "'", "", 0, steps * Gui.step_width);
      Gui.progressMonitor.setMillisToDecideToPopup(0);
      Gui.progressMonitor.setMillisToPopup(0);

      // step 1: export to csv
      step++;
      Gui.progressMonitor.setNote("Exporting Access database to csv files");
      Gui.progressMonitor.setProgress(((step - 1) * Gui.step_width) - 1);
      Export.mdb2Csv(file);

      if (Gui.progressMonitor.isCanceled()) {
        throw new Exception(Gui.CANCEL_MSG);
      }

      // step 2: create db
      step++;
      Gui.progressMonitor.setNote("Creating empty database");
      Gui.progressMonitor.setProgress(((step - 1) * Gui.step_width) - 1);
      Db.init(file.getParentFile().getName(), true);

      if (Gui.progressMonitor.isCanceled()) {
        throw new Exception(Gui.CANCEL_MSG);
      }
      // step 3: import from csv
      step++;
      Gui.progressMonitor.setNote("Importing from csv files (this may take a while...)");
      Gui.progressMonitor.setProgress(((step - 1) * Gui.step_width) - 1);
      Import.csv2Db(file);

      if (Gui.progressMonitor.isCanceled()) {
        throw new Exception(Gui.CANCEL_MSG);
      }

      // hide progress monitor
      Gui.progressMonitor.setProgress(Gui.progressMonitor.getMaximum());

    } catch (final Exception e) {

      // Remove database directory
      final File directory = new File(file.getParentFile().getAbsolutePath(),
          Const.DATABASE_DIRNAME);
      if (directory.exists()) {
        FileTools.deleteAll(directory);
      }

      if ((e != null) && (e.getMessage() != null) && (e.getMessage().equals(Gui.CANCEL_MSG))) {
        displayWarningDialog("Access import was canceled.");
      } else {
        displayErrorDialog("Access import failed!");
        e.printStackTrace();
      }
    }
  }
}
