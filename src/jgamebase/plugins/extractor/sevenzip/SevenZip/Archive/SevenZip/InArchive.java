package jgamebase.plugins.extractor.sevenzip.SevenZip.Archive.SevenZip;

import java.io.IOException;

import jgamebase.plugins.extractor.sevenzip.Common.BoolVector;
import jgamebase.plugins.extractor.sevenzip.Common.ByteBuffer;
import jgamebase.plugins.extractor.sevenzip.Common.CRC;
import jgamebase.plugins.extractor.sevenzip.Common.IntVector;
import jgamebase.plugins.extractor.sevenzip.Common.LongVector;
import jgamebase.plugins.extractor.sevenzip.Common.ObjectVector;
import jgamebase.plugins.extractor.sevenzip.SevenZip.HRESULT;
import jgamebase.plugins.extractor.sevenzip.SevenZip.IInStream;
import jgamebase.plugins.extractor.sevenzip.SevenZip.Archive.Common.BindPair;
import jgamebase.plugins.extractor.sevenzip.SevenZip.Common.StreamUtils;

class InArchive extends Header {

  // CNum
  static public final int kNumMax = 0x7FFFFFFF;
  static public final int kNumNoIndex = 0xFFFFFFFF;

  IInStream _stream; // CMyComPtr<IInStream> _stream;

  ObjectVector<InByte2> _inByteVector;
  InByte2 _inByteBack;

  long _arhiveBeginStreamPosition;
  long _position;

  public InArchive() {
    _inByteVector = new ObjectVector<InByte2>();
    _inByteBack = new InByte2();
  }

  public void AddByteStream(final byte[] buffer, final int size) {
    _inByteVector.add(new InByte2());
    _inByteBack = _inByteVector.Back();
    _inByteBack.Init(buffer, size);
  }

  void DeleteByteStream() {
    _inByteVector.DeleteBack();
    if (!_inByteVector.isEmpty()) {
      _inByteBack = _inByteVector.Back();
    }
  }

  static boolean TestSignatureCandidate(final byte[] testBytes, final int off) {
    for (int i = 0; i < kSignatureSize; i++) {
      // System.out.println(" " + i + ":" + testBytes[i] + " " + kSignature[i]);
      if (testBytes[i + off] != kSignature[i]) {
        return false;
      }
    }
    return true;
  }

  int ReadDirect(final IInStream stream, // IInStream *stream,
      final byte[] data, // void *data,
      final int off, final int size // UInt32 size,
  ) throws IOException {
    final int realProcessedSize = StreamUtils.ReadStream(stream, data, off, size);
    if (realProcessedSize != -1) {
      _position += realProcessedSize;
    }
    return realProcessedSize;
  }

  int ReadDirect(final byte[] data, final int size) throws IOException {
    return ReadDirect(_stream, data, 0, size);
  }

  int SafeReadDirectUInt32() throws IOException {
    int val = 0;
    final byte[] b = new byte[4];

    final int realProcessedSize = ReadDirect(b, 4);
    if (realProcessedSize != 4) {
      throw new IOException("Unexpected End Of Archive"); // throw
                                                          // CInArchiveException(CInArchiveException::kUnexpectedEndOfArchive);
    }

    for (int i = 0; i < 4; i++) {
      val |= ((b[i] & 0xff) << (8 * i));
    }
    return val;
  }

  int ReadUInt32() throws IOException {
    int value = 0;
    for (int i = 0; i < 4; i++) {
      final int b = ReadByte();
      value |= ((b) << (8 * i));
    }
    return value;
  }

  long ReadUInt64() throws IOException {
    long value = 0;
    for (int i = 0; i < 8; i++) {
      final int b = ReadByte();
      value |= (((long) b) << (8 * i));
    }
    return value;
  }

  int ReadBytes(final byte data[], final int size) throws IOException {
    if (!_inByteBack.ReadBytes(data, size)) {
      return HRESULT.E_FAIL;
    }
    return HRESULT.S_OK;
  }

  int ReadByte() throws IOException {
    return _inByteBack.ReadByte();
  }

  long SafeReadDirectUInt64() throws IOException {
    long val = 0;
    final byte[] b = new byte[8];

    final int realProcessedSize = ReadDirect(b, 8);
    if (realProcessedSize != 8) {
      throw new IOException("Unexpected End Of Archive"); // throw
                                                          // CInArchiveException(CInArchiveException::kUnexpectedEndOfArchive);
    }

    for (int i = 0; i < 8; i++) {
      val |= ((long) (b[i] & 0xFF) << (8 * i));
    }
    return val;
  }

  char ReadWideCharLE() throws IOException {
    final int b1 = _inByteBack.ReadByte();
    final int b2 = _inByteBack.ReadByte();
    final char c = (char) (((char) (b2) << 8) + b1);
    return c;
  }

