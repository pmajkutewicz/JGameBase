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

import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import jgamebase.Const;
import jgamebase.model.Paths;

public class Musician {

  private static final long serialVersionUID = 185610471335721469L;

  public static final int NEUTRAL_ID = 38;

  // public static final int NEUTRAL_ID_NONE = 39;

  // Fields
  private int id;

  private String name = "";

  private String group = "";

  private String nickname = "";

  private String photoFilename = "";

  // Constructors
  /** default constructor */
  public Musician() {
  }

  /** constructor with id */
  public Musician(final int id) {
    this.id = id;
  }

  // Property accessors
  public int getId() {
    return id;
  }

  public void setId(final int id) {
    this.id = id;
  }

  public String getName() {
    return name == null ? "" : name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getNameForDisplay() {
    return getName().isEmpty() ? "None" : getName();
  }

  public String getGroup() {
    return group == null ? "" : group;
  }

  public void setGroup(final String group) {
    this.group = group;
  }

  public String getGroupForDisplay() {
    return getGroup().isEmpty() ? "None" : getGroup();
  }

  public String getNickname() {
    return nickname == null ? "" : nickname;
  }

  public void setNickname(final String nickname) {
    this.nickname = nickname;
  }

  public String getNicknameForDisplay() {
    return getNickname().isEmpty() ? "None" : getNickname();
  }

  public String getPhotoFilename() {
    return photoFilename == null ? "" : photoFilename;
  }

  public void setPhotoFilename(final String picture) {
    photoFilename = picture;
  }

  public String getPhotoFilenameForDisplay() {
    return getPhotoFilename().isEmpty() ? "None" : getPhotoFilename();
  }

  public Icon getPhoto() {
    // if a filename for the photo is in the database
    if ((getPhotoFilename() != null) && (!getPhotoFilename().isEmpty())) {
      // search for file
      final File photoFile = Paths.getPhotoPath().find(new File(getPhotoFilename()));
      // if the photo file was found and exists
      if ((photoFile != null) && (photoFile.exists())) {
        // try to load the photo
        final Icon photo = new ImageIcon(photoFile.getAbsolutePath());
        // if the photo was loaded
        if ((photo.getIconWidth() > 0) && (photo.getIconHeight() > 0)) {
          return photo; // return the photo
        }
      }
    }
    return Const.ICON_NOPHOTO; // return the default photo
  }
}