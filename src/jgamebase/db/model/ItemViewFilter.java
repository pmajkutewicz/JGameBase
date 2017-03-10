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

import static jgamebase.Const.log;

import java.util.ArrayList;
import java.util.List;

import jgamebase.Const;
import jgamebase.db.Db;

public class ItemViewFilter implements Cloneable {

  private static final long serialVersionUID = -5314888349437255940L;

  // ClauseTypes
  public static final int CLAUSETYPE_CONTAINSTEXT = 0;

  public static final int CLAUSETYPE_EXISTS = 1;

  public static final int CLAUSETYPE_FILLED = 2;

  public static final int CLAUSETYPE_DBFIELD = 3;

  public static final int CLAUSETYPE_NO = 4;

  // Operators
  public static final int OPERATOR_EQUAL = 0;

  public static final int OPERATOR_NOTEQUAL = 1;

  public static final int OPERATOR_BEFORE = 2;

  public static final int OPERATOR_AFTER = 3;

  public static final int OPERATOR_LESSTHAN = 4;

  public static final int OPERATOR_MORETHAN = 5;

  public static final int OPERATOR_STARTSWITH = 6;

  public static final int OPERATOR_ENDSWITH = 7;

  // Selectors
  public static final int SELECTOR_YESNO = 0;

  public static final int SELECTOR_TEXT = 1;

  public static final int SELECTOR_DB = 2;

  public static final int SELECTOR_NONE = 3;

  public static final int SELECTOR_OTHER = 4;

  // Fields
  private int id;

  private int viewId; // the view this filter belongs to

  private String gameTable;

  private String gameField;

  private int operator;

  private int clauseType;

  private String clauseData;

  private String musicTable;

  private String musicField;

  // Fields not saved to DB
  private String name;

  private int selector;

  // Constructors
  /** default constructor */
  private ItemViewFilter() {
  }

  public ItemViewFilter(final int clauseType, final int operator, final String fieldTable,
      final String fieldName, final String musicFieldTable, final String musicFieldName,
      final String clauseData) {
    this.clauseType = clauseType;
    this.operator = operator;
    gameTable = fieldTable;
    gameField = fieldName;
    musicTable = musicFieldTable;
    musicField = musicFieldName;
    this.clauseData = clauseData;
  }

  // allow the clause data be given as integer
  public ItemViewFilter(final int clauseType, final int operator, final String gameFieldTable,
      final String gameFieldName, final String musicFieldTable, final String musicFieldName,
      final int clauseData) {
    this(clauseType, operator, gameFieldTable, gameFieldName, musicFieldTable, musicFieldName,
        clauseData + "");
  }

  public ItemViewFilter(final String name, final int selector, final int clauseType,
      final int operator, final String gameFieldTable, final String gameFieldName,
      final String musicFieldTable, final String musicFieldName) {
    this(clauseType, operator, gameFieldTable, gameFieldName, musicFieldTable, musicFieldName, "");
    this.name = name;
    this.selector = selector;
  }

  // Property accessors
  private int getId() {
    return id;
  }

  private void setId(final int id) {
    this.id = id;
  }

  public void setViewId(final int viewId) {
    this.viewId = viewId;
  }

  private int getViewId() {
    return viewId;
  }

  public String getGameTable() {
    return gameTable.toUpperCase();
  }

  public void setGameTable(final String fieldTable) {
    gameTable = fieldTable.toUpperCase();
  }

  public String getGameField() {
    return gameField.toUpperCase();
  }

  public void setGameField(final String fieldName) {
    gameField = fieldName.toUpperCase();
  }

  public int getOperator() {
    return operator;
  }

  public void setOperator(final int operator) {
    this.operator = operator;
  }

  public int getClauseType() {
    return clauseType;
  }

  public void setClauseType(final int clauseType) {
    this.clauseType = clauseType;
  }

  public String getClauseData() {
    return clauseData;
  }

  public void setClauseData(final String clauseData) {
    this.clauseData = clauseData;
  }

  public String getMusicTable() {
    return musicTable.toUpperCase();
  }

  public void setMusicTable(final String musicFieldTable) {
    if (musicFieldTable != null) {
      musicTable = musicFieldTable.toUpperCase();
    } else {
      musicTable = "";
    }
  }

  public String getMusicField() {
    return musicField.toUpperCase();
  }

  public void setMusicField(final String musicFieldName) {
    if (musicFieldName != null) {
      musicField = musicFieldName.toUpperCase();
    } else {
      musicField = "";
    }
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return getName();
  }

  public void setSelector(final int selector) {
    this.selector = selector;
  }

  public int getSelector() {
    return selector;
  }

