package jgamebase.plugins.extractor.sevenzip.SevenZip;

public interface IProgress {
  public int SetTotal(long total);

  public int SetCompleted(long completeValue);
}
