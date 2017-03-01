package jgamebase.plugins.extractor.sevenzip.SevenZip.Archive.SevenZip;

import jgamebase.plugins.extractor.sevenzip.Common.ByteBuffer;

class AltCoderInfo {
  public MethodID MethodID;
  public ByteBuffer Properties;

  public AltCoderInfo() {
    MethodID = new MethodID();
    Properties = new ByteBuffer();
  }
}
