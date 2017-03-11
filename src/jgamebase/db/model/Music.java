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

package jgamebase.db.model;

import static jgamebase.Const.log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.Icon;

import jgamebase.Const;
import jgamebase.db.Db;
import jgamebase.gui.Gui;
import jgamebase.model.Databases;
import jgamebase.model.Emulator;
import jgamebase.model.Emulators;
import jgamebase.model.FileExtensions;
import jgamebase.model.Paths;
import jgamebase.model.Plugins;
import jgamebase.model.Preferences;
import jgamebase.plugins.MusicInfo;
import jgamebase.tools.FileTools;
import jgamebase.tools.ListerTools;
import jgamebase.tools.TempDir;

public class Music implements Item {

  private static final long serialVersionUID = -9041851476859838451L;

  static boolean allowReadingInfoFromFile = false;

  public static synchronized void setAllowReadingInfoFromFile(final boolean allowReadingInfoFromFile) {
    Music.allowReadingInfoFromFile = allowReadingInfoFromFile;
  }

  // Fields
  private int id;

  private String filename;

  private String name;

  private Musician musician;

  private boolean isFavourite;

  private boolean fileExists;

  private boolean isAdult;

  private String publisherForDisplay = null;

  private String yearForDisplay = null;

  private String musicianForDisplay = null;

  // Constructors
  /** default constructor */
  public Music() {
  }

  public Music(final String name, final String filename) {
    setName(name);
    setFilename(filename);

    musician = Db.getMusicianById(Musician.NEUTRAL_ID);
    fileExists = true;
  }

  /** constructor with id */
  public Music(final int id) {
    this.id = id;
  }

  // Property accessors
  @Override
  public String getFilename() {
    return Paths.backslashToSlash(filename);
  }

