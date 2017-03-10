package jgamebase.plugins.extractor.sevenzip.SevenZip;

import java.io.File;

import jgamebase.plugins.extractor.sevenzip.SevenZip.Archive.IArchiveExtractCallback;
import jgamebase.plugins.extractor.sevenzip.SevenZip.Archive.IInArchive;
import jgamebase.plugins.extractor.sevenzip.SevenZip.Archive.SevenZipEntry;

public class ArchiveExtractCallback implements IArchiveExtractCallback // ,
                                                                       // ICryptoGetTextPassword,
{

  class OutputStream extends java.io.OutputStream {
    java.io.RandomAccessFile file;

    public OutputStream(final java.io.RandomAccessFile f) {
      file = f;
    }

    @Override
    public void close() throws java.io.IOException {
      file.close();
      file = null;
    }

    /*
     * public void flush() throws java.io.IOException { file.flush(); }
     */
    @Override
    public void write(final byte[] b) throws java.io.IOException {
      file.write(b);
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws java.io.IOException {
      file.write(b, off, len);
    }

    @Override
    public void write(final int b) throws java.io.IOException {
      file.write(b);
    }
  }

  @Override
  public int SetTotal(final long size) {
    return HRESULT.S_OK;
  }

  @Override
  public int SetCompleted(final long completeValue) {
    return HRESULT.S_OK;
  }

  public void PrintString(final String str) {
    System.out.print(str);
  }

  public void PrintNewLine() {
    System.out.println("");
  }

  @Override
  public int PrepareOperation(final int askExtractMode) {
    _extractMode = false;
    switch (askExtractMode) {
      case IInArchive.NExtract_NAskMode_kExtract:
        _extractMode = true;
    }
    ;
    switch (askExtractMode) {
      case IInArchive.NExtract_NAskMode_kExtract:
        PrintString("Extracting  ");
        break;
      case IInArchive.NExtract_NAskMode_kTest:
        PrintString("Testing     ");
        break;
      case IInArchive.NExtract_NAskMode_kSkip:
        PrintString("Skipping    ");
        break;
    }
    ;
    PrintString(_filePath);
    return HRESULT.S_OK;
  }

  @Override
  public int SetOperationResult(final int operationResult) throws java.io.IOException {
    switch (operationResult) {
      case IInArchive.NExtract_NOperationResult_kOK:
        break;
      default: {
        NumErrors++;
        PrintString("     ");
        switch (operationResult) {
          case IInArchive.NExtract_NOperationResult_kUnSupportedMethod:
            PrintString("Unsupported Method");
            break;
          case IInArchive.NExtract_NOperationResult_kCRCError:
            PrintString("CRC Failed");
            break;
          case IInArchive.NExtract_NOperationResult_kDataError:
            PrintString("Data Error");
            break;
          default:
            PrintString("Unknown Error");
        }
      }
    }
    /*
     * if(_outFileStream != null &&
     * _processedFileInfo.UTCLastWriteTimeIsDefined)
     * _outFileStreamSpec->File.SetLastWriteTime
     * (&_processedFileInfo.UTCLastWriteTime);
     */
    if (_outFileStream != null) {
      _outFileStream.close(); // _outFileStream.Release();
    }
    /*
     * if (_extractMode && _processedFileInfo.AttributesAreDefined)
     * NFile::NDirectory::MySetFileAttributes(_diskFilePath,
     * _processedFileInfo.Attributes);
     */
    PrintNewLine();
    return HRESULT.S_OK;
  }

  java.io.OutputStream _outFileStream;

  @Override
  public int GetStream(final int index, final java.io.OutputStream[] outStream,
      final int askExtractMode) throws java.io.IOException {

    outStream[0] = null;

    final SevenZipEntry item = _archiveHandler.getEntry(index);
    // extract to current directory if no directory specified
    if ((_dirPath == null) || (_dirPath.isEmpty())) {
      _filePath = item.getName();
    } else {
      String itemName = item.getName();
      // find item name without first directory
      if (itemName.contains(File.separator)) {
        itemName = item.getName().substring(itemName.indexOf(File.separatorChar) + 1,
            itemName.length());
      }
      _filePath = new File(_dirPath, itemName).getCanonicalPath();
    }

    final File file = new File(_filePath);

    switch (askExtractMode) {
      case IInArchive.NExtract_NAskMode_kTest:
        return HRESULT.S_OK;

      case IInArchive.NExtract_NAskMode_kExtract:

        try {
          isDirectory = item.isDirectory();

          if (isDirectory) {
            if (file.isDirectory()) {
              return HRESULT.S_OK;
            }
            if (file.mkdirs()) {
              return HRESULT.S_OK;
            } else {
              return HRESULT.S_FALSE;
            }
          }

          final File dirs = file.getParentFile();
          if (dirs != null) {
            if (!dirs.isDirectory()) {
              if (!dirs.mkdirs()) {
                return HRESULT.S_FALSE;
              }
            }
          }

          final long pos = item.getPosition();
          if (pos == -1) {
            file.delete();
          }

          final java.io.RandomAccessFile outStr = new java.io.RandomAccessFile(_filePath, "rw");

          if (pos != -1) {
            outStr.seek(pos);
          }

          outStream[0] = new OutputStream(outStr);
        } catch (final java.io.IOException e) {
          return HRESULT.S_FALSE;
        }

        return HRESULT.S_OK;

    }

    // other case : skip ...

    return HRESULT.S_OK;

  }

  jgamebase.plugins.extractor.sevenzip.SevenZip.Archive.IInArchive _archiveHandler; // IInArchive
  String _filePath; // name inside archive
  String _dirPath = ""; // name of directory to extract to
  String _diskFilePath; // full path to file on disk

  public long NumErrors;
  boolean PasswordIsDefined;
  String Password;
  boolean _extractMode;

  boolean isDirectory;

  public ArchiveExtractCallback() {
    PasswordIsDefined = false;
  }

  public void Init(
      final jgamebase.plugins.extractor.sevenzip.SevenZip.Archive.IInArchive archiveHandler) {
    NumErrors = 0;
    _archiveHandler = archiveHandler;
  }

  public void SetDirPath(final String path) {
    _dirPath = path == null ? "" : path;
  }

}
