package jgamebase.plugins.extractor.sevenzip.SevenZip;

public interface ICompressSetOutStream {
  public int SetOutStream(java.io.OutputStream inStream);

  public int ReleaseOutStream() throws java.io.IOException;
}
