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
import java.util.ArrayList;
import java.util.List;

import jgamebase.Const;
import jgamebase.gui.Gui;

public class Path {

  private List<File> dirs = new ArrayList<File>();
  private final String defaultDirname;

  public Path(final String defaultDirname) {
    this.defaultDirname = defaultDirname;
  }

  public List<File> get() {
    return dirs;
  }

  public void set(final List<File> dirs) {
    this.dirs = dirs;
  }

  public List<File> getWithDefault() {
    // create copy of directory list
    final List<File> dirsWithDefault = new ArrayList<File>(dirs);

    // add default rw
    dirsWithDefault.add(getDefault_rw());

    // if default ro is different from default rw, add it
    if (!getDefault_rw().equals(getDefault_ro())) {
      // add default ro
      dirsWithDefault.add(getDefault_ro());
    }

    return dirsWithDefault;
  }

  public File getDefault_rw() {
    return new File(new File(Const.GBDIR_RW, Databases.getCurrent().getName()), defaultDirname);
  }

  public File getDefault_ro() {
    return new File(new File(Const.GBDIR_RO, Databases.getCurrent().getName()), defaultDirname);
  }

  /**
   * 
   * @param fileToFind
   *          The file to search
   * @return The found File or <code>null</code>.
   */
  public File find(final File fileToFind) {

    // sanity check
    if ((fileToFind == null) || (fileToFind.getPath().isEmpty())) {
      return null; // not found
    }

    File file = null;

    for (final File dir : getWithDefault()) {
      // combine directory with relative path and filename, removing trailing
      // spaces
      file = new File(dir, fileToFind.toString().trim());

      if (file.exists()) {
        return file; // found
      }
    }

    return null; // not found
  }

  public File findAndWarn(final File fileToFind) {
    final File found = find(fileToFind);
    if (found != null) {
      return found;
    } else {
      Gui.displayWarningDialog("File '" + fileToFind + "' not found.");
      return null;
    }
  }

  public File whichBasePath(final File fileToFind) {
    File file = null;

    for (final File dir : getWithDefault()) {
      // combine directory with relative path and filename, removing trailing
      // spaces
      file = new File(dir, fileToFind.toString().trim());

      if (file.exists()) {
        return dir; // found
      }
    }
    return null; // not found
  }

  public File getRelativePathFor(final File fileToFind) {
    if (!fileToFind.isAbsolute()) {
      throw new IllegalArgumentException("File must be absolute");
    }

    final String absolute = fileToFind.getAbsolutePath();
    String base;
    for (final File dir : getWithDefault()) {
      base = dir.getAbsolutePath();
      if (absolute.startsWith(base)) {
        return new File(absolute.substring(base.length() + 1, absolute.length())); // found
      }
    }
    return null; // not found
  }

  public boolean exists(final File file) {
    return find(file) != null;
  }

  public String findCorrectFilename(final File fileToFind) {

    File tmp = new File(fileToFind.toString().trim());

    // create a list of the directories
    final List<String> paths = new ArrayList<String>();
    while (tmp.getParentFile() != null) {
      paths.add(0, tmp.getName());
      tmp = tmp.getParentFile();
    }
    paths.add(0, tmp.getName());

    // iterate over all dirs
    dirLoop: for (final File dir : getWithDefault()) {
      String correct = "";

      for (final String path : paths) {
        boolean found = false;

        final File dirToList = new File(dir, correct);
        final String[] potentialMatches = dirToList.list();

        if (potentialMatches != null) { // check potential matches
          for (final String potentialMatche : potentialMatches) {
            if (path.equalsIgnoreCase(potentialMatche)) {
              found = true;
              correct = new File(correct, potentialMatche).toString();
            }
          }
        }

        if (!found) {
          // if one part of the path was not found, it doesn't make sense to try
          // longer
          continue dirLoop;
        }
      }

      // double check that the file exists
      final File file = new File(dir, correct);
      if (file.exists()) {
        return Paths.pathStartingWithoutSeparator(correct); // found
      }

    }

    return null; // not found
  }
}
