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

import static jgamebase.Const.ADDEXTRAS_BYID_DIRNAME;
import static jgamebase.Const.ADDEXTRAS_DIRNAME;
import static jgamebase.Const.log;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

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
import jgamebase.model.Preferences;
import jgamebase.tools.FileTools;
import jgamebase.tools.ListerTools;
import jgamebase.tools.StringTools;
import jgamebase.tools.TempDir;

public class Game implements Item {

  private static final long serialVersionUID = -6900055147697075607L;

  public static final int CCODE_NOTSET = 0;

  public static final int CCODE_GAMEMUSIC = 1;

  public static final int CCODE_GAME = 2;

  public static final int CCODE_MUSIC = 3;

  public static final int CCODE_NOTHING = 4;

  // Fields
  private int id;

  private String name = "";

  private Year year = new Year(Year.NEUTRAL_ID);

  private String comment = "";

  private String filename = "";

  private String fileToRun = "";

  private int filenameIndex;

  private String screenshotFilename = "";

  private Musician musician = new Musician(Musician.NEUTRAL_ID);

  private Genre genre = new Genre(Genre.NEUTRAL_ID);

  private Publisher publisher = new Publisher(Publisher.NEUTRAL_ID);

  private Difficulty difficulty = new Difficulty(Difficulty.NEUTRAL_ID);

  private Cracker cracker = new Cracker(Cracker.NEUTRAL_ID);

  private String musicFilename = "";

  private String dateLastPlayed = "";

  private int timesPlayed;

  private String highscore = "";

  private boolean gameFileExists;

  private boolean musicFileExists;

  private boolean isFavourite;

  private Programmer programmer = new Programmer(Programmer.NEUTRAL_ID);

  private Language language = new Language(Language.NEUTRAL_ID);

  private boolean hasExtras;

  private boolean isClassic;

  private int rating = Const.NEUTRAL_ID_RATING;

  private int _hasLoadingScreen;

  private int _hasHighscoreSaver;

  private int _hasIncludedDocs;

  private int PalNtsc = Const.NEUTRAL_ID_PALNTSC;

  private int _needsTruedriveEmu;

  private int length;

  private int trainer;

  private int playersMin = 1; // 0 players doesn't make sense ;-)

  private int playersMax = 1;

  private boolean isSimultaneouslyPlayable;

  private String versionComment = "";

  private boolean isAdult;

  private String note = "";

  private int prequelId;

  private int sequelId;

  private int relatedId;

  private int control = Const.NEUTRAL_ID_CONTROL;

  private String crc = "";

  private int filesize;

  private int version;

  private String keyValuePairs = "";

  private int lengthType = Const.NEUTRAL_ID_LENGTHTYPE;

  private Artist artist = new Artist(Artist.NEUTRAL_ID);
  private Developer developer = new Developer(Developer.NEUTRAL_ID);
  private License license = new License(License.NEUTRAL_ID);
  private Rarity rarity = new Rarity(Rarity.NEUTRAL_ID);

  private String webLinkName;
  private String webLinkUrl;
  private String vWebLinkName;
  private String vWebLinkUrl;
  private int hasTitleScreen;
  private int isPlayable;
  private int isOriginal;
  private int cloneOf;
  private int reviewRating;

  private List<Extra> extras = new ArrayList<Extra>();

  // Constructors
  /** default constructor */
  public Game() {
  }

  public Game(final String name, final String filename, final String fileToRun,
      final int filenameIndex) {
    this.name = name;
    setFilename(filename);
    setFileToRun(fileToRun);
    this.filenameIndex = filenameIndex;

    year = Db.getYearById(Year.NEUTRAL_ID);
    musician = Db.getMusicianById(Musician.NEUTRAL_ID);
    genre = Db.getGenreById(Genre.NEUTRAL_ID);
    publisher = Db.getPublisherById(Publisher.NEUTRAL_ID);
    difficulty = Db.getDifficultyById(Difficulty.NEUTRAL_ID);
    cracker = Db.getCrackerById(Cracker.NEUTRAL_ID);
    programmer = Db.getProgrammerById(Programmer.NEUTRAL_ID);
    language = Db.getLanguageById(Language.NEUTRAL_ID);

    gameFileExists = true;
  }

  /** constructor with id */
  public Game(final int id) {
    this.id = id;
  }

  // Property accessors
  /**
	 * 
	 */
  @Override
  public int getId() {
    return id;
  }

  @Override
  public String getStringId() {
    return "G" + id;
  }

