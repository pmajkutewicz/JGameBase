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

package jgamebase.tools;

import java.text.Normalizer;

public abstract class StringTools {

  public static String beforeChar(final String s, final char delemiter) {
    if ((s == null) || (s.indexOf(delemiter) == -1)) {
      return "";
    }

    final String t = s.trim();
    return t.substring(0, t.indexOf(delemiter));
  }

  public static String beforeSpace(final String s) {
    return beforeChar(s, ' ');
  }

  public static String beforeEqualsign(final String s) {
    return beforeChar(s, '=');
  }

  public static String afterChar(final String s, final char delemiter) {
    if (s == null) {
      return "";
    }

    final String t = s.trim();
    final int pos = t.indexOf(delemiter);
    if (pos == -1) {
      return "";
    }
    return t.substring(pos + 1, t.length());
  }

  public static String afterSpace(final String s) {
    return afterChar(s, ' ');
  }

  public static String afterEqualsign(final String s) {
    return afterChar(s, '=');
  }

  public static String padZeroBefore(final String s, final int length) {
    if ((s == null)) {
      return "";
    }

    String t = s.trim();
    while (t.length() < length) {
      t = "0" + t;
    }

    return t;
  }

  public static String firstCharAsString(final String s) {
    if (s != null) {
      final String t = s.trim();
      if (t.length() > 0) {
        return Character.toString(firstChar(t));
      }
    }
    return "";
  }

  public static char firstChar(final String s) {
    if (s != null) {
      final String t = s.trim();
      if (t.length() > 0) {
        return t.charAt(0);
      }
    }
    return '\0';
  }

  public static String lastCharAsString(final String s) {
    if (s != null) {
      final String t = s.trim();
      if (t.length() > 0) {
        return Character.toString(lastChar(t));
      }
    }
    return "";
  }

  public static char lastChar(final String s) {
    if (s != null) {
      final String t = s.trim();
      if (t.length() > 0) {
        return t.charAt(t.length() - 1);
      }
    }
    return '\0';
  }

  public static String capitalize(String s) {

    if ((s == null) || (s.length() == 0)) {
      return s;
    }

    s = " " + s.toLowerCase();

    final char[] chars = s.toCharArray();

    // convert all characters following an underscore to upper case
    final StringBuffer t = new StringBuffer(Character.toUpperCase(chars[0]));
    for (int i = 1; i < chars.length; i++) {
      if (Character.isWhitespace(chars[i - 1]) || (chars[i - 1] == '.') || (chars[i - 1] == '!')
          || (chars[i - 1] == '?') || (chars[i - 1] == '_')) {
        t.append(Character.toUpperCase(chars[i]));
      } else {
        t.append(chars[i]);
      }
    }

    return t.toString().trim();
  }

  public static String htmlDecode(final String s) {
    return s.replaceAll("\\<[^>]*>", "");
  }

  /**
   * Encodes the string using HTML entities. The five standard XML entities are
   * replaced and additionally backslashes and linefeeds are encoded.
   * 
   * @param s
   *          The string to encode.
   * 
   * @return The encoded string.
   */
  public static String htmlEncode(final String s) {
    if (s == null) {
      return "";
    }

    final StringBuffer t = new StringBuffer();

    for (int i = 0; i < s.length(); i++) {
      final char c = s.charAt(i);

      if (c == '<') {
        t.append("&lt;");
      } else if (c == '>') {
        t.append("&gt;");
      } else if (c == '&') {
        t.append("&amp;");
      } else if (c == '"') {
        t.append("&quot;");
      } else if (c == '\'') {
        t.append("&apos;");
      } else {
        t.append(c);
      }
    }

    return t.toString();
  }

  public static String startWithUpperCase(final String s) {
    final char[] chars = s.toCharArray();
    chars[0] = Character.toUpperCase(chars[0]);
    return new String(chars);
  }

