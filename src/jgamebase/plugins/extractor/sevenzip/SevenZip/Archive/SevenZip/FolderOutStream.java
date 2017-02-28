package jgamebase.plugins.extractor.sevenzip.SevenZip.Archive.SevenZip;

import jgamebase.plugins.extractor.sevenzip.Common.BoolVector;
import jgamebase.plugins.extractor.sevenzip.SevenZip.HRESULT;
import jgamebase.plugins.extractor.sevenzip.SevenZip.Archive.IArchiveExtractCallback;
import jgamebase.plugins.extractor.sevenzip.SevenZip.Archive.IInArchive;
import jgamebase.plugins.extractor.sevenzip.SevenZip.Archive.Common.OutStreamWithCRC;

class FolderOutStream extends java.io.OutputStream {
  OutStreamWithCRC _outStreamWithHashSpec;
  java.io.OutputStream _outStreamWithHash;

  ArchiveDatabaseEx _archiveDatabase;
  BoolVector _extractStatuses;
  int _startIndex;
  int _ref2Offset;
  IArchiveExtractCallback _extractCallback;
  boolean _testMode;
  int _currentIndex;
  boolean _fileIsOpen;

  long _filePos;

  public FolderOutStream() {
    _outStreamWithHashSpec = new OutStreamWithCRC();
    _outStreamWithHash = _outStreamWithHashSpec;
  }

  public int Init(final ArchiveDatabaseEx archiveDatabase, final int ref2Offset,
      final int startIndex, final BoolVector extractStatuses,
      final IArchiveExtractCallback extractCallback, final boolean testMode)
      throws java.io.IOException {
    _archiveDatabase = archiveDatabase;
    _ref2Offset = ref2Offset;
    _startIndex = startIndex;

    _extractStatuses = extractStatuses;
    _extractCallback = extractCallback;
    _testMode = testMode;

    _currentIndex = 0;
    _fileIsOpen = false;
    return WriteEmptyFiles();
  }

  int OpenFile() throws java.io.IOException {
    int askMode;
    if (_extractStatuses.get(_currentIndex)) {
      askMode = _testMode ? IInArchive.NExtract_NAskMode_kTest
          : IInArchive.NExtract_NAskMode_kExtract;
    } else {
      askMode = IInArchive.NExtract_NAskMode_kSkip;
    }

    final int index = _startIndex + _currentIndex;

    // RINOK(_extractCallback->GetStream(_ref2Offset + index, &realOutStream,
    // askMode));
    final java.io.OutputStream[] realOutStream2 = new java.io.OutputStream[1]; // TBD
    final int ret = _extractCallback.GetStream(_ref2Offset + index, realOutStream2, askMode);
    if (ret != HRESULT.S_OK) {
      return ret;
    }

    final java.io.OutputStream realOutStream = realOutStream2[0];

    _outStreamWithHashSpec.SetStream(realOutStream);
    _outStreamWithHashSpec.Init();
    if ((askMode == IInArchive.NExtract_NAskMode_kExtract) && (realOutStream == null)) {
      final FileItem fileInfo = _archiveDatabase.Files.get(index);
      if (!fileInfo.IsAnti && !fileInfo.IsDirectory) {
        askMode = IInArchive.NExtract_NAskMode_kSkip;
      }
    }
    return _extractCallback.PrepareOperation(askMode);
  }

  int WriteEmptyFiles() throws java.io.IOException {
    for (; _currentIndex < _extractStatuses.size(); _currentIndex++) {
      final int index = _startIndex + _currentIndex;
      final FileItem fileInfo = _archiveDatabase.Files.get(index);
      if (!fileInfo.IsAnti && !fileInfo.IsDirectory && (fileInfo.UnPackSize != 0)) {
        return HRESULT.S_OK;
      }
      int res = OpenFile();
      if (res != HRESULT.S_OK) {
        return res;
      }

      res = _extractCallback.SetOperationResult(IInArchive.NExtract_NOperationResult_kOK);
      if (res != HRESULT.S_OK) {
        return res;
      }
      _outStreamWithHashSpec.ReleaseStream();
    }
    return HRESULT.S_OK;
  }

  @Override
  public void write(final int b) throws java.io.IOException {
    throw new java.io.IOException("FolderOutStream - write() not implemented");
  }

  @Override
  public void write(final byte[] data, final int off, final int size) throws java.io.IOException // UInt32
                                                                                                 // *processedSize
  {
    int realProcessedSize = 0;
    while (_currentIndex < _extractStatuses.size()) {
      if (_fileIsOpen) {
        final int index = _startIndex + _currentIndex;
        final FileItem fileInfo = _archiveDatabase.Files.get(index);
        final long fileSize = fileInfo.UnPackSize;

        long numBytesToWrite2 = (int) (fileSize - _filePos);
        final int tmp = size - realProcessedSize;
        if (tmp < numBytesToWrite2) {
          numBytesToWrite2 = tmp;
        }

        final int numBytesToWrite = (int) numBytesToWrite2;

        int processedSizeLocal;
        // int res = _outStreamWithHash.Write((const Byte *)data +
        // realProcessedSize,numBytesToWrite, &processedSizeLocal));
        // if (res != HRESULT.S_OK) throw new
        // java.io.IOException("_outStreamWithHash.Write : " + res); // return
        // res;
        processedSizeLocal = numBytesToWrite;
        _outStreamWithHash.write(data, realProcessedSize + off, numBytesToWrite);

        _filePos += processedSizeLocal;
        realProcessedSize += processedSizeLocal;

        if (_filePos == fileSize) {
          boolean digestsAreEqual;
          if (fileInfo.IsFileCRCDefined) {
            digestsAreEqual = (fileInfo.FileCRC == _outStreamWithHashSpec.GetCRC());
          } else {
            digestsAreEqual = true;
          }

          final int res = _extractCallback
              .SetOperationResult(digestsAreEqual ? IInArchive.NExtract_NOperationResult_kOK
                  : IInArchive.NExtract_NOperationResult_kCRCError);
          if (res != HRESULT.S_OK) {
            throw new java.io.IOException("_extractCallback.SetOperationResult : " + res); // return
                                                                                           // res;
          }

          _outStreamWithHashSpec.ReleaseStream();
          _fileIsOpen = false;
          _currentIndex++;
        }
        if (realProcessedSize == size) {
          final int res = WriteEmptyFiles();
          if (res != HRESULT.S_OK) {
            throw new java.io.IOException("WriteEmptyFiles : " + res); // return
                                                                       // res;
          }
          return;// return realProcessedSize;
        }
      } else {
        final int res = OpenFile();
        if (res != HRESULT.S_OK) {
          throw new java.io.IOException("OpenFile : " + res); // return res;
        }
        _fileIsOpen = true;
        _filePos = 0;
      }
    }

    // return size;
  }

  public int FlushCorrupted(final int resultEOperationResult) throws java.io.IOException {
    while (_currentIndex < _extractStatuses.size()) {
      if (_fileIsOpen) {
        final int res = _extractCallback.SetOperationResult(resultEOperationResult);
        if (res != HRESULT.S_OK) {
          return res;
        }

        _outStreamWithHashSpec.ReleaseStream();
        _fileIsOpen = false;
        _currentIndex++;
      } else {
        final int res = OpenFile();
        if (res != HRESULT.S_OK) {
          return res;
        }
        _fileIsOpen = true;
      }
    }
    return HRESULT.S_OK;
  }

  public int WasWritingFinished() {
    final int val = _extractStatuses.size();
    if (_currentIndex == val) {
      return HRESULT.S_OK;
    }
    return HRESULT.E_FAIL;
  }

}