  long ReadNumber() throws IOException {
    final int firstByte = ReadByte();

    int mask = 0x80;
    long value = 0;
    for (int i = 0; i < 8; i++) {
      if ((firstByte & mask) == 0) {
        final long highPart = firstByte & (mask - 1);
        value += (highPart << (i * 8));
        return value;
      }
      final int b = ReadByte();
      if (b < 0) {
        throw new IOException("ReadNumber - Can't read stream");
      }

      value |= (((long) b) << (8 * i));
      mask >>= 1;
    }
    return value;
  }

  int ReadNum() throws IOException { // CNum
    final long value64 = ReadNumber();
    if (value64 > InArchive.kNumMax) {
      throw new IOException("ReadNum - value > CNum.kNumMax"); // return E_FAIL;
    }

    return (int) value64;
  }

  long ReadID() throws IOException {
    return ReadNumber();
  }

  int FindAndReadSignature(final IInStream stream, // IInStream *stream,
      final long searchHeaderSizeLimit // const UInt64 *searchHeaderSizeLimit
  ) throws IOException {
    _position = _arhiveBeginStreamPosition;

    stream.Seek(_arhiveBeginStreamPosition, IInStream.STREAM_SEEK_SET);

    final byte[] signature = new byte[kSignatureSize];

    int processedSize = ReadDirect(stream, signature, 0, kSignatureSize);
    if (processedSize != kSignatureSize) {
      return HRESULT.S_FALSE;
    }

    if (TestSignatureCandidate(signature, 0)) {
      return HRESULT.S_OK;
    }

    // SFX support
    final ByteBuffer byteBuffer = new ByteBuffer();
    final int kBufferSize = (1 << 16);
    byteBuffer.SetCapacity(kBufferSize);
    final byte[] buffer = byteBuffer.data();
    int numPrevBytes = kSignatureSize - 1;

    System.arraycopy(signature, 1, buffer, 0, numPrevBytes);

    long curTestPos = _arhiveBeginStreamPosition + 1;
    for (;;) {
      if (searchHeaderSizeLimit != -1) {
        if ((curTestPos - _arhiveBeginStreamPosition) > searchHeaderSizeLimit) {
          break;
        }
      }
      final int numReadBytes = kBufferSize - numPrevBytes;

      // RINOK(ReadDirect(stream, buffer + numPrevBytes, numReadBytes,
      // &processedSize));
      processedSize = ReadDirect(stream, buffer, numPrevBytes, numReadBytes);
      if (processedSize == -1) {
        return HRESULT.S_FALSE;
      }

      final int numBytesInBuffer = numPrevBytes + processedSize;
      if (numBytesInBuffer < kSignatureSize) {
        break;
      }
      final int numTests = (numBytesInBuffer - kSignatureSize) + 1;
      for (int pos = 0; pos < numTests; pos++, curTestPos++) {
        if (TestSignatureCandidate(buffer, pos)) {
          _arhiveBeginStreamPosition = curTestPos;
          _position = curTestPos + kSignatureSize;
          stream.Seek(_position, IInStream.STREAM_SEEK_SET);
          return HRESULT.S_OK;
        }
      }
      numPrevBytes = numBytesInBuffer - numTests;
      System.arraycopy(buffer, numTests, buffer, 0, numPrevBytes);
    }

    return HRESULT.S_FALSE;
  }

  int SkeepData(final long size) throws IOException {
    for (long i = 0; i < size; i++) {
      final int temp = ReadByte();
    }
    return HRESULT.S_OK;
  }

  int SkeepData() throws IOException {
    final long size = ReadNumber();
    return SkeepData(size);
  }

  int ReadArchiveProperties(final InArchiveInfo archiveInfo) throws IOException {
    for (;;) {
      final long type = ReadID();
      if (type == NID.kEnd) {
        break;
      }
      SkeepData();
    }
    return HRESULT.S_OK;
  }

  int GetNextFolderItem(final Folder folder) throws IOException {
    final int numCoders = ReadNum();

    folder.Coders.clear();
    folder.Coders.Reserve(numCoders);
    int numInStreams = 0;
    int numOutStreams = 0;
    int i;
    for (i = 0; i < numCoders; i++) {
      folder.Coders.add(new CoderInfo());
      final CoderInfo coder = folder.Coders.Back();

      for (;;) {
        coder.AltCoders.add(new AltCoderInfo());
        final AltCoderInfo altCoder = coder.AltCoders.Back();
        final int mainByte = ReadByte();
        altCoder.MethodID.IDSize = (byte) (mainByte & 0xF);
        int ret = ReadBytes(altCoder.MethodID.ID, altCoder.MethodID.IDSize);
        if (ret != HRESULT.S_OK) {
          return ret;
        }
        if ((mainByte & 0x10) != 0) {
          coder.NumInStreams = ReadNum();
          coder.NumOutStreams = ReadNum();
        } else {
          coder.NumInStreams = 1;
          coder.NumOutStreams = 1;
        }
        if ((mainByte & 0x20) != 0) {
          final int propertiesSize = ReadNum();
          altCoder.Properties.SetCapacity(propertiesSize);
          // RINOK(ReadBytes((Byte *)altCoder.Properties,
          // (size_t)propertiesSize));
          ret = ReadBytes(altCoder.Properties.data(), propertiesSize);
        }
        if ((mainByte & 0x80) == 0) {
          break;
        }
      }
      numInStreams += coder.NumInStreams;
      numOutStreams += coder.NumOutStreams;
    }

    // RINOK(ReadNumber(numBindPairs));
    final int numBindPairs = numOutStreams - 1;
    folder.BindPairs.clear();
    folder.BindPairs.Reserve(numBindPairs);
    for (i = 0; i < numBindPairs; i++) {
      final BindPair bindPair = new BindPair();
      bindPair.InIndex = ReadNum();
      bindPair.OutIndex = ReadNum();
      folder.BindPairs.add(bindPair);
    }

    final int numPackedStreams = numInStreams - numBindPairs;
    folder.PackStreams.Reserve(numPackedStreams);
    if (numPackedStreams == 1) {
      for (int j = 0; j < numInStreams; j++) {
        if (folder.FindBindPairForInStream(j) < 0) {
          folder.PackStreams.add(j);
          break;
        }
      }
    } else {
      for (i = 0; i < numPackedStreams; i++) {
        final int packStreamInfo = ReadNum();
        folder.PackStreams.add(packStreamInfo);
      }
    }

    return HRESULT.S_OK;
  }