  public static String sanitize(String s) {

    if ((s == null) || (s.length() == 0)) {
      return s;
    }

    // preserve umlauts
    s = s.replaceAll("ä", "ae");
    s = s.replaceAll("ö", "oe");
    s = s.replaceAll("ü", "ue");
    s = s.replaceAll("Ä", "Ae");
    s = s.replaceAll("Ö", "Oe");
    s = s.replaceAll("Ü", "Ue");
    s = s.replaceAll("ß", "ss");

    // preserve ampersand
    s = s.replaceAll("&", " and ");

    // handle (), [], and {}
    s = s.replaceAll("\\(([^\\)]*)\\)", " $1");
    s = s.replaceAll("\\[([^\\]]*)\\]", " $1");
    s = s.replaceAll("\\{([^\\}]*)\\}", " $1");

    // remove points in text
    s = s.replaceAll("(\\w)?\\.(\\w)?", "$1$2");

    // delete ticks
    s = s.replaceAll("'", "");
    s = s.replaceAll("`", "");

    // replace hash before numbers
    s = s.replaceAll("#(\\d+)", "Nr$1");
    // remove leading zeros from numbers
    s = s.replaceAll("(\\D+)0+(\\d*)", "$1 $2");

    // separate complex character in ascii + modifiers
    s = Normalizer.normalize(s, Normalizer.Form.NFKD);

    // replace whitespace or non word characters with underscore
    s = s.replaceAll("[\\s\\W]", "_");

    // replace all underscore chains with exactly one underscore
    s = s.replaceAll("_+", "_");

    // remove leading underscore
    s = s.replaceAll("^_", "");

    // remove trailing underscore
    s = s.replaceAll("_$", "");

    return s;
  }

  public static String nextMedium(final String fullFilename) {
    if ((fullFilename == null) || (fullFilename.isEmpty())) {
      return "";
    }

    final String filename = FileTools.removeExtension(fullFilename);
    if ((filename.isEmpty()) || (filename.length() == 1)) {
      return "";
    }

    int pos;
    StringBuilder s;

    if (Character.isDigit(lastChar(filename))) {
      s = new StringBuilder();
      pos = filename.length() - 1;
      while ((pos > 0) && (Character.isDigit(filename.charAt(pos)))) {
        s = s.append(filename.charAt(pos));
        pos--;
      }
      s.reverse();
      try {
        final int i = Integer.valueOf(s.toString()) + 1;
        return filename.substring(0, pos + 1) + i + "." + FileTools.getExtension(fullFilename);
      } catch (final NumberFormatException e) {
        return "";
      }
    } else {
      s = new StringBuilder(fullFilename);
      pos = filename.length() - 1;
      s.setCharAt(pos, ((char) (s.charAt(pos) + 1)));
      return s.toString();
    }
  }

  /*
   * From "MiningMart", released under GPL >=2
   * 
   * Copyright (C) 2006 Martin Scholz, Timm Euler, Daniel Hakenjos, Katharina
   * Morik
   */
  public static double getStringSimilarity(final String s1, final String s2) {

    final String str1 = s1.trim();
    final String str2 = s2.trim();

    final int levenshteinDistance = getLevenshteinDistance(str1, str2);

    if (levenshteinDistance == 0) {
      return 1;
    } else {
      final String longestString = (str1.length() > str2.length() ? str1 : str2);
      return (1 - ((1.0 / longestString.length()) * levenshteinDistance));
    }
  }

  /*
   * This method was implemented by Markus Wagner. It computes the Levenshtein
   * distance between the two given Strings.
   */
  private static int getLevenshteinDistance(final String s1, final String s2) {
    int d[][]; // matrix
    int l1; // length of s1
    int l2; // length of s2
    int i1; // iterates through s1
    int i2; // iterates through s2
    char s1_i1; // i1th character of s1
    char s2_i2; // i2th character of s2
    int cost; // cost

    // Step 1
    l1 = s1.length();
    l2 = s2.length();
    if (l1 == 0) {
      return l2;
    }
    if (l2 == 0) {
      return l1;
    }
    d = new int[l1 + 1][l2 + 1];

    // Step 2
    for (i1 = 0; i1 <= l1; i1++) {
      d[i1][0] = i1;
    }
    for (i2 = 0; i2 <= l2; i2++) {
      d[0][i2] = i2;
    }

    // Step 3
    for (i1 = 1; i1 <= l1; i1++) {

      s1_i1 = s1.charAt(i1 - 1);

      // Step 4
      for (i2 = 1; i2 <= l2; i2++) {

        s2_i2 = s2.charAt(i2 - 1);

        // Step 5
        if (s1_i1 == s2_i2) {
          cost = 0;
        } else {
          cost = 1;
        }

        // Step 6
        d[i1][i2] = minimum(d[i1 - 1][i2] + 1, d[i1][i2 - 1] + 1, d[i1 - 1][i2 - 1] + cost);
      }
    }

    // Step 7
    return d[l1][l2];
  }

