/*
 * Copyright (C) 2006-2017 F. Gerbig (fgerbig@users.sourceforge.net)
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

import static jgamebase.Const.log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import jgamebase.Const.CloseAction;
import jgamebase.db.Db;
import jgamebase.db.Export;
import jgamebase.db.model.Item;
import jgamebase.gui.DatabaseSelectionDialog;
import jgamebase.gui.Gui;
import jgamebase.gui.ToolboxGui;
import jgamebase.model.Database;
import jgamebase.model.Databases;
import jgamebase.model.Emulators;
import jgamebase.model.Overlays;
import jgamebase.model.Paths;
import jgamebase.model.Plugins;
import jgamebase.model.Preferences;
import jgamebase.tools.FileTools;
import jgamebase.tools.ListerTools;
import jgamebase.tools.SystemTools;
import jgamebase.tools.TempDir;

/**
 * The main class of GameBase.
 * 
 * @author F. Gerbig (fgerbig@users.sourceforge.net)
 */
public class JGameBase {

    private static Gui gui = null;

    protected static boolean option_help;
    protected static boolean option_usage;
    protected static boolean option_releaseDatabase;
    public static boolean option_debug;
    public static boolean option_dangerous;
    public static boolean nogui = false;

    protected static String[] parseOptions(final String[] args) {

        final List<String> options = new ArrayList<String>(Arrays.asList(args));

        for (final Iterator<String> iter = options.iterator(); iter.hasNext();) {
            // all arguments
            String option = iter.next();

            if (option.startsWith("-")) {
                // is option ?
                option = option.substring(1).toLowerCase();
                iter.remove(); // remove option from arguments

                if (option.equals("?") || option.equalsIgnoreCase("h")
                        || option.equalsIgnoreCase("help")
                        || option.equalsIgnoreCase("-help")) {
                    option_help = true;
                } else if (option.equalsIgnoreCase("usage")
                        || option.equalsIgnoreCase("-usage")) {
                    option_usage = true;
                } else if (option.equalsIgnoreCase("releasedb")
                        || option.equalsIgnoreCase("-releasedb")) {
                    option_releaseDatabase = true;
                } else if (option.equalsIgnoreCase("debug")
                        || option.equalsIgnoreCase("-debug")) {
                    JGameBase.option_debug = true;
                } else if (option.equalsIgnoreCase("dangerous")
                        || option.equalsIgnoreCase("-dangerous")) {
                    JGameBase.option_dangerous = true;
                } else {
                    JGameBase.displayError("Unknown option '" + option + "'.");
                    option_usage = true;
                }
            }
        }
        return options.toArray(new String[options.size()]);
    }

    /**
     * The main class of GameBase.
     * 
     * @param args
     *            The command line arguments.
     * @throws Exception
     */
    public static void main(String[] args) {
        try {
            log.info(Const.JGAMEBASE_VERSION);
            log.info(Const.LICENSE);
            log.info(Const.COPYRIGHT);
            log.info("");
            log.info("Program directory = \"" + Const.GBDIR_RO + "\"");
            log.info("Database directory = \"" + Const.GBDIR_RW + "\"");
            log.info("Lock file = \"" + Const.LOCKFILE + "\"");
            log.info("");
            log.info("Writing log to \""
                    + new File(Const.GBDIR_RW, Const.NAME_JGAMEBASE_LC + ".log")
                            .getAbsolutePath() + "\"");
            log.info("");

            // set look and feel
            // done so early so that error messages will look "pretty" :-)
            Gui.setLookAndFeel();

            if (SystemTools.isOtherInstanceRunning()) {
                Gui.displayErrorDialog("It seems there is already an instance of jGamebase running.\n"
                        + "Exiting...");
                System.exit(1);
            }

            args = parseOptions(args);

            // help or usage?
            if (option_help) {
                displayHelp();
            } else if (option_usage) {
                displayUsage();
            }

            if (JGameBase.option_debug) {
                System.err.println("DEBUG MODE ACTIVATED.");
            }
            if (JGameBase.option_dangerous) {
                System.err.println("DANGEROUS MODE ACTIVATED.");
            }
            if (JGameBase.option_releaseDatabase) {
                System.err.println("DATABASE RELEASE MODE ACTIVATED.");
            }
            System.err.println();

            switch (args.length) {
            case 0:
                startup("", ""); // startup, no database name or item id given
                break;

            case 1:
                if (args[0].equalsIgnoreCase("toolbox")) {
                    log.info(Const.TOOLBOX_VERSION);
                    log.info(Const.COPYRIGHT);
                    log.info("");
                    new ToolboxGui();
                } else {
                        startup(args[0], ""); // startup with given database name
                }
                break;

            case 2:
                startup(args[0], args[1]); // startup with given database name and item id
                break;

            default:
                displayError("Too many arguments.");
                displayUsage();
            }
        } catch (final Throwable e) {
            SystemTools.handleFatalError(e);
            try {
                Databases.getCurrent().hideSplashscreen();
            } catch (final RuntimeException rte) {
            }
            System.exit(1);
        }
    }

