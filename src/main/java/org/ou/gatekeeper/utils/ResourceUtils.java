package org.ou.gatekeeper.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Iterator;
import java.util.Random;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * @todo description
 */
public class ResourceUtils {

  /*
   * Generate an unique random filename.
   * @param prefix
   * @param ext
   * @return
   * */
  public static String generateUniqueFilename(String prefix, String ext) {
    int rand = random.nextInt();
    long timestamp = Instant.now().toEpochMilli();
    return prefix + "-" + timestamp + "-" + rand + "." + ext;
  }

  /**
   * @param path
   * @param exts
   * @return
   * @todo description
   */
  public static Iterator<File> getResourceFiles(String path, String[] exts) {
    try {
      URL resourceUrl = ResourceUtils.class.getClassLoader().getResource(path);
      File resourceDir = new File(resourceUrl.toURI());
      return FileUtils.iterateFiles(resourceDir, exts, false);

    } catch (URISyntaxException e) {
      // @todo message resource in the path not found ?
      e.printStackTrace();
    }
    return null;
  }

  /**
   * @todo description
   */
  public static void clean(File... litter) {
    try {
      for (File file: litter) {
        Files.delete(file.toPath());
      }

    } catch (IOException e) {
      // @todo Message
      e.printStackTrace();
    }
  }

  //--------------------------------------------------------------------------//
  // Class definition
  //--------------------------------------------------------------------------//

  private static Random random = new Random();

  /**
   * @todo description
   */
  private ResourceUtils() {
  }

}