package jgamebase.plugins.extractor.sevenzip.SevenZip;

public interface IProgress {
  int SetTotal(long total);

  int SetCompleted(long completeValue);
}
