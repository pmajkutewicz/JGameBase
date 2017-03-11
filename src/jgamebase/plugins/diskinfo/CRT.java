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

package jgamebase.plugins.diskinfo;

import static jgamebase.Const.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import jgamebase.plugins.DiskInfo;

public class CRT implements DiskInfo {

  private static final String[] supportedExtensions = { "crt" };

  private static final String IDENTIFIER = "C64 CARTRIDGE   ";

  private static final int OFFSET_START = 0x00;

  private static final int OFFSET_IDENTIFIER = 0x00;

  private static final int OFFSET_IDENTIFIEREND = 0x10;

  private static final int OFFSET_TYPE = 0x16;

  private static final int OFFSET_EXROMLINE = 0x18;

  private static final int OFFSET_GAMELINE = 0x19;

  private static final int OFFSET_NAME = 0x20;

  private static final int OFFSET_END = 0x40;

  private static final String[] TYPES = { "Normal cartridge", "Action Replay",
      "KCS Power Cartridge", "Final Cartridge III", "Simons Basic", "Ocean type 1",
      "Expert Cartridge", "Fun Play, Power Play", "Super Games", "Atomic Power", "Epyx Fastload",
      "Westermann Learning", "Rex Utility", "Final Cartridge I", "Magic Formel",
      "C64 Game System, System 3", "WarpSpeed", "Dinamic", "Zaxxon, Super Zaxxon (SEGA)",
      "Magic Desk, Domark, HES Australia" };

  private String type;

  private String EXRROM_line;

  private String GAME_line;

  private String name;

  private String filesize;

  byte[] buffer;

  public CRT() {
    log.info("Loaded plugin: disk info 'CRT'.");
  }

  @Override
  public void load(final String filename) throws IOException {
    final File file = new File(filename);
    buffer = new byte[OFFSET_END - OFFSET_START];

    if (!file.exists()) {
      throw new FileNotFoundException(filename);
    }

    final FileInputStream in = new FileInputStream(file);
    final int read = in.read(buffer, OFFSET_START, OFFSET_END - OFFSET_START);
    in.close();

    final String identifier = bufferToString(OFFSET_IDENTIFIER, OFFSET_IDENTIFIEREND);
    if ((!identifier.equals(IDENTIFIER)) || (read < (OFFSET_END - OFFSET_START))) { // no
                                                                                    // CRT
                                                                                    // file
      throw new FileNotFoundException(filename);
    }

    type = TYPES[buffer[OFFSET_TYPE]];

    EXRROM_line = (buffer[OFFSET_EXROMLINE] == 0) ? "inactive" : "active";
    GAME_line = (buffer[OFFSET_GAMELINE] == 0) ? "inactive" : "active";

    name = bufferToString(OFFSET_NAME, OFFSET_END).trim();

    filesize = (new File(filename).length() / 1024) + "KB";
  }

  private String bufferToString(final int start, final int end) {
    final StringBuilder s = new StringBuilder();
    for (int i = start; i < end; i++) {
      s.append((char) buffer[i]);
    }

    return s.toString();
  }

  @Override
  public String[] getSupportedExtensions() {
    return supportedExtensions;
  }

  @Override
  public boolean supportsExtension(String extensionToFind) {
    extensionToFind = extensionToFind.toLowerCase();
    for (final String supportedExtension : supportedExtensions) {
      final String extension = supportedExtension.toLowerCase();
      if (extension.equals(extensionToFind)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String[] getDirectory() {
    final String[] files = { "\"" + name + "\"" };
    return files;
  }

  @Override
  public String getFilenameAt(final int pos) {
    if (pos == 0) {
      return name;
    }
    return "";
  }

  @Override
  public String getNativeFilenameAt(final int pos) {
    return getFilenameAt(pos);
  }

  @Override
  public String getHeader() {
    return filesize + " \"" + type + "\"";
  }

  @Override
  public String getFooter() {
    return "EXROM line: " + EXRROM_line + "   " + "GAME line: " + GAME_line;
  }

  @Override
  public boolean isBootable() {
    return true;
  }
}
