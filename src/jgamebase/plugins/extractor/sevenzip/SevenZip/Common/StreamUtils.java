package jgamebase.plugins.extractor.sevenzip.SevenZip.Common;

import java.io.IOException;

public class StreamUtils {
  static public int ReadStream(final java.io.InputStream stream, final byte[] data, final int off,
      int size) throws IOException {
    int processedSize = 0;

    while (size != 0) {
      final int processedSizeLoc = stream.read(data, off + processedSize, size);
      if (processedSizeLoc > 0) {
        processedSize += processedSizeLoc;
        size -= processedSizeLoc;
      }
      if (processedSizeLoc == -1) {
        if (processedSize > 0) {
          return processedSize;
        }
        return -1; // EOF
      }
    }
    return processedSize;
  }

  // HRESULT WriteStream(ISequentialOutStream *stream, const void *data, UInt32
  // size, UInt32 *processedSize);
}
