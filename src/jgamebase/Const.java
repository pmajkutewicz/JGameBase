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

package jgamebase;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.AWTEvent;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ContainerEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JViewport;

import jgamebase.model.FileExtensions;
import jgamebase.tools.IniFileManager;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * Contains the constants used in GameBase.
 * 
 * @author F. Gerbig (fgerbig@users.sourceforge.net)
 */
public abstract class Const {

  // TODO update the version and date
  public static final String VERSION = "0.64-6";
  public static final String DATE_DAY = "03";
  public static final String DATE_MONTH = "01";
  public static final String DATE_YEAR = "2017";

  public static float NUM_VERSION = 0;
  public static final String DATE = DATE_YEAR + "-" + DATE_MONTH + "-" + DATE_DAY;

  public static final String NAME_JGAMEBASE = "jGameBase";
  public static final String NAME_TOOLBOX = "ToolBox";

  public static final String NAME_JGAMEBASE_LC = NAME_JGAMEBASE.toLowerCase();

  public static final Logger log = Logger.getLogger(JGameBase.class);

  /** The program version. */
  public static final String JGAMEBASE_VERSION = NAME_JGAMEBASE + " V" + VERSION + " (" + DATE + ")";
  public static final String TOOLBOX_VERSION = NAME_TOOLBOX + " V" + VERSION + " (" + DATE + ")";

  public static IniFileManager global = null;

  /** true if the operating system is MS Windows, false otherwise. */
  public static final boolean OS_IS_WINDOWS = System.getProperty("os.name").toLowerCase().startsWith("windows");

  public static final String DEFAULT_SCRIPT_EXTENSION = OS_IS_WINDOWS ? "bat" : "sh";

  public static final String LINE_SEPARATOR = System.getProperty("line.separator");
  public static final String LINE_SEPARATOR_PATTERN = "\r\n|[\n\r\u2028\u2029\u0085]";
  public static final String EXTENSION_SEPARATOR = ".";

  public static final String SYSTEMPROPERTY_USE_SYSTEM_PROXIES = "java.net.useSystemProxies";
  public static final String SYSTEMPROPERTY_HTTP_PROXY_PASSWORD = "http.proxyPassword";
  public static final String SYSTEMPROPERTY_HTTP_PROXY_USER = "http.proxyUser";

  /**
   * The home directory of the program evaluate the system property "gb.dir"; if
   * this is not set, fall back to the property "user.dir"
   */
  public static final File GBDIR_RO = new File(System.getProperty("gb.dir", System.getProperty("user.dir")));

  public static boolean FHS = false;
  public static File GBDIR_RW = GBDIR_RO;
  public static File LOCKFILE = new File(GBDIR_RW, ".lock");

  /** The copyright message. */
  public static final String COPYRIGHT = "Copyright © 2006-" + DATE_YEAR + " by Frank Gerbig.";

  /** The copyright message. */
  public static final String LICENSE = "GNU General Public License version 3.";

  /** The support email address. */
  public static final String EMAIL = "fgerbig@users.sourceforge.net";

  /** The home page location. */
  public static final URI URI_HOMEPAGE = URI.create("http://jgamebase.sourceforge.net");

  /** The documentation location. */
  public static final URI URI_DOCUMENTATION = new File(GBDIR_RO, "Docs/index.html").toURI();

  /** The forum location. */
  public static final URI URI_FORUM = URI
      .create("http://sourceforge.net/forum/forum.php?forum_id=612110");

  /** The bug tracking location. */
  public static final URI URI_BUGTRACKING = URI
      .create("http://sourceforge.net/tracker/?group_id=177156&atid=880114");

  /** The database download location for jGameBase databases. */
  public static final URI URI_DOWNLOAD_JDB = URI
      .create("http://sourceforge.net/projects/jgamebase/files/Databases/");

  /** The database download location for GameBase databases. */
  public static final URI URI_DOWNLOAD_DB = URI.create("http://bu22.com/");

