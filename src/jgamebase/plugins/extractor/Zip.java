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

package jgamebase.plugins.extractor;

import static jgamebase.Const.log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import jgamebase.model.Paths;
import jgamebase.plugins.Extractor;
import jgamebase.tools.TempDir;

public class Zip implements Extractor {

  private static final String[] supportedExtensions = { "zip" };

  private final byte[] buffer = new byte[8192];

  public Zip() {
    log.info("Loaded plugin: extractor '" + supportedExtensions[0].toUpperCase() + "'.");
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

  public List<String> extractToCleanTempDir(final String filename) throws IOException {
    TempDir.getCleanPath();
    return extractToTempDir(filename);
  }
  
  @Override
  public List<String> extractToTempDir(final String filename) throws IOException {
    
    Enumeration<? extends ZipEntry> entries;

    ZipFile zipFile;
    final ArrayList<String> fileNames = new ArrayList<String>();

    log.info("Extract file '" + filename + "' to temp dir '" + TempDir.getPath() + "'.");
    zipFile = new ZipFile(filename);

    entries = zipFile.entries();

    while (entries.hasMoreElements()) {
      final ZipEntry entry = entries.nextElement();

      log.info("extract '" + entry.getName() + "'");
      final String entryFilename = new File(TempDir.getPath(), entry.getName()).toString();

      if (entry.isDirectory()) {
        (new File(entryFilename)).mkdirs();
      } else {
        final InputStream in = zipFile.getInputStream(entry);

        // create possible parent directories
        (new File(Paths.getPathOnly(entryFilename))).mkdirs();
        final OutputStream out = new BufferedOutputStream(new FileOutputStream(entryFilename));

        int len;

        while ((len = in.read(buffer)) >= 0) {
          out.write(buffer, 0, len);
        }

        in.close();
        out.close();

        fileNames.add(new File(TempDir.getPath(), entry.getName()).toString());
      }
    }

    zipFile.close();

    return fileNames;
  }

}
