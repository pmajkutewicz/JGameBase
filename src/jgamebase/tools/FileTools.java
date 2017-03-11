/*
 * Copyright (C) 2006-2014 F. Gerbig (fgerbig@users.sourceforge.net)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package jgamebase.tools;

import static jgamebase.Const.log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import jgamebase.Const;
import jgamebase.model.Databases;
import jgamebase.model.Emulators;
import jgamebase.model.Paths;
import jgamebase.model.Plugins;
import jgamebase.plugins.Extractor;

public class FileTools {

  // size of the copy buffer
  private static final int BUFFER_SIZE = 1 * 1024 * 1024;
  // a buffer for the copy method
  private static byte[] buffer = new byte[BUFFER_SIZE];

  /**
   * Deletes a directory with contained files and subdirectories.
   * 
   * @param dir
   *          The directory to delete.
   * @return <code>true</code> if the deletion could be completed,
   *         <code>false</code> if an error accoured.
   */
  public static boolean deleteAll(final File dir) {
    if (dir.isDirectory()) {
      final String[] filesAndDirs = dir.list();
      for (int i = 0; i < filesAndDirs.length; i++) {
        if (!deleteAll(new File(dir, filesAndDirs[i]))) {
          return false;
        }
      }
    }
    return dir.delete();
  }

  public static String insertBeforeFileextension(final String filename, final String insert) {
    if (filename == null) {
      return "";
    }

    final int dotIndex = filename.lastIndexOf('.');
    if (dotIndex == -1) {
      return "";
    }

    return filename.substring(0, dotIndex) + insert
        + filename.substring(dotIndex, filename.length());
  }

  public static long checksum(final String s) {
    final Checksum checksum = new CRC32();
    checksum.update(s.getBytes(), 0, s.getBytes().length);

    return checksum.getValue();
  }

  public static String getExtension(final String filename) {
    if ((filename == null) || filename.isEmpty()
        || (filename.lastIndexOf(Const.EXTENSION_SEPARATOR) == -1)) {
      return "";
    }

    return filename.substring(filename.lastIndexOf(Const.EXTENSION_SEPARATOR) + 1,
        filename.length()).toLowerCase();
  }

  public static String removeExtension(final String filename) {
    if ((filename == null) || filename.isEmpty()) {
      return "";
    }

    if (filename.lastIndexOf(Const.EXTENSION_SEPARATOR) == -1) {
      return filename;
    }

    return filename.substring(0, filename.lastIndexOf(Const.EXTENSION_SEPARATOR));
  }

  public static String removeAllExtensions(final String filename) {
    if ((filename == null) || filename.isEmpty()) {
      return "";
    }

    String s = new String(filename);
    while (s.lastIndexOf(Const.EXTENSION_SEPARATOR) != -1) {
      s = removeExtension(s);
    }

    return s;
  }

  public static String getPrefix(String filename) {
    if ((filename == null) || filename.isEmpty()
        || (filename.lastIndexOf(Const.EXTENSION_SEPARATOR) == -1)) {
      return "";
    }

    filename = Paths.removePath(filename);
    return filename.substring(0, filename.indexOf(Const.EXTENSION_SEPARATOR)).toLowerCase();
  }

  public static String removePrefix(final String filename) {
    if ((filename == null) || filename.isEmpty()) {
      return "";
    }

    if (!filename.contains(Const.EXTENSION_SEPARATOR)) {
      return filename;
    }

    return filename.substring(filename.indexOf(Const.EXTENSION_SEPARATOR) + 1, filename.length());
  }

  public static String changePrefixToExtension(final String filename) {
    return removePrefix(filename) + Const.EXTENSION_SEPARATOR + getPrefix(filename);
  }

  public static String removeSpace(String filename) {
    if (filename == null) {
      return "";
    }

    while (filename.indexOf(' ') != -1) {
      filename = filename.replace(' ', '_');
    }
    return filename;
  }

  /**
   * Copies the source file to the given destination with the same filename.
   * 
   * @param src
   *          The file to copy.
   * @param dst
   *          The destination (where to copy the source file).
   * @throws IOException
   * @throws WarningException
   * @throws ErrorException
   */
  public static void copyFile(final File src, final File dst) throws IOException {

    long lastModified;

    // first test for common errors:

    // does source file exist ?
    if (!src.exists()) {
      throw new IOException("Source file not found.");
    }

    // is source file really a file ?
    if (!src.isFile()) {
      throw new IOException("Source isn't a file.");
    }

    // can the source file be read ?
    if (!src.canRead()) {
      throw new IOException("Source file can't be read.");
    }

    // create the destination directory if necessary
    if ((dst.getParentFile() != null) && (!dst.getParentFile().isDirectory())) {
      dst.getParentFile().mkdirs();
    }

    if (dst.exists()) { // does the destination file already exist?

      // can the existing destination file be overwritten ?
      if (!dst.canWrite()) {
        throw new IOException("Destination file can't be overwritten.");
      }

    } else {

      // can the not existing destination file be created?
      try {
        dst.createNewFile();
      } catch (final Exception e) {
        throw new IOException("Destination file can't be created.");
      }
    }

    // The input stream
    FileInputStream in = null;
    // The output stream
    FileOutputStream out = null;

    try {
      try {
        in = new FileInputStream(src);
        if (in == null) {
          throw new IOException("Could not open input stream for file '" + src + "'");
        }
      } catch (final FileNotFoundException fnfe) {
        throw new IOException("Could not open input stream for file '" + src
            + "' (probably because it is being used by another process)");
      }

      try {
        out = new FileOutputStream(dst);
        if (out == null) {
          throw new IOException("Could not open output stream for file '" + dst + "'");
        }
      } catch (final FileNotFoundException fnfe) {
        throw new IOException("Could not open output stream for file '" + dst
            + "' (probably because access to the source file '" + src + "' is denied)");
      }

      // read data into the buffer and write until end of file (read() == -1)
      int copied = 0;
      while ((copied = in.read(FileTools.buffer)) != -1) {
        out.write(FileTools.buffer, 0, copied);
      }

      // copy the file modification date
      lastModified = src.lastModified();
      // solves the problem of files without modification time on NTFS
      if (lastModified < 0) {
        lastModified = 0;
      }
      dst.setLastModified(lastModified);

    } finally {
      try {
        // always close the streams
        if (in != null) {
          in.close();
        }
        if (out != null) {
          out.close();
        }
      } catch (final Exception e) {
      }
    }
  }

  /**
   * Changes the path of the given file from the source path to the destination
   * path.
   * 
   * @param srcFile
   *          The file in the source directory.
   * @param srcPath
   *          The source path.
   * @param dstPath
   *          The destination path.
   * @return File The file with the destination path.
   */
  public static File replacePath(final File srcFile, final File srcPath, final File dstPath) {

    // get actual path
    final String path = srcFile.getAbsolutePath();
    // delete source path from actual path
    String cut = path.substring(srcPath.getAbsolutePath().length(), path.length());

    if (!cut.startsWith(File.separator)) {
      cut = File.separator + cut;
    }
    // add destination path with remaining actual path
    final String newPath = dstPath.getAbsolutePath() + cut;

    return new File(newPath);
  }

  /**
   * Changes the path of the given files from the sorce path to the destination
   * path.
   * 
   * @param srcFiles
   *          The files in the source directory.
   * @param srcPath
   *          The source path.
   * @param dstPath
   *          The destination path.
   * @return File[] The files with the destination path.
   */
  private static File[] replacePath(final File[] srcFiles, final File srcPath, final File dstPath) {

    final File[] dstFiles = new File[srcFiles.length];

    // for all files
    for (int i = 0; i < srcFiles.length; i++) {
      dstFiles[i] = replacePath(srcFiles[i], srcPath, dstPath);
    }

    return dstFiles;
  }

  /**
   * Copies a directory.
   * <p>
   * Steps:
   * <ol>
   * <li>read all files from source (recursive)</li>
   * <li>Clone this dirctorystructure to destination</li>
   * <li>Copy files</li>
   * </ol>
   * 
   * @throws Exception
   */
  public static void copyDir(final File src, final File dst) throws Exception {

    // check that source directory exists
    if (!src.exists() || !src.isDirectory()) {
      throw new IOException("Source directory '" + src.getAbsolutePath()
          + "' doesn't exist or isn't a directory!");
    }

    // check that destination directory exists
    if (dst.exists() && !dst.isDirectory()) {
      throw new IOException("Destination directory '" + dst.getAbsolutePath()
          + "' isn't a directory!");
    }

    // start recursive copy
    _copyDir(src, dst, src);
  }

  private static void _copyDir(final File src, final File dst, final File currentDir)
      throws Exception {
    int i;

    final File[] srcDirs = FileTools.listDirectories(currentDir);
    // create a new array of destination dirs
    // (path changed from source to destination)
    final File[] dstDirs = FileTools.replacePath(srcDirs, src, dst);

    final File[] srcFiles = FileTools.listFiles(currentDir);

    // create a new array of destination files
    // (path changed from source to destination)
    final File[] dstFiles = FileTools.replacePath(srcFiles, src, dst);

    // files
    for (i = 0; i < srcFiles.length; i++) {
      copyFile(srcFiles[i], dstFiles[i]);
    }

    // directories
    for (i = 0; i < srcDirs.length; i++) {
      _copyDir(src, dst, srcDirs[i]);
      dstDirs[i].mkdirs();
    }
  }

  private static void _copyDir(final File src, final File dst, final File currentDir,
      final String[] excludes) throws Exception {
    int i;

    final File[] srcDirs = FileTools.listDirectories(currentDir, excludes);
    // create a new array of destination dirs
    // (path changed from source to destination)
    final File[] dstDirs = FileTools.replacePath(srcDirs, src, dst);

    final File[] srcFiles = FileTools.listFiles(currentDir);

    // create a new array of destination files
    // (path changed from source to destination)
    final File[] dstFiles = FileTools.replacePath(srcFiles, src, dst);

    // files
    for (i = 0; i < srcFiles.length; i++) {
      copyFile(srcFiles[i], dstFiles[i]);
    }

    // directories
    for (i = 0; i < srcDirs.length; i++) {
      _copyDir(src, dst, srcDirs[i]);
      dstDirs[i].mkdirs();
    }
  }

  /**
   * Copies a directory.
   * <p>
   * Steps:
   * <ol>
   * <li>read all files from source (recursive)</li>
   * <li>Clone this dirctorystructure to destination</li>
   * <li>Copy files</li>
   * </ol>
   * 
   * @throws Exception
   */
  public static void copyDir(final File src, final File dst, final String[] excludes)
      throws Exception {

    // check that source directory exists
    if (!src.exists() || !src.isDirectory()) {
      throw new IOException("Source directory '" + src.getAbsolutePath()
          + "' doesn't exist or isn't a directory!");
    }

    // check that destination directory exists
    if (dst.exists() && !dst.isDirectory()) {
      throw new IOException("Destination directory '" + dst.getAbsolutePath()
          + "' isn't a directory!");
    }

    // start recursive copy
    _copyDir(src, dst, src, excludes);
  }

  // read the File as String
  public static String readFileAsString(final String filePath) throws IOException {
    final StringBuilder fileData = new StringBuilder();
    final BufferedReader reader = new BufferedReader(new FileReader(filePath));
    final char[] buf = new char[BUFFER_SIZE];
    int numRead = 0;
    while ((numRead = reader.read(buf)) != -1) {
      final String readData = String.valueOf(buf, 0, numRead);
      fileData.append(readData);
    }
    reader.close();
    return fileData.toString();
  }

  // write the String as File
  public static void writeStringAsFile(final File file, final String content) throws IOException {
    final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
    writer.write(content);
    writer.flush();
    writer.close();
  }

  private static File[] listFiles(final File dir) {
    // get all files and dirs in this directory
    final File[] filesAndDirs = dir.listFiles();

    if (filesAndDirs == null) {
      return new File[0];
    }

    // count number of files
    int count = 0;
    for (final File filesAndDir : filesAndDirs) {
      if (filesAndDir.isFile()) {
        count++;
      }
    }

    // copy all files to a new array
    final File[] files = new File[count];
    count = 0;
    for (final File filesAndDir : filesAndDirs) {
      if (filesAndDir.isFile()) {
        files[count] = filesAndDir;
        count++;
      }
    }
    return files;
  }

  /**
   * Lists only directories (no files).
   * 
   * @return File[] The directories.
   */
  private static File[] listDirectories(final File dir) {
    // get all files and dirs in this directory
    final File[] filesAndDirs = dir.listFiles();

    if (filesAndDirs == null) {
      return new File[0];
    }

    // count number of directories
    int count = 0;
    for (final File filesAndDir : filesAndDirs) {
      if (filesAndDir.isDirectory()) {
        count++;
      }
    }

    // copy all dirs to a new array
    final File[] dirs = new File[count];
    count = 0;
    for (final File filesAndDir : filesAndDirs) {
      if (filesAndDir.isDirectory()) {
        dirs[count] = filesAndDir;
        count++;
      }
    }

    return dirs;
  }

  /**
   * Lists all directories (no files) that don't match the exclude patterns.
   * 
   * @param excludes
   *          The patterns to exclude.
   * @param printExcluded
   *          If set to <code>true</code> excluded directories are printed.
   * 
   * @return File[] The files.
   */
  private static File[] listDirectories(final File dir, final String[] excludes) {
    // get all files and dirs in this directory
    final File[] filesAndDirs = dir.listFiles();

    boolean includeDirectory;
    int count = 0;

    // count files
    for (final File filesAndDir : filesAndDirs) {
      includeDirectory = false; // might be a file

      // check if directory should be excluded
      if (filesAndDir.isDirectory()) {
        includeDirectory = true;
        // check excludes
        for (final String exclude : excludes) {
          if ((filesAndDir.getName()).equals(exclude)) {
            includeDirectory = false;
          }
        }
      }

      // count files to include
      if (includeDirectory) {
        count++;
      }
    }

    // get files
    final File[] directories = new File[count];
    count = 0;
    for (final File filesAndDir : filesAndDirs) {
      includeDirectory = false;

      // check if directory should be excluded
      if (filesAndDir.isDirectory()) {
        includeDirectory = true;
        // check excludes
        for (final String exclude : excludes) {
          if ((filesAndDir.getName()).equals(exclude)) {
            includeDirectory = false;
          }
        }
      }

      // get directories to include
      if (includeDirectory) {
        directories[count] = filesAndDir;
        count++;
      }
    }

    return directories;
  }

  // search command in os search path and (last) in script directory
  public static String findInPath(final String command) {
    if ((command == null) || (command.isEmpty())) {
      return "";
    }

    final String path = System.getenv("PATH") + File.pathSeparator
        + new File(Databases.getCurrent().getPath(), Const.SCRIPT_DIRNAME).getAbsolutePath();
    final StringTokenizer st = new StringTokenizer(path, File.pathSeparator);

    while (st.hasMoreTokens()) {
      final File file = new File(st.nextToken(), command);
      if (file.exists()) {
        return file.getAbsolutePath();
      }
    }

    return "";
  }

  public static boolean existsInPath(final String command) {
    return !findInPath(command).isEmpty();
  }

  public static boolean isAnimation(final String filename) {
    if ((filename == null) || (filename.isEmpty())) {
      return false;
    }

    return FileTools.getExtension(filename).equalsIgnoreCase("gif");
  }

  public static boolean isUrlFromFilename(final String filename) {
    return getUrlFromFilename(filename) != null;
  }

  public static URL getUrlFromFilename(final String filename) {
    URL url = null;
    final File file = new File(filename);
    final String ext = FileTools.getExtension(filename);

    if ((ext.equalsIgnoreCase("url") || ext.equalsIgnoreCase("desktop")) && (file.length() < 512)) {
      try {
        log.info("Found possible URL file '" + filename + "'");

        final String content = FileTools.readFileAsString(filename);
        final Pattern p = Pattern.compile("^[uU][rR][lL]=.*$");

        final Scanner scanner = new Scanner(content);
        scanner.useDelimiter(Const.LINE_SEPARATOR_PATTERN);
        while (scanner.hasNext()) {
          String line = scanner.next();
          final Matcher m = p.matcher(line);

          if (m.find()) {
            line = line.substring(4);
            url = new URL(line);
            log.info("got URL '" + url + "'");
          }
        }

      } catch (final MalformedURLException mfue) {
        log.warn("Error parsing URL", mfue);
      } catch (final IOException e) {
        // so there is no url...
      }
    }

    return url;
  }

  public static List<String> copyOrExtractToCleanTempDir(final String filename) {
    TempDir.getCleanPath();
    return copyOrExtractToTempDir(filename);
  }
  
  @SuppressWarnings("unchecked")
  public static List<String> copyOrExtractToTempDir(final String filename) {
    final List<String> namesOfSupportedFiles = new ArrayList<String>();

    if (Plugins.existsExtractorForExtension(getExtension(filename))) {
      // extract files
      List<String> filenames = new ArrayList<String>();

      try {
        final Extractor extractor = Plugins.getExtractorForExtension(getExtension(filename));
        filenames = extractor.extractToTempDir(filename);
      } catch (final IOException e) {
        e.printStackTrace();
      }

      // create list containing files (supported only)
      for (final String filename2 : filenames) {
        if (Emulators.getSupportedGameExtensions().contains(getExtension(filename2))) {
          namesOfSupportedFiles.add(filename2);
        }
      }

      // sort list of supported files
      Collections.sort(namesOfSupportedFiles, new Comparator() {
        @Override
        public int compare(final Object o1, final Object o2) {
          return (removeExtension(Paths.removePath(((String) o1).toLowerCase()))
              .compareTo(removeExtension(Paths.removePath(((String) o2).toLowerCase()))));
        }
      });
    } else {
      // copy files
      try {
        namesOfSupportedFiles.add(FileTools.copyToTempDir(filename));
      } catch (final IOException ioe) {
        ioe.printStackTrace();
      }
    }

    return namesOfSupportedFiles;
  }

  public static String copyToTempDir(final String filename) throws IOException {
    final File srcFile = new File(filename);
    final File dstFile = new File(TempDir.getCleanPath(), srcFile.getName());

    FileInputStream in = null;
    FileOutputStream out = null;

    try {
      in = new FileInputStream(srcFile);
      out = new FileOutputStream(dstFile);

      int copied = 0;
      while ((copied = in.read(buffer)) != -1) {
        out.write(buffer, 0, copied); // write data
      }

      return dstFile.getAbsolutePath();
    } finally {
      // always close the streams
      if (in != null) {
        in.close();
      }
      if (out != null) {
        out.close();
      }
    }
  }
}
