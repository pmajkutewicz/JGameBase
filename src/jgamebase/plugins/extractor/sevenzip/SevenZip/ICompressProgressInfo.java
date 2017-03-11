package jgamebase.plugins.extractor.sevenzip.SevenZip;

public interface ICompressProgressInfo {
  long INVALID = -1;

  int SetRatioInfo(long inSize, long outSize);
}
