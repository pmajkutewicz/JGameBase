package jgamebase.plugins.extractor.sevenzip.SevenZip.Archive.Common;

import java.io.IOException;

import jgamebase.plugins.extractor.sevenzip.Common.LongVector;
import jgamebase.plugins.extractor.sevenzip.Common.ObjectVector;
import jgamebase.plugins.extractor.sevenzip.Common.RecordVector;
import jgamebase.plugins.extractor.sevenzip.SevenZip.HRESULT;
import jgamebase.plugins.extractor.sevenzip.SevenZip.ICompressCoder;
import jgamebase.plugins.extractor.sevenzip.SevenZip.ICompressCoder2;
import jgamebase.plugins.extractor.sevenzip.SevenZip.ICompressProgressInfo;
import jgamebase.plugins.extractor.sevenzip.SevenZip.ICompressSetInStream;
import jgamebase.plugins.extractor.sevenzip.SevenZip.ICompressSetOutStream;
import jgamebase.plugins.extractor.sevenzip.SevenZip.ICompressSetOutStreamSize;

public class CoderMixer2ST implements ICompressCoder2, CoderMixer2 {

  BindInfo _bindInfo = new BindInfo();
  ObjectVector<STCoderInfo> _coders = new ObjectVector<STCoderInfo>();
  int _mainCoderIndex;

  public CoderMixer2ST() {
  }

  @Override
  public void SetBindInfo(final BindInfo bindInfo) {
    _bindInfo = bindInfo;
  }

  public void AddCoderCommon(final boolean isMain) {
    final CoderStreamsInfo csi = _bindInfo.Coders.get(_coders.size());
    _coders.add(new STCoderInfo(csi.NumInStreams, csi.NumOutStreams, isMain));
  }

  public void AddCoder2(final ICompressCoder2 coder, final boolean isMain) {
    AddCoderCommon(isMain);
    _coders.Back().Coder2 = coder;
  }

  public void AddCoder(final ICompressCoder coder, final boolean isMain) {
    AddCoderCommon(isMain);
    _coders.Back().Coder = coder;
  }

  @Override
  public void ReInit() {
  }

  @Override
  public void SetCoderInfo(final int coderIndex, final LongVector inSizes, final LongVector outSizes) {
    // _coders[coderIndex].SetCoderInfo(inSizes, outSizes);
    _coders.get(coderIndex).SetCoderInfo(inSizes, outSizes);
  }

  public int GetInStream(final RecordVector<java.io.InputStream> inStreams,
      final Object useless_inSizes, // const UInt64 **inSizes,
      final int streamIndex, final java.io.InputStream[] inStreamRes) {
    java.io.InputStream seqInStream;
    int i;
    for (i = 0; i < _bindInfo.InStreams.size(); i++) {
      if (_bindInfo.InStreams.get(i) == streamIndex) {
        seqInStream = inStreams.get(i);
        inStreamRes[0] = seqInStream; // seqInStream.Detach();
        return HRESULT.S_OK;
      }
    }
    final int binderIndex = _bindInfo.FindBinderForInStream(streamIndex);
    if (binderIndex < 0) {
      return HRESULT.E_INVALIDARG;
    }

    final int tmp1[] = new int[1]; // TBD
    final int tmp2[] = new int[1]; // TBD
    _bindInfo.FindOutStream(_bindInfo.BindPairs.get(binderIndex).OutIndex, tmp1 /* coderIndex */,
        tmp2 /* coderStreamIndex */);
    final int coderIndex = tmp1[0], coderStreamIndex = tmp2[0];

    final CoderInfo coder = _coders.get(coderIndex);
    if (coder.Coder == null) {
      return HRESULT.E_NOTIMPL;
    }

    seqInStream = (java.io.InputStream) coder.Coder; // coder.Coder.QueryInterface(IID_ISequentialInStream,
                                                     // &seqInStream);
    if (seqInStream == null) {
      return HRESULT.E_NOTIMPL;
    }

    final int startIndex = _bindInfo.GetCoderInStreamIndex(coderIndex);

    if (coder.Coder == null) {
      return HRESULT.E_NOTIMPL;
    }

    final ICompressSetInStream setInStream = (ICompressSetInStream) coder.Coder; // coder.Coder.QueryInterface(IID_ICompressSetInStream,
                                                                                 // &setInStream);
    if (setInStream == null) {
      return HRESULT.E_NOTIMPL;
    }

    if (coder.NumInStreams > 1) {
      return HRESULT.E_NOTIMPL;
    }
    for (i = 0; i < coder.NumInStreams; i++) {
      final java.io.InputStream[] tmp = new java.io.InputStream[1];
      int res = GetInStream(inStreams, useless_inSizes, startIndex + i, tmp /*
                                                                             * &
                                                                             * seqInStream2
                                                                             */);
      if (res != HRESULT.S_OK) {
        return res;
      }
      final java.io.InputStream seqInStream2 = tmp[0];
      res = setInStream.SetInStream(seqInStream2);
      if (res != HRESULT.S_OK) {
        return res;
      }
    }
    inStreamRes[0] = seqInStream; // seqInStream.Detach();
    return HRESULT.S_OK;
  }