    private static void startup(String dbName, String itemId) throws IOException {
        if (!dbName.isEmpty()) {
            log.info("dbName = " + dbName);
        }

        if (!itemId.isEmpty()) {
            itemId = itemId.toUpperCase();
            log.info("itemId = " + itemId + " (itemId specified, deactivating GUI)");
            nogui = true; // launcher needs no gui
        }

        
        if (!SystemTools.isJavaVersionOk()) {
            Gui.displayWarningDialog("It seems your are using an unsupported version of Java.\n"
                    + "Java from SUN/Oracle, Apple, and OpenJDK is supported.\n"
                    + "The version must be at least Java 1.6.\n\n"
                    + "You have been warned ;-)");
        }

        log.info("");
        Plugins.initExtractorPlugins();
        log.info("");

        Databases.init();

        Overlays.init();

        if (Databases.getList().size() == 0) {
            Gui.displayWarningDialog("No database could be found.\n"
                    + "Please get a (j)GameBase database (e.g. 'C64lite' from '"
                    + Const.URI_DOWNLOAD_JDB
                    + "')\n"
                    + "and extract it into a subdirectory under '"
                    + Const.GBDIR_RW
                    + "'.\n"
                    + "Then start jGamebase again.\n\nYour web browser will now be openend with the download location.");
            if (!nogui) { // don't open browser when launching item
                SystemTools.open(Const.URI_DOWNLOAD_JDB);
            }
            quit();
        }

        // prepare databases for release
        if (option_releaseDatabase) {
            int i = 1;
            for (Database database : Databases.getList()) {

                System.out.println("\n\n" + (i++) + "/"
                        + Databases.getList().size() + ": Preparing database '"
                        + database.getDisplayName() + "' for release:");
                Databases.setCurrent(database);

                Preferences.load();

                // clear db usage information
                System.out.println("  clearing preferences...");
                Preferences.set(Preferences.ABOUT_SINCE, "");
                Preferences.set(Preferences.ABOUT_GAMES_PLAYED, 0);
                Preferences.set(Preferences.ABOUT_MUSIC_PLAYED, 0);
                System.out.println("  reseting usage information...");
                Db.resetGamesPlayedInformation();

                // reorganize database
                System.out.println("  reorganizing database...");
                Db.reorganize();

                // export database to Access *.mdb in "Export" directory
                // try {
                // Export.db2Mdb();
                // } catch (Exception e) {
                // e.printStackTrace();
                // }

                Db.close();
                System.out.println("  closed database'"
                        + database.getDisplayName() + "'.");
            }
            quit();
        }

        // select database
        Database database = null;
        while (database == null) {
            if (Databases.getList().size() > 1) {

                if (!dbName.isEmpty()) {
                    // match name
                    for (Database possibleDatabase : Databases.getList()) {
                        if (dbName.equalsIgnoreCase(possibleDatabase.getDisplayName()) || (dbName.equalsIgnoreCase(possibleDatabase.getName()))) {
                            database = possibleDatabase;
                        }
                    }

                    // match name start
                    if (database == null) {
                        for (Database possibleDatabase : Databases.getList()) {
                            if (possibleDatabase.getDisplayName().toLowerCase().startsWith(dbName.toLowerCase()) || (possibleDatabase.getName().toLowerCase().startsWith(dbName.toLowerCase()))) {
                                database = possibleDatabase;
                            }
                        }
                    }
                }

                // no database name given, or given database name does not match
                // any database => display selection dialog
                if (database == null) {
                    if (nogui) {
                        log.error("Specified database '" + dbName + "' not found.\nExiting...");
                        quit();
                    }
                    
                    final DatabaseSelectionDialog selectDialog = new DatabaseSelectionDialog(
                            getGui(), Databases.getList(), false);
                    if (selectDialog.getCloseAction() == CloseAction.CANCEL) {
                        log.error("No database selected.\nExiting...");
                        quit();
                    }
                    
                    database = (Database) selectDialog.getSelected().get(0);
                }
            } else {
                // only one database found, so use it
                database = Databases.getList().get(0);
            }
        }

        if (!nogui) { // no db splashscreen when launching item
            database.showSplashscreen();
        }
        Databases.setCurrent(database);

        Preferences.load();

        if (!nogui) { // don't check for new overlays when launching item
            // find latest overlay for selected database
            Overlays.findLatestVersion(database);
            // get overlay version
            int latestOverlayVersion = Overlays.getLatestVersion();
            // found a matching overlay with version?
            if (latestOverlayVersion > 0) {
                int installedOverlayVersion = Preferences
                        .getInt(Preferences.OVERLAY_VERSION);
                if (latestOverlayVersion > installedOverlayVersion) {
                    // ask
                    if (Gui.displayConfirmationDialog("Newer emulator scripts and configurations were found.\nOverwrite existing scripts and configurations?") == JOptionPane.YES_OPTION) {
                        Overlays.installLatestVersion(database);
                        Preferences.set(Preferences.OVERLAY_VERSION,
                                latestOverlayVersion);
                    }
                }
            }
        }
        
        Paths.readFromIniFile();
        Emulators.readFromIniFile();

        log.info("");

        Plugins.initDiskInfoPlugins();
        Plugins.initMusicInfoPlugins();

        log.info("");

        if (nogui) {
            // launch item
            if (itemId.matches("^[mMgG]\\d+$")) { // item id e.g. G726 or M20544
                Item item = null;
                item = Db.getItembyId(itemId);
                if (item != null) {
                    item.play();
                } else {
                    Gui.displayErrorDialog("Item with id '" + itemId + "' not found in database '" + Databases.getCurrent() + "'.\nExiting...");
                }
            } else {
                Gui.displayErrorDialog("Invalid item id '" + itemId + "' specified.\nExiting...");
            }
            quit();
        } else { // start gui
            // show gui
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    // create gui
                    gui = new Gui();

                    // now gui is initialized
                    gui.reloadCurrentView();

                    // show gui
                    Databases.getCurrent().hideSplashscreen();
                    gui.setVisible(true);

                    // this has to be done after the gui is visible
                    gui.initAfterVisible();
                }
            });
        }
    }

    /** Displays the help message. */
    public static void displayHelp() {
        log.info(Const.HELP_MSG);
        Gui.displayHelpDialog();
        System.exit(0);
    }

    /** Displays the usage message. */
    public static void displayUsage() {
        log.info(Const.USAGE_MSG);
        Gui.displayUsageDialog();
        System.exit(1);
    }

    /**
     * Displays an error message.
     * 
     * @param s
     *            The error message.
     */
    public static void displayError(final String s) {
        log.error("ERROR: " + s + "\n");
        Gui.displayErrorDialog(s);
    }

    /**
     * Get the GUI.
     * 
     * @return The GUI.
     */
    public static Gui getGui() {
        return gui;
    }

    public static boolean isGuiInitialized() {
        return gui != null;
    }

    public static void quit() {

        if (isGuiInitialized()) {
            getGui().quit();
        }

        // close db
        Db.shutdown();

        TempDir.removePath();

        log.info(Const.NAME_JGAMEBASE + " finished.");
        System.exit(0); // quit program
    }

}
