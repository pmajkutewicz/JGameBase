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

public class Configuration {

  private static final long serialVersionUID = 3120880442393315955L;

  // Fields
  private ConfigId id;

  private String firstloadMessage;

  private int firstloadGemusAsk;

  private String databaseName;

  private String windowTitle;

  // Constructors
  /** default constructor */
  public Configuration() {
  }

  /** constructor with id */
  public Configuration(final ConfigId id) {
    this.id = id;
  }

  // Property accessors
  /**
     * 
     */
  public ConfigId getId() {
    return id;
  }

  public void setId(final ConfigId id) {
    this.id = id;
  }

  /**
     * 
     */
  public String getFirstloadMessage() {
    return firstloadMessage;
  }

  public void setFirstloadMessage(final String firstloadMessage) {
    this.firstloadMessage = firstloadMessage;
  }

  public int getFirstloadGemusAsk() {
    return firstloadGemusAsk;
  }

  public void setFirstloadGemusAsk(final int firstloadGemusAsk) {
    this.firstloadGemusAsk = firstloadGemusAsk;
  }

  public String getDatabaseName() {
    return databaseName;
  }

  public void setDatabaseName(final String databaseName) {
    this.databaseName = databaseName;
  }

  public String getWindowTitle() {
    return windowTitle;
  }

  public void setWindowTitle(final String windowTitle) {
    this.windowTitle = windowTitle;
  }
}