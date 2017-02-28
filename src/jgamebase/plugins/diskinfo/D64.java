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

/**
 * @author Wolfram Heyer
 * @author Frank Gerbig
 */
public class D64 implements DiskInfo {

  private static final String[] supportedExtensions = { "d64" };

  static class CbmFile {
    private boolean scratched;

    private int type;

    private boolean locked;

    private boolean closed;

    private String name;

    private String nativeName;

    private int size;

    // Constants
    private final String[] types = { "DEL", "SEQ", "PRG", "USR", "REL" };

    public CbmFile() {
      scratched = true;
      type = 0;
      locked = false;
      closed = false;
      name = "";
      nativeName = "";
      size = 0;
    }

    public boolean isClosed() {
      return closed;
    }

    public boolean isLocked() {
      return locked;
    }

    public boolean isScratched() {
      return scratched;
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

    public void setClosed(final boolean b) {
      closed = b;
    }

    public void setLocked(final boolean b) {
      locked = b;
    }

    public void setScratched(final boolean b) {
      scratched = b;
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
      String flags = "";
      flags += isLocked() ? "<" : "";
      flags += isClosed() ? "" : "*";

      final Object[] param = { Integer.valueOf(size), "\"" + name + "\"", types[type] + flags };
      return String.format("%1$-4s %2$-18s %3$s", param);
    }
  }

  private static final int[] trackOffset = { 0, 0, 5376, 10752, 16128, 21504, 26880, 32256, 37632,
      43008, 48384, 53760, 59136, 64512, 69888, 75264, 80640, 86016, 91392, 96256, 101120, 105984,
      110848, 115712, 120576, 125440, 130048, 134656, 139264, 143872, 148480, 153088, 157440,
      161792, 166144, 170496, 174848, 179200, 183552, 187904, 192256 };

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

  // data of the whole d64 image
  private byte[] cbmDisk;

  private String header;

  private String footer;

  private List<CbmFile> cbmFiles;

  // Track number of the last track
  private static final int lastTrack = 35;

  public D64() {
    log.info("Loaded plugin: disk info 'D64'.");
  }

  @Override
  public void load(final String filename) throws IOException {
    final File file = new File(filename);
    final int filesize = (int) file.length();

    // clear variables
    cbmDisk = new byte[filesize];
    header = "";
    footer = "";
    cbmFiles = new ArrayList<CbmFile>();

    if (!file.exists()) {
      throw new FileNotFoundException(filename);
    }

    // read data
    final FileInputStream input = new FileInputStream(filename);
    input.read(cbmDisk);
    input.close();

    // read BAM
    int track = 18;
    int sector = 1;

    header = "0 \"";
    for (int i = 0; i <= 15; i++) {
      header += Character
          .valueOf((char) (cbmToAscii[getCbmDiskValue(trackOffset[track] + 144 + i)]));
    }
    header += "\" ";
    for (int i = 0; i <= 4; i++) {
      header += Character
          .valueOf((char) (cbmToAscii[getCbmDiskValue(trackOffset[track] + 162 + i)]));
    }

    int blocksFree = 0;
    for (int i = 1; i <= lastTrack; i++) {
      if (i != 18) {
        blocksFree += (byte) getCbmDiskValue(trackOffset[18] + (i * 4));
      }
    }
    footer = blocksFree + " BLOCKS FREE.";

    // read directory
    do {
      for (int i = 0; i <= 7; i++) {
        final CbmFile cbmFile = readDirectoryEntry(trackOffset[track] + (256 * sector) + (i * 32));
        if (!cbmFile.isScratched()) {
          cbmFiles.add(cbmFile);
        }
      }
      track = getCbmDiskValue(trackOffset[track] + (256 * sector) + 0);
      sector = getCbmDiskValue(trackOffset[track] + (256 * sector) + 1);
    } while (track != 0);
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

  /**
   * Reads a single directory entry of the d64 image
   */
  private CbmFile readDirectoryEntry(final int dataPosition) {
    int position;
    final CbmFile file = new CbmFile();

    file.setScratched(getCbmDiskValue(dataPosition + 2) == 0);
    file.setType((getCbmDiskValue(dataPosition + 2) & 7));
    file.setLocked((getCbmDiskValue(dataPosition + 2) & 64) != 0);
    file.setClosed((getCbmDiskValue(dataPosition + 2) & 128) != 0);

    for (position = 0; position <= 15; position++) {
      if (getCbmDiskValue(dataPosition + 5 + position) != 160) {
        file.setNativeName(file.getNativeName()
            + (char) getCbmDiskValue(dataPosition + 5 + position));
        file.setName(file.getName()
            + (char) (cbmToAscii[getCbmDiskValue(dataPosition + 5 + position)]));
      }
    }

    String nativeName = file.getNativeName().toLowerCase();
    nativeName = replaceAll(nativeName, '[', '{');
    nativeName = replaceAll(nativeName, ']', '}');
    file.setNativeName(nativeName);

    file.setSize((char) (getCbmDiskValue(dataPosition + 30) + (getCbmDiskValue(dataPosition + 31) * 256)));

    return file;
  }

  private String replaceAll(String s, final char what, final char withWhat) {
    while (s.indexOf(what) != -1) {
      s = s.replace(what, withWhat);
    }
    return s;
  }

  private int getCbmDiskValue(final int position) {
    return (cbmDisk[position] & 0xff);
  }

  @Override
  public String[] getDirectory() {
    final String[] entries = new String[cbmFiles.size()];

    int i = 0;
    for (final CbmFile cbmFile : cbmFiles) {
      entries[i++] = cbmFile.toString();
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
