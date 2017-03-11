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

import static jgamebase.Const.ADDEXTRAS_DIRNAME;
import static jgamebase.Const.log;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.Icon;

import jgamebase.Const;
import jgamebase.db.Db;
import jgamebase.model.Emulators;
import jgamebase.model.Paths;
import jgamebase.model.Plugins;
import jgamebase.plugins.Extractor;
import jgamebase.tools.DownloadTools;
import jgamebase.tools.FileTools;
import jgamebase.tools.SystemTools;

public class Extra {

  /**
   * Category
   */
  public enum Category {
    GameImage(Const.ICON_CATEGORY_GAMEIMAGE, new String[] { "animations", "animation",
        "screenshot", "screenshot" }, Const.ADDEXTRAS_ANIMATION_DIRNAME), AdditionalImage(
        Const.ICON_CATEGORY_ADDITIONALIMAGE, new String[] {}, Const.ADDEXTRAS_ANIMATION_DIRNAME), AdditionalImage_Advertisement(
        Const.ICON_CATEGORY_ADDITIONALIMAGE_ADVERTISEMENT, new String[] { "adverts", "advert" },
        Const.ADDEXTRAS_COVER_DIRNAME), AdditionalImage_Bookcover(
        Const.ICON_CATEGORY_ADDITIONALIMAGE_BOOKCOVER, new String[] { "books", "book", "magcovers",
            "magcover" }, Const.ADDEXTRAS_COVER_DIRNAME), Documentation(
        Const.ICON_CATEGORY_DOCUMENTATION, new String[] { "documentation", "docs", "doc",
            "instructions", "instruction" }, Const.ADDEXTRAS_MANUAL_DIRNAME), Documentation_Manual(
        Const.ICON_CATEGORY_DOCUMENTATION_MANUAL, new String[] { "manuals", "manual" },
        Const.ADDEXTRAS_MANUAL_DIRNAME), Solution(Const.ICON_CATEGORY_SOLUTION, new String[] {
        "hints, tips, cheats & walkthroughs", "cheats", "cheat", "solutions", "solution" },
        Const.ADDEXTRAS_SOLUTION_DIRNAME), Solution_Map(Const.ICON_CATEGORY_SOLUTION_MAP,
        new String[] { "maps", "map" }, Const.ADDEXTRAS_SOLUTION_DIRNAME), Solution_Tip(
        Const.ICON_CATEGORY_SOLUTION_TIP, new String[] { "hints & tips", "hint", "tips", "tip",
            "tipps", "tipp" }, Const.ADDEXTRAS_SOLUTION_DIRNAME), Solution_walkthrough(
        Const.ICON_CATEGORY_SOLUTION_WALKTHROUGH, new String[] { "walkthrough" },
        Const.ADDEXTRAS_SOLUTION_DIRNAME), Review(Const.ICON_CATEGORY_REVIEW, new String[] {
        "gazette!", "magazine", "reviews", "review" }, Const.ADDEXTRAS_REVIEW_DIRNAME), Movie(
        Const.ICON_CATEGORY_MOVIE, new String[] { "longplays", "longplay", "movies", "movie" },
        Const.ADDEXTRAS_LONGPLAY_DIRNAME), Media(Const.ICON_CATEGORY_MEDIA, new String[] { "media",
        "sps", "listings", "listing" }, Const.ADDEXTRAS_MEDIA_DIRNAME), Media_Cartridge(
        Const.ICON_CATEGORY_MEDIA_CARTRIDGE, new String[] { "cartridges", "cartridge", "carts",
            "cart", "crt" }, Const.ADDEXTRAS_MEDIA_DIRNAME), Media_Tape(
        Const.ICON_CATEGORY_MEDIA_TAPE, new String[] { "covertapes", "covertape", "tapes", "tape",
            "tap" }, Const.ADDEXTRAS_MEDIA_DIRNAME), Media_Disk(Const.ICON_CATEGORY_MEDIA_DISK,
        new String[] { "coverdisks", "coverdisk", "disks", "disk" }, Const.ADDEXTRAS_MEDIA_DIRNAME),
    // cover after covertape, coverdisk
    AdditionalImage_Cover(Const.ICON_CATEGORY_ADDITIONALIMAGE_COVER, new String[] { "boxscans",
        "boxscan", "covers", "cover" }, Const.ADDEXTRAS_COVER_DIRNAME), Media_Harddisk(
        Const.ICON_CATEGORY_MEDIA_HARDDISK, new String[] { "harddisks", "harddisk", "whdload" },
        Const.ADDEXTRAS_MEDIA_DIRNAME), Misc(Const.ICON_CATEGORY_MISC, new String[] { "misc",
        "missing", "other", "mp3s", "mp3" }, Const.ADDEXTRAS_MISC_DIRNAME);

