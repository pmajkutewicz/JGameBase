/*
verifyAvailableFiles * Copyright (C) 2006-2014 F. Gerbig (fgerbig@users.sourceforge.net)
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import jgamebase.Const;
import jgamebase.Const.CloseAction;
import jgamebase.JGameBase;
import jgamebase.db.Db;
import jgamebase.db.Export;
import jgamebase.db.Import;
import jgamebase.db.Table;
import jgamebase.db.model.Extra;
import jgamebase.db.model.Game;
import jgamebase.db.model.Item;
import jgamebase.db.model.ItemView;
import jgamebase.db.model.ItemViewFilter;
import jgamebase.db.model.Music;
import jgamebase.db.model.Musician;
import jgamebase.gui.widgets.CompoundIcon;
import jgamebase.gui.widgets.JLongSeparator;
import jgamebase.gui.widgets.JScalingAnimation;
import jgamebase.gui.widgets.JScalingIcon;
import jgamebase.gui.widgets.JTitledSeparator;
import jgamebase.gui.widgets.JZoomScalingImage;
import jgamebase.gui.widgets.flushable;
import jgamebase.model.Databases;
import jgamebase.model.Emulator;
import jgamebase.model.Emulators;
import jgamebase.model.FileExtensions;
import jgamebase.model.Paths;
import jgamebase.model.Plugins;
import jgamebase.model.Preferences;
import jgamebase.plugins.DiskInfo;
import jgamebase.plugins.Extractor;
import jgamebase.plugins.MusicInfo;
import jgamebase.tools.DownloadTools;
import jgamebase.tools.FileTools;
import jgamebase.tools.StringTools;
import jgamebase.tools.SystemTools;

import org.apache.commons.collections.map.MultiValueMap;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * Contains the GUI methods.
 * 
 * @author F. Gerbig (fgerbig@users.sourceforge.net)
 */
public abstract class GuiMethods extends GuiObjects {

  /**
	 * 
	 */
  private static final long serialVersionUID = -4564734022002580425L;

  protected int currentTab = 0;

  protected Item lastItem = null;
  protected int numExtraImagesShown;

  protected List<Item> recent = new ArrayList<Item>();

  protected String find = "";

  protected Game linkGame = null;
  protected Extra copyExtra = null;

  Rectangle itemTableSavedPosition_Rect;

  int itemTableSavedPosition_Row;

  private List<Extra> filterExtrasByCategory(final List<Extra> allExtras,
      final Extra.Category category) {
    final List<Extra> extras = new ArrayList<Extra>();

    for (final Extra extra : allExtras) {
      if (extra.getCategory() == category) {
        extras.add(extra);
      }
    }

    return extras;
  }

  private int countImagesForExtras(final List<Extra> extras) {
    int count = 0;

    for (final Extra extra : extras) {
      if (extra.isImage() || extra.isAnimation()) {
        count++;
      }
    }

    return count;
  }

  private void addAnimationsForExtrasToSideBar(final JPanel panel, final String title,
      final List<Extra> extras) {

    if (countImagesForExtras(extras) > 0) {
      boolean addedSomething = false;

      panel.add(new JTitledSeparator(title));

      for (final Extra extra : extras) {
        final String filename = ((Paths.getExtraPath().find(new File(extra.getFilename())) != null) ? 
            Paths.getExtraPath().find(new File(extra.getFilename())).getPath() : "");

        try {
          if (!filename.isEmpty() && extra.isAnimation()) {
            addedSomething = true;
            panel.add(new JScalingAnimation(filename, "<html>" + extra.getName()
                + (Preferences.is(Preferences.EXTRAS_SHOW_FILENAMES) ? "<br>" + extra.getFilename() : "") + "</html>"));
            panel.add(new JLongSeparator());
          }
        } catch (final Exception e) {
        }
      }

      if (addedSomething) {
        panel.remove(panel.getComponentCount() - 1);
      }

    }
  }

  private void addImagesForExtrasToSideBar(final JPanel panel, final String title, final List<Extra> extras) {

    if (countImagesForExtras(extras) > 0) {
      boolean addedSomething = false;

      panel.add(new JTitledSeparator(title));

      for (final Extra extra : extras) {
        final String filename = ((Paths.getExtraPath().find(new File(extra.getFilename())) != null) ? 
            Paths.getExtraPath().find(new File(extra.getFilename())).getPath() : "");

        try {
          if (!filename.isEmpty() && extra.isImage() && (numExtraImagesShown < Preferences.getInt(Preferences.MAX_EXTRA_IMAGES_SHOWN))) {
            addedSomething = true;
            panel.add(new JZoomScalingImage(filename, "<html>" + extra.getName()
                + (Preferences.is(Preferences.EXTRAS_SHOW_FILENAMES) ? "<br>" + extra.getFilename() : "") + "<br><br>"
                + "<i>Left click</i> to open with external viewer specified in options<br>"
                + "<i>Right click</i> to open with internal viewer</html>"));
            panel.add(new JLongSeparator());
            numExtraImagesShown++;
          }
        } catch (final Exception e) {
        }
      }

      if (addedSomething) {
        panel.remove(panel.getComponentCount() - 1);
      }

    }
  }

  private void addButtonsForExtrasToSidebar(final List<Extra> extras) {
    boolean addedSomething = false;

    if (extras.size() > 0) {
      for (final Extra extra : extras) {

        // TODO URL images should be displayed as pictures, not buttons
        if (extra.isUrl() || (!extra.isImage() && !extra.isAnimation())) {
          Icon icon = extra.getCategory().getIcon();

          // Create icons with overlays

          // show if extras are equal to game marked for linking
          if (linkGame != null) {
            final List<Extra> linkExtras = linkGame.getExtras();
            for (final Extra linkExtra : linkExtras) {
              if (extra.isUrl() && extra.getFilename().equalsIgnoreCase(linkExtra.getFilename())) {
                icon = new CompoundIcon(CompoundIcon.Axis.Z_AXIS, 0, CompoundIcon.RIGHT,
                    CompoundIcon.TOP, icon, Const.ICON_CATEGORY_OVERLAY_EQUAL);
              }
            }
          }

          if (extra.isAdditional()) {
            icon = new CompoundIcon(CompoundIcon.Axis.Z_AXIS, 0, CompoundIcon.LEFT,
                CompoundIcon.BOTTOM, icon, Const.ICON_CATEGORY_OVERLAY_ADDITIONAL);
          }

          if (extra.isUrl()) {

            try {
              final String hostname = new URI(extra.getFilename()).getHost().toLowerCase();
              File iconFile = new File(new File(Databases.getCurrent().getPath(), "Gfx"), hostname
                  + ".png");

              if (iconFile.exists()) {
                // use database supplied icon for host name
                icon = new CompoundIcon(CompoundIcon.Axis.Z_AXIS, 0, CompoundIcon.RIGHT,
                    CompoundIcon.BOTTOM, icon, new ImageIcon(iconFile.getAbsolutePath()));
              } else {
                iconFile = new File(new File(Const.GBDIR_RO, "Artwork/Category/overlay"), hostname
                    + ".png");
                if (iconFile.exists()) {
                  // use jgb supplied icon for host name
                  icon = new CompoundIcon(CompoundIcon.Axis.Z_AXIS, 0, CompoundIcon.RIGHT,
                      CompoundIcon.BOTTOM, icon, new ImageIcon(iconFile.getAbsolutePath()));
                } else {
                  log.info("No favicon for host '" + hostname + "'!");
                  icon = new CompoundIcon(CompoundIcon.Axis.Z_AXIS, 0, CompoundIcon.RIGHT,
                      CompoundIcon.BOTTOM, icon, Const.ICON_CATEGORY_OVERLAY_URL);
                }
              }
            } catch (final URISyntaxException e) {
            }

            // downloadable?
            if (DownloadTools.isDownloadableFiletype(extra.getFilename())) {
              icon = new CompoundIcon(CompoundIcon.Axis.Z_AXIS, 0, CompoundIcon.LEFT,
                  CompoundIcon.BOTTOM, icon, Const.ICON_CATEGORY_OVERLAY_DOWNLOAD);
            }
          }

          // packed additional?
          if ((!extra.isUrl()) && (extra.isAdditional())
              && (Const.EXTENSIONS_PACKED.matches(extra.getFilename()))) {
            icon = new CompoundIcon(CompoundIcon.Axis.Z_AXIS, 0, CompoundIcon.RIGHT,
                CompoundIcon.BOTTOM, icon, Const.ICON_CATEGORY_OVERLAY_PACKED);
          }

          // missing?
          if ((!extra.isUrl()) && (!(Paths.getExtraPath().exists(new File(extra.getFilename()))))) {
            icon = new CompoundIcon(CompoundIcon.Axis.Z_AXIS, 0, CompoundIcon.RIGHT,
                CompoundIcon.BOTTOM, icon, Const.ICON_CATEGORY_OVERLAY_MISSING);
          }

          final JButton button = new JButton(icon);
          button.setToolTipText("<html>"
              + extra.getName()
              + (Preferences.is(Preferences.EXTRAS_SHOW_FILENAMES) ? "<br>" + extra.getFilename()
                  : "") + "</html>");

          button.setActionCommand(Integer.toString(extra.getId()));

          button.addActionListener(new java.awt.event.ActionListener() {
            // play extra
            @Override
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
              final String command = evt.getActionCommand();
              if ((command != null) && (!command.isEmpty())) {
                try {
                  final int id = Integer.parseInt(command);
                  final Extra extra = Db.getExtraById(id);
                  if (extra != null) {
                    extra.play();
                  }
                } catch (final NumberFormatException e) {
                  e.printStackTrace();
                }
              }
            }
          });

          button.addMouseListener(new MouseAdapter() {
            // context menu
            @Override
            public void mousePressed(final MouseEvent evt) {
              if (Gui.rightClick(evt)) {
                final String command = ((JButton) evt.getComponent()).getActionCommand();
                if ((command != null) && (!command.isEmpty())) {
                  try {
                    final int id = Integer.parseInt(command);
                    final Extra extra = Db.getExtraById(id);
                    if (extra != null) {
                      final JPopupMenu popupMenu = createButtonsForExtrasPopupMenu(extra);
                      if (popupMenu != null) {
                        popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                      }
                    }
                  } catch (final NumberFormatException e) {
                    e.printStackTrace();
                  }
                }
              }

            }
          });

          sideButtonPanel.add(button);
          addedSomething = true;
        }
      }
    }

