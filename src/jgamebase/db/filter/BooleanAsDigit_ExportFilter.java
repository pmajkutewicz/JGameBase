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

package jgamebase.db.filter;

import java.io.IOException;
import java.util.List;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.DataType;
import com.healthmarketscience.jackcess.ExportFilter;

/**
 * Implementation of ImportFilter which exports boolean <code>true</code> as 1
 * and <code>false</code> as 0.
 * 
 * @author Frank Gerbig
 */
public class BooleanAsDigit_ExportFilter implements ExportFilter {

  protected List<Column> columns;

  public BooleanAsDigit_ExportFilter() {
  }

  @Override
  public List<Column> filterColumns(final List<Column> columns) throws IOException {
    this.columns = columns;
    return columns;
  }

  @Override
  public Object[] filterRow(final Object[] row) throws IOException {
    final Object[] newRow = new Object[row.length];

    for (int i = 0; i < row.length; i++) {
      if (columns.get(i).getType() == DataType.BOOLEAN) {
        newRow[i] = (((Boolean) row[i]).booleanValue()) ? "1" : "0";
      } else {
        newRow[i] = row[i];
      }
    }

    return newRow;
  }

}
