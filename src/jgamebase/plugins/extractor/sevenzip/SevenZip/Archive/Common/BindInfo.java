package jgamebase.plugins.extractor.sevenzip.SevenZip.Archive.Common;

import jgamebase.plugins.extractor.sevenzip.Common.IntVector;
import jgamebase.plugins.extractor.sevenzip.Common.RecordVector;

public class BindInfo {
  public RecordVector<CoderStreamsInfo> Coders = new RecordVector<CoderStreamsInfo>();
  public RecordVector<BindPair> BindPairs = new RecordVector<BindPair>();
  public IntVector InStreams = new IntVector();
  public IntVector OutStreams = new IntVector();

  public void Clear() {
    Coders.clear();
    BindPairs.clear();
    InStreams.clear();
    OutStreams.clear();
  }

  public int FindBinderForInStream(final int inStream) // const
  {
    for (int i = 0; i < BindPairs.size(); i++) {
      if (BindPairs.get(i).InIndex == inStream) {
        return i;
      }
    }
    return -1;
  }

  public int FindBinderForOutStream(final int outStream) // const
  {
    for (int i = 0; i < BindPairs.size(); i++) {
      if (BindPairs.get(i).OutIndex == outStream) {
        return i;
      }
    }
    return -1;
  }

  public int GetCoderInStreamIndex(final int coderIndex) // const
  {
    int streamIndex = 0;
    for (int i = 0; i < coderIndex; i++) {
      streamIndex += Coders.get(i).NumInStreams;
    }
    return streamIndex;
  }

  public int GetCoderOutStreamIndex(final int coderIndex) // const
  {
    int streamIndex = 0;
    for (int i = 0; i < coderIndex; i++) {
      streamIndex += Coders.get(i).NumOutStreams;
    }
    return streamIndex;
  }

  public void FindInStream(int streamIndex, final int[] coderIndex, // UInt32
                                                                    // &coderIndex
      final int[] coderStreamIndex // UInt32 &coderStreamIndex
  )

  {
    for (coderIndex[0] = 0; coderIndex[0] < Coders.size(); coderIndex[0]++) {
      final int curSize = Coders.get(coderIndex[0]).NumInStreams;
      if (streamIndex < curSize) {
        coderStreamIndex[0] = streamIndex;
        return;
      }
      streamIndex -= curSize;
    }
    throw new UnknownError("1");
  }

  public void FindOutStream(int streamIndex, final int[] coderIndex, // UInt32
                                                                     // &coderIndex,
      final int[] coderStreamIndex /* , UInt32 &coderStreamIndex */) {
    for (coderIndex[0] = 0; coderIndex[0] < Coders.size(); coderIndex[0]++) {
      final int curSize = Coders.get(coderIndex[0]).NumOutStreams;
      if (streamIndex < curSize) {
        coderStreamIndex[0] = streamIndex;
        return;
      }
      streamIndex -= curSize;
    }
    throw new UnknownError("1");
  }

}
