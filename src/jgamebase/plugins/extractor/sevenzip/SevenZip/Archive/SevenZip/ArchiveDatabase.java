package jgamebase.plugins.extractor.sevenzip.SevenZip.Archive.SevenZip;

import jgamebase.plugins.extractor.sevenzip.Common.BoolVector;
import jgamebase.plugins.extractor.sevenzip.Common.IntVector;
import jgamebase.plugins.extractor.sevenzip.Common.LongVector;
import jgamebase.plugins.extractor.sevenzip.Common.ObjectVector;

class ArchiveDatabase {
  public LongVector PackSizes = new LongVector();
  public BoolVector PackCRCsDefined = new BoolVector();
  public IntVector PackCRCs = new IntVector();
  public ObjectVector<Folder> Folders = new ObjectVector<Folder>();
  public IntVector NumUnPackStreamsVector = new IntVector();
  public ObjectVector<FileItem> Files = new ObjectVector<FileItem>();

  void Clear() {
    PackSizes.clear();
    PackCRCsDefined.clear();
    PackCRCs.clear();
    Folders.clear();
    NumUnPackStreamsVector.clear();
    Files.clear();
  }
}