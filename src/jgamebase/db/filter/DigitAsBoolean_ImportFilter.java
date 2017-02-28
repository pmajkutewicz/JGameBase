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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.DataType;
import com.healthmarketscience.jackcess.ImportFilter;

public class DigitAsBoolean_ImportFilter implements ImportFilter {

  private final int[] columnsToConvert;

  public DigitAsBoolean_ImportFilter(final int... columnsToConvert) {
    this.columnsToConvert = columnsToConvert;
  }

  @Override
  public List<Column> filterColumns(final List<Column> destColumns,
      final ResultSetMetaData srcColumns) throws SQLException, IOException {

    for (final int element : columnsToConvert) {
      final Column column = destColumns.get(element);
      if (column.getType() != DataType.INT) {
        throw new RuntimeException("Column " + column.getName() + " has data type "
            + column.getType() + " (should be INT)!");
      }
      column.setType(DataType.BOOLEAN);
      destColumns.set(element, column);
    }

    return destColumns;
  }

  @Override
  public Object[] filterRow(final Object[] row) throws SQLException, IOException {

    for (int i = 0; i < columnsToConvert.length; i++) {
      row[columnsToConvert[i]] = (((Integer) row[columnsToConvert[i]]).intValue() == 0) ? Boolean.FALSE
          : Boolean.TRUE;
    }

    return row;
  }

}
