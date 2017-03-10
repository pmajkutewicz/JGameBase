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

import jgamebase.Const;

public class TempDir {

  private static final String TEMP_DIRNAME = Const.NAME_JGAMEBASE_LC;
  private static File dir;

  static {
    dir = new File(System.getProperty("java.io.tmpdir"), TempDir.TEMP_DIRNAME);
  }

  // don't let anyone instantiate this class
  private TempDir() {
  }

  public static void removePath() {
    FileTools.deleteAll(dir); // delete content
    dir.delete(); // delete dir
  }

  public static File getPath() {
    if (!dir.exists()) {
      dir.mkdirs();
    }
    return dir;
  }

  public static File getCleanPath() {
    removePath();
    return getPath();
  }

}