  public List<Selection> getSelections() {
    List<Selection> list = new ArrayList<Selection>();

    switch (getSelector()) {
      case SELECTOR_YESNO:
        list.add(new Selection("Yes", "-1"));
        list.add(new Selection("No", "0"));
        break;

      case SELECTOR_DB:
        list = Db.getSelections(this);
        break;

      case SELECTOR_OTHER:
        final String field = getGameField();
        final int operator = getOperator();

        // YEARS EQUAL OR NOT EQUAL
        switch (field) {
          case "YE_ID":
            list = Selection.createSelections(Const.FORDISPLAY_YEAR);
            break;
          case "YEAR":
          case "YEARVAL":
            if (operator == ItemViewFilter.OPERATOR_AFTER) {
              // YEARS IS AFTER
              for (int i = 10; i < Const.YEAR_NUMBER; i++) {
                list.add(new Selection(Const.FORDISPLAY_YEAR[i], i));
              }
            } else { // YEARS IS BEFORE
              for (int i = 11; i <= Const.YEAR_NUMBER; i++) {
                list.add(new Selection(Const.FORDISPLAY_YEAR[i], i));
              }
            }

            // GENRE EQUAL OR NOT EQUAL
            break;
          case "GE_ID":
            list = Db.getGenreSelections();

            break;
          case "PLAYERSFROM":
            if ((operator == ItemViewFilter.OPERATOR_EQUAL)
              || (operator == ItemViewFilter.OPERATOR_NOTEQUAL)) {
              // PLAYERS EQUAL OR NOT EQUAL
              list.add(new Selection("(Unknown)", -1));
              list.addAll(Selection.createSelections(0, 10));
            } else if (operator == ItemViewFilter.OPERATOR_MORETHAN) {
              // PLAYERS MORE THAN
              list = Selection.createSelections(0, 10);
            }

            break;
          case "PLAYERSTO":
            // PLAYERS LESS THAN
            list = Selection.createSelections(1, 10);

            break;
          case "V_TRAINERS":
            if ((operator == ItemViewFilter.OPERATOR_EQUAL)
              || (operator == ItemViewFilter.OPERATOR_NOTEQUAL)) {
              // TRAINERS EQUAL OR NOT EQUAL
              list.add(new Selection("(Unknown)", -1));
              list.addAll(Selection.createSelections(0, 20));
            } else if (operator == ItemViewFilter.OPERATOR_MORETHAN) {
              // TRAINERS MORE THAN
              list = Selection.createSelections(0, 20);
            } else if (operator == ItemViewFilter.OPERATOR_LESSTHAN) {
              // TRAINERS LESS THAN
              list = Selection.createSelections(1, 21);
            }

            break;
          case "V_LENGTH":
            list = Selection.createSelections(0, 1000);

            break;
          case "V_LENGTHTYPE":
            list = Selection.createSelections(Const.FORDISPLAY_LENGTHTYPE);

            break;
          case "RATING":
            if ((operator == ItemViewFilter.OPERATOR_EQUAL)
              || (operator == ItemViewFilter.OPERATOR_NOTEQUAL)) {
              // RATING EQUAL OR NOT EQUAL
              for (int i = 0; i <= 5; i++) {
                list.add(new Selection(Const.FORDISPLAY_RATING[i], i));
              }
            } else if (operator == ItemViewFilter.OPERATOR_MORETHAN) {
              // RATING MORE THAN
              for (int i = 1; i <= 4; i++) {
                list.add(new Selection(Const.FORDISPLAY_RATING[i], i));
              }
            } else if (operator == ItemViewFilter.OPERATOR_LESSTHAN) {
              // RATING LESS THAN
              for (int i = 2; i <= 5; i++) {
                list.add(new Selection(Const.FORDISPLAY_RATING[i], i));
              }
            }

            break;
          case "CONTROL":
            // CONTROL EQUAL OR NOT EQUAL
            list = Selection.createSelections(Const.FORDISPLAY_CONTROL);

            break;
          case "V_PALNTSC":
            list = Selection.createSelections(Const.FORDISPLAY_PALNTSC);

            break;
          case "VERSION":
            if ((operator == ItemViewFilter.OPERATOR_EQUAL)
              || (operator == ItemViewFilter.OPERATOR_NOTEQUAL)) {
              // VERSION EQUAL OR NOT EQUAL
              list = Selection.createSelections(0, 999);
            } else if (operator == ItemViewFilter.OPERATOR_MORETHAN) {
              // VERSION MORE THAN
              list = Selection.createSelections(0, 998);
            } else if (operator == ItemViewFilter.OPERATOR_LESSTHAN) {
              // VERSION LESS THAN
              list = Selection.createSelections(1, 999);
            }
            break;
        }
        break;
    }
    return list;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public void print() {
    log.info(id + ", " + viewId + ", " + gameTable + ", " + gameField + ", " + operator + ", "
        + clauseType + ", " + clauseData + ", " + musicTable + ", " + musicField);
  }
}