    if (addedSomething) {
      sideButtonPanel.add(new JSeparator(SwingConstants.VERTICAL));
    }

  }

  private final FileFilter csvFileFilter = new FileNameExtensionFilter(
      "Comma Sepparated Values (*.csv)", "csv");
  private final FileFilter zipFileFilter = new FileNameExtensionFilter(
      "Zip compressed game database (*.zip)", "zip");

  @Override
  protected void about() {
    new AboutDialog(JGameBase.getGui(), true);
  }

  @Override
  protected void addToRecent(Item item) {
    // remove all occurrences of item
    while (recent.contains(item)) {
      recent.remove(item);
    }

    // add item
    recent.add(0, item);

    // delete last if recent too long
    if (recent.size() > 10) {
      recent.remove(recent.size() - 1);
    }

    // clear menu
    recentMenu.removeAll();

    // build new menu
    int i = 0;
    for (final Iterator<Item> iter = recent.iterator(); iter.hasNext(); i++) {
      item = iter.next();

      // create new menu entry
      final JMenuItem menuItem = new JMenuItem(item.getName(), item.getIcon());
      menuItem.setActionCommand(i + "");
      menuItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(final ActionEvent e) {
          log.info("recent: " + e.getActionCommand());
          final int i = Integer.parseInt(e.getActionCommand());
          final Item item = recent.get(i);
          selectItem(item.getStringId(), false);
          addToRecent(item);
          item.play();
        }
      });
      recentMenu.add(menuItem);
    }
  }

  @Override
  protected void displayItem(Item item) {

    if (item == null) {
      // can happen if the item table is empty
      // => remove all info of previously displayed items

      // status id
      status0.setText(" ");

      infoTabbedPane.removeAll();
      sidePanel_GameImage.removeAll();
      sidePanel_AdditionalImage.removeAll();
      sideButtonPanel.removeAll();
      sidePanel.repaint();

      // disable buttons on filter panel
      filterPlayGameButton.setEnabled(false);
      filterPlayMusicButton.setEnabled(false);
      filterExtrasButton.setEnabled(false);
      filterAdditionalExtrasButton.setEnabled(false);

      lastItem = item;

      return;
    }

    if (item == lastItem) {
      return;
    }

    // don't know why this happens,
    // sometimes the id 0 shows up, which should be empty...
    if ((item.getId() == 0) && (lastItem != null)) {
      item = lastItem;
    }

    // to force display at program start we need a change in item
    if (lastItem == null) {
      if (item instanceof Game) {
        lastItem = new Music();
      } else {
        lastItem = new Game();
      }
    }

    if ((lastItem instanceof Music) && (item instanceof Game)) {
      infoTabbedPane.removeAll();
      infoTabbedPane.addTab("Game", Const.ICON_TAB_GAME, gameInfoPanel, "");
      infoTabbedPane.addTab("Version", Const.ICON_TAB_VERSION, versionInfoPanel, "");
      infoTabbedPane.addTab("Personal", Const.ICON_TAB_PERSONAL, personalInfoPanel, "");
      infoTabbedPane.addTab("Notes", Const.ICON_TAB_NONOTES, notesInfoPanel, "");
      infoTabbedPane.addTab("Music", Const.ICON_TAB_MUSIC, musicInfoPanel, "");
      infoTabbedPane.setSelectedIndex(currentTab);
    } else if ((lastItem instanceof Game) && (item instanceof Music)) {
      currentTab = infoTabbedPane.getSelectedIndex();
      infoTabbedPane.removeAll();
      infoTabbedPane.addTab("Music", Const.ICON_TAB_ONLYMUSIC, musicInfoPanel, "");
    }

    // status id
    status0.setText(item.getStringId());

    // clear side panel
    Component[] components;

    // clear game images
    components = sidePanel_GameImage.getComponents();
    for (final Component component2 : components) {
      if (component2 instanceof flushable) {
        ((flushable) component2).flush();
      }
    }
    components = null; // remove references
    sidePanel_GameImage.removeAll();

    // clear additional images
    components = sidePanel_AdditionalImage.getComponents();
    for (final Component component2 : components) {
      if (component2 instanceof flushable) {
        ((flushable) component2).flush();
      }
    }
    components = null; // remove references
    sidePanel_AdditionalImage.removeAll();

    sideButtonPanel.removeAll();
    sidePanel.repaint();

    if (item instanceof Game) {
      displayGame((Game) item);
    }

    if (item instanceof Music) {
      displayMusic((Music) item);
    }

    lastItem = item;
  }

  private void displayGame(final Game game) {

    try {
      if ((game == null) || (game.getName() == null) || (game.getId() == 0)) {
        return;
      }

      // buttons
      filterPlayGameButton.setEnabled(game.getGameFileExists());
      filterPlayMusicButton.setEnabled(game.getMusicFileExists());
      filterExtrasButton.setEnabled(game.getHasExtras());
      filterAdditionalExtrasButton.setEnabled(true);

      // menus
      setGameMenusEnabled(true);

      // set sidePanel layout
      sidePanel_GameImage.setLayout(new javax.swing.BoxLayout(sidePanel_GameImage,
          javax.swing.BoxLayout.Y_AXIS));

      // sidebar extras
      final List<Extra> extras = game.getExtras();
      numExtraImagesShown = 0;
      
      // add animations to sidebar
      addAnimationsForExtrasToSideBar(sidePanel_GameImage, "Additional Game Animations",
          filterExtrasByCategory(extras, Extra.Category.GameImage));

      // screenshots
      final List<String> screenshotFilenames = game.getScreenshotFilenames();

      if (screenshotFilenames.size() > 0) {
        sidePanel_GameImage.add(new JTitledSeparator("Screenshots"));

        int i = 1;
        for (final String filename : screenshotFilenames) {
          try {
            if (FileTools.isAnimation(filename)) {
              sidePanel_GameImage.add(new JScalingAnimation(filename, "<html>Screenshot #" + i
                  + " for '" + game.getName() + "'"
                  + (Preferences.is(Preferences.EXTRAS_SHOW_FILENAMES) ? "<br>" + filename : "")
                  + "</html>"));
            } else {
              sidePanel_GameImage.add(new JZoomScalingImage(filename, "<html>Screenshot #" + i
                  + " for '" + game.getName() + "'"
                  + (Preferences.is(Preferences.EXTRAS_SHOW_FILENAMES) ? "<br>" + filename : "")
                  + "<br><br>"
                  + "<i>Left click</i> to open with external viewer specified in options<br>"
                  + "<i>Right click</i> to open with internal viewer</html>"));
            }
            i++;
          } catch (final Exception e) {
            e.printStackTrace();
          }
          sidePanel_GameImage.add(new JLongSeparator());
        }

      }

      // no screenshots, no additional game images
      if ((screenshotFilenames.size() == 0)
          && (filterExtrasByCategory(extras, Extra.Category.GameImage).size() == 0)) {
        JScalingIcon noScreenshot;
        if (Databases.getCurrent().hasMissingScreenshotIcon()) {
          // use no screenshot icon of database
          noScreenshot = new JScalingIcon(Databases.getCurrent().getMissingScreenshotIcon());
        } else {
          // use default
          noScreenshot = new JScalingIcon(Const.ICON_MISSINGSCREENSHOT);
        }
        noScreenshot.setToolTipText("No screenshot for '" + game.getName() + "'");
        sidePanel_GameImage.add(noScreenshot);
      }

      // add images to sidebar
      addImagesForExtrasToSideBar(sidePanel_GameImage, "Additional Game Images",
          filterExtrasByCategory(extras, Extra.Category.GameImage));

      addImagesForExtrasToSideBar(sidePanel_GameImage, "Documentation",
          filterExtrasByCategory(extras, Extra.Category.Documentation));
      addImagesForExtrasToSideBar(sidePanel_GameImage, "Manual",
          filterExtrasByCategory(extras, Extra.Category.Documentation_Manual));

      addImagesForExtrasToSideBar(sidePanel_GameImage, "Solution",
          filterExtrasByCategory(extras, Extra.Category.Solution));
      addImagesForExtrasToSideBar(sidePanel_GameImage, "Map",
          filterExtrasByCategory(extras, Extra.Category.Solution_Map));
      addImagesForExtrasToSideBar(sidePanel_GameImage, "Tip",
          filterExtrasByCategory(extras, Extra.Category.Solution_Tip));
      addImagesForExtrasToSideBar(sidePanel_GameImage, "Walkthrough",
          filterExtrasByCategory(extras, Extra.Category.Solution_walkthrough));

      addImagesForExtrasToSideBar(sidePanel_GameImage, "Review",
          filterExtrasByCategory(extras, Extra.Category.Review));

      addImagesForExtrasToSideBar(sidePanel_AdditionalImage, "Additional Images",
          filterExtrasByCategory(extras, Extra.Category.AdditionalImage));
      addImagesForExtrasToSideBar(sidePanel_AdditionalImage, "Cover",
          filterExtrasByCategory(extras, Extra.Category.AdditionalImage_Cover));
      addImagesForExtrasToSideBar(sidePanel_AdditionalImage, "Advertisements",
          filterExtrasByCategory(extras, Extra.Category.AdditionalImage_Advertisement));
      addImagesForExtrasToSideBar(sidePanel_AdditionalImage, "Book Cover",
          filterExtrasByCategory(extras, Extra.Category.AdditionalImage_Bookcover));

      addImagesForExtrasToSideBar(sidePanel_AdditionalImage, "Movie",
          filterExtrasByCategory(extras, Extra.Category.Movie));

      addImagesForExtrasToSideBar(sidePanel_AdditionalImage, "Media",
          filterExtrasByCategory(extras, Extra.Category.Media));
      addImagesForExtrasToSideBar(sidePanel_AdditionalImage, "Cartridge",
          filterExtrasByCategory(extras, Extra.Category.Media_Cartridge));
      addImagesForExtrasToSideBar(sidePanel_AdditionalImage, "Tape",
          filterExtrasByCategory(extras, Extra.Category.Media_Tape));
      addImagesForExtrasToSideBar(sidePanel_AdditionalImage, "Disk",
          filterExtrasByCategory(extras, Extra.Category.Media_Disk));
      addImagesForExtrasToSideBar(sidePanel_AdditionalImage, "Harddisk",
          filterExtrasByCategory(extras, Extra.Category.Media_Harddisk));

      addImagesForExtrasToSideBar(sidePanel_AdditionalImage, "Miscellany",
          filterExtrasByCategory(extras, Extra.Category.Misc));

      // add buttons to sidebar
      addButtonsForExtrasToSidebar(filterExtrasByCategory(extras, Extra.Category.GameImage));

      addButtonsForExtrasToSidebar(filterExtrasByCategory(extras, Extra.Category.Movie));

      addButtonsForExtrasToSidebar(filterExtrasByCategory(extras, Extra.Category.AdditionalImage));
      addButtonsForExtrasToSidebar(filterExtrasByCategory(extras,
          Extra.Category.AdditionalImage_Cover));
      addButtonsForExtrasToSidebar(filterExtrasByCategory(extras,
          Extra.Category.AdditionalImage_Advertisement));
      addButtonsForExtrasToSidebar(filterExtrasByCategory(extras,
          Extra.Category.AdditionalImage_Bookcover));

      addButtonsForExtrasToSidebar(filterExtrasByCategory(extras, Extra.Category.Documentation));
      addButtonsForExtrasToSidebar(filterExtrasByCategory(extras,
          Extra.Category.Documentation_Manual));

      addButtonsForExtrasToSidebar(filterExtrasByCategory(extras, Extra.Category.Solution));
      addButtonsForExtrasToSidebar(filterExtrasByCategory(extras, Extra.Category.Solution_Map));
      addButtonsForExtrasToSidebar(filterExtrasByCategory(extras, Extra.Category.Solution_Tip));
      addButtonsForExtrasToSidebar(filterExtrasByCategory(extras,
          Extra.Category.Solution_walkthrough));

      addButtonsForExtrasToSidebar(filterExtrasByCategory(extras, Extra.Category.Review));

      addButtonsForExtrasToSidebar(filterExtrasByCategory(extras, Extra.Category.Media));
      addButtonsForExtrasToSidebar(filterExtrasByCategory(extras, Extra.Category.Media_Cartridge));
      addButtonsForExtrasToSidebar(filterExtrasByCategory(extras, Extra.Category.Media_Tape));
      addButtonsForExtrasToSidebar(filterExtrasByCategory(extras, Extra.Category.Media_Disk));
      addButtonsForExtrasToSidebar(filterExtrasByCategory(extras, Extra.Category.Media_Harddisk));

      addButtonsForExtrasToSidebar(filterExtrasByCategory(extras, Extra.Category.Misc));

      // calculate new size for button panel
      sideButtonPanelResized();

      // icons
      gameInfoPrequel.setIcon(Const.ICON_GAMEINFO_NOPREQUEL);
      if (game.hasPrequel()) {
        gameInfoPrequel.setIcon(Const.ICON_GAMEINFO_PREQUEL);
      }

      gameInfoSequel.setIcon(Const.ICON_GAMEINFO_NOSEQUEL);
      if (game.hasSequel()) {
        gameInfoSequel.setIcon(Const.ICON_GAMEINFO_SEQUEL);
      }

      gameInfoRelated.setIcon(Const.ICON_GAMEINFO_NORELATED);
      if (game.hasRelated()) {
        gameInfoRelated.setIcon(Const.ICON_GAMEINFO_RELATED);
      }

      gameInfoRating.setIcon(Const.ICONS_GAMEINFO_RATING[game.getRating()]);
      gameInfoRating.setToolTipText(Const.FORDISPLAY_RATING[game.getRating()]);

      gameInfoAdult.setIcon(Const.ICON_GAMEINFO_NOADULT);
      if (game.getIsAdult()) {
        gameInfoAdult.setIcon(Const.ICON_GAMEINFO_ADULT);
      }

      // game link menu
      editMenu_SetGameLink.setEnabled((linkGame != null) && (game != linkGame));
      editMenu_ClearGameLink.setEnabled(game.hasPrequel() || game.hasSequel() || game.hasRelated());

      // game info
      gameInfoTitle.setText(game.getName());
      gameInfoTitleLeft.setIcon(null);
      gameInfoTitleRight.setIcon(null);
      if (game.getIsClassic()) {
        gameInfoTitleLeft.setIcon(Const.ICON_GAMEINFO_CLASSIC);
        gameInfoTitleRight.setIcon(Const.ICON_GAMEINFO_CLASSIC);
      }
      gameInfoYear.setText(game.getYear().getName());
      gameInfoPublisher.setText(game.getPublisher().getName());
      gameInfoCoding.setText(game.getProgrammer().getName());
      gameInfoMusic.setText(game.getMusician().getName());
      gameInfoGenre.setText(game.getGenreForDisplay());

      gameInfoNoOfPlayers.setText(game.getPlayersForDisplay());

      gameInfoLanguage.setText(game.getLanguage().getName());
      gameInfoComment.setText(game.getComment() + " ");
      // so comment is not empty => height of row stays the same

      // version info
      versionInfoTitle.setText(game.getName());
      versionInfoTitleLeft.setIcon(null);
      versionInfoTitleRight.setIcon(null);
      if (game.getIsClassic()) {
        versionInfoTitleLeft.setIcon(Const.ICON_GAMEINFO_CLASSIC);
        versionInfoTitleRight.setIcon(Const.ICON_GAMEINFO_CLASSIC);
      }

      versionInfoCracked.setText(game.getCracker().getName());
      versionInfoNoOfTrainers.setText(game.getTrainerForDisplay());
      versionInfoHighScoreSaver.setText(game.getHasHighscoreSaverForDisplay());
      versionInfoGamelength.setText(game.getLengthForDisplay());

      versionInfoPalNTSC.setText(game.getPalNtscForDisplay());

      versionInfoTrueDriveEmu.setText(game.getNeedsTruedriveEmuForDisplay());
      versionInfoLoadingScreen.setText(game.getHasLoadingScreenForDisplay());
      versionInfoIncludedDocs.setText(game.getHasIncludedDocsForDisplay());

      versionInfoComment.setText(game.getVersionComment() + " ");

      // personal info
      personalInfoTitle.setText(game.getName());
      personalInfoTitleLeft.setIcon(null);
      personalInfoTitleRight.setIcon(null);
      if (game.getIsClassic()) {
        personalInfoTitleLeft.setIcon(Const.ICON_GAMEINFO_CLASSIC);
        personalInfoTitleRight.setIcon(Const.ICON_GAMEINFO_CLASSIC);
      }

      personalInfoHighScore.setText(game.getHighscoreForDisplay());
      personalInfoDifficulty.setText(game.getDifficulty().getName());
      personalInfoTimesPlayed.setText(game.getTimesPlayedForDisplay());
      personalInfoLastPlayed.setText(game.getDateLastPlayedForDisplay());
      personalInfoRating.setText(game.getRatingForDisplay());

      // notes info
      notesInfoTitle.setText(game.getName());
      notesInfoTitleLeft.setIcon(null);
      notesInfoTitleRight.setIcon(null);
      if (game.getIsClassic()) {
        notesInfoTitleLeft.setIcon(Const.ICON_GAMEINFO_CLASSIC);
        notesInfoTitleRight.setIcon(Const.ICON_GAMEINFO_CLASSIC);
      }

      notesInfoNote.setText(game.getNote());
      if (!game.getNote().isEmpty()) {
        infoTabbedPane.setIconAt(3, Const.ICON_TAB_NOTES);
      }

      // music info
      musicInfoTitle.setText(game.getName());
      musicInfoTitleLeft.setIcon(null);
      musicInfoTitleRight.setIcon(null);
      if (game.getIsClassic()) {
        musicInfoTitleLeft.setIcon(Const.ICON_GAMEINFO_CLASSIC);
        musicInfoTitleRight.setIcon(Const.ICON_GAMEINFO_CLASSIC);
      }

      final Musician musician = game.getMusician();
      musicInfoMusician.setText(musician.getNameForDisplay());
      musicInfoNickname.setText(musician.getNicknameForDisplay());
      musicInfoGroup.setText(musician.getGroupForDisplay());
      musicInfoPhoto.setText(musician.getPhotoFilenameForDisplay());

      musicInfoName.setText("");
      musicInfoAuthor.setText("");
      musicInfoCopyright.setText("");
      musicInfoSongs.setText("");
      musicInfoDefault.setText("");

      MusicInfo musicInfo = null;

      try {
        if (Plugins.existsMusicInfoForExtension(FileTools.getExtension(game.getMusicFilename()))) {
          musicInfo = Plugins.getMusicInfoForExtension(FileTools.getExtension(game
              .getMusicFilename()));
          musicInfo.load(Paths.getMusicPath().find(new File(game.getMusicFilename())));
        }
      } catch (final IOException ioe) {
        musicInfo = null;
      }

      if (musicInfo != null) {
        musicInfoName.setText(musicInfo.getName());
        musicInfoAuthor.setText(musicInfo.getAuthor());
        musicInfoCopyright.setText(musicInfo.getCopyright());
        musicInfoSongs.setText(musicInfo.getSongNumber());
        musicInfoDefault.setText(musicInfo.getDefaultSong());
      }

      // status info
      if (!game.getFilename().isEmpty()) {
        status1.setText("[v" + ((game.getVersion() < 10) ? "0" : "") + game.getVersion() + "] "
            + game.getFilename() + " ");
      } else {
        status1.setText("None");
      }

      if (!game.getMusicFilename().isEmpty()) {
        status2.setText(game.getMusicFilename());
      } else {
        status2.setText("None");
      }

      // sound
      if (game.getIsClassic() && Preferences.is(Preferences.SOUND_ON_CLASSICS)) {
        if (Databases.getCurrent().hasClassicSound()) {
          // play classic sound of database
          Databases.getCurrent().getClassicSound().play();
        } else {
          // play default classic sound
          Const.SOUND_CLASSIC.play();
        }
      }

      // update sidePanel
      sidePanel.validate();

    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void sideButtonPanelResized() {
    int height = 54;

    sideButtonPanel.setPreferredSize(new Dimension(sideButtonPanel.getParent().getWidth(), height));
    sideButtonPanel.validate();

    if (sideButtonPanel.getComponentCount() > 0) {
      height += sideButtonPanel.getComponent(sideButtonPanel.getComponentCount() - 1).getY();
      sideButtonPanel.scrollRectToVisible(new Rectangle());
    }

    sideButtonPanel.setPreferredSize(new Dimension(sideButtonPanel.getParent().getWidth(), height));
    sideButtonPanel.validate();
  }

  private void displayMusic(final Music music) {
    if ((music == null) || (music.getName() == null)) {
      return;
    }

    final Musician musician = music.getMusician();

    // buttons
    filterPlayGameButton.setEnabled(false);
    filterPlayMusicButton.setEnabled(music.getFileExists());
    filterExtrasButton.setEnabled(false);
    filterAdditionalExtrasButton.setEnabled(false);

    // menus
    setGameMenusEnabled(false);

    // musician photo
    sidePanel_GameImage.setLayout(new BorderLayout());
    final JLabel photo = new JLabel(musician.getNameForDisplay(), musician.getPhoto(),
        SwingConstants.CENTER);
    photo.setHorizontalTextPosition(SwingConstants.CENTER);
    photo.setVerticalTextPosition(SwingConstants.BOTTOM);

    photo.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(final MouseEvent e) {
        final Musician musician = new MusicianDialog(music.getMusician()).get();
        if (musician != null) {
          Db.saveOrUpdate(musician);
          updateItemTable();
        }
      }
    });
    sidePanel_GameImage.add(photo, BorderLayout.CENTER);

    // icons
    gameInfoPrequel.setIcon(Const.ICON_GAMEINFO_NOPREQUEL);
    gameInfoSequel.setIcon(Const.ICON_GAMEINFO_NOSEQUEL);
    gameInfoRelated.setIcon(Const.ICON_GAMEINFO_NORELATED);
    gameInfoRating.setIcon(Const.ICONS_GAMEINFO_RATING[0]);

    gameInfoAdult.setIcon(Const.ICON_GAMEINFO_NOADULT);
    if (music.getIsAdult()) {
      gameInfoAdult.setIcon(Const.ICON_GAMEINFO_ADULT);
    }

    // game link menu
    editMenu_MarkGameForLinking.setEnabled(false);
    editMenu_SetGameLink.setEnabled(false);
    editMenu_ClearGameLink.setEnabled(false);

    // music info
    musicInfoTitle.setText(music.getName());
    musicInfoTitleLeft.setIcon(null);
    musicInfoTitleRight.setIcon(null);

    musicInfoMusician.setText(musician.getNameForDisplay());
    musicInfoNickname.setText(musician.getNicknameForDisplay());
    musicInfoGroup.setText(musician.getGroupForDisplay());
    musicInfoPhoto.setText(musician.getPhotoFilenameForDisplay());

    musicInfoName.setText("");
    musicInfoAuthor.setText("");
    musicInfoCopyright.setText("");
    musicInfoSongs.setText("");
    musicInfoDefault.setText("");

    MusicInfo musicInfo = null;

    try {
      if (Plugins.existsMusicInfoForExtension(FileTools.getExtension(music.getFilename()))) {
        musicInfo = Plugins.getMusicInfoForExtension(FileTools.getExtension(music.getFilename()));
        musicInfo.load(Paths.getMusicPath().find(new File(music.getFilename())));
      }
    } catch (final IOException ioe) {
      musicInfo = null;
    }

    if (musicInfo != null) {
      musicInfoName.setText(musicInfo.getName());
      musicInfoAuthor.setText(musicInfo.getAuthor());
      musicInfoCopyright.setText(musicInfo.getCopyright());
      musicInfoSongs.setText(musicInfo.getSongNumber());
      musicInfoDefault.setText(musicInfo.getDefaultSong());
    }

    // status info
    status1.setText(" ");
    status2.setText(music.getFilename() + " ");

    // update sidePanel
    sidePanel.validate();
  }

  protected void setGameMenusEnabled(final boolean b) {
    editMenu_VersionInfoMenu.setEnabled(b);
    editMenu_PersonalInfoMenu.setEnabled(b);
    editMenu_GameFile.setEnabled(b);
    editMenu_Screenshots.setEnabled(b);
    editMenu_Extras.setEnabled(b);
    editMenu_AdditionalExtras.setEnabled(b);
    editMenu_KeyValuePairs.setEnabled(b);
    editMenu_MarkGameForLinking.setEnabled(b);
    editMenu_SetGameLink.setEnabled(b);
    editMenu_ClearGameLink.setEnabled(b);
  }

  @Override
  public void selectItem(String idString, final boolean displayWarnings) {
    int id, row;
    boolean found;
    final ItemTableModel data = ((ItemTableModel) itemTable.getModel());

    idString = idString.toUpperCase();

    if (idString.equals("INVALID")) {
      return;
    }

    if (idString.isEmpty()) {
      // select first
      data.setSelectedRow(0);
      return;
    }

    try {
      id = Integer.parseInt(idString.substring(1));
    } catch (final Exception e) {
      if (displayWarnings) {
        JOptionPane.showMessageDialog(null, "Invalid item id specified!", "jGameBase",
            JOptionPane.WARNING_MESSAGE);
      }
      return;
    }

    found = false;
    row = 0;
    for (final Iterator<Item> iter = data.iterator(); iter.hasNext() && !found; row++) {
      final Item item = iter.next();

      if ((item.getId() == id)
          && (((item instanceof Game) && idString.startsWith("G")) || ((item instanceof Music) && idString
              .startsWith("M")))) {
        found = true;
      }
    }

    if (found) {
      data.setSelectedRow(row - 1);
    } else {

      if (displayWarnings) {
        // game not found => switch to viel All Games?
        if ((idString.startsWith("G") && (filterViewCombobox.getSelectedIndex() != Gui.VIEW_ALL_GAMES))) {
          final int answer = JOptionPane
              .showConfirmDialog(
                  this,
                  "The Game you requested was not found in the current view!\n\n"
                      + "Do you want me to change to the '<All Games>' view so that I can show you the Game?",
                  "jGameBase", JOptionPane.YES_NO_OPTION);

          if (answer == JOptionPane.YES_OPTION) {
            filterViewCombobox.setSelectedIndex(Gui.VIEW_ALL_GAMES);
            data.setView(views.get(Gui.VIEW_ALL_GAMES));
            selectItem(idString, displayWarnings);
          }
          return;

          // music not found => switch to viel All Music?
        } else if (idString.startsWith("M")
            && (filterViewCombobox.getSelectedIndex() != Gui.VIEW_ALL_MUSIC)) {
          final int answer = JOptionPane
              .showConfirmDialog(
                  this,
                  "The Music you requested was not found in the current view!\n\n"
                      + "Do you want me to change to the '<All Music>' view so that I can show you the Music?",
                  "jGameBase", JOptionPane.YES_NO_OPTION);

          if (answer == JOptionPane.YES_OPTION) {
            filterViewCombobox.setSelectedIndex(Gui.VIEW_ALL_MUSIC);
            data.setView(views.get(Gui.VIEW_ALL_MUSIC));
            selectItem(idString, displayWarnings);
          }
          return;
        }

        JOptionPane.showMessageDialog(this, "The Game/Music was not found in the list!",
            "jGameBase", JOptionPane.WARNING_MESSAGE);

      } else { // display no warnings
        // select first
        data.setSelectedRow(0);
      }

    }
  }

  @Override
  protected void addView() {
    final ItemView view = new ItemView();
    view.setName("New View");
    view.setType(ItemView.TYPE_NORMAL);
    view.setMode(ItemView.MODE_AND);
    view.setInclude(ItemView.INCLUDE_GAMES);
    view.setFilters(new HashSet<ItemViewFilter>()); // no filters

    final ViewDialog viewDialog = new ViewDialog(view, "Add Custom View");
    if (viewDialog.getCloseAction() == CloseAction.OK) {
      final ItemView newView = viewDialog.getView();

      // copy values to original object so hibernate doesn't get confused
      view.setName(newView.getName());
      view.setMode(newView.getMode());
      view.setInclude(newView.getInclude());
      view.setFilters(newView.getFilters());
      newView.setFilters(null);

      views.add(view);
      Collections.sort(views);
      filterViewCombobox.setModel(new DefaultComboBoxModel(views.toArray()));
      filterViewCombobox.setSelectedItem(view);
      Db.saveOrUpdate(view);
      ((ItemTableModel) itemTable.getModel()).setView(view);
      ((ItemTableModel) itemTable.getModel()).reloadView();

    }
    viewDialog.dispose();
  }

  @Override
  protected void editView() {
    final ItemView view = (ItemView) filterViewCombobox.getSelectedItem();
    final int row = filterViewCombobox.getSelectedIndex();

    if (view.getType() != ItemView.TYPE_NORMAL) {
      return;
    }

    final ViewDialog viewDialog = new ViewDialog(view, "Edit Custom View");
    if (viewDialog.getCloseAction() == CloseAction.OK) {
      final ItemView newView = viewDialog.getView();

      // copy values to original object so hibernate doesn't get confused
      view.setName(newView.getName());
      view.setMode(newView.getMode());
      view.setInclude(newView.getInclude());
      view.setFilters(newView.getFilters());
      newView.setFilters(null);

      views.set(row, view);
      filterViewCombobox.setModel(new DefaultComboBoxModel(views.toArray()));
      filterViewCombobox.setSelectedIndex(row);

      Db.saveOrUpdate(view);
      ((ItemTableModel) itemTable.getModel()).setView(view);
      ((ItemTableModel) itemTable.getModel()).reloadView();
    }
    viewDialog.dispose();
  }

  @Override
  protected void removeView() {
    ItemView view = (ItemView) filterViewCombobox.getSelectedItem();
    if (view.getType() == ItemView.TYPE_NORMAL) {
      Db.delete(view);
      views.remove(view);
      filterViewCombobox.setModel(new DefaultComboBoxModel(views.toArray()));

      // show new selected view
      view = (ItemView) filterViewCombobox.getSelectedItem();
      ((ItemTableModel) itemTable.getModel()).setView(view);
    }
  }

  @Override
  protected boolean clicked(final java.awt.event.MouseEvent evt) {
    return ((evt.getButton() == MouseEvent.BUTTON1) && (evt.getClickCount() == 1));
  }

  @Override
  protected synchronized Item getSelectedItem() {
    final ItemTableModel data = (ItemTableModel) itemTable.getModel();
    return data.getItem(data.getSelectedRow());
  }

  @Override
  protected Game getSelectedGame() {
    if (isSelectedItemOfTypeGame()) {
      return (Game) getSelectedItem();
    }
    return null;
  }

  @Override
  protected Music getSelectedMusic() {
    if (isSelectedItemOfTypeMusic()) {
      return (Music) getSelectedItem();
    }
    return null;
  }

  @Override
  protected ItemView createQuickview(final String name, final int include) {
    final ItemView view = new ItemView();
    final ItemView currentView = (ItemView) filterViewCombobox.getSelectedItem();
    view.setName("[" + name + "]");
    view.setInclude(include);
    view.setType(ItemView.TYPE_QUICK);
    view.setSortColumn(currentView.getSortColumn());
    view.setSortOrder(currentView.getSortOrder());
    // view.setWidthOfColumn0(currentView.getWidthOfColumn0());
    // view.setWidthOfColumn1(currentView.getWidthOfColumn1());
    // view.setInfoColumnMajor(currentView.getInfoColumnMajor());
    // view.setInfoColumnMinor(currentView.getInfoColumnMinor());
    return view;
  }

  @Override
  protected void setQuickview(final ItemView view) {
    int row = -1;

    // search for an already existing quickview
    int i = 0;
    for (final Iterator<ItemView> iter = views.iterator(); iter.hasNext(); i++) {
      final ItemView viewInList = iter.next();
      if (viewInList.getType() == ItemView.TYPE_QUICK) {
        row = i;
      }
    }

    if (row == -1) { // no quickview found => add view
      views.add(view);
    } else { // replace existing quickview
      views.set(row, view);
    }

    filterViewCombobox.setModel(new DefaultComboBoxModel(views.toArray()));
    filterViewCombobox.setSelectedItem(view);
    ((ItemTableModel) itemTable.getModel()).setView(view);

    if (getSelectedItem() != null) {
      selectItem(getSelectedItem().getStringId(), false);
    }
  }

  @Override
  protected void editGameMusicInfo_Current() {
    saveItemTablePosition();
    if (isSelectedItemOfTypeGame()) {
      new GameInfoDialog(getSelectedGame());
    } else {
      new MusicInfoDialog(getSelectedMusic());
    }
    displaySelectedItem(false);
    loadItemTablePosition();
  }

  @Override
  protected void editGameMusicInfo_All() {
    saveItemTablePosition();
    if (isSelectedItemOfTypeGame()) {
      new GameInfoDialog(((ItemView) filterViewCombobox.getSelectedItem()).getData());
    } else {
      new MusicInfoDialog(((ItemView) filterViewCombobox.getSelectedItem()).getData());
    }
    displaySelectedItem(false);
    loadItemTablePosition();
  }

  protected boolean isSelectedItemOfTypeGame() {
    if ((getSelectedItem() != null) && (getSelectedItem() instanceof Game)) {
      return true;
    }
    return false;
  }

  protected boolean isSelectedItemOfTypeMusic() {
    if ((getSelectedItem() != null) && (getSelectedItem() instanceof Music)) {
      return true;
    }
    return false;
  }

  @Override
  protected void toggleIsFavourite() {
    final Item item = getSelectedItem();
    if (item != null) {
      item.setIsFavourite(!item.getIsFavourite());
      Db.saveOrUpdate(item);
      // clear cache of favourites view
      (views.get(Gui.VIEW_FAVOURITES)).clearCache();
      // update
      updateItemTable();
    }
  }

  @Override
  protected void editVersionInfo_Current() {
    saveItemTablePosition();
    new VersionInfoDialog(getSelectedGame());
    loadItemTablePosition();
  }

  @Override
  protected void editVersionInfo_All() {
    saveItemTablePosition();
    new VersionInfoDialog(((ItemView) filterViewCombobox.getSelectedItem()).getData());
    loadItemTablePosition();
  }

  @Override
  protected void editPersonalInfo_Current() {
    saveItemTablePosition();
    new PersonalInfoDialog(getSelectedGame());
    loadItemTablePosition();
  }

  @Override
  protected void editPersonalInfo_All() {
    saveItemTablePosition();
    new PersonalInfoDialog(((ItemView) filterViewCombobox.getSelectedItem()).getData());
    loadItemTablePosition();
  }

  private void saveItemTablePosition() {
    itemTableSavedPosition_Rect = itemTable.getVisibleRect();
    itemTableSavedPosition_Row = itemTable.getSelectedRow();

    if ((itemTableSavedPosition_Row < 0) || (itemTableSavedPosition_Row > itemTable.getRowCount())) {
      itemTableSavedPosition_Row = 0;

      if (itemTable.getRowCount() > 0) {
        itemTable.setRowSelectionInterval(0, 0);
      }
    }
  }

  private void loadItemTablePosition() {
    try {
      ((ItemTableModel) itemTable.getModel()).fireTableDataChanged();
      itemTable.scrollRectToVisible(itemTableSavedPosition_Rect);
      if (itemTableSavedPosition_Row < itemTable.getRowCount()) {
        itemTable.setRowSelectionInterval(itemTableSavedPosition_Row, itemTableSavedPosition_Row);
      }
      displaySelectedItem(false);
    } catch (final Exception e) {
    }
  }

  @Override
  protected void displaySelectedItem(final boolean forceUpdate) {
    if (forceUpdate) {
      lastItem = null;
    }
    displayItem(getSelectedItem());
  }

  @Override
  protected void updateItemTable() {
    saveItemTablePosition();
    displaySelectedItem(false);
    loadItemTablePosition();
  }

  @Override
  protected void togglePlaySoundOnClassics() {
    Preferences.set(Preferences.SOUND_ON_CLASSICS, !Preferences.is(Preferences.SOUND_ON_CLASSICS));
    toolsMenu_PlaySoundOnClassics.setSelected(Preferences.is(Preferences.SOUND_ON_CLASSICS));
    status5.setIcon(Preferences.is(Preferences.SOUND_ON_CLASSICS) ? Const.ICON_SOUND_ON
        : Const.ICON_SOUND_OFF);
  }

  @Override
  protected void toggleHardwareJoystick() {
    Preferences.set(Preferences.HARDWARE_JOYSTICK, !Preferences.is(Preferences.HARDWARE_JOYSTICK));
    toolsMenu_HardwareJoystick.setSelected(Preferences.is(Preferences.HARDWARE_JOYSTICK));
    status6.setIcon(Preferences.is(Preferences.HARDWARE_JOYSTICK) ? Const.ICON_JOYSTICK_ON
        : Const.ICON_JOYSTICK_OFF);
  }

  @Override
  protected void toggleAdultFilter() {
    if (Preferences.is(Preferences.ADULT_FILTER)) {
      // turn off
      final long pw = FileTools.checksum((String) JOptionPane.showInputDialog(this,
          "Please enter the current Password:", "Enter Password", JOptionPane.QUESTION_MESSAGE,
          null, null, ""));

      if (pw == Preferences.getLong(Preferences.ADULT_FILTER_PW)) {
        // corect password
        Preferences.set(Preferences.ADULT_FILTER, false);
        Preferences.set(Preferences.ADULT_FILTER_PW, "");
      } else {
        // wrong password
        Gui.displayErrorDialog("Wrong password!");
      }
    } else {
      // turn on
      final long pw1 = FileTools.checksum((String) JOptionPane.showInputDialog(this,
          "Please enter a new Password:", "Enter Password", JOptionPane.QUESTION_MESSAGE, null,
          null, ""));

      final long pw2 = FileTools.checksum((String) JOptionPane.showInputDialog(this,
          "Please repeat the Password:", "Enter Password", JOptionPane.QUESTION_MESSAGE, null,
          null, ""));

      if (pw1 == pw2) {
        // identical passwords
        Preferences.set(Preferences.ADULT_FILTER, true);
        Preferences.set(Preferences.ADULT_FILTER_PW, pw1);
      } else {
        // different passwords
        Gui.displayWarningDialog("Specified passwords were not identical!");
      }
    }

    toolsMenu_AdultFilter.setSelected(Preferences.is(Preferences.ADULT_FILTER));

    clearAllViewCaches();
    reloadCurrentView();
  }

  @Override
  protected void importFavourites() {
    final JFileChooser fileChooser = new JFileChooser();
    File file;
    BufferedReader in = null;
    final List<Item> list = new ArrayList<Item>();

    fileChooser.setFileFilter(csvFileFilter);
    fileChooser.setSelectedFile(new File("GameBase_Favourites_" + Databases.getCurrent().getName()
        + ".csv"));

    if (fileChooser.showOpenDialog(null) == javax.swing.JFileChooser.APPROVE_OPTION) {
      file = fileChooser.getSelectedFile();

      try {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        log.info("Marking the following items as favourites:");

        String line;

        in = new BufferedReader(new FileReader(file));
        while ((line = in.readLine()) != null) {
          int index = 0;

          // ";" or "," whatever comes first
          if ((line.indexOf(',') != -1) && (line.indexOf(';') != -1)) {
            index = Math.min(line.indexOf(','), line.indexOf(';'));
          } else if (line.indexOf(',') != -1) {
            index = line.indexOf(',');
          } else if (line.indexOf(';') != -1) {
            index = line.indexOf(';');
          }

          String id = line.substring(0, index);

          if (id.startsWith("\"") && id.endsWith("\"")) {
            id = id.substring(1, id.length() - 1);
          }

          final Item item = Db.getItembyId(id);

          if (item != null) {
            log.info("  marking item with id='" + item.getStringId() + "' and name='"
                + item.getName() + "'...");
            item.setIsFavourite(true);
            list.add(item);
          }
        }

        Db.saveOrUpdateAll(list);

      } catch (final IOException e) {
        e.printStackTrace();
      } finally {
        if (in != null) {
          try {
            in.close();
          } catch (final IOException e) {
          }
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        log.info("Finished marking items as favourite.");
      }
    }

  }

  @Override
  protected void importDbFromCSV() {
    clearAllViewCaches();

    try {
      Import.csv2Db();
      Gui.displayInformationDialog("Import of current database from directory\n'"
          + new File(Databases.getCurrent().getPath(), Const.EXPORT_DIRNAME)
          + "'\nsuccessfully finished.");
    } catch (final Exception e) {
      e.printStackTrace();
      Gui.displayErrorDialog(e.getMessage());
    }

    JGameBase.getGui().initViews();
    ((ItemTableModel) itemTable.getModel()).setView(views.get(Gui.VIEW_ALL_GAMES));
  }

  @Override
  protected void exportFavourites() {
    final JFileChooser fileChooser = new JFileChooser();
    File file;
    ItemView view;
    List<Item> list;
    PrintWriter out = null;

    fileChooser.setFileFilter(csvFileFilter);
    fileChooser.setSelectedFile(new File("GameBase_Favourites_" + Databases.getCurrent().getName()
        + ".csv"));

    if (fileChooser.showSaveDialog(null) == javax.swing.JFileChooser.APPROVE_OPTION) {
      file = fileChooser.getSelectedFile();

      try {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        out = new PrintWriter(file);

        // if file exists display warning dialog
        if (!file.exists()
            || (JOptionPane.showConfirmDialog(null, "Favourites file '" + file.getName()
                + "' exists.\nOverwrite?", "Warning", JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION)) {

          // iterate over all games
          view = views.get(Gui.VIEW_ALL_GAMES);
          list = view.getData();
          for (final Item item : list) {
            final Game game = (Game) item;
            if (game.getIsFavourite()) {
              out.println("G" + game.getId() + ",\"" + game.getName() + "\"");
            }
          }

          // iterate over all music
          view = views.get(Gui.VIEW_ALL_MUSIC);
          list = view.getData();
          for (final Item item : list) {
            if (item instanceof Music) {
              final Music music = (Music) item;
              if (music.getIsFavourite()) {
                out.println("M" + music.getId() + ",\"" + music.getName() + "\"");
              }
            }
          }
        }
      } catch (final FileNotFoundException e) {
        e.printStackTrace();
      } finally {
        if (out != null) {
          out.close();
        }
        JGameBase.getGui().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      }

    }
  }

  @Override
  protected void exportUsedFiles() {
    final String extension = "ufl"; // Used File List

    final File path = Databases.getCurrent().getPath();
    final File gamesFile = new File(path, "games." + extension);
    final File extrasFile = new File(path, "extras." + extension);
    final File musicFile = new File(path, "music." + extension);
    final File photosFile = new File(path, "photos." + extension);
    final File screenshotsFile = new File(path, "screenshots." + extension);

    ItemView view;
    List<Item> list;

    final Set<String> gamesFilenames = new TreeSet<String>();
    final Set<String> extrasFilenames = new TreeSet<String>();
    final Set<String> musicFilenames = new TreeSet<String>();
    final Set<String> photosFilenames = new TreeSet<String>();
    final Set<String> screenshotsFilenames = new TreeSet<String>();

    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    log.info("Exporting list of used files...");
    // delete old files
    gamesFile.delete();
    extrasFile.delete();
    musicFile.delete();
    photosFile.delete();
    screenshotsFile.delete();

    // iterate over all games
    view = views.get(Gui.VIEW_ALL_GAMES);
    list = view.getData();
    for (final Item item : list) {
      final Game game = (Game) item;

      if (!game.getFilename().isEmpty()) {
        gamesFilenames.add(game.getFilename());
      }

      if (!game.getMusicFilename().isEmpty()) {
        musicFilenames.add(game.getMusicFilename());
      }

      if (!game.getMusician().getPhotoFilename().isEmpty()) {
        photosFilenames.add(game.getMusician().getPhotoFilename());
      }

      if (!game.getScreenshotFilename().isEmpty()) {
        screenshotsFilenames.add(game.getScreenshotFilename());
      }

      final List<Extra> extras = game.getExtras();
      for (final Extra extra : extras) {
        if (!extra.getFilename().isEmpty()) {
          extrasFilenames.add(extra.getFilename());
        }
      }
    }

    // iterate over all music
    view = views.get(Gui.VIEW_ALL_MUSIC);
    list = view.getData();
    for (final Item item : list) {
      if (item instanceof Music) {
        final Music music = (Music) item;

        if (!music.getFilename().isEmpty()) {
          musicFilenames.add(music.getFilename());
        }

        if (!music.getMusician().getPhotoFilename().isEmpty()) {
          photosFilenames.add(music.getMusician().getPhotoFilename());
        }
      }
    }

    // write to disk
    writeUsedFilesToFile(gamesFile, gamesFilenames);
    writeUsedFilesToFile(extrasFile, extrasFilenames);
    writeUsedFilesToFile(musicFile, musicFilenames);
    writeUsedFilesToFile(photosFile, photosFilenames);
    writeUsedFilesToFile(screenshotsFile, screenshotsFilenames);

    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    log.info("Finished exporting list of used files.");
  }

  @Override
  protected void exportDbToCSV() {
    try {
      Export.db2Csv(Table.getNames());
      Gui.displayInformationDialog("Export of current database to directory\n'"
          + new File(Databases.getCurrent().getPath(), Const.EXPORT_DIRNAME)
          + "'\nsuccessfully finished.");
    } catch (final Exception e) {
      e.printStackTrace();
      Gui.displayErrorDialog(e.getMessage());
    }
  }

  @Override
  protected void exportDbToMdb() {
    try {
      Export.db2Mdb();
      Gui.displayInformationDialog("Export of current database to mdb-file\n'"
          + new File(new File(Databases.getCurrent().getPath(), Const.EXPORT_DIRNAME), Databases
              .getCurrent().getName() + ".mdb") + "'\nsuccessfully finished.");
    } catch (final Exception e) {
      e.printStackTrace();
      Gui.displayErrorDialog(e.getMessage());
    }
  }

  private void writeUsedFilesToFile(final File file, final Set<String> filenames) {
    PrintWriter out = null;

    if (filenames.size() == 0) {
      return;
    }

    try {
      log.info("  writing to '" + file + "'.");
      out = new PrintWriter(file);

      for (final String filename : filenames) {
        out.println(filename);
      }

    } catch (final FileNotFoundException e) {
      e.printStackTrace();
    } finally {
      if (out != null) {
        out.close();
      }
    }

  }

  @Override
  protected JPopupMenu createPlayGamePopupMenu() {
    final JPopupMenu menu = new JPopupMenu();
    JMenuItem menuItem;

    if (getSelectedGame() != null) {
      final List<Emulator> emulators = Emulators.getGameEmulators();

      if ((emulators != null) && (emulators.size() > 0)
          && (isSelectedItemOfTypeGame() && getSelectedGame().getGameFileExists())) {

        // iterate over emulators
        for (final Emulator emulator : emulators) {
          menuItem = new JMenuItem();

          menuItem.setText(emulator.getName());
          menuItem.setActionCommand(Integer.toString(emulators.indexOf(emulator)));
          menuItem.addActionListener(new java.awt.event.ActionListener() {
            // play
            @Override
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
              final String command = evt.getActionCommand();
              if ((command == null) || (command.isEmpty())) {
                return;
              }

              final Game game = getSelectedGame();
              if (game == null) {
                return;
              }

              Emulator emulator = null;
              try {
                emulator = Emulators.getGameEmulators().get(Integer.parseInt(command));
              } catch (final NumberFormatException e) {
              }
              if (emulator == null) {
                return;
              }

              game.play(emulator);
            }
          });
          menu.add(menuItem);
        }

        menu.add(new JSeparator());
      }
    }

    menuItem = new JMenuItem("<html><b>Manage</b></html>");
    menuItem.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        manageGameEmulators();
      }
    });
    menu.add(menuItem);

    return menu;
  }

  @Override
  protected JPopupMenu createPlayMusicPopupMenu() {
    final JPopupMenu menu = new JPopupMenu();
    JMenuItem menuItem;

    if (getSelectedItem() != null) {
      final List<Emulator> emulators = Emulators.getMusicEmulators();

      if ((emulators != null)
          && (emulators.size() > 0)
          && ((isSelectedItemOfTypeGame() && getSelectedGame().getMusicFileExists()) || (isSelectedItemOfTypeMusic() && getSelectedMusic()
              .getFileExists()))) {

        // iterate over emulators
        for (final Emulator emulator : emulators) {
          menuItem = new JMenuItem();

          menuItem.setText(emulator.getName());
          menuItem.setActionCommand(Integer.toString(emulators.indexOf(emulator)));
          menuItem.addActionListener(new java.awt.event.ActionListener() {
            // play
            @Override
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
              final String command = evt.getActionCommand();
              if ((command == null) || (command.isEmpty())) {
                return;
              }

              Emulator emulator = null;
              try {
                emulator = Emulators.getMusicEmulators().get(Integer.parseInt(command));
              } catch (final NumberFormatException e) {
              }
              if (emulator == null) {
                return;
              }

              playSelectedItemAsMusic(emulator);
            }
          });
          menu.add(menuItem);
        }

        menu.add(new JSeparator());
      }
    }

    menuItem = new JMenuItem("<html><b>Manage</b></html>");
    menuItem.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        manageMusicEmulators();
      }
    });
    menu.add(menuItem);

    return menu;
  }

  @Override
  @SuppressWarnings("unchecked")
  protected JPopupMenu createExtrasButtonPopupMenu() {
    final JPopupMenu menu = new JPopupMenu();
    JMenuItem menuItem;

    final Game game = getSelectedGame();
    if (game != null) {
      final List<Extra> extras = game.getExtras();

      if ((extras != null) && (extras.size() > 0)) {

        // iterate over extras
        for (final Extra extra : extras) {
          menuItem = new JMenuItem();
          String name = extra.getName();
          // additional extra?, mark with "+"
          if (extra.isAdditional()) {
            name = "+ " + name;
          }

          final String filename = ((Paths.getExtraPath().find(new File(extra.getFilename())) != null) ? Paths
              .getExtraPath().find(new File(extra.getFilename())).getPath()
              : "");

          // no URL and file not found?, mark with "(!)"
          if ((!extra.isUrl()) && (filename.isEmpty())) {
            name += " (!)";
          }

          menuItem.setText(name);
          menuItem.setActionCommand(Integer.toString(extra.getId()));
          menuItem.addActionListener(new java.awt.event.ActionListener() {
            // open extra
            @Override
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
              final String command = evt.getActionCommand();

              if ((command != null) && (!command.isEmpty())) {
                try {
                  final int id = Integer.parseInt(command);
                  final Extra extra = Db.getExtraById(id);
                  if (extra != null) {
                    extra.play();
                  }
                } catch (final NumberFormatException e) {
                  e.printStackTrace();
                }
              }
            }
          });
          menu.add(menuItem);
        }

        menu.add(new JSeparator());
      }
    }

    menuItem = new JMenuItem("<html><b>Manage</b></html>");
    menuItem.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        editExtrasFile();
      }
    });
    menu.add(menuItem);

    return menu;
  }

  protected JPopupMenu createButtonsForExtrasPopupMenu(final Extra extra) {
    final JPopupMenu menu = new JPopupMenu();
    JMenuItem menuItem;

    // URL
    if (extra.isUrl()) {
      // Download
      menuItem = new JMenuItem("Download to Additional Extras Directory");
      menuItem.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(final java.awt.event.ActionEvent evt) {
          extra.download();

          final Game game = Db.getGameById(extra.getGameId());
          game.syncAdditionalExtras(false);

          displaySelectedItem(true);

        }
      });
      menu.setEnabled(DownloadTools.isDownloadableFiletype(extra.getFilename()));
      menu.add(menuItem);

      menu.add(new JSeparator());

      // copy
      menuItem = new JMenuItem("Copy URL (mark for copying)");
      menuItem.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(final java.awt.event.ActionEvent evt) {
          copyExtra = extra;
          status4.setText(extra.getName());
        }
      });
      menu.add(menuItem);

      // move
      menuItem = new JMenuItem("Move URL to marked Game");
      menuItem.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(final java.awt.event.ActionEvent evt) {
          final int gameId = extra.getGameId();
          if ((gameId > 0) && (linkGame != null)) {
            final Game srcGame = Db.getGameById(gameId);
            final Game dstGame = linkGame;

            srcGame.getExtras().remove(extra);
            extra.setGameId(dstGame.getId());
            extra.setName(dstGame.getName() + " - " + extra.getCategory().getDir());
            dstGame.getExtras().add(extra);

            Db.saveOrUpdate(srcGame);
            Db.saveOrUpdate(dstGame);

            displaySelectedItem(true);
          }
        }
      });
      menuItem.setEnabled(linkGame != null);
      menu.add(menuItem);

      menu.add(new JSeparator());

      // delete
      menuItem = new JMenuItem("<html><i>Remove URL</i></html>");
      menuItem.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(final java.awt.event.ActionEvent evt) {

          final int gameId = extra.getGameId();
          if (gameId > 0) {
            final Game game = Db.getGameById(gameId);

            extra.setGameId(0);
            game.getExtras().remove(extra);
            Db.delete(extra);
            Db.saveOrUpdate(game);

            displaySelectedItem(true);
          }
        }
      });
      menu.add(menuItem);

      menu.add(new JSeparator());

      // properties
      menuItem = new JMenuItem("<html><b>Properties</b></html>");
      menuItem.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(final java.awt.event.ActionEvent evt) {

          final int gameId = extra.getGameId();
          if (gameId > 0) {
            final Game game = Db.getGameById(gameId);

            final UrlDialog urlDialog = new UrlDialog(extra.getCategory().getDir(), extra
                .getFilename());
            if (urlDialog.getCloseAction() == CloseAction.OK) {
              if ((!urlDialog.getCategoryDir().equals(extra.getCategory().getDir()))
                  || (!urlDialog.getUrl().equals(extra.getFilename()))) {
                extra.setName(game.getName() + " - " + urlDialog.getCategoryDir());
                extra.reevaluateCategory();
                extra.setFilename(urlDialog.getUrl());
                Db.saveOrUpdate(game);
                displaySelectedItem(true);
              }
            }
            urlDialog.dispose();

          }
        }
      });
      menu.add(menuItem);

    } else if (extra.isAdditional()
        && Plugins.existsExtractorForExtension(FileTools.getExtension(extra.getFilename()))) {
      // Extract
      menuItem = new JMenuItem("Extract to Additional Extras Directory");
      menuItem.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(final java.awt.event.ActionEvent evt) {

          final File extraFile = Paths.getExtraPath().findAndWarn(new File(extra.getFilename()));

          if (extraFile == null) {
            return;
          }

          final String extraFilename = extraFile.toString();

          if (Plugins.existsExtractorForExtension(FileTools.getExtension(extraFilename))) {
            log.info("extracting to temp");

            // extract
            final Extractor extractor = Plugins.getExtractorForExtension(FileTools
                .getExtension(extraFilename));
            try {
              final List<String> filenames = extractor.extractToCleanTempDir(extraFilename);

              final File targetDir = new File(Paths.getAdditionalExtraPathId(extra.getGameId()),
                  extra.getCategory().getDir());

              for (final String filename : filenames) {
                final File src = new File(filename);
                final File dst = new File(targetDir, src.getName());
                log.info("Moving '" + src + "' to '" + dst + "'");
                FileTools.copyFile(src, dst);
              }

              // make sure to only delete additional extras
              if (extra.isAdditional()) {
                extraFile.delete();
              }

              final Game game = Db.getGameById(extra.getGameId());
              game.syncAdditionalExtras(false);

              displaySelectedItem(true);

            } catch (final IOException e) {
              e.printStackTrace();
            }
          }

        }
      });
      menu.add(menuItem);
    }

    return menu;
  }

  @Override
  protected JPopupMenu createSideButtonPanelPopupMenu() {

    final JPopupMenu menu = new JPopupMenu();
    JMenuItem menuItem;

    final Game game = getSelectedGame();
    if (game != null) {
      // add new url
      menuItem = new JMenuItem("Add new URL");
      menuItem.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(final java.awt.event.ActionEvent evt) {
          final Game game = getSelectedGame();
          if (game != null) {
            final UrlDialog urlDialog = new UrlDialog();
            if (urlDialog.getCloseAction() == CloseAction.OK) {
              final Extra extra = new Extra();
              extra.setGameId(game.getId());
              extra.setName(game.getName() + " - " + urlDialog.getCategoryDir());
              extra.setFilename(urlDialog.getUrl());
              game.getExtras().add(extra);
              Db.saveOrUpdate(game);
              displaySelectedItem(true);
            }
            urlDialog.dispose();
          }
        }
      });
      menu.add(menuItem);

      menu.add(new JSeparator());

      // paste url
      menuItem = new JMenuItem("Paste marked URL");
      menuItem.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(final java.awt.event.ActionEvent evt) {
          final Game game = getSelectedGame();
          if ((game != null) && (copyExtra != null)) {

            for (final Extra existingExtra : new CopyOnWriteArrayList<Extra>(game.getExtras())) {
              // an extra with this URL already exists, so delete it
              if (existingExtra.getFilename().equalsIgnoreCase(copyExtra.getFilename())) {
                existingExtra.setGameId(0);
                game.getExtras().remove(existingExtra);
                Db.saveOrUpdate(game);
                Db.delete(existingExtra);
              }
            }

            final Extra extra = new Extra();
            extra.setGameId(game.getId());
            extra.setName(game.getName() + " - " + copyExtra.getCategory().getDir());
            extra.setFilename(copyExtra.getFilename());
            game.getExtras().add(extra);
            Db.saveOrUpdate(game);
            displaySelectedItem(true);
          }
        }
      });
      // only enabled if something to paste
      menuItem.setEnabled(copyExtra != null);
      menu.add(menuItem);

      // paste all urls
      menuItem = new JMenuItem("Paste all URLs of selected Game");
      menuItem.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(final java.awt.event.ActionEvent evt) {
          final Game game = getSelectedGame();
          if ((game != null) && (linkGame != null) && (game.getId() != linkGame.getId())) {

            for (final Extra linkExtra : linkGame.getExtras()) {

              if (linkExtra.isUrl()
                  && !linkExtra.getFilename().toLowerCase()
                      .startsWith("http://www.gamebase64.com/game.php")) {
                // remove all URLs identical to the new one
                for (final Extra existingExtra : new CopyOnWriteArrayList<Extra>(game.getExtras())) {
                  if (existingExtra.isUrl()
                      && existingExtra.getFilename().equalsIgnoreCase(linkExtra.getFilename())) {
                    existingExtra.setGameId(0);
                    game.getExtras().remove(existingExtra);
                    Db.saveOrUpdate(game);
                    Db.delete(existingExtra);
                  }
                }
                // add new extra
                final Extra extra = new Extra();
                extra.setGameId(game.getId());
                extra.setName(game.getName() + " - " + linkExtra.getCategory().getDir());
                extra.setFilename(linkExtra.getFilename());
                game.getExtras().add(extra);
              }

            }
            Db.saveOrUpdate(game);

            displaySelectedItem(true);
          }
        }
      });
      // only enabled if something to paste
      menuItem.setEnabled((linkGame != null) && (getSelectedGame() != null)
          && (linkGame.getId() != getSelectedGame().getId()));
      menu.add(menuItem);

      menu.add(new JSeparator());

      // delete URLs identical to marked Game
      menuItem = new JMenuItem("<html><i>Remove URLs identical to marked Game's URLs</i></html>");
      menuItem.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(final java.awt.event.ActionEvent evt) {

          final Game game = getSelectedGame();
          // don't remove if game and linked game are the same
          if ((game != null) && (linkGame != null) && (game.getId() != linkGame.getId())) {

            for (final Extra extra : new CopyOnWriteArrayList<Extra>(game.getExtras())) {
              for (final Extra linkExtra : linkGame.getExtras()) {
                if (extra.isUrl() && linkExtra.isUrl()
                    && extra.getFilename().equalsIgnoreCase(linkExtra.getFilename())) {
                  extra.setGameId(0);
                  game.getExtras().remove(extra);
                  Db.saveOrUpdate(game);
                  Db.delete(extra);
                }
              }
            }

            displaySelectedItem(true);
          }
        }
      });
      menuItem.setEnabled((linkGame != null) && (getSelectedGame() != null)
          && (linkGame.getId() != getSelectedGame().getId()));
      menu.add(menuItem);

      // delete ALL urls
      if (JGameBase.option_dangerous) {
        menuItem = new JMenuItem("<html><i>Remove ALL URLs</i></html>");
        menuItem.addActionListener(new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(final java.awt.event.ActionEvent evt) {

            final Game game = getSelectedGame();
            if (game != null) {

              for (final Extra extra : new CopyOnWriteArrayList<Extra>(game.getExtras())) {
                // don't delete gamebase64 urls
                if (extra.isUrl()
                    && !extra.getFilename().toLowerCase()
                        .startsWith("http://www.gamebase64.com/game.php")) {
                  extra.setGameId(0);
                  game.getExtras().remove(extra);
                  Db.saveOrUpdate(game);
                  Db.delete(extra);
                }
              }

              displaySelectedItem(true);
            }
          }
        });
        menu.add(menuItem);
      }

    }

    return menu;
  }

  protected synchronized void renameItem() {
    final Item item = getSelectedItem();
    item.setName((String) JOptionPane.showInputDialog(this, "Name:", "Add Game...",
        JOptionPane.PLAIN_MESSAGE, null, null, item.getName()));
    Db.saveOrUpdate(item);
  }

  @Override
  protected JPopupMenu createItemTablePopupMenu() {
    final JPopupMenu menu = new JPopupMenu();
    JMenuItem menuItem;

    menuItem = new JMenuItem("Rename");
    menuItem.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        renameItem();
      }
    });
    menu.add(menuItem);

    menu.add(new JSeparator());

    menuItem = new JMenuItem("Edit " + (isSelectedItemOfTypeGame() ? "Game" : "Music") + " Info");
    menuItem.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        editGameMusicInfo_Current();
      }
    });
    menu.add(menuItem);

    if (isSelectedItemOfTypeGame()) {
      menuItem = new JMenuItem("Edit Version Info");
      menuItem.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(final java.awt.event.ActionEvent evt) {
          editVersionInfo_Current();
        }
      });
      menu.add(menuItem);

      menuItem = new JMenuItem("Edit Personal Info");
      menuItem.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(final java.awt.event.ActionEvent evt) {
          editPersonalInfo_Current();
        }
      });
      menu.add(menuItem);

      menu.add(new JSeparator());

      menuItem = new JMenuItem("Edit Game File");
      menuItem.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(final java.awt.event.ActionEvent evt) {
          editGameFile();
        }
      });
      menu.add(menuItem);
    }

    menuItem = new JMenuItem("Edit Music File");
    menuItem.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        editMusicFile();
      }
    });
    menu.add(menuItem);

    if (isSelectedItemOfTypeGame()) {
      menu.add(new JSeparator());

      menuItem = new JMenuItem("Screenshots");
      menuItem.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(final java.awt.event.ActionEvent evt) {
          editScreenshotsFile();
        }
      });
      menu.add(menuItem);

      menuItem = new JMenuItem("Extras");
      menuItem.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(final java.awt.event.ActionEvent evt) {
          editExtrasFile();
        }
      });
      menu.add(menuItem);
    }

    return menu;
  }

  public void updateEditRemoveViewEnabled() {
    ((ItemTableModel) itemTable.getModel()).updateEditRemoveViewEnabled();
  }

  @Override
  protected void removeSelectedItem() {
    final Item item = getSelectedItem();

    if (item != null) {
      Db.delete(item);
      ((ItemTableModel) itemTable.getModel()).reloadView();
    }
  }

  @Override
  protected void removeAllItems() {

    if (JOptionPane.showConfirmDialog(this,
        "This will DELETE all games and music in the current view!\n"
            + "Really do it (may take a long time)?", "jGameBase", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

      // set busy cursor
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

      final ItemView view = (ItemView) filterViewCombobox.getSelectedItem();
      Db.deleteAll(view.getData());

      // set default cursor
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

      // reload view
      ((ItemTableModel) itemTable.getModel()).reloadView();
    }
  }

  @Override
  protected void addGameFile() {
    final GameChooserDialog dialog = new GameChooserDialog();

    if (dialog.getCloseAction() == CloseAction.OK) {
      String suggestedName = dialog.getGameName().trim();

      if (!suggestedName.isEmpty()) {
        // cut first and last word
        suggestedName = suggestedName.replaceAll("^\\S*\\s*", "");
        suggestedName = suggestedName.replaceAll("\\s*\\S*$", "");

        // remove "
        suggestedName = suggestedName.replaceAll("\"", "");

        // make easy to read
        suggestedName = StringTools.capitalize(suggestedName);
      } else {
        suggestedName = dialog.getGameFileToRun();
      }

      final String name = (String) JOptionPane.showInputDialog(this, "Name:", "Add Game...",
          JOptionPane.PLAIN_MESSAGE, null, null, suggestedName);
      final Game game = new Game(name, dialog.getGameFilename(), dialog.getGameFileToRun(),
          dialog.getGameFilenameIndex());

      Db.saveOrUpdate(game);
      ((ItemTableModel) itemTable.getModel()).reloadView();
    }
  }

  @Override
  protected void editGameFile() {
    final Game game = getSelectedGame();

    if (game != null) {
      final GameChooserDialog dialog = new GameChooserDialog(game);

      if (dialog.getCloseAction() == CloseAction.OK) {
        game.setFilename(dialog.getGameFilename());
        game.setFileToRun(dialog.getGameFileToRun());
        game.setFilenameIndex(dialog.getGameFilenameIndex());

        Db.saveOrUpdate(game);
        updateItemTable();
      }
    }
  }

  @Override
  protected void addMusicFile() {
    final MusicChooserDialog dialog = new MusicChooserDialog();

    if (dialog.getCloseAction() == CloseAction.OK) {
      final String name = (String) JOptionPane.showInputDialog(this, "Name:", "Add Music...",
          JOptionPane.PLAIN_MESSAGE, null, null, "");
      final Music music = new Music(name, dialog.getFilename());

      Db.saveOrUpdate(music);
      ((ItemTableModel) itemTable.getModel()).reloadView();
    }
  }

  @Override
  protected void editMusicFile() {
    final Item item = getSelectedItem();

    // nothing selected?
    if (item == null) {
      return;
    }

    if (item instanceof Game) {
      final Game game = (Game) item;

      final MusicChooserDialog dialog = new MusicChooserDialog(game);

      if (dialog.getCloseAction() == CloseAction.OK) {
        game.setMusicFilename(dialog.getFilename());

        Db.saveOrUpdate(game);
        updateItemTable();
      }

    } else if (item instanceof Music) {
      final Music music = (Music) item;

      final MusicChooserDialog dialog = new MusicChooserDialog(music);

      if (dialog.getCloseAction() == CloseAction.OK) {
        music.setFilename(dialog.getFilename());

        Db.saveOrUpdate(music);
        updateItemTable();
      }
    }
  }

  @Override
  protected void editScreenshotsFile() {
    final Game game = getSelectedGame();

    if (game != null) {
      final ScreenshotChooserDialog dialog = new ScreenshotChooserDialog(game);

      if (dialog.getCloseAction() == CloseAction.OK) {
        game.setScreenshotFilename(dialog.getFilename());
        Db.saveOrUpdate(game);
        updateItemTable();
      }
    }
  }

  @Override
  protected void editExtrasFile() {
    if (getSelectedGame() == null) {
      return;
    }

    final Game game = getSelectedGame();

    final ExtrasDialog extrasDialog = new ExtrasDialog(game.getExtras(), game.getName());
    if (extrasDialog.getCloseAction() == CloseAction.OK) {

      // delete removed extras from database
      for (final Extra extra : game.getExtras()) {
        if (!extrasDialog.getExtras().contains(extra)) {
          // extra no longer present in new extras
          log.info("deleting Extra " + extra.getName());
          extra.setGameId(0);
          Db.delete(extra);
        }
      }
      // save game
      game.setExtras(extrasDialog.getExtras());
      Db.saveOrUpdate(game);
    }
    extrasDialog.dispose();
  }

  @Override
  protected void editAdditionalExtrasFile() {
    final Game game = getSelectedGame();

    if (game != null) {
      final File dir = Paths.getAdditionalExtraPathId(game.getId());

      // create dirs
      Paths.createAdditionalExtraDirectories(game.getId(), game.getName());

      // open dir in file manager
      SystemTools.open(dir.toURI());

      // sync additional extras
      game.syncAdditionalExtras(false);

      displaySelectedItem(true);
    }
  }

  @Override
  protected void syncAdditionalExtras() {

    final ItemTableModel data = ((ItemTableModel) itemTable.getModel());

    if (filterViewCombobox.getSelectedIndex() != Gui.VIEW_ALL_GAMES) {
      final int answer = JOptionPane
          .showConfirmDialog(
              this,
              "Do you want me to change to the '<All Games>' view so that the additional extras of all games will be synchronized?\n"
                  + "Otherwise only the additional extras of the games in the current view will be synchronized.",
              "jGameBase", JOptionPane.YES_NO_OPTION);

      if (answer == JOptionPane.YES_OPTION) {
        filterViewCombobox.setSelectedIndex(Gui.VIEW_ALL_GAMES);
        data.setView(views.get(Gui.VIEW_ALL_GAMES));
      }
    }

    // sync additional file extras for each game
    for (final Iterator<Item> iter = data.iterator(); iter.hasNext();) {
      final Item item = iter.next();

      // only games have additional extras
      if (item instanceof Game) {
        
        final Game game = ((Game) item);
        
        // sync additional extras from files
        game.syncAdditionalExtras(true);
        
      }
    }

    Extra.processQueuedForDeletion();
    displaySelectedItem(true);
    data.saveOrUpdateView();

    Db.reorganizeExtras();

    Gui.displayInformationDialog("The additional extras for this view have been synchronized.");
  }

  private void removeDuplicateUrlExtras(final Game game) {
    List<Extra> urlExtras = new CopyOnWriteArrayList<Extra>();
    for (Extra extra : game.getExtras()) {
      if (extra.isUrl()) {
        urlExtras.add(extra);
      }
    }
    
    try {
      for (Extra extraA : urlExtras) {
        URI urlA = new URI(extraA.getFilename());
        for (Extra extraB : urlExtras) {
          URI urlB = new URI(extraB.getFilename());
          if (extraA != extraB) {
            if ((urlA.equals(urlB)) && (extraA.getGameId() == extraB.getGameId()) && (extraA.getGameId() != 0)) {
//              System.err.println("Removing duplicate URL");
//              System.err.println("GAME:" + game.getName());
//              System.err.println("ExtraA: " + extraA.getName() + " - " + extraA.getFilename());
//              System.err.println("ExtraB: " + extraB.getName() + " - " + extraB.getFilename());

              extraB.setGameId(0);
              game.getExtras().remove(extraB);
              Db.delete(extraB);
              Db.saveOrUpdate(game);
            }
          }
        }
      }
    } catch (Exception e) {
    }
  }

  private Extra newUrlExtra(int gameId, String name, String filename) {
    final Extra extra = new Extra();
    extra.setGameId(gameId);
    extra.setName(name);
    extra.setFilename(filename);
    return extra;
  }
  
  private void addUrlsFromCsv() {
    final ItemTableModel data = ((ItemTableModel) itemTable.getModel());

    // get url's from csv
    final List<String[]> lines = loadUrlsFromCsv();

    // convert to a map
    final MultiValueMap urls = new MultiValueMap();

    for (final String[] line : lines) {
      try {
        urls.put(Integer.valueOf(line[0]), line);
      } catch (final NumberFormatException e) {
        log.warn("Could not convert '" + line[0] + "' to valid GameID.");
      }
    }

    List<Game> gamesToUpdate = new ArrayList<Game>();
    
    // iterate over all items (games and music)
    for (final Iterator<Item> iter = data.iterator(); iter.hasNext();) {
      final Item item = iter.next();

      // only games have additional extras
      if (item instanceof Game) {
        final Game game = ((Game) item);

        // sync additional extras from url list
        if (urls.containsKey(game.getId())) {
          gamesToUpdate.add(game);
          
          @SuppressWarnings("unchecked")
          final Collection<String[]> extras = urls.getCollection(game.getId());
          
          for (final String[] rawExtra : extras) {
            // add extra from rawExtra
            log.info("Adding url extra " + rawExtra[2]);
            
            final List<Extra> newExtras = new ArrayList<Extra>();

            // iterate over existing extras
            boolean existingUpdated = false;
            for (Extra existingExtra : game.getExtras()) {
              
              if (existingExtra.isUrl() && (existingExtra.getFilename().equals(rawExtra[2]))) {
                // duplicate found
                if (!existingUpdated) {
                  // update first duplicate
                  existingUpdated = true;
                  existingExtra.setName(game.getName() + " - " + rawExtra[1]);
                  existingExtra.setFilename(rawExtra[2]);
                  existingExtra.reevaluateCategory();
                  // keep updated existing extra
                  newExtras.add(existingExtra);
                } else {
                  // queue further duplicates for deletion from database
                  existingExtra.setGameId(0);
                  Extra.enqueueForDeletion(existingExtra);
                }
              } else {
                // no url extra or different url, so keep it
                newExtras.add(existingExtra);
              }
              
            }
            
            if (!existingUpdated) {
              // no existing found, so add as new url extra
              newExtras.add(newUrlExtra(game.getId(), game.getName() + " - " + rawExtra[1], rawExtra[2]));
            }

            game.setExtras(newExtras);
          }
        }

      }
    }

    log.info("Removing deleted extras");
    Extra.processQueuedForDeletion();
    
    log.info("Writing to database");
    Db.saveOrUpdateAll(gamesToUpdate);
    
    log.info("Finished importing URLs from CSV");
  }

  private List<String[]> loadUrlsFromCsv() {
    final File csvFilename = new File(Databases.getCurrent().getPath(),
        Const.ADDEXTRAS_URL_FILENAME);
    CSVReader reader;
    List<String[]> lines = new ArrayList<String[]>();

    log.info("Loading URLs from file '" + csvFilename + "'...");

    try {
      reader = new CSVReader(new FileReader(csvFilename), CSVParser.DEFAULT_SEPARATOR,
          CSVParser.DEFAULT_QUOTE_CHARACTER, Const.CSV_ESCAPE_CHAR);
      lines = reader.readAll();
    } catch (final IOException e) {
      log.warn("Problem reading file '" + csvFilename + "'!");
    }
    return lines;
  }

  private void saveUrlsToCsv() {
    final List<String[]> urls = new ArrayList<String[]>();
    final List<Extra> extras = Db.getExtras();

    for (final Extra extra : extras) {
      if (extra.isUrl()) {
        log.info("export URL '" + extra.getFilename() + "'.");
        final String[] url = { extra.getGameId() + "", extra.getCategory().getDir(),
            extra.getFilename() };
        urls.add(url);
      }
    }

    saveUrlListToCsv(urls, false);
  }

  private void saveUrlListToCsv(final List<String[]> urls, final boolean append) {
    final File file = new File(Databases.getCurrent().getPath(), Const.ADDEXTRAS_URL_FILENAME);
    CSVWriter writer = null;

    try {
      writer = new CSVWriter(new FileWriter(file, append), CSVParser.DEFAULT_SEPARATOR,
          CSVParser.DEFAULT_QUOTE_CHARACTER, Const.CSV_ESCAPE_CHAR);

      for (final String[] line : urls) {
        writer.writeNext(line);
      }

    } catch (final IOException e) {
      e.printStackTrace();
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch (final IOException e) {
        }
      }
    }

  }

  @Override
  protected void importURLsFromCSV() {
    // URLs from the databases 'url.csv' file will be added to the database.

    try {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      addUrlsFromCsv();
      displaySelectedItem(true);
      Db.reorganizeExtras();
    } catch (final Exception e) {
      e.printStackTrace();
    } finally {
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

  }

  @Override
  protected void exportURLsToCSV() {
    // write URLs from the databases to the databases 'url.csv' file.

    try {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      saveUrlsToCsv();
    } catch (final Exception e) {
      e.printStackTrace();
    } finally {
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

  }

  @Override
  protected void matchURLsFromCSV() {
    File file;

    final JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileFilter(csvFileFilter);

    try {
      if (fileChooser.showOpenDialog(null) == javax.swing.JFileChooser.APPROVE_OPTION) {
        file = fileChooser.getSelectedFile();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        matchUrlsByName(file);
      }
    } catch (final Exception e) {
      e.printStackTrace();
    } finally {
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
  }

  private void matchUrlsByName(final File csvFile) {
    if ((csvFile == null) || (!csvFile.exists())) {
      log.error("File with new URLs '" + csvFile + "' not found; nothing to do.");
      return;
    }

    CSVReader reader;
    List<String[]> lines = new ArrayList<String[]>();

    List<String[]> found = new ArrayList<String[]>();
    final List<String[]> notFound = new ArrayList<String[]>();
    int notFoundCount = 0;

    log.info("Matching URLs from file '" + csvFile + "'...");

    try {
      reader = new CSVReader(new FileReader(csvFile), CSVParser.DEFAULT_SEPARATOR,
          CSVParser.DEFAULT_QUOTE_CHARACTER, Const.CSV_ESCAPE_CHAR);
      lines = reader.readAll();
    } catch (final IOException e) {
      log.warn("Problem reading file '" + csvFile + "'!");
    }

    if (lines.size() == 0) {
      log.info("File with new URLs '" + csvFile + "' empty; nothing to do.");
      return;
    }

    final ItemTableModel data = ((ItemTableModel) itemTable.getModel());

    final List<AbstractMap.SimpleEntry<String, Game>> Games = new ArrayList<AbstractMap.SimpleEntry<String, Game>>();
    final List<AbstractMap.SimpleEntry<String, Game>> GamesNoSpace = new ArrayList<AbstractMap.SimpleEntry<String, Game>>();

    // iterate over all items
    for (final Iterator<Item> iter = data.iterator(); iter.hasNext();) {
      final Item item = iter.next();

      // only games have additional extras
      if (item instanceof Game) {
        final Game game = ((Game) item);
        AbstractMap.SimpleEntry<String, Game> Entry;

        // build list "simplified name, Game"
        Entry = new AbstractMap.SimpleEntry<String, Game>(StringTools.simplifyForMatching(game
            .getName()), game);
        Games.add(Entry);

        // build list "simplified name without whitespace, Game"
        Entry = new AbstractMap.SimpleEntry<String, Game>(StringTools.simplifyForMatching(
            game.getName()).replaceAll("\\s", ""), game);
        GamesNoSpace.add(Entry);
      }
    }

    // match each line
    for (final String[] line : lines) {
      final String simplified = StringTools.simplifyForMatching(line[0]);
      log.info("Trying to match '" + line[0] + "' as '" + simplified + "'...");

      double bestMatch = 0.0;
      Game bestGame = null;
      boolean isFound = false;

      // match against all games PASS1: exact matches only
      for (final AbstractMap.SimpleEntry<String, Game> Entry : Games) {
        final double match = StringTools.getStringSimilarity(simplified, Entry.getKey());

        if (match > bestMatch) {
          bestMatch = match;
          bestGame = Entry.getValue();
        }

        if (match == 1.0) {
          log.info("'" + line[0] + "' matches '" + Entry.getValue().getName() + "'.");
          found = addUrlFromCsv(found, Entry.getValue(), line);
          isFound = true;
        }

      }

      if (!isFound) { // no exact match
        // match against all games PASS2: near match
        for (final AbstractMap.SimpleEntry<String, Game> Entry : Games) {
          final double match = StringTools.getStringSimilarity(simplified, Entry.getKey());

          if (match > 0.865) {
            log.info("'" + line[0] + "' matches '" + Entry.getValue().getName() + "' with " + match
                + ".");
            found = addUrlFromCsv(found, Entry.getValue(), line);
            isFound = true;
          }

        }
      }

      if (!isFound) { // no exact and no near match
        // match against all games PASS3: match shortest String without
        // whitespace
        for (final AbstractMap.SimpleEntry<String, Game> Entry : GamesNoSpace) {
          final String simplifiedNoSpace = simplified.replaceAll("\\s", "");

          // length of shorter String
          final int len = Math.min(simplifiedNoSpace.length(), Entry.getKey().length());

          // get original names
          final String name = StringTools.convertNumbers_AllToArabic(line[0]);
          final String match = StringTools.convertNumbers_AllToArabic(Entry.getValue().getName());

          // match is long enough and original names don't contain numbers
          // else "Archon" would match "Archon 2"
          if ((len >= 7)
              && (!name.matches("^.*\\d+.*$"))
              && (!match.matches("^.*\\d+.*$"))
              && (simplifiedNoSpace.substring(0, len).equalsIgnoreCase(Entry.getKey().substring(0,
                  len)))) {
            log.info("Beginning of '" + line[0] + "' matches beginning of '"
                + Entry.getValue().getName() + "' without whitespace.");
            found = addUrlFromCsv(found, Entry.getValue(), line);
            isFound = true;
          }

        }
      }

      if (!isFound) {
        // use best match if good enough
        if (bestMatch >= 0.8) {
          log.info("Best match for '" + simplified + "' found '" + bestGame.getName() + "' with "
              + bestMatch + ".");
          found = addUrlFromCsv(found, bestGame, line);
        } else {
          log.info("NO match for '" + simplified + "' found.");
          notFound.add(line);
          notFoundCount++;
        }
      }
    }

    log.info("MATCHED " + lines.size());
    log.info("  FOUND:    " + (lines.size() - notFoundCount));
    log.info("  NOT FOUND:" + notFoundCount);

    // append matched urls to urls file
    saveUrlListToCsv(found, true);

    // append notFound urls
    CSVWriter writer = null;
    final File file = new File(Databases.getCurrent().getPath(), "url_notfound.csv");
    try {
      writer = new CSVWriter(new FileWriter(file, true), CSVParser.DEFAULT_SEPARATOR,
          CSVParser.DEFAULT_QUOTE_CHARACTER, Const.CSV_ESCAPE_CHAR);
      for (final String[] line : notFound) {
        writer.writeNext(line);
      }
    } catch (final IOException e) {
      e.printStackTrace();
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch (final IOException e) {
        }
      }
    }
  }

  private List<String[]> addUrlFromCsv(final List<String[]> urls, final Game game,
      final String[] newUrl) {
    if ((newUrl.length < 2) || (newUrl.length > 3)) {
      log.warn("Could not add entry '" + Arrays.toString(newUrl) + "' to url csv file.");
      return urls;
    }

    final String[] url = new String[3];

    // game ID
    url[0] = "" + game.getId();

    if (newUrl.length == 2) {
      url[1] = Const.ADDEXTRAS_MISC_DIRNAME;
      url[2] = newUrl[1];
    } else if (newUrl.length == 3) {
      url[1] = newUrl[1];
      url[2] = newUrl[2];
    }

    urls.add(url);

    return urls;
  }

  @Override
  protected void help() {
    SystemTools.open(Const.URI_DOCUMENTATION);
  }

  @Override
  protected void forum() {
    SystemTools.open(Const.URI_FORUM);
  }

  @Override
  protected void bugtracker() {
    SystemTools.open(Const.URI_BUGTRACKING);
  }

  @Override
  protected void findInList() {
    find = JOptionPane.showInputDialog(this, "Text to find:", "Find in List",
        JOptionPane.PLAIN_MESSAGE);

    // cancel?
    if (find == null) {
      find = "";
      return;
    }

    // no text?
    find = find.toLowerCase().trim();
    if (find.isEmpty()) {
      return;
    }

    // get item data
    final ItemTableModel data = (ItemTableModel) itemTable.getModel();

    // empty table (no item data)
    if (data.getRowCount() < 1) {
      return;
    }

    // select first element
    data.setSelectedRow(0);

    // perform search
    findNext();
  }

  @Override
  protected void findNext() {
    // no text to find specified
    if (find.isEmpty()) {
      findInList();
    }

    // no item selected (and none selectable)
    if (getSelectedItem() == null) {
      return;
    }

    final int startRow = itemTable.getSelectedRow();

    // get item data
    final ItemTableModel data = (ItemTableModel) itemTable.getModel();

    int i;
    boolean found = false;
    for (i = startRow + 1; i < data.getRowCount(); i++) {
      final String col1 = ((Item) data.getValueAt(i, 0)).getName().toLowerCase();
      final String col2 = ((String) data.getValueAt(i, 1)).toLowerCase();
      if ((col1.indexOf(find) != -1) || (col2.indexOf(find) != -1)) {
        found = true;
        break;
      }
    }

    if (found) {
      // select found item
      data.setSelectedRow(i);
      updateItemTable();
    } else {
      if (i == data.getRowCount()) {
        if (startRow == 0) {
          // searched complete list, no match found
          Gui.displayInformationDialog("No matches found!");
        } else {
          // wrap search:
          // select first element
          data.setSelectedRow(0);

          // perform search
          findNext();
        }
      }
    }

  }

  @Override
  protected void markGameForLinking() {
    final Game game = getSelectedGame();

    if (game == null) {
      return;
    }

    linkGame = game;
    status3.setText(game.getStringId() + ": " + game.getName());
    displaySelectedItem(true);
  }

  @Override
  protected void setGameLink() {
    final Game selectedGame = getSelectedGame();

    if ((selectedGame == null) || (linkGame == null) || (selectedGame.getId() == linkGame.getId())) {
      return;
    }

    new SetGameLinkDialog(selectedGame, linkGame);

    updateItemTable();
  }

  @Override
  protected void clearGameLink() {
    final Game game = getSelectedGame();

    if (game == null) {
      return;
    }

    new ClearGameLinkDialog(game);

    updateItemTable();
  }

  @Override
  protected void options() {
    new OptionsDialog();
  }

  @Override
  protected void verifyAvailableFiles() {
    new VerifyAvailableFilesDialog(); // create and display dialog
    clearAllViewCaches();
    reloadCurrentView();
  }

  protected void clearAllViewCaches() {
    // clear all view caches so changes will be loaded
    for (final ItemView view : views) {
      view.clearCache();
    }
  }

  public void reloadCurrentView() {
    final ItemView view = (ItemView) filterViewCombobox.getSelectedItem();
    ((ItemTableModel) itemTable.getModel()).setView(view);
    ((ItemTableModel) itemTable.getModel()).fireTableDataChanged();
    filterViewCombobox.setAction(filterViewCombobox.getAction());
  }

  public int getSidebarWidth() {
    return mainPane.getSize().width - mainPane.getDividerLocation() - mainPane.getInsets().right
        - mainPane.getDividerSize();
  }

  @Override
  protected void setSidebarWidth(final int size) {
    mainPane.setDividerLocation(mainPane.getSize().width - mainPane.getInsets().right
        - mainPane.getDividerSize() - size);
    Preferences.set(Preferences.SIDEBAR_WIDTH, size);
  }

  // this has to be done after the gui is initialized and visible
  public void initAfterVisible() {
    setSidebarWidth(Preferences.getInt(Preferences.SIDEBAR_WIDTH));
    sidePanel_HSplitPane.setDividerLocation(Preferences.getInt(Preferences.SIDEBAR_H_DIVIDER));
    sidePanel_VSplitPane.setDividerLocation(Preferences.getInt(Preferences.SIDEBAR_V_DIVIDER));
    // select correct ITEM
    selectItem(Preferences.get(Preferences.SELECTED_ITEM), false);
  }

  @Override
  protected void playSelectedItemAsMusic(final Emulator emulator) {
    stopMusic();

    final Item item = getSelectedItem();
    boolean playing = false;

    if (item instanceof Game) {
      final Game game = (Game) item;
      final Music music = new Music();
      music.setName(game.getName());
      music.setFilename(game.getMusicFilename());
      music.play(emulator);
      playing = true;
    }

    if (item instanceof Music) {
      final Music music = (Music) item;
      addToRecent(item);
      music.play(emulator);
      filterStopMusicButton.setEnabled(Emulators.isMusicRunning());
      playing = true;
    }

    if (playing) {
      filterStopMusicButton.setEnabled(true);
      checkStopMusicButtonStatus = new SwingWorker<Void, Void>() {
        @Override
        protected Void doInBackground() throws Exception {
          while (Emulators.isMusicRunning()) {
            Thread.sleep(100);
          }
          return null;
        }

        @Override
        protected void done() {
          filterStopMusicButton.setEnabled(Emulators.isMusicRunning());
        }
      };
      checkStopMusicButtonStatus.execute();
    }
  }

  @Override
  protected void stopMusic() {
    Emulators.stopMusicRunning();
    filterStopMusicButton.setEnabled(Emulators.isMusicRunning());

    if ((checkStopMusicButtonStatus != null)) {
      checkStopMusicButtonStatus.cancel(true);
    }
  }

  @Override
  public void unzipDatabase(File file) {
    if (file == null) {
      final JFileChooser fileChooser = new JFileChooser();

      fileChooser.setFileFilter(zipFileFilter);

      if (fileChooser.showOpenDialog(null) == javax.swing.JFileChooser.APPROVE_OPTION) {
        file = fileChooser.getSelectedFile();
      }
    }

    try {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      // not yet finished ;-(
    } catch (final Exception e) {
      e.printStackTrace();
    } finally {
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
  }

  @Override
  protected void downloadJDB() {
    Gui.displayWarningDialog("Download jGameBase databases and extract them into a subdirectory under "
        + "'" + Const.GBDIR_RW + "'.\n" + "Then start jGamebase again.");
    SystemTools.open(Const.URI_DOWNLOAD_JDB);
  }

  @Override
  protected void downloadDB() {
    Gui.displayWarningDialog("Download GameBase databases and extract them into a subdirectory under "
        + "'"
        + Const.GBDIR_RW
        + "'.\n"
        + "Then start jGamebase again.\n\n"
        + "You will either need to get a matching database overlay or adjust the emulator configuration and scripts.");
    SystemTools.open(Const.URI_DOWNLOAD_DB);
  }

  @Override
  protected void importGames() {
    final JFileChooser fileChooser = new JFileChooser();
    List<File> files = new ArrayList<File>();

    // Plugins.getSupportedDiskInfoExtension(): crt;d64;t64
    // Emulators.getSupportedGameExtensions():
    // crt;d64;g41;g64;lnx;p00;prg;t64;tap

    final FileExtensions diskExts = new FileExtensions(Plugins.getSupportedDiskInfoExtension());
    final FileExtensions gameExts = Emulators.getSupportedGameExtensions();
    final FileExtensions extractorExts = new FileExtensions(
        Plugins.getSupportedExtractorExtension());

    final FileExtensions allExts = new FileExtensions();
    allExts.add(diskExts);
    allExts.add(gameExts);
    allExts.add(extractorExts);

    fileChooser.setAcceptAllFileFilterUsed(false);
    fileChooser.setFileFilter(new FileNameExtensionFilter("All supported files (" + allExts + ")",
        allExts.toArray(new String[0])));
    fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Packed files (" + extractorExts
        + ")", extractorExts.toArray(new String[0])));
    fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
        "Image files (" + diskExts + ")", diskExts.toArray(new String[0])));
    fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Game and image files ("
        + gameExts + ")", gameExts.toArray(new String[0])));
    fileChooser.setMultiSelectionEnabled(true);

    if (fileChooser.showOpenDialog(null) == javax.swing.JFileChooser.APPROVE_OPTION) {
      files = Arrays.asList(fileChooser.getSelectedFiles());

      // for each selected file
      for (final File file : files) {
        final String filename = file.getName();
        final String ext = FileTools.getExtension(filename);
        Game game;
        String name;

        if (diskExts.matches(file)) {
          log.info("IMAGE " + file);

          // PATH???
          final String relativePath = Paths.getGamePath().getRelativePathFor(file).getPath();

          final DiskInfo diskInfo = Plugins.getDiskInfoForExtension(ext);
          try {
            diskInfo.load(file.getAbsolutePath());

            final int entryCount = diskInfo.getDirectory().length;

            for (int i = 0; i < entryCount; i++) {
              name = StringTools.capitalize(diskInfo.getFilenameAt(i));

              if (name.isEmpty()) {
                name = file.getName() + "_" + i;
              }

              log.info(i + ": " + name);

              game = new Game(name, relativePath, "", i);
              Db.saveOrUpdate(game);
            }

          } catch (final IOException e) {
            e.printStackTrace();
          }

        } else if (gameExts.matches(file)) {
          name = StringTools.capitalize(FileTools.removeExtension(file.getName()));
          log.info("GAME " + file + " - " + name);

          // PATH???
          game = new Game(name, file.getAbsolutePath(), "", -1);
          // Db.saveOrUpdate(game);

        } else if (extractorExts.matches(file)) {
          name = StringTools.capitalize(FileTools.removeExtension(file.getName()));
          log.info("PACKED " + file + " - " + name);

          // PATH???
          game = new Game(name, file.getAbsolutePath(), "", -1);
          // Db.saveOrUpdate(game);
        }

      }
    }

  }

  @Override
  protected void manageMusicEmulators() {
    final ManageEmulatorsDialog musicEmulatorsDialog = new ManageEmulatorsDialog(
        Emulators.getMusicEmulators(), "Music");
    if (musicEmulatorsDialog.getCloseAction() == CloseAction.OK) {
      Emulators.setMusicEmulators(musicEmulatorsDialog.getEmulators());
    }
    Emulators.writeToIniFile();
  }

  @Override
  protected void manageGameEmulators() {
    final ManageEmulatorsDialog gameEmulatorsDialog = new ManageEmulatorsDialog(
        Emulators.getGameEmulators(), "Game");
    if (gameEmulatorsDialog.getCloseAction() == CloseAction.OK) {
      Emulators.setGameEmulators(gameEmulatorsDialog.getEmulators());
    }
    Emulators.writeToIniFile();
  }

}
