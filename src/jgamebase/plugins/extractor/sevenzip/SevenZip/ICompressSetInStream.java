package jgamebase.plugins.extractor.sevenzip.SevenZip;

public interface ICompressSetInStream {
  int SetInStream(java.io.InputStream inStream);

  int ReleaseInStream() throws java.io.IOException;
}
