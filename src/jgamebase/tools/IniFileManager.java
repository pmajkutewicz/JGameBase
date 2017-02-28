/*
 * IniEditor is Copyright (c) 2003-2005, Nik Haldimann
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * * Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package jgamebase.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import jgamebase.Const;

/**
 * Loads, edits and saves INI-style configuration files. While loading from and
 * saving to streams and files, <code>IniFileManager</code> preserves comments
 * and blank lines as well as the order of sections and lines in general.
 * <p>
 * <code>IniFileManager</code> assumes configuration files to be split in
 * sections. A section starts out with a header, which consists of the section
 * name enclosed in brackets (<code>'['</code> and <code>']'</code>). Everything
 * before the first section header is ignored when loading from a stream or
 * file. The <code>{@link IniFileManager.Section}</code> class can be used to
 * load configuration files without sections (ie Java-style properties).
 * <p>
 * A "common section" may be named. All sections inherit the options of this
 * section but can overwrite them.
 * <p>
 * <code>IniFileManager</code> represents an INI file (or rather, its sections)
 * line by line, as comment, blank and option lines. A comment is a line which
 * has a comment delimiter as its first non-white space character. The default
 * comment delimiters, which may be overwritten, are <code>'#'</code> and
 * <code>';'</code>.
 * <p>
 * A blank line is any line that consists only of white space.
 * <p>
 * Everything else is an option line. Option names and values are separated by
 * option delimiters <code>'='</code>, <code>':'</code> or white space (spaces
 * and tabs).
 * <p>
 * Here's a minimal example. Suppose, we have this in a file called
 * <code>users.ini</code>:
 * 
 * <pre>
 *   [root]
 *   role = administrator
 *   last_login = 2003-05-04
 *   [joe]
 *   role = author
 *   last_login = 2003-05-13
 * </pre>
 * 
 * Let's load that file, add something to it and save the changes:
 * 
 * <pre>
 * IniFileManager users = new IniFileManager();
 * users.load(&quot;users.ini&quot;);
 * users.set(&quot;root&quot;, &quot;last_login&quot;, &quot;2003-05-16&quot;);
 * users.addComment(&quot;root&quot;, &quot;Must change password often&quot;);
 * users.set(&quot;root&quot;, &quot;change_pwd&quot;, &quot;10 days&quot;);
 * users.addBlankLine(&quot;root&quot;);
 * users.save(&quot;users.ini&quot;);
 * </pre>
 * 
 * Now, the file looks like this:
 * 
 * <pre>
 *   [root]
 *   role = administrator
 *   last_login = 2003-05-16
 *   # Must change password often
 *   change_pwd = 10 days
 *   [joe]
 *   role = author
 *   last_login = 2003-05-13
 * </pre>
 * <p>
 * IniFileManager provides services simliar to the standard Java API class
 * <code>java.util.Properties</code>. It uses its own parser, though, which
 * differs in these respects from that of <code>Properties</code>:
 * <ul>
 * <li>Line continuations (backslashes at the end of an option line) are not
 * supported.</li>
 * <li>No kind of character escaping is performed or recognized. Characters are
 * read and written in in the default character encoding. If you want to use a
 * different character encoding, use the {@link #load(InputStreamReader)} and
 * {@link #save(OutputStreamWriter)} methods with a reader and writer tuned to
 * the desired character encoding.</li>
 * <li>As a consequence, option names may not contain option/value separators
 * (normally <code>'='</code>, <code>':'</code> and white space).</li>
 * </ul>
 * 
 * @author Nik Haldimann, me at ubique.ch
 * @version r4 (8/10/2005)
 * @revision $Id: IniFileManager.java,v 1.25 2005/10/08 19:26:43 Nik Exp $
 */
public class IniFileManager {

  private final File file;

  private final Map<String, Section> sections;

  private final List<String> sectionOrder;

  private OptionFormat optionFormat;

