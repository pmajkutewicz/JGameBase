package jgamebase.plugins.extractor.sevenzip.SevenZip.Archive.Common;

public class STCoderInfo extends CoderInfo {
  boolean IsMain;

  public STCoderInfo(final int numInStreams, final int numOutStreams, final boolean isMain) {
    super(numInStreams, numOutStreams);
    IsMain = isMain;
  }
}
