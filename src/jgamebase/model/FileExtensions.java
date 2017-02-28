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

package jgamebase.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeSet;

import jgamebase.Const;

public class FileExtensions extends TreeSet<String> {

  private static final long serialVersionUID = 7817389464880291560L;

  private static final String DELIMITER = ";";
  private static final String MATCHES_ALL = "*";

  public FileExtensions() {
    super();
  }

  public FileExtensions(final String extensionsAsString) {
    this();
    fromString(extensionsAsString);
  }

  public void fromString(String extensionsAsString) {
    extensionsAsString = (extensionsAsString == null) ? "" : extensionsAsString.toLowerCase();

    String token;
    boolean starFound = false;

    // clear existing extensions
    super.clear();

    for (final StringTokenizer st = new StringTokenizer(extensionsAsString,
        FileExtensions.DELIMITER); st.hasMoreTokens();) {
      token = st.nextToken();

      // check if token is a star
      if (token.equals(FileExtensions.MATCHES_ALL)) {
        starFound = true;
      }

      // remove leading star "*"
      if (token.startsWith(MATCHES_ALL)) {
        token = token.substring(1);
      }
      // remove leading dot "."
      if (token.startsWith(Const.EXTENSION_SEPARATOR)) {
        token = token.substring(1);
      }

      // only add non empty tokens
      if (!token.isEmpty()) {
        super.add(token);
      }
    }

    // check if star was found
    if (starFound) {
      setMatchesAll();
    }
  }

  public boolean add(final FileExtensions e) {
    // you can't add to all
    if (!matchesAll()) {
      if (e.matchesAll()) {
        setMatchesAll();
      } else {
        super.addAll(e);
      }
      return true;
    }
    return false;
  }

  @Override
  public boolean add(final String otherExtensionsAsString) {
    // if String does not contain extensions
    if (new FileExtensions(otherExtensionsAsString).isEmpty()) {
      return false;
    }

    // create new extensions consisting of extensions as String
    final FileExtensions e = new FileExtensions(toString() + DELIMITER + otherExtensionsAsString);

    // clear existing extensions and set new extensions
    super.clear();
    super.addAll(e);

    return true;
  }

  public void setMatchesAll() {
    super.clear();
    super.add(MATCHES_ALL);
  }

  public boolean matchesAll() {
    return toString().equals(MATCHES_ALL);
  }

  public boolean matches(final File file) {
    if (file == null) {
      throw new NullPointerException("File may not be null");
    }
    return matches(file.getName());
  }

  public boolean matchesAsPrefix(final File file) {
    if (file == null) {
      throw new NullPointerException("File may not be null");
    }
    return matchesAsPrefix(file.getName());
  }

  /**
   * 
   * @param filename
   *          The filename, not the extension only (matches ".html", not
   *          "html")!
   * @return
   */
  public boolean matches(final String filename) {
    // if all extensions are supported this is supported too
    if (matchesAll()) {
      return true;
    }

    // check if filename matches extensions
    String extension;
    boolean matches = false;

    for (final Iterator<String> iter = super.iterator(); iter.hasNext();) {
      extension = iter.next();
      if (filename.toLowerCase().endsWith(Const.EXTENSION_SEPARATOR + extension.toLowerCase())) {
        matches = true;
      }
    }

    return matches;
  }

  public boolean matchesAsPrefix(final String filename) {
    // if all extensions are supported this is supported too
    if (matchesAll()) {
      return true;
    }

    // check if filename matches extensions
    String extension;
    boolean matches = false;

    for (final Iterator<String> iter = super.iterator(); iter.hasNext();) {
      extension = iter.next();
      if (Paths.removePath(filename).toLowerCase()
          .startsWith(extension.toLowerCase() + Const.EXTENSION_SEPARATOR)) {
        matches = true;
      }
    }

    return matches;
  }

  public List<String> getMatching(final List<String> filenames) {
    final List<String> matchingFilenames = new ArrayList<String>();

    for (final String filename : filenames) {
      if (matches(filename)) {
        matchingFilenames.add(filename);
      }
    }

    return matchingFilenames;
  }

  public List<String> getMatchingAsPrefix(final List<String> filenames) {
    final List<String> matchingFilenames = new ArrayList<String>();

    for (final String filename : filenames) {
      if (matchesAsPrefix(filename)) {
        matchingFilenames.add(filename);
      }
    }

    return matchingFilenames;
  }

  @Override
  public boolean contains(final Object o) {
    // "*" contains all
    if (matchesAll()) {
      return true;
    }

    if (!(o instanceof String)) {
      throw new IllegalArgumentException("FileExceptions can only be of type String ");
    }

    final Iterator<String> e = new FileExtensions((String) o).iterator();
    while (e.hasNext()) {
      final String next = e.next().toLowerCase();
      if (!super.contains(next) || next.equals(MATCHES_ALL)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String toString() {
    final StringBuffer extensionsString = new StringBuffer();

    for (final Iterator<String> iter = super.iterator(); iter.hasNext();) {
      extensionsString.append(iter.next());

      if (iter.hasNext()) {
        extensionsString.append(FileExtensions.DELIMITER);
      }
    }

    return extensionsString.toString();
  }

}