  public void setId(final int id) {
    this.id = id;
  }

  /**
	 * 
	 */
  @Override
  public String getName() {
    return name == null ? "" : name;
  }

  @Override
  public void setName(final String name) {
    this.name = name;
  }

  /**
	 * 
	 */
  public Year getYear() {
    if (year == null) {
      year = new Year(Year.NEUTRAL_ID);
    }
    return year;
  }

  public void setYear(final Year year) {
    this.year = year;
  }

  /**
	 * 
	 */
  public String getComment() {
    return comment == null ? "" : comment;
  }

  public void setComment(final String comment) {
    this.comment = comment;
  }

  /**
	 * 
	 */
  @Override
  public String getFilename() {
    return Paths.backslashToSlash(filename);
  }

  public void setFilename(final String filename) {
    this.filename = Paths.slashToBackslash(filename);
  }

  /**
	 * 
	 */
  public String getFileToRun() {
    return Paths.backslashToSlash(fileToRun);
  }

  public void setFileToRun(final String fileToRun) {
    this.fileToRun = Paths.slashToBackslash(fileToRun);
  }

  /**
	 * 
	 */
  public int getFilenameIndex() {
    return filenameIndex;
  }

  public void setFilenameIndex(final int filenameIndex) {
    this.filenameIndex = filenameIndex;
  }

  /**
	 * 
	 */
  public String getScreenshotFilename() {
    return Paths.backslashToSlash(screenshotFilename);
  }

  public void setScreenshotFilename(final String screenshotFilename) {
    this.screenshotFilename = Paths.slashToBackslash(screenshotFilename);
  }

  /**
   * Count number of existing screenshots
   * 
   * @return
   */
  public int getScreenshotCount() {
    if ((getScreenshotFilename() == null) || getScreenshotFilename().isEmpty()) {
      return 0;
    }

    int number = 0;

    while (Paths.getScreenshotPath().exists(
        new File(FileTools.insertBeforeFileextension(getScreenshotFilename(), number == 0 ? ""
            : "_" + number)))) {
      number++;
    }

    return number;
  }

  public List<String> getScreenshotFilenames() {
    final List<String> filenames = new ArrayList<String>();
    final int count = getScreenshotCount();

    // if no screenshots return empty list
    if (count == 0) {
      return filenames;
    }

    for (int i = 0; i < count; i++) {
      final String filename = FileTools.insertBeforeFileextension(
          Paths.getScreenshotPath().find(new File(getScreenshotFilename())).getPath(), i == 0 ? ""
              : "_" + i);
      filenames.add(filename);
    }

    return filenames;
  }

  /**
	 * 
	 */
  @Override
  public Musician getMusician() {
    if (musician == null) {
      musician = new Musician(Musician.NEUTRAL_ID);
    }
    return musician;
  }

  public void setMusician(final Musician musician) {
    this.musician = musician;
  }

  /**
	 * 
	 */
  public Genre getGenre() {
    if (genre == null) {
      genre = new Genre(Genre.NEUTRAL_ID);
    }
    return genre;
  }

  public void setGenre(final Genre genre) {
    this.genre = genre;
  }

  public String getGenreForDisplay() {
    // genre is uncategorized or has no parent
    if ((getGenre().getId() == Genre.NEUTRAL_ID) || (getGenre().getParentGenre() == null)
        || (getGenre().getParentGenre().getName().isEmpty())) {
      return getGenre().getName();
    }

    return getGenre().getParentGenre().getName() + " - " + getGenre().getName();
  }

  /**
	 * 
	 */
  public Publisher getPublisher() {
    if (publisher == null) {
      publisher = new Publisher(Publisher.NEUTRAL_ID);
    }
    return publisher;
  }

  public void setPublisher(final Publisher publisher) {
    this.publisher = publisher;
  }

  /**
	 * 
	 */
  public Difficulty getDifficulty() {
    if (difficulty == null) {
      difficulty = new Difficulty(Difficulty.NEUTRAL_ID);
    }
    return difficulty;
  }

  public void setDifficulty(final Difficulty difficulty) {
    this.difficulty = difficulty;
  }

  /**
	 * 
	 */
  public Cracker getCracker() {
    if (cracker == null) {
      cracker = new Cracker(Cracker.NEUTRAL_ID);
    }
    return cracker;
  }

  public void setCracker(final Cracker cracker) {
    this.cracker = cracker;
  }

  /**
	 * 
	 */
  public String getMusicFilename() {
    return Paths.backslashToSlash(musicFilename);
  }

