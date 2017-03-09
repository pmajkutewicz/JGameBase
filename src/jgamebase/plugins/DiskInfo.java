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

package jgamebase.plugins;

import java.io.IOException;

import jgamebase.model.Plugin;

public interface DiskInfo extends Plugin {

  public void load(String filename) throws IOException;

  public String[] getDirectory();

  public String getFilenameAt(int pos);

  public String getNativeFilenameAt(int pos);

  public String getHeader();

  public String getFooter();

  public boolean isBootable();

}