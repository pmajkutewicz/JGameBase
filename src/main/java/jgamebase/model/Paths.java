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

import static jgamebase.Const.ADDEXTRAS_BYID_DIRNAME;
import static jgamebase.Const.ADDEXTRAS_BYNAME_DIRNAME;
import static jgamebase.Const.ADDEXTRAS_DIRNAME;
import static jgamebase.Const.ADDEXTRAS_SUBDIRS;
import static jgamebase.Const.OS_IS_WINDOWS;
import static jgamebase.Const.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import jgamebase.Const;
import jgamebase.tools.IniFileManager;
import jgamebase.tools.ListerTools;
import jgamebase.tools.StringTools;
import jgamebase.tools.SystemTools;

public class Paths {

  private static final String FILENAME_PATH = "Paths.ini";

  private static final String SECTION_GAMES = "Games";
  private static final String SECTION_MUSIC = "Music";
  private static final String SECTION_PHOTOS = "Photos";
  private static final String SECTION_SCREENSHOTS = "Screenshots";
  private static final String SECTION_EXTRAS = "Extras";

  // directories
  private static Path extraPath = new Path(Const.EXTRAS_DIRNAME);
  private static Path gamePath = new Path(Const.GAMES_DIRNAME);
  private static Path musicPath = new Path(Const.MUSIC_DIRNAME);
  private static Path photoPath = new Path(Const.PHOTOS_DIRNAME);
  private static Path screenshotPath = new Path(Const.SCREENSHOTS_DIRNAME);

  public static File getAdditionalExtraPathId() {
    // read-write extra default path for additional extras
    return new File(new File(getExtraPath().getDefault_rw(), ADDEXTRAS_DIRNAME),
        ADDEXTRAS_BYID_DIRNAME);
  }

  public static File getAdditionalExtraPathName() {
    // read-write extra default path for additional extras
    return new File(new File(getExtraPath().getDefault_rw(), ADDEXTRAS_DIRNAME),
        ADDEXTRAS_BYNAME_DIRNAME);
  }

  public static File getAdditionalExtraPathId(final int id) {
    // first extra path is path for additional extras
    return new File(getAdditionalExtraPathId(), String.format("%05d", id));
  }

  public static void createAdditionalExtraDirectories(final int id, final String name) {
    File dir;

    final String displayId = String.format("%05d", id);
    final String displayName = replaceAllSlashWithDash(name);
    final String linkName = displayName + " (" + displayId + ")";

    // no need to log that
    System.out.println(linkName);

    // create dirs and subdirs "by-id/xxxxx/Animation", "by-id/xxxxx/Cover",
    // etc.
    for (final String subdir : ADDEXTRAS_SUBDIRS) {
      dir = new File(getAdditionalExtraPathId(id), subdir);
      if (!dir.exists()) {
        dir.mkdirs();
      }
    }

    // take first letter as dirname
    String subdirName = StringTools.sanitize(displayName).substring(0, 1).toUpperCase();
    // replace 0 to 9 with hash
    subdirName = subdirName.replaceAll("[0-9]", "0-9");

    // create dir e.g. "by-name/A"
    dir = new File(getAdditionalExtraPathName(), subdirName);
    if (!dir.exists()) {
      dir.mkdirs();
    }

    // create symbolic links (windows does not support symbolic links)
    if (!OS_IS_WINDOWS) {
      // check if symbolic link exists
      if (!new File(dir.getPath(), linkName).exists()) {
        // create symbolic link
        final String command = new File(dir.getPath(), "mk_link.sh").toString();

        PrintWriter out = null;

        try {
          out = new PrintWriter(new File(command));

          out.println("#!/bin/sh");
          out.println("ln -s \"../../" + new File(ADDEXTRAS_BYID_DIRNAME, displayId) + "\" \""
              + linkName + "\"");

        } catch (final IOException e) {
          e.printStackTrace();
        } finally {
          try {
            if (out != null) {
              out.close();
            }
          } catch (final Exception e) {
            e.printStackTrace();
          }
        }

        new File(command).setExecutable(true, false);

        final String[] envp = SystemTools.getSystemEnvironment();
        BufferedReader stderr = null;

        try {
          final Process process = Runtime.getRuntime().exec(command, envp, dir);

          stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
          String line = null;

          while ((line = stderr.readLine()) != null) {
            log.info(line);
          }

          process.waitFor();
        } catch (final IOException e) {
          e.printStackTrace();
        } catch (final InterruptedException e) {
          e.printStackTrace();
        } finally {
          if (stderr != null) {
            try {
              stderr.close();
            } catch (final IOException e) {
            }
          }
        }
        new File(command).delete();
      }
    }

  }