  /**
   * Constructs new IniFileManager instance with a common section, defining
   * comment delimiters. Options in the common section are used as defaults for
   * all other sections.
   * 
   * @param file
   *          The ini <code>File</code>.
   */
  public IniFileManager(final File file) throws IOException {
    this.file = file;
    sections = new HashMap<String, Section>();
    sectionOrder = new LinkedList<String>();
    optionFormat = new OptionFormat(Section.DEFAULT_OPTION_FORMAT);

    if (file.exists()) {
      load();
    } else {
      file.createNewFile();
    }
  }

  public IniFileManager(final String filename) throws IOException {
    this(new File(filename));
  }

  /**
   * Sets the option format for this instance to the given string. Options will
   * be rendered according to the given format string when printed. The string
   * must contain <code>%s</code> three times, these will be replaced with the
   * option name, the option separator and the option value in this order.
   * Literal percentage signs must be escaped by preceding them with another
   * percentage sign (i.e., <code>%%</code> corresponds to one percentage sign).
   * The default format string is <code>"%s %s %s"</code>.
   * 
   * Option formats may look like format strings as supported by Java 1.5, but
   * the string is in fact parsed in a custom fashion to guarantee backwards
   * compatibility. So don't try clever stuff like using format conversion types
   * other than <code>%s</code>.
   * 
   * @param formatString
   *          a format string, containing <code>%s</code> exactly three times
   * @throws IllegalArgumentException
   *           if the format string is illegal
   */
  public void setOptionFormatString(final String formatString) {
    optionFormat = new OptionFormat(formatString);
  }

  /**
   * Returns the value of a given option in a given section or null if either
   * the section or the option don't exist. If a common section was defined
   * options are also looked up there if they're not present in the specific
   * section.
   * 
   * @param section
   *          the section's name
   * @param option
   *          the option's name
   * @return the option's value
   * @throws NullPointerException
   *           any of the arguments is <code>null</code>
   */
  public String get(final String section, final String option) {
    if (hasSection(section)) {
      final Section sect = getSection(section);
      if (sect.hasOption(option)) {
        return sect.get(option);
      }
    }
    return null;
  }

  /**
   * Sets the value of an option in a section, if the option exist, otherwise
   * adds the option to the section. Trims white space from the start and the
   * end of the value and deletes newline characters it might contain.
   * 
   * @param section
   *          the section's name
   * @param option
   *          the option's name
   * @param value
   *          the option's value
   * @throws IllegalArgumentException
   *           the option name is illegal, ie contains a '=' character or
   *           consists only of white space
   * @throws NullPointerException
   *           section or option are <code>null</code>
   */
  public void set(final String section, final String option, final String value) {
    if (!hasSection(section)) {
      addSection(section);
    }
    getSection(section).set(option, value);

    // * @throws IniFileManager.NoSuchSectionException no section with the
    // given name exists

    // if (hasSection(section)) {
    // getSection(section).set(option, value);
    // }
    // else {
    // throw new NoSuchSectionException(section);
    // }

  }

  /**
   * Removes an option from a section if it exists. Will not remove options from
   * the common section if it's not directly addressed.
   * 
   * @param section
   *          the section's name
   * @param option
   *          the option's name
   * @return <code>true</code> if the option was actually removed
   * @throws IniFileManager.NoSuchSectionException
   *           no section with the given name exists
   */
  public boolean remove(final String section, final String option) {
    if (hasSection(section)) {
      return getSection(section).remove(option);
    } else {
      throw new NoSuchSectionException(section);
    }
  }

  /**
   * Checks whether an option exists in a given section. Options in the common
   * section are assumed to not exist in particular sections, unless they're
   * overwritten.
   * 
   * @param section
   *          the section's name
   * @param option
   *          the option's name
   * @return true if the given section has the option
   */
  public boolean hasOption(final String section, final String option) {
    return hasSection(section) && getSection(section).hasOption(option);
  }

  /**
   * Checks whether a section with a particular name exists in this instance.
   * 
   * @param name
   *          the name of the section
   * @return true if the section exists
   */
  public boolean hasSection(final String name) {
    return sections.containsKey(normSection(name));
  }

