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

package jgamebase.model;

import static jgamebase.Const.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import jgamebase.Const;
import jgamebase.db.model.Game;
import jgamebase.db.model.Item;
import jgamebase.db.model.Music;
import jgamebase.gui.Gui;
import jgamebase.plugins.DiskInfo;
import jgamebase.tools.FileTools;
import jgamebase.tools.IniFileManager;
import jgamebase.tools.StringTools;
import jgamebase.tools.TempDir;

public class Emulators {

  private static final File FILE_GAME = new File(new File(Const.GBDIR_RW, Databases.getCurrent()
      .getName()), "GameEmulators.ini");
  private static final File FILE_MUSIC = new File(new File(Const.GBDIR_RW, Databases.getCurrent()
      .getName()), "MusicEmulators.ini");

  private static final String OPTION_NAME = "Name";

  private static final String OPTION_EXTENSIONS = "Extensions";

  private static final String OPTION_COMMAND = "Command";

  private static final String OPTION_CONFIGURATIONFILENAME = "ConfigurationFilename";

  private static List<Emulator> gameEmulators = null;

  private static List<Emulator> musicEmulators = null;

  private static Process musicRunning = null;

  public static void setGameEmulators(final List<Emulator> emulators) {
    gameEmulators = emulators;
  }

  public static List<Emulator> getGameEmulators() {
    return gameEmulators;
  }

  public static void setMusicEmulators(final List<Emulator> emulators) {
    musicEmulators = emulators;
  }

  public static List<Emulator> getMusicEmulators() {
    return musicEmulators;
  }

  public static FileExtensions getSupportedGameExtensions() {
    // collect supported extensions from all game emulators
    final FileExtensions supported = new FileExtensions();

    for (final Emulator emulator : gameEmulators) {
      // if one emulator has "*", return only "*"
      if (emulator.getSupportedExtensions().matchesAll()) {
        return emulator.getSupportedExtensions();
      }
      supported.add(emulator.getSupportedExtensions());
    }

    return supported;
  }

  public static FileExtensions getSupportedMusicExtensions() {
    // collect supported extensions from all game emulators
    final FileExtensions supported = new FileExtensions();

    for (final Emulator emulator : musicEmulators) {
      if (emulator.getSupportedExtensions().matchesAll()) {
        return emulator.getSupportedExtensions();
      }
      supported.add(emulator.getSupportedExtensions());
    }

    return supported;
  }

