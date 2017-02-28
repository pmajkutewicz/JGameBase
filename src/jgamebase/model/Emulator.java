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

public class Emulator {

  private String name;

  private FileExtensions supportedExtensions = new FileExtensions();

  private String command;

  private String configurationFilename;

  public String getName() {
    return name == null ? "" : name;
  }

  public void setName(final String name) {
    this.name = name == null ? "" : name;
  }

  // supported extensions are get and set as String, but internally stored as
  // a List.
  public FileExtensions getSupportedExtensions() {
    return supportedExtensions;
  }

  public void setSupportedExtensions(final FileExtensions supportedExtensions) {
    this.supportedExtensions = supportedExtensions;
  }

  public String getCommand() {
    return command == null ? "" : command;
  }

  public void setCommand(final String command) {
    this.command = command == null ? "" : command;
  }

  public String getConfigurationFilename() {
    return configurationFilename == null ? "" : configurationFilename;
  }

  public void setConfigurationFilename(final String configurationFilename) {
    this.configurationFilename = configurationFilename == null ? "" : configurationFilename;
  }

  @Override
  public String toString() {
    return getName();
  }
}