  /*
   * Return the minimum value of the three given values.
   */
  private static int minimum(final int a, final int b, final int c) {
    int min = a;
    if (b < min) {
      min = b;
    }
    if (c < min) {
      min = c;
    }
    return min;
  }

  public static int countLowerCase(final String s) {
    if ((s == null) || (s.isEmpty())) {
      return 0;
    }

    final char[] a = s.toCharArray();
    int c = 0;

    for (final char element : a) {
      if (Character.getType(element) == Character.LOWERCASE_LETTER) {
        c++;
      }
    }

    return c;
  }

  public static int countUpperCase(final String s) {
    if ((s == null) || (s.isEmpty())) {
      return 0;
    }

    final char[] a = s.toCharArray();
    int c = 0;

    for (final char element : a) {
      if (Character.getType(element) == Character.UPPERCASE_LETTER) {
        c++;
      }
    }

    return c;
  }

  public static int countWhitespace(final String s) {
    if ((s == null) || (s.isEmpty())) {
      return 0;
    }

    final char[] a = s.toCharArray();
    int c = 0;

    for (final char element : a) {
      if (Character.getType(element) == Character.SPACE_SEPARATOR) {
        c++;
      }
    }

    return c;
  }

  public static boolean isCamelCase(final String s) {
    if ((s == null) || (s.length() < 2)) {
      return false;
    }

    final int lower = countLowerCase(s);
    final int upper = countUpperCase(s);
    final int space = countWhitespace(s);

    // only lower or only upper case
    if ((space != 0) || (lower == 0) || (upper == 0)) {
      return false;
    }

    final char[] a = s.toCharArray();
    boolean camelCase = false;

    for (int i = 0; i < (a.length - 1); i++) {
      if ((Character.getType(a[i]) == Character.LOWERCASE_LETTER)
          && (Character.getType(a[i + 1]) == Character.UPPERCASE_LETTER)) {
        camelCase = true;
      }
    }

    return camelCase;
  }

  public static String deCamelCase(final String s) {
    if (!isCamelCase(s)) {
      return s;
    }

    final char[] a = s.toCharArray();
    final StringBuilder sb = new StringBuilder(s.length() * 2);

    for (int i = 0; i < (a.length - 1); i++) {
      sb.append(a[i]);
      if ((Character.getType(a[i]) == Character.LOWERCASE_LETTER)
          && (Character.getType(a[i + 1]) == Character.UPPERCASE_LETTER)) {
        sb.append(' ');
      }
    }
    sb.append(a[a.length - 1]);

    return sb.toString();
  }

