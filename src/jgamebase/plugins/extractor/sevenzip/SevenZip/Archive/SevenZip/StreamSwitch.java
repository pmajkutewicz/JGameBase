package jgamebase.plugins.extractor.sevenzip.SevenZip.Archive.SevenZip;

import jgamebase.plugins.extractor.sevenzip.Common.ByteBuffer;
import jgamebase.plugins.extractor.sevenzip.Common.ObjectVector;
import jgamebase.plugins.extractor.sevenzip.SevenZip.HRESULT;

class StreamSwitch {
  InArchive _archive;
  boolean _needRemove;

  public StreamSwitch() {
    _needRemove = false;
  }

  public void close() {
    Remove();
  }

  void Remove() {
    if (_needRemove) {
      _archive.DeleteByteStream();
      _needRemove = false;
    }
  }

  void Set(final InArchive archive, final ByteBuffer byteBuffer) {
    Set(archive, byteBuffer.data(), byteBuffer.GetCapacity());
  }

  void Set(final InArchive archive, final byte[] data, final int size) {
    Remove();
    _archive = archive;
    _archive.AddByteStream(data, size);
    _needRemove = true;
  }

  int Set(final InArchive archive, final ObjectVector<ByteBuffer> dataVector)
      throws java.io.IOException {
    Remove();
    final int external = archive.ReadByte();
    if (external != 0) {
      final int dataIndex = archive.ReadNum();
      Set(archive, dataVector.get(dataIndex));
    }
    return HRESULT.S_OK;
  }
}
