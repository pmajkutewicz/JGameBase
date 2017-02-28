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

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

import jgamebase.Const;
import jgamebase.tools.FileTools;

public class JScalingImage extends JLabel implements flushable {

  // don't know why this is necessary...
  // perhaps an inset or a border?
  static final int widthSubtraction = 4;

  private static final long serialVersionUID = 1L;

  String filename = "";
  String tooltip = "";
  Image image = null;
  SwingWorker<Image, Void> worker;

  boolean isLoading = false;

  float aspect = (float) 0.75;

  int width;

  // constructor already supplying an image
  public JScalingImage(final Icon icon) {
    super(icon);
    setImage(((ImageIcon) icon).getImage());
  }

  // constructor for loading an image with tooltip
  public JScalingImage(final String filename, final String tooltip) throws Exception {
    super("Loading...", Const.ICON_LOADING, CENTER);

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

    // set busy cursor
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    this.filename = filename;
    this.tooltip = tooltip;
    setToolTipText(tooltip);
    setAlignmentX(Component.LEFT_ALIGNMENT);
  }

  protected Image getImage() {
    return image;
  }

  protected void setImage(final Image image) {
    this.image = image;
    loadedImage();
  }

  public void loadImage() {
    if ((image != null) || isLoading) {
      return;
    }

    isLoading = true;

    // log.info("Loading image '" + filename+ "'.");

    worker = new SwingWorker<Image, Void>() {
      @Override
      protected Image doInBackground() throws Exception {
        return ImageIO.read(new File(filename));
      }

      @Override
      protected void done() {
        try {
          image = get();
          if (image != null) {
            // everything ok
            loadedImage();
          } else {
            setText("Error loading image!");
            setToolTipText(filename);
            setIcon(Const.ICON_LOADERROR);
          }
        } catch (final ExecutionException ignore) {
        } catch (final InterruptedException ignore) {
        } finally {
          setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
      }

    };
    worker.execute();
    worker = null;
  }

  protected void loadedImage() {
    isLoading = false;
    setText(""); // remove "Loading" text
    aspect = ((float) image.getHeight(null)) / ((float) image.getWidth(null));
    scaleImage();
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
    // scale to parents parent because the parent (Panel)
    // grows with the Image
    if (getParent().getParent() != null) {
      setWidth(getParent().getParent().getWidth());
    }

    if ((image == null) && !isLoading) {
      loadImage();
    }

    super.paint(g);
  }

  @Override
  public int getWidth() {
    // if we have no image yet, return the width
    // of the parent (how wide the image should be)
    if (image == null) {
      return super.getWidth();
    }
    return width;
  }

  protected void scaleImage() {
    if ((image == null) || ((getWidth() - widthSubtraction) <= 0) || (getHeight() <= 0)) {
      return;
    }

    if ((image.getWidth(null) * image.getHeight(null)) <= (640 * 480)) {
      // if image is small, do smooth scale
      setIcon(new ImageIcon(image.getScaledInstance(getWidth() - widthSubtraction, getHeight(),
          java.awt.Image.SCALE_SMOOTH)));
    } else {
      // if image is large, scale fast and do a smooth scale in background
      setIcon(new ImageIcon(image.getScaledInstance(getWidth() - widthSubtraction, getHeight(),
          java.awt.Image.SCALE_FAST)));

      worker = new SwingWorker<Image, Void>() {
        @Override
        protected Image doInBackground() throws Exception {
          return image.getScaledInstance(getWidth() - widthSubtraction, getHeight(),
              java.awt.Image.SCALE_SMOOTH);
        }

        @Override
        protected void done() {
          try {
            setIcon(new ImageIcon(get()));
          } catch (final Exception ignore) {
          }
        }
      };
      worker.execute();
    }
  }

  @Override
  public synchronized void flush() {
    // stop worker thread
    if (worker != null) {
      worker.cancel(true);
      worker = null;
    }

    // flush image
    if (image != null) {
      image.flush();
      image = null;
    }

    // flush ImageIcon of JLabel
    if (getIcon() instanceof ImageIcon) {
      ((ImageIcon) getIcon()).getImage().flush();
    }

    // clear icon
    setIcon(null);
  }

}
