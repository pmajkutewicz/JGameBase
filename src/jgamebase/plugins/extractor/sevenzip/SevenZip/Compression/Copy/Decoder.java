package jgamebase.plugins.extractor.sevenzip.SevenZip.Compression.Copy;

import jgamebase.plugins.extractor.sevenzip.SevenZip.HRESULT;
import jgamebase.plugins.extractor.sevenzip.SevenZip.ICompressCoder;
import jgamebase.plugins.extractor.sevenzip.SevenZip.ICompressProgressInfo;

public class Decoder implements ICompressCoder {

  static final int kBufferSize = 1 << 17;

  @Override
  public int Code(final java.io.InputStream inStream, // , ISequentialInStream
      final java.io.OutputStream outStream, // ISequentialOutStream
      final long outSize, final ICompressProgressInfo progress) throws java.io.IOException {

    final byte[] _buffer = new byte[kBufferSize];
    long TotalSize = 0;

    for (;;) {
      int realProcessedSize;
      int size = kBufferSize;

      if (outSize != -1) {
        if (size > (outSize - TotalSize)) {
          size = (int) (outSize - TotalSize);
        }
      }

      realProcessedSize = inStream.read(_buffer, 0, size);
      if (realProcessedSize == -1) {
        break;
      }
      outStream.write(_buffer, 0, realProcessedSize);
      TotalSize += realProcessedSize;
      if (progress != null) {
        final int res = progress.SetRatioInfo(TotalSize, TotalSize);
        if (res != HRESULT.S_OK) {
          return res;
        }
      }
    }
    return HRESULT.S_OK;
  }
}