  /** The "about" message. */
  public static final String ABOUT_MSG = "Homepage: " + URI_HOMEPAGE + "\n" + "Contact:  " + EMAIL
      + "\n\n" + COPYRIGHT + "\n" + LICENSE + "\n\n" + "For more help on " + NAME_JGAMEBASE
      + " please take a look at the homepage.";

  /** The help message. */
  public static final String HELP_MSG = ABOUT_MSG + "\n\n" + "For help on using " + NAME_JGAMEBASE
      + " try '-usage'.\n";

  /** The usage message. */
  public static final String USAGE_MSG = "Usage:\n'" + NAME_JGAMEBASE_LC
      + " [-help | -usage | [-debug] [toolbox | database name [item id]]]\n"
      + "  -help      Display help on homepage, contact, and license.\n"
      + "  -usage     Displays this message.\n"
      + "  -debug     Displays extra debug messages.\n"
      + "  toolbox    Start the database toolbox.\n"
      + "  db name  Name of the database to open (e.g. C64lite).\n"
      + "  item id     Id of the item to start (e.g. G726 or M20544).\n";

  public static final String OVERLAY_DIRNAME = "Overlays";
  public static final String OVERLAY_INI_FILENAME = "overlays.ini";

  /** close action codes. */
  public static enum CloseAction {
    CANCEL, OK
  };

  public static final String VIEWNAME_ALL_GAMES = "<All Games>";
  public static final String VIEWNAME_AVAILABLE_GAMES = "<Available Games>";
  public static final String VIEWNAME_ALL_MUSIC = "<All Music>";
  public static final String VIEWNAME_AVAILABLE_MUSIC = "<Available Music>";
  public static final String VIEWNAME_FAVOURITES = "<Favourites (Games and Music)>";

  // the program icon
  public static final Icon ICON_JGAMEBASE = new ImageIcon(new File(GBDIR_RO, "Artwork/ProgramIcons/GameBase.gif").toString());
  public static final Image IMAGE_JGAMEBASE = ((ImageIcon) ICON_JGAMEBASE).getImage();
  public static final Icon ICON_TOOLBOX = new ImageIcon(new File(GBDIR_RO,
      "Artwork/ProgramIcons/GBToolbox.gif").toString());
  public static final Image IMAGE_TOOLBOX = ((ImageIcon) ICON_TOOLBOX).getImage();

  public static final String DATABASE_ICONS_DIRPATH = new File(GBDIR_RO, "Artwork/DatabaseIcons")
      .toString();
  // the default database icon
  public static final Icon ICON_DEFAULT_DATABASE = new ImageIcon(new File(DATABASE_ICONS_DIRPATH,
      "Default.gif").toString());

  // icons for the item list header
  public static final Icon ICON_LISTHEADER_NOSORT = new ImageIcon();
  public static final Icon ICON_LISTHEADER_SORTUP = new ImageIcon(new File(GBDIR_RO,
      "Artwork/ListViewIcons/up.gif").toString());
  public static final Icon ICON_LISTHEADER_SORTDOWN = new ImageIcon(new File(GBDIR_RO,
      "Artwork/ListViewIcons/down.gif").toString());

  // icons for tabs
  public static final Icon ICON_TAB_GAME = new ImageIcon(new File(GBDIR_RO,
      "Artwork/DetailsTab/tab0.gif").toString());
  public static final Icon ICON_TAB_VERSION = new ImageIcon(new File(GBDIR_RO,
      "Artwork/DetailsTab/tab1.gif").toString());
  public static final Icon ICON_TAB_PERSONAL = new ImageIcon(new File(GBDIR_RO,
      "Artwork/DetailsTab/tab2.gif").toString());
  public static final Icon ICON_TAB_NOTES = new ImageIcon(new File(GBDIR_RO,
      "Artwork/DetailsTab/tab3.gif").toString());
  public static final Icon ICON_TAB_NONOTES = new ImageIcon(new File(GBDIR_RO,
      "Artwork/DetailsTab/tab3a.gif").toString());
  public static final Icon ICON_TAB_MUSIC = new ImageIcon(new File(GBDIR_RO,
      "Artwork/DetailsTab/tabsid.gif").toString());
  public static final Icon ICON_TAB_ONLYMUSIC = new ImageIcon(new File(GBDIR_RO,
      "Artwork/DetailsTab/tabsidonly.gif").toString());