  public static String simplifyForMatching(final String s) {
    if (s == null) {
      return "";
    }

    String n = new String(s);

    n = deCamelCase(n);

    // treat colon as start of subtitle
    n = n.replaceAll(":", " - ");

    // cut the subtitle "Archon II - Adept" => "Archon II"
    final int p = n.indexOf(" - ");
    if (p > 3) {
      n = n.substring(0, p);
    }

    // remove everything in parentheses or brackets
    n = n.replaceAll("\\(.*\\)", " ");
    n = n.replaceAll("\\[.*\\]", " ");

    // change "foo's" to "foos"
    n = n.replaceAll("(\\w)'([sS])", "$1$2");

    // change "2049'er" to "2049"
    n = n.replaceAll("(\\d+)[']*[eE][rR]", "$1");

    // remove quotes
    n = n.replaceAll("'", " ");
    n = n.replaceAll("\"", " ");

    // handle "+" sign
    n = n.replaceAll("\\++$", " +");
    n = n.replaceAll("\\+", " plus ");

    // remove "/text"
    n = n.replaceAll("/\\S+$", "");

    // remove leading zeros from numbers
    n = n.replaceAll("(\\D+)0+(\\d*)", "$1 $2");

    // remove 1st 2nd 3rd 4th
    n = n.replaceAll("1[sS][tT]", "1");
    n = n.replaceAll("2[nN][dD]", "2");
    n = n.replaceAll("3[rR][dD]", "3");
    n = n.replaceAll("(\\d+)[tT][hH]", "$1");

    // remove article
    n = removeArticle(n, "[aA][nN]");
    n = removeArticle(n, "[aA]");
    n = removeArticle(n, "[dD][aA][sS]");
    n = removeArticle(n, "[dD][eE][nN]");
    n = removeArticle(n, "[dD][eE][rR]");
    n = removeArticle(n, "[dD][eE][sS]");
    n = removeArticle(n, "[dD][eE][tT]");
    n = removeArticle(n, "[dD][eE]");
    n = removeArticle(n, "[dD][iI][eE]");
    n = removeArticle(n, "[eE][eE][nN]");
    n = removeArticle(n, "[eE][iI][nN][eE]");
    n = removeArticle(n, "[eE][iI][nN]");
    n = removeArticle(n, "[eE][lL]");
    n = removeArticle(n, "[hH][eE][tT]");
    n = removeArticle(n, "[iI][lL]");
    n = removeArticle(n, "[lL][aA][sS]");
    n = removeArticle(n, "[lL][aA]");
    n = removeArticle(n, "[lL][eE][sS]");
    n = removeArticle(n, "[lL][eE]");
    n = removeArticle(n, "[lL][oO][sS]");
    n = removeArticle(n, "[tT][hH][eE]");
    n = removeArticle(n, "[uU][nN][oO]");

    // remove " in ", " of ", " and ", " & "
    n = n.replaceAll("\\s[iI][nN]\\s", " ");
    n = n.replaceAll("\\s[oO][fF]\\s", " ");
    n = n.replaceAll("\\s[aA][nN][dD]\\s", " ");
    n = n.replaceAll("\\s&\\s", " ");

    // simplify "Doctor"
    n = n.replaceAll("[dD][oO][cC][tT][oO][rR]", " Dr ");
    n = n.replaceAll("[dD][oO][cC]", " Dr ");

    // insert space between text and numbers
    n = n.replaceAll("([a-zA-Z]+)(\\d+)", "$1 $2");
    n = n.replaceAll("(\\d+)([a-zA-Z]+)", "$1 $2");

    // convert Roman numbers
    n = convertNumbers_RomanToArabic(n);

    // convert numbers
    n = convertNumbers_ArabicToText(n);
    // remove "one" so "Archon" equals "Archon One"
    n = n.replaceAll("\\s[oO][nN][eE]\\s", " ");
    n = n.replaceAll("\\s[oO][nN][eE]$", "");

    n = sanitize(n);

    // cosmetics :-)
    n = n.replaceAll("_", " ");

    return n.toUpperCase();
  }

  private static String removeArticle(String s, final String pattern) {
    s = s.replaceAll("^" + pattern + "[\\s]+", "");
    s = s.replaceAll("[\\s,;]+" + pattern + "$", "");
    s = s.replaceAll("\\W" + pattern + "\\s", " ");
    return s;
  }

