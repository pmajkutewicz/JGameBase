package jgamebase.plugins.extractor.sevenzip.SevenZip.Archive.Common;

import jgamebase.plugins.extractor.sevenzip.Common.LongVector;

public interface CoderMixer2 {

  void ReInit();

  void SetBindInfo(BindInfo bindInfo);

  void SetCoderInfo(int coderIndex, LongVector inSizes, LongVector outSizes);
}
