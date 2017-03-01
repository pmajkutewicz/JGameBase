package jgamebase.plugins.extractor.sevenzip.SevenZip.Archive;

public class SevenZipEntry {

  long LastWriteTime;

  long UnPackSize;
  long PackSize;

  int Attributes;
  long FileCRC;

  boolean IsDirectory;

  String Name;
  String Methods;

  long Position;

  public SevenZipEntry(final String name, final long packSize, final long unPackSize,
      final long crc, final long lastWriteTime, final long position, final boolean isDir,
      final int att, final String methods) {

    Name = name;
    PackSize = packSize;
    UnPackSize = unPackSize;
    FileCRC = crc;
    LastWriteTime = lastWriteTime;
    Position = position;
    IsDirectory = isDir;
    Attributes = att;
    Methods = methods;
  }

  public long getCompressedSize() {
    return PackSize;
  }

  public long getSize() {
    return UnPackSize;
  }

  public long getCrc() {
    return FileCRC;
  }

  public String getName() {
    return Name;
  }

  public long getTime() {
    return LastWriteTime;
  }

  public long getPosition() {
    return Position;
  }

  public boolean isDirectory() {
    return IsDirectory;
  }

  static final String kEmptyAttributeChar = ".";
  static final String kDirectoryAttributeChar = "D";
  static final String kReadonlyAttributeChar = "R";
  static final String kHiddenAttributeChar = "H";
  static final String kSystemAttributeChar = "S";
  static final String kArchiveAttributeChar = "A";
  static public final int FILE_ATTRIBUTE_READONLY = 0x00000001;
  static public final int FILE_ATTRIBUTE_HIDDEN = 0x00000002;
  static public final int FILE_ATTRIBUTE_SYSTEM = 0x00000004;
  static public final int FILE_ATTRIBUTE_DIRECTORY = 0x00000010;
  static public final int FILE_ATTRIBUTE_ARCHIVE = 0x00000020;

  public String getAttributesString() {
    String ret = "";
    ret += (((Attributes & FILE_ATTRIBUTE_DIRECTORY) != 0) || IsDirectory) ? kDirectoryAttributeChar
        : kEmptyAttributeChar;
    ret += ((Attributes & FILE_ATTRIBUTE_READONLY) != 0) ? kReadonlyAttributeChar
        : kEmptyAttributeChar;
    ret += ((Attributes & FILE_ATTRIBUTE_HIDDEN) != 0) ? kHiddenAttributeChar : kEmptyAttributeChar;
    ret += ((Attributes & FILE_ATTRIBUTE_SYSTEM) != 0) ? kSystemAttributeChar : kEmptyAttributeChar;
    ret += ((Attributes & FILE_ATTRIBUTE_ARCHIVE) != 0) ? kArchiveAttributeChar
        : kEmptyAttributeChar;
    return ret;
  }

  public String getMethods() {
    return Methods;
  }
}
