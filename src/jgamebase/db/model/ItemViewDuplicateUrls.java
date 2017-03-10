package jgamebase.db.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import jgamebase.db.Db;

public class ItemViewDuplicateUrls extends ItemView {

  @Override
  public synchronized List<Item> getData() {

    if (!isLoaded()) {
      data = new ArrayList<Item>();

      final List<Extra> allExtras = Db.getExtras();

      // find all extras that are urls
      final List<Extra> urlExtras = new CopyOnWriteArrayList<Extra>();
      for (final Extra extra : allExtras) {
        if (extra.isUrl()
            && !extra.getFilename().toLowerCase().startsWith("http://www.gamebase64.com")) {
          // if not yet added, do so
          if (!urlExtras.contains(extra)) {
            urlExtras.add(extra);
          }
        }
      }

      for (final Extra extra : urlExtras) {
        final Game game = Db.getGameById(extra.getGameId());
        if (!data.contains(game)) {
          data.add(game);
        }
      }

      //
      // // remove extras with specific languages
      // for(Extra extra : urlExtras) {
      // Game game = Db.getGameById(extra.getGameId());
      // Language language = game.getLanguage();
      //
      // switch (language.getId()) {
      // case 1: // (Unknown)
      // case 4: // French
      // case 5: // Italian
      // case 6: // Spanish
      // case 7: // Dutch
      // case 8: // Hungarian
      // case 10: // Finnish
      // case 13: // Polish
      // case 15: // Greek
      // case 16: // Serbo-Croatian
      // case 17: // Danish
      // case 21: // Swedish
      // case 26: // Turkish
      // case 27: // Czech
      // case 28: // Slovenian
      // case 31: // Norwegian
      // case 32: // Slovakian
      // case 34: // Iranian
      // case 35: // Croatian
      // case 36: // Russian
      // System.err.println("Remove URL: " + game.getName() + " - " +
      // language.getName() + " : " + extra.getFilename());
      // // remove
      // extra.setGameId(0);
      // game.getExtras().remove(extra);
      // Db.saveOrUpdate(game);
      // Db.delete(extra);
      // urlExtras.remove(extra);
      // break;
      //
      // }
      // }
      //
      // // remove extras where the game was not published
      // for(Extra extra : urlExtras) {
      // Game game = Db.getGameById(extra.getGameId());
      // Publisher publisher = game.getPublisher();
      //
      // switch (publisher.getId()) {
      // case 37: //(Unknown)
      // case 39: //(Not Published)
      // case 623: //(Created with GKGM)
      // case 626: //(Created with SEUCK)
      // case 883: //(Preview)
      // case 1889: //(Public Domain)
      // case 1998: //(Created with PCS)
      // case 2032: //(Created with 3DCK)
      // System.err.println("Remove URL: " + game.getName() + " - " +
      // publisher.getName() + " : " + extra.getFilename());
      // // remove
      // extra.setGameId(0);
      // game.getExtras().remove(extra);
      // Db.saveOrUpdate(game);
      // Db.delete(extra);
      // urlExtras.remove(extra);
      // break;
      //
      // }
      // }
      //
      // // find urls used in more than one game
      // MultiValueMap urlsToGames = new MultiValueMap();
      // for(Extra extra : urlExtras) {
      // urlsToGames.put(extra.getFilename(),
      // Db.getGameById(extra.getGameId()));
      // }
      //
      // List<Item> doubleUrlGames = new ArrayList<Item>();
      //
      // for(Extra extra : urlExtras) {
      // String url = extra.getFilename();
      // List<Game> games = (List<Game>) urlsToGames.getCollection(url);
      // if (games.size() > 1) {
      // // iterate over all games
      // for (Game game1 : games) {
      // for (Game game2 : games) {
      // // don't compare game with itself
      // if (game1.getId() != game2.getId()) {
      // // compare name and publisher
      // String name1 = game1.getName();
      // String name2 = game2.getName();
      // String programmer1 = game1.getProgrammer().getName();
      // String programmer2 = game2.getProgrammer().getName();
      // String publisher1 = game1.getPublisher().getName();
      // String publisher2 = game2.getPublisher().getName();
      // String genre1 = game1.getGenre().getParentGenre().getName();
      // String genre2 = game2.getGenre().getParentGenre().getName();
      // String year1 = game1.getYear().getName();
      // String year2 = game2.getYear().getName();
      //
      // // possible same game ???
      //
      // double nameProp = StringTools.getStringSimilarity(name1, name2);
      // if ((nameProp < 0.7) && (containsShorter(name1, name2))) {
      // nameProp = 0.7;
      // }
      //
      // double programmerProp;
      // if ((programmer1.equals("(Unknown)")) ||
      // (programmer2.equals("(Unknown)"))) {
      // programmerProp = 0.333;
      // } else {
      // programmerProp =
      // StringTools.getStringSimilarity(StringTools.simplifyForMatching(programmer1),
      // StringTools.simplifyForMatching(programmer2));
      // if ((programmerProp < 0.75) && (containsShorter(programmer1,
      // programmer2))) {
      // programmerProp = 0.9;
      // }
      // }
      //
      // double publisherProp;
      // if ((publisher1.equals("(Unknown)")) ||
      // (publisher2.equals("(Unknown)"))) {
      // publisherProp = 0.333;
      // } else {
      // publisherProp =
      // StringTools.getStringSimilarity(StringTools.simplifyForMatching(publisher1),
      // StringTools.simplifyForMatching(publisher2));
      // publisherProp = Math.sqrt(publisherProp);
      // if ((publisherProp < 0.75) && (containsShorter(publisher1,
      // publisher2))) {
      // publisherProp = 0.9;
      // }
      // }
      //
      // double genreProp =
      // StringTools.getStringSimilarity(StringTools.simplifyForMatching(genre1),
      // StringTools.simplifyForMatching(genre2));
      // if ((genreProp < 0.75) && (containsShorter(genre1, genre2))) {
      // genreProp = 0.9;
      // }
      //
      // double yearProp = Math.abs(game1.getYear().getNameId() -
      // game2.getYear().getNameId());
      // if (yearProp > 10) {
      // yearProp = 10;
      // }
      // yearProp = (10 - yearProp)/10;
      // yearProp = Math.sqrt(yearProp);
      //
      // double prop = nameProp * genreProp * programmerProp * publisherProp *
      // yearProp;
      //
      // if (prop > 0.665) {
      // if (prop < 1.0) { // don't show perfect matches
      // // System.out.println(name1 + " ? " + name2 + " :" + nameProp);
      // // System.out.println(genre1 + " ? " + genre2 + " :" + genreProp);
      // // System.out.println(programmer1 + " ? " + programmer2 + " :" +
      // programmerProp);
      // // System.out.println(publisher1 + " ? " + publisher2 + " :" +
      // publisherProp);
      // // System.out.println(year1 + " ? " + year2 + " :" + yearProp);
      // //
      // // System.out.println("== " + prop);
      // // System.out.println();
      // // remove from Map of urls
      // }
      // } else {
      // // System.out.println(name1 + " ? " + name2 + " :" + nameProp);
      // // System.out.println(genre1 + " ? " + genre2 + " :" + genreProp);
      // // System.out.println(programmer1 + " ? " + programmer2 + " :" +
      // programmerProp);
      // // System.out.println(publisher1 + " ? " + publisher2 + " :" +
      // publisherProp);
      // // System.out.println(year1 + " ? " + year2 + " :" + yearProp);
      // //
      // // System.out.println("!= " + prop);
      // // System.out.println();
      //
      // doubleUrlGames.add(game1);
      // }
      // }
      // }
      // }
      // }
      // }
      //
      // // remove double entries
      // data = new ArrayList<Item>(new HashSet<Item>(doubleUrlGames));
      //
      // // games without screenshots are probably not mentioned in reviews
      // etc...
      // int count = 0;
      // for (Item item : data) {
      // Game game = ((Game)item);
      // if (game.getScreenshotCount() == 0) {
      // System.err.println("NO Screenshots for game " + game.getName()
      // +", removing URL extras");
      // count++;
      //
      // for (Extra extra : new CopyOnWriteArrayList<Extra>(game.getExtras())) {
      // if (extra.isUrl() &&
      // !extra.getFilename().toLowerCase().startsWith("http://www.gamebase64.com"))
      // {
      // extra.setGameId(0);
      // game.getExtras().remove(extra);
      // Db.saveOrUpdate(game);
      // Db.delete(extra);
      // data.remove(game);
      // }
      // }
      //
      // }
      // }
      // if (count > 0) {
      // System.err.println(count + " Games without Screenshots!");
      // }
      //
      // urlsToGames = new MultiValueMap();
      // for(Item item : data) {
      // if (item instanceof Game) {
      // Game game = (Game) item;
      // for(Extra extra : game.getExtras()) {
      // if (extra.isUrl() &&
      // !extra.getFilename().toLowerCase().startsWith("http://www.gamebase64.com")
      // &&
      // !extra.getFilename().toLowerCase().startsWith("http://www.zzap64.co.uk"))
      // {
      // urlsToGames.put(extra.getFilename(), game);
      // }
      // }
      // }
      // }
      //
      // List<Item> moreUrlGames = new ArrayList<Item>();
      //
      // count = 0;
      // for (Iterator iterator = urlsToGames.keySet().iterator();
      // iterator.hasNext();) {
      // String url = (String) iterator.next();
      // List<Game> games = (List<Game>) urlsToGames.getCollection(url);
      // //TODO select the sizes to handle
      // //if (games.size() > 5) {
      // //if ((games.size() > 4) && (games.size() <= 5)) {
      // //if ((games.size() > 3) && (games.size() <= 4)) {
      // //if ((games.size() > 2) && (games.size() <= 3)) {
      // //if ((games.size() > 1) && (games.size() <= 2)) {
      // //if (games.size() > 1) {
      // if (games.size() > 1
      // && (!url.toLowerCase().startsWith("http://en.wikipedia.org"))
      // && (!url.toLowerCase().startsWith("https://en.wikipedia.org"))
      // && (!url.toLowerCase().startsWith("http://de.wikipedia.org"))
      // && (!url.toLowerCase().startsWith("https://de.wikipedia.org"))) {
      //
      // count++;
      // System.out.println(games.size() + ": " + url);
      // boolean hasSameWikipediaEntry = false;
      // List<Extra> sameExtras = new
      // ArrayList<Extra>(games.get(0).getExtras());
      //
      // for (Game game : games) {
      // sameExtras.retainAll(game.getExtras());
      // }
      //
      // for (Extra extra : sameExtras) {
      // if (extra.isUrl()
      // && ((url.toLowerCase().startsWith("http://en.wikipedia.org"))
      // || (url.toLowerCase().startsWith("https://en.wikipedia.org"))
      // || (url.toLowerCase().startsWith("http://de.wikipedia.org"))
      // || (url.toLowerCase().startsWith("https://de.wikipedia.org")))) {
      // hasSameWikipediaEntry = true;
      // }
      // }
      //
      // if (!hasSameWikipediaEntry) {
      // moreUrlGames.addAll(games);
      // }
      //
      // System.out.println();
      // }
      // }
      // System.out.println(count + " urls link to more than one game.");

      // remove double entries
      // data = new ArrayList<Item>(new HashSet<Item>(moreUrlGames));
    }

    return data;
  }

  private boolean containsShorter(final String s1, final String s2) {
    if ((s1 == null) || (s2 == null) || s1.isEmpty() || s2.isEmpty()) {
      return false;
    }

    final int l1 = s1.length();
    final int l2 = s2.length();

    if (l1 == l2) {
      return s1.equalsIgnoreCase(s2);
    }

    if (l1 > l2) {
      return s1.toLowerCase().contains(s2.toLowerCase());
    }

    return s2.toLowerCase().contains(s1.toLowerCase());

  }
}