  /**
   * Adds a section if it doesn't exist yet.
   * 
   * @param name
   *          the name of the section
   * @return <code>true</code> if the section didn't already exist
   * @throws IllegalArgumentException
   *           the name is illegal, ie contains one of the characters '[' and
   *           ']' or consists only of white space
   */
  public boolean addSection(final String name) {
    final String normName = normSection(name);
    if (!hasSection(normName)) {
      // Section constructor might throw IllegalArgumentException
      final Section section = new Section(normName);
      section.setOptionFormat(optionFormat);
      sections.put(normName, section);
      sectionOrder.add(normName);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Removes a section if it exists.
   * 
   * @param name
   *          the section's name
   * @return <code>true</code> if the section actually existed
   * @throws IllegalArgumentException
   *           when trying to remove the common section
   */
  public boolean removeSection(final String name) {
    final String normName = normSection(name);
    if (hasSection(normName)) {
      sections.remove(normName);
      sectionOrder.remove(normName);
      return true;
    } else {
      return false;
    }
  }

  public void removeAllSections() {
    final List<String> sectionNames = getSectionNames();
    for (final String sectionName : sectionNames) {
      removeSection(sectionName);
    }
  }

  /**
   * Clears all options of a section.
   * 
   * @param section
   *          the section's name
   * @return <code>true</code> if the section actually existed
   */
  public boolean clearSection(final String section) {
    final String normSection = normSection(section);
    if (hasSection(normSection)) {
      getSection(normSection).clear();
      return true;
    } else {
      return false;
    }
  }

  /**
   * Sets the values in a section. The option's names are numbers starting from
   * one.
   * 
   * @param section
   *          the section's name
   * @param values
   *          the values
   * @throws NullPointerException
   *           section is <code>null</code>
   */
  public void setList(final String section, final List<String> values) {

    if (!hasSection(section)) {
      addSection(section);
    }

    getSection(section).clear();
    int i = 0;
    for (final Iterator<String> iter = values.iterator(); iter.hasNext(); i++) {
      final Object value = iter.next();
      getSection(section).set((i + 1) + "", value.toString());
    }
  }

  /**
   * Returns a list containing the values of all numbered options of this
   * section.
   * 
   * @param section
   *          the section's name
   * @return the list of values
   */
  public List<String> getList(final String section) {
    final List<String> values = new ArrayList<String>();

    if (hasSection(section)) {

      int i = 1;
      while (get(section, i + "") != null) {
        final String value = get(section, i + "");
        if (!value.isEmpty()) {
          values.add(value);
        }
        i++;
      }
    }

    return values;
  }

  /**
   * Returns all section names in this instance .
   * 
   * @return list of the section names in original/insertion order
   */
  public List<String> getSectionNames() {
    return new ArrayList<String>(sectionOrder);
  }

  /**
   * Returns all option names of a section.
   * 
   * @param section
   *          the section's name
   * @return list of option names
   * @throws IniFileManager.NoSuchSectionException
   *           no section with the given name exists
   */
  public List<String> optionNames(final String section) {
    if (hasSection(section)) {
      return getSection(section).optionNames();
    } else {
      throw new NoSuchSectionException(section);
    }
  }

  /**
   * Adds a comment line to the end of a section. A comment spanning several
   * lines (ie with line breaks) will be split up, one comment line for each
   * line.
   * 
   * @param section
   *          the section's name
   * @param comment
   *          the comment
   * @throws IniFileManager.NoSuchSectionException
   *           no section with the given name exists
   */
  public void addComment(final String section, final String comment) {
    if (hasSection(section)) {
      getSection(section).addComment(comment);
    } else {
      throw new NoSuchSectionException(section);
    }
  }

  /**
   * Writes this instance in INI format to a file.
   * 
   * @throws IOException
   *           at an I/O problem
   */
  public void save() throws IOException {
    final OutputStream out = new FileOutputStream(file);
    final Iterator<String> it = sectionOrder.iterator();
    final PrintWriter writer = new PrintWriter(new OutputStreamWriter(out), true);
    while (it.hasNext()) {
      final Section sect = getSection(it.next());
      writer.println(sect.header());
      sect.save(writer);
    }
    out.close();
  }

  /**
   * Loads INI formatted input from a file into this instance, using the default
   * character encoding. Everything in the file before the first section header
   * is ignored.
   * 
   * @throws IOException
   *           at an I/O problem
   */
  private void load() throws IOException {
    final InputStream in = new FileInputStream(file);
    final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    String curSection = null;
    String line = null;

    while (reader.ready()) {
      line = reader.readLine();
      if (line != null) {
        line = line.trim();
        if ((line.length() > 0) && (line.charAt(0) == Section.HEADER_START)) {
          final int endIndex = line.indexOf(Section.HEADER_END);
          if (endIndex >= 0) {
            curSection = line.substring(1, endIndex);
            addSection(curSection);
          }
        }
        if (curSection != null) {
          final Section sect = getSection(curSection);
          sect.load(reader);
        }
      }
    }
    in.close();
  }

  /**
   * Returns a section by name or <code>null</code> if not found.
   * 
   * @param name
   *          the section's name
   * @return the section
   */
  private Section getSection(final String name) {
    return sections.get(normSection(name));
  }

  /**
   * Normalizes an arbitrary string for use as a section name. Currently only
   * makes the string lower-case (provided this instance isn't case- sensitive)
   * and trims leading and trailing white space. Note that normalization isn't
   * enforced by the Section class.
   * 
   * @param name
   *          the string to be used as section name
   * @return a normalized section name
   */
  private String normSection(final String name) {
    return name.trim();
    // return name.toLowerCase().trim();
  }

  /**
   * Loads, edits and saves a section of an INI-style configuration file. This
   * class does actually belong to the internals of {@link IniFileManager} and
   * should rarely ever be used directly. It's exposed because it can be useful
   * for plain, section-less configuration files (Java-style properties, for
   * example).
   */
  public static class Section {

    private final String name;

    private Map<String, Option> options;

    private List<Line> lines;

    private final char[] optionDelims;

    private final char[] optionDelimsSorted;

    private final char[] commentDelims;

    private final char[] commentDelimsSorted;

    private OptionFormat optionFormat;

    private static final char[] DEFAULT_OPTION_DELIMS = new char[] { '=' };

    private static final char[] DEFAULT_COMMENT_DELIMS = new char[] { '#', ';' };

    private static final char[] OPTION_DELIMS_WHITESPACE = new char[] { ' ', '\t' };

    public static final String DEFAULT_OPTION_FORMAT = "%s%s%s"; // TODO? was
                                                                 // "%s %s %s"

    public static final char HEADER_START = '[';

    public static final char HEADER_END = ']';

    private static final int NAME_MAXLENGTH = 8192;

    private static final char[] INVALID_NAME_CHARS = { HEADER_START, HEADER_END };

    /**
     * Constructs a new section.
     * 
     * @param name
     *          the section's name
     * @throws IllegalArgumentException
     *           the section's name is illegal
     */
    public Section(final String name) {
      this(name, null);
    }

    /**
     * Constructs a new section, defining comment delimiters.
     * 
     * @param name
     *          the section's name
     * @param delims
     *          an array of characters to be recognized as starters of comment
     *          lines; the first of them will be used for newly created comments
     * @throws IllegalArgumentException
     *           the section's name is illegal
     */
    public Section(final String name, final char[] delims) {
      if (!validName(name)) {
        throw new IllegalArgumentException("Illegal section name:" + name);
      }
      this.name = name;
      options = new HashMap<String, Option>();
      lines = new LinkedList<Line>();
      optionDelims = DEFAULT_OPTION_DELIMS;
      commentDelims = (delims == null ? DEFAULT_COMMENT_DELIMS : delims);
      optionFormat = new OptionFormat(DEFAULT_OPTION_FORMAT);
      // sorting so we can later use binary search
      optionDelimsSorted = new char[optionDelims.length];
      System.arraycopy(optionDelims, 0, optionDelimsSorted, 0, optionDelims.length);
      commentDelimsSorted = new char[commentDelims.length];
      System.arraycopy(commentDelims, 0, commentDelimsSorted, 0, commentDelims.length);
      Arrays.sort(optionDelimsSorted);
      Arrays.sort(commentDelimsSorted);
    }

    /**
     * Clears all options from this section.
     */
    public void clear() {
      options = new HashMap<String, Option>();
      lines = new LinkedList<Line>();
    }

    /**
     * Sets the option format for this section to the given string. Options in
     * this section will be rendered according to the given format string. The
     * string must contain <code>%s</code> three times, these will be replaced
     * with the option name, the option separator and the option value in this
     * order. Literal percentage signs must be escaped by preceding them with
     * another percentage sign (i.e., <code>%%</code> corresponds to one
     * percentage sign). The default format string is <code>"%s %s %s"</code>.
     * 
     * Option formats may look like format strings as supported by Java 1.5, but
     * the string is in fact parsed in a custom fashion to guarantee backwards
     * compatibility. So don't try clever stuff like using format conversion
     * types other than <code>%s</code>.
     * 
     * @param formatString
     *          a format string, containing <code>%s</code> exactly three times
     * @throws IllegalArgumentException
     *           if the format string is illegal
     */
    public void setOptionFormatString(final String formatString) {
      setOptionFormat(new OptionFormat(formatString));
    }

    /**
     * Sets the option format for this section. Options will be rendered
     * according to the given format when printed.
     * 
     * @param format
     *          a compiled option format
     */
    public void setOptionFormat(final OptionFormat format) {
      optionFormat = format;
    }

    /**
     * Returns the names of all options in this section.
     * 
     * @return list of names of this section's options in original/insertion
     *         order
     */
    public List<String> optionNames() {
      final List<String> optNames = new LinkedList<String>();
      final Iterator<Line> it = lines.iterator();
      while (it.hasNext()) {
        final Object line = it.next();
        if (line instanceof Option) {
          optNames.add(((Option) line).name());
        }
      }
      return optNames;
    }

    /**
     * Checks whether a given option exists in this section.
     * 
     * @param name
     *          the name of the option to test for
     * @return true if the option exists in this section
     */
    public boolean hasOption(final String name) {
      return options.containsKey(normOption(name));
    }

    /**
     * Returns an option's value.
     * 
     * @param option
     *          the name of the option
     * @return the requested option's value or <code>null</code> if no option
     *         with the specified name exists
     */
    public String get(final String option) {
      final String normed = normOption(option);
      if (hasOption(normed)) {
        return getOption(normed).value();
      }
      return null;
    }

    /**
     * Sets an option's value and creates the option if it doesn't exist.
     * 
     * @param option
     *          the option's name
     * @param value
     *          the option's value
     * @throws IllegalArgumentException
     *           the option name is illegal, ie contains a '=' character or
     *           consists only of white space
     */
    public void set(final String option, final String value) {
      set(option, value, optionDelims[0]);
    }

    /**
     * Sets an option's value and creates the option if it doesn't exist.
     * 
     * @param option
     *          the option's name
     * @param value
     *          the option's value
     * @param delim
     *          the delimiter between name and value for this option
     * @throws IllegalArgumentException
     *           the option name is illegal, ie contains a '=' character or
     *           consists only of white space
     */
    public void set(final String option, final String value, final char delim) {
      final String normed = normOption(option);
      if (hasOption(normed)) {
        getOption(normed).set(value);
      } else {
        // Option constructor might throw IllegalArgumentException
        final Option opt = new Option(normed, value, delim, optionFormat);
        options.put(normed, opt);
        lines.add(opt);
      }
    }

    /**
     * Removes an option if it exists.
     * 
     * @param option
     *          the name of the option
     * @return <code>true</code> if the option was actually removed
     */
    public boolean remove(final String option) {
      final String normed = normOption(option);
      if (hasOption(normed)) {
        lines.remove(getOption(normed));
        options.remove(normed);
        return true;
      } else {
        return false;
      }
    }

    /**
     * Adds a comment line to the end of this section. A comment spanning
     * several lines (ie with line breaks) will be split up, one comment line
     * for each line.
     * 
     * @param comment
     *          the comment
     */
    public void addComment(final String comment) {
      addComment(comment, commentDelims[0]);
    }

    /**
     * Adds a comment line to the end of this section. A comment spanning
     * several lines (ie with line breaks) will be split up, one comment line
     * for each line.
     * 
     * @param comment
     *          the comment
     * @param delim
     *          the delimiter used to mark the start of this comment
     */
    public void addComment(final String comment, final char delim) {
      final StringTokenizer st = new StringTokenizer(comment.trim(), Const.LINE_SEPARATOR);
      while (st.hasMoreTokens()) {
        lines.add(new Comment(st.nextToken(), delim));
      }
    }

    /**
     * Adds a blank line to the end of this section.
     */
    public void addBlankLine() {
      lines.add(BLANK_LINE);
    }

    /**
     * Loads options from a reader into this instance. Will read from the stream
     * until it hits a section header, ie a '[' character, and resets the reader
     * to point to this character.
     * 
     * @param reader
     *          where to read from
     * @throws IOException
     *           at an I/O problem
     */
    public void load(final BufferedReader reader) throws IOException {
      while (reader.ready()) {
        reader.mark(NAME_MAXLENGTH);
        String line = reader.readLine();

        if (line != null) {
          line = line.trim();
          // Check for section header
          if ((line.length() > 0) && (line.charAt(0) == HEADER_START)) {
            reader.reset();
            return;
          }
          int delimIndex = -1;
          // blank line
          if (line.isEmpty()) {
            addBlankLine();
          }
          // comment line
          else if ((delimIndex = Arrays.binarySearch(commentDelimsSorted, line.charAt(0))) >= 0) {
            addComment(line.substring(1), commentDelimsSorted[delimIndex]);
          }
          // option line
          else {
            delimIndex = -1;
            int delimNum = -1;
            int lastSpaceIndex = -1;
            for (int i = 0, l = line.length(); (i < l) && (delimIndex < 0); i++) {
              delimNum = Arrays.binarySearch(optionDelimsSorted, line.charAt(i));
              if (delimNum >= 0) {
                delimIndex = i;
              } else {
                final boolean isSpace = Arrays.binarySearch(Section.OPTION_DELIMS_WHITESPACE,
                    line.charAt(i)) >= 0;
                if (!isSpace && (lastSpaceIndex >= 0)) {
                  break;
                } else if (isSpace) {
                  lastSpaceIndex = i;
                }
              }
            }
            // delimiter at start of line
            if (delimIndex == 0) {
              // what's a man got to do?
            }
            // no delimiter found
            else if (delimIndex < 0) {
              if (lastSpaceIndex < 0) {
                this.set(line, "");
              } else {
                this.set(line.substring(0, lastSpaceIndex), line.substring(lastSpaceIndex + 1));
              }
            }
            // delimiter found
            else {
              this.set(line.substring(0, delimIndex), line.substring(delimIndex + 1),
                  line.charAt(delimIndex));
            }
          }
        }
      }
    }

    /**
     * Prints this section to a print writer.
     * 
     * @param writer
     *          where to write
     * @throws IOException
     *           at an I/O problem
     */
    public void save(final PrintWriter writer) throws IOException {
      final Iterator<Line> it = lines.iterator();
      while (it.hasNext()) {
        writer.println(it.next().toString());
      }
      if (writer.checkError()) {
        throw new IOException();
      }
    }

    /**
     * Returns an actual Option instance.
     * 
     * @param option
     *          the name of the option, assumed to be normed already (!)
     * @return the requested Option instance
     * @throws NullPointerException
     *           if no option with the specified name exists
     */
    private Option getOption(final String name) {
      return options.get(name);
    }

    /**
     * Returns the bracketed header of this section as appearing in an actual
     * INI file.
     * 
     * @return the section's name in brackets
     */
    private String header() {
      return HEADER_START + name + HEADER_END;
    }

    /**
     * Checks a string for validity as a section name. It can't contain the
     * characters '[' and ']'. An empty string or one consisting only of white
     * space isn't allowed either.
     * 
     * @param name
     *          the name to validate
     * @return true if the name validates as a section name
     */
    private static boolean validName(final String name) {
      if (name.trim().isEmpty()) {
        return false;
      }
      for (final char element : INVALID_NAME_CHARS) {
        if (name.indexOf(element) >= 0) {
          return false;
        }
      }
      return true;
    }

    /**
     * Normalizes an arbitrary string for use as an option name, ie makes it
     * lower-case (provided this section isn't case-sensitive) and trims leading
     * and trailing white space.
     * 
     * @param name
     *          the string to be used as option name
     * @return a normalized option name
     */
    private String normOption(final String name) {
      return name.trim();
      // return name.toLowerCase().trim();
    }
  }

  private interface Line {
    @Override
    public String toString();
  }

  private static final Line BLANK_LINE = new Line() {
    @Override
    public String toString() {
      return "";
    }
  };

  private static class Option implements Line {

    private final String name;

    private String value;

    private final char separator;

    private final OptionFormat format;

    private static final String ILLEGAL_VALUE_CHARS = "\n\r";

    public Option(final String name, final String value, final char separator,
        final OptionFormat format) {
      if (!validName(name, separator)) {
        throw new IllegalArgumentException("Illegal option name:" + name);
      }
      this.name = name;
      this.separator = separator;
      this.format = format;
      set(value);
    }

    public String name() {
      return name;
    }

    public String value() {
      return value;
    }

    public void set(final String value) {
      if (value == null) {
        this.value = "";
      } else {
        final StringTokenizer st = new StringTokenizer(value.trim(), ILLEGAL_VALUE_CHARS);
        final StringBuffer sb = new StringBuffer();
        while (st.hasMoreTokens()) {
          sb.append(st.nextToken());
        }
        this.value = sb.toString();
      }
    }

    @Override
    public String toString() {
      return format.format(name, value, separator);
    }

    private static boolean validName(final String name, final char separator) {
      if (name.trim().isEmpty()) {
        return false;
      }
      if (name.indexOf(separator) >= 0) {
        return false;
      }
      return true;
    }

  }

  private static class Comment implements Line {

    private final String comment;

    private final char delimiter;

    public Comment(final String comment, final char delimiter) {
      this.comment = comment.trim();
      this.delimiter = delimiter;
    }

    @Override
    public String toString() {
      return delimiter + " " + comment;
    }

  }

  private static class OptionFormat {

    private static final int EXPECTED_TOKENS = 4;

    private final String[] formatTokens;

    public OptionFormat(final String formatString) {
      formatTokens = compileFormat(formatString);
    }

    public String format(final String name, final String value, final char separator) {
      final String[] t = formatTokens;
      return t[0] + name + t[1] + separator + t[2] + value + t[3];
    }

    private String[] compileFormat(final String formatString) {
      final String[] tokens = { "", "", "", "" };
      int tokenCount = 0;
      boolean seenPercent = false;
      StringBuffer token = new StringBuffer();
      for (int i = 0; i < formatString.length(); i++) {
        switch (formatString.charAt(i)) {
          case '%':
            if (seenPercent) {
              token.append("%");
              seenPercent = false;
            } else {
              seenPercent = true;
            }
            break;
          case 's':
            if (seenPercent) {
              if (tokenCount >= EXPECTED_TOKENS) {
                throw new IllegalArgumentException(
                    "Illegal option format. Too many %s placeholders.");
              }
              tokens[tokenCount] = token.toString();
              tokenCount++;
              token = new StringBuffer();
              seenPercent = false;
            } else {
              token.append("s");
            }
            break;
          default:
            if (seenPercent) {
              throw new IllegalArgumentException("Illegal option format. Unknown format specifier.");
            }
            token.append(formatString.charAt(i));
            break;
        }
      }
      if (tokenCount != (EXPECTED_TOKENS - 1)) {
        throw new IllegalArgumentException("Illegal option format. Not enough %s placeholders.");
      }
      tokens[tokenCount] = token.toString();
      return tokens;
    }

  }

  /**
   * Thrown when an inexistent section is addressed.
   */
  public static class NoSuchSectionException extends RuntimeException {
    /**
		 * 
		 */
    private static final long serialVersionUID = -3010340995171651162L;

    public NoSuchSectionException() {
      super();
    }

    public NoSuchSectionException(final String msg) {
      super(msg);
    }
  }

}