package org.commons;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collection;
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
   * @todo description
   */
  public static String readFileToString(File file) {
    try {
      return Files.readString(file.toPath());

    } catch (IOException e) {
      // @todo Message: resource not found
      e.printStackTrace();
    }

    return null;
  }

  /**
   * @param path
   * @param exts
   * @return
   * @todo description
   */
  public static Collection<File> getResourceFiles(Path path, String[] exts) {
    URL resourceUrl = classLoader.getResource(path.toString());
    File resourceDir = new File(resourceUrl.getFile());
    return FileUtils.listFiles(resourceDir, exts, false);
  }

  /**
   * @todo description
   */
  public static InputStream getResourceAsStream(String path) {
    return classLoader.getResourceAsStream(path);
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
  private static ClassLoader classLoader = ResourceUtils.class.getClassLoader();

  /**
   * @todo description
   */
  private ResourceUtils() {
  }

}