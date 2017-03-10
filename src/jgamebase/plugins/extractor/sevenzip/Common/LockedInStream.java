package jgamebase.plugins.extractor.sevenzip.Common;

import jgamebase.plugins.extractor.sevenzip.SevenZip.IInStream;

public class LockedInStream {
  IInStream _stream;

  public LockedInStream() {
  }

  public void Init(final IInStream stream) {
    _stream = stream;
  }

  /*
   * really too slow, don't use ! public synchronized int read(long startPos)
   * throws java.io.IOException { //
   * NWindows::NSynchronization::CCriticalSectionLock lock(_criticalSection);
   * _stream.Seek(startPos, IInStream.STREAM_SEEK_SET); return _stream.read(); }
   */

  public synchronized int read(final long startPos, final byte[] data, final int size)
      throws java.io.IOException {
    // NWindows::NSynchronization::CCriticalSectionLock lock(_criticalSection);
    _stream.Seek(startPos, IInStream.STREAM_SEEK_SET);
    return _stream.read(data, 0, size);
  }

  public synchronized int read(final long startPos, final byte[] data, final int off, final int size)
      throws java.io.IOException {
    // NWindows::NSynchronization::CCriticalSectionLock lock(_criticalSection);
    _stream.Seek(startPos, IInStream.STREAM_SEEK_SET);
    return _stream.read(data, off, size);
  }
}
