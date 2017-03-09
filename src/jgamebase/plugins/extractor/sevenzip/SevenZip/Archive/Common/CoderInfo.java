package jgamebase.plugins.extractor.sevenzip.SevenZip.Archive.Common;

import jgamebase.plugins.extractor.sevenzip.Common.LongVector;
import jgamebase.plugins.extractor.sevenzip.SevenZip.ICompressCoder;
import jgamebase.plugins.extractor.sevenzip.SevenZip.ICompressCoder2;

public class CoderInfo {
  ICompressCoder Coder;
  ICompressCoder2 Coder2;
  int NumInStreams;
  int NumOutStreams;

  LongVector InSizes = new LongVector();
  LongVector OutSizes = new LongVector();
  LongVector InSizePointers = new LongVector();
  LongVector OutSizePointers = new LongVector();

  public CoderInfo(final int numInStreams, final int numOutStreams) {
    NumInStreams = numInStreams;
    NumOutStreams = numOutStreams;
    InSizes.Reserve(NumInStreams);
    InSizePointers.Reserve(NumInStreams);
    OutSizePointers.Reserve(NumOutStreams);
    OutSizePointers.Reserve(NumOutStreams);
  }

  static public void SetSizes(final LongVector srcSizes, final LongVector sizes,
      final LongVector sizePointers, final int numItems) {
    sizes.clear();
    sizePointers.clear();
    for (int i = 0; i < numItems; i++) {
      if ((srcSizes == null) || (srcSizes.get(i) == -1)) // TBD null => -1
      {
        sizes.add(0L);
        sizePointers.add(-1);
      } else {
        sizes.add(srcSizes.get(i)); // sizes.Add(*srcSizes[i]);
        sizePointers.add(sizes.Back()); // sizePointers.Add(&sizes.Back());
      }
    }
  }

  public void SetCoderInfo(final LongVector inSizes, // const UInt64 **inSizes,
      final LongVector outSizes) // const UInt64 **outSizes)
  {
    SetSizes(inSizes, InSizes, InSizePointers, NumInStreams);
    SetSizes(outSizes, OutSizes, OutSizePointers, NumOutStreams);
  }
}