  // icons for side panel
  public static final Icon ICON_SIDEBAR_GAMEIMAGE = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Sidebar/GameImage.png").toString());
  public static final Icon ICON_SIDEBAR_ADDITIONALIMAGE = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Sidebar/AdditionalImage.png").toString());
  public static final Icon ICON_SIDEBAR_DOCUMENTATION = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Sidebar/Documentation.png").toString());
  public static final Icon ICON_SIDEBAR_MISC = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Sidebar/Misc.png").toString());

  // icons for category
  public static final Icon ICON_CATEGORY_GAMEIMAGE = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Category/GameImages.png").toString());
  public static final Icon ICON_CATEGORY_ADDITIONALIMAGE = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Category/AdditionalImage.png").toString());
  public static final Icon ICON_CATEGORY_ADDITIONALIMAGE_COVER = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Category/AdditionalImage_Cover.png").toString());
  public static final Icon ICON_CATEGORY_ADDITIONALIMAGE_ADVERTISEMENT = new ImageIcon(new File(
      GBDIR_RO, "Artwork/Category/AdditionalImage_Advertisement.png").toString());
  public static final Icon ICON_CATEGORY_ADDITIONALIMAGE_BOOKCOVER = new ImageIcon(new File(
      GBDIR_RO, "Artwork/Category/AdditionalImage_Bookcover.png").toString());
  public static final Icon ICON_CATEGORY_DOCUMENTATION = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Category/Documentation.png").toString());
  public static final Icon ICON_CATEGORY_DOCUMENTATION_MANUAL = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Category/Documentation_Manual.png").toString());
  public static final Icon ICON_CATEGORY_SOLUTION = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Category/Solution.png").toString());
  public static final Icon ICON_CATEGORY_SOLUTION_MAP = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Category/Solution_Map.png").toString());
  public static final Icon ICON_CATEGORY_SOLUTION_TIP = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Category/Solution_Tip.png").toString());
  public static final Icon ICON_CATEGORY_SOLUTION_WALKTHROUGH = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Category/Solution_Walkthrough.png").toString());
  public static final Icon ICON_CATEGORY_REVIEW = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Category/Review.png").toString());
  public static final Icon ICON_CATEGORY_MOVIE = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Category/Movie.png").toString());
  public static final Icon ICON_CATEGORY_MEDIA = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Category/Media.png").toString());
  public static final Icon ICON_CATEGORY_MEDIA_CARTRIDGE = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Category/Media_Cartridge.png").toString());
  public static final Icon ICON_CATEGORY_MEDIA_TAPE = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Category/Media_Tape.png").toString());
  public static final Icon ICON_CATEGORY_MEDIA_DISK = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Category/Media_Disk.png").toString());
  public static final Icon ICON_CATEGORY_MEDIA_HARDDISK = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Category/Media_Harddisk.png").toString());
  public static final Icon ICON_CATEGORY_MISC = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Category/Misc.png").toString());
  public static final Icon ICON_CATEGORY_OVERLAY_ADDITIONAL = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Category/overlay/additional.png").toString());
  public static final Icon ICON_CATEGORY_OVERLAY_EQUAL = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Category/overlay/equal.png").toString());
  public static final Icon ICON_CATEGORY_OVERLAY_DOWNLOAD = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Category/overlay/download.png").toString());
  public static final Icon ICON_CATEGORY_OVERLAY_MISSING = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Category/overlay/missing.png").toString());
  public static final Icon ICON_CATEGORY_OVERLAY_PACKED = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Category/overlay/packed.png").toString());
  public static final Icon ICON_CATEGORY_OVERLAY_URL = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Category/overlay/url.png").toString());