  int WaitAttribute(final long attribute) throws IOException {
    for (;;) {
      final long type = ReadID();
      if (type == attribute) {
        return HRESULT.S_OK;
      }
      if (type == NID.kEnd) {
        return HRESULT.S_FALSE;
      }
      final int ret = SkeepData();
      if (ret != HRESULT.S_OK) {
        return ret;
      }
    }
  }

  int Open(final IInStream stream, // IInStream *stream
      final long searchHeaderSizeLimit // const UInt64 *searchHeaderSizeLimit
  ) throws IOException {
    Close();

    _arhiveBeginStreamPosition = stream.Seek(0, IInStream.STREAM_SEEK_CUR);
    _position = _arhiveBeginStreamPosition;

    final int ret = FindAndReadSignature(stream, searchHeaderSizeLimit);
    if (ret != HRESULT.S_OK) {
      return ret;
    }

    _stream = stream;

    return HRESULT.S_OK;
  }

  void Close() throws IOException {
    if (_stream != null) {
      _stream.close(); // _stream.Release();
    }
    _stream = null;
  }

  int ReadStreamsInfo(final ObjectVector<ByteBuffer> dataVector, final long[] dataOffset,
      final LongVector packSizes, final BoolVector packCRCsDefined, final IntVector packCRCs,
      final ObjectVector<Folder> folders, final IntVector numUnPackStreamsInFolders,
      final LongVector unPackSizes, final BoolVector digestsDefined, final IntVector digests)
      throws IOException {

    for (;;) {
      final long type = ReadID();
      switch ((int) type) {
        case NID.kEnd:
          return HRESULT.S_OK;
        case NID.kPackInfo: {
          final int result = ReadPackInfo(dataOffset, packSizes, packCRCsDefined, packCRCs);
          if (result != HRESULT.S_OK) {
            return result;
          }
          break;
        }
        case NID.kUnPackInfo: {
          final int result = ReadUnPackInfo(dataVector, folders);
          if (result != HRESULT.S_OK) {
            return result;
          }
          break;
        }
        case NID.kSubStreamsInfo: {
          final int result = ReadSubStreamsInfo(folders, numUnPackStreamsInFolders, unPackSizes,
              digestsDefined, digests);
          if (result != HRESULT.S_OK) {
            return result;
          }
          break;
        }
      }
    }
  }

  int ReadFileNames(final ObjectVector<FileItem> files) throws IOException {
    for (int i = 0; i < files.size(); i++) {
      StringBuilder name = new StringBuilder();
      for (;;) {
        final char c = ReadWideCharLE();
        if (c == '\0') {
          break;
        }
        name.append(c);
      }
      files.get(i).name = name.toString();
    }
    return HRESULT.S_OK;
  }

  int ReadBoolVector(final int numItems, final BoolVector v) throws IOException {
    v.clear();
    v.Reserve(numItems);
    int b = 0;
    int mask = 0;
    for (int i = 0; i < numItems; i++) {
      if (mask == 0) {
        b = ReadByte();
        mask = 0x80;
      }
      v.add((b & mask) != 0);
      mask >>= 1;
    }
    return HRESULT.S_OK;
  }

  int ReadBoolVector2(final int numItems, final BoolVector v) throws IOException { // CBoolVector
    final int allAreDefined = ReadByte();
    if (allAreDefined == 0) {
      return ReadBoolVector(numItems, v);
    }
    v.clear();
    v.Reserve(numItems);
    for (int i = 0; i < numItems; i++) {
      v.add(true);
    }
    return HRESULT.S_OK;
  }

