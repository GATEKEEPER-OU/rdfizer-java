package org.commons;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * @todo description
 */
public class FilenameUtils {

  public static final String EXT_SEP = ".";

  /**
   *
   * @todo description
   */
  public static String trim2LvlExtension(String filename) {
    String[] parts = filename.split(Pattern.quote(EXT_SEP));
    int nParts = parts.length;
    if(nParts == 2 ) return filename; // @todo review this code
    if (nParts < 2) throw new IllegalArgumentException("The given filename doesn't contain a 2 lvl extension.");
    parts[nParts - 2] = null;
    return Arrays.stream(parts)
      .filter(Objects::nonNull)
      .collect(Collectors.joining(EXT_SEP));
  }

  /**
   * @todo description
   */
  public static String changeExtension(String filename, String newExt) {
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