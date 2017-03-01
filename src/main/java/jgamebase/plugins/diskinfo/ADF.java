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

public class ADF implements DiskInfo {

  private static final String[] supportedExtensions = { "adf" };

  // data of the whole disk image
  private byte[] nativeDisk;

  private String header;

  private String footer;

  private static final int BYTES_PER_SECTOR = 512;
  private static final int SECTORS_PER_TRACK = 11;
  private static final int TRACKS_PER_CYLINDER = 2;
  private static final int CYLINDERS_PER_DISK = 80;
  private static final int SECTORS_PER_DISK = CYLINDERS_PER_DISK * TRACKS_PER_CYLINDER
      * SECTORS_PER_TRACK;
  private static final int IMAGE_SIZE = BYTES_PER_SECTOR * SECTORS_PER_DISK;

  private static final int OFFSET_CHECKSUM = 4;
  private static final int OFFSET_ROOT_BLOCK_NUMBER = 8;

  private static final int DEFAULT_ROOT_BLOCK_NUMBER = 880;

  private boolean isDosDisk = false;
  private long rootBlockNumber;
  private long storedRootChecksum;
  private long calculatedChecksum;

  public ADF() {
    log.info("Loaded plugin: disk info 'ADF'.");
  }

  @Override
  public void load(final String filename) throws IOException {
    final File file = new File(filename);
    final int filesize = (int) file.length();

    // clear variables
    nativeDisk = new byte[filesize];
    header = "";
    footer = "";

    if (!file.exists()) {
      throw new FileNotFoundException(filename);
    }

    // read data
    final FileInputStream input = new FileInputStream(filename);
    input.read(nativeDisk);
    input.close();

    isDosDisk = false;
    if ((getByteAt(0) == 'D') && (getByteAt(1) == 'O') && (getByteAt(2) == 'S')) {
      isDosDisk = true;
    }

    storedRootChecksum = getLongAt(OFFSET_CHECKSUM);
    calculateRootChecksum();
    rootBlockNumber = getLongAt(OFFSET_ROOT_BLOCK_NUMBER);

    // try to read disk name
    if (rootBlockNumber == DEFAULT_ROOT_BLOCK_NUMBER) {
      final int length = getByteAt((int) (((BYTES_PER_SECTOR * rootBlockNumber) + BYTES_PER_SECTOR) - 80));

      for (int i = 0; i < length; i++) {
        header += Character
            .valueOf((char) (getByteAt((int) ((((BYTES_PER_SECTOR * rootBlockNumber) + BYTES_PER_SECTOR) - 79) + i))));
      }
    }
  }

  private void calculateRootChecksum() {
    long checksum = 0L;
    long precsum = 0L;

    for (int i = 0; i < (1024 / 4); i++) {

      if (i != 1) { // skip old checksum
        precsum = checksum;
        checksum = (precsum + getLongAt(i * 4));

        if ((checksum & 0xffffffffl) < (precsum & 0xffffffffl)) {
          checksum++;
        }
      }
    }

    calculatedChecksum = (~checksum) & 0xffffffffl;
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

  private int getByteAt(final int pos) {
    return (nativeDisk[pos] & 0xff);
  }

  private long getLongAt(final int pos) {
    return (((getByteAt(pos) << 24) & 0xff000000l) | ((getByteAt(pos + 1) << 16) & 0x00ff0000l)
        | ((getByteAt(pos + 2) << 8) & 0x0000ff00l) | (getByteAt(pos + 3) & 0x000000ffl)) & 0xffffffffl;
  }

  @Override
  public String[] getDirectory() {
    final String[] entries = new String[0];
    return entries;
  }

  @Override
  public String getFilenameAt(final int pos) {
    return "";
  }

  @Override
  public String getNativeFilenameAt(final int pos) {
    return "";
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
    return (isDosDisk && (storedRootChecksum == calculatedChecksum));
  }
}