  private static String convertNumbers_ArabicToText(String s) {
    s = s.replaceAll("(\\D+)1(\\D+)", "$1 one $2");
    s = s.replaceAll("(\\D+)1(\\D+)", "$1 one $2");
    s = s.replaceAll("(\\D+)2(\\D+)", "$1 two $2");
    s = s.replaceAll("(\\D+)3(\\D+)", "$1 three $2");
    s = s.replaceAll("(\\D+)4(\\D+)", "$1 four $2");
    s = s.replaceAll("(\\D+)5(\\D+)", "$1 five $2");
    s = s.replaceAll("(\\D+)6(\\D+)", "$1 six $2");
    s = s.replaceAll("(\\D+)7(\\D+)", "$1 seven $2");
    s = s.replaceAll("(\\D+)8(\\D+)", "$1 eight $2");
    s = s.replaceAll("(\\D+)9(\\D+)", "$1 nine $2");
    s = s.replaceAll("(\\D+)10(\\D+)", "$1 ten $2");
    s = s.replaceAll("(\\D+)11(\\D+)", "$1 eleven $2");
    s = s.replaceAll("(\\D+)12(\\D+)", "$1 twelve $2");
    s = s.replaceAll("(\\D+)13(\\D+)", "$1 thirteen $2");
    s = s.replaceAll("(\\D+)14(\\D+)", "$1 fourteen $2");
    s = s.replaceAll("(\\D+)15(\\D+)", "$1 fifteen $2");
    s = s.replaceAll("(\\D+)16(\\D+)", "$1 sixteen $2");
    s = s.replaceAll("(\\D+)17(\\D+)", "$1 seventeen $2");
    s = s.replaceAll("(\\D+)18(\\D+)", "$1 eighteen $2");
    s = s.replaceAll("(\\D+)19(\\D+)", "$1 nineteen $2");
    s = s.replaceAll("(\\D+)20(\\D+)", "$1 twenty $2");
    s = s.replaceAll("(\\D+)21(\\D+)", "$1 twentyone $2");
    s = s.replaceAll("(\\D+)22(\\D+)", "$1 twentytwo $2");
    s = s.replaceAll("(\\D+)23(\\D+)", "$1 twentythree $2");
    s = s.replaceAll("(\\D+)24(\\D+)", "$1 twentyfour $2");
    s = s.replaceAll("(\\D+)25(\\D+)", "$1 twentyfive $2");

    s = s.replaceAll("(\\D+)1$", "$1 one");
    s = s.replaceAll("(\\D+)2$", "$1 two");
    s = s.replaceAll("(\\D+)3$", "$1 three");
    s = s.replaceAll("(\\D+)4$", "$1 four");
    s = s.replaceAll("(\\D+)5$", "$1 five");
    s = s.replaceAll("(\\D+)6$", "$1 six");
    s = s.replaceAll("(\\D+)7$", "$1 seven");
    s = s.replaceAll("(\\D+)8$", "$1 eight");
    s = s.replaceAll("(\\D+)9$", "$1 nine");
    s = s.replaceAll("(\\D+)10$", "$1 ten");
    s = s.replaceAll("(\\D+)11$", "$1 eleven");
    s = s.replaceAll("(\\D+)12$", "$1 twelve");
    s = s.replaceAll("(\\D+)13$", "$1 thirteen");
    s = s.replaceAll("(\\D+)14$", "$1 fourteen");
    s = s.replaceAll("(\\D+)15$", "$1 fifteen");
    s = s.replaceAll("(\\D+)16$", "$1 sixteen");
    s = s.replaceAll("(\\D+)17$", "$1 seventeen");
    s = s.replaceAll("(\\D+)18$", "$1 eighteen");
    s = s.replaceAll("(\\D+)19$", "$1 nineteen");
    s = s.replaceAll("(\\D+)20$", "$1 twenty");
    s = s.replaceAll("(\\D+)21$", "$1 twentyone");
    s = s.replaceAll("(\\D+)22$", "$1 twentytwo");
    s = s.replaceAll("(\\D+)23$", "$1 twentythree");
    s = s.replaceAll("(\\D+)24$", "$1 twentyfour");
    s = s.replaceAll("(\\D+)25$", "$1 twentyfive");
    return s;
  }

  public static String convertNumbers_AllToArabic(String s) {
    s = convertNumbers_RomanToArabic(s);
    s = convertNumbers_TextToArabic(s);
    return s;
  }

