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

import java.io.Serializable;

public class ConfigId implements Serializable {

  private static final long serialVersionUID = -4001096951289552120L;

  // Fields
  private int majorVersion;

  private int minorVersion;

  private int officialUpdateNumber;

  // Constructors
  /** default constructor */
  public ConfigId() {
  }

  // equals() and hasCode()
  @Override
  public boolean equals(final Object obj) {
    if ((obj == null) || !(obj instanceof ConfigId)) {
      return false;
    }

    final ConfigId ci = (ConfigId) obj;

    return (majorVersion == ci.getMajorVersion()) && (minorVersion == ci.getMinorVersion())
        && (officialUpdateNumber == ci.getOfficialUpdateNumber());
  }

  @Override
  public int hashCode() {
    return majorVersion ^ minorVersion ^ officialUpdateNumber;
  }

  // Property accessors
  /**
     * 
     */
  public int getMajorVersion() {
    return majorVersion;
  }

  public void setMajorVersion(final int majorVersion) {
    this.majorVersion = majorVersion;
  }

  /**
     * 
     */
  public int getMinorVersion() {
    return minorVersion;
  }

  public void setMinorVersion(final int minorVersion) {
    this.minorVersion = minorVersion;
  }

  /**
     * 
     */
  public int getOfficialUpdateNumber() {
    return officialUpdateNumber;
  }

  public void setOfficialUpdateNumber(final int officialUpdateNumber) {
    this.officialUpdateNumber = officialUpdateNumber;
  }
}