  int ReadHashDigests(final int numItems, final BoolVector digestsDefined, final IntVector digests)
      throws IOException {
    final int ret = ReadBoolVector2(numItems, digestsDefined);
    if (ret != HRESULT.S_OK) {
      return ret;
    }

    digests.clear();
    digests.Reserve(numItems);
    for (int i = 0; i < numItems; i++) {
      int crc = 0;
      if (digestsDefined.get(i)) {
        crc = ReadUInt32();
      }
      digests.add(crc);
    }
    return HRESULT.S_OK;
  }

  int ReadPackInfo(final long[] dataOffset, // UInt64 &dataOffset,
      final LongVector packSizes, // CRecordVector<UInt64> &packSizes,
      final BoolVector packCRCsDefined, // CRecordVector<bool> &packCRCsDefined,
      final IntVector packCRCs) // CRecordVector<UInt32> &packCRCs)
      throws IOException {
    dataOffset[0] = ReadNumber();
    final int numPackStreams = ReadNum();

    int ret = WaitAttribute(NID.kSize);
    if (ret != HRESULT.S_OK) {
      return ret;
    }

    packSizes.clear();
    packSizes.Reserve(numPackStreams);
    for (int i = 0; i < numPackStreams; i++) // CNum i
    {
      final long size = ReadNumber();
      packSizes.add(size);
    }

    long type;
    for (;;) {
      type = ReadID();
      if (type == NID.kEnd) {
        break;
      }
      if (type == NID.kCRC) {
        ret = ReadHashDigests(numPackStreams, packCRCsDefined, packCRCs);
        if (ret != HRESULT.S_OK) {
          return ret;
        }
        continue;
      }
      ret = SkeepData();
      if (ret != HRESULT.S_OK) {
        return ret;
      }
    }
    if (packCRCsDefined.isEmpty()) {
      packCRCsDefined.Reserve(numPackStreams);
      packCRCsDefined.clear();
      packCRCs.Reserve(numPackStreams);
      packCRCs.clear();
      for (int i = 0; i < numPackStreams; i++) {
        packCRCsDefined.add(false);
        packCRCs.add(0);
      }
    }
    return HRESULT.S_OK;
  }

  int ReadUnPackInfo(final ObjectVector<ByteBuffer> dataVector, final ObjectVector<Folder> folders)
      throws IOException {
    int ret = WaitAttribute(NID.kFolder);
    if (ret != HRESULT.S_OK) {
      return ret;
    }

    final int numFolders = ReadNum();

    {
      final StreamSwitch streamSwitch = new StreamSwitch();
      ret = streamSwitch.Set(this, dataVector);
      if (ret != HRESULT.S_OK) {
        return ret;
      }
      folders.clear();
      folders.Reserve(numFolders);
      for (int i = 0; i < numFolders; i++) {
        folders.add(new Folder());
        ret = GetNextFolderItem(folders.Back());
        if (ret != HRESULT.S_OK) {
          streamSwitch.close();
          return ret;
        }
      }
      streamSwitch.close();
    }

    ret = WaitAttribute(NID.kCodersUnPackSize);
    if (ret != HRESULT.S_OK) {
      return ret;
    }

    for (int i = 0; i < numFolders; i++) {
      final Folder folder = folders.get(i);
      final int numOutStreams = folder.GetNumOutStreams();
      folder.UnPackSizes.Reserve(numOutStreams);
      for (int j = 0; j < numOutStreams; j++) {
        final long unPackSize = ReadNumber();
        folder.UnPackSizes.add(unPackSize);
      }
    }

    for (;;) {
      final long type = ReadID();
      if (type == NID.kEnd) {
        return HRESULT.S_OK;
      }
      if (type == NID.kCRC) {
        final BoolVector crcsDefined = new BoolVector();
        final IntVector crcs = new IntVector();
        ret = ReadHashDigests(numFolders, crcsDefined, crcs);
        if (ret != HRESULT.S_OK) {
          return ret;
        }
        for (int i = 0; i < numFolders; i++) {
          final Folder folder = folders.get(i);
          folder.UnPackCRCDefined = crcsDefined.get(i);
          folder.UnPackCRC = crcs.get(i);
        }
        continue;
      }
      ret = SkeepData();
      if (ret != HRESULT.S_OK) {
        return ret;
      }
    }
  }

