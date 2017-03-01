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

import static jgamebase.Const.log;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URL;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.Timer;

import jgamebase.Const;

public abstract class DownloadTools {

  /**
   * Download a file from the network via HTTP.
   * 
   * @param url
   *          The resource to download.
   * @param file
   *          The directory or file to save to.
   * @return <code>true</code> if successful, <code>false</code> otherwise.
   */
  public static boolean downloadUriToFile(final URI uri, File file) {
    URL url;
    final String oldValueOf_useSystemProxies = System
        .getProperty(Const.SYSTEMPROPERTY_USE_SYSTEM_PROXIES);
    HttpURLConnection connection = null;
    BufferedReader text_in = null;
    PrintWriter text_out = null;
    InputStream binary_in = null;
    OutputStream binary_out = null;

    try {
      System.setProperty(Const.SYSTEMPROPERTY_USE_SYSTEM_PROXIES, "true");

      url = uri.toURL();
      final List<Proxy> proxies = ProxySelector.getDefault().select(uri);
      Proxy proxy = null;

      if ((proxies != null) && (!proxies.isEmpty())) {
        proxy = proxies.get(0);
        connection = (HttpURLConnection) url.openConnection(proxy);
      } else {
        connection = (HttpURLConnection) url.openConnection();
      }

      // if a directory was specified add the file name from the URL
      if (file.isDirectory()) {
        final String filename = new File(url.getFile()).getName();
        if (filename.isEmpty()) {
          log.info("No file name specified and no file name found in URL.");
          return false;
        }
        file = new File(file, filename);
      }

      int responseCode = connection.getResponseCode();
      String responseMessage = connection.getResponseMessage();

      InetSocketAddress addr = null;
      if (proxy != null) {
        addr = (InetSocketAddress) proxy.address();
        if (addr == null) {
          log.info("Downloading '" + url + "' to file '" + file + "'");
        } else {
          log.info("Downloading '" + url + "' with " + proxy.type() + " proxy '"
              + addr.getHostName() + ":" + addr.getPort() + "' to file '" + file + "'");
        }
      }

      // proxy authentication required (HTTP 407) ?
      if (responseCode == HttpURLConnection.HTTP_PROXY_AUTH) {
        log.info("HTTP " + responseCode + " " + responseMessage);

        log.info("Checking system properties '" + Const.SYSTEMPROPERTY_HTTP_PROXY_USER + "' and '"
            + Const.SYSTEMPROPERTY_HTTP_PROXY_PASSWORD + "' for credentials");
        String username = System.getProperty(Const.SYSTEMPROPERTY_HTTP_PROXY_USER, "");
        String password = System.getProperty(Const.SYSTEMPROPERTY_HTTP_PROXY_PASSWORD, "");

        if (username.isEmpty()) {
          log.info("System property '" + Const.SYSTEMPROPERTY_HTTP_PROXY_USER
              + "' was empty - asking user");

          final JPanel panel = new JPanel(new GridLayout(2, 2));

          panel.add(new JLabel("Username:"));
          final JTextField usernameField = new JTextField();
          panel.add(usernameField);

          panel.add(new JLabel("Password:"));
          final JPasswordField passwordField = new JPasswordField();
          panel.add(passwordField);
          final JOptionPane pane = new JOptionPane(panel, JOptionPane.INFORMATION_MESSAGE,
              JOptionPane.OK_CANCEL_OPTION);
          final JDialog dia = pane.createDialog(null, addr.getHostName() + ":" + addr.getPort());

          // set focus to field for user name
          dia.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(final WindowEvent e) {
              final Timer timer = new Timer(50, new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent ev) {
                  usernameField.requestFocus(false);
                }
              });
              timer.setRepeats(false);
              timer.start();
            }
          });

          dia.setVisible(true);

          final Object selectedValue = pane.getValue();

          if ((!(selectedValue instanceof Integer))
              || (((Integer) selectedValue).intValue() != JOptionPane.OK_OPTION)) {
            log.info("Action cancelled by user.");
            return false;
          }

          username = usernameField.getText();
          password = passwordField.getText();

          System.setProperty(Const.SYSTEMPROPERTY_HTTP_PROXY_USER, username);
          System.setProperty(Const.SYSTEMPROPERTY_HTTP_PROXY_PASSWORD, password);

        }

        final String finalUsername = username;
        final String finalPassword = password;

        Authenticator.setDefault(new Authenticator() {
          @Override
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(finalUsername, finalPassword.toCharArray());
          }
        });

        log.info("Trying proxy authentication with user '" + finalUsername + "' and password '"
            + finalPassword + "'");

        connection = (HttpURLConnection) url.openConnection(proxy);
        responseCode = connection.getResponseCode();
        responseMessage = connection.getResponseMessage();

        if (responseCode != HttpURLConnection.HTTP_OK) {
          log.info("HTTP " + responseCode + " " + responseMessage);
        }
      }

      final String contentType = connection.getContentType();
      final int contentLength = connection.getContentLength();
      log.info("Content type is '" + contentType + "'"
          + (contentLength < 0 ? "" : " with length " + contentLength));

      if ((contentType != null) && (contentType.toLowerCase().startsWith("text/"))) {
        // TEXT
        text_in = new BufferedReader(new InputStreamReader(url.openStream()));
        text_out = new PrintWriter(file);
        String inputLine;

        log.info("Downloading as text");
        // copy data
        while ((inputLine = text_in.readLine()) != null) {
          text_out.println(inputLine);
        }

      } else {
        // BINARY
        binary_in = connection.getInputStream();
        binary_out = new BufferedOutputStream(new FileOutputStream(file));
        final byte[] buffer = new byte[64 * 1024];
        int read, contentRead = 0;

        log.info("Downloading as binary");
        // copy data
        while ((read = binary_in.read(buffer)) != -1) {
          binary_out.write(buffer, 0, read);
          contentRead += read;
        }

        if (contentRead < contentLength) {
          log.info("Warning: only got " + contentRead + " of " + contentLength + " bytes");
        }

      }

      responseCode = connection.getResponseCode();
      responseMessage = connection.getResponseMessage();
      log.info("HTTP " + responseCode + " " + responseMessage);
      log.info("");

      return (responseCode == HttpURLConnection.HTTP_OK);

    } catch (final Exception e) {
      log.warn("Error while downloading '" + uri + "': " + e.getMessage() + ".");
      return false;
    } finally {
      if (oldValueOf_useSystemProxies != null) {
        System.setProperty(Const.SYSTEMPROPERTY_USE_SYSTEM_PROXIES, oldValueOf_useSystemProxies);
      }
      if (text_in != null) {
        try {
          text_in.close();
        } catch (final IOException e) {
          e.printStackTrace();
        }
      }
      if (text_out != null) {
        text_out.close();
      }
      if (binary_in != null) {
        try {
          binary_in.close();
        } catch (final IOException e) {
          e.printStackTrace();
        }
      }
      if (binary_out != null) {
        try {
          binary_out.close();
        } catch (final IOException e) {
          e.printStackTrace();
        }
      }
      if (connection != null) {
        connection.disconnect();
      }
    }
  }

  public static boolean isDownloadableFiletype(final String filename) {
    final String ext = FileTools.getExtension(filename);
    final int len = FileTools.getExtension(filename).length();

    return (((len > 0) && (len <= 5)) && (!Const.EXTENSIONS_NODOWNLOAD.matches(filename)));
  }
}
