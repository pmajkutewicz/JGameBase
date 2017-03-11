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

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import jgamebase.Const;

public class GemusToJs {

  public static void gemusToJs(final File srcFile) {
    try {
      System.out.println("GemusToJavaScript: Converting file '" + srcFile.getAbsolutePath()
          + "'...");
      final String gemusScript = FileTools.readFileAsString(srcFile.getAbsolutePath());
      final String jsScript = gemusToJs(gemusScript);

      final File dstFile = new File(FileTools.removeExtension(srcFile.getAbsolutePath()) + ".js");
      System.out.println("                   Writing to file '" + dstFile.getAbsolutePath() + "'.");
      FileTools.writeStringAsFile(dstFile, jsScript);
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  protected static String gemusToJs(final String stringIn) throws IOException {
    StringBuilder stringOut = new StringBuilder();
    final Scanner scanner = new Scanner(stringIn);
    scanner.useDelimiter(Const.LINE_SEPARATOR_PATTERN);
    while (scanner.hasNext()) {
      String s = scanner.next() + System.getProperty("line.separator");
      // comment
      s = s.replaceAll(";", "// ");

      // parameters
      s = s.replaceAll("[|]{2}", "\", \"");

      // parameters
      s = s.replaceAll(" CONTAINS\\(", ".contains\\(");

      // End If
      s = s.replaceAll("End If", "}");

      // ElseIf
      s = s.replaceAll("ElseIf", "} else If");

      // if
      s = s.replaceAll("If[\\s](.*)", "if ($1) {");

      // Else
      s = s.replaceAll("Else", "} else { ");

      // semicolon after closing )
      s = s.replaceAll("\\)[\\s]*$", ");\n");

      // value
      s = s.replaceAll("%([^_]*)_value%", "Value(\"$1\")");

      // value
      s = s.replaceAll("Key_([^\\s\\.]*)", "Value(\"$1\")");

      s = s.replaceAll("Set_CFG_Value\\(1", "Set_CFG_Value(emulatorConfigFile");
      s = s.replaceAll("Set_CFG_Item\\(1", "Set_CFG_Item(emulatorConfigFile");
      s = s.replaceAll("Set_INI_Value\\(1", "Set_INI_Value(emulatorConfigFile");

      s = s.replaceAll("%crlfx2%", "\\\\n\\\\n");
      s = s.replaceAll("%crlf%", "\\\\n");
      s = s.replaceAll("%tab%", "\\\\t");

      s = s.replaceAll("\\(([^\\(\\)]*)\\)", "(\"$1\")");

      s = s.replaceAll("\\(\"\"", "(\"");
      s = s.replaceAll("\"\"\\)", "\")");
      s = s.replaceAll("\"\"", "");
      s = s.replaceAll("\\(\"\\)", "()");

      s = s.replaceAll("(%[^\\s]*%)\"", "$1");

      s = s.replaceAll("Add_CLP2\\(", "Add_CLP(");

      s = s.replaceAll("\"emulatorConfigFile\"", "emulatorConfigFile");

      s = s.replaceAll("%dbpath%", "dbPath");
      s = s.replaceAll("%emupath%", "emulatorPath");
      s = s.replaceAll("%gbgamepath%", "workPath");

      s = s.replaceAll("GameType", "itemType");

      s = s.replaceAll("%gamefilepath%", "itemPathAndFile");
      s = s.replaceAll("%gamepathfile%", "itemPathAndFile");
      s = s.replaceAll("%gamefile%", "itemFile");
      s = s.replaceAll("%gamepath%", "itemPath");
      s = s.replaceAll("%gamefilenoext%", "itemFileNoExt");

      s = s.replaceAll("%gamefilepath\\(", "itemPathsAndFiles[");
      s = s.replaceAll("%gamepathfile\\(", "itemPathsAndFiles[");
      s = s.replaceAll("%gamefile\\(", "itemFiles[");
      s = s.replaceAll("%gamepath\\(", "itemPaths[");
      s = s.replaceAll("%gamefilenoext\\(", "itemFilesNoExt[");
      s = s.replaceAll("%numfiles%", "itemFiles.length");

      s = s.replaceAll("\\)%", "]");

      s = s.replaceAll("%imageindex%", "imageIndex");
      s = s.replaceAll("%imagename%", "imageName");
      s = s.replaceAll("%c64imagename%", "imageNameNative");

      s = s.replaceAll("Control", "itemControl");
      s = s.replaceAll("NumPlayers", "itemPlayersMin");
      s = s.replaceAll("PalNTSC", "itemPalNtsc");
      s = s.replaceAll("TrueDriveEmu", "isRunsOnTrueDrive");
      s = s.replaceAll("GameComment", "itemComment");
      s = s.replaceAll("VersionComment", "itemVersionComment");
      s = s.replaceAll("ImageName", "imageName");
      s = s.replaceAll("NumFiles", "gameFiles.length");
      s = s.replaceAll("NumGameFiles", "gameFiles.length");
      s = s.replaceAll("QUESTION()", "Question()");

      s = s.replaceAll("JoyPort2", "JOYPORT2");
      s = s.replaceAll("JoyPort1", "JOYPORT1");
      s = s.replaceAll("Keyboard", "KEYBOARD");
      s = s.replaceAll("PaddlePort2", "PADDLEPORT2");
      s = s.replaceAll("PaddlePort1", "PADDLEPORT1");
      s = s.replaceAll("Mouse", "MOUSE");
      s = s.replaceAll("LightPen", "LIGHTPEN");
      s = s.replaceAll("KoalaPad", "KOALAPAD");
      s = s.replaceAll("LightGun", "LIGHTGUN");
      stringOut.append(s);
    }

    return stringOut.toString();
  }

}
