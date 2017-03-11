package jgamebase.plugins.extractor.sevenzip.SevenZip;

public interface ICompressSetOutStream {
  int SetOutStream(java.io.OutputStream inStream);

  int ReleaseOutStream() throws java.io.IOException;
}
