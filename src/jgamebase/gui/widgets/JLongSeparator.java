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
import java.awt.Dimension;

import javax.swing.JSeparator;

public class JLongSeparator extends JSeparator {

  private static final long serialVersionUID = 1L;

  public JLongSeparator() {
    super();
    setMaximumSize(new Dimension(Integer.MAX_VALUE, 5));
    setAlignmentX(Component.LEFT_ALIGNMENT);
  }
}