    private final Icon icon;
    private final String[] matches;
    private final String dir;

    Category(final Icon icon, final String[] matches, final String dir) {
      this.icon = icon;
      this.matches = matches;
      this.dir = dir;
      Extra.categories.add(this);
    }

    public Icon getIcon() {
      return icon;
    }

    public String getDir() {
      return dir;
    }

    public boolean startsWith(final String potentialMatch) {
      for (final String matche : matches) {
        if (potentialMatch.toLowerCase().startsWith(matche.toLowerCase())) {
          return true;
        }
      }
      return false;
    }

    public boolean contains(final String potentialMatch) {
      for (final String matche : matches) {
        if (potentialMatch.toLowerCase().contains(matche.toLowerCase())) {
          return true;
        }
      }
      return false;
    }
  }

  /**
   * Extra
   */
  private static final long serialVersionUID = -1550316222014503342L;

  private static final List<Category> categories = new ArrayList<Category>();

  private static List<Extra> deletionQueue = new ArrayList<Extra>();

  public static void enqueueForDeletion(final Extra extra) {
    deletionQueue.add(extra);
  }

  public static void processQueuedForDeletion() {
    if (deletionQueue.size() > 0) {
      Db.deleteAll(deletionQueue);
    }
    deletionQueue = new ArrayList<Extra>();
  }

  // Fields
  private int id;

  private int gameId = -1; // so "empty" extras will not be saved to db

  private int displayOrder;

  private int type;

  private String name;

  private String filename;

  private boolean ea;

  private String data;

  private String fileToRun;

  private Category category = null;

  // Constructors
  /** default constructor */
  public Extra() {
  }

  /** constructor with id */
  public Extra(final int id) {
    this.id = id;
  }

  // Property accessors
  public int getId() {
    return id;
  }

  public void setId(final int id) {
    this.id = id;
  }

  public int getGameId() {
    return gameId;
  }

  public void setGameId(final int gameId) {
    this.gameId = gameId;
  }

  public int getDisplayOrder() {
    return displayOrder;
  }

  public void setDisplayOrder(final int displayOrder) {
    this.displayOrder = displayOrder;
  }

  /**
   * 
   */
  public int getType() {
    return type;
  }

  public void setType(final int type) {
    this.type = type;
  }

