package jgamebase.plugins.extractor.sevenzip.SevenZip.Archive.SevenZip;

import jgamebase.plugins.extractor.sevenzip.Common.ObjectVector;

class CoderInfo {

  int NumInStreams;
  int NumOutStreams;
  public ObjectVector<AltCoderInfo> AltCoders = new jgamebase.plugins.extractor.sevenzip.Common.ObjectVector<AltCoderInfo>();

  boolean IsSimpleCoder() {
    return (NumInStreams == 1) && (NumOutStreams == 1);
  }

  public CoderInfo() {
    NumInStreams = 0;
    NumOutStreams = 0;
  }
}
