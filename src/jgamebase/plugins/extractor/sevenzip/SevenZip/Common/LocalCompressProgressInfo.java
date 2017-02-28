package jgamebase.plugins.extractor.sevenzip.SevenZip.Common;

import jgamebase.plugins.extractor.sevenzip.SevenZip.ICompressProgressInfo;

public class LocalCompressProgressInfo implements ICompressProgressInfo {
  ICompressProgressInfo _progress;

  boolean _inStartValueIsAssigned;
  boolean _outStartValueIsAssigned;
  long _inStartValue;
  long _outStartValue;

  public void Init(final ICompressProgressInfo progress, final long inStartValue,
      final long outStartValue) {

    _progress = progress;
    _inStartValueIsAssigned = (inStartValue != ICompressProgressInfo.INVALID);
    if (_inStartValueIsAssigned) {
      _inStartValue = inStartValue;
    }
    _outStartValueIsAssigned = (outStartValue != ICompressProgressInfo.INVALID);
    if (_outStartValueIsAssigned) {
      _outStartValue = outStartValue;
    }

  }

  @Override
  public int SetRatioInfo(final long inSize, final long outSize) {
    long inSizeNew, outSizeNew;
    long inSizeNewPointer;
    long outSizeNewPointer;
    if (_inStartValueIsAssigned && (inSize != ICompressProgressInfo.INVALID)) {
      inSizeNew = _inStartValue + (inSize); // *inSize
      inSizeNewPointer = inSizeNew;
    } else {
      inSizeNewPointer = ICompressProgressInfo.INVALID;
    }

    if (_outStartValueIsAssigned && (outSize != ICompressProgressInfo.INVALID)) {
      outSizeNew = _outStartValue + (outSize);
      outSizeNewPointer = outSizeNew;
    } else {
      outSizeNewPointer = ICompressProgressInfo.INVALID;
    }
    return _progress.SetRatioInfo(inSizeNewPointer, outSizeNewPointer);

  }

}
