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

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Cursor;
import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import jgamebase.Const;
import jgamebase.gui.SplashScreen;
import jgamebase.tools.FileTools;
import jgamebase.tools.ListerTools;

public class Database implements DatabaseSelection {

  private String name;

  private String displayName;

  private Icon icon = null;

  private SplashScreen splashscreen = null;

  private AudioClip classicSound = null;

  private Icon missingScreenshotIcon = null;

  public Database(final String name) {
    this.name = name;
    File file;

    // load icon (the icon will always be needed)
    file = findImage(new File(getPath(), "Gfx"), "startup_32x32");
    if (file.exists()) {
      icon = new ImageIcon(file.toString());
    }
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see jgamebase.model.NameAndIcon#getDisplayName()
   */
  @Override
  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(final String displayName) {
    this.displayName = displayName;
  }

  @Override
  public Icon getIcon() {
    return icon;
  }

  public void showSplashscreen() {
    File file;

    // load splash screen
    file = findImage(new File(getPath(), "Splash"), "splash");
    if (file.exists()) {
      splashscreen = new SplashScreen(file.toString());
      // show splash screen
      splashscreen.setVisible(true);
      splashscreen.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    // by showing the splash screen we know that the database got selected,
    // so load other objects

    // classic sound
    file = new File(getPath(), "Sounds/Classic.wav");
    if (file.exists()) {
      try {
        classicSound = Applet.newAudioClip(file.toURI().toURL());
      } catch (final MalformedURLException e) {
        e.printStackTrace();
      }
    }

    // no screenshot icon
    file = new File(getPath(), "Gfx/No_Picture_320x200.gif");
    if (file.exists()) {
      missingScreenshotIcon = new ImageIcon(file.toString());
    }

  }

  public void hideSplashscreen() {
    if (splashscreen != null) {
      splashscreen.setVisible(false);
      splashscreen.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      // don't keep splash screen in memory - it will (probably) not be used
      // again
      splashscreen.dispose();
      splashscreen = null;
    }
  }

  private static File findImage(final File dir, final String needle) {

    // if dir does not exists => no image
    if ((!dir.exists()) || (!dir.isDirectory())) {
      return new File("");
    }

    // define file extensions for images
    final FileExtensions imageExtensions = new FileExtensions("gif;png;jpg;bmp");
    // list all filenames in the directory
    final List<String> allFilenames = ListerTools.list_Dirs_Files_Paths(dir.getAbsolutePath(),
        false, true, false);
    // get only these files that are images
    final List<String> imageFilenames = imageExtensions.getMatching(allFilenames);

    // search image matching needle
    for (final String filename : imageFilenames) {
      if (FileTools.removeExtension(filename).toLowerCase().equals(needle.toLowerCase())) {
        return new File(dir, filename);
      }
    }

    // if images were found at all, return one
    if (imageFilenames.size() > 0) {
      return new File(dir, imageFilenames.get((int) ((imageFilenames.size() - 1) * Math.random())));
    }

    // no images found => return file that not exists
    return new File("");
  }

  public boolean hasClassicSound() {
    return (classicSound != null);
  }

  public AudioClip getClassicSound() {
    return classicSound;
  }

  public boolean hasMissingScreenshotIcon() {
    return (missingScreenshotIcon != null);
  }

  public Icon getMissingScreenshotIcon() {
    return missingScreenshotIcon;
  }

  public File getPath() {
    return new File(Const.GBDIR_RW, getName());
  }

  public File getROPath() {
    return new File(Const.GBDIR_RO, getName());
  }

  public File getExportPath() {
    final File dir = new File(getPath(), Const.EXPORT_DIRNAME);

    if (!dir.exists()) {
      dir.mkdirs();
    }

    return dir;
  }

  public File getCleanExportPath() {
    FileTools.deleteAll(getExportPath());
    return getExportPath();
  }

  @Override
  public String toString() {
    return getName();
  }
}