  int ReadSubStreamsInfo(final ObjectVector<Folder> folders,
      final IntVector numUnPackStreamsInFolders, final LongVector unPackSizes,
      final BoolVector digestsDefined, final IntVector digests) throws IOException {
    numUnPackStreamsInFolders.clear();
    numUnPackStreamsInFolders.Reserve(folders.size());
    long type;
    for (;;) {
      type = ReadID();
      if (type == NID.kNumUnPackStream) {
        for (int i = 0; i < folders.size(); i++) {
          final int value = ReadNum();
          numUnPackStreamsInFolders.add(value);
        }
        continue;
      }
      if ((type == NID.kCRC) || (type == NID.kSize)) {
        break;
      }
      if (type == NID.kEnd) {
        break;
      }
      final int ret = SkeepData();
      if (ret != HRESULT.S_OK) {
        return ret;
      }
    }

    if (numUnPackStreamsInFolders.isEmpty()) {
      for (int i = 0; i < folders.size(); i++) {
        numUnPackStreamsInFolders.add(1);
      }
    }

    for (int i = 0; i < numUnPackStreamsInFolders.size(); i++) {
      // v3.13 incorrectly worked with empty folders
      // v4.07: we check that folder is empty
      final int numSubstreams = numUnPackStreamsInFolders.get(i);
      if (numSubstreams == 0) {
        continue;
      }
      long sum = 0;
      for (int j = 1; j < numSubstreams; j++) {
        long size;
        if (type == NID.kSize) {
          size = ReadNumber();
          unPackSizes.add(size);
          sum += size;
        }
      }
      unPackSizes.add(folders.get(i).GetUnPackSize() - sum);
    }
    if (type == NID.kSize) {
      type = ReadID();
    }

    int numDigests = 0;
    int numDigestsTotal = 0;
    for (int i = 0; i < folders.size(); i++) {
      final int numSubstreams = numUnPackStreamsInFolders.get(i);
      if ((numSubstreams != 1) || !folders.get(i).UnPackCRCDefined) {
        numDigests += numSubstreams;
      }
      numDigestsTotal += numSubstreams;
    }

    for (;;) {
      if (type == NID.kCRC) {
        final BoolVector digestsDefined2 = new BoolVector();
        final IntVector digests2 = new IntVector();
        final int ret = ReadHashDigests(numDigests, digestsDefined2, digests2);
        if (ret != HRESULT.S_OK) {
          return ret;
        }

        int digestIndex = 0;
        for (int i = 0; i < folders.size(); i++) {
          final int numSubstreams = numUnPackStreamsInFolders.get(i);
          final Folder folder = folders.get(i);
          if ((numSubstreams == 1) && folder.UnPackCRCDefined) {
            digestsDefined.add(true);
            digests.add(folder.UnPackCRC);
          } else {
            for (int j = 0; j < numSubstreams; j++, digestIndex++) {
              digestsDefined.add(digestsDefined2.get(digestIndex));
              digests.add(digests2.get(digestIndex));
            }
          }
        }
      } else if (type == NID.kEnd) {
        if (digestsDefined.isEmpty()) {
          digestsDefined.clear();
          digests.clear();
          for (int i = 0; i < numDigestsTotal; i++) {
            digestsDefined.add(false);
            digests.add(0);
          }
        }
        return HRESULT.S_OK;
      } else {
        final int ret = SkeepData();
        if (ret != HRESULT.S_OK) {
          return ret;
        }
      }
      type = ReadID();
    }
  }

  static final long SECS_BETWEEN_EPOCHS = 11644473600L;
  static final long SECS_TO_100NS = 10000000L; /* 10^7 */

  static long FileTimeToLong(final int dwHighDateTime, final int dwLowDateTime) {
    // The FILETIME structure is a 64-bit value representing the number of
    // 100-nanosecond intervals since January 1
    long tm = dwHighDateTime;
    tm <<= 32;
    tm |= (dwLowDateTime & 0xFFFFFFFFL);
    return (tm - (SECS_BETWEEN_EPOCHS * SECS_TO_100NS)) / (10000L); /*
                                                                     * now
                                                                     * convert
                                                                     * to
                                                                     * milliseconds
                                                                     */
  }

  int ReadTime(final ObjectVector<ByteBuffer> dataVector, final ObjectVector<FileItem> files,
      final long type) throws IOException {
    final BoolVector boolVector = new BoolVector();
    int ret = ReadBoolVector2(files.size(), boolVector);
    if (ret != HRESULT.S_OK) {
      return ret;
    }

    final StreamSwitch streamSwitch = new StreamSwitch();
    ret = streamSwitch.Set(this, dataVector);
    if (ret != HRESULT.S_OK) {
      streamSwitch.close();
      return ret;
    }

    for (int i = 0; i < files.size(); i++) {
      final FileItem file = files.get(i);
      int low = 0;
      int high = 0;
      final boolean defined = boolVector.get(i);
      if (defined) {
        low = ReadUInt32();
        high = ReadUInt32();
      }
      switch ((int) type) {
        case NID.kCreationTime:
          // file.IsCreationTimeDefined = defined;
          if (defined) {
            file.CreationTime = FileTimeToLong(high, low);
          }
          break;
        case NID.kLastWriteTime:
          // file.IsLastWriteTimeDefined = defined;
          if (defined) {
            file.LastWriteTime = FileTimeToLong(high, low);
          }
          break;
        case NID.kLastAccessTime:
          // file.IsLastAccessTimeDefined = defined;
          if (defined) {
            file.LastAccessTime = FileTimeToLong(high, low);
          }
          break;
      }
    }
    streamSwitch.close();
    return HRESULT.S_OK;
  }

