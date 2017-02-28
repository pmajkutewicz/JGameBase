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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import jgamebase.gui.Gui;
import jgamebase.model.Preferences;
import jgamebase.tools.StringTools;

public class JZoomScalingImage extends JScalingImage implements flushable {

  final static float screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
  final static float screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

  String filename = null;
  int initialWidth;

  final JFrame frame = new JFrame();
  final JScrollPane scrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
  JScalingImage scalingImage = null;

  private static final int heightAddition = 32; // window decoration ?

  // constructor for loading an image
  public JZoomScalingImage(final String filename, final String tooltip) throws Exception {
    super(filename, tooltip);

    initFrame();

    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(final MouseEvent e) {
        // no image => return
        if (image == null) {
          return;
        }

        // no scalingImage (but an image, or we wouldn't be here) => create one
        if (scalingImage == null) {
          try {
            scalingImage = new JScalingImage(new ImageIcon(image));
            scrollPane.setViewportView(scalingImage);
            frame.getContentPane().add(scrollPane, CENTER);
          } catch (final Exception e1) {
            e1.printStackTrace();
          }
        }

        // calculate scaled width
        final Dimension d = calculateScaledSize(image);
        setSizes(d);

        // position frame
        frame.setLocationRelativeTo(null);

        final String externalViewerCommand = Preferences.get(Preferences.PICTURE_OPEN_COMMAND);
        boolean useInternalViewer = false;

        if (externalViewerCommand.isEmpty() || Gui.rightClick(e)) {
          // no external viewer specified
          useInternalViewer = true;
        } else {
          // try external viewer
          final String[] command = new String[] { externalViewerCommand, filename };
          log.info("Executing viewer: " + Arrays.toString(command));
          try {
            final ProcessBuilder builder = new ProcessBuilder(command);
            builder.start();
          } catch (final IOException ioe) {
            // exception => fall back to internal viewer
            log.info("Warning: error while executing command '" + Arrays.toString(command)
                + "'; using internal viewer.");
            useInternalViewer = true;
          }
        }

        if (useInternalViewer) {
          // show frame
          frame.setVisible(true);
        }
      }
    });
  }

  protected Dimension calculateScaledSize(final Image image) {
    final float width = image.getWidth(null);
    final float height = image.getHeight(null);
    final float aspect = ((float) image.getHeight(null)) / ((float) image.getWidth(null));
    int clickedWidth;

    if ((width / screenWidth) > (height / screenHeight)) {
      clickedWidth = (int) (screenWidth * 0.75);
    } else {
      clickedWidth = (int) ((screenHeight * 0.75 * width) / height);
    }

    return new Dimension(clickedWidth, (int) (clickedWidth * aspect));
  }

  protected void setSizes(final Dimension d) {
    frame.setSize(new Dimension(d.width, d.height + heightAddition));
    scrollPane.setSize(d);
    scalingImage.setSize(d);
  }

  protected void initFrame() {
    frame.setTitle(StringTools.htmlDecode(tooltip));
    frame.setResizable(true);

    scrollPane.setToolTipText("<html><i>Right click</i> to rotate</html>");
    scrollPane.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(final MouseEvent evt) {
        if (Gui.doubleClick(evt)) {
          frame.setVisible(false);
        }

        if (Gui.rightClick(evt)) {
          final int newWidth = scalingImage.getHeight();
          final int newHeight = scalingImage.getWidth();

          final Image newImage = rotate(scalingImage.getImage());
          scalingImage.setImage(newImage);

          Dimension d = new Dimension(newWidth, newHeight);

          if ((newWidth > screenWidth) || (newHeight > screenHeight)) {
            d = calculateScaledSize(newImage);
          }
          setSizes(d);
        }
      }
    });

    // resize listener
    frame.addComponentListener(new java.awt.event.ComponentAdapter() {
      @Override
      public void componentResized(final java.awt.event.ComponentEvent evt) {
        scalingImage.setSize(frame.getWidth(), frame.getHeight() - heightAddition);
      }
    });
  }

  public Image rotate(final Image imageIn) {
    final int width = imageIn.getWidth(null);
    final int height = imageIn.getHeight(null);

    final BufferedImage bufferedimageIn = new BufferedImage(width, height,
        BufferedImage.TYPE_INT_RGB);
    final Graphics g = bufferedimageIn.createGraphics();
    g.drawImage(imageIn, 0, 0, null);
    g.dispose();

    final BufferedImage bufferedimageOut = new BufferedImage(height, width,
        BufferedImage.TYPE_INT_RGB);

    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        bufferedimageOut.setRGB(j, width - 1 - i, bufferedimageIn.getRGB(i, j));
      }
    }

    bufferedimageIn.flush();
    bufferedimageOut.flush();

    return new ImageIcon(bufferedimageOut).getImage();
  }

  @Override
  public synchronized void flush() {
    super.flush();

    // flush scalingImage
    if (scalingImage != null) {
      scalingImage.flush();
    }

    // dispose frame
    if (frame != null) {
      frame.dispose();
    }
  }

}
