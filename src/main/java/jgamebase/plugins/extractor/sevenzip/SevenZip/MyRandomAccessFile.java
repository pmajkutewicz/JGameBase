package jgamebase.plugins.extractor.sevenzip.SevenZip;

public class MyRandomAccessFile extends jgamebase.plugins.extractor.sevenzip.SevenZip.IInStream {

  java.io.RandomAccessFile _file;

  public MyRandomAccessFile(final String filename, final String mode) throws java.io.IOException {
    _file = new java.io.RandomAccessFile(filename, mode);
  }

  @Override
  public long Seek(final long offset, final int seekOrigin) throws java.io.IOException {
    if (seekOrigin == STREAM_SEEK_SET) {
      _file.seek(offset);
    } else if (seekOrigin == STREAM_SEEK_CUR) {
      _file.seek(offset + _file.getFilePointer());
    }
    return _file.getFilePointer();
  }

  @Override
  public int read() throws java.io.IOException {
    return _file.read();
  }

  @Override
  public int read(final byte[] data, final int off, final int size) throws java.io.IOException {
    return _file.read(data, off, size);
  }

  public int read(final byte[] data, final int size) throws java.io.IOException {
    return _file.read(data, 0, size);
  }

  @Override
  public void close() throws java.io.IOException {
    _file.close();
    _file = null;
  }
}
