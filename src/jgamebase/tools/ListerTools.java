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

package jgamebase.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ListerTools {

  /**
   * Lists directories and/or filenames in the given directory.
   * 
   * @param dirname
   *          The path and name of the directory to list.
   * @param listDirs
   *          If <code>true</code> directories are listed, if <code>false</code>
   *          directories are not listed.
   * @param listFiles
   *          If <code>true</code> files are listed, if <code>false</code> files
   *          are not listed.
   * @param listHidden
   *          If <code>true</code> hidden directories or files are listed, if
   *          <code>false</code> hidden directories or files are not listed.
   * @param listPaths
   *          If <code>true</code> the absolute paths will be listed, if
   *          <code>false</code> only filenames will be listed.
   * @return A <code>List</code> of filenames (or filenames with paths) of the
   *         found directories and/or files.
   */
  private static List<String> list_Dirs_Files_Hidden_Paths(final String dirname,
      final boolean listDirs, final boolean listFiles, final boolean listHidden,
      final boolean listPaths) {
    if (dirname == null) {
      throw new NullPointerException("Name of directory must not be null");
    }
    if (dirname.isEmpty()) {
      throw new IllegalArgumentException("Name of directory must not be empty");
    }
    if ((!new File(dirname).exists()) || (!new File(dirname).isDirectory())) {
      throw new IllegalArgumentException("Name '" + dirname + "' must be an existing directory");
    }
    if (!listDirs && !listFiles) {
      throw new IllegalArgumentException("At least one of 'listDirs' or 'listFiles' must be true");
    }

    final File dir = new File(dirname);
    final File[] filesArray = dir.listFiles();
    final List<String> filenames = new ArrayList<String>();

    // no files => return empty list
    if ((filesArray == null) || (filesArray.length == 0)) {
      return filenames;
    }

    // convert listed files to list
    final List<File> files = Arrays.asList(filesArray);

    for (final File file : files) {
      if (listHidden || !file.isHidden()) {
        if ((listDirs && file.isDirectory()) || (listFiles && file.isFile())) {
          if (listPaths) {
            filenames.add(file.getAbsolutePath());
          } else {
            filenames.add(file.getName());
          }
        }
      }
    }

    Collections.sort(filenames);

    return filenames;
  }

  /**
   * Lists directories and/or filenames in the given directory.
   * 
   * @param dirname
   *          The path and name of the directory to list.
   * @param listDirs
   *          If <code>true</code> directories are listed, if <code>false</code>
   *          directories are not listed.
   * @param listFiles
   *          If <code>true</code> files are listed, if <code>false</code> files
   *          are not listed.
   * @param listPaths
   *          If <code>true</code> the absolute paths will be listed, if
   *          <code>false</code> only filenames will be listed.
   * @return A <code>List</code> of filenames (or filenames with paths) of the
   *         found directories and/or files.
   */
  public static List<String> list_Dirs_Files_Paths(final String dirname, final boolean listDirs,
      final boolean listFiles, final boolean listPaths) {
    return list_Dirs_Files_Hidden_Paths(dirname, listDirs, listFiles, true, listPaths);
  }

  /**
   * Lists directories and/or filenames in the given directory.
   * 
   * @param dirname
   *          The path and name of the directory to list.
   * @param listPaths
   *          If <code>true</code> the absolute paths will be listed, if
   *          <code>false</code> only filenames will be listed.
   * @return A <code>List</code> of filenames (or filenames with paths) of the
   *         found files larger than 0 bytes.
   */
  public static List<String> list_ExistingFilesRecursive(final String dirname) {
    final List<String> allFiles = list_Dirs_Files_Paths(dirname, false, true, true);
    final List<String> files = new ArrayList<String>();
    File file = null;

    // remove not existing or zero size files
    for (final String filename : allFiles) {
      file = new File(filename);

      if ((file.exists()) && (file.length() > 0)) {
        files.add(filename);
      }

    }
    Collections.sort(files);

    final List<String> dirs = list_Dirs_Files_Paths(dirname, true, false, true);
    for (final String dir : dirs) {
      files.addAll(list_ExistingFilesRecursive(dir));
    }

    return files;
  }

  public static List<String> fileListToStringList(final List<File> Files) {
    final List<String> list = new ArrayList<String>();

    for (final File File : Files) {
      list.add(File.toString());
    }
    return list;
  }

  public static List<File> stringListToFileList(final List<String> strings) {
    final List<File> list = new ArrayList<File>();

    for (final String string : strings) {
      list.add(new File(string));
    }
    return list;
  }

}
