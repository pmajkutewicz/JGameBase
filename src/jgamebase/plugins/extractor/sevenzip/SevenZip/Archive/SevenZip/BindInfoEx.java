package jgamebase.plugins.extractor.sevenzip.SevenZip.Archive.SevenZip;

import jgamebase.plugins.extractor.sevenzip.Common.RecordVector;
import jgamebase.plugins.extractor.sevenzip.SevenZip.Archive.Common.BindInfo;

class BindInfoEx extends BindInfo {

  RecordVector<MethodID> CoderMethodIDs = new RecordVector<MethodID>();

  @Override
  public void Clear() {
    super.Clear(); // CBindInfo::Clear();
    CoderMethodIDs.clear();
  }
}