  public void setMusicFilename(final String musicFilename) {
    this.musicFilename = Paths.slashToBackslash(musicFilename);
  }

  /**
	 * 
	 */
  public String getDateLastPlayed() {
    return dateLastPlayed == null ? "" : dateLastPlayed;
  }

  public void setDateLastPlayed(final String dateLastPlayed) {
    this.dateLastPlayed = dateLastPlayed;
  }

  public String getDateLastPlayedForDisplay() {
    return getDateLastPlayed().isEmpty() ? "Never" : getDateLastPlayed();
  }

  /**
	 * 
	 */
  public int getTimesPlayed() {
    return timesPlayed;
  }

  public void setTimesPlayed(final int timesPlayed) {
    this.timesPlayed = timesPlayed;
  }

  public String getTimesPlayedForDisplay() {
    return getTimesPlayed() + "";
  }

  /**
	 * 
	 */
  @SuppressWarnings("unused")
  private int get_ccode() {
    return 0;
  }

  @SuppressWarnings("unused")
  private void set_ccode(final int _ccode) {
  }

  private int getCcode() {
    final boolean gamePathSpecified = !(getFilename().isEmpty());
    final boolean musicPathSpecified = !(getMusicFilename().isEmpty());

    if (gamePathSpecified && musicPathSpecified) {
      return CCODE_GAMEMUSIC;
    } else if (gamePathSpecified && !musicPathSpecified) {
      return CCODE_GAME;
    } else if (!gamePathSpecified && musicPathSpecified) {
      return CCODE_MUSIC;
    } else if (!gamePathSpecified && !musicPathSpecified) {
      return CCODE_NOTHING;
    }
    return CCODE_NOTSET;
  }

  /**
	 * 
	 */
  public String getHighscore() {
    return highscore == null ? "" : highscore;
  }

  public void setHighscore(final String highscore) {
    this.highscore = highscore;
  }

  public String getHighscoreForDisplay() {
    return getHighscore().isEmpty() ? "Not Entered" : getHighscore();
  }

  /**
	 * 
	 */
  public boolean getGameFileExists() {
    return gameFileExists;
  }

  public void setGameFileExists(final boolean fileExists) {
    gameFileExists = fileExists;
  }

  /**
	 * 
	 */
  public boolean getMusicFileExists() {
    return musicFileExists;
  }

  public void setMusicFileExists(final boolean musicFileExists) {
    this.musicFileExists = musicFileExists;
  }

  /**
	 * 
	 */
  @Override
  public boolean getIsFavourite() {
    return isFavourite;
  }

  @Override
  public void setIsFavourite(final boolean isFavourite) {
    this.isFavourite = isFavourite;
  }

  /**
	 * 
	 */
  public Programmer getProgrammer() {
    if (programmer == null) {
      programmer = new Programmer(Programmer.NEUTRAL_ID);
    }
    return programmer;
  }

  public void setProgrammer(final Programmer programmer) {
    this.programmer = programmer;
  }

  /**
	 * 
	 */
  public Language getLanguage() {
    if (language == null) {
      language = new Language(Language.NEUTRAL_ID);
    }
    return language;
  }

  public void setLanguage(final Language language) {
    this.language = language;
  }

  /**
	 * 
	 */
  public boolean getHasExtras() {
    hasExtras = ((getExtras() != null) && (getExtras().size() > 0));
    return hasExtras;
  }

  public void setHasExtras(final boolean hasExtras) {
    this.hasExtras = hasExtras;
  }

  /**
	 * 
	 */
  public boolean getIsClassic() {
    return isClassic;
  }

  public void setIsClassic(final boolean isClassic) {
    this.isClassic = isClassic;
  }

  /**
	 * 
	 */
  public int getRating() {
    if (getIsClassic()) {
      return 6;
    }
    return rating;
  }

  public void setRating(final int rating) {
    this.rating = rating;
  }

  public String getRatingForDisplay() {
    String rating = "";

    if ((getRating() >= 0) && (getRating() < Const.FORDISPLAY_RATING.length)) {
      rating = Const.FORDISPLAY_RATING[getRating()];
    }

    if (getIsClassic()) {
      rating = "Classic!";
    }

    return rating;
  }

  /**
	 * 
	 */
  private int get_hasLoadingScreen() {
    return _hasLoadingScreen;
  }

  private void set_hasLoadingScreen(final int _hasLoadingScreen) {
    this._hasLoadingScreen = _hasLoadingScreen;
  }

