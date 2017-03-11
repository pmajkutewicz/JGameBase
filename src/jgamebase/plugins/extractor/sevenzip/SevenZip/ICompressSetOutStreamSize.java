package jgamebase.plugins.extractor.sevenzip.SevenZip;

public interface ICompressSetOutStreamSize {
  int INVALID_OUTSIZE = -1;

  int SetOutStreamSize(long outSize);
}