  public static final FileExtensions EXTENSIONS_PACKED = new FileExtensions(
      "7z;a;ace;ar;arc;arj;bz2;cab;gz;lz;lzma;lzo;rar;rz;s7z;sit;sitx;tar;tbz2;tgz;tlz;xz;z;zip;zipx;zoo");
  public static final FileExtensions EXTENSIONS_NODOWNLOAD = new FileExtensions(
      "asp;aspx;bml;cfm;cgi;hta;htm;html;htw;ihtml;jsp;las;lasso;php;php1;php2;php3;php4;php5;php6;php7;php8;php9;phtml;pl;rna;shtm;shtml;stm;xht;xhtml");

  // icons for the item list
  public static final Icon ICON_LIST_NONE = new ImageIcon(new File(GBDIR_RO,
      "Artwork/ListViewIcons/lv_none.gif").toString());
  public static final Icon ICON_LIST_SIDONLY = new ImageIcon(new File(GBDIR_RO,
      "Artwork/ListViewIcons/lv_sidonly.gif").toString());
  public static final Icon ICON_LIST_JUSTGAME = new ImageIcon(new File(GBDIR_RO,
      "Artwork/ListViewIcons/lv_justgame.gif").toString());
  public static final Icon ICON_LIST_JUSTSID = new ImageIcon(new File(GBDIR_RO,
      "Artwork/ListViewIcons/lv_justsid.gif").toString());
  public static final Icon ICON_LIST_GAMEANDSID = new ImageIcon(new File(GBDIR_RO,
      "Artwork/ListViewIcons/lv_gameandsid.gif").toString());
  public static final Icon[] ICONS_GAMELIST = { ICON_LIST_NONE, ICON_LIST_GAMEANDSID,
      ICON_LIST_JUSTGAME, ICON_LIST_JUSTSID, ICON_LIST_NONE };

  public static final Icon ICON_GAMEINFO_PREQUEL = new ImageIcon(new File(GBDIR_RO,
      "Artwork/DetailsTab/prequel.gif").toString());
  public static final Icon ICON_GAMEINFO_NOPREQUEL = new ImageIcon(new File(GBDIR_RO,
      "Artwork/DetailsTab/noprequel.gif").toString());
  public static final Icon ICON_GAMEINFO_SEQUEL = new ImageIcon(new File(GBDIR_RO,
      "Artwork/DetailsTab/sequel.gif").toString());
  public static final Icon ICON_GAMEINFO_NOSEQUEL = new ImageIcon(new File(GBDIR_RO,
      "Artwork/DetailsTab/nosequel.gif").toString());
  public static final Icon ICON_GAMEINFO_RELATED = new ImageIcon(new File(GBDIR_RO,
      "Artwork/DetailsTab/related.gif").toString());
  public static final Icon ICON_GAMEINFO_NORELATED = new ImageIcon(new File(GBDIR_RO,
      "Artwork/DetailsTab/norelated.gif").toString());

  public static final Icon[] ICONS_GAMEINFO_RATING = {
      new ImageIcon(new File(GBDIR_RO, "Artwork/DetailsTab/noratingface.gif").toString()),
      new ImageIcon(new File(GBDIR_RO, "Artwork/DetailsTab/ratingface1.gif").toString()),
      new ImageIcon(new File(GBDIR_RO, "Artwork/DetailsTab/ratingface2.gif").toString()),
      new ImageIcon(new File(GBDIR_RO, "Artwork/DetailsTab/ratingface3.gif").toString()),
      new ImageIcon(new File(GBDIR_RO, "Artwork/DetailsTab/ratingface4.gif").toString()),
      new ImageIcon(new File(GBDIR_RO, "Artwork/DetailsTab/ratingface5.gif").toString()),
      new ImageIcon(new File(GBDIR_RO, "Artwork/DetailsTab/cup.gif").toString()) };

