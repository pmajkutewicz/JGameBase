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

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import javax.swing.JCheckBox;
import javax.swing.JToggleButton;
import javax.swing.UIManager;

import jgamebase.model.Tristate;

public class JTristateCheckBox extends JCheckBox implements Serializable {

  private static final long serialVersionUID = -6778470489632624429L;

  /**
   * Creates an initially unselected check box button with no text.
   */
  public JTristateCheckBox() {
    this(null, Tristate.UNCHECKED);
  }

  /**
   * Creates an initially unselected check box button with the given text.
   * 
   * @param text
   *          The text of the check box.
   */
  public JTristateCheckBox(final String text) {
    this(text, Tristate.UNCHECKED);
  }

  /**
   * Creates a check box with text, and specifies whether or not it is initially
   * selected.
   * 
   * @param text
   *          The text of the check box.
   * @param state
   *          The initial state
   */
  public JTristateCheckBox(final String text, final Tristate state) {
    super(text);
    setModel(new TristateModel(state));
  }

  /**
   * Set the new state.
   */
  public void setState(final Tristate state) {
    ((TristateModel) model).setState(state);
  }

  /**
   * Return the current state, which is determined by the selection status of
   * the model.
   */
  public Tristate getState() {
    return ((TristateModel) model).getState();
  }

  @Override
  public void paintComponent(final Graphics g) {
    super.paintComponent(g);

    if (((TristateModel) model).getState() == Tristate.UNKNOWN) {
      final String symbol = "?";

      // draw in bold
      g.setFont(g.getFont().deriveFont(Font.BOLD));
      // draw with antialias
      ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
          RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

      // calculate where to draw symbol
      final Rectangle2D symbolBounds = g.getFontMetrics().getStringBounds(symbol, g);
      final int x = (int) (18.0 - (UIManager.getIcon("CheckBox.icon").getIconWidth() / 2.0) - symbolBounds
          .getCenterX());
      final int y = (int) ((getHeight() / 2.0) - symbolBounds.getCenterY());

      // if the check box is not enabled, draw the symbol in gray.
      if (!isEnabled()) {
        g.setColor(UIManager.getDefaults().getColor("controlShadow"));
      }

      // draw the symbol
      g.drawString(String.valueOf(symbol), x, y);
    }
  }

  /** The model for the button */
  private static class TristateModel extends JToggleButton.ToggleButtonModel {

    private static final long serialVersionUID = -3807645824630253906L;

    protected Tristate state;

    public TristateModel(final Tristate state) {
      this.state = state;
    }

    @Override
    public boolean isSelected() {
      return state == Tristate.CHECKED;
    }

    public Tristate getState() {
      return state;
    }

    public void setState(final Tristate state) {
      this.state = state;
      fireStateChanged();
    }

    @Override
    public void setPressed(final boolean pressed) {
      if (pressed) {
        switch (state) {
          case UNCHECKED:
            state = Tristate.CHECKED;
            break;
          case UNKNOWN:
            state = Tristate.UNCHECKED;
            break;
          case CHECKED:
            state = Tristate.UNKNOWN;
            break;
        }
      }

    }

    @Override
    public void setSelected(final boolean selected) {
      if (selected) {
        state = Tristate.CHECKED;
      } else {
        state = Tristate.UNCHECKED;
      }
    }
  }

}