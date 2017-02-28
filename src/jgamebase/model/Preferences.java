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

package jgamebase.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import jgamebase.Const;

public final class Preferences {
  private static final String FILENAME = "Settings.cfg";

  public static final String SELECTED_VIEW = "selected_view";

  public static final String SELECTED_ITEM = "selected_item";

  public static final String WINDOW_POSITION_X = "window_position_x";

  public static final String WINDOW_POSITION_Y = "window_position_y";

  public static final String WINDOW_SIZE_X = "window_size_x";

  public static final String WINDOW_SIZE_Y = "window_size_y";

  public static final String CATEGORY_POSITION_X = "category_position_x";

  public static final String CATEGORY_POSITION_Y = "category_position_y";

  public static final String CATEGORY_SIZE_X = "category_size_x";

  public static final String CATEGORY_SIZE_Y = "category_size_y";

  public static final String SIDEBAR_WIDTH = "sidebar_width";

  public static final String SIDEBAR_H_DIVIDER = "sidebar_h_divider";

  public static final String SIDEBAR_V_DIVIDER = "sidebar_v_divider";

  public static final String DISPLAY_DETAILS = "display_details";

  public static final String SOUND_ON_CLASSICS = "sound_on_classics";

  public static final String HARDWARE_JOYSTICK = "hardware_joystick";

  public static final String ADULT_FILTER = "adult_filter";

  public static final String ADULT_FILTER_PW = "adult_filter_pw";

  public static final String ABOUT_SINCE = "about_since";

  public static final String ABOUT_GAMES_PLAYED = "about_games_played";

  public static final String ABOUT_MUSIC_PLAYED = "about_music_played";

  public static final String OVERLAY_VERSION = "overlay_version";

  // OPTIONS
  public static final String DOCUMENT_OPEN_COMMAND = "document_open_command";

  public static final String PICTURE_OPEN_COMMAND = "picture_open_command";

  public static final String EXTRAS_SHOW_FILENAMES = "extras_show_filenames";

  public static final String MAX_EXTRA_IMAGES_SHOWN = "max_extra_images_shown";

  private static Properties properties;

  private Preferences() {
  }

  public static void load() throws IOException {
    final File file = new File(Databases.getCurrent().getPath(), FILENAME);
    FileInputStream in = null;

    properties = new Properties();

    // try to load values
    try {
      in = new FileInputStream(file);
      properties.load(in);
    } catch (final FileNotFoundException e) { // file will be created
    } finally {
      if (in != null) {
        in.close();
      }
    }

    // set default values
    setDefault(SELECTED_VIEW, "0");
    setDefault(SELECTED_ITEM, "");

    setDefault(WINDOW_POSITION_X, "0");
    setDefault(WINDOW_POSITION_Y, "0");
    setDefault(WINDOW_SIZE_X, "640");
    setDefault(WINDOW_SIZE_Y, "480");

    setDefault(CATEGORY_POSITION_X, "0");
    setDefault(CATEGORY_POSITION_Y, "0");
    setDefault(CATEGORY_SIZE_X, "240");
    setDefault(CATEGORY_SIZE_Y, "180");

    setDefault(SIDEBAR_WIDTH, "320");
    setDefault(SIDEBAR_H_DIVIDER, "-1");
    setDefault(SIDEBAR_V_DIVIDER, "-1");
    setDefault(DISPLAY_DETAILS, "true");

    setDefault(SOUND_ON_CLASSICS, "true");
    setDefault(HARDWARE_JOYSTICK, "false");
    setDefault(ADULT_FILTER, "false");
    setDefault(ADULT_FILTER_PW, "");

    final SimpleDateFormat df = (SimpleDateFormat) DateFormat.getDateInstance();
    df.applyPattern("dd MMM yyyy");
    setDefault(ABOUT_SINCE, df.format(new Date()));
    setDefault(ABOUT_GAMES_PLAYED, "0");
    setDefault(ABOUT_MUSIC_PLAYED, "0");

    setDefault(DOCUMENT_OPEN_COMMAND, Const.OS_IS_WINDOWS ? "start" : "xdg-open");
    setDefault(PICTURE_OPEN_COMMAND, Const.OS_IS_WINDOWS ? "" : "eog");

    setDefault(EXTRAS_SHOW_FILENAMES, "false");
    setDefault(MAX_EXTRA_IMAGES_SHOWN, "9999");
    
    setDefault(OVERLAY_VERSION, "19000101");

    // save values
    save();
  }

  private static void setDefault(final String key, final String value) {
    properties.put(key, properties.getProperty(key, value));
  }

  // getter
  public static String get(final String key) {
    return properties.getProperty(key);
  }

  public static int getInt(final String key) {
    int i = 0;

    try {
      i = Integer.parseInt(properties.getProperty(key));
    } catch (final NumberFormatException nfe) {
    }

    return i;
  }

  public static long getLong(final String key) {
    long l = 0l;

    try {
      l = Long.parseLong(properties.getProperty(key));
    } catch (final NumberFormatException nfe) {
    }

    return l;
  }

  public static boolean is(final String key) {
    return properties.getProperty(key).equalsIgnoreCase("true")
        || properties.getProperty(key).equalsIgnoreCase("yes");
  }

  // setter
  public static void set(final String key, final String value) {
    properties.setProperty(key, value);
    save();
  }

  public static void set(final String key, final int value) {
    properties.setProperty(key, String.valueOf(value));
    save();
  }

  public static void set(final String key, final long value) {
    properties.setProperty(key, String.valueOf(value));
    save();
  }

  public static void set(final String key, final boolean value) {
    properties.setProperty(key, String.valueOf(value));
    save();
  }

  public static void inc(final String key) {
    set(key, getInt(key) + 1);
  }

  private static void save() {
    final File file = new File(Databases.getCurrent().getPath(), FILENAME);

    try {
      final FileOutputStream out = new FileOutputStream(file);
      properties.store(out, "Properties of jGameBase");
      out.close();
    } catch (final Exception e) {
    }
  }
}