  private static String convertNumbers_TextToArabic(String s) {
    s = s.replaceAll("\\s+one\\s+", " 1 ");
    s = s.replaceAll("\\s+two\\s+", " 2 ");
    s = s.replaceAll("\\s+three\\s+", " 3 ");
    s = s.replaceAll("\\s+four\\s+", " 4 ");
    s = s.replaceAll("\\s+five\\s+", " 5 ");
    s = s.replaceAll("\\s+six\\s+", " 6 ");
    s = s.replaceAll("\\s+seven\\s+", " 7 ");
    s = s.replaceAll("\\s+eight\\s+", " 8 ");
    s = s.replaceAll("\\s+nine\\s+", " 9 ");
    s = s.replaceAll("\\s+ten\\s+", " 10 ");
    s = s.replaceAll("\\s+eleven\\s+", " 11 ");
    s = s.replaceAll("\\s+twelve\\s+", " 12 ");
    s = s.replaceAll("\\s+thirteen\\s+", " 13 ");
    s = s.replaceAll("\\s+fourteen\\s+", " 14 ");
    s = s.replaceAll("\\s+fifteen\\s+", " 15 ");
    s = s.replaceAll("\\s+sixteen\\s+", " 16 ");
    s = s.replaceAll("\\s+seventeen\\s+", " 17 ");
    s = s.replaceAll("\\s+eighteen\\s+", " 18 ");
    s = s.replaceAll("\\s+nineteen\\s+", " 19 ");
    s = s.replaceAll("\\s+twenty\\s+", " 20 ");
    s = s.replaceAll("\\s+twentyone\\s+", " 21 ");
    s = s.replaceAll("\\s+twentytwo\\s+", " 22 ");
    s = s.replaceAll("\\s+twentythree\\s+", " 23 ");
    s = s.replaceAll("\\s+twentyfour\\s+", " 24 ");
    s = s.replaceAll("\\s+twentyfive\\s+", " 25 ");

    s = s.replaceAll("\\s+one$", " 1");
    s = s.replaceAll("\\s+two$", " 2");
    s = s.replaceAll("\\s+three$", " 3");
    s = s.replaceAll("\\s+four$", " 4");
    s = s.replaceAll("\\s+five$", " 5");
    s = s.replaceAll("\\s+six$", " 6");
    s = s.replaceAll("\\s+seven$", " 7");
    s = s.replaceAll("\\s+eight$", " 8");
    s = s.replaceAll("\\s+nine$", " 9");
    s = s.replaceAll("\\s+ten$", " 10");
    s = s.replaceAll("\\s+eleven$", " 11");
    s = s.replaceAll("\\s+twelve$", " 12");
    s = s.replaceAll("\\s+thirteen$", " 13");
    s = s.replaceAll("\\s+fourteen$", " 14");
    s = s.replaceAll("\\s+fifteen$", " 15");
    s = s.replaceAll("\\s+sixteen$", " 16");
    s = s.replaceAll("\\s+seventeen$", " 17");
    s = s.replaceAll("\\s+eighteen$", " 18");
    s = s.replaceAll("\\s+nineteen$", " 19");
    s = s.replaceAll("\\s+twenty$", " 20");
    s = s.replaceAll("\\s+twentyone$", " 21");
    s = s.replaceAll("\\s+twentytwo$", " 22");
    s = s.replaceAll("\\s+twentythree$", " 23");
    s = s.replaceAll("\\s+twentyfour$", " 24");
    s = s.replaceAll("\\s+twentyfive$", " 25");
    return s;
  }

  public static String convertNumbers_RomanToArabic(String s) {
    s = s.replaceAll("[\\s]+[iI][\\s]+", " 1 ");
    s = s.replaceAll("[\\s]+[iI][iI][\\s]+", " 2 ");
    s = s.replaceAll("[\\s]+[iI][iI][iI][\\s]+", " 3 ");
    s = s.replaceAll("[\\s]+[iI][vV][\\s]+", " 4 ");
    s = s.replaceAll("[\\s]+[vV][\\s]+", " 5 ");
    s = s.replaceAll("[\\s]+[vV][iI][\\s]+", " 6 ");
    s = s.replaceAll("[\\s]+[vV][iI][iI][\\s]+", " 7 ");
    s = s.replaceAll("[\\s]+[vV][iI][iI][iI][\\s]+", " 8 ");
    s = s.replaceAll("[\\s]+[iI][xX][\\s]+", " 9 ");
    s = s.replaceAll("[\\s]+[xX][\\s]+", " 10 ");
    s = s.replaceAll("[\\s]+[xX][iI][\\s]+", " 11 ");
    s = s.replaceAll("[\\s]+[xX][iI][iI][\\s]+", " 12 ");

    s = s.replaceAll("[\\s]+[iI]$", " 1");
    s = s.replaceAll("[\\s]+[iI][iI]$", " 2");
    s = s.replaceAll("[\\s]+[iI][iI][iI]$", " 3");
    s = s.replaceAll("[\\s]+[iI][vV]$", " 4");
    s = s.replaceAll("[\\s]+[vV]$", " 5");
    s = s.replaceAll("[\\s]+[vV][iI]$", " 6");
    s = s.replaceAll("[\\s]+[vV][iI][iI]$", " 7");
    s = s.replaceAll("[\\s]+[vV][iI][iI][iI]$", " 8");
    s = s.replaceAll("[\\s]+[iI][xX]$", " 9");
    s = s.replaceAll("[\\s]+[xX]$", " 10");
    s = s.replaceAll("[\\s]+[xX][iI]$", " 11");
    s = s.replaceAll("[\\s]+[xX][iI][iI]$", " 12");
    return s;
  }

}