  public static void readFromIniFile() {
    final File path = new File(Databases.getCurrent().getPath(), FILENAME_PATH);

    try {
      final IniFileManager ini = new IniFileManager(path.toString());

      getGamePath().set(ListerTools.stringListToFileList(ini.getList(SECTION_GAMES)));
      getMusicPath().set(ListerTools.stringListToFileList(ini.getList(SECTION_MUSIC)));
      getScreenshotPath().set(ListerTools.stringListToFileList(ini.getList(SECTION_SCREENSHOTS)));
      getExtraPath().set(ListerTools.stringListToFileList(ini.getList(SECTION_EXTRAS)));
      getPhotoPath().set(ListerTools.stringListToFileList(ini.getList(SECTION_PHOTOS)));
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  public static void writeToIniFile() {
    final File path = new File(Databases.getCurrent().getPath(), FILENAME_PATH);

    try {
      final IniFileManager ini = new IniFileManager(path.toString());

      ini.setList(SECTION_GAMES, ListerTools.fileListToStringList(getGamePath().get()));
      ini.setList(SECTION_MUSIC, ListerTools.fileListToStringList(getMusicPath().get()));
      ini.setList(SECTION_SCREENSHOTS, ListerTools.fileListToStringList(getScreenshotPath().get()));
      ini.setList(SECTION_EXTRAS, ListerTools.fileListToStringList(getExtraPath().get()));
      ini.setList(SECTION_PHOTOS, ListerTools.fileListToStringList(getPhotoPath().get()));
      ini.save();
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  // don't let anyone instantiate this class
  private Paths() {
  }

  public static String backslashToSlash(final String filename) {
    if (filename == null) {
      return "";
    }

    String newFilename = new String(filename);
    while (newFilename.indexOf('\\') != -1) {
      newFilename = newFilename.replace('\\', '/');
    }

    return newFilename.trim();
  }

  public static String slashToBackslash(final String filename) {
    if (filename == null) {
      return "";
    }

    String newFilename = new String(filename);
    while (newFilename.indexOf('/') != -1) {
      newFilename = newFilename.replace('/', '\\');
    }

    return newFilename.trim();
  }

  public static String replaceAllSlashWithDash(String filename) {
    if (filename == null) {
      return "";
    }

    while (filename.indexOf('/') != -1) {
      filename = filename.replace('/', '-');
    }
    return filename;
  }

  public static String pathEndingWithSeparator(String path) {
    if (path == null) {
      return "";
    }

    if (!path.endsWith(File.separator)) {
      path += File.separator;
    }

    return path;
  }

  public static String pathEndingWithoutSeparator(String path) {
    if (path == null) {
      return "";
    }

    if (path.endsWith(File.separator)) {
      path = path.substring(0, path.length() - 1);
    }

    return path;
  }

  public static String pathStartingWithoutSeparator(String path) {
    if (path == null) {
      return "";
    }

    if (path.startsWith(File.separator)) {
      path = path.substring(1);
    }

    return path;
  }

  public static String removePath(final String filename) {
    return new File(filename).getName();
  }

  public static String getPathOnly(final String filename) {
    return (new File(filename).getParent() == null) ? "" : new File(filename).getParent();
  }

  public static void setExtraPath(final Path extraPath) {
    Paths.extraPath = extraPath;
  }

  public static Path getExtraPath() {
    return extraPath;
  }

  public static void setGamePath(final Path gamePath) {
    Paths.gamePath = gamePath;
  }

  public static Path getGamePath() {
    return gamePath;
  }

  public static void setMusicPath(final Path musicPath) {
    Paths.musicPath = musicPath;
  }

  public static Path getMusicPath() {
    return musicPath;
  }

  public static void setPhotoPath(final Path photoPath) {
    Paths.photoPath = photoPath;
  }

  public static Path getPhotoPath() {
    return photoPath;
  }

  public static void setScreenshotPath(final Path screenshotPath) {
    Paths.screenshotPath = screenshotPath;
  }

  public static Path getScreenshotPath() {
    return screenshotPath;
  }

}
