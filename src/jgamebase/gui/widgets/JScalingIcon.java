/*
 * Copyright (C) 2006-2011 F. Gerbig (fgerbig@users.sourceforge.net)
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

package jgamebase.gui.widgets;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class JScalingIcon extends JLabel {

  // don't know why this is necessary...
  // perhaps an inset or a border?
  private static final int widthSubtraction = 4;

  private static final long serialVersionUID = 1L;

  Image image = null;
  float aspect = (float) 0.75;
  int width;

  // constructor already supplying an image
  public JScalingIcon(final Icon icon) {
    // super(icon);
    image = ((ImageIcon) icon).getImage();
    aspect = ((float) image.getHeight(null)) / ((float) image.getWidth(null));
    setWidth(icon.getIconWidth());
    scaleImage();
  }

  protected void scaleImage() {
    if (((getWidth() - widthSubtraction) <= 0) || (getHeight() <= 0)) {
      return;
    }

    setIcon(new ImageIcon(image.getScaledInstance(getWidth() - widthSubtraction, getHeight(),
        java.awt.Image.SCALE_SMOOTH)));
  }

  @Override
  public void setSize(final int width, final int height) {
    setWidth(width);
  }

  @Override
  public int getHeight() {
    if (image == null) {
      return super.getHeight();
    }
    return (int) (width * aspect);
  }

  public void setWidth(final int newWidth) {
    // if width has changed, scale image
    if (newWidth != width) {
      width = newWidth;
      super.setSize(width, getHeight());
      scaleImage();
    }
  }

  @Override
  public void paint(final Graphics g) {
    // scale to parent parent because the parent (Panel)
    // grows with the Image
    if (getParent().getParent() != null) {
      setWidth(getParent().getParent().getWidth());
    }
    super.paint(g);
  }

}
