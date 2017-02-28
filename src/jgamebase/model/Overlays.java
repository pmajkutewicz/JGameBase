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

import static jgamebase.Const.log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JOptionPane;

import jgamebase.Const;
import jgamebase.JGameBase;
import jgamebase.db.Db;
import jgamebase.gui.Gui;
import jgamebase.plugins.Extractor;
import jgamebase.tools.FileTools;
import jgamebase.tools.IniFileManager;
import jgamebase.tools.ListerTools;
import jgamebase.tools.TempDir;

public class Overlays {
  
  private static String latestFilename = "";
  private static int latestVersion = -1;

  public static void init() {
    // copy overlays to rw directory
    if (Const.FHS) {
      final File overlayDir_ro = new File(Const.GBDIR_RO, Const.OVERLAY_DIRNAME);
      final File overlayDir_rw = new File(Const.GBDIR_RW, Const.OVERLAY_DIRNAME);
      
      // list files in overlays directory
      final List<String> overlays = ListerTools.list_Dirs_Files_Paths(overlayDir_ro.toString(),
          false, true, false);
      
      for (final String overlay : overlays) {
        final File src = new File(overlayDir_ro, overlay);
        final File dst = new File(overlayDir_rw, overlay);
      
        // copy overlay?
        if ((!dst.exists()) || (dst.length() == 0)) {
          try {
            log.info("Found overlay \"" + overlay + "\", copying to directory \"" + overlayDir_rw
                + "\".");
            FileTools.copyFile(src, dst);
          } catch (final IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

  private static void deleteOldFile() {
    // delete old "overlay_installed" file
    final File file = new File(Databases.getCurrent().getPath(), "overlay_installed");
    if (file.exists()) {
      try {
        file.delete();
      } catch (final Exception e) {
      }
    }
  }
  
  /**
   * Sets <code>latestVersion</code> and <code>latestFilename</code>. If no
   * matching overlay is found <code>latestVersion = -1</code> and
   * <code>latestFilename = ""</code> will be set.
   * 
   * @param database
   */
  public static void findLatestVersion(final Database database) {
    deleteOldFile();
    
    latestFilename = "";
    latestVersion = -1;
    
    final File dir = new File(Const.GBDIR_RW, Const.OVERLAY_DIRNAME);
    final File iniFile = new File(new File(Const.GBDIR_RW, Const.OVERLAY_DIRNAME), Const.OVERLAY_INI_FILENAME);

    IniFileManager ini = null;

    // try to read global overlay.ini
    if (iniFile.exists()) {
      try {
        ini = new IniFileManager(iniFile);
      } catch (final IOException e) {
      }
    }

    if (ini != null) {
      // overlay.ini exists
      final List<String> names = ini.getSectionNames();
      for (final String name : names) {
        // check each overlay
        if (JGameBase.option_debug) {
          log.info("Found entry for overlay '" + name + "'");
        }
        final List<String> patterns = ini.getList(name);
        for (final String patternString : patterns) {
          // check each overlay pattern
          if (JGameBase.option_debug) {
            log.info("Found pattern '" + patternString + "'");
          }
          try {
            final Pattern pattern = Pattern.compile(patternString.toLowerCase());
            
            Matcher matcher;
            boolean matchFound = false;

            // check db name
            matcher = pattern.matcher(database.getName().toLowerCase());
            if (matcher.matches()) {
              matchFound = true;
              if (JGameBase.option_debug) {
                log.info("Match found for pattern '" + patternString + "' in database name '" + database.getName() + "'");
              }
            }
            
            // check db display name
            matcher = pattern.matcher(database.getDisplayName().toLowerCase());
            if (matcher.matches()) {
              matchFound = true;
              if (JGameBase.option_debug) {
                log.info("Match found for pattern '" + patternString + "' in database display name '" + database.getDisplayName() + "'");
              }
            }
            
            if (matchFound) {
              // list all overlay file names
              List<String> allFilenames = ListerTools.list_Dirs_Files_Paths(dir.getAbsolutePath(), false, true, false);

              // create list of only matching overlay file names
              List<String> matchingFilenames = new ArrayList<String>();
              for (String overlayFilename : allFilenames) {
                if (overlayFilename.matches(name + "\\.overlay\\.\\d{8}+\\.zip")) {
                  matchingFilenames.add(overlayFilename);
                }
              }
              
              // if matching overlay file names exist, get highest nummer (i.e. latest) overlay
              if (matchingFilenames.size() > 0) {
                // sort ascending
                Collections.sort(matchingFilenames);
                // get last list element
                String filename = matchingFilenames.get(matchingFilenames.size()-1);
                latestFilename = filename;
                // get version as String
                int pos = name.length() + ".overlay.".length();
                String versionString = filename.substring(pos, pos + 8);
                // convert String to integer
                try {
                  latestVersion = Integer.parseInt(versionString);
                } catch (NumberFormatException e) {
                }
              }

              if (latestVersion > 0) {
                // version found
                return;
              } else {
                latestFilename = "";
                latestVersion = -1;
                // no matching overlay or problem
                log.info("Found overlay match for '" + name + "' in database '" + database.getName()
                    + ": " + database.getDisplayName() + "', but no matching overlay file was found.");
              }
            }

          } catch (final PatternSyntaxException pse) {
            log.warn("Problem with regular expression for overlay '" + name + "':");
            log.warn(pse.getMessage());
          }

        }
      }
    }
  }

  public static void installLatestVersion(final Database database) {
    final File dir = new File(Const.GBDIR_RW, Const.OVERLAY_DIRNAME);

    log.info("Extracting overlay '" + latestFilename + "'...");

    // extract overlay to temp
    try {
      TempDir.getCleanPath();
      final Extractor extractor = Plugins.getExtractorForExtension("zip");
      extractor.extractToCleanTempDir(new File(dir, latestFilename).getAbsolutePath());
    } catch (final IOException e) {
      e.printStackTrace();
    }

    // copy extracted files to dbdir
    try {
      FileTools.copyDir(TempDir.getPath(), database.getPath());
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }
  
  public static int getLatestVersion() {
    return latestVersion;
  }
  
  // don't let anyone instantiate this class
  private Overlays() {
  }

}
