package jgamebase.plugins.extractor.sevenzip.SevenZip.Common;


public class SequentialOutStreamImp2 extends java.io.OutputStream {
  byte[] _buffer;
  int _size;
  int _pos;

  public void Init(final byte[] buffer, final int size) {
    _buffer = buffer;
    _pos = 0;
    _size = size;
  }

  @Override
  public void write(final int b) throws java.io.IOException {
    throw new java.io.IOException("SequentialOutStreamImp2 - write() not implemented");
  }

  @Override
  public void write(final byte[] data, final int off, final int size) throws java.io.IOException {
    for (int i = 0; i < size; i++) {
      if (_pos < _size) {
        _buffer[_pos++] = data[off + i];
      } else {
        throw new java.io.IOException("SequentialOutStreamImp2 - can't write");
      }
    }
  }
}
