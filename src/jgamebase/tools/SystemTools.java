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

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.channels.FileLock;
import java.util.Map;
import java.util.StringTokenizer;

import jgamebase.Const;
import jgamebase.JGameBase;
import jgamebase.model.Preferences;

public class SystemTools {
  public static boolean isOtherInstanceRunning() {

    FileLock fileLock = null;
    try {
      // get file
      // try to lock file
      fileLock = new RandomAccessFile(Const.LOCKFILE, "rw").getChannel().tryLock();
      if (fileLock == null) {
        // could not lock file (other instance has lock)
        log.info("failed to lock file '" + Const.LOCKFILE.getAbsolutePath() + "'.\nExiting...");
        return true;
      }
    } catch (final IOException ioe) {
      ioe.printStackTrace();
    }

    // could lock file, will be freed at end of program
    return false;
  }

  public static boolean isJavaVersionOk() {
    // log.info("Java vendor:" + System.getProperty("java.vendor"));
    // log.info("Java version:" + System.getProperty("java.version"));

    final String vendor = System.getProperty("java.vendor").toLowerCase();
    final String versionString = System.getProperty("java.version");
    int version = 0;

    final StringTokenizer st = new StringTokenizer(versionString, ".");
    while (st.hasMoreTokens()) {
      try {
        final int i = Integer.parseInt(st.nextToken());
        version = (version * 10) + i;
      } catch (final NumberFormatException e) {
      }
    }

    // OpenJDK6 reports "sun", OpenJDK7 "oracle" as vendor
    if (!(vendor.startsWith("sun") || vendor.startsWith("oracle") || vendor.startsWith("apple"))
        || (version < 16)) {
      return false;
    }

    return true;
  }

  private static String[] toCmdArray(final String command) {
    final StringTokenizer st = new StringTokenizer(command);
    final String[] cmdarray = new String[st.countTokens()];

    for (int i = 0; st.hasMoreTokens(); i++) {
      cmdarray[i] = st.nextToken();
    }

    return cmdarray;
  }

  public static void execTempScript(final String cmdInScript) {
    final String scriptName = new File(TempDir.getPath(), "temp.sh").toString();
    final File scriptFile = new File(scriptName);
    PrintWriter out = null;

    // create script
    try {
      out = new PrintWriter(scriptFile);
      out.println("#!/bin/sh");
      out.println(cmdInScript);
    } catch (final IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (out != null) {
          out.close();
        }
      } catch (final Exception e) {
        e.printStackTrace();
      }
    }

    // execute script
    execScript(scriptName);

    // delete script
    scriptFile.delete();
  }

  public static void execScript(final String command) {
    new File(command).setExecutable(true, false);
    exec(toCmdArray(command));
  }

  public static void exec(final String[] command) {
    // start game
    Process process;
    BufferedReader stderr = null;

    try {
      process = Runtime.getRuntime().exec(command, SystemTools.getSystemEnvironment());

      stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
      String line = null;

      while ((line = stderr.readLine()) != null) {
        log.info(line);
      }

      process.waitFor();

    } catch (final IOException | InterruptedException e) {
      e.printStackTrace();
    } finally {
      if (stderr != null) {
        try {
          stderr.close();
        } catch (final IOException e) {
        }
      }
    }

  }

  public static void open(final URI uri) {
    Desktop desktop = null;

    if (Desktop.isDesktopSupported()) {
      desktop = Desktop.getDesktop();
      try {
        desktop.browse(uri);
        return;
      } catch (final IOException e) {
      }
    }

    log.info("Opening URI '" + uri + "' with OS command '"
        + Preferences.get(Preferences.DOCUMENT_OPEN_COMMAND) + "'.");
    exec(new String[] { Preferences.get(Preferences.DOCUMENT_OPEN_COMMAND), uri.toString() });
  }

  // public static void open(final String open) {
  // try {
  // URI uri = new URI(open);
  // open(uri);
  // } catch (URISyntaxException e) {
  // e.printStackTrace();
  // // there was a problem creating the URI => try to open by executing the
  // specified command
  // log.info("Now trying to execute '" + new
  // String(Preferences.get(Preferences.DOCUMENT_OPEN_COMMAND) + " " +
  // open).trim() +"':");
  // exec(new String[] { Preferences.get(Preferences.DOCUMENT_OPEN_COMMAND),
  // open });
  // }
  // }

  public static void handleFatalError(final Throwable e) {
    String message = "A fatal error occured:\n" + e
        + ".\n\nThe program will write debug information and exit.\n\n";

    if (SystemTools.isJavaVersionOk()) {
      message += "Please try to reproduce the error and find out which specific action triggers it.\n"
          + "Then check the bug tracking system at \n  '"
          + Const.URI_BUGTRACKING
          + "'\n"
          + "to see if the error has already been reported.\n"
          + "If you found a new error please enter a bug report.";
    } else {
      message += "You are not using the recommended Java environment 'SUN Java >= 1.6'.\n"
          + "Please use the correct Java environment and see if the error still exists.";
    }
    message += "\n\nThanks in advance.";

    log.error("\n\n");
    JGameBase.displayError(message);
    log.error("*** option_dangerous INFORMATION START ***");
    log.error("OS:      " + System.getProperty("os.name") + " on " + System.getProperty("os.arch"));
    log.error("JAVA:    " + System.getProperty("java.vendor") + " Version "
        + System.getProperty("java.version") + " Class-Version "
        + System.getProperty("java.class.version"));
    log.error("PROGRAM: " + Const.JGAMEBASE_VERSION);
    log.error("ERROR: " + e); // print exception
    final StackTraceElement[] trace = e.getStackTrace();
    for (final StackTraceElement element : trace) {
      log.error("\tat " + element); // print
      // stack trace
    }
    log.error("*** option_dangerous INFORMATION END ***");
    log.error("");

    log.error("");
    log.error("Exiting...");
  }

  // gets the system environment and returns an array containing all variables
  public static String[] getSystemEnvironment() {
    // get system environment
    final Map<String, String> sysEnv = System.getenv();

    // reserve place for variables of system and for own variables
    final String[] env = new String[sysEnv.size()];

    int i = 0;
    // add system environment variables
    for (final Map.Entry<String, String> entry: sysEnv.entrySet()) {
      env[i++] = entry.getKey() + "=" + entry.getValue();
    }

    return env;
  }

}