  public boolean getHasLoadingScreen() {
    return intToBoolean(get_hasLoadingScreen());
  }

  public void setHasLoadingScreen(final boolean hasLoadingScreen) {
    set_hasLoadingScreen(booleanToInt(hasLoadingScreen));
  }

  public String getHasLoadingScreenForDisplay() {
    return getHasLoadingScreen() ? "Yes" : "No";
  }

  /**
	 * 
	 */
  private int get_hasHighscoreSaver() {
    return _hasHighscoreSaver;
  }

  private void set_hasHighscoreSaver(final int _hasHighscoreSaver) {
    this._hasHighscoreSaver = _hasHighscoreSaver;
  }

  public boolean getHasHighscoreSaver() {
    return intToBoolean(get_hasHighscoreSaver());
  }

  public void setHasHighscoreSaver(final boolean hasHighscoreSaver) {
    set_hasHighscoreSaver(booleanToInt(hasHighscoreSaver));
  }

  public String getHasHighscoreSaverForDisplay() {
    return getHasHighscoreSaver() ? "Yes" : "No";
  }

  /**
	 * 
	 */
  private int get_hasIncludedDocs() {
    return _hasIncludedDocs;
  }

  private void set_hasIncludedDocs(final int _hasIncludedDocs) {
    this._hasIncludedDocs = _hasIncludedDocs;
  }

  public boolean getHasIncludedDocs() {
    return intToBoolean(get_hasIncludedDocs());
  }

  public void setHasIncludedDocs(final boolean hasIncludedDocs) {
    set_hasIncludedDocs(booleanToInt(hasIncludedDocs));
  }

  public String getHasIncludedDocsForDisplay() {
    return getHasIncludedDocs() ? "Yes" : "No";
  }

  /**
	 * 
	 */
  public int getPalNtsc() {
    return PalNtsc;
  }

  public void setPalNtsc(final int PalNtsc) {
    this.PalNtsc = PalNtsc;
  }

  public String getPalNtscForDisplay() {
    String palNtsc = "";

    if ((getPalNtsc() >= 0) && (getPalNtsc() < Const.FORDISPLAY_PALNTSC.length)) {
      palNtsc = Const.FORDISPLAY_PALNTSC[getPalNtsc()];
    }

    return palNtsc;
  }

  public boolean isRunsOnPal() {
    return (getPalNtsc() != 2);
  }

  public boolean isRunsOnNtsc() {
    return ((getPalNtsc() == 1) || (getPalNtsc() == 2));
  }

  /**
	 * 
	 */
  private int get_needsTruedriveEmu() {
    return _needsTruedriveEmu;
  }

  public void set_needsTruedriveEmu(final int _needsTruedriveEmu) {
    this._needsTruedriveEmu = _needsTruedriveEmu;
  }

  public boolean getNeedsTruedriveEmu() {
    return intToBoolean(get_needsTruedriveEmu());
  }

  public void setNeedsTruedriveEmu(final boolean needsTruedriveEmu) {
    set_needsTruedriveEmu(booleanToInt(needsTruedriveEmu));
  }

  public String getNeedsTruedriveEmuForDisplay() {
    return getNeedsTruedriveEmu() ? "Yes" : "No";
  }

  /**
	 * 
	 */
  public int getLength() {
    return length;
  }

  public void setLength(final int length) {
    this.length = length;
  }

  public String getLengthForDisplay() {
    String length = Math.abs(getLength()) + " ";

    if ((getLengthType() >= 0) && (getLengthType() < Const.FORDISPLAY_LENGTHTYPE.length)) {
      length += Const.FORDISPLAY_LENGTHTYPE[getLengthType()];
    }

    return length;
  }

  /**
	 * 
	 */
  public int getTrainer() {
    return trainer;
  }

  public void setTrainer(final int trainer) {
    this.trainer = trainer;
  }

  public String getTrainerForDisplay() {
    if (getTrainer() == -1) {
      return "(Unknown)";
    }
    return getTrainer() + "";
  }

  /**
	 * 
	 */
  public int getPlayersMin() {
    return playersMin;
  }

  public void setPlayersMin(final int playersMin) {
    this.playersMin = playersMin;
  }

  /**
	 * 
	 */
  public int getPlayersMax() {
    return playersMax;
  }

  public void setPlayersMax(final int playersMax) {
    this.playersMax = playersMax;
  }

