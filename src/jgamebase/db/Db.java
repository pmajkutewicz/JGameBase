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

package jgamebase.db;

import static jgamebase.Const.log;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import jgamebase.Const;
import jgamebase.JGameBase;
import jgamebase.db.model.Cracker;
import jgamebase.db.model.Difficulty;
import jgamebase.db.model.Extra;
import jgamebase.db.model.Game;
import jgamebase.db.model.Genre;
import jgamebase.db.model.Item;
import jgamebase.db.model.ItemView;
import jgamebase.db.model.ItemViewFilter;
import jgamebase.db.model.Language;
import jgamebase.db.model.Music;
import jgamebase.db.model.Musician;
import jgamebase.db.model.Programmer;
import jgamebase.db.model.Publisher;
import jgamebase.db.model.Selection;
import jgamebase.db.model.Year;
import jgamebase.gui.Gui;
import jgamebase.model.Databases;
import jgamebase.model.Preferences;
import jgamebase.tools.FileTools;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class Db {

  // database connection properties, filled with default values
  private static String dbDriver = "org.apache.derby.jdbc.EmbeddedDriver";

  private static String dbUrl = "jdbc:derby:";

  public static final String FALSE = "0"; // normally "FALSE"

  public static final String TRUE = "1"; // normally "TRUE"

  private static SessionFactory sessionFactory = null;

  private static Session session = null;

  protected static final double NEEDED_VERSION = 2.8;

  public static synchronized void init(final String dbName, final boolean createDb) {
    try {
      org.hibernate.cfg.Configuration config;

      if (session != null) {
        close();
      }

      if (createDb) { // create db
        log.info("\nCreating database '" + dbName + "'...");
        // Create the SessionFactory from hibernateImport.cfg.xml
        config = new org.hibernate.cfg.Configuration().configure(new File(Const.GBDIR_RO,
            "hibernateImport.cfg.xml"));

        // Database connection settings
        config.setProperty("hibernate.connection.driver_class", dbDriver);
        config.setProperty("hibernate.default_schema", "APP");

        config.setProperty("hibernate.connection.url", dbUrl
            + new File(new File(Const.GBDIR_RW, dbName), Const.DATABASE_DIRNAME) + ";create=true");
        config.setProperty("hbm2ddl.auto", "create");
      } else { // open db
        log.info("\nOpening database '" + dbName + "'...");
        // Create the SessionFactory from hibernate.cfg.xml
        config = new org.hibernate.cfg.Configuration().configure(new File(Const.GBDIR_RO,
            "hibernate.cfg.xml"));

        // Database connection settings
        config.setProperty("hibernate.connection.driver_class", dbDriver);
        config.setProperty("hibernate.default_schema", "APP");

        config.setProperty("hibernate.connection.url", dbUrl
            + new File(new File(Const.GBDIR_RW, dbName), Const.DATABASE_DIRNAME));
      }

      sessionFactory = config.buildSessionFactory();
      session = sessionFactory.openSession();

      if (!createDb) {

        // reorganize();

        final double version = getVersion();

        if (version == 0.0) {
          log.info("Warning: Could not read database version.");
          Gui.displayWarningDialog("Could not read database version.");
        } else if (version < Db.NEEDED_VERSION) {
          // update database
          log.info("\nFound database in version " + version + ", but need version "
              + NEEDED_VERSION + ": trying to update it...\n");

          // export existing data into .csv files
          Export.db2Csv(getTableNames());

          // shutdown database driver
          shutdown();

          // delete old database directory
          log.info("\nDeleting old database directory '"
              + new File(new File(Const.GBDIR_RW, dbName), Const.DATABASE_DIRNAME) + "'.\n");
          FileTools.deleteAll(new File(new File(Const.GBDIR_RW, dbName), Const.DATABASE_DIRNAME));

          // update .csv files to current database schema
          final boolean errorOccured = Update.updateFrom(Databases.getCurrent().getExportPath(),
              version);
          if (errorOccured) {
            log.info("ERROR: Could not update database to version " + NEEDED_VERSION + ".");
            Gui.displayErrorDialog("Could not update database to version " + NEEDED_VERSION + ".");
            JGameBase.quit();
          }

          // reload database driver
          Class.forName(dbDriver).newInstance();

          // create empty database
          Db.init(dbName, true);

          // import .csv files
          Import.csv2Db(Databases.getCurrent().getExportPath());
          log.info("DB Converted.");

          // close and reopen database
          close();
          Db.init(dbName, false);
        }
      }

      if (!getTableNames().contains("VIEWCOLUMNS")) {
        try {
          log.info("\nTable 'VIEWCOLUMNS' does not exist, trying to create it...");
          createTable_ViewColumns();
          log.info("Table successfully created.\n");
        } catch (final Exception e) {
          log.info("Table could NOT be created!\n");
          e.printStackTrace();
        }
      }

    } catch (final Throwable ex) {
      // Make sure you out the exception, as it might be swallowed
      log.error("Initial SessionFactory or Session creation failed." + ex);
      ex.printStackTrace();
      throw new ExceptionInInitializerError(ex);
    }
  }

  protected static synchronized Session getSession() {
    return session;
  }

  public static double getVersion() {
    double version = 0.0;

    try {
      final ResultSet srs = session.connection().createStatement()
          .executeQuery("SELECT * FROM Config");
      srs.next();
      version = Double.valueOf(srs.getInt("MajorVersion") + "." + srs.getInt("MinorVersion"))
          .doubleValue();
    } catch (final Exception e) {
      e.printStackTrace();
    }

    return version;
  }

  public static List<String> getTableNames() {
    final List<String> tableNames = new ArrayList<String>();
    DatabaseMetaData myMT;

    try {
      myMT = session.connection().getMetaData();
      final ResultSet tables = myMT.getTables(null, null, "%", new String[] { "TABLE" });

      while ((tables != null) && tables.next()) {
        final String dbTableName = tables.getString("TABLE_NAME");
        boolean found = false;

        // add known jGameBase tables in correct case
        for (final String jgbTableName : Table.getNames()) {
          if (dbTableName.equalsIgnoreCase(jgbTableName) && !found) {
            tableNames.add(jgbTableName);
            found = true;
          }
        }

        if (!found) {
          tableNames.add(dbTableName.toUpperCase()); // for table VIEWCOLUMNS
        }

      }
    } catch (final Exception e) {
      e.printStackTrace();
    }

    return tableNames;
  }

  protected static void createTable_ViewColumns() {
    session.beginTransaction();
    executeSql("create table APP.VIEWCOLUMNS (VC_ID integer not null, VW_ID integer, MODELINDEX integer, VIEWINDEX integer, WIDTH integer, FILTER varchar(32) default '' not null, primary key (VC_ID))");
    executeSql("create index VIEWCOLUMNS_VW_ID on APP.VIEWCOLUMNS (VW_ID)");
    executeSql("create index VIEWCOLUMNS_VC_ID on APP.VIEWCOLUMNS (VC_ID)");
    executeSql("alter table APP.VIEWCOLUMNS add constraint FK55249D78475830E5 foreign key (VW_ID) references APP.VIEWDATA");
    session.getTransaction().commit();
  }

  protected static void executeSql(final String sql) {
    try {
      final Statement stmt = session.connection().createStatement();
      stmt.executeUpdate(sql);
      stmt.close();
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  public static synchronized void close() {
    try {
      if (session != null) {
        if (session.getTransaction().isActive()) {
          session.getTransaction().commit();
        }

        if (session.isOpen()) {
          session.close();
        }

        session = null;
      }
    } catch (HibernateException e) {
    }
  }

  public static synchronized void shutdown() {
    close();

    boolean gotSQLExc = false;
    Connection connection = null;

    try {
      connection = DriverManager.getConnection(dbUrl + ";shutdown=true");
    } catch (final SQLException se) {
      if ((se.getSQLState() == null) || ((se.getSQLState() != null) && (se.getSQLState().equals("XJ015")))) {
        gotSQLExc = true;
      }
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (final SQLException e) {
        }
      }
    }

    if (!gotSQLExc) {
      log.info("Database did not shut down normally.");
    }

    session = null;
    sessionFactory = null;
  }

  public static synchronized List<Item> getItems(final ItemView view) {
    List<Item> items = new ArrayList<Item>();

    if ((view.getInclude() == ItemView.INCLUDE_BOTH)
        || (view.getInclude() == ItemView.INCLUDE_GAMES)) {
      final List<Item> gameItems = getItemsFromGametable(view);
      if (gameItems != null) {
        items.addAll(gameItems);
      }
    }
    if ((view.getInclude() == ItemView.INCLUDE_BOTH)
        || (view.getInclude() == ItemView.INCLUDE_MUSIC)) {
      final List<Item> musicItems = getItemsFromMusictable(view);
      if (musicItems != null) {
        items.addAll(musicItems);
      }
    }

    for (final Iterator<Item> iterator = items.iterator(); iterator.hasNext();) {
      final Item item = iterator.next();
      if ((item.getName() == null) || (item.getName().isEmpty())) {
        iterator.remove();
        delete(item);
      }
    }

    // make unique
    items = new ArrayList<Item>(new TreeSet<Item>(items));

    return items;
  }

  @SuppressWarnings("unchecked")
  private static synchronized List<Item> getItemsFromGametable(final ItemView view) {
    List<Item> items = null;

    try {
      items = session.createSQLQuery(buildGameQuery(view)).addEntity(Game.class).list();
    } catch (final NullPointerException npe) {
      // set empty list
      items = new ArrayList<Item>();
    } catch (final Exception e) {
      e.printStackTrace();
    }

    return items;
  }

  @SuppressWarnings("unchecked")
  public static synchronized List<Selection> getSelections(final ItemViewFilter filter) {
    List<Selection> selections = null;

    final String linkField = GetLinkFieldName(filter.getGameField());
    String query = "SELECT " + linkField + " AS NAME, " + filter.getGameField() + " AS VALUE FROM "
        + filter.getGameTable() + " WHERE " + linkField + " <> '' ORDER BY ";
    if (linkField.equals("DIFFICULTY")) {
      query += filter.getGameField();
    } else {
      query += linkField;
    }
    query = query.toUpperCase();

    try {
      selections = session.createSQLQuery(query).addEntity(Selection.class).list();
    } catch (final Exception e) {
      e.printStackTrace();
    }

    return selections;
  }

  // gets the actual field name from the link field name
  // so the filter data list box can be filled correctly
  // when retrieving tables from the database
  private static String GetLinkFieldName(String strLinkField) {

    strLinkField = strLinkField.toUpperCase();

    switch (strLinkField) {
      case "PU_ID":
        return "PUBLISHER";
      case "PR_ID":
        return "PROGRAMMER";
      case "MU_ID":
        return "MUSICIAN";
      case "LA_ID":
        return "LANGUAGE";
      case "GE_ID":
        return "GENRE";
      case "PG_ID":
        return "PARENTGENRE";
      case "CR_ID":
        return "CRACKER";
      case "DI_ID":
        return "DIFFICULTY";
    }

    return "";
  }

  public static synchronized List<Selection> getGenreSelections() {
    final List<Selection> selections = new ArrayList<Selection>();

    String query = "SELECT GENRE, PARENTGENRE, GE_ID FROM PGenres INNER JOIN Genres ON (PGenres.PG_Id = Genres.PG_Id) ORDER BY PGenres.ParentGenre, Genres.Genre";
    query = query.toUpperCase();

    String genre, parentGenre;
    int id;
    ResultSet rs = null;
    Statement statement = null;

    try {
      statement = session.connection().createStatement();
      rs = statement.executeQuery(query);

      while (rs.next()) {
        genre = rs.getString("GENRE") == null ? "" : rs.getString("GENRE");
        parentGenre = rs.getString("PARENTGENRE") == null ? "" : rs.getString("PARENTGENRE");
        id = rs.getInt("GE_ID");

        if (!parentGenre.isEmpty()) {
          selections.add(new Selection(parentGenre + " - " + genre, id));
        } else {
          selections.add(new Selection(genre, id));
        }
      }
    } catch (final Exception e) {
      e.printStackTrace();
    } finally {
      if (statement != null) {
        try {
          statement.close();
        } catch (final SQLException e) {
        }
      }

      if (rs != null) {
        try {
          rs.close();
        } catch (final SQLException e) {
        }
      }
    }

    return selections;
  }

  private static String buildGameQuery(final ItemView view) {
    String query = "SELECT * FROM GAMES ";

    final String whereClause = getWhereClause(view, true).toUpperCase();

    if (whereClause.indexOf("YEARS") != -1) {
      query += "JOIN YEARS ON (GAMES.YE_ID = YEARS.YE_ID) ";
    }

    if (whereClause.indexOf("PUBLISHERS") != -1) {
      query += "JOIN PUBLISHERS ON (GAMES.PU_ID = PUBLISHERS.PU_ID) ";
    }

    if (whereClause.indexOf("GENRES") != -1) {
      query += "JOIN GENRES ON (GAMES.GE_ID = GENRES.GE_ID) ";
      query += "JOIN PGENRES ON (GENRES.PG_ID = PGENRES.PG_ID) ";
    }

    if (whereClause.indexOf("MUSICIANS") != -1) {
      query += "JOIN MUSICIANS ON (GAMES.MU_ID = MUSICIANS.MU_ID) ";
    }

    if (whereClause.indexOf("DIFFICULTY") != -1) {
      query += "JOIN DIFFICULTY ON (GAMES.DI_ID = DIFFICULTY.DI_ID) ";
    }

    if (whereClause.indexOf("LANGUAGES") != -1) {
      query += "JOIN LANGUAGES ON (GAMES.LA_ID = LANGUAGES.LA_ID) ";
    }

    if (whereClause.indexOf("PROGRAMMERS") != -1) {
      query += "JOIN PROGRAMMERS ON (GAMES.PR_ID = PROGRAMMERS.PR_ID) ";
    }

    if (whereClause.indexOf("EXTRAS") != -1) {
      query += "JOIN EXTRAS ON (GAMES.GA_ID = EXTRAS.GA_ID) ";
    }

    if (whereClause.indexOf("CRACKERS") != -1) {
      query += "JOIN CRACKERS ON (GAMES.CR_ID = CRACKERS.CR_ID) ";
    }

    query += whereClause;

    return query;
  }

  @SuppressWarnings("unchecked")
  private static synchronized List<Item> getItemsFromMusictable(final ItemView view) {
    List<Item> items = new ArrayList<Item>();

    // load from database
    try {
      final String musicQuery = buildMusicQuery(view);
      if (musicQuery != null) {
        items.addAll(session.createSQLQuery(musicQuery).addEntity(Music.class).list());
      }
    } catch (final NullPointerException npe) {
      // set empty list
      items = new ArrayList<Item>();
    } catch (final Exception e) {
      e.printStackTrace();
    }

    return items;
  }

  private static String buildMusicQuery(final ItemView view) {
    String query = "SELECT * FROM MUSIC ";
    final String whereClause = getWhereClause(view, false).toUpperCase();

    if (whereClause.equals("WHERE ()")) { // catch empty where clause
      return null;
    }

    if (whereClause.indexOf("MUSICIANS") != -1) {
      query += "JOIN MUSICIANS ON (MUSIC.MU_ID = MUSICIANS.MU_ID) ";
    }

    query += whereClause;

    return query;
  }

  public static synchronized Extra getExtraById(final int id) {
    Extra extra = null;

    try {
      extra = (Extra) session.createQuery("from Extra where EX_ID=" + id).uniqueResult();
    } catch (final Exception e) {
      e.printStackTrace();
    }
    return extra;
  }

  @SuppressWarnings("unchecked")
  public static synchronized List<Extra> getExtrasByGameId(final int id) {
    List<Extra> extras = null;

    try {
      extras = session.createQuery("from Extra where GA_ID=" + id + " ORDER BY DisplayOrder")
          .list();
    } catch (final Exception e) {
      e.printStackTrace();
    }
    return extras;
  }

  @SuppressWarnings("unchecked")
  private static int getExtraIdCountByGameId(final int id) {
    int count = 0; 

    try { 
      Query q = session.createQuery("select count (*) from Extra where GA_ID=:gameId");
      q.setInteger("gameId", id);
      count = ((Long)q.uniqueResult()).intValue();
    } catch (final Exception e) {
    }
    
    return count;
  }

  private static int[] getExtraIdsByGameId(final int id) {
    int[] extraIds = new int[0]; 
    List<Integer> extraIdsList = null;

    try { 
      final Query q = session.createQuery("select id from Extra where GA_ID=:gameId");
      q.setInteger("gameId", id);
      extraIdsList = q.list();
      extraIds = ArrayUtils.toPrimitive(extraIdsList.toArray(new Integer[0])); 
    } catch (final Exception e) {
      e.printStackTrace();
    }
    return extraIds;
  }

  private static synchronized void deleteExtrasbyGameIdExtraId(final int gameId, final int extraId) {
    Query q = session.createQuery("delete from Extra where GA_ID=:gameId AND EX_ID=:extraId");
    q.setInteger("gameId", gameId);
    q.setInteger("extraId", extraId);
    q.executeUpdate();
  }

  public static synchronized Publisher getPublisherById(final int id) {
    Publisher publisher = null;

    try {
      publisher = (Publisher) session.createQuery("from Publisher where PU_ID=" + id)
          .uniqueResult();
    } catch (final Exception e) {
      e.printStackTrace();
    }
    return publisher;
  }

  public static synchronized Programmer getProgrammerById(final int id) {
    Programmer programmer = null;

    try {
      programmer = (Programmer) session.createQuery("from Programmer where PR_ID=" + id)
          .uniqueResult();
    } catch (final Exception e) {
      e.printStackTrace();
    }
    return programmer;
  }

  public static synchronized Musician getMusicianById(final int id) {
    Musician musician = null;

    try {
      musician = (Musician) session.createQuery("from Musician where MU_ID=" + id).uniqueResult();
    } catch (final Exception e) {
      e.printStackTrace();
    }
    return musician;
  }

  public static synchronized Language getLanguageById(final int id) {
    Language language = null;

    try {
      language = (Language) session.createQuery("from Language where LA_ID=" + id).uniqueResult();
    } catch (final Exception e) {
      e.printStackTrace();
    }
    return language;
  }

  public static synchronized Difficulty getDifficultyById(final int id) {
    Difficulty difficulty = null;

    try {
      difficulty = (Difficulty) session.createQuery("from Difficulty where DI_ID=" + id)
          .uniqueResult();
    } catch (final Exception e) {
      e.printStackTrace();
    }
    return difficulty;
  }

  public static synchronized Cracker getCrackerById(final int id) {
    Cracker cracker = null;

    try {
      cracker = (Cracker) session.createQuery("from Cracker where CR_ID=" + id).uniqueResult();
    } catch (final Exception e) {
      e.printStackTrace();
    }
    return cracker;
  }

  public static synchronized Year getYearById(final int id) {
    Year year = null;

    try {
      year = (Year) session.createQuery("from Year where YE_ID=" + id).uniqueResult();
    } catch (final Exception e) {
      e.printStackTrace();
    }
    return year;
  }

  public static synchronized Item getItembyId(final String id) {
    if (id.startsWith("G")) {
      return getGameById(Integer.parseInt(id.substring(1)));
    }
    if (id.startsWith("M")) {
      return getMusicById(Integer.parseInt(id.substring(1)));
    }
    return null;
  }

  public static synchronized Game getGameById(final int id) {
    Game game = null;
    try {
      game = (Game) session.createQuery("from Game where GA_ID=" + id).uniqueResult();
    } catch (final Exception e) {
      e.printStackTrace();
    }
    return game;
  }

  public static synchronized void resetGamesPlayedInformation() {
    try {
      session.createQuery("update Game set timesPlayed=0, dateLastPlayed=''").executeUpdate();
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  public static synchronized int getMaxTimesPlayed() {
    int timesPlayed = 0;
    try {
      
      timesPlayed = ((Integer)session.createQuery("select max(timesPlayed) from Game").uniqueResult()).intValue();
    } catch (final Exception e) {
      e.printStackTrace();
    }
    return timesPlayed;
  }

  public static synchronized Game getGameByTimesPlayed(int timesPlayed) {
    Game game = null;
    try {
      game = (Game) session.createQuery("from Game where timesPlayed=" + timesPlayed).uniqueResult();
    } catch (final Exception e) {
      e.printStackTrace();
    }
    return game;
  }

  public static synchronized Music getMusicById(final int id) {
    Music music = null;

    try {
      music = (Music) session.createQuery("from Music where GA_ID=" + id).uniqueResult();
    } catch (final Exception e) {
      e.printStackTrace();
    }
    return music;
  }

  public static synchronized Genre getGenreById(final int id) {
    Genre genre = null;

    try {
      genre = (Genre) session.createQuery("from Genre where GE_ID=" + id).uniqueResult();
    } catch (final Exception e) {
      e.printStackTrace();
    }
    return genre;
  }

  @SuppressWarnings("unchecked")
  public static synchronized List<Extra> getExtras() {
    List<Extra> extras = null;

    try {
      extras = session.createQuery("from Extra").list();
    } catch (final Exception e) {
      e.printStackTrace();
    }
    return extras;
  }

  @SuppressWarnings("unchecked")
  public static synchronized List<ItemView> getSystemViews() {
    List<ItemView> views = null;

    try {
      views = session.createQuery("from ItemView where name like '<%' order by name").list();
    } catch (final NullPointerException npe) {
      // empty list
    } catch (final Exception e) {
      e.printStackTrace();
    }

    if (views == null) {
      // set empty list
      views = new ArrayList<ItemView>();
    }

    return views;
  }

  @SuppressWarnings("unchecked")
  public static synchronized List<ItemView> getNormalViews() {
    List<ItemView> views = null;

    try {
      views = session.createQuery("from ItemView order by name").list();
    } catch (final NullPointerException npe) {
      // empty list
    } catch (final Exception e) {
      e.printStackTrace();
    }

    if (views == null) {
      // set empty list
      views = new ArrayList<ItemView>();
    }

    for (final ItemView view : views) {
      if (view.getName().startsWith("[")) {
        log.info("Deleting quick-view '" + view.getName() + "' from database.");
        Db.delete(view);
      }
    }

    try {
      views = session.createQuery("from ItemView where name not like '<%' order by name").list();
    } catch (final NullPointerException npe) {
      // empty list
    } catch (final Exception e) {
      e.printStackTrace();
    }

    if (views == null) {
      // set empty list
      views = new ArrayList<ItemView>();
    }

    return views;
  }

  @SuppressWarnings("unchecked")
  public static synchronized Set<ItemViewFilter> getViewFilters(final int id) {
    List<ItemViewFilter> viewFilters = new ArrayList<ItemViewFilter>();

    try {
      viewFilters = session.createQuery("from ViewFilter where ID=" + id).list();
    } catch (final Exception e) {
      e.printStackTrace();
    }
    return new HashSet<ItemViewFilter>(viewFilters);
  }

  // traverses all the filters and formulates the WHERE clause for the query
  private static String getWhereClause(final ItemView view, final boolean isGameTable) {

    // the filter mode string
    String andOr;

    // start the where clause with an opening bracket
    final StringBuffer whereClause = new StringBuffer("WHERE (");

    // no filters specified (always check adult filter)
    if (view.getFilterCount() == 0) {
      // add the adult filter restriction if needed
      if (Preferences.is(Preferences.ADULT_FILTER)) {
        whereClause.append(isGameTable ? "GAMES" : "MUSIC").append(".ADULT = ").append(FALSE).append(" )");
        return whereClause.toString();
      }
      // no filters at all
      return "";
    }

    // filter mode
    if (view.getMode() == ItemView.MODE_AND) {
      andOr = " AND ";
    } else {
      andOr = " OR ";
    }

    // add each filter to a list
    final List<String> list = new ArrayList<String>();
    for (final ItemViewFilter filter : view.getFilters()) {

      // only add a filter if the appropriate table has been specified
      if ((isGameTable && !filter.getGameTable().isEmpty())
          || (!isGameTable && !filter.getMusicTable().isEmpty())) {
        // get the filter
        list.add("(" + GetFilter(filter, isGameTable) + ")");
      }
    }

    // build whereClause, separate with the AND/OR operator
    for (final Iterator<String> iter = list.iterator(); iter.hasNext();) {
      whereClause.append(iter.next());

      // add the filter mode string if more filters to come
      if (iter.hasNext()) {
        whereClause.append(andOr);
      }
    }

    // add the adult filter restriction if needed
    if (Preferences.is(Preferences.ADULT_FILTER)) {
      whereClause.append(" AND (").append(isGameTable ? "GAMES" : "MUSIC").append(".ADULT = ").append(FALSE)
        .append(")");
    }

    // finish the WHERE Clause with a closing bracket
    whereClause.append(")");

    return whereClause.toString();
  }

  // gets the filter text for the SQL query from the filter info
  private static String GetFilter(final ItemViewFilter filter, final boolean isGameTable) {
    String tableDotField; // TableName.FieldName concatenated string
    String fieldName; // The Field Name
    String tableName; // The Table Name
    String clauseData; // The Clause data

    String GetFilter = "";
    final int clauseType = filter.getClauseType();
    final int operator = filter.getOperator();

    // Store for simplicity when building the SQL string
    if (isGameTable) {
      fieldName = filter.getGameField();
      tableName = filter.getGameTable();
    } else {
      fieldName = filter.getMusicField();
      tableName = filter.getMusicTable();
    }

    tableDotField = tableName + "." + fieldName;
    clauseData = filter.getClauseData();
    if (clauseData == null) {
      clauseData = "";
    }

    if (tableDotField.equals("YEARS.YEAR")) {
      tableDotField = "YEARS.YEARVAL"; // Derby uses YEAR as reserved
      // word
    }
    // test which type of filter
    if (clauseType == ItemViewFilter.CLAUSETYPE_CONTAINSTEXT) {

      // CONTAINSTEXT
      if (operator == ItemViewFilter.OPERATOR_EQUAL) {
        if (tableName.equals("GENRES")) {
          GetFilter = "UPPER(" + tableDotField + ") LIKE '%" + replaceWildcards(clauseData)
              + "%' OR UPPER(PGenres.ParentGenre) LIKE '%" + replaceWildcards(clauseData) + "%'";
        } else {
          GetFilter = "UPPER(" + tableDotField + ") LIKE '%" + replaceWildcards(clauseData) + "%'";
        }
      } else if (operator == ItemViewFilter.OPERATOR_NOTEQUAL) {
        if (tableName.equals("GENRES")) {
          GetFilter = "UPPER(" + tableDotField + ") NOT LIKE '%" + replaceWildcards(clauseData)
              + "%' AND UPPER(PGenres.ParentGenre) NOT LIKE '%" + replaceWildcards(clauseData)
              + "%'";
        } else {
          GetFilter = "UPPER(" + tableDotField + ") NOT LIKE '%" + replaceWildcards(clauseData)
              + "%'";
        }
      } else if (operator == ItemViewFilter.OPERATOR_STARTSWITH) {
        if (tableName.equals("GENRES")) {
          GetFilter = "UPPER(" + tableDotField + ") LIKE '" + replaceWildcards(clauseData)
              + "%' OR UPPER(PGenres.ParentGenre) LIKE '" + replaceWildcards(clauseData) + "%'";
        } else {
          GetFilter = "UPPER(" + tableDotField + ") LIKE '" + replaceWildcards(clauseData) + "%'";
        }
      } else {
        // OPERATOR_ENDSWITH
        if (tableName.equals("GENRES")) {
          GetFilter = "UPPER(" + tableDotField + ") LIKE '%" + replaceWildcards(clauseData)
              + "' OR UPPER(PGenres.ParentGenre) LIKE '%" + replaceWildcards(clauseData) + "'";
        } else {
          GetFilter = "UPPER(" + tableDotField + ") LIKE '%" + replaceWildcards(clauseData) + "'";
        }
      }

    } else if (clauseType == ItemViewFilter.CLAUSETYPE_FILLED) {

      // FIELD IS FILLED
      if (operator == ItemViewFilter.OPERATOR_EQUAL) {
        GetFilter = tableDotField + " <> ''";
      } else {
        GetFilter = tableDotField + " = ''";
      }

    } else if (clauseType == ItemViewFilter.CLAUSETYPE_EXISTS) {

      // FILE EXISTS
      if (operator == ItemViewFilter.OPERATOR_EQUAL) {
        GetFilter = tableDotField + " <> " + FALSE;
      } else {
        GetFilter = tableDotField + " = " + FALSE;
      }

    } else if (clauseType == ItemViewFilter.CLAUSETYPE_DBFIELD) {

      // DATABASE FIELD
      if (operator == ItemViewFilter.OPERATOR_EQUAL) {
        switch (fieldName) {
          case "PLAYERSFROM":
            // number of players
            GetFilter = "(" + tableDotField + " = " + clauseData + ") AND (Games.PlayersTo = "
              + clauseData + ")";
            break;
          case "PLAYERSTO":
            // number of players
            GetFilter = "(" + tableDotField + " = " + clauseData + ")";
            break;
          case "PREQUEL":
          case "Sequel":
          case "Related":
            // prequel, sequel and related game
            if (clauseData.equals("-1")) {
              GetFilter = tableDotField + " > 0"; // has
            } else {
              GetFilter = tableDotField + " = 0"; // hasn't
            }
            break;
          case "FA":
          case "SA":
          case "FAV":
          case "SFAV":
          case "EXTRAS":
          case "CLASSIC":
          case "V_LOADINGSCREEN":
          case "V_HIGHSCORESAVER":
          case "V_INCLUDEDDOCS":
          case "V_TRUEDRIVEEMU":
          case "PLAYERSSIM":
          case "ADULT":
            // prequel, sequel and related game
            if (clauseData.equals("-1")) {
              GetFilter = tableDotField + " <> 0"; // is
            } else {
              GetFilter = tableDotField + " = 0"; // isn't
            }
            break;
          default:
            // all else
            GetFilter = tableDotField + " = " + clauseData;
            break;
        }
      } else if (operator == ItemViewFilter.OPERATOR_NOTEQUAL) {
        if (fieldName.equals("PLAYERSFROM")) {
          // number of players
          GetFilter = "NOT (" + tableDotField + " = " + clauseData + ") AND (Games.PlayersTo = "
              + clauseData + ")";
        } else {
          // all else
          GetFilter = "NOT (" + tableDotField + " = " + clauseData + ")";
        }
      } else if (operator == ItemViewFilter.OPERATOR_AFTER) {
        // years only
        GetFilter = "(" + tableDotField + " > " + clauseData + ") AND (" + tableDotField
            + " < 9991)";
      } else if (operator == ItemViewFilter.OPERATOR_BEFORE) {
        // years only
        GetFilter = tableDotField + " < " + clauseData;
      } else if (operator == ItemViewFilter.OPERATOR_LESSTHAN) {
        switch (fieldName) {
          case "PLAYERSTO":
          case "V_Trainers":
            // number of players, trainers
            GetFilter = "(" + tableDotField + " < " + clauseData + ") AND (" + tableDotField
              + " > -1)";
            break;
          case "V_LENGTH":
          case "V_LENGTHTYPE":
            // game length
            GetFilter = "(" + tableDotField + " < " + clauseData + ")";
            break;
          default:
            // rating
            GetFilter = "(" + tableDotField + " < " + clauseData + ") AND (" + tableDotField
              + " > 0)";
            break;
        }
      } else if (operator == ItemViewFilter.OPERATOR_MORETHAN) {
        switch (fieldName) {
          case "PLAYERSFROM":
            // number of players
            GetFilter = "(" + tableDotField + " > " + clauseData + ") OR (PlayersTo > " + clauseData
              + ")";
            break;
          case "V_TRAINERS":
          case "RATING":
            // trainers, rating
            GetFilter = tableDotField + " > " + clauseData;
            break;
          case "V_LENGTH":
          case "V_LENGTHTYPE":
            // game length
            GetFilter = tableDotField + " > " + clauseData;
            break;
        }
      }

    } else {
      // to avoid error
      GetFilter = (isGameTable ? "GAMES" : "MUSIC") + ".NAME <> ''";

    }
    return GetFilter;
  }

  // WildCard Replacer
  private static String replaceWildcards(final String withWildcards) {
    String withoutWildcards;

    withoutWildcards = withWildcards.replace("%", "_");
    withoutWildcards = withoutWildcards.replace("'", "_");

    return withoutWildcards;
  }

  public static synchronized void delete(final Object object) {
    try {
      // delete object
      session.beginTransaction();
      session.delete(object);
      session.getTransaction().commit();
    } catch (final HibernateException e) {
    }
  }

  public static synchronized void deleteAll(final List list) {
    session.beginTransaction();

    for (final Object object : list) {
      session.delete(object);
    }

    session.getTransaction().commit();
    session.flush();
  }

  public static synchronized void saveOrUpdate(final Object object) {
    saveOrUpdate(object, true);
  }
  
  private static synchronized void saveOrUpdate(final Object object, boolean transactional) {
    // don't save quick views
    if (object instanceof ItemView) {
      final ItemView view = (ItemView) object;
      if (view.getType() == ItemView.TYPE_QUICK) {
        return;
      }
    }

    // don't save nameless items
    if (object instanceof Item) {
      final Item item = (Item) object;
      if ((item.getName() == null) || item.getName().isEmpty()) {
        return;
      }
    }

    // don't save extras not belonging to a game
    if (object instanceof Extra) {
      final Extra extra = (Extra) object;
      if (extra.getGameId() < 1) {
        delete(extra);
        return;
      }
    }
    
    if (transactional) {
      session.beginTransaction();

      // only in transactional mode
      // (i.e. not in batch mode)
      
      // delete orphaned extras
      if (object instanceof Game) {
        final Game game = (Game) object;
        game.setExtras(game.getExtras());
     
        if (game.getExtras().size() < getExtraIdCountByGameId(game.getId())) {
          log.info("Removing orphaned extras for game '" + game.getName() + "'...");
          
          int[] extraIdsInGame = new int[game.getExtras().size()];
          for (int i = 0; i < extraIdsInGame.length; i++) {
            extraIdsInGame[i] = game.getExtras().get(i).getId();
          }

          int[] extraIdsInDb = getExtraIdsByGameId(game.getId());

          // find games in db still in game (don't delete these)
          for (int i = 0; i < extraIdsInGame.length; i++) {
            for (int j = 0; j < extraIdsInDb.length; j++) {
              if (extraIdsInGame[i] == extraIdsInDb[j]) {
                // set id to zero, so this extra can't be deleted
                extraIdsInDb[j] = 0;
              }
            }
          }

          for (int j = 0; j < extraIdsInDb.length; j++) {
            // valid id => delete entry in database
            if (extraIdsInDb[j] > 0) {
              log.info(".");
              deleteExtrasbyGameIdExtraId(game.getId(), extraIdsInDb[j]);
            }
          }
        }

      }
      
    }

    // save object
    session.saveOrUpdate(object);

    if (transactional) {
      session.getTransaction().commit();
    }
  }

  public static synchronized void saveOrUpdateAll(final List list) {
    session.beginTransaction();

    for (final Object object : list) {
      saveOrUpdate(object, false);
    }

    session.getTransaction().commit();
    session.flush();
  }

  // reorganize one table
  private static void reorganizeTable(final Session session, final String tableName)
      throws Exception {
    log.info("  Reorganizing table '" + tableName.toUpperCase() + "'.");

    final CallableStatement cs = session.connection().prepareCall(
        "CALL SYSCS_UTIL.SYSCS_COMPRESS_TABLE(?,?,?)");
    cs.setString(1, "APP"); // schema name
    cs.setString(2, tableName.toUpperCase());
    cs.setShort(3, (short) 0);
    cs.execute();
    cs.close();

    session.flush();
  }

  // reorganize all tables
  public static void reorganize() {
    try {
      log.info("Starting reorganization of database.");

      getSession().beginTransaction();

      for (final String tablename : Table.getNames()) {
        reorganizeTable(getSession(), tablename);
      }

      getSession().getTransaction().commit();

      log.info("Reorganization of database successfully finished.");
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  // reorganize table Extras
  public static void reorganizeExtras() {
    try {
      log.info("Starting reorganization of database.");

      getSession().beginTransaction();

      reorganizeTable(getSession(), Table.EXTRAS.getName());

      getSession().getTransaction().commit();

      log.info("Reorganization of database successfully finished.");
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }
}
