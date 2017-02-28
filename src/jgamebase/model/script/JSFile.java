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

package jgamebase.model.script;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import jgamebase.tools.FileTools;

public class JSFile extends File {

  public JSFile(final String pathname) {
    super(pathname);
  }

  public JSFile(final URI uri) {
    super(uri);
  }

  public JSFile(final String parent, final String child) {
    super(parent, child);
  }

  public JSFile(final File parent, final String child) {
    super(parent, child);
  }

  public String read() {
    try {
      return FileTools.readFileAsString(getAbsolutePath());
    } catch (final IOException e) {
      return "";
    }
  }

  public String read(final int length) {
    final String data = read();
    return data.substring(0, Math.min(length, data.length()));
  }

  public boolean remove() {
    return delete();
  }
}
