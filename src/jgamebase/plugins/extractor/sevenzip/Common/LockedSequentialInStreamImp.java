package jgamebase.plugins.extractor.sevenzip.Common;

public class LockedSequentialInStreamImp extends java.io.InputStream {
  LockedInStream _lockedInStream;
  long _pos;

  public LockedSequentialInStreamImp() {
  }

  public void Init(final LockedInStream lockedInStream, final long startPos) {
    _lockedInStream = lockedInStream;
    _pos = startPos;
  }

  @Override
  public int read() throws java.io.IOException {
    throw new java.io.IOException("LockedSequentialInStreamImp : read() not implemented");
    /*
     * int ret = _lockedInStream.read(_pos); if (ret == -1) return -1; // EOF
     * 
     * _pos += 1;
     * 
     * return ret;
     */
  }

  @Override
  public int read(final byte[] data, final int off, final int size) throws java.io.IOException {
    final int realProcessedSize = _lockedInStream.read(_pos, data, off, size);
    if (realProcessedSize == -1) {
      return -1; // EOF
    }

    _pos += realProcessedSize;

    return realProcessedSize;
  }

}