  public static final Icon[] LARGEICONS_GAMEINFO_RATING = {
      new ImageIcon(new File(GBDIR_RO, "Artwork/DetailsTab/big_noratingface.gif").toString()),
      new ImageIcon(new File(GBDIR_RO, "Artwork/DetailsTab/big_ratingface1.gif").toString()),
      new ImageIcon(new File(GBDIR_RO, "Artwork/DetailsTab/big_ratingface2.gif").toString()),
      new ImageIcon(new File(GBDIR_RO, "Artwork/DetailsTab/big_ratingface3.gif").toString()),
      new ImageIcon(new File(GBDIR_RO, "Artwork/DetailsTab/big_ratingface4.gif").toString()),
      new ImageIcon(new File(GBDIR_RO, "Artwork/DetailsTab/big_ratingface5.gif").toString()),
      new ImageIcon(new File(GBDIR_RO, "Artwork/DetailsTab/big_cup.gif").toString()) };

  public static final Icon ICON_GAMEINFO_CLASSIC = new ImageIcon(new File(GBDIR_RO,
      "Artwork/DetailsTab/cup.gif").toString());

  public static final Icon ICON_GAMEINFO_ADULT = new ImageIcon(new File(GBDIR_RO,
      "Artwork/DetailsTab/adult.gif").toString());
  public static final Icon ICON_GAMEINFO_NOADULT = new ImageIcon(new File(GBDIR_RO,
      "Artwork/DetailsTab/noadult.gif").toString());

  // loading
  public static final Icon ICON_LOADING = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Misc/Loading.gif").toString());
  public static final Icon ICON_LOADERROR = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Misc/LoadError.png").toString());
  public static final Image IMAGE_LOADERROR = ((ImageIcon) ICON_LOADERROR).getImage();

  // no screenshot
  public static final Icon ICON_MISSINGSCREENSHOT = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Screenshots/nossbig.gif").toString());

  // no photo
  public static final Icon ICON_NOPHOTO = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Screenshots/nophoto.gif").toString());

  // path selector icons
  public static final Icon ICON_PATHSELECTOR_ADD = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Misc/PathSelector_Toolbar_New.gif").toString());
  public static final Icon ICON_PATHSELECTOR_REMOVE = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Misc/PathSelector_Toolbar_Delete.gif").toString());
  public static final Icon ICON_PATHSELECTOR_UP = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Misc/PathSelector_Toolbar_MoveUp.gif").toString());
  public static final Icon ICON_PATHSELECTOR_DOWN = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Misc/PathSelector_Toolbar_MoveDown.gif").toString());

  // sound icons
  public static final Icon ICON_SOUND_ON = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Misc/Sound_on.gif").toString());
  public static final Icon ICON_SOUND_OFF = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Misc/Sound_off.gif").toString());

  // joystick icons
  public static final Icon ICON_JOYSTICK_ON = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Misc/Joystick_on.gif").toString());
  public static final Icon ICON_JOYSTICK_OFF = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Misc/Joystick_off.gif").toString());

  // game selector icons
  public static final Icon ICON_GS_FOLDER = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Selectors/folder.png").toString());
  public static final Icon ICON_GS_FOLDER_OPEN = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Selectors/folder_open.png").toString());
  public static final Icon ICON_GS_FILE = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Selectors/file.png").toString());
  public static final Icon ICON_GS_IMAGE = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Selectors/image.png").toString());
  public static final Icon ICON_GS_GAME = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Selectors/game.png").toString());
  public static final Icon ICON_GS_MUSIC = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Selectors/music.png").toString());
  public static final Icon ICON_GS_GRAPHIC = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Selectors/graphic.png").toString());
  public static final Icon ICON_GS_COMPRESSED = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Selectors/compressed.png").toString());

  public static final Icon ICON_PLAY_MUSIC = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Misc/play_music.png").toString());
  public static final Icon ICON_STOP_MUSIC = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Misc/stop_music.png").toString());
  public static final Icon ICON_ADDEXTRAS_BROWSER = new ImageIcon(new File(GBDIR_RO,
      "Artwork/Misc/browse_addextras.png").toString());

  // classic sound
  public static AudioClip SOUND_CLASSIC;