  public static void readFromIniFile() {
    try {
      gameEmulators = load(FILE_GAME);
      musicEmulators = load(FILE_MUSIC);
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  private static List<Emulator> load(final File filename) throws IOException {
    final List<Emulator> emulators = new ArrayList<Emulator>();
    final IniFileManager ini = new IniFileManager(filename);

    final List<String> sectionNames = ini.getSectionNames();
    for (final String sectionName : sectionNames) {
      final Emulator emulator = new Emulator();
      String title = ini.get(sectionName, OPTION_NAME);
      if ((title == null) || (title.isEmpty())) {
        title = "New Emulator";
      }
      emulator.setName(title);

      final FileExtensions extensions = new FileExtensions(ini.get(sectionName, OPTION_EXTENSIONS));
      if ((extensions == null) || (extensions.isEmpty())) {
        extensions.setMatchesAll();
      }
      emulator.setSupportedExtensions(extensions);

      String command = ini.get(sectionName, OPTION_COMMAND);
      // if there is no command specified for this emulator emulate the old
      // behavior
      if ((command == null) || (command.isEmpty())) {
        command = emulator.getName().toLowerCase() + "." + Const.DEFAULT_SCRIPT_EXTENSION;
      }
      emulator.setCommand(command);

      String configurationFilename = ini.get(sectionName, OPTION_CONFIGURATIONFILENAME);
      if ((configurationFilename == null)) {
        configurationFilename = "";
      }
      emulator.setConfigurationFilename(configurationFilename);
      emulators.add(emulator);
    }

    return emulators;
  }

  public static void writeToIniFile() {
    try {
      save(FILE_GAME, gameEmulators);
      save(FILE_MUSIC, musicEmulators);
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  private static void save(final File file, final List<Emulator> emulators) throws IOException {
    final IniFileManager ini = new IniFileManager(file);
    String sectionName = "";

    ini.removeAllSections();
    int i = 1;
    for (final Emulator emulator : emulators) {
      sectionName = i + "";
      ini.addSection(sectionName);
      ini.set(sectionName, OPTION_NAME, emulator.getName());
      ini.set(sectionName, OPTION_EXTENSIONS, emulator.getSupportedExtensions().toString());
      ini.set(sectionName, OPTION_COMMAND, emulator.getCommand());
      ini.set(sectionName, OPTION_CONFIGURATIONFILENAME, emulator.getConfigurationFilename());
      i++;
    }

    ini.save();
  }

  public static Emulator findGameEmulatorForExtension(final String extension) {
    for (final Emulator emulator : gameEmulators) {
      if (emulator.getSupportedExtensions().contains(extension)) {
        return emulator;
      }
    }
    return null;
  }

  public static Emulator findMusicEmulatorForExtension(final String extension) {
    for (final Emulator emulator : musicEmulators) {
      if (emulator.getSupportedExtensions().contains(extension)) {
        return emulator;
      }
    }
    return null;
  }

  // don't let anyone instantiate this class
  private Emulators() {
  }

  // executing game prints the output of the emulator
  public static synchronized void executeGame(final String[] command,
      final Map<String, Object> itemEnvironment) {
    stopMusicRunning();

    try {
      final StringBuffer commandline = new StringBuffer();
      for (final String part : command) {
        commandline.append("<" + part + "> ");
      }
      log.info("Executing game: '" + commandline.toString().trim() + "'");

      if (command.length == 0) {
        log.info("no command to execute!");
        return;
      }

      final ProcessBuilder builder = new ProcessBuilder(command);
      builder.redirectErrorStream(true);

      // set working directory
      final File commandFile = new File(command[0]);
      File workDir = null;
      if (commandFile.isAbsolute()) { // use dir of command
        workDir = commandFile.getParentFile();
      } else { // use script dir
        workDir = new File(Databases.getCurrent().getPath(), Const.SCRIPT_DIRNAME);
      }
      log.info("Setting work directory to '" + workDir.getAbsolutePath() + "'.");
      builder.directory(workDir);

      // add environment variables
      if (itemEnvironment != null) {
        final Map<String, String> environment = builder.environment();
        environment.putAll(convertEnvironmentToString(itemEnvironment));
      }

      // Map<String,String> environment = builder.environment();
      // for (Entry entry : environment.entrySet()) {
      // log.info(entry.getKey() + "=" + entry.getValue());
      // }

      final Process process = builder.start();

      final BufferedReader stdOutErr = new BufferedReader(new InputStreamReader(
          process.getInputStream()));
      String line = null;

      // write output of command
      while ((line = stdOutErr.readLine()) != null) {
        log.info(line);
      }

      // wait for end of process
      try {
        process.waitFor();
      } catch (final InterruptedException e) {
      } finally {
        if (stdOutErr != null) {
          stdOutErr.close();
        }
      }

      log.info("");

    } catch (final FileNotFoundException e) {
      Gui.displayErrorDialog("Command '" + Arrays.toString(command) + "' not found.");
    } catch (final IOException e) {
      Gui.displayErrorDialog("Error while executing command '" + Arrays.toString(command) + "':\n"
          + e.getMessage() + ".");
      log.info(e.getMessage());
    }
  }

  private static Map<String, String> convertEnvironmentToString(final Map<String, Object> envIn) {
    final Map<String, String> envOut = new HashMap<String, String>();

    for (final String key : envIn.keySet()) {
      final Object o = (envIn.get(key) != null) ? envIn.get(key) : "";
      if (o.getClass().isArray()) {
        for (int i = 0; i < Array.getLength(o); i++) {
          envOut.put(key + "_" + i, Array.get(o, i).toString());
        }
        envOut.put(key + "_length", Integer.toString(Array.getLength(o)));
      } else {
        envOut.put(key, o.toString());
      }
    }
    return envOut;
  }

  public static synchronized void executeGame(final String[] command) {
    executeGame(command, null);
  }

  // executing music returns immediately
  public static synchronized void executeMusic(final String[] command) {
    stopMusicRunning();

    log.info("Executing music: " + Arrays.toString(command));
    try {
      final ProcessBuilder builder = new ProcessBuilder(command);
      musicRunning = builder.start();
    } catch (final FileNotFoundException fnfe) {
      Gui.displayErrorDialog("Command '" + Arrays.toString(command) + "' not found.");
    } catch (final IOException e) {
      Gui.displayErrorDialog("Error while executing command '" + Arrays.toString(command) + "'.");
    }
  }

  public static synchronized boolean isMusicRunning() {
    // no process => no music running
    if (musicRunning == null) {
      return false;
    }

    try {
      // get exit value (this throws an exception when the process is still
      // running)
      musicRunning.exitValue();
      // no exception => process has finished => no music running => dispose of
      // process
      stopMusicRunning();
      return false;
    } catch (final IllegalThreadStateException e) {
      return true;
    }
  }

  public static synchronized void stopMusicRunning() {
    if (musicRunning != null) {
      musicRunning.destroy();
      musicRunning = null;
      try {
        Thread.sleep(500);
      } catch (final InterruptedException e) {
      }
    }
  }

  public static Map<String, Object> setupEnvironment(final Item item, final String fileToRun,
      final int filenameIndex, final Emulator emulator, final List<String> namesOfSupportedFiles) {
    File file;
    final Map<String, Object> environment = new HashMap<String, Object>();

    Game game = null;
    Music music = null;
    boolean isGame;

    // determine if item is game or music
    if (item instanceof Game) {
      game = (Game) item;
      isGame = true;
    } else if (item instanceof Music) {
      music = (Music) item;
      isGame = false;
    } else {
      throw new RuntimeException("Item must be instance of Game or instance of Music");
    }
    environment.put("isGame", isGame);

    // add objects to script engine (first letter is upper case)
    environment.put("Database", Databases.getCurrent());
    environment.put("Emulator", emulator);

    environment.put("Item", item);
    environment.put("Game", game);
    environment.put("Music", music);

    // add variables to script engine (first letter is lower case)
    environment.put("osName", System.getProperty("os.name"));
    environment.put("isOsWindows", Const.OS_IS_WINDOWS);
    environment.put("lineSeparator", Const.LINE_SEPARATOR);
    environment.put("separator", File.separator);

    // The full path to the database folder.
    // For example: /data/workspace/GameBase/C64lite
    environment.put("dbPath", Databases.getCurrent().getPath());
    environment.put("emulatorPath",
        new File(Databases.getCurrent().getPath(), Const.SCRIPT_DIRNAME).getAbsolutePath());

    String emulatorConfigFile;
    if ((emulator.getConfigurationFilename() == null)
        || emulator.getConfigurationFilename().isEmpty()) {
      // no configuration file specified => use lower case name of emulator with
      // ".cfg" appended in the script directory
      emulatorConfigFile = new File(
          new File(Databases.getCurrent().getPath(), Const.SCRIPT_DIRNAME), (emulator.getName()
              .toLowerCase() + ".cfg")).getAbsolutePath();
    } else {
      emulatorConfigFile = new File(emulator.getConfigurationFilename()).isAbsolute() ? emulator
          .getConfigurationFilename() : (new File(new File(Databases.getCurrent().getPath(),
          Const.SCRIPT_DIRNAME), emulator.getConfigurationFilename()).getAbsolutePath());
    }
    // ensure that file exists
    if (!new File(emulatorConfigFile).exists()) {
      try {
        new File(emulatorConfigFile).createNewFile();
      } catch (final IOException ioe) {
        ioe.printStackTrace();
      }
    }
    environment.put("emulatorConfigFile", emulatorConfigFile);

    // The full path to the work folder (where jGameBase extracts and runs game
    // files).
    // For example: /tmp/jgamebase
    environment.put("workPath", TempDir.getPath().getAbsolutePath());

    // game environment variables
    environment.put("itemName", isGame ? game.getName() : music.getName());
    environment.put("itemComment", isGame ? game.getComment() : "");
    environment.put("itemVersionComment", isGame ? game.getVersionComment() : "");
    environment.put("itemControl", isGame ? game.getControlForDisplay() : "");
    environment.put("itemPalNtsc", isGame ? game.getPalNtscForDisplay() : "");
    environment.put("itemPlayersMin", isGame ? game.getPlayersMin() : 1);
    environment.put("itemPlayersMax", isGame ? game.getPlayersMax() : 1);
    environment.put("isRunsOnPal", isGame ? game.isRunsOnPal() : true);
    environment.put("isRunsOnNtsc", isGame ? game.isRunsOnNtsc() : true);
    environment.put("isRunsOnTrueDrive", isGame ? game.getNeedsTruedriveEmu() : false);

    // hardware joystick
    environment.put("useHardwareJoystick", Preferences.is(Preferences.HARDWARE_JOYSTICK));

    // image data

    // The full path and filename of the game being run.
    // For example: c:\games\s\sanxion.d64
    environment.put("fileToRun", fileToRun);
    // This will be set to the zero-based index of the game to run within the
    // file.
    // For example: 0, 1, 2, 3, etc..
    environment.put("imageIndex", filenameIndex);

    // For known image types, this will be set to the image name that
    // corresponds
    // to the selected image index. For all other file types this variable will
    // be empty.
    // For example: SANXION++, MONTY ON THE RUN etc..

    // default values
    environment.put("imageName", "");
    environment.put("imageNameNative", "");
    environment.put("isBootable", false);

    if (Plugins.existsDiskInfoForExtension(FileTools.getExtension(fileToRun))) {
      try {
        final DiskInfo diskinfo = Plugins
            .getDiskInfoForExtension(FileTools.getExtension(fileToRun));
        diskinfo.load(fileToRun);
        final String imageName = (diskinfo.getFilenameAt(filenameIndex) == null) ? "" : diskinfo
            .getFilenameAt(filenameIndex);
        environment.put("imageName", (imageName != null) ? imageName : "");
        final String imageNameNative = (diskinfo.getNativeFilenameAt(filenameIndex) == null) ? ""
            : diskinfo.getNativeFilenameAt(filenameIndex);
        environment.put("imageNameNative", (imageNameNative != null) ? imageNameNative : "");
        environment.put("isBootable", diskinfo.isBootable());
      } catch (final Exception e) {
        log.warn("Could not open the Diskimage '" + fileToRun + "'.");
      }
    }

    // game files

    // If the game has more than one game file (e.g. is a multi-disk game) then
    // these
    // variables will point to these extra game files.
    // The number of extra game files is determined by the following rules:
    // The last character of the filename will be incremented.
    // For example, if the initial game is "REALMS0.D64" then "REALMS1.D64",
    // "REALMS2.D64" etc.
    // For example, if the initial game is "SUMGAMES1A.D64" then
    // "SUMGAMES1B.D64", "SUMGAMES1C.D64" etc.
    final List<String> itemfilepaths = new ArrayList<String>();
    final List<String> itempaths = new ArrayList<String>();
    final List<String> itemfiles = new ArrayList<String>();
    final List<String> itemfileexts = new ArrayList<String>();
    final List<String> itemfilesnoext = new ArrayList<String>();

    file = new File(fileToRun).getAbsoluteFile();
    itemfilepaths.add(file.getAbsolutePath());
    itempaths.add(file.getParent());
    itemfiles.add(file.getName());
    itemfileexts.add(FileTools.getExtension(file.getName()));
    itemfilesnoext.add(FileTools.removeExtension(file.getName()));

    for (final String nameOfSupportedFile : namesOfSupportedFiles) {
      if (!nameOfSupportedFile.equals(fileToRun)) {
        file = new File(nameOfSupportedFile).getAbsoluteFile();
        itemfilepaths.add(file.getAbsolutePath());
        itempaths.add(file.getParent());
        itemfiles.add(file.getName());
        itemfileexts.add(FileTools.getExtension(file.getName()));
        itemfilesnoext.add(FileTools.removeExtension(file.getName()));
      }
    }

    environment.put("itemPathAndFile", itemfilepaths.get(0));
    // The path (without the filename) of the game being run.
    // For example: c:\games\s
    environment.put("itemPath", itempaths.get(0));
    // The filename (without the path) of the game being run.
    // For example: sanxion.d64
    environment.put("itemFile", itemfiles.get(0));
    environment.put("itemFileExt", itemfileexts.get(0));
    // The type (extension) of the game being run.
    // For example: d64
    environment.put("itemType", itemfileexts.get(0));
    // The filename (without the path or filename extension) of the game being
    // run.
    // For example: sanxion
    environment.put("itemFileNoExt", itemfilesnoext.get(0));

    environment.put("itemPathsAndFiles", itemfilepaths.toArray());
    environment.put("itemPaths", itempaths.toArray());
    environment.put("itemFiles", itemfiles.toArray());
    environment.put("itemFilesExt", itemfileexts.toArray());
    environment.put("itemFilesNoExt", itemfilesnoext.toArray());

    // "Key_value" pairs
    if (isGame) {
      final Scanner scanner = new Scanner(game.getKeyValuePairs());
      scanner.useDelimiter(Const.LINE_SEPARATOR_PATTERN);
      while (scanner.hasNext()) {
        final String keyAndValue = scanner.next();
        environment.put("keyAndValue_" + StringTools.beforeEqualsign(keyAndValue).toLowerCase(),
            StringTools.afterEqualsign(keyAndValue));
      }
    }

    return environment;
  }
}
