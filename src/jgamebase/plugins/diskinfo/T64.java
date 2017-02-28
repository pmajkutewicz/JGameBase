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
import java.util.ArrayList;
import java.util.List;

import jgamebase.plugins.DiskInfo;

public class T64 implements DiskInfo {

  private static final String[] supportedExtensions = { "t64" };

  private static final String IDENTIFIER = "C64";

  @SuppressWarnings("unused")
  private static final int OFFSET_START = 0x00;

  private static final int OFFSET_IDENTIFIER = 0x00;

  private static final int OFFSET_IDENTIFIEREND = 0x03;

  private static final int OFFSET_DIR_ENTRIES = 0x24;

  private static final int OFFSET_NAME = 0x28;

  private static final int OFFSET_DIR_START = 0x40;

  private static final int DIR_ENTRY_SIZE = 0x20;

  static class CbmFile {
    private int type;

    private String name;

    private String nativeName;

    private int size;

    // Constants
    private final String[] types = { "DEL", "SEQ", "PRG", "USR", "REL" };

    public CbmFile() {
      type = 0;
      name = "";
      nativeName = "";
      size = 0;
    }

    public int getType() {
      return type;
    }

    public String getName() {
      return name;
    }

    public String getNativeName() {
      return nativeName;
    }

    public void setType(final int b) {
      type = b;
    }

    public void setName(final String string) {
      name = string;
    }

    public void setNativeName(final String string) {
      nativeName = string;
    }

    public int getSize() {
      return size;
    }

    public void setSize(final int i) {
      size = i;
    }

    public String getFileType(final int type) {
      return types[type];
    }

    @Override
    public String toString() {
      final Object[] param = { Integer.valueOf(size), "\"" + name + "\"", types[type] };
      return String.format("%1$-4s %2$-18s %3$s", param);
    }
  }

  private static final int[] cbmToAscii = {
      // invisible
      32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
      32, 32,
      32,
      32,
      32,
      32,
      32,
      32,
      32,

      // visible
      32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54,
      55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77,
      78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100,
      101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118,
      119, 120, 121, 122, 123, 124,
      125,
      126,
      127,

      // invisible
      32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
      32, 32, 32, 32, 32, 32, 32, 32,
      32,
      32,

      // visible
      161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178,
      179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190,
      191,

      // cbm font trickery: codes 192-223 are 96-127
      96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114,
      115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127,

      // cbm font trickery: codes 224-254 are 160-190
      32, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178,
      179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191,

      // cbm font trickery: codes 255 is 126
      126 };

  private byte[] cbmTape;

  private String header;

  private String footer;

  private List<CbmFile> cbmFiles;

  public T64() {
    log.info("Loaded plugin: disk info 'T64'.");
  }

  @Override
  public void load(final String filename) throws IOException {
    final File file = new File(filename);
    final int filesize = (int) file.length();

    // clear variables
    cbmTape = new byte[filesize];
    header = "";
    footer = "";
    cbmFiles = new ArrayList<CbmFile>();

    if (!file.exists()) {
      throw new FileNotFoundException(filename);
    }

    // read data
    final FileInputStream input = new FileInputStream(filename);
    input.read(cbmTape);
    input.close();

    final String identifier = bufferToString(OFFSET_IDENTIFIER, OFFSET_IDENTIFIEREND);
    if (!identifier.equals(IDENTIFIER)) { // no T64 file
      throw new FileNotFoundException(filename);
    }

    header = "\"";
    for (int i = 0; i < (OFFSET_DIR_START - OFFSET_NAME); i++) {
      header += Character.valueOf((char) (cbmToAscii[getCbmTapeValue(OFFSET_NAME + i)]));
    }
    header += "\"";

    final int entry_count = getCbmTapeValue(OFFSET_DIR_ENTRIES)
        + (256 * getCbmTapeValue(OFFSET_DIR_ENTRIES + 1));
    for (int i = 0; i < entry_count; i++) {
      final CbmFile cbmFile = readDirectoryEntry(OFFSET_DIR_START + (i * DIR_ENTRY_SIZE));
      cbmFiles.add(cbmFile);
    }
  }

  /**
   * Reads a single directory entry of the d64 image
   */
  private CbmFile readDirectoryEntry(final int dataPosition) {
    int position;
    final CbmFile file = new CbmFile();

    file.setType((getCbmTapeValue(dataPosition + 1) & 7));

    for (position = 0; position <= 15; position++) {
      file.setNativeName(file.getNativeName()
          + (char) getCbmTapeValue(dataPosition + 16 + position));
      file.setName(file.getName()
          + (char) (cbmToAscii[getCbmTapeValue(dataPosition + 16 + position)]));
    }
    file.setName(file.getName().trim());

    String nativeName = file.getNativeName().trim().toLowerCase();
    nativeName = replaceAll(nativeName, '[', '{');
    nativeName = replaceAll(nativeName, ']', '}');
    file.setNativeName(nativeName);

    file.setNativeName(file.getNativeName().toLowerCase().trim());

    file.setSize(((getCbmTapeValue(dataPosition + 04) + (getCbmTapeValue(dataPosition + 05) * 256)) - (getCbmTapeValue(dataPosition + 02) + (getCbmTapeValue(dataPosition + 03) * 256))) / 256);

    return file;
  }

  private String replaceAll(String s, final char what, final char withWhat) {
    while (s.indexOf(what) != -1) {
      s = s.replace(what, withWhat);
    }
    return s;
  }

  private int getCbmTapeValue(final int position) {
    return (cbmTape[position] & 0xff);
  }

  private String bufferToString(final int start, final int end) {
    final StringBuffer s = new StringBuffer();
    for (int i = start; i < end; i++) {
      s.append((char) getCbmTapeValue(i));
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
    final String[] entries = new String[cbmFiles.size()];

    final int i = 0;
    for (final CbmFile cbmFile : cbmFiles) {
      entries[i] = cbmFile.toString();
    }
    return entries;
  }

  @Override
  public String getFilenameAt(final int pos) {
    return cbmFiles.get(pos).getName();
  }

  @Override
  public String getNativeFilenameAt(final int pos) {
    return cbmFiles.get(pos).getNativeName();
  }

  @Override
  public String getHeader() {
    return header;
  }

  @Override
  public String getFooter() {
    return footer;
  }

  @Override
  public boolean isBootable() {
    return false;
  }
}
