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

package jgamebase.gui.widgets;

import static jgamebase.Const.log;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import jgamebase.gui.Gui;
import jgamebase.model.Preferences;
import jgamebase.tools.FileTools;

public class JScalingAnimation extends JComponent {

  private static final long serialVersionUID = 1L;

  Image image;
  float aspect;
  int width = -1;

  // constructor for loading an animation with tooltip
  public JScalingAnimation(final String filename, final String tooltip) throws Exception {
    super();

    // sanity checks
    if (filename == null) {
      throw new NullPointerException("Filename must not be null!");
    }
    if (filename.isEmpty()) {
      throw new IllegalArgumentException("Filename must not be empty!");
    }
    if (!new File(filename).exists()) {
      throw new FileNotFoundException(filename);
    }
    final String ext = FileTools.getExtension(filename);
    if ((ext == null) || ext.isEmpty()) {
      throw new IOException("Could not determine extension of filename '" + filename + "'");
    }

    image = new ImageIcon(filename).getImage();
    aspect = ((float) image.getHeight(this)) / ((float) image.getWidth(this));
    setToolTipText(tooltip);
    setAlignmentX(Component.LEFT_ALIGNMENT);

    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(final MouseEvent e) {
        // no image => return
        if (image == null) {
          return;
        }

        final String externalViewerCommand = Preferences.get(Preferences.PICTURE_OPEN_COMMAND);
        boolean cantUseInternalViewer = false;

        if (externalViewerCommand.isEmpty()) {
          // no external viewer specified
          cantUseInternalViewer = true;
        } else {
          // try external viewer
          final String[] command = new String[] { externalViewerCommand, filename };
          log.info("Executing viewer: " + Arrays.toString(command));
          try {
            final ProcessBuilder builder = new ProcessBuilder(command);
            builder.start();
          } catch (final IOException ioe) {
            // exception => fall back to internal viewer
            log.info("Warning: error while executing command '" + Arrays.toString(command) + "'.");
            cantUseInternalViewer = true;
          }
        }

        if (cantUseInternalViewer) {
          Gui.displayWarningDialog("Could not open picture viewer '" + externalViewerCommand
              + "'.\n" + "Please configure the picture viewer (Menu 'Tools => Options...').");
        }
      }
    });

  }

  @Override
  public boolean imageUpdate(final Image img, final int infoflags, final int x, final int y,
      final int w, final int h) {
    calculateWidth();
    return super.imageUpdate(img, infoflags, x, y, w, h);
  }

  @Override
  public void paint(final Graphics g) {
    calculateWidth();
    revalidate();
    g.drawImage(image, 0, 0, width, (int) (width * aspect), this);
  }

  private void calculateWidth() {
    if ((getParent() != null) && (getParent().getParent() != null)
        && (getParent().getParent().getWidth() != width)) {
      width = getParent().getParent().getWidth();
      final Dimension d = new Dimension(width, (int) (width * aspect));
      setSize(d);
      setPreferredSize(d);
      setMinimumSize(d);
      setMaximumSize(d);
    }
  }

}
