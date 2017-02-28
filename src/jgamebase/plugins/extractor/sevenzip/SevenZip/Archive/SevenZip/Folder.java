package jgamebase.plugins.extractor.sevenzip.SevenZip.Archive.SevenZip;

import java.io.IOException;

import jgamebase.plugins.extractor.sevenzip.Common.IntVector;
import jgamebase.plugins.extractor.sevenzip.Common.LongVector;
import jgamebase.plugins.extractor.sevenzip.Common.RecordVector;
import jgamebase.plugins.extractor.sevenzip.SevenZip.Archive.Common.BindPair;

class Folder {
  public RecordVector<CoderInfo> Coders = new RecordVector<CoderInfo>();
  RecordVector<BindPair> BindPairs = new RecordVector<BindPair>();
  IntVector PackStreams = new IntVector();
  LongVector UnPackSizes = new LongVector();
  int UnPackCRC;
  boolean UnPackCRCDefined;

  Folder() {
    UnPackCRCDefined = false;
  }

  long GetUnPackSize() throws IOException {
    if (UnPackSizes.isEmpty()) {
      return 0;
    }
    for (int i = UnPackSizes.size() - 1; i >= 0; i--) {
      if (FindBindPairForOutStream(i) < 0) {
        return UnPackSizes.get(i);
      }
    }
    throw new IOException("1"); // throw 1 // TBD
  }

  int FindBindPairForInStream(final int inStreamIndex) {
    for (int i = 0; i < BindPairs.size(); i++) {
      if (BindPairs.get(i).InIndex == inStreamIndex) {
        return i;
      }
    }
    return -1;
  }

  int FindBindPairForOutStream(final int outStreamIndex) {
    for (int i = 0; i < BindPairs.size(); i++) {
      if (BindPairs.get(i).OutIndex == outStreamIndex) {
        return i;
      }
    }
    return -1;
  }

  int FindPackStreamArrayIndex(final int inStreamIndex) {
    for (int i = 0; i < PackStreams.size(); i++) {
      if (PackStreams.get(i) == inStreamIndex) {
        return i;
      }
    }
    return -1;
  }

  int GetNumOutStreams() {
    int result = 0;
    for (int i = 0; i < Coders.size(); i++) {
      result += Coders.get(i).NumOutStreams;
    }
    return result;
  }

}