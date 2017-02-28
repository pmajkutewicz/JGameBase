package jgamebase.plugins.extractor.sevenzip.SevenZip.Archive.SevenZip;

import jgamebase.plugins.extractor.sevenzip.Common.IntVector;
import jgamebase.plugins.extractor.sevenzip.Common.LongVector;

public class ArchiveDatabaseEx extends ArchiveDatabase {
  InArchiveInfo ArchiveInfo = new InArchiveInfo();
  LongVector PackStreamStartPositions = new LongVector();
  IntVector FolderStartPackStreamIndex = new IntVector();

  IntVector FolderStartFileIndex = new IntVector();
  IntVector FileIndexToFolderIndexMap = new IntVector();

  @Override
  void Clear() {
    super.Clear();
    ArchiveInfo.Clear();
    PackStreamStartPositions.clear();
    FolderStartPackStreamIndex.clear();
    FolderStartFileIndex.clear();
    FileIndexToFolderIndexMap.clear();
  }

  void FillFolderStartPackStream() {
    FolderStartPackStreamIndex.clear();
    FolderStartPackStreamIndex.Reserve(Folders.size());
    int startPos = 0;
    for (int i = 0; i < Folders.size(); i++) {
      FolderStartPackStreamIndex.add(startPos);
      startPos += Folders.get(i).PackStreams.size();
    }
  }

  void FillStartPos() {
    PackStreamStartPositions.clear();
    PackStreamStartPositions.Reserve(PackSizes.size());
    long startPos = 0;
    for (int i = 0; i < PackSizes.size(); i++) {
      PackStreamStartPositions.add(startPos);
      startPos += PackSizes.get(i);
    }
  }

  public void Fill() throws java.io.IOException {
    FillFolderStartPackStream();
    FillStartPos();
    FillFolderStartFileIndex();
  }

  public long GetFolderFullPackSize(final int folderIndex) {
    final int packStreamIndex = FolderStartPackStreamIndex.get(folderIndex);
    final Folder folder = Folders.get(folderIndex);
    long size = 0;
    for (int i = 0; i < folder.PackStreams.size(); i++) {
      size += PackSizes.get(packStreamIndex + i);
    }
    return size;
  }

  void FillFolderStartFileIndex() throws java.io.IOException {
    FolderStartFileIndex.clear();
    FolderStartFileIndex.Reserve(Folders.size());
    FileIndexToFolderIndexMap.clear();
    FileIndexToFolderIndexMap.Reserve(Files.size());

    int folderIndex = 0;
    int indexInFolder = 0;
    for (int i = 0; i < Files.size(); i++) {
      final FileItem file = Files.get(i);
      final boolean emptyStream = !file.HasStream;
      if (emptyStream && (indexInFolder == 0)) {
        FileIndexToFolderIndexMap.add(InArchive.kNumNoIndex);
        continue;
      }
      if (indexInFolder == 0) {
        // v3.13 incorrectly worked with empty folders
        // v4.07: Loop for skipping empty folders
        for (;;) {
          if (folderIndex >= Folders.size()) {
            throw new java.io.IOException("Incorrect Header"); // CInArchiveException(CInArchiveException::kIncorrectHeader);
          }
          FolderStartFileIndex.add(i); // check it
          if (NumUnPackStreamsVector.get(folderIndex) != 0) {
            break;
          }
          folderIndex++;
        }
      }
      FileIndexToFolderIndexMap.add(folderIndex);
      if (emptyStream) {
        continue;
      }
      indexInFolder++;
      if (indexInFolder >= NumUnPackStreamsVector.get(folderIndex)) {
        folderIndex++;
        indexInFolder = 0;
      }
    }
  }

  public long GetFolderStreamPos(final int folderIndex, final int indexInFolder) {
    return ArchiveInfo.DataStartPosition
        + PackStreamStartPositions.get(FolderStartPackStreamIndex.get(folderIndex) + indexInFolder);
  }
}