  public String getPlayersForDisplay() {
    String players = "";
    if (getPlayersMin() == getPlayersMax()) {
      players = getPlayersMin() + "P Only";
    } else {
      players = getPlayersMin() + " - " + getPlayersMax();
    }
    if (getIsSimultaneouslyPlayable()) {
      players += " (Simultaneous)";
    }
    return players;
  }

  /**
	 * 
	 */
  public boolean getIsSimultaneouslyPlayable() {
    return isSimultaneouslyPlayable;
  }

  public void setIsSimultaneouslyPlayable(final boolean isSimultaneouslyPlayable) {
    this.isSimultaneouslyPlayable = isSimultaneouslyPlayable;
  }

  /**
	 * 
	 */
  public String getVersionComment() {
    return versionComment == null ? "" : versionComment;
  }

  public void setVersionComment(final String versionComment) {
    this.versionComment = versionComment;
  }

  /**
	 * 
	 */
  @Override
  public boolean getIsAdult() {
    return isAdult;
  }

  public void setIsAdult(final boolean isAdult) {
    this.isAdult = isAdult;
  }

  /**
	 * 
	 */
  public String getNote() {
    return note == null ? "" : note;
  }

  public void setNote(final String note) {
    this.note = note;
  }

  /**
	 * 
	 */
  public int getPrequelId() {
    return prequelId;
  }

  public boolean hasPrequel() {
    return (getPrequelId() > 0);
  }

  public void setPrequelId(final int prequel) {
    prequelId = prequel;
  }

  /**
	 * 
	 */
  public int getSequelId() {
    return sequelId;
  }

  public boolean hasSequel() {
    return (getSequelId() > 0);
  }

  public void setSequelId(final int sequel) {
    sequelId = sequel;
  }

  /**
	 * 
	 */
  public int getRelatedId() {
    return relatedId;
  }

  public boolean hasRelated() {
    return (getRelatedId() > 0);
  }

  public void setRelatedId(final int related) {
    relatedId = related;
  }

  /**
	 * 
	 */
  public int getControl() {
    return control;
  }

  public void setControl(final int control) {
    this.control = control;
  }

  public String getControlForDisplay() {
    String control = "";

    if ((getControl() >= 0) && (getControl() < Const.FORDISPLAY_CONTROL.length)) {
      control = Const.FORDISPLAY_CONTROL[getControl()];
    }

    return control;
  }

  /**
	 * 
	 */
  public String getCrc() {
    return crc == null ? "" : crc;
  }

  public void setCrc(final String crc) {
    this.crc = crc;
  }

  /**
	 * 
	 */
  public int getFilesize() {
    return filesize;
  }

  public void setFilesize(final int filesize) {
    this.filesize = filesize;
  }

  /**
	 * 
	 */
  public int getVersion() {
    return version;
  }

  public void setVersion(final int version) {
    this.version = version;
  }

  /**
	 * 
	 */
  public String getKeyValuePairs() {
    return keyValuePairs == null ? "" : keyValuePairs;
  }

  public void setKeyValuePairs(final String gemus) {
    keyValuePairs = gemus;
  }

  /**
	 * 
	 */
  public int getLengthType() {
    return lengthType;
  }

  public void setLengthType(final int lengthType) {
    this.lengthType = lengthType;
  }

  public List<Extra> getExtras() {
    return extras;
  }

  public void setExtras(final List<Extra> extras) {

    // remove all null values
    extras.removeAll(Collections.singleton(null));

    // set game id
    for (final Extra extra : extras) {
      extra.setGameId(id);
    }

    // sort extras by display order
    Collections.sort(extras, new Comparator<Extra>() {
      @Override
      public int compare(final Extra e1, final Extra e2) {
        return (e1.getDisplayOrder() - e2.getDisplayOrder());
      }
    });

    this.extras = extras;
  }

  @Override
  public Icon getIcon() {
    switch (getCcode()) {
      case 1:
        return Const.ICON_LIST_GAMEANDSID;
      case 2:
        return Const.ICON_LIST_JUSTGAME;
      case 3:
        return Const.ICON_LIST_JUSTSID;
      default:
        return Const.ICON_LIST_NONE;
    }
  }

  /**
   * 
   */
  public Artist getArtist() {
    if (artist == null) {
      artist = new Artist(Artist.NEUTRAL_ID);
    }
    return artist;
  }

  public void setArtist(final Artist artist) {
    this.artist = artist;
  }

  public Developer getDeveloper() {
    if (developer == null) {
      developer = new Developer(Developer.NEUTRAL_ID);
    }
    return developer;
  }

  public void setDeveloper(final Developer developer) {
    this.developer = developer;
  }