  int ReadAndDecodePackedStreams(final long baseOffset, final long[] dataOffset,
      final ObjectVector<ByteBuffer> dataVector // CObjectVector<CByteBuffer>
                                                // &dataVector
  ) throws IOException {
    final LongVector packSizes = new LongVector(); // CRecordVector<UInt64>
                                                   // packSizes;
    final BoolVector packCRCsDefined = new BoolVector(); // CRecordVector<bool>
                                                         // packCRCsDefined;
    final IntVector packCRCs = new IntVector(); // CRecordVector<UInt32>
                                                // packCRCs;

    final ObjectVector<Folder> folders = new ObjectVector<Folder>();

    final IntVector numUnPackStreamsInFolders = new IntVector();
    final LongVector unPackSizes = new LongVector();
    final BoolVector digestsDefined = new BoolVector();
    final IntVector digests = new IntVector();

    final int ret = ReadStreamsInfo(null, dataOffset, packSizes, packCRCsDefined, packCRCs,
        folders, numUnPackStreamsInFolders, unPackSizes, digestsDefined, digests);

    // database.ArchiveInfo.DataStartPosition2 +=
    // database.ArchiveInfo.StartPositionAfterHeader;

    int packIndex = 0;
    final Decoder decoder = new Decoder(false); // _ST_MODE

    long dataStartPos = baseOffset + dataOffset[0];
    for (int i = 0; i < folders.size(); i++) {
      final Folder folder = folders.get(i); // const CFolder &folder =
                                            // folders[i];
      dataVector.add(new ByteBuffer());
      final ByteBuffer data = dataVector.Back();
      final long unPackSize = folder.GetUnPackSize();
      if (unPackSize > InArchive.kNumMax) {
        return HRESULT.E_FAIL;
      }
      if (unPackSize > 0xFFFFFFFFL) {
        return HRESULT.E_FAIL;
      }
      data.SetCapacity((int) unPackSize);

      final jgamebase.plugins.extractor.sevenzip.SevenZip.Common.SequentialOutStreamImp2 outStreamSpec = new jgamebase.plugins.extractor.sevenzip.SevenZip.Common.SequentialOutStreamImp2();
      final java.io.OutputStream outStream = outStreamSpec;
      outStreamSpec.Init(data.data(), (int) unPackSize);

      final int result = decoder.Decode(_stream, dataStartPos, packSizes, packIndex, // &packSizes[packIndex]
          folder, outStream, null
      // _ST_MODE , false, 1
          );
      if (result != HRESULT.S_OK) {
        return result;
      }

      if (folder.UnPackCRCDefined) {
        if (!CRC.VerifyDigest(folder.UnPackCRC, data.data(), (int) unPackSize)) {
          throw new IOException("Incorrect Header"); // CInArchiveException(CInArchiveException::kIncorrectHeader);
        }
      }
      for (int j = 0; j < folder.PackStreams.size(); j++) {
        dataStartPos += packSizes.get(packIndex++);
      }
    }
    return HRESULT.S_OK;
  }

  int ReadDatabase(final ArchiveDatabaseEx database) throws IOException {
    database.Clear();
    database.ArchiveInfo.StartPosition = _arhiveBeginStreamPosition;

    final byte[] btmp = new byte[2];
    int realProcessedSize = ReadDirect(btmp, 2);
    if (realProcessedSize != 2) {
      throw new IOException("Unexpected End Of Archive"); // throw
                                                          // CInArchiveException(CInArchiveException::kUnexpectedEndOfArchive);
    }

    database.ArchiveInfo.ArchiveVersion_Major = btmp[0];
    database.ArchiveInfo.ArchiveVersion_Minor = btmp[1];

    if (database.ArchiveInfo.ArchiveVersion_Major != kMajorVersion) {
      throw new IOException("Unsupported Version");
    }

    final CRC crc = new CRC();
    final int crcFromArchive = SafeReadDirectUInt32();
    final long nextHeaderOffset = SafeReadDirectUInt64();
    final long nextHeaderSize = SafeReadDirectUInt64();
    final int nextHeaderCRC = SafeReadDirectUInt32();

    /*
     * #ifdef FORMAT_7Z_RECOVERY ... #endif
     */

    crc.UpdateUInt64(nextHeaderOffset);
    crc.UpdateUInt64(nextHeaderSize);
    crc.UpdateUInt32(nextHeaderCRC);

    database.ArchiveInfo.StartPositionAfterHeader = _position;

    if (crc.GetDigest() != crcFromArchive) {
      throw new IOException("Incorrect Header"); // CInArchiveException(CInArchiveException::kIncorrectHeader);
    }

    if (nextHeaderSize == 0) {
      return HRESULT.S_OK;
    }

    if (nextHeaderSize >= 0xFFFFFFFFL) {
      return HRESULT.E_FAIL;
    }

    _position = _stream.Seek(nextHeaderOffset, IInStream.STREAM_SEEK_CUR);

    final ByteBuffer buffer2 = new ByteBuffer();
    buffer2.SetCapacity((int) nextHeaderSize);

    // SafeReadDirect(buffer2.data(), (int)nextHeaderSize);
    realProcessedSize = ReadDirect(buffer2.data(), (int) nextHeaderSize);
    if (realProcessedSize != (int) nextHeaderSize) {
      throw new IOException("Unexpected End Of Archive"); // throw
                                                          // CInArchiveException(CInArchiveException::kUnexpectedEndOfArchive);
    }

    if (!CRC.VerifyDigest(nextHeaderCRC, buffer2.data(), (int) nextHeaderSize)) {
      throw new IOException("Incorrect Header"); // CInArchiveException(CInArchiveException::kIncorrectHeader);
    }

    final StreamSwitch streamSwitch = new StreamSwitch();
    streamSwitch.Set(this, buffer2);

    final ObjectVector<ByteBuffer> dataVector = new ObjectVector<ByteBuffer>(); // CObjectVector<CByteBuffer>
                                                                                // dataVector;

    for (;;) {
      final long type = ReadID();
      if (type == NID.kHeader) {
        break;
      }
      if (type != NID.kEncodedHeader) {
        throw new IOException("Incorrect Header"); // CInArchiveException(CInArchiveException::kIncorrectHeader);
      }

      final long[] ltmp = new long[1];
      ltmp[0] = database.ArchiveInfo.DataStartPosition2;
      final int result = ReadAndDecodePackedStreams(database.ArchiveInfo.StartPositionAfterHeader,
          ltmp, // database.ArchiveInfo.DataStartPosition2,
          dataVector);
      if (result != HRESULT.S_OK) {
        return result;
      }

      database.ArchiveInfo.DataStartPosition2 = ltmp[0];

      if (dataVector.size() == 0) {
        return HRESULT.S_OK;
      }
      if (dataVector.size() > 1) {
        throw new IOException("Incorrect Header"); // CInArchiveException(CInArchiveException::kIncorrectHeader);
      }
      streamSwitch.Remove();
      streamSwitch.Set(this, dataVector.get(0)); // dataVector.Front()
    }

    streamSwitch.close();
    return ReadHeader(database);
  }