  // descriptions for display
  public static final String[] FORDISPLAY_RATING = { "?/5 - Unknown", "1/5 - Terrible",
      "2/5 - Poor", "3/5 - Average", "4/5 - Quite Good", "5/5 - Very Good", "Classic!" };

  public static final int NEUTRAL_ID_RATING = 0;

  public static final String[] FORDISPLAY_PALNTSC = { "PAL", "PAL+NTSC", "NTSC", "PAL(+NTSC?)" };

  public static final int NEUTRAL_ID_PALNTSC = 3;

  public static final String[] FORDISPLAY_LENGTHTYPE = { "Block(s)", "Disk(s)", "Cartridge(s)",
      "Tape(s)", "Unused", "Unused", "Unused", "Unused", "Unused", "Unused", "Unused", "Unused",
      "Unused", "Unused", "Unused", "Unused", "Unused", "Unused", "Unused", "Unknown" };

  public static final int NEUTRAL_ID_LENGTHTYPE = 19;

  public static final String[] FORDISPLAY_CONTROL = { "Joystick Port 2", "Joystick Port 1",
      "Keyboard", "Paddle Port 2", "Paddle Port 1", "Mouse", "Light Pen", "Koala Pad", "Light Gun" };

  public static final int NEUTRAL_ID_CONTROL = 0; // no (unknown) defined

  public static final int YEAR_EARLIEST = 1970;

  public static final int YEAR_LATEST = 2015; // GameBase uses 2100 - that seems
                                              // a little overly optimistic ;-)

  public static final int YEAR_NUMBER = (YEAR_LATEST - YEAR_EARLIEST) + 10;

  public static String[] FORDISPLAY_YEAR = new String[YEAR_NUMBER + 1];

  public static final String DATABASE_DIRNAME = "Database";

  public static final String DATABASEBACKUP_DIRNAME = DATABASE_DIRNAME + "_bck";

  public static final String EXPORT_DIRNAME = "Export";

  public static final String EXTRAS_DIRNAME = "Extras";
  public static final String GAMES_DIRNAME = "Games";
  public static final String MUSIC_DIRNAME = "Music";
  public static final String PHOTOS_DIRNAME = "Photos";
  public static final String SCREENSHOTS_DIRNAME = "Screenshots";
  public static final String[] DB_SUBDIRS = { EXTRAS_DIRNAME, GAMES_DIRNAME, MUSIC_DIRNAME,
      PHOTOS_DIRNAME, SCREENSHOTS_DIRNAME };

  public static final String SCRIPT_DIRNAME = "Scripts";
  public static final String INCLUDE_DIRNAME = "Includes";
  public static final String INCLUDE_FILENAME = "includes";

  public static final String ADDEXTRAS_DIRNAME = "Additional";

  public static final String ADDEXTRAS_URL_FILENAME = "url.csv";
  public static final String ADDEXTRAS_BYID_DIRNAME = "by-id";
  public static final String ADDEXTRAS_BYNAME_DIRNAME = "by-name";

  public static final String ADDEXTRAS_ANIMATION_DIRNAME = "Animation";
  public static final String ADDEXTRAS_COVER_DIRNAME = "Covers";
  public static final String ADDEXTRAS_MANUAL_DIRNAME = "Manuals";
  public static final String ADDEXTRAS_MEDIA_DIRNAME = "Media";
  public static final String ADDEXTRAS_MISC_DIRNAME = "Misc";
  public static final String ADDEXTRAS_LONGPLAY_DIRNAME = "Longplays";
  public static final String ADDEXTRAS_REVIEW_DIRNAME = "Reviews";
  public static final String ADDEXTRAS_SOLUTION_DIRNAME = "Solutions";

  public static final String[] ADDEXTRAS_SUBDIRS = { ADDEXTRAS_ANIMATION_DIRNAME,
      ADDEXTRAS_COVER_DIRNAME, ADDEXTRAS_MANUAL_DIRNAME, ADDEXTRAS_MEDIA_DIRNAME,
      ADDEXTRAS_MISC_DIRNAME, ADDEXTRAS_LONGPLAY_DIRNAME, ADDEXTRAS_REVIEW_DIRNAME,
      ADDEXTRAS_SOLUTION_DIRNAME };