  public License getLicense() {
    if (license == null) {
      license = new License(License.NEUTRAL_ID);
    }
    return license;
  }

  public void setLicense(final License license) {
    this.license = license;
  }

  public Rarity getRarity() {
    if (rarity == null) {
      rarity = new Rarity(Rarity.NEUTRAL_ID);
    }
    return rarity;
  }

  public void setRarity(final Rarity rarity) {
    this.rarity = rarity;
  }

  public String getWebLinkName() {
    return webLinkName == null ? "" : webLinkName;
  }

  public void setWebLinkName(final String webLinkName) {
    this.webLinkName = webLinkName;
  }

  public String getWebLinkUrl() {
    return webLinkUrl == null ? "" : webLinkUrl;
  }

  public void setWebLinkUrl(final String webLinkUrl) {
    this.webLinkUrl = webLinkUrl;
  }

  public String getvWebLinkName() {
    return vWebLinkName == null ? "" : vWebLinkName;
  }

  public void setvWebLinkName(final String vWebLinkName) {
    this.vWebLinkName = vWebLinkName;
  }

  public String getvWebLinkUrl() {
    return vWebLinkUrl == null ? "" : vWebLinkUrl;
  }

  public void setvWebLinkUrl(final String vWebLinkUrl) {
    this.vWebLinkUrl = vWebLinkUrl;
  }

  public int getHasTitleScreen() {
    return hasTitleScreen;
  }

  public void setHasTitleScreen(final int hasTitleScreen) {
    this.hasTitleScreen = hasTitleScreen;
  }

  public int getIsPlayable() {
    return isPlayable;
  }

  public void setIsPlayable(final int isPlayable) {
    this.isPlayable = isPlayable;
  }

  public int getIsOriginal() {
    return isOriginal;
  }

  public void setIsOriginal(final int isOriginal) {
    this.isOriginal = isOriginal;
  }

  public int getCloneOf() {
    return cloneOf;
  }

  public void setCloneOf(final int cloneOf) {
    this.cloneOf = cloneOf;
  }

  public int getReviewRating() {
    return reviewRating;
  }

  public void setReviewRating(final int reviewRating) {
    this.reviewRating = reviewRating;
  }

  @Override
  public void play() {
    Game.play(this, null, false);
  }

  public void play(final boolean isAlreadyExtracted) {
    Game.play(this, null, isAlreadyExtracted);
  }

  public void play(final Emulator emulator) {
    Game.play(this, emulator, false);
  }

