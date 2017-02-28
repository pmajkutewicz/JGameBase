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

import java.io.IOException;
import java.util.List;

import jgamebase.plugins.Extractor;
import jgamebase.plugins.extractor.sevenzip.SevenZip.ArchiveExtractCallback;
import jgamebase.plugins.extractor.sevenzip.SevenZip.HRESULT;
import jgamebase.plugins.extractor.sevenzip.SevenZip.IInStream;
import jgamebase.plugins.extractor.sevenzip.SevenZip.MyRandomAccessFile;
import jgamebase.plugins.extractor.sevenzip.SevenZip.Archive.IArchiveExtractCallback;
import jgamebase.plugins.extractor.sevenzip.SevenZip.Archive.IInArchive;
import jgamebase.plugins.extractor.sevenzip.SevenZip.Archive.SevenZip.Handler;
import jgamebase.tools.ListerTools;
import jgamebase.tools.TempDir;

public class SevenZip implements Extractor {

  private static final String[] supportedExtensions = { "7z", "7zip" };

  public SevenZip() {
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

    log.info("Extract file '" + filename + "' to temp dir '" + TempDir.getPath() + "'.");

    // extract 7zip
    IInStream istream = null;
    IInArchive archive = null;

    try {
      istream = new MyRandomAccessFile(filename, "r");
      archive = new Handler();
      archive.Open(istream);
      final ArchiveExtractCallback extractCallbackSpec = new ArchiveExtractCallback();
      final IArchiveExtractCallback extractCallback = extractCallbackSpec;
      extractCallbackSpec.Init(archive);

      extractCallbackSpec.SetDirPath(TempDir.getPath().getCanonicalPath());

      final int res = archive.Extract(null, -1, IInArchive.NExtract_NAskMode_kExtract,
          extractCallback);

      if ((res != HRESULT.S_OK) || ((res == HRESULT.S_OK) && (extractCallbackSpec.NumErrors != 0))) {
        log.error("ERROR while extracting 7zip archive '" + filename + "'!");
      }

    } catch (final Exception e) {
      e.printStackTrace();
    } finally {
      if (archive != null) {
        try {
          archive.close();
        } catch (final IOException e) {
        }
      }
    }

    // return list of files in temp
    return ListerTools.list_Dirs_Files_Paths(TempDir.getPath().toString(), false, true, true);
  }

}
