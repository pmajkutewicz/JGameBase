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

package jgamebase.plugins.musicinfo;

import static jgamebase.Const.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import jgamebase.plugins.MusicInfo;

public class SID implements MusicInfo {

  private static final String[] supportedExtensions = { "sid", "psid" };

  private static final String IDENTIFIER1 = "PSID";

  private static final String IDENTIFIER2 = "RSID";

  private static final int OFFSET_START = 0x00;

  private static final int OFFSET_IDENTIFIER = 0x00;

  private static final int OFFSET_VERSION = 0x04;

  private static final int OFFSET_SONGNUMBER = 0x0E;

  private static final int OFFSET_DEFAULTSONG = 0x10;

  private static final int OFFSET_NAME = 0x16;

  private static final int OFFSET_AUTHOR = 0x36;

  private static final int OFFSET_COPYRIGHT = 0x56;

  private static final int OFFSET_END = 0x76;

  private String songNumber;

  private String defaultSong;

  private String name;

  private String author;

  private String copyright;

  byte[] buffer;

  public SID() {
    log.info("Loaded plugin: music info 'SID'.");
  }

  @Override
  public void load(final File file) throws IOException {
    if (file == null) {
      return;
    }

    buffer = new byte[OFFSET_END - OFFSET_START];

    if (!file.exists()) {
      throw new FileNotFoundException(file.getName());
    }

    FileInputStream in = null;
    int read = 0;
    try {
      in = new FileInputStream(file);
      read = in.read(buffer, OFFSET_START, OFFSET_END - OFFSET_START);
    } catch (final Exception e) {
      e.printStackTrace();
    } finally {
      if (in != null) {
        in.close();
      }
    }

    final String identifier = bufferToString(OFFSET_IDENTIFIER, OFFSET_VERSION);
    if ((!identifier.equals(IDENTIFIER1) && !identifier.equals(IDENTIFIER2))
        || (read < (OFFSET_END - OFFSET_START))) {
      // no SID file
      throw new FileNotFoundException(file.getName());
    }

    songNumber = ((buffer[OFFSET_SONGNUMBER] * 256) + buffer[OFFSET_SONGNUMBER + 1]) + "";
    defaultSong = ((buffer[OFFSET_DEFAULTSONG] * 256) + buffer[OFFSET_DEFAULTSONG + 1]) + "";
    name = bufferToString(OFFSET_NAME, OFFSET_AUTHOR).trim();
    author = bufferToString(OFFSET_AUTHOR, OFFSET_COPYRIGHT).trim();
    copyright = bufferToString(OFFSET_COPYRIGHT, OFFSET_END).trim();
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

  /*
   * (non-Javadoc)
   * 
   * @see gamebase.plugin.Plugin_Music#getAuthor()
   */
  @Override
  public String getAuthor() {
    return author == null ? "" : author;
  }

  /*
   * (non-Javadoc)
   * 
   * @see gamebase.plugin.Plugin_Music#getCopyright()
   */
  @Override
  public String getCopyright() {
    return copyright == null ? "" : copyright;
  }

  /*
   * (non-Javadoc)
   * 
   * @see gamebase.plugin.Plugin_Music#getDefaultSong()
   */
  @Override
  public String getDefaultSong() {
    return defaultSong == null ? "" : defaultSong;
  }

  /*
   * (non-Javadoc)
   * 
   * @see gamebase.plugin.Plugin_Music#getName()
   */
  @Override
  public String getName() {
    return name == null ? "" : name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see gamebase.plugin.Plugin_Music#getSongNumber()
   */
  @Override
  public String getSongNumber() {
    return songNumber == null ? "" : songNumber;
  }
}
