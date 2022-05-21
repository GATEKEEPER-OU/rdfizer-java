package org.commons;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * @todo description
 */
public class UrlUtils {

  /**
   * @todo description
   */
  public static String getHost(String spec) {
    try {
      URL url = new URL(spec);
      return url.getHost();
    } catch (MalformedURLException e) {
      // @todo Message ?
      e.printStackTrace();
    }
    return null;
  }

  /**
   * @todo description
   */
  private UrlUtils() {
  }

}