  int ReadHeader(final ArchiveDatabaseEx database) throws IOException {
    long type = ReadID();

    if (type == NID.kArchiveProperties) {
      final int ret = ReadArchiveProperties(database.ArchiveInfo);
      if (ret != HRESULT.S_OK) {
        return ret;
      }
      type = ReadID();
    }

    final ObjectVector<ByteBuffer> dataVector = new ObjectVector<ByteBuffer>();

    if (type == NID.kAdditionalStreamsInfo) {
      final long[] ltmp = new long[1];
      ltmp[0] = database.ArchiveInfo.DataStartPosition2;
      final int result = ReadAndDecodePackedStreams(database.ArchiveInfo.StartPositionAfterHeader,
          ltmp, // database.ArchiveInfo.DataStartPosition2,
          dataVector);
      if (result != HRESULT.S_OK) {
        return result;
      }

      database.ArchiveInfo.DataStartPosition2 = ltmp[0];

      database.ArchiveInfo.DataStartPosition2 += database.ArchiveInfo.StartPositionAfterHeader;
      type = ReadID();
    }

    final LongVector unPackSizes = new LongVector();
    final BoolVector digestsDefined = new BoolVector();
    final IntVector digests = new IntVector();

    if (type == NID.kMainStreamsInfo) {
      final long[] ltmp = new long[1];
      ltmp[0] = database.ArchiveInfo.DataStartPosition;
      final int result = ReadStreamsInfo(dataVector,
          ltmp, // database.ArchiveInfo.DataStartPosition,
          database.PackSizes, database.PackCRCsDefined, database.PackCRCs, database.Folders,
          database.NumUnPackStreamsVector, unPackSizes, digestsDefined, digests);
      if (result != HRESULT.S_OK) {
        return result;
      }
      database.ArchiveInfo.DataStartPosition = ltmp[0];
      database.ArchiveInfo.DataStartPosition += database.ArchiveInfo.StartPositionAfterHeader;
      type = ReadID();
    } else {
      for (int i = 0; i < database.Folders.size(); i++) {
        database.NumUnPackStreamsVector.add(1);
        final Folder folder = database.Folders.get(i);
        unPackSizes.add(folder.GetUnPackSize());
        digestsDefined.add(folder.UnPackCRCDefined);
        digests.add(folder.UnPackCRC);
      }
    }

    database.Files.clear();

    if (type == NID.kEnd) {
      return HRESULT.S_OK;
    }
    if (type != NID.kFilesInfo) {
      throw new IOException("Incorrect Header"); // throw
                                                 // CInArchiveException(CInArchiveException::kIncorrectHeader);
    }

    final int numFiles = ReadNum();
    database.Files.Reserve(numFiles);
    for (int i = 0; i < numFiles; i++) {
      database.Files.add(new FileItem());
    }

    database.ArchiveInfo.FileInfoPopIDs.add(NID.kSize);
    if (!database.PackSizes.isEmpty()) {
      database.ArchiveInfo.FileInfoPopIDs.add(NID.kPackInfo);
    }
    if ((numFiles > 0) && !digests.isEmpty()) {
      database.ArchiveInfo.FileInfoPopIDs.add(NID.kCRC);
    }

    final BoolVector emptyStreamVector = new BoolVector();
    emptyStreamVector.Reserve(numFiles);
    for (int i = 0; i < numFiles; i++) {
      emptyStreamVector.add(false);
    }
    final BoolVector emptyFileVector = new BoolVector();
    final BoolVector antiFileVector = new BoolVector();
    int numEmptyStreams = 0;

    // int sizePrev = -1;
    // int posPrev = 0;

    for (;;) {
      type = ReadID();
      if (type == NID.kEnd) {
        break;
      }
      final long size = ReadNumber();

      // sizePrev = size;
      // posPrev = _inByteBack->GetProcessedSize();

      database.ArchiveInfo.FileInfoPopIDs.add(type);
      switch ((int) type) {
        case NID.kName: {
          final StreamSwitch streamSwitch = new StreamSwitch();
          int result = streamSwitch.Set(this, dataVector);
          if (result != HRESULT.S_OK) {
            return result;
          }
          result = ReadFileNames(database.Files);
          streamSwitch.close();
          if (result != HRESULT.S_OK) {
            return result;
          }
          break;
        }
        case NID.kWinAttributes: {
          final BoolVector boolVector = new BoolVector();
          int result = ReadBoolVector2(database.Files.size(), boolVector);
          if (result != HRESULT.S_OK) {
            return result;
          }

          final StreamSwitch streamSwitch = new StreamSwitch();
          result = streamSwitch.Set(this, dataVector);
          if (result != HRESULT.S_OK) {
            return result;
          }
          for (int i = 0; i < numFiles; i++) {
            final FileItem file = database.Files.get(i);
            file.AreAttributesDefined = boolVector.get(i);
            if (file.AreAttributesDefined) {
              file.Attributes = ReadUInt32();
            }
          }
          streamSwitch.close();
          break;
        }
        case NID.kStartPos: {
          final BoolVector boolVector = new BoolVector();
          int result = ReadBoolVector2(database.Files.size(), boolVector);
          if (result != HRESULT.S_OK) {
            return result;
          }

          final StreamSwitch streamSwitch = new StreamSwitch();
          result = streamSwitch.Set(this, dataVector);
          if (result != HRESULT.S_OK) {
            return result;
          }
          for (int i = 0; i < numFiles; i++) {
            final FileItem file = database.Files.get(i);
            file.IsStartPosDefined = boolVector.get(i);
            if (file.IsStartPosDefined) {
              file.StartPos = ReadUInt64();
            }
          }
          streamSwitch.close();
          break;
        }
        case NID.kEmptyStream: {
          final int result = ReadBoolVector(numFiles, emptyStreamVector);
          if (result != HRESULT.S_OK) {
            return result;
          }

          for (int i = 0; i < emptyStreamVector.size(); i++) {
            if (emptyStreamVector.get(i)) {
              numEmptyStreams++;
            }
          }
          emptyFileVector.Reserve(numEmptyStreams);
          antiFileVector.Reserve(numEmptyStreams);
          for (int i = 0; i < numEmptyStreams; i++) {
            emptyFileVector.add(false);
            antiFileVector.add(false);
          }
          break;
        }
        case NID.kEmptyFile: {
          final int result = ReadBoolVector(numEmptyStreams, emptyFileVector);
          if (result != HRESULT.S_OK) {
            return result;
          }
          break;
        }
        case NID.kAnti: {
          final int result = ReadBoolVector(numEmptyStreams, antiFileVector);
          if (result != HRESULT.S_OK) {
            return result;
          }
          break;
        }
        case NID.kCreationTime:
        case NID.kLastWriteTime:
        case NID.kLastAccessTime: {
          final int result = ReadTime(dataVector, database.Files, type);
          if (result != HRESULT.S_OK) {
            return result;
          }
          break;
        }
        default: {
          database.ArchiveInfo.FileInfoPopIDs.DeleteBack();
          final int result = SkeepData(size);
          if (result != HRESULT.S_OK) {
            return result;
          }
        }
      }
    }

    int emptyFileIndex = 0;
    int sizeIndex = 0;
    for (int i = 0; i < numFiles; i++) {
      final FileItem file = database.Files.get(i);
      file.HasStream = !emptyStreamVector.get(i);
      if (file.HasStream) {
        file.IsDirectory = false;
        file.IsAnti = false;
        file.UnPackSize = unPackSizes.get(sizeIndex);
        file.FileCRC = digests.get(sizeIndex);
        file.IsFileCRCDefined = digestsDefined.get(sizeIndex);
        sizeIndex++;
      } else {
        file.IsDirectory = !emptyFileVector.get(emptyFileIndex);
        file.IsAnti = antiFileVector.get(emptyFileIndex);
        emptyFileIndex++;
        file.UnPackSize = 0;
        file.IsFileCRCDefined = false;
      }
    }

    return HRESULT.S_OK;
  }
}