  private static void play(final Game game, Emulator emulator, final boolean isAlreadyExtracted) {
    String filename = "";
    String fileToRun;
    List<String> namesOfSupportedFiles;
    final int filenameIndex = game.getFilenameIndex();

    // update infos
    Preferences.inc(Preferences.ABOUT_GAMES_PLAYED);
    game.setDateLastPlayed(new SimpleDateFormat("dd.MM.yyyy").format(new Date()));
    game.setTimesPlayed(game.getTimesPlayed() + 1);
    Db.saveOrUpdate(game);

    // 1. [Filename]
    // The actual subfolder\filename of the game file linked to the game
    // (within your game paths).
    // 2. [FilenameIndex]
    // The image index inside the file of the PRG to run in the specified
    // emulator.
    // 3. [FileToRun]
    // If the game file set in the [Filename] field is a compressed archive
    // and contains more than one supported native emulator file, this will
    // be set to the filename (without path) to run within the archive.

    if (isAlreadyExtracted) {
      fileToRun = game.getFileToRun();
      namesOfSupportedFiles = new ArrayList<String>();
      namesOfSupportedFiles.add(fileToRun);

    } else {
      filename = (Paths.getGamePath().findAndWarn(new File(game.getFilename())) != null) ? Paths
          .getGamePath().find(new File(game.getFilename())).getPath() : "";
      fileToRun = "";

      namesOfSupportedFiles = FileTools.copyOrExtractToCleanTempDir(filename);

      // if a file to run was supplied, try to find it
      if (!game.getFileToRun().isEmpty()) {
        // check all supported files for match
        for (final String possibleFileToRun : namesOfSupportedFiles) {
          if (possibleFileToRun.equalsIgnoreCase(game.getFileToRun())) {
            fileToRun = possibleFileToRun;
            break;
          }
        }
      }

      // if no file to run found but supported files were found, default to
      // first as file to be run
      if ((fileToRun.isEmpty()) && (namesOfSupportedFiles.size() > 0)) {
        // get first supported file to run
        fileToRun = namesOfSupportedFiles.get(0);
      }

    }

    log.info("FileToRun: '" + fileToRun + "'.");

    // copy or extract more media (disk images)
    String nextMediumFilename = StringTools.nextMedium(filename);
    File nextMediumFile = new File(nextMediumFilename);

    while (nextMediumFile.exists()) {
      log.info("next medium '" + nextMediumFilename + "'");
      // extract
      namesOfSupportedFiles.addAll(FileTools.copyOrExtractToTempDir(nextMediumFilename));
      nextMediumFilename = StringTools.nextMedium(nextMediumFilename);
      nextMediumFile = new File(nextMediumFilename);
    }

    // emulator supplied?
    if (emulator == null) {
      // find suitable emulator
      emulator = Emulators.findGameEmulatorForExtension(FileTools.getExtension(fileToRun));
    }

    // emulator (supplied or found) exists?
    if (emulator != null) {
      log.info("Using Emulator: '" + emulator.getName() + "'.");

      // "namesOfSupportedFiles" contains all files supported by ALL emulators
      // now only the files for THIS emulator remain
      namesOfSupportedFiles = emulator.getSupportedExtensions().getMatching(namesOfSupportedFiles);

      final File script = new File(
          new File(Databases.getCurrent().getPath(), Const.SCRIPT_DIRNAME), emulator.getCommand());
      // check if emulator command is a script in the script directory
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
          Emulators.executeGame(new String[] { script.getAbsolutePath(), fileToRun }, Emulators
              .setupEnvironment(game, fileToRun, filenameIndex, emulator, namesOfSupportedFiles));
        } else {
          log.info("SCRIPT");
          // invoke script by script engine
          final String includesFilename = Const.INCLUDE_FILENAME + "."
              + FileTools.getExtension(emulator.getCommand());

          String includesAsString = "";
          String includesPathAndFilename = "";

          try {
            // add objects to script engine (first letter is upper case)
            final Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.putAll(Emulators.setupEnvironment(game, fileToRun, filenameIndex, emulator,
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

              final int lineNumber = se.getLineNumber();
              if ((lineNumber >= 0) && (lineNumber < 32767)) {
                log.error("IMPORTANT: When locating the error please bear in mind that an include of "
                    + includedLines + " lines was added before your script:");
                if (lineNumber <= includedLines) {
                  log.error("So the error really occured at line " + lineNumber
                      + " in the include '" + includesPathAndFilename + "' .");
                } else {
                  log.error("So the error really occured at line " + (lineNumber - includedLines)
                      + " in the script '" + script.getAbsolutePath() + "' .");
                }
                log.error("");
              }
            }
          }
        }
      } else { // command is an executable
        log.info("EXECUTABLE");
        // execute
        Emulators.executeGame(new String[] { emulator.getCommand(), fileToRun }, Emulators
            .setupEnvironment(game, fileToRun, filenameIndex, emulator, namesOfSupportedFiles));
      }

    } else {
      Gui.displayErrorDialog("No Game Emulator found for game '"
          + game.getName()
          + "'!\n"
          + (fileToRun.isEmpty() ? "" : "The requested file extension was '"
              + FileTools.getExtension(fileToRun) + "'.\n") + "\n"
          + "Please check your Game Emulator configuration:\n"
          + "The supported extensions must be specified as a semicolon\n"
          + "separated list of file extensions (e.g. 'd64;t64;tap;crt').");
    }
  }

  public void syncAdditionalExtras(final boolean enqueue) {
    Game.syncAdditionalExtras(this, enqueue);
  }

  private static void syncAdditionalExtras(final Game game, final boolean enqueue) {
    // create directories (if they don't exist)

    Paths.createAdditionalExtraDirectories(game.getId(), game.getName());

    // remove old additional extras
    final List<Extra> extras = new ArrayList<Extra>(game.getExtras());
    if (extras != null) {
      extras.removeIf(extra -> (extra == null) || (extra.isAdditional()));
    }

    // search
    final File dir = Paths.getAdditionalExtraPathId(game.getId());
    List<String> filenames;
    int displayOrder = extras.size();

    // search for additional extras in top directory
    filenames = ListerTools.list_Dirs_Files_Paths(dir.toString(), false, true, false);

    for (final String filename : filenames) {
      final String path = new File(new File(new File(ADDEXTRAS_DIRNAME, ADDEXTRAS_BYID_DIRNAME),
          String.format("%05d", game.getId())), filename).toString();

      final Extra extra = createExtra(path, "");

      if (extra != null) {
        extra.setDisplayOrder(displayOrder++);
        extras.add(extra);
      }

    }

    // search for additional extras in sub directories
    final List<String> subdirs = ListerTools.list_Dirs_Files_Paths(dir.toString(), true, false,
        false);
    for (final String subdir : subdirs) {
      filenames = ListerTools.list_Dirs_Files_Paths(new File(dir, subdir).toString(), false, true,
          false);

      for (final String filename : filenames) {
        final String path = new File(new File(new File(new File(ADDEXTRAS_DIRNAME,
            ADDEXTRAS_BYID_DIRNAME), String.format("%05d", game.getId())), subdir), filename)
            .toString();

        final Extra extra = createExtra(path, subdir);

        if (extra != null) {
          extra.setDisplayOrder(displayOrder++);
          extras.add(extra);
        }
      }

    }

    // delete removed extras from database
    for (final Extra extra : game.getExtras()) {
      if (!extras.contains(extra)) {
        // extra no longer present in new extras
        // log.info("deleting Extra " + extra.getName());
        extra.setGameId(0);
        if (enqueue) {
          Extra.enqueueForDeletion(extra);
        } else {
          Db.delete(extra);
        }
      }
    }

    // save game
    game.setExtras(extras);

    if (!enqueue) {
      Db.saveOrUpdate(game);
    }

  }

  private static Extra createExtra(final String path, final String subdir) {
    final Extra extra = new Extra();

    final String name = new File(path).getName();
    final String filename = new File(Paths.getExtraPath().getDefault_rw(), path).getAbsolutePath();

    log.info("creating Extra for file '" + name + "'...");

    if (FileTools.isUrlFromFilename(filename)) {
      // is url

      final URL url = FileTools.getUrlFromFilename(filename);
      log.info("Extra '" + name + "' is URL extra for '" + url + "'");

      final String downloadDir = new File(filename).getAbsolutePath();
      final String downloadFile = new File(url.getFile()).getName();
      final File downloaded = new File(downloadDir, downloadFile);

      // has this URL been downloaded?
      if (downloaded.exists()) {
        // don't add as extra
        log.info("has already been downloaded, will not be added");

        return null;
      }

      // add as url extra
      extra.setName(subdir + (subdir.isEmpty() ? "" : " - ")
          + FileTools.removeExtension(downloadFile));
      extra.setFilename(url.toString());

    } else {
      // normal (no url) extra

      log.info("is normal extra");
      extra.setName(subdir + (subdir.isEmpty() ? "" : " - ") + FileTools.removeExtension(name));
      extra.setFilename(path);
    }

    return extra;
  }

  public void createScreenshots() throws IOException {
    createScreenshots(this);
  }

  private static void createScreenshots(final Game game) throws IOException {
    // get the script directory
    final File dir = new File(Databases.getCurrent().getPath(), Const.SCRIPT_DIRNAME);
    // get all *.png files in the temporary directory
    final List<String> srcFilenames = new FileExtensions("png").getMatching(ListerTools
        .list_Dirs_Files_Paths(dir.getAbsolutePath(), false, true, false));

    if ((game.getScreenshotCount() == 0)
        || (((game.getScreenshotFilename() == null) || game.getScreenshotFilename().isEmpty()) && (!game
            .getName().isEmpty()))) {
      final String work = StringTools.sanitize(game.getName()) + ".png";
      game.setScreenshotFilename(new File(work.substring(0, 1).toUpperCase(), work).getPath());
      Db.saveOrUpdate(game);
    }

    String dstFilename = "";

    for (int i = 0; i < srcFilenames.size(); i++) {
      final String srcFilename = srcFilenames.get(i);
      final int j = game.getScreenshotCount();
      dstFilename = new File(Paths.getScreenshotPath().getDefault_rw(),
          FileTools.insertBeforeFileextension(game.getScreenshotFilename(), j == 0 ? "" : "_" + j))
          .getAbsolutePath();
      final File srcFile = new File(dir, srcFilename);
      log.info("Screenshot:" + srcFile.getAbsolutePath() + " => " + dstFilename);
      FileTools.copyFile(srcFile, new File(dstFilename));
      srcFile.delete();
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
    if (other instanceof Game) {
      return createId().equals(((Game) other).createId());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return createId().hashCode();
  }

  static private int booleanToInt(final boolean b) {
    if (b) {
      return 1;
    }
    return 0;
  }

  static private boolean intToBoolean(final int i) {
    return (i != 0);
  }

}