  /**
   * 
   */
  public String getName() {
    return name == null ? "" : name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  /**
   * 
   */
  public String getFilename() {
    if (checkFilenameIsUrl(filename)) {
      return filename == null ? "" : filename;
    } else {
      return Paths.backslashToSlash(filename);
    }
  }

  public void setFilename(final String filename) {
    if (checkFilenameIsUrl(filename)) {
      this.filename = filename;
    } else {
      this.filename = Paths.slashToBackslash(filename);
    }
  }

  private static boolean checkFilenameIsUrl(final String s) {
    if ((s == null) || (s.isEmpty())) {
      return false;
    }
    final String t = s.toLowerCase();
    return (t.startsWith("http:") || t.startsWith("https:") || t.startsWith("ftp:"));
  }

  /**
   * 
   */
  public boolean isUrl() {
    return checkFilenameIsUrl(filename);
  }

  /**
    * 
    */
  public boolean getEa() {
    return ea;
  }

  public void setEa(final boolean ea) {
    this.ea = ea;
  }

  /**
   * 
   */
  public String getData() {
    return data == null ? "" : data;
  }

  public void setData(final String data) {
    this.data = data;
  }

  public String getFileToRun() {
    return fileToRun == null ? "" : fileToRun;
  }

  public void setFileToRun(final String fileToRun) {
    this.fileToRun = fileToRun;
  }

  public boolean isAdditional() {
    return getFilename().startsWith(ADDEXTRAS_DIRNAME);
  }

  public void reevaluateCategory() {
    category = null;
    getCategory();
  }

  public Category getCategory() {
    if (category != null) {
      return category;
    }

    if (!isUrl()) {
      category = findCategoryByPath(getFilename());
    }
    if (category != null) {
      return category;
    }

    category = findCategoryByName(getName(), isUrl());
    if (category != null) {
      return category;
    }

    category = Category.Misc;
    return category;
  }

  private static Category findCategoryByPath(final String filename) {
    Category category = null;

    // check path directory by directory
    final StringTokenizer st = new StringTokenizer(filename, "/");
    while (st.hasMoreTokens()) {
      final String pathPart = st.nextToken();
      for (final Category possibleCategory : categories) {
        if ((category == null) && possibleCategory.startsWith(pathPart)) {
          category = possibleCategory;
          break;
        }
      }
    }

    return category;
  }

  private static Category findCategoryByName(final String name, final boolean isUrl) {
    Category category = null;

    if (!isUrl) {
      // check beginning of name
      for (final Category possibleCategory : categories) {
        if ((category == null) && possibleCategory.startsWith(name)) {
          category = possibleCategory;
          return category;
        }
      }
    }

    // tokenize name to array
    final StringTokenizer st = new StringTokenizer(name, " ,.;:-_/@|{}\\[]()");
    final List<String> partList = new ArrayList<String>();

    while (st.hasMoreTokens()) {
      partList.add(st.nextToken());
    }

    final String[] parts = partList.toArray(new String[0]);

    // check name, part by part
    if (isUrl) {
      for (int i = parts.length - 1; i > 0; i--) {
        for (final Category possibleCategory : categories) {
          if (possibleCategory.startsWith(parts[i])) {
            category = possibleCategory;
            return category;
          }
        }
      }
    } else {
      for (final String part : parts) {
        for (final Category possibleCategory : categories) {
          if (possibleCategory.startsWith(part)) {
            category = possibleCategory;
            return category;
          }
        }
      }
    }

    return category;
  }

  public boolean isAnimation() {
    final String extension = FileTools.getExtension(getFilename());
    return (extension.equalsIgnoreCase("gif") && (getCategory() == Category.GameImage));
  }

  public boolean isImage() {
    final String extension = FileTools.getExtension(getFilename());
    return ((!isAnimation()) && (extension.equalsIgnoreCase("jpg")
        || extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("gif") || extension
          .equalsIgnoreCase("bmp")));
  }

  @Override
  public String toString() {
    return getName();
  }

  public void play() {
    try {
      if (isUrl()) {
        log.info("Open extra as URL='" + new URI(filename) + "'.");
        SystemTools.open(new URI(filename));
      } else {
        Extra.play(this);
      }
    } catch (final URISyntaxException e) {
      log.warn("Could not open extra '" + getName() + "' as URL '" + filename + "'.", e);
    }
  }

  private static void play(final Extra extra) {
    final File extraFile = Paths.getExtraPath().findAndWarn(new File(extra.getFilename()));

    if (extraFile == null) {
      return;
    }

    String filename = extraFile.toString();

    List<String> filenames = null;
    log.info("Trying to open extra " + filename);

    // 1. copy or extract to temp directory
    if (Plugins.existsExtractorForExtension(FileTools.getExtension(filename))) {
      log.info("extracting to temp");

      // extracted
      final Extractor extractor = Plugins
          .getExtractorForExtension(FileTools.getExtension(filename));
      try {
        filenames = extractor.extractToCleanTempDir(filename);

        // return if no files found
        if ((filenames == null) || (filenames.size() == 0)) {
          return;
        }

        // get first extracted filename as new
        // filename
        filename = filenames.get(0);
        log.info("now Trying to open " + filename);

      } catch (final IOException e) {
        e.printStackTrace();
      }
    }

    // if more than one file, open directory
    if ((filenames != null) && (filenames.size() > 1) && (new File(filename).getParent() != null)) {
      SystemTools.open(new File(filename).getParentFile().toURI());
    }

    // 2. try to open first file with game emulator
    if (Emulators.findGameEmulatorForExtension(FileTools.getExtension(filename)) != null) {
      log.info("Opening extra '" + filename + "' as game: found emulator '"
          + Emulators.findGameEmulatorForExtension(FileTools.getExtension(filename)).getName()
          + "' for extension '" + FileTools.getExtension(filename) + "'.");
      final Game game = new Game();
      game.setFileToRun(filename);
      game.play(true); // game already extracted
      return;
    }

    // 3. try to open first file with music emulator
    if (Emulators.findMusicEmulatorForExtension(FileTools.getExtension(filename)) != null) {
      log.info("Opening extra '" + filename + "' as music: found emulator '"
          + Emulators.findMusicEmulatorForExtension(FileTools.getExtension(filename)).getName()
          + "' for extension '" + FileTools.getExtension(filename) + "'.");
      final Music music = new Music();
      music.setFilename(filename);
      music.play();
      return;
    }

    // 4. open file with os association
    try {
      // simply calling file.toURI() gives "file:/path/file", where calling the
      // constructor gives "file:///path/file" ???
      final File file = new File(filename);
      final URI uri = new URI(file.toURI().getScheme(), "", file.toURI().getPath(), null);
      log.info("Opening extra '" + filename + "' via OS:'" + uri + "'.");
      SystemTools.open(uri);
    } catch (final URISyntaxException e) {
      // this can't happen, because we get scheme and path from another uri
    }

  }

  public void download() {
    try {
      if (isUrl()) {
        log.info("Download extra from URL='" + new URI(filename) + "'.");
        download(this);
      } else {
        log.warn("Can't download: Extra is nor URL.");
      }
    } catch (final URISyntaxException e) {
      log.warn("Could not open extra '" + getName() + "' as URL '" + filename + "'.", e);
    }
  }

  private static void download(final Extra extra) {
    try {
      final File targetDir = new File(Paths.getAdditionalExtraPathId(extra.getGameId()), extra
          .getCategory().getDir());
      DownloadTools.downloadUriToFile(new URI(extra.getFilename()), targetDir);
    } catch (final URISyntaxException e) {
      log.warn("Could not open extra '" + extra.getName() + "' as URL '" + extra.getFilename()
          + "'.", e);
    }
  }

  @Override
  public boolean equals(final Object obj) {

    if (this == obj) {
      return true;
    }

    if (!(obj instanceof Extra)) {
      return false;
    }

    final Extra e = (Extra) obj;

    if ((getId() != e.getId()) || (getGameId() != e.getGameId())
        || (getDisplayOrder() != e.getDisplayOrder()) || (getType() != e.getType())
        || (!getName().equals(e.getName())) || (!getFilename().equals(e.getFilename()))
        || (isUrl() != e.isUrl()) || (!getData().equals(e.getData()))
        || (!getFileToRun().equals(e.getFileToRun())) || (!getCategory().equals(e.getCategory()))) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = (37 * result) + getId();
    result = (37 * result) + getGameId();
    result = (37 * result) + getDisplayOrder();
    result = (37 * result) + getType();
    result = (37 * result) + getName().hashCode();
    result = (37 * result) + getFilename().hashCode();
    result = (37 * result) + (isUrl() ? 0 : 1);
    result = (37 * result) + getData().hashCode();
    result = (37 * result) + getFileToRun().hashCode();
    result = (37 * result) + getCategory().hashCode();
    return result;
  }

}