  public void setFilename(final String filename) {
    this.filename = Paths.slashToBackslash(filename);
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public String getStringId() {
    return "M" + id;
  }

  public void setId(final int id) {
    this.id = id;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(final String name) {
    this.name = name;
  }

  @Override
  synchronized public Musician getMusician() {
    return musician;
  }

  synchronized public void setMusician(final Musician musician) {
    this.musician = musician;
    musicianForDisplay = null;
  }

  @Override
  public boolean getIsFavourite() {
    return isFavourite;
  }

  @Override
  public void setIsFavourite(final boolean isFavourite) {
    this.isFavourite = isFavourite;
  }

  public boolean getFileExists() {
    return fileExists;
  }

  public void setFileExists(final boolean fileExists) {
    this.fileExists = fileExists;
  }

  @Override
  public boolean getIsAdult() {
    return isAdult;
  }

  public void setIsAdult(final boolean isAdult) {
    this.isAdult = isAdult;
  }

  @Override
  public Icon getIcon() {
    return Const.ICON_LIST_SIDONLY;
  }

  public String getPublisherForDisplay() {
    if ((publisherForDisplay == null) && allowReadingInfoFromFile) {
      tryToDetermineInfoFromFile();
    }

    if ((publisherForDisplay == null) || (publisherForDisplay.isEmpty())) {
      return "(Unknown)";
    }

    return publisherForDisplay;
  }

  public String getYearForDisplay() {
    if ((yearForDisplay == null) && allowReadingInfoFromFile) {
      tryToDetermineInfoFromFile();
    }

    if ((yearForDisplay == null) || (yearForDisplay.isEmpty())) {
      return "????";
    }

    return yearForDisplay;
  }

  public synchronized String getMusicianForDisplay() {
    // if we already have info, return it
    if ((musicianForDisplay != null) && (!musicianForDisplay.isEmpty())) {
      return musicianForDisplay;
    }

    // if there is info about a musician in the database, use it
    if ((musician != null) && (musician.getName() != null) && (!musician.getName().isEmpty())
        && (musician.getId() != Musician.NEUTRAL_ID)) {
      musicianForDisplay = musician.getName();
      if (!musician.getNickname().isEmpty()) {
        musicianForDisplay += " '" + musician.getNickname() + "'";
      }
      if (!musician.getGroup().isEmpty()) {
        musicianForDisplay += " of " + musician.getGroup();
      }
    }

    if ((musicianForDisplay == null) && allowReadingInfoFromFile) {
      tryToDetermineInfoFromFile();
    }

    if ((musicianForDisplay == null) || (musicianForDisplay.isEmpty())) {
      return "(Unknown)";
    }

    return musicianForDisplay;
  }

  public void tryToDetermineInfoFromFile() {
    MusicInfo musicInfo = null;

    // so we don't try to load again if loading fails
    yearForDisplay = "";
    publisherForDisplay = "";
    musicianForDisplay = "";

    try {
      if (Plugins.existsMusicInfoForExtension(FileTools.getExtension(getFilename()))) {
        musicInfo = Plugins.getMusicInfoForExtension(FileTools.getExtension(getFilename()));
        if (musicInfo != null) {
          musicInfo.load(Paths.getMusicPath().find(new File(getFilename())));

          final String copyright = musicInfo.getCopyright() == null ? "" : musicInfo.getCopyright()
              .trim();

          String year = "";
          final StringBuffer publisher = new StringBuffer();

          // cut the copyright at whitespace
          final StringTokenizer st = new StringTokenizer(copyright);

          // first token is year
          if (st.hasMoreTokens()) {
            final String s = st.nextToken();

            if (Character.isDigit(s.length() > 0 ? s.charAt(0) : ' ')) {
              // if first char of token is digit, it could be a year
              year = s;
            } else {
              // first char is no digit, could be part of publisher
              publisher.append(s);
            }

          }

          // other tokens are publisher
          while (st.hasMoreTokens()) {
            publisher.append(" ").append(st.nextToken());
          }

          publisherForDisplay = publisher.toString().trim();
          yearForDisplay = year.trim();

          // if there is info about a musician in the database, use this instead
          // of the info from the file
          if ((musician == null) || (musician.getName() == null) || (musician.getName().isEmpty())
              || (musician.getId() == Musician.NEUTRAL_ID)) {
            musicianForDisplay = musicInfo.getAuthor() == null ? "" : musicInfo.getAuthor().trim();
          }
        }
      }

    } catch (final IOException ioe) {
      // ioe.printStackTrace();
    }

  }

  @Override
  public void play() {
    Music.play(this, null, false);
  }

  public void play(final boolean isAlreadyExtracted) {
    Music.play(this, null, isAlreadyExtracted);
  }

  public void play(final Emulator emulator) {
    Music.play(this, emulator, false);
  }

  private static void play(final Music music, Emulator emulator, final boolean isAlreadyExtracted) {
    boolean usePrefix = false;

    log.info("PLAY MUSIC: " + music.getName() + " !");

    String filename;
    String fileToRun;
    List<String> namesOfSupportedFiles = new ArrayList<String>();

    filename = (Paths.getMusicPath().findAndWarn(new File(music.getFilename())) != null) ? Paths
        .getMusicPath().findAndWarn(new File(music.getFilename())).getPath() : "";
    fileToRun = filename;

    if (Plugins.existsExtractorForExtension(FileTools.getExtension(filename))) {
      FileTools.copyOrExtractToCleanTempDir(filename);
      FileExtensions extensions;

      if (emulator == null) {
        extensions = Emulators.getSupportedMusicExtensions();
      } else {
        extensions = emulator.getSupportedExtensions();
      }

      // get files matching extensions
      namesOfSupportedFiles = extensions.getMatching(ListerTools.list_Dirs_Files_Paths(TempDir
          .getPath().getAbsolutePath(), false, true, true));

      // no files found =>
      if (namesOfSupportedFiles.isEmpty()) {
        // try prefix
        namesOfSupportedFiles = extensions.getMatchingAsPrefix(ListerTools.list_Dirs_Files_Paths(
            TempDir.getPath().getAbsolutePath(), false, true, true));
        usePrefix = true;
      }

      // still no files found =>
      if (namesOfSupportedFiles.isEmpty()) {
        // error and return
        Gui.displayErrorDialog("No playable file found for music '" + music.getName() + "'!");
        return;
      } else {
        // if supported files were found
        // get first supported file to run
        fileToRun = namesOfSupportedFiles.get(0);
      }
    }

    log.info("FileToRun: '" + fileToRun + "'.");

    if ((fileToRun != null) && (!fileToRun.isEmpty())) {

      // emulator supplied?
      if (emulator == null) {
        emulator = Emulators.findMusicEmulatorForExtension(FileTools.getExtension(fileToRun));
        if (emulator == null) {
          emulator = Emulators.findMusicEmulatorForExtension(FileTools.getPrefix(fileToRun));
          usePrefix = true;
        }
      }

      if (emulator != null) {
        log.info("Using Emulator: '" + emulator.getName() + "'.");

        // update info
        Preferences.inc(Preferences.ABOUT_MUSIC_PLAYED);

        // "namesOfSupportedFiles" contains all files supported by ALL emulators
        // now only the files for THIS emulator remain
        if (usePrefix) {
          namesOfSupportedFiles = emulator.getSupportedExtensions().getMatchingAsPrefix(
              namesOfSupportedFiles);
        } else {
          namesOfSupportedFiles = emulator.getSupportedExtensions().getMatching(
              namesOfSupportedFiles);
        }

        final File script = new File(new File(Databases.getCurrent().getPath(),
            Const.SCRIPT_DIRNAME), emulator.getCommand());

        // check if emulator command is a script
        if (script.exists()) {

          final ScriptEngineManager manager = new ScriptEngineManager();
          final ScriptEngine engine = manager.getEngineByExtension(FileTools.getExtension(emulator
              .getCommand()));

          // if script has no supported script engine, start as batch script
          if (engine == null) {
            log.info("BATCHSCRIPT");
            // make script executable
            script.setExecutable(true, false);
            // execute
            Emulators.executeMusic(new String[] { script.getAbsolutePath(), fileToRun });
          } else {
            log.info("SCRIPT");
            // invoke script by script engine
            final String includesFilename = Const.INCLUDE_FILENAME + "."
                + FileTools.getExtension(emulator.getCommand());
            final File file = null;

            String includesAsString = "";
            String includesPathAndFilename = "";

            try {
              // add objects to script engine (first letter is upper case)
              final Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
              bindings.putAll(Emulators.setupEnvironment(music, fileToRun, 0, emulator,
                  namesOfSupportedFiles));
              engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);

              // search for includes in default script dir
              includesPathAndFilename = new File(new File(Const.GBDIR_RO, Const.INCLUDE_DIRNAME),
                  includesFilename).getAbsolutePath();
              if (new File(includesPathAndFilename).exists()) {
                log.info("Loading includes '" + includesPathAndFilename + "'.");
                includesAsString = FileTools.readFileAsString(includesPathAndFilename);
              }

              if (!includesAsString.isEmpty()) {
                includesAsString += Const.LINE_SEPARATOR;
              }

              final String scriptAsString = FileTools.readFileAsString(script.getAbsolutePath());

              // execute script with includes
              log.info("Executing script '" + script.getAbsolutePath() + "'...");
              engine.eval(includesAsString + scriptAsString);

            } catch (final IOException ioe) {
              log.error("The script '" + script.getAbsolutePath() + "' could not be loaded.");
            } catch (final ScriptException se) {

              if (!includesAsString.isEmpty()) {
                final Scanner scanner = new Scanner(includesAsString);
                scanner.useDelimiter(Const.LINE_SEPARATOR_PATTERN);
                int includedLines = 0;
                while (scanner.hasNext()) {
                  includedLines++;
                  scanner.next();
                }

                log.error(se.getMessage());
                log.error("IMPORTANT: When locating the error please bear in mind that an include of "
                    + includedLines + " lines was added before your script:");

                if (se.getLineNumber() <= includedLines) {
                  log.error("So the error really occured at line " + se.getLineNumber()
                      + " in the include '" + includesPathAndFilename + "' .");
                } else {
                  log.error("So the error really occured at line "
                      + (se.getLineNumber() - includedLines) + " in the script '"
                      + script.getAbsolutePath() + "' .");
                }
                log.error("");
              }
            }
          }
        } else { // command is an executable
          log.info("EXECUTABLE");
          // execute
          Emulators.executeMusic(new String[] { emulator.getCommand(), fileToRun });
        }

      } else {
        Gui.displayErrorDialog("No Music Emulator found for "
            + ((usePrefix) ? "prefix '" + FileTools.getPrefix(fileToRun) : "extension '"
                + FileTools.getExtension(fileToRun)) + "'.");
      }
    }
  }

  @Override
  public String toString() {
    return getName();
  }

  @Override
  public String createId() {
    return getName().toLowerCase() + getId() + this.getClass();
  }

  @Override
  public int compareTo(final Item other) {
    return createId().compareTo(other.createId());
  }

  @Override
  public boolean equals(final Object other) {
    if (other instanceof Music) {
      return createId().equals(((Music) other).createId());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return createId().hashCode();
  }
}
