package org.commons;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * @todo description
 */
public class FilenameUtils {
  public static final String EXT_SEP = ".";
  /**
   * @todo description
   */
  public static String changeExtention(String filename, String newExt) {
    int indexOfExtSep = filename.lastIndexOf(EXT_SEP);
    String nameWithoutExt = filename.substring(0, indexOfExtSep);
    return nameWithoutExt + EXT_SEP + newExt;
  }

  //--------------------------------------------------------------------------//
  // Class definition
  //--------------------------------------------------------------------------//

  /**
   * @todo description
   */
  private FilenameUtils() {
  }

}