  static {
    try {
      global = new IniFileManager(new File(Const.GBDIR_RO, "global.ini").toString());
    } catch (final IOException ioe) {
      ioe.printStackTrace();
    }

    // Filesystem Hierarchy Standard?

    // read fhs mode from global.ini, but never use fhs on windows
    FHS = Boolean.parseBoolean(global.get("DIRS", "FHS")) && !(OS_IS_WINDOWS);

    // if we can't write to the database directory we have to use fhs mode
    // anyway
    if (!GBDIR_RW.canWrite()) {
      FHS = true;
    }

    // if we use fhs the database directory is a sub directory of the users home
    // directory
    if (FHS) {
      GBDIR_RW = new File(System.getProperty("user.home"), "." + NAME_JGAMEBASE_LC);
      LOCKFILE = new File(global.get("DIRS", "LOCKPATH"), NAME_JGAMEBASE_LC);
    }

    log.addAppender(new ConsoleAppender(new PatternLayout()));
    try {
      log.addAppender(new FileAppender(new PatternLayout(), new File(GBDIR_RW, NAME_JGAMEBASE_LC
          + ".log").getAbsolutePath(), false));
    } catch (final IOException ioe) {
      ioe.printStackTrace();
    }

    // init FORDISPLAY_YEAR
    FORDISPLAY_YEAR[0] = "";
    FORDISPLAY_YEAR[1] = "????";
    FORDISPLAY_YEAR[2] = "19??";
    FORDISPLAY_YEAR[3] = "197?";
    FORDISPLAY_YEAR[4] = "198?";
    FORDISPLAY_YEAR[5] = "199?";
    FORDISPLAY_YEAR[6] = "20??";
    FORDISPLAY_YEAR[7] = "200?";
    FORDISPLAY_YEAR[8] = "201?";
    FORDISPLAY_YEAR[9] = "202?";
    for (int i = 10; i <= YEAR_NUMBER; i++) {
      FORDISPLAY_YEAR[i] = ((YEAR_EARLIEST + i) - 10) + "";
    }

    try {
      SOUND_CLASSIC = Applet
          .newAudioClip(new File(GBDIR_RO, "Artwork/Classic.wav").toURI().toURL());
    } catch (final MalformedURLException e) {
      e.printStackTrace();
    }

    // initialize NUM_VERSION
    String s = new String(VERSION);
    s = s.replaceAll("[^0-9^\\.]", ""); // keep digits and dot
    try {
      NUM_VERSION = Float.parseFloat(s);
    } catch (NumberFormatException e) {
    }
    
    // WORKAROUND FOR APPLE MEMORY LEAK IN JAVA 1.6.0_29
    // see
    // "http://prod.lists.apple.com/archives/java-dev/2012/Jan/msg00000.html"
    // "http://www.concurrentaffair.org/2011/11/14/drjava-definitionspanememoryleaktest-fails-on-mac/"
    // "http://stackoverflow.com/questions/8232783/how-to-rolling-back-java-1-6-0-29-on-mac-because-of-memory-leaks"

    Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
      @Override
      public void eventDispatched(final AWTEvent event) {
        if (event.getID() == ContainerEvent.COMPONENT_ADDED) {
          if (((ContainerEvent) event).getChild() instanceof JViewport) {
            final Toolkit toolkit = Toolkit.getDefaultToolkit();
            final String name = "apple.awt.contentScaleFactor";
            for (final PropertyChangeListener each : toolkit.getPropertyChangeListeners(name)) {
              toolkit.removePropertyChangeListener(name, each);
            }
          }
        }
      }
    }, AWTEvent.CONTAINER_EVENT_MASK);
  }

  /**
   * escape char different from backslash so backslashes do not get "eaten"
   * during csv import
   */
  public static final char CSV_ESCAPE_CHAR = '\u0010';
}
