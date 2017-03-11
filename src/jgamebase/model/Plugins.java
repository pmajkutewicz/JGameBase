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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import jgamebase.JGameBase;
import jgamebase.plugins.DiskInfo;
import jgamebase.plugins.Extractor;
import jgamebase.plugins.MusicInfo;
import jgamebase.plugins.diskinfo.ADF;
import jgamebase.plugins.diskinfo.CRT;
import jgamebase.plugins.diskinfo.D64;
import jgamebase.plugins.diskinfo.T64;
import jgamebase.plugins.extractor.RAR;
import jgamebase.plugins.extractor.SevenZip;
import jgamebase.plugins.extractor.Zip;
import jgamebase.plugins.musicinfo.SID;
import jgamebase.tools.FileTools;
import jgamebase.tools.ListerTools;

public class Plugins {

  private static Hashtable<String, Plugin> diskInfo;

  private static Hashtable<String, Plugin> musicInfo;

  private static Hashtable<String, Plugin> extractor;

  public static void initExtractorPlugins() {
    // extractor = initPlugins("extractor");

    extractor = new Hashtable<String, Plugin>();
    extractor = addPlugin(extractor, new RAR());
    extractor = addPlugin(extractor, new SevenZip());
    extractor = addPlugin(extractor, new Zip());
  }

  public static void initDiskInfoPlugins() {
    // diskInfo = initPlugins("diskinfo");

    diskInfo = new Hashtable<String, Plugin>();
    diskInfo = addPlugin(diskInfo, new ADF());
    diskInfo = addPlugin(diskInfo, new CRT());
    diskInfo = addPlugin(diskInfo, new D64());
    diskInfo = addPlugin(diskInfo, new T64());
  }

  public static void initMusicInfoPlugins() {
    // musicInfo = initPlugins("musicinfo");

    musicInfo = new Hashtable<String, Plugin>();
    musicInfo = addPlugin(musicInfo, new SID());
  }

  private static Hashtable<String, Plugin> addPlugin(final Hashtable<String, Plugin> plugins,
      final Plugin plugin) {
    final String[] supportedExtensions = plugin.getSupportedExtensions();
    for (final String supportedExtension : supportedExtensions) {
      plugins.put(supportedExtension, plugin);
    }
    return plugins;
  }

  private static Hashtable<String, Plugin> initPlugins(final String packageName) {
    // list class files in the plugins directory
    final List<String> allClassFilenames = new FileExtensions("class").getMatching(ListerTools
        .list_Dirs_Files_Paths(
            (JGameBase.class.getResource("").getPath() + "plugins/" + packageName), false, true,
            false));

    // remove inner classes (these are no plugins)
    final List<String> pluginFilenames = new ArrayList<String>();
    for (final String className : allClassFilenames) {
      if (!className.contains("$")) { // inner class
        pluginFilenames.add(className);
      }
    }

    // load plugins
    final Hashtable<String, Plugin> plugins = new Hashtable<String, Plugin>();
    Plugin plugin = null;
    for (final String string : pluginFilenames) {
      final String pluginClassname = "jgamebase.plugins." + packageName + "."
          + FileTools.removeExtension(string);

      try { // load plugin
        plugin = (Plugin) (Class.forName(pluginClassname).newInstance());
      } catch (final Exception e) {
        e.printStackTrace();
      }

      // add supported extensions and plugins to hashtable
      final String[] supportedExtensions = plugin.getSupportedExtensions();
      for (final String supportedExtension : supportedExtensions) {
        plugins.put(supportedExtension, plugin);
      }
    }

    return plugins;
  }

  public static DiskInfo getDiskInfoForExtension(final String extension) {
    if (existsDiskInfoForExtension(extension) && (diskInfo.get(extension) instanceof DiskInfo)) {
      return (DiskInfo) diskInfo.get(extension);
    }
    return null;
  }

  public static boolean existsDiskInfoForExtension(final String extension) {
    return diskInfo.containsKey(extension);
  }

  public static String getSupportedDiskInfoExtension() {
    final StringBuilder supportedExtensions = new StringBuilder();

    for (final Enumeration<String> enumeration = diskInfo.keys(); enumeration.hasMoreElements();) {
      final String extension = enumeration.nextElement();
      supportedExtensions.append(extension);
      if (enumeration.hasMoreElements()) {
        supportedExtensions.append(";");
      }
    }
    return supportedExtensions.toString();
  }

  public static MusicInfo getMusicInfoForExtension(final String extension) {
    if (existsMusicInfoForExtension(extension) && (musicInfo.get(extension) instanceof MusicInfo)) {
      return (MusicInfo) musicInfo.get(extension);
    }
    return null;
  }

  public static boolean existsMusicInfoForExtension(final String extension) {
    return musicInfo.containsKey(extension);
  }

  public static String getSupportedMusicInfoExtension() {
    final StringBuilder supportedExtensions = new StringBuilder();
    for (final Enumeration<String> enumeration = musicInfo.keys(); enumeration.hasMoreElements();) {
      final String extension = enumeration.nextElement();
      supportedExtensions.append(extension);
      if (enumeration.hasMoreElements()) {
        supportedExtensions.append(";");
      }
    }
    return supportedExtensions.toString();
  }

  public static Extractor getExtractorForExtension(final String extension) {
    if (existsExtractorForExtension(extension) && (extractor.get(extension) instanceof Extractor)) {
      return (Extractor) extractor.get(extension);
    }
    return null;
  }

  public static boolean existsExtractorForExtension(final String extension) {
    return extractor.containsKey(extension);
  }

  public static String getSupportedExtractorExtension() {
    final StringBuilder supportedExtensions = new StringBuilder();
    for (final Enumeration<String> enumeration = extractor.keys(); enumeration.hasMoreElements();) {
      final String extension = enumeration.nextElement();
      supportedExtensions.append(extension);
      if (enumeration.hasMoreElements()) {
        supportedExtensions.append(";");
      }
    }
    return supportedExtensions.toString();
  }

  private Plugins() {
  }
}
