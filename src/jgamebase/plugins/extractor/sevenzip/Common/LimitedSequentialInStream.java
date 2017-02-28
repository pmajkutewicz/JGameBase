package jgamebase.plugins.extractor.sevenzip.Common;

public class LimitedSequentialInStream extends java.io.InputStream {
  java.io.InputStream _stream; // ISequentialInStream
  long _size;
  long _pos;
  boolean _wasFinished;

  public LimitedSequentialInStream() {
  }

  public void SetStream(final java.io.InputStream stream) { // ISequentialInStream
    _stream = stream;
  }

  public void Init(final long streamSize) {
    _size = streamSize;
    _pos = 0;
    _wasFinished = false;
  }

  @Override
  public int read() throws java.io.IOException {
    final int ret = _stream.read();
    if (ret == -1) {
      _wasFinished = true;
    }
    return ret;
  }

  @Override
  public int read(final byte[] data, final int off, final int size) throws java.io.IOException {
    long sizeToRead2 = (_size - _pos);
    if (size < sizeToRead2) {
      sizeToRead2 = size;
    }

    final int sizeToRead = (int) sizeToRead2;

    if (sizeToRead > 0) {
      final int realProcessedSize = _stream.read(data, off, sizeToRead);
      if (realProcessedSize == -1) {
        _wasFinished = true;
        return -1;
      }
      _pos += realProcessedSize;
      return realProcessedSize;
    }

    return -1; // EOF
  }
}
