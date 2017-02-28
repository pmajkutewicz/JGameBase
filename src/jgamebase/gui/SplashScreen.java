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

package jgamebase.gui;

import static jgamebase.Const.log;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.io.File;

import javax.imageio.ImageIO;

import jgamebase.Const;

public class SplashScreen extends javax.swing.JFrame {

  private static final long serialVersionUID = 4112240114220766904L;

  private transient Image background;

  private transient Image splash;

  public SplashScreen(final String filename) {

    if (!new File(filename).exists()) {
      log.warn("Splashscreen '" + filename + "' not found.");
      dispose();
      return;
    }

    try {
      setUndecorated(true);
      setTitle("[powered by jGameBase V" + Const.VERSION + "]");
      setIconImage(Const.IMAGE_JGAMEBASE);

      splash = ImageIO.read(new File(filename));
      final int w = splash.getWidth(null);
      final int h = splash.getHeight(null);
      setSize(w, h);

      final int x = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() - w) / 2;
      final int y = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - h) / 2;
      setLocation(x, y);

      if ((x >= 0) && (y >= 0) && (w > 0) && (h > 0)) {
        background = new Robot().createScreenCapture(new Rectangle(x, y, w, h));
      }
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void paint(final Graphics g) {
    if ((splash != null) && (background != null)) {
      background.getGraphics().drawImage(splash, 0, 0, this);
      g.drawImage(background, 0, 0, this);
    }
  }
}
