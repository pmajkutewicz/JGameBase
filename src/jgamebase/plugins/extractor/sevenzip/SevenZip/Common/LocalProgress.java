package jgamebase.plugins.extractor.sevenzip.SevenZip.Common;

import jgamebase.plugins.extractor.sevenzip.SevenZip.ICompressProgressInfo;
import jgamebase.plugins.extractor.sevenzip.SevenZip.IProgress;

public class LocalProgress implements ICompressProgressInfo {
  IProgress _progress;
  boolean _inSizeIsMain;

  public void Init(final IProgress progress, final boolean inSizeIsMain) {
    _progress = progress;
    _inSizeIsMain = inSizeIsMain;
  }

  @Override
  public int SetRatioInfo(final long inSize, final long outSize) {
    return _progress.SetCompleted(_inSizeIsMain ? inSize : outSize);

  }

}