  public int GetOutStream(final RecordVector<java.io.OutputStream> outStreams,
      final Object useless_outSizes, // const UInt64 **outSizes,
      final int streamIndex, final java.io.OutputStream[] outStreamRes) {
    java.io.OutputStream seqOutStream;
    int i;
    for (i = 0; i < _bindInfo.OutStreams.size(); i++) {
      if (_bindInfo.OutStreams.get(i) == streamIndex) {
        seqOutStream = outStreams.get(i);
        outStreamRes[0] = seqOutStream; // seqOutStream.Detach();
        return HRESULT.S_OK;
      }
    }
    final int binderIndex = _bindInfo.FindBinderForOutStream(streamIndex);
    if (binderIndex < 0) {
      return HRESULT.E_INVALIDARG;
    }

    final int tmp1[] = new int[1];
    final int tmp2[] = new int[1];
    _bindInfo.FindInStream(_bindInfo.BindPairs.get(binderIndex).InIndex, tmp1 /* coderIndex */,
        tmp2 /* coderStreamIndex */);
    final int coderIndex = tmp1[0], coderStreamIndex = tmp2[0];

    final CoderInfo coder = _coders.get(coderIndex);
    if (coder.Coder == null) {
      return HRESULT.E_NOTIMPL;
    }

    try {
      seqOutStream = (java.io.OutputStream) coder.Coder; // coder.Coder.QueryInterface(IID_ISequentialOutStream,
                                                         // &seqOutStream);
    } catch (final java.lang.ClassCastException e) {
      return HRESULT.E_NOTIMPL;
    }

    final int startIndex = _bindInfo.GetCoderOutStreamIndex(coderIndex);

    if (coder.Coder == null) {
      return HRESULT.E_NOTIMPL;
    }

    ICompressSetOutStream setOutStream = null;
    try {
      setOutStream = (ICompressSetOutStream) coder.Coder; // coder.Coder.QueryInterface(IID_ICompressSetOutStream,
                                                          // &setOutStream);
    } catch (final java.lang.ClassCastException e) {
      return HRESULT.E_NOTIMPL;
    }

    if (coder.NumOutStreams > 1) {
      return HRESULT.E_NOTIMPL;
    }
    for (i = 0; i < coder.NumOutStreams; i++) {
      final java.io.OutputStream[] tmp = new java.io.OutputStream[1];
      int res = GetOutStream(outStreams, useless_outSizes, startIndex + i, tmp /*
                                                                                * &
                                                                                * seqOutStream2
                                                                                */);
      if (res != HRESULT.S_OK) {
        return res;
      }
      final java.io.OutputStream seqOutStream2 = tmp[0];
      res = setOutStream.SetOutStream(seqOutStream2);
      if (res != HRESULT.S_OK) {
        return res;
      }
    }
    outStreamRes[0] = seqOutStream; // seqOutStream.Detach();
    return HRESULT.S_OK;
  }

  @Override
  public int Code(final RecordVector<java.io.InputStream> inStreams,
      final Object useless_inSizes, // const UInt64 ** inSizes ,
      final int numInStreams, final RecordVector<java.io.OutputStream> outStreams,
      final Object useless_outSizes, // const UInt64 ** /* outSizes */,
      final int numOutStreams, final ICompressProgressInfo progress) throws IOException {
    if ((numInStreams != _bindInfo.InStreams.size())
        || (numOutStreams != _bindInfo.OutStreams.size())) {
      return HRESULT.E_INVALIDARG;
    }

    // Find main coder
    int _mainCoderIndex = -1;
    int i;
    for (i = 0; i < _coders.size(); i++) {
      if (_coders.get(i).IsMain) {
        _mainCoderIndex = i;
        break;
      }
    }
    if (_mainCoderIndex < 0) {
      for (i = 0; i < _coders.size(); i++) {
        if (_coders.get(i).NumInStreams > 1) {
          if (_mainCoderIndex >= 0) {
            return HRESULT.E_NOTIMPL;
          }
          _mainCoderIndex = i;
        }
      }
    }
    if (_mainCoderIndex < 0) {
      _mainCoderIndex = 0;
    }

    // _mainCoderIndex = 0;
    // _mainCoderIndex = _coders.Size() - 1;
    final CoderInfo mainCoder = _coders.get(_mainCoderIndex);

    final ObjectVector<java.io.InputStream> seqInStreams = new ObjectVector<java.io.InputStream>(); // CObjectVector<
                                                                                                    // CMyComPtr<ISequentialInStream>
                                                                                                    // >
    final ObjectVector<java.io.OutputStream> seqOutStreams = new ObjectVector<java.io.OutputStream>(); // CObjectVector<
                                                                                                       // CMyComPtr<ISequentialOutStream>
                                                                                                       // >
    final int startInIndex = _bindInfo.GetCoderInStreamIndex(_mainCoderIndex);
    final int startOutIndex = _bindInfo.GetCoderOutStreamIndex(_mainCoderIndex);
    for (i = 0; i < mainCoder.NumInStreams; i++) {
      final java.io.InputStream tmp[] = new java.io.InputStream[1];
      final int res = GetInStream(inStreams, useless_inSizes, startInIndex + i, tmp /*
                                                                                     * &
                                                                                     * seqInStream
                                                                                     */);
      if (res != HRESULT.S_OK) {
        return res;
      }
      final java.io.InputStream seqInStream = tmp[0];
      seqInStreams.add(seqInStream);
    }
    for (i = 0; i < mainCoder.NumOutStreams; i++) {
      final java.io.OutputStream tmp[] = new java.io.OutputStream[1];
      final int res = GetOutStream(outStreams, useless_outSizes, startOutIndex + i, tmp);
      if (res != HRESULT.S_OK) {
        return res;
      }
      final java.io.OutputStream seqOutStream = tmp[0];
      seqOutStreams.add(seqOutStream);
    }
    final RecordVector<java.io.InputStream> seqInStreamsSpec = new RecordVector<java.io.InputStream>();
    final RecordVector<java.io.OutputStream> seqOutStreamsSpec = new RecordVector<java.io.OutputStream>();
    for (i = 0; i < mainCoder.NumInStreams; i++) {
      seqInStreamsSpec.add(seqInStreams.get(i));
    }
    for (i = 0; i < mainCoder.NumOutStreams; i++) {
      seqOutStreamsSpec.add(seqOutStreams.get(i));
    }

    for (i = 0; i < _coders.size(); i++) {
      if (i == _mainCoderIndex) {
        continue;
      }
      final CoderInfo coder = _coders.get(i);

      ICompressSetOutStreamSize setOutStreamSize = null;
      try {
        setOutStreamSize = (ICompressSetOutStreamSize) coder.Coder;

        final int res = setOutStreamSize.SetOutStreamSize(coder.OutSizePointers.get(0));
        if (res != HRESULT.S_OK) {
          return res;
        }
      } catch (final java.lang.ClassCastException e) {
        // nothing to do
      }
    }
    if (mainCoder.Coder != null) {
      final int res = mainCoder.Coder.Code(seqInStreamsSpec.get(0), seqOutStreamsSpec.get(0),
      // TBD mainCoder.InSizePointers.get(0),
          mainCoder.OutSizePointers.get(0), progress);
      if (res != HRESULT.S_OK) {
        return res;
      }
    } else {
      final int res = mainCoder.Coder2.Code(seqInStreamsSpec, // &seqInStreamsSpec.Front(
          mainCoder.InSizePointers.Front(), // &mainCoder.InSizePointers.Front()
          mainCoder.NumInStreams, seqOutStreamsSpec, // &seqOutStreamsSpec.Front()
          mainCoder.OutSizePointers.Front(), // &mainCoder.OutSizePointers.Front()
          mainCoder.NumOutStreams, progress);
      if (res != HRESULT.S_OK) {
        return res;
      }
    }

    final java.io.OutputStream stream = seqOutStreams.Front();
    stream.flush();

    return HRESULT.S_OK;
  }

  @Override
  public void close() {

  }
}
